package com.android_update.utils;

/**
 * 检测更新标识
 */

public enum CheckFlag {
    /** 网络错误 **/
    CHECK_NO_NETWORK,
    /** SD卡是否可用 **/
    CHECK_SDCARD,
    /** 正常 **/
    NORMAL,
    /** 弹出框 **/
    DIALOG
}
