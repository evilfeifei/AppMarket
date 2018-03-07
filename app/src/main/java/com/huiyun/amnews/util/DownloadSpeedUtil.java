package com.huiyun.amnews.util;

public class DownloadSpeedUtil {

	public static String getDownloadSpeed(long startTime,int downloadSize){
		long curTime = System.currentTimeMillis();
		int usedTime = (int) ((curTime-startTime)/1000);

		if(usedTime==0)usedTime = 1;
		int downloadSpeed = (downloadSize/usedTime)/1024; // 下载速度
		return downloadSpeed + "kb/s";
	}
}
