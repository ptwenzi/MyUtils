package com.newland.cloudtest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.xutils.x;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;

import com.newland.cloudtest.adapter.DebuglAdapter;
import com.newland.cloudtest.bean.BamNormalModelDetailPara;
import com.newland.cloudtest.bean.QueryBamNormalModelRespContent;
import com.newland.cloudtest.bean.TaskResultLog;
import com.newland.cloudtest.bean.TestresultDetailPhone;
import com.newland.cloudtest.util.Contant;
import com.newland.cloudtest.util.SharedPreferencesUtils;
import com.newland.cloudtest.util.SystemUtils;
import com.newland.view.PullRefreshListView;
import com.newland.view.PullRefreshListView.IXListViewListener;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DebugActivit extends Activity implements IXListViewListener{

    private RecyclerView mRecyclerView;
    private List<Map<String, Object>> mDatas;
    @ViewInject(R.id.lv)
    private PullRefreshListView listView;
    DebuglAdapter debuglAdapter;
    boolean isStop = false;
    private int pageSize = 10;
    private int  pageIndex =0;
    LayoutInflater layoutInflater ;
    View mFooterView;
    Button next ;
    @ViewInject(R.id.nothing)
    ImageView nothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.debug);
		x.view().inject(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	     layoutInflater =  (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	     mFooterView = layoutInflater.inflate(R.layout.listview_footer, null);
	    	    
        //显示自定义的actionBar
		mDatas = new ArrayList<Map<String, Object>>();
		initData();
		debuglAdapter= new DebuglAdapter(getApplicationContext(), mDatas);
		listView.setAdapter(debuglAdapter);
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(this);
		

		//refreshData();
//		next = (Button) mFooterView.findViewById(R.id.next);
//		next.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				initData();
//			}
//		});
    }
    
//    private void refreshData()
//    {
//    	new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				while (!isStop) {
//					try {
//						Thread.sleep(1000*10);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					
//					initData();
//					Message msg = new Message();
//					msg.what = 0;
//					handler.sendMessage(msg);
//					
//				}
//		
//				
//			}
//		}).start();
//    }
    
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	initData();
		if(mDatas.size() ==0)
		{
			listView.setVisibility(View.GONE);
			nothing.setVisibility(View.VISIBLE);
		}
		else {
			nothing.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
    }
    
    
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
					
				debuglAdapter.refresh(mDatas);
				
				break;
			case 1:
//				listView.removeFooterView(mFooterView);
//				listView.addFooterView(mFooterView);
				listView.setPullLoadEnable(true);
				break;
			case 2:
				listView.setPullLoadEnable(false);
				//listView.removeFooterView(mFooterView);
				
				break;	
			default:
				break;
			}
		}

	};
	
	//type 0 刷新 type 1 下一页
    protected void initData()
    {
//		if(type == 0) //刷新
//		{
//			pageIndex = 0;
//			mDatas.clear();
//		}
        
		//检查所有的任务
		try {
			List<QueryBamNormalModelRespContent> qbmrApps = SystemUtils.getDbManage()
					.selector(QueryBamNormalModelRespContent.class).limit(pageSize)
                    .offset(pageSize * pageIndex)
					.orderBy("createTime", true)
					.findAll();
	
			
			if(qbmrApps!= null)
			{
				for (QueryBamNormalModelRespContent queryBamNormalModelRespContent : qbmrApps) {
					StringBuffer sb = new StringBuffer();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA);
					String formatestr =  formatter.format(queryBamNormalModelRespContent.getCreateTime());
					
					sb.append("任务策略名：").append(queryBamNormalModelRespContent.getTasksetInstanceName()).append("\r\n")
					.append("创建时间：").append(formatestr).append("\r\n")
					.append("上传状态：").append(convertStatue(queryBamNormalModelRespContent.getStatue())).append("\r\n")
					.append("上传接口返回信息：").append(queryBamNormalModelRespContent.getLocalErroMsg()).append("\r\n")
					.append("teststl：").append(queryBamNormalModelRespContent.getTestsrl()).append("\r\n")
					.append("任务类型:").append(convertType(queryBamNormalModelRespContent.getChannel())).append("\r\n");
					TaskResultLog taskRsult = SystemUtils.getDbManage()
							.selector(TaskResultLog.class)
							.where("qbmId","=",queryBamNormalModelRespContent.getQbmId())
							.findFirst();
					if(taskRsult!=null)
					{
						sb.append("任务状态："+taskRsult.getResult()).append("\r\n");
						sb.append("任务状态说明："+taskRsult.getResultContent()).append("\r\n");
					}
					else{
						sb.append("任务状态：还未完成").append("\r\n");
					}
					
					sb.append("=====正在执行的任务明细====").append("\r\n");
					List<BamNormalModelDetailPara> details = SystemUtils.getDbManage()
							.selector(BamNormalModelDetailPara.class)
							.where("qbmId","=",queryBamNormalModelRespContent.getQbmId())
							.orderBy("createTime", false)
							.findAll();
					if(details!=null)
					{
						for (BamNormalModelDetailPara bamNormalModelDetailPara : details) {
						
							sb.append("操作动作："+bamNormalModelDetailPara.getMethodType()).append("\r\n");
							if(queryBamNormalModelRespContent.getChannel().equals("12"))
							{
								sb.append("操作动作："+bamNormalModelDetailPara.getOperateType()).append("\r\n");
							}
							sb.append("执行状态："+convertDetailStatue(bamNormalModelDetailPara.getStatue())).append("\r\n");
							sb.append("错误信息："+bamNormalModelDetailPara.getErrMsg()).append("\r\n");
							sb.append("生成时间："+bamNormalModelDetailPara.getEndtime()).append("\r\n");
							sb.append("===============").append("\r\n");
						}
					}
					
					//4G特殊处理
					if(queryBamNormalModelRespContent.getChannel().equals("9"))
					{
						List<TestresultDetailPhone> detailPhones = SystemUtils.getDbManage()
								.selector(TestresultDetailPhone.class)
								.where("testsrl","=",queryBamNormalModelRespContent.getTestsrl())
								.orderBy("orderid", false)
								.findAll();
						if(detailPhones!=null)
						{
							for (TestresultDetailPhone testresultDetailPhone : detailPhones) {
								sb.append("操作动作："+testresultDetailPhone.getMethod()+testresultDetailPhone.getTarget()).append("\r\n");
//								sb.append("执行状态："+convertDetailStatue(bamNormalModelDetailPara.getStatue())).append("\r\n");
//								sb.append("错误信息："+bamNormalModelDetailPara.getErrMsg()).append("\r\n");
								sb.append("生成时间："+testresultDetailPhone.getEndtime()).append("\r\n");
								sb.append("===============").append("\r\n");
							}
						}
					}
					
					
					Map<String, Object>map = new HashMap<String, Object>();
					map.put("str", sb.toString());
					map.put("data", queryBamNormalModelRespContent);
					mDatas.add(map);
				}
				pageIndex++;
				
				if(qbmrApps.size()==pageSize) //说明下一页还有内容
				{
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
					
				}
				else {
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
					
				}
		
			}
			
		
	
			
		} catch (DbException e) {
			e.printStackTrace();
		}
    }
    
    private String convertType(String type)
    {
    	if("12".equals(type))
    	{
    		return "短信";
    	}
    	else if("32".equals(type)){
    		return "app";
		}
    	else  if("9".equals(type)){
    		return "4G";
		}
    	else
    	{
    		return "未知";
    	}
    }

    private String convertStatue(int statue)
    {
    	if(0==statue)
    	{
    		return "正在执行中";
    	}
    	else if (1==statue) {
    		return "执行成功";
		}
      	else if (2==statue) {
    		return "任务超时";
		}
      	else if (3==statue) {
    		return "已上传";
		}
      	else if (4==statue) {
    		return "上传接口返回错误";
		}
       	else if (5==statue) {
    		return "上传成功";
		}
    	return "未知情况";
    	
    }
    
    private String convertDetailStatue(int statue)
    {
    	if(0==statue)
    	{
    		return "成功";
    	}
    	else if (1==statue) {
    		return "失败";
		}
  
    	return "未知情况";
    	
    }
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isStop = true;
		

	}

	@Override
	public void onListViewRefresh() {
		pageIndex = 0;
		mDatas.clear();
		initData();
		debuglAdapter.notifyDataSetChanged();
		onLoad();
	}

	@Override
	public void onListViewLoadMore() {
		initData();
		debuglAdapter.notifyDataSetChanged();
		onLoad();
		
	}

	
	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		String date = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分").format(new Date());
		listView.setRefreshTime(date);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
