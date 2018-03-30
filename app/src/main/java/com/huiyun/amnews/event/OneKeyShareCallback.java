package com.huiyun.amnews.event;

import android.app.Activity;
import android.util.Log;

import com.huiyun.amnews.util.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Justy on 2017/7/15.
 */

public class OneKeyShareCallback implements PlatformActionListener {

    Activity activity;

    public OneKeyShareCallback(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Log.e("onComplete",platform.getName());
        ToastUtil.toastshort(activity,"分享成功！");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.e("onError",platform.getName());
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Log.e("onCancel",platform.getName());
        ToastUtil.toastshort(activity,"取消分享！");
    }

}