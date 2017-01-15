package com.newland.mobileterminalmonitor.bean;

import java.io.Serializable;

/**
 * 表：真机非测试任务表，Model对象
 * @author Jack.Huang
 * @version 1.0
 * @since 1.0
 */
public class PhoneTask implements Serializable{

	private static final long serialVersionUID = 1L;

	//构造函数
	public PhoneTask(){
		
	}
	
	//属性 begin
	/*
	 * taskId
	 */
	private java.lang.Long taskId;
	/*
	 * 执行机器串号（null：默认全部终端配置一致，非空：某台终端）
	 */
	private java.lang.String imeiNumber;
	/*
	 * 下发探测终端IP
	 */
	private java.lang.String computerIp;
	/*
	 * 任务指令(100:终端监控时间间隔配置,200:apk安装,201:apk卸载,300:设置网络,400:定时重启)
	 */
	private java.lang.Integer command;
	/*
	 * 任务参数
	 */
	private java.lang.String taskParam1;
	/*
	 * 任务参数2
	 */
	private java.lang.String taskParam2;
	/*
	 * 任务参数3
	 */
	private java.lang.String taskParam3;
	/*
	 * 执行状态(0.未执行，1.执行中，2.已执行)
	 */
	private java.lang.Integer status;
	/*
	 * 任务下发时间
	 */
	private java.util.Date createTime;
	/*
	 * 执行时间
	 */
	private java.util.Date beginTime;
	/*
	 * 结束时间
	 */
	private java.util.Date endTime;
	/*
	 * 执行结果 （1.成功，2.失败）
	 */
	private java.lang.Integer taskResult;
	/*
	 * 说明
	 */
	private java.lang.String taskDesc;
	//属性 end
	
	//方法 begin
	/*
	 * 获取 taskId
	 */
	public java.lang.Long getTaskId(){
		return this.taskId;
	}
	/*
	 * 设置 taskId
	 */
	public void setTaskId(java.lang.Long taskId){
		this.taskId = taskId;
	}
	/*
	 * 获取 执行机器串号（null：默认全部终端配置一致，非空：某台终端）
	 */
	public java.lang.String getImeiNumber(){
		return this.imeiNumber;
	}
	/*
	 * 设置 执行机器串号（null：默认全部终端配置一致，非空：某台终端）
	 */
	public void setImeiNumber(java.lang.String imeiNumber){
		this.imeiNumber = imeiNumber;
	}
	/*
	 * 获取 下发探测终端IP
	 */
	public java.lang.String getComputerIp(){
		return this.computerIp;
	}
	/*
	 * 设置 下发探测终端IP
	 */
	public void setComputerIp(java.lang.String computerIp){
		this.computerIp = computerIp;
	}
	/*
	 * 获取 任务指令(100:终端监控时间间隔配置,200:apk安装,201:apk卸载,300:设置网络,400:定时重启)
	 */
	public java.lang.Integer getCommand(){
		return this.command;
	}
	/*
	 * 设置 任务指令(100:终端监控时间间隔配置,200:apk安装,201:apk卸载,300:设置网络,400:定时重启)
	 */
	public void setCommand(java.lang.Integer command){
		this.command = command;
	}
	/*
	 * 获取 任务参数
	 */
	public java.lang.String getTaskParam1(){
		return this.taskParam1;
	}
	/*
	 * 设置 任务参数
	 */
	public void setTaskParam1(java.lang.String taskParam1){
		this.taskParam1 = taskParam1;
	}
	/*
	 * 获取 任务参数2
	 */
	public java.lang.String getTaskParam2(){
		return this.taskParam2;
	}
	/*
	 * 设置 任务参数2
	 */
	public void setTaskParam2(java.lang.String taskParam2){
		this.taskParam2 = taskParam2;
	}
	/*
	 * 获取 任务参数3
	 */
	public java.lang.String getTaskParam3(){
		return this.taskParam3;
	}
	/*
	 * 设置 任务参数3
	 */
	public void setTaskParam3(java.lang.String taskParam3){
		this.taskParam3 = taskParam3;
	}
	/*
	 * 获取 执行状态(0.未执行，1.执行中，2.已执行)
	 */
	public java.lang.Integer getStatus(){
		return this.status;
	}
	/*
	 * 设置 执行状态(0.未执行，1.执行中，2.已执行)
	 */
	public void setStatus(java.lang.Integer status){
		this.status = status;
	}
	/*
	 * 获取 任务下发时间
	 */
	public java.util.Date getCreateTime(){
		return this.createTime;
	}
	/*
	 * 设置 任务下发时间
	 */
	public void setCreateTime(java.util.Date createTime){
		this.createTime = createTime;
	}
	/*
	 * 获取 执行时间
	 */
	public java.util.Date getBeginTime(){
		return this.beginTime;
	}
	/*
	 * 设置 执行时间
	 */
	public void setBeginTime(java.util.Date beginTime){
		this.beginTime = beginTime;
	}
	/*
	 * 获取 结束时间
	 */
	public java.util.Date getEndTime(){
		return this.endTime;
	}
	/*
	 * 设置 结束时间
	 */
	public void setEndTime(java.util.Date endTime){
		this.endTime = endTime;
	}
	/*
	 * 获取 执行结果 （1.成功，2.失败）
	 */
	public java.lang.Integer getTaskResult(){
		return this.taskResult;
	}
	/*
	 * 设置 执行结果 （1.成功，2.失败）
	 */
	public void setTaskResult(java.lang.Integer taskResult){
		this.taskResult = taskResult;
	}
	/*
	 * 获取 说明
	 */
	public java.lang.String getTaskDesc(){
		return this.taskDesc;
	}
	/*
	 * 设置 说明
	 */
	public void setTaskDesc(java.lang.String taskDesc){
		this.taskDesc = taskDesc;
	}
	//方法 end
	
	@Override
	public String toString() {
		return "PhoneTask [taskId=" + taskId + ", imeiNumber=" + imeiNumber
				+ ", computerIp=" + computerIp + ", command=" + command
				+ ", taskParam1=" + taskParam1 + ", taskParam2=" + taskParam2
				+ ", taskParam3=" + taskParam3 + ", status=" + status
				+ ", createTime=" + createTime + ", beginTime=" + beginTime
				+ ", endTime=" + endTime + ", taskResult=" + taskResult
				+ ", taskDesc=" + taskDesc + "]";
	}

}
