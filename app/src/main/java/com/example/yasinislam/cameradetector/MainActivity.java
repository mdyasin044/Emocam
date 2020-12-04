package com.example.yasinislam.cameradetector;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    Button videomode;
    Button photomode;
    Button emovideoplayer;
    Button about;
    ImageView imageView;

    AlertDialog.Builder builder;

    public static DatabaseHelper databaseHelper;
    public static DatabaseForVideos emovideohelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);                    //set up no title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);              //set up full screen
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));   //set up Action bar Color to Black
        getSupportActionBar().setDisplayShowTitleEnabled(false);          //set up No Action Bar Title

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        initializeDatabase();
        setPermissionsFromDatabase();
        //showSettings();
        //showEmoVideos();

        emovideohelper = new DatabaseForVideos(this);
        initializeDatabaseForEmoVideos();

        videomode = (Button) findViewById(R.id.videomode);
        photomode = (Button) findViewById(R.id.photomode);
        emovideoplayer = (Button) findViewById(R.id.emovideoplayer);
        about = (Button) findViewById(R.id.about);

        videomode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPermissionsAreGranted()){
                    showMessage("Alert", "Permissions are needed to be granted.");
                    return;
                }

                Intent videopage = new Intent(MainActivity.this, Videomode.class);
                startActivity(videopage);
            }
        });

        photomode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPermissionsAreGranted()){
                    showMessage("Alert", "Permissions are needed to be granted.");
                    return;
                }
                Intent photopage = new Intent(MainActivity.this, Photomode.class);
                startActivity(photopage);
            }
        });

        emovideoplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPermissionsAreGranted()){
                    showMessage("Alert", "Permissions are needed to be granted.");
                    return;
                }
                Intent emovideopage = new Intent(MainActivity.this, EmoVideo.class);
                startActivity(emovideopage);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Message = "Developer: \n" +
                                 "MD. Yasin Islam, 1405044, CSE, BUET\n" +
                                 "MD. Shariful Shohan Nayok, 1405035, CSE, BUET\n\n" +
                                 "Layout Design: \n" +
                                 "MD. Sium\n" +
                                 "MD. Yasin Islam\n\n" +
                                 "Special thanks to, Papon Sir and\n" +
                                 "my friends.";

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Credits");
                builder.setMessage(Message);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setLayout(900,1500);
                dialog.show();
            }
        });

        imageView = (ImageView) findViewById(R.id.imageview2);
        Glide.with(MainActivity.this)
                .load(R.drawable.giphy)
                .asGif()
                .placeholder(R.drawable.giphy)
                .crossFade()
                .into(imageView);
    }

    private boolean checkPermissionsAreGranted() {
        boolean result = true;
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        if(res != PackageManager.PERMISSION_GRANTED){
            result = false;
        }
        permission = "android.permission.READ_EXTERNAL_STORAGE";
        res = this.checkCallingOrSelfPermission(permission);
        if(res != PackageManager.PERMISSION_GRANTED){
            result = false;
        }
        permission = "android.permission.CAMERA";
        res = this.checkCallingOrSelfPermission(permission);
        if(res != PackageManager.PERMISSION_GRANTED){
            result = false;
        }
        return result;
    }

    private void setPermissionsFromDatabase() {
        Cursor res = databaseHelper.getAllData();
        if(res == null ) {
            showMessage("Error","Setting Permissions from Database Failed!");
        }
        else if( res.getColumnCount() == 0){
            showMessage("Error","Setting Permissions from Database Failed!");
        }
        else {
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.faceDetectionPermission = false;
            else ImageHelper.faceDetectionPermission = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.emotionDetectionPermission = false;
            else ImageHelper.emotionDetectionPermission = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.landmarksDetectionPermission = false;
            else ImageHelper.landmarksDetectionPermission = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.captureSmilesPermission = false;
            else ImageHelper.captureSmilesPermission = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.capturewithEmotionsPermission = false;
            else ImageHelper.capturewithEmotionsPermission = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.emojipermission = false;
            else ImageHelper.emojipermission = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.soundpermission = false;
            else ImageHelper.soundpermission = true;
            res.moveToNext();

            if(res.getString(1).equals("0")) ImageHelper.faceDetectionPermission_for_photo = false;
            else ImageHelper.faceDetectionPermission_for_photo = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.emotionDetectionPermission_for_photo = false;
            else ImageHelper.emotionDetectionPermission_for_photo = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.landmarksDetectionPermission_for_photo = false;
            else ImageHelper.landmarksDetectionPermission_for_photo = true;
            res.moveToNext();
            if(res.getString(1).equals("0")) ImageHelper.emojipermission_for_photo = false;
            else ImageHelper.emojipermission_for_photo = true;
        }
    }

    private boolean initializeDatabase() {
        boolean success = true;
        boolean result = databaseHelper.insertData("facedetect", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("emotiondetect", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("landmarkdetect", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("capturesmiles", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("capturewithemotion", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("emoji", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("sound", 0);
        if(!result) success = false;

        result = databaseHelper.insertData("facedetect_photo", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("emotiondetect_photo", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("landmarkdetect_photo", 0);
        if(!result) success = false;
        result = databaseHelper.insertData("emoji_photo", 0);
        if(!result) success = false;

        return success;
    }

    private boolean initializeDatabaseForEmoVideos() {
        boolean success = true;
        boolean result = emovideohelper.insertData("happy", "No_video");
        if(!result) success = false;
        result = emovideohelper.insertData("sad", "No_video");
        if(!result) success = false;
        result = emovideohelper.insertData("angry", "No_video");
        if(!result) success = false;
        result = emovideohelper.insertData("surprised", "No_video");
        if(!result) success = false;

        return success;
    }

    private void showSettings(){
        Cursor res = databaseHelper.getAllData();
        if(res == null) {
            showMessage("Error","Nothing found");
        }
        else {
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                buffer.append("PERMISSION :" + res.getString(0) + "\n");
                buffer.append("STATE :" + res.getString(1) + "\n\n");
            }
            showMessage("Data", buffer.toString());
        }
    }

    private void showEmoVideos(){
        Cursor res = emovideohelper.getAllData();
        if(res == null) {
            showMessage("Error","Nothing found");
        }
        else {
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                buffer.append("EMO :" + res.getString(0) + "\n");
                buffer.append("VIDEO :" + res.getString(1) + "\n\n");
            }
            showMessage("Data", buffer.toString());
        }
    }

    private void showMessage(String title, String Message) {
        builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
