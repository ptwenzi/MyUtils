package com.newland.cloudtest.bean;

import java.util.Date;

public class TrafficMindMsg extends RespMsg {
	private Date findTime;
	public Date getFindTime() {
		return findTime;
	}
	public void setFindTime(Date findTime) {
		this.findTime = findTime;
	}
	@Override
	public String toString() {
		return "TrafficMindMsg [findTime=" + findTime + ", getCode()="
				+ getCode() + ", getMsg()=" + getMsg() + ", getResult()="
				+ getResult() + "]";
	}
	
	
}
