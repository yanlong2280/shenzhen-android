package com.example.administrator.HuaweiMWC;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by pc on 2018/1/5.
 */
public class MyPrintDiscoverySession extends PrinterDiscoverySession {
    private static final String TAG = "MyPrintDiscoverySession";
    private final MyPrintService myPrintService;

    public MyPrintDiscoverySession(MyPrintService myPrintService) {
        Log.d(TAG, "MyPrintDiscoverySession()");
        this.myPrintService = myPrintService;

        String ip = BluetoothLeAppService.getLocAddress();
        UDPThread ut = new UDPThread(ip);
    }

    @Override
    public void onStartPrinterDiscovery(List<PrinterId> priorityList) {
        Log.d(TAG, "onStartPrinterDiscovery()");
        List<PrinterInfo> printers = this.getPrinters();
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";
            int i = 1;
            while ((line = br.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.length() < 63) continue;
                    if (line.toUpperCase(Locale.US).contains("IP")) continue;
                    ip = line.substring(0, 17).trim();
                    if(!ip.equals("192.168.1.12"))
                        continue;
                    flag = line.substring(29, 32).trim();
                    mac = line.substring(41, 63).trim();
                    if (mac.contains("00:00:00:00:00:00")) continue;
                    String name = "printer"+i;
                    PrinterInfo myprinter = new PrinterInfo
                            .Builder(myPrintService.generatePrinterId(ip), name, PrinterInfo.STATUS_IDLE)
                            .setDescription(ip)
                            .build();
                    Log.e("MyPrintDiscoverySession", "readArp: mac= "+mac+" ; ip= "+ip+" ;flag= "+ myprinter.getId());
                    printers.add(myprinter);
                    i++;
                } catch (Exception e) {
                }
            }
            br.close();

        } catch(Exception e) {
        }
//        sendBroadcast("SetPrinters",printers);
        addPrinters(printers);
    }

    private void sendBroadcast(String action,  List<PrinterInfo> msg) {
        Intent it = new Intent(action);
        HashMap map= new HashMap();
        map.put("printer",msg);
        it.putExtra("msg", map);
        myPrintService.sendBroadcast(it);
    }
    @Override
    public void onStopPrinterDiscovery() {
        Log.d(TAG, "onStopPrinterDiscovery()");
    }

    /**
     * 确定这些打印机存在
     * @param printerIds
     */
    @Override
    public void onValidatePrinters(List<PrinterId> printerIds) {
        Log.d(TAG, "onValidatePrinters()");
    }

    /**
     * 选择打印机时调用该方法更新打印机的状态，能力
     * @param printerId
     */
    @Override
    public void onStartPrinterStateTracking(PrinterId printerId) {
        Log.d(TAG, "onStartPrinterStateTracking()");
        PrinterInfo printer = findPrinterInfo(printerId);
        if (printer != null) {
            PrinterCapabilitiesInfo capabilities =
                    new PrinterCapabilitiesInfo.Builder(printerId)
                            .setMinMargins(new PrintAttributes.Margins(200, 200, 200, 200))
                            .addMediaSize(PrintAttributes.MediaSize.ISO_A4, true)
//                            .addMediaSize(PrintAttributes.MediaSize.ISO_A6, false)
                            .addResolution(new PrintAttributes.Resolution("R1", "200x200", 200, 200), false)
                            .addResolution(new PrintAttributes.Resolution("R2", "300x300", 300, 300), true)
                            .setColorModes(PrintAttributes.COLOR_MODE_COLOR
                                            | PrintAttributes.COLOR_MODE_MONOCHROME,
                                    PrintAttributes.COLOR_MODE_MONOCHROME)
                            .build();

            printer = new PrinterInfo.Builder(printer)
                    .setCapabilities(capabilities)
                    .setStatus(PrinterInfo.STATUS_IDLE)
                    //        .setDescription("fake print 1!")
                    .build();
            List<PrinterInfo> printers = new ArrayList<PrinterInfo>();

            printers.add(printer);
            addPrinters(printers);
        }
    }

    @Override
    public void onStopPrinterStateTracking(PrinterId printerId) {
        Log.d(TAG, "onStopPrinterStateTracking()");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
    }

    private PrinterInfo findPrinterInfo(PrinterId printerId) {
        List<PrinterInfo> printers = getPrinters();
        final int printerCount = getPrinters().size();
        for (int i = 0; i < printerCount; i++) {
            PrinterInfo printer = printers.get(i);
            if (printer.getId().equals(printerId)) {
                return printer;
            }
        }
        return null;
    }



}
