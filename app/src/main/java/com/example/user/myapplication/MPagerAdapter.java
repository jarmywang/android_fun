package com.example.user.myapplication;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MPagerAdapter extends PagerAdapter {
    private List<View> localPagers;
    private List<Drawable> drawables;

    public MPagerAdapter(Activity activity, List<View> views, List<Drawable> drawables) {
        this.localPagers = views;
        this.drawables = drawables;
    }

    @Override
    public int getCount() {
        return localPagers.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(localPagers.get(arg1), 0);
        ImageView imageView = (ImageView) localPagers.get(arg1).findViewById(R.id.image);
        TextView textView = (TextView) localPagers.get(arg1).findViewById(R.id.tv);
        imageView.setBackgroundDrawable(drawables.get(arg1));
        textView.setText("图片-" + arg1);
        return localPagers.get(arg1);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

//    @Override
//    public float getPageWidth(int position) {
//        return 0.75f;
//    }
}
