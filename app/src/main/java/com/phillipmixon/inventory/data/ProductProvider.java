package com.phillipmixon.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by pmixon on 1/25/18.
 */

public class ProductProvider extends ContentProvider {

    private ProductDbHelper mProductDbHelper;

    public static final int PRODUCTS = 100;
    public static final int PRODUCT_ID = 101;

    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);

    }

    @Override
    public boolean onCreate() {
        mProductDbHelper = new ProductDbHelper(this.getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mProductDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:

                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return insertProduct(uri,contentValues);
            default:
                throw new IllegalArgumentException("Invalid uri on insert: " + uri);
        }
    }

    public Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();
        long newId;
        newId = db.insert(ProductContract.ProductEntry.TABLE_NAME,null,values);

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,newId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
