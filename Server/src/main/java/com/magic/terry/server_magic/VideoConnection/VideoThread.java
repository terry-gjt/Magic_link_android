package com.magic.terry.server_magic.VideoConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by terry on 2019-01-15.
 */

public class VideoThread extends Thread {
    Socket mSocketServer;
    ServerSocket serverSocket;
    BufferedInputStream photoin;
    boolean ThreadRunning=true,ThreadAccept=false;
    int threadnum;
    VideoThreadShow videoThreadShow;
    public VideoThread(VideoThreadShow videoThreadShow,int threadnum){
        this.serverSocket=videoThreadShow.getServerSocket();
        this.threadnum=threadnum;
        this.videoThreadShow=videoThreadShow;
    }
    public void StopThread(){
        ThreadRunning=false;
        try{
            if(photoin!=null){
                photoin.close();
                photoin=null;
            }
            if(mSocketServer!=null)
            {
                mSocketServer.close();
                mSocketServer=null;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }
    public  boolean ThreadAccept(){
        return ThreadAccept;
    }
    @Override
    public void run() {
        try
        {
            SocketAddress address = null;
            if(serverSocket==null){

                return;
            }
            if(!serverSocket.isBound())
            {
                serverSocket.bind(address,0);
            }
            mSocketServer=serverSocket.accept();//等待客户连接
            ThreadAccept=true;
            Log.i("线程运行", "accept ");
            videoThreadShow.AddMySurfaceView(threadnum);
            Log.i("线程运行", "AddMySurfaceView "+threadnum);
            videoThreadShow.TryAddVideoThread();
            Log.i("线程运行", "TryAddVideoThread ");
            InputStream bis=mSocketServer.getInputStream();
            Log.i("线程运行", "getInputStream ");
            photoin=new BufferedInputStream(bis,2048000);
            Log.i("线程运行", "photoin ");
        }catch (IOException e) {
            Log.i("线程异常", "异常： "+e.toString());
            return;
        }
        while(ThreadRunning)
        {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] filelenthbbyte = new byte[8];
                int len = 0,temp=0;int filelenth=1;
                len=photoin.read(filelenthbbyte);//读取文件大小
                Log.i("线程运行","len为"+len);
                String t = new String(filelenthbbyte);
                String b=t.trim();
                filelenth = Integer.parseInt(b);//文件大小转成数字
                Log.i("线程运行","文件大小为"+ filelenth/1024+"kb\n");
                byte[] buf = new byte[filelenth];
                while((len=photoin.read(buf))>=0){
                    baos.write(buf, 0, len);
                    filelenth=filelenth-len;//还要读的长度
//                    Log.i("线程读取","读取长度"+len+"待读取"+filelenth);
                    if(filelenth==0){
                        baos.close();
                        Log.i("线程读取","读取一次完成，len为"+len);
                        break;
                    }
                    else buf=new byte[filelenth];//读出数据不够
                }
                if(len==-1){
                    Log.i("线程异常","连接中断len=" + len);
                    ThreadRunning=false;
                }
                //          Bitmap bitmap  = BitmapFactory.decodeStream(new FileInputStream(path));
                //            byte[] bmap = getIntent().getByteArrayExtra("bitmap");
                byte[] bmap = baos.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bmap, 0, bmap.length);
                videoThreadShow.ShowBitmap(bitmap,threadnum);
            }
            catch (Exception e){
                Log.i("线程异常","连接中断:" + e.getMessage());
                e.printStackTrace();
                ThreadRunning=false;
            }
        }
        videoThreadShow.ToastShow("连接中断,线程"+threadnum+"已断开");
        videoThreadShow.DeleMySurfaceView(threadnum);
        videoThreadShow.StopActivity();
//        StopThread();
//        finish();
    }
}
