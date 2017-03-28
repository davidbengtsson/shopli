package ch.dben.shopli.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ch.dben.shopli.content.CurrencyContract;

public class CurrencyAdapter extends CursorAdapter {

    private int columnIndexIso;
    private int columnIndexName;
    private int columnIndexQuote;

    public CurrencyAdapter(Context context, Cursor cursor) {
        super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);

        view.setTag(new CurrencyHolder());

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CurrencyHolder holder = (CurrencyHolder) view.getTag();
        holder.name = cursor.getString(columnIndexName);
        holder.isoCode = cursor.getString(columnIndexIso);
        holder.quote = cursor.getDouble(columnIndexQuote);

        ((TextView) view).setText(holder.name);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {

        if (newCursor != null) {
            columnIndexIso = newCursor.getColumnIndexOrThrow(CurrencyContract.Columns.COLUMN_ISO);
            columnIndexName = newCursor.getColumnIndexOrThrow(CurrencyContract.Columns.COLUMN_NAME);
            columnIndexQuote = newCursor.getColumnIndexOrThrow(CurrencyContract.Columns.COLUMN_QUOTE);
        }

        return super.swapCursor(newCursor);
    }

    class CurrencyHolder {
        String name;
        String isoCode;
        double quote;

    }
}
