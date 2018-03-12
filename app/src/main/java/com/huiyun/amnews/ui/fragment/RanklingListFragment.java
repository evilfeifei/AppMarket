package com.huiyun.amnews.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.NewsAdapter;
import com.huiyun.amnews.been.News;
import com.huiyun.amnews.been.NewsData;
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
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Justy on 2018/3/7.
 */

public class RanklingListFragment extends BaseFragment {

    int width,height;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.recycler_view)
    HeaderAndFooterRecyclerView recyclerView;
    private List<News> newsList = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private String category;
    private int pageSize = 15;
    private boolean noMore;
    private int page = 1;

    private String lastId="";

    public void onAttach(Context context) {
        super.onAttach(context);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = width*506/1125;
    }

    public RanklingListFragment(){}
    public RanklingListFragment(String category){
        this.category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, rootView);
        initMyView();
        return rootView;
    }

    private void initMyView(){
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        newsList = new ArrayList<>();

        newsAdapter = new NewsAdapter(getActivity(), newsList);
        recyclerView.setAdapter(newsAdapter);

    }

    private void getNewsList(String category, String first_id, final String last_id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("category",category);
        params.put("first_id",first_id);
        params.put("last_id",last_id);
        params.put("sizeze",pageSize);
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.NEWS_LIST_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        refreshLayout.setRefreshing(false);
                        if (TextUtils.isEmpty(s)) return;
                        NewsData newsData = (NewsData) JsonUtil.jsonToBean(s,NewsData.class);
                        if(!TextUtils.isEmpty(newsData.getLast_id())){
                            lastId = newsData.getLast_id();
                        }else{
                            noMore = true;
                        }
                        if(newsData.getCount()<1){
                            noMore = true;
                        }
                        if (page==1) { //刷新
                            newsList = new ArrayList<News>();
                        }

                        newsList.addAll(newsData.getNews());
                        newsAdapter.refreshData(newsList);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

}
