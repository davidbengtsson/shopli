package ch.dben.shopli.content.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopli.db";
    private static final int DATABASE_VERSION = 7;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        CurrencyTable.onCreate(database);
        ProductTable.onCreate(database);
        ShoppingBasketTable.onCreate(database);
        ShoppingBasketView.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        CurrencyTable.onUpgrade(database, oldVersion, newVersion);
        ProductTable.onUpgrade(database, oldVersion, newVersion);
        ShoppingBasketTable.onUpgrade(database, oldVersion, newVersion);
        ShoppingBasketView.onUpgrade(database, oldVersion, newVersion);
    }
}
