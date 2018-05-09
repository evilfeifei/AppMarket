package com.huiyun.amnews.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.adapter.AppAdapterNew;
import com.huiyun.amnews.adapter.AppSearchGridviewAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.KeybordSUtils;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.wight.LoadMoreFooter;
import com.huiyun.amnews.wight.NoScrollGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 搜索
 * Created by admin on 2016/5/26.
 */
public class SearchActivity extends BaseActivity {

    public static SearchActivity myCollectActivity;
    @Bind(R.id.list_search)
    HeaderAndFooterRecyclerView listView;

    @Bind(R.id.back_left_liner)
    LinearLayout back_liner;
    @Bind(R.id.delete_img)
    ImageView deleteImg;
    @Bind(R.id.search_et)
    EditText searchEt;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

    private List<AppInfo> appInfoList = new ArrayList<>();
    private AppAdapterNew appAdapter;

    @Bind(R.id.hot_gridview)
    NoScrollGridView hotGridview;

    @Bind(R.id.app_gridview)
    NoScrollGridView appGridview;

    @Bind(R.id.game_gridview)
    NoScrollGridView gameGridview;

    private List<AppInfo> apps = new ArrayList<>();
    private List<AppInfo> games = new ArrayList<>();
    private List<AppInfo> hots = new ArrayList<>();
    private AppSearchGridviewAdapter appSearchGridviewAdapter,gameSearchGridviewAdapter,hotSearchGridviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCollectActivity = this;
        setContentView(R.layout.activity_vr_search);
        ButterKnife.bind(this);

        initView();
        getSearchPage();
        setListener();
    }

    private void initView(){
        findView(R.id.lin_no_data).setVisibility(View.GONE);
        final LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);
        appAdapter = new AppAdapterNew(SearchActivity.this,appInfoList);
        listView.setAdapter(appAdapter);

        hotSearchGridviewAdapter = new AppSearchGridviewAdapter(SearchActivity.this,hots);
        hotGridview.setAdapter(hotSearchGridviewAdapter);
        appSearchGridviewAdapter = new AppSearchGridviewAdapter(SearchActivity.this,apps);
        appGridview.setAdapter(appSearchGridviewAdapter);
        gameSearchGridviewAdapter = new AppSearchGridviewAdapter(SearchActivity.this,games);
        gameGridview.setAdapter(gameSearchGridviewAdapter);
    }

    private void setListener() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = searchEt.getText().toString();
                if(TextUtils.isEmpty(name)){
                    deleteImg.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                    appInfoList.clear();
                    findView(R.id.lin_no_data).setVisibility(View.GONE);
                    appAdapter.refreshData(appInfoList);
                }else{
                    deleteImg.setVisibility(View.VISIBLE);

                    searchApp(name);
                }
            }
        });

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    //完成自己的事件
                    String name = searchEt.getText().toString();
                    if(TextUtils.isEmpty(name)){
                        ToastUtil.toastshort(SearchActivity.this,"请输入关键字");
                        searchEt.setFocusable(true);
                        searchEt.setFocusableInTouchMode(true);
                        searchEt.requestFocus();
                    }else {
                        searchApp(name);
                    }
                }
                return false;
            }
        });
    }

    private void getSearchPage() {
        HashMap<String, Object> params = new HashMap<>();
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.SEARCH_PAGE_URL)
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
                        if(dataMap.get("app_rank")!=null) {
                            List<AppInfo> appInfoApps = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("app_rank")), AppInfo[].class);
                            appSearchGridviewAdapter.refreshData(appInfoApps);
                        }
                        if(dataMap.get("game_rank")!=null){
                            List<AppInfo> appInfoGames = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("game_rank")),AppInfo[].class);
                            gameSearchGridviewAdapter.refreshData(appInfoGames);
                        }
                        if(dataMap.get("hot")!=null){
                            List<AppInfo> appInfoHots = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("hot")),AppInfo[].class);
                            hotSearchGridviewAdapter.refreshData(appInfoHots);
                        }



                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    public void searchApp(String name){

        HashMap<String, Object> params = new HashMap<>();
        params.put("name",name);
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.SEARCH_APP_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        scrollView.setVisibility(View.GONE);
                        if (TextUtils.isEmpty(s)) {
                            findView(R.id.lin_no_data).setVisibility(View.VISIBLE);
                        }else{
                            if(s.equals("null")){
                                findView(R.id.lin_no_data).setVisibility(View.VISIBLE);
                            }else {
                                List<AppInfo> appInfos = JsonUtil.stringToArray(s, AppInfo[].class);
                                if (appInfos != null && appInfos.size() > 0) {
                                    KeybordSUtils.closeKeybord(searchEt,SearchActivity.this);
                                    findView(R.id.lin_no_data).setVisibility(View.GONE);
                                    appInfoList.addAll(appInfos);
                                    appAdapter.refreshData(appInfoList);
                                } else {
                                    findView(R.id.lin_no_data).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }



    @OnClick({R.id.back_left_liner,R.id.delete_img})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_left_liner:
                finish();
                break;
            case R.id.delete_img: //删除搜索
                searchEt.setText("");
                appInfoList.clear();
                findView(R.id.lin_no_data).setVisibility(View.GONE);
                appAdapter.refreshData(appInfoList);
                scrollView.setVisibility(View.VISIBLE);
                break;
            case R.id.search_img: //点击搜索
                String name = searchEt.getText().toString();
                if(TextUtils.isEmpty(name)){
                    ToastUtil.toastshort(SearchActivity.this,"请输入关键字");
                    return;
                }
                searchApp(name);
                break;
        }
    }
}
