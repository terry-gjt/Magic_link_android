package com.magic.terry.magic_link_all.subacitvity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.magic.terry.magic_link_all.R;
import com.magic.terry.magic_link_all.utils.Shotter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static android.content.ContentValues.TAG;

public class ImageActivity extends Activity {
    String sIP;
    private Socket mSocketClient=null;
    public OutputStream photoout;
//    boolean sendrun=true;
    Button anniu;
    Shotter shotter;
    public static final String IP_INFO = "com.magic.terry.magic_link_all.ImageActivity.ip";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
//        mThreadClient = new Thread(mRunnable);
        anniu=(Button) findViewById(R.id.anniu);
        anniu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopsendrun();
            }
        });

        shotter=new Shotter(ImageActivity.this,getIntent());
        sendthread sendthread=new sendthread(shotter);
        sendthread.start();
    }
    private void stopsendrun(){
        shotter.stopimagereader();
        try {
            photoout.close();
            Log.i("线程运行","photoout.close()");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("线程异常","photoout："+e.toString());
        }
        finish();
    }
    private String getINFO(Intent intent) {
        // 获取传递过来的信息。
        String infoString = intent.getStringExtra(IP_INFO);
        return infoString;
    }
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.i(TAG, "Key_Stuta = " + event.getAction());
        if (keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 右键处理
            moveTaskToBack(true); }
        return true;
    }

    public class sendthread extends Thread{
        boolean run=true;
        Shotter oneshotter;
        public sendthread(Shotter s){
            oneshotter=s;
        }
        @Override
        public void run() {
            sIP=getINFO(getIntent());
        Log.i("线程运行","ip为"+sIP);
        if(sIP.length()<=0)
        {
            Log.i("线程运行","ip为空");
            Toast.makeText(ImageActivity.this, "IP不能为空:" , Toast.LENGTH_SHORT).show();
            return;
        }
        try
        {
            //连接服务器
            Log.i("线程运行","ip为1");
            mSocketClient = new Socket(sIP,55566);            //取得输入、输出流
            photoout=mSocketClient.getOutputStream();
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("线程运行","连接IP异常:"  + e.getMessage());
            return;
        }
//            while (run){
                Log.i("线程运行.发送线程","这里执行");
                oneshotter.startScreenShot(photoout);
        }
    }
}
