package ch.dben.shopli.ui;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

import ch.dben.shopli.R;
import ch.dben.shopli.content.CurrencyContract;
import ch.dben.shopli.content.ShoppingBasketContract;

public class ShoppingBasketFragment extends ListFragment {

    private static final String TAG = ShoppingBasketFragment.class.getSimpleName();
    private ShoppingBasketAdapter mBasketAdapter;
    private CurrencyAdapter mCurrencyAdapter;
    private ShoppingBasketSumAdapter mTotalSumAdapter;

    private CurrencyAdapter.CurrencyHolder mHolder;

    private final LoaderManager.LoaderCallbacks<Cursor> mBasketLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), ShoppingBasketContract.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mBasketAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mBasketAdapter.swapCursor(null);
        }
    };

    private final LoaderManager.LoaderCallbacks<Cursor> mCurrencyLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), CurrencyContract.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCurrencyAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCurrencyAdapter.swapCursor(null);
        }
    };

    private final LoaderManager.LoaderCallbacks<Cursor> mBasketSumLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), ShoppingBasketContract.TotalCost.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mTotalSumAdapter.swapCursor(data);
            refreshSumLabel();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mTotalSumAdapter.swapCursor(null);
            refreshSumLabel();
        }
    };

    private TextView mSumLabel;
    private Spinner mCurrencySelector;

    private void refreshSumLabel() {
        mTotalSumAdapter.bindView(mSumLabel);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShoppingBasketFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBasketAdapter = new ShoppingBasketAdapter(getActivity(), null);
        mCurrencyAdapter = new CurrencyAdapter(getActivity(), null);
        mTotalSumAdapter = new ShoppingBasketSumAdapter();
        getLoaderManager().initLoader(1, null, mBasketLoaderCallbacks);
        getLoaderManager().initLoader(2, null, mBasketSumLoaderCallbacks);
        getLoaderManager().initLoader(3, null, mCurrencyLoaderCallbacks);

        setListAdapter(mBasketAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basket_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSumLabel = (TextView) view.findViewById(R.id.sumLabel);
        mCurrencySelector = (Spinner) view.findViewById(R.id.currencySelector);
        mCurrencySelector.setAdapter(mCurrencyAdapter);
        mCurrencySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Selected currency : " + id);

                mHolder = (CurrencyAdapter.CurrencyHolder) mCurrencySelector.getSelectedView().getTag();
                ((TextView)mCurrencySelector.getSelectedView()).setText(mHolder.isoCode + " (" + Double.toString(mHolder.quote) + ")");

                mBasketAdapter.setCurrency(mHolder);
                mTotalSumAdapter.setCurrency(mHolder);
                refreshSumLabel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No currency selected");
            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

                new AlertDialog.Builder(getActivity())
                        .setMessage("Do you really want to remove this product from the basket?")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getContentResolver().delete(ContentUris.withAppendedId(ShoppingBasketContract.CONTENT_URI, id), null, null);
                            }
                        }).show();

                return true;
            }
        });
    }
}
