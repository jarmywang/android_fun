package com.example.user.myapplication;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 各种view测试，SurfaceFunActivity也有一些
 * Created by Wang on 2016/4/27.
 */
public class ViewTestFunActivity extends AppCompatActivity {

    final static String TAG = ViewTestFunActivity.class.getSimpleName();

    TextView tvBar, square;
    ImageView iv1, iv2, iv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fun);
        findView();
        animalTest();
        ImgTest();
    }

    private void findView() {
        tvBar = (TextView) findViewById(R.id.tv_bar);
        square = (TextView) findViewById(R.id.square);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);

    }

    private void ImgTest() {
        try {
            Bitmap bitmapO = BitmapFactory.decodeStream(getResources().getAssets().open("t.png"));
            Bitmap bitmap8 = ThumbnailUtils.extractThumbnail(bitmapO, 8, 8); // 缩小
            Bitmap bitmapG = SimilarPicture.convertGreyImg(bitmap8); // 灰度图像
            Bitmap bitmapWAB = SimilarPicture.convertWABImg(bitmapG, SimilarPicture.getAvg(bitmapG));

            String Str64 = SimilarPicture.getBinary(bitmapG, SimilarPicture.getAvg(bitmapG));
            System.out.println("Str64=" + Str64);
            System.out.println("Str64->16=" + SimilarPicture.binaryString2hexString(Str64));
            System.out.println("Str64->long=" + Long.parseLong(SimilarPicture.binaryString2hexString(Str64), 16));

            iv1.setImageBitmap(bitmapO);
            iv2.setImageBitmap(bitmapG);
            iv3.setImageBitmap(bitmapWAB);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void animalTest() {
//        tvBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                animalParabola();
//            }
//        });
        square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvBar.scrollTo(650, 500);
                    }
                }, 500);
//                animalScroll();
            }
        });
    }

    private void animalParabola() { // 抛物线
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setObjectValues(new PointF());
        valueAnimator.setEvaluator(new TypeEvaluator<PointF>() {
            @Override
            public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
                PointF pointF = new PointF();
                pointF.x = fraction * 2 * 150;
                pointF.y = 75 * fraction * 2 * fraction * 2;
                return pointF;
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                square.setX(pointF.x);
                square.setY(pointF.y);
            }
        });
        valueAnimator.start();
    }

    private void animalScroll() { // 利用属性动画实现弹性滑动
        final int startY = 0;
        final int deltaY = 500;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.5f).setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float fraction = animation.getAnimatedFraction(); // fraction=当前时间/duration   0->1f
                Log.v(TAG, "fraction=" + fraction + ", y=" + (startY + fraction * deltaY));
                tvBar.scrollTo(0, (int) (startY + fraction * deltaY));
            }
        });
        valueAnimator.start();

    }


}
