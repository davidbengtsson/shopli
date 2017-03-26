package ch.dben.shopli.currencylayer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import ch.dben.shopli.BuildConfig;
import ch.dben.shopli.content.CurrencyContract;

public class RequestHandler {

    private static final String TAG = RequestHandler.class.getSimpleName();

    private static final int TIMEOUT_CONNECTION_MS = 14000;
    private static final int TIMEOUT_READ_MS = 16000;

    private static final String CURRENCY_REQUEST_URL = "http://apilayer.net/api/list?access_key=" + BuildConfig.ACCESS_KEY;
    private static final String EXCHANGE_RATE_REQUEST_URL = "http://apilayer.net/api/live?access_key=" + BuildConfig.ACCESS_KEY;

    private static final String PREFERENCE_CURRENCIES_LOADED = "hasLoadedCurrencies";
    private static final String PREFERENCE_QUOTES_TIMESTAMP = "quotesValidTimestamp";

    private static final long QUOTES_VALIDITY_PERIOD = 60 * 60 * 1000; // currecylayer has 1h between updates for free accounts

    private final ObjectMapper mObjectMapper = new ObjectMapper();
    private final SharedPreferences mPref;
    private final ContentResolver mContentResolver;

    public RequestHandler(Context context) {
        mPref = context.getSharedPreferences("currencylayer", Context.MODE_PRIVATE);
        mContentResolver = context.getContentResolver();
    }

    public void refreshData() {
        new RequestSender().execute();
    }

    @Nullable
    private <ResponseType> ResponseType downloadRequest(String downloadUrl, Class<ResponseType> responseTypeClass) {
        URL url;
        try {
            url = new URL(downloadUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url", e);
        }

        HttpURLConnection urlConnection = null;
        try {
            Log.d(TAG, "Trying to open connection to url: " + url);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection == null) {
                Log.w(TAG, "Failed to open connection");
                // Url incorrect or not permitted
                return null;
            }

            urlConnection.setReadTimeout(TIMEOUT_READ_MS);
            urlConnection.setConnectTimeout(TIMEOUT_CONNECTION_MS);

            int responseCode;
            try {
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                //work around when server do not set the WWW-Authenticate header...
                responseCode = urlConnection.getResponseCode();
            }

            Log.d(TAG, "Got response code: " + responseCode);

            if (HttpURLConnection.HTTP_OK == responseCode) {
                InputStream in = urlConnection.getInputStream();
                ResponseType response = mObjectMapper.readValue(in, responseTypeClass);

                try {
                    in.close();
                } catch (IOException e) {
                    // failed to close input stream, was it closed already?
                }

                return response;

            } else {
                return null;
            }

        } catch (IOException e) {
            Log.w(TAG, "Failed to connect to url=" + url + ", or get a response", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        //something went wrong when connecting with the server...
        return null;
    }

    private class RequestSender extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (mPref.getBoolean(PREFERENCE_CURRENCIES_LOADED, false)) {
                Log.d(TAG, "Currencies already downloaded, continue to quotes");

            } else {
                Log.d(TAG, "First time load of currencies");
                CurrencyResponse response = downloadRequest(CURRENCY_REQUEST_URL, CurrencyResponse.class);
                if (response != null && response.isSuccess()) {
                    Log.d(TAG, "Nbr downloaded currencies: " + response.getCurrencyMap().keySet().size());
                    ContentValues values = new ContentValues();
                    for (Map.Entry<String, String> entry : response.getCurrencyMap().entrySet()) {
                        values.clear();

                        values.put(CurrencyContract.Columns.COLUMN_ISO, entry.getKey().toUpperCase());
                        values.put(CurrencyContract.Columns.COLUMN_NAME, entry.getValue());
                        mContentResolver.insert(CurrencyContract.CONTENT_URI, values);
                    }

                    mPref.edit()
                            .putBoolean(PREFERENCE_CURRENCIES_LOADED, true)
                            .apply();
                } else {
                    Log.w(TAG, "Failed to init currencies");
                }
            }

            if (mPref.getLong(PREFERENCE_QUOTES_TIMESTAMP, 0) < System.currentTimeMillis()) {
                Log.d(TAG, "No wait, will update quotes");
                QuotesResponse response = downloadRequest(EXCHANGE_RATE_REQUEST_URL, QuotesResponse.class);
                if (response != null && response.isSuccess()) {
                    Log.d(TAG, "Nbr quotes downloaded: " + response.getQuotesMap().keySet().size());
                    ContentValues values = new ContentValues();
                    for (Map.Entry<String, Double> entry : response.getQuotesMap().entrySet()) {
                        values.clear();

                        values.put(CurrencyContract.Columns.COLUMN_QUOTE, entry.getValue());
                        String isoCode = entry.getKey().substring(3);
                        mContentResolver.update(CurrencyContract.getIsoContentUri(isoCode), values, null, null);
                    }

                    Log.d(TAG, "Timestamp: " + response.getTimestampMillis());

                    mPref.edit()
                            .putLong(PREFERENCE_QUOTES_TIMESTAMP, response.getTimestampMillis() + QUOTES_VALIDITY_PERIOD)
                            .apply();
                } else {
                    Log.d(TAG, "Failed to refresh quotes");
                }
            } else {
                Log.d(TAG, "Waiting until " + new Date(mPref.getLong(PREFERENCE_QUOTES_TIMESTAMP, 0)));
            }

            return null;
        }
    }
}
