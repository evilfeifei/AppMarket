package com.huiyun.amnews.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.AppAdapterOld;
import com.huiyun.amnews.adapter.AppDetailsAdapter;
import com.huiyun.amnews.adapter.ImagesAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.downLoad.LogDownloadListener;
import com.huiyun.amnews.downLoad.OkDownLoad;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.event.OneKeyShareCallback;
import com.huiyun.amnews.event.ScrolledEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.fragment.CommentFragment;
import com.huiyun.amnews.ui.fragment.IntroduceFragment;
import com.huiyun.amnews.util.ApkUtils;
import com.huiyun.amnews.util.DateUtil;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.NetworkUtil;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.view.DragTopLayout;
import com.huiyun.amnews.view.roundimage.RoundedImageView;
import com.huiyun.amnews.wight.NumberProgressBar;
import com.huiyun.amnews.wight.ProgressButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;
import com.lzy.okserver.download.db.DownloadDBManager;
import com.lzy.okserver.listener.DownloadListener;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.onekeyshare.OnekeyShare;
import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;
import it.sephiroth.android.library.widget.HListView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by admin on 2016/5/26.
 */
public class AppDettailsActivity2 extends BaseActivity {

    @Bind(R.id.appbarlayout)
    AppBarLayout appbarlayout;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.iv_icon)
    RoundedImageView ivIcon;

    public static AppDettailsActivity2 appDettailsActivity;
    private AppDetailsAdapter appDetailsAdapter;
    private ViewPager viewPager;
    private String userId,token;

    public AppInfo appBean;
    private boolean isDownLoad = false;
    private String[] imageNames;
    private HListView hListView;
    private ImagesAdapter imagesAdapter;
    private ImageView collectImg;
    private TextView trampleTv,praiseTv,commentTv;//踩，赞,评论
    private ImageView trampleImg,praiseImg;
    private NumberProgressBar pbProgress;
    private ProgressButton progressButton;

    private boolean isShow;
    private RatingBar ratingBar;

    public static String SENDMESSAGE="vr_ref_comment";
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDettailsActivity = this;
        setContentView(R.layout.activity_details2);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView(){
        EventBus.getDefault().register(this);//订阅
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        hListView = findView(R.id.hlistview);

        appDetailsAdapter = new AppDetailsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(appDetailsAdapter);
        viewPager.setOffscreenPageLimit(appDetailsAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new myOnPageChangeListener());

        findView(R.id.back_details_liner).setOnClickListener(this);
        findView(R.id.collect_liner).setOnClickListener(this);
        findView(R.id.trample_line).setOnClickListener(this);
        findView(R.id.praise_line).setOnClickListener(this);
        findView(R.id.comment_tv).setOnClickListener(this);
        findView(R.id.down_load_tv).setOnClickListener(this);
        findView(R.id.send_comment_tv).setOnClickListener(this);
        findView(R.id.share_liner).setOnClickListener(this);
        collectImg = findView(R.id.collect_img);
        trampleTv = findView(R.id.trample_tv);
        trampleImg = findView(R.id.trample_img);
        praiseTv = findView(R.id.praise_tv);
        praiseImg = findView(R.id.praise_img);
        ratingBar = findView(R.id.environment_rat);
        pbProgress = findView(R.id.pbProgress);

        progressButton = findView(R.id.progressButton);
        progressButton.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            appBean = (AppInfo) bundle.get(PreferenceCode.APP_INFO);
            isDownLoad = bundle.getBoolean("isDownload");
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
        ((TextView)findViewById(R.id.doun_load_num)).setText(appBean.getDownload_count()+"次下载");//下载次数
        ((TextView)findViewById(R.id.time_tv)).setText(DateUtil.timesOne(appBean.getLastModifiedTime()+""));
        ((TextView)findViewById(R.id.app_score_tv)).setText(appBean.getComment_score()+"分");
        ((TextView)findViewById(R.id.system_req_tv)).setText(appBean.getSystem_require());
        showImg(AppDettailsActivity2.this,appBean.getThumbnailName(),ivIcon,0);

        DecimalFormat df = new DecimalFormat("#.0");
        double x = Double.valueOf(appBean.getSize()) / 1024 / 1024;
        String size = df.format(x);
        ((TextView)findViewById(R.id.app_size_tv)).setText(size + "MB");


        ((TextView)findViewById(R.id.name_tv)).setText(appBean.getName());
        ((TextView)findViewById(R.id.name_tv)).setText(appBean.getName());
        trampleTv.setText("踩（" + appBean.getTrampleCount() + "）");
        praiseTv.setText("赞（"+appBean.getPraiseCount()+"）");

        getDownLoadState();
    }



    @Subscribe(threadMode = ThreadMode.MAIN) // 如果滑动到顶部展开、底部折叠
    public void onScrolledEvent(ScrolledEvent scrolledEvent) {
        if(scrolledEvent!=null){
            if(scrolledEvent.isScrolledToTop()){
                appbarlayout.setExpanded(true);
            }else{
                appbarlayout.setExpanded(false);
                ToastUtil.toastshort(AppDettailsActivity2.this,"到底部了");
            }
        }
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
            case R.id.share_liner:
                showShare(appBean.getName(),appBean.getName(),appBean.getDownloadUrl(),appBean.getThumbnailName());
                break;
            case R.id.back_details_liner:
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
//            case R.id.down_load_tv:
            case R.id.progressButton:
                if(downloadInfo!=null){
                    if(downloadInfo.getState() == DownloadManager.FINISH) { //已经下载完成
                        if (ApkUtils.isAvailable(AppDettailsActivity2.this, new File(downloadInfo.getTargetPath()))) {
                            ApkUtils.openApp(AppDettailsActivity2.this, ApkUtils.getPackageName(AppDettailsActivity2.this, downloadInfo.getTargetPath()));
                        } else {
                            ApkUtils.install(AppDettailsActivity2.this, new File(downloadInfo.getTargetPath()));
                        }
                    } else{
                        if(downloadInfo.getState()==DownloadManager.NONE||downloadInfo.getState()==DownloadManager.PAUSE||downloadInfo.getState()==DownloadManager.WAITING) {
                            downloadManager.addTask(downloadInfo.getUrl(), downloadInfo.getRequest(), downloadInfo.getListener());
                            ((TextView) findView(R.id.down_load_tv)).setText("暂停");
                            progressButton.setText("暂停");
                        }else if(downloadInfo.getState()==DownloadManager.DOWNLOADING){
                            downloadManager.pauseTask(downloadInfo.getUrl());
                            ((TextView) findView(R.id.down_load_tv)).setText("继续");
                            progressButton.setText("继续");
                        }
                    }
                }else{
                    GetRequest request = OkGo.get(appBean.getDownloadUrl());
                    OkDownLoad.getInstance().getManger().addTask(appBean.getName() + ".apk", appBean, appBean.getDownloadUrl(), request, new LogDownloadListener());
                    getDownLoadState();
                    if (!AppmarketPreferences.getInstance(this).getStringKey(PreferenceCode.USERID).equals("")) {
                        receiveScore();
                    }
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

    DownloadInfo downloadInfo;
    private void getDownLoadState(){
        downloadManager = DownloadService.getDownloadManager();
        downloadInfo = OkDownLoad.getInstance().getManger().getDownloadInfo(appBean.getDownloadUrl());
        if(downloadInfo!=null) {
            DownloadListener downloadListener = new MyDownloadListener();
            downloadInfo.setListener(downloadListener);
            if(downloadInfo.getState() == DownloadManager.FINISH) {
                if (ApkUtils.isAvailable(AppDettailsActivity2.this, ((AppInfo) downloadInfo.getData()).getPackage_name())) {
                    ((TextView) findView(R.id.down_load_tv)).setText("打开");
                    progressButton.setText("打开");
                } else {
                    ((TextView) findView(R.id.down_load_tv)).setText("安装");
                    progressButton.setText("安装");
                }
            }else if(downloadInfo.getState()==DownloadManager.NONE||downloadInfo.getState()==DownloadManager.PAUSE||downloadInfo.getState()==DownloadManager.WAITING){
                if(!isDownLoad) {
                    ((TextView) findView(R.id.down_load_tv)).setText("继续");
                    progressButton.setText("继续");
                }else{
                    downloadManager.addTask(downloadInfo.getUrl(), downloadInfo.getRequest(), downloadInfo.getListener());
                    ((TextView) findView(R.id.down_load_tv)).setText("暂停");
                    progressButton.setText("暂停");
                }
            }else if(downloadInfo.getState()==DownloadManager.DOWNLOADING){
                ((TextView)findView(R.id.down_load_tv)).setText("暂停");
                progressButton.setText("暂停");
            }else if(downloadInfo.getState()==DownloadManager.ERROR){
                ((TextView)findView(R.id.down_load_tv)).setText("出错");
                progressButton.setText("出错");
            }
            pbProgress.setVisibility(View.GONE);
            pbProgress.setMax(10000);
            pbProgress.setProgress((int) (Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 1));
            progressButton.setProgress((int) (Math.round(downloadInfo.getProgress() * 100) * 1.0f / 1));
        }else{
            ((TextView)findView(R.id.down_load_tv)).setText("下载");
            progressButton.setText("下载");
            pbProgress.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 如果有课程下载完成 刷新列表
    public void onDownLoadFinishEvent(DownLoadFinishEvent downLoadFinishEvent) {
        getDownLoadState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
    }

    private void showShare(String title,String text,String shareurl,String shareimage) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(shareurl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(shareimage);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareurl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(title);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(shareurl);
        oks.setCallback(new OneKeyShareCallback(AppDettailsActivity2.this));
        // 启动分享GUI
        oks.show(AppDettailsActivity2.this);
    }

    private class MyDownloadListener extends DownloadListener {

        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            pbProgress.setMax(10000);
            pbProgress.setProgress((int) (Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 1));
            Log.e("pbProgress",(int) (Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 1)+"");
            progressButton.setProgress((int) (Math.round(downloadInfo.getProgress() * 100) * 1.0f / 1));
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
            Toast.makeText(AppDettailsActivity2.this, "下载完成", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new DownLoadFinishEvent());
            ApkUtils.install(AppDettailsActivity2.this, new File(downloadInfo.getTargetPath()));
            setDownLoadCount(appBean);
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
            if (errorMsg != null) Toast.makeText(AppDettailsActivity2.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    //下载
    public void setDownLoadCount(AppInfo appInfo) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("app_id",appInfo.getId());
        params.put("userId",userId);
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.DOWN_LOAD_COUNT_URL)
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
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }
}
