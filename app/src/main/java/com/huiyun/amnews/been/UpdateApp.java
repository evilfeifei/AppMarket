package com.huiyun.amnews.been;

import java.io.Serializable;

/**
 * Created by Justy on 2018/3/25.
 */

public class UpdateApp implements Serializable {
    private String package_name;
    private String version_name;

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }
}
