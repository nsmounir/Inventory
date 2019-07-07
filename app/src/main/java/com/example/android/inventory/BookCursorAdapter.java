package com.example.android.inventory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.Data.Contract;

public class BookCursorAdapter extends CursorAdapter {

    Context mContext;


    //    Constructs a new BookCursorAdapter
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        mContext = context;

        // Find fields to populate in inflated template
        TextView bookName = (TextView) view.findViewById(R.id.list_name);
        TextView bookPrice = (TextView) view.findViewById(R.id.list_price);
        final TextView bookQuantity = (TextView) view.findViewById(R.id.list_quantity);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndex(Contract.BookEntry.COLUMN_BOOK_NAME));
        String price = cursor.getString(cursor.getColumnIndex(Contract.BookEntry.COLUMN_PRICE));
        final String itemQuantity = cursor.getString(cursor.getColumnIndex(Contract.BookEntry.COLUMN_QUANTITY));

        final int[] quantity = {Integer.parseInt(itemQuantity)};
        final String id = cursor.getString(cursor.getColumnIndex(Contract.BookEntry._ID));


        // Populate fields with extracted properties
        bookName.setText(name);
        bookPrice.setText(price);
        bookQuantity.setText(itemQuantity);

        Button decrementButton = (Button) view.findViewById(R.id.decrement);
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity[0] == 0) {
                    Toast.makeText(mContext, (R.string.sell_book_failed), Toast.LENGTH_SHORT).show();

                } else {
                    quantity[0] = quantity[0] - 1;
                    ContentValues values = new ContentValues();
                    values.put(Contract.BookEntry.COLUMN_QUANTITY, quantity[0]);
                    bookQuantity.setText(quantity[0] + "");

                    Uri currentItemUri = Uri.withAppendedPath(Contract.BookEntry.CONTENT_URI, id);

                    mContext.getContentResolver().update(currentItemUri,
                            values, null, null);

                }
            }
        });

    }

}
