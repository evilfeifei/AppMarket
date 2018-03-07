package com.huiyun.amnews.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titles;

    public NewFragmentAdapter(@NonNull FragmentManager manager,List<Fragment> fragmentList,List<String> titles) {
        super(manager);
        this.fragmentList = fragmentList;
        this.titles = titles;
    }

    public void update() {
//        fragmentList.get(0).notifyDataSetChanged(user.getRecentReplyList());
//        fragmentList.get(1).notifyDataSetChanged(user.getRecentTopicList());
    }

//    public void update(@NonNull List<Topic> topicList) {
//        List<TopicSimple> topicSimpleList = new ArrayList<>();
//        for (Topic topic : topicList) {
//            topicSimpleList.add(topic);
//        }
//        fragmentList.get(2).notifyDataSetChanged(topicSimpleList);
//    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

}
