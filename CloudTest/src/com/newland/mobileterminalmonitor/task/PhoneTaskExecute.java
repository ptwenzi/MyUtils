package com.newland.mobileterminalmonitor.task;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;
import com.newland.mobileterminalmonitor.bean.Phone;
import com.newland.mobileterminalmonitor.bean.PhoneState;
import com.newland.mobileterminalmonitor.bean.PhoneTask;
import com.newland.mobileterminalmonitor.common.AppManager;
import com.newland.mobileterminalmonitor.common.CpuInfo;
import com.newland.mobileterminalmonitor.common.MemoryInfo;
import com.newland.mobileterminalmonitor.common.PhoneInfo;


/**
 * 执行下发非测试配置的任务
 * @author linh
 */
public class PhoneTaskExecute extends Thread {
	
	private static String TAG = "PhoneTaskExecute";
	private Socket socket;
	private static Context mContext;
	private Phone phone;
	private PhoneInfo phoneInfo;
	private PhoneTask phonetask;
	private PhoneState phoneState;
	private SharedPreferences preferences;
	
	public PhoneTaskExecute(Socket socket,Context context){
		this.socket = socket;
		mContext = context;
		this.phone = new Phone();
		this.phoneInfo = new PhoneInfo(context);
		this.phonetask = new PhoneTask();
		this.phoneState = new PhoneState();
		preferences = mContext.getSharedPreferences("phone", Context.MODE_PRIVATE);
	}
	@Override
	public void run() {
		try {
			if(!socket.isConnected()) {
				return;
			}
			BufferedInputStream in =new BufferedInputStream(socket.getInputStream());
			String receiveMessage = readFromSocket(in);
			Log.d(TAG, "收到电脑发来的数据=================" + receiveMessage);
			phonetask = new Gson().fromJson(receiveMessage, PhoneTask.class);
			processPhonetask(phonetask);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null!=socket){
				try {
					socket.close();
					socket=null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 读取来自PC发送的消息
	 * @param in
	 * @return
	 */
	private String readFromSocket(InputStream in) {
		String message = "";
		byte[] tempbuffer = new byte[2048];
		try {
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			message = new String(tempbuffer, 0, numReadedBytes, "utf-8");
			tempbuffer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	/**
	 * 执行任务
	 * @param phoneTask
	 * @param handler
	 */
	private void processPhonetask(PhoneTask phoneTask){
		int command = phoneTask.getCommand();
		String taskParam = phoneTask.getTaskParam1();
		Log.d(TAG, "command============="+command+"\n"+"taskparam============="+taskParam);
		boolean needBreak = false;
		switch (command) {
		// 定时上传
		case 000:
			phone = createPhoneinfo();
				try {
					OutputStream out = socket.getOutputStream();
					PrintWriter pw = new PrintWriter(out,true);
					pw.println(new Gson().toJson(phone));
					pw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			break;
		// 设置终端信息上传时间间隔
		case 100:
			phone = createPhoneinfo();
			while(true){
				try {
					OutputStream out = socket.getOutputStream();
					PrintWriter pw = new PrintWriter(out, true);
					pw.println(new Gson().toJson(phone));
					pw.flush();
					Thread.sleep(Integer.parseInt(taskParam));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if(needBreak){
						break;
					}
				}
			}
			break;
		// 上网模式变更
		case 300:
			WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			if(StringUtils.equalsIgnoreCase("WIFI", taskParam)){
				wm.setWifiEnabled(true);
				AppManager.getAppManager().toggleMobileData(mContext, false);
				try {
					OutputStream out = socket.getOutputStream();
					PrintWriter pw=new PrintWriter(out,true);
					pw.println("==================当前上网模式已变更为wifi===============");
					pw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(StringUtils.equalsIgnoreCase("MobileData", taskParam)){
				wm.setWifiEnabled(false);
				AppManager.getAppManager().toggleMobileData(mContext, true);
				try {
					OutputStream out = socket.getOutputStream();
					PrintWriter pw=new PrintWriter(out,true);
					pw.println("===================当前上网模式已变更为移动网络==================");
					pw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		// 上传手机信号值和当前网络制式
		case 999:
			try {
				OutputStream socketOut=socket.getOutputStream();
				PrintWriter pw = new PrintWriter(socketOut,true);
				int dbm = preferences.getInt("dbm", 0);
				String networkType = preferences.getString("networktype", "");
				phoneState.setDbm(String.valueOf(dbm));
				phoneState.setNetworkType(networkType);
				String sendToComputer = new Gson().toJson(phoneState);
				Log.d(TAG, "发送到电脑的数据==================="+sendToComputer);
				pw.println(sendToComputer);
				pw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	/**
	 * 设置数据
	 * @return
	 */
	private Phone createPhoneinfo(){
		Phone phone = new Phone();
		phone.setBrandName(phoneInfo.getPhoneBrand());
		phone.setModelName(phoneInfo.getPhoneName());
		phone.setImeiNumber(phoneInfo.getImeiNumber());
		phone.setImsiNumber(phoneInfo.getImsiNumber());
		phone.setOsName(phoneInfo.getOsName());
		phone.setOsVersion(phoneInfo.getOsVersion());
		phone.setScreenResolution(preferences.getString("screenResolution", ""));
		phone.setCpu(CpuInfo.getCpuName());
		phone.setRam(((Long) MemoryInfo.getTotalRam(mContext)).intValue());
		phone.setRom(((Long) MemoryInfo.getTotalRom(mContext)).intValue());
		phone.setCpuUse(((Long) Math.round(CpuInfo.getUsage())).intValue());
		phone.setRamUse(Integer.parseInt(MemoryInfo.getRamUse(mContext)));
		phone.setRomUse(Integer.parseInt(MemoryInfo.getRomUse(mContext)));
		phone.setAppDesc(phoneInfo.getAllApp());
		phone.setBatteryUse(preferences.getInt("batteryUse", 0));
		phone.setBatteryVoltage(preferences.getLong("batteryVoltage", 0L));
		phone.setBatteryTemp(preferences.getInt("batteryTemp", 0));
		phone.setDbm(preferences.getInt("dbm", 0));
		phone.setNetworkModel(preferences.getString("networktype", ""));
		phone.setSmsMonitorVersion(phoneInfo.getSmsMonitorVersion());
		phone.setTerminalMonitorVersion(phoneInfo.getTerminalMonitorVersion());
		phone.setWapMonitorVersion(phoneInfo.getWapMonitorVersion());
		phone.setWechatMonitorVersion(phoneInfo.getWechatVersion());
		return phone;
	}

}
