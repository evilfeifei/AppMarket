package com.huiyun.amnews.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.adapter.MainHotGameAdapter;
import com.huiyun.amnews.been.AppHotAndFinalListBean;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.configuration.DefaultValues;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.CategoryListActivity;
import com.huiyun.amnews.ui.ClassifyActivity;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.ui.MyWebViewActivity;
import com.huiyun.amnews.ui.SearchActivity;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.view.AbListView;
import com.huiyun.amnews.view.LoopViewPager;
import com.huiyun.amnews.wight.NoScrollGridView;
import com.huiyun.amnews.wight.ObservableScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
public class MainFragment extends BaseFragment implements ObservableScrollView.ScrollViewListener {

    View rootView;
    private String userId;
    private String avatar;
    private String token;
    private String nickname = "";
    private int pagesize;
    private NoScrollGridView gameGridview;
    private MainHotGameAdapter mainHotGameAdapter;
    private List<AppInfo> appInfoListGame = new ArrayList<>();

    private AbListView finalList;
    private List<AppInfo> mAppInfoList = new ArrayList<>();
    private AppAdapter getAppAdapterFinal;

    private LoopViewPager loopViewPager;
    List<Map<String, Object>> dataList1;
    private static int width ,height;

    ObservableScrollView homeScrollview;
    private int imageHeight;
    View insearchLay;
    LinearLayout home_search_lin;
    int page  = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        initView(rootView);
        initListeners();
//        getAllAppInfoList(1,userId);
        getGameList();
        getAppMoreList(page, DefaultValues.APP_TYPE_APPLICATION);
        getAdList("苏州");
        addListener();
        return rootView;
    }

    private void initView(View view){
        EventBus.getDefault().register(this);//订阅
        view.findViewById(R.id.down_right_liner).setOnClickListener(this);
        view.findViewById(R.id.bibei_lin).setOnClickListener(this);
        view.findViewById(R.id.jingxuan_lin).setOnClickListener(this);
        view.findViewById(R.id.remen_lin).setOnClickListener(this);
        view.findViewById(R.id.fenlei_lin).setOnClickListener(this);
        view.findViewById(R.id.paihangbang_lin).setOnClickListener(this);
        view.findViewById(R.id.search_edit).setOnClickListener(this);
        view.findViewById(R.id.more_game_tv).setOnClickListener(this);
        homeScrollview = (ObservableScrollView) view.findViewById(R.id.home_scrollview);
        insearchLay = view.findViewById(R.id.insearch_layout_lin);
        home_search_lin = (LinearLayout) view.findViewById(R.id.search_home_lin);

        loopViewPager = (LoopViewPager) view.findViewById(R.id.loop_vp);
        loopViewPager.setPointPosition(LoopViewPager.POINT_CENTER);

        LinearLayout.LayoutParams paraRight;
        paraRight = (LinearLayout.LayoutParams) loopViewPager.getLayoutParams();
        paraRight.width = width;
        paraRight.height = (int) (width*((double)400/1080));
        loopViewPager.setLayoutParams(paraRight);

        finalList = (AbListView) view.findViewById(R.id.final_list);
        getAppAdapterFinal = new AppAdapter(getActivity(),mAppInfoList);
        finalList.setAdapter(getAppAdapterFinal);

        gameGridview = (NoScrollGridView) view.findViewById(R.id.game_gridview);
        mainHotGameAdapter = new MainHotGameAdapter(getActivity(),appInfoListGame);
        gameGridview.setAdapter(mainHotGameAdapter);
    }

    private void initListeners() {
        // 获取顶部轮播图高度后，设置滚动监听
        ViewTreeObserver vto = loopViewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                loopViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imageHeight = loopViewPager.getHeight();
                homeScrollview.setScrollViewListener(MainFragment.this);
            }
        });
    }

    private void addListener(){
        loopViewPager.setOnPagerClickLisenter(new LoopViewPager.OnPagerClickLisenter() {
            @Override
            public void onPagerClickLisenter(int clickPosition) {
                if(dataList1!=null&&dataList1.size()>0){
                    Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    bundle.putString("title_name",dataList1.get(clickPosition).get("title").toString());
                    bundle.putString("html_data",dataList1.get(clickPosition).get("content").toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 获取首页推荐精品应用(精品游戏)
     */
    private void getGameList() {
        HashMap<String, Object> params = new HashMap<>();
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.HOME_GAME_APP_LIST_URL)
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
                        List<AppInfo> appInfoGames = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("games")),AppInfo[].class);
                        mainHotGameAdapter.refreshData(appInfoGames);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    /**
     * 更多精品应用
     * @param page
     * @param type:更多类型 1：精品应用 2：精品游戏
     */
    private void getAppMoreList(int page,int type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("page",page);
        params.put("type",type);
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.MORE_APP_LIST_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (TextUtils.isEmpty(s)) return;
                        List<AppInfo> appInfos = JsonUtil.stringToArray(s,AppInfo[].class);
                        mAppInfoList.addAll(appInfos);
                        getAppAdapterFinal.refreshData(appInfos);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }


    public void getAllAppInfoList(int pageNo ,String userId){
        RequestParams rp = new RequestParams();
        rp.put("pageNo", pageNo);
        rp.put("userId", userId);
        ahc.post(getActivity(), Constant.HOT_FINAL_URL, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        Log.e("response", response.toString());
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            AppHotAndFinalListBean appHotAndFinalListBean = (AppHotAndFinalListBean) JsonUtil.jsonToBean(response.toString(), AppHotAndFinalListBean.class);
                            if (appHotAndFinalListBean.getError().equals("")) {
                                if (appHotAndFinalListBean.getMenu1().size() > 0) {
                                    getAppAdapterFinal.refreshData(appHotAndFinalListBean.getMenu2());
                                }


                            } else {
                                Toast.makeText(getActivity(), appHotAndFinalListBean.getError(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(getActivity(), "请检查网络!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getAdList(String city){
        RequestParams rp = new RequestParams();
        ahc.post(getActivity(), Constant.APP_AD+city, rp,
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
                            String error = (String) dataMap.get("error");
                            if (error == null || error.equals("")) {
                                    dataList1 = (List<Map<String, Object>>) dataMap.get("list");
                                    if (dataList1 != null) {
                                        List<String> listPics = new ArrayList<String>();
                                        for (int i = 0; i < dataList1.size(); i++) {
                                            Map<String, Object> map = dataList1.get(i);
                                            listPics.add(map.get("image").toString());
                                        }
                                        loopViewPager.initPageView(listPics, null, false, R.drawable.top_main_bg);
                                    }

                            } else {
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
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
    public void onResume() {
        super.onResume();
        userId = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.USERID);
        token = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.TOKEN);
        nickname = AppmarketPreferences.getInstance(getActivity()).getStringKey(
                PreferenceCode.NICKNAME);
        avatar = AppmarketPreferences.getInstance(getActivity()).getStringKey(
                PreferenceCode.AVATAR);

        if(getAppAdapterFinal!=null){
            getAppAdapterFinal.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle ;
        switch (v.getId()){
            case R.id.down_right_liner:
                bundle = new Bundle();
                bundle.putString(PreferenceCode.DOWNLOAD_MANAGER, "dm");
                switchActivity(DownloadManagerActivity.class, bundle);
                break;
            case R.id.bibei_lin: //必备
//                MainActivity.instance.setCurrentTab(1, 0);
                bundle = new Bundle();
                bundle.putInt(PreferenceCode.CURRENTITEM, 0);
                switchActivity(ClassifyActivity.class, bundle);
                break;
            case R.id.jingxuan_lin: //精选
//                MainActivity.instance.setCurrentTab(1,1);
                bundle = new Bundle();
                bundle.putInt(PreferenceCode.CURRENTITEM, 1);
                switchActivity(ClassifyActivity.class, bundle);
                break;
            case R.id.remen_lin: //热门
//                MainActivity.instance.setCurrentTab(1,2);
                bundle = new Bundle();
                bundle.putInt(PreferenceCode.CURRENTITEM, 2);
                switchActivity(ClassifyActivity.class, bundle);
                break;
            case R.id.fenlei_lin: //分类
//                MainActivity.instance.setCurrentTab(1,3);
                bundle = new Bundle();
                bundle.putInt(PreferenceCode.CURRENTITEM, 3);
                switchActivity(ClassifyActivity.class, bundle);
                break;
            case R.id.paihangbang_lin: //排行榜
//                MainActivity.instance.setCurrentTab(1,4);
                bundle = new Bundle();
                bundle.putInt(PreferenceCode.CURRENTITEM, 4);
                switchActivity(ClassifyActivity.class, bundle);
                break;
            case R.id.search_edit: //搜索
                switchActivity(SearchActivity.class,null);
                break;
            case R.id.more_game_tv://更多游戏
                bundle = new Bundle();
                bundle.putString(PreferenceCode.CATEGORY_TITLE, "游戏");
                switchActivity(CategoryListActivity.class, bundle);
                break;
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

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {
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
        }
    }
}
