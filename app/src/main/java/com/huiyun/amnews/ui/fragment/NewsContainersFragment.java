package com.huiyun.amnews.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.NewFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        return rootView;
    }

    private void initMyView(){
        fragmentList.add(new NewsFragment());
        fragmentList.add(new NewsFragment());
        fragmentList.add(new NewsFragment());
        fragmentList.add(new NewsFragment());
        fragmentList.add(new NewsFragment());
        fragmentList.add(new NewsFragment());
        titles.add("推荐");
        titles.add("热点");
        titles.add("视频");
        titles.add("美女");
        titles.add("感情");
        titles.add("图片");

        newFragmentAdapter = new NewFragmentAdapter(getActivity().getSupportFragmentManager(),fragmentList,titles);
        viewPager.setAdapter(newFragmentAdapter);
        viewPager.setOffscreenPageLimit(newFragmentAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);
    }
}
