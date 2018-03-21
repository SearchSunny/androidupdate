package com.android_update.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android_update.R;
import com.android_update.utils.DeviceUtil;

/**
 *
 */

public class UpdateDialog implements View.OnClickListener {

    // 声明Dialog对象
    private Dialog dialog;
    // dialog的标题
    private TextView titleTxt;
    // dialog的内容
    private TextView content;

    // 确定按钮
    private Button btn_ok;
    // 取消按钮
    private Button btn_cancel;

    private LinearLayout custom_dialog_ll;


    // 屏幕的宽度
    private int screenWidth = 0;

    private DialogInterface.OnClickListener ok_Listener;
    private DialogInterface.OnClickListener cancel_Listener;
    /**
     * @param context 上下文对象
     * @param hasCancel 是否显示“取消按钮”
     */
    public UpdateDialog(Context context, boolean hasCancel) {
        screenWidth = (int) DeviceUtil.getDeviceWidth(context);
        dialog = new Dialog(context, R.style.update_CustomDialog);
        initView(hasCancel);
    }

    // 初始化控件
    private void initView(boolean hasCancel) {
        dialog.setContentView(R.layout.dialog_update);
        titleTxt = (TextView) dialog.findViewById(R.id.custom_dialog_title);
        content = (TextView) dialog.findViewById(R.id.custom_dialog_content);
        btn_ok = (Button) dialog.findViewById(R.id.custom_dialog_btnOk);
        btn_ok.setOnClickListener(this);
        btn_cancel = (Button) dialog.findViewById(R.id.custom_dialog_btnCancel);
        btn_cancel.setOnClickListener(this);
        /**
         * 只有调用了该方法，TextView才能不依赖于ScrollView而实现滚动的效果。
         * 要在XML中设置TextView的textcolor，否则，当TextView被触摸时，会灰掉。
         */
        content.setMovementMethod(ScrollingMovementMethod.getInstance());

        // 判断是否需要“取消”按钮
        if (!hasCancel) {
            btn_cancel.setVisibility(View.GONE);
        }

        // 重构对话框的宽度
        if (screenWidth != 0) {
            custom_dialog_ll = (LinearLayout) dialog
                    .findViewById(R.id.custom_dialog_ll);
            ViewGroup.LayoutParams lp = custom_dialog_ll.getLayoutParams();
            lp.width = screenWidth * 3 / 4;
        }

    }

    /**
     * @param title
     *            Dialog的标题
     * @param content
     *            Dialog的内容
     */
    public void setTitleContent(String title, String content) {
        if (TextUtils.isEmpty(title)) {
            titleTxt.setVisibility(View.GONE);
        } else {
            titleTxt.setVisibility(View.VISIBLE);
        }
        this.titleTxt.setText(title);
        this.content.setText(content);
    }

    // 确定按钮设置
    public void setPositiveButton(String buttonOk,
                                  DialogInterface.OnClickListener onClickListener_ok) {
        this.btn_ok.setText(buttonOk);
        this.ok_Listener = onClickListener_ok;
    }

    // 取消按钮设置
    public void setNagetiveButton(String buttonCancel,
                                  DialogInterface.OnClickListener onClickListener_ok) {
        this.btn_cancel.setText(buttonCancel);
        this.cancel_Listener = onClickListener_ok;
    }

    /**
     * @return dialog
     */
    public Dialog getDialog() {
        return dialog;
    }

    /**
     * 显示dialog
     */
    public void show() {
        dialog.show();
    }

    /**
     * 隐藏dialog
     */
    public void dismiss() {
        dialog.cancel();
    }

    public boolean isShowing() {
        return dialog.isShowing();

    }

    public void setCancelable(boolean flag) {
        dialog.setCancelable(flag);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.custom_dialog_btnOk) {
            ok_Listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
        } else if (i == R.id.custom_dialog_btnCancel) {
            cancel_Listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
        }
    }
}
