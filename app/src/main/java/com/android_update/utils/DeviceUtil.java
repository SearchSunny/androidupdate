package com.android_update.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 *
 */

public class DeviceUtil {

    /**
     * 获取屏幕的宽度 <br/>
     *
     * cx-[上下文对象] <br/>
     * @return 屏幕宽度（单位px）
     */
    public static float getDeviceWidth(Context cx) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度 <br/>
     *
     * cx <br/>
     * @return 屏幕高度（单位px）
     */
    public static float getDeviceHight(Context cx) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
