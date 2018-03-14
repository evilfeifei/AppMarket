package com.huiyun.amnews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.MyFragmentPagerAdapter;
import com.huiyun.amnews.been.AppInfo;
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
 * Created by Justy on 2018/3/12.
 */

public class RankingListContainersFragment extends BaseFragment {

    @Bind(R.id.assignment_viewPager)
    ViewPager assignmentViewPager;
    @Bind(R.id.ass_radioGroup)
    RadioGroup radioGroup;

    @Bind(R.id.rb_sign)
    RadioButton rb_sign;

    @Bind(R.id.rb_task)
    RadioButton rb_task;
    List<Fragment> alFragment = new ArrayList<Fragment>();

    int currentItem = 0;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ranking_containers, container, false);

        ButterKnife.bind(this,rootView);
        initMyView();
        addListener();
        getGameList();
        return rootView;
    }

    private void initMyView(){
        if(currentItem==0){
            rb_sign.setChecked(true);
            rb_task.setChecked(false);
        }else{
            rb_sign.setChecked(false);
            rb_task.setChecked(true);
        }
        //ViewPager页面切换监听
        assignmentViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.rb_sign);
                        break;
                    case 1:
                        radioGroup.check(R.id.rb_task);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setData(List<AppInfo> appInfos,List<AppInfo> games){
        alFragment.add(new RanklingListFragment(appInfos));
        alFragment.add(new RanklingListFragment(games));
        //ViewPager设置适配器
        assignmentViewPager.setAdapter(new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), alFragment));
        //ViewPager显示第一个Fragment
        assignmentViewPager.setCurrentItem(currentItem);
    }

    private void addListener(){
        //RadioGroup选中状态改变监听
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sign:
                        assignmentViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_task:
                        assignmentViewPager.setCurrentItem(1, false);
                        break;
                }
            }
        });
    }

    private void getGameList() {
        HashMap<String, Object> params = new HashMap<>();
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.RANKING_APP_LIST_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (TextUtils.isEmpty(s)) return;
                        Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(s);
                        if (dataMap == null) {
                            return;
                        }
                        List<AppInfo> appInfos = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("app_rank")),AppInfo[].class);
                        List<AppInfo> appInfoGames = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("game_rank")),AppInfo[].class);
                        setData(appInfos,appInfoGames);
                        addListener();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }
}
