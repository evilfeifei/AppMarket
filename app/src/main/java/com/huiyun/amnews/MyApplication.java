package com.huiyun.amnews;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.service.DownloadAsync;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;

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
		initOkgo();
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

	private void initOkgo(){
		//必须调用初始化
		OkGo.init(this);

		//以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
		//好处是全局参数统一,特定请求可以特别定制参数
		try {
			//以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
			OkGo.getInstance()
					.debug("OkGo", Level.INFO, true)
					//如果使用默认的 60秒,以下三行也不需要传
					.setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
					.setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
					.setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间

					//可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
					.setCacheMode(CacheMode.NO_CACHE)

					//可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
					.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

					//可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
					.setRetryCount(3)

					//如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//              .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
					.setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

					//可以设置https的证书,以下几种方案根据需要自己设置
					.setCertificates();                                  //方法一：信任所有证书,不安全有风险
//              .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
//              .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
//              //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//               .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

			//配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//               .setHostnameVerifier(new SafeHostnameVerifier())

			//可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })

			//这两行同上，不需要就不要加入
//                    .addCommonHeaders(headers)  //设置全局公共头
//                    .addCommonParams(params);   //设置全局公共参数
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
