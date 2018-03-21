package com.huiyun.amnews.ui;

import java.io.File;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.util.FormatTools;
import com.huiyun.amnews.util.GetUuid;
import com.huiyun.amnews.util.NetworkUtil;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.view.roundimage.RoundedImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserSettingActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout myaccount_btn1;// 头像
	private RelativeLayout myaccount_btn2;// 昵称
	private TextView title;
	private LinearLayout back_left_liner;
	private Context context;
	private TextView txtNickName;
	private String nickname = "";
	private String token;
	private String sex;
	private String userId;
	private String userPhoneNum;
	private String avatar;
	private Button security_new_ok;
	private RoundedImageView imgHeadpictureSmall;
	public String key;
	private String path = "";
	private PopupWindow selectPopupWindow;
	private View mPopView;
	private TextView myaccount_phone_num;       //电话号码
	private RelativeLayout myaccount_user_phone;
	private RelativeLayout myaccount_shop_adress;   //收货地址
	private static final int REQUEST_CODE_CHOOSE = 23;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);
		

		userId = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.USERID);
		token = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.TOKEN);
		avatar = AppmarketPreferences.getInstance(context).getStringKey(
				PreferenceCode.AVATAR);
		userPhoneNum = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.PHONE);

		if (userId == "") {
			switchActivity(LoginActivity.class, null);
			this.finish();
		} else {
			context = this;
			initView();
		}
	}

	private void initView() {
		title = findView(R.id.t_title);
		title.setText("用户设置");
		back_left_liner = findView(R.id.back_left_liner);
		back_left_liner.setVisibility(View.VISIBLE);
		back_left_liner.setOnClickListener(this);
		myaccount_btn1 = (RelativeLayout) findViewById(R.id.myaccount_btn1);
		myaccount_btn2 = (RelativeLayout) findViewById(R.id.myaccount_btn2);

		myaccount_btn1.setOnClickListener(this);
		myaccount_btn2.setOnClickListener(this);
		security_new_ok = (Button) findViewById(R.id.security_new_ok);
		security_new_ok.setOnClickListener(this);

		//add by  popovich
		myaccount_phone_num = (TextView) findViewById(R.id.myaccount_phone_num);
		myaccount_phone_num.setOnClickListener(this);
		myaccount_user_phone = (RelativeLayout) findViewById(R.id.myaccount_user_phone);
		myaccount_user_phone.setOnClickListener(this);
		myaccount_shop_adress = (RelativeLayout) findViewById(R.id.myaccount_shop_adress);
		myaccount_shop_adress.setOnClickListener(this);

		initData();

	}

	private void initData() {
		imgHeadpictureSmall = (RoundedImageView) findViewById(R.id.myaccount_headpicture_small);
		txtNickName = (TextView) findViewById(R.id.myaccount_nickname);

		Glide.with(UserSettingActivity.this)
				.load(Constant.HEAD_URL + avatar)
				.placeholder(R.drawable.touxiang)
				.dontAnimate()
				.into(imgHeadpictureSmall);

		myaccount_phone_num.setText(userPhoneNum);
	}

	@Override
	protected void onResume() {
		super.onResume();
		nickname = AppmarketPreferences.getInstance(context).getStringKey(
				PreferenceCode.NICKNAME);
		txtNickName.setText(nickname);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.back_left_liner:
			finish();
			break;
		case R.id.myaccount_btn2:
			switchActivity(SetNameActivity.class,null);
			break;
		case R.id.myaccount_btn1:
			startCamcer();
			break;
			case R.id.myaccount_shop_adress:    //收货地址
				break;
		default:
			break;
		}
	}

	private void startCamcer(){
		Matisse.from(UserSettingActivity.this)
			.choose(MimeType.of(MimeType.JPEG, MimeType.PNG))//选择mime的类型
//                    .choose(MimeType.allOf())
			.countable(true)//设置从1开始的数字
				.maxSelectable(1)//选择图片的最大数量限制
				.capture(true)//启用相机
				.captureStrategy(new CaptureStrategy(true, "com.huiyun.amnews.fileprovider"))//自定义FileProvider
			.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//屏幕显示方向
				.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size)) // 列表中显示的图片大小
			.thumbnailScale(0.85f) // 缩略图的比例
				.imageEngine(new PicassoEngine()) // 使用的图片加载引擎
			.theme(R.style.Matisse_Zhihu) // 黑色背景
				.forResult(REQUEST_CODE_CHOOSE); // 设置作为标记的请求码
}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
			List<String> paths = Matisse.obtainPathResult(data);
			if(paths!=null&&paths.size()>0){
				path = paths.get(0);
				showImg(UserSettingActivity.this,path,imgHeadpictureSmall,0);
				getToken(userId);
			}
		}
	}

	private void getToken(final String userId) {
		RequestParams rp = new RequestParams();
		ahc.post(this, Constant.QINIU_TOKEN, rp, new JsonHttpResponseHandler(
				Constant.UNICODE) {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
								  JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				if (statusCode == 200) {
					JSONObject jsonObject = response;
					try {
						JSONObject responseMsg = jsonObject
								.getJSONObject("responseMsg");
						if (responseMsg.getString("success").equals("S")) {
							String qiniutoken = responseMsg
									.getString("qiniuToken");
							key = GetUuid.getUuid().toString();
							if (NetworkUtil.isNetworkConnected(UserSettingActivity.this)) {
								uploadImage(path, key, qiniutoken, context);
							} else {
								ToastUtil.toastshort(UserSettingActivity.this, "当前无网络");
							}

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
				Toast.makeText(context, "请检查网络!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void uploadImage(String data, String key, final String qiniutoken,
			final Context context) {
		UploadManager uploadManager = new UploadManager();
		uploadManager.put(data, key, qiniutoken, new UpCompletionHandler() {
			public void complete(String key, ResponseInfo info,
								 JSONObject response) {
				Log.i("qiniu", key);
				Log.i("qiniu", info.toString());
				Log.i("qiniu", response.toString());
				if (getError(info.toString()).equals("null")) {
					endLoading();
					ToastUtil.toastshort(context, "上传成功！");
					if (NetworkUtil.isNetworkConnected(UserSettingActivity.this)) {
						changeAvatar();
					} else {
						ToastUtil.toastshort(UserSettingActivity.this, "当前无网络");
					}
				} else {
					ToastUtil.toastshort(context, getError(info.toString()));
					endLoading();
				}
			}

		}, null);
	}

	private void changeAvatar() {
		RequestParams rp = new RequestParams();
		rp.put("userId", userId);
		rp.put("token", token);
		rp.put("avatar", key);
		ahc.post(context, Constant.CHANGE_AVATAR_URL, rp,
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
									ToastUtil.toastshort(context, "修改成功");
									AppmarketPreferences.getInstance(context)
											.setStringKey(
													PreferenceCode.AVATAR, key);
								} else {
									String error = responseMsg
											.getString("error");
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
						super.onFailure(statusCode, headers, responseString,
								throwable);
						throwable.printStackTrace();
						endLoading();
						Toast.makeText(context, "请检查网络!", Toast.LENGTH_SHORT).show();
					}
				});
	}

}
