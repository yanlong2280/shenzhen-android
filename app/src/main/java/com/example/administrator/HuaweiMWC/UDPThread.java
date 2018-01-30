package com.example.administrator.HuaweiMWC;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by pc on 2018/1/25.
 */

public class UDPThread {
    public UDPThread(String ip){
        try {
            DatagramPacket dp = new DatagramPacket(new byte[0], 0, 0);
            DatagramSocket socket = new DatagramSocket();
            int position = 2;
            while (position < 255) {
                dp.setAddress(InetAddress.getByName(ip));
                socket.send(dp);
                position++;
                if (position == 125) {//分两段掉包，一次性发的话，达到236左右，会耗时3秒左右再往下发
                    socket.close();
                        socket = new DatagramSocket();
                }
            }
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
