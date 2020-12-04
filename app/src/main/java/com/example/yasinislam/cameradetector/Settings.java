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

public class Settings extends AppCompatActivity {

    Switch faceDetection;
    Switch emotionDetection;
    Switch landmarkDetection;
    Switch capturesmiles;
    Switch capturewithemotions;
    Switch emoji;
    Switch sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);                    //set up no title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);              //set up full screen
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));   //set up Action bar Color to Black
        getSupportActionBar().setDisplayShowTitleEnabled(false);          //set up Action Bar Title true

        setContentView(R.layout.activity_settings);

        faceDetection = (Switch) findViewById(R.id.facedetection);
        faceDetection.setChecked(ImageHelper.faceDetectionPermission);
        faceDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ImageHelper.faceDetectionPermission = isChecked;

                if(!isChecked){
                    emotionDetection.setChecked(false);
                    ImageHelper.emotionDetectionPermission = false;
                    landmarkDetection.setChecked(false);
                    ImageHelper.landmarksDetectionPermission = false;
                    emoji.setChecked(false);
                    ImageHelper.emojipermission = false;
                }

                DatabaseHelper.updateDatabase();
            }
        });

        emotionDetection = (Switch) findViewById(R.id.emotiondetection);
        emotionDetection.setChecked(ImageHelper.emotionDetectionPermission);
        emotionDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(ImageHelper.faceDetectionPermission) ImageHelper.emotionDetectionPermission = isChecked;
                else{
                    emotionDetection.setChecked(false);
                    Toast.makeText(Settings.this,"Face Detection must be enabled",Toast.LENGTH_LONG).show();
                }

                DatabaseHelper.updateDatabase();
            }
        });

        landmarkDetection = (Switch) findViewById(R.id.landmarkdetection);
        landmarkDetection.setChecked(ImageHelper.landmarksDetectionPermission);
        landmarkDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(ImageHelper.faceDetectionPermission) ImageHelper.landmarksDetectionPermission = isChecked;
                else{
                    landmarkDetection.setChecked(false);
                    Toast.makeText(Settings.this,"Face Detection must be enabled",Toast.LENGTH_LONG).show();
                }

                DatabaseHelper.updateDatabase();
            }
        });

        capturesmiles = (Switch) findViewById(R.id.capturesmiles);
        capturesmiles.setChecked(ImageHelper.captureSmilesPermission);
        capturesmiles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ImageHelper.captureSmilesPermission = isChecked;

                DatabaseHelper.updateDatabase();
            }
        });

        capturewithemotions = (Switch) findViewById(R.id.capturewithemotions);
        capturewithemotions.setChecked(ImageHelper.capturewithEmotionsPermission);
        capturewithemotions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ImageHelper.capturewithEmotionsPermission = isChecked;

                DatabaseHelper.updateDatabase();
            }
        });

        emoji = (Switch) findViewById(R.id.emoji);
        emoji.setChecked(ImageHelper.emojipermission);
        emoji.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(ImageHelper.faceDetectionPermission) ImageHelper.emojipermission = isChecked;
                else{
                    emoji.setChecked(false);
                    Toast.makeText(Settings.this,"Face Detection must be enabled",Toast.LENGTH_LONG).show();
                }

                DatabaseHelper.updateDatabase();
            }
        });

        sound = (Switch) findViewById(R.id.sound);
        sound.setChecked(ImageHelper.soundpermission);
        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ImageHelper.soundpermission = isChecked;

                DatabaseHelper.updateDatabase();
            }
        });
    }
}
