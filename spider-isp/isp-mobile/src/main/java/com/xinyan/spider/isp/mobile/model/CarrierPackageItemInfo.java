package com.xinyan.spider.isp.mobile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 套餐项目信息
 * Created by heliang on 2017/2/9.
 */
@Getter
@Setter
@ToString
public class CarrierPackageItemInfo {
    
    private String mappingId;//映射id 
    private String item;//套餐项目名称 
    private String total;//项目总量 
    private String used;//项目已使用量 
    private String unit;//单位：语音-分; 流量-KB; 短/彩信-条
    private String billStartDate;//账单起始日, 格式为yyyy-MM-dd 
    private String billEndDate;//账单结束日, 格式为yyyy-MM-dd 
}
