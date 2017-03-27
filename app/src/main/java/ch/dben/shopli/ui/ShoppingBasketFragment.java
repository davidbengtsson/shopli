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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Locale;

import ch.dben.shopli.R;
import ch.dben.shopli.content.ShoppingBasketContract;

public class ShoppingBasketFragment extends ListFragment {

    private CursorAdapter mAdapter;

    private final LoaderManager.LoaderCallbacks<Cursor> mBasketLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), ShoppingBasketContract.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
    };

    private final LoaderManager.LoaderCallbacks<Cursor> mBasketSumLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), ShoppingBasketContract.TotalCost.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            updateSumLabel(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            updateSumLabel(null);;
        }
    };
    private TextView mSumLabel;

    private void updateSumLabel(Cursor data) {
        if (mSumLabel != null) {

            int sum = 0;
            if (data != null && data.moveToFirst()) {
                sum = data.getInt(data.getColumnIndex(ShoppingBasketContract.TotalCost.Columns.COLUMN_TOTAL_COST));
            }

            mSumLabel.setText(String.format(Locale.ROOT, "Total cost: %.2f %s", sum/100.0f, "USD"));
        }
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

        mAdapter = new ShoppingBasketAdapter(getActivity(), null);
        getLoaderManager().initLoader(1, null, mBasketLoaderCallbacks);
        getLoaderManager().initLoader(2, null, mBasketSumLoaderCallbacks);

        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basket_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSumLabel = (TextView) view.findViewById(R.id.sumLabel);

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
