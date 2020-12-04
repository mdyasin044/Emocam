package com.example.yasinislam.cameradetector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Yasin-Islam on 12/29/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "Settings.db";
    public static String TABLE_NAME = "Settings";
    public static String COL_1 = "PERMISSION";
    public static String COL_2 = "STATE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (PERMISSION TEXT PRIMARY KEY, STATE INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String permission, int state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,permission);
        contentValues.put(COL_2,state);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateData(String permission, int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, permission);
        contentValues.put(COL_2, state);
        int result = db.update(TABLE_NAME, contentValues, "PERMISSION = ?",new String[] { permission });
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);

    }

    public static void updateDatabase(){
        if(ImageHelper.faceDetectionPermission) MainActivity.databaseHelper.updateData("facedetect", 1);
        else MainActivity.databaseHelper.updateData("facedetect", 0);
        if(ImageHelper.emotionDetectionPermission) MainActivity.databaseHelper.updateData("emotiondetect", 1);
        else MainActivity.databaseHelper.updateData("emotiondetect", 0);
        if(ImageHelper.landmarksDetectionPermission) MainActivity.databaseHelper.updateData("landmarkdetect", 1);
        else MainActivity.databaseHelper.updateData("landmarkdetect", 0);
        if(ImageHelper.captureSmilesPermission) MainActivity.databaseHelper.updateData("capturesmiles", 1);
        else MainActivity.databaseHelper.updateData("capturesmiles", 0);
        if(ImageHelper.capturewithEmotionsPermission) MainActivity.databaseHelper.updateData("capturewithemotion", 1);
        else MainActivity.databaseHelper.updateData("capturewithemotion", 0);
        if(ImageHelper.emojipermission) MainActivity.databaseHelper.updateData("emoji", 1);
        else MainActivity.databaseHelper.updateData("emoji", 0);
        if(ImageHelper.soundpermission) MainActivity.databaseHelper.updateData("sound", 1);
        else MainActivity.databaseHelper.updateData("sound", 0);

        if(ImageHelper.faceDetectionPermission_for_photo) MainActivity.databaseHelper.updateData("facedetect_photo", 1);
        else MainActivity.databaseHelper.updateData("facedetect_photo", 0);
        if(ImageHelper.faceDetectionPermission_for_photo) MainActivity.databaseHelper.updateData("emotiondetect_photo", 1);
        else MainActivity.databaseHelper.updateData("emotiondetect_photo", 0);
        if(ImageHelper.faceDetectionPermission_for_photo) MainActivity.databaseHelper.updateData("landmarkdetect_photo", 1);
        else MainActivity.databaseHelper.updateData("landmarkdetect_photo", 0);
        if(ImageHelper.emojipermission_for_photo) MainActivity.databaseHelper.updateData("emoji_photo", 1);
        else MainActivity.databaseHelper.updateData("emoji_photo", 0);
    }
}
