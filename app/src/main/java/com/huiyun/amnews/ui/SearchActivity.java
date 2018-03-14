package com.huiyun.amnews.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.adapter.AppAdapterNew;
import com.huiyun.amnews.adapter.AppSearchGridviewAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.ToastUtil;
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
    @Bind(R.id.list)
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
        appAdapter = new AppAdapterNew(SearchActivity.this);
        listView.setAdapter(appAdapter);
//        searchEt.setFocusableInTouchMode(true);
//        searchEt.requestFocus();

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
                }else{
                    deleteImg.setVisibility(View.VISIBLE);
                }
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
                        List<AppInfo> appInfoApps = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("app_rank")),AppInfo[].class);
                        List<AppInfo> appInfoGames = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("game_rank")),AppInfo[].class);
                        List<AppInfo> appInfoHots = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("hot")),AppInfo[].class);

                        hotSearchGridviewAdapter.refreshData(appInfoHots);
                        appSearchGridviewAdapter.refreshData(appInfoApps);
                        gameSearchGridviewAdapter.refreshData(appInfoGames);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    public void searchApp(String name){
        RequestParams rp = new RequestParams();
        rp.put("name", name);
        ahc.post(SearchActivity.this, Constant.APP_SEARCH, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        scrollView.setVisibility(View.GONE);
                        if (statusCode == 200) {
                            Log.e("dd",response.toString());
                            Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(response.toString());
                            if (dataMap == null) {
                                return;
                            }
                            Map<String, Object> responseMsg = (Map<String, Object>) dataMap.get("responseMsg");
                            if (responseMsg.get("error").toString().equals("")) {
                                findView(R.id.lin_no_data).setVisibility(View.GONE);
                                List<AppInfo> appBeans = JsonUtil.stringToArray(JsonUtil.objectToJson(responseMsg.get("categoryList")), AppInfo[].class);
                                if(appBeans!=null&&appBeans.size()>0){
                                    appInfoList.addAll(appBeans);
                                    appAdapter.refreshData(appInfoList);
                                }
                            }else{
                                List<AppInfo> appBeanList = new ArrayList<AppInfo>();
                                appAdapter.refreshData(appBeanList);
                                findView(R.id.lin_no_data).setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(SearchActivity.this, "请检查网络!", Toast.LENGTH_LONG).show();
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
