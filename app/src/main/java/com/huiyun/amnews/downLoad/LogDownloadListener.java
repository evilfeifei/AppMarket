/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huiyun.amnews.downLoad;

import android.util.Log;

import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.listener.DownloadListener;

public class LogDownloadListener extends DownloadListener {

    public LogDownloadListener() {
    }

    @Override
    public void onProgress(DownloadInfo downloadInfo) {
        Log.e("Progress=",downloadInfo.getProgress()+"");
    }

    @Override
    public void onFinish(DownloadInfo downloadInfo) {
        Log.e("Progress=","下载完成");
        Log.e("Progress=",downloadInfo.getFileName());
    }

    @Override
    public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
        Log.e("errorMsg=","下载出错");
//        Log.e("errorMsg=",errorMsg);
    }


}
