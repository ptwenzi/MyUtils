package com.newland.cloudtest.bean;

import java.util.Date;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 短信对象
 * @author TongLee
 *
 */
@Table(name = "SMSInfo")
public class SMSInfo {
	@Column(name = "smsId", isId = true)
	private String smsId;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "content")
	private String content ;
	@Column(name = "time")
	private String time;
	@Column(name = "createTime")
	private Date createTime;//创建数据库时间
	@Column(name = "type")
	private int type = 0;  //0发送 1接收
	
	
	public String getSmsId() {
		return smsId;
	}
	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}
