package com.huiyun.amnews.been;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Justy on 2018/3/7.
 */

public class News implements Serializable {

    private List<String> category ;
    private List<String> thumbnail_img ;
    private String news_id;
    private String title;
    private String source;
    private long gmt_publish;
    private int hot_index;
    public boolean selection;
    public String url;

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<String> getThumbnail_img() {
        return thumbnail_img;
    }

    public void setThumbnail_img(List<String> thumbnail_img) {
        this.thumbnail_img = thumbnail_img;
    }

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getGmt_publish() {
        return gmt_publish;
    }

    public void setGmt_publish(long gmt_publish) {
        this.gmt_publish = gmt_publish;
    }

    public int getHot_index() {
        return hot_index;
    }

    public void setHot_index(int hot_index) {
        this.hot_index = hot_index;
    }

    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
