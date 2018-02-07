package com.phillipmixon.inventory;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.phillipmixon.inventory.data.ProductContract;

/**
 * Created by phill on 1/24/2018.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, viewGroup, false);
    }

    public void updateQuantity(Context context, String position, int value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, value);

        Uri uri = Uri.withAppendedPath(ProductContract.ProductEntry.CONTENT_URI, position);

        context.getContentResolver().update(uri, contentValues, null, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price);

        int productNameColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int producQuantityColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int productPriceColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);

        productNameTextView.setText(cursor.getString(productNameColumnId));
        productQuantityTextView.setText(Integer.toString(cursor.getInt(producQuantityColumnId)));

        Log.d(this.getClass().getSimpleName(), "Price value: " + cursor.getInt(productPriceColumnId));
        int price = cursor.getInt(productPriceColumnId);

        productPriceTextView.setText("$" + Integer.toString(price));

        int productIdColumnId = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        final String productId = cursor.getString(productIdColumnId);
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));

        Button decreaseButton = (Button) view.findViewById(R.id.decrease_quantity_button);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    updateQuantity(context, productId, quantity - 1);
                }
            }
        });

    }
}
