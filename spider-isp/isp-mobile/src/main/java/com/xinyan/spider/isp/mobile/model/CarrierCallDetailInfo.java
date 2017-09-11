package com.xinyan.spider.isp.mobile.model;

import com.xinyan.spider.isp.common.utils.IdentityUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 运营商通话详情
 * Created by heliang on 2017/2/10.
 */
@Getter
@Setter
@ToString
public class CarrierCallDetailInfo {

    private String mappingId;//映射id
    private String detailsId;//详情唯一标识

    public CarrierCallDetailInfo() {
        this.detailsId = IdentityUtils.getUUID();
    }

    private String billMonth;//通话月份，格式：yyyy-MM
    private String time;//通话时间，格式：yyyy-MM-dd HH:mm:ss
    private String peerNumber;//对方号码
    private String location;//通话地(自己的)
    private String locationType;//通话地类型. e.g.省内漫游
    private String duration;//通话时长(单位:秒)
    private String dialType;//DIAL-主叫; DIALED-被叫
    private int fee;//通话费(单位:分)
}
