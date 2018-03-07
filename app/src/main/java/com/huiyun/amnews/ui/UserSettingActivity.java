package com.huiyun.amnews.ui;

import java.io.File;

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
import android.content.Context;
import android.content.Intent;
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
	private Bitmap bitmap;
	private PopupWindow selectPopupWindow;
	private View mPopView;
	private TextView myaccount_phone_num;       //电话号码
	private RelativeLayout myaccount_user_phone;
	private RelativeLayout myaccount_shop_adress;   //收货地址

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

//		ImageLoaderUtil.getInstance().displayImg(imgHeadpictureSmall,
//				Constant.HEAD_URL + avatar, options);

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
			showSelectPhPopupWindow();
			break;
			case R.id.myaccount_shop_adress:    //收货地址
				break;
		default:
			break;
		}
	}


	// 从相册选择
	RelativeLayout layout_choose;
	PopupWindow layout_pop;

	private void showAvatarPop() {
		View viewLayoutView = LayoutInflater.from(this).inflate(
				R.layout.my_choice_head, null);
		layout_choose = (RelativeLayout) viewLayoutView
				.findViewById(R.id.layout_choose);
		// 从相册选择
		layout_choose.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_choose.setBackgroundColor(Color.WHITE);
				// layout_choose.setBackgroundDrawable(getResources().getDrawable(
				// R.drawable.pop_bg_press));
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, 2);
			}
		});
		layout_pop = new PopupWindow(viewLayoutView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layout_pop.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					layout_pop.dismiss();
					return true;
				}
				return false;
			}
		});
		layout_pop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		layout_pop.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
		layout_pop.setTouchable(true);
		layout_pop.setFocusable(true);
		layout_pop.setOutsideTouchable(true);
		layout_pop.setBackgroundDrawable(new BitmapDrawable());
		layout_pop.showAtLocation(myaccount_btn1, Gravity.CENTER, 0, 0);
	}


	private void getToken(final String userId, final Bitmap bitmap) {
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
							byte[] data = FormatTools.getInstance()
									.Bitmap2Bytes(bitmap);
							key = GetUuid.getUuid().toString();
							if (NetworkUtil.isNetworkConnected(UserSettingActivity.this)) {
								uploadImage(data, key, qiniutoken, context);
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

	public void uploadImage(byte[] data, String key, final String qiniutoken,
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


	public void showSelectPhPopupWindow(){
		if(mPopView != null){
			mPopView = null;
		}
		mPopView=LayoutInflater.from(this).inflate(R.layout.popup_select_photo, null);
		if(selectPopupWindow == null){
			selectPopupWindow = new PopupWindow(mPopView,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		selectPopupWindow.setOutsideTouchable(true);
		selectPopupWindow.setFocusable(true);
		selectPopupWindow.setTouchable(true);
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		selectPopupWindow.showAtLocation(mPopView, Gravity.CENTER, 0, 0);
		
		RelativeLayout selectPhBtn = (RelativeLayout)mPopView.findViewById(R.id.select_photos_btn);
		RelativeLayout takePhBtn = (RelativeLayout)mPopView.findViewById(R.id.take_photos_btn);
		selectPhBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectPopupWindow.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						"image/*");
				startActivityForResult(intent, 1);
			}
		});
		takePhBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectPopupWindow.dismiss();
				Intent intent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				//下面这句指定调用相机拍照后的照片存储的路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
						.fromFile(new File(Environment
								.getExternalStorageDirectory(),
								"xiaoma.jpg")));
				startActivityForResult(intent, 2);
			}
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 如果是直接从相册获取
		case 1:
			if(data.getData()!=null){
				startPhotoZoom(data.getData());
			}
			break;
		// 如果是调用相机拍照时
		case 2:
			File temp = new File(Environment.getExternalStorageDirectory()
					+ "/xiaoma.jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		// 取得裁剪后的图片
		case 3:
			/**
			 * 非空判断大家一定要验证，如果不验证的话，
			 * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
			 * 当前功能时，会报NullException，小马只
			 * 在这个地方加下，大家可以根据不同情况在合适的
			 * 地方做判断处理类似情况
			 * 
			 */
			if(data != null){
				setPicToView(data);
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
		 * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
		 * 制做的了...吼吼
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}
	
	/**
	 * 保存裁剪之后的图片数据
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			bitmap = extras.getParcelable("data");
			//Drawable drawable = new BitmapDrawable(photo);
			
			/**
			 * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上
			 * 传到服务器，QQ头像上传采用的方法跟这个类似
			 */
			
			/*ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			byte[] b = stream.toByteArray();
			// 将图片流以字符串形式存储下来
			
			tp = new String(Base64Coder.encodeLines(b));
			这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，
			服务器处理的方法是服务器那边的事了，吼吼
			
			如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
			为我们可以用的图片类型就OK啦...吼吼
			Bitmap dBitmap = BitmapFactory.decodeFile(tp);
			Drawable drawable = new BitmapDrawable(dBitmap);
			*/
			imgHeadpictureSmall.setImageBitmap(bitmap);
			if(NetworkUtil.isNetworkConnected(this)){
				beginLoading(UserSettingActivity.this);
				getToken(userId, bitmap);
			}else{
				ToastUtil.toastshort(this, "当前无网络");
			}
		}
	}
}
