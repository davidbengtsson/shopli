package ch.dben.shopli.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.util.Log;

public class ShopliContentProviderTest extends ProviderTestCase2<ShopliContentProvider> {

    private static final String TAG = ShopliContentProviderTest.class.getSimpleName();
    private ContentResolver mMockResolver;

    public ShopliContentProviderTest() {
        super(ShopliContentProvider.class, ShopliContentProvider.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.d(TAG, "setUp: ");
        mMockResolver = getMockContentResolver();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Log.d(TAG, "tearDown:");
    }

    public void testProductInsert_add_a_valid_product() {
        Uri uri = mMockResolver.insert(ProductsContract.CONTENT_URI, getProductContentValues());
        assertEquals(1L, ContentUris.parseId(uri));
    }

    public void testProductInsert_product_contains_valid_data() {
        Uri uri = mMockResolver.insert(ProductsContract.CONTENT_URI, getProductContentValues());
        Cursor cursor = mMockResolver.query(ProductsContract.CONTENT_URI, null, null, new String[] {}, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals("test", cursor.getString(cursor.getColumnIndex(ProductsContract.Columns.COLUMN_DESCRIPTION)));
        assertEquals(100, cursor.getInt(cursor.getColumnIndex(ProductsContract.Columns.COLUMN_PRICE)));
        assertEquals("bag", cursor.getString(cursor.getColumnIndex(ProductsContract.Columns.COLUMN_PRICE_UNIT)));
    }

    public void testAddProductToShoppingBasket() {
        Uri uri = mMockResolver.insert(ProductsContract.CONTENT_URI, getProductContentValues());
        long id = ContentUris.parseId(uri);
        assertEquals(1L, id);

        mMockResolver.insert(ShoppingBasketContract.CONTENT_URI, getBasketContentValues(id, 5));

        Cursor cursor = mMockResolver.query(ShoppingBasketContract.CONTENT_URI, null, null, null, null);

        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        assertTrue(cursor.moveToFirst());
        assertEquals("test", cursor.getString(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_DESCRIPTION)));
        assertEquals(100, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_PRICE)));
        assertEquals("bag", cursor.getString(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_PRICE_UNIT)));
        assertEquals(5, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_QUANTITY)));
    }

    private ContentValues getProductContentValues() {
        ContentValues values = new ContentValues();
        values.put(ProductsContract.Columns.COLUMN_DESCRIPTION, "test");
        values.put(ProductsContract.Columns.COLUMN_PRICE, 100);
        values.put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "bag");

        return values;
    }

    private ContentValues getBasketContentValues(long id, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ShoppingBasketContract.Columns.COLUMN_PRODUCT_ID, id);
        values.put(ShoppingBasketContract.Columns.COLUMN_QUANTITY, quantity);

        return values;
    }
}