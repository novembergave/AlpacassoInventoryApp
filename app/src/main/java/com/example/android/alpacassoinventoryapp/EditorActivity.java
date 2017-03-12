package com.example.android.alpacassoinventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.alpacassoinventoryapp.data.AlpacassoContract.AlpacassoEntry;


/**
 * Created by novembergave on 01/01/2017.
 */

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int EXISTING_ALPACASSO_LOADER = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    private EditText seriesEditText;
    private EditText colourEditText;
    private EditText priceEditText;
    private Spinner sizeSpinner;
    private EditText stockEditText;
    private RadioGroup stockIndicator;
    private RadioButton inStockIndicator;
    private RadioButton outStockIndicator;
    private EditText restockEditText;
    private ImageView alpacassoImageView;
    private String seriesString;
    private String colourString;
    private String stockString;
    private String restockString;
    private String priceInputString;
    private byte[] alpacassoBitmap;
    private Bitmap captureBmp;
    private Bitmap orientedBmp;
    private int mSize = AlpacassoEntry.SIZE_SMALL;
    private int mStockIndicator = AlpacassoEntry.STOCK_IN;
    private float priceFloat;
    private String priceString;
    private Uri mCurrentUri;
    private boolean hasAlpacassoChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            hasAlpacassoChanged = true;
            return false;
        }
    };
    private View.OnClickListener minusAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String stockString = stockEditText.getText().toString().trim();
            int stockLevel;
            if (TextUtils.isEmpty(stockString)) {
                stockLevel = 0;
            } else {
                stockLevel = Integer.parseInt(stockString);
            }
            if (stockLevel > 0) {
                stockLevel = stockLevel - 1;
                stockEditText.setText(String.valueOf(stockLevel));
            }
        }
    };
    private View.OnClickListener plusAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String stockString = stockEditText.getText().toString().trim();
            int stockLevel;
            if (TextUtils.isEmpty(stockString)) {
                stockLevel = 0;
            } else {
                stockLevel = Integer.parseInt(stockString);
            }
            stockLevel = stockLevel + 1;
            stockEditText.setText(String.valueOf(stockLevel));
        }
    };
    private View.OnClickListener restockAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            stockString = stockEditText.getText().toString().trim();
            restockString = restockEditText.getText().toString().trim();
            addRestockedItems();
        }
    };
    private View.OnClickListener cameraAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            Log.d(LOG_TAG, "Activity started");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle(R.string.add_alpacasso);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_alpacasso);
            getLoaderManager().initLoader(EXISTING_ALPACASSO_LOADER, null, this);
            // Make restock section visible
            LinearLayout restockSection = (LinearLayout) findViewById(R.id.restock_section);
            restockSection.setVisibility(View.VISIBLE);
            TextView restockButton = (TextView) findViewById(R.id.restock_button);
            restockButton.setOnTouchListener(mTouchListener);
            restockButton.setOnClickListener(restockAction);
        }


        seriesEditText = (EditText) findViewById(R.id.edit_series_name);
        colourEditText = (EditText) findViewById(R.id.edit_colour);
        priceEditText = (EditText) findViewById(R.id.edit_price);
        sizeSpinner = (Spinner) findViewById(R.id.spinner_size);
        stockEditText = (EditText) findViewById(R.id.edit_number_stock);
        stockIndicator = (RadioGroup) findViewById(R.id.stock_boolean);
        inStockIndicator = (RadioButton) findViewById(R.id.in_stock);
        outStockIndicator = (RadioButton) findViewById(R.id.out_of_stock);
        restockEditText = (EditText) findViewById(R.id.restock);
        alpacassoImageView = (ImageView) findViewById(R.id.alpacasso_image);

        seriesEditText.setOnTouchListener(mTouchListener);
        colourEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        sizeSpinner.setOnTouchListener(mTouchListener);
        stockEditText.setOnTouchListener(mTouchListener);
        stockIndicator.setOnTouchListener(mTouchListener);
        inStockIndicator.setOnTouchListener(mTouchListener);
        outStockIndicator.setOnTouchListener(mTouchListener);
        restockEditText.setOnTouchListener(mTouchListener);
        alpacassoImageView.setOnTouchListener(mTouchListener);

        setUpSpinner();
        setUpRadioListener();

        TextView minusButton = (TextView) findViewById(R.id.minus_button);
        TextView plusButton = (TextView) findViewById(R.id.plus_button);
        minusButton.setOnTouchListener(mTouchListener);
        plusButton.setOnTouchListener(mTouchListener);

        minusButton.setOnClickListener(minusAction);
        plusButton.setOnClickListener(plusAction);
        alpacassoImageView.setOnClickListener(cameraAction);


    }

    private void setUpSpinner() {
        ArrayAdapter sizeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.size_array,
                android.R.layout.simple_spinner_item);

        sizeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeSpinnerAdapter);

        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.size_medium))) {
                        mSize = AlpacassoEntry.SIZE_MEDIUM;
                    } else if (selection.equals(getString(R.string.size_large))) {
                        mSize = AlpacassoEntry.SIZE_LARGE;
                    } else {
                        mSize = AlpacassoEntry.SIZE_SMALL;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSize = AlpacassoEntry.SIZE_SMALL;
            }
        });
    }

    private void setUpRadioListener() {
        // As stock level is tied to the value of these radio buttons, set up listener to ensure
        // that changes made to the radio buttons will change the stock level value
        stockIndicator.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.in_stock:
                        mStockIndicator = AlpacassoEntry.STOCK_IN;
                        stockEditText.setEnabled(true);
                        // As the minimum for in stock is 1,
                        stockEditText.setText("1");
                        break;
                    case R.id.out_of_stock:
                        mStockIndicator = AlpacassoEntry.STOCK_OUT;
                        stockEditText.setText("0");
                        // As there is no need to enter any value since it is naturally 0
                        stockEditText.setEnabled(false);
                        break;
                }
            }
        });
    }

    private void saveAlpacasso() {
        seriesString = seriesEditText.getText().toString().trim();
        colourString = colourEditText.getText().toString().trim();
        stockString = stockEditText.getText().toString().trim();
        restockString = restockEditText.getText().toString().trim();
        alpacassoBitmap = ImageByte.drawableToByteArray(alpacassoImageView.getDrawable());

        Log.e(LOG_TAG, String.valueOf(alpacassoBitmap));

        if (alpacassoBitmap == null) {
            alpacassoBitmap =
                    ImageByte.drawableToByteArray(ContextCompat.getDrawable(this, R.drawable.ic_action_camera));
            Log.d(LOG_TAG, String.valueOf(alpacassoBitmap));
        }

        priceInputString = priceEditText.getText().toString().trim();

        // The following try block is to ensure no blank values are saved in price
        try {
            priceFloat = Float.valueOf(priceInputString);
            priceString = String.format("%.02f", priceFloat);
        } catch (NumberFormatException nfe) {
            priceFloat = 0;
        }

        // The following is to capture the null or zero value inputs in stock
        if (TextUtils.isEmpty(stockString)) {
            changeStockToZero(stockString);
        } else if (stockString.equals("0")) {
            changeStockToZero(stockString);
        } else mStockIndicator = AlpacassoEntry.STOCK_IN;

        if (TextUtils.isEmpty(restockString) || restockString == null) {
            restockString = "0";
        }

        if (mCurrentUri == null &&
                TextUtils.isEmpty(seriesString) && TextUtils.isEmpty(colourString)
                && TextUtils.isEmpty(priceInputString) && TextUtils.isEmpty(stockString)
                && mSize == AlpacassoEntry.SIZE_SMALL && mStockIndicator == AlpacassoEntry.STOCK_IN) {
            Toast.makeText(this, getString(R.string.prompt_no_change), Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else if (mCurrentUri == null &&
                (TextUtils.isEmpty(seriesString) || TextUtils.isEmpty(colourString)
                        || TextUtils.isEmpty(priceInputString) || TextUtils.isEmpty(stockString))) {
            Toast.makeText(this, getString(R.string.prompt_missing), Toast.LENGTH_SHORT).show();
        } else {

            ContentValues values = new ContentValues();
            values.put(AlpacassoEntry.COLUMN_SERIES_NAME, seriesString);
            values.put(AlpacassoEntry.COLUMN_COLOUR, colourString);
            values.put(AlpacassoEntry.COLUMN_UNIT_PRICE, priceString);
            values.put(AlpacassoEntry.COLUMN_STOCK_LEVEL, stockString);
            values.put(AlpacassoEntry.COLUMN_STOCK_STATUS, mStockIndicator);
            values.put(AlpacassoEntry.COLUMN_RESTOCK_AMOUNT, restockString);
            values.put(AlpacassoEntry.COLUMN_SIZE, mSize);
            values.put(AlpacassoEntry.COLUMN_IMAGE, alpacassoBitmap);

            if (mCurrentUri == null) {
                Uri newUri = getContentResolver().insert(AlpacassoEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.prompt_error_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.prompt_save_successful), Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.prompt_no_change), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.prompt_save_successful), Toast.LENGTH_SHORT).show();
                }
            }

        }


    }

    // Helper to change stock to zero value and set the check boxes accordingly
    private void changeStockToZero(String stockAmount) {
        inStockIndicator.setChecked(false);
        outStockIndicator.setChecked(true);
        mStockIndicator = AlpacassoEntry.STOCK_OUT;
        stockAmount = "0";
    }

    private void addRestockedItems() {
        restockString = restockEditText.getText().toString().trim();
        if (TextUtils.isEmpty(restockString)) {
            restockString = "0";
            return;
        } else if (restockString.equals("0")) {
            return;
        }
        int newStockLevel = Integer.valueOf(restockString) + Integer.valueOf(stockString);
        restockString = "0";
        restockEditText.setText(restockString);
        stockEditText.setText(String.valueOf(newStockLevel));
        Toast.makeText(this, getResources().getString(R.string.stocked) + "!", Toast.LENGTH_SHORT).show();
    }

    private void deleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_prompt);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAlpacasso();
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

    private void deleteAlpacasso() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.prompt_error_delete), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_confirmation), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private String createEmailMessage(String seriesName, String colour, String quantity, int size) {
        String sizeDisplay;
        switch (size) {
            default:
                sizeDisplay = getResources().getString(R.string.size_small);
                break;
            case 1:
                sizeDisplay = getResources().getString(R.string.size_medium);
                break;
            case 2:
                sizeDisplay = getResources().getString(R.string.size_large);
                break;
        }
        String emailMessage = getResources().getString(R.string.series) + ": " + seriesName;
        emailMessage += "\n" + getResources().getString(R.string.colour) + ": " + colour;
        emailMessage += "\n" + getResources().getString(R.string.size) + ": " + sizeDisplay;
        emailMessage += "\n" + getResources().getString(R.string.restock_amount) + ": " + quantity;
        return emailMessage;
    }

    private void sendEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prompt_save_send);
        builder.setPositiveButton(R.string.positive_save_send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveAlpacasso();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, "alpacasso@alpacasso.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Restock of ");
                intent.putExtra(Intent.EXTRA_TEXT, createEmailMessage(seriesString, colourString, restockString, mSize));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(EditorActivity.this, "There are no email applications installed.", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentUri == null) {
            MenuItem deleteMenu = menu.findItem(R.id.action_delete);
            MenuItem restockMenu = menu.findItem(R.id.action_restock);
            deleteMenu.setVisible(false);
            restockMenu.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (captureBmp != null) {
            ImageByte.clearBitmap(captureBmp);
            Log.d(LOG_TAG, "Clear bitmap action performed");
        }

        if (orientedBmp != null) {
            ImageByte.clearBitmap(orientedBmp);
            Log.d(LOG_TAG, "Clear oriented bitmap action performed");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_save:
                saveAlpacasso();
                finish();
                Log.d(LOG_TAG, "save alpacasso finished");
//                ImageByte.clearBitmap(captureBmp);
//                Log.d(LOG_TAG, "Clear bitmap action performed");
//                ImageByte.clearBitmap(orientedBmp);
//                Log.d(LOG_TAG, "Clear oriented bitmap action performed");
                return true;

            case R.id.action_restock:
                sendEmail();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete:
                deleteConfirmation();
                return true;
            case android.R.id.home:
                if (!hasAlpacassoChanged) {
                    // Navigate back to parent activity
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                unSavedChangedDialog(discardClickListener);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!hasAlpacassoChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        unSavedChangedDialog(discardClickListener);
    }

    private void unSavedChangedDialog(DialogInterface.OnClickListener discardClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved);
        builder.setNegativeButton(R.string.discard, discardClickListener);
        builder.setPositiveButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AlpacassoEntry._ID,
                AlpacassoEntry.COLUMN_SERIES_NAME,
                AlpacassoEntry.COLUMN_COLOUR,
                AlpacassoEntry.COLUMN_SIZE,
                AlpacassoEntry.COLUMN_STOCK_STATUS,
                AlpacassoEntry.COLUMN_STOCK_LEVEL,
                AlpacassoEntry.COLUMN_RESTOCK_AMOUNT,
                AlpacassoEntry.COLUMN_UNIT_PRICE,
                AlpacassoEntry.COLUMN_IMAGE
        };

        return new CursorLoader(
                this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int seriesColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_SERIES_NAME);
            int colourColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_COLOUR);
            int sizeColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_SIZE);
            int stockStatusColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_STOCK_STATUS);
            int stockLevelColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_STOCK_LEVEL);
            int restockColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_RESTOCK_AMOUNT);
            int priceColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_UNIT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(AlpacassoEntry.COLUMN_IMAGE);

            String seriesName = cursor.getString(seriesColumnIndex);
            String colour = cursor.getString(colourColumnIndex);
            String restockAmount = cursor.getString(restockColumnIndex);

            int size = cursor.getInt(sizeColumnIndex);
            int stockStatus = cursor.getInt(stockStatusColumnIndex);
            int stockLevel = cursor.getInt(stockLevelColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);

            seriesEditText.setText(seriesName);
            colourEditText.setText(colour);
            priceEditText.setText(String.format("%.02f", price));
            stockEditText.setText(Integer.toString(stockLevel));
            restockEditText.setText(restockAmount);
            alpacassoImageView.setImageBitmap(ImageByte.byteToDrawable(image));

            switch (size) {
                case AlpacassoEntry.SIZE_MEDIUM:
                    sizeSpinner.setSelection(1);
                    break;
                case AlpacassoEntry.SIZE_LARGE:
                    sizeSpinner.setSelection(2);
                    break;
                default:
                    sizeSpinner.setSelection(0);
                    break;
            }

            switch (stockStatus) {
                case AlpacassoEntry.STOCK_OUT:
                    inStockIndicator.setChecked(false);
                    outStockIndicator.setChecked(true);
                    break;
                case AlpacassoEntry.STOCK_IN:
                    inStockIndicator.setChecked(true);
                    outStockIndicator.setChecked(false);
                    break;
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        seriesEditText.setText("");
        colourEditText.setText("");
        priceEditText.setText("");
        stockEditText.setText("");
        restockEditText.setText("");
        sizeSpinner.setSelection(0);
        inStockIndicator.setChecked(true);
        outStockIndicator.setChecked(false);
        alpacassoImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_camera));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView alpacassoImage = (ImageView) findViewById(R.id.alpacasso_image);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imgDecodableString, options);
                int photoW = options.outWidth;

                int scaleFactor = photoW / 120;

                Log.e(LOG_TAG, String.valueOf(scaleFactor));

                options.inJustDecodeBounds = false;
                options.inScaled = false;
                options.inSampleSize = scaleFactor;
                Log.e(LOG_TAG, imgDecodableString);
                captureBmp = BitmapFactory
                        .decodeFile(imgDecodableString, options);
                orientedBmp = ExifUtil.rotateBitmap(imgDecodableString, captureBmp);
                // Set the Image in ImageView after decoding the String
                alpacassoImage.setImageBitmap(orientedBmp);
                Log.d(LOG_TAG, "After set Image action complete");

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


    }


}

