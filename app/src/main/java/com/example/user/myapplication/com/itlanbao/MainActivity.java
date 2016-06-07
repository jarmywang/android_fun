package com.example.user.myapplication.com.itlanbao;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.example.user.myapplication.R;

public class MainActivity extends Activity {
	HorizontalListView hListView;
	HorizontalListViewAdapter hListViewAdapter;
	ImageView previewImg;
	View olderSelectView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_g);
		initUI();
	}

	public void initUI(){
		hListView = (HorizontalListView)findViewById(R.id.horizon_listview);
		previewImg = (ImageView)findViewById(R.id.image_preview);
		String[] titles = {"1", "2", "3", "4", "5", "6"};
		final int[] ids = {R.drawable.img1, R.drawable.img2,
				R.drawable.img3, R.drawable.img4,
				R.drawable.img5, R.drawable.img3};
		hListViewAdapter = new HorizontalListViewAdapter(getApplicationContext(),titles,ids);
		hListView.setAdapter(hListViewAdapter);
		 
		 
		hListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				if(olderSelectView == null){
//					olderSelectView = view;
//				}else{
//					olderSelectView.setSelected(false);
//					olderSelectView = null;
//				}
//				olderSelectView = view;
//				view.setSelected(true);
				previewImg.setImageResource(ids[position]);
				hListViewAdapter.setSelectIndex(position);
				hListViewAdapter.notifyDataSetChanged();
				
			}
		});

	}

} 