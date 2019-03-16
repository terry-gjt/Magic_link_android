package com.magic.terry.magic_link_all.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magic.terry.magic_link_all.R;
import com.magic.terry.magic_link_all.common.AdminConnect;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AdminFragment extends Fragment {//管理员Fragment

    @Bind(R.id.iv_title_back)    ImageView ivTitleBack;
    @Bind(R.id.tv_title)    TextView tvTitle;
    @Bind(R.id.iv_title_select)    ImageView ivTitleSelect;
    @Bind(R.id.adminmiyao)    EditText adminmiyao;
    @Bind(R.id.Connectadmin)    Button Connectadmin;
    @Bind(R.id.adminMessage)    EditText adminMessage;
    @Bind(R.id.SendButtonadmin)    Button SendButtonadmin;
    @Bind(R.id.adminrecv)    TextView adminrecv;
    @Bind(R.id.qinqiu)    LinearLayout qinqiu;
    private Context mContext;
    AdminConnect adminConnect;

    Handler shandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String ss = adminConnect.getmess();
                String ff = getfront(ss);
                int cnum = getclientnum(ss);
                switch (ff) {
                    case "0201":
                        Askview a = new Askview(cnum);
                        a.addView2();
                        qinqiu.addView(a.vv);

                        break;
                    case "no":

                        break;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_admin, null);
        ButterKnife.bind(this, view);
        initTitle();
        mContext = getActivity();
        adminmiyao.setText("521371");
        adminMessage.setText("我是管理员");
        return view;
    }

    private void initTitle() {
        tvTitle.setText("投影管理");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(adminConnect!=null)
        adminConnect.stopconnect();
        ButterKnife.unbind(this);
    }

    private String decipher(String s) {
        String sIP = s.substring(0,3);
        int port1 = Integer.parseInt(sIP);
        port1 -= 226;
        sIP = s.substring(3);
        int port2 = Integer.parseInt(sIP);
        port2 -= 119;
        port1 -= port2;
        s = "192.168." + port1 + "." + port2;
        return s;
    }

    @OnClick({R.id.Connectadmin, R.id.SendButtonadmin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Connectadmin:
                adminConnect = new AdminConnect(decipher(adminmiyao.getText().toString()));
                adminConnect.sethandler(shandler);
                adminConnect.startconnect();
                Connectadmin.setText("断开");
                adminmiyao.setEnabled(false);
                break;
            case R.id.SendButtonadmin:
                String msgText = adminMessage.getText().toString();
                sendstring("管理员: "+msgText);
                break;
        }
    }

    public class Askview {
        int askviewnum;
        LinearLayout vv;
        public Askview(int i) {
            askviewnum = i;
        }
        private View addView1() {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LayoutInflater inflater3 = LayoutInflater.from(mContext);
            View view = inflater3.inflate(R.layout.layout_qinqiuitem, null);
            view.setLayoutParams(lp);
            return view;
        }
        public View addView2() {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            vv = new LinearLayout(mContext);
            vv.setLayoutParams(lp);//设置布局参数
            vv.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams vlp1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3.0f);
            LinearLayout.LayoutParams vlp2 = new LinearLayout.LayoutParams(0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38,getResources().getDisplayMetrics()), 1.0f);
            TextView tv1 = new TextView(mContext);
            Button btn1 = new Button(mContext);
            Button btn2 = new Button(mContext);
            tv1.setLayoutParams(vlp1);
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            btn1.setLayoutParams(vlp2);
//            btn1.setBackground();
            btn2.setLayoutParams(vlp2);
            tv1.setText(askviewnum+"号机请求连接");
            btn1.setBackgroundResource(R.drawable.buttonstyle);
            btn1.setTextColor(getResources().getColor(R.color.white));
            btn1.setText("同意");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qinqiu.removeAllViews();
                    adminConnect.sendstring("0202.,."+askviewnum);
                }
            });
            btn2.setBackgroundResource(R.drawable.buttonstyle);
            btn2.setTextColor(getResources().getColor(R.color.white));
            btn2.setText("拒绝");
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qinqiu.removeAllViews();
                    adminConnect.sendstring("0203.,."+askviewnum);
                }
            });
            vv.addView(tv1);
            vv.addView(btn1);
            vv.addView(btn2);
            return vv;
        }
    }

    private String getfront(String s) {
        int start = s.indexOf(".,.");//正常返回4
        if ((start != -1) && (start + 1 < s.length())) {
            s = s.substring(0, start);//获取前面字符串
        }
        return s;
    }
    private int getclientnum(String s) {
        int port = 0;
        int start = s.indexOf(".,.");//正常返回0
        if ((start != -1) && (start + 1 < s.length())) {
            String sPort = s.substring(start + 3);//获取后面字符串
            port = Integer.parseInt(sPort);
        }
        return port;
    }
    private void showmess(String ss){
        adminrecv.append(ss+"\n");
    }
    public void sendstring(String ss) {
        boolean bb=adminConnect.sendstring(ss);
        if (bb){
            showmess(ss);
            Log.i("线程正常ScreenFra","发送成功"+ss);
        }
    }
}
