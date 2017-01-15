package com.newland.mobileterminalmonitor.receiver;

import com.newland.mobileterminalmonitor.AppActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

/**
 * 监听usb拔插状态，已插入电脑即打开appactivity
 * 
 * @author zhujy
 * 
 */
public class UsbStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent();
		i.setClass(context, AppActivity.class);
		context.startActivity(i);

//		Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
//		if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
////			Intent i = new Intent();
////			i.setClass(context, AppActivity.class);
////			context.startActivity(i);
//		}
	}

}
