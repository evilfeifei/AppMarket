package com.huiyun.amnews.service;
//Download by http://www.codefans.net
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiyun.amnews.Constants;
import com.huiyun.amnews.adapter.DownloadManagerAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.db.buss.DBBuss;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.myview.RectProgressView;
import com.huiyun.amnews.util.CheckRoot;

// AsyncTask<Params, Progress, Result> 
@TargetApi(Build.VERSION_CODES.CUPCAKE)
@SuppressLint("NewApi")
public class DownloadAsync extends AsyncTask<String, Integer, String> 
{
	private boolean finished = true;
	private boolean paused = false;
	private RectProgressView mRectProgressView;
	public static List<RectProgressView> listPb = new ArrayList<RectProgressView>();
	private int progress;
	private int position;
	private Context mContext;
	private AppInfo mAppInfo;
	private String mDownloadUrl;
	private String downloadApkName;
	private TextView mPercentTV;
	private ImageView mPauseBtn;
	private TextView mDownLoadSpeedTV;
	private boolean is_open_apk;
	private boolean mark;
	private boolean dbmark;
	private long mStartTime;
	private int curSize;
	private int startPosition;
	private int length;
	private TextView tv_install;
	
	private OnDownloadListener onDownloadListener;
	
	public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
		this.onDownloadListener = onDownloadListener;
	}

	//下载回调接口
	public interface OnDownloadListener {
		public void downloadProgress(int progress,int endTime, String appId);
		public void downloadedBtn(String appId);
	}

	public DownloadAsync(Context context,RectProgressView rectProgressView,AppInfo appInfo, TextView percentTV,ImageView pauseBtn, TextView downLoadSpeedTV, TextView tv_install){
		super();
		this.mRectProgressView = rectProgressView;
		this.mContext = context;
		this.mPercentTV = percentTV;
		this.mPauseBtn = pauseBtn;
		this.mDownLoadSpeedTV = downLoadSpeedTV;
		this.mStartTime = System.currentTimeMillis();
		this.tv_install = tv_install;
		
		AppInfo appInfoDB = DBBuss.getInstance(context).getAppInfo(appInfo.getId());
		if(appInfoDB != null){
			this.mAppInfo = appInfoDB;
		}else{
			DBBuss.getInstance(context).insertDownloadAppInfo(appInfo);
			this.mAppInfo = appInfo;
		}
	}
	
	public boolean isPaused() 
	{
		return paused;
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
	
	@Override
	protected String doInBackground(String... Params)
	{
		Log.d("Async","doInBackground");
//		position = Integer.parseInt(Params[0]);
//		Log.d("debug","position:"+position);
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		RandomAccessFile outputStream = null;
//		String path = Environment.getExternalStorageDirectory().getPath();
		length = 0;
		try 
		{
			startPosition = mAppInfo.getDownloadSize();
			mDownloadUrl = mAppInfo.getDownloadUrl();
			Log.d("mDownloadUrl","mDownloadUrl:"+mDownloadUrl);
			url = new URL(mDownloadUrl);
			httpURLConnection = (HttpURLConnection)url.openConnection();
			// 设置断点续传的开始位置
			httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPosition + "-");
			 //设置当前线程下载的起点，终点
			length = httpURLConnection.getContentLength();
			Log.d("downloadlength","length:"+length);
//			httpURLConnection.setAllowUserInteraction(true);
			
//			Log.d("debug","getContentLength:"+length);
//			System.out.println("getContentLength:"+length);
			inputStream = httpURLConnection.getInputStream();
			
//			//设置User-Agent 
//			httpURLConnection.setRequestProperty("User-Agent","NetFox"); 
//			//设置断点续传的开始位置 
//			httpURLConnection.setRequestProperty("RANGE","bytes=4096"); 
			
			downloadApkName = mDownloadUrl.substring(mDownloadUrl.lastIndexOf("/") + 1);
			File outFile = getAppDownloadPath(downloadApkName);
//			Log.d("mDownloadUrl","outFile:"+Constant.ROOT_PATH + "/" + downloadApkName);
			//使用java中的RandomAccessFile 对文件进行随机读写操作
			outputStream = new RandomAccessFile(outFile,"rw");
			//设置开始写文件的位置
			outputStream.seek(startPosition);
			
			byte[] buf = new byte[1024*10];
			int read = 0;
			curSize = startPosition;
			Log.d("debug","buf："+buf.length);
			while(finished)
			{
//				mAppInfo.setDownloadProgress(progress);
//				mRectProgressView.setmProgress(progress);
				while(paused)
				{
					Thread.sleep(500);
				}
				read = inputStream.read(buf);
				if(read==-1)
				{
					break;
				}
				outputStream.write(buf,0,read);
				curSize = curSize+read;
				progress = (int)(curSize*100.0f/(startPosition + length));
				if(progress < 0){
					progress = 0;
				}
				mAppInfo.setDownloadProgress(progress);
				mRectProgressView.setmProgress(progress);
				// 当调用这个方法的时候会自动去调用onProgressUpdate方法，传递下载进度
				publishProgress((int)(curSize*100.0f/(startPosition + length)),curSize);
				if(curSize == startPosition + length)
				{
					break;
				}
				
				Thread.sleep(10);
			}
			inputStream.close();
			outputStream.close();
			httpURLConnection.disconnect();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			finished = false;
			if(inputStream!=null)
			{
				try 
				{
					inputStream.close();
					if(outputStream!=null)
					{
						outputStream.close();
					}
					if(httpURLConnection!=null)
					{
						httpURLConnection.disconnect();
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		// 这里的返回值将会被作为onPostExecute方法的传入参数
//		return String.valueOf(position);
		return Params[0];
	}
	/**
	 * 暂停下载
	 */
	public void pause()
	{
		paused = true;
		if(curSize >= mAppInfo.getDownloadSize()){
			DBBuss.getInstance(mContext).updateDownloadAppDownloadSize(curSize + 1, mAppInfo.getId());
		}else{
			DBBuss.getInstance(mContext).updateDownloadAppDownloadSize(mAppInfo.getDownloadSize() + curSize + 1, mAppInfo.getId());
		}
		
		Log.d("debug","paused------------"+paused);
	}
	/**
	 * 继续下载
	 */
	public void continued()
	{
		paused = false;
		Log.d("debug","paused------------"+paused);
	}
	
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public boolean getPaused(){
		return this.paused;
	}

	/**
	 * ֹͣ停止下载
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	@Override
	protected void onCancelled()
	{
		Log.d("debug","onCancelled");
		finished = false;
		super.onCancelled();
	}
	
	public void remove(final int i){
		new Handler().postDelayed(new Runnable(){    
		    public void run() {    
		    	Log.d("debug","remove sucess?:"+DownloadManagerAdapter.listTask.remove(i));
		    }    
		 }, 201);
	}
	/**
	 * 当一个下载任务成功下载完成的时候回来调用这个方法，这里的result参数就是doInBackground方法的返回ֵ
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	@Override
	protected void onPostExecute(String result) 
	{
//		mPauseBtn.setVisibility(View.GONE);
//		tv_install.setVisibility(View.VISIBLE);
		if (onDownloadListener != null) {
			onDownloadListener.downloadedBtn(mAppInfo.getId());
		}
		new Handler().postDelayed(new Runnable(){    
		    public void run() {    
		    	mDownLoadSpeedTV.setText("下载完成");
		    }    
		 }, 300);
		
		int pos = -1;
		try 
		{
			// 判断但前结束的这个任务在任务列表中是否还存在，如果存在就移除
			for(int i=0;i<DownloadManagerAdapter.listTask.size();i++)
			{
				if(DownloadManagerAdapter.listTask.get(i).get(result)!=null)
				{
					finished = false;
					DownloadManagerAdapter.mAppInfoList.remove(mAppInfo);
					remove(i);
					DBBuss.getInstance(mContext).updateDownloadAppDownloadSize(mAppInfo.getSize(), mAppInfo.getId());
					break;
				}
			}
			Log.d("debug","onPostExecute:"+DownloadManagerAdapter.listTask.size());
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
		//打开文件
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
/*					if(AppmarketPreferences.getInstance(mContext).getBooleanKey(PreferenceCode.INSTALL_CHECK)){
						
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
					}else{*/
						Intent intent = new Intent("android.intent.action.VIEW");  
						intent.addCategory("android.intent.category.DEFAULT");  
						intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);  
						Uri uri = Uri.fromFile(file);  
						intent.setDataAndType (uri, "application/vnd.android.package-archive");  
						mContext.startActivity(intent);
//					}
				}
			}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
	}
	/**
	 * 更新下载进度，当publishProgress方法被调用的时候就会自动来调用这个方法
	 */
	@Override
	protected void onProgressUpdate(final Integer... values) 
	{
		
		if (onDownloadListener != null) {
			onDownloadListener.downloadProgress(progress,values[1],mAppInfo.getId());
		}
		
		/*mRectProgressView.setmProgress(progress);
		mPercentTV.setText(progress + "%");
		mAppInfo.setDownloadProgress(progress);
		Log.d("DownloadAsync", "" + progress);
		if(!mark){
			mark = true;
			new Handler().postDelayed(new Runnable(){    
			    public void run() {    
			    	mDownLoadSpeedTV.setText(DownloadSpeedUtil.getDownloadSpeed(mStartTime, values[1]));
			    	mark = false;
			    }    
			 }, 200);
		}
		*/
		super.onProgressUpdate(values);
	}
	
	public void setProcessBar(RectProgressView rectProgressView,TextView percentTV, TextView tv_speed){
		if(this.mRectProgressView == null){
			this.mRectProgressView = rectProgressView;
		}
		if(this.mPercentTV == null){
			this.mPercentTV = percentTV;
		}
		if(this.mDownLoadSpeedTV == null){
			this.mDownLoadSpeedTV = tv_speed;
		}
	}
}
