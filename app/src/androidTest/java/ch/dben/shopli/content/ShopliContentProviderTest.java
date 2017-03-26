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

    private static final String TEST_PRODUCT_DESCRIPTION = "test";
    private static final String TEST_PRODUCT_PRICE_UNIT = "bag";
    private static final int TEST_PRODUCT_PRICE = 123;

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
        mMockResolver.insert(ProductsContract.CONTENT_URI, getProductContentValues());

        Cursor cursor = mMockResolver.query(ProductsContract.CONTENT_URI, null, null, new String[]{}, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(TEST_PRODUCT_DESCRIPTION, cursor.getString(cursor.getColumnIndex(ProductsContract.Columns.COLUMN_DESCRIPTION)));
        assertEquals(TEST_PRODUCT_PRICE, cursor.getInt(cursor.getColumnIndex(ProductsContract.Columns.COLUMN_PRICE)));
        assertEquals(TEST_PRODUCT_PRICE_UNIT, cursor.getString(cursor.getColumnIndex(ProductsContract.Columns.COLUMN_PRICE_UNIT)));
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
        assertEquals(TEST_PRODUCT_DESCRIPTION, cursor.getString(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_DESCRIPTION)));
        assertEquals(TEST_PRODUCT_PRICE, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_PRICE)));
        assertEquals(TEST_PRODUCT_PRICE_UNIT, cursor.getString(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_PRICE_UNIT)));
        assertEquals(5, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_QUANTITY)));
    }

    public void testShoppingBasketView() {
        Uri uri = mMockResolver.insert(ProductsContract.CONTENT_URI, getProductContentValues());
        long id = ContentUris.parseId(uri);
        assertEquals(1L, id);

        mMockResolver.insert(ShoppingBasketContract.CONTENT_URI, getBasketContentValues(id, 1));
        mMockResolver.insert(ShoppingBasketContract.CONTENT_URI, getBasketContentValues(id, 1));
        mMockResolver.insert(ShoppingBasketContract.CONTENT_URI, getBasketContentValues(id, 1));
        mMockResolver.insert(ShoppingBasketContract.CONTENT_URI, getBasketContentValues(id, 1));
        mMockResolver.insert(ShoppingBasketContract.CONTENT_URI, getBasketContentValues(id, 1));


        String altProductUnit = "can";
        int altProductPrice = 321;
        uri = mMockResolver.insert(ProductsContract.CONTENT_URI, getProductContentValues(TEST_PRODUCT_DESCRIPTION, altProductPrice, altProductUnit));
        id = ContentUris.parseId(uri);
        assertEquals(2L, id);

        mMockResolver.insert(ShoppingBasketContract.CONTENT_URI, getBasketContentValues(id, 2));

        Cursor cursor = mMockResolver.query(ShoppingBasketContract.CONTENT_URI, null, null, null, null);

        assertNotNull(cursor);
        assertEquals(2, cursor.getCount());

        assertTrue(cursor.moveToFirst());

        assertEquals(5, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_QUANTITY)));
        assertEquals(5 * TEST_PRODUCT_PRICE, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_COST)));

        cursor.moveToNext();

        assertEquals(2, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_QUANTITY)));
        assertEquals(2 * altProductPrice, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.Columns.COLUMN_COST)));

        cursor = mMockResolver.query(ShoppingBasketContract.TotalCost.CONTENT_URI, null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(5 * TEST_PRODUCT_PRICE + 2 * altProductPrice, cursor.getInt(cursor.getColumnIndex(ShoppingBasketContract.TotalCost.Columns.COLUMN_TOTAL_COST)));
    }

    public void testCurrencyCodeMustContain3Characters() {
        Uri uri = mMockResolver.insert(CurrencyContract.CONTENT_URI, getCurrencyValues("A", "name"));
        assertEquals(-1L, ContentUris.parseId(uri));

        uri = mMockResolver.insert(CurrencyContract.CONTENT_URI, getCurrencyValues("AA", "name"));
        assertEquals(-1L, ContentUris.parseId(uri));

        uri = mMockResolver.insert(CurrencyContract.CONTENT_URI, getCurrencyValues("AAAA", "name"));
        assertEquals(-1L, ContentUris.parseId(uri));

        uri = mMockResolver.insert(CurrencyContract.CONTENT_URI, getCurrencyValues("AAA", "name"));
        assertEquals(1L, ContentUris.parseId(uri));
    }

    public void testUniqueCaseInsensitiveCurrencyCodes() {

        Uri uri  = mMockResolver.insert(CurrencyContract.CONTENT_URI, getCurrencyValues("AAA", "name"));
        assertEquals(1L, ContentUris.parseId(uri));

        uri = mMockResolver.insert(CurrencyContract.CONTENT_URI, getCurrencyValues("BBB", "name"));
        assertEquals(2L, ContentUris.parseId(uri));

        uri = mMockResolver.insert(CurrencyContract.CONTENT_URI, getCurrencyValues("aAa", "name"));
        assertEquals(-1L, ContentUris.parseId(uri));
    }

    public void testUpdatesExistingQuote() {
        String testIsoCode = "ABC";
        double testQuote = 1.23;
        String testName =  "Very long and complicated name";

        Uri uri = mMockResolver.insert(CurrencyContract.CONTENT_URI, getCurrencyValues(testIsoCode, testName));
        assertEquals(1L, ContentUris.parseId(uri));

        Cursor cursor = mMockResolver.query(CurrencyContract.getIsoContentUri(testIsoCode), null, null, null, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        assertTrue(cursor.moveToFirst());
        assertEquals(1.0, cursor.getDouble(cursor.getColumnIndex(CurrencyContract.Columns.COLUMN_QUOTE)));

        int updated = mMockResolver.update(CurrencyContract.getIsoContentUri(testIsoCode), getCurrencyValues(testQuote), null, null);
        assertEquals(1, updated);

        cursor = mMockResolver.query(CurrencyContract.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        assertTrue(cursor.moveToFirst());
        assertEquals(testIsoCode, cursor.getString(cursor.getColumnIndex(CurrencyContract.Columns.COLUMN_ISO)));
        assertEquals(testQuote, cursor.getDouble(cursor.getColumnIndex(CurrencyContract.Columns.COLUMN_QUOTE)));
        assertEquals(testName, cursor.getString(cursor.getColumnIndex(CurrencyContract.Columns.COLUMN_NAME)));
    }

    private ContentValues getProductContentValues() {
        return getProductContentValues(TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE, TEST_PRODUCT_PRICE_UNIT);
    }

    private ContentValues getProductContentValues(String description, int price, String priceUnit) {
        ContentValues values = new ContentValues();
        values.put(ProductsContract.Columns.COLUMN_DESCRIPTION, description);
        values.put(ProductsContract.Columns.COLUMN_PRICE, price);
        values.put(ProductsContract.Columns.COLUMN_PRICE_UNIT, priceUnit);

        return values;
    }

    private ContentValues getBasketContentValues(long id, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ShoppingBasketContract.Columns.COLUMN_PRODUCT_ID, id);
        values.put(ShoppingBasketContract.Columns.COLUMN_QUANTITY, quantity);

        return values;
    }

    public ContentValues getCurrencyValues(String isoCode, String name) {
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.Columns.COLUMN_ISO, isoCode);
        values.put(CurrencyContract.Columns.COLUMN_NAME, name);

        return values;
    }

    public ContentValues getCurrencyValues(double quote) {
        ContentValues values = new ContentValues();

        values.put(CurrencyContract.Columns.COLUMN_QUOTE, quote);

        return values;
    }
}