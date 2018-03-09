package com.huiyun.amnews.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huiyun.amnews.Constants;
import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.NewFragmentAdapter;
import com.huiyun.amnews.been.NewsCategory;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Justy on 2018/3/1.
 */

public class NewsContainersFragment extends BaseFragment {

    int width,height;
    @Bind(R.id.tab_layout_news)
    TabLayout tabLayout;

    @Bind(R.id.view_pager_news)
    ViewPager viewPager;

    private NewFragmentAdapter newFragmentAdapter;
    private final List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public void onAttach(Context context) {
        super.onAttach(context);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = width*506/1125;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_containers, container, false);
        ButterKnife.bind(this, rootView);
        initMyView();
        getNewsCategory();
        return rootView;
    }

    private void initMyView(){
//        fragmentList.add(new NewsFragment());
//        fragmentList.add(new NewsFragment());
//        fragmentList.add(new NewsFragment());
//        fragmentList.add(new NewsFragment());
//        fragmentList.add(new NewsFragment());
//        fragmentList.add(new NewsFragment());
//        titles.add("推荐");
//        titles.add("热点");
//        titles.add("视频");
//        titles.add("美女");
//        titles.add("感情");
//        titles.add("图片");
//
//        newFragmentAdapter = new NewFragmentAdapter(getActivity().getSupportFragmentManager(),fragmentList,titles);
//        viewPager.setAdapter(newFragmentAdapter);
//        viewPager.setOffscreenPageLimit(newFragmentAdapter.getCount());
//        tabLayout.setupWithViewPager(viewPager);
    }

    private void getNewsCategory() {
        HashMap<String, Object> params = new HashMap<>();
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.get(Constant.NEWS_CATEGORY_URL)
                .tag(this)
//                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (TextUtils.isEmpty(s)) return;
                        List<NewsCategory> newsCategories = JsonUtil.stringToArray(s, NewsCategory[].class);
                        if(newsCategories!=null&&newsCategories.size()>0){
                            for(NewsCategory newsCategory:newsCategories){
                                titles.add(newsCategory.getName());
                                fragmentList.add(new NewsFragment(newsCategory.getAlias()));

                                newFragmentAdapter = new NewFragmentAdapter(getActivity().getSupportFragmentManager(),fragmentList,titles);
                                viewPager.setAdapter(newFragmentAdapter);
                                viewPager.setOffscreenPageLimit(newFragmentAdapter.getCount());
                                tabLayout.setupWithViewPager(viewPager);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }
}
