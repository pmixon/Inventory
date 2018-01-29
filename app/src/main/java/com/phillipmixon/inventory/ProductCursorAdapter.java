package com.phillipmixon.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price);

        int productNameColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int producQuantityColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int productPriceColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);

        productNameTextView.setText(cursor.getString(productNameColumnId));
//        productQuantityTextView.setText(cursor.getInt(producQuantityColumnId));
//        productPriceTextView.setText(cursor.getInt(productPriceColumnId));


    }
}
