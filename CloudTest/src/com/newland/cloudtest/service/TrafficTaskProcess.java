package com.newland.cloudtest.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.xutils.ex.DbException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.GetKeyConfig;
import com.newland.cloudtest.bean.GetKeyConfigDetail;
import com.newland.cloudtest.bean.QueryBamNormalModelRespContent;
import com.newland.cloudtest.bean.RespMsg;
import com.newland.cloudtest.bean.SMSInfo;
import com.newland.cloudtest.bean.Smstaskstep;
import com.newland.cloudtest.bean.TaskResultLog;
import com.newland.cloudtest.bean.Tbusiness4g;
import com.newland.cloudtest.bean.TestresultDetailPhone;
import com.newland.cloudtest.bean.TrafficMindMsg;
import com.newland.cloudtest.util.Contant;
import com.newland.cloudtest.util.DateUtil;
import com.newland.cloudtest.util.DownThread;
import com.newland.cloudtest.util.FileHelper;
import com.newland.cloudtest.util.KeyConfigUtil;
import com.newland.cloudtest.util.SharedPreferencesUtils;
import com.newland.cloudtest.util.SmsSytemUtils;
import com.newland.cloudtest.util.StringUtil;
import com.newland.cloudtest.util.SystemUtils;
import com.newland.cloudtest.util.TimerUtils;
import com.orhanobut.logger.Logger;

public class TrafficTaskProcess {
	private Long testsrl;
	private Integer orderid = 0;
	private float thresholdTraffic = 0f;
	private float  buyFee=0.0f;
	private QueryBamNormalModelRespContent qbmr = null;
	private TimerUtils taskTimer = new TimerUtils();
	private Context context;
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	/**
	 * 执行流量提醒探测任务
	 * 循环按顺序做每个阀值流量提醒测试
	 *	1.发送短信查询剩余流量
	 *	2.根据提取规则提取当前剩余流量
	 *	3.根据当前剩余流量 与 阀值比较 当流量大于0且小于500M,并且小于要测试的阀值时，开始消耗流量，如果剩余流量大于阀值时不做消耗步骤
	 *	4.再次查流量余额，第二次比较当前流量小于等于0  超时时间45分钟,45分钟仍然未小于等于0，继续执行
	 *	5.开始订购流量,
	 *	6.获取当前剩余流量，当当前剩余流量大于订购前流量时说明到账,超时时间45分钟,45分钟仍然未小于等于0，继续执行
	 *  7.开始消耗流量到阀值，消耗成功后开始等待提醒短信,时间根据脚本配置
	 *  8.中间出现异常直接返回异常结果
	 * @return
	 */
	public synchronized void executeTrafficTask() {
		Logger.v("executeTrafficTask begin...");
		List<BamNormalModelDetailPara>  list = new ArrayList<BamNormalModelDetailPara>();
		TimerUtils totalTime = new TimerUtils();//总跑表标示
		try{
			//从数据库查询任务
			qbmr = SystemUtils.getDbManage()
					.selector(QueryBamNormalModelRespContent.class)
					.where("statue", "=", 0).and("channel", "=", Contant.Traffic4G_CHANNEL)
					.findFirst();
		//	SystemUtils.getDbManage().close();
			if(qbmr==null){
				Logger.v("不存在4G流量探测任务...");
				return;
			}
			//数据库json串还原为对象
			if(qbmr != null)
			{
				String qbmId = qbmr.getQbmId();
				String responseJsonDate = qbmr.getResponseJsonDate();
				qbmr = JSON.parseObject(responseJsonDate, QueryBamNormalModelRespContent.class);
				qbmr.setQbmId(qbmId);
				qbmr.setResponseJsonDate(responseJsonDate);
				testsrl = qbmr.getTestsrl();
				//TODO 变更任务开始执行标志
			}
			taskTimer.startTime();
			
			//脚本数据验证
			String validResult = validParamData(qbmr);
			if(StringUtils.isNotEmpty(validResult)){
				RespMsg respMsg = new RespMsg();
				respMsg.setCode(-1);
				respMsg.setMsg(validResult);
				saveErrResult( "下发的脚本信息异常，"+respMsg.getMsg());
				return;
			}
			saveStepLog("验证脚本信息", "下发的脚本数据", "", new Date(taskTimer.getStartTime()), new Date(), "");
			//初始化数据准备及检查
			List<BamNormalModelDetailPara> BamNormalModelDetailList = qbmr.getBamNormalModel().getBamNormalModelDetailParas();
			int index = 0;//阀值计数
			String _overtime = qbmr.getBamNormalModel().getOvertime();
			String _totalTraffic = qbmr.getBamNormalModel().getBalance();
			if(!NumberUtils.isNumber(_overtime)){
				_overtime = 45*60*1000+"";
			}
			
			long timeout = 10*60*1000;//
			long overtime = Long.parseLong(_overtime);//主脚本中的超时时间，消耗完流量后等待时长，超过为超时,单位分钟
			float totalTraffic = Float.parseFloat(_totalTraffic);//主脚本中套餐总流量
			float remainTrafficValue = totalTraffic;//理论剩余流量，每次任务开始前的流量必须小于上个步骤消耗后剩余的流量大小
			long beginTimestamp = 0;
			String downloadUrl = qbmr.getTrafficTDownloadUrl();
			String result = "success";
			//开始测试，循环下发脚本中的每一个阀值
			for (Iterator<BamNormalModelDetailPara> iterator = BamNormalModelDetailList.iterator(); iterator.hasNext();) {
				index++;
				//获取当前脚本及测试的阀值值
				BamNormalModelDetailPara bamNormalModelDetail = (BamNormalModelDetailPara) iterator.next();
				long threshold = Long.parseLong(bamNormalModelDetail.getTarget());//阀值
				//当前测试阀值的值
				thresholdTraffic = threshold;
				float curTraffic = 0;//当前流量
				if ("0".equals(bamNormalModelDetail.getMethodType())) {
					//totalTraffic 套餐总量
					thresholdTraffic = totalTraffic * threshold / 100; // 按照百分比来取
				}
				//消耗流量开始  buyFee为溢出值
				buyFee = 0.0f;
				if ("0".equals(bamNormalModelDetail.getMethodType())) {//根据类型计算溢出值
					buyFee = totalTraffic * (Float.parseFloat(bamNormalModelDetail.getBuyfee())) / 100; // 按照百分比来取
				}else {
					buyFee = Float.parseFloat(bamNormalModelDetail.getBuyfee());
				}
				float consumeTraffic;
				//lsc_不是安心包判断 1---
				if(!"1".equals(qbmr.getBamNormalModel().getIsbalance())){
					Logger.v("开始第"+index+"个阀值测试："+thresholdTraffic+"M");
					saveStepLog("测试第"+index+"阀值",thresholdTraffic+ "M", "", new Date(), "");
					//查询剩余流量
					saveStepLog("开始查询剩余流量","", "", new Date(), "");
					RespMsg respMsg = new RespMsg();
					beginTimestamp = System.currentTimeMillis();
					int ioFailRetryCount = 0;
					//待确认 如果是第一个阀值测试，当前流量大于主叫本的套餐总流量值，不需要等45分钟
					while (System.currentTimeMillis() - beginTimestamp < timeout) {
						Float _curTraffic = queryLeftTraffic(qbmr.getQueryTrafficSteps(), qbmr.getQueryTrafficKeyconfig(),qbmr.getQueryTrafficKeyconfigDetail(), respMsg);
						if (respMsg.getCode() != 0){
							if (++ioFailRetryCount <= 5) { //连续5次查询失败，任务结束
								Thread.sleep(2 * 60 * 1000);// 每2分钟查询一次
								continue;
							}else {
								saveErrResult("查询剩余流量异常：" + respMsg.getMsg());
								return;
							}
						}
						
						if (_curTraffic != null) {
							curTraffic = _curTraffic;
						}
						ioFailRetryCount = 0;
						if (curTraffic <= remainTrafficValue) {
							//如果实际的流量小于理论剩余流量，则以实际流量为准，校正剩余流量
							//主要是用在做完第一个阀值后，验证消耗的流量是否入账，当实际的流量小于理论剩余流量 表示已入账
							//如果大于45分钟，超时后，也继续往下做
							remainTrafficValue = curTraffic;
							saveStepLog("查询剩余流量成功",curTraffic+"M", "", new Date(), "");
							break;
						}
						
						Thread.sleep(2 * 60 * 1000);// 每2分钟查询一次
					}
					
					if (!(curTraffic <= remainTrafficValue)) {
						//如果短信查询超时后，实际流量仍然大于理论流量值，则设置理论流量为实际流量，同时设置当前流量=实际流量
						remainTrafficValue = curTraffic;
					}
					
					//流量大于0，但小于要测试的阀值，需要订购加油包，需要先把剩余流量消耗完
					if(curTraffic > 0 && curTraffic <= thresholdTraffic){
						//消耗流量
						saveStepLog("开始消耗流量","", "", new Date(), "");
						Log.i("LSC","a"+DateUtil.getDateNowStringWithmi(new Date()));
						RespMsg respmsg = useTraffic(downloadUrl,curTraffic);
						if (0 != respmsg.getCode()){
							saveStepLog(respmsg.getMsg(),"", "", new Date(), "");
							saveErrResult(respmsg.getMsg());
							return;
						}
						Log.i("LSC","a"+DateUtil.getDateNowStringWithmi(new Date()));
						saveStepLog("流量消耗结束","", "", new Date(), "");
						
						//查询剩余流量，如果剩余流量小于等于0，退出等待，超时时长为timeout（45分钟），超时后仍然继续
						beginTimestamp = System.currentTimeMillis();
						while (System.currentTimeMillis()-beginTimestamp<timeout) {
							Float _curTraffic = queryLeftTraffic(qbmr.getQueryTrafficSteps(), qbmr.getQueryTrafficKeyconfig(),qbmr.getQueryTrafficKeyconfigDetail(), respMsg);
							if (respMsg.getCode() != 0){
								if (++ioFailRetryCount <= 5) { //连续5次查询失败，任务结束
									Thread.sleep(2 * 60 * 1000);// 每2分钟查询一次
									continue;
								}else {
									saveErrResult("查询剩余流量异常：" + respMsg.getMsg());
									return;
								}
							}
							ioFailRetryCount = 0;
							if (_curTraffic != null) {
								curTraffic = _curTraffic;
							}
							
							if(curTraffic <= 0){
								break;
							}
							Thread.sleep(2 * 60 * 1000);// 每五分钟查询一次
						}
						
					}
					//当前流量为负数，或者流量已消耗完成(不管有没有计费)，开始订购加油包
					if(curTraffic > 0 && curTraffic < thresholdTraffic || curTraffic <= 0 && curTraffic <= thresholdTraffic){
						saveStepLog("当前流量为负数，或者流量已消耗完成，开始订购加油包","", "", new Date(), "");
						RespMsg respmsg = orderTraffic(qbmr.getAddTrafficSteps());
						if(respmsg.getCode() != 0){
							//未收到订购成功短信，流量未到账
							saveErrResult("订购加油包发生异常，"+respmsg.getMsg());
							return;
						}
						
						boolean isOrderSuccess = false;
						//查询剩余流量，如果剩余流量大于之前的值，退出等待，超时时长为timeout（45分钟），超时后仍然继续
						beginTimestamp = System.currentTimeMillis();
						while (System.currentTimeMillis() - beginTimestamp < timeout) {
							Float _curTraffic = queryLeftTraffic(qbmr.getQueryTrafficSteps(), qbmr.getQueryTrafficKeyconfig(),qbmr.getQueryTrafficKeyconfigDetail(), respMsg);
							if (respMsg.getCode() != 0){
								if (++ioFailRetryCount <= 5) { //连续5次查询失败，任务结束
									Thread.sleep(2 * 60 * 1000);// 每2分钟查询一次
									continue;
								}else {
									saveErrResult("查询剩余流量异常：" + respMsg.getMsg());
									return;
								}
							}
							if(_curTraffic != null && _curTraffic > curTraffic){
								curTraffic = _curTraffic;
								isOrderSuccess = true;
								saveStepLog("成功订购加油包",curTraffic+"M", "", new Date(), "");
								break;
							}
							Thread.sleep(2 * 60 * 1000);// 每五分钟查询一次
						}
						
						if(!isOrderSuccess){
							saveErrResult("订购流量未到账");
							return;
						}
					}
					//开始消耗流量
					//算出要消耗的量 = 当前流量-阀值+溢出值
					consumeTraffic = curTraffic - thresholdTraffic + buyFee;
					if(consumeTraffic<=0){
						saveErrResult("计算后需消耗流量小于等于0，当前流量："+curTraffic+"，阀值流量："+thresholdTraffic+"，溢出值："+buyFee);
						return;
					}else{
						remainTrafficValue = (thresholdTraffic-buyFee)>0?thresholdTraffic-buyFee:0;
					}
				}else{
					if ("0".equals(bamNormalModelDetail.getMethodType())) {//按百分比
						consumeTraffic = totalTraffic - totalTraffic * threshold / 100 + totalTraffic * Float.parseFloat(bamNormalModelDetail.getBuyfee()) / 100;
					} else {
						consumeTraffic = totalTraffic - threshold + Float.parseFloat(bamNormalModelDetail.getBuyfee());
					}

				}
				
				//消耗流量
				saveStepLog("开始消耗流量",consumeTraffic+"M", "", new Date(), "");
				Log.i("LSC","B"+DateUtil.getDateNowStringWithmi(new Date()));
				//LSC_开始下载时间
				long beginconsumetime = System.currentTimeMillis(); 
				RespMsg respmsg = useTraffic(downloadUrl,consumeTraffic);
				if (0 != respmsg.getCode()){
					saveStepLog(respmsg.getMsg(),"", "", new Date(), "");
					saveErrResult(respmsg.getMsg());
					return;
				}
				Log.i("LSC","B"+DateUtil.getDateNowStringWithmi(new Date()));
				saveStepLog("流量消耗结束,等待短信提醒","", "", new Date(), "");
				TrafficMindMsg trafficMindMsg = null;
				if(respmsg.getCode()==0){
					//等待提醒短信
					beginTimestamp = System.currentTimeMillis();
					while((System.currentTimeMillis()-beginTimestamp) < overtime*60*1000){//暂时  overtime*60*1000
						Thread.sleep(30* 1000);
						//查询是否包含流量提醒短信
						trafficMindMsg = findTrafficMindMsg(qbmr.getWarnTrafficSteps().get(0).getKeywordConfig(), new Date(beginTimestamp), 60*1000*60L,context);
						if(trafficMindMsg.getCode()==0){
							saveStepLog("成功找到流量提醒短信","", "", new Date(), "");
							break;
						}else{
							saveStepLog("未找到流量提醒短信","", "", new Date(), "");
						}
					}
					if(trafficMindMsg!=null && trafficMindMsg.getCode()==0){//成功接收流量提醒短信
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, Contant.CONTEXT_KEY_BEGINTIMER,DateUtil.getDateNowStringWithmi(new Date(beginTimestamp)));
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, Contant.CONTEXT_KEY_ENDTIMER,DateUtil.getDateNowStringWithmi(trafficMindMsg.getFindTime()));
						SharedPreferencesUtils.setConfigLong(context, Contant.cachName, Contant.CONTEXT_KEY_WASTETIME,((Long)(trafficMindMsg.getFindTime().getTime()-beginTimestamp)));
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, Contant.CONTEXT_KEY_TIMERTOKEN,"4G流量提醒");
						saveSuccessTBusiness4G(new Date(beginconsumetime),new Date(beginTimestamp), trafficMindMsg.getFindTime(), trafficMindMsg.getResult(), threshold, Long.parseLong(bamNormalModelDetail.getMethodType()),((Float)thresholdTraffic).longValue());
					}else{
						//任务超时
						saveFailTBusiness4G(new Date(beginconsumetime),new Date(beginTimestamp), trafficMindMsg.getFindTime(), trafficMindMsg.getResult(), threshold, Long.parseLong(bamNormalModelDetail.getMethodType()),((Float)thresholdTraffic).longValue(),"超时");
					    result = "失败";
					}
				}else{
					saveErrResult("开始消耗流量到阀值时出现异常."+respmsg.getMsg());
					saveFailTBusiness4G(new Date(beginconsumetime),new Date(beginTimestamp), trafficMindMsg.getFindTime(), trafficMindMsg.getResult(), threshold, Long.parseLong(bamNormalModelDetail.getMethodType()),((Float)thresholdTraffic).longValue(),"测试过程异常");
					return;
				}
			}
			if("失败".equals(result)){
				saveSuccessResult("失败","存在超时或者异常的阀值");
			}else{
				saveSuccessResult("成功","4G流量提醒测试完成");
			}
			
		}catch(Exception e){
			//java.lang.NullPointerException: Attempt to invoke virtual method 'long java.util.Date.getTime()' on a null object reference
			saveErrResult("程序执行过程发生异常，"+e.getMessage());
			//saveFailTBusiness4G(null,null, null, null, null, null,((Float)thresholdTraffic).longValue(),e.getMessage()+"程序过程异常");
		}
		Logger.v("executeTrafficTask end...");
	}
	/**
	 * 下发的脚本数据校验
	 * @param qbmr
	 * @return
	 */
	private static String validParamData(QueryBamNormalModelRespContent qbmr){
		if(qbmr==null){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		if(qbmr.getBamNormalModel()==null || qbmr.getBamNormalModel().getBamNormalModelDetailParas()==null){
			sb.append("主脚本或脚本步骤信息为空\n");
		}else{
			if(!NumberUtils.isNumber(qbmr.getBamNormalModel().getBalance())){
				sb.append("主脚本中套餐总流量信息异常\n");
			}
			List<BamNormalModelDetailPara> _list = qbmr.getBamNormalModel().getBamNormalModelDetailParas();
			for (BamNormalModelDetailPara bamNormalModelDetailPara : _list) {
				String target = bamNormalModelDetailPara.getTarget();
				String methodType = bamNormalModelDetailPara.getMethodType();
				String buyFee = bamNormalModelDetailPara.getBuyfee();
				if(StringUtils.isEmpty(target)||!NumberUtils.isNumber(target)){
					sb.append("脚本步骤中的阀值信息异常值为："+target+"\n");
				}
				if(StringUtils.isEmpty(methodType)){
					sb.append("脚本步骤中的阀值类型信息异常值为空\n");
				}
				if(StringUtils.isEmpty(buyFee)||!NumberUtils.isNumber(buyFee)){
					sb.append("脚本步骤中的溢出值信息异常值为："+buyFee+"\n");
				}
			}
		}
		if((qbmr.getQueryTrafficKeyconfig()==null || qbmr.getQueryTrafficKeyconfig().size()==0)&&!"1".equals(qbmr.getBamNormalModel().getIsbalance())){
            sb.append("提取剩余流量规则配置不正确\n");
        }
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//			System.out.println(NumberUtils.isNumber("22.2"));
//			System.out.println(((Float)22.2f).longValue());
//	}
	/**
	 * 记录异常结果
	 */
	public void saveErrResult(String curmsg){
		TaskResultLog taskResultLog = new TaskResultLog();
		taskResultLog.setQbmId(qbmr.getQbmId());
		taskResultLog.setResult("失败");
		taskResultLog.setResultContent(curmsg);
		taskResultLog.setResultDetail(curmsg);
		taskResultLog.setIsUpload(0);
		Logger.v("开始记录异常结果，异常信息："+curmsg+"...");
		saveStepLog("任务执行失败",curmsg,"", new Date(), taskResultLog.getResultContent());
		saveResultToDB(taskResultLog);
	}
	/**
	 * 记录正常结果
	 * @param string 
	 */
	public void saveSuccessResult(String result,String msg){
		TaskResultLog taskResultLog = new TaskResultLog();
		taskResultLog.setQbmId(qbmr.getQbmId());
		taskResultLog.setResult(result);
		taskResultLog.setResultContent(msg);
		taskResultLog.setResultDetail(msg);
		taskResultLog.setIsUpload(0);
		saveStepLog("任务执行"+result,"","", new Date(), taskResultLog.getResultContent());
		saveResultToDB(taskResultLog);
	}
	public void saveResultToDB(TaskResultLog taskResultLog){
		taskTimer.stopTime();
		SharedPreferencesUtils.setConfigStr(context, Contant.cachName, Contant.CONTEXT_KEY_TASKSTARTTIME,taskTimer.getStartTimeStr());
		SharedPreferencesUtils.setConfigStr(context, Contant.cachName, Contant.CONTEXT_KEY_TASKENDTIME,taskTimer.getEndTimeStr());
		SharedPreferencesUtils.setConfigLong(context, Contant.cachName, Contant.CONTEXT_KEY_TASKWASTETIME,taskTimer.getWasteTime());
		//保存TaskResultLog
		try {
			SystemUtils.getDbManage().save(taskResultLog);
		} catch (DbException e) {
			e.printStackTrace();
		}
		//变更任务状态
		
	}
	/**
	 * 记录正常结果到TbUSINESS4G
	 */
	public void saveSuccessTBusiness4G(Date beginConsumeTime,Date beginTime,Date endTime,String smsmsg,Long threshold,Long thresholdtype,Long thresholdvalue){
		Tbusiness4g data = new Tbusiness4g();
		data.setTestsrl(qbmr.getTestsrl());
		data.setBrand(qbmr.getBamNormalModel().getBrand());
		data.setCarrier(qbmr.getBamNormalModel().getCarrier());
		data.setCreatetime(DateUtil.getDateNowStringWithmi(new Date()));
		data.setBeginconsumetime(DateUtil.getDateNowStringWithmi(beginConsumeTime));
		data.setBegintime(DateUtil.getDateNowStringWithmi(beginTime));
		data.setEndtime(DateUtil.getDateNowStringWithmi(endTime));
		data.setWastetime(endTime.getTime()-beginTime.getTime());
		data.setPhoneno(qbmr.getPhoneNo());
		data.setProvince(qbmr.getBamNormalModel().getProvince());
		data.setResult("成功");
		data.setSmsmsg(smsmsg);
		data.setTaskid(Long.parseLong(qbmr.getBamNormalModel().getObjectId()));
		data.setTasksetid(qbmr.getTasksetId());
		data.setTasksetinstancename(qbmr.getTasksetInstanceName());
		data.setThresholdN(threshold);
		data.setThresholdtype(thresholdtype);
		data.setThresholdvalue(thresholdvalue);
		//保存数据库
		try {
			SystemUtils.getDbManage().save(data);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 记录失败结果到TbUSINESS4G
	 * @param msg 
	 */
	public void saveFailTBusiness4G(Date beginConsumeTime,Date beginTime,Date endTime,String smsmsg,Long threshold,Long thresholdtype,Long thresholdvalue, String msg){
		Tbusiness4g data = new Tbusiness4g();
		data.setTestsrl(qbmr.getTestsrl());
		data.setBrand(qbmr.getBamNormalModel().getBrand());
		data.setCarrier(qbmr.getBamNormalModel().getCarrier());
		data.setCreatetime(DateUtil.getDateNowStringWithmi(new Date()));
		if(beginConsumeTime!=null){
			data.setBeginconsumetime(DateUtil.getDateNowStringWithmi(beginConsumeTime));

		}
		if(beginTime!=null){
			data.setBegintime(DateUtil.getDateNowStringWithmi(beginTime));
		}
		if(endTime!=null){
			data.setEndtime(DateUtil.getDateNowStringWithmi(endTime));
		}
		
		if(endTime != null && beginTime!=null)
		data.setWastetime(endTime.getTime()-beginTime.getTime());
		data.setPhoneno(qbmr.getPhoneNo());
		data.setProvince(qbmr.getBamNormalModel().getProvince());
		data.setResult(msg);
		data.setSmsmsg(smsmsg);
		data.setTaskid(Long.parseLong(qbmr.getBamNormalModel().getObjectId()));
		data.setTasksetid(qbmr.getTasksetId());
		data.setTasksetinstancename(qbmr.getTasksetInstanceName());
		data.setThresholdN(threshold);
		data.setThresholdtype(thresholdtype);
		data.setThresholdvalue(thresholdvalue);
		//保存数据库
		try {
			SystemUtils.getDbManage().save(data);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	public void saveStepLog(String method,String target,String postparam,Date time,String returncontent){
		if(time==null){
			return;
		}
		saveStepLog(method, target, postparam, time, time, returncontent);
	}
	public void saveStepLog(String method,String target,String postparam,Date beginTime,Date endTime,String returncontent){
		if(beginTime==null || endTime==null){
			return;
		}
		TestresultDetailPhone log = new TestresultDetailPhone();
		log.setTestsrl(testsrl);
		log.setOrderid((double)orderid++);
		log.setTestorder(log.getOrderid());
		log.setSteptype(0L);
		log.setDescribe(thresholdTraffic+"");//阀值信息
		log.setMethod(method);//操作名称
		log.setTarget(target);//操作对象
		log.setPostparam(postparam);//短信内容
		log.setBegintime(DateUtil.getDateNowStringWithmi(beginTime));//开始时间 yyyy-MM-dd HH:mm:ss
		log.setEndtime(DateUtil.getDateNowStringWithmi(endTime));//结束时间yyyy-MM-dd HH:mm:ss
		log.setWastetime( ((Long)(endTime.getTime()-beginTime.getTime())).longValue());//耗时 毫秒
		log.setReturncontent(returncontent);//返回内容
		log.setBuyfee(((Float)buyFee).longValue());//溢出值
		log.setCreateTime(new Date());
		//保存进数据库
		try {
			SystemUtils.getDbManage().save(log);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询剩余流量
	 * @throws Exception 
	 */
	public Float queryLeftTraffic(List<Smstaskstep> queryTrafficSteps,List<GetKeyConfig> queryTrafficKeyconfig, List<GetKeyConfigDetail> queryTrafficKeyconfigDetail,
			RespMsg resp) throws Exception{
		RespMsg respMsg = queryLeftTraffic(queryTrafficSteps);
		if(respMsg.getCode()==0){
			try{
				Float result = getTrafficValue(respMsg.getResult(), queryTrafficKeyconfig, queryTrafficKeyconfigDetail);
				resp.setCode(0);
				return result;
			} catch(Exception e){
				resp.setCode(-1);
				resp.setMsg(e.getMessage());
				return null;
			}
		}else{
			resp.setCode(-1);
			resp.setMsg(respMsg.getMsg());
			return null;
		}
	}
	
	/**
	 * 查询剩余流量  
	 * TDDO lsd
	 */
	public RespMsg queryLeftTraffic(List<Smstaskstep> queryTrafficSteps){
		RespMsg respMsg = new RespMsg();
		if(queryTrafficSteps != null)
		{
			try {
				for (Smstaskstep smstaskstep : queryTrafficSteps) {
					SmsSytemUtils.decodeDownLoadSmsScript(smstaskstep, context,respMsg);
				}
				respMsg.setCode(0);
			} catch (Exception e) {
				respMsg.setCode(1);
				respMsg.setMsg(e.getMessage());
				e.printStackTrace();
			}
		}else{
			respMsg.setCode(1);
			respMsg.setMsg("传入参数为空");
		}
		
		return respMsg;
	}
	
	/**
	 * 订购流量
	 * @param list
	 * TDDO lsd 
	 */
	public RespMsg orderTraffic(List<Smstaskstep> list){
		RespMsg respMsg = new RespMsg();
		if(list != null)
		{
			try {
				for (Smstaskstep smstaskstep : list) {
					SmsSytemUtils.decodeDownLoadSmsScript(smstaskstep, context,respMsg);
				}
				respMsg.setCode(0);
			} catch (Exception e) {
				respMsg.setCode(1);
				respMsg.setMsg(e.getMessage());
				e.printStackTrace();
			}
		}else{
			respMsg.setCode(1);
			respMsg.setMsg("传入参数为空");
		}
		return respMsg;
	}
	
	/**
	 * 消耗流量
	 * @param curTraffic 
	 * @param downloadUrl  http://gdown.baidu.com/data/wisegame/08ddc3835a617b8b/baidushoujizhushou_16790412.apk
	 * TDDO lsd 
	 */
	public RespMsg useTraffic(final String downloadUrl, final float curTraffic){
		final RespMsg respMsg =  new RespMsg();
		respMsg.setCode(-1);
		//获取当前流量
		Long currentFlow = (long) SystemUtils.get4Gflow();
		String rootPath = 	Environment.getExternalStorageDirectory()+"/cloudCrash/";
		String fileName1 = "4Gflow1.ct";
		String fileName2 = "4Gflow2.ct";
		File dest1 = new File(rootPath+fileName1);
		File dest2 = new File(rootPath+fileName2);
		File dir = new File(rootPath);
		try{
			if (!dir.exists()){
				if(!dir.exists()){
					FileHelper.createDir(dir);
				}
			}
			if (!dest1.exists()){
				dest1.createNewFile();
			}
			if (!dest2.exists()){
				dest2.createNewFile();
			}
			
		} catch(Exception e){
			e.printStackTrace();
			respMsg.setCode(1); //这里不能是-1（-1表示还在下载中）
			respMsg.setMsg("下载失败");
			return respMsg;
		}
		SharedPreferencesUtils.setConfigBoolean(context, Contant.cachName, "thread1finishflag", false);
		SharedPreferencesUtils.setConfigBoolean(context, Contant.cachName, "thread2finishflag", false);
		SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "thread1err", "");
		SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "thread2err", "");
		
		DownThread thread1 = new DownThread(context, currentFlow, curTraffic, downloadUrl, dest1, "thread1");
		DownThread thread2 = new DownThread(context, currentFlow, curTraffic, downloadUrl, dest2, "thread2");
		thread1.start();
		thread2.start();
		Long curTime = System.currentTimeMillis();
		Long oTime = 60*60*1000L;
		
		while (true) {
			Long downedFlow = (long) SystemUtils.get4Gflow();
			if((downedFlow-currentFlow) < curTraffic) {
				if (System.currentTimeMillis() - curTime > oTime){
					respMsg.setCode(1); //这里不能是-1（-1表示还在下载中）
					if (StringUtil.isEmpty(respMsg.getMsg())){
						respMsg.setMsg("流量消耗失败或者超时");
						break;
					}
				} else {
					boolean isFinish1 = SharedPreferencesUtils.getConfigBoolean(context, Contant.cachName, "thread1finishflag");
					boolean isFinish2 = SharedPreferencesUtils.getConfigBoolean(context, Contant.cachName, "thread2finishflag");
					if (isFinish1 && isFinish2){
						respMsg.setCode(1); //这里不能是-1（-1表示还在下载中）
						String err1 = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "thread1err");
						String err2 = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "thread2err");
						respMsg.setMsg(err1+err2);
						break;
					}
				}
			} else {
				respMsg.setCode(0);
				respMsg.setMsg("流量消耗结束");
				break;
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return respMsg;
	}
	
	/**
	 * 提取剩余流量
	 * @param content 短信内容
	 * @param queryTrafficKeyconfig 流量提取规则配置
	 * @param queryTrafficKeyconfigDetail 
	 * @return
	 */
	private Float getTrafficValue(String content,List<GetKeyConfig> queryTrafficKeyconfig, List<GetKeyConfigDetail> queryTrafficKeyconfigDetail) throws Exception{
		if(StringUtils.isEmpty(content) || queryTrafficKeyconfig==null || queryTrafficKeyconfig.size()==0){
			return null;
		}
		Float result = null;
		String value = null;
		if(queryTrafficKeyconfigDetail != null && queryTrafficKeyconfigDetail.size() > 0){
			value = KeyConfigUtil.getValueByDetail(content, queryTrafficKeyconfigDetail);
		}else{
			value = KeyConfigUtil.getValueByMain(content, queryTrafficKeyconfig);
		}

		if(NumberUtils.isNumber(value)){
			result = Float.parseFloat(value);
			Logger.v("流量提取成功，获取到的剩余流量为："+value);
		}else{
			Logger.v("流量提取失败，提取到的值为："+value);
		}
		return result;
	}
	/**
	 * 查询流量提醒短信
	 * @param keyword 短信关键字
	 * @param beginTime 开始时间
	 * @return
	 * TDDO lsd 
	 */
	private static TrafficMindMsg findTrafficMindMsg(String keyword,Date beginTime,Long TimeOut,Context context){
		TrafficMindMsg trafficMindMsg =  new TrafficMindMsg();
		try {
			List<SMSInfo> smsInfos = SmsSytemUtils.getInstance().findSmsInfoAfterTime(context, beginTime);
			if(smsInfos!=null && smsInfos.size() > 0){
				for (SMSInfo smsInfo : smsInfos) {
					if(smsInfo.getCreateTime().getTime() > beginTime.getTime())	{
						String[] keyWords = StringUtils.split(keyword, ">>");
						for (String _keyWord : keyWords) {
							if (StringUtils.contains(smsInfo.getContent(), _keyWord)) {
								trafficMindMsg.setCode(0);
								trafficMindMsg.setFindTime(smsInfo.getCreateTime());
								trafficMindMsg.setMsg("成功找到");
								trafficMindMsg.setResult(smsInfo.getContent());
								return trafficMindMsg;
							}
						}
					}
				}
			} 
			trafficMindMsg.setCode(1);
			trafficMindMsg.setMsg("未找到匹配关键字");
		} catch (Exception e) {
			e.printStackTrace();
			trafficMindMsg.setCode(1);
			trafficMindMsg.setMsg(e.getMessage());
		}
		
		return trafficMindMsg;	
	}
}
