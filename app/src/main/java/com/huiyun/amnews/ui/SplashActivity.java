package com.huiyun.amnews.ui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.huiyun.amnews.MainActivity;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.wight.LoadMoreFooter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import okhttp3.Call;
import okhttp3.Response;

@SuppressLint("NewApi")
public class SplashActivity extends BaseActivity {

	private Intent intent;
	private ImageView splash_img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		final View view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);
		splash_img = (ImageView) view.findViewById(R.id.splash_img);
		getImg();
//		if(AppmarketPreferences.getInstance(SplashActivity.this).getBooleanKey(PreferenceCode.IS_FIRST_OPEN)){
//			intent = new Intent(SplashActivity.this,WelcomeActivity.class);
//		}else{
			intent = new Intent(SplashActivity.this,MainActivity.class);
//		}
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				startActivity(intent);
				SplashActivity.this.finish();
			}
		};
		timer.schedule(task, 3000);

		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(2000);
		view.setAnimation(animation);
	}

	private void getImg() {
		HashMap<String, Object> params = new HashMap<>();
		String jsonData = JsonUtil.objectToJson(params);
		OkGo.post(Constant.SPLASH_IMG_)
				.tag(this)
				.cacheKey("splash_data_cache")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
				.cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)    // 缓存模式，详细请看缓存介绍
				.upJson(jsonData)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						if (TextUtils.isEmpty(s)) return;
						Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(s);
						String imgUrl = dataMap.get("pic").toString();
						if(!TextUtils.isEmpty(imgUrl)){
							showImg(SplashActivity.this,imgUrl,splash_img,0);
						}
					}

					@Override
					public void onCacheSuccess(String s, Call call) {
						super.onCacheSuccess(s, call);
						if (TextUtils.isEmpty(s)) return;
						Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(s);
						String imgUrl = dataMap.get("pic").toString();
						if(!TextUtils.isEmpty(imgUrl)){
							showImg(SplashActivity.this,imgUrl,splash_img,0);
						}
					}
					@Override
					public void onError(Call call, Response response, Exception e) {
					}
				});
	}


	private void showImg(Activity activity, Object path, ImageView imageView, int defaultId) {
		if(activity==null){
			return;
		}
		try {
			if (Util.isOnMainThread()) {
				Glide.with(activity)
						.load(path)
						.placeholder(defaultId)
						.dontAnimate()
						.into(imageView);
			}
		}catch (Exception e){}
	}
}
