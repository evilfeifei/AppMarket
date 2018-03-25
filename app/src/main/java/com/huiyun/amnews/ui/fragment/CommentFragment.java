package com.huiyun.amnews.ui.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiyun.amnews.R;
import com.huiyun.amnews.adapter.CommentAdapter;
import com.huiyun.amnews.been.Comment;
import com.huiyun.amnews.configuration.AppmarketPreferences;
import com.huiyun.amnews.fusion.Constant;
import com.huiyun.amnews.fusion.PreferenceCode;
import com.huiyun.amnews.ui.AppDettailsActivity2;
import com.huiyun.amnews.util.JsonUtil;
import com.huiyun.amnews.view.RefreshLayout;
import com.huiyun.amnews.wight.LoadMoreFooter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
@SuppressLint("ValidFragment")
public class CommentFragment extends BaseFragment implements LoadMoreFooter.OnLoadMoreListener {

    View rootView;
    private int page = 1;
    private boolean noMore;
    private static int pagesize = 20;
    private HeaderAndFooterRecyclerView listView;
    private String userId,token;
    private List<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter commentAdapter;

    private TextView zhpTtv,cyrsTv;
    private RatingBar ratingBar;
    private View headerView;
    protected LayoutInflater mInflater;
    private LoadMoreFooter loadMoreFooter;
    private RelativeLayout headerRel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comment, container, false);
        this.mInflater = inflater;
        userId = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.USERID);
        token = AppmarketPreferences.getInstance(getActivity()).getStringKey(PreferenceCode.TOKEN);

        initView(rootView);
        setListener();
        return rootView;
    }

    private void initView(View view){
        listView = (HeaderAndFooterRecyclerView)view.findViewById(R.id.recycler_view);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);

        loadMoreFooter = new LoadMoreFooter(getContext(), listView, this);
        loadMoreFooter.setState(LoadMoreFooter.STATE_DISABLED);

        headerView = mInflater.inflate(R.layout.header_fragment_comment, null);
        listView.addHeaderView(headerView);

        zhpTtv = (TextView) headerView.findViewById(R.id.zhpf_tv);
        cyrsTv = (TextView) headerView.findViewById(R.id.cyrs_tv);
        ratingBar = (RatingBar) headerView.findViewById(R.id.rating_bar);
        headerRel = (RelativeLayout) headerView.findViewById(R.id.header_rel);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)headerRel.getLayoutParams();
        params.width = width;
        headerRel.setLayoutParams(params);

        zhpTtv.setText("综合评分"+AppDettailsActivity2.appDettailsActivity.appBean.getComment_score());
        cyrsTv.setText(AppDettailsActivity2.appDettailsActivity.appBean.getComment_count()+"人参与");
        if(AppDettailsActivity2.appDettailsActivity.appBean.getComment_score()!=null) {
            ratingBar.setRating(Float.valueOf(AppDettailsActivity2.appDettailsActivity.appBean.getComment_score()));
        }

        getComments(page);
    }

    private void setListener() {
        getActivity().registerReceiver(broadcastReceiver, mIntentFilter());
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().unregisterReceiver(this);
            noMore = false;
                page = 1;
                getComments(page);
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

                                if(noMore){
                                    loadMoreFooter.setState(LoadMoreFooter.STATE_FINISHED);
                                }else{
                                    loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
                                }
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


//    public void onRefresh() {
//        refreshLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                noMore = false;
//                page = 1;
//                getComments(page);
//
//            }
//        }, 0);
//    }

    @Override
    public void onLoadMore() {
        if(!noMore){
            page = page+1;
            getComments(page);
        }
    }
}
