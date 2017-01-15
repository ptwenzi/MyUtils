package com.newland.cloudtest.bean;

import java.util.Date;
import java.util.List;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


@Table(name = "queryBamNormalModel")
public class QueryBamNormalModelRespContent
{ 
	@Column(name = "qbmId", isId = true)
	private String qbmId;//由客户端自己生成
	@Column(name = "createTime")
	private Date createTime;//由客户端自己生成
	@Column(name = "statue")
	private int statue;//执行状态 0未执行 1执行成功 2任务超时 3已上传  4上传出错   5已成功
	@Column(name = "testsrl")
	private Long testsrl;//探测流水
	@Column(name = "phoneNo")
	private String phoneNo; //手机号码
	@Column(name = "strategyId")
	private Long strategyId; //策略ID
	@Column(name = "groupId")
	private Long groupId; //调度组ID
	@Column(name = "tasksetId")
	private Long tasksetId; //任务集ID
	@Column(name = "tasksetInstanceName")
	private String tasksetInstanceName; //任务集实例名
	@Column(name = "channel")
	private String channel; //渠道（ 12短厅）
	

	//等待时间  毫秒
	private Integer pauseTime=0;

	private List<GetKeyConfig> getkeyconfig;
	//app
	private List<GetKeyConfig> getSmskeyconfigs; //短信验证码提取规则
	
	//4g
	private List<Smstaskstep> queryTrafficSteps;//取流量查询步骤信息
	private List<GetKeyConfig> queryTrafficKeyconfig;//流量提取规则
	private List<GetKeyConfigDetail> queryTrafficKeyconfigDetail;//流量提取规则
	private List<Smstaskstep> addTrafficSteps;//取订购流量加油包步骤信息
	private List<Smstaskstep> warnTrafficSteps;//流量提醒短信步骤信息
	private String trafficTDownloadUrl;//浏览消耗使用的HTTP下载地址
	private BamNormalModelPara bamNormalModel; //脚本
	@Column(name = "responseJsonDate")
	private String responseJsonDate;//把本对象转成json串
	@Column(name = "localErroMsg")
	private String localErroMsg;
	
	
	public String getLocalErroMsg() {
		return localErroMsg;
	}
	public void setLocalErroMsg(String localErroMsg) {
		this.localErroMsg = localErroMsg;
	}
	public Integer getPauseTime() {
		return pauseTime;
	}
	public void setPauseTime(Integer pauseTime) {
		this.pauseTime = pauseTime;
	}
	public List<GetKeyConfig> getGetkeyconfig() {
		return getkeyconfig;
	}
	public void setGetkeyconfig(List<GetKeyConfig> getkeyconfig) {
		this.getkeyconfig = getkeyconfig;
	}
	public List<GetKeyConfig> getGetSmskeyconfigs() {
		return getSmskeyconfigs;
	}
	public void setGetSmskeyconfigs(List<GetKeyConfig> getSmskeyconfigs) {
		this.getSmskeyconfigs = getSmskeyconfigs;
	}
	public List<Smstaskstep> getQueryTrafficSteps() {
		return queryTrafficSteps;
	}
	public void setQueryTrafficSteps(List<Smstaskstep> queryTrafficSteps) {
		this.queryTrafficSteps = queryTrafficSteps;
	}
	public List<GetKeyConfig> getQueryTrafficKeyconfig() {
		return queryTrafficKeyconfig;
	}
	public void setQueryTrafficKeyconfig(List<GetKeyConfig> queryTrafficKeyconfig) {
		this.queryTrafficKeyconfig = queryTrafficKeyconfig;
	}
	public List<GetKeyConfigDetail> getQueryTrafficKeyconfigDetail() {
		return queryTrafficKeyconfigDetail;
	}
	public void setQueryTrafficKeyconfigDetail(
			List<GetKeyConfigDetail> queryTrafficKeyconfigDetail) {
		this.queryTrafficKeyconfigDetail = queryTrafficKeyconfigDetail;
	}
	public List<Smstaskstep> getAddTrafficSteps() {
		return addTrafficSteps;
	}
	public void setAddTrafficSteps(List<Smstaskstep> addTrafficSteps) {
		this.addTrafficSteps = addTrafficSteps;
	}
	public List<Smstaskstep> getWarnTrafficSteps() {
		return warnTrafficSteps;
	}
	public void setWarnTrafficSteps(List<Smstaskstep> warnTrafficSteps) {
		this.warnTrafficSteps = warnTrafficSteps;
	}
	public String getResponseJsonDate() {
		return responseJsonDate;
	}
	public void setResponseJsonDate(String responseJsonDate) {
		this.responseJsonDate = responseJsonDate;
	}
	public Long getTestsrl() {
		return testsrl;
	}
	public void setTestsrl(Long testsrl) {
		this.testsrl = testsrl;
	}
	public int getStatue() {
		return statue;
	}
	public void setStatue(int statue) {
		this.statue = statue;
	}
	public String getQbmId() {
		return qbmId;
	}
	public void setQbmId(String qbmId) {
		this.qbmId = qbmId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public BamNormalModelPara getBamNormalModel() {
		return bamNormalModel;
	}
	public void setBamNormalModel(BamNormalModelPara bamNormalModel) {
		this.bamNormalModel = bamNormalModel;
	}
	public Long getStrategyId() {
		return strategyId;
	}
	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getTasksetId() {
		return tasksetId;
	}
	public void setTasksetId(Long tasksetId) {
		this.tasksetId = tasksetId;
	}
	public String getTasksetInstanceName() {
		return tasksetInstanceName;
	}
	public void setTasksetInstanceName(String tasksetInstanceName) {
		this.tasksetInstanceName = tasksetInstanceName;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getTrafficTDownloadUrl() {
		return trafficTDownloadUrl;
	}
	public void setTrafficTDownloadUrl(String trafficTDownloadUrl) {
		this.trafficTDownloadUrl = trafficTDownloadUrl;
	}
}
