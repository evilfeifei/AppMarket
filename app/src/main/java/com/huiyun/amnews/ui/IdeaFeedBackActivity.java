package com.huiyun.amnews.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huiyun.amnews.R;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.wight.LoadMoreFooter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 意见反馈
 * Created by Justy on 2018/3/20.
 */

public class IdeaFeedBackActivity extends BaseActivity {

    @Bind(R.id.t_title)
    TextView titleTv;
    @Bind(R.id.back_left_liner)
    LinearLayout backLeftLiner;
    @Bind(R.id.idea_edit)
    EditText ideaEdit;
    @Bind(R.id.phone_edit)
    EditText phoneEdit;
    @Bind(R.id.text_num)
    TextView textNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_feedback);
        ButterKnife.bind(this);

        initView();
    }

    private void initView(){
        titleTv.setText("意见反馈");
        backLeftLiner.setVisibility(View.VISIBLE);
        ideaEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = ideaEdit.getText().toString();
                if(!TextUtils.isEmpty(str)){
                    textNum.setText(str.length()+"/200字");
                }else{
                    textNum.setText("0/200字");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick({R.id.back_left_liner,R.id.text_num,R.id.submit_tv})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back_left_liner:
                finish();
                break;
            case R.id.text_num:
                ideaEdit.setText("");
                break;
            case R.id.submit_tv: //提交
                String submitStr = ideaEdit.getText().toString();
                if(TextUtils.isEmpty(submitStr)){
                    ToastUtil.toastshort(IdeaFeedBackActivity.this,"请输入反馈意见！");
                    return;
                }
                if(submitStr.length()<8){
                    ToastUtil.toastshort(IdeaFeedBackActivity.this,"至少8个字哦！");
                    return;
                }
                String phoneStr = phoneEdit.getText().toString();
                if(TextUtils.isEmpty(phoneStr)){
                    ToastUtil.toastshort(IdeaFeedBackActivity.this,"请输入联系方式！");
                    return;
                }
                submit(submitStr,phoneStr, AppmarketPreferences.getInstance(IdeaFeedBackActivity.this).getStringKey(PreferenceCode.USERID));
                break;

        }
    }

    private void submit(String content,String phone,String userid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("content",content);
        params.put("phone",phone);
        params.put("userid",userid);
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.FEED_BACK_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (TextUtils.isEmpty(s)) {
                            ToastUtil.toastshort(IdeaFeedBackActivity.this, "谢谢反馈！");
                            return;
                        }
                        Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(s);
                        if (dataMap == null) {
                            return;
                        }
                        if (dataMap.get("error").toString().equals("")) {
                            ToastUtil.toastshort(IdeaFeedBackActivity.this,"谢谢反馈！");
                        }else{
                            ToastUtil.toastshort(IdeaFeedBackActivity.this,dataMap.get("error").toString());
                        }
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }
}
