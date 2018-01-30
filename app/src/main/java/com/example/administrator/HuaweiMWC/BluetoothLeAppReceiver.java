package com.example.administrator.HuaweiMWC;

/**
 * Created by Administrator on 2016/12/21.
 */

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class BluetoothLeAppReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT.equals(intent.getAction())){
//            // 启动完成
//            Intent localIntent = new Intent(context, Alarmreceiver.class);
//            localIntent.setAction("com.example.administrator.bluetooth.alarm.action");
//            PendingIntent sender = PendingIntent.getBroadcast(context, 0,
//                    localIntent, 0);
//            long firstime = SystemClock.elapsedRealtime();
//            AlarmManager am = (AlarmManager) context
//                    .getSystemService(Context.ALARM_SERVICE);
//            // 开机发送
//            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime  , sender);
            //开机启动服务
            Intent it = new Intent();
            it.setComponent(new ComponentName("com.example.administrator.HuaweiMWC",
                    "com.example.administrator.HuaweiMWC.BluetoothLeAppService"));
            context.startService(it);
            //开启APP
            Intent mainActivityIntent = new Intent(context, ControlActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);

        }
//        else if(ACTION_DESTROY.equals(intent.getAction())){
//        	Intent service = new Intent(context, BluetoothLeAppService.class);
//            context.startService(service);
//        }
    }
}
