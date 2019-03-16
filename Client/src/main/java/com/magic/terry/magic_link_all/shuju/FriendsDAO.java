package com.magic.terry.magic_link_all.shuju;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * contacts关联数据库的操作类
 */
public class FriendsDAO implements FriendsDaoInterface {
	private MySqlHelper mySqlHelper;
	private String tablename = "friends";
	private SQLiteDatabase db;

	public FriendsDAO(Context context) {
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

	public boolean delete(String whereClause, String[] whereArgs) {
		// TODO Auto-generated method stub
		boolean b = false;
		try {
			db = mySqlHelper.getWritableDatabase();
			long i = db.delete(tablename, whereClause, whereArgs);
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
			while (cs.moveToFirst()) {
				// 获取到查询的个数
				int count = cs.getColumnCount();
				for (int i = 0; i < count; i++) {
					String name = cs.getColumnName(i);
					String zhi = cs.getString(i);
					map.put(name, zhi);
				}
			}
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

	public List<Map<String, String>> selectall(String[] columns,
											   String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		try {
			db = mySqlHelper.getReadableDatabase();
			Cursor cs = db.query(tablename, columns, selection, selectionArgs,
					null, null, null);
			// 如何下一个还有值的话
			while (cs.moveToNext()) {
				// 获取到查询的个数
				map = new HashMap<String, String>();
				int count = cs.getColumnCount();
				for (int i = 0; i < count; i++) {
					String name = cs.getColumnName(i);
					String zhi = cs.getString(i);
					map.put(name, zhi);
				}
				list.add(map);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}

		}
		return list;
	}

	public boolean update(ContentValues values, String whereClause,
						  String[] whereArgs) {
		// TODO Auto-generated method stub
		boolean b = false;
		try {
			db = mySqlHelper.getWritableDatabase();
			long i = db.update(tablename, values, whereClause, whereArgs);
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

	@Override
	public Object selectsingleline2(String[] columns, String selection,
									String[] selectionArgs) {
		// TODO Auto-generated method stub
		Friends friend = null;
		try {
			db = mySqlHelper.getReadableDatabase();
			Cursor cs = db.query(tablename, columns, selection, selectionArgs,
					null, null, null);
			// 如何下一个还有值的话
			while (cs.moveToFirst()) {
				// 获取到查询的个数
				int id = cs.getInt(cs.getColumnIndex("id"));
				String name = cs.getString(cs.getColumnIndex("name"));
				String classify = cs.getString(cs.getColumnIndex("classify"));
				friend = new Friends(id, name,classify);
				System.out.println("1");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}

		}
		return friend;
	}

	@Override
	public List<Friends> selectall2(String[] columns, String selection,
								   String[] selectionArgs) {
		// TODO Auto-generated method stub
		List<Friends> list = new ArrayList<Friends>();

		try {
			db = mySqlHelper.getReadableDatabase();
			Cursor cs = db.query(tablename, columns, selection, selectionArgs,
					null, null, null);
			Friends friend = null;
			// 如何下一个还有值的话
			while (cs.moveToNext()) {
				// 获取到查询的个数
				int id = cs.getInt(cs.getColumnIndex("id"));
				String name = cs.getString(cs.getColumnIndex("name"));
				String classify = cs.getString(cs.getColumnIndex("classify"));
				friend = new Friends(id, name, classify);
				System.out.print(friend.getId()+friend.getUsername()+friend.getClassify());
				list.add(friend);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}

		}
		return list;
	}

}
