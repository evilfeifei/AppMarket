package com.huiyun.vrxf;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huiyun.vrxf.configuration.AppmarketPreferences;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.huiyun.vrxf.service.DownloadAsync;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application{
	private static final String TAG = MyApplication.class.getSimpleName();
	private static MyApplication instance;
	
	private List<Map<String, DownloadAsync>> listTask;
	private boolean flag = true;

	public static double latitude, longitude;
	public static String city;
	public static String province;
	public static String address;
	public static LocationClient mLocationClient;
	public static MyLocationListener mMyLocationListener;
	
	public static MyApplication getInstance() {
		if (null == instance)
			instance = new MyApplication();
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		initLocation();
		refreshLocation();
	}

	public List<Map<String, DownloadAsync>> getListTask() {
		if(listTask == null){
			listTask = new ArrayList<Map<String,DownloadAsync>>();
		}
		return listTask;
	}

	public void setListTask(List<Map<String, DownloadAsync>> listTask) {
		this.listTask = listTask;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}



	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(final BDLocation location) {
			//Receive Location
			if(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				address=location.getAddrStr();
				province=location.getProvince();
				city = location.getCity();
			}
			Log.i(TAG, "Addr = " + location.getAddrStr());
			Log.i(TAG, "ltitude = " + location.getAltitude());
			Log.i(TAG, "City = " + location.getCity());
			Log.i(TAG, "Direction = " + location.getDirection());
			Log.i(TAG, "Floor = " + location.getFloor());
			Log.i(TAG, "Latitude = " + location.getLatitude());
			Log.i(TAG, "Longitude = " + location.getLongitude());
			Log.i(TAG, "Province = " + location.getProvince());

			if(location.getCity()!=null&&!location.getCity().equals("")){
//				CommonSettingProvider.setLatitude(instance, latitude+"");
//				CommonSettingProvider.setLongitude(instance, longitude+"");
//                CommonSettingProvider.MainSet.setCurCityName(mContext,city);
				AppmarketPreferences.getInstance(MyApplication.this).setStringKey(PreferenceCode.LOCATION_CITY,city);
			}
		}
	}

	private static void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
		option.setOpenGps(true);
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
//		int span=5000;
//		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		option.setAddrType("all");
		mLocationClient.setLocOption(option);
	}

	public static void refreshLocation() {
		if (mLocationClient != null) {
			// 开始定位
			mLocationClient.start();
			mLocationClient.requestLocation();
		} else {
			mLocationClient = new LocationClient(instance);
			mLocationClient.registerLocationListener(mMyLocationListener);
			initLocation();
			// 开始定位
			mLocationClient.start();
			mLocationClient.requestLocation();
		}
	}
	
}
