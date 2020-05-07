package com.example.notecar;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
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

    SharedPreferences preferences;
    Context context;

    SQLiteDatabase db;
    //Context c;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
        this.context=context;
    }

    public void onCreate(SQLiteDatabase db)
    {
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

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_DATA);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LIST);
        onCreate(db);
    }

    public void insertData(String date, String time, String person, String place)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_TIME, time);
        contentValues.put(COLUMN_PERSON, person);
        contentValues.put(COLUMN_PLACE, place);

        db=this.getWritableDatabase();
        db.insert(TABLE_DATA,null,contentValues);
        db.close();
    }

    public void insertList(String time, String person, String place)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_TIME, time);
        contentValues.put(COLUMN_PERSON, person);
        contentValues.put(COLUMN_PLACE, place);

        db=this.getWritableDatabase();
        db.insert(TABLE_LIST,null,contentValues);
        db.close();
    }

    public void updateData(int id, String date, String time, String person, String place)
    {
        db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_PERSON, person);
        values.put(COLUMN_PLACE, place);

        db.update(TABLE_DATA, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteData(int id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_DATA, COLUMN_ID + "=?",  new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteList(int id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_LIST, COLUMN_ID + "=?",  new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<DataTable> getAllData()
    {
        String sql;

        preferences = context.getSharedPreferences("preferences", 0);

        //SharedPreferences preferences = c.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        int number = preferences.getInt("orderByData",0);

        if(number==0)
        {
            sql = "SELECT * FROM " + TABLE_DATA + " ORDER BY " + COLUMN_TIME + " ASC";
        }
        else if(number==1)
        {
            sql = "SELECT * FROM " + TABLE_DATA + " ORDER BY " + COLUMN_PERSON + " ASC";
        }
        else if (number==2)
        {
            sql = "SELECT * FROM " + TABLE_DATA + " ORDER BY " + COLUMN_PLACE + " ASC";
        }
        else
        {
            sql = "SELECT * FROM " + TABLE_DATA + " ORDER BY " + COLUMN_ID + " ASC";
        }

        //sql = "SELECT * FROM " + TABLE_DATA;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataTable> arrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst())
        {
            do{
                DataTable dataTable=new DataTable();

                dataTable.setId((cursor.getInt(0)));
                dataTable.setDate(cursor.getString(1));
                dataTable.setTime(cursor.getString(2));
                dataTable.setPerson(cursor.getString(3));
                dataTable.setPlace((cursor.getString(4)));

                arrayList.add(dataTable);

            }while(cursor.moveToNext());
        }

        return arrayList;
    }

    public ArrayList<ListTable> getAllList()
    {
        String sql;
        /*
        if (0==0)
        {
            sql = "SELECT * FROM " + TABLE_LIST + " ORDER BY " + COLUMN_TIME + " DESC";
        }
        else if(1==1)
        {
            sql = "SELECT * FROM " + TABLE_LIST + " ORDER BY " + COLUMN_PERSON + " DESC";
        }
        else if (2==2)
        {
            sql = "SELECT * FROM " + TABLE_LIST + " ORDER BY " + COLUMN_PLACE + " DESC";
        }
        else
        {
            sql = "SELECT * FROM " + TABLE_LIST + " ORDER BY " + COLUMN_ID + " DESC";
        }

         */


        sql = "SELECT * FROM " + TABLE_LIST;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListTable> arrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst())
        {
            do{
                ListTable listTable=new ListTable();

                listTable.setId((cursor.getInt(0)));
                listTable.setTime(cursor.getString(1));
                listTable.setPerson(cursor.getString(2));
                listTable.setPlace((cursor.getString(3)));

                arrayList.add(listTable);

            }while(cursor.moveToNext());
        }

        return arrayList;
    }
}
