package ch.dben.shopli.content.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import ch.dben.shopli.content.ProductsContract;
import ch.dben.shopli.content.ShoppingBasketContract;

public class ShoppingBasketTable implements ShoppingBasketContract.Entity {

    private static final String TAG = ShoppingBasketTable.class.getName();

    public static final String TABLE_NAME = "shoppingbasket";

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PRODUCT_ID + " INTEGER, "
            + COLUMN_QUANTITY + " INTEGER DEFAULT 0 CHECK(" + COLUMN_QUANTITY + " >= 0),  "
            + "FOREIGN KEY(" + COLUMN_PRODUCT_ID  + ") REFERENCES "  + ProductTable.TABLE_NAME + "(" + ProductsContract.Columns.COLUMN_ID + ")"
            + ");";

    static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public static void checkProjection(String... projection) {
        String[] available = {
                COLUMN_ID,
                COLUMN_PRODUCT_ID,
                COLUMN_QUANTITY
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
