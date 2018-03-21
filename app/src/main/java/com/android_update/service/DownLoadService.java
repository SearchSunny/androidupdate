package com.android_update.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android_update.utils.ConstantsUtil;
import com.android_update.utils.FileUtil;
import com.android_update.utils.SpUpdateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 */

public class DownLoadService extends IntentService {

    private static final int BUFFER_SIZE = 100 * 1024;
    private static final String TAG = DownLoadService.class.getSimpleName();

    public DownLoadService() {
        super("DownLoadService");
    }
    /**
     * 执行通过Intent传递过来的work queue
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String urlStr = intent.getStringExtra(ConstantsUtil.APK_DOWNLOAD_URL);
        String version = intent.getStringExtra(ConstantsUtil.APK_DOWNLOAD_VERSION);
        InputStream in = null;
        FileOutputStream out = null;
        FileUtil fileInstance = null;
        try {
            Log.d(TAG, "更新功能--APk更新下载地址==="+urlStr);
            fileInstance = FileUtil.getInstance(this);
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);//建立连接的超时时间
            urlConnection.setReadTimeout(10 * 1000);//传递数据的超时时间
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            String dir = fileInstance.getUpdatePath();
            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File apkFile = new File(dir, version+"_"+apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int oldProgress = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);
                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    Intent localIntent = new Intent(ConstantsUtil.ACTION_UPDATE_PROGRESS);
                    localIntent.putExtra(ConstantsUtil.DOWNLOAD_PROGRESS, progress);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_STATUS, ConstantsUtil.DOWNLOAD_PROGRESS);
                    Log.d(TAG, "更新功能--下载中==="+progress);
                }
                oldProgress = progress;
            }
            //偶尔更新到90%多的情况下也会处理以两行操作
            SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_STATUS, "");
            SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_FILE_PATH, apkFile.getAbsolutePath());
            //发送下载完成广播
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ConstantsUtil.ACTION_UPDATE_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            Log.e(TAG, "download apk file error:" + e.getMessage());
            Log.d(TAG, "更新功能--download apk file error:" + e.getMessage());
            SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_STATUS, "");
            SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_FILE_PATH,"");
            fileInstance.delete(fileInstance.getUpdatePath());
            //发送网络异常广播
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ConstantsUtil.ACTION_UPDATE_DOWNLOAD_FAILED));
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                    Log.e(TAG, "download apk file IOException out error:" + ignored.getMessage());
                    Log.d(TAG, "更新功能--download apk file error:" + ignored.getMessage());
                    SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_STATUS, "");
                    SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_FILE_PATH,"");
                    fileInstance.delete(fileInstance.getUpdatePath());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ConstantsUtil.ACTION_UPDATE_DOWNLOAD_FAILED));
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                    Log.e(TAG, "download apk file IOException in error:" + ignored.getMessage());
                    Log.d(TAG, "更新功能--download apk file error:" + ignored.getMessage());
                    SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_STATUS, "");
                    SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_FILE_PATH,"");
                    fileInstance.delete(fileInstance.getUpdatePath());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ConstantsUtil.ACTION_UPDATE_DOWNLOAD_FAILED));
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "更新功能--DownLoadService--onDestroy===");
        SpUpdateUtil.getInstance(this).putString(SpUpdateUtil.DOWNLOAD_STATUS, "");
    }
}
