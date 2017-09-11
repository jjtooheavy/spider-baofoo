package com.xinyan.spider.isp.mobile.model;

/**
 * 上网信息
 * @description 
 * @author heliang
 * @date 2016年8月15日 下午2:41:30
 * @version V1.0
 */
public class NetInfo {
	
	private String mobileNo;//本机号码
	private String place;//上网地点
	private String netTime;//上网时间
	private String onlineTime;//上网时长
	private String netType;//上网类型
	
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getNetTime() {
		return netTime;
	}
	public void setNetTime(String netTime) {
		this.netTime = netTime;
	}
	public String getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(String onlineTime) {
		this.onlineTime = onlineTime;
	}
	public String getNetType() {
		return netType;
	}
	public void setNetType(String netType) {
		this.netType = netType;
	}
}
