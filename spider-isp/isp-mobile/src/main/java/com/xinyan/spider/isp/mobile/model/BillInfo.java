package com.xinyan.spider.isp.mobile.model;

/**
 * 账单信息
 * @description 
 * @author heliang
 * @date 2016年8月15日 下午2:42:52
 * @version V1.0
 */
public class BillInfo {
	
	private String mobileNo;//本机号码
	private String startTime;//账单月份
	private String comboCost;//套餐消费
	private String sumCost;//总金额
	private String realCost;//实际费用
	
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getComboCost() {
		return comboCost;
	}
	public void setComboCost(String comboCost) {
		this.comboCost = comboCost;
	}
	public String getSumCost() {
		return sumCost;
	}
	public void setSumCost(String sumCost) {
		this.sumCost = sumCost;
	}
	public String getRealCost() {
		return realCost;
	}
	public void setRealCost(String realCost) {
		this.realCost = realCost;
	}
	
}
