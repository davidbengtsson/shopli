package ch.dben.shopli.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ch.dben.shopli.R;
import ch.dben.shopli.content.ProductsContract;

public class ProductCursorAdapter extends CursorAdapter {

    private int columnIndexId;
    private int columnIndexDescription;

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.fragment_product_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView id = (TextView) view.findViewById(R.id.id);
        TextView content = (TextView) view.findViewById(R.id.content);

        id.setText(cursor.getString(columnIndexId));
        content.setText(cursor.getString(columnIndexDescription));
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {

        if (newCursor != null) {
            columnIndexId = newCursor.getColumnIndexOrThrow(ProductsContract.Columns.COLUMN_ID);
            columnIndexDescription = newCursor.getColumnIndexOrThrow(ProductsContract.Columns.COLUMN_DESCRIPTION);
        }

        return super.swapCursor(newCursor);
    }
}
