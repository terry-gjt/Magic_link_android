package com.magic.terry.magic_link_all.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.magic.terry.magic_link_all.R;
import com.magic.terry.magic_link_all.subacitvity.ImageActivity;
import com.magic.terry.magic_link_all.activity.MainActivity;
import com.magic.terry.magic_link_all.common.Connection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ScreenFragment extends Fragment {

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    @Bind(R.id.lname)
    TextView lname;
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.clientMessageText)
    EditText clientMessageText;
    @Bind(R.id.SendButtonClient)
    Button SendButtonClient;
    @Bind(R.id.recvText)
    TextView recvText;
    @Bind(R.id.startrecord)
    Button startrecord;
    @Bind(R.id.tv_title)
    TextView tvTitle;

    private Context mContext;
    private String myname = "myself";
    private Connection connection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_screen, null);
        ButterKnife.bind(this, view);
        tvTitle.setText("屏幕分享");
        mContext=getActivity();
        MainActivity activity=(MainActivity)getActivity();
        connection=activity.getconnection();
        connection.sethandler(shandler);//跳转此页面前已经判空
        clientMessageText.setText("我是客户机");
        recvText.setMovementMethod(ScrollingMovementMethod.getInstance());
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    @OnClick({R.id.SendButtonClient, R.id.startrecord})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.SendButtonClient:
                if(connection!=null&&connection.isConnecting()){
                    myname = name.getText().toString();
                    String msgText = clientMessageText.getText().toString();
                    sendstring(myname+": "+msgText);
                }
                else toast("还未连接，请在首页输入密钥");
                break;
            case R.id.startrecord:
                startrecord();
                break;
        }
    }
    public void sendstring(String ss) {
        boolean bb=connection.sendstring(ss);
        if (bb){
            showmess(ss);
            Log.i("线程正常ScreenFra","发送成功"+ss);
        }
    }
    public void startrecord(){
        boolean bb=connection.sendstring("0001.,."+connection.mynum);
        if (bb){
            Log.i("线程正常ScreenFra","0001.,."+connection.mynum);
        }
//        sendstring("0001.,."+connection.mynum);
    }
    public void requestScreenShot() {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivityForResult(((MediaProjectionManager) mContext.getSystemService(mContext.MEDIA_PROJECTION_SERVICE))
                    .createScreenCaptureIntent(),REQUEST_MEDIA_PROJECTION);
            Log.i("线程正常ScreenFra","请求投屏");
        }
        else
        {
            Log.i("线程异常ScreenFra","版本过低,无法截屏");
            toast("版本过低,无法截屏");
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == -1 && data != null) {
                    Intent intent = new Intent(mContext, ImageActivity.class);
                    String ss=connection.getsIP();
                    intent.putExtra(ImageActivity.IP_INFO, ss);
                    Log.i("线程运行","加入ip信息:"+ss);
                    intent.putExtras(data);
                    startActivity(intent);
                }
            }
        }
    }
    private void toast(String str) {
        Toast.makeText(mContext,str,Toast.LENGTH_LONG).show();
    }
    Handler shandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what==1)
            {
                String MessageServer=connection.getmess();
                Log.i("线程运行ScreenFra","收到指令"+MessageServer+"66");
//                    showmess(MessageServer);
                String ff=getfront(MessageServer);
                switch (ff){
                    case "0002":
                        SystemClock.sleep(1000);
                        requestScreenShot();
                        break;
                    case "no":
                        showmess(MessageServer);
                        break;
                }
            }
            if(msg.what==10)
            {

            }
        };
    };
    private void showmess(String ss){
        recvText.append(ss+"\n");
    }
    private String getfront(String s){
        String sss="no";
        int start = s.indexOf(".,.");//正常返回4
//        Log.i("线程getfront","start+"+start+"+s.lenth+"+s.length());
        if((start!=-1)&&(start+1<s.length())){
            sss = s.substring(0,start);//获取前面字符串
        }
        return sss;
    }
}
