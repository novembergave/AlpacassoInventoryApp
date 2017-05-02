package com.example.android.alpacassoinventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.alpacassoinventoryapp.data.AlpacassoContract.AlpacassoEntry;

/**
 * Created by novembergave on 01/01/2017.
 */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ALPACASSO_LOADER = 0;
    AlpacassoCursorAdaptor mCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView alpacassoListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        alpacassoListView.setEmptyView(emptyView);

        mCursorAdaptor = new AlpacassoCursorAdaptor(this, null);
        alpacassoListView.setAdapter(mCursorAdaptor);

        alpacassoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentAlpacassoUri = ContentUris.withAppendedId(AlpacassoEntry.CONTENT_URI, id);
                intent.setData(currentAlpacassoUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(ALPACASSO_LOADER, null, this);

    }

    private void insertDummyData() {

        // Prefilled data for user to enter to see how it can work
        ContentValues values = new ContentValues();
        values.put(AlpacassoEntry.COLUMN_SERIES_NAME, "Poppin Ribbon");
        values.put(AlpacassoEntry.COLUMN_COLOUR, "Yellow");
        values.put(AlpacassoEntry.COLUMN_STOCK_LEVEL, 10);
        values.put(AlpacassoEntry.COLUMN_UNIT_PRICE, 7.00);
        values.put(AlpacassoEntry.COLUMN_SIZE, AlpacassoEntry.SIZE_SMALL);
        values.put(AlpacassoEntry.COLUMN_STOCK_STATUS, AlpacassoEntry.STOCK_IN);
        values.put(AlpacassoEntry.COLUMN_RESTOCK_AMOUNT, 0);

        Uri newUri = getContentResolver().insert(AlpacassoEntry.CONTENT_URI, values);
    }

    private void deleteAllConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_prompt);
        builder.setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllData();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllData() {
        int rowsDeleted = getContentResolver().delete(AlpacassoEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from alpacasso database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllConfirmation();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AlpacassoEntry._ID,
                AlpacassoEntry.COLUMN_SERIES_NAME,
                AlpacassoEntry.COLUMN_COLOUR,
                AlpacassoEntry.COLUMN_SIZE,
                AlpacassoEntry.COLUMN_UNIT_PRICE,
                AlpacassoEntry.COLUMN_STOCK_LEVEL,
                AlpacassoEntry.COLUMN_STOCK_STATUS
        };

        return new CursorLoader(
                this,
                AlpacassoEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdaptor.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdaptor.swapCursor(null);
    }
}
