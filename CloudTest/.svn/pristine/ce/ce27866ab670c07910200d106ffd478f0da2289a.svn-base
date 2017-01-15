package com.newland.cloudtest.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.JsonReader;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.newland.cloudtest.MyApplication;
import com.newland.cloudtest.R;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.PhoneTask;
import com.newland.cloudtest.bean.QueryBamNormalModelRespContent;
import com.newland.cloudtest.bean.SMSInfo;
import com.newland.cloudtest.bean.ScoketRequest;
import com.newland.cloudtest.bean.TaskResultLog;
import com.newland.cloudtest.bean.Taskresult;
import com.newland.cloudtest.bean.TestresultDetailPhone;
import com.newland.cloudtest.exception.SmsException;
import com.newland.cloudtest.util.ApkController;
import com.newland.cloudtest.util.AppSytemUtils;
import com.newland.cloudtest.util.Contant;
import com.newland.cloudtest.util.DownloadUtils;
import com.newland.cloudtest.util.SharedPreferencesUtils;
import com.newland.cloudtest.util.SmsSytemUtils;
import com.newland.cloudtest.util.StringUtil;
import com.newland.cloudtest.util.SystemUtils;
import com.newland.cloudtest.util.TimerUtils;
import com.orhanobut.logger.Logger;

/**
 * 短信服务 接受解析
 * 
 * @author TongLee
 *
 */
public class SmsService extends Service {
	public MyReceiver myReceiver;
	public static String Action = "com.newland.SmsServiceBroadcast";
	public static String sendMsg = "sendMsg";
	public static String receiverMsg = "receiverMsg";
	public static String startWork = "startWork";// 开始进行短信解析
	public static String startWorkAPP = "startWorkAPP";// 开始进行app解析
	public static String startUnSurveyTask = "startUnSurveyTask";// 开始进行非探测类
	public static String startTraffic = "startTrafficTask";// 开始4G流量提醒探测
	 public boolean isStop = false;
	//public List<SMSInfo> smsInfos = new ArrayList<SMSInfo>();//用于接收最新短信的缓存，处理完则清空
//	public SMSInfo smsInfo;//用于接收最新短信的缓存，处理完则清空
	TimerUtils TaskAll = new TimerUtils(); // 一个任务计时
	private boolean stopThread = false;//线程停止标志
	
	WakeLock mWakeLock;// 电源锁

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Logger.v("SmsServiceService Service Create...");
		
		super.onCreate();
		isStop = false;
		myReceiver = new MyReceiver();
		registerReceiver(myReceiver, new IntentFilter(Action));
		//轮询是否还有多余的 非探测性app没做
		LoopUnSurveyTask();
		acquireWakeLock();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.v("SmsServiceService Service StartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Logger.v("SmsServiceService Service Destroy");
		unregisterReceiver(myReceiver);
		stopThread = true;
		isStop = true;
//		Intent intent = new Intent(this,SmsService.class);
//		startService(intent);
		releaseWakeLock();
	
	}
	
	/**
	 * 服务重启时
	 */
	private void clearTimeOutTask()
	{
		
	}
	

	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String smsBiz = intent.getStringExtra("smsBiz");
//			if (sendMsg.equals(smsBiz)) {// 发送短信
//				String sendCode = intent.getStringExtra("sendCode");//
//				String number = intent.getStringExtra("number");
//
//			
//				Logger.i("接受sendCode" + sendCode);
//				SmsSytemUtils.getInstance().sendSMS(number, sendCode, context);
//				SMSInfo smInfo = new SMSInfo();
//				smInfo.setSmsId(UUID.randomUUID().toString());
//				smInfo.setMobile(number);
//				smInfo.setContent(sendCode);
//				smInfo.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date()));
//				smInfo.setCreateTime(new Date());
//				smInfo.setType(0);
//				try {
//					SystemUtils.getDbManage().save(smInfo);
//					//SystemUtils.getDbManage().close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//			} else if (receiverMsg.equals(smsBiz)) { // 接收信息
//
//				String mobile = intent.getStringExtra("mobile");
//				String content = intent.getStringExtra("content");
//				String time = intent.getStringExtra("time");
//				Logger.v(mobile + content + time);
//				SMSInfo smsInfo = new SMSInfo();
//				smsInfo.setSmsId(UUID.randomUUID().toString());
//				smsInfo.setMobile(mobile);
//				smsInfo.setContent(content);
//				smsInfo.setTime(time);
//				smsInfo.setCreateTime(new Date());
//				smsInfo.setType(1);
//				MyApplication myApplication = (MyApplication) getApplication();
//				myApplication.setSmsInfo(smsInfo);
//
//				try {
//					SystemUtils.getDbManage().save(smsInfo);
//					//SystemUtils.getDbManage().close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
			//短信任务
			 if (startWork.equals(smsBiz))// 开始查表检测未完成的任务
			{
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						clearToken();
						// 查表取出未执行数据
						startSmsWork();
						
					}
				}).start();
			
			}
			//app
			else if (startWorkAPP.equals(smsBiz))// 开始查表检测APP未完成的任务
			{
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 查表取出未执行数据
						clearToken();
						startAppWork();
					}
				}).start();
			}
			else if (startUnSurveyTask.equals(smsBiz))// 开始查表检测非探测未完成的任务
			{
//				new Thread(new Runnable() {		
//					@Override
//					public void run() {
//						//startUnSurveyTask();	
//					}
//				}).start();
			}
			else if (startTraffic.equals(smsBiz))// 开始查表检测4G流量提醒未完成的任务
			{
				new Thread(new Runnable() {
					@Override
					public void run() {
						TrafficTaskProcess p = new TrafficTaskProcess();
						p.setContext(SmsService.this);
						p.executeTrafficTask();
					}
				}).start();
			}
		}
	}
	
	private void clearToken()
	{
		SharedPreferencesUtils.setConfigStr(SmsService.this, Contant.cachName, "timertoken", null);
		SharedPreferencesUtils.setConfigStr(SmsService.this, Contant.cachName, "BeginTimer", null);
		SharedPreferencesUtils.setConfigStr(SmsService.this, Contant.cachName, "EndTimer", null);
		SharedPreferencesUtils.setConfigLong(SmsService.this, Contant.cachName, "Wastetime", 0L);
		SharedPreferencesUtils.setConfigStr(SmsService.this, Contant.cachName, "Taskstarttime", null);
		SharedPreferencesUtils.setConfigStr(SmsService.this, Contant.cachName, "timertoken", null);

	}
	
	/**
	 * 轮询是否还有多余的 非探测性app没做
	 */
	private void LoopUnSurveyTask()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!isStop) {
					
					try {
						Thread.sleep(1000*60);
						//查看是否有在执行的任务
						PhoneTask phoneTask = SystemUtils.getDbManage()
								.selector(PhoneTask.class)
								.where("status", "=",1)
								.findFirst();
						if(phoneTask == null)
						{
							Message msg = new Message();
							msg.what = 0;
							handler.sendMessage(msg);
						}
						
				
					
					} catch (DbException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
			}
		}).start();
		
		
	}



	/**
	 * 非探测类
	 */
	private void startUnSurveyTask()
	{
		try {
			PhoneTask phoneTask = SystemUtils.getDbManage()
			.selector(PhoneTask.class)
			.where("status", "=", 0)
			.findFirst();
			if(phoneTask != null)
			{
				if (200 == phoneTask.getCommand()) { //下载app
					phoneTask.setBeginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date()));
					phoneTask.setStatus(1);
					SystemUtils.getDbManage().saveOrUpdate(phoneTask);
					DownloadUtils.getInstance().downloadfileWithNotifycation(phoneTask.getTaskParam1(), this,true,phoneTask);
				}
				else if(201 == phoneTask.getCommand()) //卸载
				{
					phoneTask.setBeginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date()));
					phoneTask.setStatus(1);
					SystemUtils.getDbManage().saveOrUpdate(phoneTask);
					
					if("com.newland.cloudtest".equals(phoneTask.getTaskParam1()))
					{
						//自身不卸载
						phoneTask.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date())); 
						phoneTask.setStatus(6);//执行失败
						phoneTask.setTaskResult(2);//失败
						SystemUtils.getDbManage().saveOrUpdate(phoneTask);
						return;
					}
	
					boolean isuninstall =  ApkController.uninstall(StringUtil.noNull(phoneTask.getTaskParam1()), this);
					if(isuninstall)
					{
						phoneTask.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date())); 
						phoneTask.setStatus(2);//已执行
						phoneTask.setTaskResult(1);//成功
						SystemUtils.getDbManage().saveOrUpdate(phoneTask);
					}
					else {
						phoneTask.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date())); 
						phoneTask.setStatus(6);//执行失败
						phoneTask.setTaskResult(2);//失败
						SystemUtils.getDbManage().saveOrUpdate(phoneTask);
					}
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * app测试工作  
	 */
	
	private void startAppWork() {
		
		QueryBamNormalModelRespContent qbmr = null;
		List<BamNormalModelDetailPara>  list = new ArrayList<BamNormalModelDetailPara>();
		TimerUtils totalTime = new TimerUtils();//总跑表标示

		try {
			
			qbmr = SystemUtils.getDbManage()
					.selector(QueryBamNormalModelRespContent.class)
					.where("statue", "=", 0).and("channel", "=", "32")
					.findFirst();
			//SystemUtils.getDbManage().close();
			if (qbmr != null) {// 有数据
				//清除历史这个业务脚本数据
				clearHisLog(qbmr.getQbmId());
			
				QueryBamNormalModelRespContent bean =JSON.parseObject(qbmr.getResponseJsonDate(), QueryBamNormalModelRespContent.class);
				 list= bean.getBamNormalModel().getBamNormalModelDetailParas();
				//计时时间
				totalTime.startTime();
				ScoketRequest scoketInputRequest = new ScoketRequest();
				scoketInputRequest.setMethod("34");//屏幕亮起来
				String scoketInputResult = AppSytemUtils.getInstance().connectScoket(JSON.toJSONString(scoketInputRequest));
					
			    for (int i = 0; i < list.size(); i++) {//解析脚本
			    	BamNormalModelDetailPara bamNormalModelDetailPara = list.get(i);
			    	bamNormalModelDetailPara.setQbmId(bean.getQbmId());
					AppSytemUtils.getInstance().decodeAppScript(bean.getBamNormalModel(), bamNormalModelDetailPara, SmsService.this,bean.getGetSmskeyconfigs(),bean,i,list.size());
			
				}
//				for (BamNormalModelDetailPara bamNormalModelDetailPara : list) { //解析脚本
//				}
				totalTime.stopTime();
				upLoadResult("成功",null,qbmr,list,totalTime);
			
			}
			else {
				//throw new SmsException("下发任务数据格式有误");
			}
			
		} 
	
		catch (DbException e) {
			totalTime.stopTime();
			upLoadResult("失败","本地数据库操作失败"+e.getMessage(),qbmr,list,totalTime);
			e.printStackTrace();
			
		}
		catch (SmsException e)
		{
			totalTime.stopTime();
			upLoadResult("失败",e.getMessage(),qbmr,list,totalTime);
			e.printStackTrace();
			
		}
	
		catch (Exception e) {
			totalTime.stopTime();
			upLoadResult("失败",e.getMessage(),qbmr,list,totalTime);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 清空遗留数据
	 */
	private void clearHisLog(String qbmId)
	{
		BamNormalModelDetailPara bamNormalModelDetailPara = new BamNormalModelDetailPara();
		bamNormalModelDetailPara.setQbmId(qbmId);
		try {
			SystemUtils.getDbManage().delete(bamNormalModelDetailPara);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 开始查表取出未执行数据
	 */
	private void startSmsWork() {
		QueryBamNormalModelRespContent qbmr = null;
		List<BamNormalModelDetailPara>  list = new ArrayList<BamNormalModelDetailPara>();
		TimerUtils totalTime = new TimerUtils();//总跑表标示
		try {
			//查询短厅业务
			qbmr = SystemUtils.getDbManage()
					.selector(QueryBamNormalModelRespContent.class)
					.where("statue", "=", 0).and("channel", "=", "12")
					.findFirst();
	
			
			//SystemUtils.getDbManage().close();
			if (qbmr != null) {// 有数据
				//清除历史这个业务脚本数据
				clearHisLog(qbmr.getQbmId());
				QueryBamNormalModelRespContent bean =JSON.parseObject(qbmr.getResponseJsonDate(), QueryBamNormalModelRespContent.class);
				

				if(bean != null)
				{
					//清除旧数据
					SystemUtils.getDbManage().delete(BamNormalModelDetailPara.class, WhereBuilder.b("qbmId", "=", bean.getQbmId()));
					list= bean.getBamNormalModel().getBamNormalModelDetailParas();
					
					//计时时间
					 totalTime.startTime();
					for (BamNormalModelDetailPara bamNormalModelDetailPara : list) { //解析脚本
						
						bamNormalModelDetailPara.setQbmId(bean.getQbmId());
						SmsSytemUtils.decodeSmsScript(bean.getBamNormalModel(),bamNormalModelDetailPara, this,totalTime);
						
					}
					totalTime.stopTime();
					upLoadResult("成功",null,qbmr,list,totalTime);
				}
		
			
			}
			else {
				//throw new SmsException("下发任务数据格式有误");
			}
			
		} 
	
		catch (DbException e) {
			totalTime.stopTime();
			upLoadResult("失败","本地数据库操作失败"+e.getMessage(),qbmr,list,totalTime);
			e.printStackTrace();
			
		}
		catch (SmsException e)
		{
			totalTime.stopTime();
			upLoadResult("失败",e.getMessage(),qbmr,list,totalTime);
			e.printStackTrace();
			
		}
	
		catch (Exception e) {
			totalTime.stopTime();
			upLoadResult("失败",e.getMessage(),qbmr,list,totalTime);
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存短厅结果到本地
	 */
	private void upLoadResult(String result,String resultContent,QueryBamNormalModelRespContent qbmr,List<BamNormalModelDetailPara>  list,TimerUtils totalTime)
	{
//		if(stopThread != true)
//		{
			Taskresult taskresult = new Taskresult();
			if("成功".equals(result))
			{
				taskresult.setResult(result);
				taskresult.setResultcontent("task_no_erro");
			}
			else {
				taskresult.setResult(result);
				taskresult.setResultcontent(resultContent);
				String screenshotPicPath = SharedPreferencesUtils.getConfigStr(getApplicationContext(), Contant.cachName,"screenshotPicPath");
				//Toast.makeText(getApplicationContext(), screenshotPicPath, Toast.LENGTH_LONG).show();
				if (StringUtil.isNotEmpty(screenshotPicPath)){
					taskresult.setPiclink(screenshotPicPath);
				}
			}
			SharedPreferencesUtils.setConfigStr(getApplicationContext(), Contant.cachName, "Taskstarttime", totalTime.getStartTimeStr());
			SharedPreferencesUtils.setConfigLong(getApplicationContext(), Contant.cachName, "TaskWastetime", (totalTime.getCurrentTime()));
			SharedPreferencesUtils.setConfigStr(getApplicationContext(), Contant.cachName, "Taskendtime", totalTime.getEndTimeStr());


			
			if(qbmr != null)
			{
				TaskResultLog taskResultLog = new TaskResultLog();
				taskResultLog.setQbmId(qbmr.getQbmId());
				taskResultLog.setTaskResultJson(JSON.toJSONString(taskresult));
				taskResultLog.setResult(result);
				taskResultLog.setResultContent(resultContent);
				taskResultLog.setIsUpload(0);
				taskResultLog.setCreateTime(new Date());
				taskResultLog.setChannel(qbmr.getChannel());
				Date d = new Date();
				taskResultLog.setRecoreTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(d));
				try {
					SystemUtils.getDbManage().saveOrUpdate(taskResultLog);
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		
//		}
		
		
		
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0: //执行是否有非探测性任务
				startUnSurveyTask();
				break;
			case 1:
				break;
				
				
			default:
				break;
			}
		}

	};
	
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
