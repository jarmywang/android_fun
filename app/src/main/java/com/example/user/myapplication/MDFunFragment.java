package com.example.user.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.myapplication.cu.MRecyclerViewAdapter;

/**
 * Created by Wang on 2015/12/7.
 */
public class MDFunFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MRecyclerViewAdapter.OnItemClickListener{

    private View mView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MRecyclerViewAdapter mRecyclerViewAdapter;

    private static final int VERTICAL_LIST = 0;
    private static final int HORIZONTAL_LIST = 1;
    private static final int VERTICAL_GRID = 2;
    private static final int HORIZONTAL_GRID = 3;
    private static final int STAGGERED_GRID = 4;

    private static final int SPAN_COUNT = 3;
    private int flag = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_md, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swiperefreshlayout);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        flag = (int)getArguments().get("flags");
        configRecyclerView();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.tab_text_color_focused, R.color.ripple_red, R.color.bar_press_1, R.color.ripple_material_teal);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void configRecyclerView() {
        switch (flag) {
            case VERTICAL_LIST:
                mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                break;
            case HORIZONTAL_LIST:
                mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
                break;
            case VERTICAL_GRID:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT, GridLayoutManager.VERTICAL, false);
                break;
            case HORIZONTAL_GRID:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT, GridLayoutManager.HORIZONTAL, true);
                break;
            case STAGGERED_GRID:
                mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                break;
        }

        mRecyclerViewAdapter = new MRecyclerViewAdapter(getActivity());
        mRecyclerViewAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                if (flag == STAGGERED_GRID) {
                    configRecyclerView();
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }, 2000);
    }

    @Override
    public void onItemClick(View view, int position) {
        getActivity().startActivity(new Intent(getActivity(), MDFunDetailActivity.class));
    }
}
