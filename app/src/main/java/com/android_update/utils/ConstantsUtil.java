package com.android_update.utils;

/**
 *
 */

public class ConstantsUtil {

    public  final static String APK_DOWNLOAD_URL = "url";

    public  final static String APK_DOWNLOAD_VERSION = "version";

    public final static String DOWNLOAD_PROGRESS = "progress";

    public final static int UPDATE_PROGRESS = 0x301;

    public final static int UPDATE_FAILED = 0x302;

    public final static int MAX_PROGRESS = 100;

    /**
     * 下载进度
     */
    public final static String ACTION_UPDATE_PROGRESS = "ACTION_UPDATE_PROGRESS";

    /**
     * 更新包下载完成广播action
     */
    public final static String ACTION_UPDATE_DOWNLOAD_COMPLETE = "ACTION_UPDATE_DOWNLOAD_COMPLETE";
    /**
     * 更新包下载失败广播action
     */
    public final static String ACTION_UPDATE_DOWNLOAD_FAILED = "ACTION_UPDATE_DOWNLOAD_FAILED";

}
