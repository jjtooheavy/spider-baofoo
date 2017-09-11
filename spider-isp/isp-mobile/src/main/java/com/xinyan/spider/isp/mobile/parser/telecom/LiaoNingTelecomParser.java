package com.xinyan.spider.isp.mobile.parser.telecom;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.utils.CollectionUtil;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierBillDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierCallDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.model.CarrierNetDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierPackageItemInfo;
import com.xinyan.spider.isp.mobile.model.CarrierSmsRecordInfo;
import com.xinyan.spider.isp.mobile.model.CarrierUserRechargeItemInfo;

/**
 * 辽宁电信数据解析类
 * @description
 * @author yyj
 * @date 2017年6月21日 下午6:33:47
 * @version V1.0
 */
@Component
public class LiaoNingTelecomParser {
	
    protected static Logger logger = LoggerFactory.getLogger(LiaoNingTelecomParser.class);
	
	public Result parse(Context context, CarrierInfo ci, CacheContainer cc){
		Result result = new Result();
		try{
			JSONObject info = new JSONObject();
			JSONArray arrInfo = new JSONArray();
			JSONObject tmp = new JSONObject();
			
			logger.info("==>[{}]正在解析运营商基本信息...", context.getTaskId());
			Page page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
			info = JSONObject.parseObject(page.getWebResponse().getContentAsString());
			if(null != info){
				ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
				ci.getCarrierUserInfo().setProvince("辽宁");//所属省份
				ci.getCarrierUserInfo().setCity(context.getMobileHCodeDto().getCityName());//所属城市
				ci.getCarrierUserInfo().setName(info.getString("userName"));//姓名
				ci.getCarrierUserInfo().setIdCard(info.getString("indentCode"));//证件号
				ci.getCarrierUserInfo().setAddress(info.getString("userAddress"));//地址
				String opneDate = info.getString("acceptDate");
				ci.getCarrierUserInfo().setOpenTime(opneDate.substring(0, 10));//入网时间，格式：yyyy-MM-dd
				ci.getCarrierUserInfo().setLevel(info.getString("custLevelName"));//帐号星级
				ci.getCarrierUserInfo().setPackageName(info.getString("prodName"));//套餐名称
				
				//帐号状态, -1未知 0正常 1单向停机 2停机 3预销户 4销户 5过户 6改号 99号码不存在
				if("正常服务".equals(info.getString("servingStatusName"))){
					ci.getCarrierUserInfo().setState(0);//帐号状态
				}
				ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss"));//最近一次更新时间，格式: yyyy-MM-dd HH:mm:ss
			}
			
			page = cc.getPage(ProcessorCode.AMOUNT.getCode());
			info = JSONObject.parseObject(page.getWebResponse().getContentAsString());
			if(null != info){
				Double amount = Double.parseDouble(info.getString("restFee")) * 100;
				ci.getCarrierUserInfo().setAvailableBalance(amount.intValue());//当前可用余额（单位: 分）
			}
			
			logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
	        List<Page> pages = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
	        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		info = JSONObject.parseObject(p.getWebResponse().getContentAsString());
	        		arrInfo = info.getJSONArray("items");        	
	        		if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        			for (int i = 0; i < arrInfo.size(); i++) {
	        				tmp = (JSONObject) arrInfo.get(i);
	        				ccdi = new CarrierCallDetailInfo(); 
	        				ccdi.setMappingId(ci.getMappingId());//映射id
	        				String startDateNew = tmp.getString("callDate");
	        				ccdi.setTime(startDateNew);//通话时间，格式：yyyy-MM-dd HH:mm:ss
	        				ccdi.setPeerNumber(tmp.getString("counterNumber"));//对方号码
	        				ccdi.setLocation("");//通话地(自己的)
	        				ccdi.setLocationType(tmp.getString("feeName"));//通话地类型. e.g.省内漫游
	        				ccdi.setDuration(tmp.getString("duration"));//通话时长(单位:秒)
	        				String callType = tmp.getString("callType");
	        				if(callType.contains("主叫")){
	        					ccdi.setDialType("DIAL");//DIAL-主叫; DIALED-被叫
	        				}else if(callType.contains("被叫")){
	        					ccdi.setDialType("DIALED");//DIAL-主叫; DIALED-被叫
	        				}else{
	        					ccdi.setDialType(callType);//DIAL-主叫; DIALED-被叫
	        				}
	        				Double totalFee = Double.parseDouble(tmp.getString("tollFee")) * 100;
	        				ccdi.setFee(totalFee.intValue());//通话费(单位:分)
	        				ccdi.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));//通话月份
	        				ci.getCalls().add(ccdi);
	        			}
	        		}
	        	}
	        }
	        
			logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
	        CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		info = JSONObject.parseObject(p.getWebResponse().getContentAsString());
	        		arrInfo = info.getJSONArray("items");        	
	        		if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        			for (int i = 0; i < arrInfo.size(); i++) {
	        				tmp = (JSONObject) arrInfo.get(i);
	        				csri = new CarrierSmsRecordInfo();
	    					csri.setMappingId(ci.getMappingId());//映射id
	    					String startDateNew = tmp.getString("beginDate");
	    					csri.setTime(startDateNew);//收/发短信时间
	    					csri.setPeerNumber(tmp.getString("callPhone"));//对方号码
	    					csri.setLocation("");//通话地(自己的)
	    					String callType = tmp.getString("callType");
	    					if(callType.contains("上行")){
	    						csri.setSendType("SEND");//SEND-发送; RECEIVE-收取
	    					}else if(callType.contains("下行")){
	    						csri.setSendType("RECEIVE");//DIAL-主叫; DIALED-被叫
	    					}
	    					
	    					callType = tmp.getString("inCode");
	    					if(callType.contains("SMS")){
	    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
	    					}else if(callType.contains("MMS")){
	    						csri.setMsgType("MMS");//SMS-短信; MMS-彩信
	    					}else{
	    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
	    					}
	    					csri.setServiceName(tmp.getString("feeKind"));//业务名称. e.g. 点对点(网内)
	    					Double fee1 = Double.parseDouble(tmp.getString("fee")) * 100;
	    					csri.setFee(fee1.intValue());//通话费(单位:分)
	    					csri.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));//通话月份
	        				ci.getSmses().add(csri);
	        			}
	        		}
	        	}
	        }
			
			logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
	        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		info = JSONObject.parseObject(p.getWebResponse().getContentAsString());
	        		if(null != info){
	        			info = info.getJSONObject("cdmaDataQueryResp");
	        			arrInfo = info.getJSONArray("detailGroup");
	        			if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        				for (int i = 0; i < arrInfo.size(); i++) {
	        					tmp = (JSONObject) arrInfo.get(i);
	        					cndi = new CarrierNetDetailInfo();
	        					cndi.setMappingId(ci.getMappingId());//映射id
	        					String startDateNew = tmp.getString("beginDate");
	        					cndi.setTime(startDateNew);//流量使用时间
	        					cndi.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));//通话月份
	        					String duration = tmp.getString("duration");
	        					
	        					int dur = 0;
	        					String durs = RegexUtils.matchValue("(\\d+)小?时", duration);
	        					if(StringUtils.isNotEmpty(durs)){
	        						dur += Integer.parseInt(durs) * 3600;
	        					}
	        					durs = RegexUtils.matchValue("(\\d+)分钟?", duration);
	        					if(StringUtils.isNotEmpty(durs)){
	        						dur += Integer.parseInt(durs) * 60;
	        					}
	        					durs = RegexUtils.matchValue("(\\d+)秒", duration);
	        					if(StringUtils.isNotEmpty(durs)){
	        						dur += Integer.parseInt(durs);
	        					}
	        					cndi.setDuration(dur);//流量使用时长
	        					
	        					duration = tmp.getString("discharge");
	        					durs = RegexUtils.matchValue("(\\d+)GB", duration);
	        					dur = 0;
	        					if(StringUtils.isNotEmpty(durs)){
	        						dur += Integer.parseInt(durs) * 1048576;
	        					}
	        					durs = RegexUtils.matchValue("(\\d+)MB", duration);
	        					if(StringUtils.isNotEmpty(durs)){
	        						dur += Integer.parseInt(durs) * 1024;
	        					}
	        					durs = RegexUtils.matchValue("(\\d+)KB", duration);
	        					if(StringUtils.isNotEmpty(durs)){
	        						dur += Integer.parseInt(durs);
	        					}
	        					cndi.setSubflow(dur);//流量使用量，单位:KB
	        					cndi.setLocation(tmp.getString("onlineCity"));//流量使用地点
	        					cndi.setNetType(tmp.getString("netType"));//网络类型
	        					cndi.setServiceName(tmp.getString("useService"));//业务名称
	        					
	        					duration = tmp.getString("fee").replace("元", "");
	        					Double fee1 = Double.parseDouble(duration) * 100;
	        					cndi.setFee(fee1.intValue());//通信费(单位:分)
	        					ci.getNets().add(cndi);
	        				}
	        			}
	        		}
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
	        CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
	        for (Page p : pages) {
	        	if(null != p){
	          		String billJson = RegexUtils.matchValue("var billJson = (.*?);", p.getWebResponse().getContentAsString());
	          		if(StringUtils.isNotEmpty(billJson)){
	          			info = JSONObject.parseObject(billJson);
	          			cbdi = new CarrierBillDetailInfo();
	          			cbdi.setMappingId(ci.getMappingId());//映射id
	          			String billDate = RegexUtils.matchValue("var billDate = '(.*?)';", p.getWebResponse().getContentAsString());
	          			Date date = DateUtils.stringToDate(billDate, "yyyyMM");
	          			cbdi.setBillStartDate(DateUtils.getFirstDay(date, "yyyy-MM-dd"));//账期起始日期，格式：yyyy-MM-dd
	          			cbdi.setBillEndDate(DateUtils.getLastDay(date, "yyyy-MM-dd"));//账期结束日期，格式：yyyy-MM-dd
	          			cbdi.setBillMonth(DateUtils.dateToString(date, "yyyy-MM"));//账单月，格式：yyyy-MM
	          			
	          			tmp = info.getJSONObject("BillQueryResponse");
	          			
	          			String itemCharge = tmp.getJSONObject("acctFeeInfo").getString("usedFee");
	    				if(StringUtils.isNotEmpty(itemCharge)){
	    					Double fee1 = Double.parseDouble(itemCharge);
	    					cbdi.setTotalFee(fee1.intValue());//总费用 单位分
	    				}
	          			
	          			itemCharge = tmp.getJSONObject("acctFeeInfo").getString("paidFee");
	    				if(StringUtils.isNotEmpty(itemCharge)){
	    					Double fee1 = Double.parseDouble(itemCharge);
	    					cbdi.setPaidFee(fee1.intValue());//本期已付费用 单位分
	    				}
	    				
	    				itemCharge = tmp.getJSONObject("acctFeeInfo").getString("shouldPayFee");
	    				if(StringUtils.isNotEmpty(itemCharge)){
	    					Double fee1 = Double.parseDouble(itemCharge);
	    					cbdi.setUnpaidFee(fee1.intValue());//本期未付费用 单位分
	    				}
	          			
	    				itemCharge = tmp.getJSONObject("pointInfo").getString("thisPoint");
	    				if(StringUtils.isNotEmpty(itemCharge)){
	    					Double fee1 = Double.parseDouble(itemCharge);
	    					cbdi.setPoint(fee1.intValue());//本期可用积分
	    				}
	    				
	    				itemCharge = tmp.getJSONObject("pointInfo").getString("lastPoint");
	    				if(StringUtils.isNotEmpty(itemCharge)){
	    					Double fee1 = Double.parseDouble(itemCharge);
	    					cbdi.setLastPoint(fee1.intValue());//上期可用积分
	    				}
	          			
	          			arrInfo = tmp.getJSONArray("billInfoGroup");
	          			if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	          				cbdi.setRelatedMobiles(arrInfo.getJSONObject(0).getString("serviceInfoStr"));//本手机关联号码, 多个手机号以逗号分隔
	          				arrInfo = arrInfo.getJSONObject(0).getJSONArray("feeInfo");
	          				if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	          					for (int i = 0; i < arrInfo.size(); i++) {
	          						tmp = (JSONObject) arrInfo.get(i);
					    			String itemName = tmp.getString("name");
					    			itemCharge = tmp.getString("value");
					    			if("2".equals(tmp.getString("level"))){
					    				if(itemName.contains("套餐月基本费")){
					    					if(StringUtils.isNotEmpty(itemCharge)){
					    						Double fee1 = Double.parseDouble(itemCharge) * 100;
					    						cbdi.setBaseFee(cbdi.getBaseFee() + fee1.intValue());//套餐及固定费 单位分
					    					}
					    				}else if(itemName.contains("通话费")){
					    					if(StringUtils.isNotEmpty(itemCharge)){
					    						Double fee1 = Double.parseDouble(itemCharge) * 100;
					    						cbdi.setVoiceFee(cbdi.getVoiceFee() + fee1.intValue());//语音费 单位分
					    					}
					    				}else if(itemName.contains("短信费")){
					    					if(StringUtils.isNotEmpty(itemCharge)){
					    						Double fee1 = Double.parseDouble(itemCharge) * 100;
					    						cbdi.setSmsFee(cbdi.getSmsFee() + fee1.intValue());//短彩信费 单位分
					    					}
					    				}else if(itemName.contains("上网费")){
					    					if(StringUtils.isNotEmpty(itemCharge)){
					    						Double fee1 = Double.parseDouble(itemCharge) * 100;
					    						cbdi.setWebFee(cbdi.getWebFee() + fee1.intValue());//网络流量费 单位分
					    					}
					    				}else if(itemName.contains("优惠费")){
					    					if(StringUtils.isNotEmpty(itemCharge)){
					    						Double fee1 = Double.parseDouble(itemCharge) * 100;	        		
					    						cbdi.setDiscount(cbdi.getDiscount() + fee1.intValue());//优惠费 单位分
					    					}
					    				}
					    			}
	          					}
	          				}
	          			}
	          			ci.getBills().add(cbdi);
				    }
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
	        List<String> ps = cc.getStrings(ProcessorCode.PACKAGE_ITEM.getCode());
	        CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
	        for (String pi: ps) {
	        	if(StringUtils.isNotEmpty(pi)){
	        		String[] pis = pi.split("!@!");
	        		if(null != pis && pis.length>2){
	        			arrInfo = JSONObject.parseArray(pis[0]);
	        			if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        				arrInfo = arrInfo.getJSONArray(0);
	        				if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        					for (int i = 0; i < arrInfo.size(); i++) {
	        						tmp = (JSONObject) arrInfo.get(i);
	        						cpti = new CarrierPackageItemInfo();
	        						
	        						String unitName  = tmp.getString("unitName");
	        						cpti.setMappingId(ci.getMappingId());//映射id
	        						cpti.setItem(tmp.getString("accuName"));//套餐项目名称 
	        						String durs = tmp.getString("cumulationTotal");
	        						if(unitName.contains("G")){//流量
	        	    					if(StringUtils.isNotEmpty(durs)){
	        	    						Double fee1 = Double.parseDouble(durs) * 1048576;
	        	    						cpti.setTotal(fee1.intValue() + "");//项目总量 
	        	    					}
	        	    					durs = tmp.getString("cumulationAlready");
	        	    					if(StringUtils.isNotEmpty(durs)){
	        	    						Double fee1 = Double.parseDouble(durs) * 1048576;
	        	    						cpti.setUsed(fee1.intValue() + "");//项目已使用量 
	        	    					}
	        							cpti.setUnit("KB");//单位：语音-分; 流量-KB; 短/彩信-条 
	        						}if(unitName.contains("M")){//流量
	        	    					if(StringUtils.isNotEmpty(durs)){
	        	    						Double fee1 = Double.parseDouble(durs) * 1024;
	        	    						cpti.setTotal(fee1.intValue() + "");//项目总量 
	        	    					}
	        	    					durs = tmp.getString("cumulationAlready");
	        	    					if(StringUtils.isNotEmpty(durs)){
	        	    						Double fee1 = Double.parseDouble(durs) * 1024;
	        	    						cpti.setUsed(fee1.intValue() + "");//项目已使用量 
	        	    					}
	        							cpti.setUnit("KB");//单位：语音-分; 流量-KB; 短/彩信-条 
	        						}else{
	        							cpti.setTotal(tmp.getString("cumulationTotal"));//项目总量 
	        							cpti.setUsed(tmp.getString("cumulationAlready"));//项目已使用量 
	        							cpti.setUnit(tmp.getString("unitName"));//单位：语音-分; 流量-KB; 短/彩信-条 
	        						}
	        						cpti.setBillStartDate(pis[1]);;//账单起始日, 格式为yyyy-MM-dd
	        						cpti.setBillEndDate(pis[2]);;//账单结束日, 格式为yyyy-MM-dd
	        						ci.getPackages().add(cpti);
	        					}
	        				}
	        			}
	        		}
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
	        page = cc.getPage(ProcessorCode.RECHARGE_INFO.getCode());
	        CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
        	if(null != page){
        		info = JSONObject.parseObject(page.getWebResponse().getContentAsString());
        		arrInfo = info.getJSONArray("items");
        		if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
        			for (int i = 0; i < arrInfo.size(); i++) {
        				tmp = (JSONObject) arrInfo.get(i);
        				curi = new CarrierUserRechargeItemInfo();
        				curi.setMappingId(ci.getMappingId());//映射id
        			    String startDate = tmp.getString("payTime");
        			    curi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
        			    curi.setRechargeTime(startDate);//充值时间，格式：yyyy-MM-dd HH:mm:ss 
        			    Double fee1 = Double.parseDouble(tmp.getString("pay_fee")) * 100;
        			    curi.setAmount(fee1.intValue());//充值金额(单位: 分) 
        			    curi.setType(tmp.getString("payWayName"));//充值方式. e.g. 现金 
        				ci.getRecharges().add(curi);
        			}
        		}
        	}
	        
	        result.setData(ci);
	        result.setResult(StatusCode.解析成功);
		}catch(Exception e){
			logger.info("==>[{}]解析出错了:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.数据解析中发生错误);
		}
		return result;
	}
}
