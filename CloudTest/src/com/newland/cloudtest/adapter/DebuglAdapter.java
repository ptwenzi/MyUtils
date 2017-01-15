package com.newland.cloudtest.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.xutils.ex.DbException;

import com.alibaba.fastjson.JSON;
import com.newland.cloudtest.R;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.QueryBamNormalModelRespContent;
import com.newland.cloudtest.bean.TaskResultLog;
import com.newland.cloudtest.bean.Taskresult;
import com.newland.cloudtest.util.Contant;
import com.newland.cloudtest.util.SharedPreferencesUtils;
import com.newland.cloudtest.util.SystemUtils;
import com.newland.cloudtest.util.TimerUtils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 积分兑换适配器 frgment
 * @author H81
 *
 */
public class DebuglAdapter extends BaseAdapter {
	LayoutInflater inflater;

	List<Map<String, Object>> list;
	Context context;
	
	public DebuglAdapter(Context context, List<Map<String, Object>> group_list) {
		this.context = context;
		list = group_list;
		inflater = LayoutInflater.from(context);
	}


	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void refresh( List<Map<String, Object>> group_list) {
		list = group_list;
	notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.debug_item, null);
			viewHolder = new ViewHolder();
			
			viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			viewHolder.finish = (Button) convertView.findViewById(R.id.finish);
//			viewHolder. intergral = (TextView)convertView. findViewById(R.id.intergral);
//			viewHolder.less_num = (TextView) convertView.findViewById(R.id.less_num);
			
		  
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, Object>map = list.get(position);
		final QueryBamNormalModelRespContent qbm = (QueryBamNormalModelRespContent)map.get("data");
		viewHolder.tv_content.setText(map.get("str").toString()); 
		viewHolder.finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					QueryBamNormalModelRespContent queryBamNormalModelRespContent = SystemUtils.getDbManage()
							.selector(QueryBamNormalModelRespContent.class)
							.where("qbmId","=",qbm.getQbmId())
							.findFirst();
					upLoadResult("失败","手动结束任务",queryBamNormalModelRespContent);
					Toast.makeText(context, "结束成功，等待结果上传", 1000).show();
					
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		});
		
		return convertView;
	}

	class ViewHolder {
		    public TextView tv_content;  //标题
		    public Button finish;  
		    
	} 
	
	/**
	 * 保存短厅结果到本地
	 */
	private void upLoadResult(String result,String resultContent,QueryBamNormalModelRespContent qbmr)
	{
	
			Taskresult taskresult = new Taskresult();
			if("成功".equals(result))
			{
				taskresult.setResult(result);
				taskresult.setResultcontent("task_no_erro");
			}
			else {
				taskresult.setResult(result);
				taskresult.setResultcontent(resultContent);
			}
			SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "Taskstarttime", null);
			SharedPreferencesUtils.setConfigLong(context, Contant.cachName, "TaskWastetime", 0L);
			SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "Taskendtime", null);


			
			if(qbmr != null)
			{
				TaskResultLog taskResultLog = new TaskResultLog();
				taskResultLog.setQbmId(qbmr.getQbmId());
				taskResultLog.setTaskResultJson(JSON.toJSONString(taskresult));
				taskResultLog.setResult(result);
				taskResultLog.setResultContent(resultContent);
				taskResultLog.setIsUpload(0);
				taskResultLog.setCreateTime(new Date());
				taskResultLog.setChannel(qbmr.getChannel());
				Date d = new Date();
				taskResultLog.setRecoreTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(d));
				try {
					SystemUtils.getDbManage().saveOrUpdate(taskResultLog);
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
	}
		
}
