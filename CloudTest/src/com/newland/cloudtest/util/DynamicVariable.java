package com.newland.cloudtest.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import android.util.Log;

import com.newland.cloudtest.exception.SmsException;



/**
 * 动态变量类
 * @author linh
 */
public class DynamicVariable {

	/**
	 * 需要替换的手机号码标识
	 */
	public static final String PHONENUMBER_TOKEN = "{phoneNumber}";
	/**
	 * 需要替换的手机号码标识
	 */
	public static final String PASSWORD_TOKEN = "{password}";
	/**
	 * 需要替换的日期标识（前缀）
	 */
	public static final String DATE_TOKEN_PREF = "{now";
	/**
	 * 需要替换的IMSI号码标识
	 */
	public static final String IMSINO_TOKEN = "{imsiNumber}";
	/**
	 * 需要替换的SIM卡号码标识
	 */
	public static final String SIMCARDNO_TOKEN = "{simcardNumber}";

	/**
	 * 获得所有待替换的字符串
	 * 
	 * @param oldStr
	 *            待替换的初始字符串
	 * @return
	 * @throws ProgramException
	 */
	private static Set<String> getAllReplacedStrSet(String oldStr)
			throws SmsException {
		Set<String> replacedStrSet = new HashSet<String>();
		int strLength = StringUtils.length(oldStr);
		StringBuffer sb = new StringBuffer();
		// 标识位，防止{{}}嵌套情况
		int flag = 0;
		for (int i = 0; i < strLength; i++) {
			String temp = StringUtils.substring(oldStr, i, i + 1);
			if (StringUtils.equals(temp, "{")) {
				if (flag != 0)
					throw new SmsException("字符串[" + oldStr + "]中存在嵌套！");
				sb.delete(0, sb.toString().length());
				flag = 1;
			}
			if (flag == 1)
				sb.append(temp);
			if (StringUtils.equals(temp, "}")) {
				replacedStrSet.add(sb.toString());
				flag = 0;
			}
		}
		if (flag == 1)
			throw new SmsException("字符串[" + oldStr + "]中没有动态变量标识结束符[}]！");
		return replacedStrSet;
	}

	/**
	 * 根据时间替换标识计算需要被替换成的时间字符串
	 * 
	 * @param token
	 *            时间替换标识 {now(yyyy-MM-dd)-3M}
	 * @return 被替换成的时间字符串
	 * @throws ProgramException
	 */
	private static String getReplaceDateStr(String token)
			throws SmsException {
		final String DEFAULTDATEFORMATESTR = "yyyy-mm-dd HH:mm:ss";
		String dateStr = "";
		int len = token.length();
		int beginIndex = StringUtils.indexOf(token, "(");
		if (beginIndex < 0)
			throw new SmsException("替换时间标识[" + token + "]没有配置日期格式！");
		int endIndex = StringUtils.indexOf(token, ")");
		if (endIndex < 0)
			throw new SmsException("替换时间标识[" + token + "]设置有误！");
		String dateFormatStr = StringUtils.substring(token, beginIndex + 1,
				endIndex);

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(DEFAULTDATEFORMATESTR);

		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		if (endIndex + 2 == len) {
			// {now(yyyy-MM-dd)}
		} else {
			if (len - endIndex < 4)
				throw new SmsException("替换时间标识[" + token + "]后半段长度 < 4！");
			// {now(yyyy-MM-dd)-3M}
			String value = StringUtils.substring(token, endIndex + 1, len - 2);
			int amount = 0;
			try {
				if (StringUtils.startsWith(value, "+"))
					value = StringUtils.substring(value, 1);
				amount = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new SmsException("替换时间标识[" + token + "]后半段amount值["
						+ value + "]设置有误！");
			}
			String type = StringUtils.substring(token, len - 2, len - 1);
			int field = getCalendarField(type);

			cal.add(field, amount);
			dateStr = sdf.format(cal.getTime());
		}

		try {
			sdf.applyPattern(dateFormatStr);
		} catch (IllegalArgumentException e) {
			throw new SmsException("替换时间模式[" + dateFormatStr + "]设置有误！");
		}
		dateStr = sdf.format(cal.getTime());

		return dateStr;
	}

	private static int getCalendarField(String type) throws SmsException {
		int field = 0;
		if (StringUtils.equals("y", type))
			field = Calendar.YEAR;
		else if (StringUtils.equals("M", type))
			field = Calendar.MONTH;
		else if (StringUtils.equals("d", type))
			field = Calendar.DAY_OF_MONTH;
		else if (StringUtils.equals("H", type))
			field = Calendar.HOUR_OF_DAY;
		else if (StringUtils.equals("m", type))
			field = Calendar.MINUTE;
		else if (StringUtils.equals("s", type))
			field = Calendar.SECOND;
		else
			throw new SmsException("替换时间标识最后未知的Calendar标识[" + type + "]");
		return field;
	}

	/**
	 * 替换字符串中的所有动态时间变量
	 * 
	 * @param oldStr
	 *            待替换的初始字符串
	 * @return 替换后的字符串
	 * @param token
	 *            时间替换标识
	 * @return 替换后的字符串
	 * @throws ProgramException
	 */
	private static String replaceDynamicDates(String oldStr, String token)
			throws SmsException {
		String dateStr = getReplaceDateStr(token);
		return StringUtils.replace(oldStr, token, dateStr);
	}

	/**
	 * 替换字符串中的动态手机号变量
	 * 
	 * @param oldStr
	 *            待替换的初始字符串
	 * @param newPhoneNum
	 *            替换手机号 {phoneNumber}
	 * @return
	 */
	private static String replaceDynamicPhoneNum(String oldStr,
			String newPhoneNum) {
		return StringUtils.replace(oldStr, PHONENUMBER_TOKEN, newPhoneNum);
	}

	/**
	 * 替换字符串中的动态密码变量
	 * 
	 * @param oldStr
	 *            待替换的初始字符串
	 * @param newPassword
	 *            替换密码 {password}
	 * @return
	 */
	private static String replaceDynamicPassword(String oldStr,
			String newPassword) {
		return StringUtils.replace(oldStr, PASSWORD_TOKEN, newPassword);
	}

	/**
	 * 替换字符串中的动态IMSI号变量
	 * 
	 * @param oldStr
	 *            待替换的初始字符串
	 * @param newImsiNum
	 *            替换手机号 {imsiNumber}
	 * @return
	 */
	private static String replaceDynamicImsiNum(String oldStr, String newImsiNum) {
		return StringUtils.replace(oldStr, IMSINO_TOKEN, newImsiNum);
	}

	/**
	 * 替换字符串中的动态SIMcard号变量
	 * 
	 * @param oldStr
	 *            待替换的初始字符串
	 * @param newSimcardNum
	 *            替换手机号 {simcardNumber}
	 * @return
	 */
	private static String replaceDynamicSimcardNum(String oldStr,
			String newSimcardNum) {
		return StringUtils.replace(oldStr, SIMCARDNO_TOKEN, newSimcardNum);
	}

	/**
	 * 替换字符串中的所有变量
	 * 
	 * @param oldStr
	 *            待替换的初始字符串
	 * @param newPhoneNum
	 *            替换手机号
	 * @param newPassword
	 *            替换密码
	 * @return
	 * @throws ProgramException
	 */
	public static String replaceAllDynamicVariables(String oldStr,
			String newPhoneNum, String imsiNum,
			String simcardNum) throws SmsException {
		String temp = oldStr;
		if (StringUtils.contains(oldStr, "{")) {
			// 存在需要被替换的动态变量
			Set<String> replaceSet = getAllReplacedStrSet(oldStr);
			for (Iterator<String> it = replaceSet.iterator(); it.hasNext();) {
				String token = it.next();
				if (StringUtils.equalsIgnoreCase(PHONENUMBER_TOKEN, token))
					temp = replaceDynamicPhoneNum(temp, newPhoneNum);
//				else if (StringUtils.equalsIgnoreCase(PASSWORD_TOKEN, token))
//					temp = replaceDynamicPassword(temp, newPassword);
				else if (StringUtils.startsWithIgnoreCase(token,
						DATE_TOKEN_PREF))
					temp = replaceDynamicDates(temp, token);
				else if (StringUtils.equalsIgnoreCase(IMSINO_TOKEN, token)) {
					temp = replaceDynamicImsiNum(temp, imsiNum);
				} else if (StringUtils.equalsIgnoreCase(SIMCARDNO_TOKEN, token)) {
					temp = replaceDynamicSimcardNum(temp, simcardNum);
				}
			}
			Log.d(DynamicVariable.class.getName(), "待替换的字符串为：" + oldStr);
			Log.d(DynamicVariable.class.getName(), "动态变量替换后的新字符串为：" + temp);
		}
		return temp;
	}
}
