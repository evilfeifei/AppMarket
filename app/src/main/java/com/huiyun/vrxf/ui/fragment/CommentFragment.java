package com.huiyun.vrxf.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.vrxf.R;
import com.huiyun.vrxf.adapter.CommentAdapter;
import com.huiyun.vrxf.been.Comment;
import com.huiyun.vrxf.configuration.AppmarketPreferences;
import com.huiyun.vrxf.fusion.Constant;
import com.huiyun.vrxf.fusion.PreferenceCode;
import com.huiyun.vrxf.ui.AppDettailsActivity2;
import com.huiyun.vrxf.util.JsonUtil;
import com.huiyun.vrxf.view.RefreshLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
public class CommentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener {

    View rootView;
    private int page = 1;
    private boolean noMore;
    private static int pagesize = 20;
    private RefreshLayout refreshLayout;
    private ListView listView;
    private String userId,token;
    private List<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter commentAdapter;

    private TextView zhpTtv,cyrsTv;
    private RatingBar ratingBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        userId = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.USERID);
        token = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.TOKEN);

        initView(rootView);
        setListener();
        return rootView;
    }

    private void initView(View view){
        zhpTtv = (TextView) view.findViewById(R.id.zhpf_tv);
        cyrsTv = (TextView) view.findViewById(R.id.cyrs_tv);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

        zhpTtv.setText("综合评分"+AppDettailsActivity2.appDettailsActivity.appBean.getComment_score());
        cyrsTv.setText(AppDettailsActivity2.appDettailsActivity.appBean.getComment_count()+"人参与");
        ratingBar.setRating(Float.valueOf(AppDettailsActivity2.appDettailsActivity.appBean.getComment_score()));

        refreshLayout = (RefreshLayout)view.findViewById(R.id.refreshlayout);
        listView = (ListView)view.findViewById(R.id.list);
        refreshLayout.setColorSchemeResources(R.color.black, R.color.app_blue);
        refreshLayout.post(new Thread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }));
        onRefresh();
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadListener(this);

        getActivity().registerReceiver(broadcastReceiver, mIntentFilter());
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().unregisterReceiver(this);
            onRefresh();
        }
    };

    public static IntentFilter mIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppDettailsActivity2.SENDMESSAGE);
        return intentFilter;
    }

    public void getComments(int pageNo){
        RequestParams rp = new RequestParams();
        rp.put("pageNo", pageNo);
        rp.put("appId", AppDettailsActivity2.appDettailsActivity.appBean.getId());
        ahc.post(getActivity(), Constant.APP_GET_COMMENT, rp,
                new JsonHttpResponseHandler(Constant.UNICODE) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            refreshLayout.setRefreshing(false);
                            Map<String, Object> dataMap = (Map<String, Object>) JsonUtil.jsonToMap(response.toString());
                            if (dataMap == null) {
                                return;
                            }
                            Map<String, Object> responseMsg = (Map<String, Object>) dataMap.get("responseMsg");
                            if (responseMsg.get("error").toString().equals("")) {

                                List<Comment> comments = JsonUtil.stringToArray(JsonUtil.objectToJson(responseMsg.get("list")), Comment[].class);
                                if (commentList.size() % pagesize != 0) {
                                    noMore = true;
                                }

                                if (page == 1) {
                                    commentList = new ArrayList<Comment>();
                                }

                                commentList.addAll(comments);

                                if (commentAdapter == null) {
                                    commentAdapter = new CommentAdapter(getActivity(), commentList);
                                }

                                if (page > 1) {
                                    commentAdapter.refreshData(commentList);
                                    return;
                                }

                                listView.setAdapter(commentAdapter);
                                commentAdapter.refreshData(commentList);
                                commentAdapter.notifyDataSetInvalidated();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        Toast.makeText(getActivity(), "请检查网络!", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
        }
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onRefresh() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                noMore = false;
                page = 1;
                getComments(page);

            }
        }, 0);
    }
}
