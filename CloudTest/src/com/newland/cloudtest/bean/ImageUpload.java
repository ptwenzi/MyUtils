package com.newland.cloudtest.bean;
/**
 * 图片验证码上传
 * @author TongLee
 *
 */
public class ImageUpload {

	private Long strategyid;
	private Long groupid;
	private Long tasksetid;
	private String tasksetinstancename;
	private Long taskid;
	private Long testsrl;
	public Long getStrategyid() {
		return strategyid;
	}
	public void setStrategyid(Long strategyid) {
		this.strategyid = strategyid;
	}
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	public Long getTasksetid() {
		return tasksetid;
	}
	public void setTasksetid(Long tasksetid) {
		this.tasksetid = tasksetid;
	}
	public String getTasksetinstancename() {
		return tasksetinstancename;
	}
	public void setTasksetinstancename(String tasksetinstancename) {
		this.tasksetinstancename = tasksetinstancename;
	}
	public Long getTaskid() {
		return taskid;
	}
	public void setTaskid(Long taskid) {
		this.taskid = taskid;
	}
	public Long getTestsrl() {
		return testsrl;
	}
	public void setTestsrl(Long testsrl) {
		this.testsrl = testsrl;
	}
	
}
