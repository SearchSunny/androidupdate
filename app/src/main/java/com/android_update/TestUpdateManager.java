package com.android_update;

import android.content.Context;

import com.android_update.Interface.OnUpdateListener;
import com.android_update.error.UpdateError;
import com.android_update.utils.CheckType;

/**
 *
 */

public class TestUpdateManager {

    private static final String TAG = TestUpdateManager.class.getSimpleName();

    private Context mContext;
    /**
     * 是否自动检测更新,true自动检测更新,false手动检测更新
     */
    private boolean autoUpdate;

    private OnUpdateListener mOnUpdateListener;


    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.mOnUpdateListener = onUpdateListener;
    }
    public TestUpdateManager(Context context) {
        this.mContext = context;
    }

    private UpdateManager updateManager = UpdateManager.getInstance(mContext, "MyClinic");

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public void checkVersionUpdate(boolean autoDetection) {
        autoUpdate = autoDetection;
        this.mOnUpdateListener.onStart();
        CheckType checkType = updateManager.checkVersionUpdate(autoDetection);
        if (checkType == CheckType.CHECK_NO_NETWORK) {
            this.mOnUpdateListener.onFailure(new UpdateError(UpdateError.CHECK_NO_NETWORK));
        } else if(checkType == CheckType.CHECK_SDCARD){
            this.mOnUpdateListener.onFailure(new UpdateError(UpdateError.CHECK_SDCARD));
        } else if (checkType == CheckType.NORMAL) {
            //网络请求APP更新
           /*HttpCall.getApiService(mContext).getUpdateVersion().enqueue(new HttpCallBack<CheckNewUpdateInfo>(mContext) {
                @Override
                public void onSuccess(CheckNewUpdateInfo updateInfo) {
                    String hasNew = updateInfo.getResult().isHasNew();
                    boolean hasNewVersion = hasNew.equals("YES");
                    mOnUpdateListener.onFinish(hasNewVersion);
                    if (hasNewVersion) {
                        updateManager.doHandleCheckNewVersionResponse(updateInfo);
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
