package com.newland.mobileterminalmonitor.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * 手机内存信息
 * @author linh
 */
public class MemoryInfo {

	/**
	 * 获取总内存大小
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
	 * 获取可用内存大小
	 * @param mContext
	 * @return
	 */
	public static long getAvailRam(Context mContext) {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem / (1024 * 1024);
	}
	/**
	 * 获取内存使用率
	 * @param mContext
	 * @return
	 */
	public static String getRamUse(Context mContext) {
		long total = getTotalRam(mContext);
		long avail = getAvailRam(mContext);
		long use = total - avail;
		String ramUse = String.valueOf(use * 100 / total);
		return ramUse;
	}
	/**
	 * 获取存储大小
	 * @param mContext
	 * @return
	 */
	public static long getTotalRom(Context mContext) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize / (1024 * 1024);
	}
	/**
	 * 获取存储使用率
	 * @param mContext
	 * @return
	 */
	public static String getRomUse(Context mContext) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalRom = stat.getBlockCount() * blockSize / (1024 * 1024);
		long availRom = stat.getAvailableBlocks() * blockSize / (1024 * 1024);
		long usedRom = totalRom - availRom;
		String romUse = String.valueOf(usedRom * 100 / totalRom);
		return romUse;
	}
	
}
