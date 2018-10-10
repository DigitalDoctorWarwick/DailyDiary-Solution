package com.example.android.dailydiary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.dailydiary.data.DiaryContract.DiaryEntry;

public class DiaryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dailydiary.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DiaryEntry.TABLE_NAME + " (" +
                    DiaryEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    DiaryEntry.COLUMN_DIARY_NAME + TEXT_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_DIARY_DATE + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_DIARY_MOBILITY + INTEGER_TYPE + " NOT NULL DEFAULT 0" + COMMA_SEP +
                    DiaryEntry.COLUMN_DIARY_STIFFNESS + INTEGER_TYPE + " NOT NULL DEFAULT 0" + COMMA_SEP +
                    DiaryEntry.COLUMN_DIARY_PAIN + INTEGER_TYPE + " NOT NULL DEFAULT 0" + COMMA_SEP +
                    DiaryEntry.COLUMN_DIARY_FALLS + INTEGER_TYPE + " NOT NULL DEFAULT 0" + COMMA_SEP +
                    DiaryEntry.COLUMN_DIARY_SUBMITTED + INTEGER_TYPE + " NOT NULL DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DiaryEntry.TABLE_NAME;

    public DiaryDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DiaryDbHelper", "SQL Statement = " + SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Cursor c = db.query(DiaryEntry.TABLE_NAME, null, null, null, null, null, null);
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        while (c.moveToNext()){
            ContentValues values = new ContentValues();
            values.put(DiaryEntry.COLUMN_DIARY_NAME, c.getString(c.getColumnIndex(DiaryEntry.COLUMN_DIARY_NAME)));
            values.put(DiaryEntry.COLUMN_DIARY_DATE, c.getLong(c.getColumnIndex(DiaryEntry.COLUMN_DIARY_DATE)));
            values.put(DiaryEntry.COLUMN_DIARY_MOBILITY, c.getInt(c.getColumnIndex(DiaryEntry.COLUMN_DIARY_MOBILITY)));
            values.put(DiaryEntry.COLUMN_DIARY_STIFFNESS, c.getInt(c.getColumnIndex(DiaryEntry.COLUMN_DIARY_STIFFNESS)));
            values.put(DiaryEntry.COLUMN_DIARY_PAIN, c.getInt(c.getColumnIndex(DiaryEntry.COLUMN_DIARY_PAIN)));
            values.put(DiaryEntry.COLUMN_DIARY_FALLS, c.getInt(c.getColumnIndex(DiaryEntry.COLUMN_DIARY_FALLS)));
            //values.put(DiaryEntry.COLUMN_DIARY_SUBMITTED, c.getString(c.getColumnIndex(DiaryEntry.COLUMN_DIARY_NAME)));
            db.insert(DiaryEntry.TABLE_NAME, null, values);
        }
        c.close();
    }
}
