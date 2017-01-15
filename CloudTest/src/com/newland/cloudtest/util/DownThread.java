package com.newland.cloudtest.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;

public class DownThread extends Thread {
	private boolean isFinish = false;
	private Long beginFlow;
	private float conFlow;
	private String url;
	private File dest;
	private String threadname;
	private Context context;

	public DownThread(Context context, Long currentFlow, float curTraffic, String url, File dest, String threadname) {
		this.beginFlow = currentFlow;
		this.conFlow = curTraffic;
		this.dest = dest;
		this.url = url;
		this.threadname = threadname;
		this.context = context;
	}

	@Override
	public void run() {
		try{
			boolean isSucc = false;
			Long curTime = System.currentTimeMillis();
			Long failBegin = System.currentTimeMillis();
			Long oTime = 60*60*1000L;
			while (true) {
				Long downedFlow = (long) SystemUtils.get4Gflow();
				if((downedFlow-beginFlow) < conFlow) {
					try {
						isSucc = DownLoadFlow.getInstance().downloadfile(this.url, dest);
					} catch (IOException e) {
						isSucc = false;
						e.printStackTrace();
					}
					if (isSucc) {
						failBegin = System.currentTimeMillis(); //成功一次即将连续失败次数清零
					}
				} else {
					this.isFinish = true;
					break;
				}
				if (System.currentTimeMillis() - curTime > oTime){
					SharedPreferencesUtils.setConfigStr(this.context, Contant.cachName, this.threadname + "err", "线程" + this.threadname + "流量消耗超时");
					break;
				}
				if (System.currentTimeMillis() - failBegin >= 5 * 60 * 1000){
					SharedPreferencesUtils.setConfigStr(this.context, Contant.cachName, this.threadname + "err", "线程" + this.threadname + "流量消耗失败，连续30秒无法正常下载");
					break;
				}
			}
		} catch(Exception e){
			SharedPreferencesUtils.setConfigStr(this.context, Contant.cachName, this.threadname + "err", "线程" + this.threadname + "流量消耗失败：" + e.getMessage());
		} finally{
			SharedPreferencesUtils.setConfigBoolean(context, Contant.cachName, this.threadname + "finishflag", true);
		}
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	public Long getBeginFlow() {
		return beginFlow;
	}

	public void setBeginFlow(Long beginFlow) {
		this.beginFlow = beginFlow;
	}

	public float getConFlow() {
		return conFlow;
	}

	public void setConFlow(float conFlow) {
		this.conFlow = conFlow;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public File getDest() {
		return dest;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

	public String getThreadname() {
		return threadname;
	}

	public void setThreadname(String threadname) {
		this.threadname = threadname;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	

	
	

	
}
