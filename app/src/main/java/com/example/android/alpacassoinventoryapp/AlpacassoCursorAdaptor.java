package com.example.android.alpacassoinventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.alpacassoinventoryapp.data.AlpacassoContract.AlpacassoEntry;

import org.w3c.dom.Text;

import static com.example.android.alpacassoinventoryapp.R.string.colour;
import static com.example.android.alpacassoinventoryapp.R.string.price;
import static java.security.AccessController.getContext;

/**
 * Created by novembergave on 02/01/2017.
 */

public class AlpacassoCursorAdaptor extends CursorAdapter{

    public AlpacassoCursorAdaptor(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView seriesNameTv = (TextView) view.findViewById(R.id.item_series_name);
        TextView sizeTv = (TextView) view.findViewById(R.id.item_size);
        TextView colourTv = (TextView) view.findViewById(R.id.item_colour);
        TextView priceTv = (TextView) view.findViewById(R.id.item_price);
        TextView stockTv = (TextView) view.findViewById(R.id.item_stock);
        TextView currencyTV = (TextView) view.findViewById(R.id.item_currency);
        ImageView imageIV = (ImageView) view.findViewById(R.id.item_image);

        int seriesColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_SERIES_NAME);
        int sizeColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_SIZE);
        int colourColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_COLOUR);
        int stockStatusColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_STOCK_STATUS);
        int stockColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_STOCK_LEVEL);
        int priceColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_UNIT_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_IMAGE);


        String seriesName = cursor.getString(seriesColumnIndex);
        int size = cursor.getInt(sizeColumnIndex);
        // Convert the integer values into text for display
        String sizeDisplayed;
        if (size == 2){
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
        int stockStatus = cursor.getInt(stockStatusColumnIndex);
        String stockDisplay;
        // Change colour of the view to greyed out if stock level is zero
        if (stockStatus == 1) {
            stockDisplay = context.getString(R.string.not_in_stock);
            seriesNameTv.setTextColor(context.getResources().getColor(R.color.greyedOutColor));
            priceTv.setTextColor(context.getResources().getColor(R.color.greyedOutColor));
            currencyTV.setTextColor(context.getResources().getColor(R.color.greyedOutColor));
        } else {
            stockDisplay = stockLevel + " " + context.getString(R.string.in_stock_display);
            seriesNameTv.setTextColor(context.getResources().getColor(R.color.normalTextColor));
            priceTv.setTextColor(context.getResources().getColor(R.color.normalTextColor));
            currencyTV.setTextColor(context.getResources().getColor(R.color.normalTextColor));
        }

        byte[] image = cursor.getBlob(imageColumnIndex);


        seriesNameTv.setText(seriesName);
        sizeTv.setText(sizeDisplayed);
        colourTv.setText(colour);
        priceTv.setText(priceDisplay);
        stockTv.setText(stockDisplay);
        imageIV.setImageBitmap(ImageByte.byteToDrawable(image));
    }
}
