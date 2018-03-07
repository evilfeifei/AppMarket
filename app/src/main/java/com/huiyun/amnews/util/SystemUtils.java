package com.huiyun.amnews.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class SystemUtils {
	
	private static float 	sDensity 			= 0;
	
	private static int		sArmArchitecture 	= -1;
	
	public static final String COMMON_NUMBER = "^(\\+86)?[0-9]\\d*$";

    /** 中国的电话 */
    public static final String CN_PHONE = "^(((\\+86)?13[0-9])|((\\+86)?14[5-9])|((\\+86)?15[^4,//D])|((\\+86)?18[0-9]))\\d{8}$";

	public static boolean matcherNumber(String Str) {

		Pattern pattern = Pattern.compile(COMMON_NUMBER);
		return pattern.matcher(Str).matches();
	}
    /**
     * <b>description :</b> 执行正则表达匹配 </br><b>time :</b> 2012-8-16 下午10:03:26
     * 
     * @param regex
     *            正则表达式
     * @param str
     *            字符内容
     * @return 如果正则表达式匹配成功，返回true，否则返回false。
     */
    public static boolean matcherRegex(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }
	
    /**
     * <b>description :</b> 判断手机号码 </br><b>time :</b> 2012-8-16 下午10:03:26
     * 
     * @param number
     *            手机号码
     * @return 如果正则表达式匹配成功，返回true，否则返回false。
     */
    public static boolean matchCNMobileNumber(String number) {
        return number == null ? false : matcherRegex(CN_PHONE, number);
    }
	
	public static int getArmArchitecture() {
		if (sArmArchitecture != -1)
			return sArmArchitecture;
		try {
			InputStream is = new FileInputStream("/proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(ir);
			try {
				String name = "CPU architecture";
				while (true) {
					String line = br.readLine();
					String[] pair = line.split(":");
					if (pair.length != 2)
						continue;
					String key = pair[0].trim();
					String val = pair[1].trim();
					if (key.compareToIgnoreCase(name) == 0) {
						String n = val.substring(0, 1);
						sArmArchitecture = Integer.parseInt(n);
						break;
					}
				}
			} finally {
				br.close();
				ir.close();
				is.close();
				if (sArmArchitecture == -1)
					sArmArchitecture = 6;
			}
		} catch (Exception e) {
			sArmArchitecture = 6;
		}
		return sArmArchitecture;
	}
	
	public static boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	
	public static boolean isWifi(Context context){
		ConnectivityManager cm = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI){
			return true;
		}
		return false;
	}
	
	public static boolean isInBackground(Context context){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> listAppProcessInfo = am.getRunningAppProcesses();
		if(listAppProcessInfo != null){
			final String strPackageName = context.getPackageName();
			for(RunningAppProcessInfo pi : listAppProcessInfo){
			
				if(pi.processName.equals(strPackageName)){
					if(pi.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
							pi.importance != RunningAppProcessInfo.IMPORTANCE_VISIBLE){
						return true;
					}else{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static boolean isNetworkAvailable(Context context){
		ConnectivityManager cm = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED){
			return true;
		}
		return false;
	}
	
	public static void launchHome(Context context){
		Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
	}
	
	public static int randomRange(int nStart,int nEnd){
		if(nStart >= nEnd){
			return nEnd;
		}
		return nStart + (int)(Math.random() * (nEnd - nStart));
	}
	
	public static boolean isExternalStorageMounted(){
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static String getExternalArtWorkCachePath(Context context){
		return Environment.getExternalStorageDirectory().getPath() +
//				"/Android/data/" + context.getPackageName() + "/cache/artwork";
				"/Android/data/" + context.getPackageName() + "/files/artwork";
	}

	public static String getExternalThumbCachePath(Context context){
		return Environment.getExternalStorageDirectory().getPath() +
				"/Android/data/" + context.getPackageName() + "/cach/thumb";
	}
    

	
	private static void deleteFile(String path) {
		try {
			File file = new File(path);
			if(file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static String getDeviceUUID(Context context){
		final TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String strIMEI = tm.getDeviceId();
		if(TextUtils.isEmpty(strIMEI)){
			strIMEI = "1";
		}
		
		String strMacAddress = getMacAddress(context);
		if(TextUtils.isEmpty(strMacAddress)){
			strMacAddress = "1";
		}
		
		return strIMEI + strMacAddress;
	}
	
	public static String getMacAddress(Context context){
		final WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		return wm.getConnectionInfo().getMacAddress();
	}
	
	public static void printCallStack(){
		for(StackTraceElement e : new Throwable().getStackTrace()){
			System.out.println(e.toString());
		}
	}
	
	public static void copyToClipBoard(Context context, String strText){
		final ClipboardManager manager = (ClipboardManager)context.getSystemService(
				Context.CLIPBOARD_SERVICE);
		manager.setText(strText);
	}
	
	public static boolean isEmail(String strEmail){
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(strEmail);
		return matcher.matches();
	}
	
	public static String getVersionName(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	    	ex.printStackTrace();
	    }
	    return null;
	}

	public static void	addEditTextLengthFilter(EditText editText, int nLengthLimit){
		InputFilter filters[] = editText.getFilters();
		if(filters == null){
			editText.getEditableText().setFilters(
					new InputFilter[]{new InputFilter.LengthFilter(nLengthLimit)});
		}else{
			final int nSize = filters.length + 1;
			InputFilter newFilters[] = new InputFilter[nSize];
			int nIndex = 0;
			for(InputFilter filter : filters){
				newFilters[nIndex++] = filter;
			}
			newFilters[nIndex] = new InputFilter.LengthFilter(nLengthLimit);
			editText.getEditableText().setFilters(newFilters);
		}
	}
	
	public static void addEditTextInputFilter(EditText et, InputFilter filter){
		if(filter == null){
			return;
		}
		InputFilter filters[] = et.getFilters();
		if(filters == null){
			et.getEditableText().setFilters(
					new InputFilter[]{filter});
		}else{
			final int nSize = filters.length + 1;
			InputFilter newFilters[] = new InputFilter[nSize];
			int nIndex = 0;
			for(InputFilter f : filters){
				newFilters[nIndex++] = f;
			}
			newFilters[nIndex] = filter;
			et.getEditableText().setFilters(newFilters);
		}
	}
	
	public static boolean getCursorBoolean(Cursor cursor, int nColumnIndex){
		return cursor.getInt(nColumnIndex) == 1 ? true : false;
	}
	
	public static void safeSetImageBitmap(ImageView iv, String path){
		BitmapFactory.Options op = new BitmapFactory.Options();
		computeSampleSize(op, path, 512, 512 * 512);
		try{
			iv.setImageBitmap(BitmapFactory.decodeFile(path, op));
		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}
	}
	

	public static Bitmap getVideoThumbnail(String filePath, int maxSize){
		Bitmap bmp = ThumbnailUtils.createVideoThumbnail(filePath, Images.Thumbnails.MINI_KIND);
		if(bmp != null){
			final int width = bmp.getWidth();
			final int height = bmp.getHeight();
			if(width > maxSize || height > maxSize){
				int fixWidth = 0;
				int fixHeight = 0;
				if(height > width){
					fixHeight = maxSize;
					fixWidth = width * fixHeight / height;
				}else{
					fixWidth = maxSize;
					fixHeight = height * fixWidth / width;
				}
				bmp = Bitmap.createScaledBitmap(bmp, fixWidth, fixHeight, false);
			}
		}
		return bmp;
	}
	
	public static int nextPowerOf2(int n) {
        n -= 1;
        n |= n >>> 16;
        n |= n >>> 8;
        n |= n >>> 4;
        n |= n >>> 2;
        n |= n >>> 1;
        return n + 1;
    }
	
	public static int computeSampleSize(BitmapFactory.Options options, String path,
                                        int minSideLength, int maxNumOfPixels) {
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		if(options.outWidth != -1){
			options.inJustDecodeBounds = false;
			
	        int initialSize = computeInitialSampleSize(options, minSideLength,
	                maxNumOfPixels);

	        int roundedSize;
	        if (initialSize <= 8) {
	            roundedSize = 1;
	            while (roundedSize < initialSize) {
	                roundedSize <<= 1;
	            }
	        } else {
	            roundedSize = (initialSize + 7) / 8 * 8;
	        }
	        
	        options.inSampleSize = roundedSize;

	        return roundedSize;
		}else{
			return -1;
		}
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels < 0) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength < 0) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if (maxNumOfPixels < 0 && minSideLength < 0) {
            return 1;
        } else if (minSideLength < 0) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


	public static Bitmap compressBitmapFile(String srcPath, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFile(srcPath, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		try {
			Bitmap bmp = BitmapFactory.decodeFile(srcPath, options);
			return bmp;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}



	public static Bitmap getNewBitmap(String dstPath, int reqWidth){
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFile(dstPath, options);
		if(options.outWidth > 0){

			   int reqHeight = (reqWidth*options.outHeight) / options.outWidth;
				options.inSampleSize = calculateInSampleSize(options, reqWidth,reqHeight);
				options.inJustDecodeBounds = false;
				try{
					Bitmap bmp = BitmapFactory.decodeFile(dstPath,options);
					return bmp;
				}catch(OutOfMemoryError e){
					e.printStackTrace();
				}
		}
		 return null;
	}

	public static Bitmap ZoomInHeightOrWidth(Context context, Bitmap bitmap, int layoutW, int layoutH)
	{

		int w=bitmap.getWidth();
		int h=bitmap.getHeight();

		//原始图片的宽高比例
		double imgscal = Double.valueOf(w)/ Double.valueOf(h);
		//存放图片的容器的宽高比例
		double imgcontainer = Double.valueOf(layoutW)/ Double.valueOf(layoutH);

		float sx;
		float sy;

		float x;

		if (imgcontainer >= imgscal) {
			// 上下滑
			x = SystemUtils.dip2px(context, layoutW);
			sx=(float)x/w;//要强制转换，不转换我的在这总是死掉。
			sy = sx;
		} else {
			// 左右滑
			x = SystemUtils.dip2px(context, layoutH);
			sy=(float)x/h;//要强制转换，不转换我的在这总是死掉。
			sx = sy;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, w,
				h, matrix, true);
		return resizeBmp;
	}


	public static float getScaling(Context context, Bitmap bitmap, int width, int height)
	{

		int w=bitmap.getWidth();
		int h=bitmap.getHeight();

		float sx;
		float sy;

		float x;

		if(w>h){
			x = SystemUtils.dip2px(context, height);
			sy=(float)x/h;//要强制转换，不转换我的在这总是死掉。
			sx=sy;

		}else{
			x = SystemUtils.dip2px(context, width);
			sx=(float)x/w;//要强制转换，不转换我的在这总是死掉。
			sy=sx;
		}
		return sx;
	}


	//new
	public static Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;//只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 640f;//
		float ww = 360f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置采样率

		newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收\

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		//其实是无效的,大家尽管尝试
//		return bitmap;
	}

	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap dstPathToBitmap(String dstPath, int inSampleSize){
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inSampleSize = inSampleSize;
		Bitmap bitmap = BitmapFactory.decodeFile(dstPath, options);
		return bitmap;
	}

	public static Bitmap ZoomInHeight(String dstPath, float x)
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeFile(dstPath, options);

		int w=bitmap.getWidth();
		int h=bitmap.getHeight();

		float sx =(float)x/w;//要强制转换，不转换我的在这总是死掉。
		float sy=sx;

//		float sx=(float)x/w;//要强制转换，不转换我的在这总是死掉。
//		float sy=sx;
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, w,
				h, matrix, true);
		return resizeBmp;
	}

	public static Bitmap ZoomInWidth(String dstPath, float y)
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeFile(dstPath, options);

		int w=bitmap.getWidth();
		int h=bitmap.getHeight();

		float sy=(float)y/h;//要强制转换，不转换我的在这总是死掉。
		float sx=sy;

		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, w,
				h, matrix, true);
		return resizeBmp;
	}

	public static Bitmap ZoomIWidth(String dstPath, float y)
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(dstPath, options);

		int w=bitmap.getWidth();
		int h=bitmap.getHeight();
		float sy=(float)y/h;//要强制转换，不转换我的在这总是死掉。
		float sx=sy;
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, w,
				h, matrix, true);
		return resizeBmp;
	}


	public static Bitmap decodeSampledBitmapFromFilePath(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(path, options);
        if(options.outWidth > 0){
        	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            try{
            	return BitmapFactory.decodeFile(path,options);
            }catch(OutOfMemoryError e){
            	e.printStackTrace();
            }
        }
        return null;
    }
    
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;

            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
    
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);

            final float totalPixels = width;

            final float totalReqPixelsCap = reqWidth;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
    
    public static boolean isArrayContain(Object[] objs, Object item){
    	for(Object obj : objs){
    		if(obj.equals(item)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public static String throwableToString(Throwable e){
    	StringBuffer sb = new StringBuffer();
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		e.printStackTrace(pw);
		Throwable cause = e.getCause();

		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();
		String result = writer.toString();
		result = result.replaceAll("\n", "\r\n");
		sb.append(result);
		return sb.toString();
    }
    
    public static byte[] objectToByteArray(Object obj) throws IOException {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream bos = new ObjectOutputStream(baos);
		bos.writeObject(obj);
		try{
			return baos.toByteArray();
		}finally{
			bos.close();
		}
    }
    
    public static Object byteArrayToObject(byte[] data) throws
            StreamCorruptedException, IOException, ClassNotFoundException {
    	if(data == null){
    		return null;
    	}
    	ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = new ObjectInputStream(bais);
		try{
			return ois.readObject();
		}finally{
			ois.close();
		}
    }

	public static Bitmap getBitmap(Activity activity, Uri uri) {
		String filePaht = getImageAbsolutePath(activity,uri);
		Bitmap bm = BitmapFactory.decodeFile(filePaht, null);
		return bm;
	}


	public static String getImageAbsolutePath(Activity context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
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


	public static Bitmap convertViewToBitmap(View view){
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
				Bitmap.Config.ARGB_4444);
		//利用bitmap生成画布
		Canvas canvas = new Canvas(bitmap);
//		canvas.drawColor(Color.WHITE);
		//把view中的内容绘制在画布上
		view.draw(canvas);
		return bitmap;
	}

	public static boolean isEmpty(String str) {
		if (str == null||str.equals("")||str.equals("null")) {
			return true;
		}
		return str.trim().length() == 0;
	}

/*	public static String getFromAssets(Context context, String fileName){
		String result = "";
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			int lenght = in.available();
			byte[]  buffer = new byte[lenght];
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}*/

	public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
	{
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}
		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);
		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);
		return bm1;
	}

	public static Bitmap adjustPhotoRotationNew(Bitmap bitmap, final int orientationDegree){
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(bitmap2);
		Matrix matrix = new Matrix();
		matrix.setRotate(orientationDegree, bitmap2.getWidth() / 2, bitmap2.getHeight() / 2);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmap, matrix, paint);
		return bitmap2;
	}

	public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	public static String getNowDate(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return sDateFormat.format(new Date());
	}

	public static String getError(String info){
		Map<String,String> map=new HashMap<String,String>();
		String str = info;
		String[] arr = str.substring(1, str.length() - 1).split(",");
		for(int i = 0;i < arr.length;i++){
			String[] v = arr[i].split(":");
			if(v.length>1) {
				map.put(v[0], v[1]);
			}
		}
		return map.get("error");
	}

	public static String getTextValue(String str)
	{
		String s=new String();
		for(int i=0;i<str.length();i++)
		{
			String text=str.substring(i, i + 1);

			Pattern p = Pattern.compile("[0-9]*");
			Matcher m = p.matcher(text);
			if (m.matches())
			{
				s+=text;
			}
			p = Pattern.compile("[a-zA-Z]");
			m = p.matcher(text);
			if (m.matches())
			{
				s+=text;
			}
			p = Pattern.compile("[\u4e00-\u9fa5]");
			m = p.matcher(text);
			if (m.matches())
			{
				s+=text;
			}
		}
		return s.trim();
	}

	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
	 * @param options
	 */
	private static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
		int inSampleSize = 1;
		if(viewWidth == 0 || viewWidth == 0){
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;

		//假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

			//为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}

	//把字符串转为日期
	public static Date ConverToDate(String strDate) throws Exception
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return df.parse(strDate);
	}



	//时间戳转换成字符窜
	public static String getDateToString(long time){
		Date date = new Date(time);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(date);
	}

	public static long getimeMillis(){
		return  System.currentTimeMillis();
	}

	//把日期转为字符串
	public static String ConverToString(Date date)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		return df.format(date);
	}

	//把字符串转为日期
	public static Date ConverToDate(String strDate, String simpdataf) throws Exception
	{
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		DateFormat df = new SimpleDateFormat(simpdataf);
		return df.parse(strDate);
	}
	//把日期转为字符串
	public static String ConverToString(Date date, String simpdataf)
	{
		DateFormat df = new SimpleDateFormat(simpdataf);

		return df.format(date);
	}

	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree  = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
    * 旋转图片
    * @param angle
    * @param bitmap
    * @return Bitmap
    */
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
		//旋转图片 动作
		Matrix matrix = new Matrix();;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static String getDoubleData(double data){
		DecimalFormat df = new DecimalFormat("#0.00");
		return df.format(data);
	}

	public static Bitmap saveViewBitmap(View view) {
		// get current view bitmap
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		Bitmap bitmap = view.getDrawingCache(true);

		Bitmap bmp = duplicateBitmap(bitmap);
		if (bitmap != null && !bitmap.isRecycled()) { bitmap.recycle(); bitmap = null; }
		// clear the cache
		view.setDrawingCacheEnabled(false);
		return bmp;
	}


	public static Bitmap duplicateBitmap(Bitmap bmpSrc)
	{
		if (null == bmpSrc)
		{ return null; }

		int bmpSrcWidth = bmpSrc.getWidth();
		int bmpSrcHeight = bmpSrc.getHeight();

		Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Bitmap.Config.ARGB_8888); if (null != bmpDest) { Canvas canvas = new Canvas(bmpDest); final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);

		canvas.drawBitmap(bmpSrc, rect, rect, null); }

		return bmpDest;
	}

	/**
	 *
	 * @return if version1 > version2, return 1, if equal, return 0, else return
	 *         -1
	 */
	public static int VersionComparison(String versionServer, String versionLocal) {
		String version1 = versionServer;
		String version2 = versionLocal;
		if (version1 == null || version1.length() == 0 || version2 == null || version2.length() == 0)
			throw new IllegalArgumentException("Invalid parameter!");

		int index1 = 0;
		int index2 = 0;
		while (index1 < version1.length() && index2 < version2.length()) {
			int[] number1 = getValue(version1, index1);
			int[] number2 = getValue(version2, index2);
			if (number1[0] < number2[0]){
				return -1;
			}
			else if (number1[0] > number2[0]){
				return 1;
			}
			else {
				index1 = number1[1] + 1;
				index2 = number2[1] + 1;
			}
		}
		if (index1 == version1.length() && index2 == version2.length())
			return 0;
		if (index1 < version1.length())
			return 1;
		else
			return -1;
	}

	/**
	 *
	 * @param version
	 * @param index
	 *            the starting point
	 * @return the number between two dots, and the index of the dot
	 */
	public static int[] getValue(String version, int index) {
		int[] value_index = new int[2];
		StringBuilder sb = new StringBuilder();
		while (index < version.length() && version.charAt(index) != '.') {
			sb.append(version.charAt(index));
			index++;
		}
		value_index[0] = Integer.parseInt(sb.toString());
		value_index[1] = index;

		return value_index;
	}

	/**
	 * EditText获取焦点并显示软键盘
	 */
	public static void showSoftInputFromWindow(Activity activity, EditText editText) {
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public static String getTowDouble(double d){
		DecimalFormat df=new DecimalFormat(".##");
		return  df.format(d);
	}

	//判断微信是否可用
	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}
		return false;
	}

}
