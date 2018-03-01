package com.huiyun.vrxf;

import android.os.Environment;

public class Constants {
	
	/**
	 * 本地文件基础存储路径
	 */
	public static final String BASE_PATH=Environment.getExternalStorageDirectory().getAbsolutePath();
	
	/**
	 * 项目地址
	 */
	public static final String BASE_APP_PATH="/dingjianhui";
	
	
	/**
	 * 数据库路径
	 */
	public static final String BASE_DB_PATH=BASE_PATH+BASE_APP_PATH+"/database/";
	
	/**
	 * apk下载路径
	 */
	public static final String BASE_APP_DOWNLOAD_PATH=BASE_PATH+BASE_APP_PATH+"/app/";


	public static final String UPDATE_SAVENAME="vr_update_app";
}
