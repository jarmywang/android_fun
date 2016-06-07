package com.example.user.myapplication.com.itlanbao;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.example.user.myapplication.R;

/* 
*  MyImageAdapter 用来控制gallery的资源和操作. 
*/  
public class MyImageAdapter extends BaseAdapter {  
 
   private int mGalleryItemBackground;// 用来设置gallery的风格  
     
   public int getmGalleryItemBackground() {  
       return mGalleryItemBackground;  
   }  
 
   public void setmGalleryItemBackground(int mGalleryItemBackground) {  
       this.mGalleryItemBackground = mGalleryItemBackground;  
   }  
 
   private Context context;  
   private Integer[] imageids={R.drawable.img1,R.drawable.img2,
		   R.drawable.img3,R.drawable.img4,R.drawable.img5};//图片的资源ID，我们在gallery浏览的图片
     
   public MyImageAdapter(Context context){//构造函数    
       this.context=context;  
   }  
     
   public int getCount() {//返回所有图片的个数            
       return imageids.length;  
   }  
 
   public Object getItem(int arg0) { //返回图片在资源的位置            
       return arg0;  
   }  
 
   public long getItemId(int position) {//返回图片在资源的位置         
       return position;  
   }  
 
   public View getView(int position, View convertView, ViewGroup parent) {  //此方法是最主要的，他设置好的ImageView对象返回给Gallery    
       ImageView imageview = new ImageView(context);   
       imageview.setImageResource(imageids[position]);  //通过索引获得图片并设置给ImageView  </span>  
       imageview.setScaleType(ImageView.ScaleType.FIT_XY);  //设置ImageView的伸缩规格，用了自带的属性值</span>  
       imageview.setLayoutParams(new Gallery.LayoutParams(180, 180)); //设置布局参数  </span>  
       imageview.setBackgroundResource(mGalleryItemBackground); //设置风格，此风格的配置是在xml中    
       return imageview;  
   }  
}  