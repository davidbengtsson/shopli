package ch.dben.shopli.content;


import android.content.ContentResolver;
import android.net.Uri;

public class ShoppingBasketContract {

    protected static final String BASE_PATH = "basket";

    public static final Uri CONTENT_URI = Uri.parse("content://" + ShopliContentProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/products";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/product";

    public interface Entity extends BaseColumns {
        String COLUMN_QUANTITY = "quantity";
        String COLUMN_PRODUCT_ID = "product_id";
    }

    public interface Columns extends Entity, ProductsContract.Columns {
        String COLUMN_COST = "cost";
    }

    public static class TotalCost {

        public static final Uri CONTENT_URI = ShoppingBasketContract.CONTENT_URI.buildUpon().appendPath("sum").build();

        protected static final String BASE_PATH = CONTENT_URI.getPath();

        public interface Columns {
            String COLUMN_TOTAL_COST = "total_cost";
        }
    }

    private ShoppingBasketContract() {
    }
}
