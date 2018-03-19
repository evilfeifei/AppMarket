package com.huiyun.amnews.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.NewFragmentAdapter;
import com.huiyun.amnews.been.CategorySecond;
import com.huiyun.amnews.configuration.DefaultValues;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.fragment.CategoryChildAppFragment;
import com.huiyun.amnews.ui.fragment.ClassifyFragment;
import com.huiyun.amnews.ui.fragment.NewsFragment;
import com.huiyun.amnews.ui.fragment.RankingListContainersFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Justy on 2018/3/9.
 */

public class ClassifyActivity extends BaseActivity {

    @Bind(R.id.tab_layout_news)
    TabLayout tabLayout;

    @Bind(R.id.view_pager_news)
    ViewPager viewPager;
    private NewFragmentAdapter newFragmentAdapter;
    private final List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        ButterKnife.bind(this);

        if(getIntent()!=null){
            currentItem = getIntent().getExtras().getInt("currentItem");
        }

        initMyView();
    }

    private void initMyView(){
        findViewById(R.id.down_right_liner).setOnClickListener(this);
        findViewById(R.id.search_edit).setOnClickListener(this);
        findViewById(R.id.back_img).setOnClickListener(this);
        fragmentList.add(new CategoryChildAppFragment("", DefaultValues.APP_TYPE_HOME_BIBEI));
        fragmentList.add(new CategoryChildAppFragment("", DefaultValues.APP_TYPE_HOME_JINGXUAN));
        fragmentList.add(new CategoryChildAppFragment("", DefaultValues.APP_TYPE_HOME_REMEN));
        fragmentList.add(new ClassifyFragment());
        fragmentList.add(new RankingListContainersFragment());
        titles.add("必备");
        titles.add("精选");
        titles.add("热门");
        titles.add("分类");
        titles.add("排行榜");

        newFragmentAdapter = new NewFragmentAdapter(ClassifyActivity.this.getSupportFragmentManager(),fragmentList,titles);
        viewPager.setAdapter(newFragmentAdapter);
        viewPager.setOffscreenPageLimit(newFragmentAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(currentItem);
    }

    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle;
        switch (v.getId()) {
            case R.id.down_right_liner:
                bundle = new Bundle();
                bundle.putString(PreferenceCode.DOWNLOAD_MANAGER, "dm");
                switchActivity(DownloadManagerActivity.class, bundle);
                break;
            case R.id.search_edit:
                switchActivity(SearchActivity.class,null);
                break;
            case R.id.back_img:
                finish();
                break;
        }
    }

}
