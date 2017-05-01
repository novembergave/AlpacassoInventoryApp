package com.example.android.alpacassoinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.alpacassoinventoryapp.R;
import com.example.android.alpacassoinventoryapp.data.AlpacassoContract.AlpacassoEntry;

/**
 * Created by novembergave on 02/01/2017.
 */

public class AlpacassoProvider extends ContentProvider {
    public static final String LOG_TAG = AlpacassoProvider.class.getSimpleName();

    private static final int ALPACASSO = 100;
    private static final int ALPACASSO_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AlpacassoContract.CONTENT_AUTHORITY, AlpacassoContract.PATH_ALPACASSO, ALPACASSO);
        sUriMatcher.addURI(AlpacassoContract.CONTENT_AUTHORITY, AlpacassoContract.PATH_ALPACASSO + "/#",
                ALPACASSO_ID);
    }

    private AlpacassoDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new AlpacassoDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ALPACASSO:
                cursor = database.query(AlpacassoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ALPACASSO_ID:
                selection = AlpacassoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(AlpacassoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALPACASSO:
                return AlpacassoEntry.CONTENT_LIST_TYPE;
            case ALPACASSO_ID:
                return AlpacassoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALPACASSO:
                return insertAlpacasso(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertAlpacasso(Uri uri, ContentValues contentValues) {
        // Check that the name is not null
        String seriesName = contentValues.getAsString(AlpacassoEntry.COLUMN_SERIES_NAME);
        if (seriesName == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.error_series_required));
        }

        String colour = contentValues.getAsString(AlpacassoEntry.COLUMN_COLOUR);
        if (colour == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.error_colour_required));
        }

        String stockLevel = contentValues.getAsString(AlpacassoEntry.COLUMN_STOCK_LEVEL);
        if (stockLevel == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.error_stock_level));
        }

        Integer size = contentValues.getAsInteger(AlpacassoEntry.COLUMN_SIZE);
        if (size == null || !AlpacassoEntry.isValidSize(size)) {
            throw new IllegalArgumentException(getContext().getString(R.string.error_size_required));
        }

        Integer stockStatus = contentValues.getAsInteger(AlpacassoEntry.COLUMN_STOCK_STATUS);
        if (stockStatus == null || !AlpacassoEntry.isValidStock(stockStatus)) {
            throw new IllegalArgumentException(getContext().getString(R.string.error_stock_required));
        }

        Integer restockLevel = contentValues.getAsInteger(AlpacassoEntry.COLUMN_RESTOCK_AMOUNT);
        if (restockLevel == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.error_restock));
        }

        Float price = contentValues.getAsFloat(AlpacassoEntry.COLUMN_UNIT_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException(getContext().getString(R.string.error_price_required));
        }

        byte[] image = contentValues.getAsByteArray(AlpacassoEntry.COLUMN_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.error_image));
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(AlpacassoEntry.TABLE_NAME, null, contentValues);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALPACASSO:
                rowsDeleted = database.delete(AlpacassoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ALPACASSO_ID:
                selection = AlpacassoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(AlpacassoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALPACASSO:
                return updateAlpacasso(uri, contentValues, selection, selectionArgs);
            case ALPACASSO_ID:
                selection = AlpacassoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAlpacasso(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateAlpacasso(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(AlpacassoEntry.COLUMN_SERIES_NAME)) {
            String seriesName = values.getAsString(AlpacassoEntry.COLUMN_SERIES_NAME);
            if (seriesName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_series_required));
            }
        }
        if (values.containsKey(AlpacassoEntry.COLUMN_COLOUR)) {
            String colour = values.getAsString(AlpacassoEntry.COLUMN_COLOUR);
            if (colour == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_colour_required));
            }
        }

        if (values.containsKey(AlpacassoEntry.COLUMN_SIZE)) {
            Integer size = values.getAsInteger(AlpacassoEntry.COLUMN_SIZE);
            if (size == null || !AlpacassoEntry.isValidSize(size)) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_size_required));
            }
        }

        if (values.containsKey(AlpacassoEntry.COLUMN_STOCK_STATUS)) {
            Integer stockStatus = values.getAsInteger(AlpacassoEntry.COLUMN_STOCK_STATUS);
            if (stockStatus == null || !AlpacassoEntry.isValidStock(stockStatus)) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_stock_required));
            }
        }

        if (values.containsKey(AlpacassoEntry.COLUMN_STOCK_LEVEL)) {
            Integer stockLevel = values.getAsInteger(AlpacassoEntry.COLUMN_STOCK_LEVEL);
            if (stockLevel == null && stockLevel < 0) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_stock_level));
            }
        }

        if (values.containsKey(AlpacassoEntry.COLUMN_RESTOCK_AMOUNT)) {
            Integer stockLevel = values.getAsInteger(AlpacassoEntry.COLUMN_RESTOCK_AMOUNT);
            if (stockLevel == null && stockLevel < 0) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_restock));
            }
        }


        if (values.containsKey(AlpacassoEntry.COLUMN_UNIT_PRICE)) {
            Float price = values.getAsFloat(AlpacassoEntry.COLUMN_UNIT_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_price_required));
            }
        }

        if (values.containsKey(AlpacassoEntry.COLUMN_IMAGE)) {
            byte[] image = values.getAsByteArray(AlpacassoEntry.COLUMN_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.error_image));
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(AlpacassoEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
