package com.magic.terry.magic_link_all.utils;

/**
 * Created by terry on 2018-03-19.
 */
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.nio.ByteBuffer;

import static com.magic.terry.magic_link_all.subacitvity.ImageActivity.IP_INFO;

public class Shotter extends Thread{
    private final SoftReference<Context> mRefContext;
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private  boolean isConnecting=false;
    private Socket mSocketClient=null;
    private Context mContext;
    public boolean sendrun=false,tupianrun=false;
    public OutputStream photoout;
    public String sIP="192.168.43.1";
    private String recvMessageServer = "";

    ImageReader.OnImageAvailableListener ImageReaderListener=new ImageReader.OnImageAvailableListener(){
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.i("线程Listener","ip为1");
            Image image = reader.acquireNextImage();
            Log.i("线程Listener","ip为2");
            Bitmap bitmap=imagetobitmap(image);
            Log.i("线程Listener","ip为3");
            sendbitmap(bitmap);
            Log.i("线程Listener","ip为4");
        }
    };
    Handler ImageReaderhandler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100856:
                    break;
                default:Log.i("线程Handler","ip为1");
            }
        }
    };
    public Shotter(Context context, Intent data ) {
        this.mRefContext = new SoftReference<>(context);
        mContext=context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK,data);
        }
    }
    public void startScreenShot(OutputStream pp) {
        photoout=pp;
        Log.i("线程运行.发送线程","这里还好");
//        mOnShotListener = onShotListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mImageReader = ImageReader.newInstance(getScreenWidth(),getScreenHeight(),PixelFormat.RGBA_8888,//此处必须和下面 buffer处理一致的格式 ，RGB_565在一些机器上出现兼容问题。
                    10);
            Log.i("线程运行","ip为1");
            virtualDisplay();
            Log.i("线程运行","ip为2");
            mImageReader.setOnImageAvailableListener(ImageReaderListener,ImageReaderhandler);
//            SystemClock.sleep(300);
            Log.i("线程运行","ip为3");
//            Image image = mImageReader.acquireNextImage();
//            Bitmap bitmap=imagetobitmap(image);
//            sendbitmap(bitmap);
            Log.i("线程异常.发送线程","结束循环");

        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {
        Log.i("线程运行","virtualDisplay1");
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                getScreenWidth(),
                getScreenHeight(),
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
        Log.i("线程运行","virtualDisplay1");
    }
    class SaveTask extends AsyncTask<Image, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            Image image = params[0];
            Bitmap bitmap=imagetobitmap(image);
            sendbitmap(bitmap);
            if (photoout != null) {
                return bitmap;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }
        }
    }
    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }
    private Context getContext() {
        return mRefContext.get();
    }
    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    private Bitmap imageZoom(Bitmap bitMap) {
        //图片允许最大空间   单位：KB
        int maxSize =200;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        int mid = b.length/1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            int i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitMap = getReduceBitmap(bitMap, (int) Math.floor(bitMap.getWidth() / Math.sqrt(i)),
                    (int) Math.floor(bitMap.getHeight() / Math.sqrt(i)));
        }
        return bitMap;
    }//压缩图片至指定大小，不超过200kb
    public Bitmap getReduceBitmap(Bitmap bitmap , int w, int h){
        int     width     =     bitmap.getWidth();
        int     hight     =     bitmap.getHeight();
        android.graphics.Matrix matrix     =     new android.graphics.Matrix();
        float     wScake     =     ((float)w/width);//缩放倍数
        float     hScake     =     ((float)h/hight);
        matrix.postScale(wScake, hScake);//缩放图片
        return Bitmap.createBitmap(bitmap, 0,0,width,hight,matrix,true);
    }
    public class sendthread extends Thread{
        boolean run;
        @Override
        public void run() {
            while (run){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("线程异常.发送线程",e.getMessage());
                }
                Log.e("线程异常.发送线程","这里执行");
                Image image = mImageReader.acquireNextImage();
                Bitmap bitmap=imagetobitmap(image);
                sendbitmap(bitmap);
                run=sendrun;
            }
        }
    }
    private String getINFO(Intent intent) {
        // 获取传递过来的信息。
        String infoString = intent.getStringExtra(IP_INFO);
        return infoString;
    }
    private void sendphoto(String fileth){
        try
        {
            File fileok=new File(fileth);
            FileInputStream fis=new FileInputStream(fileok);
            BufferedInputStream bis=new BufferedInputStream(fis,1000000);
            String  filelenth = String.valueOf(fileok.length());
            byte[] filelenthb = filelenth.getBytes();
            byte[] filelenthb2 = new byte[8];
            for (int i = 0; i < filelenthb.length; i++)
            {
                filelenthb2[i] = filelenthb[i];
            }
            String t = new String(filelenthb2);
            String b=t.trim();
            long l = Long.parseLong(b);
            photoout.write(filelenthb2, 0, 8);//发送长度
            byte[] buf = new byte[102400];
            int len = 0;
            while((len=bis.read(buf))>=0){
                photoout.write(buf, 0, len);
            }
            bis.close();
            fis.close();
        }
        catch(IOException e) {
            Toast.makeText(mContext, "文件不存在或路径有误!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(mContext, "其他异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private Bitmap imagetobitmap(Image image){
        if(image==null){
            Log.e("线程异常.imagetobitmap","image为空");
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        //每个像素的间距
        int pixelStride = planes[0].getPixelStride();
        //总的间距
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                Bitmap.Config.ARGB_8888);//虽然这个色彩比较费内存但是 兼容性更好
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();
        return bitmap;
    }
    private void sendbitmap(Bitmap bitmap){
        if (bitmap != null) {
            try {
                if (photoout != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] baobyte = baos.toByteArray();
                    int len = baobyte.length;//读取bitmap压缩后大小
                    Log.i("线程运行","len大小为"+len/1024+"kb");
                    String  filelenth = String.valueOf(len);//读取文件长度
                    byte[] filelenthb = filelenth.getBytes();//长度字符串转byte
                    byte[] filelenthb2 = new byte[8];//标准化8位
                    for (int i = 0; i < filelenthb.length; i++)
                    {
                        filelenthb2[i] = filelenthb[i];
                    }
                    String t = new String(filelenthb2);
//                    String b=t.trim();//去空格
//                    int l = Integer.parseInt(b);//这里显示看看
                    photoout.write(filelenthb2, 0, 8);//发送长度
                    photoout.flush();
                    Log.i("线程运行","长度标识已发送");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoout);
                    photoout.flush();
                    Log.i("线程运行","bitmap已发送");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("线程异常.sendbitmap",""+e.toString());
            }
        }
        else {
            Log.e("线程异常.sendbitmap","bitmap为空");
        }
    }
    public void stopimagereader(){
        mImageReader.close();
    }
}
