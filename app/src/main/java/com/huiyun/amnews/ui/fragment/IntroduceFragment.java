package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.event.ScrolledEvent;
import com.huiyun.amnews.ui.AppDettailsActivity2;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.wight.SmartScrollView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
@SuppressLint("ValidFragment")
public class IntroduceFragment extends BaseFragment implements SmartScrollView.ISmartScrollChangedListener{

    View rootView;
    SmartScrollView introduce_scrollview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_introduce, container, false);

        initView(rootView);
        return rootView;
    }

    private void initView(View view){
        ((TextView)view.findViewById(R.id.content_tv)).setText(AppDettailsActivity2.appDettailsActivity.appBean.getIntroduction());
        introduce_scrollview = (SmartScrollView)view.findViewById(R.id.introduce_scrollview);
        introduce_scrollview.setScanScrollChangedListener(this);
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
