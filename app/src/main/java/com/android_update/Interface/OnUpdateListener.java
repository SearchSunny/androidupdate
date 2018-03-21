package com.android_update.Interface;

import com.android_update.error.UpdateError;

/**
 * 检测更新监听
 */

public interface OnUpdateListener {

    void onStart();

    void onFailure(UpdateError error);

    void onFinish(boolean hasNewVersion);
}
