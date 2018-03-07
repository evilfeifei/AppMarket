package com.huiyun.amnews.been;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Justy on 2018/3/7.
 */

public class News implements Serializable {

    private List<String> urls ;
    private String name;
    private String from;
    private String content;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
