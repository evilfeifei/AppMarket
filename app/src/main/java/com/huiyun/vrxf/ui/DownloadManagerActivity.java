package com.huiyun.vrxf.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.vrxf.MyApplication;
import com.huiyun.vrxf.R;
import com.huiyun.vrxf.adapter.DownloadManagerAdapter;
import com.huiyun.vrxf.adapter.DownloadedAdapter;
import com.huiyun.vrxf.been.AppInfo;
import com.huiyun.vrxf.db.buss.DBBuss;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class DownloadManagerActivity extends BaseActivity implements OnClickListener{

	private ListView lv_download;
	private List<AppInfo> mAppInfoList;
	private DownloadManagerAdapter mDownloadManagerAdapter;
	
	private ListView lv_downloaded;
	private List<AppInfo> mDownloadedAppInfoList;
	private DownloadedAdapter mDownloadedAdapter;
	
	private TextView t_title;
	public final static int PROGRESS_STATE_CHANGE = 1;
	private AppInfo mAppInfo;
	private LinearLayout back_left_liner;
	
	private MyApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_manager);
		initView();
		
		app = MyApplication.getInstance();
	}
	
	@SuppressWarnings("static-access")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		PreferenceCode.IS_DESTORY = true;
		if(mAppInfoList != null){
			for(AppInfo appInfo : mAppInfoList){
				appInfo.setTag(true);
			}
		}
	}

	private void initView() {
		t_title = findView(R.id.t_title);
		t_title.setText("下载管理");
		back_left_liner = findView(R.id.back_left_liner);
		back_left_liner.setOnClickListener(this);
		back_left_liner.setVisibility(View.VISIBLE);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
//		initBottomMenu();
		initDownLoadListView();
		initDownloadedData();
		if (bundle != null) {
			if(bundle.get(PreferenceCode.APP_INFO) != null){
				mAppInfo = (AppInfo)bundle.get(PreferenceCode.APP_INFO);
				initData();
			}
		}
	}

	private void initDownloadedData() {
		mAppInfoList = new ArrayList<AppInfo>();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null){
			if(bundle.getString(PreferenceCode.DOWNLOAD_MANAGER) != null){
				List<AppInfo> downloadingAppInfoList = DBBuss.getInstance(this).getAllDownloadingAppInfoList();
				if(downloadingAppInfoList != null){
					mAppInfoList.addAll(downloadingAppInfoList);
					mDownloadManagerAdapter.notifyDataSetChanged(mAppInfoList);
				}
				
				mDownloadedAppInfoList = new ArrayList<AppInfo>();
				mDownloadedAppInfoList.addAll(DBBuss.getInstance(this).getAllDownloadedAppInfoList());
				mDownloadedAdapter.notifyDataSetChanged(mDownloadedAppInfoList);
//				if (mDownloadedAppInfoList.size() < 1) {
//					findView(R.id.lin_no_data).setVisibility(View.VISIBLE);
//				} else {
//					findView(R.id.lin_no_data).setVisibility(View.GONE);
//				}
			}
		}
		
	}

	private void initDownLoadListView() {
		lv_download = (ListView)findView(R.id.lv_download);
		mDownloadManagerAdapter = new DownloadManagerAdapter(this);
		lv_download.setAdapter(mDownloadManagerAdapter);
		
		lv_downloaded = (ListView)findView(R.id.lv_downloaded);
		mDownloadedAdapter = new DownloadedAdapter(this);
		lv_downloaded.setAdapter(mDownloadedAdapter);
	}
	
	public void initData(){
		AppInfo downloadApp = DBBuss.getInstance(this).getAllDownloadedAppInfo(mAppInfo.getId());
		if(downloadApp == null){
			//没有下载完成
			AppInfo newAppInfo = DBBuss.getInstance(this).getAppInfo(mAppInfo.getId());
			if(newAppInfo != null){
				mAppInfo = newAppInfo;
			}
//			List<AppInfo> downloadingAppInfoList = DBBuss.getInstance(this).getAllDownloadingAppInfoList();
//			if(downloadingAppInfoList != null){
//				mAppInfoList.addAll(downloadingAppInfoList);
//			}
//			boolean tag = true;
//			for(AppInfo appInfo : mAppInfoList){
//				if(appInfo.getId().equals(mAppInfo.getId())){
//					tag = false;
//					break;
//				}
//			}
//			if(tag){
//				mAppInfoList.add(mAppInfo);
//			}
			mAppInfoList.add(mAppInfo);
			getAddDownloadCount(mAppInfo.getId());
		}else{
			mDownloadedAppInfoList = new ArrayList<AppInfo>();
			mDownloadedAppInfoList.add(downloadApp);
			mDownloadedAdapter.notifyDataSetChanged(mDownloadedAppInfoList);
		}
		
		
		mDownloadManagerAdapter.notifyDataSetChanged(mAppInfoList);
//		mProgressHandler.postDelayed(mProgressRunnable, 500);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()){
		case R.id.back_left_liner:
			finish();
			break;
		}
	}
	
	Runnable mProgressRunnable = new Runnable() {
		public void run() {
			Message msg = mProgressHandler.obtainMessage(PROGRESS_STATE_CHANGE);
			mProgressHandler.handleMessage(msg);
			mProgressHandler.postDelayed(this, 500);
		}
	};
	
	private final Handler mProgressHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case PROGRESS_STATE_CHANGE:
					AppInfo mAppInfo = new AppInfo();
					int num = (int)(1 + Math.random() * 100);
					mAppInfo.setDownloadProgress(num);
					mAppInfoList.set(0, mAppInfo);
					mDownloadManagerAdapter.notifyDataSetChanged(mAppInfoList);
					break;
	
				default:
					break;
			}
		};
	};
	
	public void getAddDownloadCount( String appId){
	RequestParams rp = new RequestParams();
	rp.put("appId", appId);
	ahc.post(this, Constant.ADDDOWNLOAD_COUNT, rp,
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
						}else{
							System.out.println("下载数量加1");
							}
						}catch (JSONException e) {
							e.printStackTrace();
						}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString,
						throwable);
				Toast.makeText(DownloadManagerActivity.this, "请检查网络!",
						Toast.LENGTH_LONG).show();
			}
		});
}
	
	

}
