package com.example.notecar;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME ="database.db";
    private static final String TABLE_DATA ="data";
    private static final String TABLE_LIST ="list";
    public static final String COLUMN_ID ="id";
    private static final String COLUMN_DATE ="date";
    private static final String COLUMN_TIME ="time";
    private static final String COLUMN_PERSON ="person";
    private static final String COLUMN_PLACE ="place";

    Context context;
    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
        this.context=context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DATA + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_PERSON + " TEXT NOT NULL, " +
                COLUMN_PLACE + " TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIST + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_PERSON + " TEXT NOT NULL, " +
                COLUMN_PLACE + " TEXT NOT NULL )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_DATA);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LIST);
        onCreate(db);
    }

    public boolean insertData(String date, String time, String person, String place) {
        db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_DATA + " WHERE "+ COLUMN_DATE+ "=? AND "+ COLUMN_TIME +"= ? AND "+ COLUMN_PERSON +"= ? AND "+ COLUMN_PLACE+"= ?", new String[]{String.valueOf(date), String.valueOf(time), String.valueOf(person), String.valueOf(place)});

        if (cursor.getCount() == 0) {

            ContentValues contentValues=new ContentValues();
            contentValues.put(COLUMN_DATE, date);
            contentValues.put(COLUMN_TIME, time);
            contentValues.put(COLUMN_PERSON, person);
            contentValues.put(COLUMN_PLACE, place);

            db=this.getWritableDatabase();
            db.insert(TABLE_DATA,null,contentValues);
            db.close();

            cursor.close();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean insertList(String time, String person, String place) {
        db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_LIST + " WHERE "+ COLUMN_TIME +"= ? AND "+ COLUMN_PERSON +"= ? AND "+ COLUMN_PLACE+"= ?", new String[]{String.valueOf(time), String.valueOf(person), String.valueOf(place)});

        if (cursor.getCount() == 0) {
            ContentValues contentValues=new ContentValues();
            contentValues.put(COLUMN_TIME, time);
            contentValues.put(COLUMN_PERSON, person);
            contentValues.put(COLUMN_PLACE, place);

            db=this.getWritableDatabase();
            db.insert(TABLE_LIST,null,contentValues);
            db.close();

            cursor.close();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean insertToData(int id, String displayDate) {
        db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_LIST + " WHERE "+ COLUMN_ID +"= ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            String time = cursor.getString(1);
            String person = cursor.getString(2);
            String place = cursor.getString(3);

            Cursor row = db.rawQuery("SELECT * FROM "+ TABLE_DATA + " WHERE "+ COLUMN_DATE +"= ? AND "+ COLUMN_TIME +"= ? AND "+ COLUMN_PERSON +"= ? AND "+ COLUMN_PLACE+"= ?", new String[]{String.valueOf(displayDate), String.valueOf(time), String.valueOf(person), String.valueOf(place)});
            if (row.getCount() == 0){
                ContentValues contentValues=new ContentValues();
                contentValues.put(COLUMN_DATE, displayDate);
                contentValues.put(COLUMN_TIME, time);
                contentValues.put(COLUMN_PERSON, person);
                contentValues.put(COLUMN_PLACE, place);

                db.insert(TABLE_DATA,null,contentValues);
                db.close();

                row.close();
                return true;
            }
            else {
                return false;
            }
        }
        cursor.close();
        return false;
    }

    public void updateData(int id, String date, String time, String person, String place) {
        db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_PERSON, person);
        values.put(COLUMN_PLACE, place);

        db.update(TABLE_DATA, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateList(int id, String time, String person, String place) {
        db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_PERSON, person);
        values.put(COLUMN_PLACE, place);

        db.update(TABLE_LIST, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteData(int id) {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_DATA, COLUMN_ID + "=?",  new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteList(int id) {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_LIST, COLUMN_ID + "=?",  new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllData(String datePicker){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_DATA, COLUMN_DATE + "=?", new String[]{datePicker});
        db.close();
    }

    public void deleteAllList(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_LIST, null,null);
        db.close();
    }

    public ArrayList<DataTable> getAllData(String datePicker, int sortData) {
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataTable> arrayList = new ArrayList<>();

        if (sortData==0) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA + " WHERE " + COLUMN_DATE + "=?" + " ORDER BY " + COLUMN_ID + " ASC", new String[]{datePicker});
        }
        else if (sortData==1) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA + " WHERE " + COLUMN_DATE + "=?" + " ORDER BY " + COLUMN_TIME + " ASC", new String[]{datePicker});
        }
        else if (sortData==2) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA + " WHERE " + COLUMN_DATE + "=?" + " ORDER BY " + COLUMN_PERSON + " ASC", new String[]{datePicker});
        }
        else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA + " WHERE " + COLUMN_DATE + "=?" + " ORDER BY " + COLUMN_PLACE + " ASC", new String[]{datePicker});
        }

        if (cursor.moveToFirst()) {
            do{
                DataTable dataTable=new DataTable();
                dataTable.setId((cursor.getInt(0)));
                dataTable.setDate(cursor.getString(1));
                dataTable.setTime(cursor.getString(2));
                dataTable.setPerson(cursor.getString(3));
                dataTable.setPlace((cursor.getString(4)));

                arrayList.add(dataTable);

            } while(cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<ListTable> getAllList(int sortList) {
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListTable> arrayList = new ArrayList<>();

        if (sortList==0) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_LIST +" ORDER BY " + COLUMN_ID + " ASC", null);
        }
        else if (sortList==1) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_LIST +" ORDER BY " + COLUMN_TIME + " ASC", null);
        }
        else if (sortList==2) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_LIST +" ORDER BY " + COLUMN_PERSON + " ASC", null);
        }
        else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_LIST +" ORDER BY " + COLUMN_PLACE + " ASC", null);
        }

        if (cursor.moveToFirst()) {
            do {
                ListTable listTable=new ListTable();
                listTable.setId((cursor.getInt(0)));
                listTable.setTime(cursor.getString(1));
                listTable.setPerson(cursor.getString(2));
                listTable.setPlace((cursor.getString(3)));

                arrayList.add(listTable);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }
}
