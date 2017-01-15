package com.newland.cloudtest.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 配置参数表
 * @author TongLee
 *
 */
@Table(name = "config_db")
public class ConfigDb {
	 @Column(name = "ID", isId = true, autoGen = true)
	 private int id;
	 @Column(name = "key")
	 private String key;//
	 @Column(name = "value")
	 private int value;//频率
	 @Column(name = "desc")
	 private String desc;//说明
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	 
}
