package com.huiyun.amnews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.amnews.been.MainEvent;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.myview.dialogview.DialogTips;
import com.huiyun.amnews.ui.BaseActivity;
import com.huiyun.amnews.ui.dialog.WelcomeDialog;
import com.huiyun.amnews.ui.fragment.GameFragment;
import com.huiyun.amnews.ui.fragment.MainFragment;
import com.huiyun.amnews.ui.fragment.MeFragment;
import com.huiyun.amnews.ui.fragment.NewsContainersFragment;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.view.FragmentTabHost;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
//    private Class fragmentArray[] ={MainFragment.class, CategoryAppActivity.class, NewsContainersFragment.class, MeFragment.class};
    private Class fragmentArray[] ={MainFragment.class, GameFragment.class, NewsContainersFragment.class, MeFragment.class};
    private String[] titleArray = {"推荐", "游戏", "头条", "我的"};
    private int[] tabsImage ={R.drawable.icon_recommend_selector,R.drawable.icon_category_selector,R.drawable.icon_small_selector,R.drawable.icon_me_selector};
    private FragmentTabHost mTabHost;
    public static MainActivity instance;
    private Context mContext;
    private int currFragmentIndex; //当前选中的fragment的序号，取值：0-3
    private long curTime;
    public int categoryIndex=0;
    private static final int REQUEST_CODE_CAMERA = 112; //权限请求码



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what){
                case 333:
                    pBar.cancel();
                    update();
                    break;
                case 999:
                    int arg1 = msg.arg1;
                    pBar.setMessage("请稍候"+arg1+"%...");
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        EventBus.getDefault().register(this);   //注册监听返回activity界面
        init();
//        getSwipeBackLayout().setEnableGesture(false);

        if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(this, "应用需要拍照权限", REQUEST_CODE_CAMERA, android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_PHONE_STATE);
        }
//        checkServerUpdate(PhoneUtils.getVersionName(MainActivity.this));
        if(AppmarketPreferences.getInstance(MainActivity.this).getBooleanKey(PreferenceCode.IS_FIRST_OPEN)) {
            initWelcomeDialog();
        }
    }

    /**
     * 接收返回的信息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MainEvent event){
        int position = event.getPosition();
        mTabHost.setCurrentTab(position);
    }

    /**
     * 初始化控件
     */
    protected void init() {
        mContext = this;
        mTabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        mTabHost.setup(mContext, getSupportFragmentManager(), R.id.container);
        mTabHost.getTabWidget().setDividerDrawable(null);
        for (int i = 0; i < titleArray.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(titleArray[i])
                    .setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabName) {
                if (tabName.equals(titleArray[0])) {
                    currFragmentIndex = 0;
                } else if (tabName.equals(titleArray[1])) {
                    currFragmentIndex = 1;

                } else if (tabName.equals(titleArray[2])) {
                    currFragmentIndex = 2;
                } else if (tabName.equals(titleArray[3])) {
                    currFragmentIndex = 3;
                }
            }
        });

    }

    public void setCurrentTab(int currFragmentIndex,int categoryIndex){
        mTabHost.setCurrentTab(currFragmentIndex);
        this.categoryIndex = categoryIndex;
    }


    private View getTabItemView(int index) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.tab_bottom_nav, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
        imageView.setImageResource(tabsImage[index]);
        TextView textView = (TextView) view.findViewById(R.id.tv_icon);
        textView.setText(titleArray[index]);
        return view;
    }



    @Override
    public void onBackPressed() {
        if (curTime == 0 || System.currentTimeMillis() - curTime > 2000) {
            curTime = System.currentTimeMillis();
//            showToast("再按一次，退出程序");
            ToastUtil.toastshort(MainActivity.this,"再按一次，退出程序");
        } else if (curTime != 0
                && System.currentTimeMillis() - curTime < 2000) {
            finish();
        }
    }


    public void checkServerUpdate(String version) {
        RequestParams rp = new RequestParams();
        ahc.post(this, Constant.APP_UPDATE+version, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                JSONObject jsonObject = response;
                                if(jsonObject!=null&&!jsonObject.toString().equals("")){
                                    String url  = jsonObject.getString("url");
                                    if(url!=null&&!url.equals("")){
                                        isUpdate(url);
                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(MainActivity.this, "请检查网络!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    ProgressDialog pBar;
    private void isUpdate(final String updateUrl){
        DialogTips dialog = new DialogTips(this,"检测到有新版本是否更新?","立即更新","取消", "温馨提示",false);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
//                Toast.makeText(MainActivity.this,"dddd",Toast.LENGTH_SHORT).show();
//                PhoneUtils.loadAppMarketPage(MainActivity.this, updateUrl);

                pBar = new ProgressDialog(MainActivity.this);
                pBar.setTitle("正在下载");
                pBar.setMessage("请稍候...");
                pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                downFile(updateUrl);
        }
        });

        dialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        // 显示确认对话框
        dialog.show();
    }

    void downFile(final String url) {
        pBar.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                Constants.UPDATE_SAVENAME);
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                                Message message = new Message();
                                message.what=999;
                                message.arg1 = (int) (count * 100 / length);
                                handler.sendMessage(message);
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
//                    down();
                    handler.sendEmptyMessage(333);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

        void update() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), Constants.UPDATE_SAVENAME)),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE_CAMERA) {
//            LogUtils.v("权限同意了");
        }
    }

    //权限拒绝
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        LogUtils.d("onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            }
        }
    }

    private void initWelcomeDialog() {
        WelcomeDialog.Builder welcomeDialog = new WelcomeDialog.Builder(MainActivity.this,ahc);
        final WelcomeDialog welcomeDialogV = welcomeDialog.create();
        welcomeDialogV.setCanceledOnTouchOutside(false);// 点击外部区域关闭
        welcomeDialogV.show();
    }

}
