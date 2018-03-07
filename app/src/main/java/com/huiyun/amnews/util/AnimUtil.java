package com.huiyun.amnews.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

/**
 * 
 * @author ping
 * @create 2014-11-10 下午8:48:26
 */
public class AnimUtil {
	private static final int durationMillis = 300;
	
	/**
	 * 菜单出现动画
	 * @author ping
	 * @create 2014-11-10 下午8:48:56
	 * @param view
	 * @param durationMillis
	 */
	public static void StartShowMenuAnimation(final View view) {
		view.setVisibility(View.VISIBLE);
		Animation maxAnimation = new ScaleAnimation(0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		maxAnimation.setDuration(durationMillis);
		maxAnimation.setFillAfter(true);
		maxAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
//				view.setVisibility(View.VISIBLE);
			}
		});
		
		view.startAnimation(maxAnimation);
	}
	
	/**
	 * 菜单隐藏动画
	 * @author ping
	 * @create 2014-11-10 下午8:50:51
	 * @param view
	 * @param durationMillis
	 */
	public static void StartHideMenuAnimation(final View view) {
		Animation miniAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		miniAnimation.setDuration(durationMillis);
		miniAnimation.setFillAfter(true);
		miniAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
//				view.setVisibility(View.GONE);
			}
		});
		
		view.startAnimation(miniAnimation);
	}
	
	/**
	 * 菜单点击动画
	 * @author ping
	 * @create 2014-11-10 下午8:50:29
	 * @param view
	 * @param durationMillis
	 */
	public static void StartMenuClickAnimation(final View view) {


		Animation maxAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		maxAnimation.setDuration(durationMillis);
		maxAnimation.setFillAfter(false);
		
		Animation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(durationMillis);
		alphaAnimation.setFillAfter(false);

		AnimationSet animationset = new AnimationSet(true);
		animationset.addAnimation(maxAnimation);
		animationset.addAnimation(alphaAnimation);
		animationset.setDuration(durationMillis);
		animationset.setFillAfter(false);
		animationset.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				Animation alphaAnimation = new AlphaAnimation(0, 1);
				alphaAnimation.setDuration(durationMillis*3);
				alphaAnimation.setFillAfter(true);
				view.startAnimation(alphaAnimation);
			}
		});
		
		view.startAnimation(animationset);
	}
	
	
	// 中间动画
	public static void startCenterAnimationsIn(final View view) {
		RotateAnimation animation =new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);   

		animation.setFillAfter(true);
		animation.setDuration(durationMillis);
		animation.setStartOffset(100);// 下一个动画的偏移时间
		animation.setInterpolator(new OvershootInterpolator(2F));// 动画的效果 弹出再回来的效果
		view.startAnimation(animation);
		
		
//		view.invalidate();
//		Animation bigAnimation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		bigAnimation.setDuration(durationMillis);
//		bigAnimation.setFillAfter(false);
//		
//		Animation alphaAnimation = new AlphaAnimation(1, 0.8f);
//		alphaAnimation.setDuration(durationMillis);
//		alphaAnimation.setFillAfter(false);
//		
//		AnimationSet animationset = new AnimationSet(true);
//		animationset.addAnimation(bigAnimation);
//		animationset.addAnimation(alphaAnimation);
//		animationset.setDuration(durationMillis);
//		animationset.setFillAfter(false);
//		
//		view.startAnimation(animationset);
		
//		 ViewPropertyAnimator.animate(view).scaleX(1.5f).start();
	}
}
