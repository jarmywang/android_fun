package com.example.user.myapplication.com.itlanbao;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MainGalleryActivity extends Activity {

    private int current;
    public Gallery gallery = null;
    public GalleryAdapter adapter = null;
    public int selectNum = 0;//全局变量，保存被选中的item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gallery = (Gallery) findViewById(R.id.gallery);
        // 图片
        final List<Drawable> list = new ArrayList<Drawable>();
        list.add(getResources().getDrawable(R.drawable.img1));
        list.add(getResources().getDrawable(R.drawable.img2));
        list.add(getResources().getDrawable(R.drawable.img3));
        list.add(getResources().getDrawable(R.drawable.img4));
        list.add(getResources().getDrawable(R.drawable.img5));
        list.add(getResources().getDrawable(R.drawable.img1));
        list.add(getResources().getDrawable(R.drawable.img2));
        list.add(getResources().getDrawable(R.drawable.img3));
        list.add(getResources().getDrawable(R.drawable.img4));
        list.add(getResources().getDrawable(R.drawable.img5));
        list.add(getResources().getDrawable(R.drawable.img1));
        adapter = new GalleryAdapter(this, list);

        gallery.setAdapter(adapter);
        current = (int) (list.size() / 2);
        gallery.setSelection(current);// 设置默认显示的图片
        gallery.setOnItemSelectedListener(new Gallery.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                System.out.println(arg2);
                selectNum = arg2;//
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                System.out.println("not");
            }
        });

        gallery.setOnItemClickListener(new Gallery.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(selectNum == arg2)
                    Toast.makeText(MainGalleryActivity.this, "点击了图片-" + arg2, Toast.LENGTH_SHORT).show(); // 点击处理
            }
        });

//	        myHandler.sendEmptyMessageDelayed(1, 3000);

    }

//	    Handler myHandler = new Handler(){
//
//			@Override
//			public void handleMessage(Message msg) {
//				// TODO Auto-generated method stub
//				super.handleMessage(msg);
//
//				switch (msg.what) {
//				case 1:
//					if(current+1 < adapter.getCount()){
//						current++;
//						gallery.setSelection(current);// 设置默认显示的图片
//					}else{
//						current = 0;
//						gallery.setSelection(current);// 设置默认显示的图片
//					}
//					 myHandler.sendEmptyMessageDelayed(1, 3000);
//					break;
//
//				default:
//					break;
//				}
//			}
//
//	    };

    public class GalleryAdapter extends BaseAdapter {
        public List<Drawable> list = null;
        public Context ctx = null;

        public GalleryAdapter(Context ctx, List<Drawable> list) {
            this.list = list;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(ctx, R.layout.image_item, null);
                holder.imageRel = (RelativeLayout) convertView.findViewById(R.id.image_rel);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.tv = (TextView) convertView.findViewById(R.id.tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv.setText("图片"+position);

            holder.image.setImageDrawable(list.get(position));
            if (selectNum == position) {
                holder.image.setLayoutParams(new RelativeLayout.LayoutParams(360, 360));//如果被选择则放大显示
                holder.image.setAlpha(254);
            } else {
                holder.image.setLayoutParams(new RelativeLayout.LayoutParams(210, 210));//否则正常
                holder.image.setAlpha(100);
            }

            return convertView;
        }

    }

    class ViewHolder {
        ImageView image;
        TextView tv;
        RelativeLayout imageRel;
    }

}
