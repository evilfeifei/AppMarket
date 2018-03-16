package com.huiyun.amnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.Classify;
import com.huiyun.amnews.event.AdapterOnItemClickListener;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.AppDettailsActivity2;

import java.util.List;

public class ClassifyGridviewAdapter extends BaseAdapter{

	private List<Classify> classifyList;
	private Context mContext;
	private AdapterOnItemClickListener adapterOnItemClickListener;
	private int type;


	public ClassifyGridviewAdapter(Context context, List<Classify> classifyList){
		this.classifyList = classifyList;
		this.mContext = context;
	}

	public void setType(int type){
		this.type = type;
	}

	public void setAdapterOnItemClickListener(AdapterOnItemClickListener adapterOnItemClickListener){
		this.adapterOnItemClickListener = adapterOnItemClickListener;
	}

	public void refreshData(List<Classify> classifyList) {
		this.classifyList = classifyList;
		notifyDataSetChanged();
	}

	public int getCount() {
		return classifyList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		final int index = position;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_classify_text, null);
			holder = new ViewHolder();
			holder.nameTv = (TextView)convertView.findViewById(R.id.name_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		holder.nameTv.setText(classifyList.get(position).getName());
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(adapterOnItemClickListener!=null){
					adapterOnItemClickListener.onItemClickListener(classifyList.get(position),type);
				}
			}
		});

		return convertView;
	}

	private static class ViewHolder
	{
		TextView nameTv;
	}

	protected void switchActivity(Class<?> clazz,Bundle bundle){
		Intent intent=new Intent(mContext, clazz);
		if(bundle!=null){
			intent.putExtras(bundle);
		}
		mContext.startActivity(intent);
	}

}
