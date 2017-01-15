package com.newland.mobileterminalmonitor.common;

import java.lang.reflect.Method;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 手机相关信息(品牌、型号、操作系统、信号强度、电池信息、已安装程序、CPU和内存信息)
 * @author linh
 */
public class PhoneInfo {

	/*
	 * 手机品牌
	 */
	private String phoneBrand = android.os.Build.BRAND;
	/*
	 * 手机型号
	 */
	private String phoneName = android.os.Build.MODEL;
	/*
	 * 操作系统
	 */
	private String osName = "Android";
	/*
	 * 操作系统版本号
	 */
	private String osVersion = android.os.Build.VERSION.RELEASE;
	/*
	 * cpu型号
	 */
	private String cpu;
	/*
	 * 内存大小
	 */
	private int ram;
	/*
	 * 存储大小
	 */
	private int rom;
	/*
	 * CPU使用率
	 */
	private int cpuUse;
	/*
	 * 内存使用率
	 */
	private int ramUse;
	/*
	 * 存储使用率
	 */
	private int romUse;
	/*
	 * 剩余电量
	 */
	public static int batteryUse;
	/*
	 * 电池电压
	 */
	public static long batteryVoltage;
	/*
	 * 电池温度
	 */
	public static int batteryTemp;
	/*
	 * SIM卡运营商
	 */
	public static String simOperatorName;
	/*
	 * 手机信号
	 */
	public static int dbm;

	private static Context mContext;

	public PhoneInfo(Context mContext) {
		PhoneInfo.mContext = mContext;
	}

	public String getPhoneBrand() {
		return phoneBrand;
	}

	public String getPhoneName() {
		return phoneName;
	}

	public String getOsName() {
		return osName;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public String getCpu() {
		return cpu;
	}

	public int getRam() {
		return ram;
	}

	public int getRom() {
		return rom;
	}

	public static int getBatteryUse() {
		return batteryUse;
	}

	public static long getBatteryVoltage() {
		return batteryVoltage;
	}

	public static int getBatteryTemp() {
		return batteryTemp;
	}

	public static String getSimOperatorName() {
		return simOperatorName;
	}

	public static int getDbm() {
		return dbm;
	}

	public double getCpuUse() {
		return cpuUse;
	}

	public int getRamUse() {
		return ramUse;
	}

	public int getRomUse() {
		return romUse;
	}

	/**
	 * 获取手机串号
	 * @param context
	 * @return
	 */
	public String getImeiNumber() {
		String serial = null;
	    try {
	       Class<?> c = Class.forName("android.os.SystemProperties");
	       Method get = c.getMethod("get", String.class);
	       serial = (String)get.invoke(c, "ro.serialno");
	    } catch (Exception e) {
	       e.printStackTrace();
	    }
	    return serial;
	}
	/**
	 * 获取IMEI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
        
		String imei = telephonyManager.getDeviceId();
		return imei;
	}
	/**
	 * 获取手机卡串号
	 * @param context
	 * @return
	 */
	public String getImsiNumber() {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi;
	}
	/**
	 * 是否有手机卡
	 * @return
	 */
	public int hasSimCard() {
		int status		;
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		if(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT){
			status = 1;
		} else {
			status = 0;
		}
		return status;
	}
	/**
	 * 获取手机已安装的非系统应用程序信息
	 * @param context
	 * @return
	 */
	public String getAllApp() {
		String result = "";
		Log.v("zjy","------->" + PackageManager.GET_UNINSTALLED_PACKAGES);
		List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		Log.v("zjy","包名：------->" );
		for (PackageInfo pi : packages) {
			Log.v("zjy",pi.packageName);
			if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				result += pi.applicationInfo.loadLabel(mContext.getPackageManager()).toString()
						+ " " + pi.versionName + " " + pi.packageName + "\n";
			}
		}
		Log.v("zjy","<---------");
		return result.substring(0, result.length() - 1);
	}
	/**
	 * 获取监控软件版本号
	 * @return
	 */
	public String getTerminalMonitorVersion(){
		String version = "";
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi = pm.getPackageInfo("com.newland.mobileterminalmonitor", 0);
			version = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}
	/**
	 * 获取WAP厅探测软件版本
	 * @return
	 */
	public String getWapMonitorVersion(){
		String version = "";
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi = pm.getPackageInfo("io.selendroid.androiddriver", 0);
			version = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}
	/**
	 * 获取短厅探测软件版本
	 * @return
	 */
	public String getSmsMonitorVersion(){
		String version = "";
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi = pm.getPackageInfo("com.newland.smstaskclient", 0);
			version = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}
	/**
	 * 获取微信探测软件版本
	 * @return
	 */
	public String getWechatVersion(){
		String version = "";
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi = pm.getPackageInfo("com.tencent.mm", 0);
			version = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}
	/**
	 * 删除三个月之前的短信
	 */
	public static void delSms() {
		try {
			ContentResolver resolver = mContext.getContentResolver();
			Uri smsUrl = Uri.parse("content://sms/");
			String selection="date < "+String.valueOf(System.currentTimeMillis()-1000L*60*60*24*90);
			System.out.println("=================selection==============="+selection);
			Cursor cursor = resolver.query(smsUrl, new String[] { "_id", "thread_id" }, selection, null, null);
			if (null != cursor && cursor.moveToFirst()) {
				resolver.delete(smsUrl, null, null);
			}while(cursor.moveToNext());
		} catch (Exception e) {
			Log.d("删除短信", e.getMessage());
		}
	}
	
}
