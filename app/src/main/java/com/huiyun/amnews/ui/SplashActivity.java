package com.huiyun.amnews.ui;
import java.util.Timer;
import java.util.TimerTask;

import com.huiyun.amnews.MainActivity;
import com.huiyun.amnews.R;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.PreferenceCode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

@SuppressLint("NewApi")
public class SplashActivity extends BaseActivity {

	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		final View view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);

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
}
