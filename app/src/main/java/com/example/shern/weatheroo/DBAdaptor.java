package com.example.shern.weatheroo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdaptor {
    private static final String DATABASE_NAME = "SaneSharks1";
    private static final String DATABASE_TABLE = "weather";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table weather (code integer primary key autoincrement, " +
                    "url text not null);";

    public final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdaptor(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    //-----------------------openes the database------------------------
    public DBAdaptor open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //------------------close the database------------------
    public void close() {
        DBHelper.close();
    }

    //check if table is empty
    public boolean tableIsEmpty() {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        String count = "SELECT count(*) FROM " + DATABASE_TABLE;
        Cursor c = db.rawQuery(count, null);
        c.moveToFirst();
        int icount = c.getInt(0);
        if (icount > 0)
            return false;
        else
            return true;
    }

    //------------insert value into the database------------
    public long insertValue(String url) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("url", url);
        return db.insert(DATABASE_TABLE, null, initialValues);

    }

    //-------deletes a particular value/row--------
    public boolean deleteValue(long rowId) {
        return db.delete(DATABASE_TABLE, "code=" + rowId, null) > 0;
    }

    //-----get all values-----
    public Cursor getAllValues() {
        return db.query(DATABASE_TABLE, new String[]{"code", "url"},
                null, null, null, null, null);
    }

    //----get back one value/row----
    public Cursor getOneValue(long rowId) throws SQLException {
        Cursor c = db.query(true, DATABASE_TABLE, new String[]{"code", "url"},
                "code=" + rowId, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destory all data");
            db.execSQL("DROP TABLE IF EXISTS weather");
            onCreate(db);
        }
    }//end of databasehelper class

}
