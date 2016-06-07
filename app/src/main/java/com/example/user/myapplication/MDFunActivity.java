package com.example.user.myapplication;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.user.myapplication.cu.MyViewPagerAdapter;
import com.example.user.myapplication.cu.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Wang on 2015/11/30.
 */
public class MDFunActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    @InjectView(R.id.main_coordinator)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tablayout)
    TabLayout tablayout;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.navigation_view)
    NavigationView navigationView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private String[] mTitles; // TabLayout中的tab标题
    private List<Fragment> mFragments;
    private MyViewPagerAdapter mViewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_md);
        ButterKnife.inject(this);
        initData();
        configViews();
    }

    private void initData(){
        mTitles = getResources().getStringArray(R.array.tab_titles);
        mFragments = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("flags", i);
            Fragment fragment = new MDFunFragment();
            fragment.setArguments(bundle);
            mFragments.add(i, fragment);
        }
    }

    private void configViews(){
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        setNavigationItemSelectedListener();

        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        viewpager.setAdapter(mViewPagerAdapter);
        viewpager.setOffscreenPageLimit(mFragments.size());
        viewpager.addOnPageChangeListener(this);

        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tablayout.setupWithViewPager(viewpager);
        tablayout.setTabsFromPagerAdapter(mViewPagerAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showSnackbar(v, "fab click!");
            }
        });
    }

    private void setNavigationItemSelectedListener(){ // 左侧菜单点击后的操作
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Utils.showSnackbar(getWindow().getDecorView(), "setting click!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        toolbar.setTitle(mTitles[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
