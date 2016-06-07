package com.example.user.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2015/8/31.
 */
public class PageHActivity extends Activity {
    private RelativeLayout viewPagerContainer;
    private PagerContainer mContainer;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        mContainer = (PagerContainer) findViewById(R.id.pager_container);

        ViewPager pager = mContainer.getViewPager();
        PagerAdapter adapter = new MyPagerAdapter();
        pager.setAdapter(adapter);
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(adapter.getCount());
        //A little space between pages
        pager.setPageMargin(15);

        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        pager.setClipChildren(false);
//        pager.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 左右两边不显示就是这一句引起的
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView view = new TextView(PageHActivity.this);
            view.setText("Item "+position);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50));

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }

//    @Override
    protected void onCreate1(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
//        viewPagerContainer = (RelativeLayout)findViewById(R.id.pager_layout);
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        viewPager = mContainer.getViewPager();
//        viewPager = (ViewPager) findViewById(R.id.vp);
//        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
//        layoutParams.width = 360;
//        viewPager.setLayoutParams(layoutParams);

        List<View> views = new ArrayList<>();
        View v1 = LayoutInflater.from(this).inflate(R.layout.image_item, null);
        View v2 = LayoutInflater.from(this).inflate(R.layout.image_item, null);
        View v3 = LayoutInflater.from(this).inflate(R.layout.image_item, null);
        View v4 = LayoutInflater.from(this).inflate(R.layout.image_item, null);
        View v5 = LayoutInflater.from(this).inflate(R.layout.image_item, null);
        View v6 = LayoutInflater.from(this).inflate(R.layout.image_item, null);
        View v7 = LayoutInflater.from(this).inflate(R.layout.image_item, null);
        views.add(v1);
        views.add(v2);
        views.add(v3);
        views.add(v4);
        views.add(v5);
        views.add(v6);
        views.add(v7);
        final List<Drawable> list = new ArrayList<Drawable>();
        list.add(getResources().getDrawable(R.drawable.img1));
        list.add(getResources().getDrawable(R.drawable.img2));
        list.add(getResources().getDrawable(R.drawable.img3));
        list.add(getResources().getDrawable(R.drawable.img4));
        list.add(getResources().getDrawable(R.drawable.img5));
        list.add(getResources().getDrawable(R.drawable.img1));
        list.add(getResources().getDrawable(R.drawable.img2));
        MPagerAdapter mPagerAdapter = new MPagerAdapter(this, views, list);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(views.size() / 2);
        viewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        viewPager.setPageMargin(10);
        viewPager.setClipChildren(false);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (viewPagerContainer != null) {
//                    viewPagerContainer.invalidate();
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer
    {
        private static final float MIN_SCALE = 0.75f;
        private static final float MIN_ALPHA = 0.5f;

        @SuppressLint("NewApi")
        public void transformPage(View view, float position)
        {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            Log.e("TAG", view + " , " + position + "");

            if (position < -1)
            { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
            { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0)
                {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else
                {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                        / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else
            { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
