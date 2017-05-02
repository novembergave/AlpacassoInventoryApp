package com.example.android.alpacassoinventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.alpacassoinventoryapp.data.AlpacassoContract.AlpacassoEntry;

/**
 * Created by novembergave on 02/01/2017.
 */

public class AlpacassoCursorAdaptor extends CursorAdapter {

    private int stockQuantity;
    private int stockStatus;
    private TextView stockTv;
    private TextView seriesNameTv;
    private TextView sizeTv;
    private TextView colourTv;
    private TextView priceTv;
    private TextView currencyTv;

    public AlpacassoCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        seriesNameTv = (TextView) view.findViewById(R.id.item_series_name);
        sizeTv = (TextView) view.findViewById(R.id.item_size);
        colourTv = (TextView) view.findViewById(R.id.item_colour);
        priceTv = (TextView) view.findViewById(R.id.item_price);
        stockTv = (TextView) view.findViewById(R.id.item_stock);
        currencyTv = (TextView) view.findViewById(R.id.item_currency);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        final int columnID = cursor.getColumnIndex(AlpacassoEntry._ID);
        int seriesColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_SERIES_NAME);
        int sizeColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_SIZE);
        int colourColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_COLOUR);
        int stockStatusColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_STOCK_STATUS);
        final int stockColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_STOCK_LEVEL);
        int priceColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_UNIT_PRICE);


        String seriesName = cursor.getString(seriesColumnIndex);
        int size = cursor.getInt(sizeColumnIndex);
        // Convert the integer values into text for display
        String sizeDisplayed;
        if (size == 2) {
            sizeDisplayed = context.getString(R.string.size_large);
        } else if (size == 1) {
            sizeDisplayed = context.getString(R.string.size_medium);
        } else {
            sizeDisplayed = context.getString(R.string.size_small);
        }

        String colour = cursor.getString(colourColumnIndex);

        Float price = cursor.getFloat(priceColumnIndex);
        // Display value as a price i.e. 2 decimal places
        String priceDisplay = String.format("%.02f", price);

        String stockLevel = cursor.getString(stockColumnIndex);
        stockQuantity = cursor.getInt(stockColumnIndex);
        stockStatus = cursor.getInt(stockStatusColumnIndex);
        String stockDisplay;
        // Change colour of the view to greyed out if stock level is zero
        if (stockStatus == 1) {
            stockDisplay = context.getString(R.string.not_in_stock);
            seriesNameTv.setTextColor(context.getResources().getColor(R.color.greyedOutColor));
            priceTv.setTextColor(context.getResources().getColor(R.color.greyedOutColor));
            currencyTv.setTextColor(context.getResources().getColor(R.color.greyedOutColor));
        } else {
            stockDisplay = stockLevel + " " + context.getString(R.string.in_stock_display);
            seriesNameTv.setTextColor(context.getResources().getColor(R.color.normalTextColor));
            priceTv.setTextColor(context.getResources().getColor(R.color.normalTextColor));
            currencyTv.setTextColor(context.getResources().getColor(R.color.normalTextColor));
        }

        seriesNameTv.setText(seriesName);
        sizeTv.setText(sizeDisplayed);
        colourTv.setText(colour);
        priceTv.setText(priceDisplay);
        stockTv.setText(stockDisplay);

        final int position = cursor.getPosition();
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                int oldQuantity = (cursor.getInt(stockColumnIndex));
                if (oldQuantity > 0) {
                    oldQuantity--;
                    if (oldQuantity > 0) {
                        int newStockLevel = oldQuantity;
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AlpacassoEntry.COLUMN_STOCK_LEVEL, newStockLevel);
                        String whereArg = AlpacassoEntry._ID + " =?";
                        //Get the item id which should be updated
                        int item_id = cursor.getInt(columnID);
                        String itemIDArgs = Integer.toString(item_id);
                        String[] selectionArgs = {itemIDArgs};
                        int rowsAffected = view.getContext().getContentResolver().update(
                                ContentUris.withAppendedId(AlpacassoEntry.CONTENT_URI, item_id),
                                contentValues,
                                whereArg, selectionArgs);
                        String newQu = cursor.getString(stockColumnIndex);
                        stockTv.setText(newQu + " " +
                                view.getContext().getString(R.string.in_stock_display));
                    } else {
                        int newStockLevel = oldQuantity;
                        stockStatus = 1;
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AlpacassoEntry.COLUMN_STOCK_LEVEL, newStockLevel);
                        contentValues.put(AlpacassoEntry.COLUMN_STOCK_STATUS, stockStatus);
                        String whereArg = AlpacassoEntry._ID + " =?";
                        //Get the item id which should be updated
                        int item_id = cursor.getInt(columnID);
                        String itemIDArgs = Integer.toString(item_id);
                        String[] selectionArgs = {itemIDArgs};
                        int rowsAffected = view.getContext().getContentResolver().update(
                                ContentUris.withAppendedId(AlpacassoEntry.CONTENT_URI, item_id),
                                contentValues,
                                whereArg, selectionArgs);
                        stockTv.setText(view.getContext().getString(R.string.not_in_stock));
                        seriesNameTv.setTextColor(view.getContext().getResources().getColor(R.color.greyedOutColor));
                        priceTv.setTextColor(view.getContext().getResources().getColor(R.color.greyedOutColor));
                        currencyTv.setTextColor(view.getContext().getResources().getColor(R.color.greyedOutColor));
                    }

                }

            }
        });

    }

}
