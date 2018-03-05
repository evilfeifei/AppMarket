package com.huiyun.vrxf.downLoad;

import android.os.Environment;

import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;

import java.io.File;

/**
 * Created by Justy on 2017/12/7.
 */

public class OkDownLoad {

    private String folder;                                      //下载的默认文件夹
    DownloadManager manger;

    public static OkDownLoad getInstance() {
        return OkDownloadHolder.instance;
    }

    private static class OkDownloadHolder {
        private static final OkDownLoad instance = new OkDownLoad();
    }

    private OkDownLoad() {
        folder = Environment.getExternalStorageDirectory() + File.separator + "download" + File.separator;
        IOUtils.createFolder(folder);
        manger= DownloadService.getDownloadManager();
        manger.setTargetFolder(folder);
        manger.getThreadPool().setCorePoolSize(9);//设置线程数，也就是设置最多同时下载的个数。
    }

    public DownloadManager getManger() {
        return manger;
    }
}
