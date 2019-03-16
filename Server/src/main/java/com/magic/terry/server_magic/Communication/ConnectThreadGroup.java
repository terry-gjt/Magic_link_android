package com.magic.terry.server_magic.Communication;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.magic.terry.server_magic.com.Safe.encipher;

/**
 * Created by terry on 2018-05-26.
 */

public  class ConnectThreadGroup implements ConnectAdd {
    private List<ConnectThread> threadgroup = new ArrayList<ConnectThread>();
    private ConnectThread connectthread;
    private int threadgroupnum=0;//当前连接的数量
    private boolean serverRunning;
    private ServerSocket serverSocket;
    private Handler handler;
    private MyMessage myMessage;
    private ActivityShow ActivityShow;
    public ConnectThreadGroup(ActivityShow ActivityShow, Handler handler,MyMessage myMessage){
        this.ActivityShow=ActivityShow;
        this.handler=handler;
        this.myMessage=myMessage;
        if(!serverRunning){
            serverRunning=true;
            try {
                serverSocket = new ServerSocket(55555);
                getLocalAddress();
            } catch (IOException e) {
                ActivityShow.ToastShow("本机55555端口被占用，请退出冲突程序");
                e.printStackTrace();
            }
            connectthread=new ConnectThread(1,this);//第一个客户机线程
            connectthread.start();
            threadgroup.add(connectthread);
            threadgroupnum=1;
        }
    }
    public void DestroyGroup(){
        for(int    i=0;    i<threadgroup.size();    i++)    {
            connectthread   =    threadgroup.get(i);
            connectthread.StopThread();
            connectthread.interrupt();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.i("线程serveractivity","seversocket已经关闭了");
        }
    }
    public void SendCommand(String ss,int i){
        if(serverRunning)
        {
            if(ss.length()<=0){
                Log.i("线程异常sendstring","发送内容不能为空");
//                Toast.makeText(mContext, "发送内容不能为空！", Toast.LENGTH_SHORT).show();
            }
            else{
                connectthread   =    threadgroup.get(i-1);
                connectthread.SendString(ss);
            }
        }
        else{
            Log.i("线程异常sendstring","没有连接");
            ActivityShow.ToastShow("没有连接");
        }
    }
    public void getLocalAddress(){//将ip计算为密码显示出来
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
                        Log.i("密码测试","ip为"+ipstr);
                        MessageServer=encipher(ipstr);
                        Log.i("密码测试",MessageServer);
                    }else{
                        Log.i("密码测试","ip："+ipstr+"不属于局域网");
                    }
//                    MessageServer += "请连接IP："+inetAddress.getHostAddress()+"\n";
                }
            }
        }catch (SocketException ex) {
            ActivityShow.TextShow("获取IP地址异常:" + ex.getMessage());
        }
        if("".equals(MessageServer))
            ActivityShow.TextShow("IP地址不属于局域网或未连接网络");
        else
            ActivityShow.TextShow("密钥为："+MessageServer);
        try{
            ActivityShow.QRcodeImageShow(MessageServer);
        }catch (Exception e){
            Log.i("线程异常","异常为"+e.toString());
        }

    }
    @Override
    public void add(ConnectThread connectthread) {
        threadgroup.add(connectthread);
        threadgroupnum++;
    }
    @Override
    public boolean serverrunning() {
        return serverRunning;
    }

    @Override
    public Handler gerhandler() {
        return handler;
    }

    @Override
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public MyMessage getMyMessage() {
        return myMessage;
    }
}
