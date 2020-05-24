package com.example.sharedcfc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//Sqlite database used to store logged in user instance
public class DatabaseHelper extends SQLiteOpenHelper {
    private final String LOGTAG="Scan QrCode";
    public static String DB_NAME="mydb.sqlite";
    public static String TABLE_NAME="login_instance";
    public static String COL1="email";
    public static String COL2="refresh_token";
    public static String COL3="name";
    public static String COL4="imageUri";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        SQLiteDatabase db=this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create TABLE "+TABLE_NAME+"(email text primary key, refresh_token text, name text, imageUri text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists "+TABLE_NAME);
        onCreate(db);
    }
    public long insertRecord(String email, String refresh_token, String name, String imageUri)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COL1,email);
        contentValues.put(COL2,refresh_token);
        contentValues.put(COL3,name);
        contentValues.put(COL4,imageUri);
        return db.insert(TABLE_NAME, null, contentValues);
    }

    public boolean isDatabaseTableEmpty(){
        String[] columns = {"email"};
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        close();
        Log.d("Database count is ",Integer.toString(count));
        if(count > 0){
            return false;
        } else {
            return true;
        }
    }
    public String[] fetchLocalInstance(){
        String[] columns = {"email, refresh_token, name, imageUri"};
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToLast();
//        Log.d(LOGTAG,cursor.getColumnName(0));
        String[] row = new String[4];
        row[0]=cursor.getString(cursor.getColumnIndex("email"));
        row[1]=cursor.getString(cursor.getColumnIndex("refresh_token"));
        row[2]=cursor.getString(cursor.getColumnIndex("name"));
        row[3]=cursor.getString(cursor.getColumnIndex("imageUri"));
//        Log.d("table content",row[0]+row[1]+row[2]+row[3]);
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String code = cursor.getString( cursor.getColumnIndex("name") );
//                Log.d(LOGTAG,"Database query result:"+code );
            }
        }
        return row;
    }
    public void deleteInstance(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Drop table if exists "+TABLE_NAME);
        onCreate(db);
    }
    public String getEmail(){
        String[] columns = {"email"};
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToLast();
        String row;
        row=cursor.getString(cursor.getColumnIndex("email"));
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String code = cursor.getString( cursor.getColumnIndex("name") );
                Log.d(LOGTAG,"Database query result:"+code );
            }
        }
        return row;
    }
    public String getName(){
        String[] columns = {"name"};
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToLast();
        String row;
        row=cursor.getString(cursor.getColumnIndex("name"));
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String code = cursor.getString( cursor.getColumnIndex("name") );
            }
        }
        return row;
    }
}