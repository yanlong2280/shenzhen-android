package com.example.administrator.HuaweiMWC;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends AppCompatActivity {
    private static  final int MY_PERMISSIONS_REQUEST_ALL  = 100;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    List<String> mPermissionList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        if (Build.VERSION.SDK_INT >= 23 ) {
            checkBluetoothPermission();
        }else{
            connectBluetooth();
        }
    }
    /*
    校验权限
    */
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有多权限
            mPermissionList.clear();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(MyActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
                connectBluetooth();
            } else {//请求权限方法
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(MyActivity.this, permissions, MY_PERMISSIONS_REQUEST_ALL);
            }
        } else {
            //系统不高于6.0直接执行
            connectBluetooth();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ALL) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MyActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        // 权限拒绝，提示用户开启权限
                        finish();
                        System.exit(0);
                    }
                }
            }
            connectBluetooth();
        }
    }
    private void connectBluetooth() {
        startService(new Intent(MyActivity.this, BluetoothLeAppService.class));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                MyPrintService ms = new MyPrintService();
////                ms.onCreatePrinterDiscoverySession();
////                startService(new Intent(MyActivity.this, MyPrintService.class));
//                finish();
//            }
//        }, 1000);
    }
}
