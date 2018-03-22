package com.android_update;

import android.content.Context;
import android.util.Log;

import com.android_update.Interface.OnUpdateListener;
import com.android_update.entity.CheckNewUpdate;
import com.android_update.entity.CheckNewUpdateInfo;
import com.android_update.error.UpdateError;
import com.android_update.utils.AndroidUtil;
import com.android_update.utils.CheckFlag;
import com.android_update.utils.SpUpdateUtil;

/**
 *
 */

public class ClinicUpdateManager {

    private static final String TAG = ClinicUpdateManager.class.getSimpleName();
    private Context mContext;

    private boolean autoUpdate;//是否自动检测更新,true自动检测更新,false手动检测更新
    private OnUpdateListener mOnUpdateListener;

    private AppUpdateManager updateManager;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.mOnUpdateListener = onUpdateListener;
    }
    public ClinicUpdateManager(Context context) {
        this.mContext = context;
        updateManager = new AppUpdateManager(mContext, "MyClinic");
    };
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public void checkVersionUpdate(boolean autoDetection) {
        autoUpdate = autoDetection;
        this.mOnUpdateListener.onStart();
        CheckFlag checkType = updateManager.checkVersionUpdate(autoDetection);
        if (checkType == CheckFlag.CHECK_NO_NETWORK) {
            this.mOnUpdateListener.onFailure(new UpdateError(UpdateError.CHECK_NO_NETWORK));
        } else if(checkType == CheckFlag.CHECK_SDCARD){
            this.mOnUpdateListener.onFailure(new UpdateError(UpdateError.CHECK_SDCARD));
        } else if (checkType == CheckFlag.NORMAL) {
            //网络请求APP更新
            /*HttpCall.getApiService(mContext).getUpdateVersion().enqueue(new HttpCallBack<CheckNewUpdateInfo>(mContext) {
                @Override
                public void onSuccess(CheckNewUpdateInfo updateInfo) {
                    String hasNew = updateInfo.getResult().isHasNew();
                    boolean hasNewVersion = hasNew.equals("YES");
                    mOnUpdateListener.onFinish(hasNewVersion);
                    if (hasNewVersion) {
                        doHandleCheckNewVersionResponse(updateInfo);
                    } else {
                        FileUtil.getInstance(mContext).delete(FileUtil.getInstance(mContext).getUpdatePath());
                    }
                }
                @Override
                public void onFailure(Call<CheckNewUpdateInfo> call, Throwable t) {
                    super.onFailure(call, t);
                }
            });*/

        }
    }

    /**
     * @param result 服务端返回参数
     * @return 返回是否是最新版本, true表示是最新版本，否则为false
     */
    public void doHandleCheckNewVersionResponse(CheckNewUpdateInfo result) {
        Log.d("test1", "版本更新：" + result);
        try {
            CheckNewUpdate bean = result.getResult();
            String hasNew = bean.isHasNew();
            // 有更新
            String size = bean.getVersionSize();
            String apkURL = bean.getDownloadUrl();
            String mustUpdate = bean.isMustUpdate();
            String version = bean.getVersion();
            String content = bean.getContent();
            // 保存版本更新信息
            SpUpdateUtil.getInstance(mContext).saveUpdateInfo(hasNew, size, apkURL, mustUpdate, version, content);
            if (autoUpdate) {// 自动检查更新
                if (AndroidUtil.isWifi(mContext) && mustUpdate.equals("NO")) {// 当前为wifi且非强制更新开始静默下载
                    updateManager.startUpdate(apkURL, true, version);
                } else {
                    //非WIFI情况下或强制更新情况
                    updateManager.showUpdateDialog();
                }
            } else {// 手动检查更新
                updateManager.showUpdateDialog();
            }
        } catch (Exception e) {
            Log.e("BaseActivity", "JSON数据解析异常");
        }
    }

    /**
     * 初始化版本更新广播
     */
    public void initUpdateReceiver(Context context) {
        updateManager.initUpdateReceiver(context);
    }
    /**
     * 解除广播接收器注册
     * @param context
     */
    public void unregisterReceiver(Context context) {
        updateManager.unregisterReceiver(context);
    }
}
