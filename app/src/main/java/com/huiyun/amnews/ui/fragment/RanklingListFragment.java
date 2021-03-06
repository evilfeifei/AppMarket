package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.NewsAdapter;
import com.huiyun.amnews.adapter.RankingAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.News;
import com.huiyun.amnews.been.NewsData;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.wight.LoadMoreFooter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Justy on 2018/3/7.
 */
@SuppressLint("ValidFragment")
public class RanklingListFragment extends BaseFragment {

    @Bind(R.id.recycler_view)
    HeaderAndFooterRecyclerView recyclerView;
    private List<AppInfo> appInfoList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public RanklingListFragment(List<AppInfo> appInfoList){
        this.appInfoList = appInfoList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ranking_app, container, false);
        ButterKnife.bind(this, rootView);
        initMyView();
        return rootView;
    }

    private void initMyView(){
        EventBus.getDefault().register(this);//订阅
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        rankingAdapter = new RankingAdapter(getActivity(), appInfoList);
        recyclerView.setAdapter(rankingAdapter);

    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 如果有课程下载完成 刷新列表
    public void onDownLoadFinishEvent(DownLoadFinishEvent downLoadFinishEvent) {
        rankingAdapter.myNotifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

}
