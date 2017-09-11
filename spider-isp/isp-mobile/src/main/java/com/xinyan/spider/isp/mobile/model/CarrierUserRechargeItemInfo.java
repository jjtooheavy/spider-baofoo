package com.xinyan.spider.isp.mobile.model;

import com.xinyan.spider.isp.common.utils.IdentityUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 运营商充值信息
 * Created by heliang on 2017/2/9.
 */
@Getter
@Setter
@ToString
public class CarrierUserRechargeItemInfo {
    public CarrierUserRechargeItemInfo() {
        this.detailsId = IdentityUtils.getUUID();
    }
    private String mappingId;//映射id
    private String detailsId;//详情标识 
    private String billMonth;//充值月份，格式：yyyy-MM
    private String rechargeTime;//充值时间，格式：yyyy-MM-dd HH:mm:ss 
    private int amount;//充值金额(单位: 分) 
    private String type;//充值方式. e.g. 现金 
}
