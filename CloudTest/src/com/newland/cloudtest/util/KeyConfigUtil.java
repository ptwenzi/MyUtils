package com.newland.cloudtest.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.newland.cloudtest.bean.GetKeyConfig;
import com.newland.cloudtest.bean.GetKeyConfigDetail;
import com.orhanobut.logger.Logger;

public class KeyConfigUtil {
	//短信提取出的剩余流量值
	private static String value = "无";
	
	public static String getFinalStringValueByMain(String smsContent, List<GetKeyConfig> listKey) throws Exception{
		List<String> list = getStringValueByMain(smsContent, listKey);
		if(list!=null && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public static List<String> getStringValueByMain(String smsContent, List<GetKeyConfig> listKey) throws Exception{
		ArrayList<String> valueList = new ArrayList<String>();
		String tempStr = null;
		for (GetKeyConfig config : listKey) {
			// 截取查询流量的前置关键字
			String beforekeywords = config.getBeforekeywords();
			// 截取查询流量的后置关键字
			String afterkeywords = config.getAfterkeywords();
			
			// 流量长度，length=-1，表示没有配置查询流量长度
			int length = -1;
			if (config.getKeywordslen() != null && config.getKeywordslen() > 0) {
				length = Integer.valueOf(String.valueOf(config.getKeywordslen()));
			}

			if (StringUtils.isEmpty(beforekeywords)) {
				Logger.v("未配置前关键字！");
				throw new Exception("未配置前关键字！");
			} else {
				Logger.v("前关键字:" + beforekeywords + ",后关键字:" + afterkeywords + ",关键字长度(-1或0表示没有配置固定长度):" + length);
				tempStr = smsContent;
				
				Pattern p = Pattern.compile(beforekeywords); 
				Matcher m = p.matcher(tempStr.toString());
				if (m.find()) {
					// 流量起始位置
					int startIndex = -1;
					// 流量结束位置
					int endIndex = -1;
					
					beforekeywords = m.group(); //如果正则表达式匹配到字符串
					int index = tempStr.indexOf(beforekeywords);
					
					if (index >= 0) {
						startIndex = index + beforekeywords.length();
						if (length > 0) { // 如果查询流量长度确定，直接根据长度截取
							endIndex = startIndex + length; 
						} else { // 如果查询流量长度不确定，需要通过后关键字进行匹配提取
							if (StringUtils.isEmpty(afterkeywords)){
								startIndex = -1;
								Logger.v("未配置长度，未配置后关键字，无法定位提取关键字");
							} else {
								Logger.v("使用后置关键字识别...");
								endIndex = tempStr.substring(startIndex).indexOf(afterkeywords.replace("\\", ""));
								if (endIndex >= 0 ) {
									endIndex = startIndex + endIndex;
								}
							}
						}
					} else {
						startIndex = -1;
					}
					
					if (endIndex >= 0 && startIndex >= 0) {
						if ("-".equals(config.getSymbol())) {
							valueList.add("-" + tempStr.substring(startIndex, endIndex).trim());
						} else {
							valueList.add(tempStr.substring(startIndex, endIndex).trim());
						}
						
						Logger.v("前index:" + startIndex + ",后index:" + endIndex 	+ ",取得的流量结果：" + tempStr.substring(0, endIndex).trim());
					} 
					//DELETE 2016/8/29 
					/*if (startIndex > 0){
						tempStr = tempStr.substring(startIndex + beforekeywords.length());
					}*/
				}
			}
		}
		return valueList;
	}
	public static String getValueByMain(String smsContent, List<GetKeyConfig> listKey) throws Exception {
		List<String> valueList = getStringValueByMain(smsContent, listKey);
		if (valueList.size() > 0) {
			double tempValue = 0.0;
			//对提取余额的值进行处理
			for (int i = 0; i < valueList.size(); i++) {
				tempValue += Double.valueOf(valueList.get(i));
			}
			return tempValue + "";
		} else {
			return null;
		}
	}
	
	public static String getFinalStringValueByDetail(String tempContent,List<GetKeyConfigDetail> configMapList){
		List<String> valueList =  getStringValueByDetail(tempContent, configMapList);
		if(valueList!=null && valueList.size()>0){
			return valueList.get(0);
		}else{
			return null;
		}
	}
	
	public static List<String> getStringValueByDetail(String tempContent,List<GetKeyConfigDetail> configMapList){
		//对configMapList进行内容替换
		configMapList = replaceDetailList(configMapList);
		
		ArrayList<String> valueList = new ArrayList<String>();
		if (configMapList == null) return null;
		if (StringUtils.isEmpty(tempContent)) {
			Logger.v("待查询结果为空字符串或为null");
			return  null;
		}
		
		List<String> tempValueInfoList = null;
		//获取所有valueInfo的集合
		for (GetKeyConfigDetail detail : configMapList){
			if (StringUtils.isEmpty(detail.getValueinfo())){//未配置有效内容字段的直接忽略
				Logger.v("未配置有效内容字段的直接忽略");
				continue;
			}
			
			String valueInfo = detail.getValueinfo();
			Logger.v("关键字内容："+valueInfo);
			tempValueInfoList = getPatternMatcherContentList(tempContent,valueInfo);
			if (tempValueInfoList == null || tempValueInfoList.size() == 0) {//未匹配到有效内容，跳过
				Logger.v("未提取到有效内容");
				continue;
			}
		
			String beforeKeywords = detail.getBeforekeywords();
			Logger.v("前关键字:" + beforeKeywords);
			// 先判断是否匹配前置关键字
			if (isMatching(tempContent, beforeKeywords)) {
				value = getValue(tempValueInfoList,detail);
				Logger.v("提取到内容---------------------" + value + "--------------------------");
				if (StringUtils.isNotEmpty(value) && isNumericValue(value)){
					valueList.add(value);
				}
			}
		}
		return valueList;
	}
	
	public static String getValueByDetail(String tempContent,List<GetKeyConfigDetail> configMapList){
		List<String> valueList = getStringValueByDetail(tempContent, configMapList);
		
		BigDecimal tempValue = null;
		//对提取余额的值进行处理
		for (int i = 0; i < valueList.size(); i++) {
			try{
				if (isNumericValue(valueList.get(i))) {
					if (tempValue == null){
						tempValue = new BigDecimal(valueList.get(i).trim());
					} else {
						tempValue = tempValue.add(new BigDecimal(valueList.get(i).trim()));
					}
				}
			} catch(NumberFormatException e){
				Logger.v("提取内容无法转化为数字");
			}
		}
		Logger.v("最终提取内容：" + tempValue);
		if (tempValue != null){
			return tempValue + "";
		} else {
			return null;
		}
		
	}
	
	/**计算总量
	 * @param tempValueInfoList valueinfo集合
	 * @param configMap 
	 * @param beforekeywords 前关键字
	 * @param afterkeywords 后关键字
	 * @param keywordslen 关键字长度
	 */
	private static String getValue(List<String> tempValueInfoList, GetKeyConfigDetail getKeyConfigDetail) {
		String beforekeywords = getKeyConfigDetail.getBeforekeywords();
		String afterkeywords = getKeyConfigDetail.getAfterkeywords();
		String keywordslen = getKeyConfigDetail.getKeywordslen() + "";
		String symbol = getKeyConfigDetail.getSymbol();
		Double finalValue = null;
		List<String> finalValueList = new ArrayList<String>();
		for (String string : tempValueInfoList) {
			Pattern beforekeywordspattern = Pattern.compile(beforekeywords);
			Matcher beforekeywordsmatcher = beforekeywordspattern.matcher(string);
			if (beforekeywordsmatcher.find()) {
				String tempBeforekeywordString = beforekeywordsmatcher.group();
				String content = string.substring(tempBeforekeywordString.length());// 截取前关键字之后的内容（要提取的内容+后关键字）
				if (StringUtils.isNotEmpty(afterkeywords)) { // 配置了后关键字
					List<String> afterKeywordsPatterContentList = getPatternMatcherContentList(content, afterkeywords);
					if (afterKeywordsPatterContentList.size() != 0) {// 前关键字+后关键字截取
						for (String string2 : afterKeywordsPatterContentList) {
							Logger.v("后关键字匹配内容：" + string2);
							String[] strings = content.split(afterkeywords);
							if (strings != null && strings.length > 0) {
								if (!isNumericValue(strings[0].trim())) {
									Logger.v("------------前关键字匹配不正确------------");
									continue;
								}
								finalValueList.add(handleValue(strings[0]));
							}
						}
					}
				} else {// 没有配置后关键字
					if (isNumericValue(content.trim())) {
						finalValueList.add(handleValue(content.trim()));
					} else {
						Logger.v("去除前关键字后的内容不是数字");
						// 如果配置了关键字长度
						if (StringUtils.isNotEmpty(keywordslen) && Integer.valueOf(keywordslen) >= 1) {// 前关键字+关键字长度
							content = content.substring(0, Integer.valueOf(keywordslen).intValue());
						} else {
							int endIndex = 0;
							String finalContent = "";
							Logger.v("逐个字符识别...");
							while (endIndex < content.length()
									&& ((content.charAt(endIndex) >= '0' && content.charAt(endIndex) <= '9') 
											|| ('.' == content.charAt(endIndex)) || ('-' == content.charAt(endIndex))
											|| (' ' == content.charAt(endIndex)) || ('\t' == content.charAt(endIndex)))) {
								finalContent += content.charAt(endIndex);
								endIndex++;
							}
							finalValueList.add(handleValue(finalContent));
						}
					}
				}
			} else {
				Logger.v("前关键字字与关键字内容不匹配");
			}
		}
		
		finalValue = getFinalValue(finalValueList, symbol, finalValue);
		if (finalValue != null) {
			return finalValue + "";
		} else {
			return null;
		}

	}
	
	/**某条ValueInfo匹配到的全部内容进行加减操作
	 */
	private static Double getFinalValue(List<String> finalValueList,String symbol,Double finalValue){
		if (finalValueList != null && finalValueList.size() > 0) {
			for (String tempFinalValue : finalValueList) {
				if (isNumericValue(tempFinalValue.trim())) {
					if (StringUtils.isEmpty(symbol)) {
						symbol = "";
					}
					if (finalValue != null) {
						finalValue += Double.valueOf(symbol + tempFinalValue.trim());
					} else {
						finalValue = Double.valueOf(symbol + tempFinalValue.trim());
					}
				}
			}
		}
		Logger.v("某条匹配，提取的值为："+finalValue);
		return finalValue;
	}
	
	/**对configMapList进行内容替换
	 * @param configMapList
	 * @return
	 */
	private static List<GetKeyConfigDetail> replaceDetailList(List<GetKeyConfigDetail> configMapList){
		//add by ab 替换动态日期---begin
		for (GetKeyConfigDetail getKeyConfigDetail : configMapList) {
			String valueinfo = getKeyConfigDetail.getValueinfo();
			String beforekeywords = getKeyConfigDetail.getBeforekeywords();
			String afterkeywords = getKeyConfigDetail.getAfterkeywords();
			if(StringUtils.isNotEmpty(valueinfo)){
				getKeyConfigDetail.setValueinfo(replacePattern(valueinfo));
			}
			if (StringUtils.isNotEmpty(beforekeywords)) {
				getKeyConfigDetail.setBeforekeywords(replacePattern(beforekeywords));
			}
			if (StringUtils.isNotEmpty(afterkeywords)) {
				getKeyConfigDetail.setAfterkeywords(replacePattern(afterkeywords));
			}
		}//add by ab 替换动态日期---end
		return configMapList;
	}
	
	/** 防止 截取的字串中间 含有空格或者tab字符，例如“-1243.12 2010-12-20”将最终解析为“-1243.12”
	 * @param value
	 * @return
	 */
	private static String handleValue(String value){
		// 防止 截取的字串中间 含有空格或者tab字符，例如“-1243.12 2010-12-20”将最终解析为“-1243.12”
		if (StringUtils.countMatches(value, " ") >= 1) {
			int index1st = value.indexOf(' ');
			value = value.substring(0, index1st);
		}
		if (StringUtils.countMatches(value, "\t") >= 1) {
			int index1st = value.indexOf('\t');
			value = value.substring(0, index1st);
		}
		
		// 防止 截取的字串末尾 含有多个“-”或者“.”，例如“-1243.12.3-.12”将最终解析为“-1243.12”
		if (StringUtils.countMatches(value, "-") > 1) {
			int index1st = value.indexOf('-');
			int index2ed = value.indexOf('-', index1st+1);
			value = value.substring(0, index2ed);
		}
		if (StringUtils.countMatches(value, ".") > 1) {
			int index1st = value.indexOf('.');
			int index2ed = value.indexOf('.', index1st+1);
			value = value.substring(0, index2ed);
		}
		if(isNumericValue(value)) {
			// 精确到小数点后2位
			BigDecimal valueString= new BigDecimal(value);
			value = valueString.setScale(2, RoundingMode.DOWN).toPlainString();
			Logger.v("解析得value=" + value);
		}
		return value;
	}
	
	/**替换正则格式为具体值（如替换#mm#为具体月份）
  	 * @param content
  	 * @return
  	 * add by shenbh20160524
  	 */
	private static String replacePattern(String content){
  		boolean isDel = false;
  		int delNum1 = 1;//减去的数
  		int delNum2 = 2;
  		String replaceSrcContent = "";
  		SimpleDateFormat sdf = null;
  		String dateFormat = "";
  		if (content.contains("#today#")) {
  			replaceSrcContent = "#today#";
  			dateFormat = "yyyy-MM-dd";
  		}else if (content.contains("#today1#")) {
  			replaceSrcContent = "#today1#";
  			dateFormat = "yyyy-M-d";
  		}else if (content.contains("#today2#")) {
  			replaceSrcContent = "#today2#";
  			dateFormat = "yyyyMMdd";
  		}else if (content.contains("#dd#")) {
  			replaceSrcContent = "#dd#";
  			dateFormat = "dd";
  		}else if (content.contains("#d#")) {
  			replaceSrcContent = "#d#";
  			dateFormat = "d";
  		}else if (content.contains("#mm#")) {
  			replaceSrcContent = "#mm#";
  			dateFormat = "MM";
  		}else if (content.contains("#m#")) {
  			replaceSrcContent = "#m#";
  			dateFormat = "M";
  		}else if (content.contains("#yyyy#")) {
  			replaceSrcContent = "#yyyy#";
  			dateFormat = "yyyy";
  		}else if (content.contains("#yy#")) {
  			replaceSrcContent = "#yy#";
  			dateFormat = "yy";
  		}else if (content.contains("#yyyymm#")) {
  			replaceSrcContent = "#yyyymm#";
  			dateFormat = "yyyyMM";
  		}else if (content.contains("#yyyy-mm#")) {
  			replaceSrcContent = "#yyyy-mm#";
  			dateFormat = "yyyy-MM";
  		}else if (content.contains("#yyyy-m#")) {
  			replaceSrcContent = "#yyyy-m#";
  			dateFormat = "yyyy-M";
  		}else if (content.contains("#hh#")) {
  			replaceSrcContent = "#hh#";
  			dateFormat = "HH";
  		}else if (content.contains("#mi#")) {
  			replaceSrcContent = "#mi#";
  			dateFormat = "mm";
  		}else if (content.contains("#ss#")) {
  			replaceSrcContent = "#ss#";
  			dateFormat = "ss";
  		}if (content.contains("#m<m-1>#")) {
  			replaceSrcContent = "#m<m-1>#";
  			dateFormat = "M";
  			isDel = true;
  		}else if (content.contains("#m<m-2>#")) {
  			replaceSrcContent = "#m<m-2>#";
  			dateFormat = "M";
  			isDel = true;
  			delNum1 = delNum2;
  		}else if (content.contains("#mm<mm-1>#")) {
  			replaceSrcContent = "#mm<mm-1>#";
  			dateFormat = "MM";
  			isDel = true;
  		}else if (content.contains("#mm<mm-2>#")) {
  			replaceSrcContent = "#mm<mm-2>#";
  			dateFormat = "MM";
  			isDel = true;
  			delNum1 = delNum2;
  		}else if (content.contains("#yyyy<mm-1>#")) {
  			replaceSrcContent = "#yyyy<mm-1>#";
  			dateFormat = "yyyyMM";
  			isDel = true;
  		}else if (content.contains("#yyyy<mm-2>#")) {
  			replaceSrcContent = "#yyyy<mm-2>#";
  			dateFormat = "yyyyMM";
  			isDel = true;
  			delNum1 = delNum2;
  		}else if (content.contains("#yyyy-<mm-1>#")) {
  			replaceSrcContent = "#yyyy-<mm-1>#";
  			dateFormat = "yyyy-MM";
  			isDel = true;
  		}else if (content.contains("#yyyy-<mm-2>#")) {
  			replaceSrcContent = "#yyyy-<mm-2>#";
  			dateFormat = "yyyy-MM";
  			isDel = true;
  			delNum1 = delNum2;
  		}
  		sdf = new SimpleDateFormat(dateFormat);
  		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		if (isDel) {
			calendar.add(Calendar.MONTH, -delNum1);
			Logger.v("delNum1--------------"+delNum1);
			isDel = false;
		}
  		
  		String replaceDstContent = "";
  		replaceDstContent = sdf.format(calendar.getTime());
  		if (StringUtils.isNotEmpty(replaceSrcContent)) {
  			Logger.v("-------------------------------进行日期动态替换--------------------------------------");
  			Logger.v("replaceDstContent----------"+replaceDstContent);
  			content = content.replace(replaceSrcContent, replaceDstContent);
  			Logger.v("content-----------"+content);
  		}
  		return content;
  	}
	
	/**获取tempString中所有正则匹配的结果
	 * @param tempString 待匹配内容
	 * @param patterFormat 匹配的正则表达式
	 * @return 返回String的集合
	 */
	private  static List<String> getPatternMatcherContentList(String tempString,String patterFormat){
		List<String> returnContentList = new ArrayList<String>();
		Pattern pattern = Pattern.compile(patterFormat);
		Matcher matcher = pattern.matcher(tempString);
		while (matcher.find()) {
			returnContentList.add(matcher.group());
		}
		return returnContentList;
	}
	
	/**是否匹配
	 * @param content 内容
	 * @param matchKey 匹配的关键字
	 * @return
	 */
	private static boolean isMatching(String content,String matchKey){
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(matchKey);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			isMatch = true;
		}
		return isMatch;
	}
	
	/**
	 * 判断截取的字串是否为数值
	 * @param value
	 * @return
	 */
	private static boolean isNumericValue(String value) {
		String regex = "^((-?[1-9]\\d*)|(-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0))|0)$";
        return Pattern.matches(regex, value);
	}

}
