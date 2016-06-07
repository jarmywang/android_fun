package com.example.user.myapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity0 extends Activity {

    Button btn_add, btn_del;

    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayoutManager linearLayoutManager;
//    private RecyclerView.ItemAnimator itemAnimator;

    private GalleryMyAdapter mAdapter;
    private MyItemAnimator itemAnimator;

    private List<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main0);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_del = (Button) findViewById(R.id.btn_del);
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        mDatas = new ArrayList<>();
        for (int i = 0; i < 12; i++)
        {
            mDatas.add("item." + i);
        }
//        mLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        itemAnimator = new MyItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);

        mAdapter = new GalleryMyAdapter();
        mAdapter.setmOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity0.this, mDatas.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.addItem();
            }
        });

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.removeData();
            }
        });

    }


    class GalleryViewHolder extends RecyclerView.ViewHolder{

        ImageView mImg;
        TextView mTxt;

        public GalleryViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    class GalleryMyAdapter extends RecyclerView.Adapter<GalleryViewHolder>{

        private OnItemClickLitener mOnItemClickLitener;

        @Override
        public GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(MainActivity0.this).inflate(R.layout.item_gallery, viewGroup, false);
            GalleryViewHolder galleryViewHolder = new GalleryViewHolder(v);
            galleryViewHolder.mTxt = (TextView) v.findViewById(R.id.id_index_gallery_item_text);
            galleryViewHolder.mImg = (ImageView) v.findViewById(R.id.id_index_gallery_item_image);
            return galleryViewHolder;
        }

        @Override
        public void onBindViewHolder(GalleryViewHolder galleryViewHolder, final int i) {
            galleryViewHolder.mTxt.setText(mDatas.get(i));
            if(mOnItemClickLitener!=null) {
                galleryViewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickLitener.onItemClick(v, i);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        //  删除指定的Item
        public void removeData(int position)
        {
            if(mDatas.size()<=0) return;
            mDatas.remove(position);
            //  通知RecyclerView控件某个Item已经被删除
            notifyItemRemoved(position);

        }
        public void removeData(){
            removeData(0);
        }
        //  在指定位置添加一个新的Item
        public void addItem(String model, int positionToAdd)
        {
            mDatas.add(model);
            //  通知RecyclerView控件插入了某个Item
            notifyItemInserted(positionToAdd);
        }
        public void addItem(){
            addItem("new.item", 0);
        }

        public void setmOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }
    }

    private void initMyItemAnimator(){

    }

    public class MyItemAnimator extends RecyclerView.ItemAnimator {

        List<RecyclerView.ViewHolder> mAnimationAddViewHolders = new ArrayList<RecyclerView.ViewHolder>();
        List<RecyclerView.ViewHolder> mAnimationRemoveViewHolders = new ArrayList<RecyclerView.ViewHolder>();
        //需要执行动画时会系统会调用，用户无需手动调用
        @Override
        public void runPendingAnimations() {
            if (!mAnimationAddViewHolders.isEmpty()) {

                AnimatorSet animator;
                View target;
                for (final RecyclerView.ViewHolder viewHolder : mAnimationAddViewHolders) {
                    target = viewHolder.itemView;
                    animator = new AnimatorSet();

                    animator.playTogether(
                            ObjectAnimator.ofFloat(target, "translationX", -target.getMeasuredWidth(), 0.0f),
                            ObjectAnimator.ofFloat(target, "alpha", target.getAlpha(), 1.0f)
                    );

                    animator.setTarget(target);
                    animator.setDuration(100);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAnimationAddViewHolders.remove(viewHolder);
                            if (!isRunning()) {
                                dispatchAnimationsFinished();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator.start();
                }
            }
            else if(!mAnimationRemoveViewHolders.isEmpty()){
            }
        }
        //remove时系统会调用，返回值表示是否需要执行动画
        @Override
        public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
            return mAnimationRemoveViewHolders.add(viewHolder);
        }

        //viewholder添加时系统会调用
        @Override
        public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
            return mAnimationAddViewHolders.add(viewHolder);
        }

        @Override
        public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
            return false;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1, int i, int i1, int i2, int i3) {
            return false;
        }

        @Override
        public void endAnimation(RecyclerView.ViewHolder viewHolder) {
        }

        @Override
        public void endAnimations() {
        }

        @Override
        public boolean isRunning() {
            return !(mAnimationAddViewHolders.isEmpty()&&mAnimationRemoveViewHolders.isEmpty());
        }

    }
}
