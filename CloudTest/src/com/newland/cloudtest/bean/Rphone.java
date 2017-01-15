package com.newland.cloudtest.bean;

/**
 * 移动终端资源表 domain对象
 */
public class Rphone  {


	/**
	 * @fields rid 终端序列
	 */
	private java.lang.Long rid;
	
	private String brandName;
	private String modelName;

	private String screenResolution;
	
	
	/**
	 * @fields rphoneType 品牌分类
	 */
	private java.lang.Long rphoneType;
	/**
	 * @fields rphoneName 终端名称
	 */
	private java.lang.String rphoneName;
	/**
	 * @fields imeiNumber 终端串号
	 */
	private java.lang.String imeiNumber;
	/**
	 * @fields phoneGroup 终端分类（1.真机短厅测试，2.真机WAP厅测试、3.真机微信营业厅测试，4.综合测试,5.4G流量监控51：自动化测试，52：拨测类型）
	 */
	private java.lang.Integer phoneGroup;
	/**
	 * @fields hasSimcard 是否手机卡
	 */
	private java.lang.Integer hasSimcard;
	/**
	 * @fields imsiNumber 手机卡imsi号
	 */
	private java.lang.String imsiNumber;
	/**
	 * @fields isConnect 是否接入终端(1.接入，2.未接入)
	 */
	private java.lang.Integer isConnect;
	/**
	 * @fields connectStatus 接入状态（1.在线、2.离线）
	 */
	private java.lang.Integer connectStatus;
	/**
	 * @fields usingStatus 使用状态（0.闲置、1.占用、2.维护、99.-- 离线状态使用）
	 */
	private java.lang.Integer usingStatus;
	/**
	 * @fields computerIp 连接的探测终端IP
	 */
	private java.lang.String computerIp;
	/**
	 * @fields osName 操作系统
	 */
	private java.lang.String osName;
	/**
	 * @fields osVersion 操作系统版本号
	 */
	private java.lang.String osVersion;
	/**
	 * @fields cpu cpu型号
	 */
	private java.lang.String cpu;
	/**
	 * @fields ram 内存大小
	 */
	private java.lang.Long ram;
	/**
	 * @fields rom 存储大小
	 */
	private java.lang.Long rom;
	/**
	 * @fields cpuUse cpu使用率
	 */
	private java.lang.Integer cpuUse;
	/**
	 * @fields ramUse 内存使用率
	 */
	private java.lang.Integer ramUse;
	/**
	 * @fields romUse 存储使用率
	 */
	private java.lang.Integer romUse;
	/**
	 * @fields batteryUse 电量使用百分比
	 */
	private java.lang.Integer batteryUse;
	/**
	 * @fields batteryVoltage 电压，单位MV
	 */
	private java.lang.Integer batteryVoltage;
	/**
	 * @fields batteryTemp 电池温度
	 */
	private java.lang.Integer batteryTemp;
	/**
	 * @fields networkModel 移动网络制式2G/3G/4G/WIFI
	 */
	private java.lang.String networkModel;
	/**
	 * @fields apnType 连接方式（NET/WAP）
	 */
	private java.lang.String apnType;
	/**
	 * @fields dbm 移动信号强度
	 */
	private java.lang.Integer dbm;
	/**
	 * @fields updateTime 最后更新时间
	 */
	private java.util.Date updateTime;
	/**
	 * @fields isUnusual 是否异常（0.正常，1.异常）
	 */
	private java.lang.Integer isUnusual;
	/**
	 * @fields unusualDesc 异常描述
	 */
	private java.lang.String unusualDesc;
	/**
	 * @fields terminalMonitorVersion 监控软件版本
	 */
	private java.lang.String terminalMonitorVersion;
	/**
	 * @fields smsMonitorVersion 短厅探测软件版本
	 */
	private java.lang.String smsMonitorVersion;
	/**
	 * @fields wapMonitorVersion WAP厅探测软件版本
	 */
	private java.lang.String wapMonitorVersion;
	/**
	 * @fields wechatMonitorVersion 微信营业厅探测软件版本
	 */
	private java.lang.String wechatMonitorVersion;
	/**
	 * @fields appVersion UAT测试软件版本
	 */
	private java.lang.String appVersion;
	/**
	 * @fields appDesc 已安装软件信息
	 */
	private java.lang.String appDesc;
	/**
	 * @fields comments 备注
	 */
	private java.lang.String comments;
	
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getScreenResolution() {
		return screenResolution;
	}
	public void setScreenResolution(String screenResolution) {
		this.screenResolution = screenResolution;
	}
	/**
	 * @fields uatStatus uat-状态
	 */
	private java.lang.Integer uatStatus;

	public java.lang.Long getRid(){
		return this.rid;
	}
	public void setRid(java.lang.Long rid){
		this.rid = rid;
	}
	public java.lang.Long getRphoneType(){
		return this.rphoneType;
	}
	public void setRphoneType(java.lang.Long rphoneType){
		this.rphoneType = rphoneType;
	}
	public java.lang.String getRphoneName(){
		return this.rphoneName;
	}
	public void setRphoneName(java.lang.String rphoneName){
		this.rphoneName = rphoneName;
	}
	public java.lang.String getImeiNumber(){
		return this.imeiNumber;
	}
	public void setImeiNumber(java.lang.String imeiNumber){
		this.imeiNumber = imeiNumber;
	}
	public java.lang.Integer getPhoneGroup(){
		return this.phoneGroup;
	}
	public void setPhoneGroup(java.lang.Integer phoneGroup){
		this.phoneGroup = phoneGroup;
	}
	public java.lang.Integer getHasSimcard(){
		return this.hasSimcard;
	}
	public void setHasSimcard(java.lang.Integer hasSimcard){
		this.hasSimcard = hasSimcard;
	}
	public java.lang.String getImsiNumber(){
		return this.imsiNumber;
	}
	public void setImsiNumber(java.lang.String imsiNumber){
		this.imsiNumber = imsiNumber;
	}
	public java.lang.Integer getIsConnect(){
		return this.isConnect;
	}
	public void setIsConnect(java.lang.Integer isConnect){
		this.isConnect = isConnect;
	}
	public java.lang.Integer getConnectStatus(){
		return this.connectStatus;
	}
	public void setConnectStatus(java.lang.Integer connectStatus){
		this.connectStatus = connectStatus;
	}
	public java.lang.Integer getUsingStatus(){
		return this.usingStatus;
	}
	public void setUsingStatus(java.lang.Integer usingStatus){
		this.usingStatus = usingStatus;
	}
	public java.lang.String getComputerIp(){
		return this.computerIp;
	}
	public void setComputerIp(java.lang.String computerIp){
		this.computerIp = computerIp;
	}
	public java.lang.String getOsName(){
		return this.osName;
	}
	public void setOsName(java.lang.String osName){
		this.osName = osName;
	}
	public java.lang.String getOsVersion(){
		return this.osVersion;
	}
	public void setOsVersion(java.lang.String osVersion){
		this.osVersion = osVersion;
	}
	public java.lang.String getCpu(){
		return this.cpu;
	}
	public void setCpu(java.lang.String cpu){
		this.cpu = cpu;
	}
	public java.lang.Long getRam(){
		return this.ram;
	}
	public void setRam(java.lang.Long ram){
		this.ram = ram;
	}
	public java.lang.Long getRom(){
		return this.rom;
	}
	public void setRom(java.lang.Long rom){
		this.rom = rom;
	}
	public java.lang.Integer getCpuUse(){
		return this.cpuUse;
	}
	public void setCpuUse(java.lang.Integer cpuUse){
		this.cpuUse = cpuUse;
	}
	public java.lang.Integer getRamUse(){
		return this.ramUse;
	}
	public void setRamUse(java.lang.Integer ramUse){
		this.ramUse = ramUse;
	}
	public java.lang.Integer getRomUse(){
		return this.romUse;
	}
	public void setRomUse(java.lang.Integer romUse){
		this.romUse = romUse;
	}
	public java.lang.Integer getBatteryUse(){
		return this.batteryUse;
	}
	public void setBatteryUse(java.lang.Integer batteryUse){
		this.batteryUse = batteryUse;
	}
	public java.lang.Integer getBatteryVoltage(){
		return this.batteryVoltage;
	}
	public void setBatteryVoltage(java.lang.Integer batteryVoltage){
		this.batteryVoltage = batteryVoltage;
	}
	public java.lang.Integer getBatteryTemp(){
		return this.batteryTemp;
	}
	public void setBatteryTemp(java.lang.Integer batteryTemp){
		this.batteryTemp = batteryTemp;
	}
	public java.lang.String getNetworkModel(){
		return this.networkModel;
	}
	public void setNetworkModel(java.lang.String networkModel){
		this.networkModel = networkModel;
	}
	public java.lang.String getApnType(){
		return this.apnType;
	}
	public void setApnType(java.lang.String apnType){
		this.apnType = apnType;
	}
	public java.lang.Integer getDbm(){
		return this.dbm;
	}
	public void setDbm(java.lang.Integer dbm){
		this.dbm = dbm;
	}
	public java.util.Date getUpdateTime(){
		return this.updateTime;
	}
	public void setUpdateTime(java.util.Date updateTime){
		this.updateTime = updateTime;
	}
	public java.lang.Integer getIsUnusual(){
		return this.isUnusual;
	}
	public void setIsUnusual(java.lang.Integer isUnusual){
		this.isUnusual = isUnusual;
	}
	public java.lang.String getUnusualDesc(){
		return this.unusualDesc;
	}
	public void setUnusualDesc(java.lang.String unusualDesc){
		this.unusualDesc = unusualDesc;
	}
	public java.lang.String getTerminalMonitorVersion(){
		return this.terminalMonitorVersion;
	}
	public void setTerminalMonitorVersion(java.lang.String terminalMonitorVersion){
		this.terminalMonitorVersion = terminalMonitorVersion;
	}
	public java.lang.String getSmsMonitorVersion(){
		return this.smsMonitorVersion;
	}
	public void setSmsMonitorVersion(java.lang.String smsMonitorVersion){
		this.smsMonitorVersion = smsMonitorVersion;
	}
	public java.lang.String getWapMonitorVersion(){
		return this.wapMonitorVersion;
	}
	public void setWapMonitorVersion(java.lang.String wapMonitorVersion){
		this.wapMonitorVersion = wapMonitorVersion;
	}
	public java.lang.String getWechatMonitorVersion(){
		return this.wechatMonitorVersion;
	}
	public void setWechatMonitorVersion(java.lang.String wechatMonitorVersion){
		this.wechatMonitorVersion = wechatMonitorVersion;
	}
	public java.lang.String getAppVersion(){
		return this.appVersion;
	}
	public void setAppVersion(java.lang.String appVersion){
		this.appVersion = appVersion;
	}
	public java.lang.String getAppDesc(){
		return this.appDesc;
	}
	public void setAppDesc(java.lang.String appDesc){
		this.appDesc = appDesc;
	}
	public java.lang.String getComments(){
		return this.comments;
	}
	public void setComments(java.lang.String comments){
		this.comments = comments;
	}
	public java.lang.Integer getUatStatus(){
		return this.uatStatus;
	}
	public void setUatStatus(java.lang.Integer uatStatus){
		this.uatStatus = uatStatus;
	}


}
