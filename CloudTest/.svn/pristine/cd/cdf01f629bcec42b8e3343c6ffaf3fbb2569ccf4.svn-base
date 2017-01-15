package com.newland.cloudtest.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 真机非测试任务表 domain对象
 */
@Table(name = "PhoneTask")
public class PhoneTask  {

	/**
	 * @fields serialVersionUID 
	 */

	/**
	 * @fields taskId taskId
	 */
	@Column(name = "taskId", isId = true)
	private String taskId;
	/**
	 * @fields imeiNumber 执行机器串号（null：默认全部终端配置一致，非空：某台终端）
	 */
	@Column(name = "imeiNumber")
	private java.lang.String imeiNumber;
	/**
	 * @fields computerIp 下发探测终端IP
	 */
	@Column(name = "computerIp")
	private java.lang.String computerIp;
	/**
	 * @fields command 任务指令(100:终端监控时间间隔配置)
	 */
	@Column(name = "command")
	private java.lang.Integer command;
	/**
	 * @fields taskParam1 任务参数
	 */
	@Column(name = "taskParam1")
	private java.lang.String taskParam1;
	/**
	 * @fields taskParam2 任务参数2
	 */
	@Column(name = "taskParam2")
	private java.lang.String taskParam2;
	/**
	 * @fields taskParam3 任务参数3
	 */
	@Column(name = "taskParam3")
	private java.lang.String taskParam3;
	/**
	 * @fields status 执行状态(0.未执行，1.执行中，2.已执行  6执行失败 7任务超时 3正在上传  4上传失败  5上传成功  )
	 */
	@Column(name = "status")
	private java.lang.Integer status;
	/**
	 * @fields createTime 任务下发时间
	 */
	@Column(name = "createTime")
	private java.util.Date createTime;
	/**
	 * @fields beginTime 执行时间
	 */
	@Column(name = "beginTime")
	private String beginTime;
	/**
	 * @fields endTime 结束时间
	 */
	@Column(name = "endTime")
	private String endTime;
	/**
	 * @fields taskResult 执行结果 （1.成功，2.失败）
	 */
	@Column(name = "taskResult")
	private java.lang.Integer taskResult;
	/**
	 * @fields taskDesc 说明
	 */
	@Column(name = "taskDesc")
	private java.lang.String taskDesc;
	
	
	

	public String getTaskId(){
		return this.taskId;
	}
	public void setTaskId(String taskId){
		this.taskId = taskId;
	}
	public java.lang.String getImeiNumber(){
		return this.imeiNumber;
	}
	public void setImeiNumber(java.lang.String imeiNumber){
		this.imeiNumber = imeiNumber;
	}
	public java.lang.String getComputerIp(){
		return this.computerIp;
	}
	public void setComputerIp(java.lang.String computerIp){
		this.computerIp = computerIp;
	}
	public java.lang.Integer getCommand(){
		return this.command;
	}
	public void setCommand(java.lang.Integer command){
		this.command = command;
	}
	public java.lang.String getTaskParam1(){
		return this.taskParam1;
	}
	public void setTaskParam1(java.lang.String taskParam1){
		this.taskParam1 = taskParam1;
	}
	public java.lang.String getTaskParam2(){
		return this.taskParam2;
	}
	public void setTaskParam2(java.lang.String taskParam2){
		this.taskParam2 = taskParam2;
	}
	public java.lang.String getTaskParam3(){
		return this.taskParam3;
	}
	public void setTaskParam3(java.lang.String taskParam3){
		this.taskParam3 = taskParam3;
	}
	public java.lang.Integer getStatus(){
		return this.status;
	}
	public void setStatus(java.lang.Integer status){
		this.status = status;
	}
	public java.util.Date getCreateTime(){
		return this.createTime;
	}
	public void setCreateTime(java.util.Date createTime){
		this.createTime = createTime;
	}
	
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public java.lang.Integer getTaskResult(){
		return this.taskResult;
	}
	public void setTaskResult(java.lang.Integer taskResult){
		this.taskResult = taskResult;
	}
	public java.lang.String getTaskDesc(){
		return this.taskDesc;
	}
	public void setTaskDesc(java.lang.String taskDesc){
		this.taskDesc = taskDesc;
	}

}
