package com.huiyun.amnews.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtil {
	/**
	 */
	public static void bitmapRec(Bitmap bitmap){
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
	}
		
	public static Bitmap getRoundBitmap(Bitmap bitmap, float ratio) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float clip, right = 0, left = 0, top = 0, bottom = 0, dstRight, dstLeft, dstTop, dstBottom;
		if (width <= height) {
			clip = (height - width) / 2;
			left = 0;
			right = width;
			top = clip;
			bottom = width + clip;
			dstTop = 0;
			dstLeft = 0;
			dstRight = width;
			dstBottom = width;
			height = width;
		} else {
			clip = (width - height) / 2;
			left = clip;
			right = height + clip;
			top = 0;
			bottom = height;
			dstTop = 0;
			dstLeft = 0;
			dstRight = height;
			dstBottom = height;
			width = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dstLeft, (int) dstTop, (int) dstRight,
				(int) dstBottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, width / ratio, height / ratio, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		
		return output;
	}
	 public static Bitmap getBitmapFromUrl(String url){  
	        URL myFileURL;  
	        Bitmap bitmap=null;  
	        try{  
	            myFileURL = new URL(url);  
	            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();  
	            conn.setConnectTimeout(6000);  
	            conn.setDoInput(true);  
	            //��ʹ�û���  
	            conn.setUseCaches(false);  
	            conn.connect();  
	            InputStream is = conn.getInputStream();  
	            bitmap = BitmapFactory.decodeStream(is);  
	            is.close();  
	        }catch(Exception e){  
	            e.printStackTrace();  
	        }  
	          
	        return bitmap;  
	          
	    }  
	public static Bitmap changeSize(Bitmap bitmap, float targetWidth,
			float targetHeight) {
		float xScale, yScale;
		xScale = targetWidth / bitmap.getWidth();
		yScale = targetHeight / bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(xScale, yScale);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}
	public static Bitmap changeLightness(Bitmap bitmap,float lightness){
		Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		ColorMatrix matrix = new ColorMatrix();
		matrix.reset();
		matrix.setScale(lightness, lightness, lightness, 1);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColorFilter(new ColorMatrixColorFilter(matrix));
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return bmp;
	}
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
    {
     if(null == bitmap || edgeLength <= 0)
     {
      return  null;
     }
     
     Bitmap result = bitmap;
     int widthOrg = bitmap.getWidth();
     int heightOrg = bitmap.getHeight();
     
     if(widthOrg > edgeLength && heightOrg > edgeLength)
     {
      //压缩到一个最小长度是edgeLength的bitmap
      int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
      int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
      int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
      Bitmap scaledBitmap;
            try{
             scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } 
            catch(Exception e){
             return null;
            }
            
         int xTopLeft = (scaledWidth - edgeLength) / 2;
         int yTopLeft = (scaledHeight - edgeLength) / 2;
         
         try{
          result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
          scaledBitmap.recycle();
         }
         catch(Exception e){
          return null;
         }         
     }
          
     return result;
    }
    
    public static Bitmap adjustTheSizeOfBitmap(Bitmap bitmap, int edgeLength)
    {
     if(null == bitmap || edgeLength <= 0)
     {
      return  null;
     }
     
     Bitmap result = bitmap;
     int widthOrg = bitmap.getWidth();
     int heightOrg = bitmap.getHeight();
     
     if(widthOrg > edgeLength && heightOrg > edgeLength)
     {
      int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
      int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
      int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
	    try{
	    	result = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
	    } 
	    catch(Exception e){
	     return null;
	    }
     }
          
     return result;
    }
    
    
}
