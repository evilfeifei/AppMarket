package com.huiyun.amnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.Comment;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.DateUtil;
import com.huiyun.amnews.view.roundimage.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

	private List<Comment> comments;
	private Context mContext;
	private LayoutInflater mInflater;

	public CommentAdapter(Context context){
		this.mContext = context;
		comments = new ArrayList<Comment>();
		mInflater = LayoutInflater.from(context);
	}

	public CommentAdapter(Context context, List<Comment> comments){
		this.comments = comments;
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public void refreshData(List<Comment> comments) {
		this.comments = comments;
		notifyDataSetChanged();
	}

	public int getCount() {
		return comments.size();
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = mInflater.inflate(R.layout.item_comment, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.userNameTv.setText(comments.get(position).getUsername());
		holder.contentTv.setText(comments.get(position).getContent());
		holder.timeTv.setText(DateUtil.timesOne(comments.get(position).getCreated_time()));

		Glide.with(mContext)
				.load(comments.get(position).getAvatar())
				.dontAnimate()
				.into(holder.headIc);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemCount() {
		return comments.size();
	}

    public class ViewHolder extends RecyclerView.ViewHolder {
		TextView userNameTv,timeTv,contentTv;
		RoundedImageView headIc;

		public ViewHolder(View convertView) {
			super(convertView);

			headIc = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
			userNameTv = (TextView)convertView.findViewById(R.id.user_name_tv);
			timeTv = (TextView)convertView.findViewById(R.id.time_tv);
			contentTv = (TextView)convertView.findViewById(R.id.content_tv);
		}
	}

	protected void switchActivity(Class<?> clazz,Bundle bundle){
		Intent intent=new Intent(mContext, clazz);
		if(bundle!=null){
			intent.putExtras(bundle);
		}
		mContext.startActivity(intent);
	}
}
