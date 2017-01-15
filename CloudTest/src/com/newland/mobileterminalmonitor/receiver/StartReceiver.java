package com.newland.mobileterminalmonitor.receiver;


import com.newland.mobileterminalmonitor.AppActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

/**
 * 随机自启动：打开AppActivity.java
 * @author zhujy
 *
 */
public class StartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Intent i = new Intent();
			i.setClass(context, AppActivity.class);
			context.startActivity(i);
		}
	}

}
