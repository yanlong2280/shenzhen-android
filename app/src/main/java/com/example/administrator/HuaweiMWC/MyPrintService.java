package com.example.administrator.HuaweiMWC;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.print.PrintDocumentInfo;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.support.v4.print.PrintHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import static android.R.attr.port;

/**
 * Created by pc on 2018/1/5.
 */

public class MyPrintService extends PrintService {

    private static final String TAG = "MyPrintService";

    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        Log.d(TAG, "onCreatePrinterDiscoverySession()");
        return new MyPrintDiscoverySession(this);
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {
        Log.d(TAG, "onRequestCancelPrintJob()");
        printJob.cancel();
    }

    @Override
    protected void onPrintJobQueued(final PrintJob printJob) {
        Log.d(TAG, "onPrintJobQueued()");
        final PrintJobInfo printjobinfo = printJob.getInfo();
        PrintDocument printdocument = printJob.getDocument();
//        //打印设置
//        PrintAttributes pb = printjobinfo.getAttributes();
//        PrintDocumentInfo pi = printdocument.getInfo();
//        ParcelFileDescriptor pfd =printdocument.getData();
        //打印内容
//        FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(printdocument.getData());
        if (!printJob.isQueued()) {
            return;
        }
        printJob.start();

//        String filename = "docu.pdf";
//        final File outfile = new File(this.getFilesDir(), filename);
//        outfile.delete();
//        FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(printdocument.getData());
//        //创建一个长度为1024的内存空间
//        byte[] bbuf = new byte[1024];
//        //用于保存实际读取的字节数
//        int hasRead = 0;
//        //使用循环来重复读取数据
//        try {
//            FileOutputStream outStream = new FileOutputStream(outfile);
//            while ((hasRead = file.read(bbuf)) > 0) {
//                //将字节数组转换为字符串输出
//                //System.out.print(new String(bbuf, 0, hasRead));
//                outStream.write(bbuf);
//            }
//            outStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            //关闭文件输出流，放在finally块里更安全
//            try {
//                file.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                OutputStream outputStream = null;
                try {


                    SocketAddress ipe = new InetSocketAddress(printjobinfo.getPrinterId().getLocalId(),9100);
                    socket = new Socket();  //Socket(ipaddress, netport,true);
                    socket.connect(ipe);
                    outputStream = socket.getOutputStream();

                    //这边是输入指令根据自己的需求进行输入
    //            for (int i = 0; i < eCmd.getbList().size(); i++) {
    //                outputStream.write(eCmd.getbList().get(i));
    //            }
                    outputStream.write(0x1B);
                    outputStream.write(0x40);

//                    outfile.getName();

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.huaweiicon);
                    bmp = compressPic(bmp);

                    int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
                    byte[] data = new byte[size];
                    int k = 0;
                    //设置行距为0的指令
                    data[k++] = 0x1B;
                    data[k++] = 0x33;
                    data[k++] = 0x00;
                    // 逐行打印
                    for (int j = 0; j < bmp.getHeight() / 24f; j++) {
                        //打印图片的指令
                        data[k++] = 0x1B;
                        data[k++] = 0x2A;
                        data[k++] = 33;
                        data[k++] = (byte) (bmp.getWidth() % 256); //nL
                        data[k++] = (byte) (bmp.getWidth() / 256); //nH
                        //对于每一行，逐列打印
                        for (int i = 0; i < bmp.getWidth(); i++) {
                            //每一列24个像素点，分为3个字节存储
                            for (int m = 0; m < 3; m++) {
                                //每个字节表示8个像素点，0表示白色，1表示黑色
                                for (int n = 0; n < 8; n++) {
                                    byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                                    data[k] += data[k] + b;
                                }

                                k++;
                            }
                        }
                        data[k++] = 10;//换行
                    }
                    //   long a=System.currentTimeMillis();
                    byte[] data1 = new byte[k];
                    System.arraycopy(data, 0, data1, 0, k);

                    outputStream.write(data1);
                    outputStream.flush();
                    outputStream.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    if (socket != null)
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (socket != null)
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                }
            }
        });
        th.start();
        printJob.complete();
//        try {
//            Thread.sleep(1000 * 10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
    public static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }
    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return gray;
    }
    /**
     * 对图片进行压缩（去除透明度）
     *
     * @param
     */
    private Bitmap compressPic(Bitmap bitmap) {
        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 指定调整后的宽度和高度
        int newWidth = 200;
        int newHeight = 200;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmap, new Rect(0, 0, width, height), new Rect(0, 0, newWidth, newHeight), null);
        return targetBmp;
    }

}
