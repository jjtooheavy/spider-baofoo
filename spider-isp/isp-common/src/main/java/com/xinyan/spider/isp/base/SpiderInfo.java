package com.xinyan.spider.isp.base;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YuYangjun on 2016/6/27.
 */
public class SpiderInfo {

    //唯一taskId
    private String taskId;
    //姓名
    private String realName;
    //用户识别ID(用户身份证或其他识别ID)
    private String userId;
	//用户名
	private String userName;
    //商户id
    private String merchantId;
    //版本信息
    private String version="1.0.0";
    //创建日期
    private String ctime= new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
