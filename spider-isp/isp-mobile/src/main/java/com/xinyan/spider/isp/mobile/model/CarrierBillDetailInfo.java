package com.xinyan.spider.isp.mobile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 手机账单明细
 * Created by heliang on 2017/2/10.
 */
@Getter
@Setter
@ToString
public class CarrierBillDetailInfo {

    private String mappingId;//映射id
    private String billMonth;//账单月，格式：yyyy-MM
    private String billStartDate;//账期起始日期，格式：yyyy-MM-dd
    private String billEndDate;//账期结束日期，格式：yyyy-MM-dd
    private int baseFee;//套餐及固定费 单位分
    private int extraServiceFee;//增值业务费 单位分
    private int voiceFee;//语音费 单位分
    private int smsFee;//短彩信费 单位分
    private int webFee;//网络流量费 单位分
    private int extraFee;//其它费用 单位分
    private int totalFee;//总费用 单位分
    private int discount;//优惠费 单位分
    private int extraDiscount;//其它优惠 单位分
    private int actualFee;//个人实际费用 单位分
    private int paidFee;//本期已付费用 单位分
    private int unpaidFee;//本期未付费用 单位分
    private int point;//本期可用积分
    private int lastPoint;//上期可用积分
    private String relatedMobiles;//本手机关联号码, 多个手机号以逗号分隔
    private String notes;//备注
}
