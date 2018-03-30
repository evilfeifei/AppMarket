package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.adapter.ClassifyGridviewAdapter;
import com.huiyun.amnews.adapter.MainHotGameAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.Classify;
import com.huiyun.amnews.configuration.DefaultValues;
import com.huiyun.amnews.event.AdapterOnItemClickListener;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.CategoryListActivity;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.ui.MyWebViewActivity;
import com.huiyun.amnews.ui.SearchActivity;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.view.AbListView;
import com.huiyun.amnews.view.LoopViewPager;
import com.huiyun.amnews.wight.NoScrollGridView;
import com.huiyun.amnews.wight.ObservableScrollView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 游戏
 * Created by Justy on 2018/3/16.
 */
@SuppressLint("ValidFragment")
public class GameFragment extends BaseFragment implements ObservableScrollView.ScrollViewListener,AdapterOnItemClickListener {

    View rootView;

    @Bind(R.id.home_scrollview)
    ObservableScrollView homeScrollview;
    @Bind(R.id.loop_vp)
    LoopViewPager loopViewPager;
    private int imageHeight;
    @Bind(R.id.insearch_layout_lin)
    View insearchLay;
    @Bind(R.id.search_home_lin)
    LinearLayout home_search_lin;

    //热门精选
    @Bind(R.id.game_gridview)
    NoScrollGridView gameGridview;
    private MainHotGameAdapter mainHotGameAdapter;
    private List<AppInfo> appInfoListGame = new ArrayList<>();

    //精品推荐
    @Bind(R.id.final_list)
    AbListView finalList;
    private List<AppInfo> mAppInfoList = new ArrayList<>();
    private AppAdapter getAppAdapterFinal;

    //分类
    @Bind(R.id.categories_gridview)
    NoScrollGridView categoriesGridview;
    private List<Classify> classifyList = new ArrayList<>();
    private ClassifyGridviewAdapter classifyGridviewAdapterApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game,container,false);
        ButterKnife.bind(this,rootView);

        initView();
        initListeners();
        initData();

        return rootView;
    }

    private void initView(){
        EventBus.getDefault().register(this);//订阅
        loopViewPager.setPointPosition(LoopViewPager.POINT_CENTER);
        rootView.findViewById(R.id.search_edit).setOnClickListener(this);
        rootView.findViewById(R.id.down_right_liner).setOnClickListener(this);
        rootView.findViewById(R.id.more_game_tv).setOnClickListener(this);

        LinearLayout.LayoutParams paraRight;
        paraRight = (LinearLayout.LayoutParams) loopViewPager.getLayoutParams();
        paraRight.width = width;
        paraRight.height = (int) (width*((double)400/1080));
        loopViewPager.setLayoutParams(paraRight);

        loopViewPager.setOnPagerClickLisenter(new LoopViewPager.OnPagerClickLisenter() {
            @Override
            public void onPagerClickLisenter(int clickPosition) {
                if(dataListAd!=null&&dataListAd.size()>0){
                    Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    bundle.putString("title_name",dataListAd.get(clickPosition).get("title").toString());
                    bundle.putString("html_data",dataListAd.get(clickPosition).get("content").toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void initData(){
        mainHotGameAdapter = new MainHotGameAdapter(getActivity(),appInfoListGame);
        gameGridview.setAdapter(mainHotGameAdapter);

        getAppAdapterFinal = new AppAdapter(getActivity(),mAppInfoList);
        finalList.setAdapter(getAppAdapterFinal);

        classifyGridviewAdapterApp = new ClassifyGridviewAdapter(getActivity(),classifyList);
        categoriesGridview.setAdapter(classifyGridviewAdapterApp);
        classifyGridviewAdapterApp.setType(DefaultValues.APP_TYPE_GAME);
        classifyGridviewAdapterApp.setAdapterOnItemClickListener(this);

        getAppDataList();
    }

    private void initListeners() {
        // 获取顶部轮播图高度后，设置滚动监听
        ViewTreeObserver vto = loopViewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                loopViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imageHeight = loopViewPager.getHeight();
                homeScrollview.setScrollViewListener(GameFragment.this);
            }
        });
    }

    List<Map<String, Object>> dataListAd;
    private void getAppDataList() {
        HashMap<String, Object> params = new HashMap<>();
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.GAME_FRAGMENT_DATA)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (TextUtils.isEmpty(s)) return;
                        Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(s);
                        List<AppInfo> appInfosSelection = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("selection")),AppInfo[].class); //精选
                        List<AppInfo> appInfosRecommend = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("recommend")),AppInfo[].class); //推荐
                        List<Classify> classifys = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("categories")),Classify[].class); //游戏分类
                        appInfoListGame.addAll(appInfosSelection);
                        mainHotGameAdapter.refreshData(appInfoListGame);

                        mAppInfoList.addAll(appInfosRecommend);
                        getAppAdapterFinal.refreshData(mAppInfoList);

                        classifyList.addAll(classifys);
                        classifyGridviewAdapterApp.refreshData(classifyList);

                        dataListAd = (List<Map<String, Object>>) dataMap.get("carousel");
                        if (dataListAd != null) {
                            List<String> listPics = new ArrayList<String>();
                            for (int i = 0; i < dataListAd.size(); i++) {
                                Map<String, Object> map = dataListAd.get(i);
                                listPics.add(map.get("image").toString());
                            }
                            loopViewPager.initPageView(listPics, null, false, R.drawable.top_main_bg);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        /*if (y <= 0) {
            insearchLay.setBackgroundColor(Color.argb((int) 0, 255,255,255));//AGB由相关工具获得，或者美工提供
            home_search_lin.setBackgroundResource(R.drawable.search_home_gray_bg);
        } else if (y > 0 && y <= imageHeight) {
            float scale = (float) y / imageHeight;
            float alpha = (255 * scale);
            // 只是layout背景透明(仿知乎滑动效果)
            insearchLay.setBackgroundColor(Color.argb((int) alpha, 255,255,255));
        } else {
            insearchLay.setBackgroundColor(Color.argb((int) 255, 255,255,255));
            home_search_lin.setBackgroundResource(R.drawable.search_home_gray_bg);
        }*/
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
            case R.id.search_edit: //搜索
                switchActivity(SearchActivity.class,null);
                break;
            case R.id.more_game_tv://更多游戏
                bundle = new Bundle();
                bundle.putString(PreferenceCode.CATEGORY_TITLE, "游戏");
                bundle.putInt(PreferenceCode.CATEGORY_TYPE, DefaultValues.APP_TYPE_GAME);
                bundle.putInt(PreferenceCode.CATEGORY_ID, DefaultValues.CATEGORY_GAME_ID);
                switchActivity(CategoryListActivity.class, bundle);
                break;
        }
    }

    @Override
    public void onItemClickListener(Object clazz, int type) {
        Classify classify = (Classify) clazz;
//        Intent intent = new Intent(getActivity(),CategoryAppActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("currentClassifyId",classify.getId());
//        bundle.putInt("type",type);
//        bundle.putSerializable("classifyList", (Serializable) classifyList);
//        intent.putExtras(bundle);
//        getActivity().startActivity(intent);

        if(TextUtils.isEmpty(classify.getUrl())) {
            int typeId = (int) (double) (Double.parseDouble(classify.getId()));
            Bundle bundle = new Bundle();
            bundle.putString(PreferenceCode.CATEGORY_TITLE, classify.getName());
            bundle.putInt(PreferenceCode.CATEGORY_TYPE, typeId);
            bundle.putInt(PreferenceCode.CATEGORY_ID, DefaultValues.CATEGORY_GAME_ID);
            switchActivity(CategoryListActivity.class, bundle);
        }else{
            Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("type",1);
            bundle.putString("title_name",classify.getName().toString());
            bundle.putString("html_data",classify.getUrl());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 如果有课程下载完成 刷新列表
    public void onDownLoadFinishEvent(DownLoadFinishEvent downLoadFinishEvent) {
        getAppAdapterFinal.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

}
