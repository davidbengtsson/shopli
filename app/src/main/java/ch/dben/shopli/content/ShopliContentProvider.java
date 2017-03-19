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

public class ShopliContentProvider extends ContentProvider {

    protected static final String AUTHORITY = "ch.dben.shopli.contentprovider";

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, ProductsContract.BASE_PATH, PRODUCTS);
        sURIMatcher.addURI(AUTHORITY, ProductsContract.BASE_PATH + "/#", PRODUCT_ID);
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
        ProductTable.checkProjection(projection);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ProductTable.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PRODUCTS:
                break;
            case PRODUCT_ID:
                // append the ID to the query
                queryBuilder.appendWhere(ProductsContract.Columns.COLUMN_ID + "=" + uri.getLastPathSegment());
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

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        switch (uriType) {
            case PRODUCTS:
                id = sqlDB.insert(ProductTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(ProductsContract.BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new IllegalStateException("Delete not implemented, yet");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new IllegalStateException("Update not implemented, yet");
    }
}
