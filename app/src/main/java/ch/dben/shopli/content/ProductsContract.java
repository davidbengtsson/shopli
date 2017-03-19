package ch.dben.shopli.content;

import android.content.ContentResolver;
import android.net.Uri;

public class ProductsContract {

    protected static final String BASE_PATH = "products";

    public static final Uri CONTENT_URI = Uri.parse("content://" + ShopliContentProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/products";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/product";

    public static class Columns {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PRICE_UNIT = "price_unit";
    }

    private ProductsContract() {
    }
}
