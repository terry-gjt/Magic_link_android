package com.magic.terry.magic_link_all.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.magic.terry.magic_link_all.R;
import com.magic.terry.magic_link_all.common.Connection;
import com.magic.terry.magic_link_all.fragment.AdFragment;
import com.magic.terry.magic_link_all.fragment.AdminFragment;
import com.magic.terry.magic_link_all.fragment.HomeFragment;
import com.magic.terry.magic_link_all.fragment.ScreenFragment;
import com.magic.terry.magic_link_all.ui.SlideMenu;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {

    @Bind(R.id.f1_main)
    FrameLayout f1Main;
    @Bind(R.id.iv_main_home)
    ImageView ivMainHome;
    @Bind(R.id.tv_main_home)
    TextView tvMainHome;
    @Bind(R.id.iv_main_screen)
    ImageView ivMainScreen;
    @Bind(R.id.tv_main_screen)
    TextView tvMainScreen;
    @Bind(R.id.iv_main_user)
    ImageView ivMainUser;
    @Bind(R.id.tv_main_user)
    TextView tvMainUser;
    @Bind(R.id.iv_main_screening)
    ImageView ivMainScreening;
    @Bind(R.id.tv_main_screening)
    TextView tvMainScreening;
    @Bind(R.id.denglu)
    TextView denglu;
    @Bind(R.id.zhuce)
    TextView zhuce;
    @Bind(R.id.tv_menu_username)
    TextView tvMenuUsername;
    private FragmentTransaction transaction;
    private SlideMenu slideMenu;
    ImageView iv_title_back;
    private HomeFragment homeFragment;
    private ScreenFragment screenFragment;
    private AdminFragment AdminFragment;
    private AdFragment AdFragment;
    private boolean flag = true;
    private static final int WHAT_RESET_BACK = 1;
    private static final String username = "myself";
    private Connection connection;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_RESET_BACK:
                    flag = true;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ButterKnife.bind(this);
        //默认显示首页
        setSelect(0);
        //侧滑栏
        iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
        slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
//        connection=new Connection();
//        connection.start();
//        点击返回键打开或关闭Menu
//        iv_title_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                slideMenu.switchMenu();
//            }
//        });
    }

    @OnClick({R.id.lin_main_home, R.id.lin_main_screen, R.id.lin_main_user, R.id.lin_main_screening})
    public void showTab(View view) {
        // Toast.makeText(MainActivity.this, "选择了具体的Tab", Toast.LENGTH_SHORT).show();
        switch (view.getId()) {
            case R.id.lin_main_home:
                setSelect(0);
                break;
            case R.id.lin_main_screen:
                if (connection == null) {
                    toast("请先在首页输入密钥");
                } else {
                    setSelect(1);
                }
                break;
            case R.id.lin_main_user:
                if (connection == null) {
                    toast("请先在首页输入密钥");
                } else {
                    setSelect(2);
                }
                break;
            case R.id.lin_main_screening:
                setSelect(3);
                break;

        }
    }

    //Fragment的显示
    private void setSelect(int i) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        //影藏所有fragment显示
        hideFragments();
        resetTab();
        switch (i) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();//创建是对象后不马上调用生命周期方法，commit后调用
                    transaction.add(R.id.f1_main, homeFragment);
                }
                //显示当前fragment
                transaction.show(homeFragment);
                //改变选中项的图片文本颜色
                ivMainHome.setImageResource(R.drawable.home2);
                tvMainHome.setTextColor(getResources().getColor(R.color.title_all));
                break;
            case 1:
                if (screenFragment == null) {
                    screenFragment = new ScreenFragment();//创建是对象后不马上调用生命周期方法，commit后调用
                    transaction.add(R.id.f1_main, screenFragment);
                }
                //显示当前fragment
                transaction.show(screenFragment);
                //改变选中项的图片文本颜色
                ivMainScreen.setImageResource(R.drawable.screen2);
                tvMainScreen.setTextColor(getResources().getColor(R.color.title_all));
                break;
            case 2:
                if (AdFragment == null) {
                    AdFragment = new AdFragment();//创建是对象后不马上调用生命周期方法，commit后调用
                    transaction.add(R.id.f1_main, AdFragment);
                }
                //显示当前fragment
                transaction.show(AdFragment);
                //改变选中项的图片文本颜色
                ivMainUser.setImageResource(R.drawable.ad2);
                tvMainUser.setTextColor(getResources().getColor(R.color.title_all));
                break;
            case 3:
                if (AdminFragment == null) {
                    AdminFragment = new AdminFragment();//创建是对象后不马上调用生命周期方法，commit后调用
                    transaction.add(R.id.f1_main, AdminFragment);
                }
                //显示当前fragment
                transaction.show(AdminFragment);
                //改变选中项的图片文本颜色
                ivMainScreening.setImageResource(R.drawable.admin2);
                tvMainScreening.setTextColor(getResources().getColor(R.color.title_all));
                break;
        }
        transaction.commit();//提交事务

    }

    private void resetTab() {
        ivMainHome.setImageResource(R.drawable.home);
        ivMainScreen.setImageResource(R.drawable.screen);
        ivMainUser.setImageResource(R.drawable.ad);
        ivMainScreening.setImageResource(R.drawable.admin);
        tvMainHome.setTextColor(getResources().getColor(R.color.home_back_unselected));
        tvMainScreen.setTextColor(getResources().getColor(R.color.home_back_unselected));
        tvMainUser.setTextColor(getResources().getColor(R.color.home_back_unselected));
        tvMainScreening.setTextColor(getResources().getColor(R.color.home_back_unselected));
    }

    private void hideFragments() {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (screenFragment != null) {
            transaction.hide(screenFragment);
        }
        if (AdFragment != null) {
            transaction.hide(AdFragment);
        }
        if (AdminFragment != null) {
            transaction.hide(AdminFragment);
        }
    }

    //重写onkeyup();实现两次点击退出当前应用
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && flag) {
            Toast.makeText(MainActivity.this, "再按一次返回键退出此应用", Toast.LENGTH_SHORT).show();
            flag = false;
            //发送延迟消息
            handler.sendEmptyMessageDelayed(WHAT_RESET_BACK, 1000);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    //避免内存泄露，需在ondestroy()中移除所有未被执行的消息
    @Override
    protected void onDestroy() {
        if (connection != null)
            connection.stopconnect();
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public Connection getconnection() {
        return connection;
    }

    public void newconnection(String ip) {
        connection = null;
        connection = new Connection(ip);
    }

    private void toast(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
    }

    @OnClick({R.id.denglu, R.id.zhuce})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.denglu:
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.zhuce:
                Intent sintent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(sintent);
                break;
        }
    }
}
