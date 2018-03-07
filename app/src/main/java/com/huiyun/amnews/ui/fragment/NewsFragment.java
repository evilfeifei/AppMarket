package com.huiyun.amnews.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.NewsAdapter;
import com.huiyun.amnews.been.News;
import com.huiyun.amnews.wight.LoadMoreFooter;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Justy on 2018/3/7.
 */

public class NewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreFooter.OnLoadMoreListener{

    int width,height;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.recycler_view)
    HeaderAndFooterRecyclerView recyclerView;
    private LoadMoreFooter loadMoreFooter;
    private List<News> newsList = new ArrayList<>();
    private NewsAdapter newsAdapter;

    public void onAttach(Context context) {
        super.onAttach(context);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = width*506/1125;
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

        loadMoreFooter = new LoadMoreFooter(getContext(), recyclerView, this);
        loadMoreFooter.setState(LoadMoreFooter.STATE_DISABLED);
        newsList = new ArrayList<>();

        newsAdapter = new NewsAdapter(getActivity(), newsList);
        recyclerView.setAdapter(newsAdapter);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        News news1 = new News();
        List<String> imgs1 = new ArrayList<>();
        imgs1.add("http://inews.gtimg.com/newsapp_match/0/2986153431/0");
        news1.setUrls(imgs1);
        news1.setName("骚乱扩大，斯里兰卡宣布进入为期一周全国紧急状态");
        news1.setFrom("腾讯新闻");

        News news2 = new News();
        List<String> imgs2 = new ArrayList<>();
        imgs2.add("http://inews.gtimg.com/newsapp_ls/0/2986099076_150120/0");
        imgs2.add("http://inews.gtimg.com/newsapp_bt/0/2934215954/641");
        imgs2.add("http://inews.gtimg.com/newsapp_match/0/2986153431/0");
        news2.setUrls(imgs2);
        news2.setName("云南省长阮成发：治理旅游乱象 去年全省问责40多名干部");
        news2.setFrom("腾讯新闻");

        News news3 = new News();
        List<String> imgs3 = new ArrayList<>();
        imgs3.add("http://inews.gtimg.com/newsapp_match/0/2985408704/0");
        news3.setUrls(imgs3);
        news3.setName("冯远征委员：中国影视剧将走向高质量 老戏骨始终是中流砥柱");
        news3.setFrom("腾讯新闻");

        News news4 = new News();
        List<String> imgs4 = new ArrayList<>();
        imgs4.add("http://inews.gtimg.com/newsapp_match/0/2982504313/0");
        news4.setUrls(imgs4);
        news4.setName("如果你常看央视新闻频道，你可能会对她很熟悉，一位央视20多年不说话的女主播，她总是出现在电视屏幕的左下角——");
        news4.setFrom("腾讯新闻");

        newsList.add(news1);
        newsList.add(news2);
        newsList.add(news3);
        newsList.add(news4);
        newsAdapter.refreshData(newsList);

        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadMore() {

    }

}
