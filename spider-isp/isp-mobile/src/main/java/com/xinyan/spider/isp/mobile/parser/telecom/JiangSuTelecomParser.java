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
 * 江苏电信数据解析类
 * @description
 * @author yyj
 * @date 2017年4月21日 下午4:33:47
 * @version V1.0
 */
@Component
public class JiangSuTelecomParser {
	
    protected static Logger logger = LoggerFactory.getLogger(JiangSuTelecomParser.class);
	
	public Result parse(Context context, CarrierInfo ci, CacheContainer cc){
		Result result = new Result();
		try{
	
			JSONObject info = new JSONObject();
			JSONArray arrInfo = new JSONArray();
			JSONObject tmp = new JSONObject();
			
			logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
			Page page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
			if(page!=null){
				info = JSONObject.parseObject(page.getWebResponse().getContentAsString());

				String product = info.getString("productCollection").replaceFirst("\"", "");
				product = product.substring(0, product.lastIndexOf("\""));
				arrInfo = JSONObject.parseArray(product);

				ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
				ci.getCarrierUserInfo().setProvince("江苏");//所属省份
				String address = info.getString("userAddress");
				ci.getCarrierUserInfo().setCity(context.getMobileHCodeDto().getCityName());//所属城市
				ci.getCarrierUserInfo().setAddress(address);//地址
				ci.getCarrierUserInfo().setIdCard(info.getString("indentCode"));//证件号
				Double amount = Double.parseDouble(info.getString("blanceMoney")) * 100;
				ci.getCarrierUserInfo().setAvailableBalance(amount.intValue());//当前可用余额（单位: 分）
				if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
					tmp = arrInfo.getJSONObject(0);
					ci.getCarrierUserInfo().setMobile(context.getUserName());//手机号码
					ci.getCarrierUserInfo().setName(tmp.getJSONObject("accoutInfo").getString("partyName"));//姓名
					if(StringUtils.isEmpty(ci.getCarrierUserInfo().getName())){
						ci.getCarrierUserInfo().setName(info.getString("userName"));//姓名
					}

					//获取到开卡日期
					String servCreateDate = tmp.getJSONObject("productInfo").getString("servCreateDate");
					try{
						servCreateDate = DateUtils.dateToString(DateUtils.stringToDate(servCreateDate,DateUtils.PATTERN2), DateUtils.PATTERN1);
						ci.getCarrierUserInfo().setOpenTime(servCreateDate);//入网时间，格式：yyyy-MM-dd
					}catch (Exception e) {
						logger.info("==>[{}]入网时间解析出错:[{}]", context.getTaskId(), e);
					}

					ci.getCarrierUserInfo().setLevel("");//帐号星级
					ci.getCarrierUserInfo().setPackageName(info.getString(""));//套餐名称
					//帐号状态, -1未知 0正常 1单向停机 2停机 3预销户 4销户 5过户 6改号 99号码不存在
					if("在用".equals(tmp.getJSONObject("productInfo").getString("productStatusName"))){
						ci.getCarrierUserInfo().setState(0);//帐号状态
					}
					ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss"));//最近一次更新时间，格式: yyyy-MM-dd HH:mm:ss
				}else{
					ci.getCarrierUserInfo().setName(info.getString("userName"));//姓名
				}
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
	        				String startDateNew = tmp.getString("startDateNew");
	        				ccdi.setTime(startDateNew +" "+ tmp.getString("startTimeNew"));//通话时间，格式：yyyy-MM-dd HH:mm:ss
	        				ccdi.setPeerNumber(tmp.getString("nbr"));//对方号码
	        				ccdi.setLocation(tmp.getString("areaCode"));//通话地(自己的)
	        				ccdi.setLocationType(tmp.getString("ticketType"));//通话地类型. e.g.省内漫游
	        				String callDuriation = tmp.getString("durationCh");
	        				String[] durs = callDuriation.split(":");
	        				int dur = 0;
	        				if(durs.length > 2){
	        					dur += Integer.parseInt(durs[0]) * 3600;
	        					dur += Integer.parseInt(durs[1]) * 60;
	        					dur += Integer.parseInt(durs[2]);
	        				}
	        				ccdi.setDuration(dur+"");//通话时长(单位:秒)
	        				String callType = tmp.getString("ticketType");
	        				if(callType.contains("主叫")){
	        					ccdi.setDialType("DIAL");//DIAL-主叫; DIALED-被叫
	        				}else if(callType.contains("被叫")){
	        					ccdi.setDialType("DIALED");//DIAL-主叫; DIALED-被叫
	        				}else{
	        					ccdi.setDialType(callType);//DIAL-主叫; DIALED-被叫
	        				}
	        				Double totalFee = Double.parseDouble(tmp.getString("ticketChargeCh")) * 100;
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
	    					String startDateNew = tmp.getString("startDateNew");
	    					csri.setTime(startDateNew +" "+ tmp.getString("startTimeNew"));//收/发短信时间
	    					csri.setPeerNumber(tmp.getString("nbr"));//对方号码
	    					csri.setLocation("");//通话地(自己的)
	    					String callType = tmp.getString("ticketType");
	    					if(callType.contains("发")){
	    						csri.setSendType("SEND");//SEND-发送; RECEIVE-收取
	    					}else if(callType.contains("收")){
	    						csri.setSendType("RECEIVE");//DIAL-主叫; DIALED-被叫
	    					}
	    					if(callType.contains("短信")){
	    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
	    					}else if(callType.contains("彩信")){
	    						csri.setMsgType("MMS");//SMS-短信; MMS-彩信
	    					}else{
	    						csri.setMsgType(callType);//SMS-短信; MMS-彩信
	    					}
	    					csri.setServiceName(tmp.getString("ticketType"));//业务名称. e.g. 点对点(网内)
	    					Double fee1 = Double.parseDouble(tmp.getString("ticketChargeCh")) * 100;
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
	        		arrInfo = info.getJSONArray("items");        	
	        		if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        			for (int i = 0; i < arrInfo.size(); i++) {
	        				tmp = (JSONObject) arrInfo.get(i);
	        				cndi = new CarrierNetDetailInfo();
	    					cndi.setMappingId(ci.getMappingId());//映射id
	    					String startDateNew = tmp.getString("START_TIME");
	    					cndi.setTime(startDateNew);//流量使用时间
	    					cndi.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));//通话月份
	    					String duration = tmp.getString("DURATION_CH");
	    					
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
	    					
	    					duration = tmp.getString("BYTES_CNT");
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
	    					cndi.setLocation(tmp.getString("TICKET_TYPE"));//流量使用地点
	    					cndi.setNetType(tmp.getString("SERVICE_TYPE"));//网络类型
	    					cndi.setServiceName(tmp.getString("CCG_PRODUCT_NAME"));//业务名称
	    					Double fee1 = Double.parseDouble(tmp.getString("TICKET_CHARGE_CH")) * 100;
	    					cndi.setFee(fee1.intValue());//通信费(单位:分)
	    					
	    					ci.getNets().add(cndi);
	        			}
	        		}
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
	        CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		info = JSONObject.parseObject(p.getWebResponse().getContentAsString());
					cbdi = new CarrierBillDetailInfo();
					
				    cbdi.setMappingId(ci.getMappingId());//映射id
				    arrInfo = info.getJSONArray("custBaseInfo");
				    if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        			for (int i = 0; i < arrInfo.size(); i++) {
	        				tmp = (JSONObject) arrInfo.get(i);
	        				String itemName = tmp.getString("itemName");
	        				if(itemName.contains("计费帐期")){
	        					String itemCharge = tmp.getString("itemCharge");
	        					String[] tmps = itemCharge.split("-");
	        				    if(null != tmps && tmps.length>1){
	        				    	String startDateNew = tmps[0].replaceAll("/", "-");
	        				    	cbdi.setBillStartDate(startDateNew);//账期起始日期，格式：yyyy-MM-dd
	        				    	cbdi.setBillEndDate(tmps[1].replaceAll("/", "-"));//账期结束日期，格式：yyyy-MM-dd
	        				    	cbdi.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));//账单月，格式：yyyy-MM
	        				    }
	        				}
	        			}
				    }
				    
				    if(StringUtils.isNotEmpty(cbdi.getBillStartDate())){
				    	arrInfo = info.getJSONArray("custBillInfoList");
				    	if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
				    		for (int i = 0; i < arrInfo.size(); i++) {
				    			tmp = (JSONObject) arrInfo.get(i);
				    			String itemName = tmp.getString("itemName");
				    			if(itemName.contains("套餐月基本费")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge);
				    					cbdi.setBaseFee(cbdi.getBaseFee() + fee1.intValue());//套餐及固定费 单位分
				    				}
				    			}else if(itemName.contains("通话费")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge);
				    					cbdi.setVoiceFee(cbdi.getVoiceFee() + fee1.intValue());//语音费 单位分
				    				}
				    			}else if(itemName.contains("短信费")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge);
				    					cbdi.setSmsFee(cbdi.getSmsFee() + fee1.intValue());//短彩信费 单位分
				    				}
				    			}else if(itemName.contains("无线宽带使用费")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge);
				    					cbdi.setWebFee(cbdi.getWebFee() + fee1.intValue());//网络流量费 单位分
				    				}
				    			}else if(itemName.contains("优惠费")){
				    				String itemCharge = tmp.getString("itemCharge").replace("-", "");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge);	        		
				    					cbdi.setDiscount(cbdi.getDiscount() + fee1.intValue());//优惠费 单位分
				    				}
				    			}else if(itemName.contains("用户号码")){
				    				cbdi.setRelatedMobiles(itemName.replace("用户号码：", ""));//本手机关联号码, 多个手机号以逗号分隔
				    			}
				    		}
				    	}
				    	
				    	arrInfo = info.getJSONArray("feeCountAndPresendtedFee");
				    	if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
				    		for (int i = 0; i < arrInfo.size(); i++) {
				    			tmp = (JSONObject) arrInfo.get(i);
				    			String itemName = tmp.getString("itemName");
				    			if(itemName.contains("本期费用合计")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge) * 100;
				    					cbdi.setTotalFee(fee1.intValue());//总费用 单位分
				    				}
				    			}else if(itemName.contains("本期已付费用")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge) * 100;
				    					cbdi.setPaidFee(fee1.intValue());//本期已付费用 单位分
				    				}
				    			}else if(itemName.contains("本期应付费用")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge) * 100;
				    					cbdi.setActualFee(fee1.intValue());//个人实际费用 单位分
				    				}
				    			}
				    		}
				    	}
				    	cbdi.setUnpaidFee(cbdi.getTotalFee()-cbdi.getPaidFee());//本期未付费用 单位分
				    	
				    	arrInfo = info.getJSONArray("pointInfoList");
				    	if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
				    		for (int i = 0; i < arrInfo.size(); i++) {
				    			tmp = (JSONObject) arrInfo.get(i);
				    			String itemName = tmp.getString("itemName");
				    			if(itemName.contains("本期末可用积分")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge);
				    					cbdi.setPoint(fee1.intValue());//本期可用积分
				    				}
				    			}else if(itemName.contains("上期末可用积分")){
				    				String itemCharge = tmp.getString("itemCharge");
				    				if(StringUtils.isNotEmpty(itemCharge)){
				    					Double fee1 = Double.parseDouble(itemCharge);
				    					cbdi.setLastPoint(fee1.intValue());//上期可用积分
				    				}
				    			}
				    		}
				    	}
				    	cbdi.setNotes(info.getString("wxts"));//备注
				    	ci.getBills().add(cbdi);
				    }
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
	        CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		arrInfo = JSONObject.parseArray(p.getWebResponse().getContentAsString());
	        		if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        			if(StringUtils.isEmpty(ci.getCarrierUserInfo().getPackageName())){
	        				ci.getCarrierUserInfo().setPackageName(RegexUtils.matchValue("\"wareResultID\":\"0\",.*?\"offerName\":\"(.*?)\",", p.getWebResponse().getContentAsString()));//套餐名称
	        			}
	        			for (int i = 0; i < arrInfo.size(); i++) {
	        				tmp = (JSONObject) arrInfo.get(i);
	        				cpti = new CarrierPackageItemInfo();
	        				
	        				cpti.setMappingId(ci.getMappingId());//映射id
	        			    cpti.setItem(tmp.getString("accuName"));//套餐项目名称 
	        			    cpti.setTotal(tmp.getString("cumulationTotal"));//项目总量 
	        			    cpti.setUsed(tmp.getString("cumulationAlready"));//项目已使用量 
	        			    cpti.setUnit(tmp.getString("unitName"));//单位：语音-分; 流量-KB; 短/彩信-条 
	        			    String startTime = tmp.getString("startTime");
	        			    Date d = DateUtils.stringToDate(startTime, "yyyyMMddhhmmss");
	        			    cpti.setBillStartDate(DateUtils.getFirstDay(d, "yyyy-MM-dd"));;//账单起始日, 格式为yyyy-MM-dd
	        			    cpti.setBillEndDate(DateUtils.getLastDay(d, "yyyy-MM-dd"));;//账单结束日, 格式为yyyy-MM-dd
	    					ci.getPackages().add(cpti);
	        			}
	        		}
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.RECHARGE_INFO.getCode());
	        CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		info = JSONObject.parseObject(p.getWebResponse().getContentAsString());
	        		arrInfo = info.getJSONArray("items");
	        		if(null != arrInfo && CollectionUtil.isNotEmpty(arrInfo)){
	        			for (int i = 0; i < arrInfo.size(); i++) {
	        				tmp = (JSONObject) arrInfo.get(i);
	        				curi = new CarrierUserRechargeItemInfo();
	        				curi.setMappingId(ci.getMappingId());//映射id
	        			    String startDate = tmp.getString("startDate");
	        			    curi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
	        			    curi.setRechargeTime(startDate + " " + tmp.getString("startTime"));//充值时间，格式：yyyy-MM-dd HH:mm:ss 
	        			    Double fee1 = Double.parseDouble(tmp.getString("paymentCharge"));
	        			    curi.setAmount(fee1.intValue());//充值金额(单位: 分) 
	        			    curi.setType(tmp.getString("payMentMethodName"));//充值方式. e.g. 现金 
	        				ci.getRecharges().add(curi);
	        			}
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
