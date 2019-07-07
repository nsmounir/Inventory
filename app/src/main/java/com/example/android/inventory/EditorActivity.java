package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.inventory.Data.Contract;
import com.example.android.inventory.Data.DbHelper;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_book_LOADER = 0;
    private DbHelper dbHelper;
    private Uri uri;
    //    EditText to enter book name
    private EditText mNameEditText;

    //    EditText to enter book price
    private EditText mPriceEditText;

    //    EditText to enter book quantity
    private EditText mQuantityEditText;

    //    EditText to enter book supplier name
    private EditText mSupplierNameEditText;

    //    EditText to enter supplier phone number
    private EditText mSupplierPhoneNumberEditText;

    //    Button to subtract from quantity
    private ImageButton mSubtractQuantityBtn;

    //    Button to add from quantity
    private ImageButton mAddQuantityBtn;

    //     Button to call supplier
    private ImageButton mCallSupplierBtn;

    //     Button to delete book
    private Button mDeleteBookBtn;

    private boolean mBookHasChanged = false;


    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mBookHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        dbHelper = new DbHelper(this);
        Log.v("EDITOR", "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");

        // Get Uri from Intent
        Intent intent = getIntent();
        uri = intent.getData();
        if (uri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
            getLoaderManager().initLoader(EXISTING_book_LOADER, null, this);


        }
//        Find all views to read user input
        mNameEditText = (EditText) findViewById(R.id.book_name);
        mPriceEditText = (EditText) findViewById(R.id.price);
        mQuantityEditText = (EditText) findViewById(R.id.quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.supplier_phone_number);
        mSubtractQuantityBtn = (ImageButton) findViewById(R.id.editor_subtract);
        mAddQuantityBtn = (ImageButton) findViewById(R.id.editor_add);
        mCallSupplierBtn = (ImageButton) findViewById(R.id.editor_call_btn);
        mDeleteBookBtn = (Button) findViewById(R.id.editor_delete_btn);

        // Set an onClick listener to subtract btn
        mSubtractQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractQuantity();
                mBookHasChanged = true;

            }
        });

        // Set an onClick listener to add btn
        mAddQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuantity();
                mBookHasChanged = true;
            }
        });

        //Set onClick listener to call supplier btn
        mCallSupplierBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                intent.setData(Uri.fromParts("tel", mSupplierPhoneNumberEditText.getText().toString().trim(), null));
                startActivity(intent);
            }
        });

        //Set onClick listener to delete btn
        mDeleteBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mSubtractQuantityBtn.setOnTouchListener(mTouchListener);
        mAddQuantityBtn.setOnTouchListener(mTouchListener);


    }

    //    method for subtracting one from current quantity
    private void subtractQuantity() {
        String currentQuantityString = mQuantityEditText.getText().toString();
        int currentQuantity;
        if (currentQuantityString.isEmpty()) {
            return;
        } else if (currentQuantityString.equals("0")) {
            return;
        } else {
            currentQuantity = Integer.parseInt(currentQuantityString);
            mQuantityEditText.setText(String.valueOf(currentQuantity - 1));
        }

    }

    //    method for add one to current quantity
    private void addQuantity() {
        String currentQuantityString = mQuantityEditText.getText().toString();
        int currentQuantity;
        if (currentQuantityString.isEmpty()) {
            return;
        } else if (currentQuantityString.equals("0")) {
            return;
        } else {
            currentQuantity = Integer.parseInt(currentQuantityString);
            mQuantityEditText.setText(String.valueOf(currentQuantity + 1));
        }
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //    Get user input and save it to database
    private void saveBook() {
        DbHelper mDbHelper = new DbHelper(this);

//        Get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues insertValues = new ContentValues();

        String nameString = mNameEditText.getText().toString().trim();
        insertValues.put(Contract.BookEntry.COLUMN_BOOK_NAME, nameString);

        String priceString = mPriceEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        insertValues.put(Contract.BookEntry.COLUMN_PRICE, price);

        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        insertValues.put(Contract.BookEntry.COLUMN_QUANTITY, quantity);

        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        insertValues.put(Contract.BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);

        String supplierPhoneString = mSupplierPhoneNumberEditText.getText().toString().trim();
        int supplierPhone = Integer.parseInt(supplierPhoneString);
        insertValues.put(Contract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);


        if (uri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString)
                && TextUtils.isEmpty(supplierPhoneString)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Determine if this is a new or existing book by checking if uri is null or not
        if (uri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(Contract.BookEntry.CONTENT_URI, insertValues);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: uri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because current uri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(uri, insertValues, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // insert book into database
                saveBook();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Determine if this is a new or existing book by checking if uri is null or not
        if (uri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(uri, null, null);
            // Show a toast message depending on whether or not the insertion was successful.
            if (rowsDeleted == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {Contract.BookEntry._ID,
                Contract.BookEntry.COLUMN_BOOK_NAME,
                Contract.BookEntry.COLUMN_PRICE,
                Contract.BookEntry.COLUMN_QUANTITY,
                Contract.BookEntry.COLUMN_SUPPLIER_NAME,
                Contract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this, uri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhoneNumber = cursor.getInt(supplierPhoneNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(String.valueOf(supplierPhoneNumber));


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");

    }
}
