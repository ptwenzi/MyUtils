package com.newland.mobileterminalmonitor.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import com.newland.mobileterminalmonitor.common.PhoneInfo;
import com.newland.mobileterminalmonitor.task.PhoneTaskExecute;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 真机非测试服务
 * @author linh
 */
public class AppTaskService extends Service {

	private static String TAG = "AppService";
	private static Boolean isStart = false;
	private ServerSocket serverSocket;
	private Socket socket;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private Timer timer=new Timer();

	@Override
	public void onCreate() {
		super.onCreate();
		if(!isStart){
			Log.d(TAG,"AppTaskService还未初始化，开始初始化。。。。。");
			preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
			editor = preferences.edit();
			registerReceiver(networkStateReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
			Log.d(TAG,"AppTaskService服务已创建");
			new Thread(){
				public void run() {
					doListen();
				};
			}.start();
			Log.d(TAG,"AppTaskService线程已创建");
			isStart = true;
		}else{
			Log.d(TAG,"AppTaskService已初始化，无需再次初始化。。。。。");
		}
		timer.schedule(task, 1000*60*60*24*7, 1000*60*60*24*7);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==1){
				PhoneInfo.delSms();
			}
		};
	};
	
	TimerTask task=new TimerTask(){
		@Override
		public void run() {
			Message msg=new Message();
			msg.what=1;
			handler.sendMessage(msg);
		}
	};
	
	private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String netWorkType = "";
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info == null) {
				netWorkType = "无网络";
				editor.putString("networktype", netWorkType);
				editor.commit();
			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				netWorkType = "WIFI";
				editor.putString("networktype", netWorkType);
				editor.commit();
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				int subType = info.getSubtype();
				if (subType == TelephonyManager.NETWORK_TYPE_CDMA
						|| subType == TelephonyManager.NETWORK_TYPE_GPRS
						|| subType == TelephonyManager.NETWORK_TYPE_EDGE) {
					netWorkType = "2G";
					editor.putString("networktype", netWorkType);
					editor.commit();
				} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
						|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
						|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
						|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
						|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
					netWorkType = "3G";
					editor.putString("networktype", netWorkType);
					editor.commit();
				} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
					netWorkType = "4G";
					editor.putString("networktype", netWorkType);
					editor.commit();
				} 
			}
		}
	};
	
	private void doListen() {
		try {
			while(true){
				if(serverSocket==null){
					serverSocket = new ServerSocket();
					serverSocket.setReuseAddress(true);
					serverSocket.bind(new InetSocketAddress(12582));
				}
				socket = serverSocket.accept();
				PhoneTaskExecute phoneTaskExecute = new PhoneTaskExecute(socket, this);
				phoneTaskExecute.start();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(socket!=null){
				try{
					socket.close();
					socket=null;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(serverSocket!=null){
				try{
					serverSocket.close();
					serverSocket=null;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		startService(new Intent("com.newland.mobileterminalmonitor.service.AppTaskService"));
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
