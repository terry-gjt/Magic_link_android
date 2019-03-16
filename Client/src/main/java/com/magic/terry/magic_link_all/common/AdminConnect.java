package com.magic.terry.magic_link_all.common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by terry on 2018-05-26.
 */

public class AdminConnect extends Thread {
    Handler shandler;
    public boolean ipsetted = false;
    private boolean isConnecting = false;
    private Socket mSocketClient = null;
    BufferedReader mBufferedReaderClient = null;
    PrintWriter mPrintWriterClient = null;
    private String recvMessageClient = "", sIP = "192.168.43.1";
    private String[] mesage = new String[10];
    private int front = 0, rear = 0;

    @Override
    public void run() {
        if (sIP.length() <= 0) {
            Log.i("线程adminconnect", "IP不能为空！\n");
            return;
        }
        try {
            //连接服务器
            mSocketClient = new Socket(sIP, 55544);
            //取得输入、输出流
            mBufferedReaderClient = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));
            mPrintWriterClient = new PrintWriter(mSocketClient.getOutputStream(), true);
            Log.i("线程adminconnect", "已经成功连接！");
        } catch (Exception e) {
            Log.i("线程adminconnect", "连接异常:" + e.getMessage());
            return;
        }

        char[] buffer = new char[256];
        int count = 0;
        while (isConnecting) {
            try {
                if ((count = mBufferedReaderClient.read(buffer)) > 0) {
                    recvMessageClient = getInfoBuff(buffer, count);
                    Log.i("线程adminconnect", "收到信息："+recvMessageClient);
                    receivemess(recvMessageClient);
                }
            } catch (Exception e) {
                Log.i("线程adminconnect", "接收异常：" + e.getMessage());
            }
        }
    }

    public AdminConnect(String ip) {
        sIP = ip;
        ipsetted = true;
    }

    public void startconnect() {
        if (!isConnecting) {
            isConnecting = true;
            this.start();
        } else
            stopconnect();
    }

    public void stopconnect() {
        if (isConnecting) {
            Log.i("线程关闭", "关闭线程");
            isConnecting = false;
            try {
                if (mSocketClient != null) {
                    mSocketClient.close();
                    mSocketClient = null;

                    mPrintWriterClient.close();
                    mPrintWriterClient = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.interrupt();
        } else {
            Log.i("线程关闭", "线程不在运行");
        }
    }

    public boolean sendstring(String ss) {
        if (isConnecting && mSocketClient != null) {
            if (ss.length() <= 0) {
                Log.i("线程adminconnect", "发送内容不能为空！");
            } else {
                try {
                    Log.i("线程adminconnect", "成功发送：" + ss);
                    mPrintWriterClient.print(ss);
                    mPrintWriterClient.flush();
                    return true;
                } catch (Exception e) {
                    Log.i("线程adminconnect", "发送异常：" + e.getMessage());
                }
            }
        } else {
            Log.i("线程adminconnect", "没有连接");
        }
        return false;
    }

    private void receivemess(String ss) {//收到消息后保存信息，并提示上层来取
        if (shandler != null) {
            Message msg = new Message();
            msg.what = 1;
            shandler.sendMessage(msg);
            mesage[rear] = ss;//放入消息
            if (rear < 9) rear++;
            else {
                rear = 0;
            }
        }
    }

    public String getmess() {//上层调用以取走消息
        String s = mesage[front];
        mesage[front] = null;
        if (front < 9) front++;
        else {
            front = 0;
        }
        return s;
    }

    private String getInfoBuff(char[] buff, int count) {
        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i] = buff[i];
        }
        return new String(temp);
    }

    public boolean isConnecting() {
        return isConnecting;
    }

        public String getsIP() {
        return sIP;
    }
    public void  sethandler(Handler h){
        shandler=h;
    }
}