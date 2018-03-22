package com.android_update;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android_update.service.DownLoadService;
import com.android_update.utils.AndroidUtil;
import com.android_update.utils.CheckFlag;
import com.android_update.utils.ConstantsUtil;
import com.android_update.utils.FileUtil;
import com.android_update.utils.SpUpdateUtil;
import com.android_update.widget.UpdateDialog;
import com.android_update.widget.UpdateProgressDialog;

import java.io.File;

/**
 * 更新管理
 */

public class AppUpdateManager {

    private static final String TAG = AppUpdateManager.class.getSimpleName();

    private Context mContext;
    /**
     * 是否自动检测更新,true自动检测更新,false手动检测更新
     */
    private boolean autoUpdate;
    /**
     * 安装包是否已经下载完成，避免重复下载
     */
    private boolean ready;
    private UpdateDialog updateDialog;
    private static FileUtil fileUtils;
    /**
     *
     * @param context
     * @param root_directory 更新文件根目录名称
     * @return
     */
    public AppUpdateManager(Context context,String root_directory) {
        this.mContext = context;
        //初始化工具类(需先初始化SpUpdateUtil)
        SpUpdateUtil.getInstance(context).putString(SpUpdateUtil.DOWNLOAD_ROOT_DIRECTORY, root_directory);
        fileUtils = FileUtil.getInstance(context);
    }

    /**
     * 检查版本更新
     */
    public CheckFlag checkVersionUpdate(boolean autoDetection) {
        autoUpdate = autoDetection;
        if (!AndroidUtil.isNetworkAvailable(mContext)) {
            return CheckFlag.CHECK_NO_NETWORK;
        }
        if (!AndroidUtil.isSDCardEnable(mContext)) {
            Log.d(TAG, "无SDCard！");
            return CheckFlag.CHECK_SDCARD;
        }
        if (updateDialog == null) {
            updateDialog = new UpdateDialog(mContext, true);
        }
        String filePath = SpUpdateUtil.getInstance(mContext).getString(SpUpdateUtil.DOWNLOAD_FILE_PATH, "");
        Log.d(TAG, "更新功能--文件路径：" + filePath);
        if (filePath != null && !filePath.equals("")) {
            File fileByPath = fileUtils.getFileByPath(filePath);
            // 安装包是否已经下载完成，避免重复下载,防止手动删除安装包
            ready = fileUtils.isFileExists(fileByPath);
            Log.d(TAG, "更新功能--安装包是否已经下载完成：" + ready);
            if (ready) {
                String tempPath = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
                String tempVersion = tempPath.substring(0, tempPath.indexOf("_"));
                Log.d(TAG, "更新功能--文件版本：" + tempVersion);
                String currentVersion = AndroidUtil.getVersionName(mContext);
                Log.d(TAG, "更新功能--当前APP文件版本：" + currentVersion);
                if (tempVersion.equals(currentVersion)) {
                    Log.d(TAG, "更新功能--删除本地下载文件");
                    fileUtils.delete(fileUtils.getUpdatePath());
                    SpUpdateUtil.getInstance(mContext).putString(SpUpdateUtil.DOWNLOAD_FILE_PATH, "");
                } else {
                    Log.d(TAG, "更新功能--文件版本号不相等");
                    showUpdateDialog();
                    return CheckFlag.DIALOG;
                }
            }
        }
        SpUpdateUtil.getInstance(mContext).clearUpdateInfo();
        return CheckFlag.NORMAL;
    }
    /**
     * 显示升级对话框
     */
    public void showUpdateDialog() {

        final SpUpdateUtil spUtils = SpUpdateUtil.getInstance(mContext);
        final String filePath = spUtils.getString(SpUpdateUtil.DOWNLOAD_FILE_PATH, "");
        final boolean isForce = spUtils.getBoolean(SpUpdateUtil.VERSION_IS_FORCE, false);
        String title;// 标题
        String content;// 内容
        String nagetive;// 取消按钮
        String positive;// 确定按钮

        //文案显示start----------------------------
        if (isForce) {// 强制更新
            updateDialog.setCancelable(false);
            title = mContext.getString(R.string.update_soft_update_title);
            content = String.format(mContext.getString(R.string.update_soft_update_force_content),
                    spUtils.getString(SpUpdateUtil.VERSION_NAME, ""), spUtils.getString(SpUpdateUtil.VERSION_SIZE, ""),
                    spUtils.getString(SpUpdateUtil.VERSION_CONTENT, ""));
            nagetive = mContext.getString(R.string.update_soft_update_later);
            positive = mContext.getString(R.string.update_string_install_btn);
        } else {
            updateDialog.setCancelable(true);
            if (ready) {
                title = mContext.getString(R.string.update_soft_update_title);
                content = String.format(mContext.getString(R.string.update_soft_install_content),
                        spUtils.getString(SpUpdateUtil.VERSION_CONTENT, ""));
                nagetive = mContext.getString(R.string.update_soft_update_later);
                positive = mContext.getString(R.string.update_string_install_btn);
            } else {
                title = mContext.getString(R.string.update_soft_update_title);
                content = String.format(mContext.getString(R.string.update_soft_update_default_content),
                        spUtils.getString(SpUpdateUtil.VERSION_NAME, ""), spUtils.getString(SpUpdateUtil.VERSION_SIZE, ""),
                        spUtils.getString(SpUpdateUtil.VERSION_CONTENT, ""));
                nagetive = mContext.getString(R.string.update_soft_update_later);
                positive = mContext.getString(R.string.update_string_install_btn);
            }
        }

        updateDialog.setTitleContent(title, content);
        //文案显示end----------------------------
        //取消操作
        updateDialog.setNagetiveButton(nagetive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //是否强制更新
                if (isForce) {
                    ((Activity) mContext).finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
        //确认操作
        updateDialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //应判断是否正在下载中(从关于界面点击更新或其它界面)
                String status = SpUpdateUtil.getInstance(mContext).getString(SpUpdateUtil.DOWNLOAD_STATUS, "");
                if (status.equals(ConstantsUtil.DOWNLOAD_PROGRESS)) {
                    Toast.makeText(mContext, "最新版本正在下载中", Toast.LENGTH_LONG).show();
                } else {
                    if (isForce) {
                        if (ready) {// 安装
                            File fileByPath = fileUtils.getFileByPath(filePath);
                            AndroidUtil.installAPk(mContext, fileByPath);
                            ((Activity) mContext).finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } else {
                            //弹出下载框
                            showProgressDialog(isForce);
                        }
                    } else {
                        if (ready) {// 安装
                            File fileByPath = fileUtils.getFileByPath(filePath);
                            AndroidUtil.installAPk(mContext, fileByPath);
                        } else {
                            //关于界面不管是强制或非强制都弹出下载框
                            showProgressDialog(isForce);
                        }
                    }
                }
            }
        });
        updateDialog.setCancelable(false);
        updateDialog.show();
    }


    /**
     * 显示下载ProgressBar弹出框
     */
    public void showProgressDialog(final boolean isForce) {
        final SpUpdateUtil spUtils = SpUpdateUtil.getInstance(mContext);
        final String apkURL = spUtils.getString(SpUpdateUtil.VERSION_URL, "");
        final String apkVersion = spUtils.getString(SpUpdateUtil.VERSION_NAME, "");
        //当前网络是否正常
        if (AndroidUtil.isNetworkAvailable(mContext)) {
            final UpdateProgressDialog progressDialog = new UpdateProgressDialog(mContext);
            //是否WIFI环境
            if (AndroidUtil.isWifi(mContext)) {
                progressDialog.setTitle("正在更新");
                progressDialog.setProgressbarVisibility(true, isForce);
                startUpdate(apkURL, false, apkVersion);
            } else {
                //提示当前网络不在WIFI环境
                progressDialog.setTitle("温馨提示");
                progressDialog.setProgressbarVisibility(false, isForce);
                progressDialog.setContent(mContext.getString(R.string.update_soft_no_wifi));
                //取消操作
                progressDialog.setNagetiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                        if (updateDialog != null) {
                            updateDialog.show();
                        }
                    }
                });
                //确认操作
                progressDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setTitle("正在更新");
                        progressDialog.setProgressbarVisibility(true, isForce);
                        startUpdate(apkURL, false, apkVersion);
                    }
                });
            }
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }
    /**
     * 开始下载
     *
     * @param url
     * @param silent 是否静默下载(用于判断下载中的广播接收)
     */
    public void startUpdate(String url, boolean silent, String version) {
        if (AndroidUtil.isNetworkUrl(url)){
            SpUpdateUtil.getInstance(mContext).putBoolean(SpUpdateUtil.DOWNLOAD_SILENCE, silent);
            Intent intent = new Intent(mContext, DownLoadService.class);
            intent.putExtra(ConstantsUtil.APK_DOWNLOAD_URL, url);
            intent.putExtra(ConstantsUtil.APK_DOWNLOAD_VERSION, version);
            mContext.startService(intent);
        }else{
            Log.d(TAG, "更新功能--下载url错误=="+url );
        }

    }
    /**
     * 初始化版本更新广播
     */
    public void initUpdateReceiver(Context context) {
        Log.d(TAG, "更新功能--初始化版本更新广播是否自动检测更新=" + autoUpdate);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsUtil.ACTION_UPDATE_DOWNLOAD_COMPLETE);
        filter.addAction(ConstantsUtil.ACTION_UPDATE_DOWNLOAD_FAILED);
        LocalBroadcastManager.getInstance(context).registerReceiver(apkDownLoadReceiver, filter);
    }

    /**
     * 解除广播接收器注册
     *
     * @param context
     */
    public void unregisterReceiver(Context context) {
        try {
            if (apkDownLoadReceiver != null) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(apkDownLoadReceiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载完成后，接收到广播
     */
    private BroadcastReceiver apkDownLoadReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConstantsUtil.ACTION_UPDATE_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                Log.d(TAG, "更新功能--下载完成是否自动检测更新=" + autoUpdate);
                Log.d(TAG, "更新功能--下载完成是否静默下载=" + SpUpdateUtil.getInstance(mContext).getBoolean(SpUpdateUtil.DOWNLOAD_SILENCE, false));
                if (autoUpdate && SpUpdateUtil.getInstance(mContext).getBoolean(SpUpdateUtil.DOWNLOAD_SILENCE, false)) {
                    // 下载完成对话框
                    showDownLoadCompleteDialog();
                } else {
                    //直接安装
                    String filePath = SpUpdateUtil.getInstance(mContext).getString(SpUpdateUtil.DOWNLOAD_FILE_PATH, "");
                    File fileByPath = fileUtils.getFileByPath(filePath);
                    AndroidUtil.installAPk(mContext, fileByPath);
                    if (SpUpdateUtil.getInstance(mContext).getBoolean(SpUpdateUtil.VERSION_IS_FORCE, false)) {
                        ((Activity) mContext).finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            } else if (ConstantsUtil.ACTION_UPDATE_DOWNLOAD_FAILED.equals(intent.getAction())) {
                Log.d(TAG, "更新功能--文件下载失败");
                if (SpUpdateUtil.getInstance(mContext).getBoolean(SpUpdateUtil.VERSION_IS_FORCE, false)) {
                    ((Activity) mContext).finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    };

    /**
     * 下载完成对话框
     */
    public void showDownLoadCompleteDialog() {
        final SpUpdateUtil spUtils = SpUpdateUtil.getInstance(mContext);
        final String filePath = spUtils.getString(SpUpdateUtil.DOWNLOAD_FILE_PATH, "");
        final boolean isForce = spUtils.getBoolean(SpUpdateUtil.VERSION_IS_FORCE, false);
        String title;// 标题
        String content;// 内容
        String nagetive;// 取消按钮
        String positive;// 确定按钮
        UpdateDialog dialog = new UpdateDialog(mContext, true);
        //文案显示start----------------------------
        if (isForce) {// 强制更新
            dialog.setCancelable(false);
            title = mContext.getString(R.string.update_soft_update_title);
            content = String.format(mContext.getString(R.string.update_soft_update_force_content),
                    spUtils.getString(SpUpdateUtil.VERSION_NAME, ""), spUtils.getString(SpUpdateUtil.VERSION_SIZE, ""),
                    spUtils.getString(SpUpdateUtil.VERSION_CONTENT, ""));
            nagetive = mContext.getString(R.string.update_soft_update_later);
            positive = mContext.getString(R.string.update_string_install_btn);
        } else {
            dialog.setCancelable(true);
            title = mContext.getString(R.string.update_soft_update_title);
            content = String.format(mContext.getString(R.string.update_soft_install_content),
                    spUtils.getString(SpUpdateUtil.VERSION_CONTENT, ""));
            nagetive = mContext.getString(R.string.update_soft_update_later);
            positive = mContext.getString(R.string.update_string_install_btn);
        }
        dialog.setTitleContent(title, content);
        //文案显示end----------------------------
        //取消操作
        dialog.setNagetiveButton(nagetive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //是否强制更新
                if (isForce) {
                    ((Activity) mContext).finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
        //确认安装操作
        dialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                File fileByPath = fileUtils.getFileByPath(filePath);
                AndroidUtil.installAPk(mContext, fileByPath);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}
