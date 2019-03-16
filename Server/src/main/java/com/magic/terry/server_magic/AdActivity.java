package com.magic.terry.server_magic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class AdActivity extends Activity implements OnClickListener {
    //public static List<Socket> clients = new ArrayList<Socket>();// 创建一个集合存放所有的客户端
//  public static List<ChatThread> clientthreads = new ArrayList<ChatThread>();
    adthread clientthread;
    ServerSocket serverSocket;
    public BufferedInputStream photoin;
    private SurfaceView surfaceViewad;
    private SurfaceHolder mSurfaceHolder;
    private Button adbut1,adbut2;
    private boolean serverRunning=false;
    private Context mContext;
    private String recvMessageServer = "";
    Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0)
            {
                adbut2.setText(recvMessageServer);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_ad);
        mContext = this;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  //监控特定线程 磁盘读 磁盘写 网络访问 自定义的运行速度慢的代码分析
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  		//内存泄露的Activity对象  SQLite对象 内存泄露的释放的对象  内存泄漏的BroadcastReceiver or ServiceConnection
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());/////这么一大堆其实就是强制解除严格模式。。。。。

        adbut1=(Button) findViewById(R.id.adbut1);
        adbut1.setOnClickListener(this);

        adbut2=(Button) findViewById(R.id.adbut2);
        adbut2.setOnClickListener(this);

        surfaceViewad=(SurfaceView) findViewById(R.id.surfaceViewad);
        mSurfaceHolder=surfaceViewad.getHolder();
        create();
    }
    @Override
    public void onDestroy() {
        clientthread.interrupt();
        super.onDestroy();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.adbut2:
                create();
                break;
            case R.id.adbut1:
                clientthread.interrupt();
                finish();
                break;
        }
    }
    public void create() {
        if(serverRunning)
        {
            serverRunning=false;
//      for(int    i=0;    i<clientthreads.size();    i++)    {
//        clientthread   =    clientthreads.get(i);
            try
            {
                if(clientthread.mSocketServer!=null)
                {
                    clientthread.mSocketServer.close();
                    clientthread.mSocketServer=null;
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            clientthread.interrupt();
            finish();
//      }
            try{
                if(serverSocket!=null){
                    serverSocket.close();
                    serverSocket=null;
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            adbut2.setText("创建服务");
//      clientthreads.clear();
        }
        else
        {
            serverRunning=true;
            try {
                serverSocket = new ServerSocket(55544);
                adbut2.setText("55544端口开启");
            } catch (IOException e) {
                Toast.makeText(mContext, "本机55544端口被占用，请退出冲突程序", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            clientthread=new adthread();
            clientthread.start();
//      clientthreads.add(clientthread);
        }
    }
    public  class adthread extends Thread {
        Socket mSocketServer;

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
                //方法用于等待客服连接
                mSocketServer=serverSocket.accept();
                InputStream bis=mSocketServer.getInputStream();
                photoin=new BufferedInputStream(bis,1000000);

            }catch (IOException e) {
                return;
            }
            int count = 0;
            while(serverRunning)
            {
                try
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] datas = baos.toByteArray();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

                    byte[] filelenthbbyte = new byte[8];
                    int len = 0,temp=0;int filelenth=1;
                    len=photoin.read(filelenthbbyte);//读取文件大小
                    Log.i("线程异常","len"+len+"文件大小");
                    String t = new String(filelenthbbyte);
                    String b=t.trim();
                    filelenth = Integer.parseInt(b);//文件大小转成数字
                    Log.i("线程异常","文件大小为"+ filelenth/1024+"kb\n");
                    byte[] buf = new byte[102400];
                    while((len=photoin.read(buf))>=0){
                        if(len<temp) {
                            temp=len;
                            recvMessageServer = "已接收" + len+ "\n";//消息换行
                            Message msgd = new Message();
                            msgd.what = 0;
                            mHandler.sendMessage(msgd);
                        }
                        baos.write(buf, 0, len);
                        filelenth-=len;
                        if(filelenth<=0){
                            baos.close();
                            break;
                        }
                    }
                    if(count!=222222){
                        Log.i("线程异常","跳出循环" + filelenth);
                        count=222222;
                    }
//          Bitmap bitmap  = BitmapFactory.decodeStream(new FileInputStream(path));
//            byte[] bmap = getIntent().getByteArrayExtra("bitmap");
                    byte[] bmap = baos.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bmap, 0, bmap.length);
                    showphoto(bitmap);
                }
                catch (Exception e){
//          recvMessageServer = "接收异常:" + e.getMessage() + "\n";//消息换行
//          Message msg = new Message();
//          msg.what = 0;
//          mHandler.sendMessage(msg);
                    Log.i("线程异常","接收异常:" + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    public Bitmap getReduceBitmap(Bitmap bitmap , int w, int h){
        int     width     =     bitmap.getWidth();
        int     hight     =     bitmap.getHeight();
        Matrix matrix     =     new Matrix();
        float     wScake     =     ((float)w/width);//缩放倍数
        float     hScake     =     ((float)h/hight);
        matrix.postScale(wScake, hScake);//缩放图片
        return Bitmap.createBitmap(bitmap, 0,0,width,hight,matrix,true);
    }
    public void showphoto(Bitmap bitmap){
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if(canvas != null || mSurfaceHolder != null){
            try{
                if(bitmap!=null){
                    int height = surfaceViewad.getHeight();
                    int width  = surfaceViewad.getWidth();
                    bitmap = getReduceBitmap(bitmap,width,height);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setStyle(Paint.Style.FILL);
                    //清屏
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(new Rect(0, 0, width,height), paint);
                    Matrix matrix             =     new Matrix();
                    canvas.drawBitmap(bitmap, matrix, paint);
                }
                //解锁显示
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }catch(Exception ex){
                Log.e("ImageSurfaceView",ex.getMessage());
                return;
            }finally{
                //资源回收
                if(bitmap!=null){
                    bitmap.recycle();
                }
            }
        }
    }
}
