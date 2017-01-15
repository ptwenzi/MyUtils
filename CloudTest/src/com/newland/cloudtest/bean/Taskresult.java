package com.newland.cloudtest.bean;

import org.xutils.db.annotation.Table;


/**
 * 任务结果信息 domain对象
 */

public class Taskresult {



	/**
	 * @fields strategyid 策略ID
	 */
	private Long strategyid;
	/**
	 * @fields groupid 调度组ID
	 */
	private java.lang.Long groupid;
	/**
	 * @fields tasksetid 任务集ID
	 */
	private java.lang.Long tasksetid;
	/**
	 * @fields tasksetinstancename 任务集实例名
	 */
	private java.lang.String tasksetinstancename;
	/**
	 * @fields taskid 任务ID
	 */
	private java.lang.Long taskid;
	/**
	 * @fields timertoken 跑表标识
	 */
	private java.lang.String timertoken;
	/**
	 * @fields timerstarttime 跑表开始时间
	 */
	private String timerstarttime;
	/**
	 * @fields timerendtime 跑表结束时间
	 */
	private String timerendtime;
	/**
	 * @fields taskstarttime 任务开始时间
	 */
	private String taskstarttime;
	/**
	 * @fields taskendtime 任务结束时间
	 */
	private String taskendtime;
	/**
	 * @fields wastetime 耗时（毫秒）
	 */
	private java.lang.Long wastetime;
	/**
	 * @fields callingno 主叫号码
	 */
	private java.lang.String callingno;
	/**
	 * @fields calledno 被叫号码
	 */
	private java.lang.String calledno;
	/**
	 * @fields balance1 余额信息1(可以做为专题业务充值前余额信息记录)
	 */
	private java.lang.String balance1;
	/**
	 * @fields balance2 余额信息2(可以做为专题业务充值后余额信息记录)
	 */
	private java.lang.String balance2;
	/**
	 * @fields channel 渠道（网厅，短信、彩信.......）
	 */
	private java.lang.String channel;
	/**
	 * @fields businesstype 业务类型（专题业务，日常业务）
	 */
	private java.lang.String businesstype;
	/**
	 * @fields resultcontent 测试结果内容，如果用错误，则记录错误信息，否则记录task_no_erro
	 */
	private java.lang.String resultcontent;
	/**
	 * @fields result 测试结果(成功，失败，系统繁忙....... 该信息与ErrorType中用户定义的信息一致)
	 */
	private java.lang.String result;
	/**
	 * @fields note 备注
	 */
	private java.lang.String note;
	/**
	 * @fields piclink 错误截图链接
	 */
	private java.lang.String piclink;
	/**
	 * @fields warningflag 告警产生标志[0 未产生告警 1产生告警]
	 */
	private java.lang.Long warningflag;
	/**
	 * @fields testsrl testsrl
	 */
	private java.lang.Long testsrl;
	/**
	 * @fields initiator 业务发起方
	 */
	private java.lang.String initiator;
	/**
	 * @fields inquirer 业务落地方
	 */
	private java.lang.String inquirer;
	/**
	 * @fields strategyRunTyp 任务运行类型 1.内网环境 2.云测环境
	 */
	private java.lang.Integer strategyRunType;
	
	
	private String resultDetail; //结果说明
	private String warnNo;//告警
	private String warnName;//告警名称

	
	
	public String getResultDetail() {
		return resultDetail;
	}
	public void setResultDetail(String resultDetail) {
		this.resultDetail = resultDetail;
	}
	public String getWarnNo() {
		return warnNo;
	}
	public void setWarnNo(String warnNo) {
		this.warnNo = warnNo;
	}
	public String getWarnName() {
		return warnName;
	}
	public void setWarnName(String warnName) {
		this.warnName = warnName;
	}
	public Long getStrategyid(){
		return this.strategyid;
	}
	public void setStrategyid(Long strategyid){
		this.strategyid = strategyid;
	}
	public java.lang.Long getGroupid(){
		return this.groupid;
	}
	public void setGroupid(java.lang.Long groupid){
		this.groupid = groupid;
	}
	public java.lang.Long getTasksetid(){
		return this.tasksetid;
	}
	public void setTasksetid(java.lang.Long tasksetid){
		this.tasksetid = tasksetid;
	}
	public java.lang.String getTasksetinstancename(){
		return this.tasksetinstancename;
	}
	public void setTasksetinstancename(java.lang.String tasksetinstancename){
		this.tasksetinstancename = tasksetinstancename;
	}
	public java.lang.Long getTaskid(){
		return this.taskid;
	}
	public void setTaskid(java.lang.Long taskid){
		this.taskid = taskid;
	}
	public java.lang.String getTimertoken(){
		return this.timertoken;
	}
	public void setTimertoken(String timertoken){
		this.timertoken = timertoken;
	}
	public String getTimerstarttime(){
		return this.timerstarttime;
	}
	public void setTimerstarttime(String timerstarttime){
		this.timerstarttime = timerstarttime;
	}
	public String getTimerendtime(){
		return this.timerendtime;
	}
	public void setTimerendtime(String timerendtime){
		this.timerendtime = timerendtime;
	}
	public String getTaskstarttime(){
		return this.taskstarttime;
	}
	public void setTaskstarttime(String taskstarttime){
		this.taskstarttime = taskstarttime;
	}
	public String getTaskendtime(){
		return this.taskendtime;
	}
	public void setTaskendtime(String taskendtime){
		this.taskendtime = taskendtime;
	}
	public java.lang.Long getWastetime(){
		return this.wastetime;
	}
	public void setWastetime(java.lang.Long wastetime){
		this.wastetime = wastetime;
	}
	public java.lang.String getCallingno(){
		return this.callingno;
	}
	public void setCallingno(java.lang.String callingno){
		this.callingno = callingno;
	}
	public java.lang.String getCalledno(){
		return this.calledno;
	}
	public void setCalledno(java.lang.String calledno){
		this.calledno = calledno;
	}
	public java.lang.String getBalance1(){
		return this.balance1;
	}
	public void setBalance1(java.lang.String balance1){
		this.balance1 = balance1;
	}
	public java.lang.String getBalance2(){
		return this.balance2;
	}
	public void setBalance2(java.lang.String balance2){
		this.balance2 = balance2;
	}
	public java.lang.String getChannel(){
		return this.channel;
	}
	public void setChannel(java.lang.String channel){
		this.channel = channel;
	}
	public java.lang.String getBusinesstype(){
		return this.businesstype;
	}
	public void setBusinesstype(java.lang.String businesstype){
		this.businesstype = businesstype;
	}
	public java.lang.String getResultcontent(){
		return this.resultcontent;
	}
	public void setResultcontent(java.lang.String resultcontent){
		this.resultcontent = resultcontent;
	}
	public java.lang.String getResult(){
		return this.result;
	}
	public void setResult(java.lang.String result){
		this.result = result;
	}
	public java.lang.String getNote(){
		return this.note;
	}
	public void setNote(java.lang.String note){
		this.note = note;
	}
	public java.lang.String getPiclink(){
		return this.piclink;
	}
	public void setPiclink(java.lang.String piclink){
		this.piclink = piclink;
	}
	public java.lang.Long getWarningflag(){
		return this.warningflag;
	}
	public void setWarningflag(java.lang.Long warningflag){
		this.warningflag = warningflag;
	}
	public java.lang.Long getTestsrl(){
		return this.testsrl;
	}
	public void setTestsrl(java.lang.Long testsrl){
		this.testsrl = testsrl;
	}
	public java.lang.String getInitiator(){
		return this.initiator;
	}
	public void setInitiator(java.lang.String initiator){
		this.initiator = initiator;
	}
	public java.lang.String getInquirer(){
		return this.inquirer;
	}
	public void setInquirer(java.lang.String inquirer){
		this.inquirer = inquirer;
	}
	public java.lang.Integer getStrategyRunType() {
		return strategyRunType;
	}
	public void setStrategyRunType(java.lang.Integer strategyRunType) {
		this.strategyRunType = strategyRunType;
	}



}
