package com.example.user.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Wang on 2015/11/11.
 */
public class TiltTextView extends TextView{

    private float tiltR = -45; // 在左上角倾斜45度

    public TiltTextView(Context context) {
        super(context);
    }

    public TiltTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribute(attrs);
    }

    public TiltTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttribute(attrs);
    }

    public TiltTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setAttribute(attrs);
    }

    private void setAttribute(AttributeSet attrs){
        if(attrs==null) return;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TiltTextView);
        if(typedArray!=null) {
            tiltR = typedArray.getFloat(R.styleable.TiltTextView_tiltR, -45);
            typedArray.recycle();
        }
        init();
    }

    private void init(){}


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        canvas.rotate(tiltR, this.getWidth() / 2f, this.getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();
    }

    public void setDegrees(int tiltR) {
        this.tiltR = tiltR;
        invalidate();
    }
}
