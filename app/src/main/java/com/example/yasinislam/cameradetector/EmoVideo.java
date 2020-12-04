package com.example.yasinislam.cameradetector;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class EmoVideo extends AppCompatActivity implements Detector.ImageListener {

    CameraDetector cameraDetector;
    SurfaceView surfaceView;
    AlertDialog dialog;

    Button setvideos;

    public static TextToSpeech textToSpeech;
    public static int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);                    //set up no title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);              //set up full screen
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));   //set up Action bar Color to Black
        getSupportActionBar().setDisplayShowTitleEnabled(false);          //set up No Action Bar Title

        setContentView(R.layout.activity_emo_video);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, 2000);

        setvideos = (Button) findViewById(R.id.setvideos);

        surfaceView = (SurfaceView) findViewById(R.id.emopreview);

        textToSpeech = new TextToSpeech(EmoVideo.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    result = textToSpeech.setLanguage(Locale.UK);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Feature is not supported in your device", Toast.LENGTH_LONG).show();
                }
            }
        });

        cameraDetector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, surfaceView, 4, Detector.FaceDetectorMode.LARGE_FACES);
        cameraDetector.setImageListener(this);
        cameraDetector.setDetectAllAppearances(true);
        cameraDetector.setDetectAllEmotions(true);
        cameraDetector.setDetectAllExpressions(true);
        cameraDetector.setDetectAllEmojis(true);
        cameraDetector.setMaxProcessRate(10);
        cameraDetector.start();

        setvideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmoVideo.this, EmoVideoSettings.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onImageResults(List<Face> faces, Frame frame, float v) {
        for(int i=0;i<faces.size();i++){
            Face face = faces.get(i);
            String emo = ImageHelper.getEmotion(face, 50);

            if(!emo.equals("")) {
                if (emo.equals("Joy")) {
                    try {
                        playVideo("happy", "You are happy. lets enhance your happiness.");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                else if (emo.equals("Sad")){
                    try {
                        playVideo("sad", "You are sad. Lets make you more sad.");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                else if(emo.equals("Angry")){
                    try {
                        playVideo("angry", "You are angry. Lets make you cool.");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                else if(emo.equals("Surprise")){
                    try {
                        playVideo("surprised", "You are surprised. Lets make you more surprise.");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void playVideo(String emo, String msg) throws URISyntaxException {
        File file = new File(DatabaseForVideos.getVideoPath(emo));
        if(!file.exists()){
            showMessage("File does not exist.");
        }
        else {
            Intent tostart = new Intent(Intent.ACTION_VIEW);
            tostart.setDataAndType(Uri.parse(DatabaseForVideos.getVideoPath(emo)), "video/*");
            tostart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (textToSpeech != null) {
                if (!textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                    textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);

                    startActivity(tostart);
                }
            }


        }
    }

    private void showMessage(String Message) {
        cameraDetector.stop();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(Message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                cameraDetector.start();
            }
        });
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(textToSpeech != null){
            cameraDetector.stop();
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
