package com.maple.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by liu on 22/2/17.
 */

public class Mscoket extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_scoket);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mScoket();
            }
        }).start();


    }
    private void mScoket(){

        try {

            System.out.println("准备连接");
            Socket socket = new Socket("192.168.199.161", 3333);
            System.out.println("连接上了");

            InputStream inputStream = socket.getInputStream();
            byte buffer[] = new byte[1024*4];
            int temp = 0;
            String res = null;
            //从inputstream中读取客户端所发送的数据
            System.out.println("接收到服务器的信息是：");

            while ((temp = inputStream.read(buffer)) != -1){
                System.out.println(new String(buffer, 0, temp));
                res += new String(buffer, 0, temp);
            }

            System.out.println("已经结束接收信息……");

            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
