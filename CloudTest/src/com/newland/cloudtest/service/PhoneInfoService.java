package com.newland.cloudtest.service;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newland.cloudtest.MyApplication;
import com.newland.cloudtest.bean.BamNl9800Result;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.ConfigDb;
import com.newland.cloudtest.bean.FlowBean;
import com.newland.cloudtest.bean.MyXutilsRequestParams;
import com.newland.cloudtest.bean.PInfo;
import com.newland.cloudtest.bean.RequestData;
import com.newland.cloudtest.bean.ResponeData;
import com.newland.cloudtest.bean.Rphone;
import com.newland.cloudtest.bean.SMSInfo;
import com.newland.cloudtest.util.ApkController;
import com.newland.cloudtest.util.Contant;
import com.newland.cloudtest.util.DateUtil;
import com.newland.cloudtest.util.DownloadUtils;
import com.newland.cloudtest.util.FileHelper;
import com.newland.cloudtest.util.HttpInterfaceHelper;
import com.newland.cloudtest.util.SharedPreferencesUtils;
import com.newland.cloudtest.util.SmsSytemUtils;
import com.newland.cloudtest.util.SystemUtils;
import com.orhanobut.logger.Logger;

/**
 * 获取手机信息
 * 
 * @author TongLee
 *
 */
public class PhoneInfoService extends Service {
	// public ServiceBinder mBinder = new ServiceBinder(); /* 数据通信的桥梁 */
	public MyReceiver myReceiver; // 主接受广播
	public CheckUpdateApkReceiver checkUpdateApkReceiver;
	public BatteryReceiver batteryReceiver = new BatteryReceiver();
	public static String Action = "com.newland.PhoneInfoBroadcast";
	public static String isNeedUpdateApkAction = "com.newland.UpdateApkBroadcast";
	private int battery;// 剩余电量
	private int batteryVoltage;// 电压
	private int batteryTempl;// 电池温度
	private String networkType;// 网络状态
	TelephonyManager tel;
	private int netWorkStrength;// 网络信号
	private String ApkModel = android.os.Build.VERSION.RELEASE; // apk版本
	private String phoneModel = android.os.Build.MODEL; // 手机型号
	private String brand = android.os.Build.BRAND;
	private String simOperatorName;// 运营商名字
	private int ramUseRate;// 内存使用率
	private int romUseRate;// 存储使用率
	private int CPUUseRate;// CPU使用率
	private String CpuName;// cpu名字
	private Long diskInfo;// 存储大小
	private Long memoryInfo;// 内存大小
	private final Timer updateConfigRatetaskTimer = new Timer();
	private final Timer uploadTimer = new Timer(); //上传信息
	private final Timer updateTimer = new Timer(); //检查更新
	public boolean isStop = false;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
	
	WakeLock mWakeLock;// 电源锁
	// 网络状态监听
	private MyPhoneNetWorkStateListener MyPhoneNetWorkStateListener = new MyPhoneNetWorkStateListener();

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Logger.i("PhoneInfoService Service Create...");
		super.onCreate();
		isStop= false;
		myReceiver = new MyReceiver();
		checkUpdateApkReceiver = new CheckUpdateApkReceiver();
		registerReceiver(checkUpdateApkReceiver, new IntentFilter(isNeedUpdateApkAction));
		registerReceiver(myReceiver, new IntentFilter(Action));
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryReceiver, filter);// 注册BroadcastReceiver
		// 监听网络状态信号
		tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tel.listen(MyPhoneNetWorkStateListener,	PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//		Logger.v(networkType);
//		Logger.v(brand);
		// SystemUtils.getInstalledApps(false,this);
		initInfo();
		startTimer();
		saveLog();//保存日志
		monitor4GFlow();
		//uploadPhoneThread();
		//updateThread();//自动检查更新
		acquireWakeLock();
	}
	
	private void startTimer(){
		//Contant.updatePhoneInfoRate
		uploadTimer.schedule(uploadTask, 10000, 15 * 60 * 1000); 
		updateTimer.schedule(updateTask, 20000, 60 * 60 * 1000);
	}

	/**
	 * 更新配置表
	 */
	TimerTask UpdateConfigRatetask = new TimerTask() {

		@Override
		public void run() {
			getUpdateRate();
		}
	};
	
	Handler myHandler = new Handler() {  
	    @Override  
	    public void handleMessage(Message msg) {  
	    	switch (msg.what) {
			case 1:
				uploadPhoneInfo(); 
				uploadSmsInfo();
				break;
			case 2:
				isUpdateApk();
				break;
			default:
				break;
			}
	        super.handleMessage(msg);  
	    }  
	};
	/**
	 * 上传信息
	 */
	TimerTask uploadTask = new TimerTask() {
		@Override
		public void run() {
			//uploadPhoneInfo();
			Message message = new Message();  
	        message.what = 1;  
	        myHandler.sendMessage(message);  
		}
	};
	
	/**
	 * 上传信息
	 */
	TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			//uploadPhoneInfo();
			Message message = new Message();  
	        message.what = 2;  
	        myHandler.sendMessage(message);  
		}
	};
	  
	
	
	/**
	 * 监控当日4g流量消耗  12:00上报一次
	 *
	 */
	private void monitor4GFlow()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isStop)
				{
					//检查今日是否有记录起始日期
					Date recordDate = new Date();
					String recordDateStr = DateUtil.getDateNowString(recordDate);//获取记录开始时间
					try {
						FlowBean flowBean = SystemUtils.getDbManage().selector(FlowBean.class).where("recordTime","=",recordDateStr).findFirst();
						if(flowBean == null)//说明今日没有记录起始，开始记录
						{
							flowBean = new FlowBean();
							flowBean.setFlowBeanId(UUID.randomUUID().toString());
							flowBean.setRecordTime(recordDateStr);
							flowBean.setRecordStart(new Date());
							flowBean.setUpflowStart(SystemUtils.get4GUploadFlow()<0?0:SystemUtils.get4GUploadFlow());
							flowBean.setDownflowStart(SystemUtils.get4GDownFlow()<0?0:SystemUtils.get4GDownFlow());
							SystemUtils.getDbManage().save(flowBean);
							HttpInterfaceHelper.getInstance().upload4GDayFlow(getApplicationContext());
							HttpInterfaceHelper.getInstance().clearLastDate();
						}
						else {//已经有今日数据了
							flowBean.setRecordEnd(new Date());
							flowBean.setUpflowStart(SystemUtils.get4GUploadFlow()<0?0:SystemUtils.get4GUploadFlow());
							flowBean.setDownflowStart(SystemUtils.get4GDownFlow()<0?0:SystemUtils.get4GDownFlow());
							SystemUtils.getDbManage().update(flowBean,null);  
						}
					} catch (DbException e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(1000*30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
		
	}
	
	
	private void updateThread()
	{
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!isStop) {
					
					try {
						isUpdateApk();
						Thread.sleep(30*60*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				
			}
		}).start();
	}
	
	
	
	/**
	 * 上传手机信息
	 */
	private void uploadPhoneThread()
	{
		new Thread(){
			@SuppressWarnings("static-access")
			public synchronized void run(){
				while (!isStop) {
					try {
						uploadPhoneInfo();
						this.sleep(Contant.updatePhoneInfoRate);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/*
	 * 获取刷新频率
	 */
	private void getUpdateRate() {
		MyXutilsRequestParams params = new MyXutilsRequestParams("https://www.pgyer.com/");
		params.setCharset("utf-8");
		params.addBodyParameter("wd", "xUtils");
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Logger.v(result);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
		
				Logger.v(ex.getMessage());
			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
				Logger.v("cancelled");
			}

			@Override
			public void onFinished() {

			}
		});
	}
	
	/**
	 * 上传手机信息
	 */
	private void uploadPhoneInfo()
	{
		
		Rphone rphone = new Rphone();
		rphone.setRphoneName(brand+" - "+phoneModel);
		rphone.setModelName(phoneModel);
		rphone.setBrandName(brand);
		rphone.setImeiNumber(SystemUtils.getIMEI(getApplicationContext()));
		rphone.setImsiNumber(SystemUtils.getImsiNumber(getApplicationContext()));
		rphone.setOsName("Android");
		rphone.setOsVersion(ApkModel);
		rphone.setCpu(CpuName);
		rphone.setRam(memoryInfo); 
		rphone.setRom(diskInfo);
		rphone.setCpuUse(CPUUseRate);
		rphone.setRamUse(ramUseRate);
		rphone.setRomUse(romUseRate);
		rphone.setBatteryUse(battery);
		rphone.setBatteryVoltage(batteryVoltage);
		rphone.setBatteryTemp(batteryTempl);
		rphone.setNetworkModel(networkType);
		rphone.setDbm(netWorkStrength);
		rphone.setScreenResolution(SharedPreferencesUtils.getConfigStr(getApplicationContext(), Contant.cachName, "density"));
		Long lastTime = SharedPreferencesUtils.getConfigLong(getApplicationContext(), Contant.cachName, "lastUploadPInfoTime", System.currentTimeMillis() - 3 * 60 * 60 * 1000);
//		if (System.currentTimeMillis() - lastTime > 2 * 60 * 60 * 1000){//如果距离上次上报安装软件信息超过2小时，重新上报
			ArrayList<PInfo> list =  SystemUtils.getInstalledApps(false, this);
			rphone.setAppDesc(JSON.toJSONString(list));
			SharedPreferencesUtils.setConfigLong(getApplicationContext(), Contant.cachName, "lastUploadPInfoTime", System.currentTimeMillis());
//		}
		MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP+"/rphone/upPhoneInfo");
		params.setCharset("utf-8");
		RequestData requestData = new RequestData();
		requestData.setRequestTime(System.currentTimeMillis()+"");
		HashMap<String, Object>map = new HashMap<String, Object>();
		map.put("phone", rphone);
		requestData.setData(map);
//		Logger.v(JSON.toJSONString(requestData));
		String json = JSON.toJSONString(requestData);
		params.addBodyParameter("data",JSON.toJSONString(requestData));
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
//				Toast.makeText(x.app(), "上报手机信息成功：" + result, Toast.LENGTH_LONG).show();
				Logger.v(result);
				SharedPreferencesUtils.setConfigStr(getApplicationContext(), Contant.cachName, "onlineStatue", "在线");
				ResponeData responeData = JSON.parseObject(result, ResponeData.class);
				if(responeData!= null && responeData.getData()!=null)
				{
					JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(responeData.getData()));
					int updatePhoneInfoRate  = jsonObject.getIntValue("updatePhoneInfoRate");
					int task4GTimeOut  = jsonObject.getIntValue("task4GTimeOut");
					int taskSmsTimeOut  = jsonObject.getIntValue("taskSmsTimeOut");
					int taskAppTimeOut  = jsonObject.getIntValue("taskAppTimeOut");
					int taskResultUploadTimeOut  = jsonObject.getIntValue("taskResultUploadTimeOut");
					try {
						SystemUtils.getDbManage().delete(ConfigDb.class);
					} catch (DbException e1) {
						e1.printStackTrace();
					}
					if(updatePhoneInfoRate!=0)
					{
						Contant.updatePhoneInfoRate = updatePhoneInfoRate;
						ConfigDb configDb = new ConfigDb();
						configDb.setKey("updatePhoneInfoRate");
						configDb.setValue(updatePhoneInfoRate);
						try {
							SystemUtils.getDbManage().save(configDb);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
					if(task4GTimeOut!=0)
					{
						Contant.task4GTimeOut = task4GTimeOut;
						ConfigDb configDb = new ConfigDb();
						configDb.setKey("task4GTimeOut");
						configDb.setValue(task4GTimeOut);
						try {
							SystemUtils.getDbManage().save(configDb);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
					if(taskSmsTimeOut!=0)
					{
						Contant.taskSmsTimeOut = taskSmsTimeOut;
						ConfigDb configDb = new ConfigDb();
						configDb.setKey("taskSmsTimeOut");
						configDb.setValue(taskSmsTimeOut);
						try {
							SystemUtils.getDbManage().save(configDb);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
					if(taskAppTimeOut!=0)
					{
						Contant.taskAppTimeOut = taskAppTimeOut;
						ConfigDb configDb = new ConfigDb();
						configDb.setKey("taskAppTimeOut");
						configDb.setValue(taskAppTimeOut);
						try {
							SystemUtils.getDbManage().save(configDb);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
					if(taskResultUploadTimeOut!=0)
					{
						Contant.taskResultUploadTimeOut = taskResultUploadTimeOut;
						ConfigDb configDb = new ConfigDb();
						configDb.setKey("taskResultUploadTimeOut");
						configDb.setValue(taskResultUploadTimeOut);
						try {
							SystemUtils.getDbManage().save(configDb);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
					
					
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Toast.makeText(x.app(), "上报手机信息出错：" + ex.getMessage(), Toast.LENGTH_LONG).show();
				Logger.v(ex.getMessage());
				SharedPreferencesUtils.setConfigStr(getApplicationContext(), Contant.cachName, "onlineStatue", "离线");
			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
				Logger.v("cancelled");
			}

			@Override
			public void onFinished() {

			}
		});
	}

	/**
	 * 上传短信内容
	 */
	private void uploadSmsInfo() {

		//获取5分钟前到现在的所有短信
		Long uploadTime = System.currentTimeMillis();
		String uploadTimeStr = sdf.format(new Date(uploadTime)).substring(0, 16);
		Long lastUploadNl9800Time = SharedPreferencesUtils.getConfigLong(getApplicationContext(),  Contant.cachName, "uploadSmsInfoTime", uploadTime - 2 * 60 * 60 * 1000);
		if(uploadTime - lastUploadNl9800Time >  2 * 60 * 60 * 1000){
			lastUploadNl9800Time = uploadTime - 2 * 60 * 60 * 1000;
		}
		
		SharedPreferencesUtils.setConfigLong(getApplicationContext(), Contant.cachName, "uploadSmsInfoTime", uploadTime);
		String lastUploadNl9800TimeStr  = sdf.format(new Date(lastUploadNl9800Time)).substring(0, 16);
		try {
			List<BamNl9800Result> nl9800Results = new ArrayList<BamNl9800Result>();
			
			//查询到发送的短信内容
			List<BamNormalModelDetailPara> sendList = SystemUtils.getDbManage().selector(BamNormalModelDetailPara.class)
					.where("begintime","<=",uploadTimeStr)
					.and("begintime",">",lastUploadNl9800TimeStr)
					.and("operateType","=","Send").findAll();
			StringBuffer sb = new StringBuffer();
			if(sendList != null && sendList.size() > 0){
				for (BamNormalModelDetailPara bamNormalModelDetailPara : sendList) {
					String timeStr = bamNormalModelDetailPara.getBegintime();
					if(StringUtils.isNotEmpty(bamNormalModelDetailPara.getBegintime())){
						timeStr = formatTime(bamNormalModelDetailPara.getBegintime());
					}
					
					BamNl9800Result bamNl9800Result = new BamNl9800Result();
					bamNl9800Result.setImsinumber(SystemUtils.getImsiNumber(getApplicationContext()));//Sim卡的IMSI号码
					bamNl9800Result.setCalltype(71);//71短信发送 72短信接收
					bamNl9800Result.setOtherparty(bamNormalModelDetailPara.getReceivePhoneNo());// 对方号码
					bamNl9800Result.setSmmessage(bamNormalModelDetailPara.getContent());//短信内容,cmwap报文内容
					bamNl9800Result.setBegintime(timeStr);//任务开始时间
					bamNl9800Result.setCalltime(timeStr);//拨打时间
					bamNl9800Result.setWaittime(timeStr);//振铃时间
					bamNl9800Result.setStarttime(timeStr);//通话时间，短信发送成功时间，短信接收时间
					if(bamNormalModelDetailPara.getStatue() == 0){
						bamNl9800Result.setStopcause(1);//话单结果，1成功,0失败
					}else if(bamNormalModelDetailPara.getStatue() == 1){
						bamNl9800Result.setStopcause(0);//话单结果，1成功,0失败
					}
					bamNl9800Result.setJobname("本地化探测");//任务名称
					bamNl9800Result.setSmsflag(0);//接收短信话单是否已经取走，1取走
//						bamNl9800Result.setCreatetime(new Date());//话单保存到oracle数据库的时间
					
					nl9800Results.add(bamNl9800Result);
					
				}
			}
			
			//获取收到的短信内容
			List<SMSInfo> receiveList = SmsSytemUtils.getInstance().findReceiveSmsInfoAfterTime(getApplicationContext(), new Date(lastUploadNl9800Time));
			if (receiveList != null && receiveList.size() > 0){
				for (SMSInfo sms : receiveList){
					String timeStr = sdf2.format(sms.getCreateTime());
					
					BamNl9800Result bamNl9800Result = new BamNl9800Result();
					bamNl9800Result.setImsinumber(SystemUtils.getImsiNumber(getApplicationContext()));//Sim卡的IMSI号码
					bamNl9800Result.setCalltype(72);//71短信发送 72短信接收
					bamNl9800Result.setOtherparty(sms.getMobile());// 对方号码
					bamNl9800Result.setSmmessage(sms.getContent());//短信内容,cmwap报文内容
					bamNl9800Result.setBegintime(timeStr);//任务开始时间
					bamNl9800Result.setCalltime(timeStr);//拨打时间
					bamNl9800Result.setWaittime(timeStr);//振铃时间
					bamNl9800Result.setStarttime(timeStr);//通话时间，短信发送成功时间，短信接收时间
					bamNl9800Result.setStopcause(1);//话单结果，1成功,0失败
					
					bamNl9800Result.setJobname("本地化探测");//任务名称
					bamNl9800Result.setSmsflag(0);//接收短信话单是否已经取走，1取走
//					bamNl9800Result.setCreatetime(new Date());//话单保存到oracle数据库的时间
					
					nl9800Results.add(bamNl9800Result);
				}
			}			
			
			//上传两个内容
			if(nl9800Results.size() > 0){
				uploadBamNl9800Result(nl9800Results);
			}
			
		} catch (DbException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	
	/**处理时间格式
	 * @param begintime yyyy-MM-dd hh:mm:ss.SSS
	 * @return yyyyMMddHHmmss.SSS
	 */
	private String formatTime(String begintime) {
		return begintime.replace(" ","").replace("-", "").replace(":", "");
	}

	/**
	 * 上传话单结果
	 * 
	 * @param socket
	 * @param bamNl9800Result
	 */
	private static void uploadBamNl9800Result(List<BamNl9800Result> bamNl9800Result) {
		MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP + "/bamNl9800Result/upload");
		params.setCharset("utf-8");
		RequestData requestData = new RequestData();
		requestData.setRequestTime(System.currentTimeMillis()+"");
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("list", bamNl9800Result);
		requestData.setData(map);
//		Logger.v(JSON.toJSONString(requestData));
		String json = JSON.toJSONString(requestData);
		params.addBodyParameter("data", json);
//		System.out.println("json---------"+json);
		x.http().post(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
//				Toast.makeText(x.app(), "success", Toast.LENGTH_LONG).show();
				Logger.v(result);
				
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Toast.makeText(x.app(), "onlineStatue", Toast.LENGTH_LONG).show();
				Logger.v(ex.getMessage());
			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
				Logger.v("cancelled");
			}

			@Override
			public void onFinished() {

			}
		});
	}
	
	private void initInfo() {
		// 获取网络类型
		networkType = SystemUtils.GetNetworkType(this);
		CpuName = SystemUtils.getCpuName();
		diskInfo = (Long)SystemUtils.getMemoryInfo(Environment.getDataDirectory(),
				this).get("totalMemory");
		memoryInfo = SystemUtils.getTotalRam(this);
		try {
			CPUUseRate = (int) Math.round(SystemUtils.getCPUPercent());
			ramUseRate = SystemUtils.getRamUse(this);
			romUseRate = SystemUtils.getRomUse(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

		
		
		
	}
	/** 检查数据库中是否有这个版本号，本地是否还有这个apk
	 * @param getVersionCode 获取到的版本号 
	 * @return
	 */
	private boolean checkUpdateApk(int getVersionCode){
		String versionCode = SharedPreferencesUtils.getConfigStr(getApplicationContext(), Contant.cachName, "versionCode");
		if (StringUtils.isEmpty(versionCode)){//如果本地没有versionCode，表示没有成功下载过，直接下载
			if(MyApplication.ISDEBUG){
				Toast.makeText(getApplicationContext(), "如果本地没有versionCode，表示没有成功下载过，直接下载", Toast.LENGTH_LONG).show();
			}
			return true;
		}
		if (!String.valueOf(getVersionCode).equalsIgnoreCase(versionCode)) {//如果上次下在成功版本号和服务器返回版本号不一致，直接下载
			if(MyApplication.ISDEBUG){
				Toast.makeText(getApplicationContext(), "如果上次下在成功版本号和服务器返回版本号不一致，直接下载", Toast.LENGTH_LONG).show();
			}
			return true;
		} else {
			String apkName = SharedPreferencesUtils.getConfigStr(getApplicationContext(), Contant.cachName, "apkName");
			if (StringUtils.isEmpty(apkName)){//如果本地没有apkName，表示没有成功下载过，直接下载
				if(MyApplication.ISDEBUG){
					Toast.makeText(getApplicationContext(), "如果本地没有apkName，表示没有成功下载过，直接下载", Toast.LENGTH_LONG).show();
				}
				return true;
			}
			
			if (!new File(apkName).exists()) {//如果上次下载的APK不存在，重新下载
				if(MyApplication.ISDEBUG){
					Toast.makeText(getApplicationContext(), "如果上次下载的APK不存在，重新下载", Toast.LENGTH_LONG).show();
				}
				return true;
			}else{
				//本地有安装包了，不需要再下载,进行手动安装
				if(MyApplication.ISDEBUG){
					Toast.makeText(getApplicationContext(), "本地有安装包了，不需要再下载,检测是否是手动更新", Toast.LENGTH_LONG).show();
				}
				if("manualCheck".equals(manualCheck)){//是手动检查版本更新
//					Toast.makeText(context, "普通方式升级", 0).show();
					// 没有root权限，利用意图进行安装
					if(MyApplication.ISDEBUG){
						Toast.makeText(getApplicationContext(), "没有root权限，利用意图进行安装", Toast.LENGTH_LONG).show();
					}
					File file = new File(apkName);
					if (!file.exists())
						return false;
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					getApplicationContext().startActivity(intent);
					manualCheck = "";
				}
			}
		}

		return false;
	}
	
	/**
	 * 检查是否更新apk
	 */
	private  void isUpdateApk()
	{
		MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP+"/appUpdate/getAppVersion");
		params.setCharset("utf-8");
		RequestData requestData = new RequestData();
		Map<String, String>map = new HashMap<String, String>();
		map.put("bizId", "1001");
		requestData.setData(map);
		requestData.setRequestTime(System.currentTimeMillis()+"");
		params.addBodyParameter("data", JSON.toJSONString(requestData));
		if(MyApplication.ISDEBUG){
			Toast.makeText(getApplicationContext(), "post检查是否更新apk", Toast.LENGTH_LONG).show();
		}
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Logger.v(result);
				ResponeData responeData = JSON.parseObject(result, ResponeData.class);
				if(responeData != null && responeData.getCode()==0)
				{
					//查看是否有数据
					if(MyApplication.ISDEBUG){
						Toast.makeText(getApplicationContext(), "正在查看是否有数据", Toast.LENGTH_LONG).show();
					}
					if(responeData.getData() !=null)
					{
						JSONObject jsonObject =  JSON.parseObject(JSON.toJSONString(responeData.getData()));
						jsonObject = jsonObject.getJSONObject("appUpdate");
						if(jsonObject != null)
						{
							//比对本地版本
							if(MyApplication.ISDEBUG){
								Toast.makeText(getApplicationContext(), "正在对比版本", Toast.LENGTH_LONG).show();
							}
							int versionCode =  jsonObject.getIntValue("versionCode");
							if(MyApplication.ISDEBUG){
								Toast.makeText(getApplicationContext(), "json取到版本:"+versionCode, Toast.LENGTH_LONG).show();
							}
							int localVersionCode = SystemUtils.getAppVersionCode(getApplicationContext());
							if(MyApplication.ISDEBUG){
								Toast.makeText(getApplicationContext(), "本地取到版本:"+localVersionCode, Toast.LENGTH_LONG).show();
							}
							if (versionCode> localVersionCode) {//有更新
								if(MyApplication.ISDEBUG){
									Toast.makeText(getApplicationContext(), "本地版本比较低，有更新", Toast.LENGTH_LONG).show();
								}
								if(checkUpdateApk(versionCode)){
									DownloadUtils.getInstance().downloadfileWithNotifycation(Contant.IP+"/"+jsonObject.getString("downPath"), getApplicationContext(),false,null,versionCode);
								}
							}else{
								Toast.makeText(getApplicationContext(), "已经是最新版本,不需要更新!", Toast.LENGTH_LONG).show();
							}
						}
					}
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
		
				Logger.v(ex.getMessage());
			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
				Logger.v("cancelled");
			}

			@Override
			public void onFinished() {

			}
		});
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Toast.makeText(PhoneInfoService.this,
//				"PhoneInfoService Service StartCommand", Toast.LENGTH_SHORT)
//				.show();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {

		Logger.v("PhoneInfoService Service Destroy");
		unregisterReceiver(myReceiver);
		unregisterReceiver(batteryReceiver);
		unregisterReceiver(checkUpdateApkReceiver);
		updateConfigRatetaskTimer.cancel();
		uploadTimer.cancel();
		updateTimer.cancel();
		isStop = true;
//		Intent intent = new Intent(this,PhoneInfoService.class);
//		startService(intent);
		releaseWakeLock();
		
	}

	/* 第一种模式通信：Binder */
	// public class ServiceBinder extends Binder {
	//
	// public void startDownload() throws InterruptedException {
	// /* 模拟下载，休眠2秒 */
	// // Toast.makeText( PhoneInfoService.this, "模拟下载2秒钟,开始下载...",
	// // Toast.LENGTH_SHORT).show();
	// // Thread.sleep(2);
	// // Toast.makeText( PhoneInfoService.this, "下载结束...",
	// // Toast.LENGTH_SHORT).show();
	// Intent i = new Intent(TaskScheduService.Action);
	// i.putExtra("test", "我是phone");
	// sendBroadcast(i);
	//
	// }
	//
	// }
	
	private String manualCheck = "";
	public class CheckUpdateApkReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			manualCheck = arg1.getStringExtra("manualCheck");
			if(MyApplication.ISDEBUG){
				Toast.makeText(getApplicationContext(), "接收到手动检测 版本广播，检测版本", Toast.LENGTH_LONG).show();
			}
			isUpdateApk();
		}
		
	}

	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) { 

			String test = intent.getStringExtra("test");
			Logger.i(test);
			
			String biz = intent.getStringExtra("biz");
			if("stopSercice".equals(biz))
			{
				unregisterReceiver(myReceiver);
				unregisterReceiver(batteryReceiver);
				updateConfigRatetaskTimer.cancel();
				uploadTimer.cancel();
				updateTimer.cancel();
				stopSelf();
			}

		}

	}

	/**
	 * 监听电量广播
	 * 
	 * @author TongLee
	 *
	 */
	private class BatteryReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int current = intent.getExtras().getInt("level");// 获得当前电量
			int total = intent.getExtras().getInt("scale");// 获得总电量
			int percent = current * 100 / total;
			int voltage = intent.getIntExtra("voltage", 0);
			int temp = intent.getIntExtra("temperature", 0);
		
			battery = percent;
			SharedPreferencesUtils.setConfigStr(getApplicationContext(), Contant.cachName, "battery", battery+"%");
			batteryVoltage = voltage;
			batteryTempl= temp;
			
		}
	}

	private class MyPhoneNetWorkStateListener extends PhoneStateListener

	{

		/*
		 * Get the Signal strength from the provider, each tiome there is an
		 * update 从得到的信号强度,每个tiome供应商有更新
		 */

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			super.onSignalStrengthsChanged(signalStrength);
			// final int type = tel.getNetworkType();
			// StringBuffer sb = new StringBuffer();
			// String strength = String.valueOf(signalStrength
			// .getGsmSignalStrength());
			//
			//
			// netWorkStrength = strength;

			super.onSignalStrengthsChanged(signalStrength);
			String imsi = SystemUtils.getImsiNumber(PhoneInfoService.this);
			if (null != imsi) {
				if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
					simOperatorName = "中国移动";
					int asu = signalStrength.getGsmSignalStrength();
					netWorkStrength = asu;

				} else if (imsi.startsWith("46001")) {
					simOperatorName = "中国联通";
					int dbm = signalStrength.getCdmaDbm();
					netWorkStrength = dbm;
				} else if (imsi.startsWith("46003")) {
					simOperatorName = "中国电信";
					int dbm = signalStrength.getEvdoDbm();
					netWorkStrength = dbm;
				}
			}

	
		}

	};/* End of private Class */

	private void saveLog()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String shell = "logcat";
		        try
		        {
		            Process process = Runtime.getRuntime().exec(shell);
		            InputStream inputStream = process.getInputStream();
		            
		            
		            
		            boolean sdCardExist = Environment.getExternalStorageState().equals(
		                    android.os.Environment.MEDIA_MOUNTED);
		            File dir = null;
		            if (sdCardExist)
		            {
		                dir = new File(Environment.getExternalStorageDirectory().toString()
		                        + File.separator + "/ClouldLog/logcatcloud-"+DateUtil.getDateNowString(new Date())+".txt");
		            
		                if (!dir.exists())
		                {
		                	FileHelper.createFile(dir);
//		                    dir.createNewFile();
		                }

		            }
		            byte[] buffer = new byte[1024];
		            int bytesLeft = 5 * 1024 * 1024; // Or whatever
		            try
		            {
		                FileOutputStream fos = new FileOutputStream(dir);
		                try
		                {
		                    while (bytesLeft > 0)
		                    {
		                        int read = inputStream.read(buffer, 0, Math.min(bytesLeft,
		                                buffer.length));
		                        if (read == -1)
		                        {
		                            throw new EOFException("Unexpected end of data");
		                        }
		                        fos.write(buffer, 0, read);
		                        bytesLeft -= read;
		                    }
		                } finally
		                {
		                    fos.close(); // Or use Guava's
		                                 // Closeables.closeQuietly,
		                    // or try-with-resources in Java 7
		                }
		            } finally
		            {
		                inputStream.close();
		            }
//		            String logcat = convertStreamToString(inputStream);
//		            outputFile2SdTest(logcat, "logwyx.txt");
		        } catch (IOException e)
		        {
		            e.printStackTrace();
		        }
				
			}
		}).start();
		

	}
	
	
	private void acquireWakeLock() {
		if (null == mWakeLock) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "myService");
			if (null != mWakeLock) {
				mWakeLock.acquire();
			}
		}
	}

	/**
	 * onDestroy时，释放设备电源锁
	 */
	private void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
}
