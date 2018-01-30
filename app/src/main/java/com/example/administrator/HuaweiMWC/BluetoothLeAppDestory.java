package com.example.administrator.HuaweiMWC;

/**
 * Created by Administrator on 2016/12/21.
 */

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothLeAppDestory extends BroadcastReceiver {
    private final String ACTION_DESTROY = "com.example.administrator.HuaweiMWC.ActivityFinish";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction() == null || intent.getAction().equals(ACTION_DESTROY)) {
            Log.e("BluetoothLeAppService", "Component");
//           	Intent i = new Intent();
//            i.setClass(context, BluetoothLeAppService.class);
            // 启动service
            // 多次调用startService并不会启动多个service 而是会多次调用onStart
//            context.startService(i);
            Intent it = new Intent();
            it.setComponent(new ComponentName("com.example.administrator.HuaweiMWC",
                    "com.example.administrator.HuaweiMWC.BluetoothLeAppService"));
            context.startService(it);
        }
    }
}

