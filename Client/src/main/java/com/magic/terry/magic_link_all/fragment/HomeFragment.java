package com.magic.terry.magic_link_all.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.magic.terry.magic_link_all.R;
import com.magic.terry.magic_link_all.activity.MainActivity;
import com.magic.terry.magic_link_all.ui.SlideMenu;
import com.magic.terry.magic_link_all.common.Connection;
import com.magic.terry.magic_link_all.ui.CustomImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeFragment extends Fragment {//首页输入密钥

    @Bind(R.id.tv_title)    TextView tvTitle;
    @Bind(R.id.iv_title_back)    CustomImageView ivTitleBack;
    @Bind(R.id.iv_title_select)    ImageView ivTitleSelect;
    SlideMenu slideMenu;
    @Bind(R.id.v_dot0)    View vDot0;
    @Bind(R.id.v_dot1)    View vDot1;
    @Bind(R.id.v_dot2)    View vDot2;
    @Bind(R.id.v_dot3)    View vDot3;
    @Bind(R.id.v_dot4)    View vDot4;
    @Bind(R.id.vp)    ViewPager vp;
    @Bind(R.id.tv_title_vp)    TextView tv_title;
    @Bind(R.id.miyaotext)    EditText miyaotext;
    @Bind(R.id.Connect)    Button Connect;
    @Bind(R.id.ScanButton)    Button ScanButton;
    private List<ImageView> imageViews; // 滑动的图片集合
    private String[] titles; // 图片标题
    private int[] imageResId; // 图片ID
    private List<View> dots; // 图片标题正文的那些点
    private ScheduledExecutorService scheduledExecutorService;
    private Connection connection;
    // 切换当前显示的图片
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            vp.setCurrentItem(currentItem);// 切换当前显示的图片
        }

        ;
    };
    private int currentItem = 0; // 当前图片的索引号

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);
        //初始化title
        ButterKnife.bind(this, view);
        initTitle();
        miyaotext.setText("412288");
//        slideMenu = (SlideMenu)view.findViewById(R.id.slideMenu);
//        //点击返回键打开或关闭Menu
//        ivTitleBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                slideMenu.switchMenu();
//            }
//        });
        imageResId = new int[]{R.mipmap.damen, R.mipmap.x, R.mipmap.y, R.mipmap.j, R.mipmap.l};
        titles = new String[imageResId.length];
        titles[0] = "";
        titles[1] = "";
        titles[2] = "";
        titles[3] = "";
        titles[4] = "";

        imageViews = new ArrayList<ImageView>();

        // 初始化图片资源
        for (int i = 0; i < imageResId.length; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(imageResId[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
        }
        tv_title.setText(titles[0]);

        dots = new ArrayList<View>();
        dots.add(vDot0);
        dots.add(vDot1);
        dots.add(vDot2);
        dots.add(vDot3);
        dots.add(vDot4);


        vp.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        vp.setOnPageChangeListener(new MyPageChangeListener());
        return view;
    }

    private void initTitle() {
        tvTitle.setText("首页");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    @Override
    public void onStart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒钟切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
        super.onStart();
    }
    @Override
    public void onStop() {
        // 当Activity不可见的时候停止切换
        scheduledExecutorService.shutdown();
        super.onStop();
    }
    @OnClick({R.id.vp, R.id.Connect, R.id.ScanButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vp:

                break;
            case R.id.Connect:
                StartConnect();
                break;
            case R.id.ScanButton:
//                new IntentIntegrator((Activity)getActivity()).initiateScan();
                IntentIntegrator.forSupportFragment(this).initiateScan();
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                miyaotext.setText(result.getContents());
                StartConnect();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**     * 换行切换任务     */
    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (vp) {
                System.out.println("currentItem: " + currentItem);
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
            }
        }

    }

    /**     * 当ViewPager中页面的状态发生改变时调用     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        private int oldPosition = 0;
        /**         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        public void onPageSelected(int position) {
            currentItem = position;
            tv_title.setText(titles[position]);
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }
        public void onPageScrollStateChanged(int arg0) {
        }
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }
    /**
     * 填充ViewPager页面的适配器
     */
    private class MyAdapter extends PagerAdapter {
        //PagerAdapter是android.support.v4包中的类，它的子类有FragmentPagerAdapter、 FragmentStatePagerAdapter，这两个adapter都是Fragment的适配器，用于实现Fragment的滑动效果
        @Override
        public int getCount() {
            return imageResId.length;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(imageViews.get(arg1));
            return imageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
    public void StartConnect() {
        if(connection!=null)//连接中
        {
            connection.stopconnect();//关闭连接
            Connect.setText("连接");
            miyaotext.setEnabled(true);
        }else
        {
            String ss=decipher(miyaotext.getText().toString());
            MainActivity activity=(MainActivity)getActivity();
            activity.newconnection(ss);//创建新连接
            connection=activity.getconnection();
            connection.startconnect();//打开连接
            Connect.setText("断开");
            miyaotext.setEnabled(false);
        }
    }
    private String decipher(String s){
        String sIP = s.substring(0, 3);
        int port1 = Integer.parseInt(sIP);
        port1-=213;
        sIP = s.substring(3);
        int port2 = Integer.parseInt(sIP);
        port2-=132;
        port1-=port2;
        s="192.168."+port1+"."+port2;
        return s;
    }
}
