package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.MainActivity;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.UpdateApp;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.ui.IdeaFeedBackActivity;
import com.huiyun.amnews.ui.LoginActivity;
import com.huiyun.amnews.ui.MyCollectActivity;
import com.huiyun.amnews.ui.MyPraiseActivity;
import com.huiyun.amnews.ui.MyTrampleActivity;
import com.huiyun.amnews.ui.SettingActivity;
import com.huiyun.amnews.ui.UninstallAppListActivity;
import com.huiyun.amnews.ui.UpdateAppListActivity;
import com.huiyun.amnews.ui.UserSettingActivity;
import com.huiyun.amnews.util.ApkUtils;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.view.roundimage.RoundedImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 我的
 */
@SuppressLint("ValidFragment")
public class MeFragment extends BaseFragment {

    View rootView;
    private String userId;
    private String avatar;
    private String token;
    private String nickname = "";
    private String score = "";
    private RoundedImageView my_header_img;
    private View undate_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_me, container, false);

        initView(rootView);
        getUpdateAppCount();
        return rootView;
    }

    private void initView(View view){
        view.findViewById(R.id.user_set_relate).setOnClickListener(this);
        view.findViewById(R.id.my_accunt_rel).setOnClickListener(this);
//        view.findViewById(R.id.my_header_img).setOnClickListener(this);
        view.findViewById(R.id.user_download_relate).setOnClickListener(this);
        view.findViewById(R.id.user_collect_relate).setOnClickListener(this);
        view.findViewById(R.id.user_caiguo_relate).setOnClickListener(this);
        view.findViewById(R.id.user_zanguo_relate).setOnClickListener(this);
        view.findViewById(R.id.user_idea_relate).setOnClickListener(this);
        view.findViewById(R.id.app_update_relate).setOnClickListener(this);
        view.findViewById(R.id.app_unistall_relate).setOnClickListener(this);
        my_header_img = (RoundedImageView) view.findViewById(R.id.my_header_img);
        undate_view = (View) view.findViewById(R.id.undate_view);
        undate_view.setVisibility(View.GONE);
    }

    List<UpdateApp> updateAppList = new ArrayList<>();
    private void getUpdateAppCount(){
        updateAppList.clear();
        List<PackageInfo> packageInfos = ApkUtils.getInstalledApp(getActivity());
        if(packageInfos!=null){
            for(int i=0;i<packageInfos.size();i++){
                UpdateApp updateApp = new UpdateApp();
                updateApp.setPackage_name(packageInfos.get(i).packageName);
                updateApp.setVersion_name(packageInfos.get(i).versionName);

                if((packageInfos.get(i).applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
                {
                    updateAppList.add(updateApp);//如果非系统应用，则添加至appList
                }
            }
            getAppMoreList(updateAppList);
        }
    }

    /**
     * 更新
     */
    private void getAppMoreList(final List<UpdateApp> updateAppList) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("data",updateAppList);
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.UPDATE_APP_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (TextUtils.isEmpty(s)) return;
                        List<AppInfo> appInfos = JsonUtil.stringToArray(s,AppInfo[].class);
                        if(appInfos==null){
                            return;
                        }
                        if(appInfos!=null&&appInfos.size()>0){
                            undate_view.setVisibility(View.VISIBLE);
                        }else{
                            undate_view.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        userId = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.USERID);
        token = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.TOKEN);
        nickname = AppmarketPreferences.getInstance(getActivity()).getStringKey(
                PreferenceCode.NICKNAME);
        avatar = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.AVATAR);
        score = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.USER_SCORE);
        if(userId.equals("")){
            rootView.findViewById(R.id.user_line).setVisibility(View.GONE);
            rootView.findViewById(R.id.unlogin_text).setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(avatar)
                    .placeholder(R.drawable.touxiang)
                    .dontAnimate()
                    .into(my_header_img);

            ((TextView)rootView.findViewById(R.id.my_name)).setText(nickname);
        }else{
            rootView.findViewById(R.id.user_line).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.unlogin_text).setVisibility(View.GONE);

            Glide.with(getActivity())
                    .load( avatar)
                    .placeholder(R.drawable.touxiang)
                    .dontAnimate()
                    .into(my_header_img);

            ((TextView)rootView.findViewById(R.id.my_name)).setText(nickname);
            ((TextView)rootView.findViewById(R.id.score_tv)).setText(score+"V币");

            getScore();
        }
    }

    public void getScore() {
        RequestParams rp = new RequestParams();
        rp.put("userId", userId);
        rp.put("token", token);
        ahc.post(getActivity(), Constant.GET_SCORE, rp,
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
                                    ToastUtil.toastshort(getActivity(), error);
                                } else {
                                    String score = responseMsg.getString("score");
                                    ((TextView)rootView.findViewById(R.id.score_tv)).setText(score+"V币");
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
                        Toast.makeText(getActivity(), "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.user_set_relate:
                loadNext(SettingActivity.class);
                break;
            case R.id.my_accunt_rel:
                if (userId.equals("")) {
                    loadNext(LoginActivity.class);
                } else {
                    loadNext(UserSettingActivity.class);
                }
                break;
            case R.id.user_download_relate:
                Bundle bundle = new Bundle();
                bundle.putString(PreferenceCode.DOWNLOAD_MANAGER, "dm");
                switchActivity(DownloadManagerActivity.class, bundle);
                break;
            case R.id.user_collect_relate://我的收藏
                if (userId.equals("")) {
                    loadNext(LoginActivity.class);
                } else {
                    loadNext(MyCollectActivity.class);
                }
                break;
            case R.id.user_caiguo_relate://我踩过的
                if (userId.equals("")) {
                    loadNext(LoginActivity.class);
                } else {
                    loadNext(MyTrampleActivity.class);
                }
                break;
            case R.id.user_zanguo_relate://我赞过的
                if (userId.equals("")) {
                    loadNext(LoginActivity.class);
                } else {
                    loadNext(MyPraiseActivity.class);
                }
                break;
            case R.id.user_idea_relate: //意见反馈
                loadNext(IdeaFeedBackActivity.class);
                break;
            case R.id.app_update_relate://应用升级
                loadNext(UpdateAppListActivity.class);
                break;
            case R.id.app_unistall_relate://应用卸载
                loadNext(UninstallAppListActivity.class);
        }
    }
}
