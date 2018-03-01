package com.huiyun.vrxf.ui;



import java.lang.reflect.Field;

import com.bumptech.glide.Glide;
import com.huiyun.vrxf.R;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.wight.viewpagerindicator.CirclePageIndicator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ShowImagesActivity extends BaseActivity {

	private ViewPager mViewPager;
	private SharedPreferences share;
	private Context context;
	public String[] mIntroduceImageIds = {};
	public final static String BUNDLE_IMAGENAMES="bundle_imageNames";
	public final static String BUNDLE_PAGERNO="bundle_pagerno";
	public int pageNo;
	private CirclePageIndicator mIndicator;
	private ImageView image_show_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;
		setContentView(R.layout.activity_show_images);
		
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		if(bundle != null){
			if(bundle.get(BUNDLE_IMAGENAMES) != null){
				mIntroduceImageIds = (String[]) bundle.get(BUNDLE_IMAGENAMES);
			}
			if(bundle.get(BUNDLE_PAGERNO) != null){
				pageNo = (Integer) bundle.get(BUNDLE_PAGERNO);
			}
			initWidgetProperty();
			mViewPager.setCurrentItem(pageNo, true);
		}
	}

	protected void initWidgetProperty() {
		mViewPager = (ViewPager) findViewById(R.id.v_view_pager);
		ViewPagerAdapter adapter = new ViewPagerAdapter();
		mViewPager.setAdapter(adapter);
		
		image_show_back = findView(R.id.image_show_back);
		
		mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		mIndicator.setPageColor(getResources().getColor(R.color.collect_text_color));
		mIndicator.setFillColor(getResources().getColor(R.color.gray));
		
		image_show_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	public class ViewPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return mIntroduceImageIds.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View pagerLayout = inflater.inflate(R.layout.image_item,
					null);
			ImageView image = (ImageView) pagerLayout
					.findViewById(R.id.i_pager_image);
			
//			ImageLoaderUtil.getInstance().displayImg(image,
//					Constant.SCREENSHOT_URL_PREFIX + mIntroduceImageIds[position], options);
			Glide.with(ShowImagesActivity.this)
					.load(Constant.SCREENSHOT_URL_PREFIX + mIntroduceImageIds[position])
					.placeholder(R.drawable.vr_default_img)
					.dontAnimate()
					.into(image);

			pagerLayout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
			
			((ViewPager) container).addView(pagerLayout, 0);
			return pagerLayout;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}
	
	
	/**
	 * 设置背景自动适应,屏幕大小。通过裁剪 宽高 让其比列与屏幕比列一致
	 */
	private Drawable getBGAutoadapt(Bitmap bm){
		 //通知栏高度
        int i = getStatusBarHeight(this);
        
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels-i;   // 屏幕高度（像素） 减去通知栏的高度
        System.out.println(height);
        Log.i("图片处理","屏幕宽高-->"+width+","+height+"   图片资源宽高-->"+bm.getWidth()+","+bm.getHeight());
        
        //(1)确定是 裁剪宽度 还是 裁剪 高度， 通过宽高比列
        boolean isTailorWidth=(float)bm.getWidth()/(float)bm.getHeight()>(float)width/(float)height;
        
        if(isTailorWidth){
        //(2)计算裁剪的值  裁剪后的宽高比 要等于屏幕的宽高比
        float caijianWidth=bm.getWidth()-(float)width/(float)height*(float)bm.getHeight();
        Log.i("caijianWidth","caijianWidth-->"+caijianWidth);
        //(左右两边各裁剪一半的值)
        Bitmap bmCajjian_1 = Bitmap.createBitmap(bm, (int)caijianWidth/2, 0, bm.getWidth()-(int)caijianWidth/2, bm.getHeight());
        bitmapRec(bm);//回收
        Bitmap bmCajjian_2 = Bitmap.createBitmap(bmCajjian_1, 0, 0,  bmCajjian_1.getWidth()-(int)caijianWidth/2, bm.getHeight());
        bitmapRec(bmCajjian_1);//回收
        Drawable bd = new BitmapDrawable(getResources(), bmCajjian_2);
        return bd;
        }
        
        else{
        	//(2)计算裁剪的值  裁剪后的宽高比 要等于屏幕的宽高比
            float caijianHeigth=bm.getHeight()-(float)height*(float)bm.getWidth()/(float)width;
            Log.i("caijianHeigth","caijianHeigth-->"+caijianHeigth);
            //(左右两边各裁剪一半的值)
            Bitmap bmCajjian_1 = Bitmap.createBitmap(bm, 0,(int)caijianHeigth/2, bm.getWidth(), bm.getHeight()-(int)caijianHeigth/2);
            Bitmap bmCajjian_2 = Bitmap.createBitmap(bmCajjian_1, 0, 0, bm.getWidth() ,bmCajjian_1.getHeight()-(int)caijianHeigth/2);
            Drawable bd = new BitmapDrawable(getResources(), bmCajjian_2);
            return bd;
        }
	}
	
	/**
	 * 获取通知栏高度
	 */
    public static int getStatusBarHeight(Context context){
    	Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
	/**
	 * 不再使用的图片资源要回收掉
	 */
	private void bitmapRec(Bitmap bitmap){
		if(bitmap != null){
			Log.i("", "回收无用的bitmap");
			bitmap.recycle();
			bitmap = null;
	}
		//垃圾回收
		System.gc();
	}
}
