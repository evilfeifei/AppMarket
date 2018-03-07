package com.huiyun.amnews.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.CategorySecond;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.ui.VRCategoryActivity;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.view.RefreshLayout;
import com.huiyun.amnews.view.Tag;
import com.huiyun.amnews.view.TagListView;
import com.huiyun.amnews.view.TagView;
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
 * Created by Administrator on 2016/4/17 0017.
 */
public class CategoryChildAppFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener{

    View rootView;
    private RefreshLayout refreshLayout;
    private ListView listView;

    private List<AppInfo> appBeans;
    private int page = 1;
    private boolean noMore;
    private static int pagesize = 20;
    private AppAdapter appAdapter;
    private int seniorCategoryId;
    List<CategorySecond> category;

    private View headView;
    private TagListView mTagListView;
    private final List<Tag> mTags = new ArrayList<Tag>();

    public CategoryChildAppFragment(int seniorCategoryId,List<CategorySecond> category){
        this.seniorCategoryId = seniorCategoryId;
        this.category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category_child,container,false);

        initView(rootView);
        setListener();

        return rootView;
    }

    private void initView(View view){
        EventBus.getDefault().register(this);//订阅
        view.findViewById(R.id.lin_no_data).setVisibility(View.GONE);
        refreshLayout = (RefreshLayout)view.findViewById(R.id.refreshlayout);
        listView = (ListView)view.findViewById(R.id.list);
        refreshLayout.setColorSchemeResources(R.color.black, R.color.app_blue);
        refreshLayout.post(new Thread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }));
        appAdapter = new AppAdapter(getActivity());
        listView.setAdapter(appAdapter);
        onRefresh();

        headView = LayoutInflater.from(getActivity()).inflate(R.layout.select_tag_view, null);
        mTagListView = (TagListView) headView.findViewById(R.id.tagview);
        setUpData();
        if(category!=null&&category.size()>1){
            setUpData();
            mTagListView.setTags(mTags);
            listView.addHeaderView(headView);
        }

    }

    private void setUpData() {
        mTags.clear();
        for (int i = 0; i < category.size(); i++) {
            Tag tag = new Tag();
            tag.setId(Integer.valueOf(category.get(i).getId()));
            tag.setChecked(true);
            tag.setTitle(category.get(i).getName());
            mTags.add(tag);
        }
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadListener(this);

        mTagListView.setOnTagClickListener(new TagListView.OnTagClickListener() {
            @Override
            public void onTagClick(TagView tagView, Tag tag) {
                Bundle bundle = new Bundle();
                bundle.putInt("seniorCategoryId",tag.getId());
                bundle.putString("name", tag.getTitle());
                switchActivity(VRCategoryActivity.class,bundle);
            }
        });
    }

    public void getMAppInfoByCategoryId(int pageNo){
        RequestParams rp = new RequestParams();
        rp.put("seniorCategoryId", seniorCategoryId);
        rp.put("pageNo", pageNo);
        ahc.post(getActivity(), Constant.GET_APPINFO_BY_CATEGORYID, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("response",response.toString());
                        if (statusCode == 200) {
                            refreshLayout.setRefreshing(false);
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
                                    appAdapter = new AppAdapter(getActivity(), appBeans);
                                }

                                if (page > 1) {
                                    appAdapter.refreshData(appBeans);
                                    return;
                                }

                                listView.setAdapter(appAdapter);
                                appAdapter.refreshData(appBeans);
                                appAdapter.notifyDataSetInvalidated();
                                if(appBeans.size()<1){
                                    rootView.findViewById(R.id.lin_no_data).setVisibility(View.VISIBLE);
                                }else{
                                    rootView.findViewById(R.id.lin_no_data).setVisibility(View.GONE);
                                }
                        }
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(getActivity(), "请检查网络!",Toast.LENGTH_LONG).show();
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
    public void onLoad() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setLoading(false);
                if(!noMore){
                    page++;
//                    getOrderList(page);
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

    @Subscribe(threadMode = ThreadMode.MAIN) // 如果有课程下载完成 刷新列表
    public void onDownLoadFinishEvent(DownLoadFinishEvent downLoadFinishEvent) {
        appAdapter.notifyDataSetChanged();
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
