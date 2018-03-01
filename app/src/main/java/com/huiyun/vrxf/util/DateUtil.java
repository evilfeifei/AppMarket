package com.huiyun.vrxf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.util.Log;


public class DateUtil {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String TimeToDate(int time){
		return sdf.format(new Date(time*1000L));
	}
	
	public static String TimeToShortDate(int time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		return sdf.format(new Date(time*1000L));
	}
	
	
	/**
	 * 将String转换为Date类型
	 * 
	 * @param string
	 * @return Date
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date parseStringToDate(String string) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		Date date = null;
		try {
			date = dateFormat.parse(string);
		} catch (ParseException e) {
			Log.e("parseStringToDate", "转换失败");
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 将时间转换为中文
	 * 
	 * @param datetime
	 * @return
	 */
	public static String parseDateToChinese(Date datetime) {
		Date today = new Date();
		long seconds = (today.getTime() - datetime.getTime()) / 1000;

		long year = seconds / (24 * 60 * 60 * 30 * 12);// 相差年数
		long month = seconds / (24 * 60 * 60 * 30);// 相差月数
		long date = seconds / (24 * 60 * 60); // 相差的天数
		long hour = (seconds - date * 24 * 60 * 60) / (60 * 60);// 相差的小时数
		long minute = (seconds - date * 24 * 60 * 60 - hour * 60 * 60) / (60);// 相差的分钟数
		long second = (seconds - date * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);// 相差的秒数

		if (year > 0) {
			return year + "年前";
		}
		if (month > 0) {
			return month + "月前";
		}
		if (date > 0) {
			return date + "天前";
		}
		if (hour > 0) {
			return hour + "小时前";
		}
		if (minute > 0) {
			return minute + "分钟前";
		}
		if (second > 0) {
			return second + "秒前";
		}
		return parseDateToString(datetime);
	}
	
	/**
	 * 将Date转换为String类型
	 * 
	 * @param date
	 * @return String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String parseDateToString(Date date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			return dateFormat.format(date);
		} catch (Exception e) {
			Log.e("parseDateToString", "转换失败");
			e.printStackTrace();
		}
		return null;
	}

	// 并用分割符把时间分成时间数组
	/**
	 * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14-16-09-00"）
	 *
	 * @param time
	 * @return
	 */
	public static String timesOne(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
		@SuppressWarnings("unused")
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		String times = sdr.format(new Date(i * 1000L));
		return times;

	}

	/**
	 * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
	 *
	 * @param time
	 * @return
	 */
	public String times(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		@SuppressWarnings("unused")
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		String times = sdr.format(new Date(i * 1000L));
		return times;

	}
	/**
	 * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分"）
	 *
	 * @param time
	 * @return
	 */
	public String timet(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
		@SuppressWarnings("unused")
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		String times = sdr.format(new Date(i * 1000L));
		return times;

	}
}
