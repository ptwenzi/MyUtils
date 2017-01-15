package com.newland.mobileterminalmonitor;

import android.app.Application;

public class AppContext extends Application {

	private static AppContext appContext;

	public static AppContext getAppContext() {
		return appContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = this;
	}
	
}
