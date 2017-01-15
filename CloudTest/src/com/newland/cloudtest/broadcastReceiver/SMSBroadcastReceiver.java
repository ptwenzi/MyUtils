package com.newland.cloudtest.broadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.newland.cloudtest.service.SmsService;
import com.newland.cloudtest.util.SystemUtils;
import com.orhanobut.logger.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SMSBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if(!SystemUtils.isServiceWork(context, "com.newland.cloudtest.service.SmsService"))
		{
			context.startService(new Intent(context,SmsService.class));
		}
		String mobile = null;
		String time = null ;
		StringBuffer sb = new StringBuffer();
		Object[] pduses = (Object[]) intent.getExtras().get("pdus");
		for (Object pdus : pduses) {
			byte[] pdusmessage = (byte[]) pdus;
			SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
			mobile = sms.getOriginatingAddress();// 发送短信的手机号码
			String content = sms.getMessageBody(); // 短信内容
			Date date = new Date(sms.getTimestampMillis());
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			time = format.format(date); // 得到发送时间
			
			sb.append(content);
		
			

		}
		Logger.v(mobile+ sb.toString()+time);
		 Intent sendIntent = new Intent();  
		 sendIntent.setAction(SmsService.Action);  
		 sendIntent.putExtra("smsBiz", "receiverMsg");
		 sendIntent.putExtra("mobile", mobile);
		 sendIntent.putExtra("content", sb.toString());
		 sendIntent.putExtra("time", time);
		 context.sendBroadcast(sendIntent);  

	}

}
