package com.xinyan.spider.isp.mobile.parser.telecom;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xinyan.spider.isp.common.utils.PageUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.mobile.model.CarrierBillDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierCallDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.model.CarrierNetDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierSmsRecordInfo;
import com.xinyan.spider.isp.mobile.model.CarrierUserRechargeItemInfo;

/**
 * 上海电信数据解析类
 * <b>特殊处理<b/>
 * @author yyj
 * @date 2017年5月4日 下午4:33:47
 * @version V1.0
 */
@Component
public class ShangHaiTelecomParser {
	
	/**
	 * 获取记录数
	 * @param cd
	 * @return
	 */
	public int getSumRow(JSONObject cd){
		JSONObject resultMap = cd.getJSONObject("RESULT");
		int sumRow = 0;
		if(null != resultMap){
			JSONArray pagedResult = resultMap.getJSONArray("pagedResult");
			if(null != pagedResult && CollectionUtils.isNotEmpty(pagedResult)){//有记录
				JSONObject d = pagedResult.getJSONObject(0);
				try{
					sumRow = d.getIntValue("sumRow");
				}catch(Exception e){}
			}
		}
		return sumRow;
	}
	
    /**
     * 解析通话记录
     * @description 
     * @author yyj
     * @create 2016年8月19日 上午11:29:49
     * @param cd
     * @param ci
     * @return
     */
	public void callRecordParse(JSONObject cd, CarrierInfo ci){
		JSONObject resultMap = cd.getJSONObject("RESULT");
		if(null != resultMap){
			JSONArray pagedResult = resultMap.getJSONArray("pagedResult");
			if(null != pagedResult && CollectionUtils.isNotEmpty(pagedResult)){//有通话记录
				JSONObject d;
				CarrierCallDetailInfo ccdi;
				for (int j = 1; j < pagedResult.size(); j++) {
					d = (JSONObject) pagedResult.get(j);
					ccdi = new CarrierCallDetailInfo();
					ccdi.setMappingId(ci.getMappingId());//映射id
					String startDate = d.getString("beginTime");
					ccdi.setTime(startDate);//通话时间，格式：yyyy-MM-dd HH:mm:ss
					ccdi.setPeerNumber(d.getString("targetParty"));//对方号码
					ccdi.setLocation(d.getString("callingPartyVisitedCity"));//通话地(自己的)
					ccdi.setLocationType(d.getString("longDistanceType"));//通话地类型. e.g.省内漫游
					String callDuriation = d.getString("callDuriation");
					String[] durs = RegexUtils.matchMutiValue("(\\d+)小?时(\\d+)分(\\d+)秒", callDuriation);
					int dur = 0;
					if(durs.length > 2){
						dur += Integer.parseInt(durs[0]) * 3600;
						dur += Integer.parseInt(durs[1]) * 60;
						dur += Integer.parseInt(durs[2]);
					}
					ccdi.setDuration(dur+"");//通话时长(单位:秒)

					String callType = d.getString("callType");
					if("主叫".equals(callType)){
						ccdi.setDialType("DIAL");//DIAL-主叫; DIALED-被叫
					}else if("被叫".equals(callType)){
						ccdi.setDialType("DIALED");//DIAL-主叫; DIALED-被叫
					}
					Double totalFee = Double.parseDouble(d.getString("totalFee")) * 100;
					ccdi.setFee(totalFee.intValue());//通话费(单位:分)
					ccdi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//通话月份

					ci.getCalls().add(ccdi);
				}
			}
		}
	}
	
	/**
	 * 解析短信记录
	 * @description 
	 * @author yyj
	 * @create 2016年8月19日 上午11:29:49
	 * @param sms
	 * @param ci
	 * @return
	 */
	public void smsParse(JSONObject sms, CarrierInfo ci){
		JSONObject resultMap = sms.getJSONObject("RESULT");
		if(null != resultMap){
			JSONArray pagedResult = resultMap.getJSONArray("pagedResult");
			if(null != pagedResult && CollectionUtils.isNotEmpty(pagedResult)){//有通话记录
				JSONObject d;
				CarrierSmsRecordInfo csri;
				for (int j = 1; j < pagedResult.size(); j++) {
					d = (JSONObject) pagedResult.get(j);
					csri = new CarrierSmsRecordInfo();
					csri.setMappingId(ci.getMappingId());//映射id
					String startDate = d.getString("beginTime");
					csri.setTime(startDate);//收/发短信时间
					csri.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//通话月份
					csri.setPeerNumber(d.getString("targetParty"));//对方号码
					csri.setLocation("");//通话地(自己的)
					String callType = d.getString("callType");
					if("主叫".equals(callType)){
						csri.setSendType("SEND");//SEND-发送; RECEIVE-收取
					}else if("被叫".equals(callType)){
						csri.setSendType("RECEIVE");//SEND-发送; RECEIVE-收取
					}else{
						csri.setMsgType(callType);//SMS-短信; MMS-彩信
					}
					csri.setMsgType("SMS");//SMS-短信; MMS-彩信
					csri.setServiceName("");//业务名称. e.g. 点对点(网内)
					Double fee1 = Double.parseDouble(d.getString("fee1")) * 100;
					csri.setFee(fee1.intValue());//通话费(单位:分)

					ci.getSmses().add(csri);
				}
			}
		}
	}
	
	/**
	 * 解析上网记录
	 * @description 
	 * @author yyj
	 * @create 2016年8月19日 上午11:29:49
	 * @param net
	 * @param ci
	 * @return
	 */
	public void netParse(JSONObject net, CarrierInfo ci){
		JSONObject resultMap = net.getJSONObject("RESULT");
		if(null != resultMap){
			JSONArray pagedResult = resultMap.getJSONArray("pagedResult");
			if(null != pagedResult && CollectionUtils.isNotEmpty(pagedResult)){//有上网记录
				JSONObject d;
				CarrierNetDetailInfo cndi;
				for (int j = 1; j < pagedResult.size(); j++) {
					d = (JSONObject) pagedResult.get(j);
					cndi = new CarrierNetDetailInfo();
					cndi.setMappingId(ci.getMappingId());//映射id
					String startDateNew = d.getString("beginTime");
					cndi.setTime(startDateNew);//流量使用时间，格式：yyyy-MM-dd HH:mm:ss
					cndi.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));//通话月份
					String duration = d.getString("duration");
					String[] durs = RegexUtils.matchMutiValue("(\\d+)小?时(\\d+)分(\\d+)秒", duration);
					int dur = 0;
					if(durs.length > 2){
						dur += Integer.parseInt(durs[0]) * 3600;
						dur += Integer.parseInt(durs[1]) * 60;
						dur += Integer.parseInt(durs[2]);
					}
					cndi.setDuration(dur);//流量使用时长
					
					Double db = new Double(d.getString("unitTimes"));
					cndi.setSubflow(db.intValue());//流量使用量，单位:KB
					cndi.setLocation(d.getString("internetSite"));//流量使用地点
					cndi.setNetType(d.getString("cdmaChargingType"));//网络类型
					cndi.setServiceName(d.getString("cdmaChargingType"));//业务名称
					Double fee1 = Double.parseDouble(d.getString("fee1")) * 100;
					cndi.setFee(fee1.intValue());//通信费(单位:分)
					ci.getNets().add(cndi);
				}
			}
		}
	}

	/**
	 * 解析账单信息
	 * @param billPage
	 * @param ci
	 */
	public void billParse(Page billPage, CarrierInfo ci){
		CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
	    cbdi.setMappingId(ci.getMappingId());//映射id
	    String billDate = PageUtils.getValueByXpath(billPage, "//div[@class='fl account-overview-r']/div[3]/label[2]");
	    String[] tmp = billDate.split("—");
	    if(null != tmp && tmp.length>1){
	    	String startDate = tmp[0].replaceAll("\\.", "-");
	    	cbdi.setBillStartDate(startDate);//账期起始日期，格式：yyyy-MM-dd
	    	cbdi.setBillEndDate(tmp[1].replaceAll("\\.", "-"));//账期结束日期，格式：yyyy-MM-dd
	    	cbdi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
	    	Double fee;
	    	String sfee = RegexUtils.matchValue("月基本费（小计：&#165;(.*?)元）", billPage.getWebResponse().getContentAsString());
	    	if(StringUtils.isNotEmpty(sfee)){
	    		fee = new Double(sfee) * 100;
	    		cbdi.setBaseFee(fee.intValue());//套餐及固定费
	    	}else{
	    		sfee = RegexUtils.matchValue("月基本费（小计：&#(.*?);）", billPage.getWebResponse().getContentAsString());
	    		if(StringUtils.isNotEmpty(sfee)){
	    			fee = new Double(sfee) * 100;
	    			cbdi.setBaseFee(fee.intValue());//套餐及固定费
	    		}
	    	}
	    	
	    	List<HtmlElement> tmps = PageUtils.getElementByXpath(billPage, "//div[@class='bill-list']/div[@class='bill-list-bd']");
	    	for(HtmlElement e : tmps){
	    		String title = PageUtils.getValueByXpath(e, "div[@class='bill-title']/p[@class='bill-title-bd']");
	    		sfee = PageUtils.getValueByXpath(e, "div[@class='bill-price']/p[@class='bill-price-bd']");
	    		sfee.replace("-", "");
	    		if(StringUtils.isEmpty(title)){
	    			continue;
	    		}else if(title.contains("语音费")||title.contains("通话费")){
	    			if(StringUtils.isNotEmpty(sfee)){
	    				fee = new Double(RegexUtils.matchValue("(.*?)元", sfee)) * 100;
	    				cbdi.setVoiceFee(cbdi.getVoiceFee() + fee.intValue());//语音费
	    			}
	    		}else if(title.contains("短信费")){
	    			if(StringUtils.isNotEmpty(sfee)){
	    				fee = new Double(RegexUtils.matchValue("(.*?)元", sfee)) * 100;
	    				cbdi.setSmsFee(cbdi.getSmsFee() + fee.intValue());//短彩信费
	    			}
	    		}else if(title.contains("上网费")){
	    			if(StringUtils.isNotEmpty(sfee)){
	    				fee = new Double(RegexUtils.matchValue("(.*?)元", sfee)) * 100;
	    				cbdi.setWebFee(cbdi.getWebFee() + fee.intValue());//网络流量费
	    			}
	    		}else if(title.contains("其它费") || title.contains("零头费")){
	    			if(StringUtils.isNotEmpty(sfee)){
	    				fee = new Double(RegexUtils.matchValue("(.*?)元", sfee)) * 100;
	    				cbdi.setExtraFee(cbdi.getExtraFee() + fee.intValue());//其它费用
	    			}
	    		}else if(title.contains("赠款抵扣")){
	    			sfee = sfee.replace("-", "");
	    			if(StringUtils.isNotEmpty(sfee)){
	    				fee = new Double(RegexUtils.matchValue("(.*?)元", sfee)) * 100;
	    				cbdi.setDiscount(fee.intValue());//优惠费
	    			}
	    		}
	    	}
	    	
	    	List<String> t = PageUtils.getListValueByXpath(billPage, "//div[@class='cur-table gutter-mtb']/table/tbody/tr/td");
	    	if(CollectionUtils.isNotEmpty(t) && t.size()>1){
	    		sfee = t.get(0);
	    		sfee.replace("-", "");
	    		if(StringUtils.isNotEmpty(sfee)){
	    			fee = new Double(RegexUtils.matchValue("(.*?)元", sfee)) * 100;
	    			cbdi.setUnpaidFee(fee.intValue());//本期未付费用 单位分
	    		}
	    		sfee = t.get(6);
	    		if(StringUtils.isNotEmpty(sfee)){
	    			sfee = sfee.replace(" -", "");
	    			fee = new Double(RegexUtils.matchValue("(.*?)元", sfee)) * 100;
	    			cbdi.setPaidFee(fee.intValue());//本期已付费用
	    		}
	    		
	    	}
	    	cbdi.setTotalFee(cbdi.getBaseFee() + cbdi.getVoiceFee() + cbdi.getSmsFee() + cbdi.getWebFee() + cbdi.getExtraFee());//总费用 单位分
	    	cbdi.setRelatedMobiles("");//本手机关联号码, 多个手机号以逗号分隔
	    	cbdi.setNotes("");//备注
	    	ci.getBills().add(cbdi);
	    }
	}
	
	/**
	 * 解析充值记录
	 * @description
	 * @author yyj
	 * @create 2016年8月19日 上午11:29:49
	 * @param recharge
	 * @param ci
	 * @return
	 */
	public int rechargeParse(JSONObject recharge, CarrierInfo ci){
		int sumRow = recharge.getIntValue("count");
		JSONArray list = recharge.getJSONArray("list");
		if(null != list && CollectionUtils.isNotEmpty(list)){//有充值记录
			JSONObject d;
			CarrierUserRechargeItemInfo curi;
			
			for (int j = 0; j < list.size(); j++) {
				d = (JSONObject) list.get(j);

				curi = new CarrierUserRechargeItemInfo();
				curi.setMappingId(ci.getMappingId());//映射id
				String startDate = d.getString("partnerTransDate");
				curi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
				curi.setRechargeTime(startDate);//充值时间，格式：yyyy-MM-dd HH:mm:ss
				curi.setBillMonth(d.getString("partnerTransDate").substring(0,7));
				Double totalFee = Double.parseDouble(d.getString("storeInAmount")) * 100;
				curi.setAmount(totalFee.intValue());//充值金额(单位: 分) 
				curi.setType(d.getString("officeName"));//充值方式. e.g. 现金 
				
				ci.getRecharges().add(curi);
			}
		}
		return sumRow;
	}
	
}
