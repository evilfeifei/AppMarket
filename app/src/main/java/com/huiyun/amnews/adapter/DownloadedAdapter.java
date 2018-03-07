package com.huiyun.amnews.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.Constants;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.db.buss.DBBuss;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.myview.dialogview.DialogTips;
import com.huiyun.amnews.util.CheckRoot;
import com.huiyun.amnews.util.ViewHolder;
import com.huiyun.amnews.view.roundimage.RoundedImageView;

public class DownloadedAdapter extends BaseAdapter{

	private List<AppInfo> mAppInfoList;
	private Context mContext;

	public DownloadedAdapter(Context context){
		this.mContext = context;
		mAppInfoList = new ArrayList<AppInfo>();
	}
	
	public DownloadedAdapter(Context context, List<AppInfo> appInfoList){
		this.mAppInfoList = appInfoList;
		this.mContext = context;
	}
	
	public int getCount() {
		return mAppInfoList.size();
	}

	public AppInfo getItem(int position) {
		return mAppInfoList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_downloaded, null);
		}
		final AppInfo mAppInfo = getItem(position);
		RoundedImageView i_chat_avatar_icon = (RoundedImageView)ViewHolder.get(convertView,R.id.i_chat_avatar_icon);
		final ImageView btn_delete = (ImageView)ViewHolder.get(convertView,R.id.btn_delete);
		TextView tv_install = (TextView)ViewHolder.get(convertView,R.id.tv_install);
		
		TextView tv_app_name = (TextView)ViewHolder.get(convertView,R.id.tv_app_name);
//		ImageLoaderUtil.getInstance().displayImg(i_chat_avatar_icon, Constant.THUMBNAIL_URL_PREFIX + mAppInfo.getThumbnailName(),options);
		Glide.with(mContext)
				.load(Constant.THUMBNAIL_URL_PREFIX + mAppInfo.getThumbnailName())
				.placeholder(R.drawable.vr_default_img)
				.dontAnimate()
				.into(i_chat_avatar_icon);
		tv_app_name.setText(mAppInfo.getName());
		
		String mDownloadUrl = mAppInfo.getDownloadUrl();
		final String downloadApkName = mDownloadUrl.substring(mDownloadUrl.lastIndexOf("/") + 1);
		
		tv_install.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final File file = new File(Constants.BASE_APP_DOWNLOAD_PATH + downloadApkName);
				if(file.exists()){
					//root用户快速安装
					if(CheckRoot.is_root()&&AppmarketPreferences.getInstance(mContext).getBooleanKey(PreferenceCode.ROOT_INSTALL_CHECK)){
						Intent intent = new Intent("android.intent.action.VIEW");  
						intent.addCategory("android.intent.category.DEFAULT");  
						intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);  
						Uri uri = Uri.fromFile(file);  
						intent.setDataAndType (uri, "application/vnd.android.package-archive");  
						mContext.startActivity(intent);
					}else{
					if(AppmarketPreferences.getInstance(mContext).getBooleanKey(PreferenceCode.INSTALL_CHECK)){
						
						DialogTips dialog = new DialogTips(mContext,"温馨提示","你确定要安装吗？", "确定",true,true);
						dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialogInterface, int userId) {
								Intent intent = new Intent("android.intent.action.VIEW");  
								intent.addCategory("android.intent.category.DEFAULT");  
								intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);  
								Uri uri = Uri.fromFile(file);  
								intent.setDataAndType (uri, "application/vnd.android.package-archive");  
								mContext.startActivity(intent);
							}
						});
						// 显示确认对话框
						dialog.show();
						dialog = null;
					}else{
						Intent intent = new Intent("android.intent.action.VIEW");  
						intent.addCategory("android.intent.category.DEFAULT");  
						intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);  
						Uri uri = Uri.fromFile(file);  
						intent.setDataAndType (uri, "application/vnd.android.package-archive");  
						mContext.startActivity(intent);
					}
					}
				}
			}
		});
		
		btn_delete.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mAppInfoList.remove(mAppInfo);
				notifyDataSetChanged(mAppInfoList);
				DBBuss.getInstance(mContext).deleteDownloadAppInfoById(mAppInfo.getId());
				String mDownloadUrl = mAppInfo.getDownloadUrl();
				String downloadApkName = mDownloadUrl.substring(mDownloadUrl.lastIndexOf("/") + 1);
				File file = getAppDownloadPath(downloadApkName);
				if(file.exists()){
					file.delete();
				}
			}
		});
		
		return convertView;
	}
	
	public void notifyDataSetChanged(List<AppInfo> appInfoList) {
		this.mAppInfoList = appInfoList;
		this.notifyDataSetChanged();
	}

	public void addData(List<AppInfo> appInfoList) {
		this.mAppInfoList.addAll(appInfoList);
		this.notifyDataSetChanged();
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

}
