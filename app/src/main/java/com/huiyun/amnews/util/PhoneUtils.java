package com.huiyun.amnews.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.util.UUID;

/**
 * 获取设备信息工具类
 * Created by lmy on 2015/10/12.
 */
public class PhoneUtils {
    private static final String TAG = "PhoneUtils";

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    //获取导航栏高度
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取手机MODLE,手机型号
     */
    public static String getModel() {
        String model = Build.MODEL;
        return model;
    }

    /**
     * 获取厂商
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机ROM
     */
    public static String getRom() {
        String DISPLAY = Build.DISPLAY; // 如 titanium-userdebug 2.1
        // TITA_K29_00.13.01I 173018 test-keys
        return DISPLAY;
    }

    /**
     * 获取屏幕分辨率
     *
     * @param
     * @return int[]{width,height}
     */
    public static int[] getResolution(Context ctx) {
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        Display screen = wm.getDefaultDisplay();
        return new int[]{screen.getWidth(), screen.getHeight()};
    }

    /**
     * 获取状态栏的高度 (需在屏幕显示后台调用)
     *
     * @param mActivity
     * @return
     */
    public static int getStatusBarHeight(Activity mActivity) {
        Rect frame = new Rect();
        mActivity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取标题栏高度 (需在屏幕显示后台调用)
     *
     * @param mActivity
     * @return
     */
    public static int getTitleBarHeight(Activity mActivity) {
        int contentTop = mActivity.getWindow()
                .findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return contentTop - getStatusBarHeight(mActivity);
    }

    /**
     * 取得手机生产商
     */
    public static String getBrand() {
        String brand = Build.BRAND == null ? "" : Build.BRAND;
        return brand;
    }

    /**
     * 取得手机系统版本
     *
     * @return
     */
    public static String getClientOsVersion() {
        String clientOsVersion = "Android " + Build.VERSION.RELEASE;
        return clientOsVersion;
    }

    /**
     * 获取手机当前Build.VERSION.SDK
     *
     * @return
     */
    public static String getversionsdk() {
        String vsdk = Build.VERSION.SDK;
        return vsdk;
    }

    /**
     * 获取手机CPUABI号
     *
     * @param context
     * @return
     */
    public static String getcpu(Context context) {
        String icpu = Build.CPU_ABI.toString();
        return icpu;
    }

    static final int ERROR = -1;

    /**
     * 外部存储是否可用
     *
     * @return
     */
    static public boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return
     */
    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部空间大小
     *
     * @return
     */
    static public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机外部可用空间大小
     *
     * @return
     */
    static public long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return (availableBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    /**
     * 获取手机外部空间大小
     *
     * @return
     */
    static public long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    static public String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KiB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MiB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    /**
     * 获取手机唯一码
     *
     * @param ctx
     * @return
     */
    public static String getDeviceUUID(Context ctx) {
        if (ctx != null) {
            TelephonyManager tm = (TelephonyManager) ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            return deviceUuid.toString();
        } else {
            return "";
        }
    }

    /**
     * 取得本应用的版本名
     *
     * @param ctx
     * @return
     */

    public static String getVersionName(Context ctx) {
        String versionName = "";
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info;
            info = manager.getPackageInfo(ctx.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            Log.e(TAG, "getVersionName exception:" + e.getMessage());
        }
        return versionName;
    }

    /**
     * 取得本应用的版本名versionCode
     *
     * @param ctx
     * @return
     */

    public static int getVersionCode(Context ctx) {
        int versionCode = 0;
        PackageManager manager = ctx.getPackageManager();

        PackageInfo info;
        try {
            info = manager.getPackageInfo(ctx.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionName exception:" + e.getMessage());
        }
        return versionCode;
    }

    public static String getImei(Context ctx) {
        if (ctx != null) {
            TelephonyManager tm = (TelephonyManager) ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String esn = tm.getDeviceId();// DeviceId(IMEI)
            return esn;
        } else {
            return "";
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取手机厂家
     */
    public static String getPhoneVender() {
        return Build.MANUFACTURER;
    }

    public final static void loadAppMarketPage(Context con,String url) {

        try {
            Uri uri=null;
            if(url!=null&&!(url.equals(""))){
                uri = Uri.parse(url);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            con.startActivity(intent);
        } catch (Exception e1) {
            e1.printStackTrace();
        }


//		try {
//			Uri uri = Uri.parse("market://search?q=pname:" + con.getPackageName());
//			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//			con.startActivity(intent);
//		} catch (Exception e) {
//			e.printStackTrace();
////			UIUtil.showToasts(con, "没有可用的应用市场更新我们的app,请在我们的移动官网中下载");
//
//			try {
//				Uri uri = Uri.parse("http://www.51lfx.com/");
//				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//				con.startActivity(intent);
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//
//		}

        // 搜索: " market://search?q=pname: " + <package>;
        // 详细: "market://details?id= " + <package>;

        // 1. 网页版
        // 通过网页进入自己应用。比如，你的应用名叫: 苏州实时交通 包名为: com.yfz.bus
        // 那只要通过下面的URL即可进入：
        // "https://market.android.com/details?id" + <package>;
        // 搜索某个应用时可以用下面这个:
        // "https://market.android.com/search?q" + <package>;
        // 或者"https://market.android.com/search?q" + <应用名>;
        // 比如: https://market.android.com/details?id=苏州实时交通
    }
}
