package com.huiyun.amnews.ui;

import com.huiyun.amnews.Constants;
import com.huiyun.amnews.MainActivity;
import com.huiyun.amnews.R;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.configuration.DefaultValues;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.myview.dialogview.DialogTips;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.MyProvider;
import com.huiyun.amnews.util.PhoneUtils;
import com.huiyun.amnews.util.ToastUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SettingActivity extends BaseActivity implements OnClickListener{
	
	private CheckBox wifi_set_check,
	                 cache_clean_check,
	                 langeuage_check,
	                 install_check,
	                 root_install_check,
	                 apk_path_check;
	private RelativeLayout net_set_relate,user_set_relate,about_us_relate;
	private Context context;
	private TextView logout_text,title,flow_type;
	private LinearLayout back_left_liner;
	private PopupWindow selectPopupWindow;
	private View mPopView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context  = this;
		
		setContentView(R.layout.activity_setting);
		initView();
		addListener();
	}


	
	private void initView() {
		title = findView(R.id.t_title);
		title.setText("设置");
		back_left_liner = findView(R.id.back_left_liner);
		back_left_liner.setOnClickListener(this);
		back_left_liner.setVisibility(View.VISIBLE);
		wifi_set_check = findView(R.id.wifi_set_check);
		cache_clean_check = findView(R.id.cache_clean_check);
		langeuage_check = findView(R.id.langeuage_check);
		install_check = findView(R.id.install_check);
		root_install_check = findView(R.id.root_install_check);
		apk_path_check = findView(R.id.apk_path_check);
		flow_type = findView(R.id.flow_type);
		logout_text = findView(R.id.logout_text);
		logout_text.setOnClickListener(this);
		user_set_relate = findView(R.id.user_set_relate);
		user_set_relate.setOnClickListener(this);
		
		about_us_relate = findView(R.id.about_us_relate);
		about_us_relate.setOnClickListener(this);
		
		
		
		wifi_set_check.setChecked(AppmarketPreferences.getInstance(context).getBooleanKey(PreferenceCode.WIFI_SET_CHECK));
		cache_clean_check.setChecked(AppmarketPreferences.getInstance(context).getBooleanKey(PreferenceCode.CACHE_CLEAN_CHECK));
		langeuage_check.setChecked(AppmarketPreferences.getInstance(context).getBooleanKey(PreferenceCode.LANGEUAGE_CHECK));
		install_check.setChecked(AppmarketPreferences.getInstance(context).getBooleanKey(PreferenceCode.INSTALL_CHECK));
		root_install_check.setChecked(AppmarketPreferences.getInstance(context).getBooleanKey(PreferenceCode.ROOT_INSTALL_CHECK));
		apk_path_check.setChecked(AppmarketPreferences.getInstance(context).getBooleanKey(PreferenceCode.APK_PATH_CHECK));
		
		flow_type.setText(AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.FLOW_TYEP));
		
		
		net_set_relate = findView(R.id.net_set_relate);
		net_set_relate.setOnClickListener(this);

		findView(R.id.update_relate).setOnClickListener(this);
		((TextView)findViewById(R.id.version_tv)).setText("v"+ PhoneUtils.getVersionName(SettingActivity.this));
	}
	
	private void addListener() {
		wifi_set_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppmarketPreferences.getInstance(context).putBooleanKey(PreferenceCode.WIFI_SET_CHECK, isChecked);
			}
		});
		cache_clean_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppmarketPreferences.getInstance(context).putBooleanKey(PreferenceCode.CACHE_CLEAN_CHECK, isChecked);
			}
		});
		langeuage_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppmarketPreferences.getInstance(context).putBooleanKey(PreferenceCode.LANGEUAGE_CHECK, isChecked);
			}
		});
		install_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppmarketPreferences.getInstance(context).putBooleanKey(PreferenceCode.INSTALL_CHECK, isChecked);
			}
		});
		root_install_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppmarketPreferences.getInstance(context).putBooleanKey(PreferenceCode.ROOT_INSTALL_CHECK, isChecked);
			}
		});
		apk_path_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AppmarketPreferences.getInstance(context).putBooleanKey(PreferenceCode.APK_PATH_CHECK, isChecked);
			}
		});
		
	}
	public void showClickPopupWindow() {
		if (mPopView != null) {
			mPopView = null;
		}
		mPopView = LayoutInflater.from(this).inflate(R.layout.popup_collect,
				null);
		if (selectPopupWindow == null) {
			selectPopupWindow = new PopupWindow(mPopView,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		selectPopupWindow.setOutsideTouchable(true);
		selectPopupWindow.setFocusable(true);
		selectPopupWindow.setTouchable(true);
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		selectPopupWindow.showAtLocation(mPopView, Gravity.CENTER, 0, 0);

		RelativeLayout relat_5 = (RelativeLayout) mPopView
				.findViewById(R.id.relat_5);
		RelativeLayout relat_10 = (RelativeLayout) mPopView
				.findViewById(R.id.relat_10);
		RelativeLayout relat_20 = (RelativeLayout) mPopView
				.findViewById(R.id.relat_20);
		RelativeLayout relat_50 = (RelativeLayout) mPopView
				.findViewById(R.id.relat_50);
		RelativeLayout relat_100 = (RelativeLayout) mPopView
				.findViewById(R.id.relat_100);
		relat_5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flow_type.setText("5M");
				selectPopupWindow.dismiss();
				AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.FLOW_TYEP,"5M");
			}
		});
		relat_10.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flow_type.setText("10M");
				selectPopupWindow.dismiss();
				AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.FLOW_TYEP,"10M");
			}
		});
		relat_20.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flow_type.setText("20M");
				selectPopupWindow.dismiss();
				AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.FLOW_TYEP,"20M");
			}
		});
		relat_50.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flow_type.setText("50M");
				selectPopupWindow.dismiss();
				AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.FLOW_TYEP,"50M");
			}
		});
		relat_100.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flow_type.setText("100M");
				selectPopupWindow.dismiss();
				AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.FLOW_TYEP,"100M");
			}
		});
	}
	public void onClick(View v) {
	   switch(v.getId()){
	   case R.id.net_set_relate:
		   showClickPopupWindow();
		   break;
	   case R.id.logout_text:
		   if(AppmarketPreferences.getInstance(SettingActivity.this).getStringKey(PreferenceCode.USERID).equals("")){
			   switchActivity(LoginActivity.class,null);
		   }else {
			   AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.TOKEN, "");
			   AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.PHONE, "");
			   AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.USERID, "");
			   AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.AVATAR, "");
			   AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.NICKNAME, "");
			   switchActivity(LoginActivity.class, null);
			   finish();
		   }
		   break;
	   case R.id.user_set_relate:
		   switchActivity(UserSettingActivity.class, null);
		   break;
	   case R.id.back_left_liner:
		   finish();
		   break;
	   case R.id.about_us_relate:
		   switchActivity(AboutusActivity.class, null);
		   break;
		   case R.id.update_relate:
			   checkServerUpdate(PhoneUtils.getVersionName(SettingActivity.this));
			   break;
	   }
	}

	String userId;
	@Override
	protected void onResume() {
		super.onResume();
		userId = AppmarketPreferences.getInstance(SettingActivity.this).getStringKey(PreferenceCode.USERID);
		if(userId.equals("")){
			((TextView)findViewById(R.id.logout_text)).setText("登录");
		}else{
			((TextView)findViewById(R.id.logout_text)).setText("退出登录");
		}
	}

	public void checkServerUpdate(String version) {
		HashMap<String, Object> params = new HashMap<>();
		String jsonData = JsonUtil.objectToJson(params);
		OkGo.post(Constant.APP_UPDATE+version)
				.tag(this)
				.upJson(jsonData)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						if (TextUtils.isEmpty(s)) return;
						Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(s);
						String url  = dataMap.get("url").toString();
						if(url!=null&&!url.equals("")){
							isUpdate(url);
						}else{
							ToastUtil.toastshort(SettingActivity.this,"当前为最新版啦！");
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
					}
				});
	}

	ProgressDialog pBar;
	private void isUpdate(final String updateUrl){
		DialogTips dialog = new DialogTips(this,"检测到有新版本是否更新?","立即更新","取消", "温馨提示",false);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
//                Toast.makeText(MainActivity.this,"dddd",Toast.LENGTH_SHORT).show();
//                PhoneUtils.loadAppMarketPage(MainActivity.this, updateUrl);

				pBar = new ProgressDialog(SettingActivity.this);
				pBar.setTitle("正在下载");
				pBar.setMessage("请稍候...");
				pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				downFile(updateUrl);
			}
		});

		dialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		});
		// 显示确认对话框
		dialog.show();
	}

	private void downFile(String apkUrl){
		pBar.show();
		File file = new File(Environment.getExternalStorageDirectory(), DefaultValues.UPDATE_SAVENAME);
		if(file.exists()) {
			file.delete();
			Log.e("apk_file","删除旧apk");
		}
		OkGo.get(apkUrl).execute(new FileCallback(file.getName()) {
			@Override
			public void onSuccess(File file, Call call, Response response) {
				pBar.cancel();
				update(file);
			}
			@Override
			public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
				//这里回调下载进度(该回调在主线程,可以直接更新ui)
				int pro = (int) (progress*100);
				pBar.setProgress(pro);
				pBar.setMessage("请稍候"+pro+"%...");
			}

			@Override
			public void onError(Call call, Response response, Exception e) {
				super.onError(call, response, e);
			}
		});
	}

	void update(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data;
		// 判断版本大于等于7.0
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
			data = MyProvider.getUriForFile(SettingActivity.this, "com.huiyun.amnews.fileprovider", file);
			// 给目标应用一个临时授权
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			data = Uri.fromFile(file);
		}
		intent.setDataAndType(data, "application/vnd.android.package-archive");
		startActivity(intent);
	}

}
