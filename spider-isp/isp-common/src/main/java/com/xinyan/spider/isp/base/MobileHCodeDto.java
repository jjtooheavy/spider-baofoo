package com.xinyan.spider.isp.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by johnny on 2017/6/29.
 */
@Getter
@Setter
@ToString
public class MobileHCodeDto {
    //运营商名称
    private String carrierName;

    //城市名称
    private String cityName;

    //手机H码
    private String mobileHCode;

    //省会编码
    private String provTelCode;

    //地区编码
    private String telCode;
}
