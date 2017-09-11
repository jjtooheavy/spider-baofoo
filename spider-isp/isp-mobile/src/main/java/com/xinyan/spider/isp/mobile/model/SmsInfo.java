package com.xinyan.spider.isp.mobile.model;

/**
 * 短信信息
 * @description 
 * @author heliang
 * @date 2016年8月15日 下午2:37:17
 * @version V1.0
 */
public class SmsInfo {

	private String mobileNo;//本机号码
	private String sendSmsToTelCode;//与本机通话手机号码
	private String sendSmsAddress;//发送地
	private String sendSmsTime;//发送时间
	private String sendType;//发送类型【接收、发送】
	
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getSendSmsToTelCode() {
		return sendSmsToTelCode;
	}
	public void setSendSmsToTelCode(String sendSmsToTelCode) {
		this.sendSmsToTelCode = sendSmsToTelCode;
	}
	public String getSendSmsAddress() {
		return sendSmsAddress;
	}
	public void setSendSmsAddress(String sendSmsAddress) {
		this.sendSmsAddress = sendSmsAddress;
	}
	public String getSendSmsTime() {
		return sendSmsTime;
	}
	public void setSendSmsTime(String sendSmsTime) {
		this.sendSmsTime = sendSmsTime;
	}
	public String getSendType() {
		return sendType;
	}
	public void setSendType(String sendType) {
		this.sendType = sendType;
	}
}
