package com.huiyun.vrxf.myview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import com.huiyun.vrxf.R;


public class LoadingDialog extends Dialog {

	private static LoadingDialog loadingDialog = null;
	private static  Context mYcontext;
	
	public LoadingDialog(Context context) {
		super(context);
		this.mYcontext = context;
	}
	
	public LoadingDialog(Context context, int theme)
	{
		super(context, theme);
		this.mYcontext = context;
	}
	
	public static LoadingDialog createDialog(Context context) {
		mYcontext = context;
		loadingDialog = new LoadingDialog(context, R.style.Theme_billing_dialog);
		loadingDialog.setContentView(R.layout.xiangyin_loading_dialog);
		loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		loadingDialog.setCanceledOnTouchOutside(true);
		loadingDialog.setCancelable(true);
		return loadingDialog;
	}
	
	public LoadingDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView)loadingDialog.findViewById(R.id.progress_tv);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return loadingDialog;
	}
	

}
