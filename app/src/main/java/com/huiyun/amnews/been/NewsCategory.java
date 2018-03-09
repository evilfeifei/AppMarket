package com.huiyun.amnews.been;

import java.io.Serializable;

/**
 * Created by Justy on 2018/3/9.
 */

public class NewsCategory implements Serializable {
    private String name;
    private String alias;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
