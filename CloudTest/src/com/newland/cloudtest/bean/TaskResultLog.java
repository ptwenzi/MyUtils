package com.newland.cloudtest.bean;

import java.util.Date;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import android.R.integer;

/**
 * 结果日志
 * @author TongLee
 *
 */
@Table(name = "taskResultLog")
public class TaskResultLog {
	@Column(name = "qbmId", isId = true)
	private String qbmId;//任务表的任务id
	@Column(name = "taskResultJson")
	private String taskResultJson;
	@Column(name = "taskresultDetailJsonArray")
	private String taskresultDetailJsonArray;
	@Column(name = "isUpload")
	private int isUpload;//0未上传 1 上传
	@Column(name = "createTime")
	private Date createTime;
	@Column(name = "result")
	private String result;
	@Column(name = "resultContent")
	private String resultContent;
	@Column(name = "resultDetail")
	private String resultDetail;
	@Column(name = "channel")
	private String channel;
	/**
	 * 记录时间
	 */
	@Column(name = "recoreTime")
	private String recoreTime;//记录时间
	
	
	public String getRecoreTime() {
		return recoreTime;
	}
	public void setRecoreTime(String recoreTime) {
		this.recoreTime = recoreTime;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResultContent() {
		return resultContent;
	}
	public void setResultContent(String resultContent) {
		this.resultContent = resultContent;
	}
	public String getQbmId() {
		return qbmId;
	}
	public void setQbmId(String qbmId) {
		this.qbmId = qbmId;
	}
	public String getTaskResultJson() {
		return taskResultJson;
	}
	public void setTaskResultJson(String taskResultJson) {
		this.taskResultJson = taskResultJson;
	}
	public String getTaskresultDetailJsonArray() {
		return taskresultDetailJsonArray;
	}
	public void setTaskresultDetailJsonArray(String taskresultDetailJsonArray) {
		this.taskresultDetailJsonArray = taskresultDetailJsonArray;
	}
	public int getIsUpload() {
		return isUpload;
	}
	public void setIsUpload(int isUpload) {
		this.isUpload = isUpload;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getResultDetail() {
		return resultDetail;
	}
	public void setResultDetail(String resultDetail) {
		this.resultDetail = resultDetail;
	}
}
