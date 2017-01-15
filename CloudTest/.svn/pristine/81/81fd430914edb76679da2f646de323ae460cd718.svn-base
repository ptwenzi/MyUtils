package com.newland.cloudtest;

import java.util.ArrayList;

import org.xutils.x;

import com.newland.cloudtest.bean.SMSInfo;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * 自定义application
 * 
 * @author TongLee
 *
 */
public class MyApplication extends Application {
	public ArrayList<Activity> activities = new ArrayList<Activity>();
	public SMSInfo smsInfo;

	private static Context context;
	
	public static final boolean ISDEBUG = true;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CrashReport
				.initCrashReport(getApplicationContext(), "900044183", false);
		x.Ext.init(this);
		x.Ext.setDebug(true);
		Logger.init("newland");

		//
		context = getApplicationContext();
	}

	public static Context getContext() {
		return context;
	}

	/**
	 * 清空堆栈
	 */
	public void clearStatck() {
		for (Activity act : activities) {
			act.finish();// 显式结束
		}
	}

	public SMSInfo getSmsInfo() {
		return smsInfo;
	}

	public void setSmsInfo(SMSInfo smsInfo) {
		this.smsInfo = smsInfo;
	}

}
