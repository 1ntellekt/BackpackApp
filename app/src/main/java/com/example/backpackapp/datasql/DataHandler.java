package com.example.backpackapp.datasql;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.backpackapp.enteties.Book;
import com.example.backpackapp.serverutils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public class DataHandler extends SQLiteOpenHelper {

    public DataHandler(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String q = "CREATE TABLE "+Util.TABLE_NAME+"("
                +Util.KEY_ID+" INTEGER PRIMARY KEY,"
                +Util.KEY_NAME+" TEXT,"
                +Util.KEY_FILE+" TEXT,"
                +Util.KEY_NAME_AUTHOR+" TEXT,"
                +Util.KEY_SUB_NAME+" TEXT,"
                +Util.KEY_CLASS+" INTEGER,"
                +"time TEXT"+
                ")";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String q = "DROP TABLE IF EXISTS "+Util.TABLE_NAME;
        db.execSQL(q);
        onCreate(db);
    }

    public void addBook(Book book){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.KEY_ID,book.getId());
        contentValues.put(Util.KEY_NAME,book.getName());
        contentValues.put(Util.KEY_NAME_AUTHOR,book.getAuthorName());
        contentValues.put(Util.KEY_SUB_NAME,book.getSubName());
        contentValues.put(Util.KEY_FILE,book.getFilename());
        contentValues.put(Util.KEY_CLASS,book.getClassNum());
        contentValues.put("time", dateFormat.format(new Date()));
        db.insert(Util.TABLE_NAME,null,contentValues);
        db.close();

    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()){
            do {
                Book book = new Book(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)
                );
               String name=book.getName();
               if (cursor.getString(6)!=null)
               book.setName(name+" "+cursor.getString(6));
                books.add(book);
            } while (cursor.moveToNext());
        }
        return books;
    }

}
