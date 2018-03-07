package com.huiyun.amnews.util;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneUtil {

	public static String getPhoneNumber(Context context){  
		String phoneNum;
	    TelephonyManager mTelephonyMgr;   
	    mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    if(mTelephonyMgr != null){
	    	if(mTelephonyMgr.getLine1Number() != null){
	    		if(mTelephonyMgr.getLine1Number().length() > 11){
		    		phoneNum = mTelephonyMgr.getLine1Number().substring(mTelephonyMgr.getLine1Number().length() - 11, mTelephonyMgr.getLine1Number().length());
		    		return phoneNum;
			    }else{
			    	return mTelephonyMgr.getLine1Number();
			    }
	    	}else{
	    		return null;
	    	}
	    }else{
	    	return null;
	    }
	}
}
