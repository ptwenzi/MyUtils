package com.newland.cloudtest.service;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.BamNormalModelPara;
import com.newland.cloudtest.bean.MyXutilsRequestParams;
import com.newland.cloudtest.bean.PhoneTask;
import com.newland.cloudtest.bean.QueryBamNormalModelRespContent;
import com.newland.cloudtest.bean.RequestData;
import com.newland.cloudtest.bean.ResponeData;
import com.newland.cloudtest.bean.TaskResultContent;
import com.newland.cloudtest.bean.TaskResultLog;
import com.newland.cloudtest.bean.Taskresult;
import com.newland.cloudtest.bean.Tbusiness4g;
import com.newland.cloudtest.bean.TestresultDetailPhone;
import com.newland.cloudtest.util.Contant;
import com.newland.cloudtest.util.DateUtil;
import com.newland.cloudtest.util.SharedPreferencesUtils;
import com.newland.cloudtest.util.StringUtil;
import com.newland.cloudtest.util.SystemUtils;
import com.orhanobut.logger.Logger;

/**
 * 任务调度服务
 * @author TongLee
 *
 */
public class TaskScheduService extends Service{

//	 public ServiceBinder mBinder = new ServiceBinder(); /* 数据通信的桥梁 */
	 public  MyReceiver myReceiver;
	 public static String Action = "com.newland.TaskScheduBroadcast";
	 public boolean isStop = false;
	 public boolean isPause = false;//是否暂停中
		WakeLock mWakeLock;// 电源锁
		@Override
		public IBinder onBind(Intent arg0) {
			// TODO Auto-generated method stub
			  return null;
		}

	    @Override
	    public void onCreate() {    
	       // Toast.makeText( TaskScheduService.this, "TaskScheduService Service Create...", Toast.LENGTH_SHORT).show();
	        super.onCreate();
	        isStop = false;
	        myReceiver=new MyReceiver();
	        IntentFilter filter=new IntentFilter();
	        filter.addAction(Action);
	        registerReceiver(myReceiver, filter);
	        
	        monitorNewTask();
	        //获取非探测性任务
	        monitorUnSurveyTask();
	       //testAppDb();
	        //testDb();
//			Intent intent = new Intent();  
//			intent.setAction(SmsService.Action);  
//			intent.putExtra("smsBiz", SmsService.startWorkAPP);
//			sendBroadcast(intent);
			monitorAppTask();
			//monitorScoket();
			acquireWakeLock();
	    }
	    
	    
	    
	    /**
	     * lsc_判断是否有未完成的任务，有的话继续执行
	     * 获取新任务
	     */
	    private void monitorNewTask()
	    {
	    		new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (!isStop ) {
					
						try {
							if(!isPause)//没有暂停就继续循环执行
							{
								Logger.v("获取任务线程，检查是否还有任务 ："+DateUtil.getDateNowStringWithmi(new Date()));
								QueryBamNormalModelRespContent qbmr = SystemUtils.getDbManage()
										.selector(QueryBamNormalModelRespContent.class)
										.where("statue", "=", 0).findFirst();
								//SystemUtils.getDbManage().close();
								if(qbmr == null)//说明手机里没有任务了
								{
									getData();
//							
								}
							}
							
				
							Thread.sleep(1000*20);//任务结果进行上传的时候，睡眠时间
							
						} catch (InterruptedException | IOException e) {
						
							e.printStackTrace();
						}
					}
				
					
					
				}
			}).start();


			
			
	    }
	    
	    
	    
	    /**
	     * 获取非探测任务
	     */
	    private void monitorUnSurveyTask()
	    {
	    	new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (!isStop ) {
					
						try {
							MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP+"/phoneTask/getPhoneTask");
							params.setCharset("utf-8");
							RequestData requestData = new RequestData();
							Map<String, String>map = new HashMap<String, String>();
							map.put("imei",SystemUtils.getIMEI(getApplicationContext()));
							requestData.setData(map);
							params.addBodyParameter("data", JSON.toJSONString(requestData));
							x.http().post(params, new Callback.CommonCallback<String>() {
								@Override
								public void onSuccess(String result) {
									Logger.v(result);
									ResponeData responeData = JSON.parseObject(result, ResponeData.class);
									if(responeData != null && responeData.getCode()==0)
									{
										//查看是否有数据
										if(responeData.getData() !=null)
										{
											JSONArray jsonArray =  JSON.parseObject(JSON.toJSONString(responeData.getData())).getJSONArray("phoneTasks");
											 List<PhoneTask> phoneTasks = JSON.parseArray(JSON.toJSONString(jsonArray), PhoneTask.class);
											if(phoneTasks != null && phoneTasks.size()>0)
											{
												try {
													for (PhoneTask phoneTask : phoneTasks) {
														phoneTask.setCreateTime(new Date());
													}
													SystemUtils.getDbManage().save(phoneTasks);
												} catch (DbException e) {
													e.printStackTrace();
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
				
							Thread.sleep(1000*60*5);
							
						} catch (InterruptedException  e) {
						
							e.printStackTrace();
						}
					}
				
					
					
				}
			}).start();


			
			
	    }
	    
	    
	    
	    
	    /**
	     * 获取数据  java 通过 SSL/TLS 发送 Post 请求
	     */
	    private void getData()
	    {
//	    	自定义一个RequestParams 通过TLS进行参数的封装
	    	MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP+"/taskexecute/getTask");
	    	params.setCharset("utf-8");
			RequestData requestData = new RequestData();
			Map<String, String>map = new HashMap<String, String>();
			map.put("imsi",SystemUtils.getImsiNumber(getApplicationContext()));
			map.put("imei",SystemUtils.getIMEI(getApplicationContext()));
			requestData.setData(map);
			params.addBodyParameter("data", JSON.toJSONString(requestData));
			x.http().post(params, new Callback.CommonCallback<String>() {
				@Override
				public void onSuccess(String result) {
					Logger.v("收到消息："+DateUtil.getDateNowStringWithmi(new Date()));
					Logger.json(result);
					ResponeData responeData = JSON.parseObject(result, ResponeData.class);
					if(responeData != null && responeData.getCode()==0)
					{
						//查看是否有数据
						if(responeData.getData() !=null)
						{
							//final QueryBamNormalModelRespContent qbm = (QueryBamNormalModelRespContent)responeData.getData();
							final QueryBamNormalModelRespContent qbm = JSON.parseObject(JSON.toJSONString(responeData.getData()), QueryBamNormalModelRespContent.class);
							if(qbm.getBamNormalModel() != null)
							{
								
								
								
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										try {//如果有暂停时间需要暂停完再接着做
											isPause = true;
											Thread.sleep(qbm.getPauseTime());
											isPause = false;
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
										
										//排序脚本明细 短厅
										List<BamNormalModelDetailPara> bamNormalModelDetailParas = qbm.getBamNormalModel().getBamNormalModelDetailParas();
//										if(bamNormalModelDetailParas!= null)
//										{
//											List<BamNormalModelDetailPara> newArray = new ArrayList<BamNormalModelDetailPara>();
//											for (BamNormalModelDetailPara bamNormalModelDetailPara : bamNormalModelDetailParas) {
//												bamNormalModelDetailPara.getStepNo();
//											}
//										      Collections.sort(bamNormalModelDetailParas,new Comparator<BamNormalModelDetailPara>(){
//										            public int compare(BamNormalModelDetailPara arg0, BamNormalModelDetailPara arg1) {
//										                return arg0.getStepNo().compareTo(arg1.getStepNo());
//										            }
//										        });
//										      qbm.getBamNormalModel().setBamNormalModelDetailParas(bamNormalModelDetailParas);
//										}
										
										qbm.setQbmId(UUID.randomUUID().toString());
										qbm.setCreateTime(new Date());
										qbm.setResponseJsonDate(JSON.toJSONString(qbm));
										try {
											SystemUtils.getDbManage().save(qbm);
											if(Contant.SMS_CHANNEL.equals(qbm.getChannel())) //短信
											{
												Intent intent = new Intent();  
												intent.setAction(SmsService.Action);  
												intent.putExtra("smsBiz", SmsService.startWork);
												sendBroadcast(intent);
											}
											else if(Contant.APP_CHANNEL.equals(qbm.getChannel()))
											{
												Intent intent = new Intent();  
												intent.setAction(SmsService.Action);  
												intent.putExtra("smsBiz", SmsService.startWorkAPP);
												sendBroadcast(intent);
											}
											else if(Contant.Traffic4G_CHANNEL.equals(qbm.getChannel()))//4G流量探测
											{
												Intent intent = new Intent();  
												intent.setAction(SmsService.Action);  
												intent.putExtra("smsBiz", SmsService.startTraffic);
												sendBroadcast(intent);
											}
										} catch (DbException e) {
											e.printStackTrace();
										}
										
									}
								}).start();
						
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
	    
	    
	    /**
	     * 监控Soket服务器是否正常
	     */
	    private void monitorScoket()
	    {
	    	new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (!isStop) {
					
						Socket sender;
						try {
							Thread.sleep(1000*10);
							sender = new Socket("localhost", 8908);
							
							sender.close();
						} catch (IOException | InterruptedException e) {
							//如果没连接则重启服务器
							boolean  isStarted= SystemUtils.execShellCmd("uiautomator runtest appdriver.jar -c com.newland.appdriver.AppDriverEntry");
							Logger.v("monitorScoket重启服务器"+isStarted);
							;
							e.printStackTrace();
						}
					}
				
					
					
				}
			}).start();
	    }
	    
	    

	    
	    /**
	     * 每隔一段时间查看一次，任务是否已经完成
	     * 
	     * 监控App任务完成情况
	     */
	    private void monitorAppTask()
	    {
	    	new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					while(!isStop)
					{
						Logger.v("监控任务线程，检查是否有做完任务并上传 ："+DateUtil.getDateNowStringWithmi(new Date()));
						try {
							Logger.v("遍历存在任务====");
							//检查是否有超时的任务  短厅
							QueryBamNormalModelRespContent qbmr = SystemUtils.getDbManage()
									.selector(QueryBamNormalModelRespContent.class)
									.where("statue", "=", 0).and("channel", "=", "12")
									.findFirst();
							//SystemUtils.getDbManage().close();
						
							if(qbmr != null)
							{
								String qbmId = qbmr.getQbmId();
								Date createTime = qbmr.getCreateTime();
								String responseJsonDate = qbmr.getResponseJsonDate();
								qbmr = JSON.parseObject(responseJsonDate, QueryBamNormalModelRespContent.class);
								qbmr.setQbmId(qbmId);
								qbmr.setResponseJsonDate(responseJsonDate);
								qbmr.setCreateTime(createTime);
								saveLog(qbmr,1);
							}
							
							
							
							//检查是否有超时的任务app
							QueryBamNormalModelRespContent qbmrApp = SystemUtils.getDbManage()
									.selector(QueryBamNormalModelRespContent.class)
									.where("statue", "=", 0).and("channel", "=", "32")
									.findFirst();
							//SystemUtils.getDbManage().close();
							if(qbmrApp != null)
							{
								String qbmId = qbmrApp.getQbmId();
								String responseJsonDate = qbmrApp.getResponseJsonDate();
								Date createTime = qbmrApp.getCreateTime();
								qbmrApp = JSON.parseObject(qbmrApp.getResponseJsonDate(), QueryBamNormalModelRespContent.class);
								qbmrApp.setResponseJsonDate(responseJsonDate);
								qbmrApp.setQbmId(qbmId);
								qbmrApp.setCreateTime(createTime);
								saveLog(qbmrApp,3);
							}
							
							
							//检查是否有超时的4G流量
							QueryBamNormalModelRespContent flow4G = SystemUtils.getDbManage()
									.selector(QueryBamNormalModelRespContent.class)
									.where("statue", "=", 0).and("channel", "=", "9")
									.findFirst();
							//SystemUtils.getDbManage().close();
							if(flow4G != null)
							{
								String qbmId = flow4G.getQbmId();
								Date createTime = flow4G.getCreateTime();
								String responseJsonDate = flow4G.getResponseJsonDate();
								flow4G = JSON.parseObject(flow4G.getResponseJsonDate(), QueryBamNormalModelRespContent.class);
								flow4G.setResponseJsonDate(responseJsonDate);
								flow4G.setQbmId(qbmId);
								flow4G.setCreateTime(createTime);
								saveLog(flow4G,2);
							}
							
							
						//检查是非探测性任务超时情况
							PhoneTask phoneTask = SystemUtils.getDbManage()
							.selector(PhoneTask.class)
							.where("status", "=", 0).or("status", "=", 1)
							.findFirst();
							if(phoneTask != null)
							{
								//检查是否超时
								long epoch = System.currentTimeMillis();
								Long currentTime = epoch-phoneTask.getCreateTime().getTime();
								if(currentTime>Contant.unPhonetaskTimeOut)//超时
								{
									phoneTask.setStatus(7);
									phoneTask.setTaskResult(2);
									try {
										SystemUtils.getDbManage().saveOrUpdate(phoneTask);
									} catch (DbException e) {
										e.printStackTrace();
									}	
									List<PhoneTask>phoneTasks = new ArrayList<PhoneTask>();
									phoneTasks.add(phoneTask);
									uploadUnPhoneTask(phoneTasks);
								}
							}
							else {
								//检查是否有完成的任务
								List<PhoneTask> list = SystemUtils.getDbManage()
										.selector(PhoneTask.class)
										.where("status", "=", 2).or("status", "=", 6)
										.findAll();
								if(list != null)
								{
									uploadUnPhoneTask(list);
								}
							}
							
							Thread.sleep(Contant.taskResultUploadTimeOut);
						} catch (IOException e) {
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
	     * 非探测性任务上传
	     * @param phoneTask
	     */
	    private void uploadUnPhoneTask(final List<PhoneTask>phoneTasks)
	    {
	    	
	    	if(phoneTasks.size()==0)
	    	{
	    		return;
	    	}
	    	for (PhoneTask phoneTask : phoneTasks) {
	    		phoneTask.setCreateTime(null);
			}
	    	MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP+"/phoneTask/upPhoneTaskResult");
	    	params.setCharset("utf-8");
			RequestData requestData = new RequestData();
			Map<String, Object>map = new HashMap<String, Object>();
			map.put("imsi",SystemUtils.getImsiNumber(getApplicationContext()));
			map.put("phoneTaskResults", phoneTasks);
			requestData.setData(map);
			params.addBodyParameter("data", JSON.toJSONString(requestData));
			x.http().post(params, new Callback.CommonCallback<String>() {
				@Override
				public void onSuccess(String result) {
					Logger.v(result);
					ResponeData responeData = JSON.parseObject(result, ResponeData.class);
					if(responeData != null && responeData.getCode()==0)
					{
						for (PhoneTask phoneTask : phoneTasks) {
							phoneTask.setStatus(5);
							try {
								SystemUtils.getDbManage().saveOrUpdate(phoneTask);
							} catch (DbException e) {
								e.printStackTrace();
							}	
						}
						
					}
					else {
						for (PhoneTask phoneTask : phoneTasks) {
							phoneTask.setStatus(4);
							try {
								SystemUtils.getDbManage().saveOrUpdate(phoneTask);
							} catch (DbException e) {
								e.printStackTrace();
							}	
						}
					
					}
				}

				@Override
				public void onError(Throwable ex, boolean isOnCallback) {
			
					Logger.v(ex.getMessage());
					for (PhoneTask phoneTask : phoneTasks) {
						phoneTask.setStatus(4);
						try {
							SystemUtils.getDbManage().saveOrUpdate(phoneTask);
						} catch (DbException e) {
							e.printStackTrace();
						}	
					}
					
		
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
	     * 上传日志
	     * taskType  1 sms 2 -4g  3-app  
	     */
	    private void smsUploadLog(int statue, QueryBamNormalModelRespContent qbmr,TaskResultLog taskResultLog,int taskType)
	    {
	    	
			List<Tbusiness4g> tbusiness4gs = null;
			try {
				tbusiness4gs = SystemUtils.getDbManage()
						.selector(Tbusiness4g.class)
						.where("testsrl", "=", qbmr.getTestsrl())
						.findAll();
				
			} catch (DbException e) {
				e.printStackTrace();
			}
			
			
			
	    	if(statue == 2)//超时处理
	    	{
	    		Taskresult taskresult = new Taskresult();
	    		taskresult.setResultDetail("手机端处理超时");
	    		
	    		try {
	    			//设置日志超时
//					TaskResultLog taskResultLog2 =SystemUtils.getDbManage()
//							.selector(TaskResultLog.class).where("qbmId","=",qbmr.getQbmId()).findFirst();
					TaskResultLog taskResultLog2 = new TaskResultLog();
					taskResultLog2.setQbmId(qbmr.getQbmId());
					taskResultLog2.setResult("失败");
					taskResultLog2.setResultContent("手机端处理超时");
					taskResultLog2.setCreateTime(new Date());
					SystemUtils.getDbManage().saveOrUpdate(taskResultLog2);
				} catch (DbException e1) {
					e1.printStackTrace();
				}
	    		
	    		
				taskresult.setResult("1");
				taskresult.setResultcontent("任务超时");
				taskresult.setStrategyid(qbmr.getStrategyId());
				taskresult.setGroupid(qbmr.getGroupId());
				taskresult.setTaskid(Long.parseLong(qbmr.getBamNormalModel().getObjectId()));
				taskresult.setTasksetid(qbmr.getTasksetId());
				taskresult.setTasksetinstancename(qbmr.getTasksetInstanceName());
				String timertoken = SharedPreferencesUtils.getConfigStr(this, Contant.cachName, "timertoken");
				String BeginTimer = SharedPreferencesUtils.getConfigStr(this, Contant.cachName, "BeginTimer");
				String EndTimer = SharedPreferencesUtils.getConfigStr(this, Contant.cachName, "EndTimer");
				Long Wastetime = SharedPreferencesUtils.getConfigLong(this, Contant.cachName, "Wastetime",null);
				taskresult.setTimertoken(StringUtil.convertStrToNull(timertoken));
				taskresult.setTimerstarttime(StringUtil.convertStrToNull(BeginTimer));
				taskresult.setTimerendtime(StringUtil.convertStrToNull(EndTimer));
				taskresult.setWastetime(Wastetime);
				
				taskresult.setTaskstarttime(SharedPreferencesUtils.getConfigStr(getApplicationContext(), Contant.cachName, "Taskstarttime"));
				//Long WastetimeTotal = SharedPreferencesUtils.getConfigLong(getApplicationContext(), Contant.cachName, "TaskWastetime",0L);
				
		
				taskresult.setTaskendtime(SharedPreferencesUtils.getConfigStr(getApplicationContext(), Contant.cachName, "Taskendtime"));
				
				taskresult.setChannel(qbmr.getChannel());
				taskresult.setTestsrl(qbmr.getTestsrl());
				taskresult.setStrategyRunType(2);
				List<TestresultDetailPhone> TestresultDetailPhoneList = new ArrayList<TestresultDetailPhone>();
				
		
				
				//查询脚本明细
				try {
					List<BamNormalModelDetailPara> list= SystemUtils.getDbManage()
							.selector(BamNormalModelDetailPara.class).where("qbmId", "=", qbmr.getQbmId()).findAll();
					StringBuffer sb = new StringBuffer();
					//SystemUtils.getDbManage().close();
					if(list != null)
					{
						for (BamNormalModelDetailPara bamNormalModelDetailPara : list) {
							if(StringUtil.isNotEmpty(bamNormalModelDetailPara.getReturncontent()))
							{
								sb.append("\r\n"+bamNormalModelDetailPara.getReturncontent());
							}
							if(StringUtil.isNotEmpty(bamNormalModelDetailPara.getErrMsg()))
							{
								taskresult.setResultDetail(bamNormalModelDetailPara.getErrMsg());
							}
							
							TestresultDetailPhone testresultDetailPhone = new TestresultDetailPhone();
							testresultDetailPhone.setTestsrl(qbmr.getTestsrl());
							testresultDetailPhone.setOrderid(bamNormalModelDetailPara.getStepNo());
							testresultDetailPhone.setMethod(bamNormalModelDetailPara.getMethodType());
							testresultDetailPhone.setSmsverify(StringUtil.convertStr2Long(bamNormalModelDetailPara.getSmsVerify()));
							testresultDetailPhone.setVerify(StringUtil.convertStr2Long(bamNormalModelDetailPara.getVerify()));
							testresultDetailPhone.setVerifyimg(bamNormalModelDetailPara.getVerifyId());
							if(taskType == 1)
							{
								testresultDetailPhone.setTarget(bamNormalModelDetailPara.getReceivePhoneNo());
								testresultDetailPhone.setSuccstr1(bamNormalModelDetailPara.getKeywordConfig());
								testresultDetailPhone.setPostparam(bamNormalModelDetailPara.getContent());
								testresultDetailPhone.setMethod(bamNormalModelDetailPara.getOperateType());
							}
							else if(taskType == 3){
								testresultDetailPhone.setTarget(bamNormalModelDetailPara.getTarget());
								testresultDetailPhone.setSuccstr1(bamNormalModelDetailPara.getSuccstr1());
								testresultDetailPhone.setSuccstr2(bamNormalModelDetailPara.getSuccstr2());
								testresultDetailPhone.setSuccstr3(bamNormalModelDetailPara.getSuccstr3());
								testresultDetailPhone.setPostparam(bamNormalModelDetailPara.getPostParam());
							}
							testresultDetailPhone.setErrmsg(bamNormalModelDetailPara.getErrMsg());
							testresultDetailPhone.setErrstr1(bamNormalModelDetailPara.getErrstr1());
							testresultDetailPhone.setErrstr2(bamNormalModelDetailPara.getErrstr2());
							testresultDetailPhone.setFailretry(0L);
							testresultDetailPhone.setRetrycount(0L);
							testresultDetailPhone.setTokentype(0);
							testresultDetailPhone.setTimertoken(bamNormalModelDetailPara.getTimerToken());
							//testresultDetailPhone.setDetailid();
							testresultDetailPhone.setTimertoken(bamNormalModelDetailPara.getTimerToken());
							testresultDetailPhone.setResult(1L);
							
							testresultDetailPhone.setReturncontent(bamNormalModelDetailPara.getReturncontent());
							testresultDetailPhone.setDescribe(bamNormalModelDetailPara.getDescribe());
							testresultDetailPhone.setBegintime(bamNormalModelDetailPara.getBegintime());
							testresultDetailPhone.setEndtime(bamNormalModelDetailPara.getEndtime());
							testresultDetailPhone.setWastetime(bamNormalModelDetailPara.getWastetime());
							
							testresultDetailPhone.setTestorder(bamNormalModelDetailPara.getStepNo());
							testresultDetailPhone.setShotpic(bamNormalModelDetailPara.getShotpic());
							TestresultDetailPhoneList.add(testresultDetailPhone);
						}
						taskresult.setResultcontent(sb.toString());
					}
					
					
					//4g流量明细特别处理
					if(taskType ==2)
					{
						TestresultDetailPhoneList = flowTestresultDetailPhone(qbmr.getTestsrl());
					}
					smsUploadData(taskresult,TestresultDetailPhoneList,qbmr,tbusiness4gs);
					
				} catch (DbException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
	    	}
	    	else if(statue == 1) //任务做完 
	    	{
	    		String result = taskResultLog.getResult();
	    		String resultContent = taskResultLog.getResultContent();
	    		try {
					taskResultLog =SystemUtils.getDbManage()
							.selector(TaskResultLog.class).where("qbmId","=",qbmr.getQbmId()).findFirst();
				} catch (DbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    		Taskresult taskresult = new Taskresult();
				if("成功".equals(result))
				{
					taskresult.setResult("0");
					StringBuffer sb = new StringBuffer();
					taskresult.setResultcontent("task_no_erro");
				}
				else {
					taskresult.setResult("1");
					taskresult.setResultcontent(resultContent);
					taskresult.setResultDetail(taskResultLog.getResultDetail());
					try{
						if (null != taskResultLog && null != taskResultLog.getTaskResultJson()){
							Taskresult tmp = JSON.parseObject(taskResultLog.getTaskResultJson(), Taskresult.class);
							taskresult.setPiclink(tmp.getPiclink());	
						}
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				taskresult.setStrategyid(qbmr.getStrategyId());
				taskresult.setGroupid(qbmr.getGroupId());
				taskresult.setTaskid(Long.parseLong(qbmr.getBamNormalModel().getObjectId()));
				taskresult.setTasksetid(qbmr.getTasksetId());
				taskresult.setTasksetinstancename(qbmr.getTasksetInstanceName());
				
				String timertoken = SharedPreferencesUtils.getConfigStr(this, Contant.cachName, "timertoken");
				String BeginTimer = SharedPreferencesUtils.getConfigStr(this, Contant.cachName, "BeginTimer");
				String EndTimer = SharedPreferencesUtils.getConfigStr(this, Contant.cachName, "EndTimer");
				Long Wastetime = SharedPreferencesUtils.getConfigLong(this, Contant.cachName, "Wastetime",null);
				taskresult.setTimertoken(StringUtil.convertStrToNull(timertoken));
				taskresult.setTimerstarttime(StringUtil.convertStrToNull(BeginTimer));
				taskresult.setTimerendtime(StringUtil.convertStrToNull(EndTimer));
				taskresult.setWastetime(Wastetime);
			
				taskresult.setTaskstarttime(SharedPreferencesUtils.getConfigStr(getApplicationContext(), Contant.cachName, "Taskstarttime"));
		
				taskresult.setTaskendtime(SharedPreferencesUtils.getConfigStr(getApplicationContext(), Contant.cachName, "Taskendtime"));
				taskresult.setChannel(qbmr.getChannel());
				taskresult.setTestsrl(qbmr.getTestsrl());
				taskresult.setStrategyRunType(2);
				List<TestresultDetailPhone> TestresultDetailPhoneList = new ArrayList<TestresultDetailPhone>();
				
				List<BamNormalModelDetailPara> list;
				try {
					list = SystemUtils.getDbManage()
							.selector(BamNormalModelDetailPara.class).where("qbmId", "=", qbmr.getQbmId()).findAll();
					//SystemUtils.getDbManage().close();
					if(list != null)
					{
						StringBuffer sb = new StringBuffer();
						//脚本明细
						for (BamNormalModelDetailPara bamNormalModelDetailPara : list) {
							if(StringUtil.isNotEmpty(bamNormalModelDetailPara.getReturncontent()))
							{
								sb.append("\r\n"+bamNormalModelDetailPara.getReturncontent());
							}
							
							TestresultDetailPhone testresultDetailPhone = new TestresultDetailPhone();
							testresultDetailPhone.setSmsverify(StringUtil.convertStr2Long(bamNormalModelDetailPara.getSmsVerify()));
							testresultDetailPhone.setVerify(StringUtil.convertStr2Long(bamNormalModelDetailPara.getVerify()));
							testresultDetailPhone.setVerifyimg(bamNormalModelDetailPara.getVerifyId());
							
							testresultDetailPhone.setTestsrl(qbmr.getTestsrl());
							testresultDetailPhone.setOrderid(bamNormalModelDetailPara.getStepNo());
							testresultDetailPhone.setMethod(bamNormalModelDetailPara.getMethodType());
							if(taskType == 1)
							{
								testresultDetailPhone.setTarget(bamNormalModelDetailPara.getReceivePhoneNo());
								testresultDetailPhone.setSuccstr1(bamNormalModelDetailPara.getKeywordConfig());
								testresultDetailPhone.setPostparam(bamNormalModelDetailPara.getContent());
								testresultDetailPhone.setMethod(bamNormalModelDetailPara.getOperateType());
							}
							else if(taskType == 3){
								testresultDetailPhone.setTarget(bamNormalModelDetailPara.getTarget());
								testresultDetailPhone.setSuccstr1(bamNormalModelDetailPara.getSuccstr1());
								testresultDetailPhone.setSuccstr2(bamNormalModelDetailPara.getSuccstr2());
								testresultDetailPhone.setSuccstr3(bamNormalModelDetailPara.getSuccstr3());
								testresultDetailPhone.setPostparam(bamNormalModelDetailPara.getPostParam());
							}
							testresultDetailPhone.setErrmsg(bamNormalModelDetailPara.getErrMsg());
							testresultDetailPhone.setErrstr1(bamNormalModelDetailPara.getErrstr1());
							testresultDetailPhone.setErrstr2(bamNormalModelDetailPara.getErrstr2());
							
							testresultDetailPhone.setFailretry(0L);
							testresultDetailPhone.setRetrycount(0L);
							testresultDetailPhone.setShotpic(bamNormalModelDetailPara.getShotpic());
							testresultDetailPhone.setTokentype(0);
							testresultDetailPhone.setTimertoken(bamNormalModelDetailPara.getTimerToken());
							//testresultDetailPhone.setDetailid();
							if(bamNormalModelDetailPara.getStatue() ==0)//成功
							{
								testresultDetailPhone.setResult(0L);
							}
							
							else {
								testresultDetailPhone.setResult(1L);
							}
							testresultDetailPhone.setReturncontent(bamNormalModelDetailPara.getReturncontent());
							testresultDetailPhone.setDescribe(bamNormalModelDetailPara.getDescribe());
							testresultDetailPhone.setBegintime(bamNormalModelDetailPara.getBegintime());
							testresultDetailPhone.setEndtime(bamNormalModelDetailPara.getEndtime());
							testresultDetailPhone.setWastetime(bamNormalModelDetailPara.getWastetime());
							
							testresultDetailPhone.setTestorder(bamNormalModelDetailPara.getStepNo());
							TestresultDetailPhoneList.add(testresultDetailPhone);
						}
						taskresult.setResultcontent(sb.toString());
					
					}
					
					//4g流量明细特别处理
					if(taskType ==2)
					{
						TestresultDetailPhoneList = flowTestresultDetailPhone(qbmr.getTestsrl());
					}
					
					
					smsUploadData(taskresult,TestresultDetailPhoneList,qbmr,tbusiness4gs);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
	    /**
	     * 4G流量结果明细特别处理
	     * @return 
	     */
	    private List<TestresultDetailPhone> flowTestresultDetailPhone(Long testsrl)
	    {
			List<TestresultDetailPhone> list = null;
			try {
				list = SystemUtils.getDbManage()
						.selector(TestresultDetailPhone.class)
						.where("testsrl", "=", testsrl)
						.findAll();
				
				
			} catch (DbException e) {
				e.printStackTrace();
			}
			return list;
	    }
	    
	    /**
	     * 上传数据
	     * @param taskresult
	     * @param TestresultDetailPhoneList
	     * @param tbusiness4gs 
	     */
	    private void smsUploadData(Taskresult taskresult,List<TestresultDetailPhone> TestresultDetailPhoneList,final QueryBamNormalModelRespContent qbmr, List<Tbusiness4g> tbusiness4gs)
	    {
	    	Logger.v(JSON.toJSONString(taskresult));
	    	Logger.v(JSON.toJSONString(TestresultDetailPhoneList));
	    	
	    	
	    	MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP+"/taskresult/upResultLog");
	    	params.setCharset("utf-8");
			TaskResultContent taskResultContent = new TaskResultContent();
			taskResultContent.setImsi(SystemUtils.getIMEI(this));
			taskResultContent.setPhoneNo(qbmr.getPhoneNo());
			taskResultContent.setTaskresult(taskresult);
			taskResultContent.setTestresultDetailPhones(TestresultDetailPhoneList);
			taskResultContent.setTbusiness4gs(tbusiness4gs);
			qbmr.setStatue(3);
			try {
				SystemUtils.getDbManage().saveOrUpdate(qbmr);
			} catch (DbException e) {
				e.printStackTrace();
			}
			
			RequestData requestData = new RequestData();
			requestData.setData(taskResultContent);
			requestData.setRequestTime(System.currentTimeMillis()+"");
		//	params.addQueryStringParameter("data", JSON.toJSONString(requestData));
		
			params.addBodyParameter("data", JSON.toJSONString(requestData));
			Logger.v(JSON.toJSONString(requestData));
			x.http().post(params, new Callback.CommonCallback<String>() {
				@Override
				public void onSuccess(String result) {
					Logger.v("上传成功 ："+DateUtil.getDateNowStringWithmi(new Date()));
					Logger.v(result);
					ResponeData responeData = JSON.parseObject(result, ResponeData.class);
					if(responeData != null && responeData.getCode()==0)
					{
						qbmr.setStatue(5);
						qbmr.setLocalErroMsg(responeData.getMsg());
				    	
					}
					else {
						qbmr.setStatue(4);
						qbmr.setLocalErroMsg(JSON.toJSONString(responeData));
					}
					
					try {
						SystemUtils.getDbManage().saveOrUpdate(qbmr);
					} catch (DbException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(Throwable ex, boolean isOnCallback) {
			
					Logger.v(ex.getMessage());
					qbmr.setStatue(0);
					qbmr.setLocalErroMsg("网络访问错误"+ex.getMessage());
					try {
						SystemUtils.getDbManage().saveOrUpdate(qbmr);
					} catch (DbException e) {
						e.printStackTrace();
					}
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
	    
	    
	    private void testAppDb()
	    {
			QueryBamNormalModelRespContent bamContent = new QueryBamNormalModelRespContent();
			String qbmId = UUID.randomUUID().toString();
			bamContent.setQbmId(qbmId);
			bamContent.setCreateTime(new Date());
		
			bamContent.setStatue(0);
			bamContent.setChannel("32");
			bamContent.setTasksetInstanceName("模拟数据app");
			String ObjId = UUID.randomUUID().toString();
			BamNormalModelPara bamNormalModelPara = new BamNormalModelPara();
			bamNormalModelPara.setObjectId(ObjId);
			bamNormalModelPara.setEnabled("1");//有效
			
			List<BamNormalModelDetailPara> list = new ArrayList<BamNormalModelDetailPara>();
			BamNormalModelDetailPara bamNormalModelDetailPara = new BamNormalModelDetailPara();
			bamNormalModelDetailPara.setOperateType("3");
			bamNormalModelDetailPara.setTarget("description=test");
			bamNormalModelDetailPara.setPostParam("123243");
			
			BamNormalModelDetailPara bamNormalModelDetailPara2 = new BamNormalModelDetailPara();
			bamNormalModelDetailPara2.setOperateType("47");
			bamNormalModelDetailPara2.setTarget("http://static2.tuicool.com/images/ad/teambition120.jpg");
			bamNormalModelDetailPara2.setPostParam("0.99");
	

			
			list.add(bamNormalModelDetailPara);
			list.add(bamNormalModelDetailPara2);


			bamNormalModelPara.setBamNormalModelDetailParas(list);
			bamContent.setBamNormalModel(bamNormalModelPara);
			
			bamContent.setResponseJsonDate(JSON.toJSONString(bamContent));
			try {
				SystemUtils.getDbManage().save(bamContent);
		
			
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
		private void testDb()
		{
			QueryBamNormalModelRespContent bamContent = new QueryBamNormalModelRespContent();
			String qbmId = UUID.randomUUID().toString();
			bamContent.setQbmId(qbmId);
			bamContent.setCreateTime(new Date());
		
			bamContent.setStatue(1);
			bamContent.setChannel("12");
			bamContent.setTasksetInstanceName("模拟数据");
			
			
			String ObjId = UUID.randomUUID().toString();
			BamNormalModelPara bamNormalModelPara = new BamNormalModelPara();
			bamNormalModelPara.setObjectId(ObjId);
			bamNormalModelPara.setEnabled("1");//有效
			
			
			List<BamNormalModelDetailPara> list = new ArrayList<BamNormalModelDetailPara>();
			BamNormalModelDetailPara bamNormalModelDetailPara = new BamNormalModelDetailPara();
			bamNormalModelDetailPara.setOperateType("Send");
			bamNormalModelDetailPara.setTimeout("0");
			bamNormalModelDetailPara.setSendMax("1");
			bamNormalModelDetailPara.setSendPhoneNo("10086");
			bamNormalModelDetailPara.setContent("11");
			
			BamNormalModelDetailPara bamNormalModelDetailPara2 = new BamNormalModelDetailPara();
			bamNormalModelDetailPara2.setOperateType("BeginTimer");
			bamNormalModelDetailPara2.setTimerToken("账户余额查询");
			bamNormalModelDetailPara2.setTimeout("0");
			bamNormalModelDetailPara2.setSendMax("0");
			
			BamNormalModelDetailPara bamNormalModelDetailPara3 = new BamNormalModelDetailPara();
			bamNormalModelDetailPara3.setOperateType("Receive");
			bamNormalModelDetailPara3.setTimerToken("账户余额查询");
			bamNormalModelDetailPara3.setTimeout("140");
			bamNormalModelDetailPara3.setSendMax("0");
			bamNormalModelDetailPara3.setKeywordConfig("余额>>已欠费");
			
			BamNormalModelDetailPara bamNormalModelDetailPara4 = new BamNormalModelDetailPara();
			bamNormalModelDetailPara4.setOperateType("EndTimer");
			bamNormalModelDetailPara4.setTimerToken("账户余额查询");
			bamNormalModelDetailPara4.setTimeout("0");
			bamNormalModelDetailPara4.setSendMax("0");
			
			list.add(bamNormalModelDetailPara);
			list.add(bamNormalModelDetailPara2);
			list.add(bamNormalModelDetailPara3);
			list.add(bamNormalModelDetailPara4);
			bamNormalModelPara.setBamNormalModelDetailParas(list);
			bamContent.setBamNormalModel(bamNormalModelPara);
			bamContent.setStatue(0);
			bamContent.setResponseJsonDate(JSON.toJSONString(bamContent));
			
			try {
				SystemUtils.getDbManage().save(bamContent);
		
			
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	    @Override
	    public int onStartCommand(Intent intent2, int flags, int startId) {
//	        Toast.makeText(TaskScheduService.this, "TaskScheduService Service StartCommand", Toast.LENGTH_SHORT).show();
	        
	      
	        return super.onStartCommand(intent2, flags, startId);
	    }

	    @Override
	    public void onDestroy() {   
	        //Toast.makeText( TaskScheduService.this, "TaskScheduService Service Destroy", Toast.LENGTH_SHORT).show();
	        Logger.v("TaskScheduService Service Destroy");
	        unregisterReceiver(myReceiver);
	        
	        isStop = true;
//	    	Intent intent = new Intent(this,TaskScheduService.class);
//			startService(intent);
	      
	        releaseWakeLock();
			
	    }
	    
	    

	    /**
	     * 保存日志   1 sms 2 -4g  3-app  
	     * @param qbmr
	     */
	    private void saveLog(QueryBamNormalModelRespContent qbmr,int taskType) {
			try {
				int taskTimeOut ;
				if(taskType == 1)
				{
					taskTimeOut = Contant.taskSmsTimeOut;
				}
				else if(taskType == 2)
				{
					taskTimeOut = Contant.task4GTimeOut;
				}
				else {
					taskTimeOut = Contant.taskAppTimeOut;
				}
				
				if(qbmr != null) //说明有未执行任务
				{
					//查看结果是否做完
					TaskResultLog taskResultLog =SystemUtils.getDbManage()
							.selector(TaskResultLog.class).where("qbmId","=",qbmr.getQbmId()).findFirst();
					//查看是否超时
					long epoch = System.currentTimeMillis();
					Long currentTime = epoch-qbmr.getCreateTime().getTime();
					//SystemUtils.getDbManage().close();
				
					
					if(taskResultLog == null)//任务还没做完
					{
						if(currentTime>taskTimeOut)//超时
						{
							Intent sMSServiceIntent = new Intent(TaskScheduService.this,SmsService.class);
							stopService(sMSServiceIntent);
							//提交请求...设置超时
						    qbmr.setStatue(2);//设置超时
							SystemUtils.getDbManage().saveOrUpdate(qbmr);
							smsUploadLog(2,qbmr,null,taskType);
							startService(sMSServiceIntent);
						}
					
					}else {//任务做完了
						if(currentTime>taskTimeOut)//超时
						{
							Intent sMSServiceIntent = new Intent(TaskScheduService.this,SmsService.class);
							stopService(sMSServiceIntent);
							//提交请求...设置超时
						    qbmr.setStatue(2);//设置超时
						    SystemUtils.getDbManage().saveOrUpdate(qbmr);
							smsUploadLog(2,qbmr,null,taskType);
							startService(sMSServiceIntent);
						}
						else
						{
							smsUploadLog(1,qbmr,taskResultLog,taskType);
						}
						
						
					}
				
					
				}
				
				
				
			} catch (DbException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}


		public class MyReceiver extends BroadcastReceiver {

	    	@Override
	    	public void onReceive(Context context, Intent intent) {

	    		String test = intent.getStringExtra("test");
	    		Logger.i("接受"+test);

	    	}

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
