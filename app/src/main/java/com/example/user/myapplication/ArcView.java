package com.example.user.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

/**
 * Created by User on 2015/9/7.
 */
public class ArcView extends RelativeLayout {
    private String TAG = "ArcView";

    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

    private float startR;
    private float endR;

    int backgroundColor = Color.parseColor("#aaaaaa");

    Bitmap bitmap;
    Canvas temp;
    Paint paint;

    public ArcView(Context context) {
        super(context);
    }

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    public ArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(attrs);
    }

    public int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    protected void setAttributes(AttributeSet attrs) {
        if(attrs!=null){
            TypedArray typedArray = this.getContext().obtainStyledAttributes(attrs, R.styleable.ArcView);
            if(typedArray!=null) {
                startR = typedArray.getFloat(R.styleable.ArcView_startR, 0);
                endR = typedArray.getFloat(R.styleable.ArcView_endR, 0);
//                Log.i(TAG, "setAttributes startR=" + startR + ", endR="+endR);
                typedArray.recycle();
            }
        }
        int tempBackgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
//        Log.i(TAG, "setAttributes tempBackgroundColor=" + tempBackgroundColor);
        if (tempBackgroundColor != -1) {
            backgroundColor = getResources().getColor(tempBackgroundColor);
        } else {
            String background = attrs.getAttributeValue(ANDROIDXML, "background");
//            Log.i(TAG, "setAttributes background=" + background);
            if (background != null) {
                backgroundColor = Color.parseColor(background);
            }
        }
        setBackgroundColor(Color.parseColor("#00ffffff"));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bitmap == null) // 避免在onDraw方法中创建对象
            bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        if(paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(backgroundColor);
        }
        if(temp == null) {
            temp = new Canvas(bitmap);
            temp.drawArc(new RectF(0, 0, getWidth(), getHeight()), startR, endR, true, paint);
        }
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        invalidate();
    }
}
