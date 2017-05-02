package com.example.android.alpacassoinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by novembergave on 02/01/2017.
 */

public class AlpacassoContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.alpacassoinventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ALPACASSO = "alpacasso";

    private void AlpacassoContract() {
    }

    public static final class AlpacassoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ALPACASSO);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALPACASSO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALPACASSO;
        public final static String TABLE_NAME = "alpacassos";
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_SERIES_NAME = "series";
        public final static String COLUMN_COLOUR = "colour";
        public final static String COLUMN_SIZE = "size";
        public final static String COLUMN_STOCK_STATUS = "stockstatus";
        public final static String COLUMN_STOCK_LEVEL = "stock";
        public final static String COLUMN_RESTOCK_AMOUNT = "restock";
        public final static String COLUMN_UNIT_PRICE = "price";

        public static final int SIZE_SMALL = 0;
        public static final int SIZE_MEDIUM = 1;
        public static final int SIZE_LARGE = 2;

        public static final int STOCK_IN = 0;
        public static final int STOCK_OUT = 1;

        public static boolean isValidSize(int size) {
            if (size == SIZE_SMALL || size == SIZE_MEDIUM || size == SIZE_LARGE) {
                return true;
            }
            return false;
        }

        public static boolean isValidStock(int stock) {
            if (stock == STOCK_IN || stock == STOCK_OUT) {
                return true;
            }
            return false;
        }

    }
}
