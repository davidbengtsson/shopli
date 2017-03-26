package ch.dben.shopli;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ch.dben.shopli.content.ProductsContract;
import ch.dben.shopli.currencylayer.RequestHandler;
import ch.dben.shopli.ui.ProductOverviewFragment;
import ch.dben.shopli.ui.ShoppingBasketFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProductOverviewFragment.OnListFragmentInteractionListener {

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

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            initData();

            getFragmentManager().beginTransaction()
                    .replace(R.id.content, new ProductOverviewFragment())
                    .commit();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.basket);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Shopping basket", Snackbar.LENGTH_LONG)
                        .setAction("Open", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "Time to show shopping basket");
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.content, new ShoppingBasketFragment())
                                        .addToBackStack(null)
                                        .commit();
                            }
                        }).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initData() {

        ContentResolver resolver = getContentResolver();

        ContentValues[] valuesProducts = new ContentValues[4];
        valuesProducts[0] = new ContentValues();
        valuesProducts[0].put(ProductsContract.Columns.COLUMN_DESCRIPTION, "Tomatoes");
        valuesProducts[0].put(ProductsContract.Columns.COLUMN_PRICE, 95);
        valuesProducts[0].put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "bag");

        valuesProducts[1] = new ContentValues();
        valuesProducts[1].put(ProductsContract.Columns.COLUMN_DESCRIPTION, "Eggs");
        valuesProducts[1].put(ProductsContract.Columns.COLUMN_PRICE, 210);
        valuesProducts[1].put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "dozen");

        valuesProducts[2] = new ContentValues();
        valuesProducts[2].put(ProductsContract.Columns.COLUMN_DESCRIPTION, "Milk");
        valuesProducts[2].put(ProductsContract.Columns.COLUMN_PRICE, 130);
        valuesProducts[2].put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "bottle");

        valuesProducts[3] = new ContentValues();
        valuesProducts[3].put(ProductsContract.Columns.COLUMN_DESCRIPTION, "Beans");
        valuesProducts[3].put(ProductsContract.Columns.COLUMN_PRICE, 73);
        valuesProducts[3].put(ProductsContract.Columns.COLUMN_PRICE_UNIT, "can");

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onProductSelected(long id) {
        Log.d(TAG, "Product selected: " + id);
        ShoppingBasketHelper.getsInstance().addToBasket(id, 1);

        Toast.makeText(this, R.string.notification_added_to_basket, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            ShoppingBasketHelper.getsInstance().clearBasket();
        }
    }
}
