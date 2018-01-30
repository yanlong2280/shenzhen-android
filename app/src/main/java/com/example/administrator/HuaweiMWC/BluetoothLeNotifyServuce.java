package com.example.administrator.HuaweiMWC;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

public class BluetoothLeNotifyServuce extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {
        super.onCreate();
//        Log.i("BluetoothLeNotifyServuce","onCreate");
//      Intent intent = new Intent(BluetoothLeNotifyServuce.this, MyActivity.class);
//      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//      startActivity(intent);
    }

    @Override
    @SuppressWarnings("ResourceType")
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i("BluetoothLeNotifyServuce","onStartCommand");
        flags = START_STICKY;
        Bundle bundle = intent.getExtras();
        String type=bundle.getString("type");
        String title=bundle.getString("title");
        String msg=bundle.getString("msg");
        Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
//                    .setLargeIcon(aBitmap)
                .build();
        if(type.equals("create")){
//    		//通知
//            Notification notification = new Notification(R.mipmap.huaweiicon, title
//                    , System.currentTimeMillis());
//            notification.setLatestEventInfo(this, title,msg, null);
//            //设置通知默认效果
//            notification.flags = Notification.FLAG_SHOW_LIGHTS;
//            startForeground(1, notification);
            startForeground(1, notification);
            //定时任务
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            //此处是设置每隔1分钟启动一次
            //这是1分钟的毫秒数
            int Minutes = 1 * 60 * 1000;
            //SystemClock.elapsedRealtime()表示1970年1月1日0点至今所经历的时间
            long triggerAtTime = SystemClock.elapsedRealtime() + Minutes;
            //此处设置开启AlarmReceiver这个Service
            Intent i = new Intent(BluetoothLeNotifyServuce.this, Alarmreceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(BluetoothLeNotifyServuce.this, 0, i, 0);
            //ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机算起，并且会唤醒CPU。
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

//    		startForeground(-1213, new Notification());
//    		stopSelf();
        }else{
//            NotificationManager nm = (NotificationManager) this
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notification = new Notification(R.mipmap.huaweiicon, title
//                    , System.currentTimeMillis());
//            notification.setLatestEventInfo(this, title,msg, null);
//            //设置通知默认效果
//            notification.flags = Notification.FLAG_SHOW_LIGHTS;
//            nm.notify(1, notification);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        NotificationManager nm = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
        stopForeground(true);
        super.onDestroy();
    }
}
