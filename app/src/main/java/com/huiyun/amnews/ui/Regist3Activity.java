package com.huiyun.amnews.ui;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.util.FormatTools;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.NetworkUtil;
import com.huiyun.amnews.util.ToastUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

public class Regist3Activity extends BaseActivity implements OnClickListener{
	private LinearLayout back_left_liner;
	private TextView  title,regist_3;
	private ImageView add_head;
	private String path = "";
	private Context context;
	private static String token;
	private static final int REQUEST_CODE_CHOOSE = 23;


	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 222:
					String result = (String) msg.obj;
					if(!TextUtils.isEmpty(result)){
						Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(result);
						if(dataMap!=null){
							String url = dataMap.get("url").toString();
							if(!TextUtils.isEmpty(url)){
								saveUserAvatar(url);
							}
						}
					}
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		userId = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.USERID);
		token = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.TOKEN);
		
		
		setContentView(R.layout.activity_register3);
		initView();
	}

	private void initView() {
		title = findView(R.id.t_title);
		title.setText("注册");
		back_left_liner = findView(R.id.back_left_liner);
		regist_3 = findView(R.id.regist_3);
		add_head = findView(R.id.add_head);
		add_head.setOnClickListener(this);
		back_left_liner.setOnClickListener(this);
		regist_3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_left_liner:
			this.finish();
			break;
		case R.id.add_head:
			startCamcer();
			break;
		case R.id.regist_3:
			if(TextUtils.isEmpty(path)){
				ToastUtil.toastshort(context, "请选择图片");
				return;
			}else{
				String userId = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.USERID);
				if(userId.equals("")){
					return ;
				}else{
					if(NetworkUtil.isNetworkConnected(this)){
						regist_3.setEnabled(false);
						beginLoading(Regist3Activity.this);
					}else{
						ToastUtil.toastshort(this, "当前无网络");
					}
				}
			}
			break;
		}
	}

	private void startCamcer(){
		Matisse.from(Regist3Activity.this)
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
				showImg(Regist3Activity.this,path,add_head,0);
				uploadImg(path);
			}
		}
	}


	private String fileName;  //报文中的文件名参数
	private void uploadImg(final String mImagePath){
		new Thread(new Runnable() {
			@Override
			public void run() {
				fileName = System.currentTimeMillis() + ".jpg";  //报文中的文件名参数
				String end = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";
				try {
					URL url = new URL(Constant.UPLOAD_FILE_URL);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setUseCaches(false);
					con.setRequestMethod("POST");
					con.setRequestProperty("Connection", "Keep-Alive");
					con.setRequestProperty("Charset", "UTF-8");
					con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
					StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
					DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection
					ds.writeBytes(twoHyphens + boundary + end);
					ds.writeBytes("Content-Disposition: form-data; " +
							"name=\"file\";filename=\"" +
							fileName + "\"" + end);
					ds.writeBytes(end);
					FileInputStream fStream = new FileInputStream(mImagePath);
					int bufferSize = 8192;
					byte[] buffer = new byte[bufferSize];   //8k
					int length = -1;
					while ((length = fStream.read(buffer)) != -1) {
						ds.write(buffer, 0, length);
					}
					ds.writeBytes(end);
					ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
					fStream.close();
					ds.close();
					BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
					String line = "";
					String result = "";
					while (null != (line = br.readLine())) {
						result += line;
					}
					Log.e("接到的数据: " , result);
					Message message = new Message();
					message.what = 222;
					message.obj = result;
					myHandler.sendMessage(message);
					br.close();

				} catch (Exception e) {
				}
			}
		}).start();

	}

		
		public void saveUserAvatar(final String path){
			RequestParams rp = new RequestParams();
			rp.put("userId", userId);
			rp.put("token", token);
			rp.put("avatar", path);
			ahc.post(context, Constant.REGISTER3_URL, rp,
				new JsonHttpResponseHandler(Constant.UNICODE) {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						regist_3.setEnabled(true);
						if(statusCode==200){
							JSONObject jsonObject = response;
							try {
								JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
								if(responseMsg.getString("success").equals("S")){
									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.AVATAR, path);
									finish();
									if(LoginActivity.loginActivity!=null){
										LoginActivity.loginActivity.finish();
									}
								}else{
									String error =responseMsg.getString("error");
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
						regist_3.setEnabled(true);
						Toast.makeText(context, "请检查网络!", 1).show();
					}
				});
		}
}
