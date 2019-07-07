package com.example.android.inventory.Data;

;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "books.db";
    public final static int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = " CREATE TABLE " + Contract.BookEntry.TABLE_NAME + " ("
                + Contract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + Contract.BookEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + Contract.BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + Contract.BookEntry.COLUMN_SUPPLIER_NAME + " TEXT,"
                + Contract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL )";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS books");
        onCreate(db);
    }
}
