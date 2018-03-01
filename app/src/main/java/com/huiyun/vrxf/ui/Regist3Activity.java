package com.huiyun.vrxf.ui;

import java.io.File;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.vrxf.R;
import com.huiyun.vrxf.configuration.AppmarketPreferences;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.huiyun.vrxf.util.FormatTools;
import com.huiyun.vrxf.util.NetworkUtil;
import com.huiyun.vrxf.util.ToastUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

public class Regist3Activity extends BaseActivity implements OnClickListener{
	private LinearLayout back_left_liner;
	private TextView  title,regist_3;
	private ImageView add_head;
	private Bitmap bitmap = null;
	private Context context;
	private static String userId;
	private static String token;
	private PopupWindow selectPopupWindow;
	private View mPopView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		userId = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.USERID);
		token = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.TOKEN);
		
		
		setContentView(R.layout.activity_register3);
		initView();
	}

	private void initView() {
		title = findView(R.id.t_title);
		title.setText("注册");
		back_left_liner = findView(R.id.back_left_liner);
		regist_3 = findView(R.id.regist_3);
		add_head = findView(R.id.add_head);
		add_head.setOnClickListener(this);
		back_left_liner.setOnClickListener(this);
		regist_3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_left_liner:
			this.finish();
			break;
		case R.id.add_head:
//			ShowPickDialog();
			showSelectPhPopupWindow();
			break;
		case R.id.regist_3:
			if(bitmap==null){
				ToastUtil.toastshort(context, "请选择图片");
				return;
			}else{
				String userId = AppmarketPreferences.getInstance(context).getStringKey(PreferenceCode.USERID);
				if(userId.equals("")){
					return ;
				}else{
					if(NetworkUtil.isNetworkConnected(this)){
						getToken(userId);
						regist_3.setEnabled(false);
						beginLoading(Regist3Activity.this);
					}else{
						ToastUtil.toastshort(this, "当前无网络");
					}
				}
			}
			break;
		}
	}
	
	
	private void getToken(final String userId) {
		RequestParams rp = new RequestParams();
		ahc.post(this, Constant.QINIU_TOKEN, rp,
				new JsonHttpResponseHandler(Constant.UNICODE) {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						if(statusCode==200){
							//{"responseMsg":{"error":"","success":"S"}}
							JSONObject jsonObject = response;
							try {
								JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
								if(responseMsg.getString("success").equals("S")){
									String qiniutoken=responseMsg.getString("qiniuToken");
									byte[] data=FormatTools.getInstance().Bitmap2Bytes(bitmap);
									if(NetworkUtil.isNetworkConnected(Regist3Activity.this)){
										uploadImage(data, userId, qiniutoken,context);
									}else{
										ToastUtil.toastshort(Regist3Activity.this, "当前无网络");
									}
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
						regist_3.setEnabled(true);
						Toast.makeText(context, "请检查网络!", Toast.LENGTH_SHORT).show();
					}
				});
	}
	
	//从相册选择
		RelativeLayout layout_choose;
		PopupWindow layout_pop;

		private void showAvatarPop(View v) {
			View viewLayoutView = LayoutInflater.from(this).inflate(
					R.layout.my_choice_head, null);
			layout_choose = (RelativeLayout) viewLayoutView
					.findViewById(R.id.layout_choose);
			//从相册选择
			layout_choose.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					layout_choose.setBackgroundColor(Color.WHITE);
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(intent, 2);
				}
			});
			layout_pop = new PopupWindow(viewLayoutView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			layout_pop.setTouchInterceptor(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						layout_pop.dismiss();
						return true;
					}
					return false;
				}
			});
			layout_pop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
			layout_pop.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
			layout_pop.setTouchable(true);
			layout_pop.setFocusable(true);
			layout_pop.setOutsideTouchable(true);
			layout_pop.setBackgroundDrawable(new BitmapDrawable());
			layout_pop.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
		
//		@Override
//		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//			// TODO Auto-generated method stub
//			super.onActivityResult(requestCode, resultCode, data);
//			switch (requestCode) {
//			case 2:
//				if (layout_pop != null) {
//					layout_pop.dismiss();
//				}
//				Uri uri = null;
//				if (data == null) {
//					return;
//				}
//				if (resultCode == RESULT_OK) {
//					if (!Environment.getExternalStorageState().equals(
//							Environment.MEDIA_MOUNTED)) {
//						Toast.makeText(this, "SD不可用", 1).show();
//						return;
//					}
//					uri = data.getData();
//					if(uri!=null){
////						Bitmap bitmap = null;
//						try {
//							bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//							add_head.setImageBitmap(bitmap);
//						} catch (FileNotFoundException e) {
//							e.printStackTrace();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//					break;
//				}
//			}
//		}
		
//		private void upload(Bitmap bitmap,String key,String token){
//			byte[] data=FormatTools.getInstance().Bitmap2Bytes(bitmap);
//			UploadUtil.uploadImage(data, key, token);
//		}
		public  void uploadImage(byte[] data,String key,final String qiniutoken,final Context context){
			UploadManager uploadManager = new UploadManager();
			uploadManager.put(data, key, qiniutoken,
				new UpCompletionHandler() {
				    public void complete(String key, ResponseInfo info, JSONObject response) {
				        Log.i("qiniu", key);
				        Log.i("qiniu", info.toString());
				        Log.i("qiniu", response.toString());
				        if(getError(info.toString()).equals("null")){
				        	endLoading();
				        	ToastUtil.toastshort(context, "上传成功！");
				        	if(NetworkUtil.isNetworkConnected(Regist3Activity.this)){
				        		saveUserAvatar();
				        	}else{
								ToastUtil.toastshort(Regist3Activity.this, "当前无网络");
							}
				        	AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.AVATAR, key);
				        }else{
				        	regist_3.setEnabled(true);
				        	ToastUtil.toastshort(context, getError(info.toString()));
				        	endLoading();
				        }
				}

			}, null);
		}
		
		
		public void saveUserAvatar(){
			RequestParams rp = new RequestParams();
			rp.put("userId", userId);
			rp.put("token", token);
			rp.put("avatar", userId);
			ahc.post(context, Constant.REGISTER3_URL, rp,
				new JsonHttpResponseHandler(Constant.UNICODE) {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						regist_3.setEnabled(true);
						if(statusCode==200){
							JSONObject jsonObject = response;
							try {
								JSONObject responseMsg = jsonObject.getJSONObject("responseMsg");
								if(responseMsg.getString("success").equals("S")){
									AppmarketPreferences.getInstance(context).setStringKey(PreferenceCode.AVATAR, userId);
//									switchActivity(MyCustomActivity.class, null);
									finish();
									if(LoginActivity.loginActivity!=null){
										LoginActivity.loginActivity.finish();
									}
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
						regist_3.setEnabled(true);
						Toast.makeText(context, "请检查网络!", 1).show();
					}
				});
		}
		
		/**
		 * 选择提示对话框
		 */
/*		private void ShowPickDialog() {
			new AlertDialog.Builder(this)
					.setTitle("设置头像...")
					.setNegativeButton("相册", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							*//**
							 * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，
							 * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
							 *//*
							Intent intent = new Intent(Intent.ACTION_PICK, null);
							
							*//**
							 * 下面这句话，与其它方式写是一样的效果，如果：
							 * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							 * intent.setType(""image/*");设置数据类型
							 * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
							 * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
							 *//*
							intent.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							startActivityForResult(intent, 1);

						}
					})
					.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();
							*//**
							 * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
							 * 文档，you_sdk_path/docs/guide/topics/media/camera.html
							 * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
							 * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正
							 *//*
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							//下面这句指定调用相机拍照后的照片存储的路径
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
									.fromFile(new File(Environment
											.getExternalStorageDirectory(),
											"xiaoma.jpg")));
							startActivityForResult(intent, 2);
						}
					}).show();
		}*/
		
		
		public void showSelectPhPopupWindow(){
			if(mPopView != null){
				mPopView = null;
			}
			mPopView=LayoutInflater.from(this).inflate(R.layout.popup_select_photo, null);
			if(selectPopupWindow == null){
				selectPopupWindow = new PopupWindow(mPopView,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			}
			selectPopupWindow.setOutsideTouchable(true);
			selectPopupWindow.setFocusable(true);
			selectPopupWindow.setTouchable(true);
			selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			selectPopupWindow.showAtLocation(mPopView, Gravity.CENTER, 0, 0);
			
			RelativeLayout selectPhBtn = (RelativeLayout)mPopView.findViewById(R.id.select_photos_btn);
			RelativeLayout takePhBtn = (RelativeLayout)mPopView.findViewById(R.id.take_photos_btn);
			selectPhBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					selectPopupWindow.dismiss();
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, 1);
				}
			});
			takePhBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					selectPopupWindow.dismiss();
					Intent intent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					//下面这句指定调用相机拍照后的照片存储的路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(new File(Environment
									.getExternalStorageDirectory(),
									"xiaoma.jpg")));
					startActivityForResult(intent, 2);
				}
			});
		}
		
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			switch (requestCode) {
			// 如果是直接从相册获取
			case 1:
				if(data.getData()!=null){
					startPhotoZoom(data.getData());
				}
				break;
			// 如果是调用相机拍照时
			case 2:
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/xiaoma.jpg");
				startPhotoZoom(Uri.fromFile(temp));
				break;
			// 取得裁剪后的图片
			case 3:
				/**
				 * 非空判断大家一定要验证，如果不验证的话，
				 * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
				 * 当前功能时，会报NullException，小马只
				 * 在这个地方加下，大家可以根据不同情况在合适的
				 * 地方做判断处理类似情况
				 * 
				 */
				if(data != null){
					setPicToView(data);
				}
				break;
			default:
				break;

			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		
		/**
		 * 裁剪图片方法实现
		 * @param uri
		 */
		public void startPhotoZoom(Uri uri) {
			/*
			 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
			 * yourself_sdk_path/docs/reference/android/content/Intent.html
			 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
			 * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
			 * 制做的了...吼吼
			 */
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
			intent.putExtra("crop", "true");
			// aspectX aspectY 是宽高的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", 150);
			intent.putExtra("outputY", 150);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, 3);
		}
		
		/**
		 * 保存裁剪之后的图片数据
		 * @param picdata
		 */
		private void setPicToView(Intent picdata) {
			Bundle extras = picdata.getExtras();
			if (extras != null) {
				bitmap = extras.getParcelable("data");
				//Drawable drawable = new BitmapDrawable(photo);
				
				/**
				 * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上
				 * 传到服务器，QQ头像上传采用的方法跟这个类似
				 */
				
				/*ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
				byte[] b = stream.toByteArray();
				// 将图片流以字符串形式存储下来
				
				tp = new String(Base64Coder.encodeLines(b));
				这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，
				服务器处理的方法是服务器那边的事了，吼吼
				
				如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
				为我们可以用的图片类型就OK啦...吼吼
				Bitmap dBitmap = BitmapFactory.decodeFile(tp);
				Drawable drawable = new BitmapDrawable(dBitmap);
				*/
				add_head.setImageBitmap(bitmap);
				//add_head.setBackgroundDrawable(drawable);
			}
		}
}
