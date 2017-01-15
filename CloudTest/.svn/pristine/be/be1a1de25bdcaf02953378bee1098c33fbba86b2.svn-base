/**
 * 系统项目名称
 * com.lee.utils
 * SharedPreferencesUtils.java
 * 
 * 2013-5-30-上午10:49:03
 * 
 */
package com.newland.cloudtest.util;

import com.orhanobut.logger.Logger;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * SharedPreferencesUtils  
 * 
 * Tony Lee
 * 2013-5-30 上午10:49:03
 * 
 * @version 1.0.0
 * 
 */
public class SharedPreferencesUtils
{
	public static  String Name="COMP";
//	public static SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
//	private SharedPreferencesUtils(){};
//	
//	public static SharedPreferencesUtils getInstence()
//	{
//		return sharedPreferencesUtils;
//	}
//	
	/**
	 * setConfig设置布尔变量
	 * (这里描述这个方法适用条件 – 可选)
	 * @param context
	 * @param cachName
	 * @param key
	 * @param bl 
	 *void
	 * @exception 
	 * @since  1.0.0
	 */
	  public  static void setConfigBoolean(Context context,String cachName, String key, Boolean bl)
	    {
	    	SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
	    	SharedPreferences.Editor  edit = share.edit();
			edit.putBoolean(key, bl); 
			edit.commit();
	    }
	  
	  /**
	   * 
	   * getConfig 获取本地布尔变量 
	   * 没有这个变量 默认false
	   * @param context
	   * @param cachName
	   * @param key
	   * @return 
	   *boolean
	   * @exception 
	   * @since  1.0.0
	   */
	  public  static boolean getConfigBoolean(Context context, String cachName, String key)
	    {
	    	SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
	    	return  share.getBoolean(key, false); 

	    }
	  
	  
	  public static void setConfigStr(Context context,String cachName, String key, String str)
	    {
	    	SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
	    	SharedPreferences.Editor  edit = share.edit();
			edit.putString(key, str);
			edit.commit();
	    }
	  
	  public static void setConfigLong(Context context,String cachName, String key, Long str)
	    {
	    	SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
	    	SharedPreferences.Editor  edit = share.edit();
			edit.putLong(key, str);
			edit.commit();
	    }
	  
	  
	  
	  public static String getConfigStr(Context context, String cachName, String key)
	    {
	    	SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
	    	return  share.getString(key, null); 
	    }
	  
	  public static int getConfigInt(Context context,String cachName,String key,int defaultVal)
	  {
			SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
			return share.getInt(key, defaultVal);
	  }
	  
	  public static Long getConfigLong(Context context,String cachName,String key,Long defaultVal)
	  {
		  Long temp = null ;
		  try {
			  SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
				 temp = share.getLong(key, 0);
		} catch (Exception e) {
			Logger.v(e.getMessage());
		}finally{
			return temp;
		}
			
		
	  }
	  public static void setConfigInt(Context context,String cachName, String key, int val)
	    {
	    	SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
	    	SharedPreferences.Editor  edit = share.edit();
			edit.putInt(key, val);
			edit.commit();
	    }
	  
	  public static void clear(Context context,String cachName)
	  {
		  SharedPreferences share = context.getSharedPreferences(cachName, Activity.MODE_PRIVATE);
	    	SharedPreferences.Editor  edit = share.edit();
	    	edit.clear();
			edit.commit();
	  }
}
