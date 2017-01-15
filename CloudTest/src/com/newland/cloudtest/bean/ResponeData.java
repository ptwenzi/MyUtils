package com.newland.cloudtest.bean;
/**
 * 返回数据
 * @author TongLee
 *
 */
public class ResponeData {
	private String appId;
	private int code; //0是正常
	private Object data;
	private String msg;//返回消息
	private Long respTime; //返回时间
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Long getRespTime() {
		return respTime;
	}
	public void setRespTime(Long respTime) {
		this.respTime = respTime;
	}
	
	
}
