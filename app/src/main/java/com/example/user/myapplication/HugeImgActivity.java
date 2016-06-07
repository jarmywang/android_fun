package com.example.user.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Wang on 2016/3/25.
 */
public class HugeImgActivity extends AppCompatActivity {

    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huge);
        iv = (ImageView) findViewById(R.id.iv);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        Bitmap pictures = BitmapFactory.decodeResource(getResources(), R.drawable.huge, o);

        int k = 2;
        int width = 3998;
        int height = 2827;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                pictures.compress(Bitmap.CompressFormat.PNG, 100, baos);
                InputStream istream = new ByteArrayInputStream(baos.toByteArray());
                BitmapRegionDecoder decoder = null;
                try {
                    decoder = BitmapRegionDecoder.newInstance(istream, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int nw = (j * width / k);
                int nh = (i * height / k);

                Bitmap bMap = decoder.decodeRegion(new Rect(nw, nh, (nw + width / k), (nh + height / k)), null);
                iv.setImageBitmap(bMap);

            }
        }
    }
}
