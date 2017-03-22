package ch.dben.shopli.content.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import ch.dben.shopli.content.ShoppingBasketContract;

public class ShoppingBasketView implements ShoppingBasketContract.Columns {

    private static final String TAG = ShoppingBasketView.class.getSimpleName();

    public static final String VIEW_NAME = "shoppingbasketview";

    private static final String DATABASE_CREATE = "CREATE VIEW " + VIEW_NAME + " AS "
            + "SELECT "
            + ShoppingBasketTable.TABLE_NAME + "." + ShoppingBasketTable.COLUMN_ID + " AS " + COLUMN_ID + ", "
            + "SUM(" + ShoppingBasketTable.TABLE_NAME + "." + ShoppingBasketTable.COLUMN_QUANTITY + ") AS " + COLUMN_QUANTITY + ", "
            + ShoppingBasketTable.TABLE_NAME + "." + ShoppingBasketTable.COLUMN_PRODUCT_ID + " AS " + COLUMN_PRODUCT_ID + ", "
            + ProductTable.TABLE_NAME + "." + ProductTable.COLUMN_DESCRIPTION + " AS " + COLUMN_DESCRIPTION + ", "
            + ProductTable.TABLE_NAME + "." + ProductTable.COLUMN_PRICE + " AS " + COLUMN_PRICE + ", "
            + ProductTable.TABLE_NAME + "." + ProductTable.COLUMN_PRICE_UNIT + " AS " + COLUMN_PRICE_UNIT + ", "
            + "SUM(" + COLUMN_QUANTITY + "*" + COLUMN_PRICE + ") AS " + COLUMN_COST
            + " FROM "
            + ShoppingBasketTable.TABLE_NAME
            + " INNER JOIN " + ProductTable.TABLE_NAME
            + " ON " + ShoppingBasketTable.TABLE_NAME + "." + ShoppingBasketTable.COLUMN_PRODUCT_ID + " = " + ProductTable.TABLE_NAME + "." + ProductTable.COLUMN_ID
            + " GROUP BY " + COLUMN_PRODUCT_ID;

    static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP VIEW IF EXISTS " + VIEW_NAME);
        onCreate(database);
    }

    public static void checkProjection(String... projection) {
        String[] available = {
                COLUMN_ID,
                COLUMN_PRODUCT_ID,
                COLUMN_QUANTITY,
                COLUMN_DESCRIPTION,
                COLUMN_PRICE,
                COLUMN_PRICE_UNIT,
                COLUMN_COST
        };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));

            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown column(s) in projection");
            }
        }
    }
}
