package com.huiyun.amnews.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.been.News;
import com.huiyun.amnews.util.SystemUtils;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter{

    private Activity activity;
	private List<News> newses;
    private int size;
	protected LayoutInflater mInflater;
    int width,height;
    private static final int TYPE_1 = 1;
    private static final int TYPE_3 = 3;

    public NewsAdapter(Activity activity, List<News> newses) {
		this.activity = activity;
		mInflater = LayoutInflater.from(activity);
		this.newses = newses;
		size = newses.size();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
    }

    public void refreshData(List<News> newses) {
    	this.newses = newses;
    	size = newses.size();
    	notifyDataSetChanged();
    }

	@Override
	public int getItemCount() {
		return newses.size();
	}

    @Override
    public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = null;
        switch (viewType) {

            case TYPE_1:
                View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_type_1, null);
                holder = new Type1ViewHolder(myView);
                break;
            case TYPE_3:
                View serviceView = LayoutInflater.from(activity).inflate(R.layout.item_news_type_3, null);
                holder = new Type3ViewHolder(serviceView);
                break;
            default:
                break;
        }

        return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_1) {
            ((Type1ViewHolder) holder).titleTv.setText(newses.get(position).getName());
            ((Type1ViewHolder) holder).fromTv.setText(newses.get(position).getFrom());
            showHeader(activity, newses.get(position).getUrls().get(0), 0, ((Type1ViewHolder) holder).imageView);

        }else if (holder.getItemViewType() == TYPE_3) {
            ((Type3ViewHolder) holder).titleTv.setText(newses.get(position).getName());
            ((Type3ViewHolder) holder).fromTv.setText(newses.get(position).getFrom());
            showHeader(activity, newses.get(position).getUrls().get(0), 0, ((Type3ViewHolder) holder).imageView1);
            showHeader(activity, newses.get(position).getUrls().get(1), 0, ((Type3ViewHolder) holder).imageView2);
            showHeader(activity, newses.get(position).getUrls().get(2), 0, ((Type3ViewHolder) holder).imageView3);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((Type3ViewHolder) holder).imageView1.getLayoutParams();
            params.width = (width-SystemUtils.dip2px(activity,40))/3;
            params.setMargins(0,0,SystemUtils.dip2px(activity,10),0);
            ((Type3ViewHolder) holder).imageView1.setLayoutParams(params);
            ((Type3ViewHolder) holder).imageView2.setLayoutParams(params);
            ((Type3ViewHolder) holder).imageView3.setLayoutParams(params);
        }
	}

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        if (newses.get(position).getUrls().size()== 1) {
            return TYPE_1;
        } else if (newses.get(position).getUrls().size()== 3) {
            return TYPE_3;
        }else{
            return TYPE_1;
        }
    }

    class Type1ViewHolder extends ViewHolder {
        TextView titleTv,fromTv;
        ImageView imageView;

        public Type1ViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.new_title_tv);
            fromTv = (TextView) itemView.findViewById(R.id.new_from_tv);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

    }

    class Type3ViewHolder extends ViewHolder {
        TextView titleTv,fromTv;
        ImageView imageView1,imageView2,imageView3;

        public Type3ViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.new_title_tv);
            fromTv = (TextView) itemView.findViewById(R.id.new_from_tv);
            imageView1 = (ImageView) itemView.findViewById(R.id.image1);
            imageView2 = (ImageView) itemView.findViewById(R.id.image2);
            imageView3 = (ImageView) itemView.findViewById(R.id.image3);
        }

    }

    public void showHeader(Context activity,String path,int defaultImg,ImageView avatarImg){
        Glide.with(activity)
                .load(path)
                .placeholder(defaultImg)
                .dontAnimate()
                .into(avatarImg);
    }

}
