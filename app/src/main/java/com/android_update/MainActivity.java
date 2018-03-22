package com.android_update;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.android_update.Interface.OnUpdateListener;
import com.android_update.error.UpdateError;

public class MainActivity extends Activity {

    private ClinicUpdateManager mClinicUpdateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity_main);
        mClinicUpdateManager = new ClinicUpdateManager(this);
        mClinicUpdateManager.setOnUpdateListener(new OnUpdateListener() {
            @Override
            public void onStart() {
                Toast.makeText(MainActivity.this, "版本检测中", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(UpdateError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(boolean hasNewVersion) {
                if (!hasNewVersion) {
                    Toast.makeText(MainActivity.this,"已是最新版本",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mClinicUpdateManager.checkVersionUpdate(true);
        //版本更新广播
        mClinicUpdateManager.initUpdateReceiver(this);

    }


    @Override
    protected void onDestroy() {
        //解除广播接收器注册
        if (this != null){
            mClinicUpdateManager.unregisterReceiver(this);
        }
        super.onDestroy();
    }
}
