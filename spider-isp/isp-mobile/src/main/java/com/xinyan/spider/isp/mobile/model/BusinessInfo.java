package com.xinyan.spider.isp.mobile.model;

/**
 * 办理业务信息
 * @description 
 * @author heliang
 * @date 2016年8月15日 下午2:42:00
 * @version V1.0
 */
public class BusinessInfo {
	
	private String mobileNo;//本机号码
	private String businessName;//业务名称
	private String beginTime;//业务开始时间
	private String cost;//业务消费
	
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
}
