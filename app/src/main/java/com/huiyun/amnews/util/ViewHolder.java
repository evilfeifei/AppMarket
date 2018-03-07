package com.huiyun.amnews.util;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
	
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view,int id){
		SparseArray<View> viewHolder = (SparseArray<View>)view.getTag();
		if(viewHolder==null){
			viewHolder=new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View chileView=viewHolder.get(id);
		if(chileView==null){
			chileView = view.findViewById(id);
			viewHolder.put(id, chileView);
		}
		return (T) chileView;
	}
}
