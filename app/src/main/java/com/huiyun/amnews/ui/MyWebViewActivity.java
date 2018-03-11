package com.huiyun.amnews.ui;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.html5.HTML5WebView;
import com.huiyun.amnews.ui.BaseActivity;

/**
 * Created by admin on 2016/1/27.
 */
public class MyWebViewActivity extends BaseActivity {

    public static MyWebViewActivity myWebViewActivity;
    private LinearLayout liner_main;
    private HTML5WebView mWebView;

    private String titleName="",htmlData="";
    private int type; //type=0 html代码 type=1 链接
    private boolean isShowShare = false;
    private ProgressBar mPageLoadingProgressBar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myWebViewActivity = this;
        setContentView(R.layout.activity_webview);
        initData();
        initMyView();
    }

    private void initData(){
        if(getIntent()!=null){
            type = getIntent().getExtras().getInt("type");
            titleName = getIntent().getExtras().getString("title_name");
            htmlData = getIntent().getExtras().getString("html_data");
            isShowShare = getIntent().getExtras().getBoolean("is_show_share",false);
        }
    }

    private void initMyView() {
        findView(R.id.back_img).setOnClickListener(this);
        ((TextView)findView(R.id.web_title_tv)).setText(titleName);
        initProgressBar();

        liner_main = findView(R.id.liner_main);
        mWebView = new HTML5WebView(this);
        mWebView.requestFocus();

        if(type==1) {
            mWebView.loadUrl(htmlData);
        } else {
            htmlData = htmlData.replaceAll("&amp;", "");
            htmlData = htmlData.replaceAll("quot;", "\"");
            htmlData = htmlData.replaceAll("lt;", "<");
            htmlData = htmlData.replaceAll("gt;", ">");
            mWebView.loadDataWithBaseURL(null,htmlData, "text/html", "utf-8", null);
        }

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

        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        if(type!=1) {
//            mWebView.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
        }

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mPageLoadingProgressBar.setVisibility(View.GONE);

                } else {
                    if (mPageLoadingProgressBar.getVisibility() == View.GONE)
                        mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                    mPageLoadingProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }


    private void initProgressBar() {
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);// new
        // ProgressBar(getApplicationContext(),
        // null,
        // android.R.attr.progressBarStyleHorizontal);
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.drawable.color_progressbar));
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        myWebViewActivity = null;
        super.onDestroy();
        if (mWebView != null) {
            mWebView.removeAllViews();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.setTag(null);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
        }
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
        public void onReceivedSslError(WebView view,
                                       SslErrorHandler handler, SslError error) {
            // handler.cancel();// Android默认的处理方式
            handler.proceed();// 接受所有网站的证书
            // handleMessage(Message msg);// 其他处理
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
