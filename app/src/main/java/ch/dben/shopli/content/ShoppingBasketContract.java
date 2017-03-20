package ch.dben.shopli.content;


import android.content.ContentResolver;
import android.net.Uri;

public class ShoppingBasketContract {

    protected static final String BASE_PATH = "basket";

    public static final Uri CONTENT_URI = Uri.parse("content://" + ShopliContentProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/products";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/product";

    public interface Columns extends ProductsContract.Columns {
        String COLUMN_QUANTITY = "quantity";
        String COLUMN_PRODUCT_ID = "product_id";
    }

    private ShoppingBasketContract() {
    }
}
