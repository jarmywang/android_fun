package com.example.user.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.user.myapplication.vr.VRFunActivity;

import java.util.ArrayList;
import java.util.List;

public class LaucherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laucher);
        ListView listIntent = $(R.id.list_intent);

        final List<Class<?>> listClass = new ArrayList<>();
        listClass.add(SignActivity.class);
        listClass.add(OcrActivity.class);
        listClass.add(OpencvBlurCameraActivity.class);
        listClass.add(OpencvBlurActivity.class);
        listClass.add(MDFunActivity.class);
        listClass.add(MaterialDesignFunActivity.class);
        listClass.add(ToolbarDlActivity.class);
        listClass.add(EmptyListViewHeaderTestActivity.class);
        listClass.add(SurfaceFunActivity.class);
        listClass.add(ViewTestFunActivity.class);
        listClass.add(VRFunActivity.class);
        listClass.add(HugeImgActivity.class);
        listClass.add(QqLoginActivity.class);
        listClass.add(ScrollFunActivity.class);
        listClass.add(com.example.user.myapplication.com.itlanbao.MainActivity.class);
        listClass.add(com.example.user.myapplication.com.itlanbao.MainGalleryActivity.class);
        listClass.add(MainActivity.class);
        listClass.add(MainActivity0.class);
        listClass.add(PageHActivity.class);
        listClass.add(SettingsActivity.class);
//=========== 添加一项记得到manifest也添加============

        List<String> listSimpleName = new ArrayList<>();
        for (Class<?> clazz : listClass) {
            if (clazz.getName().contains(".itlanbao.")) {
                listSimpleName.add("itlanbao." + clazz.getSimpleName());
            }
            else if (clazz.getName().contains("SignActivity")) {
                listSimpleName.add("手势签名");
            }
            else if (clazz.getName().contains("OcrActivity")) {
                listSimpleName.add("图片文字识别");
            }
            else if (clazz.getName().contains("OpencvBlurCameraActivity")) {
                listSimpleName.add("图片清晰度识别");
            }
            else {
                listSimpleName.add(clazz.getSimpleName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_list_text, R.id.tv, listSimpleName);
        listIntent.setAdapter(adapter);
        listIntent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchFor(listClass.get(position));
            }
        });
    }

    private <T extends View> T $(@IdRes int id) { // activity用
        T view = (T) findViewById(id);
        return view;
    }

    private void launchFor(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }
}
