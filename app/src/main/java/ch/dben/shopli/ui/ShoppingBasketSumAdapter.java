package ch.dben.shopli.ui;

import android.database.Cursor;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import ch.dben.shopli.content.ShoppingBasketContract;

public class ShoppingBasketSumAdapter {

    private final NumberFormat mCurrencyFormatter;
    private Cursor mCursor;
    CurrencyAdapter.CurrencyHolder mHolder;
    private int columnIndexTotalCost;

    public ShoppingBasketSumAdapter() {
        mCurrencyFormatter = NumberFormat.getInstance();
        mCurrencyFormatter.setMinimumFractionDigits(2);
        Currency currency = Currency.getInstance("USD");
        mCurrencyFormatter.setCurrency(currency);
    }


    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        if (cursor != null) {
            columnIndexTotalCost = cursor.getColumnIndex(ShoppingBasketContract.TotalCost.Columns.COLUMN_TOTAL_COST);
        }

        mCursor = cursor;
    }

    public void setCurrency(CurrencyAdapter.CurrencyHolder holder) {
        mHolder = holder;
    }

    public void bindView(TextView totalSumLabel) {

        if (totalSumLabel != null) {

            int sum = 0;
            if (mCursor != null && mCursor.moveToFirst()) {
                sum = mCursor.getInt(columnIndexTotalCost);
            }

            double quote = 1.0d;
            String iso = "USD";
            if (mHolder != null) {
                quote = mHolder.quote;
                iso = mHolder.isoCode;

                Currency currency = Currency.getInstance(iso);
                mCurrencyFormatter.setCurrency(currency);
            }

            totalSumLabel.setText(String.format(Locale.ROOT, "Total cost: %s %s", mCurrencyFormatter.format((sum * quote)/100), iso));
        }
    }
}
