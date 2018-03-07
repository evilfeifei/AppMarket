package com.huiyun.amnews.db.manager;

import java.util.concurrent.atomic.AtomicInteger;

import com.huiyun.amnews.db.help.DataBaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * SQLite数据库管理类
 * 主要负责数据库资源的初始化,开启,关闭,以及获得DatabaseHelper帮助类操作
 * 
 */
public class DBManager {
	private int version = 1;
	private String databaseName;

	// 本地Context对象
	private Context mContext = null;

	//单例模式
	private static DBManager dBManager = null;
	
	 private AtomicInteger mOpenCounter = new AtomicInteger();  
	 private SQLiteDatabase mDatabase;  

	/**
	 * 构造函数
	 * 
	 * @param mContext
	 */
	private DBManager(Context mContext) {
		super();
		this.mContext = mContext;

	}

	public static DBManager getInstance(Context mContext, String databaseName) {
		if (null == dBManager) {
			dBManager = new DBManager(mContext);
		}
		
		dBManager.databaseName = databaseName;
		return dBManager;
	}


	/**
	 * 打开数据库 注:SQLiteDatabase资源一旦被关闭,该底层会重新产生一个新的SQLiteDatabase
	 */
	public synchronized SQLiteDatabase openDatabase() {
		//数据库连接计数器
		  if(mOpenCounter.incrementAndGet() == 1) {  
	            // Opening new database  
	            mDatabase =getDatabaseHelper().getWritableDatabase();  
	        }
	        return mDatabase;  
	}
	
	  public synchronized void closeDatabase() {  
	        if(mOpenCounter.decrementAndGet() == 0) {  
	            // Closing database 
	        	if(mDatabase!=null){
	            mDatabase.close();  
	        	}
	        }  
	    }  

	/**
	 * 获取DataBaseHelper
	 * 
	 * @return
	 */
	public synchronized DataBaseHelper getDatabaseHelper() {
		return new DataBaseHelper(mContext, this.databaseName, null,
				this.version);
	}
	
	

}
