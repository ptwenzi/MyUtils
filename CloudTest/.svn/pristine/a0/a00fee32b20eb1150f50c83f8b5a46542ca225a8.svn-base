package com.newland.cloudtest.broadcastReceiver;

import com.newland.cloudtest.TabHostMainActivity;
import com.orhanobut.logger.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartingUpReceiver extends BroadcastReceiver {
	 static final String action_boot="android.intent.action.BOOT_COMPLETED"; 
	
	 @Override

	    public void onReceive(Context context, Intent intent) {

	        if (intent.getAction().equals(action_boot)){ 
	        	Logger.v("收到自启命令");
	            Intent ootStartIntent=new Intent(context,TabHostMainActivity.class); 

	            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 

	            context.startActivity(ootStartIntent); 

	        }

	 

	    }
}
