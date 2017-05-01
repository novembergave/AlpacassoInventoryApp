package com.example.android.alpacassoinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.alpacassoinventoryapp.data.AlpacassoContract.AlpacassoEntry;

/**
 * Created by novembergave on 02/01/2017.
 */

public class AlpacassoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = AlpacassoDbHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    public AlpacassoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_INVENTORY_TABLE =
                "CREATE TABLE " + AlpacassoEntry.TABLE_NAME + " ("
                        + AlpacassoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + AlpacassoEntry.COLUMN_SERIES_NAME + " TEXT NOT NULL, "
                        + AlpacassoEntry.COLUMN_COLOUR + " TEXT NOT NULL, "
                        + AlpacassoEntry.COLUMN_SIZE + " INTEGER NOT NULL, "
                        + AlpacassoEntry.COLUMN_STOCK_STATUS + " INTEGER NOT NULL, "
                        + AlpacassoEntry.COLUMN_STOCK_LEVEL + " INTEGER NOT NULL, "
                        + AlpacassoEntry.COLUMN_RESTOCK_AMOUNT + " INTEGER NOT NULL, "
                        + AlpacassoEntry.COLUMN_UNIT_PRICE + " REAL NOT NULL DEFAULT 0, "
                        + AlpacassoEntry.COLUMN_IMAGE + " BLOB); ";

        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //nothing to do here as database is still on version 1
    }
}
