package com.xinyan.spider.isp.base;

/**
 * @Description:数据抓取编码
 * @author: york
 * @date: 2016-09-02 11:02
 * @version: v1.0
 */
public enum ProcessorCode {

    //基本信息
    BASIC_INFO("basic_info"),
    //积分
    POINTS_VALUE("points_value"),
    //余额
    AMOUNT("amount"),
    //入网时间
    REGISTER_DATE("registerDate"),
    //通话记录
    CALLRECORD_INFO("callrecord_info"),
    //短信记录
    SMS_INFO("sms_info"),
    //上网记录
    NET_INFO("net_info"),
    //账单信息记录
    BILL_INFO("bill_info"),
    //套餐信息
    PACKAGE_ITEM("package_item"),
    //充值记录
    RECHARGE_INFO("recharge_info"),
    //业务办理记录
    BUSINESS_INFO("business_info"),
    //星级
    VIP_LVL("vip_lvl"),
    //真实姓名
    REAL_NAME("real_name"),
    //其他
    OTHER_INFO("other_info");

    private String code;

    private ProcessorCode(String code){
        this.code=code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
