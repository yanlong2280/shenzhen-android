package com.example.administrator.HuaweiMWC;

/**
 * Created by Administrator on 2016/12/21.
 */
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

public class ConnectThread  extends Thread {
    //	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mySocket;
    private ReceiveDatas connectBluetooth;
    private OutputStream mmOutStream;
    private Handler handler;
    private String socketName;
    private ThreadCallback threadCallback;
    @SuppressLint("NewApi")
    public ConnectThread(BluetoothDevice device,String uuid,Handler handler,ThreadCallback tc) {
        int sdk = Build.VERSION.SDK_INT;
        UUID MY_UUID = UUID.fromString(uuid);
        this.handler = handler;
        threadCallback = tc;
        Log.i("ConnectThread", threadCallback.socketName);
        if (sdk >= 10) {
            try {
                mySocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//                threadCallback.started(this);
                this.socketName = threadCallback.socketName;
                mySocket.connect();
                threadCallback.started(this);
            } catch (IOException e) {
//                try {
//                    mySocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
//                    mySocket.connect();
//                    threadCallback.started(this);
//                } catch (IllegalAccessException e1) {
//                    threadCallback.fail("连接失败 ");
//                } catch (InvocationTargetException e1) {
//                    threadCallback.fail("连接失败 ");
//                } catch (NoSuchMethodException e1) {
//                    threadCallback.fail("连接失败 ");
//                } catch (IOException e1) {
//                    threadCallback.fail("连接失败 ");
//                }
                threadCallback.fail("连接失败 ");
            }
        } else {
            try {
                mySocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//                threadCallback.started(this);
                this.socketName = threadCallback.socketName;
                mySocket.connect();
                threadCallback.started(this);
            } catch (IOException e) {
                threadCallback.fail("连接失败");
            }
        }

    }

    public void run() {
        //
//	        mAdapter.cancelDiscovery();
        try {

            // 启动接收远程设备发送过来的数据
            connectBluetooth = new ReceiveDatas(mySocket,this.handler,this.socketName);
            connectBluetooth.start();
            //输出流
            mmOutStream = mySocket.getOutputStream();
            //连接成功发送消息
        } catch (IOException e) {
            // TODO Auto-generated catch block
            try {
                mySocket.close();
            } catch (IOException ee) {
                // TODO Auto-generated catch block
                ee.printStackTrace();
            }

        }

    }

    // 写数据
    @SuppressLint("NewApi")
    public void sendMessage(String msg) {
        byte[] buffer = new byte[16];
        try {
            if (mmOutStream == null) {
                Log.i("info", "输出流为空");
                return;
            }
            // 写数据
            buffer = msg.getBytes();
            mmOutStream.write(buffer);
        } catch (IOException e) {
//	            e.printStackTrace();
            try {
                if(mySocket.isConnected())
                    mySocket.close();
                mySocket = null;
            } catch (IOException e1) {
            }
        } finally {
            try {
                if (mmOutStream != null) {
                    mmOutStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    public void destroy(){
        if(connectBluetooth!=null){
            connectBluetooth.runing = false;
            connectBluetooth.interrupt();
            connectBluetooth = null;
        }
        try {
            if(mySocket!=null){
                if(mySocket.isConnected()) {
                    mySocket.close();
                }
                mySocket = null;
            }
        } catch (Exception e) {
        }

    }

}
