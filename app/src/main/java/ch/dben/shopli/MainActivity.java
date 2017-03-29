package ch.dben.shopli;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import ch.dben.shopli.content.ProductsContract;
import ch.dben.shopli.currencylayer.RequestHandler;
import ch.dben.shopli.ui.ProductOverviewFragment;
import ch.dben.shopli.ui.ShoppingBasketFragment;

public class MainActivity extends AppCompatActivity implements ProductOverviewFragment.OnListFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_IDENTIFIER = 0x1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                new RequestHandler(this).refreshData();

            } else {
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.
                    requestPermissions(new String[]{Manifest.permission.INTERNET}, PERMISSIONS_REQUEST_IDENTIFIER);
                }
            }
        }

        ShoppingBasketHelper.init(getApplicationContext());

        setContentView(R.layout.content_main);
        if (savedInstanceState == null) {
            initData();

            getFragmentManager().beginTransaction()
                    .replace(R.id.content, new ProductOverviewFragment())
                    .commit();
        }
    }

    private void initData() {

        ContentResolver resolver = getContentResolver();

        ContentValues[] valuesProducts = new ContentValues[4];
        valuesProducts[0] = new ContentValues();
        valuesProducts[0].put(ProductsContract.Columns.COLUMN_DESCRIPTION, "Tomatoes");
        valuesProducts[0].put(ProductsContract.Columns.COLUMN_PRICE, 95);
        valuesProducts[0].put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "bag");
        valuesProducts[0].put(ProductsContract.Columns.COLUMN_IMAGE_RESOURCE, R.drawable.tomatoes);

        valuesProducts[1] = new ContentValues();
        valuesProducts[1].put(ProductsContract.Columns.COLUMN_DESCRIPTION, "Eggs");
        valuesProducts[1].put(ProductsContract.Columns.COLUMN_PRICE, 210);
        valuesProducts[1].put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "dozen");
        valuesProducts[1].put(ProductsContract.Columns.COLUMN_IMAGE_RESOURCE, R.drawable.eggs);

        valuesProducts[2] = new ContentValues();
        valuesProducts[2].put(ProductsContract.Columns.COLUMN_DESCRIPTION, "Milk");
        valuesProducts[2].put(ProductsContract.Columns.COLUMN_PRICE, 130);
        valuesProducts[2].put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "bottle");
        valuesProducts[2].put(ProductsContract.Columns.COLUMN_IMAGE_RESOURCE, R.drawable.milk);

        valuesProducts[3] = new ContentValues();
        valuesProducts[3].put(ProductsContract.Columns.COLUMN_DESCRIPTION, "Beans");
        valuesProducts[3].put(ProductsContract.Columns.COLUMN_PRICE, 73);
        valuesProducts[3].put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "can");
        valuesProducts[3].put(ProductsContract.Columns.COLUMN_IMAGE_RESOURCE, R.drawable.beans);

        resolver.bulkInsert(ProductsContract.CONTENT_URI, valuesProducts);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_IDENTIFIER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new RequestHandler(this).refreshData();

                } else {
                    // permission denied
                }
            }
        }
    }

    @Override
    public void onProductSelected(long id) {
        Log.d(TAG, "Product selected: " + id);

        ShoppingBasketHelper.getsInstance().addToBasket(id, 1);

        Toast.makeText(this, R.string.notification_added_to_basket, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckoutShoppingBasket() {
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new ShoppingBasketFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            ShoppingBasketHelper.getsInstance().clearBasket();
        }
    }
}
