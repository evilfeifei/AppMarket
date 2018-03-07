package com.huiyun.amnews.db.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库使用常量
 * @author melon
 *
 */
public class DBConstants {
	//数据库名称
	public static final String DBNAME="vr_xianfeng";
	public static List<String> DB_CREATE_LIST=new ArrayList<String>();
	
//	public static final String USER="create table USER("
//			  +"id       NVARCHAR(128) PRIMARY KEY,"  
//			  +"nickname   NVARCHAR(128),"                        
//			  +"gender    NVARCHAR(20),"        
//			  +"avatar     NVARCHAR(128)," 
//			  +"age INTEGER(3),"
//			  +"created_by    NVARCHAR(128),"          
//			  +"created_time   NVARCHAR(60),"           
//			  +"last_modified_by NVARCHAR(128),"                          
//			  +"last_modified_time   NVARCHAR(60))";
	
	public static final String DOWNLOAD_APPINFO = "create table download_appinfo(" +
			"id       				NVARCHAR(40) PRIMARY KEY NOT NULL," +
			"name   				NVARCHAR(50) NOT NULL," +
			"version   				NVARCHAR(10) NOT NULL," +
			"size					INTEGER NOT NULL," +
			"downloadsize			INTEGER NOT NULL," +
			"download_url			NVARCHAR(255) NOT NULL," +
			"update_description		TEXT NOT NULL," +
			"introduction			TEXT NOT NULL," +
			"screenshots_name		TEXT NOT NULL," +
			"thumbnail_name			NVARCHAR(100) NOT NULL," +
			"created_by				NVARCHAR(50) NOT NULL," +
			"created_time			INTEGER NOT NULL," +
			"last_modified_by		NVARCHAR(50) NOT NULL," +
			"last_modified_time		INTEGER NOT NULL," +
			"is_del					CHAR DEFAULT 'F' NOT NULL)";
	
	static {
		DB_CREATE_LIST.add(DOWNLOAD_APPINFO);
	}
	
}
