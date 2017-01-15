package com.newland.cloudtest.bean;

import java.util.List;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "bamNormalModelPara")
public class BamNormalModelPara {
	@Column(name = "objectId",isId = true)
	private String objectId; //业务标识

	@Column(name = "testType")
	private String testType;
	@Column(name = "modelName")
	private String modelName; //脚本名
	@Column(name = "modeldesc")
	private String modeldesc;
	@Column(name = "runflow")
	private String runflow;
	@Column(name = "enabled")
	private String enabled; //是否有效0：无效 1：有效（默认为1）
	@Column(name = "remainTime")
	private String remainTime; //最长执行时间，超过停止（默认为8）
	@Column(name = "carrier")
	private String carrier; 
	@Column(name = "province")
	private String province;
	@Column(name = "brand")
	private String brand;
	@Column(name = "channel")
	private String channel; //默认为1
	@Column(name = "getValueType")
	private int getValueType; //默认为0
	@Column(name = "note")
	private String note;
	@Column(name = "func")
	private String func; //1：登录脚本；2：余额查询脚本；3：停机验证；4：复机验证；5：充值缴费；6：充值审计（默认为0）
	@Column(name = "isbalance")
	private String isbalance; //是否需要余额判断：1需要；0：不需要；（默认为0）
	@Column(name = "balance")
	private String balance; //余额阀值
	private String overtime;

	private List<BamNormalModelDetailPara> bamNormalModelDetailParas; //脚本明细

	private List<GetKeyConfig> getKeyConfigList;//关键字配置值
	

	
	public List<GetKeyConfig> getGetKeyConfigList() {
		return getKeyConfigList;
	}

	public void setGetKeyConfigList(List<GetKeyConfig> getKeyConfigList) {
		this.getKeyConfigList = getKeyConfigList;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public List<BamNormalModelDetailPara> getBamNormalModelDetailParas() {
		return bamNormalModelDetailParas;
	}

	public void setBamNormalModelDetailParas(
			List<BamNormalModelDetailPara> bamNormalModelDetailParas) {
		this.bamNormalModelDetailParas = bamNormalModelDetailParas;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModeldesc() {
		return modeldesc;
	}

	public void setModeldesc(String modeldesc) {
		this.modeldesc = modeldesc;
	}

	public String getRunflow() {
		return runflow;
	}

	public void setRunflow(String runflow) {
		this.runflow = runflow;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(String remainTime) {
		this.remainTime = remainTime;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getGetValueType() {
		return getValueType;
	}

	public void setGetValueType(int getValueType) {
		this.getValueType = getValueType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public String getIsbalance() {
		return isbalance;
	}

	public void setIsbalance(String isbalance) {
		this.isbalance = isbalance;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getOvertime() {
		return overtime;
	}
	public void setOvertime(String overtime) {
		this.overtime = overtime;
	}
}
