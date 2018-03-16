package com.huiyun.amnews.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;

import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.myview.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;


/**
 * 基类fragment
 */
public class BaseFragment extends Fragment implements View.OnClickListener{

    protected int x, y;
    protected AsyncHttpClient ahc;
    protected String userPhoneNum;
    protected static int width ,height;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ahc = new AsyncHttpClient();
        userPhoneNum = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.PHONE);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }

    protected void loadNext(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    protected void switchActivity(Class<?> clazz,Bundle bundle){
        Intent intent=new Intent(getActivity(), clazz);
        if(bundle!=null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    private LoadingDialog processDialog;

    protected void beginLoading(String msg,Context context ) {
        if (processDialog == null || !processDialog.isShowing()) {
            processDialog = LoadingDialog.createDialog(context);
            processDialog.setCancelable(false);
            processDialog.setMessage(msg);
            processDialog.show();
        }
    }

    protected void endLoading() {
        if (processDialog != null && processDialog.isShowing()) {
            processDialog.dismiss();
            processDialog = null;
        }
    }

    protected void beginLoading(Context context) {
        if (processDialog == null || !processDialog.isShowing()) {
            processDialog = LoadingDialog.createDialog(context);
            processDialog.setCancelable(false);
            processDialog.show();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
