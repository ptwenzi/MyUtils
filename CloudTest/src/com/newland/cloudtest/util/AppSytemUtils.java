package com.newland.cloudtest.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.ex.DbException;

import android.app.Service;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newland.cloudtest.MyApplication;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.BamNormalModelPara;
import com.newland.cloudtest.bean.GetKeyConfig;
import com.newland.cloudtest.bean.ImageUpload;
import com.newland.cloudtest.bean.MyXutilsRequestParams;
import com.newland.cloudtest.bean.QueryBamNormalModelRespContent;
import com.newland.cloudtest.bean.RequestData;
import com.newland.cloudtest.bean.SMSInfo;
import com.newland.cloudtest.bean.ScoketRequest;
import com.newland.cloudtest.bean.ScoketResponse;
import com.newland.cloudtest.exception.SmsException;
import com.orhanobut.logger.Logger;

/**
 * 调用cmd jar接口
 * 
 * @author TongLee
 *
 */
public class AppSytemUtils implements postVerifyCode {

	private AppSytemUtils() {
	}

	private static AppSytemUtils single = null;

	// 静态工厂方法
	public static AppSytemUtils getInstance() {
		if (single == null) {
			single = new AppSytemUtils();
		}
		return single;
	}

	public <T> void decodeAppScript(final BamNormalModelPara bamNormalModelPara,
			final BamNormalModelDetailPara bamNormalModelDetailPara, final Context context, List<GetKeyConfig> list,
			final QueryBamNormalModelRespContent qBamNormalModelRespContent, final int num, final int total) throws SmsException, DbException {

		TimerUtils timerUtils = new TimerUtils();

		if ("44".equals(bamNormalModelDetailPara.getOperateType()))// 图片验证码
		{
			FileHelper.deleteFile(Environment.getDataDirectory() + "/com.newland.cloudtest/element.png");
			FileHelper.deleteFile(Environment.getDataDirectory() + "/com.newland.cloudtest/screen.png");

			TimerUtils detailScriptTimer = new TimerUtils();
			detailScriptTimer.startTime();
			// 先发送操作对象
			ScoketRequest scoketRequest = new ScoketRequest();
			scoketRequest.setMethod("14");// 截图
			scoketRequest.setPostparam("element");
			scoketRequest.setTarget(bamNormalModelDetailPara.getPostParam());
			String result = connectScoket(JSON.toJSONString(scoketRequest));
			// 处理scoket返回擦做
			checkScoketReturn(qBamNormalModelRespContent, bamNormalModelPara, bamNormalModelDetailPara, result, context, detailScriptTimer,num,total);
			ScoketResponse scoketResponse = JSON.parseObject(result, ScoketResponse.class);
			
			if (scoketResponse != null) {
				String codeImg = scoketResponse.getDetail();
				// 开始上传
				ImageUpload imageUpload = new ImageUpload();
				imageUpload.setGroupid(qBamNormalModelRespContent.getGroupId());
				imageUpload.setStrategyid(qBamNormalModelRespContent.getStrategyId());
				imageUpload.setTaskid(Long.parseLong(bamNormalModelPara.getObjectId()));
				imageUpload.setTasksetid(qBamNormalModelRespContent.getTasksetId());
				imageUpload.setTasksetinstancename(qBamNormalModelRespContent.getTasksetInstanceName());
				imageUpload.setTestsrl(qBamNormalModelRespContent.getTestsrl());
				SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "verifyImgId", null);
				DownloadUtils.uploadImg(Contant.IP + "/bamVerify/upVerifyImg", codeImg, new CommonCallback<String>() {

					@Override
					public void onCancelled(CancelledException arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(Throwable arg0, boolean arg1) {
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "verifyImgId", null);
					}

					@Override
					public void onFinished() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(String result) {
						Logger.v("成功" + result);
						RequestData requestData = JSON.parseObject(result, RequestData.class);
						JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(requestData.getData()));
						String verifyImgId = jsonObject.getString("verifyImgId");
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "verifyImgId", verifyImgId);

					}

				}, imageUpload);

				boolean isGetCode = false;// 是否取到验证码
				Long reckonByTime = Contant.smsAndImgTimeOut;
				Long currentTime = 0L;// 当前时间计时

				while (!isGetCode) {

					String verifyImgId = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "verifyImgId");
					if (currentTime < reckonByTime) // 没超时
					{
						if (StringUtils.isNotEmpty(verifyImgId)) {
							isGetCode = true;
							break;
						}
					} else {
						detailScriptTimer.stopTime();
						bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
						bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
						bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());

						bamNormalModelDetailPara.setStatue(1);
						bamNormalModelDetailPara.setErrMsg("上传图片验证码超时");
						bamNormalModelDetailPara.setCreateTime(new Date());
						SystemUtils.getDbManage().save(bamNormalModelDetailPara);
						throw new SmsException("接收验证码短信超时");
					}

					try {
						Thread.sleep(1000 * 5);
						currentTime += 1000 * 5;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				isGetCode = false;// 是否取到验证码
				reckonByTime = Contant.smsAndImgTimeOut;
				currentTime = 0L;// 当前时间计时
				SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "verifyCode", null);
				while (!isGetCode) {

					String verifyCode = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "verifyCode");
					if (currentTime < reckonByTime) // 没超时
					{
						if (StringUtils.isNotEmpty(verifyCode)) {
							isGetCode = true;
							break;
						} else {
							postVeryfyCode(context, bamNormalModelDetailPara);
						}
					} else {
						detailScriptTimer.stopTime();
						bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
						bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
						bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());
						bamNormalModelDetailPara.setStatue(1);
						bamNormalModelDetailPara.setErrMsg("获取图片验证码超时");
						bamNormalModelDetailPara.setCreateTime(new Date());
						SystemUtils.getDbManage().save(bamNormalModelDetailPara);
						throw new SmsException("获取图片验证码超时");
					}

					try {
						Thread.sleep(1000 * 5);
						currentTime += 1000 * 5;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// 传递给scoket
				String verifyCode = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "verifyCode");
				bamNormalModelDetailPara.setVerify(verifyCode);
				// 先发送操作对象
				ScoketRequest scoketRequest2 = new ScoketRequest();
				scoketRequest2.setMethod("3");
				scoketRequest2.setPostparam(verifyCode);
				scoketRequest2.setTarget(bamNormalModelDetailPara.getTarget());
				String result2 = connectScoket(JSON.toJSONString(scoketRequest2));
				checkScoketReturn(qBamNormalModelRespContent, bamNormalModelPara, bamNormalModelDetailPara, result2, context, detailScriptTimer,num,total);

				try {
					detailScriptTimer.stopTime();
					bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
					bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
					bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());

					bamNormalModelDetailPara.setCreateTime(new Date());
					SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
				} catch (DbException e) {
					e.printStackTrace();
					throw new SmsException(e.getMessage());
				}
			}

		} else if ("45".equals(bamNormalModelDetailPara.getOperateType()))// 短信验证码
		{
			MyApplication myApplication = (MyApplication) ((Service) context).getApplication();
			myApplication.setSmsInfo(null);
			TimerUtils detailScriptTimer = new TimerUtils();
			detailScriptTimer.startTime();
			// 先发送操作对象
			String result = null;
			SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "smsverify", null); //清空本地缓存短信验证码变量
			Long currentappTime = System.currentTimeMillis();
			if (StringUtil.isNotEmpty(bamNormalModelDetailPara.getPostParam())) {//操作之不为空
				if (!bamNormalModelDetailPara.getPostParam().toLowerCase().equalsIgnoreCase("#sms#")){//如果有配置需要点击的元素或者坐标，先点击
					ScoketRequest scoketRequest = new ScoketRequest();
					if (bamNormalModelDetailPara.getPostParam().toLowerCase().contains("resourceid=")
							|| bamNormalModelDetailPara.getPostParam().toLowerCase().contains("text=")) {
						scoketRequest.setMethod(Contant.APPCodePush);
					} else {
						scoketRequest.setMethod(Contant.CLICK_POINT);
					}
					scoketRequest.setTarget(bamNormalModelDetailPara.getPostParam().replace("#sms#", ""));
					result = connectScoket(JSON.toJSONString(scoketRequest));
					checkScoketReturn(qBamNormalModelRespContent, bamNormalModelPara, bamNormalModelDetailPara, result, context, detailScriptTimer,num,total);
				}
				//有配置#sms#，表示需要从服务器获取短信验证码，否则从本机取短信验证码
				if (bamNormalModelDetailPara.getPostParam().toLowerCase().contains("#sms#")){
					getSmsVerifyFromServer(context, qBamNormalModelRespContent, bamNormalModelDetailPara, detailScriptTimer, currentappTime);
				} else {
					getSmsVerifyFromLocal(context, bamNormalModelDetailPara, list, detailScriptTimer, currentappTime);
				}
			} else {
				getSmsVerifyFromLocal(context, bamNormalModelDetailPara, list, detailScriptTimer, currentappTime);
			}
			
			String smsverify = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "smsverify");
			bamNormalModelDetailPara.setSmsVerify(smsverify);
			ScoketRequest scoketInputRequest = new ScoketRequest();
			if (bamNormalModelDetailPara.getTarget().toLowerCase().contains("resourceid")) {
				scoketInputRequest.setMethod(Contant.APPCodeInput);
			} else {
				scoketInputRequest.setMethod(Contant.INPUT_POINT);
			}

			bamNormalModelDetailPara.setPostParam(smsverify);
			scoketInputRequest.setTarget(bamNormalModelDetailPara.getTarget());
			scoketInputRequest.setPostparam(smsverify);
			String scoketInputResult = connectScoket(JSON.toJSONString(scoketInputRequest));
			checkScoketReturn(qBamNormalModelRespContent, bamNormalModelPara, bamNormalModelDetailPara, scoketInputResult, context,	detailScriptTimer,num,total);

			try {
				bamNormalModelDetailPara.setCreateTime(new Date());
				detailScriptTimer.stopTime();
				bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
				bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());
				SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
			} catch (DbException e) {
				e.printStackTrace();
				throw new SmsException(e.getMessage());
			}
		} else if ("47".equals(bamNormalModelDetailPara.getOperateType())) {// 图片比对
			final TimerUtils detailScriptTimer = new TimerUtils();
			detailScriptTimer.startTime();
			final String picUrl = bamNormalModelDetailPara.getTarget();
			if (StringUtil.isNotEmpty(picUrl)) {
				String result = picUrl.substring(picUrl.lastIndexOf('/') + 1);
				final File file = new File(Environment.getExternalStorageDirectory() + "/CloudTestCash/" + result);
				final File dir = new File(Environment.getExternalStorageDirectory() + "/CloudTestCash/");
				if (file.exists()) {
					// 存在
					// 处理业务
					ScoketRequest scoketRequest = new ScoketRequest();
					scoketRequest.setMethod(bamNormalModelDetailPara.getOperateType());
					scoketRequest.setTarget(file.getAbsolutePath());
					scoketRequest.setPostparam(bamNormalModelDetailPara.getPostParam());
					scoketRequest.setSuccstr1(bamNormalModelDetailPara.getSuccstr1());
					scoketRequest.setErrstr1(bamNormalModelDetailPara.getErrstr1());

					String result45 = connectScoket(JSON.toJSONString(scoketRequest));
					// 处理scoket返回擦做
					checkScoketReturn(qBamNormalModelRespContent, bamNormalModelPara, bamNormalModelDetailPara, result45, context, detailScriptTimer,num,total);
					try {
						bamNormalModelDetailPara.setCreateTime(new Date());
						detailScriptTimer.stopTime();
						bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
						bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
						bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());
						SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
					} catch (DbException e) {
						e.printStackTrace();
						throw new SmsException(e.getMessage());
					}

				} else {// 不存在就下载
					SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "downpicStatue", "");
					Looper.prepare();
					new Thread(){  
					      public void run(){  
					       try {  
					    	boolean isHasDown = false;
							isHasDown = DownloadUtils.getInstance().downloadfile(picUrl, file, dir);
					        Message msg = new Message();  
					        msg.what=0;  
					        msg.obj=isHasDown;  
					        handler.sendMessage(msg);  
					       } catch (Exception e) {  
					        Message msg = new Message();  
					        msg.what=-1;  
					        handler.sendMessage(msg);  
					          
					       }  
					      }  
					      private Handler handler = new Handler() {

					  		@Override
					  		public void handleMessage(Message msg) {
					  			if (msg.what == 0) {
					  				boolean isHasDown = (boolean) msg.obj;
					  				if (!isHasDown) {
					  					detailScriptTimer.stopTime();
					  					bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
					  					bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
					  					bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());

					  					bamNormalModelDetailPara.setStatue(1);
					  					bamNormalModelDetailPara.setCreateTime(new Date());
					  					bamNormalModelDetailPara.setErrMsg("对比图片下载失败");
					  					try {
											SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
										} catch (DbException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
					  					try {
											throw new SmsException("对比图片下载失败");
										} catch (SmsException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
					  				}

					  				// 处理业务
					  				ScoketRequest scoketRequest = new ScoketRequest();
					  				scoketRequest.setMethod(bamNormalModelDetailPara.getOperateType());
					  				scoketRequest.setTarget(file.getAbsolutePath());
					  				scoketRequest.setPostparam(bamNormalModelDetailPara.getPostParam());

					  				String result45 = connectScoket(JSON.toJSONString(scoketRequest));
					  				// 处理scoket返回擦做
					  				try {
										checkScoketReturn(qBamNormalModelRespContent, bamNormalModelPara, bamNormalModelDetailPara, result45, context, detailScriptTimer,num,total);
									} catch (DbException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (SmsException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
					  				try {
					  					bamNormalModelDetailPara.setCreateTime(new Date());
					  					detailScriptTimer.stopTime();
					  					bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
					  					bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
					  					bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());
					  					SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
					  				} catch (DbException e) {
					  					e.printStackTrace();
					  					try {
											throw new SmsException(e.getMessage());
										} catch (SmsException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
					  				}
					  			} else if (msg.what == -1) {

					  			}
					  		}

					  	};
					     }.start();  
					
					
				}

			}
		}

		else {// 正常脚本业务
			TimerUtils timerUtils2 = new TimerUtils();
			timerUtils2.startTime();
			ScoketRequest scoketRequest = new ScoketRequest();
			scoketRequest.setMethod(bamNormalModelDetailPara.getOperateType());
			scoketRequest.setTarget(bamNormalModelDetailPara.getTarget());
			scoketRequest.setPostparam(bamNormalModelDetailPara.getPostParam());
			scoketRequest.setSuccstr1(bamNormalModelDetailPara.getSuccstr1());
			scoketRequest.setErrstr1(bamNormalModelDetailPara.getErrstr1());
			String result = connectScoket(JSON.toJSONString(scoketRequest));
			checkScoketReturn(qBamNormalModelRespContent, bamNormalModelPara, bamNormalModelDetailPara, result, context, timerUtils2,num,total);

			try {
				// 获取上传图片
				if ("14".equals(bamNormalModelDetailPara.getOperateType())) {
					ScoketResponse scoketResponse = JSON.parseObject(result, ScoketResponse.class);
					String pic = scoketResponse.getDetail();
					if(scoketResponse!=null){
						// 开始上传
						ImageUpload imageUpload = new ImageUpload();
						imageUpload.setGroupid(qBamNormalModelRespContent.getGroupId());
						imageUpload.setStrategyid(qBamNormalModelRespContent.getStrategyId());
						imageUpload.setTaskid(Long.parseLong(bamNormalModelPara.getObjectId()));
						imageUpload.setTasksetid(qBamNormalModelRespContent.getTasksetId());
						imageUpload.setTasksetinstancename(qBamNormalModelRespContent.getTasksetInstanceName());
						imageUpload.setTestsrl(qBamNormalModelRespContent.getTestsrl());
						DownloadUtils.uploadImg(Contant.IP + "/NativeFile/upload", pic, new CommonCallback<String>() {

							@Override
							public void onCancelled(CancelledException arg0) {

							}

							@Override
							public void onError(Throwable arg0, boolean arg1) {
								SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "screenshotPicPath", null);
							}

							@Override
							public void onFinished() {

							}

							@Override
							public void onSuccess(String result) {
								Logger.v("成功" + result);
								JSONObject jsonObject = JSON.parseObject(result);

								SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "screenshotPicPath",
										jsonObject.getString("response"));
							}

						}, imageUpload);

						boolean isGetScreenshotPicPath = false;// 是否取到验证码
						Long reckonByTime = Contant.smsAndImgTimeOut;
						Long currentTime = 0L;// 当前时间计时
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "screenshotPicPath", null);
						while (!isGetScreenshotPicPath) {

							String screenshotPicPath = SharedPreferencesUtils.getConfigStr(context, Contant.cachName,
									"screenshotPicPath");
							if (currentTime < reckonByTime) // 没超时
							{
								if (StringUtils.isNotEmpty(screenshotPicPath)) {
									isGetScreenshotPicPath = true;
									timerUtils2.stopTime();
									if(scoketResponse!=null){
										bamNormalModelDetailPara.setBegintime(scoketResponse.getStrStartTime());
										bamNormalModelDetailPara.setEndtime(scoketResponse.getStrEndTime());
										bamNormalModelDetailPara.setWastetime(scoketResponse.getConsume());
										bamNormalModelDetailPara.setShotpic(screenshotPicPath);
									}else{
										bamNormalModelDetailPara.setBegintime(timerUtils2.getStartTimeStr());
										bamNormalModelDetailPara.setEndtime(timerUtils2.getEndTimeStr());
										bamNormalModelDetailPara.setWastetime(timerUtils2.getCurrentTime());
										bamNormalModelDetailPara.setShotpic(screenshotPicPath);
									}
									
									break;
								}

							} else {
								timerUtils2.stopTime();
								if(scoketResponse!=null){
									bamNormalModelDetailPara.setBegintime(scoketResponse.getStrStartTime());
									bamNormalModelDetailPara.setEndtime(scoketResponse.getStrEndTime());
									bamNormalModelDetailPara.setWastetime(scoketResponse.getConsume());
									bamNormalModelDetailPara.setShotpic(screenshotPicPath);
								}else{
									bamNormalModelDetailPara.setBegintime(timerUtils2.getStartTimeStr());
									bamNormalModelDetailPara.setEndtime(timerUtils2.getEndTimeStr());
									bamNormalModelDetailPara.setWastetime(timerUtils2.getCurrentTime());
									bamNormalModelDetailPara.setShotpic(screenshotPicPath);
								}
								bamNormalModelDetailPara.setStatue(1);
								bamNormalModelDetailPara.setErrMsg("上传截图超时");
								bamNormalModelDetailPara.setCreateTime(new Date());
								SystemUtils.getDbManage().save(bamNormalModelDetailPara);
								throw new SmsException("上传截图超时");
							}

							try {
								Thread.sleep(1000 * 5);
								currentTime += 1000 * 5;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					

				} else {

					timerUtils2.stopTime();
					ScoketResponse scoketResponse = JSON.parseObject(result, ScoketResponse.class);
					if(scoketResponse!=null){
						bamNormalModelDetailPara.setBegintime(scoketResponse.getStrStartTime());
						bamNormalModelDetailPara.setEndtime(scoketResponse.getStrEndTime());
						bamNormalModelDetailPara.setWastetime(scoketResponse.getConsume());
					}else{
						bamNormalModelDetailPara.setBegintime(timerUtils2.getStartTimeStr());
						bamNormalModelDetailPara.setEndtime(timerUtils2.getEndTimeStr());
						bamNormalModelDetailPara.setWastetime(timerUtils2.getCurrentTime());
					}
					
				}
                //lsc_记录详细的步骤。每次执行的步骤o
				SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
			} catch (DbException e) {
				e.printStackTrace();
				throw new SmsException(e.getMessage());
			}
		}
	}

	private void postVeryfyCode(final Context context, BamNormalModelDetailPara bamNormalModelDetailPara) {
		// 这时候循环等待人工输入图片验证码
		MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP + "/bamVerify/getVerifyCode");
		params.setCharset("utf-8");
		RequestData requestData = new RequestData();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("verifyId", SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "verifyImgId"));
		bamNormalModelDetailPara
				.setVerifyId(SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "verifyImgId"));
		requestData.setData(map);
		params.addBodyParameter("data", JSON.toJSONString(requestData));
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Logger.v(result);
				RequestData requestData = JSON.parseObject(result, RequestData.class);
				if (requestData.getData() != null) {
					JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(requestData.getData()));
					String verifyCode = jsonObject.getString("verifyCode");
					if (StringUtil.isNotEmpty(verifyCode))
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "verifyCode", verifyCode);
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {

				Logger.v(ex.getMessage());
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {

			}
		});
	}
	
	private void getSmsVerifyFromLocal(final Context context,BamNormalModelDetailPara bamNormalModelDetailPara, 
			List<GetKeyConfig> list, TimerUtils detailScriptTimer, Long currentappTime) throws DbException, SmsException{
		if (list == null && list.size() == 0){
			detailScriptTimer.stopTime();
			bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
			bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
			bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());
			bamNormalModelDetailPara.setCreateTime(new Date());
			bamNormalModelDetailPara.setErrMsg("未配置短信验证码提取规则(主)");
			SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
			throw new SmsException("未配置短信验证码提取规则(主)");

		}
		Long reckonByTime = Contant.smsAndImgTimeOut;
		Long currentTime = 0L;// 当前时间计时
		boolean isTimeOut = false;// 是否超时
		while (!isTimeOut) {
			currentTime += 1000;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			SMSInfo smsInfo = SmsSytemUtils.getInstance().getFirstSmsInfo(context);
			if (currentTime < reckonByTime) {
				if (null == smsInfo) {
					continue;
				}
				// 确认在计时前收到短信
				if (smsInfo.getCreateTime().getTime() > currentappTime) {
					String content = smsInfo.getContent();
					bamNormalModelDetailPara.setReturncontent(content);

					String value;
					try {
						value = KeyConfigUtil.getFinalStringValueByMain(content, list);
						if (StringUtil.isNotEmpty(value)) {
							SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "smsverify", value);
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} 
			} else {
				detailScriptTimer.stopTime();
				bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
				bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setErrMsg("接收验证码短信超时");
				bamNormalModelDetailPara.setCreateTime(new Date());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("接收验证码短信超时");
			}
		}
	}
	
	private void getSmsVerifyFromServer(final Context context, QueryBamNormalModelRespContent qBamNormalModelRespContent,
			BamNormalModelDetailPara bamNormalModelDetailPara, TimerUtils detailScriptTimer, Long currentappTime) throws DbException, SmsException{
		querySmsVerifyFromServer(context, qBamNormalModelRespContent, currentappTime);
		Long currentTime = 0L;// 当前时间计时
		boolean isGetCode = false;// 是否取到验证码
		Long reckonByTime = Contant.smsAndImgTimeOut;
	    try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    currentTime += 1000 * 10;
		while (!isGetCode) {
			String smsverify = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "smsverify");
			if (currentTime < reckonByTime) // 没超时
			{
				if (StringUtils.isNotEmpty(smsverify)) {
					isGetCode = true;
					break;
				} else {
					querySmsVerifyFromServer(context, qBamNormalModelRespContent, currentappTime);
				}
			} else {
				detailScriptTimer.stopTime();
				bamNormalModelDetailPara.setBegintime(detailScriptTimer.getStartTimeStr());
				bamNormalModelDetailPara.setEndtime(detailScriptTimer.getEndTimeStr());
				bamNormalModelDetailPara.setWastetime(detailScriptTimer.getCurrentTime());
				bamNormalModelDetailPara.setStatue(1);
				bamNormalModelDetailPara.setErrMsg("获取短信验证码超时");
				bamNormalModelDetailPara.setCreateTime(new Date());
				SystemUtils.getDbManage().save(bamNormalModelDetailPara);
				throw new SmsException("获取短信验证码超时");
			}

			try {
				Thread.sleep(1000 * 10);
				currentTime += 1000 * 10;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void querySmsVerifyFromServer(final Context context, QueryBamNormalModelRespContent qBamNormalModelRespContent,
			Long currentappTime) {
		// 这时候循环等待人工输入图片验证码
		MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP + "/sms/getSmsVerify");
		params.setCharset("utf-8");
		RequestData requestData = new RequestData();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phoneno", qBamNormalModelRespContent.getPhoneNo());
		map.put("begintime", currentappTime);//
		BamNormalModelPara bamNormalModel = qBamNormalModelRespContent.getBamNormalModel();
		map.put("carrier", bamNormalModel.getCarrier());
		map.put("province", bamNormalModel.getProvince());
		map.put("brand", bamNormalModel.getBrand());
		requestData.setData(map);
		params.addBodyParameter("data", JSON.toJSONString(requestData));
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Logger.v(result);
				RequestData requestData = JSON.parseObject(result, RequestData.class);
				if (requestData.getData() != null) {
					JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(requestData.getData()));
					String smsverify = jsonObject.getString("sms");
					if (StringUtil.isNotEmpty(smsverify))
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "smsverify", smsverify);
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {

				Logger.v(ex.getMessage());
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {

			}
		});
	}

	/**
	 * 检查scoket返回
	 * 
	 * @param bamNormalModelDetailPara
	 * @param result
	 * @param timerUtils2
	 * @throws DbException
	 * @throws SmsException
	 */
	private void checkScoketReturn(QueryBamNormalModelRespContent qBamNormalModelRespContent, BamNormalModelPara bamNormalModelPara, final BamNormalModelDetailPara bamNormalModelDetailPara, String result,
			Context context, TimerUtils timerUtils2,int num,int total) throws DbException, SmsException {
		if ("timeOut".equals(result)) {
			timerUtils2.stopTime();
			bamNormalModelDetailPara.setBegintime(timerUtils2.getStartTimeStr());
			bamNormalModelDetailPara.setEndtime(timerUtils2.getEndTimeStr());
			bamNormalModelDetailPara.setWastetime(timerUtils2.getCurrentTime());
			bamNormalModelDetailPara.setStatue(1);
			bamNormalModelDetailPara.setErrMsg("scoket连接超时");
			bamNormalModelDetailPara.setCreateTime(new Date());
			SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
			throw new SmsException("scoket连接超时");
		} else if ("ScoketError".equals(result)) {
			timerUtils2.stopTime();
			bamNormalModelDetailPara.setBegintime(timerUtils2.getStartTimeStr());
			bamNormalModelDetailPara.setEndtime(timerUtils2.getEndTimeStr());
			bamNormalModelDetailPara.setWastetime(timerUtils2.getCurrentTime());
			bamNormalModelDetailPara.setStatue(1);
			bamNormalModelDetailPara.setErrMsg("连接不上scoket");
			bamNormalModelDetailPara.setCreateTime(new Date());
			SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
			throw new SmsException("连接不上scoket");
		} else {
			ScoketResponse scoketResponse = JSON.parseObject(result, ScoketResponse.class);
			if (scoketResponse != null) {
				bamNormalModelDetailPara.setStatue(scoketResponse.getCode());
				bamNormalModelDetailPara.setCreateTime(new Date());
				bamNormalModelDetailPara.setReturncontent(scoketResponse.getDetail());
				bamNormalModelDetailPara.setDescribe(scoketResponse.getDescribe());
				bamNormalModelDetailPara.setMethodType(scoketResponse.getMethod_name());
				//lsc_没有配置时间戳
				if ("0".equals(bamNormalModelDetailPara.getTokenType())){
					String begin = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "BeginTimer");
					String end = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "EndTimer");
					if(num == (total-1) && StringUtils.isEmpty(end)&&StringUtils.isEmpty(begin)){
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "BeginTimer",
								DateUtil.getDateNowStringWithmi(scoketResponse.getStartTime()));
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "EndTimer",
								DateUtil.getDateNowStringWithmi(scoketResponse.getEndTime()));
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						Date d1;
						Date d2;
						try {
							d1 = df.parse(DateUtil.getDateNowStringWithmi(scoketResponse.getEndTime())); // 结束时间
							d2 = df.parse(DateUtil.getDateNowStringWithmi(scoketResponse.getStartTime()));
							long diff = (d1.getTime() - d2.getTime());
							SharedPreferencesUtils.setConfigLong(context, Contant.cachName, "Wastetime", diff);
							SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "timertoken",
									bamNormalModelDetailPara.getTimerToken());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
					}
					
					
				}else{
					if ("1".equals(bamNormalModelDetailPara.getTokenType()))// 时间戳起点
					{
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "BeginTimer",
								DateUtil.getDateNowStringWithmi(scoketResponse.getStartTime()));
					} else if ("2".equals(bamNormalModelDetailPara.getTokenType()))// 时间戳终点
					{
						String BeginTimer = SharedPreferencesUtils.getConfigStr(context, Contant.cachName, "BeginTimer");
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						Date d1;
						Date d2;
						try {
							d1 = df.parse(DateUtil.getDateNowStringWithmi(scoketResponse.getEndTime()));
							d2 = df.parse(BeginTimer);
							long diff = (d1.getTime() - d2.getTime());
							SharedPreferencesUtils.setConfigLong(context, Contant.cachName, "Wastetime", diff);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "timertoken",
								bamNormalModelDetailPara.getTimerToken());
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "EndTimer",
								DateUtil.getDateNowStringWithmi(scoketResponse.getEndTime()));
					} else if ("3".equals(bamNormalModelDetailPara.getTokenType()))// 时间戳起点+时间戳终点
					{
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "BeginTimer",
								DateUtil.getDateNowStringWithmi(scoketResponse.getStartTime()));
						SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "EndTimer",
								DateUtil.getDateNowStringWithmi(scoketResponse.getEndTime()));
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						Date d1;
						Date d2;
						try {
							d1 = df.parse(DateUtil.getDateNowStringWithmi(scoketResponse.getEndTime())); // 结束时间
							d2 = df.parse(DateUtil.getDateNowStringWithmi(scoketResponse.getStartTime()));
							long diff = (d1.getTime() - d2.getTime());
							SharedPreferencesUtils.setConfigLong(context, Contant.cachName, "Wastetime", diff);
							SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "timertoken",
									bamNormalModelDetailPara.getTimerToken());
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				
				if (scoketResponse.getCode() != 0) // 没成功
				{
					try{
						if (!"14".equalsIgnoreCase(bamNormalModelDetailPara.getOperateType())){
							errShot(bamNormalModelPara, qBamNormalModelRespContent, bamNormalModelDetailPara, context, num, total);
						}
						
					} catch(Exception e){
						e.printStackTrace();
					}
					timerUtils2.stopTime();
					bamNormalModelDetailPara.setBegintime(timerUtils2.getStartTimeStr());
					bamNormalModelDetailPara.setEndtime(timerUtils2.getEndTimeStr());
					bamNormalModelDetailPara.setWastetime(timerUtils2.getCurrentTime());
					bamNormalModelDetailPara.setStatue(1);
					bamNormalModelDetailPara.setReturncontent(scoketResponse.getDetail());
					bamNormalModelDetailPara.setDescribe(scoketResponse.getDescribe());
					bamNormalModelDetailPara.setErrMsg(scoketResponse.getDescribe());
					bamNormalModelDetailPara.setCreateTime(new Date());

					SystemUtils.getDbManage().saveOrUpdate(bamNormalModelDetailPara);
					throw new SmsException(scoketResponse.getDescribe());
				}
			}
		}
	}

	/**
	 * 链接scoket
	 * 
	 * @param scoketRequest
	 * @return
	 */
	public String connectScoket(String scoketRequest) {
		try {

			Socket sender = new Socket("localhost", 8908);
			// sender.setSoTimeout(Contant.scoketTimeOut);
			// SocketAddress addr = new
			// SocketAddress(SOCKET_ADDRESS,LocalSocketAddress.Namespace.FILESYSTEM);
			// sender.connect(addr);
			// String request =
			// "{\"method\":\"15\", \"target\":\"home\", \"postparam\":\"\",
			// \"succstr1\":\"\", \"errstr1\":\"\"}";
			sender.getOutputStream().write(scoketRequest.getBytes());
			StringBuilder recvStrBuilder = new StringBuilder();
			InputStream inputStream = null;

			inputStream = sender.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			char[] buf = new char[4096];
			int readBytes = -1;
			while ((readBytes = inputStreamReader.read(buf)) != -1) {
				String tempStr = new String(buf, 0, readBytes);
				recvStrBuilder.append(tempStr);
			}
			sender.close();
			Logger.v(recvStrBuilder.toString());
			return recvStrBuilder.toString();

		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return "timeOut";
		}

		catch (IOException e) {
			e.printStackTrace();
			return "ScoketError";
		}

	}

	public boolean installApp(String path) {
		
		Process process = null;
		OutputStream out = null;
		InputStream in = null;

		try {
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();

			out.write(("pm install -r " + path + "\n").getBytes());
			in = process.getInputStream();
			int len = 0;
			byte[] bs = new byte[256];

			while (-1 != (len = in.read(bs))) {
				String state = new String(bs, 0, len);
				if (state.equals("Success\n")) {

					return true;

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return false;

	}

	private void errShot(BamNormalModelPara bamNormalModelPara, QueryBamNormalModelRespContent qBamNormalModelRespContent, 
			final BamNormalModelDetailPara bamNormalModelDetailPara, final Context context, int num,int total) throws DbException, SmsException{
		ScoketRequest scoketRequest = new ScoketRequest();
		scoketRequest.setMethod("14");
		String result = connectScoket(JSON.toJSONString(scoketRequest));
		if ("timeOut".equals(result) || "ScoketError".equals(result)) {
			return;
		}
		ScoketResponse scoketResponse = JSON.parseObject(result, ScoketResponse.class);
		if (null == scoketResponse || 0 != scoketResponse.getCode()){
			return;
		}
		String pic = scoketResponse.getDetail();
		
		// 开始上传
		ImageUpload imageUpload = new ImageUpload();
		imageUpload.setGroupid(qBamNormalModelRespContent.getGroupId());
		imageUpload.setStrategyid(qBamNormalModelRespContent.getStrategyId());
		imageUpload.setTaskid(Long.parseLong(bamNormalModelPara.getObjectId()));
		imageUpload.setTasksetid(qBamNormalModelRespContent.getTasksetId());
		imageUpload.setTasksetinstancename(qBamNormalModelRespContent.getTasksetInstanceName());
		imageUpload.setTestsrl(qBamNormalModelRespContent.getTestsrl());
		//截图前先清空缓存的截图路径
		SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "screenshotPicPath", null);
		DownloadUtils.uploadImg(Contant.IP + "/NativeFile/upload", pic, new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "screenshotPicPath", null);
			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onSuccess(String result) {
				Logger.v("成功" + result);
				JSONObject jsonObject = JSON.parseObject(result);

				SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "screenshotPicPath",
						jsonObject.getString("response"));
			}

		}, imageUpload);

		boolean isGetScreenshotPicPath = false;// 是否取到验证码
		Long reckonByTime = Contant.smsAndImgTimeOut;
		Long currentTime = 0L;// 当前时间计时
		SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "screenshotPicPath", null);
		while (!isGetScreenshotPicPath) {
			String screenshotPicPath = SharedPreferencesUtils.getConfigStr(context, Contant.cachName,"screenshotPicPath");
			if (currentTime >= reckonByTime) {
				break;
			}
			
			if (StringUtils.isNotEmpty(screenshotPicPath)) {
				isGetScreenshotPicPath = true;
				bamNormalModelDetailPara.setShotpic(screenshotPicPath);
				
				break;
			}

			try {
				Thread.sleep(1000 * 5);
				currentTime += 1000 * 5;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
