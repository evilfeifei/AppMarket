package com.huiyun.vrxf.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiyun.vrxf.R;
import com.huiyun.vrxf.been.AppInfo;
import com.huiyun.vrxf.configuration.AppmarketPreferences;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.huiyun.vrxf.ui.AppDettailsActivity2;
import com.huiyun.vrxf.ui.DownloadManagerActivity;
import com.huiyun.vrxf.view.roundimage.RoundedImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		final int index = position;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app, null);
			holder = new ViewHolder();
			holder.appIcon = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
			holder.nameTv = (TextView)convertView.findViewById(R.id.name_tv);
			holder.sizeTv = (TextView)convertView.findViewById(R.id.size_tv);
			holder.contentTv = (TextView)convertView.findViewById(R.id.content_tv);
			holder.downloadTv = (TextView)convertView.findViewById(R.id.tv_download);
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

		Glide.with(mContext)
				.load(Constant.THUMBNAIL_URL_PREFIX + appBeans.get(position).getThumbnailName())
				.placeholder(R.drawable.vr_default_img)
				.dontAnimate()
				.into(holder.appIcon);

		holder.downloadTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putSerializable(PreferenceCode.APP_INFO, appBeans.get(index));
				switchActivity(DownloadManagerActivity.class, bundle);

				if(!AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.USERID).equals("")){
					receiveScore(mContext,AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.USERID),
							AppmarketPreferences.getInstance(mContext).getStringKey(PreferenceCode.TOKEN));
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

	private static class ViewHolder
	{
		TextView nameTv,sizeTv,contentTv,downloadTv;
		RoundedImageView appIcon;
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