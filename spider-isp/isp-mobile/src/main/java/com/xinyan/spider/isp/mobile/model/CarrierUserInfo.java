package com.xinyan.spider.isp.mobile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 运营商用户信息
 * Created by heliang on 2017/2/9.
 */
@Getter
@Setter
@ToString
public class CarrierUserInfo {
	
    private String mappingId;//映射id
    private String mobile;//手机号码
    private String name;//姓名
    private String idCard;//证件号
    private String carrier;//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
    private String province;//所属省份
    private String city;//所属城市
    private String address;//地址
    private String openTime;//入网时间，格式：yyyy-MM-dd
    private String level;//帐号星级
    private String packageName;//套餐名称
    private int state;//帐号状态, -1未知 0正常 1单向停机 2停机 3预销户 4销户 5过户 6改号 99号码不存在
    private int availableBalance;//当前可用余额（单位: 分）
    private String lastModifyTime;//最近一次更新时间，格式: yyyy-MM-dd HH:mm:ss

}
