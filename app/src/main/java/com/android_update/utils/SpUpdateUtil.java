package com.android_update.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 */

public class SpUpdateUtil {

    public final static String SP_NAME = "download_sp";

    public static final String DOWNLOAD_SILENCE = "download_silence";
    public final static String HAS_NEW_VERSION = "has_new_version";// 安装包大小
    public final static String VERSION_SIZE = "version_size";// 安装包大小
    public final static String VERSION_CONTENT = "version_contetent";// 更新内容
    public final static String VERSION_NAME = "version_name";// 版本号
    public final static String VERSION_URL = "version_url";// 新版本下载地址

    public final static String VERSION_IS_FORCE = "version_isforce";// 是否强制更新
    public final static String DOWNLOAD_STATUS = "download_status";// 下载载是否完成
    public static final String DOWNLOAD_FILE_PATH = "download_file_path";//下载文件路径

    public static final String DOWNLOAD_ROOT_DIRECTORY = "download_root_directory";//SD卡文件根目录名称

    private SharedPreferences sp;
    private static SpUpdateUtil instance;

    private SpUpdateUtil(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SpUpdateUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SpUpdateUtil(context.getApplicationContext());
        }
        return instance;
    }

    public SpUpdateUtil putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
        return this;
    }

    public int getInt(String key, int dValue) {
        return sp.getInt(key, dValue);
    }

    public SpUpdateUtil putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
        return this;
    }

    public long getLong(String key, Long dValue) {
        return sp.getLong(key, dValue);
    }

    public SpUpdateUtil putFloat(String key, float value) {
        sp.edit().putFloat(key, value).apply();
        return this;
    }

    public Float getFloat(String key, Float dValue) {
        return sp.getFloat(key, dValue);
    }

    public SpUpdateUtil putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
        return this;
    }

    public Boolean getBoolean(String key, boolean dValue) {
        return sp.getBoolean(key, dValue);
    }

    public SpUpdateUtil putString(String key, String value) {
        sp.edit().putString(key, value).apply();
        return this;
    }

    public String getString(String key, String dValue) {
        return sp.getString(key, dValue);
    }

    public void remove(String key) {
        if (isExist(key)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public boolean isExist(String key) {
        return sp.contains(key);
    }

    /**
     * 保存新版本信息
     *
     * @param size
     * @param apkUrl
     * @param mustUpdate
     * @param version
     * @param content
     */
    public void saveUpdateInfo(String hasNew, String size, String apkUrl, String mustUpdate, String version,
                               String content) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(HAS_NEW_VERSION, "YES".equals(hasNew));
        editor.putString(VERSION_SIZE, size);
        editor.putString(VERSION_URL, apkUrl);
        editor.putBoolean(VERSION_IS_FORCE, "YES".equals(mustUpdate));
        editor.putString(VERSION_NAME, version);
        editor.putString(VERSION_CONTENT, content);
        editor.apply();
    }

    /**
     * 清空更新信息
     */
    public void clearUpdateInfo() {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(HAS_NEW_VERSION).remove(VERSION_SIZE).remove(VERSION_URL).remove(VERSION_IS_FORCE)
                .remove(VERSION_NAME).remove(VERSION_CONTENT);
        editor.apply();
    }

}
