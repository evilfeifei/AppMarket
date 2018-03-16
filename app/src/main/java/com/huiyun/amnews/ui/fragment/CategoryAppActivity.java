package com.huiyun.amnews.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.NewFragmentAdapter;
import com.huiyun.amnews.been.Classify;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.BaseActivity;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.ui.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 资源（暂时不用）
 * Created by Administrator on 2016/4/17 0017.
 */
public class CategoryAppActivity extends BaseActivity {

    @Bind(R.id.tab_layout_news)
    TabLayout tabLayout;

    @Bind(R.id.view_pager_news)
    ViewPager viewPager;
    private NewFragmentAdapter newFragmentAdapter;
    private final List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<Classify> classifyList = new ArrayList<>();
    private String currentClassifyId;
    private int type;

    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_category);
        ButterKnife.bind(this);

        if(getIntent()!=null){
            currentClassifyId = getIntent().getExtras().getString("currentClassifyId");
            type = getIntent().getExtras().getInt("type");
            classifyList = (List<Classify>) getIntent().getSerializableExtra("classifyList");
        }

        initView();
        initData();
    }

    private void initView(){
        findViewById(R.id.down_right_liner).setOnClickListener(this);
        findViewById(R.id.search_edit).setOnClickListener(this);
        findViewById(R.id.back_img).setOnClickListener(this);

    }


    private void initData() {
        for(int i=0;i<classifyList.size();i++){
            fragmentList.add(new CategoryChildAppFragment(classifyList.get(i).getId(),type));
            titles.add(classifyList.get(i).getName());
            if(classifyList.get(i).getId().equals(currentClassifyId)){
                currentItem = i;
            }
        }
        newFragmentAdapter = new NewFragmentAdapter(CategoryAppActivity.this.getSupportFragmentManager(),fragmentList,titles);
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
