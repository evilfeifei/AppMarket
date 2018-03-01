package com.huiyun.vrxf.ui;
import java.util.Timer;
import java.util.TimerTask;

import com.huiyun.vrxf.MainActivity;
import com.huiyun.vrxf.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

@SuppressLint("NewApi")
public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);

//		final Intent intent = new Intent(SplashActivity.this,
//				LeadActivity.class);
		final Intent intent = new Intent(SplashActivity.this,
				MainActivity.class);
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
