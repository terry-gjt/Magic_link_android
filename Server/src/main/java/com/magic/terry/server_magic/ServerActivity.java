package com.magic.terry.server_magic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.magic.terry.server_magic.Communication.ActivityShow;
import com.magic.terry.server_magic.Communication.ConnectThreadGroup;
import com.magic.terry.server_magic.Communication.MyMessage;

import java.util.Hashtable;

import static com.magic.terry.server_magic.com.Safe.getclientnum;
import static com.magic.terry.server_magic.com.Safe.getfront;

public class ServerActivity extends Activity implements ActivityShow {
    private TextView serverinfo;
    private MyMessage myMessage;
    private Connectadmin connectadmin;
    private Context mContext;
    Handler allhandler;
    ConnectThreadGroup Connectclientgroup;
    boolean ServerfullActivityRunning=false;
    private ImageView ErWeiMa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        mContext = this;
        ErWeiMa=(ImageView) findViewById(R.id.ErWeiMa);
        serverinfo=(TextView) findViewById(R.id.serverinfo);
        serverinfo.setMovementMethod(ScrollingMovementMethod.getInstance());
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
                .build());
        inithandler();
        connectadmin=new Connectadmin();
        connectadmin.sethandler(allhandler);
        connectadmin.startadmin();
        myMessage=new MyMessage();
        Connectclientgroup=new ConnectThreadGroup(this,allhandler,myMessage);
    }
    @Override
    public void onDestroy(){
        Connectclientgroup.DestroyGroup();
        connectadmin.stopadmin();
        super.onDestroy();
    }
    private void inithandler(){
        allhandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                if(msg.what==4)//4代表客户端
                {
                    String ss=myMessage.getmess();
//                    TextShow(ss);
                    String ff=getfront(ss);
                    int cnum=getclientnum(ss);
                    Log.i("线程h测试client","ss:"+ss+"cnum:"+cnum);
                    switch (ff){
                        case "0001":
                            if(connectadmin.isConnecting())
                                connectadmin.sendstring("0201.,."+cnum);//管理在线则请求
                            else {
                                Connectclientgroup.SendCommand("0002.,."+cnum,cnum);//不在线默认同意
                                Log.i("线程client","不在线默认同意cnum:"+cnum);
                                if (!ServerfullActivityRunning){
                                    Intent intent = new Intent(ServerActivity.this , ServerfullActivity.class);
                                    startActivityForResult(intent, 1001);
                                    ServerfullActivityRunning=true;
                                }
                            }
                            break;
                        case "no":
                        TextShow(ss);
                            break;
                    }
                }
                else if(msg.what==5){//5代表管理hander
                    String ss=connectadmin.getmess();
//                    TextShow(ss);
                    String ff=getfront(ss);
                    int cnum=getclientnum(ss);
                    Log.i("线程h测试admin","ss："+ss+"cnum:"+cnum);
                    switch (ff){
                        case "0202":
                            if (!ServerfullActivityRunning){
                                Intent intent = new Intent(ServerActivity.this , ServerfullActivity.class);
                                startActivityForResult(intent, 1001);
                                ServerfullActivityRunning=true;
                            }
                            Connectclientgroup.SendCommand("0002.,."+cnum,cnum);
                            break;
                        case "0101":

                            break;
                        case "no":
                        TextShow(ss);
                            break;
                    }
                }
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//注意requestCode 和resultCode 接收的值和第一次放入的值必须一样
        if (requestCode == 1001 && resultCode == 1002) {
            Log.i("线程onActivityResult", "requestCode: "+requestCode+"resultCode"+resultCode);
            ServerfullActivityRunning=false;
        }
    }
    @Override
    public void ToastShow(String ss) {
        Toast.makeText(mContext,ss, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void TextShow(String ss) {
        serverinfo.append(ss+"\n");
    }
    public void QRcodeImageShow(String url){
//        int w=ErWeiMa.getWidth();//主线程来不及绘图，返回为0，所以这里定死
//        int h=ErWeiMa.getHeight();
        int w=200,h=200;
        Log.i("线程QRcodeImageShow","kuan"+w+"gao"+h);
        try
        {
            //判断URL合法性
            if (url ==null || "".equals(url) || url.length() < 1)
            {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < h; y++)
            {
                for (int x = 0; x < w; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * w + x] = 0xff000000;
                    }
                    else
                    {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            //显示到我们的ImageView上面
            ErWeiMa.setImageBitmap(bitmap);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
    }
}
