package com.xinyan.spider.isp.base.http;

/**
 * Created by heliang on 2017/3/8.
 */
public enum TaskStatus {

    START("START","开始"),
    LOGIN_SUCCESS("LOGIN_SUCCESS","登陆成功"),
    LOGIN_WAIT("LOGIN_WAIT","登陆等待"),
    CRAWLING_SUCCESS("CRAWLING_SUCCESS","爬取成功"),
    CRAWLING_FAILED("CRAWLING_FAILED","爬取失败"),
    CRAWLING("CRAWLING","爬取中"),
    CRAWLING_WAIT("CRAWLING_WAIT","等待验证码"),
    ;

   //登陆中（login）、登陆成功（loginSuccess）、登录失败（loginFailed）、爬取中（crawling）、爬取成功（crawlingSuccess）、爬取失败(crawlingFailed)、解析中（parsing）、解析成功（parsingSuccess）、解析失败（parsingFailure）、完成（complete）

    private String code;
    private String status;

    TaskStatus(String code, String status){
        this.status = status;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
