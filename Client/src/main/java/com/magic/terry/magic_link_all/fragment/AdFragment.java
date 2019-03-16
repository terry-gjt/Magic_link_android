package com.magic.terry.magic_link_all.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.magic.terry.magic_link_all.R;
import com.magic.terry.magic_link_all.activity.MainActivity;
import com.magic.terry.magic_link_all.common.Connection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class AdFragment extends Fragment {
    @Bind(R.id.iv_title_back)      ImageView ivTitleBack;
    @Bind(R.id.tv_title)            TextView tvTitle;
    @Bind(R.id.iv_title_select)    ImageView ivTitleSelect;
    @Bind(R.id.textView4)           TextView textView4;
    @Bind(R.id.newfile)             Button newfile;
    @Bind(R.id.chooseallvideo)    Button chooseallvideo;
    @Bind(R.id.videos)              RelativeLayout videos;
    @Bind(R.id.wenjian0001)         TextView wenjian0001;
    @Bind(R.id.choose0001)          ImageButton choose0001;
    @Bind(R.id.delete)              Button delete;
    @Bind(R.id.wenjian0002)          TextView wenjian0002;
    @Bind(R.id.choose0002)          ImageButton choose0002;
    @Bind(R.id.chooseallpicture)    Button chooseallpicture;
    @Bind(R.id.pictures)             RelativeLayout pictures;
    @Bind(R.id.p1)                  TextView p1;
    @Bind(R.id.choose0101)          ImageButton choose0101;
    @Bind(R.id.p2)                  TextView p2;
    @Bind(R.id.choose0102)          ImageButton choose0102;
    @Bind(R.id.bofang)              Button bofang;
    private Connection connection;
    private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_ad, null);
        ButterKnife.bind(this, view);
        initTitle();
        mContext = getActivity();
        MainActivity activity = (MainActivity) getActivity();
        connection = activity.getconnection();
        connection.sethandler(adhandler);//跳转此页面前已经判空
//        connection.sendstring("0011.,."+connection.mynum);
        return view;
    }
    private void toast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
    }
    private void initTitle() {
        tvTitle.setText("广告投放");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    private void choosefile() {
        Intent intent = new Intent();
        //intent.setType("image/*");
        // intent.setType("audio/*");
//        intent.setType("video/*"); //android支持mp4 3gp
        intent.setType("video/*;image/*");//同时选择视频和图片
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent wrapperIntent = Intent.createChooser(intent, "选择一个要上传的文件");
        startActivityForResult(wrapperIntent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
//                String s=data.getDataString();
                String APath;
//                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                String[] filePathColumn = {MediaStore.Video.Media.DATA};
                Cursor cursor = mContext.getContentResolver().query(uri,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    APath = picturePath;
                } else {
                    APath = uri.getPath();
                }
//                urltext.setText(APath);
//                Toast.makeText(this, APath, Toast.LENGTH_SHORT).show();
                // String imgNo = cursor.getString(0); // 图片编号
//                String vpath = cursor.getString(1); // 图片文件路径
//                String vsize = cursor.getString(2); // 图片大小
//                String vname = cursor.getString(3); // 图片文件名
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private Handler adhandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String MessageServer = connection.getmess();
                Log.i("线程运行AdFra", "收到指令" + MessageServer + "66");
                switch (MessageServer) {
                    case "0012.,.0012":

                        SystemClock.sleep(1000);
//                        requestScreenShot();
                        break;
                }
//                showmess(MessageServer);
            }
        }

        ;
    };

    @OnClick({R.id.newfile, R.id.chooseallvideo, R.id.choose0001, R.id.choose0002, R.id.delete, R.id.chooseallpicture, R.id.choose0101,R.id.choose0102})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newfile:
                choosefile();
                break;
            case R.id.chooseallvideo:
                choose0001.setBackgroundResource(R.mipmap.gqe);
                choose0002.setBackgroundResource(R.mipmap.gqe);
                break;
            case R.id.choose0001:
                choose0001.setBackgroundResource(R.mipmap.gqd);
                break;
            case R.id.choose0002:
                choose0002.setBackgroundResource(R.mipmap.gqd);
                break;
            case R.id.delete:

                break;
            case R.id.chooseallpicture:
                choose0101.setBackgroundResource(R.mipmap.gqe);
                choose0102.setBackgroundResource(R.mipmap.gqe);
                break;
            case R.id.choose0101:
                choose0101.setBackgroundResource(R.mipmap.gqd);
                break;
            case R.id.choose0102:
                choose0102.setBackgroundResource(R.mipmap.gqd);
                break;

        }
    }
    public class Fileview {
        int fileviewnum;
        LinearLayout vv;
        public Fileview(int i) {
            fileviewnum = i;
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
            ImageButton tv1 = new ImageButton(mContext);
            Button btn1 = new Button(mContext);
            Button btn2 = new Button(mContext);
            tv1.setLayoutParams(vlp1);
//            tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            btn1.setLayoutParams(vlp2);
//            btn1.setBackground();
            btn2.setLayoutParams(vlp2);
//            tv1.setText(fileviewnum+"号机请求连接");
            btn1.setBackgroundResource(R.drawable.buttonstyle);
            btn1.setTextColor(getResources().getColor(R.color.white));
            btn1.setText("同意");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    qinqiu.removeAllViews();
//                    adminConnect.sendstring("0202.,."+fileviewnum);
                }
            });
            btn2.setBackgroundResource(R.drawable.buttonstyle);
            btn2.setTextColor(getResources().getColor(R.color.white));
            btn2.setText("拒绝");
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    qinqiu.removeAllViews();
//                    adminConnect.sendstring("0203.,."+fileviewnum);
                }
            });
            vv.addView(tv1);
            vv.addView(btn1);
            vv.addView(btn2);
            return vv;
        }
    }
}
