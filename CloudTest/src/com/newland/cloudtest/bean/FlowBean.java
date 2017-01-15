package com.newland.cloudtest.bean;

import java.util.Date;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 记录
 * @author TongLee
 *
 */
@Table(name = "flowBean")
public class FlowBean {

	@Column(name = "flowBeanId", isId = true)
	private String flowBeanId;
	@Column(name = "recordTime")
	private String recordTime;//统计时间 2016-8-4
	@Column(name = "recordStart")
	private Date recordStart;//记录起始时间
	@Column(name = "recordEnd")
	private Date recordEnd;//记录结束时间
	@Column(name = "upflowStart")
	private Long upflowStart;//上传流量 起始
	@Column(name = "downflowStart") 
	private Long downflowStart;//下行流量  起始
	
	@Column(name = "upflowEnd")
	private Long upflowEnd;//上传流量 结束
	@Column(name = "downflowEnd") 
	private Long downflowEnd;//下行流量  结束
	
	public String getFlowBeanId() {
		return flowBeanId;
	}
	public void setFlowBeanId(String flowBeanId) {
		this.flowBeanId = flowBeanId;
	}
	public String getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}
	public Date getRecordStart() {
		return recordStart;
	}
	public void setRecordStart(Date recordStart) {
		this.recordStart = recordStart;
	}
	public Date getRecordEnd() {
		return recordEnd;
	}
	public void setRecordEnd(Date recordEnd) {
		this.recordEnd = recordEnd;
	}
	public Long getUpflowStart() {
		return upflowStart==null?0:upflowStart;
	}
	public void setUpflowStart(Long upflowStart) {
		this.upflowStart = upflowStart;
	}
	public Long getDownflowStart() {
		return downflowStart==null?0:downflowStart;
	}
	public void setDownflowStart(Long downflowStart) {
		this.downflowStart = downflowStart;
	}
	public Long getUpflowEnd() {
		return upflowEnd==null?0:upflowEnd;
	}
	public void setUpflowEnd(Long upflowEnd) {
		this.upflowEnd = upflowEnd;
	}
	public Long getDownflowEnd() {
		return downflowEnd==null?0:downflowEnd;
	}
	public void setDownflowEnd(Long downflowEnd) {
		this.downflowEnd = downflowEnd;
	}

	
	

	
	
}
