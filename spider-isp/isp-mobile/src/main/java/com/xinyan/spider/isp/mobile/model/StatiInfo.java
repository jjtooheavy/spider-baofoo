package com.xinyan.spider.isp.mobile.model;

/**
 * 统计信息
 * @description 
 * @author heliang
 * @date 2016年8月15日 下午2:44:10
 * @version V1.0
 */
public class StatiInfo {
	
	private String mobileNo ;//与本机通话手机号码
	private String callCount;//与本机通话次数
	
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getCallCount() {
		return callCount;
	}
	public void setCallCount(String callCount) {
		this.callCount = callCount;
	}
}
