package com.xinyan.spider.isp.base;

/**
 * CharsetCode
 *
 * @author Yu Yangjun
 * @date 2016/7/3
 */
public enum  CharsetCode {

    UTF8("UTF-8"),GB2312("gb2312"),GBK("gbk");

    private String code;

    private CharsetCode(String code){
        this.code=code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}

