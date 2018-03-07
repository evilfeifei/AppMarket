package com.huiyun.amnews.ui;

import com.huiyun.amnews.R;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutusActivity extends BaseActivity{

	private TextView title;
	private LinearLayout back_left_liner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		
		title = findView(R.id.t_title);
		title.setText("关于我们");
		back_left_liner = findView(R.id.back_left_liner);
		back_left_liner.setOnClickListener(this);
		back_left_liner.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back_left_liner:
			finish();
			break;

		default:
			break;
		}
	}
}
