package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.ui.AppDettailsActivity2;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
@SuppressLint("ValidFragment")
public class IntroduceFragment extends BaseFragment {

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_introduce, container, false);

        initView(rootView);
        return rootView;
    }

    private void initView(View view){
        ((TextView)view.findViewById(R.id.content_tv)).setText(AppDettailsActivity2.appDettailsActivity.appBean.getIntroduction());
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
}
