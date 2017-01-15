package com.newland.mobileterminalmonitor.receiver;

import com.newland.mobileterminalmonitor.service.AppTaskService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppTaskReceiver extends BroadcastReceiver {

	private static String TAG = "AppTaskReceiver";
	private static String START_ACTION = "NotifyServiceStart";
	private static String STOP_ACTION = "NotifyServiceStop";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "AppReceiver onReceive");  
        String action = intent.getAction();  
        if (START_ACTION.equalsIgnoreCase(action)) {  
            context.startService(new Intent(context, AppTaskService.class));  
            Log.d(TAG, "AppReceiver onReceive start end");  
        } else if (STOP_ACTION.equalsIgnoreCase(action)) {  
            context.stopService(new Intent(context, AppTaskService.class));  
            Log.d(TAG, "AppReceiver onReceive stop end");  
        }  
	}

}
