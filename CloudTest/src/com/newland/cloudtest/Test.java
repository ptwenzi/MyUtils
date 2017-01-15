package com.newland.cloudtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;

import com.alibaba.fastjson.JSON;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.newland.cloudtest.bean.MyXutilsRequestParams;
import com.newland.cloudtest.bean.RequestData;
import com.newland.cloudtest.util.Contant;

public class Test {
	
	public static void test()
	{
		System.out.println("aaaa");
	}
	public static void main(String[] args) throws InterruptedException {
/*		String timeStr = "2016-12-13 10:51:12";
		String ss = timeStr.substring(0, 16);
		System.out.println(ss);
		
		long a = System.currentTimeMillis();
		Thread.sleep(2000);
		long b = System.currentTimeMillis();
		System.out.println(b-a);
		String a = "尊敬的客户,截至目前您各类套餐使用情况如下:"+
"1.免GPRS4G流量(M):总100.00,余100.00;"+
"2.免GPRS4G流量(M)(结转):总69.20,余26.11;"+
"【便捷服务】下载“甘肃移动掌上营业厅”，5元500M、10元1G超值流量快餐包任您订。各大应用商店搜“甘肃移动掌上营业厅”或点击 http://wap.gs.10086.cn/wap/khdxz/khdxz.html 下载【中国移动】";
		
		List<String> returnContentList = new ArrayList<String>();
		Pattern pattern = Pattern.compile("流量(M)(结转)总：[1-9]\\d*(.\\d+)*,余[1-9]\\d*(.\\d+)*;");
		Matcher matcher = pattern.matcher(a);
		while (matcher.find()) {
			returnContentList.add(matcher.group());
		}
		for (String string : returnContentList) {
			System.out.println(string);
		}
	
		Integer a = 1000,b = 1000;
		System.out.println(a == b);
		Integer c = 100,d = 100;
		System.out.println(c == d);
		
//		int a = 1000,b = 1000;
//		System.out.println(a == b);
//		int c = 100,d = 100;
//		System.out.println(c == d);
	*/
		
	
	}
	
}
