package com.magic.terry.server_magic.VideoConnection;

import android.graphics.Bitmap;
import android.os.Handler;

import com.magic.terry.server_magic.Communication.MyMessage;

import java.net.ServerSocket;

/**
 * Created by terry on 2019-01-15.
 */

public interface VideoThreadShow {
    void AddMySurfaceView(int threadnum);
    void DeleMySurfaceView(int threadnum);
    void TryAddVideoThread();//开启新的线程
    void ShowBitmap(Bitmap bitmap,int sufacenum);
    void ToastShow(String ss);
    ServerSocket getServerSocket();
    Handler GetUIHandler();
    void StopActivity();
}
