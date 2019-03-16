package com.magic.terry.magic_link_all.shuju;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/*/
 * user表的操作类
 */
public class UserDAO implements UserDaoInterface {
	private MySqlHelper mySqlHelper;
	private String tablename = "user";
	private SQLiteDatabase db;

	public UserDAO(Context context) {
		mySqlHelper = new MySqlHelper(context);
	}

	public boolean insert(ContentValues cv) {
		// TODO Auto-generated method stub
		boolean b = false;
		try {
			db = mySqlHelper.getWritableDatabase();
			long i = db.insert(tablename, null, cv);
			if (i > 0) {
				b = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}

		}

		return b;
	}



	public Map<String, String> selectsingleline(String[] columns,
			String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		try {
			db = mySqlHelper.getReadableDatabase();
			Cursor cs = db.query(tablename, columns, selection, selectionArgs,
					null, null, null);
			// 如何下一个还有值的话
			if(cs.moveToFirst()) {
				// 获取到查询的个数
				int count = cs.getColumnCount();
				for (int i = 0; i < count; i++) {
					String name = cs.getColumnName(i);
					String zhi = cs.getString(i);
					map.put(name, zhi);
				}
			}
			System.out.println("1"+map.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}

		}
		return map;
	}


	@Override
	public Object selectsingleline2(String[] columns, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		User user = null;
		try {
			db = mySqlHelper.getReadableDatabase();
			Cursor cs = db.query(tablename, columns, selection, selectionArgs,
					null, null, null);
			// 如何下一个还有值的话
			if(cs.moveToFirst()) {
				// 获取到查询的个数
				int id = cs.getInt(cs.getColumnIndex("id"));
				String username = cs.getString(cs.getColumnIndex("username"));
				String password = cs.getString(cs.getColumnIndex("password"));
				user = new User(id, username, password);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}

		}
		return user;
	}

	
}
