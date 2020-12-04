package com.example.yasinislam.cameradetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.affectiva.android.affdex.sdk.detector.PhotoDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Photomode extends AppCompatActivity implements Detector.ImageListener{

    ImageView imageView;
    Bitmap bitmap;
    Frame.BitmapFrame frame;
    Paint paint;

    Button analyze;
    Button load;
    Button save;
    Button settings;

    public static Map<String, Bitmap> bitmapByName;

    private static final int IMAGE_GALLERY = 0x0001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);                    //set up no title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);              //set up full screen
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));   //set up Action bar Color to Black
        getSupportActionBar().setDisplayShowTitleEnabled(false);          //set up No Action Bar Title

        setContentView(R.layout.activity_photomode);

        //bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/tom.jpg");
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);

        frame = new Frame.BitmapFrame(bitmap, Frame.COLOR_FORMAT.UNKNOWN_TYPE);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

        analyze = (Button) findViewById(R.id.analyze);
        load = (Button) findViewById(R.id.load);
        save = (Button) findViewById(R.id.save_);
        settings = (Button) findViewById(R.id.settings_);

        bitmapByName = new HashMap<>();
        setBitmapByName();

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        final PhotoDetector photoDetector = new PhotoDetector(this,  4, Detector.FaceDetectorMode.LARGE_FACES );
        photoDetector.setImageListener(this);
        photoDetector.setDetectAllAppearances(true);
        photoDetector.setDetectAllEmotions(true);
        photoDetector.setDetectAllExpressions(true);
        photoDetector.setDetectAllEmojis(true);
        photoDetector.start();

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, IMAGE_GALLERY);
                }catch(Exception exp){
                    Log.i("Error",exp.toString());
                }
            }
        });

        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame = new Frame.BitmapFrame(bitmap, Frame.COLOR_FORMAT.UNKNOWN_TYPE);
                photoDetector.process(frame);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingspage = new Intent(Photomode.this,SettingsForPhoto.class);
                startActivity(settingspage);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap != null) {
                    boolean success = ImageHelper.saveImage(bitmap);
                    if(success) Toast.makeText(Photomode.this,"Image is saved",Toast.LENGTH_LONG).show();
                    else Toast.makeText(Photomode.this,"An error occurs while saving the image",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if(requestCode == IMAGE_GALLERY)
            {
                try
                {
                    Uri imageUri = data.getData();
                    Toast.makeText(this, imageUri.getPath(), Toast.LENGTH_LONG).show();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(bitmap);
                }
                catch(Exception ex)
                {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onImageResults(List<Face> faces, Frame frame, float v) {

        if(faces == null){
            Toast.makeText(this, "faces is null", Toast.LENGTH_LONG).show();
            return;
        }

        if(faces.size() == 0){
            Toast.makeText(this, "faces size is zero", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap blackBitmap = ImageHelper.drawCanvas(350, 400, faces , frame, paint);

        if(blackBitmap != null){
            Toast.makeText(this, "Face analysis succeed", Toast.LENGTH_LONG).show();
            imageView.setImageBitmap(blackBitmap);
            bitmap = blackBitmap;
        }

        else Toast.makeText(this, "blackBitmap is null", Toast.LENGTH_LONG).show();
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


}
