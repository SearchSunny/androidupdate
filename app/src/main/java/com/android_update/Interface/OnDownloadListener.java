package com.android_update.Interface;

import com.android_update.error.UpdateError;

/**
 * 下载监听接口
 */

public interface OnDownloadListener {

    void onStart();

    void onFailure(UpdateError error);

    void onProgress(int progress);

    void onFinish();
}
