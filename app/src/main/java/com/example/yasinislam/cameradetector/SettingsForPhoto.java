package com.example.yasinislam.cameradetector;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsForPhoto extends AppCompatActivity {

    Switch faceDetection;
    Switch emotionDetection;
    Switch landmarkDetection;
    Switch emoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);                    //set up no title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);              //set up full screen
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));   //set up Action bar Color to Black
        getSupportActionBar().setDisplayShowTitleEnabled(false);          //set up Action Bar Title true

        setContentView(R.layout.activity_settings_for_photo);

        faceDetection = (Switch) findViewById(R.id.facedetection_);
        faceDetection.setChecked(ImageHelper.faceDetectionPermission_for_photo);
        faceDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ImageHelper.faceDetectionPermission_for_photo = isChecked;

                if(!isChecked){
                    emotionDetection.setChecked(false);
                    ImageHelper.emotionDetectionPermission_for_photo = false;
                    landmarkDetection.setChecked(false);
                    ImageHelper.landmarksDetectionPermission_for_photo = false;
                    emoji.setChecked(false);
                    ImageHelper.emojipermission_for_photo = false;
                }

                DatabaseHelper.updateDatabase();
            }
        });

        emotionDetection = (Switch) findViewById(R.id.emotiondetection_);
        emotionDetection.setChecked(ImageHelper.emotionDetectionPermission_for_photo);
        emotionDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(ImageHelper.faceDetectionPermission_for_photo) ImageHelper.emotionDetectionPermission_for_photo = isChecked;
                else{
                    emotionDetection.setChecked(false);
                    Toast.makeText(SettingsForPhoto.this,"Face Detection must be enabled",Toast.LENGTH_LONG).show();
                }

                DatabaseHelper.updateDatabase();
            }
        });

        landmarkDetection = (Switch) findViewById(R.id.landmarkdetection_);
        landmarkDetection.setChecked(ImageHelper.landmarksDetectionPermission_for_photo);
        landmarkDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(ImageHelper.faceDetectionPermission_for_photo) ImageHelper.landmarksDetectionPermission_for_photo = isChecked;
                else{
                    landmarkDetection.setChecked(false);
                    Toast.makeText(SettingsForPhoto.this,"Face Detection must be enabled",Toast.LENGTH_LONG).show();
                }

                DatabaseHelper.updateDatabase();
            }
        });

        emoji = (Switch) findViewById(R.id.emoji_);
        emoji.setChecked(ImageHelper.emojipermission_for_photo);
        emoji.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(ImageHelper.faceDetectionPermission_for_photo) ImageHelper.emojipermission_for_photo = isChecked;
                else{
                    emoji.setChecked(false);
                    Toast.makeText(SettingsForPhoto.this,"Face Detection must be enabled",Toast.LENGTH_LONG).show();
                }

                DatabaseHelper.updateDatabase();
            }
        });
    }
}
