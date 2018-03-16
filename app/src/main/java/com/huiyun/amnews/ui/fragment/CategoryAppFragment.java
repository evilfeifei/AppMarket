package com.huiyun.amnews.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.huiyun.amnews.MainActivity;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.CategoryFirst;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.ui.SearchActivity;
import com.huiyun.amnews.util.JsonUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.widget.Toast;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;

/**
 * 资源（暂时不用）
 * Created by Administrator on 2016/4/17 0017.
 */
public class CategoryAppFragment extends BaseFragment {

    View rootView;
    private ModelPagerAdapter adapter;
    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private List<String> titles = new ArrayList<String>();
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private int index = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category,container,false);

        initView(rootView);
        initData();

        return rootView;
    }

    private void initView(View view){
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        pagerSlidingTabStrip = (PagerSlidingTabStrip)view. findViewById(R.id.tabs);
        view.findViewById(R.id.down_right_liner).setOnClickListener(this);
        view.findViewById(R.id.search_edit).setOnClickListener(this);

        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MainActivity.instance.categoryIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initData() {
        getAllAppInfoList();
    }

    public void getAllAppInfoList( ){
        RequestParams rp = new RequestParams();
        ahc.get(getActivity(), Constant.GET_CATEGORY_LIST, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        Log.e("response", response.toString());
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {

                            Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(response.toString());
                            if (dataMap == null) {
                                return;
                            }
                            if (dataMap.get("error").toString().equals("")) {
                                List<CategoryFirst> categoryList = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("list")), CategoryFirst[].class);
                                if(categoryList!=null&&categoryList.size()>0){
                                    titles.clear();
                                    fragments.clear();
                                    for(int i=0;i<categoryList.size();i++){
                                        titles.add(categoryList.get(i).getName());
                                        fragments.add(new CategoryChildAppFragment(Integer.valueOf(categoryList.get(i).getId()),categoryList.get(i).getCategory()));
                                    }
                                    PagerModelManager factory = new PagerModelManager();
                                    factory.addCommonFragment(fragments, titles);
                                    adapter = new ModelPagerAdapter(getActivity().getSupportFragmentManager(), factory);
                                    viewPager.setAdapter(adapter);
                                    pagerSlidingTabStrip.setViewPager(viewPager);
                                    viewPager.setOffscreenPageLimit(2);
                                    index = MainActivity.instance.categoryIndex;
                                    viewPager.setCurrentItem(index);
                                    MainActivity.instance.categoryIndex = 0;
                                }
                            } else {
                                Toast.makeText(getActivity(), dataMap.get("error").toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(getActivity(), "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            index = MainActivity.instance.categoryIndex;
//            if(index!=0){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(index);
//                        MainActivity.instance.categoryIndex = 0;
                    }
                }, 100);
//            }
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.down_right_liner:
                Bundle bundle = new Bundle();
                bundle.putString(PreferenceCode.DOWNLOAD_MANAGER, "dm");
                switchActivity(DownloadManagerActivity.class, bundle);
                break;
            case R.id.search_edit:
                switchActivity(SearchActivity.class,null);
                break;
        }
    }
}
