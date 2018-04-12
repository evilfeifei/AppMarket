package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.adapter.AppAdapterNew;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.CategorySecond;
import com.huiyun.amnews.configuration.DefaultValues;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.ui.CategoryListActivity;
import com.huiyun.amnews.ui.VRCategoryActivity;
import com.huiyun.amnews.util.ApkUtils;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.view.RefreshLayout;
import com.huiyun.amnews.view.Tag;
import com.huiyun.amnews.view.TagListView;
import com.huiyun.amnews.view.TagView;
import com.huiyun.amnews.wight.LoadMoreFooter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
@SuppressLint("ValidFragment")
public class CategoryChildAppFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreFooter.OnLoadMoreListener{

    View rootView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.recycler_view)
    HeaderAndFooterRecyclerView recyclerView;
    private LoadMoreFooter loadMoreFooter;
    private List<AppInfo> appInfoList ;
    private AppAdapterNew appAdapter;
    private boolean noMore;
    private int page = 1;
    private String categoryId;
    private int type;

    public CategoryChildAppFragment(){}

    public CategoryChildAppFragment(String categoryId,int type){
        this.categoryId = categoryId;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category_child,container,false);
        ButterKnife.bind(this,rootView);

        initView(rootView);

        return rootView;
    }

    private void initView(View view){
        EventBus.getDefault().register(this);//订阅
        view.findViewById(R.id.lin_no_data).setVisibility(View.GONE);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        loadMoreFooter = new LoadMoreFooter(getActivity(), recyclerView, this);
        loadMoreFooter.setState(LoadMoreFooter.STATE_DISABLED);
        appInfoList = new ArrayList<>();

        appAdapter = new AppAdapterNew(getActivity(), appInfoList);
        recyclerView.setAdapter(appAdapter);
        refreshLayout.setOnRefreshListener(this);

        onRefresh();
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
        params.put("category_id",categoryId);
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
        }
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        page = 1;
        noMore = false;
        getAppMoreList(page, type);
    }

    @Override
    public void onLoadMore() {
        if(!noMore){
            page = page+1;
            getAppMoreList(page, type);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN) // 如果有课程下载完成 刷新列表
    public void onDownLoadFinishEvent(DownLoadFinishEvent downLoadFinishEvent) {
        appAdapter.myNotifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

    @Override
    public void onResume() {
        super.onResume();
        if(appAdapter!=null){
            appAdapter.notifyDataSetChanged();
        }
    }
}
