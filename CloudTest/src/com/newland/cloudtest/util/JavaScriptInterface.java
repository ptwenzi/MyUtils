package com.newland.cloudtest.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xutils.ex.DbException;

import com.alibaba.fastjson.JSON;
import com.newland.cloudtest.bean.FlowBean;

import android.webkit.JavascriptInterface;

public class JavaScriptInterface {

	/*
	 * 获取图表数据
	 */
	@JavascriptInterface
	public String getchartData()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try {
		
			List<Integer> xList = new ArrayList<Integer>();
			List<Double> yList = new ArrayList<Double>();
			List<FlowBean> flowBeanList =  SystemUtils.getDbManage()
					.selector(FlowBean.class)
					.where("recordStart", ">", DateUtil.getCurrentMonthFirstDay()).orderBy("recordStart", false)
					.findAll();
			if (flowBeanList != null) {
				for (FlowBean flowBean : flowBeanList) {
					Date date = new Date(flowBean.getRecordStart().getTime());
					int dayStr = date.getDate();
					Long upflow = flowBean.getUpflowEnd()-flowBean.getUpflowStart();
					if(upflow<0)
					{
						upflow = upflow*-1;
					}
					Long downflow = flowBean.getDownflowEnd()-flowBean.getDownflowStart();
					if(downflow<0)
					{
						downflow = downflow*-1;
					}
					Long total = upflow+downflow;
					Float allFlow =  SystemUtils.bytes2kb(total);
					BigDecimal   b =  new BigDecimal(allFlow);  
					double f1 =  b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
					xList.add(dayStr);
					yList.add(f1);
				}
				map.put("xList", xList);
				map.put("yList", yList);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		finally{
			return JSON.toJSONString(map);
		}
		
	}
}
