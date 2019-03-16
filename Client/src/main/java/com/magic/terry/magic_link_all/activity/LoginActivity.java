package com.magic.terry.magic_link_all.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.magic.terry.magic_link_all.R;

import com.magic.terry.magic_link_all.shuju.MySqlHelper;
import com.magic.terry.magic_link_all.shuju.UserDAO;

public class LoginActivity extends Activity {
	EditText et_username, et_password;
	Button btn;
	UserDAO userDAO;
	ContentValues cv;
	Boolean b;
	EditText et;
	ImageView image_back;
	ImageView dliv_xianshi, dliv_buxianshi;
	TextView tv_kuaisuzhuce;
	SQLiteDatabase db;
	MySqlHelper helper;
	Cursor cs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		setContentView(R.layout.login_layout);

		helper=new MySqlHelper(this);
		et_username = (EditText) this.findViewById(R.id.et_usernameforlogin);
		et_password = (EditText) this.findViewById(R.id.et_passwordforlogin);
		dliv_xianshi = (ImageView) this.findViewById(R.id.dliv_xianshi);
		dliv_buxianshi = (ImageView) this.findViewById(R.id.dliv_buxianshi);
		tv_kuaisuzhuce = (TextView) this.findViewById(R.id.tv_kuaisuzhuce);
		image_back=(ImageView)this.findViewById(R.id.image_back);
		btn = (Button) this.findViewById(R.id.btn_login);
//		userDAO = new UserDAO(this);

		image_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tv_kuaisuzhuce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 =new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i2);
            }
        });

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cv = new ContentValues();
				final String username = et_username.getText().toString().trim();
				String password = et_password.getText().toString().trim();
				if (TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
					Toast.makeText(LoginActivity.this, "请输入信息",
							Toast.LENGTH_SHORT).show();
				} else {
					//					Map<String, String> map;
					//					map = null;
					//					map = userDAO.selectsingleline(new String[] { "id",
					//							"username", "password" },
					//							"username=? and password=?", new String[] {
					//									username + "", password + "" });
					//					System.out.println("2"+map.toString());
					db = helper.getReadableDatabase();
					cs = db.query("user", new String[]{"id",
									"username", "password"},
							"username=? and password=?", new String[]{
									username, password}, null, null, null);

					if (cs != null) {
						if (cs.moveToNext()) {
							Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
							Intent i=new Intent("GUANGBO2");
							sendBroadcast(i);
							//实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
							SharedPreferences sp =getSharedPreferences("qq", MODE_PRIVATE);

							//实例化SharedPreferences.Editor对象
							SharedPreferences.Editor editor =sp.edit();
							b=true;
							//用putString的方法保存数据
							editor.putString("name", username);
							editor.putBoolean("zhuangtai",b);
							//editor.putBoolean("state",true);
							//提交数据
							editor.commit();


                            finish();
						} else {
							Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});
	}



	public void click(View v) {
		switch (v.getId()) {

			case R.id.dliv_xianshi:
				et_password.setTransformationMethod(HideReturnsTransformationMethod
						.getInstance());
				dliv_buxianshi.setVisibility(View.VISIBLE);
				et_password.setSelection(et_password.length());
				break;
			case R.id.dliv_buxianshi:
				et_password.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
				dliv_buxianshi.setVisibility(View.INVISIBLE);
				et_password.setSelection(et_password.length());
				break;
			default:
				break;
		}
	}

}
