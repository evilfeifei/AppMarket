package com.huiyun.vrxf.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.huiyun.vrxf.R;

import java.lang.reflect.Field;

;

public class ViewPageScrollAnim {

	// 给viewpager加载动画
	public static void controlViewPagerSpeed(ViewPager v) {
		try {
			Field mField;

			mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			FixedSpeedScroller mScroller = new FixedSpeedScroller(
					v.getContext(), new LinearInterpolator());
			mScroller.setmDuration(300);
			mField.set(v, mScroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 水平平滑动画
	public static class FixedSpeedScroller extends Scroller {
		private int mDuration = 10;

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		public void setmDuration(int time) {
			mDuration = time;
		}

		public int getmDuration() {
			return mDuration;
		}
	}

	public static void enterActivity(Activity outActivity, Class<?> inActivity,
			boolean isfinish) {
		outActivity.startActivity(new Intent(outActivity, inActivity));
		outActivity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
		if (isfinish)
			outActivity.finish();
	}

	public static void exitActivity(Activity outActivity, Class<?> inActivity) {
		outActivity.finish();
		outActivity.overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_right_out);
	}
}
