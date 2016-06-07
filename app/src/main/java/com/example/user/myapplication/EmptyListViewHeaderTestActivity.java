package com.example.user.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Wang on 2016/4/27.
 */
public class EmptyListViewHeaderTestActivity extends AppCompatActivity {

    ListView listView;
    TextView textView;
    View header;
    ArrayAdapter<String> adapter;
    List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_list_header);

        listView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.tv);
        header = LayoutInflater.from(this).inflate(R.layout.item_gallery, null);
        View view = header.findViewById(R.id.id_index_gallery_item_image);
        view.getLayoutParams().width = getDeviceWidth();

        listView.addHeaderView(header);
        data = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.item_list_text, R.id.tv, data);
        listView.setAdapter(adapter);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.add("" + System.currentTimeMillis());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private int getDeviceWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;     // 屏幕宽度（像素）
    }
}
