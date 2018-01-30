package com.example.administrator.HuaweiMWC;

/**
 * Created by Administrator on 2016/12/23.
 */

public abstract class ThreadCallback {
    public  String socketName;
    public  ThreadCallback(String socketName){
        this.socketName = socketName;
    }

    public ThreadCallback() {

    }
    public  void event(){

    }
    public  void started(ConnectThread connectThread){

    }
    public  void started(){

    }
    public  void fail(String msg){

    }
}
