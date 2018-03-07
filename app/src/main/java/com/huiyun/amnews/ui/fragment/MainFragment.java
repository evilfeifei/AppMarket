package com.huiyun.amnews.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.amnews.MainActivity;
import com.huiyun.amnews.MyApplication;
import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.been.AppHotAndFinalListBean;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.ui.MyWebViewActivity;
import com.huiyun.amnews.ui.SearchActivity;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.view.AbListView;
import com.huiyun.amnews.view.LoopViewPager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
public class MainFragment extends BaseFragment {

    View rootView;
    private String userId;
    private String avatar;
    private String token;
    private String nickname = "";
    private String city = "";

    private int pagesize;
    private List<AppInfo> mAppInfoList;
    private AbListView hotList,finalList;
    private AppAdapter appAdapterHot,getAppAdapterFinal;
    private LoopViewPager loopViewPager;
    List<Map<String, Object>> dataList1;
    private static int width ,height;
    private TextView locationTv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        initView(rootView);
        getAllAppInfoList(1,userId);
        getAdList(city);
        addListener();
        return rootView;
    }

    private void initView(View view){
        EventBus.getDefault().register(this);//订阅
        view.findViewById(R.id.down_right_liner).setOnClickListener(this);
        view.findViewById(R.id.bibei_lin).setOnClickListener(this);
        view.findViewById(R.id.jingxuan_lin).setOnClickListener(this);
        view.findViewById(R.id.dianyin_lin).setOnClickListener(this);
        view.findViewById(R.id.yingyin_lin).setOnClickListener(this);
        view.findViewById(R.id.youxi_lin).setOnClickListener(this);
        view.findViewById(R.id.search_edit).setOnClickListener(this);

        loopViewPager = (LoopViewPager) view.findViewById(R.id.loop_vp);
        loopViewPager.setPointPosition(LoopViewPager.POINT_CENTER);

        LinearLayout.LayoutParams paraRight;
        paraRight = (LinearLayout.LayoutParams) loopViewPager.getLayoutParams();
        paraRight.width = width;
        paraRight.height = (int) (width*((double)375/1080));
        loopViewPager.setLayoutParams(paraRight);

        hotList = (AbListView) view.findViewById(R.id.hot_list);
        finalList = (AbListView) view.findViewById(R.id.final_list);
        appAdapterHot = new AppAdapter(getActivity());
        getAppAdapterFinal = new AppAdapter(getActivity());
        hotList.setAdapter(appAdapterHot);
        finalList.setAdapter(getAppAdapterFinal);

        locationTv = (TextView) view.findViewById(R.id.location_tv);
        if(MyApplication.getInstance().city!=null){
            city = MyApplication.getInstance().city;
        }else{
            city = AppmarketPreferences.getInstance(getActivity()).getCityKey(
                    PreferenceCode.LOCATION_CITY);
        }
        locationTv.setText(city);
    }

    private void addListener(){
        loopViewPager.setOnPagerClickLisenter(new LoopViewPager.OnPagerClickLisenter() {
            @Override
            public void onPagerClickLisenter(int clickPosition) {
                if(dataList1!=null&&dataList1.size()>0){
                    Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("html_url",dataList1.get(clickPosition).get("content").toString());
                    bundle.putString("title",dataList1.get(clickPosition).get("title").toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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

//                            Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(response.toString());
//                            if (dataMap == null) {
//                                return;
//                            }
                            AppHotAndFinalListBean appHotAndFinalListBean = (AppHotAndFinalListBean) JsonUtil.jsonToBean(response.toString(), AppHotAndFinalListBean.class);
                            if (appHotAndFinalListBean.getError().equals("")) {
                                if (appHotAndFinalListBean.getMenu1().size() > 0) {
                                    appAdapterHot.refreshData(appHotAndFinalListBean.getMenu1());
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
                        Toast.makeText(getActivity(), "请检查网络!",
                                Toast.LENGTH_LONG).show();
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
        city = AppmarketPreferences.getInstance(getActivity()).getCityKey(
                PreferenceCode.LOCATION_CITY);

        if(appAdapterHot!=null&&getAppAdapterFinal!=null){
            appAdapterHot.notifyDataSetChanged();
            getAppAdapterFinal.notifyDataSetChanged();
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
            case R.id.bibei_lin: //必备
                MainActivity.instance.setCurrentTab(1, 0);
                break;
            case R.id.jingxuan_lin: //精选
                MainActivity.instance.setCurrentTab(1,1);
                break;
            case R.id.dianyin_lin: //电影
                MainActivity.instance.setCurrentTab(1,2);
                break;
            case R.id.yingyin_lin: //影音
                MainActivity.instance.setCurrentTab(1,3);
                break;
            case R.id.youxi_lin: //游戏
                MainActivity.instance.setCurrentTab(1,4);
                break;
            case R.id.search_edit:
                switchActivity(SearchActivity.class,null);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 如果有课程下载完成 刷新列表
    public void onDownLoadFinishEvent(DownLoadFinishEvent downLoadFinishEvent) {
        appAdapterHot.notifyDataSetChanged();
        getAppAdapterFinal.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }
}
