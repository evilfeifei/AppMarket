package com.huiyun.amnews.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.huiyun.amnews.Constants;
import com.huiyun.amnews.R;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.myview.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity implements OnClickListener{
	protected int x, y;
	protected LayoutInflater inflater;
	private Context context;
	protected  AsyncHttpClient ahc;
	protected  String userId;
	protected String userPhoneNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		ahc = new AsyncHttpClient();
		inflater = LayoutInflater.from(context);
		userId = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.USERID);
		userPhoneNum = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.PHONE);
		MobclickAgent.setDebugMode(true);
		MobclickAgent.setScenarioType(BaseActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
	}

	public <T extends View> T findView(int id) {
		return (T) findViewById(id);
	}
	
	public <T extends View> T findView(View view,int id) {
		return (T) view.findViewById(id);
	}
	
	 protected void switchActivity(Class<?> clazz,Bundle bundle){
		 Intent intent=new Intent(this, clazz);
		 if(bundle!=null){
			 intent.putExtras(bundle);
		 }
		 startActivity(intent);
	}
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
		}
		
	}
	protected static String getError(String info){
		Map<String,String> map=new HashMap<String,String>();
        String str = info;
        String[] arr = str.substring(1, str.length() - 1).split(",");
        for(int i = 0;i < arr.length;i++){
        	String[] v = arr[i].split(":");
        	map.put(v[0], v[1]);
        }
       return map.get("error");
	}
	
	public File getAppDownloadPath(String name) {
		String EXTERN_PATH = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) == true) {
			EXTERN_PATH = Constants.BASE_APP_DOWNLOAD_PATH;
			File f = new File(EXTERN_PATH);
			if (!f.exists()) {
				f.mkdirs();
			}
		}
		return new File(EXTERN_PATH + name);
	}

	private LoadingDialog processDialog;

	protected void beginLoading(String msg,Context context ) {
		if (processDialog == null || !processDialog.isShowing()) {
			processDialog = LoadingDialog.createDialog(context);
			processDialog.setCancelable(true);
			processDialog.setMessage(msg);
			processDialog.show();
		}
	}

	protected void endLoading() {
		if (processDialog != null && processDialog.isShowing()&&processDialog.getWindow()!=null) {
			processDialog.dismiss();
			processDialog = null;
		}
	}

	protected void beginLoading(Context context) {
		if (processDialog == null || !processDialog.isShowing()) {
			processDialog = LoadingDialog.createDialog(context);
			processDialog.setCancelable(true);
			processDialog.show();
		}
	}

	protected void showImg(Context mContext, String path, ImageView imageView,int defaultId){
		Glide.with(mContext)
				.load(path)
				.placeholder(defaultId)
				.dontAnimate()
				.into(imageView);
	}


	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected void showImg(Activity activity, String path, ImageView imageView, int defaultId) {
		if(activity==null){
			return;
		}
		try {
			if (Util.isOnMainThread()) {
				Glide.with(activity)
						.load(path)
						.placeholder(defaultId)
						.dontAnimate()
						.into(imageView);
			}
		}catch (Exception m){}
	}
}
