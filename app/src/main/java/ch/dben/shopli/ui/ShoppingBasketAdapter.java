package ch.dben.shopli.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import ch.dben.shopli.R;
import ch.dben.shopli.content.ShoppingBasketContract;

public class ShoppingBasketAdapter extends CursorAdapter {

    private final NumberFormat mCurrencyFormatter;

    private int columnIndexId;
    private int columnIndexProductId;
    private int columnIndexDescription;
    private int columnIndexQuantity;
    private int columnIndexPrice;
    private int columnIndexPriceUnit;
    private int columnIndexCost;
    private CurrencyAdapter.CurrencyHolder mHolder;

    public ShoppingBasketAdapter(Context context, Cursor cursor) {
        super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mCurrencyFormatter = NumberFormat.getInstance();
        mCurrencyFormatter.setMaximumFractionDigits(2);
        mCurrencyFormatter.setMinimumFractionDigits(2);
        Currency currency = Currency.getInstance("USD");
        mCurrencyFormatter.setCurrency(currency);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.fragment_basket_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView descriptionLabel = (TextView) view.findViewById(R.id.description);
        TextView quantityLabel = (TextView) view.findViewById(R.id.quantity);
        TextView priceLabel = (TextView) view.findViewById(R.id.price);

        descriptionLabel.setText(cursor.getString(columnIndexDescription));

        int quantity = cursor.getInt(columnIndexQuantity);
        String unit = cursor.getString(columnIndexPriceUnit);
        int cost = cursor.getInt(columnIndexCost);
        quantityLabel.setText(String.format(Locale.ROOT, "%d %s", quantity, unit));
        priceLabel.setText(String.format("%s %s", mCurrencyFormatter.format((cost * (mHolder != null ? mHolder.quote : 1.0f))/100), mHolder != null ? mHolder.isoCode : "USD"));
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {

        if (newCursor != null) {
            columnIndexId = newCursor.getColumnIndexOrThrow(ShoppingBasketContract.Columns.COLUMN_ID);
            columnIndexProductId = newCursor.getColumnIndexOrThrow(ShoppingBasketContract.Columns.COLUMN_PRODUCT_ID);
            columnIndexDescription = newCursor.getColumnIndexOrThrow(ShoppingBasketContract.Columns.COLUMN_DESCRIPTION);
            columnIndexQuantity = newCursor.getColumnIndexOrThrow(ShoppingBasketContract.Columns.COLUMN_QUANTITY);
            columnIndexPrice = newCursor.getColumnIndexOrThrow(ShoppingBasketContract.Columns.COLUMN_PRICE);
            columnIndexPriceUnit = newCursor.getColumnIndexOrThrow(ShoppingBasketContract.Columns.COLUMN_PRICE_UNIT);
            columnIndexCost = newCursor.getColumnIndexOrThrow(ShoppingBasketContract.Columns.COLUMN_COST);
        }

        return super.swapCursor(newCursor);
    }

    public void setCurrency(CurrencyAdapter.CurrencyHolder holder) {

        mHolder = holder;
        mCurrencyFormatter.setCurrency(Currency.getInstance(holder.isoCode));
        notifyDataSetChanged();
    }
}
