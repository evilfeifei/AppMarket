package com.huiyun.vrxf.db.buss;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.huiyun.vrxf.been.AppInfo;
import com.huiyun.vrxf.db.constant.DBConstants;
import com.huiyun.vrxf.db.help.SQLiteTemplate;
import com.huiyun.vrxf.db.help.SQLiteTemplate.RowMapper;
import com.huiyun.vrxf.db.manager.DBManager;

/**
 * 数据库业务操作封装类，单例模式
 * 
 * @author melon
 * 
 */
public class DBBuss {
	private static SQLiteTemplate template;
	private static SharedPreferences preferences;

	private static String preferences_key = "key";
	public static String OPERATION_TIME_KEY = "OPERATION_TIME";

	private static DBBuss instance;

	private DBBuss() {

	}

	/**
	 * @param mContext
	 * @return
	 */
	public static DBBuss getInstance(Context mContext) {
		if (instance == null) {
			instance = new DBBuss();
		}

		if (preferences == null) {
			preferences = mContext.getSharedPreferences(preferences_key, 0);
		}

		if (template == null) {
			// 多线程访问数据库。不能随意关闭， 如果其中一个线程关闭了， 那么另一个正在访问有问题
			template = SQLiteTemplate.getInstance(
					DBManager.getInstance(mContext, DBConstants.DBNAME), false);
		}
		return instance;
	}

	public int insertDownloadAppInfo(AppInfo appInfo){
		ContentValues content = new ContentValues();
		content.put("id",appInfo.getId());
		content.put("name",appInfo.getName());
		content.put("version",appInfo.getVersion());
		content.put("size",appInfo.getSize());
		content.put("downloadsize",appInfo.getDownloadSize());
		content.put("download_url",appInfo.getDownloadUrl());
		content.put("update_description",appInfo.getUpdateDescription());
		content.put("introduction",appInfo.getIntroduction());
		content.put("screenshots_name",appInfo.getScreenshotsName());
		content.put("thumbnail_name",appInfo.getThumbnailName());
		content.put("created_by",appInfo.getCreatedBy());
		content.put("created_time",appInfo.getCreatedTime());
		content.put("last_modified_by",appInfo.getLastModifiedBy());
		content.put("last_modified_time",appInfo.getLastModifiedTime());
		content.put("is_del",appInfo.getIsDel());
		return (int) template.insert("download_appinfo", content);
	}
	
	public void deleteDownloadAppInfoById(String id){
		template.execSQL("delete from download_appinfo where id="+"'"+id+"'");
	}
	
	public int updateDownloadAppDownloadSize(int downloadSize,String id){
		ContentValues content = new ContentValues();
		content.put("downloadsize", downloadSize);
		return template.updateById("download_appinfo", id, content);
	}
	
	public List<AppInfo> getAllDownloadedAppInfoList(){
		String sql = "select * from download_appinfo where size = downloadsize order by created_time desc";
		tracSql(sql);
		List<AppInfo> downloadAppInfoList = template.queryForList(
				new RowMapper<AppInfo>() {
					public AppInfo mapRow(Cursor cursor, int index) {
						return getDownloadAppInfoFromCursor(cursor);
					}

				}, sql, null);
		return downloadAppInfoList;
	}
	
	public List<AppInfo> getAllDownloadingAppInfoList(){
		String sql = "select * from download_appinfo where size != downloadsize order by created_time desc";
		tracSql(sql);
		List<AppInfo> downloadAppInfoList = template.queryForList(
				new RowMapper<AppInfo>() {
					public AppInfo mapRow(Cursor cursor, int index) {
						return getDownloadAppInfoFromCursor(cursor);
					}

				}, sql, null);
		return downloadAppInfoList;
	}
	
	public AppInfo getAllDownloadedAppInfo(String id){
		String sql = "select * from download_appinfo where size <= downloadsize and id=?";
		tracSql(sql);
		AppInfo downloadAppInfo = template.queryForObject(
				new RowMapper<AppInfo>() {
					public AppInfo mapRow(Cursor cursor, int index) {
						return getDownloadAppInfoFromCursor(cursor);
					}

				}, sql, new String[] { id });
		return downloadAppInfo;
	}
	
	public AppInfo getAppInfo(String id){
		String sql = "select * from download_appinfo where id=?";
		AppInfo appInfo = template.queryForObject(new RowMapper<AppInfo>() {
			public AppInfo mapRow(Cursor cursor, int index) {
				return getDownloadAppInfoFromCursor(cursor);
			}
		}, sql, new String[] { id });
		return appInfo;
	}
	
	public AppInfo getDownloadAppInfoFromCursor(Cursor cursor) {
		AppInfo appInfo = new AppInfo();
		appInfo.setId(cursor.getString(cursor.getColumnIndex("id")));
		appInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
		appInfo.setVersion(cursor.getString(cursor.getColumnIndex("version")));
		appInfo.setSize(cursor.getInt(cursor.getColumnIndex("size")));
		appInfo.setDownloadSize(cursor.getInt(cursor.getColumnIndex("downloadsize")));
		appInfo.setDownloadUrl(cursor.getString(cursor.getColumnIndex("download_url")));
		appInfo.setUpdateDescription(cursor.getString(cursor.getColumnIndex("update_description")));
		appInfo.setIntroduction(cursor.getString(cursor.getColumnIndex("introduction")));
		appInfo.setScreenshotsName(cursor.getString(cursor.getColumnIndex("screenshots_name")));
		appInfo.setThumbnailName(cursor.getString(cursor.getColumnIndex("thumbnail_name")));
		appInfo.setCreatedBy(cursor.getString(cursor.getColumnIndex("created_by")));
		appInfo.setCreatedTime(cursor.getInt(cursor.getColumnIndex("created_time")));
		appInfo.setLastModifiedBy(cursor.getString(cursor.getColumnIndex("last_modified_by")));
		appInfo.setLastModifiedTime(cursor.getInt(cursor.getColumnIndex("last_modified_time")));
		appInfo.setIsDel(cursor.getString(cursor.getColumnIndex("is_del")));
		
		return appInfo;
	}

	/**
	 * 打印sql
	 */
	private void tracSql(String sql) {
		Log.i("db", sql);
	}

}
