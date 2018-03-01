package com.huiyun.vrxf.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.vrxf.R;
import com.huiyun.vrxf.adapter.AppAdapter;
import com.huiyun.vrxf.been.AppInfo;
import com.huiyun.vrxf.configuration.AppmarketPreferences;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.huiyun.vrxf.util.JsonUtil;
import com.huiyun.vrxf.view.RefreshLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我赞过的
 * Created by admin on 2016/5/26.
 */
public class MyPraiseActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener {

    public static MyPraiseActivity myCollectActivity;
    private RefreshLayout refreshLayout;
    private ListView listView;
    private TextView t_title;
    private LinearLayout back_liner;

    private List<AppInfo> appBeans;
    private int page = 1;
    private boolean noMore;
    private static int pagesize = 20;
    private AppAdapter appAdapter;
    private String userId,token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCollectActivity = this;
        setContentView(R.layout.activity_my_praise);

        userId = AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.USERID);
        token = AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.TOKEN);
        initView();
        setListener();
    }

    private void initView(){
        t_title = findView(R.id.t_title);
        t_title.setText("我赞过的");
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
        appAdapter = new AppAdapter(MyPraiseActivity.this);
        listView.setAdapter(appAdapter);
        onRefresh();
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadListener(this);
    }

    public void getMyPraise(int pageNo){
        RequestParams rp = new RequestParams();
        rp.put("userId", userId);
        rp.put("token", token);
        rp.put("pageNo", pageNo);
        ahc.post(MyPraiseActivity.this, Constant.MY_PRAISE_LIST, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            refreshLayout.setRefreshing(false);
                            Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(response.toString());
                            if (dataMap == null) {
                                return;
                            }
                            if (dataMap.get("error").toString().equals("")) {

                                List<AppInfo> appBeanList = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("list")), AppInfo[].class);
                                if (appBeanList.size() % pagesize != 0) {
                                    noMore = true;
                                }

                                if (page == 1) {
                                    appBeans = new ArrayList<AppInfo>();
                                }

                                appBeans.addAll(appBeanList);

                                if (appAdapter == null) {
                                    appAdapter = new AppAdapter(MyPraiseActivity.this, appBeans);
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
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(MyPraiseActivity.this, "请检查网络!", Toast.LENGTH_LONG).show();
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
                    getMyPraise(page);
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
                getMyPraise(page);

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
}
