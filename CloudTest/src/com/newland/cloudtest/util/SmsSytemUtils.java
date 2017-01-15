package com.newland.cloudtest.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;

import android.R.integer;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.newland.cloudtest.MyApplication;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.BamNormalModelPara;
import com.newland.cloudtest.bean.MyXutilsRequestParams;
import com.newland.cloudtest.bean.QueryBamNormalModelRespContent;
import com.newland.cloudtest.bean.RequestData;
import com.newland.cloudtest.bean.RespMsg;
import com.newland.cloudtest.bean.SMSInfo;
import com.newland.cloudtest.bean.Smstaskstep;
import com.newland.cloudtest.exception.SmsException;
import com.newland.cloudtest.service.SmsService;
import com.orhanobut.logger.Logger;

/**
 * 发送短信，读取短信接口
 * 
 * @author TongLee
 *
 */
public class SmsSytemUtils {

	private SmsSytemUtils() {
	}

	private static SmsSytemUtils single = null;

	// 静态工厂方法
	public static SmsSytemUtils getInstance() {
		if (single == null) {
			single = new SmsSytemUtils();
		}
		return single;
	}

	/**
	 * 直接调用短信接口发短信
	 * 
	 * @param phoneNumber
	 * @param message
	 */
	public Integer sendSMS(String number, String message, final Context context) {
		String SENT = "sms_sent";
		String DELIVERED = "sms_delivered";
		//
		// PendingIntent sentPI = PendingIntent.getActivity(context, 0, new
		// Intent(SENT), 0);
		// PendingIntent deliveredPI = PendingIntent.getActivity(context, 0, new
		// Intent(DELIVERED), 0);
		//
		// 处理返回的发送状态
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
				sentIntent, 0);
		PendingIntent deliveredPI = PendingIntent.getActivity(context, 0,
				new Intent(DELIVERED), 0);
		
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					SharedPreferencesUtils.setConfigInt(context, Contant.cachName, "smssendflag", 0);
					SharedPreferencesUtils.setConfigLong(context, Contant.cachName, "smssendtime", System.currentTimeMillis());
					Logger.v("短信发送成功");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Logger.v("短信发送失败");
					SharedPreferencesUtils.setConfigInt(context, Contant.cachName, "smssendflag", 1);
					SharedPreferencesUtils.setConfigLong(context, Contant.cachName, "smssendtime", System.currentTimeMillis());
					Toast.makeText(context, "短信发送失败", 1500).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Logger.v("无线广播被明确地关闭");
					Toast.makeText(context, "无线广播被明确地关闭", 1500).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Logger.v("没有提供pdu");
					Toast.makeText(context, "没有提供pdu", 1500).show();
					break;
				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));

		// 获取短信管理器
		android.telephony.SmsManager smsManager = android.telephony.SmsManager
				.getDefault();
		SharedPreferencesUtils.setConfigInt(context, Contant.cachName, "smssendflag", -1);
		// 拆分短信内容（手机短信长度限制）
		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents) {
			smsManager.sendTextMessage(number, null, text, sentPI, deliveredPI);
		}
		Long timeout = 120 * 1000L;
		Long curTime = System.currentTimeMillis();
		while(true){
			int smssendflag = SharedPreferencesUtils.getConfigInt(context, Contant.cachName, "smssendflag", -1);
			if (-1 != smssendflag){
				return smssendflag;
			}
			if (System.currentTimeMillis() - curTime > timeout){
				return 2;
			}
		}
	}

	
	
	
	
	
	/**
	 * 截取余额 preKey 关键字之前字符串 endKey 关键字之后字符串 如果关键字之后字符串 为空 则截取截取长度 splitLength
	 * 截取长度
	 * 
	 * @return
	 */

	public String splitBalance(String preKey, String endKey, int splitLength,
			String content) {
		int index = content.indexOf(preKey);
		if (StringUtil.isEmpty(endKey)) // 直接截取长度
		{
			String endStr = content.substring(index, content.length());
			endStr = endStr.replace(preKey, "");
			return endStr.substring(0, splitLength);

		} else {// 截取开头到结尾
			String endStr = content.substring(index, content.length());
			int endindex = endStr.indexOf(endKey);
			String shortStr = endStr.substring(0, endindex); // 当前账户总余额为44.67
			String replace = shortStr.replace(preKey, "");
			return replace;
		}

	}
	

	/**
	 * 解析短厅脚本
	 * 
	 * @param smsInfos
	 * @throws SmsException
	 * @throws IOException 
	 */
	public static void decodeSmsScript( BamNormalModelPara bamNormalModelPara,
			BamNormalModelDetailPara bamNormalModelDetailPara, Context context,TimerUtils timerUtils
			) throws SmsException, IOException {
		

		
		// 发送
		if ("Send".equals(bamNormalModelDetailPara.getOperateType())) {
		
			TimerUtils smsTimerUtils = new TimerUtils();
			smsTimerUtils.startTime();
			
			Logger.v("正在执行发送短信操作...");
			String receivePhoneNo;
			String sendContent;
			// 判断短信内容不为空
			if (StringUtil.isEmpty(bamNormalModelDetailPara.getContent())) {
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setErrMsg("发送短信内容为空,短信脚本配置有误！");
				bamNormalModelDetailPara.setCreateTime(new Date());
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("发送短信内容为空,短信脚本配置有误！");
			}
			// 转义解析短信内容，把时间转成对应时间
			sendContent = DynamicVariable.replaceAllDynamicVariables(
					bamNormalModelDetailPara.getContent(),
					SystemUtils.getPhoneNo(context),
					SystemUtils.getIMEI(context), null);

			// 判断接收短信号码是否为空
			if (StringUtil
					.isEmpty(bamNormalModelDetailPara.getReceivePhoneNo())) {
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setErrMsg("被叫号码为空，短信脚本配置有误！");
				bamNormalModelDetailPara.setCreateTime(new Date());
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("被叫号码为空，短信脚本配置有误！");
			}
			// 检查接收短信号码是类似 10086
			if (SystemUtils.isInteger(bamNormalModelDetailPara
					.getReceivePhoneNo())) {
				receivePhoneNo = bamNormalModelDetailPara.getReceivePhoneNo();

//				Intent intent = new Intent();
//				intent.setAction(SmsService.Action);
//				intent.putExtra("smsBiz", SmsService.sendMsg);
//				intent.putExtra("sendCode", sendContent);
//				intent.putExtra("number", receivePhoneNo);
//				context.sendBroadcast(intent);
				
				int flag = SmsSytemUtils.getInstance().sendSMS(receivePhoneNo,sendContent, context);
				switch (flag){
				case 0:
					bamNormalModelDetailPara.setStatue(0);// 设置标志已成功
					break;
				case 1:
					bamNormalModelDetailPara.setStatue(1);// 设置标志失败
					bamNormalModelDetailPara.setErrMsg("短信发送失败");
					break;
				case 2:
					bamNormalModelDetailPara.setStatue(2);// 设置标志超时
					bamNormalModelDetailPara.setErrMsg("短信发送超时");
					break;
				} 
			
				bamNormalModelDetailPara.setContent(sendContent);
				bamNormalModelDetailPara.setCreateTime(new Date());
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);

				if(flag != 0){//如果短信发送不成功，则抛出异常终止脚本继续运行
					throw new SmsException(bamNormalModelDetailPara.getErrMsg());
				}
			} else if (bamNormalModelDetailPara.getReceivePhoneNo().toLowerCase().startsWith("reply")) {// 回复短信
				
				try {
					//查找上一条接收的短信
//					SMSInfo smsInfo = SystemUtils.getDbManage()
//							.selector(SMSInfo.class)
//							.where("type", "=", 1).orderBy("createTime", true)
//							.findFirst();
				  SMSInfo smsInfo = SmsSytemUtils.getInstance().getFirstSmsInfo(context);
					//SystemUtils.getDbManage().close();
//					Intent intent = new Intent();
//					intent.setAction(SmsService.Action);
//					intent.putExtra("smsBiz", SmsService.sendMsg);
//					intent.putExtra("sendCode", bamNormalModelDetailPara.getContent());
//					intent.putExtra("number", smsInfo.getMobile());
//					context.sendBroadcast(intent);
					
					SmsSytemUtils.getInstance().sendSMS(smsInfo.getMobile(), bamNormalModelDetailPara.getContent(), context);
					
					bamNormalModelDetailPara.setStatue(0);// 设置标志已成功
					bamNormalModelDetailPara.setContent(sendContent);
					bamNormalModelDetailPara.setCreateTime(new Date());
					smsTimerUtils.stopTime();
					bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
					bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
					SystemUtils.getDbManage().save(bamNormalModelDetailPara);
					
				} catch (DbException e) {
					e.printStackTrace();
				}
				
				

			} else {
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setErrMsg("无法识别叫号码类别，短信脚本配置有误！");
				bamNormalModelDetailPara.setCreateTime(new Date());
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("无法识别叫号码类别，短信脚本配置有误！");
			}

			// 如果短信内容包含定义的变量
			// if (isContainVariable(bamNormalModelDetailPara.getContent())) {
			//
			// }

		}

		// 接收
		else if ("Receive".equals(bamNormalModelDetailPara.getOperateType())) {
			Logger.v("正在执行接收短信操作...");
	
			TimerUtils smsTimerUtils = new TimerUtils();
			smsTimerUtils.startTime();
			
			// 接收短信超时时间未配置
			if (bamNormalModelDetailPara.getTimeout() == null
					|| Integer.parseInt(bamNormalModelDetailPara.getTimeout()) <= 0) {
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setErrMsg("操作时间无效，短信脚本配置有误");
				bamNormalModelDetailPara.setCreateTime(new Date());
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getEndTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("操作时间无效，短信脚本配置有误");
			}
			// 关键字匹配
			if (StringUtils
					.isEmpty(bamNormalModelDetailPara.getKeywordConfig())) {
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setErrMsg("关键字配置都为空，短信脚本配置有误！");
				bamNormalModelDetailPara.setCreateTime(new Date());
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getEndTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("关键字配置都为空，短信脚本配置有误！");
			}
			Long reckonByTime = Long.parseLong(bamNormalModelDetailPara
					.getTimeout()) * 1000;
			Long currentTime = 0L;//当前时间计时
			Long currentsmsTime = System.currentTimeMillis();
			try {
			
				boolean isTimeOut = false;// 是否超时
				while (!isTimeOut) {
					
					currentTime+=1000;
					Thread.sleep(1000);
//					MyApplication myApplication = (MyApplication) ((Service)context).getApplication();
		
//					SMSInfo smsInfo = SystemUtils.getDbManage()
//							.selector(SMSInfo.class)
//							.where("createTime", ">", currentsmsTime).orderBy("createTime", true).and("type", "=", 1)
//							.findFirst();
					SMSInfo smsInfo = SmsSytemUtils.getInstance().getFirstSmsInfo(context);
//					SMSInfo smsInfo = myApplication.getSmsInfo();
					
					if(currentTime< reckonByTime ) //在超时时间内
					{
						 //确认在计时前收到短信
						if(smsInfo != null && smsInfo.getCreateTime().getTime() > currentsmsTime)
						{
							 
                            	String content = smsInfo.getContent();
    							bamNormalModelDetailPara.setReturncontent(content);
    							// 不包含任何字符的关键字符号  有符合的关键字
    							if (StringUtils.contains(content,bamNormalModelDetailPara.getKeywordConfig())) {
    								
    								bamNormalModelDetailPara.setStatue(0);// 设置标志已成功
    								bamNormalModelDetailPara.setCreateTime(new Date());
    								smsTimerUtils.stopTime();
    								bamNormalModelDetailPara.setBegintime(smsTimerUtils.getEndTimeStr());
    								bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
    								SystemUtils.getDbManage().save(bamNormalModelDetailPara);
//    								myApplication.setSmsInfo(null);
    								break;
    							}

    							// 包含">>"的关键字
    							else if (StringUtils.contains(bamNormalModelDetailPara.getKeywordConfig(), ">>")) {

    								String[] keyWords = StringUtils.split(bamNormalModelDetailPara.getKeywordConfig(),
    										">>");
    								int countkey = 0;
    								for (String _keyWord : keyWords) {
    									if (StringUtils.contains(content, _keyWord)) {
    										countkey++;
    									}
    								}
    								if (countkey ==0) {
    									bamNormalModelDetailPara.setStatue(1);
    									bamNormalModelDetailPara.setCreateTime(new Date());
    									smsTimerUtils.stopTime();
    									bamNormalModelDetailPara.setBegintime(smsTimerUtils.getEndTimeStr());
    									bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
    									bamNormalModelDetailPara.setErrMsg("接收短信并不包含关键字");
    									SystemUtils.getDbManage().save(bamNormalModelDetailPara);
//    									myApplication.setSmsInfo(null);
    									throw new SmsException("接收短信并不包含关键字");
    								}
    								else {
    								
    							
    										SharedPreferencesUtils.setConfigInt(context, Contant.cachName, "taskValue", 0);
    										bamNormalModelDetailPara.setStatue(0);// 设置标志已成功
    										bamNormalModelDetailPara.setCreateTime(new Date());
    										smsTimerUtils.stopTime();
    										bamNormalModelDetailPara.setBegintime(smsTimerUtils.getEndTimeStr());
    										bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
    										SystemUtils.getDbManage().save(bamNormalModelDetailPara);
//    										myApplication.setSmsInfo(null);
    										break;
    										
    								}
    							}
    							else {//关键字不匹配
    								bamNormalModelDetailPara.setStatue(1);
    								bamNormalModelDetailPara.setErrMsg("接收短信并不包含关键字");
    								bamNormalModelDetailPara.setCreateTime(new Date());
    								smsTimerUtils.stopTime();
    								bamNormalModelDetailPara.setBegintime(smsTimerUtils.getEndTimeStr());
    								bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
    								SystemUtils.getDbManage().save(bamNormalModelDetailPara);
//    								myApplication.setSmsInfo(null);
    								throw new SmsException("接收短信并不包含关键字");
    							}
                        
                            	
							
						}
						else {//说明还没收到短信
							continue;
						}
				
						
					}
					else {
						bamNormalModelDetailPara.setStatue(1);
						bamNormalModelDetailPara.setErrMsg("接收短信超时");
						bamNormalModelDetailPara.setCreateTime(new Date());
						smsTimerUtils.stopTime();
						bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
						bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
						SystemUtils.getDbManage().save(bamNormalModelDetailPara);
//						myApplication.setSmsInfo(null);
						throw new SmsException("接收短信超时");
					}
					
		

				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//启动跑表
		else if ("BeginTimer".equals(bamNormalModelDetailPara.getOperateType())) {
			TimerUtils smsTimerUtils = new TimerUtils();
			smsTimerUtils.startTime();
			
			if (StringUtils.isEmpty(bamNormalModelDetailPara.getTimerToken())) {
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setCreateTime(new Date());
				bamNormalModelDetailPara.setErrMsg("开始跑表标志为空，短信脚本配置有误！");
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("开始跑表标志为空，短信脚本配置有误！");
			}
			
			
			timerUtils.startTime();
			SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "BeginTimer",timerUtils.getStartTimeStr());
			SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "timertoken",bamNormalModelDetailPara.getTimerToken());
			bamNormalModelDetailPara.setStatue(0);// 设置标志已成功
			bamNormalModelDetailPara.setCreateTime(new Date());
			smsTimerUtils.stopTime();
			bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
			bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
			SystemUtils.getDbManage().save(bamNormalModelDetailPara);
		}
		
		//结束跑表
		else if ("EndTimer".equals(bamNormalModelDetailPara.getOperateType())) {
			
			TimerUtils smsTimerUtils = new TimerUtils();
			smsTimerUtils.startTime();
			
			if (StringUtils.isEmpty(bamNormalModelDetailPara.getTimerToken())) {
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setCreateTime(new Date());
				bamNormalModelDetailPara.setErrMsg("结束跑表标志为空，短信脚本配置有误！");
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				
				throw new SmsException("结束跑表标志为空，短信脚本配置有误！");
			}
			
			//保存跑表结束计时时间
			timerUtils.stopTime();
			SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "EndTimer",timerUtils.getEndTimeStr());
			SharedPreferencesUtils.setConfigLong(context, Contant.cachName, "Wastetime",timerUtils.getCurrentTime());
			bamNormalModelDetailPara.setStatue(0);// 设置标志已成功
			bamNormalModelDetailPara.setCreateTime(new Date());
			smsTimerUtils.stopTime();
			bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
			bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
			SystemUtils.getDbManage().save(bamNormalModelDetailPara);
		}
		
		// 暂停操作
		else if ("Pause".equalsIgnoreCase(bamNormalModelDetailPara.getOperateType())) {
			TimerUtils smsTimerUtils = new TimerUtils();
			smsTimerUtils.startTime();
			
			if(StringUtils.isEmpty(bamNormalModelDetailPara.getTimeout()))
			{
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setErrMsg("暂停时间为空，短信脚本配置有误！");
				bamNormalModelDetailPara.setCreateTime(new Date());
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("暂停时间为空，短信脚本配置有误！");
			}
			//暂停时间
			Long pauseTime = Long.parseLong(bamNormalModelDetailPara.getTimeout())*1000;
			try {
				Thread.sleep(pauseTime);
				bamNormalModelDetailPara.setStatue(0);// 设置标志已成功
				bamNormalModelDetailPara.setCreateTime(new Date());
				smsTimerUtils.stopTime();
				bamNormalModelDetailPara.setBegintime(smsTimerUtils.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(smsTimerUtils.getEndTimeStr());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		

	}

	/**
	 * 判定是否包含一个变量值 格式为：#任意字母、数字、下划线、汉字#
	 * 
	 * @param value
	 * @return
	 */
	private static boolean isContainVariable(String value) {
		String[] values = StringUtils.substringsBetween(value, "#", "#");
		return (values == null || values.length <= 0) ? false : true;
	}
	
	/**
	 * 根据取值类型返回业务关键值
	 * @param getValueType
	 * @param receiveContent
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getTaskReturnValue(int getValueType, String receiveContent,BamNormalModelPara bamNormalModelPara) {
		// 去除回车、换行字符
		String tempContent = StringUtils.remove(receiveContent, '\n');
		tempContent = StringUtils.remove(tempContent, '\r');
		String value = Contant.TOKEN_TASKVALUE_DEFAULT;
		List configMapList = null;
		Map configMap = null;
		BigDecimal valueSum = new BigDecimal(0);
		// 保存正常情况的配置
		List normalConfigMapList = new ArrayList();
		// 保存欠费情况的配置
		List overdueConfigMapList = new ArrayList();
		// 是否正确获取
		boolean isChange = false;
		switch (getValueType) {
		// 取余额
		case Contant.TOKEN_GETVALUETYPE_BALANCE:
			configMapList = bamNormalModelPara.getGetKeyConfigList();
			Log.d("", "取余额.getValueType===========" + getValueType);
			break;
		// 取积分
		case Contant.TOKEN_GETVALUETYPE_BONUS:
			configMapList =  bamNormalModelPara.getGetKeyConfigList();
			Log.d("", "取积分.getValueType===========" + getValueType);
			break;
		// 取PUK码
		case Contant.TOKEN_GETVALUETYPE_PUK:
			configMapList =  bamNormalModelPara.getGetKeyConfigList();
			Log.d("", "取PUK码.getValueType=========" + getValueType);
			break;
		// 取实时话费
		case Contant.TOKEN_GETVALUETYPE_REAL:
			configMapList =  bamNormalModelPara.getGetKeyConfigList();
			Log.d("", "取实时话费.getValueType========" + getValueType);
			break;
		}
		if (configMapList != null) {
			// 先分组
			Iterator it = configMapList.iterator();
			while (it.hasNext()) {
				// 获取配置信息
				configMap = (Map) it.next();
				String note = "";
				if (configMap.containsKey("note")) {
					note = StringUtils.trim((String) configMap.get("note"));
				}
				// 如果是欠费
//				if (note.toLowerCase().contains(Contant.TOKEN_OVERDUE.toLowerCase())) {
//					overdueConfigMapList.add(configMap);
//				} else {
//					normalConfigMapList.add(configMap);
//				}
			}
			// 先匹配正常情况
			it = normalConfigMapList.iterator();
			boolean flg = false;
			while (it.hasNext()) {
				// 获取配置信息
				configMap = (Map) it.next();
				String beforeKeywords = (String)configMap.get("beforekeywords");
				// 先判断是否包含前置关键字
				if (StringUtils.containsIgnoreCase(tempContent, beforeKeywords)) {
					flg = true;
					String symbol = (String) configMap.get("symbol");
					value = parseNumber(configMap, beforeKeywords, tempContent);
					if (isNumericValue(value)) {
						// 减法操作
						if ("-".equals(symbol)) {
							valueSum = valueSum.subtract(new BigDecimal(value));
						}
						// 加法操作
						else if ("+".equals(symbol)) {
							valueSum = valueSum.add(new BigDecimal(value));
						}
						isChange = true;
					}
					// 如果提取的内容非数值，不用进行计算
					else {
						isChange = false;
						valueSum = new BigDecimal(0);
						// 如果没有配置欠费情况，则返回
						if (overdueConfigMapList.isEmpty()) {
							return Contant.TOKEN_TASKVALUE_NON_NUMERICAL+ value;
						}
					}
				} else {
					continue;
				}
			}
			if (!flg) {
				isChange = false;
				valueSum = new BigDecimal(0);
				// 如果没有配置欠费情况，则返回
				if (overdueConfigMapList.isEmpty()) {
					return Contant.TOKEN_TASKVALUE_FAIL;
				}
			}
			// 如果还没正确提取到余额，并且配置了欠费情况，匹配欠费情况
			if (!overdueConfigMapList.isEmpty() && !isChange) {
				it = overdueConfigMapList.iterator();
				while (it.hasNext()) {
					// 获取配置信息
					configMap = (Map) it.next();
					String beforeKeywords = (String)configMap.get("beforekeywords");
					Log.d("", "beforekeywords:" + beforeKeywords);
					// 先判断是否包含前置关键字
					if (StringUtils.containsIgnoreCase(tempContent,beforeKeywords)) {
						String symbol = (String) configMap.get("symbol");
						value = parseNumber(configMap,beforeKeywords,tempContent);
						if (isNumericValue(value)) {
							// 减法操作
							if ("-".equals(symbol)) {
								valueSum = valueSum.subtract(new BigDecimal(value));
							}
							// 加法操作
							else if ("+".equals(symbol)) {
								valueSum = valueSum.add(new BigDecimal(value));
							}
							isChange = true;
						}
						// 如果提取的内容非数值，不用进行计算
						else {
							isChange = false;
							valueSum = new BigDecimal(0);
							// 如果没有配置欠费情况，则返回
							Log.w("","从页面提取的内容，不是数值！请查看配置和页面情况。");
							return Contant.TOKEN_TASKVALUE_NON_NUMERICAL+ value;
						}
					}
				}
			}
			// 如果是正确获取
			if (isChange) {
				// 精度为2位， 舍入规则为：四舍五入
				return valueSum.setScale(2, RoundingMode.HALF_UP).toPlainString();
			}
		}
		// 如果配置为空，则返回默认值
		return Contant.TOKEN_TASKVALUE_DEFAULT;
	}
	/**
	 * 依据配置信息从抓取的内容中，解析出数值
	 * @param configMap
	 * @param beforeKeywords
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private static String parseNumber(Map configMap, String beforeKeywords, String tempContent) {
		String value = "";
		String afterKeywords = (String) configMap.get("afterkeywords");
		// 判断备注里面是否配置了#beforekeywordsAppendLen=11#
		String note = (String) configMap.get("note");
		int addlen = 0;
		if (StringUtils.isNotEmpty(note)) {
			String reg = "#beforekeywordsAppendLen=[0-9]+#";
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(note.trim());
			String keyConifg = "";
			while (matcher.find()) {
				keyConifg = matcher.group();
			}
			if (StringUtils.isNotEmpty(keyConifg)) {
				// 获取到前关键字追加长度值"="
				String str=StringUtils.substring(keyConifg, keyConifg.toLowerCase().indexOf("=")+1, keyConifg.lastIndexOf("#"));
				addlen = Integer.parseInt(str);
			}
		}
		// 有可能有多个前关键字
		int count = StringUtils.countMatches(tempContent, beforeKeywords);
		// 逐个前关键字开始匹配
		for (int i = 1; i <= count; i++) {
			int beginIndex = StringUtils.ordinalIndexOf(tempContent,beforeKeywords,i) + beforeKeywords.length();
			int endIndex = beginIndex;
			// 如果配置了后关键字
			if (afterKeywords != null && !"".equals(afterKeywords)) {
				// 后关键字 必定 在前关键字后面
				if (StringUtils.containsIgnoreCase(StringUtils.substring(tempContent, beginIndex), afterKeywords)) {
					endIndex = beginIndex + StringUtils.substring(tempContent, beginIndex).indexOf(afterKeywords);
				} else {
					Log.w("", "页面找不到AFTERKEYWORDS：" + afterKeywords);
					return Contant.TOKEN_TASKVALUE_FAIL;
				}
			} else {
				// 如果配置了关键字长度
				int len = 0;
				if (isNumericValue(configMap.get("keywordslen").toString())) {
					len=(int)Double.parseDouble(configMap.get("keywordslen").toString());
				}
				if (configMap.get("keywordslen") != null && !"".equals(configMap.get("keywordslen")) && len > 1) {
					int keywordsLen = len;
					if (keywordsLen > 0) {
						// 判断是否为正整数
						if (Pattern.matches("^[1-9]\\d*$",Integer.valueOf(keywordsLen).toString())) {
							endIndex = beginIndex + keywordsLen;
							if (endIndex >= tempContent.length()) {
								Log.w("", "关键字长度KEYWORDSLEN：" + keywordsLen + "，过大！请核实。");
								return Contant.TOKEN_TASKVALUE_FAIL;
							}
						} else {
							Log.w("", "关键字长度KEYWORDSLEN：" + keywordsLen + "，配置有误");
							return Contant.TOKEN_TASKVALUE_FAIL;
						}
					}
				} else {
					Log.d("", "逐个字符识别...");
					while (endIndex < tempContent.length()
							&& ((tempContent.charAt(endIndex) >= '0' 
							&& tempContent.charAt(endIndex) <= '9')
							|| ('.' == tempContent.charAt(endIndex))
							|| ('-' == tempContent.charAt(endIndex))
							|| (' ' == tempContent.charAt(endIndex)) 
							|| ('\t' == tempContent.charAt(endIndex)))) {
						endIndex++;
					}
				}
			}
			// 截取字串，去除首尾空白
			value = tempContent.substring(beginIndex, endIndex).trim();
			// 防止 截取的字串中间 含有空格或者tab字符，例如“-1243.12 2010-12-20”将最终解析为“-1243.12”
			if (StringUtils.countMatches(value, " ") >= 1) {
				int index1st = value.indexOf(' ');
				value = value.substring(0, index1st);
			}
			if (StringUtils.countMatches(value, "\t") >= 1) {
				int index1st = value.indexOf('\t');
				value = value.substring(0, index1st);
			}
			// 防止 截取的字串末尾 含有多个“-”或者“.”，例如“-1243.12.3-.12”将最终解析为“-1243.12”
			if (StringUtils.countMatches(value, "-") > 1) {
				int index1st = value.indexOf('-');
				int index2ed = value.indexOf('-', index1st + 1);
				value = value.substring(0, index2ed);
			}
			if (StringUtils.countMatches(value, ".") > 1) {
				int index1st = value.indexOf('.');
				int index2ed = value.indexOf('.', index1st + 1);
				value = value.substring(0, index2ed);
			}
			if (isNumericValue(value)) {
				// 精确到小数点后2位
				BigDecimal valueString = new BigDecimal(value);
				value = valueString.setScale(2, RoundingMode.DOWN).toPlainString();
				Log.d("", "解析得value=" + value);
				// 跳出循环
				break;
			}
		}
		return value;
	}
	
	/**
	 * 判断截取的字串是否为数值
	 * @param value
	 * @return
	 */
	private static boolean isNumericValue(String value) {
		String regex = "^((-?[1-9]\\d*)|(-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0))|0)$";
		return Pattern.matches(regex, value);
	}

	/**
	 * 解析下载流量消耗短信
	 * @param smstaskstep
	 * @throws SmsException 
	 * @throws DbException 
	 */
	static Date currentsmsDate ;
	public static void decodeDownLoadSmsScript(Smstaskstep smstaskstep,	Context context,RespMsg respMsg) throws SmsException, DbException
	{
		
		// 发送
		if ("Send".equals(smstaskstep.getOperateType())) {
			Logger.v("正在执行发送短信操作...");
			currentsmsDate = new Date();
			String receivePhoneNo;
			String sendContent;
			// 判断短信内容不为空
			if (StringUtil.isEmpty(smstaskstep.getContent())) {
		
				throw new SmsException("发送短信内容为空,短信脚本配置有误！");
			}
			// 转义解析短信内容，把时间转成对应时间
			sendContent = DynamicVariable.replaceAllDynamicVariables(
					smstaskstep.getContent(),
					SystemUtils.getPhoneNo(context),
					SystemUtils.getIMEI(context), null);
			
			if (StringUtil.isEmpty(smstaskstep.getReceivephoneno())) {
	
				throw new SmsException("被叫号码为空，短信脚本配置有误！");
			}
			// 检查接收短信号码是类似 10086
			if (SystemUtils.isInteger(smstaskstep
					.getReceivephoneno())) {
				receivePhoneNo = smstaskstep.getReceivephoneno();
				MyApplication myApplication = (MyApplication) ((Service)context).getApplication();
				myApplication.setSmsInfo(null);
//				Intent intent = new Intent();
//				intent.setAction(SmsService.Action);
//				intent.putExtra("smsBiz", SmsService.sendMsg);
//				intent.putExtra("sendCode", sendContent);
//				intent.putExtra("number", receivePhoneNo);
//				context.sendBroadcast(intent);
				SmsSytemUtils.getInstance().sendSMS(receivePhoneNo, sendContent, context);

			}else if (smstaskstep.getReceivephoneno().toLowerCase().startsWith("reply")) {// 回复短信
				
			
					//查找上一条接收的短信
//					SMSInfo smsInfo = SystemUtils.getDbManage()
//							.selector(SMSInfo.class)
//							.where("type", "=", 1).orderBy("createTime", true)
//							.findFirst();
				   SMSInfo smsInfo = SmsSytemUtils.getInstance().getFirstSmsInfo(context);
//				   currentsmsTime = System.currentTimeMillis();
					//SystemUtils.getDbManage().close();
//					Intent intent = new Intent();
//					intent.setAction(SmsService.Action);
//					intent.putExtra("smsBiz", SmsService.sendMsg);
//					intent.putExtra("sendCode", smstaskstep.getContent());
//					intent.putExtra("number", smsInfo.getMobile());
//					context.sendBroadcast(intent);
					SmsSytemUtils.getInstance().sendSMS(smsInfo.getMobile(), smstaskstep.getContent(), context);
				
			} else {
				throw new SmsException("无法识别叫号码类别，短信脚本配置有误！");
			}
			

		}
		// 接收
		else if ("Receive".equals(smstaskstep.getOperateType())) {
			Logger.v("正在执行接收短信操作...");
			
			Long reckonByTime = smstaskstep.getTimeout() * 1000;
			Long currentTime = 0L;//当前时间计时

			Long currentsmsTime = System.currentTimeMillis();
	
			// 接收短信超时时间未配置
			if (smstaskstep.getTimeout() == null
					|| smstaskstep.getTimeout()<= 0) {
				throw new SmsException("操作时间无效，短信脚本配置有误");
			}
			// 关键字匹配
			if (StringUtils.isEmpty(smstaskstep.getKeywordConfig())) {
				throw new SmsException("关键字配置都为空，短信脚本配置有误！");
			}
		
			
			try {
				boolean isTimeOut = false;// 是否超时
				while (!isTimeOut) {
					currentTime+=1000;
					Thread.sleep(1000);
					MyApplication myApplication = (MyApplication) ((Service)context).getApplication();
					List<SMSInfo> smsInfo = SmsSytemUtils.getInstance().findSmsInfoAfterTime(context,currentsmsDate);
					if (null != smsInfo) 
					for (int i = 0; i < smsInfo.size(); i++) {
						if(smsInfo != null && smsInfo.get(i).getCreateTime().getTime()>currentsmsTime) {
							String content = smsInfo.get(i).getContent();
							respMsg.setResult(content);
							
							if (StringUtil.isEmpty(smstaskstep.getKeywordConfig())){//未配置关键字的话接收到短信直接当作成功
								isTimeOut = true;
								break;
							} 
							String[] keyWords = StringUtils.split(smstaskstep.getKeywordConfig(), ">>");
							for (String _keyWord : keyWords) {
								if (StringUtils.contains(content, _keyWord)) {//匹配到关键字立即退出函数
									return;
								}
							}
						}
					}
					
					if(currentTime < reckonByTime){
				    	continue;
				    } else {
				    	myApplication.setSmsInfo(null);
						throw new SmsException("未接收到预期短信或者接收短信超时");
				    }
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 暂停操作
		else if ("Pause".equalsIgnoreCase(smstaskstep.getOperateType())) {
			if(smstaskstep.getTimeout()==0)
			{
				throw new SmsException("暂停时间为空，短信脚本配置有误！");
			}
			//暂停时间
			Long pauseTime = smstaskstep.getTimeout()*1000;
			try {
				Thread.sleep(pauseTime);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}


	
	  /**
     * Role:获取短信的各种信息 <BR>
     * Date:2012-3-19 <BR>
     * 
     * @author CODYY)peijiangping
     */ 
    public SMSInfo getFirstSmsInfo(Context context) { 
    	  final String SMS_URI_ALL   = "content://sms/";     
    	    final String SMS_URI_INBOX = "content://sms/inbox";   
    	    final String SMS_URI_SEND  = "content://sms/sent";   
    	    final String SMS_URI_DRAFT = "content://sms/draft";   
    	       
    	    StringBuilder smsBuilder = new StringBuilder();   
    	       
    	    try{   
    	        ContentResolver cr = context.getContentResolver();   
    	        String[] projection = new String[]{"_id", "address", "person",    
    	                "body", "date", "type"};   
    	        Uri uri = Uri.parse(SMS_URI_ALL);   
    	        Cursor cur = cr.query(uri, projection, null, null, "date desc");   
    	  
    	        if (cur.moveToFirst()) {   
    	            String name;    
    	            String phoneNumber;          
    	            String smsbody;   
    	            String date;   
    	            String type;   
    	            
    	            int nameColumn = cur.getColumnIndex("person");   
    	            int phoneNumberColumn = cur.getColumnIndex("address");   
    	            int smsbodyColumn = cur.getColumnIndex("body");   
    	            int dateColumn = cur.getColumnIndex("date");   
    	            int typeColumn = cur.getColumnIndex("type");   
    	            
    	            do{   
    	                name = cur.getString(nameColumn);                
    	                phoneNumber = cur.getString(phoneNumberColumn);   
    	                smsbody = cur.getString(smsbodyColumn);   
    	                   
    	                SimpleDateFormat dateFormat = new SimpleDateFormat(   
    	                        "yyyy-MM-dd hh:mm:ss.SSS");   
    	                Date d = new Date(Long.parseLong(cur.getString(dateColumn)));   
    	                date = dateFormat.format(d);   
    	                   
    	                int typeId = cur.getInt(typeColumn);   
    	                if(typeId == 1){   
    	                    type = "接收";   
    	                    SMSInfo smsInfo = new SMSInfo();
    	                    smsInfo.setCreateTime(d);
    	                    smsInfo.setContent(smsbody);
    	                    smsInfo.setMobile(phoneNumber);
    	                    cur.close();
    	                    return smsInfo;
    	                } else if(typeId == 2){   
    	                    type = "发送";   
    	                } else {   
    	                    type = "";   
    	                }   
    	                
    	                smsBuilder.append("[");   
    	                smsBuilder.append(name+",");   
    	                smsBuilder.append(phoneNumber+",");   
    	                smsBuilder.append(smsbody+",");   
    	                smsBuilder.append(date+",");   
    	                smsBuilder.append(type);   
    	                smsBuilder.append("] ");   
    	                
    	                if(smsbody == null) smsbody = "";     
    	            }while(cur.moveToNext());   
    	        } else {   
    	            smsBuilder.append("no result!");   
    	        }   
    	            
    	        smsBuilder.append("getSmsInPhone has executed!");   
    	    } catch(SQLiteException ex) {   
    	        Log.d("SQLiteException in getSmsInPhone", ex.getMessage());   
    	    }
			return null;   
    } 
    
    
    

	/**
	 * 
	 * 获取时间之后的短信
	 */
	public List<SMSInfo> findSmsInfoAfterTime(Context context, Date afterTime) {
		final String SMS_URI_ALL = "content://sms/";
		final String SMS_URI_INBOX = "content://sms/inbox";
		final String SMS_URI_SEND = "content://sms/sent";
		final String SMS_URI_DRAFT = "content://sms/draft";

		StringBuilder smsBuilder = new StringBuilder();
		List<SMSInfo> list = new ArrayList<SMSInfo>();
		try {
			ContentResolver cr = context.getContentResolver();
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, null, null, "date desc");

			if (cur.moveToFirst()) {
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;

				int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");

				do {
					name = cur.getString(nameColumn);
					phoneNumber = cur.getString(phoneNumberColumn);
					smsbody = cur.getString(smsbodyColumn);

					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss.SSS");
					Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
					date = dateFormat.format(d);

					int typeId = cur.getInt(typeColumn);
					if (typeId == 1) {
						type = "接收";
						SMSInfo smsInfo = new SMSInfo();
						smsInfo.setCreateTime(d);
						smsInfo.setContent(smsbody);
						smsInfo.setMobile(phoneNumber);
						if (afterTime.getTime() > d.getTime()) // 没有最新
						{
							cur.close();
							return list;
						} else {
							list.add(smsInfo);
						}

					} else if (typeId == 2) {
						type = "发送";
					} else {
						type = "";
					}

					smsBuilder.append("[");
					smsBuilder.append(name + ",");
					smsBuilder.append(phoneNumber + ",");
					smsBuilder.append(smsbody + ",");
					smsBuilder.append(date + ",");
					smsBuilder.append(type);
					smsBuilder.append("] ");

					if (smsbody == null)
						smsbody = "";
				} while (cur.moveToNext());
			} else {
				smsBuilder.append("no result!");
			}

			smsBuilder.append("getSmsInPhone has executed!");
		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}
		return null;
	} 
	
	/**获取指定时间之间的接收的短信（不获取发送的短信）
	 * @param context
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<SMSInfo> findReceiveSmsInfoAfterTime(Context context, Date beginDate) {
		final String SMS_URI_ALL = "content://sms/";
		final String SMS_URI_INBOX = "content://sms/inbox";
		final String SMS_URI_SEND = "content://sms/sent";
		final String SMS_URI_DRAFT = "content://sms/draft";
		
		StringBuilder smsBuilder = new StringBuilder();
		List<SMSInfo> list = new ArrayList<SMSInfo>();
		try {
			ContentResolver cr = context.getContentResolver();
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, null, null, "date desc");
			
			if (cur.moveToFirst()) {
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type = "";
				
				int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");
				
				do {
					name = cur.getString(nameColumn);
					phoneNumber = cur.getString(phoneNumberColumn);
					smsbody = cur.getString(smsbodyColumn);
					
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
					date = dateFormat.format(d);
					
					int typeId = cur.getInt(typeColumn);
					if (typeId == 1) {
						type = "接收";
						SMSInfo smsInfo = new SMSInfo();
						smsInfo.setCreateTime(d);
						smsInfo.setContent(smsbody);
						smsInfo.setMobile(phoneNumber);
						if (beginDate.getTime() > d.getTime()) // 没有最新
						{
							cur.close();
							return list;
						} else {
							list.add(smsInfo);
						}
						
					}
					
					smsBuilder.append("[");
					smsBuilder.append(name + ",");
					smsBuilder.append(phoneNumber + ",");
					smsBuilder.append(smsbody + ",");
					smsBuilder.append(date + ",");
					smsBuilder.append(type);
					smsBuilder.append("] ");
					
					if (smsbody == null)
						smsbody = "";
				} while (cur.moveToNext());
			} else {
				smsBuilder.append("no result!");
			}
			
			smsBuilder.append("getSmsInPhone has executed!");
		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}
		return null;
	} 
} 



