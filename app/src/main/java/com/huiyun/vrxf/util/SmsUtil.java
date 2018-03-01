package com.huiyun.vrxf.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;


public class SmsUtil {

	private static String username = "70206849";		//用户账号
	private static String password = "Sms123.";		//密码
	
	public static String sendMsg(String phone,String ranNum){
		MD5 getMD5 = new MD5();
//		int num = 100000+(int)(Math.random()*1000000);
//		String content = "感谢您注册约吃饭，验证码" + ranNum;
		System.out.println(CodeUtil.utf82gbk("验证码"));
		String content = ranNum;
		// 创建StringBuffer对象用来操作字符�?
		StringBuffer sb = new StringBuffer("http://api.duanxin.cm/?");
		// 向StringBuffer追加用户�?
		sb.append("action=send&username=");
		sb.append(username);
		// 向StringBuffer追加密码（密码采用MD5 32�? 小写�?
		sb.append("&password=");
		sb.append(getMD5.GetMD5Code(password));
		// 向StringBuffer追加手机号码
		sb.append("&phone=");
		sb.append(phone);
		// 向StringBuffer追加消息内容转URL标准�?
		sb.append("&content="+URLEncoder.encode(content));
		// 创建url对象
		String inputline = "";
		try {
			URL url = new URL(sb.toString());
			// 打开url连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 设置url请求方式 ‘get�? 或�?? ‘post�?
			connection.setRequestMethod("POST");
			// 发�??
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			// 返回发�?�结�?
			inputline = in.readLine();
			// 返回结果为�??100�? 发�?�成�?
			System.out.println(inputline);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return inputline;
	}
	
}
