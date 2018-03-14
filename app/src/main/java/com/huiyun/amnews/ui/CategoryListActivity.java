package com.huiyun.amnews.ui;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.adapter.AppAdapterNew;
import com.huiyun.amnews.adapter.NewsAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.News;
import com.huiyun.amnews.configuration.DefaultValues;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.wight.LoadMoreFooter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Justy on 2018/3/14.
 */

public class CategoryListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, LoadMoreFooter.OnLoadMoreListener{

    @Bind(R.id.t_title)
    TextView titleTv;
    private String titleName="";
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.recycler_view)
    HeaderAndFooterRecyclerView recyclerView;
    private LoadMoreFooter loadMoreFooter;
    private List<AppInfo> appInfoList ;
    private AppAdapterNew appAdapter;
    private boolean noMore;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);

        if(getIntent()!=null){
            titleName = getIntent().getExtras().getString("name");
        }
        initMyView();
    }

    private void initMyView(){
        titleTv.setText(titleName);
        final LinearLayoutManager manager = new LinearLayoutManager(CategoryListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        loadMoreFooter = new LoadMoreFooter(CategoryListActivity.this, recyclerView, this);
        loadMoreFooter.setState(LoadMoreFooter.STATE_DISABLED);
        appInfoList = new ArrayList<>();

        appAdapter = new AppAdapterNew(CategoryListActivity.this, appInfoList);
        recyclerView.setAdapter(appAdapter);
        refreshLayout.setOnRefreshListener(this);

        onRefresh();
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
     * 更多精品应用
     * @param page
     * @param type:更多类型 1：精品应用 2：精品游戏
     */
    private void getAppMoreList(final int page, int type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("page",page);
        params.put("type",type);
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.MORE_APP_LIST_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        refreshLayout.setRefreshing(false);
                        if (TextUtils.isEmpty(s)) return;
                        List<AppInfo> appInfos = JsonUtil.stringToArray(s,AppInfo[].class);
                        if(appInfos==null){
                            return;
                        }
                        if(appInfos.size()==0){
                            noMore = true;
                        }
                        if (page==1) { //刷新
                            appInfoList = new ArrayList<>();
                        }
                        appInfoList.addAll(appInfos);
                        appAdapter.refreshData(appInfoList);
                        if(noMore){
                            loadMoreFooter.setState(LoadMoreFooter.STATE_FINISHED);
                        }else{
                            loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        page = 1;
        noMore = false;
        getAppMoreList(page, DefaultValues.APP_TYPE_GAME);
    }

    @Override
    public void onLoadMore() {
        if(!noMore){
            page = page+1;
            getAppMoreList(page, DefaultValues.APP_TYPE_GAME);
        }
    }
}
