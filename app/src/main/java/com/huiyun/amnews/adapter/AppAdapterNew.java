package com.huiyun.amnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.downLoad.LogDownloadListener;
import com.huiyun.amnews.downLoad.OkDownLoad;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.DownloadManagerActivity;
import com.huiyun.amnews.util.ApkUtils;
import com.huiyun.amnews.view.roundimage.RoundedImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.download.DownloadManager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AppAdapterNew extends  RecyclerView.Adapter<AppAdapterNew.ViewHolder>{

	private List<AppInfo> appBeans;
	private Context mContext;
	protected AsyncHttpClient ahc;

	public AppAdapterNew(Context context){
		this.mContext = context;
		appBeans = new ArrayList<AppInfo>();
		if(ahc==null){
			ahc = new AsyncHttpClient();
		}
	}

	public AppAdapterNew(Context context, List<AppInfo> appBeans){
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

	@Override
	public int getItemCount() {
		return appBeans.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = null;
		View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
		holder = new ViewHolder(myView);
		return holder;
	}



	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final int index = position;
		holder.nameTv.setText(appBeans.get(position).getName());
		holder.contentTv.setText(appBeans.get(position).getIntroduction());

		DecimalFormat df = new DecimalFormat("#.0");
		double x = Double.valueOf(appBeans.get(position).getSize()) / 1024 / 1024;
		String size = df.format(x);
		holder.sizeTv.setText(size + "M");

		if(OkDownLoad.getInstance().getManger().getDownloadInfo(appBeans.get(index).getDownloadUrl())!=null){

			DownloadInfo downloadInfo = OkDownLoad.getInstance().getManger().getDownloadInfo(appBeans.get(index).getDownloadUrl());
			if(downloadInfo.getState() == DownloadManager.FINISH){
//				if (ApkUtils.isAvailable(mContext, new File(downloadInfo.getTargetPath()))) {
				if (ApkUtils.isAvailable(mContext, ((AppInfo)downloadInfo.getData()).getPackage_name())) {
					holder.downloadTv.setText("打开");
				} else {
					holder.downloadTv.setText("安装");
				}
			}
		}else{
			holder.downloadTv.setText("下载");
		}

		Glide.with(mContext)
				.load(appBeans.get(position).getThumbnailName())
				.placeholder(R.drawable.vr_default_img)
				.dontAnimate()
				.into(holder.appIcon);

		holder.downloadTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(OkDownLoad.getInstance().getManger().getDownloadInfo(appBeans.get(index).getDownloadUrl())!=null){
					DownloadInfo downloadInfo = OkDownLoad.getInstance().getManger().getDownloadInfo(appBeans.get(index).getDownloadUrl());
//					if (ApkUtils.isAvailable(mContext, new File(downloadInfo.getTargetPath()))) {
					if (ApkUtils.isAvailable(mContext, ((AppInfo)downloadInfo.getData()).getPackage_name())) {
//						ApkUtils.uninstall(mContext, ApkUtils.getPackageName(mContext, downloadInfo.getTargetPath()));//卸载
//						ApkUtils.openApp(mContext,ApkUtils.getPackageName(mContext, downloadInfo.getTargetPath()));
						ApkUtils.openApp(mContext,((AppInfo)downloadInfo.getData()).getPackage_name());
					} else {
						ApkUtils.install(mContext, new File(downloadInfo.getTargetPath()));
					}
				}else {
					GetRequest request = OkGo.get(appBeans.get(index).getDownloadUrl());
					OkDownLoad.getInstance().getManger().addTask(appBeans.get(index).getName() + ".apk", appBeans.get(index), appBeans.get(index).getDownloadUrl(), request, new LogDownloadListener());
					Intent intent = new Intent(mContext, DownloadManagerActivity.class);
					mContext.startActivity(intent);

					if (!AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.USERID).equals("")) {
						receiveScore(mContext, AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.USERID),
								AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.TOKEN));
					}
				}
			}
		});

		/*convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AppDettailsActivity2.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(PreferenceCode.APP_INFO, appBeans.get(index));
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		});*/

	}


	class ViewHolder extends RecyclerView.ViewHolder {
		TextView nameTv,sizeTv,contentTv,downloadTv;
		RoundedImageView appIcon;

		public ViewHolder(View convertView) {
			super(convertView);
			appIcon = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
			nameTv = (TextView)convertView.findViewById(R.id.name_tv);
			sizeTv = (TextView)convertView.findViewById(R.id.size_tv);
			contentTv = (TextView)convertView.findViewById(R.id.content_tv);
			downloadTv = (TextView)convertView.findViewById(R.id.tv_download);
		}

	}

	public void addData(List<AppInfo> appList) {
		this.appBeans.addAll(appList);
		this.notifyDataSetChanged();
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
}
