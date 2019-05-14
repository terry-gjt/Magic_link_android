package com.magic.terry.server_magic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.magic.terry.server_magic.VideoConnection.VideoThread;
import com.magic.terry.server_magic.VideoConnection.VideoThreadShow;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class ServerfullActivity extends Activity implements VideoThreadShow,View.OnClickListener {
    //public static List<Socket> clients = new ArrayList<Socket>();// 创建一个集合存放所有的客户端
//    public static List<ChatThread> videothreads = new ArrayList<ChatThread>();
    VideoThread videothread[]=new VideoThread[3];
    ServerSocket serverSocket;
    boolean IsComputer=false;
    public BufferedInputStream photoin;
    private SurfaceView surfaceView[]=new SurfaceView[3];
    private RelativeLayout RLayout[]=new RelativeLayout[3];
    int videothreadnum=0;//当前线程数量
    int tempthreadnum;//操作的线程号
    private SurfaceHolder mSurfaceHolder[]=new SurfaceHolder[3];
    private boolean serverRunning=false;
    private Context mContext;
    private Handler UIhandler;
    private String tempss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_serverfull);
        mContext = this;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()    //监控特定线程 磁盘读 磁盘写 网络访问 自定义的运行速度慢的代码分析
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()     // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()    		//内存泄露的Activity对象    SQLite对象 内存泄露的释放的对象    内存泄漏的BroadcastReceiver or ServiceConnection
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        surfaceView[0]=(SurfaceView) findViewById(R.id.surfaceView0);
        surfaceView[1]=(SurfaceView) findViewById(R.id.surfaceView1);
        surfaceView[2]=(SurfaceView) findViewById(R.id.surfaceView2);
        surfaceView[0].setOnClickListener(this);
        surfaceView[1].setOnClickListener(this);
        surfaceView[2].setOnClickListener(this);
        RLayout[0]=(RelativeLayout) findViewById(R.id.RLayout0);
        RLayout[1]=(RelativeLayout) findViewById(R.id.RLayout1);
        RLayout[2]=(RelativeLayout) findViewById(R.id.RLayout2);
        mSurfaceHolder[0]=surfaceView[0].getHolder();
        Log.i("线程onCreate","h");
        mSurfaceHolder[1]=surfaceView[1].getHolder();
        Log.i("线程onCreate","h2");
        mSurfaceHolder[2]=surfaceView[2].getHolder();
        Log.i("线程onCreate","h3");
        InitHandler();
        create();
        Log.i("线程onCreate","h4");
//            orientationListener = new MyOrientationListener(this);
//            orientationListener.enable();
//            startOrientationChangeListener();
    }
    @Override
    public void onDestroy() {
        for(int i=0;i<3;i++){
            if (videothread[i]!=null){
                videothread[i].StopThread();
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.i("线程onDestroy","seversocket已经关闭了");
        }
        super.onDestroy();
    }
    public void create() {
        {
            serverRunning=true;
            try {
                serverSocket = new ServerSocket(55566);
            } catch (IOException e) {
                Toast.makeText(mContext, "本机55566端口被占用，请退出冲突程序", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            videothread[0]=new VideoThread(this,0);
            videothreadnum++;
            Log.i("线程create","videothreadnum："+videothreadnum);
            resetSize(IsComputer);
            videothread[0].start();
        }
    }

    private void resetSize(boolean IsComputer) {
        DisplayMetrics dm = getResources().getDisplayMetrics();//获取屏幕大小
        int heigth = dm.heightPixels;
        int width = dm.widthPixels;
        int SourceW,SourceH;
        Log.i("线程resetSize","设备heigth"+heigth+"width"+width);
        if(IsComputer){
            SourceW=16;SourceH=9;
        }else {
            SourceW=9;SourceH=16;
        }
        if(SourceW*videothreadnum*heigth>=SourceH*width){
            // 竖屏16:9，此时屏幕偏高
            width=width/videothreadnum;
            heigth = (int) (width*SourceH/SourceW);
            Log.i("线程resetSize","长heigth"+heigth+"width"+width);
        } else {
            //高不变，因为是横向布局
            width = (int) (heigth*SourceW/SourceH);
            Log.i("线程resetSize","短heigth"+heigth+"width"+width);
        }
        RelativeLayout.LayoutParams layoutParamsSV = new RelativeLayout.LayoutParams(width, heigth);
        layoutParamsSV.addRule(RelativeLayout.CENTER_IN_PARENT);
        // 取控件aaa当前的布局参数
        Log.i("线程resetSize","heigth"+heigth+"width"+width);
        for (int i=0;i<videothreadnum;i++){
            surfaceView[i].setLayoutParams(layoutParamsSV); // 使设置好的布局参数应用到控件
        }
    }
    public Bitmap getReduceBitmap(Bitmap bitmap , int w, int h){
        int width = bitmap.getWidth();
        int hight = bitmap.getHeight();
        Matrix matrix=new Matrix();
        float wScake=((float)w/width);//缩放倍数
        float hScake=((float)h/hight);
        matrix.postScale(wScake, hScake);//缩放图片
        return Bitmap.createBitmap(bitmap, 0,0,width,hight,matrix,true);
    }
    private void InitHandler(){
        UIhandler=new Handler(){
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                if (msg.what == 4)//4代表添加surefaceview
                {
                    RLayout[tempthreadnum].setVisibility(View.VISIBLE);
//                    videothreadnum++;
                    videothreadnum=CountVideoThreadNum();
                    Log.i("线程UIhandler", "videothreadnum:" + videothreadnum + "" );
                    resetSize(IsComputer);
                    Log.i("线程UIhandler", "tempthreadnum:" + tempthreadnum + "显示" );
                }else if (msg.what==5){//5代表减少surefaceview
                    RLayout[tempthreadnum].setVisibility(View.GONE);
//                    videothreadnum--;
                    videothreadnum=CountVideoThreadNum();
                    Log.i("线程UIhandler", "videothreadnum:" + videothreadnum + "" );
                    resetSize(IsComputer);
                    Log.i("线程UIhandler", "tempthreadnum:" + tempthreadnum + "隐藏" );
                }else if (msg.what==1){
                    Toast.makeText(mContext, tempss, Toast.LENGTH_SHORT).show();
                }
                else if (msg.what==2){
                    videothreadnum=CountVideoThreadNum();
                    Log.i("线程UIhandler", "msg.what==2，videothreadnum" + videothreadnum + "stop" );
                    if (videothreadnum==0){
                        ServerfullActivity.this.setResult(1002);
                        finish();
                    }
                }
            }
        };
    }

    @Override
    public void AddMySurfaceView(int threadnum) {
        tempthreadnum=threadnum;
        Log.i("线程AddMySurfaceView", "tempthreadnum:" + tempthreadnum + "videothreadnum" +videothreadnum);
        Message msg = new Message();
        msg.what=4;
        UIhandler.sendMessage(msg);
    }

    @Override
    public void DeleMySurfaceView(int threadnum) {
//        RLayout[threadnum].setVisibility(View.GONE);//子线程不能操作UI
        tempthreadnum=threadnum;
        Log.i("线程DeleMySurfaceView", "tempthreadnum:" + tempthreadnum + "videothreadnum" +videothreadnum);
        Message msg = new Message();
        msg.what=5;
        UIhandler.sendMessage(msg);
    }

    @Override
    public void TryAddVideoThread() {
        if(videothread[0]==null||!videothread[0].isAlive()){
            videothread[0]=new VideoThread(this,0);
            Log.i("线程TryAddVideoThread", "添加线程0:videothreadnum" + videothreadnum );
            videothread[0].start();
        }else if (videothread[1]==null||!videothread[1].isAlive()){
            videothread[1]=new VideoThread(this,1);
            Log.i("线程TryAddVideoThread", "添加线程1:videothreadnum" + videothreadnum );
            videothread[1].start();
        }else if (videothread[2]==null||!videothread[2].isAlive()) {
            videothread[2] = new VideoThread(this,2);
            Log.i("线程TryAddVideoThread", "添加线程2:videothreadnum" + videothreadnum );
            videothread[2].start();
        }
    }
    @Override
    public void ShowBitmap(Bitmap bitmap, int num){

        Canvas canvas = mSurfaceHolder[num].lockCanvas();
        if(canvas != null || mSurfaceHolder != null){
            try{
                if(bitmap!=null){
                    int height = surfaceView[num].getHeight();
                    int width    = surfaceView[num].getWidth();
                    bitmap = getReduceBitmap(bitmap,width,height);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setStyle(Paint.Style.FILL);
                    //清屏
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(new Rect(0, 0, width,height), paint);
                    Matrix matrix                =new Matrix();
                    canvas.drawBitmap(bitmap, matrix, paint);
                }
                //解锁显示
                mSurfaceHolder[num].unlockCanvasAndPost(canvas);
            }catch(Exception ex){
                Log.e("线程ShowBitmap",ex.getMessage());
                return;
            }finally{
                //资源回收
                if(bitmap!=null){
                    bitmap.recycle();
                }
            }
        }
    }

    @Override
    public void ToastShow(String ss) {
        tempss=ss;
        Log.i("线程ToastShow", "显示" + ss );
        Message msg = new Message();
        msg.what=1;
        UIhandler.sendMessage(msg);
//        Toast.makeText(mContext, ss, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public Handler GetUIHandler() {
        return UIhandler;
    }

    @Override
    public void StopActivity() {
        Log.i("线程StopActivity", "videothreadnum:" + videothreadnum + "" );
        Message msg = new Message();
        msg.what=2;
        UIhandler.sendMessage(msg);
    }
    private int CountVideoThreadNum(){
        int temp=0;
        boolean a=videothread[0]!=null&&videothread[0].isAlive()&&videothread[0].ThreadAccept();
        boolean b=videothread[1]!=null&&videothread[1].isAlive()&&videothread[1].ThreadAccept();
        boolean c=videothread[2]!=null&&videothread[2].isAlive()&&videothread[2].ThreadAccept();
        if(a)temp++;if(b)temp++;if(c)temp++;
        Log.i("线程CountVideoThreadNum", "temp:" + temp + "" );
        return temp;
    }
//SurfaceView改变方向的监听器
    @Override
    public void onClick(View v) {
//        ChangeSurfaceViewOrientation((SurfaceView)v);
        IsComputer=!IsComputer;
        resetSize(IsComputer);
    }

//    private class MyOrientationListener extends OrientationEventListener {
//        DisplayMetrics dm = getResources().getDisplayMetrics();//获取屏幕大小
//        int heigth = dm.heightPixels;
//        int width = dm.widthPixels;
//
//        private int mCurrentNormalizedOrientation;
//        private int mRememberedNormalOrientation;
//        public MyOrientationListener(Context context) {
//            super(context, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//        @Override
//        public void onOrientationChanged(final int orientation) {
//            Log.i("屏幕", "当前屏幕手持角度:" + orientation + "°");
//            if (orientation != ORIENTATION_UNKNOWN) {
//                mCurrentNormalizedOrientation = normalize(orientation);
//            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i("屏幕", "当前屏幕手持方向:" + mCurrentNormalizedOrientation+"°");
//                }
//            });
//        }
//        private int normalize(int degrees) {
//            if (degrees > 315 || degrees <= 45) {
//                return 0;
//            }
//            if (degrees > 45 && degrees <= 135) {
//                return 90;
//            }
//            if (degrees > 135 && degrees <= 225) {
//                return 180;
//            }
//            if (degrees > 225 && degrees <= 315) {
//                return 270;
//            }
//            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
//        }
//        public void rememberOrientation() {
//            mRememberedNormalOrientation = mCurrentNormalizedOrientation;
//        }
//        public int getRememberedNormalOrientation() {
//            return mRememberedNormalOrientation;
//        }
//    }
//    private void startOrientationChangeListener() {
//        OrientationEventListener mOrEventListener = new OrientationEventListener(this) {
//            @Override
//            public void onOrientationChanged(int rotation) {
//                Log.i(TAG, "当前屏幕手持角度方法:" + rotation + "°");
//                if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315)) {
//                    rotation = 0;
//                } else if ((rotation > 45) && (rotation <= 135)) {
//                    rotation = 90;
//                } else if ((rotation > 135) && (rotation <= 225)) {
//                    rotation = 180;
//                } else if ((rotation > 225) && (rotation <= 315)) {
//                    rotation = 270;
//                } else {
//                    rotation = 0;
//                }
//                if (rotation == mOrientation)
//                    return;
//                mOrientation = rotation;
//
//            }
//        };
//        mOrEventListener.enable();
//        }
}
