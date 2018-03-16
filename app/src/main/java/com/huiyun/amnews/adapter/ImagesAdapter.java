package com.huiyun.amnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.ui.ShowImagesActivity;

public class ImagesAdapter extends BaseAdapter{

	private String[] imageNames;
	private Context mContext;


	public ImagesAdapter(Context context, String[] imageNames){
		this.imageNames = imageNames;
		this.mContext = context;
	}
	
	public int getCount() {
		return imageNames.length;
	}

	public String getItem(int position) {
		return imageNames[position];
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Holder holder;
		if (convertView == null) {
		    convertView = View.inflate(parent.getContext(), R.layout.item_images, null);
			holder = new Holder();
			convertView.setTag(holder);

			holder.picture = (ImageView)convertView.findViewById(R.id.image_choice);
		} else {
			holder = (Holder) convertView.getTag();
		}

		Glide.with(mContext)
				.load(imageNames[position])
				.placeholder(R.drawable.vr_default_img)
//				.override(200, 200)
//				.crossFade()
				.dontAnimate()
				.into(holder.picture);

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,ShowImagesActivity.class);
				Bundle bundleImg = new Bundle();
				bundleImg.putInt(ShowImagesActivity.BUNDLE_PAGERNO, position);
				bundleImg.putStringArray(ShowImagesActivity.BUNDLE_IMAGENAMES, imageNames);
				intent.putExtras(bundleImg);
				mContext.startActivity(intent);
			}
		});

		return convertView;

	}

	private final class Holder {
		ImageView picture;
		FrameLayout framelayoutChoice;
	}
}
