package com.newland.mobileterminalmonitor.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 手机CPU信息
 * 
 * @author linh
 *
 */
public class CpuInfo {

	static long total = 0;
	static long idle = 0;
	static double usage = 0;

	public void CPULoad() {
		readUsage();
	}

	public static void readUsage() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			String[] toks = load.split(" ");
			long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
			long currIdle = Long.parseLong(toks[5]);
			usage = (currTotal - total) * 100.0f / (currTotal - total + currIdle - idle);
			total = currTotal;
			idle = currIdle;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 获取CPU使用率
	 * @return
	 */
	public static double getUsage() {
		readUsage();
		return usage;
	}
	/**
	 * 获取CPU型号
	 * @return
	 */
	public static String getCpuName() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" };
		String[] strs;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader br = new BufferedReader(fr);
			str2 = br.readLine();
			strs = str2.split("\\s+");
			for (int i = 2; i < strs.length; i++) {
				cpuInfo[0] = cpuInfo[0] + strs[i] + " ";
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cpuInfo[0];
	}

}
