package com.magic.terry.server_magic;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by terry on 2018-05-20.
 */

public class Connectadmin extends Thread{
    public ServerSocket adminserverSocket;
    public boolean serverRunning=false;
    private Handler shandler;
    private boolean isConnecting = false;
    private Socket mSocketServer = null;
    private PrintWriter mPrintWriterServer = null;
    private String []mesage=new String[10];
    private int front=0, rear=0;
    BufferedReader mBufferedReaderServer;
    String MessageServer="";

    public void sethandler(Handler h){
        shandler=h;
    }
    public void stopadmin() {
        if(serverRunning)
        {
            serverRunning = false;
            try
            {
                if(mSocketServer!=null)
                {
                    mSocketServer.close();
                    mSocketServer = null;
                    mPrintWriterServer.close();
                    mPrintWriterServer = null;

                    Log.i("线程关闭","正常关闭线程");
                }
            }catch (IOException e) {
                Log.i("线程结束异常","出现异常"+e.getMessage());
            }
            try {
                if(adminserverSocket!=null)
                adminserverSocket.close();
            } catch (IOException e) {
                Log.i("线程结束异常","adminserverSocket关闭异常"+e.getMessage());
            }
            this.interrupt();
        }else
        {
            Log.i("线程关闭","线程不在运行");
        }
    }
    public void startadmin(){
        if (!serverRunning){
            serverRunning=true;
            this.start();
        }
        else Log.i("线程启动","线程已经运行了");
    }
    public boolean sendstring(String ss) {
        if(isConnecting)
        {
            if(ss.length()<=0)
            {
                Log.i("线程connecadmin","发送内容不能为空！");
            }else
            {
                try
                {
                    mPrintWriterServer.print(ss);
                    mPrintWriterServer.flush();
                    Log.i("线程connecadmin","成功发送："+ss);
                    return true;
                }catch (Exception e) {
                    Log.i("线程connecadmin","发送异常："+e.getMessage());
                }
            }
        }else
        {
            Log.i("线程connecadmin","没有连接");
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
            msg.what = 5;
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
    public boolean isConnecting(){
        return isConnecting;
    }
    private String getInfoBuff(char[] buff, int count)	{
        char[] temp = new char[count];
        for(int i=0; i<count; i++)
        {
            temp[i] = buff[i];
        }
        return new String(temp);
    }
    @Override
    public void run() {
        try
        {
            try {
                adminserverSocket = new ServerSocket(55544);
                getLocalAddress(adminserverSocket);
            } catch (IOException e) {
                Log.i("线程connecadmin","本机55544端口被占用，请退出冲突程序");
                e.printStackTrace();
            }
            SocketAddress address = null;
            if(!adminserverSocket.isBound())
            {
                adminserverSocket.bind(address,0);
            }
            mSocketServer=adminserverSocket.accept();
            mBufferedReaderServer=new BufferedReader(new InputStreamReader(mSocketServer.getInputStream()));
            mPrintWriterServer=new PrintWriter(mSocketServer.getOutputStream(),true);
            isConnecting=true;
            receivemess("管理员已连接");
            Log.i("线程connecadmin","管理员已连接！");
        }catch (IOException e) {
            Log.i("线程connecadmin","管理员连接异常:" + e.getMessage()/* + e.toString()*/);
            return;
        }
        char[] buffer = new char[256];
        int count = 0;
        while(serverRunning)
        {
            try
            {
                if((count=mBufferedReaderServer.read(buffer))>0);
                {
                    MessageServer = getInfoBuff(buffer, count);//消息换行
                    receivemess(MessageServer);
                }
            }
            catch (Exception e){
                Log.i("线程connecadmin","其他异常:" + e.getMessage());
                isConnecting=false;
                return;
            }
        }
    }
    public String getLocalAddress(ServerSocket adminserverSocket){
        String MessageServer="";
        try
        {

            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for(Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIPAddr.nextElement();
                    String ipstr=inetAddress.getHostAddress();
                    int start = ipstr.indexOf("192.168");
                    if((start!=-1)&&(start+1<ipstr.length())){
                        Log.i("密码测试","密码为"+ipstr);
                        MessageServer="管理员密钥为："+encipher(ipstr);
                        Log.i("密码测试",MessageServer);
                    }
//                    MessageServer += "请连接IP："+inetAddress.getHostAddress()+"\n";
                }
            }
        }catch (SocketException ex) {
            Log.i("线程admin","获取IP地址异常:" + ex.getMessage());
        }
        if("".equals(MessageServer))
            receivemess("获取IP地址异常");
        else
            receivemess(MessageServer);
        return null;
    }
    private String encipher(String s){
        int start = s.indexOf("192.168.");//正常返回0
        if((start!=-1)&&(start+1<s.length())){
            String sPort = s.substring(8);//获取剩余字符串
            start = sPort.indexOf(".");
            if((start!=-1)&&(start+1<sPort.length())) {
                String sIP = sPort.substring(0, start);
                int port1 = Integer.parseInt(sIP);
                port1+=226;
                sIP = sPort.substring(start + 1);
                int port2 = Integer.parseInt(sIP);
                port1+=port2;
                port2+=119;
                s = ""+port1+port2;
            }
        }
        return s;
    }
}