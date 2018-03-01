package com.huiyun.vrxf.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.util.CodeUtil;
import com.huiyun.vrxf.util.NetworkUtil;
import com.huiyun.vrxf.util.PhoneUtil;
import com.huiyun.vrxf.util.ToastUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ForgetPwdActivity extends BaseActivity implements OnClickListener {

	private TextView login_certify, login_certifycode, title;
	private EditText phoneNumber;
	private EditText password;
	private String phoneNumString, passwordString;
	private final int GET_PWD = 1;
	private final int GET_PWD_AGAIN = 2;
	private final int GET_PHNOENUMBER_EXIT = 3;
	private LinearLayout back_left_liner;
	private Context context;
	private String ranNum;
	private String certifycode;
	private TextView regist_1;
	Dialog dialog;
	/**
	 * 等待的秒数
	 */
	int count;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_PWD:
				login_certify.setEnabled(false);
				login_certify.setText("" + count + "秒");
				break;
			case GET_PWD_AGAIN:
				login_certify.setEnabled(true);
				login_certify.setText("重新获取");
				break;
			case GET_PHNOENUMBER_EXIT:
				String returnString = (String) msg.obj;
				JSONArray jsonArray = null;
				try {
					if (returnString != null && !returnString.equals("")) {
						jsonArray = new JSONArray(returnString);
						if (jsonArray.length() > 0) {
							dialog.dismiss();
							Toast.makeText(context, "您的号码已经注册过了!", Toast.LENGTH_SHORT).show();
						} else {
							Bundle bundle = new Bundle();
							bundle.putString("phoneNumString", phoneNumString);
							bundle.putString("passwordString", passwordString);
							// switchActivity(RegisterActivity.class, bundle);
							dialog.dismiss();
							finish();
						}
					} else {
						dialog.dismiss();
						Toast.makeText(context, "请检查网络是否异常!", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		context = this;

		initView();

	}

	private void initView() {
		title = findView(R.id.t_title);
		title.setText("忘记密码");
		back_left_liner = findView(R.id.back_left_liner);
		back_left_liner.setOnClickListener(this);
		back_left_liner.setVisibility(View.VISIBLE);
		regist_1 = (TextView) this.findViewById(R.id.regist_1);
		regist_1.setOnClickListener(this);
		login_certify = (TextView) this.findViewById(R.id.login_certify);
		login_certifycode = (TextView) this
				.findViewById(R.id.login_certifycode);

//		login_certifycode.setKeyListener(null);
		login_certify.setOnClickListener(this);
		login_certify.setText("获取验证码");

		phoneNumber = (EditText) findViewById(R.id.login_num);
		password = (EditText) findViewById(R.id.login_pwd);
		phoneNumber.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		
		if(PhoneUtil.getPhoneNumber(this) != null){
			phoneNumber.setText(PhoneUtil.getPhoneNumber(this));
		}
	}

	@Override
	public void onClick(View v) {
		phoneNumString = phoneNumber.getText().toString();
		passwordString = password.getText().toString();
		certifycode = login_certifycode.getText().toString();
		switch (v.getId()) {
		case R.id.regist_1:
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
				return;
			}
			if(passwordString.length()<6){
				ToastUtil.toastshort(this, "请设置最少6位字符密码");
				return;
			}
			if(TextUtils.isEmpty(certifycode)){
				ToastUtil.toastshort(this, "请输入验证码");
				return;
			}
			
			else{
				if(certifycode.equals(ranNum)){
					if(NetworkUtil.isNetworkConnected(this)){
						findPassword(phoneNumString, passwordString);
//						regist_1.setEnabled(false);
					}else{
						ToastUtil.toastshort(this, "当前无网络");
					}
				}else{
					ToastUtil.toastshort(this, "验证码不正确");
				}
			}
//			registUser(phoneNumString,passwordString);
			break;
		case R.id.login_certify:
			if(phoneNumString == null || phoneNumString.equals("")){
				ToastUtil.toastshort(this, "请输入手机号");
				return;
			}
			if(phoneNumString.length()!=11){
				ToastUtil.toastshort(this, "请输入正确的手机号码");
				return;
			}
			ranNum = CodeUtil.getFourCode();
			if (NetworkUtil.isNetworkConnected(this)) {
				getCode(phoneNumString, ranNum);
				startTimer();
			} else {
				ToastUtil.toastshort(this, "当前无网络");
			}
			break;
		case R.id.back_left_liner:
			this.finish();
			break;
		}
	}

	
	private void findPassword(final String userPhone, String passwd) {
		beginLoading(ForgetPwdActivity.this);
		RequestParams rp = new RequestParams();
		rp.put("phone", userPhone);
		rp.put("password", passwd);
		ahc.post(this, Constant.USER_FIND_PWD, rp,
				new JsonHttpResponseHandler(Constant.UNICODE) {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						regist_1.setEnabled(true);
						if(statusCode==200){
							//{"token":"b76d6b6fc7670d8c829a3ed4261ac33e","userId":1,"responseMsg":{"error":"","success":"S"}}
							JSONObject jsonObject = response;
							try {
								JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
								if(responseMsg.getString("success").equals("S")){
									Toast.makeText(ForgetPwdActivity.this,"密码重置成功，请重新登录",Toast.LENGTH_SHORT).show();
									finish();
								}else{
									String error =responseMsg.getString("error");
									ToastUtil.toastshort(context, error);
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
						regist_1.setEnabled(true);
						Toast.makeText(context, "请检查网络!", Toast.LENGTH_SHORT).show();
						endLoading();
					}
				});
	}

	private void getCode(final String phone, String code) {
		RequestParams rp = new RequestParams();
		rp.put("phone", phone);
		rp.put("code", code);
		ahc.post(this, Constant.USER_SEND_SMS, rp,
				new JsonHttpResponseHandler(Constant.UNICODE) {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						regist_1.setEnabled(true);
						Log.e("response",response.toString());
						if (statusCode == 200) {
							JSONObject jsonObject = response;
							try {
								JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
								if (responseMsg.getString("success").equals("S")) {
									startTimer();
								} else {
									String error = responseMsg.getString("error");
									ToastUtil.toastshort(context, error);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString, throwable);
						throwable.printStackTrace();
						regist_1.setEnabled(true);
//						Toast.makeText(context, "请检查网络!", 1).show();
					}
				});
	}

	private void startTimer() {
		count = 120;
		// 获取验证码
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (count > 0) {
					Message msg = handler.obtainMessage();
					msg.what = GET_PWD;
					handler.sendMessage(msg);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = GET_PWD_AGAIN;
					handler.sendMessage(msg);
					this.cancel();
				}
				count--;

			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 0, 1000);
	}

	
}
