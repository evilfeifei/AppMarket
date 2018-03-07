package com.huiyun.amnews.configuration;

import android.content.Context;
import android.content.SharedPreferences;

public class AppmarketPreferences {

	private static SharedPreferences preferences;
	private static String preferences_key = "key";
	private static AppmarketPreferences instance;

	public static AppmarketPreferences getInstance(Context mContext) {
		if (instance == null) {
			instance = new AppmarketPreferences();
		}

		if (preferences == null) {
			preferences = mContext.getSharedPreferences(preferences_key, 0);
		}
		return instance;
	}

	public void setStringKey(String key, String value) {
		preferences.edit().putString(key, value).commit();
	}

	public String getStringKey(String key) {
		return preferences.getString(key, "");
	}

	public String getCityKey(String key) {
		return preferences.getString(key, "苏州市");
	}
	
	public void setIntKey(String key, int value) {
		preferences.edit().putInt(key, value).commit();
	}

	public Integer getIntKey(String key) {
		return preferences.getInt(key, 0);
	}

	public void putBooleanKey(String key, boolean setBool) {
		preferences.edit().putBoolean(key, setBool).commit();
	}

	public boolean getBooleanKey(String key) {
		return preferences.getBoolean(key, true);
	}
	
	 
	
}
