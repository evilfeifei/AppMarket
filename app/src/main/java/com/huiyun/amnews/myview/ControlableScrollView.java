package com.huiyun.amnews.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ControlableScrollView extends ScrollView {

	private OnScrollListener mListener;
	
	public ControlableScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ControlableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ControlableScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mListener != null
                && getHeight() + getScrollY() >= computeVerticalScrollRange()) {
	        mListener.onScroll(this);
	        
		}
	}
	
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mListener = onScrollListener;
    }

    public static interface OnScrollListener {
        public void onScroll(ControlableScrollView v);
    }
}
