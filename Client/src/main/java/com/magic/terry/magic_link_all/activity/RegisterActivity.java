package com.magic.terry.magic_link_all.activity;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.Toast;

import com.magic.terry.magic_link_all.R;
import com.magic.terry.magic_link_all.shuju.UserDAO;

public class RegisterActivity extends Activity {
	EditText et_username, et_password1, et_password2;
	Button btn;
	UserDAO userDAO;
	ContentValues cv;
	boolean b;
	EditText et;
	ImageView zciv_xianshi1, zciv_buxianshi1,zciv_xianshi2, zciv_buxianshi2,zciv_fanhui;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		setContentView(R.layout.register_layout);
		zciv_fanhui = (ImageView) this.findViewById(R.id.zciv_fanhui);
		zciv_fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();;
			}
		});
		et_username = (EditText) this.findViewById(R.id.et_username);
		et_password1 = (EditText) this.findViewById(R.id.et_password1);
		et_password2 = (EditText) this.findViewById(R.id.et_password2);
		zciv_xianshi1 = (ImageView) this.findViewById(R.id.zciv_xianshi1);
		zciv_buxianshi1 = (ImageView) this.findViewById(R.id.zciv_buxianshi1);
		zciv_xianshi2 = (ImageView) this.findViewById(R.id.zciv_xianshi2);
		zciv_buxianshi2 = (ImageView) this.findViewById(R.id.zciv_buxianshi2);
		btn = (Button) this.findViewById(R.id.btn_register);
		userDAO = new UserDAO(this);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (et_password1.getText().toString().trim()
						.equals(et_password2.getText().toString().trim())) {
					cv = new ContentValues();
					String username = et_username.getText().toString().trim();
					String password1 = et_password1.getText().toString().trim();
					if (TextUtils.isEmpty(password1)
							|| TextUtils.isEmpty(username)) {
						Toast.makeText(RegisterActivity.this, "请输入数据",
								Toast.LENGTH_SHORT).show();
					} else {
						cv.put("username", username);
						cv.put("password", password1);
						b = userDAO.insert(cv);
						if (b) {
							Toast.makeText(RegisterActivity.this, "注册成功",
									Toast.LENGTH_SHORT).show();
							//RegisterActivity.this.sendBroadcast(new Intent(
									//"UPDATE"));
							finish();
						} else {
							Toast.makeText(RegisterActivity.this, b+"",
									Toast.LENGTH_SHORT).show();
						}
					}
				}else{
					Toast.makeText(RegisterActivity.this, "两次密码不一致",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void click(View v) {
		switch (v.getId()) {

			case R.id.zciv_xianshi1:
				et_password1.setTransformationMethod(HideReturnsTransformationMethod
						.getInstance());
				zciv_buxianshi1.setVisibility(View.VISIBLE);
				et_password1.setSelection(et_password1.length());
				break;
			case R.id.zciv_buxianshi1:
				et_password1.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
				zciv_buxianshi1.setVisibility(View.INVISIBLE);
				et_password1.setSelection(et_password1.length());
				break;
			case R.id.zciv_xianshi2:
				et_password2.setTransformationMethod(HideReturnsTransformationMethod
						.getInstance());
				zciv_buxianshi2.setVisibility(View.VISIBLE);
				et_password2.setSelection(et_password2.length());
				break;
			case R.id.zciv_buxianshi2:
				et_password2.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
				zciv_buxianshi2.setVisibility(View.INVISIBLE);
				et_password2.setSelection(et_password2.length());
				break;
			default:
				break;
		}
	}

}
