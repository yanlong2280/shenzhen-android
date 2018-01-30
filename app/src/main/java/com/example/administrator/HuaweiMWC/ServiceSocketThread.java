package com.example.administrator.HuaweiMWC;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created by pc on 2018/1/29.
 */

public class ServiceSocketThread  extends Thread {
    private BluetoothServerSocket mmServerSocket;
    private final String TAG = "BluetoothLeAppService";
    private BluetoothSocket[] sockets;
    private ReceiveDatas[] connectBluetooths;
    private Handler handler;
    private String socketName;
    private boolean mmServerSocketRuning;
    private BluetoothAdapter adapter;
    public ServiceSocketThread(BluetoothAdapter mBluetoothAdapter,String uuid,Handler handler,ThreadCallback threadCallback) {
        mmServerSocket = null;
        this.sockets = new BluetoothSocket[]{null};
        this.connectBluetooths = new ReceiveDatas[]{null};
        this.handler = handler;
        this.adapter = mBluetoothAdapter;
        try {
            mmServerSocket = mBluetoothAdapter
                    .listenUsingRfcommWithServiceRecord(
                            "lanyaban",
                            UUID.fromString(uuid));
            threadCallback.started();
            this.socketName = threadCallback.socketName;
            mmServerSocketRuning = true;
        } catch (IOException e) {
            threadCallback.fail(e.getMessage());
        }
//        mmServerSocket = tmp;
    }

    public void run() {
        // Keep listening until exception occurs or a socket is returned
        while (mmServerSocketRuning) {
            try {
                Log.i(TAG, "开始等待客户端接入");
                if (this.sockets[0] == null) {
                    BluetoothSocket socket = mmServerSocket.accept();
                    //socket.getRemoteDevice();
                    Message message = Message.obtain();
                    message.what = BluetoothLeAppService.SERVICE_BLUETOOTHLCONNECT;
                    this.handler.sendMessage(message);
                    Log.i(TAG, "客户端一号端接入成功");
                    // If a connection was accepted
                    if (socket != null) {
                        // Do work to manage the connection (in a separate
                        // thread)
                        Log.i(TAG, "接收输入线程开启");
                        ReceiveDatas receiveDatas = manageConnectedSocket(socket,0);//相关处理函数
                        this.sockets[0] = socket;
                        this.connectBluetooths[0] = receiveDatas;
                        // break;
                    }
                }
            } catch (IOException e) {
                Log.i(TAG, "manageConnectedSocketClose");
                break;
            }
        }
    }

    // 写数据
    @SuppressLint("NewApi")
    public void sendMessage(int index,String msg) {
        OutputStream os = null;
        byte[] buffer = new byte[16];
        BluetoothSocket socket = this.sockets[ index];
        try {
            if (socket == null) {
                Log.i(TAG, "输出流为空");
                return;
            }
            os = socket.getOutputStream();
            // 写数据
            buffer = msg.getBytes();
            os.write(buffer);
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                if(socket.isConnected())
                    socket.close();
                socket = null;
            } catch (IOException e1) {
            }

        } finally {
            try {
                if (os != null) {
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //关闭线程
    public void destroy() {
        try {
            mmServerSocketRuning = false;
            Log.i(TAG, "正在断开服务端所有连接");
            for (ReceiveDatas receiveDatas : this.connectBluetooths ){
                if(receiveDatas != null) {
                    receiveDatas.runing = false;
                    receiveDatas.interrupt();
                }
            }
            this.connectBluetooths = null;
            for (BluetoothSocket socket : this.sockets ){
                if(socket != null) {
                    if(socket.isConnected()) {
//                        BluetoothDevice bd = socket.getRemoteDevice();
                        socket.close();
//                        ClsUtils.removeBond(bd.getClass(),bd);
                    }
                }
            }
            this.sockets = null;
            //关闭服务器端连接
            if(mmServerSocket != null){
                mmServerSocket.close();
                mmServerSocket = null;
            }
            Log.i(TAG, "开服务端所有连接断开成功");
        } catch (Exception e) {
        }
    }
    public void destroy(int index){
        try {
            Log.i(TAG, "客户端"+ (index+1) +"号断开开始");
            ReceiveDatas connectBluetooth = this.connectBluetooths[index];
            //关闭接收数据线程
            if(connectBluetooth!=null){
                connectBluetooth.runing = false;
                connectBluetooth.interrupt();
            }
//            this.connectBluetooths.remove(index);
            this.connectBluetooths[index] = null;
            BluetoothSocket socket = this.sockets[index];
            //关闭客户端连接
            String  address = "";
            if(socket != null){
                address = socket.getRemoteDevice().getAddress();
                if(socket.isConnected()) {
                    socket.close();
                }
//                BondedDevicesRemove(address);
                Log.i(TAG, "客户端"+ address +"删除配对");
            }
//            this.sockets.remove(index);
            this.sockets[index] = null;
            Log.i(TAG, "客户端"+ (index+1) +"号断开成功");
        } catch (Exception e) {
        }
    }
//    private void BondedDevicesRemove(String address){
//        try {
//            Set<BluetoothDevice> devices = adapter.getBondedDevices();
//            if(devices.size()>0){
//                for(Iterator iterator = devices.iterator(); iterator.hasNext();){
//                    BluetoothDevice device = (BluetoothDevice)iterator.next();
//                    if(device.getAddress().equals(address))
//                        ClsUtils.removeBond(device.getClass(),device);
//                }
//            }
//        } catch (Exception e) {
//
//        }
//    }
    //接收客户端数据
    private ReceiveDatas manageConnectedSocket(BluetoothSocket socket,int index) {
        if (socket!=null) {
            ReceiveDatas  connectBluetooth = new ReceiveDatas(socket,this.handler,this.socketName + "-" + index);
            connectBluetooth.start();
            return connectBluetooth;
        }
        return null;
    }
}