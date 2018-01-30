package com.example.administrator.HuaweiMWC;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//api>=23
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanResult;
//
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.util.Log;

public class BluetoothLeAppService extends Service {
    private final static String C_UUID = "00001101-0000-1000-8000-00805F9B34FB";//6501ffff-aaaa-bbbb-cccc-10101F9B34FB
    private final static String MY_UUID = "abcdplmn-abzx-abcv-abbn-abcdefqwerty";
    private final String LOG_TAG = "BluetoothLeAppService";
    private final String SerialPath="/dev/ttyS3";
    public final static int SERVICE_SRART = 1;
//    public final static int SERVICE_SERIALPORTSRART = 2;
//    public final static int SERVICE_SERIALPORTEND = 3;
    public final static int SERVICE_BLUETOOTHLEMESSAGE = 4;
    public final static int SERVICE_BLUETOOTHLEERROR = 5;
    public final static int SERVICE_BLUETOOTHLCONNECT = 6;
    private int curreF;
    private int  sensorStatus;
    public Handler servuceHandler;
    public Messenger activityHandler;
    private SerialPortEntity sp;
    private BluetoothAdapter adapter;
    private boolean isSensor;
    private boolean isChange;
//    private ConnectThread connectThread;
    private ServiceSocketThread serviceSocketThread;
    private Thread th;
    private Boolean thRun;
    private String sendMsg;
//    private  Receiver receiver;
//    public  static List<PrinterInfo> printers;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onBind");
        return null;
//        return new MyBinder();
    }
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG,"onCreate");
        if (Build.VERSION.SDK_INT < 18)
            startForeground(-1213, new Notification());
        //前台信息
        sendMsg = "HaweiMWCB";

        //获取蓝牙
        adapter = BluetoothAdapter.getDefaultAdapter();

        //线程回调事件
        servuceHandler = new Handler() {
            //回调信息
            //1:蓝牙板socket连接成功
            @SuppressLint("NewApi")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.replyTo != null)
                    activityHandler = msg.replyTo;
                switch (msg.what) {
                    case SERVICE_SRART:
                        curreF = ControlActivity.CONTROLACTIVITY_SERIALPORTEND;
                        serialPort();
                        thRun = true;
                        isChange = false;
                        th = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (thRun){
                                    if(isChange) {
                                        if (isSensor) {
                                            if(sensorStatus == curreF)
                                            {
                                                isChange = false;
                                                Message message = Message.obtain();
                                                message.what = sensorStatus;
                                                try {
                                                    activityHandler.send(message);
                                                } catch (RemoteException e) {
                                                }
                                            }
                                        } else {
                                            isChange = false;
                                            Message message = Message.obtain();
                                            message.what = curreF;
                                            try {
                                                activityHandler.send(message);
                                            } catch (RemoteException e) {
                                            }
                                        }
                                    }
                                    try {
                                        Thread.sleep(50);// 延迟
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        th.start();
                        break;
                    case SERVICE_BLUETOOTHLEMESSAGE:
                        int msgs = sensorStatus;
                        try {
                            msgs = Integer.parseInt(msg.obj.toString());
                        } catch (Exception e) {
                        }
                        if (sensorStatus != msgs && msgs != 3) {
                            sensorStatus = msgs;
//                            isChange = true;
                        }
//                        sendHandler(sensorStatus,msgs);
                        break;
                    case SERVICE_BLUETOOTHLCONNECT:
                        isSensor = true;
                        sensorStatus = ControlActivity.CONTROLACTIVITY_SERIALPORTEND;
                        break;
                    case SERVICE_BLUETOOTHLEERROR:
                        isSensor = false;
                        serviceSocketThread.destroy(0);
                        break;

                }
            }
        };

        Intent intent = new Intent(BluetoothLeAppService.this, ControlActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("messenger",new Messenger(servuceHandler));
        startActivity(intent);

        isSensor = false;
        BondedDevicesRemove();
    }
    //串口
    private void serialPort() {
        try {
            if(sp!=null){
                sp.close();
                sp = null;
            }
            sp = new SerialPortEntity(SerialPath, 115200, 8, 1, 'N');
            sp.setOnReadyDataListener(new SerialPortEntity.OnReadyDataListener() {
                @Override
                public void OnReadData(int msg) {
//                    if (curreF != msg && msg != 3) {
//                        curreF = msg;
//                        sensorStatus = msg+"";
//                        Message message = Message.obtain();
//                        message.what = msg;
//                        try {
//                            activityHandler.send(message);
//                        } catch (RemoteException e) {
//                        }
//                    }
//                    //sendHandler(curreF,msg);
                    if (curreF != msg && msg != 3) {
                        curreF = msg;
                        isChange = true;
                    }
                }
            });
        } catch (SecurityException e) {

        }

    }
    private synchronized void sendHandler(int curreStart,int start){
        if (curreStart != start && start != 3) {
            curreF = start;
            sensorStatus = start;
            Message message = Message.obtain();
            message.what = start;
            try {
                activityHandler.send(message);
            } catch (RemoteException e) {
            }
        }
    }
    /**
     * 获取配对设备
     * */
    private void BondedDevicesRemove(){
//        try {
//            Set<BluetoothDevice> devices = adapter.getBondedDevices();
//            if(devices.size()>0){
////                connect(devices.iterator().next());
//                BluetoothDevice device = devices.iterator().next();
//                //经典蓝牙
//                connect(device);
//
//            }
//        } catch (Exception e) {
//
//        }
        serviceConnect();
    }

    @Override
    @SuppressWarnings("ResourceType")
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG,"onStartCommand");
        flags = START_STICKY;
//    	flags = START_REDELIVER_INTENT;
//    	startForeground(-1213, new Notification());
        startServiceExtras("MWC",sendMsg,"create");
        return super.onStartCommand(intent, flags, startId);
//    	return START_STICKY;
    }
    private void startServiceExtras(String title,String msg,String type){
        Intent intent = new Intent(this, BluetoothLeNotifyServuce.class);
        intent.putExtra("title", title);
        intent.putExtra("msg", msg);
        intent.putExtra("type", type);
        startService(intent);
    }
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(LOG_TAG, "onStart");
    }

    /**
     * 解除绑定服务时调用
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    public synchronized void serviceConnect() {
        Log.i(LOG_TAG, "serviceConnect");
        //此设备成为蓝牙服务端
        if (serviceSocketThread == null) {
            serviceSocketThread = new ServiceSocketThread(adapter
                    , C_UUID
                    , servuceHandler
                    , new ThreadCallback("serivce") {
                @Override
                public void started() {
                    Log.i(LOG_TAG, "服务器连接成功");
                }
                @Override
                public void fail(String msg) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BondedDevicesRemove();
                        }
                    },1000 * 10);
                }
            });
            serviceSocketThread.start();
        }
    }

    public static String getLocAddress() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> address = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress()
                            && (ip instanceof Inet4Address)) {
                        ipaddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG,"onDestroy");
//        unregisterReceiver(receiver);
//        CloseThread();
        if(serviceSocketThread != null){
            serviceSocketThread.destroy();
            serviceSocketThread.interrupt();
            serviceSocketThread = null;
        }
        if(sp!=null) {
            sp.close();
            sp = null;
        }
        if(th!=null) {
            thRun = false;
            th.interrupt();
            th = null;
        }
        super.onDestroy();
    }
}

