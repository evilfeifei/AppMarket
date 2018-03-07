package com.huiyun.amnews.been;

import java.util.List;

public class CategoryAppInfo {
	
	private int id;
	private int icon;
	private String name;
	private int level;
	private CategoryAppInfo categoryAppInfo;
	private List<AppInfo> appInfoList;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<AppInfo> getAppInfoList() {
		return appInfoList;
	}
	public void setAppInfoList(List<AppInfo> appInfoList) {
		this.appInfoList = appInfoList;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public CategoryAppInfo getCategoryAppInfo() {
		return categoryAppInfo;
	}
	public void setCategoryAppInfo(CategoryAppInfo categoryAppInfo) {
		this.categoryAppInfo = categoryAppInfo;
	}
}
