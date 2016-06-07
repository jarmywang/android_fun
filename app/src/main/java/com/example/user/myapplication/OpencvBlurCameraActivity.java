package com.example.user.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 清晰度识别demo
 * Created by Wang on 2016/5/5.
 */
public class OpencvBlurCameraActivity extends AppCompatActivity {

    private final int REQUEST_CODE_GETIMAGE_PHOTO = 0; // 请求相册
    private final int REQUEST_CODE_GETIMAGE_CAMERA = 1; // 请求相机

    static {
        System.loadLibrary("opencv_java");
    }

    TextView tvCamera, tvPhoto;
    TextView tvInfo;
    ImageView imageView;
    final String PIC_PATH = Environment.getExternalStorageDirectory().toString() + "/Download/opencv/";
    boolean _taken;
    final String PHOTO_TAKEN = "photo_taken";
    String picFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv);
        verifyStoragePermissions(this);
        tvCamera = (TextView) findViewById(R.id.tv_do_camera);
        tvPhoto = (TextView) findViewById(R.id.tv_do_photo);
        imageView = (ImageView) findViewById(R.id.iv_pic);
        tvInfo = (TextView) findViewById(R.id.tv_info);

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

    private void opencvProcess() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
//            int permsRequestCode = 200;
//            requestPermissions(perms, permsRequestCode);
//        }
        System.out.println("opencvanswers..imgPath=" + picFilePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = decodeSampledBitmapFromFile(picFilePath, options, 2000, 2000);
        System.out.println("opencvanswers..image.w=" + image.getWidth() + ",image.h=" + image.getHeight());
        int l = CvType.CV_8UC1; //8-bit grey scale image
        Mat matImage = new Mat();
        Utils.bitmapToMat(image, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

        Bitmap destImage;
        destImage = Bitmap.createBitmap(image);
        Mat dst2 = new Mat();
        Utils.bitmapToMat(destImage, dst2);
        Mat laplacianImage = new Mat();
        dst2.convertTo(laplacianImage, l);
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
        Mat laplacianImage8bit = new Mat();
        laplacianImage.convertTo(laplacianImage8bit, l);

        Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(), laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(laplacianImage8bit, bmp);
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight()); // bmp为轮廓图

        int maxLap = -16777216; // 16m
        for (int pixel : pixels) {
            if (pixel > maxLap)
                maxLap = pixel;
        }

        int soglia = -6118750;
        System.out.println("opencvanswers..maxLap=" + maxLap);
        if (maxLap <= soglia) {
            System.out.println("opencvanswers.. is blur image");
        }
        System.out.println("==============================================\n");
        soglia += 6118750;
        maxLap += 6118750;
        tvInfo.setText("图片位置=" + picFilePath
//                + "\nimage.w=" + image.getWidth() + ", image.h=" + image.getHeight()
                + "\nmaxLap= " + maxLap + "(清晰范围:0~6118750)"
                + "\n" + Html.fromHtml("<font color='#eb5151'><b>" + (maxLap <= soglia ? "模糊" : "清晰") + "</b></font>")); // 这句font效果无效
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have read or write permission
            int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch(requestCode){
//            case 200:
//                boolean audioAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
//                boolean cameraAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
//                break;
//        }
//    }

    private void startActionAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GETIMAGE_PHOTO);
    }

    protected void startCameraActivity() {
        File dir = new File(PIC_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
        picFilePath = PIC_PATH + "img_" + timeStamp + "_.jpg";
        File file = new File(picFilePath);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, REQUEST_CODE_GETIMAGE_CAMERA);
    }

    protected void onPhotoTaken() {
        _taken = true;
        Bitmap bitmap = BitmapFactory.decodeFile(picFilePath);
        similarPicTest(bitmap);
        imageView.setImageBitmap(bitmap);
        opencvProcess();
    }

    private void similarPicTest(Bitmap bitmapO) {
        Bitmap bitmap8 = ThumbnailUtils.extractThumbnail(bitmapO, 8, 8); // 缩小
        Bitmap bitmapG = SimilarPicture.convertGreyImg(bitmap8); // 灰度图像

        String Str64 = SimilarPicture.getBinary(bitmapG, SimilarPicture.getAvg(bitmapG));
//        System.out.println("Str64=" + Str64);
        System.out.println("Str64->16=" + SimilarPicture.binaryString2hexString(Str64));
//        System.out.println("Str64->long=" + Long.parseLong(SimilarPicture.binaryString2hexString(Str64), 16));
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
        System.out.println("opencvanswers..o.w=" + width + ",o.h=" + height);
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
