package com.huiyun.vrxf.ui;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huiyun.vrxf.R;
import com.huiyun.vrxf.html5.HTML5WebView;
import com.huiyun.vrxf.util.NetWorkUtils;


/**
 * Created by admin on 2016/1/27.
 */
public class MyWebViewActivity extends BaseActivity{

    public static MyWebViewActivity myWebViewActivity;
    private LinearLayout liner_main;
    private HTML5WebView mWebView;
    private String url,title;
    private TextView t_title;
    private LinearLayout back_liner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myWebViewActivity = this;
        setContentView(R.layout.activity_webview);
        initData();

        liner_main = findView(R.id.liner_main);
        mWebView = new HTML5WebView(this);
        mWebView.requestFocus();

        mWebView.loadUrl(url);

        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        liner_main.addView(mWebView.getLayout(), params);

        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        mWebView.setVerticalScrollBarEnabled(false);

        //支持javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
    // 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
    // 设置出现缩放工具
        mWebView.getSettings().setBuiltInZoomControls(true);
    //扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.setWebViewClient(new MyWebViewClient());
        if(NetWorkUtils.isNetworkAvailable(MyWebViewActivity.this)){
            beginLoading(MyWebViewActivity.this);
        }

        initView();
    }

    private void initData(){
        if(getIntent()!=null){
            url = (String) getIntent().getExtras().getString("html_url");
            title = (String) getIntent().getExtras().getString("title");
        }
    }

    private void initView() {
        t_title = findView(R.id.t_title);
        t_title.setText(title);
        back_liner = findView(R.id.back_left_liner);
        back_liner.setVisibility(View.VISIBLE);
        back_liner.setOnClickListener(this);
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

    @Override
    protected void onDestroy() {
        myWebViewActivity = null;
        super.onDestroy();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            view.stopLoading();
            view.clearView();
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            endLoading();
        }
    }
}
