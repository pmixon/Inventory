package com.phillipmixon.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.phillipmixon.inventory.data.ProductContract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class EditProduct extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mProductNameEditText;
    private EditText mProductQuantityEditText;
    private EditText mProductPriceEditText;
    private ImageView mProductImageView;
    private Bitmap mProductBitmap;
    private Button mIncreaseQuantityButton;
    private Button mDecreaseQuantityButton;
    private Button mOrderButton;

    private Uri mProductUri;
    private static final int PRODUCT_LOADER_ID = 1;
    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mProductQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mProductPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mProductImageView = (ImageView) findViewById(R.id.product_image_view);
        mIncreaseQuantityButton = (Button) findViewById(R.id.quantity_increase_button);
        mDecreaseQuantityButton = (Button) findViewById(R.id.quantity_decrease_button);
        mOrderButton = (Button) findViewById(R.id.order_more_button);

        Intent intent = getIntent();

        if (intent.getData() != null) {
            mProductUri = intent.getData();
            getLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
        }

        Button selectImage = (Button) findViewById(R.id.select_image);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });

        mIncreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity;
                String quantityString = mProductQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantityString)) {
                    quantity = 0;
                } else {
                    quantity = Integer.valueOf(quantityString);
                }
                quantity++;
                mProductQuantityEditText.setText(String.valueOf(quantity));
            }
        });

        mDecreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity;
                String quantityString = mProductQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantityString)) {
                    quantity = 0;
                } else {
                    quantity = Integer.valueOf(quantityString);
                }
                if (quantity > 0) {
                    quantity--;
                }
                mProductQuantityEditText.setText(String.valueOf(quantity));
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mProductUri == null) {
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, "phillipmixon@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order for product: " + mProductNameEditText.getText());
                intent.putExtra(Intent.EXTRA_TEXT, "I would like to order product: " +
                        mProductNameEditText.getText() + " and sell at Price: " +
                        mProductPriceEditText.getText() + " I currently have in stock: " + mProductQuantityEditText.getText());

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mProductBitmap != null) {
            mProductImageView.setImageBitmap(mProductBitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.editor_save) {
            saveProduct();
        } else if (item.getItemId() == R.id.editor_delete) {
            if (mProductUri != null) {
                deleteProduct();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm Product deletion?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int column;
                column = getContentResolver().delete(mProductUri, null, null);
                if (dialog != null) {
                    dialog.dismiss();
                    finish();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct() {

        String productName = mProductNameEditText.getText().toString();
        String productQuantity = mProductQuantityEditText.getText().toString();
        String productPrice = mProductPriceEditText.getText().toString();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productQuantity) || TextUtils.isEmpty(productPrice) || mProductBitmap == null) {
            Toast.makeText(this, "Invalid product values!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);

        if (mProductBitmap != null) {
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, DbBitmapUtility.getBytes(mProductBitmap));
        }

        if (mProductUri != null) {
            getContentResolver().update(mProductUri, contentValues, null, null);
        } else {
            getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, contentValues);
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String projection[] = new String[]{
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE
        };

        return new CursorLoader(this, mProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(this.getClass().getSimpleName(), " loader ID: " + loader.getId());
        if (cursor.moveToFirst()) {

            int productNameColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int productQuantityId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int productPriceColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int productImageColumnId = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);

            mProductNameEditText.setText(cursor.getString(productNameColumnId));
            mProductQuantityEditText.setText(cursor.getString(productQuantityId));
            mProductPriceEditText.setText(cursor.getString(productPriceColumnId));

            byte[] byteArray = cursor.getBlob(productImageColumnId);

            if (cursor.getBlob(productImageColumnId) != null) {
                mProductBitmap = DbBitmapUtility.getImage(cursor.getBlob(productImageColumnId));
                mProductImageView.setImageBitmap(mProductBitmap);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductQuantityEditText.setText("");
        mProductPriceEditText.setText("");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            mProductBitmap = getScaledBitmap(picturePath, 600, 600);
            mProductImageView.setImageBitmap(mProductBitmap);
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
