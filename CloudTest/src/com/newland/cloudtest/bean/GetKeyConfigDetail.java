package com.newland.cloudtest.bean;

/**
 * GetKeyConfigDetail domain对象
 */
public class GetKeyConfigDetail  {

	/**
	 * @fields serialVersionUID 
	 */
	private static final long serialVersionUID = 1680528725540605902L;

	/**
	 * @fields id 自增ID
	 */
	private java.lang.Long id;
	/**
	 * @fields pid 与主表ID关联
	 */
	private java.lang.Long pid;
	/**
	 * @fields beforekeywords 关键字前字符串
	 */
	private java.lang.String beforekeywords;
	/**
	 * @fields afterkeywords 关键字后字符串
	 */
	private java.lang.String afterkeywords;
	/**
	 * @fields keywordslen 关键字长度
	 */
	private java.lang.Long keywordslen;
	/**
	 * @fields symbol 符号(+,-)，标识获取值的正负（主要针对余额查询）
	 */
	private java.lang.String symbol;
	/**
	 * @fields valueinfo 关键字内容
	 */
	private java.lang.String valueinfo;

	public java.lang.Long getId(){
		return this.id;
	}
	public void setId(java.lang.Long id){
		this.id = id;
	}
	public java.lang.Long getPid(){
		return this.pid;
	}
	public void setPid(java.lang.Long pid){
		this.pid = pid;
	}
	public java.lang.String getBeforekeywords(){
		return this.beforekeywords;
	}
	public void setBeforekeywords(java.lang.String beforekeywords){
		this.beforekeywords = beforekeywords;
	}
	public java.lang.String getAfterkeywords(){
		return this.afterkeywords;
	}
	public void setAfterkeywords(java.lang.String afterkeywords){
		this.afterkeywords = afterkeywords;
	}
	public java.lang.Long getKeywordslen(){
		return this.keywordslen;
	}
	public void setKeywordslen(java.lang.Long keywordslen){
		this.keywordslen = keywordslen;
	}
	public java.lang.String getSymbol(){
		return this.symbol;
	}
	public void setSymbol(java.lang.String symbol){
		this.symbol = symbol;
	}
	public java.lang.String getValueinfo(){
		return this.valueinfo;
	}
	public void setValueinfo(java.lang.String valueinfo){
		this.valueinfo = valueinfo;
	}


}
