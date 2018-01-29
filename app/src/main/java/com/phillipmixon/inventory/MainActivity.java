package com.phillipmixon.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.phillipmixon.inventory.data.ProductContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PRODUCT_LOADER = 0;
    private ProductCursorAdapter mProductCursorAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_insert_dummy_data) {
            insertDummyProduct();
            return true;
        } else if (item.getItemId() == R.id.action_delete_all_products) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        ListView productListView = (ListView) findViewById(R.id.product_list_view);
        productListView.setAdapter(mProductCursorAdapter);

        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
        };

        return new CursorLoader(this, ProductContract.ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mProductCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdapter.swapCursor(null);
    }


    public void insertDummyProduct() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,"Dummy Name");
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,99);
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,58);

        getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI,contentValues);
    }
}
