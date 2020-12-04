package com.example.yasinislam.cameradetector;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public class EmoVideoSettings extends AppCompatActivity {

    EditText happy;
    EditText sad ;
    EditText angry ;
    EditText surprised;
    Button happybtn;
    Button sadbtn;
    Button angrybtn;
    Button surprisedbtn;

    int button_no = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);                    //set up no title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);              //set up full screen
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));   //set up Action bar Color to Black
        getSupportActionBar().setDisplayShowTitleEnabled(false);          //set up No Action Bar Title

        setContentView(R.layout.activity_emo_video_settings);

        happy = (EditText) findViewById(R.id.happyvideo);
        sad = (EditText) findViewById(R.id.sadvideo);
        angry = (EditText) findViewById(R.id.angryvideo);
        surprised = (EditText) findViewById(R.id.surprisedvideo);
        happybtn = (Button) findViewById(R.id.happybrowse);
        sadbtn = (Button) findViewById(R.id.sadbrowse);
        angrybtn = (Button) findViewById(R.id.angrybrowse);
        surprisedbtn = (Button) findViewById(R.id.surprisedbrowse);

        setVideoFromDatabase();

        happybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_no = 1;
                browse();
            }
        });

        sadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_no = 2;
                browse();
            }
        });

        angrybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_no = 3;
                browse();
            }
        });

        surprisedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_no = 4;
                browse();
            }
        });
    }

    public void browse(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"), 0x0001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0x0001) {
                String selectedImagePath = "";

                Uri selectedImageUri = data.getData();

                if(selectedImageUri != null)
                    selectedImagePath = ImageFilePath.getPath(EmoVideoSettings.this, selectedImageUri);

                if(button_no == 1){
                    happy.setText(selectedImagePath);
                    MainActivity.emovideohelper.updateData("happy", selectedImagePath);
                }
                if(button_no == 2){
                    sad.setText(selectedImagePath);
                    MainActivity.emovideohelper.updateData("sad", selectedImagePath);
                }
                if(button_no == 3){
                    angry.setText(selectedImagePath);
                    MainActivity.emovideohelper.updateData("angry", selectedImagePath);
                }
                if(button_no == 4){
                    surprised.setText(selectedImagePath);
                    MainActivity.emovideohelper.updateData("surprised", selectedImagePath);
                }
            }
        }
    }

    public void setVideoFromDatabase(){
        Cursor res = MainActivity.emovideohelper.getAllData();
        if(res == null ) {
            Toast.makeText(this, "Setting Permissions from Database Failed!", Toast.LENGTH_LONG).show();
        }
        else if( res.getColumnCount() == 0){
            Toast.makeText(this, "Setting Permissions from Database Failed!", Toast.LENGTH_LONG).show();
        }
        else {
            res.moveToNext();
            happy.setText(res.getString(1));
            res.moveToNext();
            sad.setText(res.getString(1));
            res.moveToNext();
            angry.setText(res.getString(1));
            res.moveToNext();
            surprised.setText(res.getString(1));
        }
    }
}
