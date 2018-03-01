package com.huiyun.vrxf.util;

/**
 * @Title: StringUtil.java 
 * @Package com.rey.demo.util 
 * @Description: 字符串工具类
 * @author reybin reybin0329@gmail.com   
 * @date 2014年4月10日 上午11:34:22 
 * @version V1.0
 */
public class StringUtil {

	/**
	 * @Title: isEmpty 
	 * @Description: 判断字符串是否为空 
	 * @param @param input
	 * @param @return    设定文件 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

}
