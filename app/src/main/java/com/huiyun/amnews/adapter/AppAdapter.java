package com.huiyun.amnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
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
import com.lzy.okserver.download.DownloadService;
import com.lzy.okserver.download.db.DownloadDBManager;
import com.lzy.okserver.listener.DownloadListener;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		myNotifyDataSetChanged();
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
							gotoDetails(index,true);
                        }
                    } else {

                        addDownLoad(appBeans.get(index),index);

                        if (!AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.USERID).equals("")) {
                            receiveScore(mContext, AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.USERID),
                                    AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.TOKEN));
                        }
                    }
                }else{
                    //需要升级
                    if(ApkUtils.isUpdate(mContext,appBeans.get(index).getPackage_name(),appBeans.get(index).getVersion())){
                        addDownLoad(appBeans.get(index),index);
                    }else{
                        ApkUtils.openApp(mContext, appBeans.get(index).getPackage_name());
                    }
                }
            }
        });

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoDetails(index,false);
//				Intent intent = new Intent(mContext, AppDettailsActivity2.class);
//				Bundle bundle = new Bundle();
//				bundle.putSerializable(PreferenceCode.APP_INFO, appBeans.get(index));
//				intent.putExtras(bundle);
//				mContext.startActivity(intent);
			}
		});

		return convertView;
	}

	private void gotoDetails(int index,boolean isDownload){
		Intent intent = new Intent(mContext, AppDettailsActivity2.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(PreferenceCode.APP_INFO, appBeans.get(index));
		bundle.putBoolean("isDownload",isDownload);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
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
	}

	private void addDownLoad(AppInfo appInfo,int index){
		GetRequest request = OkGo.get(appInfo.getDownloadUrl());
		OkDownLoad.getInstance().getManger().addTask(appInfo.getName() + ".apk", appInfo, appInfo.getDownloadUrl(), request, new LogDownloadListener());
//		Intent intent = new Intent(mContext, DownloadManagerActivity.class);
//		mContext.startActivity(intent);
		myNotifyDataSetChanged();
		gotoDetails(index,true);
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
