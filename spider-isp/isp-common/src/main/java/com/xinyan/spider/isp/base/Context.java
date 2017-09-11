package com.xinyan.spider.isp.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heliang on 2016/6/15.
 */
@Getter
@Setter
@ToString
public class Context {
    /**
     * 映射id
     */
    private String mappingId;
    //唯一taskId
    private String taskId;
    //任务状态
    private String taskStatus;
    //任务子状态 用于判断从什么地方开始执行代码,由爬虫传给网关,再回传即可
    private String taskSubStatus;
    //业务类型
    private String taskType;
    //业务子类类型
    private String taskSubType;

    //用户名
    private String userName;
    //密码
    private String password;
    //身份证号码
    private String idCard;
    //身份证姓名
    private String idName;

    //用户识别ID(用户身份证或其他识别ID)
    private String userId;
    //商户id
    private String merchantId;

    //账户类型
    private String accType;
    //地区(省份)
    private String area;
    //附加信息
    private String attachment;
    //通知地址
    private String notifyUrl;

    //验证码值
    private String userInput;

    //查询记录次数（如运营商账单数）
    private Integer recordSize = 6;

    private final Map<String, Object> map = new HashMap<>();
    //备用参数1

    //手机H码
    private MobileHCodeDto mobileHCodeDto = new MobileHCodeDto();

    private String param1;
    //备用参数2
    private String param2;
    //备用参数3
    private String param3;
    //备用参数4
    private String param4;
    //备用参数5
    private String param5;
    //备用参数6
    private String param6;
}
