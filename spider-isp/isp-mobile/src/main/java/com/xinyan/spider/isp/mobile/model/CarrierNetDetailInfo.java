package com.xinyan.spider.isp.mobile.model;

import com.xinyan.spider.isp.common.utils.IdentityUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 流量账单明细
 * Created by heliang on 2017/2/10.
 */
@Getter
@Setter
@ToString
public class CarrierNetDetailInfo {

    private String mappingId;//映射id
    private String detailsId;//详情标识
    public CarrierNetDetailInfo() {
        this.detailsId = IdentityUtils.getUUID();
    }

    private String billMonth;//流量月份，格式：yyyy-MM
    private String time;//流量使用时间 ，格式：yyyy-MM-dd HH:mm:ss
    private int duration;//流量使用时长
    private int subflow;//流量使用量，单位:KB
    private String location;//流量使用地点
    private String netType;//网络类型
    private String serviceName;//业务名称
    private int fee;//通信费(单位:分)
}
