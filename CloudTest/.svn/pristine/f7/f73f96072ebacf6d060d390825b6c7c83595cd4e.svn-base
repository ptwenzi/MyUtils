package com.newland.cloudtest.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xutils.DbManager;
import org.xutils.x;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.newland.cloudtest.bean.PInfo;
import com.newland.cloudtest.service.PhoneInfoService;
import com.newland.cloudtest.service.SmsService;
import com.newland.cloudtest.service.TaskScheduService;
import com.orhanobut.logger.Logger;

/**
 * 系统工具类
 * 
 * @author TongLee
 *
 */
public class SystemUtils {
	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			int versioncode = pi.versionCode;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}

	/**
	 * 返回当前程序版本名
	 */
	public static int getAppVersionCode(Context context) {
		String versionName = "";
		int versioncode = 0;
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			versioncode = pi.versionCode;
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versioncode;
	}

	public static boolean execShellCmd(String cmd) {

		try {
			System.out.println(cmd);
			// 申请获取root权限，这一步很重要，不然会没有作用
			Process process = Runtime.getRuntime().exec("su");
			// 获取输出流
			OutputStream outputStream = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(
					outputStream);
			dataOutputStream.writeBytes(cmd);
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
			String ls = null;
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), "gbk"));
			while ((ls = bufferedReader.readLine()) != null) {
				Logger.v("CMD" + ls);
				if ("INSTRUMENTATION_STATUS_CODE: 1".equals(ls)) {
					return true;
				}
			}

			bufferedReader.close();
			process.getOutputStream().close();

		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
	 * @return true代表正在运行，false代表服务没有正在运行
	 */
	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(400);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

	/**
	 * 获取网络类型
	 * 
	 * @param context
	 * @return 为空串时是没开启网络
	 */
	public static String GetNetworkType(Context context) {
		String strNetworkType = "";

		ConnectivityManager mConnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = mConnMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo networkInfo = mConnMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mWifi != null && mWifi.isConnected()) {
			strNetworkType = "WIFI";
			return strNetworkType;
		}

		if (networkInfo != null && networkInfo.isConnected()) {

			String _strSubTypeName = networkInfo.getSubtypeName();

			Log.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

			// TD-SCDMA networkType is 17
			int networkType = networkInfo.getSubtype();
			switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by 11
				strNetworkType = "2G";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by 14
			case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by 12
			case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by 15
				strNetworkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by 13
				strNetworkType = "4G";
				break;
			default:
				// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
				if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA")
						|| _strSubTypeName.equalsIgnoreCase("WCDMA")
						|| _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
					strNetworkType = "3G";
				} else {
					strNetworkType = _strSubTypeName;
				}

				break;
			}

			Log.e("cocos2d-x",
					"Network getSubtype : "
							+ Integer.valueOf(networkType).toString());

		}

		Log.e("cocos2d-x", "Network Type : " + strNetworkType);

		return strNetworkType;
	}

	/**
	 * 获取IMEI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
//		TelephonyManager telephonyManager = (TelephonyManager) context
//				.getSystemService(Context.TELEPHONY_SERVICE);
//        
//		String imei = telephonyManager.getDeviceId();
//		return imei;
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
	 * 获取手机号
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNo(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String phoneNo = telephonyManager.getLine1Number();
		return phoneNo;
	}

	/**
	 * 获取手机卡串号
	 * 
	 * @param context
	 * @return
	 */
	public static String getImsiNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi;
	}

	public static int getVersionCode(Context context)// 获取版本号(内部识别号)
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public static String getVersion(Context context)// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取安装的APP列表
	 * 
	 * @param getSysPackages
	 * @param context
	 * @return
	 */
	public static ArrayList<PInfo> getInstalledApps(boolean getSysPackages,
			Context context) {
		ArrayList<PInfo> res = new ArrayList<PInfo>();
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			ApplicationInfo appInfo = p.applicationInfo;
			if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)
			{
				   //系统程序
				continue;
			}
			else   //不是系统程序
			{
				PInfo newInfo = new PInfo();
				newInfo.appname = p.applicationInfo.loadLabel(
						context.getPackageManager()).toString();
				newInfo.pname = p.packageName;
				newInfo.versionName = p.versionName;
				newInfo.versionCode = p.versionCode;

				res.add(newInfo);
			}

			 
				
//			if ((!getSysPackages) && (p.versionName == null)) {
//				
//			}
		
		}
		return res;
	}

	/**
	 * 根据路径获取存储状态
	 * 
	 * @param path
	 * @return
	 */
	public static Map<String, Object> getMemoryInfo(File path, Context context) {
		// 获得一个磁盘状态对象
		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize(); // 获得一个扇区的大小
//		long blockSize = 3072; // 获得一个扇区的大小

		long totalBlocks = stat.getBlockCount(); // 获得扇区的总数

		long availableBlocks = stat.getAvailableBlocks(); // 获得可用的扇区数量

		// 总空间
		String totalMemory = Formatter.formatFileSize(context, totalBlocks
				* blockSize);
		// 可用空间
		String availableMemory = Formatter.formatFileSize(context,
				availableBlocks * blockSize);
		Map<String, Object> map = new HashMap<String, Object>();
		double  totalMemoryD = 0 ;
		double  availableMemoryD = 0;
		
		if(totalMemory.contains("GB")){
			totalMemoryD  = Double.parseDouble(totalMemory.replace("GB", "").trim())*1024;
		}else if(totalMemory.contains("MB")){
			totalMemoryD  = Double.parseDouble(totalMemory.replace("MB", "").trim());
		}else if(totalMemory.contains("KB")){
			totalMemoryD  = Double.parseDouble(totalMemory.replace("KB", "").trim())/1024;
		}
		
		if(availableMemory.contains("GB")){
			availableMemoryD =  Double.parseDouble(availableMemory.replace("GB", "").trim())*1024;
		}else if(totalMemory.contains("MB")){
			availableMemoryD =  Double.parseDouble(availableMemory.replace("MB", "").trim());
		}else if(totalMemory.contains("KB")){
			availableMemoryD =  Double.parseDouble(availableMemory.replace("KB", "").trim())/1024;
		}
		
		map.put("totalMemory", Math.round(totalMemoryD));
		map.put("availableMemory", 	Math.round(availableMemoryD));
		return map;
	}

	/**
	 * 获取总内存大小
	 * 
	 * @param mContext
	 * @return
	 */
	public static long getTotalRam(Context mContext) {
		String str1 = "/proc/meminfo";
		String str2;
		String[] strs;
		long totalRam = 0L;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader br = new BufferedReader(fr, 8192);
			str2 = br.readLine();
			strs = str2.split("\\s+");
			totalRam = Integer.valueOf(strs[1]).intValue() / 1024;
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return totalRam;
	}

	/**
	 * 获取内存使用率
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getRamUse(Context mContext) {
		long total = getTotalRam(mContext);
		long avail = getAvailRam(mContext);
		long use = total - avail;
		String ramUse = String.valueOf(use * 100 / total);
		float ramD = Float.parseFloat(ramUse);
		return Math.round(ramD);
	}

	/**
	 * 获取可用内存大小
	 * 
	 * @param mContext
	 * @return
	 */
	public static long getAvailRam(Context mContext) {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem / (1024 * 1024);
	}

	/**
	 * 获取存储使用率
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getRomUse(Context mContext) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalRom = stat.getBlockCount() * blockSize / (1024 * 1024);
		long availRom = stat.getAvailableBlocks() * blockSize / (1024 * 1024);
		long usedRom = totalRom - availRom;
		String romUse = String.valueOf(usedRom * 100 / totalRom);
		Float romUseF = Float.parseFloat(romUse);
		return Math.round(romUseF);
	}

	/**
	 * 获取4G流量总和
	 * 
	 * @return
	 */
	public static float get4Gflow() {
		float total = bytes2kb((TrafficStats.getMobileRxBytes() + TrafficStats
				.getMobileTxBytes()));

		return total;

	}
	
	/**
	 * 获取总wifi +4G流量总和
	 * 
	 * @return
	 */
	public static float getAllflow() {
		float total = bytes2kb((TrafficStats.getTotalTxBytes() + TrafficStats
				.getTotalRxBytes()));

		return total;

	}
	
	
	
	public static Long get4GUploadFlow()
	{
		long total =TrafficStats.getMobileTxBytes();
//		return bytes2kb(total);
		return total;
	}
	
	public static Long get4GDownFlow()
	{
		long total =TrafficStats.getMobileRxBytes();
//		return bytes2kb(total);
		return total;
	}

	/**
	 * 重启机子
	 * 
	 * @param context
	 */
	public static void rebootPhone(Context context) {
		// Intent restartIntent = new Intent(context, MainActivity.class);
		// int pendingId = 1;
		// PendingIntent pendingIntent = PendingIntent.getActivity(context,
		// pendingId, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		// AlarmManager mgr =
		// (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		// mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,
		// pendingIntent);
		String cmd = "su -c reboot";
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block

			new AlertDialog.Builder(context).setTitle("Error")
					.setMessage(e.getMessage()).setPositiveButton("OK", null)
					.show();
		}

	}

	/**
	 * 重启服务
	 * 
	 * @param context
	 */
	public static void rebootService(Context context) {
		Intent phoneInfoServiceIntent = new Intent(context,
				PhoneInfoService.class);
		Intent SmsServiceIntent = new Intent(context, SmsService.class);
		Intent taskScheduServiceIntent = new Intent(context,
				TaskScheduService.class);
		context.stopService(phoneInfoServiceIntent);
		context.stopService(taskScheduServiceIntent);
		context.stopService(SmsServiceIntent);

		context.startService(phoneInfoServiceIntent);
		context.startService(taskScheduServiceIntent);
		context.startService(SmsServiceIntent);

	}

	/**
	 * 获取DB管理对象
	 * 
	 * @return
	 */
	public static DbManager getDbManage() {
		DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
				.setDbName("cloudTest.db").setAllowTransaction(true)//设置数据库名称  
				.setDbDir(new File("/sdcard/CloudTestDB/")).setDbVersion(2) // 不设置dbDir时, 默认存储在app的私有目录  // 数据库存储路径 
				    .setDbOpenListener(new DbManager.DbOpenListener() {  
                    @SuppressLint("NewApi")
					@Override  
                    public void onDbOpened(DbManager db) {  
                       // 开启WAL, 对写入加速提升巨大  
                       db.getDatabase().enableWriteAheadLogging();  
                    }  
                })  

				.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
					@Override
					public void onUpgrade(DbManager db, int oldVersion,
							int newVersion) {
						// TODO: ...
						// db.addColumn(...);
						// db.dropTable(...);
						// ...
					}
				});
		//获取数据库单例 
		DbManager db = x.getDb(daoConfig);
		 
		return db;
	}

	/**
	 * 获取Android本机IP地址
	 * 
	 * @return
	 */

	public static String getLocalIPAddress() throws SocketException {
		for (Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
					.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress()
						&& (inetAddress instanceof Inet4Address)) {
					return inetAddress.getHostAddress().toString();
				}
			}
		}
		return null;
	}

	/**
	 * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
	 * 
	 * @param bytes
	 *            MB
	 * @return
	 */
	public static float bytes2kb(long bytes) {
		BigDecimal filesize = new BigDecimal(bytes);
		BigDecimal megabyte = new BigDecimal(1024 * 1024);
		float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
				.floatValue();
		if (returnValue > 1)
		{
//			returnValue = returnValue *1024;
			return (returnValue);
		}
		
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
				.floatValue();
		return (returnValue/1024 );
	}

	/**
	 * 获取CPU名称
	 * 
	 * @return
	 */
	public static String getCpuName() {
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);
			for (int i = 0; i < array.length; i++) {
			}
			return array[1];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static double getCPUPercent() throws IOException {
		String Result;
		StringBuffer sb = new StringBuffer();
		Process p = Runtime.getRuntime().exec("top -n 1");

		BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		while ((Result = br.readLine()) != null) {
			if (Result.trim().length() < 1) {
				continue;
			} else {
				String[] CPUusr = Result.split("%");
				// sb.append("USER:" + CPUusr[0] + "\n");
				String[] CPUusage = CPUusr[0].split("User");
				// String[] SYSusage = CPUusr[1].split("System");
				// sb.append("CPU:" + CPUusage[1].trim() + " length:"
				// + CPUusage[1].trim().length() + "\n");
				// sb.append("SYS:" + SYSusage[1].trim() + " length:"
				// + SYSusage[1].trim().length() + "\n");
				// sb.append(Result + "\n");
				sb.append(CPUusage[1].trim());
				break;
			}
		}
		return Double.parseDouble(sb.toString()) ;
	}

	/**
	 * 计算已使用内存的百分比
	 *
	 */
	public static String getUsedPercentValue(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine
					.indexOf("MemTotal:"));
			br.close();
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
					"\\D+", ""));
			long availableSize = getAvailableMemory(context) / 1024;
			int percent = (int) ((totalMemorySize - availableSize)
					/ (float) totalMemorySize * 100);
			return percent + "%";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "无结果";
	}

	/**
	 * 获取当前可用内存，返回数据以字节为单位。
	 * 
	 * @return 当前可用内存。
	 */
	private static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		am.getMemoryInfo(mi);
		return mi.availMem;
	}

	/*
	 * 判断是否为整数
	 * 
	 * @param str 传入的字符串
	 * 
	 * @return 是整数返回true,否则返回false
	 */

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 
	 * 截屏
	 * 
	 * @param activity
	 * 
	 * @return
	 */

	public static String captureScreen(Context activity) {

		// 获取屏幕大小：

		DisplayMetrics metrics = new DisplayMetrics();

		WindowManager WM = (WindowManager) activity

		.getSystemService(Context.WINDOW_SERVICE);

		Display display = WM.getDefaultDisplay();

		display.getMetrics(metrics);

		int height = metrics.heightPixels; // 屏幕高

		int width = metrics.widthPixels; // 屏幕的宽

		// 获取显示方式

		int pixelformat = display.getPixelFormat();

		PixelFormat localPixelFormat1 = new PixelFormat();

		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);

		int deepth = localPixelFormat1.bytesPerPixel;// 位深

		byte[] piex = new byte[height * width * deepth];

		try {

			Runtime.getRuntime().exec(

			new String[] { "/system/bin/su", "-c",

			"chmod 777 /dev/graphics/fb0" });

		} catch (IOException e) {

			e.printStackTrace();

		}

		try {

			// 获取fb0数据输入流

			InputStream stream = new FileInputStream(new File(

			"/dev/graphics/fb0"));

			DataInputStream dStream = new DataInputStream(stream);

			dStream.readFully(piex);

		} catch (Exception e) {

			e.printStackTrace();

		}

		// 保存图片

		int[] colors = new int[height * width];

		for (int m = 0; m < colors.length; m++) {

			int r = (piex[m * 4] & 0xFF);

			int g = (piex[m * 4 + 1] & 0xFF);

			int b = (piex[m * 4 + 2] & 0xFF);

			int a = (piex[m * 4 + 3] & 0xFF);

			colors[m] = (a << 24) + (r << 16) + (g << 8) + b;

		}

		// piex生成Bitmap

		Bitmap bitmap = Bitmap.createBitmap(colors, width, height,

		Bitmap.Config.ARGB_8888);

		File f = null;
		try {    		
		   	Bitmap mBitmap = bitmap;
		   	f = new File(Environment.getExternalStorageDirectory()+"/mypic.png"); 
		   	f.createNewFile();                         
		   	FileOutputStream fOut = new FileOutputStream(f);
		   	mBitmap.compress(Bitmap.CompressFormat.PNG, 80, fOut);
	    		fOut.flush();              
	    		fOut.close();                         
	    	} catch (Exception e) {                                 
	    			e.printStackTrace();                         
	    	}                 
		
		
		return f.getAbsolutePath();

	}
	
	public static void killPorcess(String processId){
		try{
			String cmd = "kill -9 " + processId;
			Process process = Runtime.getRuntime().exec("su");
			// 获取输出流
			OutputStream outputStream = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(
					outputStream);
			dataOutputStream.writeBytes(cmd);
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void stopProcess(String processName){
		 String pscmd = "ps | grep " + processName;
		 try{
			 Runtime runtime = Runtime.getRuntime();
			 Process proc = runtime.exec(pscmd);
			 InputStream inputstream = proc.getInputStream();  
			 InputStreamReader inputstreamreader = new InputStreamReader(inputstream);  
			 BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
			 String line = "";  
			 StringBuilder sb = new StringBuilder(line);
			 while ((line = bufferedreader.readLine()) != null) {  
		            //System.out.println(line);  
				 sb.append(line);  
				 sb.append('\n');  
			 }
			 proc.waitFor();
			 String[] l = sb.toString().split("root");
			 System.out.print(sb.toString());
			 for(String s:l){
				 if(s.contains(processName)){
					 String[] ls = s.split(" ");
					 for(String p:ls){
						 if(p.length() > 0){
							 killPorcess(p);
							 break;
						 }
					 }
				 }
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
	final static Pattern pattern = Pattern.compile("\\S*[?]\\S*");

	/**
	     * 获取链接的后缀名
	     * @return
	     */
	    public static String parseSuffix(String url) {

	    	if(url != null && url.contains("."))
	    	{

		        Matcher matcher = pattern.matcher(url);

		        String[] spUrl = url.toString().split("/");
		        int len = spUrl.length;
		        String endUrl = spUrl[len - 1];

		        if(matcher.find()) {
		            String[] spEndUrl = endUrl.split("\\?");
		            return spEndUrl[0].split("\\.")[1];
		        }
		        return endUrl.split("\\.")[1];
	    	}
	    	else {
				return "ct";
			}
	    	
	    }

}
