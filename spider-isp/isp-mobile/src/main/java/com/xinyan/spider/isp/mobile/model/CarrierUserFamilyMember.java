package com.xinyan.spider.isp.mobile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户亲情号码信息
 * Created by heliang on 2017/2/9.
 */
@Getter
@Setter
@ToString
public class CarrierUserFamilyMember {
    /** 映射id */
    private String mappingId;
    /**  	亲情网编号 */
    private String familyNum;
    /** 长号 */
    private String longNumber;
    /** 短号 */
    private String shortNumber;
    /** 成员类型. MASTER-家长, MEMBER-成员 */
    private String memberType;
    /** 加入日期, 格式yyyy-MM-dd */
    private String joinDate;
    /** 失效日期, 格式yyyy-MM-dd */
    private String expireDate;
}
