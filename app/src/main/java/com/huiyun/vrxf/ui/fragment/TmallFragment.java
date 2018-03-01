package com.huiyun.vrxf.ui.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.huiyun.vrxf.R;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.html5.HTML5WebView;
import com.huiyun.vrxf.util.NetWorkUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
public class TmallFragment extends BaseFragment {

    View rootView;
    private LinearLayout liner_main;
    private HTML5WebView mWebView;
    String url ;
    protected AsyncHttpClient ahc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tmall,container,false);

        if(ahc==null){
            ahc = new AsyncHttpClient();
        }

        initView();

        mWebView.setWebViewClient(new MyWebViewClient());
        if(NetWorkUtils.isNetworkAvailable(getActivity())){
            beginLoading(getActivity());
            getAppShopUrl(getActivity());
        }

        return rootView;
    }

    private void initView(){
        liner_main = (LinearLayout) rootView.findViewById(R.id.liner_main);
        mWebView = new HTML5WebView(getActivity());
        mWebView.requestFocus();
//        ((TextView)rootView.findViewById(R.id.t_title)).setText("商城");

//        mWebView.loadUrl(url);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);

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

//        mWebView.setWebViewClient(new MyWebViewClient());
//        if(NetWorkUtils.isNetworkAvailable(MyWebViewActivity.this)){
//            beginLoading(MyWebViewActivity.this);
//        }

    }



    public void getAppShopUrl(Context context) {
        RequestParams rp = new RequestParams();
        ahc.post(context, Constant.APP_SHOP, rp,
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
                                } else {
                                    url = responseMsg.getString("shop");
                                    if(url!=null){
                                        mWebView.loadUrl(url);
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
                    }
                });
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
