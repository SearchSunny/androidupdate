package com.android_update.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android_update.R;
import com.android_update.utils.ConstantsUtil;
import com.android_update.utils.DeviceUtil;


/**
 * 自定义ProgressDialog
 */

public class UpdateProgressDialog implements View.OnClickListener {

    private static final String TAG = UpdateProgressDialog.class.getSimpleName();
    // 声明Dialog对象
    private Dialog dialog;

    private Context mContext;

    private TextView custom_dialog_title;
    //下载进度条
    private NumberProgressBar progressBar;
    // 确定按钮
    private Button btn_ok;
    // 取消按钮
    private Button btn_cancel;
    //后台下载
    private Button btn_back_down;

    private LinearLayout custom_progress_dialog;

    private LinearLayout linear_dialog_bar;

    private LinearLayout linear_nowifi;

    private TextView custom_dialog_content;

    // 屏幕的宽度
    private int screenWidth = 0;

    private DialogInterface.OnClickListener ok_Listener;
    private DialogInterface.OnClickListener cancel_Listener;

    public UpdateProgressDialog(Context context) {
        this.mContext = context;
        screenWidth = (int) DeviceUtil.getDeviceWidth(context);
        dialog = new Dialog(context, R.style.update_CustomDialog);
        initView();
    }

    // 初始化控件
    private void initView() {
        dialog.setContentView(R.layout.dialogprogress_layout);
        custom_dialog_title = (TextView) dialog.findViewById(R.id.custom_dialog_title);

        linear_dialog_bar = (LinearLayout) dialog.findViewById(R.id.linear_dialog_bar);
        progressBar = (NumberProgressBar) dialog.findViewById(R.id.custom_dialog_bar);

        linear_nowifi = (LinearLayout) dialog.findViewById(R.id.linear_nowifi);
        custom_dialog_content = (TextView) dialog.findViewById(R.id.custom_dialog_content);

        btn_ok = (Button) dialog.findViewById(R.id.custom_dialog_btnOk);
        btn_ok.setOnClickListener(this);
        btn_cancel = (Button) dialog.findViewById(R.id.custom_dialog_btnCancel);
        btn_cancel.setOnClickListener(this);

        btn_back_down = (Button) dialog.findViewById(R.id.btn_back_down);
        btn_back_down.setOnClickListener(this);
        // 重构对话框的宽度
        if (screenWidth != 0) {
            custom_progress_dialog = (LinearLayout) dialog.findViewById(R.id.custom_progress_dialog);
            ViewGroup.LayoutParams lp = custom_progress_dialog.getLayoutParams();
            lp.width = screenWidth * 3 / 4;
        }
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            custom_dialog_title.setVisibility(View.GONE);
        } else {
            custom_dialog_title.setVisibility(View.VISIBLE);
        }
        this.custom_dialog_title.setText(title);
    }

    public void setContent(String content) {
        if (TextUtils.isEmpty(content)) {
            custom_dialog_content.setVisibility(View.GONE);
        } else {
            custom_dialog_content.setVisibility(View.VISIBLE);
        }
        this.custom_dialog_content.setText(content);
    }

    public void setProgressbarVisibility(boolean isVisibilityBar,boolean isForce) {
        if (isForce){
            this.btn_back_down.setVisibility(View.GONE);
            if (isVisibilityBar) {
                this.linear_dialog_bar.setVisibility(View.VISIBLE);
                this.progressBar.setVisibility(View.VISIBLE);
                this.linear_nowifi.setVisibility(View.GONE);
            } else {
                this.linear_dialog_bar.setVisibility(View.GONE);
                this.progressBar.setVisibility(View.GONE);
                this.linear_nowifi.setVisibility(View.VISIBLE);
            }
        }else{
            this.btn_back_down.setVisibility(View.VISIBLE);
            if (isVisibilityBar) {
                this.linear_dialog_bar.setVisibility(View.VISIBLE);
                this.progressBar.setVisibility(View.VISIBLE);
                this.linear_nowifi.setVisibility(View.GONE);
            } else {
                this.linear_dialog_bar.setVisibility(View.GONE);
                this.progressBar.setVisibility(View.GONE);
                this.linear_nowifi.setVisibility(View.VISIBLE);
            }
        }

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

    public void setCancelable(boolean flag) {
        dialog.setCancelable(flag);
    }

    public void show() {
        dialog.show();
        initUpdateProgressReceiver();
    }

    public void dismiss() {
        dialog.cancel();
        //解除广播接收器注册
        try {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(progressReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // 初始化版本更新广播
    private void initUpdateProgressReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsUtil.ACTION_UPDATE_PROGRESS);
        filter.addAction(ConstantsUtil.ACTION_UPDATE_DOWNLOAD_FAILED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(progressReceiver, filter);
    }

    /**
     * 下载完成后，接收到广播
     */
    BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConstantsUtil.ACTION_UPDATE_PROGRESS.equals(intent.getAction())) {
                // 显示进度条
                int progress = intent.getIntExtra(ConstantsUtil.DOWNLOAD_PROGRESS, 0);
                Message message = mHandler.obtainMessage();
                message.what = ConstantsUtil.UPDATE_PROGRESS;
                message.arg1 = progress;
                mHandler.sendMessage(message);
            }else if (ConstantsUtil.ACTION_UPDATE_DOWNLOAD_FAILED.equals(intent.getAction())){
                Message message = mHandler.obtainMessage();
                message.what = ConstantsUtil.UPDATE_FAILED;
                mHandler.sendMessage(message);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtil.UPDATE_PROGRESS:
                    if (msg.arg1 == ConstantsUtil.MAX_PROGRESS) {
                        dismiss();
                        return;
                    }
                    progressBar.setProgress(msg.arg1);
                    break;
                case ConstantsUtil.UPDATE_FAILED:
                    Toast.makeText(mContext,"安装包下载失败，请重试",Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.custom_dialog_btnCancel) {
        } else if (i == R.id.custom_dialog_btnOk) {
            ok_Listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
        } else if (i == R.id.custom_dialog_btnCancel) {
            cancel_Listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
        } else if (i == R.id.btn_back_down) {
            dismiss();
        }
    }
}
