package com.huiyun.vrxf.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiyun.vrxf.Constants;
import com.huiyun.vrxf.MyApplication;
import com.huiyun.vrxf.R;
import com.huiyun.vrxf.been.AppInfo;
import com.huiyun.vrxf.configuration.AppmarketPreferences;
import com.huiyun.vrxf.db.buss.DBBuss;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.huiyun.vrxf.myview.RectProgressView;
import com.huiyun.vrxf.myview.dialogview.DialogTips;
import com.huiyun.vrxf.service.DownloadAsync;
import com.huiyun.vrxf.service.DownloadAsync.OnDownloadListener;
import com.huiyun.vrxf.util.CheckRoot;
import com.huiyun.vrxf.util.DownloadSpeedUtil;
import com.huiyun.vrxf.util.ViewHolder;
import com.huiyun.vrxf.view.roundimage.RoundedImageView;

public class DownloadManagerAdapter extends BaseAdapter{

	public static List<AppInfo> mAppInfoList;
	private Context mContext;
	private DownloadAsync asyncTask = null;
	private Map<String, DownloadAsync> mAsyncMap = null;

	public static List<Map<String, DownloadAsync>> listTask = new ArrayList<Map<String, DownloadAsync>>();
	public static Map<String, RectProgressView> mapProgressView = new Hashtable<String, RectProgressView>();
	private static List<ImageView> listPause = new ArrayList<ImageView>();
	private static List<ImageView> listContinue = new ArrayList<ImageView>();
	private static List<ImageView> listStart = new ArrayList<ImageView>();
	private boolean isHas;
	private MyApplication app;
	private boolean mark;
	private long mStartTime;
	private AppInfo mAppInfo;
	
	
	@SuppressWarnings("static-access")
	public DownloadManagerAdapter(Context context){
		this.mContext = context;
		this.app = MyApplication.getInstance();
		mAppInfoList = new ArrayList<AppInfo>();
		this.mStartTime = System.currentTimeMillis();
	}
	
	public DownloadManagerAdapter(Context context, List<AppInfo> appInfoList){
		this.app = MyApplication.getInstance();
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

	@SuppressWarnings("static-access")
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_download, null);
		}
		mAppInfo = getItem(position);
		
		final RectProgressView pv_progress = (RectProgressView)ViewHolder.get(convertView,R.id.pv_progress);
		final ImageView btn_start = (ImageView)ViewHolder.get(convertView,R.id.btn_start);
		final ImageView btn_continue = (ImageView)ViewHolder.get(convertView,R.id.btn_continue);
		final ImageView btn_pause = (ImageView)ViewHolder.get(convertView,R.id.btn_pause);
		final ImageView btn_delete = (ImageView)ViewHolder.get(convertView,R.id.btn_delete);
		final TextView tv_install = (TextView)ViewHolder.get(convertView,R.id.tv_install);
		tv_install.setVisibility(View.GONE);
		RoundedImageView i_chat_avatar_icon = (RoundedImageView)ViewHolder.get(convertView,R.id.i_chat_avatar_icon);
		TextView tv_app_name = (TextView)ViewHolder.get(convertView,R.id.tv_app_name);
		final TextView tv_percent = (TextView)ViewHolder.get(convertView,R.id.tv_percent);
		final TextView tv_speed = (TextView)ViewHolder.get(convertView,R.id.tv_speed);
		
		Glide.with(mContext)
				.load(Constant.THUMBNAIL_URL_PREFIX + mAppInfo.getThumbnailName())
				.placeholder(R.drawable.vr_default_img)
				.dontAnimate()
				.into(i_chat_avatar_icon);
		tv_app_name.setText(mAppInfo.getName());
		Log.d("DownloadSize", mAppInfo.getDownloadSize() + "");
		if(mAppInfo.getDownloadSize() == 0){
			tv_percent.setText(mAppInfo.getDownloadProgress() + "%");
			pv_progress.setmProgress(mAppInfo.getDownloadProgress());
		}else{
			int progress = 0;
			if(mAppInfo.getDownloadProgress() != 0){
				progress = mAppInfo.getDownloadProgress();
			}else{
				progress = (int)(mAppInfo.getDownloadSize()*100.0f/mAppInfo.getSize());
			}
			tv_percent.setText(progress + "%");
			pv_progress.setmProgress(progress);
		}
		
		tv_speed.setText("0kb/s");
		
		if(btn_start.getTag() == null){
			btn_start.setTag(mAppInfo.getId());
			btn_continue.setTag(mAppInfo.getId());
			btn_pause.setTag(mAppInfo.getId());
			pv_progress.setTag(mAppInfo.getId());
			mapProgressView.put(mAppInfo.getId(), pv_progress);
		}
		
//		DownloadAsync.listPb.add(pv_progress);
		listStart.add(btn_start);
		listPause.add(btn_pause);
		listContinue.add(btn_continue);
		
		DownloadAsync startTask = null;
		
		isHas = false;
		
		Log.d("dsfwef",mapProgressView.size() + "");
		if(mAppInfo.getDownloadSize() == 0){
			for(int i=0;i<listTask.size();i++)
			{
				startTask = listTask.get(i).get(mAppInfo.getId());
				if(startTask!=null)
				{
					isHas = true;
					break;
				}
			}
			Log.d("sdffe", isHas + "");
			if(!isHas)
			{
				mAppInfo.setTag(false);
				asyncTask = new DownloadAsync(mContext,pv_progress,mAppInfo,tv_percent,btn_pause,tv_speed,tv_install);
				mAsyncMap = new Hashtable<String, DownloadAsync>();
				mAsyncMap.put(mAppInfo.getId(), asyncTask);
				listTask.add(mAsyncMap);
				List<Map<String, DownloadAsync>> mlistTask = new ArrayList<Map<String, DownloadAsync>>();
				mlistTask.add(mAsyncMap);
				asyncTask.execute(mAppInfo.getId());
				
				btn_start.setVisibility(View.GONE);
				btn_pause.setVisibility(View.VISIBLE);
				btn_continue.setVisibility(View.GONE);
				
				asyncTask.setOnDownloadListener(new OnDownloadListener() {
					
					@Override
					public void downloadProgress(int progress,final int endTime,String appId) {
						Log.d("fefef",pv_progress.getTag().toString());
						if(pv_progress.getTag().toString().equals(appId)){
							pv_progress.setmProgress(progress);
							tv_percent.setText(progress + "%");
							mAppInfo.setDownloadProgress(progress);
							Log.d("DownloadAsync", "" + progress);
							Log.d("dwwdw","tag0:" + pv_progress.getTag().toString());
							Log.d("dwwdw","id0:" + appId);
							if(!mark){
								mark = true;
								new Handler().postDelayed(new Runnable(){    
								    public void run() {    
								    	tv_speed.setText(DownloadSpeedUtil.getDownloadSpeed(mStartTime, endTime));
								    	mark = false;
								    }    
								 }, 200);
							}
						}
						
					}

					@Override
					public void downloadedBtn(String appId) {
						if(pv_progress.getTag().toString().equals(appId)){
							btn_pause.setVisibility(View.GONE);
							tv_install.setVisibility(View.VISIBLE);
							tv_speed.setVisibility(View.GONE);
						}
					}
				});
			}else{
				if(PreferenceCode.IS_DESTORY && mAppInfo.isTag()){
					Log.d("fefef",pv_progress.getTag().toString());
					mAppInfo.setTag(false);
					startTask.setOnDownloadListener(new OnDownloadListener() {
						
						@Override
						public void downloadProgress(int progress,final int endTime,String appId) {
							if(pv_progress.getTag().toString().equals(appId)){
								pv_progress.setmProgress(progress);
								tv_percent.setText(progress + "%");
								mAppInfo.setDownloadProgress(progress);
								Log.d("dwwdw","tag1:" + pv_progress.getTag().toString());
								Log.d("dwwdw","id1:" + appId);
								Log.d("DownloadAsync", "" + progress);
								if(!mark){
									mark = true;
									new Handler().postDelayed(new Runnable(){    
									    public void run() {    
									    	tv_speed.setText(DownloadSpeedUtil.getDownloadSpeed(mStartTime, endTime));
									    	mark = false;
									    }    
									 }, 200);
								}
							}
						}

						@Override
						public void downloadedBtn(String appId) {
							if(pv_progress.getTag().toString().equals(appId)){
								btn_pause.setVisibility(View.GONE);
								tv_install.setVisibility(View.VISIBLE);
								tv_speed.setVisibility(View.GONE);
							}
						}
					});
					btn_start.setVisibility(View.GONE);
					btn_pause.setVisibility(View.VISIBLE);
					btn_continue.setVisibility(View.GONE);
				}
			}
		}else{
			for(int i=0;i<listTask.size();i++)
			{
				startTask = listTask.get(i).get(mAppInfo.getId());
				if(startTask!=null)
				{
					isHas = true;
					break;
				}
			}
			if(startTask != null){
				if(startTask.getPaused()){
					btn_start.setVisibility(View.GONE);
					btn_pause.setVisibility(View.GONE);
					btn_continue.setVisibility(View.VISIBLE);
				}else{
					btn_pause.setVisibility(View.GONE);
					btn_continue.setVisibility(View.GONE);
					btn_start.setVisibility(View.VISIBLE);
				}
			}
			
		}
		
		btn_start.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				DownloadAsync startTask = null;
				boolean isHas = false;
				for(int i=0;i<listTask.size();i++)
				{
					startTask = listTask.get(i).get(mAppInfo.getId());
					if(startTask!=null)
					{
						isHas = true;
						break;
					}
				}
				if(!isHas)
				{
					asyncTask = new DownloadAsync(mContext,pv_progress,mAppInfo,tv_percent,btn_pause,tv_speed,tv_install);
					mAsyncMap = new Hashtable<String, DownloadAsync>();
					mAsyncMap.put(mAppInfo.getId(), asyncTask);
					listTask.add(mAsyncMap);
					List<Map<String, DownloadAsync>> mlistTask = new ArrayList<Map<String, DownloadAsync>>();
					mlistTask.add(mAsyncMap);
					asyncTask.execute(mAppInfo.getId());
					
					btn_start.setVisibility(View.GONE);
					btn_pause.setVisibility(View.VISIBLE);
					btn_continue.setVisibility(View.GONE);
					
					asyncTask.setOnDownloadListener(new OnDownloadListener() {
						
						@Override
						public void downloadProgress(int progress,final int endTime,String appId) {
							if(pv_progress.getTag().toString().equals(appId)){
								pv_progress.setmProgress(progress);
								tv_percent.setText(progress + "%");
								mAppInfo.setDownloadProgress(progress);
								Log.d("DownloadAsync", "" + progress);
								if(!mark){
									mark = true;
									new Handler().postDelayed(new Runnable(){    
									    public void run() {    
									    	tv_speed.setText(DownloadSpeedUtil.getDownloadSpeed(mStartTime, endTime));
									    	mark = false;
									    }    
									 }, 200);
								}
							}
						}

						@Override
						public void downloadedBtn(String appId) {
							if(pv_progress.getTag().toString().equals(appId)){
								btn_pause.setVisibility(View.GONE);
								tv_install.setVisibility(View.VISIBLE);
								tv_speed.setVisibility(View.GONE);
							}
						}
					});
				}else{
					
						startTask.setOnDownloadListener(new OnDownloadListener() {
							
							@Override
							public void downloadProgress(int progress,final int endTime,String appId) {
								if(pv_progress.getTag().toString().equals(appId)){
									pv_progress.setmProgress(progress);
									tv_percent.setText(progress + "%");
									mAppInfo.setDownloadProgress(progress);
									Log.d("DownloadAsync", "" + progress);
									if(!mark){
										mark = true;
										new Handler().postDelayed(new Runnable(){    
										    public void run() {    
										    	tv_speed.setText(DownloadSpeedUtil.getDownloadSpeed(mStartTime, endTime));
										    	mark = false;
										    }    
										 }, 200);
									}
								}
							}

							@Override
							public void downloadedBtn(String appId) {
								if(pv_progress.getTag().toString().equals(appId)){
									btn_pause.setVisibility(View.GONE);
									tv_install.setVisibility(View.VISIBLE);
									tv_speed.setVisibility(View.GONE);
								}
							}
						});
						btn_start.setVisibility(View.GONE);
						btn_pause.setVisibility(View.VISIBLE);
						btn_continue.setVisibility(View.GONE);
						
				}
			}
		});
		
		btn_delete.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mAppInfoList.remove(mAppInfo);
				for(int i=0;i<listTask.size();i++)
				{
					DownloadAsync startTask = listTask.get(i).get(mAppInfo.getId());
					if(startTask!=null)
					{
						listTask.remove(i);
						startTask.cancel(true);
						break;
					}
				}
				Log.d("ewfwef",listTask.size() + "");
				DBBuss.getInstance(mContext).deleteDownloadAppInfoById(mAppInfo.getId());

				String mDownloadUrl = mAppInfo.getDownloadUrl();
				String downloadApkName = mDownloadUrl.substring(mDownloadUrl.lastIndexOf("/") + 1);
				File file = getAppDownloadPath(downloadApkName);
				if(file.exists()){
					file.delete();
				}
				notifyDataSetChanged(mAppInfoList);
			}
		});
		
		btn_pause.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
//				btn_delete.setVisibility(View.VISIBLE);
				DownloadAsync pauseTask = null;
				for(int i=0;i<listTask.size();i++)
				{
					pauseTask = listTask.get(i).get(String.valueOf(v.getTag()));
					if(pauseTask!=null)
					{
						pauseTask.pause();
						
						v.setVisibility(View.GONE);
						btn_continue.setVisibility(View.VISIBLE);
						btn_start.setVisibility(View.GONE);
//						DownloadAsync.listPb.get(position).setVisibility(View.VISIBLE);
					}
				}
				
			}
		});
		
		btn_continue.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
//				btn_delete.setVisibility(View.GONE);
				DownloadAsync continueTask = null;
				// 鍒ゆ柇浣嗗墠琚仠姝㈢殑杩欎釜浠诲姟鍦ㄤ换鍔″垪琛ㄤ腑鏄惁瀛樺湪锛屽鏋滃瓨鍦ㄥ氨缁х画
				for(int i=0;i<listTask.size();i++)
				{
					continueTask = listTask.get(i).get(String.valueOf(v.getTag()));
					if(continueTask!=null)
					{
//						asyncTask = new Async(pv_progress);
						continueTask.continued();
						
						continueTask.setOnDownloadListener(new OnDownloadListener() {
							
							@Override
							public void downloadProgress(int progress,final int endTime,String appId) {
								if(pv_progress.getTag().toString().equals(appId)){
									pv_progress.setmProgress(progress);
									tv_percent.setText(progress + "%");
									mAppInfo.setDownloadProgress(progress);
									Log.d("DownloadAsync", "" + progress);
									if(!mark){
										mark = true;
										new Handler().postDelayed(new Runnable(){    
										    public void run() {    
										    	tv_speed.setText(DownloadSpeedUtil.getDownloadSpeed(mStartTime, endTime));
										    	mark = false;
										    }    
										 }, 200);
									}
								}
							}

							@Override
							public void downloadedBtn(String appId) {
								if(pv_progress.getTag().toString().equals(appId)){
									btn_pause.setVisibility(View.GONE);
									tv_install.setVisibility(View.VISIBLE);
									tv_speed.setVisibility(View.GONE);
								}
							}
						});
						
						v.setVisibility(View.GONE);
						btn_pause.setVisibility(View.VISIBLE);
						btn_start.setVisibility(View.GONE);
						break;
//						DownloadAsync.listPb.get(position).setVisibility(View.VISIBLE);
					}
				}
			}
		});
		
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

//	public void onClick(View v) {
//		switch (v.getId()) {
//			case R.id.btn_start:
//				Async startTask = null;
//				boolean isHas = false;
//				// 鍒ゆ柇褰撳墠瑕佷笅杞界殑杩欎釜杩炴帴鏄惁宸茬粡姝ｅ湪杩涜锛屽鏋滄鍦ㄨ繘琛屽氨闃绘鍦ㄦ鍚姩涓�涓笅杞戒换鍔�
//				for(int i=0;i<listTask.size();i++)
//				{
//					startTask = listTask.get(i).get(String.valueOf(v.getTag()));
//					if(startTask!=null)
//					{
//						isHas = true;
//						break;
//					}
//				}
//				// 濡傛灉杩欎釜杩炴帴鐨勪笅杞戒换鍔¤繕娌℃湁寮�濮嬶紝灏卞垱寤轰竴涓柊鐨勪笅杞戒换鍔″惎鍔ㄤ笅杞斤紝骞惰繖涓笅杞戒换鍔″姞鍒颁笅杞藉垪琛ㄤ腑
//				if(!isHas)
//				{
//					asyncTask = new Async();  // 鍒涘缓鏂板紓姝�
//					mAsyncMap = new Hashtable<String, Async>();
//					mAsyncMap.put(String.valueOf(v.getTag()), asyncTask);
//					listTask.add(mAsyncMap);
//					asyncTask.execute(String.valueOf(v.getTag()));
//					
//					listStart.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.GONE);
//					listPause.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.VISIBLE);
//					listContinue.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.GONE);
//					Async.listPb.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.VISIBLE);
//				}
//				break;
//			case R.id.btn_continue:
//				Async continueTask = null;
//				// 鍒ゆ柇浣嗗墠琚仠姝㈢殑杩欎釜浠诲姟鍦ㄤ换鍔″垪琛ㄤ腑鏄惁瀛樺湪锛屽鏋滃瓨鍦ㄥ氨缁х画
//				for(int i=0;i<listTask.size();i++)
//				{
//					continueTask = listTask.get(i).get(String.valueOf(v.getTag()));
//					if(continueTask!=null)
//					{
//						asyncTask = new Async();
//						Log.d("debug","-------asyncTask.paused:"+asyncTask.isPaused());
//						continueTask.continued();
//						
//						listStart.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.GONE);
//						listPause.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.VISIBLE);
//						listContinue.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.GONE);
//						Async.listPb.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.VISIBLE);
//						break;
//					}
//				}
//				break;
//			case R.id.btn_pause:
//				Async pauseTask = null;
//				// 鍒ゆ柇浣嗗墠琚仠姝㈢殑杩欎釜浠诲姟鍦ㄤ换鍔″垪琛ㄤ腑鏄惁瀛樺湪锛屽鏋滃瓨鍦ㄥ氨鏆傚仠
//				for(int i=0;i<listTask.size();i++)
//				{
//					pauseTask = listTask.get(i).get(String.valueOf(v.getTag()));
//					if(pauseTask!=null)
//					{
//						asyncTask = new Async();
//						//  涓轰粈涔堣繖閲岀殑asyncTask.isPaused()涓�鐩存槸false锛燂紵锛燂紵锛燂紵锛燂紵锛燂紵
//						Log.d("debug","-------asyncTask.paused:"+asyncTask.isPaused());
//						pauseTask.pause();
//						
//						listStart.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.GONE);
//						listPause.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.GONE);
//						listContinue.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.VISIBLE);
//						Async.listPb.get(Integer.parseInt(String.valueOf(v.getTag()))).setVisibility(View.VISIBLE);
//						break;
//					}
//				}
//				break;
//	
//			default:
//				break;
//		}
//		
//	}
}
