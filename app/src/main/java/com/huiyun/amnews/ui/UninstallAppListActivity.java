package com.huiyun.amnews.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapterNew;
import com.huiyun.amnews.adapter.UninstallAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.UninstallAppInfo;
import com.huiyun.amnews.been.UpdateApp;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.ApkUtils;
import com.huiyun.amnews.util.JsonUtil;
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
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 应用卸载
 * Created by Justy on 2018/3/14.
 */

public class UninstallAppListActivity extends BaseActivity {

    @Bind(R.id.t_title)
    TextView titleTv;
    @Bind(R.id.back_left_liner)
    LinearLayout backLeftLiner;

    @Bind(R.id.recycler_view)
    HeaderAndFooterRecyclerView recyclerView;
    private UninstallAdapter uninstallAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall_list);
        ButterKnife.bind(this);
        initMyView();
        initData();
    }



    private void initMyView(){
        titleTv.setText("应用卸载");
        backLeftLiner.setVisibility(View.VISIBLE);
        final LinearLayoutManager manager = new LinearLayoutManager(UninstallAppListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        uninstallAdapter = new UninstallAdapter(UninstallAppListActivity.this, appList);
        recyclerView.setAdapter(uninstallAdapter);


        // 注册广播事件
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(broadcastRec, intentFilter);// 注册监听函数
    }

    public final BroadcastReceiver broadcastRec = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
                    || intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                initData();
            }
        }
    };

    ArrayList<UninstallAppInfo> appList = new ArrayList<UninstallAppInfo>(); //用来存储获取的应用信息数据
    private void initData(){
        appList.clear();
        List<PackageInfo> packageInfos = ApkUtils.getInstalledApp(UninstallAppListActivity.this);

        for(int i=0;i<packageInfos.size();i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            UninstallAppInfo tmpInfo =new UninstallAppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
            //Only display the non-system app info
            if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
            {
                appList.add(tmpInfo);//如果非系统应用，则添加至appList
            }
        }
        uninstallAdapter.refreshData(appList);
    }

    @OnClick({R.id.back_left_liner})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back_left_liner:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 如果有课程下载完成 刷新列表
    public void onDownLoadFinishEvent(DownLoadFinishEvent downLoadFinishEvent) {
        uninstallAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

}
