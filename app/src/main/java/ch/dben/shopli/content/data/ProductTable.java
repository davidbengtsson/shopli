package ch.dben.shopli.content.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import static ch.dben.shopli.content.ProductsContract.Columns.*;

public class ProductTable {

    public static final String TABLE_NAME = "product";

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_PRICE + " INTEGER DEFAULT 0 CHECK(" + COLUMN_PRICE + " >= 0), "
            + COLUMN_PRICE_UNIT + " CHAR(10) NOT NULL CHECK(" + COLUMN_PRICE_UNIT + " IN ('bag', 'dozen', 'bottle', 'can')), "
            + "UNIQUE(" + COLUMN_DESCRIPTION  + ","  + COLUMN_PRICE_UNIT + ") ON CONFLICT REPLACE"
            + ");";

    static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ProductTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public static void checkProjection(String... projection) {
        String[] available = {
                COLUMN_ID,
                COLUMN_DESCRIPTION,
                COLUMN_PRICE,
                COLUMN_PRICE_UNIT
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
