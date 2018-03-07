package com.huiyun.amnews.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.view.RefreshLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 子分类列表
 * Created by admin on 2016/5/26.
 */
public class VRCategoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener {

    public static VRCategoryActivity myCollectActivity;
    private RefreshLayout refreshLayout;
    private ListView listView;
    private TextView t_title;
    private LinearLayout back_liner;

    private List<AppInfo> appBeans;
    private int page = 1;
    private boolean noMore;
    private static int pagesize = 20;
    private AppAdapter appAdapter;
    private int seniorCategoryId;
    private  String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCollectActivity = this;
        setContentView(R.layout.activity_vr_category);

        initData();
        initView();
        setListener();
    }

    private void initData(){
        if(getIntent()!=null){
            seniorCategoryId = getIntent().getExtras().getInt("seniorCategoryId");
            name = getIntent().getExtras().getString("name");
        }
    }

    private void initView(){
        EventBus.getDefault().register(this);//订阅
        t_title = findView(R.id.t_title);
        if(name!=null){
            t_title.setText(name);
        }
        back_liner = findView(R.id.back_left_liner);
        back_liner.setVisibility(View.VISIBLE);
        back_liner.setOnClickListener(this);
        findView(R.id.lin_no_data).setVisibility(View.GONE);
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshlayout);
        listView = (ListView)findViewById(R.id.list);
        refreshLayout.setColorSchemeResources(R.color.black, R.color.app_blue);
        refreshLayout.post(new Thread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }));
        appAdapter = new AppAdapter(VRCategoryActivity.this);
        listView.setAdapter(appAdapter);
        onRefresh();
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadListener(this);
    }

    public void getMAppInfoByCategoryId(int pageNo){
        RequestParams rp = new RequestParams();
        rp.put("seniorCategoryId", seniorCategoryId);
        rp.put("pageNo", pageNo);
        ahc.post(VRCategoryActivity.this, Constant.GET_APPINFO_BY_CATEGORYID, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            refreshLayout.setRefreshing(false);
                            Log.e("d",response.toString());
                            Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(response.toString());
                            if (dataMap == null) {
                                return;
                            }
                            Map<String, Object> responseMsg = (Map<String, Object>) dataMap.get("responseMsg");
                            if(responseMsg.get("error").toString().equals("")){

                                List<AppInfo> appBeanList = JsonUtil.stringToArray(JsonUtil.objectToJson(responseMsg.get("categoryList")), AppInfo[].class);
                                if (appBeanList.size() % pagesize != 0) {
                                    noMore = true;
                                }

                                if (page == 1) {
                                    appBeans = new ArrayList<AppInfo>();
                                }

                                appBeans.addAll(appBeanList);

                                if (appAdapter == null) {
                                    appAdapter = new AppAdapter(VRCategoryActivity.this, appBeans);
                                }

                                if (page > 1) {
                                    appAdapter.refreshData(appBeans);
                                    return;
                                }

                                listView.setAdapter(appAdapter);
                                appAdapter.refreshData(appBeans);
                                appAdapter.notifyDataSetInvalidated();
                                if(appBeans.size()<1){
                                    findView(R.id.lin_no_data).setVisibility(View.VISIBLE);
                                }else{
                                    findView(R.id.lin_no_data).setVisibility(View.GONE);
                                }
                            }else{
                                findView(R.id.lin_no_data).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(VRCategoryActivity.this, "请检查网络!",Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onLoad() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setLoading(false);
                if(!noMore){
                    page++;
                    getMAppInfoByCategoryId(page);
                }else{

                }
            }
        }, 0);
    }

    @Override
    public void onRefresh() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                noMore = false;
                page = 1;
                getMAppInfoByCategoryId(page);

            }
        }, 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_left_liner:
                finish();
                break;
        }
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
