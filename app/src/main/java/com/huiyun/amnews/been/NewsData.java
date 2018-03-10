package com.huiyun.amnews.been;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Justy on 2018/3/10.
 */

public class NewsData implements Serializable {
    private int count;
    private String first_id;
    private String last_id;
    private List<News> news;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFirst_id() {
        return first_id;
    }

    public void setFirst_id(String first_id) {
        this.first_id = first_id;
    }

    public String getLast_id() {
        return last_id;
    }

    public void setLast_id(String last_id) {
        this.last_id = last_id;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
}
