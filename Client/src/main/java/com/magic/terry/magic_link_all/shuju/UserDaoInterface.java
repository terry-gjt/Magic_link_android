package com.magic.terry.magic_link_all.shuju;

import android.content.ContentValues;

import java.util.Map;

public interface UserDaoInterface {
	// 插入
	public boolean insert(ContentValues cv);

	// 查找单行数据返回map
	public Map<String, String> selectsingleline(String[] columns,
                                                String selection, String[] selectionArgs);

	// 查找单行数据返回对象
	public Object selectsingleline2(String[] columns, String selection,
                                    String[] selectionArgs);

}
