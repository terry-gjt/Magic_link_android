package com.magic.terry.magic_link_all.shuju;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqlHelper extends SQLiteOpenHelper {
	private static String SQL_NAME = "data.db";
	private static int version = 1;

	// 构造方法
	public MySqlHelper(Context context) {
		super(context, SQL_NAME, null, version);
		// TODO Auto-generated constructor stub
	}

	// 只有创建数据库的时候才会执行，只有执行一次
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table user (id integer PRIMARY KEY,username text,password text)");
		db.execSQL("create table friends (id integer PRIMARY KEY,username text,class text)");
		db.execSQL("insert into friends(username,class) values ('yjy','同学')");
		db.execSQL("insert into friends(username,class) values ('yxq','同学')");
		db.execSQL("insert into friends(username,class) values ('wyz','老师')");
		db.execSQL("insert into user(username,password) values ('123','12')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
