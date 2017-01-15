package com.newland.cloudtest.util;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import android.widget.EditText;

public class StringUtil {
	
	/**
	 * MD5加密验证
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	public static String noNull(Object str)
	{
      if(str == null|| str.equals("")||"null".equals(str))
    	  return "";
      else 
    	  return str.toString();
	}
		
		/**
		 * 如果源串为空或NULL，返回默认值
		 * @param str
		 * @param defaultValue
		 * @return
		 */
		public static String noNull(Object str,String defaultValue)
		   {
		      if(str == null || str.equals("")||"null".equals(str))
		    	  return defaultValue;
		      else 
		    	  return str.toString();
		   }
		

	   public static boolean isEmpty(String str)
	   {
	     return (str == null) || (str.length() == 0) || str.equals("null")  || str.equals("") ;
	   }
	 
	   public static boolean isNotEmpty(String str)
	   {
	     return !isEmpty(str);
	   }
	   
	   
	/**
	 * 身份证号验证
	 * @param sfzh
	 * @return true验证通过 false不通过
	 */
	public static boolean isChinaIDCard(String sfzh){
		try
		{
			int flag = 1;
			//如果长度不是15和18，则返回错误
			if(sfzh.length()!=18&&sfzh.length()!=15){
				flag = flag*0;
			}
			if(sfzh.length()==18){
				String start = sfzh.substring(0,6);
				int year = Integer.parseInt(sfzh.substring(6,10));
				int month = Integer.parseInt(sfzh.substring(10,12));
				int day = Integer.parseInt(sfzh.substring(12,14));
				int nowYear = (new Date()).getYear()+1900;
				Log.v("sfzh year is ", year+"");
				Log.v("sfzh month is ", month+"");
				Log.v("sfzh day is ", day+"");
				Log.v("now year is ", nowYear+"");
				if(year<1900||year>nowYear){
					flag = flag*0;
				}
				if(month<1||month>12){
					flag = flag*0;
				}
				if(day<1||day>31){
					flag = flag*0;
				}
				//判断验证位
				String check = "";
				int a = Integer.parseInt(sfzh.substring(0,1))*7+Integer.parseInt(sfzh.substring(1,2))*9+
						Integer.parseInt(sfzh.substring(2,3))*10+Integer.parseInt(sfzh.substring(3,4))*5+
						Integer.parseInt(sfzh.substring(4,5))*8+Integer.parseInt(sfzh.substring(5,6))*4+
						Integer.parseInt(sfzh.substring(6,7))*2+Integer.parseInt(sfzh.substring(7,8))*1+
						Integer.parseInt(sfzh.substring(8,9))*6+Integer.parseInt(sfzh.substring(9,10))*3+
						Integer.parseInt(sfzh.substring(10,11))*7+Integer.parseInt(sfzh.substring(11,12))*9+
						Integer.parseInt(sfzh.substring(12,13))*10+Integer.parseInt(sfzh.substring(13,14))*5+
						Integer.parseInt(sfzh.substring(14,15))*8+Integer.parseInt(sfzh.substring(15,16))*4+
						Integer.parseInt(sfzh.substring(16,17))*2;
				int b = a%11;
				switch (b) {
					case 0:check = "1";	break;
					case 1:check = "0";	break;
					case 2:check = "X";	break;
					case 3:check = "9";	break;
					case 4:check = "8";	break;
					case 5:check = "7";	break;
					case 6:check = "6";	break;
					case 7:check = "5";	break;
					case 8:check = "4";	break;
					case 9:check = "3";	break;
					case 10:check = "2";break;
					default:
						break;
				}
				Log.v("check num is ", check);
				if(!check.equals(sfzh.subSequence(17, 18))){
					flag = flag*0;
				}
			}
			Log.v("flag", flag+"");
			if(flag==1){
				return true;
			}else{
				return false;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 传入时间返回long型时间
	 * @param time 格式为HH:mm
	 * @return
	 */
	public static long getLongTime(String time){
		long longTime = 0;
		String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		nowDate = nowDate.substring(0,10);
		time = nowDate+" "+time+":00";
		try {
			longTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return longTime;
	}
	/**
	 * 
	 * 获取相对URL地址
	 * 
	 * @param split
	 *            切割符
	 * @param url
	 * 
	 * @return
	 */
	public static final String splitSubUrl(String split, String url) {
		try
		{
			if (url != null && !url.equals("")) {
				int pos = url.indexOf(split);
				if (pos <= -1) {
					return "";
				} else {
					String temp = url.substring(pos);
					return temp;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return "";
	}
	/**
	 * 
	 * @Title: clearSpaces
	 * @Description: 清除字符串中的空格字符
	 * @param str
	 * @return 设定文件
	 */
	public static final String clearSpaces(String str) {
		StringTokenizer st = new StringTokenizer(str, " ", false);
		String t = "";
		while (st.hasMoreElements()) {
			t += st.nextElement();
		}
		return t;
	}

	/**
	 * 
	 * @Title: formatStr
	 * @Description:如果为null,返回"" ,如果不是却掉前后空格
	 * @param str
	 * @return 设定文件
	 */
	public static final String formatStr(String str) {
		if (str == null) {
			return "";
		} else {
			return str.trim();
		}
	}

	

	/**
	 * 取得指定参数的值
	 * 
	 * @param key
	 * @param Keys
	 * @param Values
	 * @return
	 */
	public static String getKeyValue(String key, ArrayList<String> Keys,
			ArrayList<String> Values) {
		int len = Keys.size();
		for (int i = 0; i < len; i++) {
			if (Keys.get(i).equalsIgnoreCase(key))
				return Values.get(i);
		}
		return "";
	}



	
	/**
	 * 去掉空格，回车符
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		
		        String dest = "";
		
		        if (str!=null) {
		
		            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		
		            Matcher m = p.matcher(str);
		
		            dest = m.replaceAll("");
		
		        }
		
		        return dest;
		
		    }



	
	
	
	/**
	 * 判断是否是身份证
	 */
	public static boolean isPid(String str){

		
		if(isEmpty(str)){
			return  false;
		}
		String[] arg = { str };

		// 17位加权因子，与身份证号前17位依次相乘。

		int w[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3,
				7, 9, 10, 5, 8, 4, 2 };

		int sum = 0;// 保存级数和

		try {
			for (int i = 0; i < arg[0].length() - 1; i++) {
				sum += new Integer(arg[0]
						.substring(i, i + 1))
						* w[i];
			}
		} catch (Exception e) {
			return false;

		}

		/**
		 *校验结果，上一步计算得出的结果与11取模，得到的结果相对应的字符就是身份证最后一位
		 * ，也就是校验位。例如：0对应下面数组第一个元素， 以此类推。
		 */

		String sums[] = { "1", "0", "X", "9", "8",
				"7", "6", "5", "4", "3", "2" };
		if (sums[(sum % 11)].equals(arg[0]
				.substring(arg[0].length() - 1,
						arg[0].length()))) {// 与身份证最后一位比较
				return true;

		} else {
			return false;
		}
	
	}
	
	/**
	 * 把字符串空串转成null
	 * @param str
	 */
	public static String convertStrToNull(String str)
	{
		if(str!=null)
		{
			if("".equals(str))
			{
				return null;
			}
			else {
				return str;
			}
		}
		else
		{
			return str;
		}
	}
	
	
	/**
	 * 检查Edit是否是空的
	 * isEditTextIsEmpty(这里用一句话描述这个方法的作用)
	 * (这里描述这个方法适用条件 – 可选)
	 * @param editText
	 * @return 
	 *boolean
	 * @exception 
	 * @since  1.0.0
	 */
	public static boolean isEditTextIsEmpty(EditText editText)
	{
		String str = editText.getText().toString().trim();
		if ("".equals(str))
		{
			return true;
		}
		return false;
		
	}
	
	public static String DAY_TIME = "yyyy-MM-dd";
	public static String MOUTH_TIME = "yyyy-MM";
	public static String YEAR_TIME = "yyyy";
	public static String SECOND="yyyy-MM-dd HH:ss:mm";
	public static String getNowTime(String type)
	{
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat(type);//可以方便地修改日期格式 


		return dateFormat.format( now ); 


	}
	
	/**
	 * 获取昨天时间
	 * @return
	 */
	public static String getLastTime()
	{
		Calendar   cal   =   Calendar.getInstance();
		  cal.add(Calendar.DATE,   -1);
		  String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
		 return yesterday;
	}
	
	/**
	 * 获取上月
	 * @return
	 */
	public static String getLastMouth()
	{
		Calendar   cal   =   Calendar.getInstance();
		  cal.add(Calendar.MONTH,   -1);
		  String day = new SimpleDateFormat( "yyyy-MM ").format(cal.getTime());
		 return day;
	}
	/**
	 * 获取上年
	 * @return
	 */
	public static String getLastYear()
	{
		Calendar   cal   =   Calendar.getInstance();
		  cal.add(Calendar.YEAR,   -1);
		  String day = new SimpleDateFormat( "yyyy ").format(cal.getTime());
		 return day;
	}
	
	
	
	public static String formateNum(String date)
	{
		if("1".equals(date))
		{
			date = "01";
			return date;
		}
		else if("2".equals(date))
		{
			date = "02";
			return date;
		}
		else if("3".equals(date))
		{
			date = "03";
			return date;
		}
		else if("4".equals(date))
		{
			date = "04";
			return date;
		}
		else if("5".equals(date))
		{
			date = "05";
			return date;
		}
		else if("6".equals(date))
		{
			date = "06";
			return date;
		}
		else if("7".equals(date))
		{
			date = "07";
			return date;
		}
		else if("8".equals(date))
		{
			date = "08";
			return date;
		}
		else if("9".equals(date))
		{
			date = "09";
			return date;
		}
		else
		{
			return date;
		}
	}
	
	
	public static Long convertStr2Long(String str)
	{
		if(StringUtil.isEmpty(str))
		{
			return 0L;
		}
		else {
			Long code = 0L;
			try {
				code = Long.parseLong(str);
			} catch (Exception e) {
				e.printStackTrace();
				code =0L;
			}
			return code;
		}
	}
	/**
	 * 获取 /后面的名字
	 * @param str
	 * @return
	 */
	public static String getDownName(String str)
	{
		String temp[] = str.replaceAll("\\\\","/").split("/");
		if(temp.length > 1){
			return temp[temp.length - 1];
		} 
		return str;
	}
	
	
}
