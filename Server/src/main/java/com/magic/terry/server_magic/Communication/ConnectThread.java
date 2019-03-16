package com.magic.terry.server_magic.Communication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by terry on 2019-01-13.
 */

public class ConnectThread extends Thread {
    private int number;
    private Socket mSocket;
    private BufferedReader mBufferedReaderThread;
    private PrintWriter mPrintWriterThread;
    private String MessageServer="";
    private ConnectThread Connectclient;
    private ServerSocket serverSocket;
    private ConnectAdd Connectadd;
    private MyMessage myMessage;
    private Handler handler;
    public ConnectThread(int threadnum, ConnectAdd Connectadd){
        number=threadnum;
        this.Connectadd=Connectadd;
        this.serverSocket=Connectadd.getServerSocket();
        this.myMessage=Connectadd.getMyMessage();
        this.handler=Connectadd.gerhandler();
    }
    @Override
    public void run() {
        try
        {
            SocketAddress address = null;
            if(!serverSocket.isBound())
            {
                serverSocket.bind(address,0);
            }
            mSocket=serverSocket.accept();//等待客户机连接
            mBufferedReaderThread=new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mPrintWriterThread=new PrintWriter(mSocket.getOutputStream(),true);
            threadmess(number+"号机已连接！");
            mPrintWriterThread.print("1111.,."+number);//通知客户机编号
            mPrintWriterThread.flush();
            Log.i("线程运行","发送:1111.,."+number);
            Connectclient =new ConnectThread(number+1,Connectadd);
            Connectclient.start();
            Connectadd.add(Connectclient);
        }catch (IOException e) {
            threadmess("连接异常:" + e.getMessage()/* + e.toString()*/);
            Log.i("线程运行","连接异常:" + e.getMessage()/* + e.toString()*/);
            return;
        }

        char[] buffer = new char[256];
        int count = 0;
        while(Connectadd.serverrunning())
        {
            try
            {
                if((count=mBufferedReaderThread.read(buffer))>0);
                {
                    MessageServer = getInfoBuff(buffer, count);//消息换行
//                        0001.,.0001请求0002.,.0002同意0003.,.0003拒绝
                    threadmess(MessageServer);
                    Log.i("线程运行",MessageServer);
                }
            }
            catch (Exception e){
                if(e.getMessage()=="-1")Log.i("线程异常",number+"号机已断开");
                else Log.i("线程异常","其他异常:" + e.getMessage());
                return;
            }
        }
    }
    private String getInfoBuff(char[] buff, int count)	{
        char[] temp = new char[count];
        for(int i=0; i<count; i++)
        {
            temp[i] = buff[i];
        }
        return new String(temp);
    }
    public void SendString(String ss){
        try{
            if(mSocket!=null) {
                mPrintWriterThread.print(ss);
                mPrintWriterThread.flush();
                Log.i("线程正常sendstring","发送内容："+ss);
            }
        }catch (Exception e) {
            Log.i("线程异常sendstring","发送异常："+e.getMessage());
//                    Toast.makeText(mContext, "发送异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void StopThread(){
        try
        {
            if(mSocket!=null)
            {
                mSocket.close();
                mSocket=null;
            }
        }catch (IOException e) {
            Log.i("线程StopThread", "StopThread: "+e.toString());
            e.printStackTrace();
        }
    }
    private void threadmess(String ss){//放入消息并通过handler通知activity
        if(ss.length()>0){
            myMessage.putmess(ss);
            Message msg = new Message();
            msg.what=4;
            handler.sendMessage(msg);
        }
    }
}