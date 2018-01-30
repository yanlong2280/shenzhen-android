package com.example.administrator.HuaweiMWC;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android_serialport_api.SerialPort;

/**
 * Created by pc on 2018/1/26.
 */

public class SerialPortEntity  {
    private SerialPort _serialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
//    private SerialPortReceiveDatas receiveDatas;
    private Thread messageThread;
    private Boolean runing;
    private final int maxReadnum = 20;
    private Integer[] eventVals;
    public interface OnReadyDataListener{
        public   void OnReadData(int msg);
    }
    private  OnReadyDataListener onReadyDataListener;
    public void  setOnReadyDataListener(OnReadyDataListener listener){
        onReadyDataListener = listener;
    }
    public SerialPortEntity(String path,int baudrate, int databits,int stopbits,char parity){
        try {
            _serialPort = new SerialPort(new File(path), baudrate, databits,stopbits,parity);
            mOutputStream = _serialPort.getOutputStream();
            mInputStream = _serialPort.getInputStream();
            //串口监听
//            readSerialPostData();
            start();
        } catch (IOException e) {

        }
    }
//    private void readSerialPostData(){
//        receiveDatas = new SerialPortReceiveDatas(mInputStream);//相关处理函数
//        receiveDatas.setOnReadyDataListener(new SerialPortReceiveDatas.OnThreadDataListener() {
//            @Override
//            public void OnReadData(String msg) {
//                onReadyDataListener.OnReadData(msg);
//            }
//        });
//        receiveDatas.start();
//    }
    public void send() {
        // TODO Auto-generated method stub
        try {
            mOutputStream.write("1".getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }
    public void  stop(){
        if(messageThread != null) {
            runing = false;
            messageThread.interrupt();
            messageThread = null;
        }
    }
    public void start(){
        runing = true;
        messageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = null;// 缓冲数据流
                int bytes = 0;// 返回读取到的数据
                int readnum = 0;
                String ret = "";
                Integer[] vals = new Integer[maxReadnum];
                Integer[] curreVals = null;
                // 监听输入流
                while (runing) {
                    try {
                        if (readnum == maxReadnum) {
                            if (eventVals != null) {
                                curreVals = unique(vals);
                                Arrays.sort(curreVals);
                                onReadyDataListener.OnReadData(compare(curreVals));
                            } else {
                                eventVals = unique(vals);
                                Arrays.sort(eventVals);
                            }
                            readnum = 0;
                            Thread.sleep(100);
                        } else {
                            mOutputStream.write("1".getBytes());
                            Thread.sleep(50);
//                            Thread.sleep(100);
                            //处理数据
                            bytes = mInputStream.available();
                            buffer = new byte[bytes];
                            mInputStream.read(buffer, 0, bytes);
                            ret = new String(buffer, 0, bytes, "utf-8");
                            vals[readnum] = getNumbers(ret.split("=")[1]);
//                            onReadyDataListener.OnReadData( getNumbers(ret.split("=")[1]));
                            readnum++;
                        }
                    } catch (IOException e) {
                    } catch (InterruptedException e) {
                    }
                }
                messageThread.interrupt();
                messageThread = null;
            }
        });
        messageThread.start();
    }
    public int compare(Integer[] arr) {
        boolean isEqual = true;
//        if(arr.length < eventVals.length)
//            arr = supplement(arr);
        if(arr.length == eventVals.length) {
            for (int i = 0; i < eventVals.length; i++) {
                if (!eventVals[i].equals(arr[i])) {
                    isEqual = false;
                    break;
                }
            }
        }else {
            isEqual = false;
        }
        if(!isEqual){
            if (eventVals[0] > arr[0])
                return ControlActivity.CONTROLACTIVITY_SERIALPORTEND;
            else if(eventVals[eventVals.length - 1] < arr[0])
                return ControlActivity.CONTROLACTIVITY_SERIALPORTSRART;
            else if(eventVals[1] < arr[0])
                return ControlActivity.CONTROLACTIVITY_SERIALPORTSRART;
            else
                return 3;
        }
        return ControlActivity.CONTROLACTIVITY_SERIALPORTEND;
    }
    public Integer[] supplement(Integer[] array){
        try {
            int arrayLength =  eventVals.length;
            Integer val = array[0];
            Integer[] newArray = new Integer[arrayLength];
            for (int i=0;i<arrayLength;i++){
                if(i != array.length){
                    newArray[i] = array[i];
                }else {
                    val--;
                    newArray[i] = val;
                }
            }
            Arrays.sort(newArray);
            return  newArray;
        }catch (Exception e){

        }
        return array;
    }
    public Integer[] unique(Integer[] array){
        Map<Integer,Object> map = new HashMap();
        for(int el : array) {
            map.put(el, true);
        }
        Set<Integer> set = map.keySet();
        Integer[] a = new Integer[set.size()];
        return  map.keySet().toArray(a);
    }
    public int getNumbers(String content) {
        try{
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                return Integer.parseInt(matcher.group(0));
            }
        }catch (Exception e){

        }
        return 0;
    }
    public  void close() {
        if(messageThread != null) {
            runing = false;
            messageThread.interrupt();
            messageThread = null;
        }
        if(_serialPort != null) {
            _serialPort.close();
            _serialPort = null;
        }
    }
}
