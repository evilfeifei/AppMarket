package com.huiyun.vrxf.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.vrxf.R;
import com.huiyun.vrxf.configuration.AppmarketPreferences;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.huiyun.vrxf.util.NetworkUtil;
import com.huiyun.vrxf.util.PhoneUtil;
import com.huiyun.vrxf.util.ToastUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends BaseActivity implements OnClickListener {

	public static LoginActivity loginActivity;
	private TextView regist_next, login_next, title;
	private EditText phoneNumber;
	private EditText password;
	private String phoneNumString, passwordString;
	private LinearLayout back_left_liner;
	private Context context;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginActivity = this;
		setContentView(R.layout.activity_login);
		context = this;
		initView();
	}

	private void initView() {
		title = findView(R.id.t_title);
		title.setText("登录");
		back_left_liner = findView(R.id.back_left_liner);
		back_left_liner.setOnClickListener(this);
		back_left_liner.setVisibility(View.VISIBLE);
		regist_next = findView(R.id.regist_next);
		login_next = findView(R.id.login_next);

		findView(R.id.forget_tv).setOnClickListener(this);
		
		phoneNumber = (EditText) findViewById(R.id.login_num);
		password = (EditText) findViewById(R.id.login_pwd);
		phoneNumber.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		
		regist_next.setOnClickListener(this);
		login_next.setOnClickListener(this);
		
		if(PhoneUtil.getPhoneNumber(this) != null){
			phoneNumber.setText(PhoneUtil.getPhoneNumber(this));
		}
	}

	@Override
	public void onClick(View v) {
		phoneNumString = phoneNumber.getText().toString();
		passwordString = password.getText().toString();
		switch (v.getId()) {
		case R.id.regist_next:
			switchActivity(Regist1Activity.class, null);
			break;
			case R.id.forget_tv:
				switchActivity(ForgetPwdActivity.class, null);
				break;
		case R.id.login_next:
//			switchActivity(MainActivity.class, null);
			if (TextUtils.isEmpty(phoneNumString)) {
				ToastUtil.toastshort(this, "请输入手机号码");
				return;
			}
			if(phoneNumString.length()!=11){
				ToastUtil.toastshort(this, "请输入正确的手机号码");
				return;
			}
			if(TextUtils.isEmpty(passwordString)){
				ToastUtil.toastshort(this, "请输入密码");
			}else{
				if(NetworkUtil.isNetworkConnected(this)){
					login(phoneNumString,passwordString);
				}else{
					ToastUtil.toastshort(this, "当前无网络");
				}
			}
			break;
		case R.id.back_left_liner:
			this.finish();
			break;
		}
	}

	private void login(final String userPhone, String passwd) {
		beginLoading(LoginActivity.this);
		RequestParams rp = new RequestParams();
		rp.put("username", userPhone);
		rp.put("password", passwd);
		ahc.post(this, Constant.LOGIN, rp,
				new JsonHttpResponseHandler(Constant.UNICODE) {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						Log.e("login",response.toString());
						if(statusCode==200){
							JSONObject jsonObject = response;
							try {
								JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
								if(responseMsg.getString("success").equals("S")){
									
									String token = jsonObject.getString("token");
									String userId = jsonObject.getString("userId");
									String nickname = jsonObject.getString("nickname");
									String gender = jsonObject.getString("gender");
									String avatar = jsonObject.getString("avatar");
									String age = jsonObject.getString("age");
									String score = jsonObject.getString("score");

									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.TOKEN, token);
									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.PHONE, userPhone);
									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.USERID, userId);
									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.AVATAR, avatar);
									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.NICKNAME, nickname);
									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.USER_SCORE, score);

									ToastUtil.toastshort(context, "欢迎回来!");
//									switchActivity(MyCustomActivity.class,null);
									finish();
								}else{
									String error =responseMsg.getString("error");
									if(error.equals("NN") || error.equals("GN")){
										AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.USERID, responseMsg.getString("userId"));
										AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.TOKEN, responseMsg.getString("token"));
//										switchActivity(Regist2Activity.class,null);
										finish();
										ToastUtil.toastshort(context, "请完善信息");
									}else if(error.equals("AN")){
										AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.USERID, responseMsg.getString("userId"));
										AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.TOKEN, responseMsg.getString("token"));
//										switchActivity(Regist3Activity.class,null);
										finish();
										ToastUtil.toastshort(context, "请上传头像");
									}else{
										ToastUtil.toastshort(context, error);
									}
									
								}
							
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						endLoading();
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString, throwable);
						throwable.printStackTrace();
						endLoading();
						Toast.makeText(context, "请检查网络!", Toast.LENGTH_SHORT).show();
					}
				});
	}
}
