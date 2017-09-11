package com.xinyan.spider.isp.base;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yu Yangjun on 2016/6/27.
 */
public class BaseEntry {
	
    //数据唯一token
    private String token;
    //用户名
    private String userName;
    //密码
    private String password;
    //商户id
    private String merchantId;
    //地区
    private String area;
    //版本信息
    private String version="1.0.0";
    //创建日期
    private String createDate= new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

	public String getUserName() {
        String userNameHide = "";
        if(userName != null){
            userNameHide = userName.replaceAll("(\\w{3})(\\w{4})","$1****");
        }
        return userNameHide;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
        String passwordHide = "";
        if(password != null){
            passwordHide = password.replaceAll("\\w", "*");
        }
		return passwordHide;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
