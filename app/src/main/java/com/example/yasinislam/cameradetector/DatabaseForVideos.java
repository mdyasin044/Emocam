package com.example.yasinislam.cameradetector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yasin-Islam on 12/31/2017.
 */

public class DatabaseForVideos extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "Emovideos.db";
    public static String TABLE_NAME = "Emovideo";
    public static String COL_1 = "EMO";
    public static String COL_2 = "VIDEO";

    public DatabaseForVideos(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (EMO TEXT PRIMARY KEY, VIDEO TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String emo, String video){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,emo);
        contentValues.put(COL_2,video);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateData(String emo, String video) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, emo);
        contentValues.put(COL_2, video);
        int result = db.update(TABLE_NAME, contentValues, "EMO = ?",new String[] { emo });
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);

    }

    public static String getVideoPath(String emo){
        Cursor res = MainActivity.emovideohelper.getAllData();
        if(res != null){
            if(res.getColumnCount() != 0){
                while (res.moveToNext()){
                    if(res.getString(0).equals(emo)) return res.getString(1);
                }
            }
        }
        return "No_Video";
    }

}
