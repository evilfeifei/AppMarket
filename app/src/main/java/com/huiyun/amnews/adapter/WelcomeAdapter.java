package com.huiyun.amnews.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.view.roundimage.RoundedImageView;

import java.text.DecimalFormat;
import java.util.List;

public class WelcomeAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private Context context;
	List<AppInfo> appInfoList;
	private ChoiceOnClickListener choiceOnClickListener;

	public WelcomeAdapter(Context context, List<AppInfo> appInfoList,ChoiceOnClickListener choiceOnClickListener){
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.appInfoList = appInfoList;
		this.choiceOnClickListener =choiceOnClickListener;
	}

	public void refreshData(List<AppInfo> appBeans) {
		this.appInfoList = appBeans;
		notifyDataSetChanged();
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return appInfoList.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		final int index = position;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_welcome_app, null);
			holder = new ViewHolder();
			holder.appIcon = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
			holder.nameTv = (TextView)convertView.findViewById(R.id.name_tv);
			holder.sizeTv = (TextView)convertView.findViewById(R.id.size_tv);
			holder.choiceImg = (ImageView)convertView.findViewById(R.id.choice_img);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		holder.nameTv.setText(appInfoList.get(position).getName());

		if(appInfoList.get(position).isChoiced()){
			holder.choiceImg.setImageResource(R.drawable.app_choice_icon);
		}else{
			holder.choiceImg.setImageResource(R.drawable.app_un_choice_icon);
		}

		holder.appIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(appInfoList.get(position).isChoiced()){
					appInfoList.get(position).setChoiced(false);
				}else{
					appInfoList.get(position).setChoiced(true);
				}
				choiceOnClickListener.setChoiceListener();
				notifyDataSetChanged();
			}
		});

		DecimalFormat df = new DecimalFormat("#.0");
		double x = Double.valueOf(appInfoList.get(position).getSize()) / 1024 / 1024;
		String size = df.format(x);
		holder.sizeTv.setText(size + "M");

		Glide.with(context)
				.load(Constant.THUMBNAIL_URL_PREFIX + appInfoList.get(position).getThumbnailName())
				.placeholder(R.drawable.vr_default_img)
				.dontAnimate()
				.into(holder.appIcon);
		return convertView;
	}

	private static class ViewHolder
	{
		TextView nameTv,sizeTv;
		RoundedImageView appIcon;
		ImageView choiceImg;
	}

	public interface ChoiceOnClickListener{
		void setChoiceListener();
	}

}
