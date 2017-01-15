package com.newland.cloudtest.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

public class DateUtil {
	public static final String DATE_FORMAT_NORMAL_SECOND = "yyyy-MM-dd HH:mm:ss";
	// 获取当前的时间 格式2015-10-10
	@SuppressLint("SimpleDateFormat")
	public static String getDateNowString( Date date) {
//		java.util.Date today = new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}
				
	public static String getDateNowStringWithmi( Date date) {
//		java.util.Date today = new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA);
		return formatter.format(date);
	}
	
	public static Long getStartTime(){  
        Calendar todayStart = Calendar.getInstance();  
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0);  
        return todayStart.getTime().getTime();  
    }  
      
	public static Long getEndTime(){  
        Calendar todayEnd = Calendar.getInstance();  
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);  
        todayEnd.set(Calendar.SECOND, 59);  
        todayEnd.set(Calendar.MILLISECOND, 999);  
        return todayEnd.getTime().getTime();  
    }  
	
	/**
	 * 获取当月第一天
	 * @return
	 */
	public static Long getCurrentMonthFirstDay()
	{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	       Calendar c = Calendar.getInstance();    
	        c.add(Calendar.MONTH, 0);
	        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
	        c.set(Calendar.HOUR_OF_DAY, 0);
	        c.set(Calendar.MINUTE, 1);
	      //  String first = format.format(c.getTime());
	        return c.getTime().getTime();
	}

	/**
	 * 获取当月最后一天
	 * @return
	 */
	public static Long getCurrentMonthLasttDay()
	{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
        Calendar ca = Calendar.getInstance();    
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));  
       // String last = format.format(ca.getTime());
       
	    return  ca.getTime().getTime();
	}

	public static String getDateTimeString(Date date) {
		return getDateTimeString(date, DATE_FORMAT_NORMAL_SECOND);
	}
	
	public static String getDateTimeString(Date date, String format) {
		String ret = "";
		if (format != null) {
			ret = new java.text.SimpleDateFormat(format).format(date);
		} else {
			ret = new java.text.SimpleDateFormat(DATE_FORMAT_NORMAL_SECOND).format(date);
		}
		return ret;
	}
}
