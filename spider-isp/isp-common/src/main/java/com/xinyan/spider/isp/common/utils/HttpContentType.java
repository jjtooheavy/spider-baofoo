package com.xinyan.spider.isp.common.utils;

/**
 * http请求枚举
 * Created by Lijing on 2017/2/14.
 */
public enum HttpContentType {

    DEFAULT("DEFAULT","DEFAULT"),
    JSON("JSON","application/json");

    private String code;
    private String content;

    HttpContentType(String code, String content){
        this.code = code;
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
