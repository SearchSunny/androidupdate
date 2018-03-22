package com.android_update.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 *
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();
    // SD卡文件根目录名称
    private static String ROOT_DIRECTORY = "RgdAppUpdate";
    // Apk的更新目录
    public static final String APK_UPDATE_PATH = "apk_update";
    private static FileUtil instance;

    public static synchronized FileUtil getInstance(Context context) {
        if (instance == null) {
            instance = new FileUtil(context.getApplicationContext());
        }
        return instance;
    }
    private FileUtil(Context context) {
        ROOT_DIRECTORY = SpUpdateUtil.getInstance(context).getString(SpUpdateUtil.DOWNLOAD_ROOT_DIRECTORY,"RgdAppUpdate");
    }

    // 更新包路径
    public String getUpdatePath() {
        return getStructureDirs(APK_UPDATE_PATH);
    }

    /**
     * 获取sdcard目录或者文件路径
     *
     * @param directoryPath
     * @param fileName
     * @return
     */
    public String getDirectoryFilePath(String directoryPath, String fileName) {
        String sdPath = getSdcardPath();
        if (sdPath == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(sdPath).append(File.separator).append(ROOT_DIRECTORY);
        if (directoryPath != null) {
            stringBuffer.append(File.separator).append(directoryPath);
        }
        if (fileName != null) {
            stringBuffer.append(File.separator).append(fileName);
        }
        return stringBuffer.toString();
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    private boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 创建文件路径 <br/>
     * dir-[路径名] <br/>
     */
    public String getStructureDirs(String dir) {
        String path = Environment.getExternalStorageDirectory() + File.separator + ROOT_DIRECTORY + File.separator
                + dir;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * 判定SD卡是否挂载
     */
    public boolean checkSdcardMounted() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡跟目录
     */
    public String getSdcardPath() {
        if (checkSdcardMounted()) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public boolean delete(String filePath) {
        boolean flag = false;
        if (TextUtils.isEmpty(filePath)) {
            return flag;
        }
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return file.delete();
                }
                for (File f : childFile) {
                    delete(f.getAbsolutePath());
                }
                file.delete();
            }
        }
        return flag;
    }
}
