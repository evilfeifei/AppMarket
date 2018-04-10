package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapterNew;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.event.ScrolledEvent;
import com.huiyun.amnews.ui.AppDettailsActivity2;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.wight.SmartScrollView;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
@SuppressLint("ValidFragment")
public class IntroduceFragment extends BaseFragment implements SmartScrollView.ISmartScrollChangedListener{

    View rootView;
    SmartScrollView introduce_scrollview;

    HeaderAndFooterRecyclerView recyclerView;
    private View headerMomentView;
    protected LayoutInflater mInflater;
    private AppAdapterNew appAdapterNew;
    private List<AppInfo> appInfoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_introduce_new, container, false);
        this.mInflater = inflater;
        initView(rootView);
        return rootView;
    }

    private void initView(View view){
        recyclerView = (HeaderAndFooterRecyclerView) view.findViewById(R.id.recycler_view);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        headerMomentView = mInflater.inflate(R.layout.fragment_introduce, null);
        introduce_scrollview = (SmartScrollView) headerMomentView.findViewById(R.id.introduce_scrollview);
        recyclerView.addHeaderView(headerMomentView);


        ((TextView)headerMomentView.findViewById(R.id.content_tv)).setText(AppDettailsActivity2.appDettailsActivity.appBean.getIntroduction());
        ((TextView)headerMomentView.findViewById(R.id.auth_tv)).setText("授权："+AppDettailsActivity2.appDettailsActivity.appBean.getAuth());
        ((TextView)headerMomentView.findViewById(R.id.system_req_tv)).setText("系统要求："+AppDettailsActivity2.appDettailsActivity.appBean.getSystem_require());
//        introduce_scrollview = (SmartScrollView)view.findViewById(R.id.introduce_scrollview);
//        introduce_scrollview.setScanScrollChangedListener(this);

        appAdapterNew = new AppAdapterNew(getActivity(),appInfoList);
        recyclerView.setAdapter(appAdapterNew);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
        }
    }

    @Override
    public void onScrolledToBottom() {
        ScrolledEvent scrolledEvent = new ScrolledEvent();
        scrolledEvent.setScrolledToTop(false);
        EventBus.getDefault().post(scrolledEvent);
    }

    @Override
    public void onScrolledToTop() {
        ScrolledEvent scrolledEvent = new ScrolledEvent();
        scrolledEvent.setScrolledToTop(true);
        EventBus.getDefault().post(scrolledEvent);
    }
}
