package com.xinyan.spider.isp.base;

/**
 * AccTypeCode
 *
 * @author Yu Yangjun
 * @date 2016/7/21
 */
public enum  AccTypeCode {

    IENTITY_NO("1"),PERSON_NO("2"),USER_NAME("3"),HOUSEFUND_NO("4"),CARD("5");

    private String code;

    private AccTypeCode(String code){
        this.code=code;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code=code;
    }
}
