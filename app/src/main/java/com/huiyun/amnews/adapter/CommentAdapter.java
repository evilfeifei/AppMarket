package com.huiyun.amnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.Comment;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.DateUtil;
import com.huiyun.amnews.view.roundimage.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends BaseAdapter{

	private List<Comment> comments;
	private Context mContext;

	public CommentAdapter(Context context){
		this.mContext = context;
		comments = new ArrayList<Comment>();
	}

	public CommentAdapter(Context context, List<Comment> comments){
		this.comments = comments;
		this.mContext = context;
	}

	public void refreshData(List<Comment> comments) {
		this.comments = comments;
		notifyDataSetChanged();
	}

	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		final int index = position;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);
			holder = new ViewHolder();
			holder.headIc = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
			holder.userNameTv = (TextView)convertView.findViewById(R.id.user_name_tv);
			holder.timeTv = (TextView)convertView.findViewById(R.id.time_tv);
			holder.contentTv = (TextView)convertView.findViewById(R.id.content_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		holder.userNameTv.setText(comments.get(position).getUsername());
		holder.contentTv.setText(comments.get(position).getContent());
		holder.timeTv.setText(DateUtil.timesOne(comments.get(position).getCreated_time()));

		Glide.with(mContext)
				.load(Constant.HEAD_URL + comments.get(position).getAvatar())
				.dontAnimate()
				.into(holder.headIc);

		return convertView;
	}

	private static class ViewHolder
	{
		TextView userNameTv,timeTv,contentTv;
		RoundedImageView headIc;
	}

	public void addData(List<Comment> comments) {
		this.comments.addAll(comments);
		this.notifyDataSetChanged();
	}

	protected void switchActivity(Class<?> clazz,Bundle bundle){
		Intent intent=new Intent(mContext, clazz);
		if(bundle!=null){
			intent.putExtras(bundle);
		}
		mContext.startActivity(intent);
	}
}
