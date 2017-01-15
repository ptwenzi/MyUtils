package com.newland.cloudtest.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 计时器
 * @author TongLee
 *
 */
public class TimerUtils {
	
	
	private Long startTime=0L;
	private Long endTime=0L;
	private String startTimeStr="";
	private String endTimeStr="";
	private Long currentTime;
	/**
	 * 开始计时
	 */
	public void startTime()
	{
		Date d = new Date();
		startTime = d.getTime();
		startTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(d);
	}
	
	public Long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Long currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * 结束计时
	 * @return
	 */
	public Long stopTime()
	{
		Date d = new Date();
		endTime = d.getTime();
		Long currentTime =  (d.getTime()-startTime);
		endTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(d);
		this.currentTime = currentTime;
		return currentTime;
	}
	
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}
	
	public Long getWasteTime(){
		if(startTime==0||endTime==0){
			return null;
		}
		return endTime-startTime;
	}
	public static void main(String[] args) {
		TimerUtils ts = new TimerUtils();
		ts.startTime();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ts.stopTime());
		System.out.println(ts.getStartTimeStr());
		System.out.println(ts.getEndTimeStr());
	}
}
