package com.newland.cloudtest.util;

import java.io.File;
import java.io.PrintWriter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class ApkController {

	/**
	 * 描述: 安装 修改人: 吴传龙 最后修改时间:2015年3月8日 下午9:07:50
	 */
	public static boolean install(String apkPath, Context context) {
		Toast.makeText(context, "准备升级APP", 0).show();
		// 先判断手机是否有root权限
		if (hasRootPerssion()) {
			Toast.makeText(context, "静默方式升级", 0).show();
			// 有root权限，利用静默安装实现
			return ApkController.clientInstall(apkPath, context);
		} else {
			Toast.makeText(context, "普通方式升级", 0).show();
			// 没有root权限，利用意图进行安装
			File file = new File(apkPath);
			if (!file.exists())
				return false;
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			context.startActivity(intent);
			return true;
		}
	}
	
	public static boolean installByIntent(String apkPath, Context context) {
			// 没有root权限，利用意图进行安装
			File file = new File(apkPath);
			if (!file.exists())
				return false;
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
			return true;
	}
	

	/**
	 * 描述: 卸载 修改人: 吴传龙 最后修改时间:2015年3月8日 下午9:07:50
	 */
	public static boolean uninstall(String packageName, Context context) {
		if (hasRootPerssion()) {
			// 有root权限，利用静默卸载实现
			return clientUninstall(packageName);
		} else {
			Uri packageURI = Uri.parse("package:" + packageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
					packageURI);
			uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(uninstallIntent);
			return true;
		}
	}

	/**
	 * 判断手机是否有root权限
	 */
	private static boolean hasRootPerssion() {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		
	}

	/**
	 * 静默安装
	 */
	private static boolean clientInstall(String apkPath, Context context) {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.println("chmod 777 " + apkPath);
			//PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
			PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + apkPath);
			//PrintWriter.println("exit");
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (process != null) {
					process.destroy();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/*private static boolean clientInstall(String apkPath, Context context) {
		boolean result = false;
		Process process = null;
		OutputStream out = null;
		try {
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(out);
			dataOutputStream.writeBytes("chmod 777 " + apkPath + "\n");
			//dataOutputStream.writeBytes("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
			dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "+apkPath);

			// 提交命令
			dataOutputStream.flush();
			dataOutputStream.writeBytes("exit");
			dataOutputStream.flush();
			// 关闭流操作
			dataOutputStream.close();
			out.close();
			int value = process.waitFor();
			// 代表成功
			if (value == 0) {
				result = true;
			} else if (value == 1) { // 失败
				result = false;
			} else { // 未知情况
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			result = false;
		} catch (Exception e){
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}*/
	

	/**
	 * 静默卸载
	 */
	private static boolean clientUninstall(String packageName) {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
			PrintWriter.println("pm uninstall " + packageName);
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	/**
	 * 启动app com.exmaple.client/.MainActivity
	 * com.exmaple.client/com.exmaple.client.MainActivity
	 */
	public static boolean startApp(String packageName, String activityName) {
		boolean isSuccess = false;
		String cmd = "am start -n " + packageName + "/" + activityName + " \n";
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return isSuccess;
	}

	private static boolean returnResult(int value) {
		// 代表成功
		if (value == 0) {
			return true;
		} else if (value == 1) { // 失败
			return false;
		} else { // 未知情况
			return false;
		}
	}

}
