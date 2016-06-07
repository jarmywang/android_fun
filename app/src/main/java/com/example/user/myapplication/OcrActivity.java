package com.example.user.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 图片文字识别
 * Created by Wang on 2016/4/27.
 */
public class OcrActivity extends AppCompatActivity {

    private final int REQUEST_CODE_GETIMAGE_PHOTO = 0; // 请求相册
    private final int REQUEST_CODE_GETIMAGE_CAMERA = 1; // 请求相机

    final String TAG = "OcrActivity";
    final String lang = "eng";
    final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Download/ocr/";

    View tvCamera, tvPhoto;
    TextView tvInfo;
    ImageView imageView;
    boolean _taken;
    final String PHOTO_TAKEN = "photo_taken";
    String picFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv);
        OpencvBlurCameraActivity.verifyStoragePermissions(this);
        tvCamera = findViewById(R.id.tv_do_camera);
        tvPhoto = findViewById(R.id.tv_do_photo);
        imageView = (ImageView) findViewById(R.id.iv_pic);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        initFile();
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });
        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionAlbum();
            }
        });
    }

    private void ocrProcess() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(picFilePath, options);

        try {
            ExifInterface exif = new ExifInterface(picFilePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: \n" + recognizedText);

//        if ( lang.equalsIgnoreCase("eng") ) {
//            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
//        }
//
//        recognizedText = recognizedText.trim();

        if ( recognizedText.length() != 0 ) {
            tvInfo.setText(recognizedText);
        }

        // Cycle done.
    }

    private void initFile(){
        String[] paths = new String[] {DATA_PATH, DATA_PATH + "tessdata/" };
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + lang + ".traineddata");
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

        String demoPic = DATA_PATH + "simple_ocr_img.jpg";
        if (!(new File(demoPic)).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/simple_ocr_img.png");
                OutputStream out = new FileOutputStream(demoPic);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void startActionAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GETIMAGE_PHOTO);
    }

    protected void startCameraActivity() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
        picFilePath = DATA_PATH + "img_" + timeStamp + "_.jpg";
        File file = new File(picFilePath);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, REQUEST_CODE_GETIMAGE_CAMERA);
    }

    protected void onPhotoTaken() {
        _taken = true;
        Bitmap bitmap = BitmapFactory.decodeFile(picFilePath);
        imageView.setImageBitmap(bitmap);
        ocrProcess();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GETIMAGE_CAMERA:
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File file = new File(picFilePath);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                    onPhotoTaken();
                    break;
                case REQUEST_CODE_GETIMAGE_PHOTO:
                    Uri selectedImage = data.getData();
                    if (!TextUtils.isEmpty(selectedImage.toString())) {
                        if (selectedImage.toString().startsWith("file://")) {
                            try {
                                picFilePath = URLDecoder.decode(selectedImage.toString().replace("file://", ""), "UTF-8");
                                onPhotoTaken();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedImage.toString().startsWith("content://")) {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                picFilePath = cursor.getString(columnIndex);  //获取照片路径
                                cursor.close();
                                onPhotoTaken();
                            }
                        }
                    }
                    break;
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String imgPath, BitmapFactory.Options options, int reqWidth, int reqHeight) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
        System.out.println("opencvanswers..inSampleSize=" + inSampleSize);
        return inSampleSize;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        System.out.println("opencvanswers..onRestoreInstanceState");
        if (savedInstanceState.getBoolean(PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

}
