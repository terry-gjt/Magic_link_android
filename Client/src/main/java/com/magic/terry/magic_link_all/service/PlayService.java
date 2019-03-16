package com.magic.terry.magic_link_all.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 这个类是服务组件的范本，用于参考，这两个方法很重要
 create by terry terry_gjt@qq.com
 */
public class PlayService extends Service {

	public class PlayBinder extends Binder {
		public PlayService getService() {//PlayBinder的对象调用此方法返回本服务的对象，以便使用服务对象的方法
			return PlayService.this;
		}
	}
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return new PlayBinder();//绑定返回内部类PlayBinder的对象
	}
}