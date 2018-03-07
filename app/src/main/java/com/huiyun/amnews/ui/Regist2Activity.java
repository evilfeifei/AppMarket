package com.huiyun.amnews.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.SpinnerAdapter;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.util.NetworkUtil;
import com.huiyun.amnews.util.ToastUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Regist2Activity extends BaseActivity implements OnClickListener{
	private LinearLayout back_left_liner;
	private TextView  title,regist_2;
	private EditText nick_name_edit,mAgeEditET;
	private Context context;
	private String gender,zodiac,age,constellation,character,hobby;
	private String nickName;
	private String userId;
	private String token;
	private Spinner sex_spinner,age_spinner,animal_spinner,constellation_spinner,character_spinner,hobby_category_spinner,hobby_spinner;
	private SpinnerAdapter sexSpinnerAdapter,ageSpinnerAdapter,animalSpinnerAdapter,
	constellationSpinnerAdapter,characterSpinnerAdapter,hobbyCategorySpinnerAdapter,hobbySpinnerAdapter;
	private String sexs[] = null,ages[]=null,animals[]=null,constellations[]=null,femaleCharacters[]=null,maleCharacters[]=null,hobbyCategory[]=null,hobby1[]=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context  =this;
		setContentView(R.layout.activity_register2);
		
		userId = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.USERID);
		token = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.TOKEN);
		
		initData();
		initView();
	}

	private void initData(){
		sexs = getResources().getStringArray(R.array.sex_type);
		ages = getResources().getStringArray(R.array.age_type);
		animals = getResources().getStringArray(R.array.animal_type);
		constellations = getResources().getStringArray(R.array.constellation_type);
		femaleCharacters = getResources().getStringArray(R.array.female_character_type);
		maleCharacters = getResources().getStringArray(R.array.male_character_type);
		hobbyCategory = getResources().getStringArray(R.array.hobby_category_type);
		hobby1 = getResources().getStringArray(R.array.hobby_1_type);
	}

	private void initView() {
		title = findView(R.id.t_title);
		title.setText("注册");
		back_left_liner = findView(R.id.back_left_liner);
		back_left_liner.setVisibility(View.VISIBLE);
		regist_2 = findView(R.id.regist_2);
		nick_name_edit = findView(R.id.nick_name_edit);
		
		sex_spinner = findView(R.id.sex_spinner);
		sexSpinnerAdapter = new SpinnerAdapter(context, sexs);
		sex_spinner.setAdapter(sexSpinnerAdapter);
		sex_spinner.setSelection(0,true);
		
		age_spinner = findView(R.id.age_spinner);
		ageSpinnerAdapter = new SpinnerAdapter(context, ages);
		age_spinner.setAdapter(ageSpinnerAdapter);
		age_spinner.setSelection(0,true);
		
		animal_spinner = findView(R.id.animal_spinner);
		animalSpinnerAdapter = new SpinnerAdapter(context, animals);
		animal_spinner.setAdapter(animalSpinnerAdapter);
		animal_spinner.setSelection(0,true);
		
		constellation_spinner = findView(R.id.constellation_spinner);
		constellationSpinnerAdapter = new SpinnerAdapter(context, constellations);
		constellation_spinner.setAdapter(constellationSpinnerAdapter);
		constellation_spinner.setSelection(0,true);
		
		character_spinner = findView(R.id.character_spinner);
		characterSpinnerAdapter = new SpinnerAdapter(context, femaleCharacters);
		character_spinner.setAdapter(characterSpinnerAdapter);
		character_spinner.setSelection(0,true);
		
		hobby_category_spinner = findView(R.id.hobby_category_spinner);
		hobbyCategorySpinnerAdapter = new SpinnerAdapter(context, hobbyCategory);
		hobby_category_spinner.setAdapter(hobbyCategorySpinnerAdapter);
		hobby_category_spinner.setSelection(0,true);
		
		hobby_spinner = findView(R.id.hobby_spinner);
		hobbySpinnerAdapter = new SpinnerAdapter(context, hobby1);
		hobby_spinner.setAdapter(hobbySpinnerAdapter);
		hobby_spinner.setSelection(0,true);
		
		back_left_liner.setOnClickListener(this);
		regist_2.setOnClickListener(this);
		
		addListener();
	}

	private void addListener(){
		sex_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if(sexs[position].equals("男") || sexs[position].equals("中性男")){
					characterSpinnerAdapter = new SpinnerAdapter(context, maleCharacters);
					character_spinner.setAdapter(characterSpinnerAdapter);
				}else{
					characterSpinnerAdapter = new SpinnerAdapter(context, femaleCharacters);
					character_spinner.setAdapter(characterSpinnerAdapter);
				}
				
				gender = sexs[position];
			}
	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		age_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				age = ages[position];
			}
	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		animal_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				zodiac = animals[position];
			}
	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		constellation_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				constellation = constellations[position];
			}
	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		character_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if(gender.equals("男") || gender.equals("中性男")){
					character = maleCharacters[position];
				}else if(gender.equals("女") || gender.equals("中性女")){
					character = femaleCharacters[position];
				}
			}
	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		hobby_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				hobby = hobby1[position];
			}
	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_left_liner:
			this.finish();
			break;
		case R.id.regist_2:
			nickName = nick_name_edit.getText().toString().trim();
			
			if (TextUtils.isEmpty(nickName)) {
				ToastUtil.toastshort(this, "请输入用户名");
				return;
			}
			if(NetworkUtil.isNetworkConnected(this)){
				addUserInfo();
				regist_2.setEnabled(false);
//				switchActivity(Regist3Activity.class, null);
			}else{
				ToastUtil.toastshort(this, "当前无网络");
			}
			break;
		}
	}
	
	public void addUserInfo(){
		RequestParams rp = new RequestParams();
		rp.put("userId", userId);
		rp.put("token", token);
		rp.put("nickName", nickName);
//		rp.put("gender", gender);
//		rp.put("age", age);
//		rp.put("zodiac", zodiac);
//		rp.put("constellation", constellation);
//		rp.put("character", character);
//		rp.put("hobby", hobby);
//		rp.put("pushNumber", 1);
//		rp.put("pushTime", "12：00");
//		rp.put("frequency", "周末");
		ahc.post(context, Constant.REGISTER2_URL, rp,
				new JsonHttpResponseHandler(Constant.UNICODE) {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						regist_2.setEnabled(true);
						if(statusCode==200){
							//{"responseMsg":{"error":"","success":"S"}}
							JSONObject jsonObject = response;
							try {
								JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
								if(responseMsg.getString("success").equals("S")){
									switchActivity(Regist3Activity.class, null);
									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.NICKNAME, nickName);
									finish();
								}else{
									String error =responseMsg.getString("error");
									ToastUtil.toastshort(context, error);
								}
							
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString, throwable);
						throwable.printStackTrace();
						regist_2.setEnabled(true);
						Toast.makeText(context, "请检查网络!", 1).show();
					}
				});
	}
}
