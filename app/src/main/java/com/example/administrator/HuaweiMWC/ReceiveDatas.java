package com.example.administrator.HuaweiMWC;

/**
 * Created by Administrator on 2016/12/21.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;


public class ReceiveDatas  extends Thread {
    // 变量
    private InputStream mmInStream;
    private Handler handler;
    public boolean runing;
    private String msg;
    private long time;
    private final int minutes = 1000;
    private String tag;
    // 构造方法
    public ReceiveDatas(BluetoothSocket socket,Handler handler,String tag) {
        runing = true;
        msg = "";
        time = 0;
        this.tag = tag;
//        this.mmSocket = socket;
        mmInStream = null;
        this.handler = handler;
        // 获取输入流
        try {
            mmInStream = socket.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
//      Handler handler;
//
//      public void setHandler(Handler handler){
//          this.handler = handler;
//      }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];// 缓冲数据流
        int bytes;// 返回读取到的数据
        // 监听输入流
        while (runing) {
            Log.i("ReceiveDatas", "接收");
            Message message = Message.obtain();
            try {
//            	Log.e("接收命令", "1");
                bytes = mmInStream.read(buffer);
                //处理数据

                String ret =  new String(buffer, 0, bytes, "utf-8");
//                if(!isNumeric(ret)){
//                    ret =  bytesToHexString(buffer);
//                    ret = ret.substring(0,1);
//                }
//                //命令过滤
//                if(ret.equals(msg)){
//                    if(SystemClock.elapsedRealtime() < (time + minutes) ){
////    	                //发送命令
////    	                message.arg1 = Integer.parseInt(ret);
////    	                this.handler.sendMessage(message);
//                        continue;
//                    }
//                }
//                else{
                //发送命令
                message.obj = ret;
                message.what = BluetoothLeAppService.SERVICE_BLUETOOTHLEMESSAGE;
                this.handler.sendMessage(message);
//                }
//                msg = ret;
//                time = SystemClock.elapsedRealtime();
            } catch (IOException e) {
                try {
                    Log.i("ReceiveDatas", this.tag);
                    if (mmInStream != null) {
                        mmInStream.close();
                        mmInStream = null;
                    }
                    if(this.tag.contains("serivce")) {
                        message.arg1 = 102;
                        message.arg2 = Integer.parseInt(this.tag.split("-")[1]);
                        message.what = BluetoothLeAppService.SERVICE_BLUETOOTHLEERROR;
                    }
                    this.handler.sendMessage(message);
                    Log.i("ReceiveDatas", "异常");
                    break;
                } catch (IOException e1) {

                }
            }
            try {
                Thread.sleep(50);// 延迟
            } catch (InterruptedException e) {

            }
        }
    }
    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
//            if (hexString.length() == 1) {
//                hexString = '0' + hexString;
//            }
            result += hexString.toUpperCase();
        }
        return result;
    }
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
