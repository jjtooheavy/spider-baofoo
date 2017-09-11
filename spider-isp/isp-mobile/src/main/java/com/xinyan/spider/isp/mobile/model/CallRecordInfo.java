package com.xinyan.spider.isp.mobile.model;

import com.xinyan.spider.isp.common.utils.IdentityUtils;

/**
 * 通话详单
 * @description 
 * @author heliang
 * @date 2016年8月12日 下午7:41:38
 * @version V1.0
 */
public class CallRecordInfo {
	private String detailsId;
	private String callAddress;//通话地点
	private String callDateTime;//通话时间
	private String callTimeLength;//通话时长
	private String callType;//通话类型
	private String mobileNo;//对方号码

	public CallRecordInfo() {
		this.detailsId = IdentityUtils.getUUID();
	}

	public String getCallAddress() {
		return callAddress;
	}
	public void setCallAddress(String callAddress) {
		this.callAddress = callAddress;
	}
	public String getCallDateTime() {
		return callDateTime;
	}
	public void setCallDateTime(String callDateTime) {
		this.callDateTime = callDateTime;
	}
	public String getCallTimeLength() {
		return callTimeLength;
	}
	public void setCallTimeLength(String callTimeLength) {
		this.callTimeLength = callTimeLength;
	}
	public String getCallType() {
		return callType;
	}
	public void setCallType(String callType) {
		this.callType = callType;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
}
