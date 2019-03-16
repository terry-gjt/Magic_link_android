package com.magic.terry.magic_link_all.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Created by terry on 2018-05-19.
 */

public class Connection extends Thread{
    public int mynum=100;
    private Handler shandler;
    public boolean ipsetted=false;
    private boolean isConnecting = false;
    private Socket mSocketClient = null;
    private BufferedReader mBufferedReaderClient = null;
    private PrintWriter mPrintWriterClient = null;
    private String sIP = "192.168.43.1";
    private String []mesage=new String[10];
    private int front=0, rear=0;
    @Override
    public void run() {
        if(sIP.length()<=0)
        {
            Log.i("线程connection","IP不能为空！\n");
            return;
        }
        try
        {
            //连接服务器
            mSocketClient = new Socket(sIP,55555);
            //取得输入、输出流
            mBufferedReaderClient=new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));
            mPrintWriterClient=new PrintWriter(mSocketClient.getOutputStream(),true);
            Log.i("线程connection","已经成功连接！");
        }catch (Exception e) {
            Log.i("线程connection","连接IP异常:"  + e.getMessage());
            return;
        }

        char[] buffer = new char[256];
        int count = 0;
        while(isConnecting)
        {
            try
            {
                if((count = mBufferedReaderClient.read(buffer))>0)
                {
                    String recvMessageClient = "";
                    recvMessageClient=getInfoBuff(buffer,count);
                    Log.i("线程connection","接收到消息："+recvMessageClient);
                    getmynum(recvMessageClient);
                    receivemess(recvMessageClient);
                }
            }catch (Exception e) {
                Log.i("线程connection","接收异常："+e.getMessage()+e.toString());
            }
        }
    }
    private void getmynum(String s){
        int start = s.indexOf("1111.,.");//正常返回0
        if((start!=-1)&&(start+1<s.length())){
            String sPort = s.substring(7);//获取剩余字符串
            int port = Integer.parseInt(sPort);
            Log.i("线程connection","mynum："+port);
            mynum=port;
        }
    }
    public Connection(String ip){
        sIP=ip;
        ipsetted=true;
    }
    public void sethandler(Handler h){
        shandler=h;
    }
    public void stopconnect() {
        if(isConnecting)
        {
            Log.i("线程关闭","关闭线程");
            isConnecting = false;
            try
            {
                if(mSocketClient!=null)
                {
                    mSocketClient.close();
                    mSocketClient = null;

                    mPrintWriterClient.close();
                    mPrintWriterClient = null;
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            this.interrupt();
        }else
        {
            Log.i("线程关闭","线程不在运行");
        }
    }
    public void startconnect(){
        if (!isConnecting){
            isConnecting=true;
            this.start();
        }
        else Log.i("线程启动","线程已经运行了");
    }
    public boolean sendstring(String ss) {
        Log.i("线程connection","准备发送："+ss);
        if(isConnecting&&mSocketClient!=null)
        {
            if(ss.length()<=0)
            {
                Log.i("线程connection","发送内容不能为空！");
            }else
            {
                try
                {
                    mPrintWriterClient.print(ss);
                    Log.i("线程connection","成功发送1："+ss);
                    mPrintWriterClient.flush();
                    Log.i("线程connection","成功发送："+ss);
                    return true;
                }catch (Exception e) {
                    Log.i("线程connection","发送异常："+e.getMessage()+"66\n"+e.toString());
                }
            }
        }else
        {
            Log.i("线程connection","没有连接");
        }
        return false;
    }
    private void receivemess(String ss){//收到消息
        if(shandler!=null){
            mesage[rear]=ss;//放入消息
            if(rear<9)rear++;
            else {
                rear=0;
            }
            Message msg = new Message();
            msg.what = 1;
            shandler.sendMessage(msg);
        }
    }
    public String getmess(){
        String s=mesage[front];
        mesage[front]=null;
        if(front<9)front++;
        else {
            front=0;
        }
        return s;
    }
    private String getInfoBuff(char[] buff,int count) {
        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i]=buff[i];
        }
        return new String(temp);
    }

    public boolean isConnecting(){
        return isConnecting;
    }
    public String getsIP(){
        return sIP;
    }
}
