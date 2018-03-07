package com.huiyun.amnews.util;

import java.io.File;

public class CheckRoot {
	// 判断是否具有ROOT权限
	public static boolean is_root() {

		boolean res = false;

		try {
			if ((!new File("/system/bin/su").exists())
					&& (!new File("/system/xbin/su").exists())) {
				res = false;
			} else {
				res = true;
			}
			;
		} catch (Exception e) {

		}
		return res;
	}
}
