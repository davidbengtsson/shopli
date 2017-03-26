package ch.dben.shopli.content;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;

public class CurrencyContract {

    protected static final String BASE_PATH = "currencies";

    public static final Uri CONTENT_URI = Uri.parse("content://" + ShopliContentProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/currencies";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/currency";

    @NonNull
    public static Uri getIsoContentUri(@NonNull String isoCode) {
        if (isoCode.length() != 3) {
            return CONTENT_URI;
        }
        return CONTENT_URI.buildUpon().appendPath(isoCode).build();
    }

    public interface Columns extends BaseColumns {
        String COLUMN_ISO = "iso";
        String COLUMN_NAME = "name";
        String COLUMN_QUOTE = "quote";
    }

    private CurrencyContract() {
    }
}
