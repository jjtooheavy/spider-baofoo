package com.xinyan.spider.isp.mobile.model;

import java.util.ArrayList;
import java.util.List;

import com.xinyan.spider.isp.base.SpiderInfo;
import com.xinyan.spider.isp.common.utils.IdentityUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 运营商信息
 * Created by heliang on 2017/2/10.
 */
@Getter
@Setter
public class CarrierInfo extends SpiderInfo{

    public CarrierInfo(){
        this.mappingId = IdentityUtils.getUUID();
        this.carrierUserInfo.setMappingId(mappingId);
    }

    /** 映射id */
    private String mappingId;
    /**用户信息*/
    private CarrierUserInfo carrierUserInfo = new CarrierUserInfo();
    /** 账单信息 */
    private List<CarrierBillDetailInfo> bills = new ArrayList<>();
    /** 通话信息 */
    private List<CarrierCallDetailInfo> calls = new ArrayList<>();
    /** 短信信息 */
    private List<CarrierSmsRecordInfo> smses = new ArrayList<>();
    /** 流量信息 */
    private List<CarrierNetDetailInfo> nets = new ArrayList<>();
    /** 套餐信息 */
    private List<CarrierPackageItemInfo> packages = new ArrayList<>();
    /** 亲情号码信息 */
    private List<CarrierUserFamilyMember> families = new ArrayList<>();
    /** 充值记录详情明细 */
    private List<CarrierUserRechargeItemInfo> recharges = new ArrayList<>();
    
	@Override
	public String toString() {
		return "运营商信息 [userName=" + getUserName() + "], 账单["+bills.size()+"]条记录, 通话["+calls.size()+"]条记录, "
				+ "短信["+smses.size()+"]条记录, 流量["+nets.size()+"]条记录, 套餐["+packages.size()+"]条记录, "
				+ "亲情号码["+families.size()+"]条记录, 充值["+recharges.size()+"]条记录";
	}
}
