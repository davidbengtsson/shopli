package ch.dben.shopli;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import ch.dben.shopli.content.ShoppingBasketContract;

public class ShoppingBasketHelper {

    private static ShoppingBasketHelper sInstance;

    private final ContentResolver mContentResolver;

    public static void init(Context context) {
        sInstance = new ShoppingBasketHelper(context.getContentResolver());
    }

    public static ShoppingBasketHelper getsInstance() {
        if (sInstance != null) {
            return sInstance;
        }

        throw new IllegalStateException("Must call init first");
    }

    private ShoppingBasketHelper(ContentResolver resolver) {
        mContentResolver = resolver;
    }

    public void addToBasket(long id, int quantity) {

        ContentValues values = new ContentValues();
        values.put(ShoppingBasketContract.Columns.COLUMN_PRODUCT_ID, id);
        values.put(ShoppingBasketContract.Columns.COLUMN_QUANTITY, quantity);

        mContentResolver.insert(ShoppingBasketContract.CONTENT_URI, values);
    }

    public void clearBasket() {
        mContentResolver.delete(ShoppingBasketContract.CONTENT_URI, null, null);
    }
}
