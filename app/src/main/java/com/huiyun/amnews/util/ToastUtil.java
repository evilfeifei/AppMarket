package com.huiyun.amnews.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 *
 */
public final class ToastUtil {
	public static void toastshort(Context context, String msg) {
		Activity activity = (Activity) context;
		if (context != null) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}
