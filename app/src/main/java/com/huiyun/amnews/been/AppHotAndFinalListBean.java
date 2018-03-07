package com.huiyun.amnews.been;

import java.io.Serializable;
import java.util.List;

public class AppHotAndFinalListBean implements Serializable {

	/**
	 *
	 */
    String error;
	private List<AppInfo> menu1;
	private List<AppInfo> menu2;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<AppInfo> getMenu1() {
		return menu1;
	}

	public void setMenu1(List<AppInfo> menu1) {
		this.menu1 = menu1;
	}

	public List<AppInfo> getMenu2() {
		return menu2;
	}

	public void setMenu2(List<AppInfo> menu2) {
		this.menu2 = menu2;
	}
}
