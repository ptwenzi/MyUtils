package com.newland.mobileterminalmonitor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.newland.cloudtest.R;
import com.newland.mobileterminalmonitor.common.CpuInfo;
import com.newland.mobileterminalmonitor.common.MemoryInfo;
import com.newland.mobileterminalmonitor.common.PhoneInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 主界面
 * @author linh
 */
public class AppActivity extends Activity {

	private TelephonyManager tm;
	private MyPhoneStateListener myListener;
	private PhoneInfo phoneInfo;
	private TextView text1, text2, text3, text4, text5;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 禁止横屏切换
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕常亮
		setContentView(R.layout.activity_mobile);
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneInfo = new PhoneInfo(this);
		myListener = new MyPhoneStateListener();
		tm.listen(myListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
				| PhoneStateListener.LISTEN_SERVICE_STATE);
		// 注册网络和电池状态广播
		registerReceiver(networkStateReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
		editor = preferences.edit();
		// 获取手机分辨率
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		String screenResolution = outMetrics.widthPixels+"x"+outMetrics.heightPixels;
		editor.putString("screenResolution", screenResolution);
		editor.commit();
		
		text1 = (TextView) findViewById(R.id.tv_monitor_info1);
		text2 = (TextView) findViewById(R.id.tv_monitor_info2);
		text3 = (TextView) findViewById(R.id.tv_monitor_info3);
		text4 = (TextView) findViewById(R.id.tv_monitor_info4);
		text5 = (TextView) findViewById(R.id.tv_monitor_info5);
		
		text1.setText("手机名称：" + phoneInfo.getPhoneBrand() + " "
				+ phoneInfo.getPhoneName() + "\n" + "手机串号："
				+ phoneInfo.getImeiNumber() + "\n" + "手机卡串号："
				+ phoneInfo.getImsiNumber() + "\n" + "操作系统："
				+ phoneInfo.getOsName() + "\n" + "操作系统版本号："
				+ phoneInfo.getOsVersion() + "\n" +"手机分辨率："
				+ screenResolution + "\n" + "cpu型号："
				+ CpuInfo.getCpuName() + "\n" + "内存大小："
				+ MemoryInfo.getTotalRam(this) + "M" + "\n" + "存储大小："
				+ MemoryInfo.getTotalRom(this) + "M" + "\n" + "cpu使用率："
				+ Math.round(CpuInfo.getUsage()) + "%" + "\n" + "内存使用率："
				+ MemoryInfo.getRamUse(this) + "%" + "\n" + "存储使用率："
				+ MemoryInfo.getRomUse(this) + "%");
		text5.setText("已安装程序：\n" + phoneInfo.getAllApp());
		
	}
	private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String netWorkType = "";
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info == null) {
				netWorkType = "无网络";
				text2.setText("当前网络类型："+netWorkType);
				editor.putString("networktype", netWorkType);
				editor.commit();
				System.out.println("nettype================"+preferences.getString("networktype", ""));
			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				netWorkType = "WIFI";
				text2.setText("当前网络类型："+netWorkType);
				editor.putString("networktype", netWorkType);
				editor.commit();
				System.out.println("nettype================"+preferences.getString("networktype", ""));
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				int subType = info.getSubtype();
				if (subType == TelephonyManager.NETWORK_TYPE_CDMA
						|| subType == TelephonyManager.NETWORK_TYPE_GPRS
						|| subType == TelephonyManager.NETWORK_TYPE_EDGE) {
					netWorkType = "2G";
					text2.setText("当前网络类型："+netWorkType);
					editor.putString("networktype", netWorkType);
					editor.commit();
				} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
						|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
						|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
						|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
						|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
					netWorkType = "3G";
					text2.setText("当前网络类型："+netWorkType);
					editor.putString("networktype", netWorkType);
					editor.commit();
				} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
					netWorkType = "4G";
					text2.setText("当前网络类型："+netWorkType);
					editor.putString("networktype", netWorkType);
					editor.commit();
				} 
			}
		}
	};
	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			String imsi = phoneInfo.getImsiNumber();
			if (null != imsi) {
				if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
					PhoneInfo.simOperatorName = "中国移动";
					int asu = signalStrength.getGsmSignalStrength();
					PhoneInfo.dbm = asu;
					text3.setText("信号强度：" + PhoneInfo.dbm + "asu");
					editor.putInt("dbm", PhoneInfo.dbm);
					editor.commit();
				} else if (imsi.startsWith("46001")) {
					PhoneInfo.simOperatorName = "中国联通";
					PhoneInfo.dbm = signalStrength.getCdmaDbm();
					text3.setText("信号强度：" + PhoneInfo.dbm + "asu");
					editor.putInt("dbm", PhoneInfo.dbm);
					editor.commit();
				} else if (imsi.startsWith("46003")) {
					PhoneInfo.simOperatorName = "中国电信";
					PhoneInfo.dbm = signalStrength.getEvdoDbm();
					text3.setText("信号强度：" + PhoneInfo.dbm + "asu");
					editor.putInt("dbm", PhoneInfo.dbm);
					editor.commit();
				}
			}
		}
	}
	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				int voltage = intent.getIntExtra("voltage", 0);
				int temp = intent.getIntExtra("temperature", 0);

				PhoneInfo.batteryUse = level * 100 / scale;
				PhoneInfo.batteryVoltage = (long) voltage;
				PhoneInfo.batteryTemp = temp;

				text4.setText("剩余电量："+ PhoneInfo.batteryUse + "%" + "\n"
						+ "电池电压："+ PhoneInfo.batteryVoltage + "MV" + "\n"
						+ "电池温度："+ new BigDecimal(PhoneInfo.batteryTemp * 0.1).setScale(0, BigDecimal.ROUND_HALF_UP) + "℃");
				DecimalFormat df = new DecimalFormat("0");
				editor.putInt("batteryUse", PhoneInfo.batteryUse );
				editor.putLong("batteryVoltage", PhoneInfo.batteryVoltage );
				editor.putInt("batteryTemp",  Integer.parseInt(df.format(PhoneInfo.batteryTemp * 0.1)));
				editor.commit();
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(networkStateReceiver);
		unregisterReceiver(batteryInfoReceiver);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
