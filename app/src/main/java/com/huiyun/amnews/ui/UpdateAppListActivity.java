package com.huiyun.amnews.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapterNew;
import com.huiyun.amnews.adapter.UpdateAppAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.UpdateApp;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.util.ApkUtils;
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
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 应用升级
 * Created by Justy on 2018/3/14.
 */

public class UpdateAppListActivity extends BaseActivity {

    @Bind(R.id.t_title)
    TextView titleTv;
    @Bind(R.id.back_left_liner)
    LinearLayout backLeftLiner;

    @Bind(R.id.recycler_view)
    HeaderAndFooterRecyclerView recyclerView;
    private List<AppInfo> appInfoList ;
    private UpdateAppAdapter appAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list);
        ButterKnife.bind(this);
        initMyView();
        initData();
    }



    private void initMyView(){
        titleTv.setText("应用升级");
        backLeftLiner.setVisibility(View.VISIBLE);
        final LinearLayoutManager manager = new LinearLayoutManager(UpdateAppListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        appInfoList = new ArrayList<>();

        appAdapter = new UpdateAppAdapter(UpdateAppListActivity.this, appInfoList);
        recyclerView.setAdapter(appAdapter);


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

    List<UpdateApp> updateAppList = new ArrayList<>();
    private void initData(){
        updateAppList.clear();
        List<PackageInfo> packageInfos = ApkUtils.getInstalledApp(UpdateAppListActivity.this);
        if(packageInfos!=null){
            for(int i=0;i<packageInfos.size();i++){
                UpdateApp updateApp = new UpdateApp();
                updateApp.setPackage_name(packageInfos.get(i).packageName);
                updateApp.setVersion_name(packageInfos.get(i).versionName);

                if((packageInfos.get(i).applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
                {
                    updateAppList.add(updateApp);//如果非系统应用，则添加至appList
                }
            }
            getAppMoreList(updateAppList);
        }
    }

    @OnClick({R.id.back_left_liner})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back_left_liner:
                finish();
                break;
        }
    }

    /**
     * 更新
     */
    private void getAppMoreList(List<UpdateApp> updateAppList) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("data",updateAppList);
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.UPDATE_APP_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (TextUtils.isEmpty(s)) return;
                        List<AppInfo> appInfos = JsonUtil.stringToArray(s,AppInfo[].class);
                        if(appInfos==null){
                            return;
                        }
                        appInfoList.clear();
                        appInfoList.addAll(appInfos);
                        appAdapter.refreshData(appInfoList);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 如果有课程下载完成 刷新列表
    public void onDownLoadFinishEvent(DownLoadFinishEvent downLoadFinishEvent) {
        appAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

}
