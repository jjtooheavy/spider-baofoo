package com.xinyan.spider.isp.mobile.model;

/**
 * 基本信息
 * @description 
 * @author heliang
 * @date 2016年8月12日 下午5:02:26
 * @version V1.0
 */
public class BaseInfo {

	private String mobileNo;//本机号码
	private String realName;//真实姓名
	private String registerDate;//入网时间
	private String idCard;//证件号码（身份证号）
	private String address;//地址
	private String vipLevelstr;//用户等级（星级）
	private String email;//邮箱
	private String pointsValuestr;//可用积分
	private String amount;//可用余额
	
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getVipLevelstr() {
		return vipLevelstr;
	}
	public void setVipLevelstr(String vipLevelstr) {
		this.vipLevelstr = vipLevelstr;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPointsValuestr() {
		return pointsValuestr;
	}
	public void setPointsValuestr(String pointsValuestr) {
		this.pointsValuestr = pointsValuestr;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}
