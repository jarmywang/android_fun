package com.example.user.myapplication;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by Wang on 2015/10/26.
 */
public class ToolbarDlActivity extends AppCompatActivity {

    //声明相关变量
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private String[] lvs = {"List Item 01", "List Item 02", "List Item 03", "List Item 04"};
    private ArrayAdapter arrayAdapter;
    private ImageView ivRunningMan;
    private AnimationDrawable mAnimationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toobar_drawerlayout);
        findViews(); //获取控件
        materialFun();
        //京东RunningMan动画效果，和本次Toolbar无关
        mAnimationDrawable = (AnimationDrawable) ivRunningMan.getBackground();
        mAnimationDrawable.start();
        toolbar.setTitle("Toolbar");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mAnimationDrawable.stop();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mAnimationDrawable.start();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //设置菜单列表
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);
    }
    private void findViews() {
        ivRunningMan = (ImageView) findViewById(R.id.iv_main);
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);
    }
    private void materialFun(){
        final FloatingActionMButton floatingActionButton = (FloatingActionMButton)findViewById(R.id.fab);
        floatingActionButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                floatingActionButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                System.out.println("floatingActionButton.W="+floatingActionButton.getWidth() + "，floatingActionButton.H="+floatingActionButton.getHeight());
            }
        });
    }
}
