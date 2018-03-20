package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.ClassifyGridviewAdapter;
import com.huiyun.amnews.adapter.MyFragmentPagerAdapter;
import com.huiyun.amnews.been.AppInfo;
import com.huiyun.amnews.been.Classify;
import com.huiyun.amnews.configuration.DefaultValues;
import com.huiyun.amnews.event.AdapterOnItemClickListener;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.util.ToastUtil;
import com.huiyun.amnews.wight.NoScrollGridView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 分类
 * Created by Justy on 2018/3/12.
 */
@SuppressLint("ValidFragment")
public class ClassifyFragment extends BaseFragment implements AdapterOnItemClickListener {

    @Bind(R.id.app_gridview)
    NoScrollGridView gridviewApp;
    private List<Classify> classifyListApp = new ArrayList<>();

    @Bind(R.id.game_gridview)
    NoScrollGridView gridviewGame;
    private List<Classify> classifyListGame = new ArrayList<>();

    private ClassifyGridviewAdapter classifyGridviewAdapterApp,classifyGridviewAdapterGame;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_classify, container, false);

        ButterKnife.bind(this,rootView);
        initMyView();
        getDataList();
        return rootView;
    }

    private void initMyView(){
        classifyGridviewAdapterApp = new ClassifyGridviewAdapter(getActivity(),classifyListApp);
        gridviewApp.setAdapter(classifyGridviewAdapterApp);
        classifyGridviewAdapterApp.setType(DefaultValues.APP_TYPE_APPLICATION_LIST);
        classifyGridviewAdapterGame = new ClassifyGridviewAdapter(getActivity(),classifyListGame);
        gridviewGame.setAdapter(classifyGridviewAdapterGame);
        classifyGridviewAdapterGame.setType(DefaultValues.APP_TYPE_GAME_LIST);
        classifyGridviewAdapterGame.setAdapterOnItemClickListener(this);
        classifyGridviewAdapterApp.setAdapterOnItemClickListener(this);
    }

    private void getDataList() {
        HashMap<String, Object> params = new HashMap<>();
        String jsonData = JsonUtil.objectToJson(params);
        OkGo.post(Constant.CATEGORY_URL)
                .tag(this)
                .upJson(jsonData)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (TextUtils.isEmpty(s)) return;
                         Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(s);
                        if (dataMap == null) {
                            return;
                        }
                        List<Classify> classifiesApp = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("apps")),Classify[].class);
                        List<Classify> classifiesGame = JsonUtil.stringToArray(JsonUtil.objectToJson(dataMap.get("games")),Classify[].class);

                        classifyListApp.addAll(classifiesApp);
                        classifyListGame.addAll(classifiesGame);
                        classifyGridviewAdapterApp.refreshData(classifyListApp);
                        classifyGridviewAdapterGame.refreshData(classifyListGame);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    @Override
    public void onItemClickListener(Object clazz,int type) {
        Classify classify = (Classify) clazz;
//        ToastUtil.toastshort(getActivity(),classify.getName());

        Intent intent = new Intent(getActivity(),CategoryAppActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("currentClassifyId",classify.getId());
        bundle.putInt("type",type);
        if(type == DefaultValues.APP_TYPE_APPLICATION_LIST) {
            bundle.putSerializable("classifyList", (Serializable) classifyListApp);
        }else{
            bundle.putSerializable("classifyList", (Serializable) classifyListGame);
        }
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }
}
