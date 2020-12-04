package com.example.yasinislam.cameradetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Videomode extends AppCompatActivity implements Detector.ImageListener {
    CameraDetector detector;
    SurfaceView surfaceView;
    SurfaceView surfaceView1;
    SurfaceHolder surfaceHolder;


    Paint paint;
    Frame currentFrame;
    List<Face> currentFaces;
    float currentV;

    Button capture;
    Button changeCamera;
    Button settings;
    Button save;
    Button delete;

    int cameraType = 1;

    public static TextToSpeech textToSpeech;
    public static int result;

    public static Map<String, Bitmap> bitmapByName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);                    //set up no title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);              //set up full screen
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));   //set up Action bar Color to Black
        getSupportActionBar().setDisplayShowTitleEnabled(false);          //set up No Action Bar Title

        setContentView(R.layout.activity_videomode);

        capture = (Button) findViewById(R.id.capture);
        changeCamera = (Button) findViewById(R.id.changeCamera);
        settings = (Button) findViewById(R.id.settings);
        save = (Button) findViewById(R.id.yes);
        delete = (Button) findViewById(R.id.no);

        save.setVisibility(Button.INVISIBLE);
        delete.setVisibility(Button.INVISIBLE);

        textToSpeech = new TextToSpeech(Videomode.this, new TextToSpeech.OnInitListener() {
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

        surfaceView = (SurfaceView) findViewById(R.id.cameraPreview);
        surfaceView.setSecure(true);

        surfaceView1 = (SurfaceView) findViewById(R.id.changedPreview);
        surfaceView1.setZOrderOnTop(true);

        surfaceHolder = surfaceView1.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(80);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        bitmapByName = new HashMap<>();
        setBitmapByName();

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, surfaceView, 4, Detector.FaceDetectorMode.LARGE_FACES);
        detector.setImageListener(this);
        detector.setDetectAllAppearances(true);
        detector.setDetectAllEmotions(true);
        detector.setDetectAllExpressions(true);
        detector.setDetectAllEmojis(true);
        detector.setMaxProcessRate(10);
        detector.start();

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setVisibility(Button.VISIBLE);
                delete.setVisibility(Button.VISIBLE);

                changeCamera.setVisibility(Button.INVISIBLE);
                settings.setVisibility(Button.INVISIBLE);
                capture.setVisibility(Button.INVISIBLE);

                detector.stop();
            }
        });


        changeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameraType == 1) {
                    detector.setCameraType(CameraDetector.CameraType.CAMERA_BACK);
                    cameraType = 2;
                }
                else if(cameraType == 2) {
                    detector.setCameraType(CameraDetector.CameraType.CAMERA_FRONT);
                    cameraType = 1;
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingspage = new Intent(Videomode.this,Settings.class);
                startActivity(settingspage);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detector.start();

                if(currentFrame != null) {
                    boolean success = ImageHelper.saveImage(currentFaces, currentFrame, currentV, paint, cameraType);
                    if(success) Toast.makeText(Videomode.this,"Image is saved",Toast.LENGTH_LONG).show();
                    else Toast.makeText(Videomode.this,"An error occurs while saving the image",Toast.LENGTH_LONG).show();
                }

                changeCamera.setVisibility(Button.VISIBLE);
                settings.setVisibility(Button.VISIBLE);
                capture.setVisibility(Button.VISIBLE);

                delete.setVisibility(Button.INVISIBLE);
                save.setVisibility(Button.INVISIBLE);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detector.start();

                changeCamera.setVisibility(Button.VISIBLE);
                settings.setVisibility(Button.VISIBLE);
                capture.setVisibility(Button.VISIBLE);

                save.setVisibility(Button.INVISIBLE);
                delete.setVisibility(Button.INVISIBLE);
            }
        });
    }

    @Override
    public void onImageResults(List<Face> faces, Frame frame, float v) {
        currentFrame = frame;
        currentFaces = faces;
        currentV = v;

        Canvas c = surfaceHolder.lockCanvas();
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);

        Canvas cc = ImageHelper.getCanvas(c, faces, frame, v, paint, cameraType);
        if (cc != null) {
            surfaceHolder.unlockCanvasAndPost(cc);
        }

        if(ImageHelper.captureSmilesPermission) {
            boolean issmile = ImageHelper.isSmile(faces);
            if (issmile) {
                save.setVisibility(Button.VISIBLE);
                delete.setVisibility(Button.VISIBLE);

                changeCamera.setVisibility(Button.INVISIBLE);
                settings.setVisibility(Button.INVISIBLE);
                capture.setVisibility(Button.INVISIBLE);

                detector.stop();

                Toast.makeText(Videomode.this,"Smile is detected.",Toast.LENGTH_LONG).show();

                return;
            }
        }
    }

    private void setBitmapByName() {
        bitmapByName.put("DISAPPOINTED", BitmapFactory.decodeResource(getResources(), R.drawable.disappointed_emoji));
        bitmapByName.put("FLUSHED", BitmapFactory.decodeResource(getResources(), R.drawable.flushed_emoji));
        bitmapByName.put("KISSING", BitmapFactory.decodeResource(getResources(), R.drawable.kissing_emoji));
        bitmapByName.put("LAUGHING", BitmapFactory.decodeResource(getResources(), R.drawable.laughing_emoji));
        bitmapByName.put("RAGE", BitmapFactory.decodeResource(getResources(), R.drawable.rage_emoji));
        bitmapByName.put("RELAXED", BitmapFactory.decodeResource(getResources(), R.drawable.relaxed_emoji));
        bitmapByName.put("SCREAM", BitmapFactory.decodeResource(getResources(), R.drawable.scream_emoji));
        bitmapByName.put("SMILEY", BitmapFactory.decodeResource(getResources(), R.drawable.smiley_emoji));
        bitmapByName.put("SMIRK", BitmapFactory.decodeResource(getResources(), R.drawable.smirk_emoji));
        bitmapByName.put("WINK", BitmapFactory.decodeResource(getResources(), R.drawable.wink_emoji));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(textToSpeech != null){
            detector.stop();
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
