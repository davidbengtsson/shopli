package ch.dben.shopli.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.dben.shopli.content.data.DatabaseHelper;
import ch.dben.shopli.content.data.ProductTable;
import ch.dben.shopli.content.data.ShoppingBasketTable;
import ch.dben.shopli.content.data.ShoppingBasketView;

public class ShopliContentProvider extends ContentProvider {

    protected static final String AUTHORITY = "ch.dben.shopli.contentprovider";

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final int BASKET = 200;
    private static final int BASKET_ID = 201;
    private static final int BASKET_SUM = 299;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, ProductsContract.BASE_PATH, PRODUCTS);
        sURIMatcher.addURI(AUTHORITY, ProductsContract.BASE_PATH + "/#", PRODUCT_ID);

        sURIMatcher.addURI(AUTHORITY, ShoppingBasketContract.BASE_PATH, BASKET);
        sURIMatcher.addURI(AUTHORITY, ShoppingBasketContract.BASE_PATH + "/#", BASKET_ID);
        sURIMatcher.addURI(AUTHORITY, ShoppingBasketContract.TotalCost.BASE_PATH, BASKET_SUM);
    }

    private DatabaseHelper database;

    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ProductTable.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PRODUCTS:
                ProductTable.checkProjection(projection);
                queryBuilder.setTables(ProductTable.TABLE_NAME);
                break;
            case PRODUCT_ID:
                ProductTable.checkProjection(projection);
                queryBuilder.setTables(ProductTable.TABLE_NAME);
                queryBuilder.appendWhere(ProductsContract.Columns.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case BASKET:
                ShoppingBasketView.checkProjection(projection);
                queryBuilder.setTables(ShoppingBasketView.VIEW_NAME);
                break;
            case BASKET_ID:
                ShoppingBasketView.checkProjection(projection);
                queryBuilder.setTables(ShoppingBasketView.VIEW_NAME);
                queryBuilder.appendWhere(ShoppingBasketContract.Columns.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            case BASKET_SUM:
                projection = new String[]{ShoppingBasketView.PROJECTION_TOTAL_COST};
                selection = null;
                selectionArgs = null;
                queryBuilder.setTables(ShoppingBasketView.VIEW_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PRODUCTS:
                return ProductsContract.CONTENT_TYPE;

            case PRODUCT_ID:
                return ProductsContract.CONTENT_ITEM_TYPE;

            case BASKET:
                return ShoppingBasketContract.CONTENT_TYPE;

            case BASKET_ID:
                return ShoppingBasketContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PRODUCTS:
                id = sqlDB.insert(ProductTable.TABLE_NAME, null, values);
                break;

            case BASKET:
                id = sqlDB.insert(ShoppingBasketTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return uri.buildUpon().appendPath(Long.toString(id)).build();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {

            case PRODUCTS:
                tableName = ProductTable.TABLE_NAME;
                break;
            case PRODUCT_ID:
                tableName = ProductTable.TABLE_NAME;
                selection = ProductsContract.Columns.COLUMN_ID + " = " + uri.getLastPathSegment();
                break;

            case BASKET:
                tableName = ShoppingBasketTable.TABLE_NAME;
                break;

            case BASKET_ID:
                tableName = ShoppingBasketTable.TABLE_NAME;
                selection = ShoppingBasketContract.Columns.COLUMN_ID + " = " + uri.getLastPathSegment();
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase sqlDB = database.getWritableDatabase();
        return sqlDB.delete(tableName, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new IllegalStateException("Update not implemented, yet");
    }
}
