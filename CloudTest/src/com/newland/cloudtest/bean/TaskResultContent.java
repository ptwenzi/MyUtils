package com.newland.cloudtest.bean;

import java.util.List;

/**
 * 上传结果类
 * @author TongLee
 *
 */
public class TaskResultContent {
	private String phoneNo;
	private String imsi;
	
	private Taskresult taskresult;
	private List<TestresultDetailPhone> testresultDetailPhones;
	private List<Tbusiness4g> tbusiness4gs;
	
	public List<Tbusiness4g> getTbusiness4gs() {
		return tbusiness4gs;
	}
	public void setTbusiness4gs(List<Tbusiness4g> tbusiness4gs) {
		this.tbusiness4gs = tbusiness4gs;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public Taskresult getTaskresult() {
		return taskresult;
	}
	public void setTaskresult(Taskresult taskresult) {
		this.taskresult = taskresult;
	}
	public List<TestresultDetailPhone> getTestresultDetailPhones() {
		return testresultDetailPhones;
	}
	public void setTestresultDetailPhones(
			List<TestresultDetailPhone> testresultDetailPhones) {
		this.testresultDetailPhones = testresultDetailPhones;
	}
	
	
}
