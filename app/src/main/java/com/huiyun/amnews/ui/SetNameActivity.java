package com.huiyun.amnews.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.huiyun.amnews.R;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.util.ToastUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by malei on 2016/3/7.
 */
public class SetNameActivity extends BaseActivity {

    private EditText editName;
    private String name;

    private String userId;
    private String avatar;
    private String token;
    private String nickname = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        userId = AppmarketPreferences.getInstance(SetNameActivity.this).getStringKey(PreferenceCode.USERID);
        token = AppmarketPreferences.getInstance(SetNameActivity.this).getStringKey(PreferenceCode.TOKEN);
        nickname = AppmarketPreferences.getInstance(SetNameActivity.this).getStringKey(
                PreferenceCode.NICKNAME);
        avatar = AppmarketPreferences.getInstance(SetNameActivity.this).getStringKey(
                PreferenceCode.AVATAR);
        initView();
    }

    private void initView(){
        findView(R.id.refresh_right_liner).setVisibility(View.VISIBLE);
        findView(R.id.right_title_img).setVisibility(View.GONE);
        findView(R.id.right_title_tv).setVisibility(View.VISIBLE);
        findView(R.id.refresh_right_liner).setOnClickListener(this);
        ((TextView)findViewById(R.id.right_title_tv)).setText("保存");
        ((TextView)findViewById(R.id.t_title)).setText("编辑昵称");

        findView(R.id.back_left_liner).setVisibility(View.VISIBLE);
        findView(R.id.back_left_liner).setOnClickListener(this);

        editName = (EditText) findViewById(R.id.edit_name);
        editName.setText(nickname);
    }


    private void changeName(final String name) {
        RequestParams rp = new RequestParams();
        rp.put("userId", userId);
        rp.put("token", token);
        rp.put("nickName", name);
        ahc.post(SetNameActivity.this, Constant.CHANGE_NICKNAME_URL, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            // {"responseMsg":{"error":"","success":"S"}}
                            JSONObject jsonObject = response;
                            try {
                                JSONObject responseMsg = jsonObject
                                        .getJSONObject("responseMsg");
                                if (responseMsg.getString("success")
                                        .equals("S")) {
                                    ToastUtil.toastshort(SetNameActivity.this, "修改成功");
                                    AppmarketPreferences.getInstance(SetNameActivity.this)
                                            .setStringKey(
                                                    PreferenceCode.NICKNAME, name);
                                    finish();
                                } else {
                                    String error = responseMsg
                                            .getString("error");
                                    ToastUtil.toastshort(SetNameActivity.this, error);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        throwable.printStackTrace();
                        Toast.makeText(SetNameActivity.this, "请检查网络!", 1).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        name = editName.getText().toString();
        switch (v.getId()){
            case R.id.refresh_right_liner:
                if(name==null||name.equals("")){
                    Toast.makeText(SetNameActivity.this,"请输入昵称!",Toast.LENGTH_SHORT).show();
                    return;
                }
                changeName(name);
                break;
            case R.id.back_left_liner:
                finish();
                break;
        }
    }
}
