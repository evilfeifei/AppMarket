package com.huiyun.amnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.downLoad.LogDownloadListener;
import com.huiyun.amnews.downLoad.OkDownLoad;
import com.huiyun.amnews.event.DownLoadFinishEvent;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.AppDettailsActivity2;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.util.ApkUtils;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.view.roundimage.RoundedImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.db.DownloadDBManager;
import com.lzy.okserver.listener.DownloadListener;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.huiyun.amnews.R.id.downloadSize;
import static com.huiyun.amnews.R.id.netSpeed;

public class AppAdapter extends BaseAdapter{

	private List<AppInfo> appBeans;
	private Context mContext;
	protected AsyncHttpClient ahc;

	public AppAdapter(Context context){
		this.mContext = context;
		appBeans = new ArrayList<AppInfo>();
		if(ahc==null){
			ahc = new AsyncHttpClient();
		}
	}

	public AppAdapter(Context context, List<AppInfo> appBeans){
		this.appBeans = appBeans;
		this.mContext = context;
		if(ahc==null){
			ahc = new AsyncHttpClient();
		}
	}

	public void refreshData(List<AppInfo> appBeans) {
		this.appBeans = appBeans;
		notifyDataSetChanged();
	}

	public void myNotifyDataSetChanged(){
		if(appBeans==null)return;
		for(AppInfo appInfo:appBeans){
			if (ApkUtils.isAvailable(mContext, appInfo.getPackage_name())) { //已安装
				appInfo.setAvailable(true);
				if(ApkUtils.isUpdate(mContext,appInfo.getPackage_name(),appInfo.getVersion())){ //x需要升级
					appInfo.setUpdate(true);
				}else{
					appInfo.setUpdate(false);
				}
			}else{
				appInfo.setAvailable(false);
			}
		}
		notifyDataSetChanged();
	}

	public int getCount() {
		return appBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		final int index = position;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		holder.nameTv.setText(appBeans.get(position).getName());
		holder.contentTv.setText(appBeans.get(position).getIntroduction());

		DecimalFormat df = new DecimalFormat("#.0");
		double x = Double.valueOf(appBeans.get(position).getSize()) / 1024 / 1024;
		String size = df.format(x);
		holder.sizeTv.setText(size + "M");
		holder.downCountTv.setText(appBeans.get(position).getDownload_count()+"次下载");

//		if (!ApkUtils.isAvailable(mContext, appBeans.get(index).getPackage_name())) {
		if (!appBeans.get(index).isAvailable()) {
			if (OkDownLoad.getInstance().getManger().getDownloadInfo(appBeans.get(index).getDownloadUrl()) != null) {
				DownloadInfo downloadInfo = OkDownLoad.getInstance().getManger().getDownloadInfo(appBeans.get(index).getDownloadUrl());
				if (downloadInfo.getState() == DownloadManager.FINISH) {
					if (ApkUtils.isAvailable(mContext, ((AppInfo) downloadInfo.getData()).getPackage_name())) {
						holder.downloadTv.setText("打开");
					} else {
						holder.downloadTv.setText("安装");
					}
				}
			} else {
				holder.downloadTv.setText("下载");
			}
		}else{
//			if(ApkUtils.isUpdate(mContext,appBeans.get(index).getPackage_name(),appBeans.get(index).getVersion())){
			if(appBeans.get(index).isUpdate()){
				holder.downloadTv.setText("升级");
			}else{
				holder.downloadTv.setText("打开");
			}
		}

		Glide.with(mContext)
				.load(appBeans.get(position).getThumbnailName())
				.placeholder(R.drawable.vr_default_img)
				.dontAnimate()
				.into(holder.appIcon);

		final ViewHolder finalHolder = holder;
		holder.downloadTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!ApkUtils.isAvailable(mContext, appBeans.get(index).getPackage_name())) {
					if (OkDownLoad.getInstance().getManger().getDownloadInfo(appBeans.get(index).getDownloadUrl()) != null) {
						DownloadInfo downloadInfo = OkDownLoad.getInstance().getManger().getDownloadInfo(appBeans.get(index).getDownloadUrl());
						if (downloadInfo.getState() == DownloadManager.FINISH) { //已经下载完成
							if (ApkUtils.isAvailable(mContext, ((AppInfo) downloadInfo.getData()).getPackage_name())) {
								ApkUtils.openApp(mContext, ((AppInfo) downloadInfo.getData()).getPackage_name());
							} else {
								ApkUtils.install(mContext, new File(downloadInfo.getTargetPath()));
							}
						} else {
							ToastUtil.toastshort(mContext, "已添加到下载队列");
						}
					} else {
						addDownLoad(appBeans.get(index));
//						addDownLoad(appBeans.get(index), finalHolder);
						if (!AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.USERID).equals("")) {
							receiveScore(mContext, AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.USERID),
									AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.TOKEN));
						}
					}
				}else{
					//需要升级
					if(ApkUtils.isUpdate(mContext,appBeans.get(index).getPackage_name(),appBeans.get(index).getVersion())){
						addDownLoad(appBeans.get(index));
//						addDownLoad(appBeans.get(index), finalHolder);
					}else{
						ApkUtils.openApp(mContext, appBeans.get(position).getPackage_name());
					}
				}
			}
		});

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AppDettailsActivity2.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(PreferenceCode.APP_INFO, appBeans.get(index));
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}

	private class ViewHolder
	{
		private DownloadInfo downloadInfo;
		TextView nameTv,sizeTv,downCountTv,contentTv,downloadTv;
		RoundedImageView appIcon;

		public ViewHolder(View convertView) {
			appIcon = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
			nameTv = (TextView)convertView.findViewById(R.id.name_tv);
			sizeTv = (TextView)convertView.findViewById(R.id.size_tv);
			downCountTv = (TextView)convertView.findViewById(R.id.down_count_tv);
			contentTv = (TextView)convertView.findViewById(R.id.content_tv);
			downloadTv = (TextView)convertView.findViewById(R.id.tv_download);
		}

		public void refresh(DownloadInfo downloadInfo) {
			this.downloadInfo = downloadInfo;
			refresh();
		}

		//对于实时更新的进度ui，放在这里，例如进度的显示，而图片加载等，不要放在这，会不停的重复回调
		//也会导致内存泄漏
		private void refresh() {
			String downloadLength = Formatter.formatFileSize(mContext, downloadInfo.getDownloadLength());
			String totalLength = Formatter.formatFileSize(mContext, downloadInfo.getTotalLength());
//			downloadTv.setText(downloadLength + "/" + totalLength);
			if (downloadInfo.getState() == DownloadManager.NONE) {
				downloadTv.setText("下载");
			} else if (downloadInfo.getState() == DownloadManager.PAUSE) {
				downloadTv.setText("继续");
			} else if (downloadInfo.getState() == DownloadManager.ERROR) {
				downloadTv.setText("出错");
			} else if (downloadInfo.getState() == DownloadManager.WAITING) {
				downloadTv.setText("等待");
			} else if (downloadInfo.getState() == DownloadManager.FINISH) {
//				if (ApkUtils.isAvailable(DownloadManagerActivity.this, new File(downloadInfo.getTargetPath()))) {
				if (ApkUtils.isAvailable(mContext, ((AppInfo)downloadInfo.getData()).getPackage_name())) {
					downloadTv.setText("卸载");
				} else {
					downloadTv.setText("安装");
				}
			} else if (downloadInfo.getState() == DownloadManager.DOWNLOADING) {
				String networkSpeed = Formatter.formatFileSize(mContext, downloadInfo.getNetworkSpeed());
				downloadTv.setText("暂停");
			}
			downloadTv.setText((Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 100) + "%");
		}
	}

	private void addDownLoad(AppInfo appInfo){
		GetRequest request = OkGo.get(appInfo.getDownloadUrl());
		OkDownLoad.getInstance().getManger().addTask(appInfo.getName() + ".apk", appInfo, appInfo.getDownloadUrl(), request, new LogDownloadListener());
		Intent intent = new Intent(mContext, DownloadManagerActivity.class);
		mContext.startActivity(intent);
	}

	private void addDownLoad(AppInfo appInfo,ViewHolder holder){
		GetRequest request = OkGo.get(appInfo.getDownloadUrl());
		DownloadListener downloadListener = new MyDownloadListener();
		downloadListener.setUserTag(holder);
		OkDownLoad.getInstance().getManger().addTask(appInfo.getName() + ".apk", appInfo, appInfo.getDownloadUrl(), request, new LogDownloadListener());
//		Intent intent = new Intent(mContext, DownloadManagerActivity.class);
//		mContext.startActivity(intent);
		DownloadInfo downloadInfo = OkDownLoad.getInstance().getManger().getDownloadInfo(appInfo.getDownloadUrl());
		holder.refresh(downloadInfo);
		downloadInfo.setListener(downloadListener);
	}

	protected void switchActivity(Class<?> clazz,Bundle bundle){
		Intent intent=new Intent(mContext, clazz);
		if(bundle!=null){
			intent.putExtras(bundle);
		}
		mContext.startActivity(intent);
	}

	//下载送积分
	public void receiveScore(Context context,String userId,String token) {
		RequestParams rp = new RequestParams();
		rp.put("userId", userId);
		rp.put("token", token);
		ahc.post(context, Constant.RECEIVE_SCORE, rp,
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
//									ToastUtil.toastshort(AppDettailsActivity2.this, error);
								} else {
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
					}
				});
	}

	private class MyDownloadListener extends DownloadListener {

		@Override
		public void onProgress(DownloadInfo downloadInfo) {
			if (getUserTag() == null) return;
			AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) getUserTag();
			holder.refresh();  //这里不能使用传递进来的 DownloadInfo，否者会出现条目错乱的问题
		}

		@Override
		public void onFinish(DownloadInfo downloadInfo) {
			String packageName = ApkUtils.getPackageName(mContext, downloadInfo.getTargetPath());
			AppInfo appInfo = (AppInfo) downloadInfo.getData();
			appInfo.setPackage_name(packageName);
			downloadInfo.setData(appInfo);
			DownloadDBManager.INSTANCE.replace(downloadInfo);
			Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();
			EventBus.getDefault().post(new DownLoadFinishEvent());

//			ApkUtils.install(DownloadManagerActivity.this, new File(downloadInfo.getTargetPath()));
		}

		@Override
		public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
			if (errorMsg != null) Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
		}
	}
}
