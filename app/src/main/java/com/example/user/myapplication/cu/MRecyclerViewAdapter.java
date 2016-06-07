package com.example.user.myapplication.cu;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang on 2015/12/7.
 */
public class MRecyclerViewAdapter extends RecyclerView.Adapter<MRecyclerViewAdapter.MRecyclerViewHolder> {

    private OnItemClickListener mOnItemClickListener;

    public Activity activity;
    public List<String> mDatas;
    public List<Integer> mHeights;
    public LayoutInflater mLayoutInflater;

    public MRecyclerViewAdapter(Activity activity) {
        this.activity = activity;
        mLayoutInflater = LayoutInflater.from(activity);
        mDatas = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            mDatas.add("item " + i);
        }

        mHeights = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            mHeights.add((int) (Math.random() * 500) + 200);
        }
    }

    @Override
    public MRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mLayoutInflater.inflate(R.layout.item_md_recyclerview, parent, false);
        MRecyclerViewHolder mRecyclerViewHolder = new MRecyclerViewHolder(mView);
        return mRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(MRecyclerViewHolder holder, final int position) {
        if(mOnItemClickListener!=null) {
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
        ViewGroup.LayoutParams mLayoutParams = holder.mTextView.getLayoutParams();
        mLayoutParams.height = mHeights.get(position);
        holder.mTextView.setLayoutParams(mLayoutParams);
        holder.mTextView.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class MRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public MRecyclerViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textview);
        }
    }

}
