package com.huiyun.vrxf.adapter;


import com.huiyun.vrxf.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter{
	
	String s[];
	private LayoutInflater inflater;
	private Context context;
	
	public SpinnerAdapter(Context context, String[] s){
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.s = s;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return s.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.spinner_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position == 0){
			holder.text.setText(s[0]);
//			holder.text.setTextColor(context.getResources().getColor(R.color.unchecked));
			holder.text.setTextColor(Color.GRAY);
		} else{
		    holder.text.setText(s[position]);
		    holder.text.setTextColor(Color.BLACK);
		}
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.spinner_dropdown_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(s[position]);
//		holder.text.setTextColor(Color.RED);
		return convertView;
	}

	class ViewHolder{
		TextView text;
	}

}
