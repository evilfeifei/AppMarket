package com.huiyun.amnews.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.JsonUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索
 * Created by admin on 2016/5/26.
 */
public class SearchActivity extends BaseActivity {

    public static SearchActivity myCollectActivity;
    private ListView listView;
    private LinearLayout back_liner;

    private List<AppInfo> appBeans;
    private AppAdapter appAdapter;
    private EditText searchEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCollectActivity = this;
        setContentView(R.layout.activity_vr_search);

        initView();
        setListener();
    }

    private void initView(){
        back_liner = findView(R.id.back_left_liner);
        back_liner.setVisibility(View.VISIBLE);
        back_liner.setOnClickListener(this);
        findView(R.id.lin_no_data).setVisibility(View.GONE);
        listView = (ListView)findViewById(R.id.list);
        appAdapter = new AppAdapter(SearchActivity.this);
        listView.setAdapter(appAdapter);
        searchEt = findView(R.id.search_et);
        searchEt.setFocusableInTouchMode(true);
        searchEt.requestFocus();
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
                searchApp(name);
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
                        if (statusCode == 200) {
                            Log.e("dd",response.toString());
                            Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(response.toString());
                            if (dataMap == null) {
                                return;
                            }
                            Map<String, Object> responseMsg = (Map<String, Object>) dataMap.get("responseMsg");
                            if (responseMsg.get("error").toString().equals("")) {
                                findView(R.id.lin_no_data).setVisibility(View.GONE);
                                List<AppInfo> appBeanList = JsonUtil.stringToArray(JsonUtil.objectToJson(responseMsg.get("categoryList")), AppInfo[].class);
                                if(appBeanList!=null&&appBeanList.size()>0){
                                    appAdapter.refreshData(appBeanList);
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



    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_left_liner:
                finish();
                break;
        }
    }
}
