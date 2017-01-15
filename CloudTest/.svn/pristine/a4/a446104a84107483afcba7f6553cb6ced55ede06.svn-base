package com.newland.cloudtest.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.BamNormalModelPara;
import com.newland.cloudtest.bean.FlowBean;
import com.newland.cloudtest.bean.MyXutilsRequestParams;
import com.newland.cloudtest.bean.QueryBamNormalModelRespContent;
import com.newland.cloudtest.bean.RequestData;
import com.newland.cloudtest.bean.ResponeData;
import com.newland.cloudtest.bean.SimTrafficLog;
import com.newland.cloudtest.bean.TaskResultLog;
import com.newland.cloudtest.bean.Tbusiness4g;
import com.newland.cloudtest.bean.TestresultDetailPhone;
import com.orhanobut.logger.Logger;

/**
 * 服务端接口辅助类
 * @author TongLee
 *
 */
public class HttpInterfaceHelper {
	private HttpInterfaceHelper() {
	}

	private static HttpInterfaceHelper single = null;
	
	// 静态工厂方法
	public static HttpInterfaceHelper getInstance() {
		if (single == null) {
			single = new HttpInterfaceHelper();
		}
		return single;
	}
	
	
	/**
	 * 上传每日4G流量
	 * @throws DbException 
	 */
	public void upload4GDayFlow(Context context) throws DbException
	{
		//获取昨日数据
		Calendar   cal   =   Calendar.getInstance();
		 cal.add(Calendar.DATE,   -1);
		 String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
		 FlowBean flowBean = SystemUtils.getDbManage()
					.selector(FlowBean.class)
					.where("recordTime", "=",yesterday)
					.findFirst();
		if (flowBean == null) { //第一天
			return;
		}	
		SimTrafficLog simTrafficLog = new SimTrafficLog();
		simTrafficLog.setUpTraffic (flowBean.getUpflowEnd()-flowBean.getUpflowStart());
		simTrafficLog.setDownTraffic(flowBean.getDownflowEnd()-flowBean.getDownflowStart());
		simTrafficLog.setImsi(SystemUtils.getImsiNumber(context));
//		simTrafficLog.setImsi("4324242");
		simTrafficLog.setImei(SystemUtils.getIMEI(context));
		simTrafficLog.setTrafficTime(yesterday+" 23:59:00.000");
		
		MyXutilsRequestParams params = new MyXutilsRequestParams(Contant.IP+"/simTrafficLog/upTrafficLog");
		params.setCharset("utf-8");
		RequestData requestData = new RequestData();
		Map<String, Object>map = new HashMap<String, Object>();
		map.put("simTrafficLog", simTrafficLog);
		requestData.setData(map);
		requestData.setRequestTime(System.currentTimeMillis()+"");
		params.addBodyParameter("data", JSON.toJSONString(requestData));
		params.setCharset("utf-8");
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Logger.v(result);
				ResponeData responeData = JSON.parseObject(result, ResponeData.class);
				if(responeData != null && responeData.getCode()==0)
				{
					//查看是否有数据
					if(responeData.getData() !=null)
					{
						
					
						
					}
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
		
				Logger.v(ex.getMessage());
			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
				Logger.v("cancelled");
			}

			@Override
			public void onFinished() {

			}
		});
	}
	
	
	/**
	 * 清理前2天的任务数据
	 */
	public void clearLastDate()
	{
		//获取前日数据
		Calendar   cal   =   Calendar.getInstance();
		 cal.add(Calendar.DATE,   -2);
		 String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
		 try {
//			QueryBamNormalModelRespContent queryBamNormalModelRespContent = SystemUtils.getDbManage()
//						.selector(QueryBamNormalModelRespContent.class)
//						.where("createTime", "<",cal.getTime())
//						.findAll();
			SystemUtils.getDbManage().delete(QueryBamNormalModelRespContent.class, WhereBuilder.b("createTime", "<", cal.getTime()));
			SystemUtils.getDbManage().delete(BamNormalModelDetailPara.class, WhereBuilder.b("createTime", "<", cal.getTime()));
			SystemUtils.getDbManage().delete(TaskResultLog.class, WhereBuilder.b("createTime", "<", cal.getTime()));
			SystemUtils.getDbManage().delete(Tbusiness4g.class, WhereBuilder.b("createtime", "<", cal.getTime()));
			SystemUtils.getDbManage().delete(TestresultDetailPhone.class, WhereBuilder.b("createTime", "<", cal.getTime()));
		} catch (DbException e) {
			e.printStackTrace();
		}
		 
		 
		 String LogPath =   Environment.getExternalStorageDirectory().toString()+ File.separator + "/ClouldLog/";
		 File[] fList=new File(LogPath).listFiles(); 
		 Long deleteTime = System.currentTimeMillis()-1000*60*60*24 *5;
		 for(int j=0;j<fList.length;j++) 
          { 
				Long modifyTime =  fList[j].lastModified();
				if(modifyTime<deleteTime)  //小于1天前删掉
				{
					FileHelper.deleteFile( fList[j].getAbsolutePath());
				}
				
          } 	
		 
		 
		 
		 
		 
	}
}
