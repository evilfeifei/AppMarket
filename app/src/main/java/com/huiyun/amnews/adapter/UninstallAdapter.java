package com.huiyun.amnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.UninstallAppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.downLoad.LogDownloadListener;
import com.huiyun.amnews.downLoad.OkDownLoad;
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

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UninstallAdapter extends  RecyclerView.Adapter<UninstallAdapter.ViewHolder>{

	private ArrayList<UninstallAppInfo> appList;
	private Context mContext;

	public UninstallAdapter(Context context){
		this.mContext = context;
		appList = new ArrayList<UninstallAppInfo>();
	}

	public UninstallAdapter(Context context, ArrayList<UninstallAppInfo> appList){
		this.appList = appList;
		this.mContext = context;
	}

	public void refreshData(ArrayList<UninstallAppInfo> appList) {
		this.appList = appList;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return appList.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = null;
		View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uninstall_app, parent, false);
		holder = new ViewHolder(myView);
		return holder;
	}



	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		final int index = position;
		holder.nameTv.setText(appList.get(position).getAppName());
		holder.versionTv.setText("版本："+appList.get(position).getVersionName());
		holder.appIcon.setBackground(appList.get(position).getAppIcon());

		holder.downloadTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ApkUtils.uninstall(mContext,appList.get(position).getPackageName());
			}
		});
	}


	class ViewHolder extends RecyclerView.ViewHolder {
		TextView nameTv,sizeTv,versionTv,downloadTv;
		RoundedImageView appIcon;

		public ViewHolder(View convertView) {
			super(convertView);
			appIcon = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
			nameTv = (TextView)convertView.findViewById(R.id.name_tv);
			sizeTv = (TextView)convertView.findViewById(R.id.size_tv);
			versionTv = (TextView)convertView.findViewById(R.id.version_tv);
			downloadTv = (TextView)convertView.findViewById(R.id.tv_download);
		}

	}
}
