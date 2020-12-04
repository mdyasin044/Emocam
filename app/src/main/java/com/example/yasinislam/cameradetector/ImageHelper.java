package com.example.yasinislam.cameradetector;

import com.affectiva.android.affdex.sdk.Frame;
import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

public class ImageHelper {

    public static boolean faceDetectionPermission = true;                         //Permissions for Videomode
    public static boolean emotionDetectionPermission = true;
    public static boolean landmarksDetectionPermission = false;
    public static boolean captureSmilesPermission = false;
    public static boolean capturewithEmotionsPermission = false;
    public static boolean emojipermission = true;
    public static boolean soundpermission = true;

    public static boolean faceDetectionPermission_for_photo = true;               //Permissions for Photomode
    public static boolean emotionDetectionPermission_for_photo = true;
    public static boolean landmarksDetectionPermission_for_photo = false;
    public static boolean emojipermission_for_photo = true;

    public static String prestate = "";

    public static String getEmotion(Face face, int thres){
        float mx = Math.max(face.emotions.getJoy(),
                Math.max(face.emotions.getAnger(),
                        Math.max(face.emotions.getSadness(),
                                Math.max(face.emotions.getFear(),face.emotions.getSurprise()))));

        String emo = "";

        if(mx < thres) return emo;

        if(mx == face.emotions.getJoy()){
            emo = "Joy";
        }
        else if(mx == face.emotions.getAnger()){
            emo = "Angry";
        }
        else if(mx == face.emotions.getSadness()){
            emo = "Sad";
        }
        else if(mx == face.emotions.getFear()){
            emo = "Fear";
        }
        else if(mx == face.emotions.getSurprise()){
            emo = "Surprise";
        }

        return emo;
    }


    public static Canvas getCanvas(Canvas c, List<Face> faces, Frame frame, float v, Paint paint, int cameraType){

        for(int i=0;i<faces.size();i++) {
            Face face = faces.get(i);
            Rect boundingbox = new Rect(c.getWidth(), c.getHeight(), 0, 0);

            for (PointF point : face.getFacePoints()) {
                float x;
                if(cameraType == 1)
                    x = (float) c.getWidth() - point.x * ((float) c.getWidth() / (float) frame.getHeight());  //for front camera
                else
                    x = point.x * ((float) c.getWidth() / (float) frame.getHeight());  //for back camera

                float y = point.y * ((float) c.getHeight() / (float) frame.getWidth());

                boundingbox.union(Math.round(x), Math.round(y));
                boundingbox.union(Math.round(x), Math.round(y));

                if(landmarksDetectionPermission) c.drawCircle(x, y, 2, paint);
            }

            String emo = "";
            if(emotionDetectionPermission) {
                emo = getEmotion(face, 20);
                if (emo.equals("Joy")) paint.setColor(Color.GREEN);
                if (emo.equals("Angry")) paint.setColor(Color.RED);
                if (emo.equals("Sad")) paint.setColor(Color.rgb(173, 216, 230));
                if (emo.equals("Fear")) paint.setColor(Color.rgb(255, 165, 0));
                if (emo.equals("Surprise")) paint.setColor(Color.YELLOW);

                c.drawText( emo , boundingbox.left, boundingbox.bottom + 100, paint);

                if(Videomode.bitmapByName.get(face.emojis.getDominantEmoji().name()) != null && emojipermission) {
                    Bitmap b = Videomode.bitmapByName.get(face.emojis.getDominantEmoji().name());
                    if (emo.equals("Joy")) b = Videomode.bitmapByName.get("RELAXED");
                    if (emo.equals("Angry")) b = Videomode.bitmapByName.get("RAGE");
                    if (emo.equals("Sad")) b = Videomode.bitmapByName.get("DISAPPOINTED");
                    if (emo.equals("Fear")) b = Videomode.bitmapByName.get("FLUSHED");
                    if (emo.equals("Surprise")) b = Videomode.bitmapByName.get("SCREAM");
                    b = Bitmap.createScaledBitmap(b, boundingbox.width()/2, boundingbox.height()/2, false);
                    c.drawBitmap(b, boundingbox.left, boundingbox.top - boundingbox.height()/2, paint);
                }
            }

            if(faceDetectionPermission) c.drawRect(boundingbox.left, boundingbox.top, boundingbox.right, boundingbox.bottom, paint);
            paint.setColor(Color.WHITE);

            if(!emo.equals(prestate) && emo != "" && emotionDetectionPermission && soundpermission){
                prestate = emo;
                if(Videomode.textToSpeech != null) {
                    Videomode.textToSpeech.stop();
                    Videomode.textToSpeech.speak(emo, TextToSpeech.QUEUE_FLUSH, null);
                }
                //saveImage(faces, frame, v, paint, cameraType);
            }
        }

        return c;
    }

    public static boolean isSmile(List<Face> faces){
        for(int i=0;i<faces.size();i++){
            if(faces.get(i).emotions.getJoy() > 50) return true;
        }
        return false;
    }


    public static boolean saveImage(List<Face> faces, Frame frame, float v, Paint paint, int cameraType) {

        byte[] pixels = ((Frame.ByteArrayFrame) frame).getByteArray();    // convert Frame YUVNV21 to Bitmap
        YuvImage yuvImage = new YuvImage(pixels, ImageFormat.NV21, frame.getWidth(), frame.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            Log.e("IOException", "Exception while closing output stream", e);
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        Matrix matrix = new Matrix();                                       // rotate Bitmap by 90 degree angle anticlockwise
        matrix.postRotate((float) frame.getTargetRotation().toDouble());
        bitmap =  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if(bitmap == null){
            Log.d("Error", "Bitmap is null");
            return false;
        }

        if(capturewithEmotionsPermission) {
            if (cameraType == 1) cameraType = 2;
            else if (cameraType == 2) cameraType = 1;


            Canvas canvas = new Canvas(bitmap);
            canvas = getCanvas(canvas, faces, frame, v, paint, cameraType);
        }

        File folder = new File(Environment.getExternalStorageDirectory() + "/MyCameraPhotos");

        if (!folder.exists()) {
            folder.mkdir();
        }

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        String filename = "image_" + formattedDate + ".png";

        File file = new File(folder.getPath() + "/" + filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", e.getMessage());
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                    Log.d("Message", "OK");
                }
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }


    public static boolean saveImage(Bitmap bitmap) {

        if(bitmap == null){
            Log.d("Error", "Bitmap is null");
            return false;
        }

        File folder = new File(Environment.getExternalStorageDirectory() + "/MyCameraPhotos");

        if (!folder.exists()) {
            folder.mkdir();
        }

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        String filename = "image_" + formattedDate + ".png";

        File file = new File(folder.getPath() + "/" + filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", e.getMessage());
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                    Log.d("Message", "OK");
                }
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }


    public static Bitmap drawCanvas(int width, int height, List<Face> faces, Frame frame, Paint paint) {
        if (width <= 0 || height <= 0) {
            return null;
        }

        Bitmap blackBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        blackBitmap.eraseColor(Color.BLACK);
        Canvas c = new Canvas(blackBitmap);

        Frame.ROTATE frameRot = frame.getTargetRotation();
        Bitmap bitmap;

        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();
        int canvasWidth = c.getWidth();
        int canvasHeight = c.getHeight();
        int scaledWidth;
        int scaledHeight;
        int topOffset = 0;
        int leftOffset= 0;
        float radius = 2;

        if (frame instanceof Frame.BitmapFrame) {
            bitmap = ((Frame.BitmapFrame)frame).getBitmap();
        } else { //frame is ByteArrayFrame
            byte[] pixels = ((Frame.ByteArrayFrame)frame).getByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(pixels);
            bitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
        }

        if (frameRot == Frame.ROTATE.BY_90_CCW || frameRot == Frame.ROTATE.BY_90_CW) {
            int temp = frameWidth;
            frameWidth = frameHeight;
            frameHeight = temp;
        }

        float frameAspectRatio = (float)frameWidth/(float)frameHeight;
        float canvasAspectRatio = (float) canvasWidth/(float) canvasHeight;
        if (frameAspectRatio > canvasAspectRatio) { //width should be the same
            scaledWidth = canvasWidth;
            scaledHeight = (int)((float)canvasWidth / frameAspectRatio);
            topOffset = (canvasHeight - scaledHeight)/2;
        } else { //height should be the same
            scaledHeight = canvasHeight;
            scaledWidth = (int) ((float)canvasHeight*frameAspectRatio);
            leftOffset = (canvasWidth - scaledWidth)/2;
        }

        float scaling = (float)scaledWidth/(float)frame.getOriginalBitmapFrame().getWidth();

        Matrix matrix = new Matrix();
        matrix.postRotate((float)frameRot.toDouble());
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,frameWidth,frameHeight,matrix,false);
        c.drawBitmap(rotatedBitmap,null,new Rect(leftOffset,topOffset,leftOffset+scaledWidth,topOffset+scaledHeight),null);

        for(int i=0;i<faces.size();i++) {
            Face face = faces.get(i);
            Rect boundingbox = new Rect(c.getWidth(), c.getHeight(), 0, 0);

            for (PointF point : face.getFacePoints()) {
                float x = (point.x * scaling) + leftOffset;
                float y = (point.y * scaling) + topOffset;

                boundingbox.union(Math.round(x), Math.round(y));
                boundingbox.union(Math.round(x), Math.round(y));

                if(landmarksDetectionPermission_for_photo) c.drawCircle(x, y, 1, paint);
            }

            if(emotionDetectionPermission_for_photo) {
                String emo = getEmotion(face, 20);
                if (emo.equals("Joy")) paint.setColor(Color.GREEN);
                if (emo.equals("Angry")) paint.setColor(Color.RED);
                if (emo.equals("Sad")) paint.setColor(Color.rgb(173, 216, 230));
                if (emo.equals("Fear")) paint.setColor(Color.rgb(255, 165, 0));
                if (emo.equals("Surprise")) paint.setColor(Color.YELLOW);
                c.drawText( emo , boundingbox.left, boundingbox.bottom + 20, paint);

                if(!face.emojis.getDominantEmoji().name().equals("UNKNOWN") && emojipermission_for_photo) {
                    Bitmap b = Photomode.bitmapByName.get(face.emojis.getDominantEmoji().name());
                    if(b != null) {
                        b = Bitmap.createScaledBitmap(b, boundingbox.width()/2, boundingbox.height()/2, false);
                        c.drawBitmap(b, boundingbox.left, boundingbox.top - boundingbox.height()/2, paint);
                    }
                }
            }

            if(faceDetectionPermission_for_photo) c.drawRect(boundingbox.left, boundingbox.top, boundingbox.right, boundingbox.bottom, paint);
            paint.setColor(Color.WHITE);
        }

        return blackBitmap;
    }
}
