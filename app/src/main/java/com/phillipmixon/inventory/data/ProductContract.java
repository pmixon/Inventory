package com.phillipmixon.inventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by phill on 1/24/2018.
 */

public class ProductContract {

    private ProductContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.phillipmixon.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "product";

    public static final class ProductEntry implements BaseColumns {



    }
}
