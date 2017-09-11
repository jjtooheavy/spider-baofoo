package com.xinyan.spider.isp.mobile.model;

import com.xinyan.spider.isp.common.utils.IdentityUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 运营商短信记录明细
 * Created by heliang on 2017/2/10.
 */
@Getter
@Setter
@ToString
public class CarrierSmsRecordInfo {

    private String mappingId;//映射id
    private String detailsId;//详情标识

    public CarrierSmsRecordInfo() {
        this.detailsId = IdentityUtils.getUUID();
    }

    private String billMonth;//通话月份,格式:yyyy-MM
    private String time;//收/发短信时间,格式：yyyy-MM-dd HH:mm:ss
    private String peerNumber;//对方号码
    private String location;//通话地(自己的)
    private String sendType;//SEND-发送; RECEIVE-收取
    private String msgType;//SMS-短信; MMS-彩信
    private String serviceName;//业务名称. e.g. 点对点(网内)
    private int fee;//通话费(单位:分)
}
