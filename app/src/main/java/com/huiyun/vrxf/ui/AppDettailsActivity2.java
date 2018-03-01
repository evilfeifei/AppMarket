package com.huiyun.vrxf.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.common.collect.Lists;
import com.huiyun.vrxf.R;
import com.huiyun.vrxf.adapter.ImagesAdapter;
import com.huiyun.vrxf.been.AppInfo;
import com.huiyun.vrxf.configuration.AppmarketPreferences;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.huiyun.vrxf.ui.fragment.CommentFragment;
import com.huiyun.vrxf.ui.fragment.IntroduceFragment;
import com.huiyun.vrxf.util.DateUtil;
import com.huiyun.vrxf.util.NetworkUtil;
import com.huiyun.vrxf.util.ToastUtil;
import com.huiyun.vrxf.view.DragTopLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by admin on 2016/5/26.
 */
public class AppDettailsActivity2 extends BaseActivity {

    public static AppDettailsActivity2 appDettailsActivity;
    private DragTopLayout dragLayout;
    private ModelPagerAdapter adapter;
    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private String userId,token;

    public AppInfo appBean;
    private String[] imageNames;
    private HListView hListView;
    private ImagesAdapter imagesAdapter;
    private ImageView collectImg;
    private TextView trampleTv,praiseTv,commentTv;//踩，赞,评论
    private ImageView trampleImg,praiseImg;

    private boolean isShow;
    private RatingBar ratingBar;

    public static String SENDMESSAGE="vr_ref_comment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDettailsActivity = this;
        setContentView(R.layout.activity_details2);

        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            appBean = (AppInfo) bundle.get(PreferenceCode.APP_INFO);
        }

        if(NetworkUtil.isNetworkConnected(this)){
//            getAppPraiseCountById();
//            getAppTrampleCountById();
        }else{
            ToastUtil.toastshort(this, "当前无网络");
        }

        imageNames = appBean.getScreenshotsName().split(",");
        if(imageNames!=null&&imageNames.length>0){
            imagesAdapter = new ImagesAdapter(AppDettailsActivity2.this,imageNames);
            hListView.setAdapter(imagesAdapter);
        }
        ((TextView)findViewById(R.id.name_tv)).setText(appBean.getName());
        ((TextView)findViewById(R.id.time_tv)).setText(DateUtil.timesOne(appBean.getCreatedTime()+""));
        ((TextView)findViewById(R.id.app_score_tv)).setText(appBean.getComment_score()+"分");
        ((TextView)findViewById(R.id.name_tv)).setText(appBean.getName());
        ((TextView)findViewById(R.id.name_tv)).setText(appBean.getName());
        trampleTv.setText("踩（" + appBean.getTrampleCount() + "）");
        praiseTv.setText("赞（"+appBean.getPraiseCount()+"）");
    }

    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dragLayout = (DragTopLayout) findViewById(R.id.drag_layout);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        hListView = findView(R.id.hlistview);

        PagerModelManager factory = new PagerModelManager();
        factory.addCommonFragment(getFragments(), getTitles());
        adapter = new ModelPagerAdapter(getSupportFragmentManager(), factory);
        viewPager.setAdapter(adapter);
        pagerSlidingTabStrip.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new myOnPageChangeListener());

        findView(R.id.back_left_liner).setOnClickListener(this);
        findView(R.id.collect_liner).setOnClickListener(this);
        findView(R.id.trample_line).setOnClickListener(this);
        findView(R.id.praise_line).setOnClickListener(this);
        findView(R.id.comment_tv).setOnClickListener(this);
        findView(R.id.down_load_tv).setOnClickListener(this);
        findView(R.id.send_comment_tv).setOnClickListener(this);
        collectImg = findView(R.id.collect_img);
        trampleTv = findView(R.id.trample_tv);
        trampleImg = findView(R.id.trample_img);
        praiseTv = findView(R.id.praise_tv);
        praiseImg = findView(R.id.praise_img);
        ratingBar = findView(R.id.environment_rat);
    }

    private List<String> getTitles(){
        return Lists.newArrayList("介绍", "评论");
    }

    private List<Fragment> getFragments(){
        List<Fragment> list = new ArrayList<>();
        Fragment listFragment = new IntroduceFragment();
        Fragment recyclerFragment = new CommentFragment();
        list.add(listFragment);
        list.add(recyclerFragment);
        return list;
    }

    private class myOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
           CommentOrPraise(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void CommentOrPraise(int index){
        if(index==0){
            findView(R.id.trample_line).setVisibility(View.VISIBLE);
            findView(R.id.praise_line).setVisibility(View.VISIBLE);
            findView(R.id.comment_tv).setVisibility(View.GONE);
        }else{
            findView(R.id.trample_line).setVisibility(View.GONE);
            findView(R.id.praise_line).setVisibility(View.GONE);
            findView(R.id.comment_tv).setVisibility(View.VISIBLE);
        }
    }

    public void addMCollectedApp() {
        RequestParams rp = new RequestParams();
        rp.put("userId", userId);
        rp.put("token", token);
        rp.put("appId", appBean.getId());
        ahc.post(this, Constant.ADD_COLLECTED_APP, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");

                                if (!responseMsg.getString("success").equals("S")) {
                                    String error = responseMsg.getString("error");
                                    ToastUtil.toastshort(AppDettailsActivity2.this, error);
                                } else {
                                    if (responseMsg.getString("status").equals("Y")) {
                                        collectImg.setImageResource(R.drawable.vr_collected_icon);
                                        ToastUtil.toastshort(AppDettailsActivity2.this, "已收藏");
                                    } else {
                                        collectImg.setImageResource(R.drawable.vr_collect_icon);
                                        ToastUtil.toastshort(AppDettailsActivity2.this, "取消收藏");
                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    // 点赞
    public void addMPraiseApp() {
        RequestParams rp = new RequestParams();
        rp.put("userId", userId);
        rp.put("token", token);
        rp.put("appId", appBean.getId());
        ahc.post(this, Constant.ADD_PRAISE_APP, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("response",response.toString());
//                        tv_collect.setEnabled(true);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
                                if (!responseMsg.getString("success").equals("S")) {
                                    String error = responseMsg.getString("error");
//									ToastUtil.toastshort(AppDetailsActivity.this,error);
                                } else {
                                    if(responseMsg.getString("status").equals("Y")){
                                        praiseImg.setImageResource(R.drawable.vr_praised_icon);
                                    }else{
                                        praiseImg.setImageResource(R.drawable.vr_praise_icon);
                                    }
                                    praiseTv.setText("赞"+"（"+responseMsg.getString("count")+"）");
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
//                        tv_collect.setEnabled(true);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    //吐槽
    public void addMTrampleApp() {
        RequestParams rp = new RequestParams();
        rp.put("userId", userId);
        rp.put("token", token);
        rp.put("appId", appBean.getId());
        ahc.post(this, Constant.ADD_TRAMPLE_APP, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
//                        tv_collect.setEnabled(true);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
                                if (!responseMsg.getString("success").equals("S")) {
                                    String error = responseMsg.getString("error");
//									ToastUtil.toastshort(AppDetailsActivity.this,error);
                                } else {
//                                    tv_trample_count.setText(appInfo.getTrampleCount() + 1 + "");
//                                    trample_check.setVisibility(View.GONE);
//                                    trample_checked.setVisibility(View.VISIBLE);
                                    if(responseMsg.getString("status").equals("Y")){
                                        trampleImg.setImageResource(R.drawable.vr_trampleed_icon);
                                    }else{
                                        trampleImg.setImageResource(R.drawable.vr_trample_icon);
                                    }
                                    trampleTv.setText("踩"+"（"+responseMsg.getString("count")+"）");
                                    Log.d("response", response.toString());
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
//                        tv_collect.setEnabled(true);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        userId = AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.USERID);
        token = AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.TOKEN);
        if(!userId.equals("")){
//            tv_collect.setVisibility(View.GONE);
//			ll_praise.setEnabled(false);
//			ll_trample.setEnabled(false);
            if(NetworkUtil.isNetworkConnected(this)){
                isCollected();
                isPraised();
                isTrample();
                getAppCount();
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_left_liner:
                finish();
                break;
            case R.id.collect_liner:
                if(!AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.USERID).equals("")){
                    addMCollectedApp();
                }else{
                    Toast.makeText(AppDettailsActivity2.this,"请先登录后收藏",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.trample_line://踩
                if(!AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.USERID).equals("")){
                    addMTrampleApp();
                }else{
                    Toast.makeText(AppDettailsActivity2.this,"请先登录后踩",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.praise_line://赞
                if(!AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.USERID).equals("")){
                    addMPraiseApp();
                }else{
                    Toast.makeText(AppDettailsActivity2.this,"请先登录后赞",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.comment_tv://评论
                if(!AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.USERID).equals("")){
                    isShow = true;
                    showComment(isShow);
                }else{
                    Toast.makeText(AppDettailsActivity2.this,"请先登录后发表评论",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.down_load_tv:
                Bundle bundle = new Bundle();
                bundle.putSerializable(PreferenceCode.APP_INFO,appBean);
                switchActivity(DownloadManagerActivity.class, bundle);
                if(!AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.USERID).equals("")){
                    receiveScore();
                }
                break;
            case R.id.send_comment_tv:
                String sendCommentString  = ((EditText)findViewById(R.id.comment_edit)).getText().toString();
                if(sendCommentString!=null&&!sendCommentString.equals("")){
                    addComment(sendCommentString);
                }else{
                    Toast.makeText(AppDettailsActivity2.this,"请输入评论",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showComment(boolean isShow){
        if(isShow){
            findView(R.id.tp_rel).setVisibility(View.GONE);
            findView(R.id.comment_line).setVisibility(View.VISIBLE);
        }else{
            findView(R.id.tp_rel).setVisibility(View.VISIBLE);
            findView(R.id.comment_line).setVisibility(View.GONE);
        }
    }

    //下载送积分
    public void receiveScore() {
        RequestParams rp = new RequestParams();
        rp.put("userId", userId);
        rp.put("token", token);
        ahc.post(AppDettailsActivity2.this, Constant.RECEIVE_SCORE, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");

                                if (!responseMsg.getString("success").equals("S")) {
                                    String error = responseMsg.getString("error");
                                    ToastUtil.toastshort(AppDettailsActivity2.this, error);
                                } else {
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    //添加评论
    public void addComment(String comment){
        RequestParams rp = new RequestParams();
        rp.put("appId", appBean.getId());
        rp.put("userId", userId);
        rp.put("token", token);
        rp.put("comment", comment);
        rp.put("score", ratingBar.getRating());
        ahc.post(this, Constant.APP_ADD_COMMENT, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
                                if (!responseMsg.getString("error").equals("")) {
                                    String error = responseMsg.getString("error");
                                } else {
                                    closeInput();
                                    Toast.makeText(AppDettailsActivity2.this,"评论发表成功",Toast.LENGTH_SHORT).show();
                                    isShow = false;
                                    showComment(isShow);

                                    Intent intentCloseActivity = new Intent(SENDMESSAGE);
				                    sendBroadcast(intentCloseActivity);

                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    //获取点赞，踩，收藏数量
    public void getAppCount(){
        RequestParams rp = new RequestParams();
        rp.put("appId", appBean.getId());
        rp.put("userId", userId);
        rp.put("token", token);
        ahc.post(this, Constant.APP_COUNT, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
                                if (!responseMsg.getString("error").equals("")) {
                                    String error = responseMsg.getString("error");
//								ToastUtil.toastshort(AppDetailsActivity.this,error);
                                } else {
                                    praiseTv.setText("赞"+"（"+responseMsg.getString("praise_count")+"）");
                                    trampleTv.setText("踩"+"（"+responseMsg.getString("trample_count")+"）");
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void isCollected(){
        RequestParams rp = new RequestParams();
        rp.put("appId", appBean.getId());
        rp.put("userId", userId);
        rp.put("token", token);
        ahc.post(this, Constant.IS_COLLECTED, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
                                if (!responseMsg.getString("error").equals("")) {
                                    String error = responseMsg.getString("error");
//								ToastUtil.toastshort(AppDetailsActivity.this,error);
                                } else {
                                    if(responseMsg.getString("result").equals("Y")){
                                        collectImg.setImageResource(R.drawable.vr_collected_icon);
                                    }else{
                                        collectImg.setImageResource(R.drawable.vr_collect_icon);
                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    //判断是否点赞
    public void isPraised(){
        RequestParams rp = new RequestParams();
        rp.put("appId", appBean.getId());
        rp.put("userId", userId);
        rp.put("token", token);
        ahc.post(this, Constant.IS_PRAISED, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
                                if (!responseMsg.getString("error").equals("")) {
                                    String error = responseMsg.getString("error");
//								ToastUtil.toastshort(AppDetailsActivity.this,error);
                                } else {
                                    if(responseMsg.getString("result").equals("Y")){
//                                        ll_praise.setEnabled(false);
//                                        praise_check.setVisibility(View.GONE);
//                                        praise_checked.setVisibility(View.VISIBLE);
                                        praiseImg.setImageResource(R.drawable.vr_praised_icon);
                                    }else{
//                                        ll_praise.setEnabled(true);
//                                        praise_check.setVisibility(View.VISIBLE);
//                                        praise_checked.setVisibility(View.GONE);
                                        praiseImg.setImageResource(R.drawable.vr_praise_icon);
                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    //判断是否吐槽
    public void isTrample(){
        RequestParams rp = new RequestParams();
        rp.put("appId", appBean.getId());
        rp.put("userId", userId);
        rp.put("token", token);
        ahc.post(this, Constant.IS_TRAMPLE, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
                                if (!responseMsg.getString("error").equals("")) {
                                    String error = responseMsg.getString("error");
//								ToastUtil.toastshort(AppDetailsActivity.this,error);
                                } else {
                                    if (responseMsg.getString("result").equals("Y")) {
//                                        ll_trample.setEnabled(false);
//                                        trample_check.setVisibility(View.GONE);
//                                        trample_checked.setVisibility(View.VISIBLE);
                                        trampleImg.setImageResource(R.drawable.vr_trampleed_icon);
                                    } else {
//                                        ll_trample.setEnabled(true);
//                                        trample_check.setVisibility(View.VISIBLE);
//                                        trample_checked.setVisibility(View.GONE);
                                        trampleImg.setImageResource(R.drawable.vr_trample_icon);
                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(AppDettailsActivity2.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void closeInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen=imm.isActive();
        if(isOpen){
            imm.hideSoftInputFromWindow(AppDettailsActivity2.this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if (isShow) {
                isShow = false;
                showComment(isShow);
            } else {
                finish();
            }
        }
        return false;
    }
}
