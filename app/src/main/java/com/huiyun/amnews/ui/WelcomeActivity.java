//package com.huiyun.amnews.ui;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.huiyun.amnews.R;
//import com.huiyun.amnews.adapter.WelcomeAdapter;
//import com.huiyun.amnews.been.AppHotAndFinalListBean;
//import com.huiyun.amnews.been.AppInfo;
//import com.huiyun.amnews.fusion.Constant;
//import com.huiyun.amnews.util.JsonUtil;
//import com.huiyun.amnews.wight.NoScrollGridView;
//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//
//import org.apache.http.Header;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//
///**
// * Created by Justy on 2018/3/8.
// */
//
//public class WelcomeActivity extends BaseActivity {
//
//    @Bind(R.id.app_gridview)
//    NoScrollGridView noScrollGridView;
//    private List<AppInfo> appInfoList = new ArrayList<>();
//    private WelcomeAdapter welcomeAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_welcome);
//        ButterKnife.bind(this);
//
//        initView();
//    }
//
//    private void initView(){
//        welcomeAdapter = new WelcomeAdapter(WelcomeActivity.this,appInfoList);
//        noScrollGridView.setAdapter(welcomeAdapter);
//
//        getAllAppInfoList(1,userId);
//    }
//
//    public void getAllAppInfoList(int pageNo ,String userId){
//        RequestParams rp = new RequestParams();
//        rp.put("pageNo", pageNo);
//        rp.put("userId", userId);
//        ahc.post(WelcomeActivity.this, Constant.HOT_FINAL_URL, rp,
//                new JsonHttpResponseHandler(Constant.UNICODE) {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers,
//                                          JSONObject response) {
//                        Log.e("response", response.toString());
//                        super.onSuccess(statusCode, headers, response);
//                        if (statusCode == 200) {
//                            AppHotAndFinalListBean appHotAndFinalListBean = (AppHotAndFinalListBean) JsonUtil.jsonToBean(response.toString(), AppHotAndFinalListBean.class);
//                            if (appHotAndFinalListBean.getError().equals("")) {
//                                if (appHotAndFinalListBean.getMenu1().size() > 0) {
//                                    appInfoList.addAll(appHotAndFinalListBean.getMenu1());
//                                    appInfoList.addAll(appHotAndFinalListBean.getMenu2());
//                                    welcomeAdapter.refreshData(appInfoList);
//                                }
//                            } else {
//                                Toast.makeText(WelcomeActivity.this, appHotAndFinalListBean.getError(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers,
//                                          String responseString, Throwable throwable) {
//                        super.onFailure(statusCode, headers, responseString,
//                                throwable);
//                        Toast.makeText(WelcomeActivity.this, "请检查网络!",
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//
//}
