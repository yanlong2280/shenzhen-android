package com.example.administrator.HuaweiMWC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/12/21.
 */

public class Alarmreceiver extends BroadcastReceiver {
    private final String ACTION_DESTROY = "com.example.administrator.HuaweiMWC.alarm.action";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction() == null || intent.getAction().equals(ACTION_DESTROY)) {
            Intent i = new Intent();
            i.setClass(context, BluetoothLeAppService.class);
            // 启动service
            // 多次调用startService并不会启动多个service 而是会多次调用onStart
            context.startService(i);

            Intent mainActivityIntent = new Intent(context, ControlActivity.class);  // 要启动的Activity
//            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(mainActivityIntent);
        }
    }
}