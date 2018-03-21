package com.huiyun.amnews.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.amnews.MainActivity;
import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.WelcomeAdapter;
import com.huiyun.amnews.been.AppHotAndFinalListBean;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.downLoad.LogDownloadListener;
import com.huiyun.amnews.downLoad.OkDownLoad;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.wight.NoScrollGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.GetRequest;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by admin on 2016/9/20.
 */

public class WelcomeDialog extends Dialog {

    public WelcomeDialog(Context context) {
        super(context);
    }

    public WelcomeDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder implements WelcomeAdapter.ChoiceOnClickListener{
        private Context context;
        NoScrollGridView noScrollGridView;
        private List<AppInfo> appInfoList = new ArrayList<>();
        private WelcomeAdapter welcomeAdapter;
        private AsyncHttpClient ahc;
        ImageView deleteImg;
        private TextView installAllTv,choice_num_tv;

        public Builder(Context context,AsyncHttpClient ahc) {
            this.context = context;
            this.ahc = ahc;
        }
        public WelcomeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final WelcomeDialog dialog = new WelcomeDialog(context, R.style.add_costomer_dialog);
            View layout = inflater.inflate(R.layout.activity_welcome, null);
            dialog.addContentView(layout, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);

            noScrollGridView = (NoScrollGridView) layout.findViewById(R.id.app_gridview);
            deleteImg = (ImageView) layout.findViewById(R.id.delete_img);
            installAllTv = (TextView) layout.findViewById(R.id.install_all_tv);
            choice_num_tv = (TextView) layout.findViewById(R.id.choice_num_tv);
            welcomeAdapter = new WelcomeAdapter(context,appInfoList,this);
            noScrollGridView.setAdapter(welcomeAdapter);

            deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppmarketPreferences.getInstance(context).putBooleanKey(PreferenceCode.IS_FIRST_OPEN,false);
                    dialog.dismiss();
                }
            });

            installAllTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean hasCh = false;
                    if(appInfoList!=null&&appInfoList.size()>0){
                        for (AppInfo appInfo:appInfoList){
                            if(appInfo.isChoiced()) {
                                hasCh = true;
                                GetRequest request = OkGo.get(appInfo.getDownloadUrl());
                                OkDownLoad.getInstance().getManger().addTask(appInfo.getName() + ".apk", appInfo, appInfo.getDownloadUrl(), request, new LogDownloadListener());
                            }
                        }
                    }
                    AppmarketPreferences.getInstance(context).putBooleanKey(PreferenceCode.IS_FIRST_OPEN,false);
                    dialog.dismiss();
                    if(hasCh) {
//                        Intent intent = new Intent(context, DownloadManagerActivity.class);
//                        context.startActivity(intent);
                    }
                }
            });

//            getAllAppInfoList(1,"");
            getAppWelcomeList();
            return dialog;
        }



        public void getAllAppInfoList(int pageNo ,String userId){
            RequestParams rp = new RequestParams();
            rp.put("pageNo", pageNo);
            rp.put("userId", userId);
            ahc.post(context, Constant.HOT_FINAL_URL, rp,
                    new JsonHttpResponseHandler(Constant.UNICODE) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONObject response) {
                            Log.e("response", response.toString());
                            super.onSuccess(statusCode, headers, response);
                            if (statusCode == 200) {
                                AppHotAndFinalListBean appHotAndFinalListBean = (AppHotAndFinalListBean) JsonUtil.jsonToBean(response.toString(), AppHotAndFinalListBean.class);
                                if (appHotAndFinalListBean.getError().equals("")) {
                                    if (appHotAndFinalListBean.getMenu1().size() > 0) {
                                        appInfoList.addAll(appHotAndFinalListBean.getMenu1());
                                        appInfoList.addAll(appHotAndFinalListBean.getMenu2());
                                        for(AppInfo appInfo:appInfoList){
                                            appInfo.setChoiced(true);
                                        }
                                        choiceNum();
                                        welcomeAdapter.refreshData(appInfoList);
                                    }
                                } else {
                                    Toast.makeText(context, appHotAndFinalListBean.getError(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString,
                                    throwable);
                            Toast.makeText(context, "请检查网络!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }

        //首次安装为你推荐
        private void getAppWelcomeList() {
            HashMap<String, Object> params = new HashMap<>();
            String jsonData = JsonUtil.objectToJson(params);
            OkGo.post(Constant.FIRST_INSTALL_APP_LIST_URL)
                    .tag(this)
                    .upJson(jsonData)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (TextUtils.isEmpty(s)) return;
                            List<AppInfo> appInfos = JsonUtil.stringToArray(s,AppInfo[].class);
                            if(appInfos!=null&&appInfos.size()>0){
                                appInfoList.addAll(appInfos);
                                for(AppInfo appInfo:appInfoList){
                                    appInfo.setChoiced(true);
                                }
                                choiceNum();
                                welcomeAdapter.refreshData(appInfoList);
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                        }
                    });
        }

        @Override
        public void setChoiceListener() {
            choiceNum();
        }

        private void choiceNum(){
            if(appInfoList==null)return;
            DecimalFormat df = new DecimalFormat("#.0");
            double size=0;
            int num = 0;
            for(AppInfo appInfo:appInfoList){
                if(appInfo.isChoiced()){
                    size = size+Double.valueOf(appInfo.getSize()) / 1024 / 1024;
                    num = num+1;
                }
            }
            String sizeStr = df.format(size);
            choice_num_tv.setText("已选择"+num+"个应用，共"+sizeStr+"MB");
        }
    }


}
