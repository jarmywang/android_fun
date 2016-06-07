package com.example.user.myapplication;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.myapplication.cu.BaseActivity;
import com.example.user.myapplication.cu.ContentView;
import com.example.user.myapplication.cu.DynamicWave;

/**
 * Created by User on 2015/9/22.
 */
@ContentView(value = R.layout.activity_surfacefun)
public class SurfaceFunActivity extends BaseActivity implements SurfaceHolder.Callback{

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    Canvas canvas;

    TextView tv_like_plus1;

    TextView tvCc;
    Button btnCc;

    DynamicWave dynamicWave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(getViewRes());
        loadContentView();
        surfaceView = (SurfaceView) findViewById(R.id.sv);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        canvas = surfaceHolder.lockCanvas();
        otherTestAnimPlus1();
        otherTestAnimColorChange();
        otherTestDynamicWave();
    }

    private void otherTestDynamicWave(){
        dynamicWave = (DynamicWave) findViewById(R.id.dw);
    }

    private void otherTestAnimPlus1(){
        tv_like_plus1 = (TextView) findViewById(R.id.tv_like_plus1);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.plus_1);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_like_plus1.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_like_plus1.startAnimation(animation);
            }
        });
    }


    private void otherTestAnimColorChange(){
        tvCc = (TextView) findViewById(R.id.tv_cc);
        btnCc = (Button) findViewById(R.id.btn_cc);
        btnCc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animatorCc = ObjectAnimator.ofInt(tvCc, "backgroundColor", Color.RED, Color.BLUE, Color.GRAY, Color.GREEN);
                animatorCc.setDuration(1500);
                animatorCc.setRepeatCount(-1);
                animatorCc.setInterpolator(new DecelerateInterpolator());
                animatorCc.setRepeatMode(Animation.REVERSE);
                animatorCc.setEvaluator(new ArgbEvaluator());
                animatorCc.start();
            }
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(surfaceHolder);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
