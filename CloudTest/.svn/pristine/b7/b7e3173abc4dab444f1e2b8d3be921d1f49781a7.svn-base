package com.newland.cloudtest.bean;

import java.util.Date;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "bamNormalModelDetailPara")
public class BamNormalModelDetailPara {
    @Column(name="id",isId=true,autoGen=true)  
   private int id;  

	@Column(name = "qbmId")
	private String qbmId ;
	@Column(name = "createTime")
	private Date createTime;
	
	@Column(name = "stepNo")
	private Double stepNo; //(orderId) 操作步骤
	@Column(name = "operateType")
	private String operateType;//(method) 操作类型
	@Column(name = "timerToken")
	private String timerToken; //跑表标志(时间戳)
	
	//sms
	@Column(name = "sendPhoneNo")
	private String sendPhoneNo; //发送短信手机号
	@Column(name = "receivePhoneNo")
	private String receivePhoneNo; //接收短信手机号
	@Column(name = "content")
	private String content; //发送内容
	@Column(name = "timeout")
	private String timeout; //操作时间，单位（秒）
	@Column(name = "sendMax")
	private String sendMax; //操作次数
	@Column(name = "keywordConfig")
	private String keywordConfig; //关键字配置
	@Column(name = "timeConfig")
	private String timeConfig; //时间配置
	@Column(name = "retry")
	private String retry; //失败是否重试("yes","no")
	@Column(name = "runAnyway")
	private String runAnyway; //失败是否继续("yes","no")
	@Column(name = "note")
	private String note; //备注
	
	//other
	@Column(name = "stepType")
	private String stepType;
	@Column(name = "describe")
	private String describe;
	@Column(name = "target")
	private String target;
	@Column(name = "postParam")
	private String postParam;
	@Column(name = "referer")
	private String referer;
	@Column(name = "succstr1")
	private String succstr1; //成功关键字
	@Column(name = "succstr2")
	private String succstr2; //成功关键字
	@Column(name = "succstr3")
	private String succstr3; //成功关键字
	@Column(name = "errstr1")
	private String errstr1; //失败关键字
	@Column(name = "errstr2")
	private String errstr2; //失败关键字
	@Column(name = "enabled")
	private String enabled; //失败关键字
	@Column(name = "verify")
	private String verify;
	@Column(name = "verifyId")
	private String verifyId;
	@Column(name = "smsVerify")
	private String smsVerify;
	@Column(name = "utf8encode")
	private String utf8encode;
	@Column(name = "failRetry")
	private String failRetry; //0：不重试；1：从起始位置开始重试；2：从当前步骤重试；（默认为0）
	@Column(name = "retryCount")
	private String retryCount; //重试次数（备用，暂未使用），默认为1
	@Column(name = "checkPoint")
	private String checkPoint;
	@Column(name = "tokenType")
	private String tokenType; //0 --时间戳类型,对应dict_bam中dict_type=8的项
	@Column(name = "methodType")
	private String methodType; //对于method为get、post的步骤，0：为http步骤；1：为wap步骤（默认为0）
	@Column(name = "carrierType")
	private String carrierType; //语音步骤被叫号码归属运营商1：移动；2：联通；3：电信（默认为0）
	@Column(name = "sleepTime")
	private String sleepTime; //步骤休眠时间，单位分钟，给增值业务72小时业务使用（默认为0）
	@Column(name = "buyfee")
	private String buyfee; //订购业务费用（默认为0）
	@Column(name = "stepfun")
	private String stepfun; //从dict_bam匹配dict_type=22的记录，可以多选
	@Column(name = "urlGetType")
	private String urlGetType; //wap探测url获取方式：0：配置；1：从上步获取（默认为0）
	@Column(name = "recordType")
	private String recordType; //录音类型 0不录音 1拨号后录音 2拨通后录音 3根据二次拨号串进行录音
	@Column(name = "recordBegin")
	private String recordBegin; //录音开始时间点，单位：秒
	@Column(name = "recordDuration")
	private String recordDuration; //录音时长，0录音到通化结束
	@Column(name = "speech")
	private String speech; //是否需要语音识别，1需要语音识别
	@Column(name = "play")
	private String play; //是否放音
	@Column(name = "playFile")
	private String playFile; //放音文件
	@Column(name = "callDuration")
	private String callDuration; //拨打时长
	
	@Column(name = "statue")
	private int statue = 1;//0成功  1失败
	@Column(name = "errMsg")
	private String errMsg;//失败日志
	@Column(name = "returncontent")
	private String returncontent;//返回短信内容
	
	/**
	 * 以下用于记录
	 * @return
	 */
	@Column(name = "begintime")
	private String begintime;
	@Column(name = "endtime")
	private String endtime;
	@Column(name = "wastetime")
	private Long wastetime;
	
	
	@Column(name = "shotpic")
	private java.lang.String shotpic;
	
	
	
	public java.lang.String getShotpic() {
		return shotpic;
	}
	public void setShotpic(java.lang.String shotpic) {
		this.shotpic = shotpic;
	}
	public String getVerifyId() {
		return verifyId;
	}
	public void setVerifyId(String verifyId) {
		this.verifyId = verifyId;
	}
	public String getBegintime() {
		return begintime;
	}
	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public Long getWastetime() {
		return wastetime;
	}
	public void setWastetime(Long wastetime) {
		this.wastetime = wastetime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getQbmId() {
		return qbmId;
	}
	public void setQbmId(String qbmId) {
		this.qbmId = qbmId;
	}
	public String getReturncontent() {
		return returncontent;
	}
	public void setReturncontent(String returncontent) {
		this.returncontent = returncontent;
	}
	public int getStatue() {
		return statue;
	}
	public void setStatue(int statue) {
		this.statue = statue;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public Double getStepNo() {
		return stepNo;
	}
	public void setStepNo(Double stepNo) {
		this.stepNo = stepNo;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public String getTimerToken() {
		return timerToken;
	}
	public void setTimerToken(String timerToken) {
		this.timerToken = timerToken;
	}
	public String getSendPhoneNo() {
		return sendPhoneNo;
	}
	public void setSendPhoneNo(String sendPhoneNo) {
		this.sendPhoneNo = sendPhoneNo;
	}
	public String getReceivePhoneNo() {
		return receivePhoneNo;
	}
	public void setReceivePhoneNo(String receivePhoneNo) {
		this.receivePhoneNo = receivePhoneNo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	public String getSendMax() {
		return sendMax;
	}
	public void setSendMax(String sendMax) {
		this.sendMax = sendMax;
	}
	public String getKeywordConfig() {
		return keywordConfig;
	}
	public void setKeywordConfig(String keywordConfig) {
		this.keywordConfig = keywordConfig;
	}
	public String getTimeConfig() {
		return timeConfig;
	}
	public void setTimeConfig(String timeConfig) {
		this.timeConfig = timeConfig;
	}
	public String getRetry() {
		return retry;
	}
	public void setRetry(String retry) {
		this.retry = retry;
	}
	public String getRunAnyway() {
		return runAnyway;
	}
	public void setRunAnyway(String runAnyway) {
		this.runAnyway = runAnyway;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getStepType() {
		return stepType;
	}
	public void setStepType(String stepType) {
		this.stepType = stepType;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getPostParam() {
		return postParam;
	}
	public void setPostParam(String postParam) {
		this.postParam = postParam;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public String getSuccstr1() {
		return succstr1;
	}
	public void setSuccstr1(String succstr1) {
		this.succstr1 = succstr1;
	}
	public String getSuccstr2() {
		return succstr2;
	}
	public void setSuccstr2(String succstr2) {
		this.succstr2 = succstr2;
	}
	public String getSuccstr3() {
		return succstr3;
	}
	public void setSuccstr3(String succstr3) {
		this.succstr3 = succstr3;
	}
	public String getErrstr1() {
		return errstr1;
	}
	public void setErrstr1(String errstr1) {
		this.errstr1 = errstr1;
	}
	public String getErrstr2() {
		return errstr2;
	}
	public void setErrstr2(String errstr2) {
		this.errstr2 = errstr2;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public String getVerify() {
		return verify;
	}
	public void setVerify(String verify) {
		this.verify = verify;
	}
	public String getSmsVerify() {
		return smsVerify;
	}
	public void setSmsVerify(String smsVerify) {
		this.smsVerify = smsVerify;
	}
	public String getUtf8encode() {
		return utf8encode;
	}
	public void setUtf8encode(String utf8encode) {
		this.utf8encode = utf8encode;
	}
	public String getFailRetry() {
		return failRetry;
	}
	public void setFailRetry(String failRetry) {
		this.failRetry = failRetry;
	}
	public String getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(String retryCount) {
		this.retryCount = retryCount;
	}
	public String getCheckPoint() {
		return checkPoint;
	}
	public void setCheckPoint(String checkPoint) {
		this.checkPoint = checkPoint;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	public String getCarrierType() {
		return carrierType;
	}
	public void setCarrierType(String carrierType) {
		this.carrierType = carrierType;
	}
	public String getSleepTime() {
		return sleepTime;
	}
	public void setSleepTime(String sleepTime) {
		this.sleepTime = sleepTime;
	}
	public String getBuyfee() {
		return buyfee;
	}
	public void setBuyfee(String buyfee) {
		this.buyfee = buyfee;
	}
	public String getStepfun() {
		return stepfun;
	}
	public void setStepfun(String stepfun) {
		this.stepfun = stepfun;
	}
	public String getUrlGetType() {
		return urlGetType;
	}
	public void setUrlGetType(String urlGetType) {
		this.urlGetType = urlGetType;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getRecordBegin() {
		return recordBegin;
	}
	public void setRecordBegin(String recordBegin) {
		this.recordBegin = recordBegin;
	}
	public String getRecordDuration() {
		return recordDuration;
	}
	public void setRecordDuration(String recordDuration) {
		this.recordDuration = recordDuration;
	}
	public String getSpeech() {
		return speech;
	}
	public void setSpeech(String speech) {
		this.speech = speech;
	}
	public String getPlay() {
		return play;
	}
	public void setPlay(String play) {
		this.play = play;
	}
	public String getPlayFile() {
		return playFile;
	}
	public void setPlayFile(String playFile) {
		this.playFile = playFile;
	}
	public String getCallDuration() {
		return callDuration;
	}
	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}
}
