package com.huiyun.vrxf.db.help;

import com.huiyun.vrxf.db.constant.DBConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * SQLite数据库的帮助类
 * 
 * 该类属于扩展类,主要承担数据库初始化和版本升级使用,其他核心全由核心父类完成
 */
public class DataBaseHelper extends SDCardSQLiteOpenHelper {

	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for(String createsql:DBConstants.DB_CREATE_LIST){
			db.execSQL(createsql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
}
