package ch.dben.shopli.content.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import ch.dben.shopli.content.CurrencyContract;

public class CurrencyTable implements CurrencyContract.Columns {

    private static final String TAG = CurrencyTable.class.getName();

    public static final String TABLE_NAME = "currency";

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ISO + " CHAR(3) NOT NULL CHECK(LENGTH(" + COLUMN_ISO + ") == 3), "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_QUOTE + " REAL DEFAULT 1 CHECK(" + COLUMN_QUOTE + " > 0), "
            + "UNIQUE (" + COLUMN_ISO + " COLLATE NOCASE)"
            + ");";

    static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        if (oldVersion >= 6) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }

        // else no need to throw out data
    }

    public static void checkProjection(String... projection) {
        String[] available = {
                COLUMN_ID,
                COLUMN_ISO,
                COLUMN_NAME,
                COLUMN_QUOTE
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
