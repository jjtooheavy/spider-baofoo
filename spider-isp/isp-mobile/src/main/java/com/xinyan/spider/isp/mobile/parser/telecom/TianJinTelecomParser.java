package com.xinyan.spider.isp.mobile.parser.telecom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
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

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 天津电信数据解析类
 * @description
 * @author yyj
 * @date 2017年5月22日 下午4:33:47
 * @version V1.0
 */
@Component
public class TianJinTelecomParser {
	
    protected static Logger logger = LoggerFactory.getLogger(TianJinTelecomParser.class);
	
	public Result parse(Context context, CarrierInfo ci, CacheContainer cc){
		Result result = new Result();
		try{
			logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
			String pageInfo = "";
			String tmp = "";
			ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
			ci.getCarrierUserInfo().setProvince("天津");//所属省份
			
			// 积分、余额、星级、、Email
			Page page = cc.getPage(ProcessorCode.AMOUNT.getCode());
			if(page!=null){
				tmp = RegexUtils.matchValue("\"accountBalance\":\"(.*?)\"", page.getWebResponse().getContentAsString());
				if(StringUtils.isNotEmpty(tmp)){
					Double amount = Double.parseDouble(tmp) * 100;
					ci.getCarrierUserInfo().setAvailableBalance(amount.intValue());//当前可用余额（单位: 分）
				}
			}
			page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
			if(page!=null){
				pageInfo = page.getWebResponse().getContentAsString();
				pageInfo = pageInfo.replaceAll("\\s", " ").replaceAll("\"", "'");
				pageInfo = pageInfo.substring(pageInfo.indexOf("我的资料")+1);
				if(pageInfo.indexOf("温馨提示") > 0){
					pageInfo = pageInfo.substring(0,pageInfo.indexOf("温馨提示"));
				}
				ci.getCarrierUserInfo().setName(RegexUtils.matchValue("<th width='10%'>机主姓名：</th>                    <td class='tl pdt-10' width='40%'>(.*?)</td>", pageInfo).trim());//姓名
				ci.getCarrierUserInfo().setIdCard(RegexUtils.matchValue("证件号码：</th>                    <td class='tl pdt-10'>(.*?)</td>", pageInfo).trim());//证件号
				String address = RegexUtils.matchValue("通信地址：</th>                    <td class='tl pdt-10'>(.*?)</td> ", pageInfo).trim();
				ci.getCarrierUserInfo().setCity("天津");//所属城市
				String openTime = RegexUtils.matchValue("入网时间：</th>                    <td class='tl pdt-10'>(.*?)</td>", pageInfo);
				if(StringUtils.isNotEmpty(openTime) && openTime.length()>10){
					ci.getCarrierUserInfo().setOpenTime(openTime.substring(0,10));//入网时间，格式：yyyy-MM-dd
				}else{
					ci.getCarrierUserInfo().setOpenTime(openTime);//入网时间，格式：yyyy-MM-dd
				}
				ci.getCarrierUserInfo().setAddress(address);//地址
				ci.getCarrierUserInfo().setLevel("");//帐号星级
				ci.getCarrierUserInfo().setState(0);//帐号状态, -1未知 0正常 1单向停机 2停机 3预销户 4销户 5过户 6改号 99号码不存在
				ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss"));//最近一次更新时间，格式: yyyy-MM-dd HH:mm:ss
			}


			page = cc.getPage(ProcessorCode.POINTS_VALUE.getCode());
			if(null != page){
				JSONObject t = JSONObject.parseObject(page.getWebResponse().getContentAsString());
				if(null != t){
					ci.getCarrierUserInfo().setPackageName(t.getJSONObject("mainpackage").getString("pricingPlanName"));//套餐名称
				}
			}			

			logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
	        List<Page> pages = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
	        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
	        for (Page p : pages) {
				if(null != p && !StringUtils.contains(p.getWebResponse().getContentAsString(), "数据为空")){
					try {
						InputStream input = p.getWebResponse().getContentAsStream();
						Workbook book= Workbook.getWorkbook(input);
						Sheet sheet = book.getSheet(0);
						for(int i=0; i<sheet.getRows()-1; i++){
							tmp = sheet.getCell(0,i).getContents();
							if(!StringUtils.contains(tmp, "主叫号码")){
								ccdi = new CarrierCallDetailInfo(); 
								ccdi.setMappingId(ci.getMappingId());//映射id
								String startDate = sheet.getCell(4,i).getContents();
								ccdi.setTime(startDate);//通话时间，格式：yyyy-MM-dd HH:mm:ss
			    				ccdi.setPeerNumber(sheet.getCell(1,i).getContents());//对方号码
			    				ccdi.setLocation(sheet.getCell(3,i).getContents());//通话地(自己的)
			    				ccdi.setDuration(sheet.getCell(5,i).getContents());//通话时长(单位:秒)
			    				String callType = sheet.getCell(2,i).getContents();
			    				if(callType.contains("主叫")){
			    					ccdi.setDialType("DIAL");//DIAL-主叫; DIALED-被叫
			    				}else if(callType.contains("被叫")){
			    					ccdi.setDialType("DIALED");//DIAL-主叫; DIALED-被叫
			    				}else{
			    					ccdi.setDialType(callType);//DIAL-主叫; DIALED-被叫
			    				}
			    				Double totalFee = Double.parseDouble(sheet.getCell(6,i).getContents()) * 100;
			    				ccdi.setFee(totalFee.intValue());//通话费(单位:分)
			    				ccdi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//通话月份
			    				ccdi.setLocationType("");//通话地类型. e.g.省内漫游
								ci.getCalls().add(ccdi);
							}
						}
						book.close();
						input.close();
					} catch (IOException e) {
						logger.info("==>[{}]解析通话详单出错了:[{}]", context.getTaskId(), e);
					} catch (BiffException e) {
						logger.info("==>[{}]解析通话详单出错了:[{}]", context.getTaskId(), e);
					}
				}
	        }

			logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
	        CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
	        for (Page p : pages) {
	        	if(null != p && !StringUtils.contains(p.getWebResponse().getContentAsString(), "数据为空")){
	    			try {
	    				InputStream input = p.getWebResponse().getContentAsStream();
	    				Workbook book = Workbook.getWorkbook(input);
	    				Sheet sheet = book.getSheet(0);
	    				for(int i=0; i<sheet.getRows()-1; i++){
	    					tmp = sheet.getCell(0,i).getContents();
	    					if(!StringUtils.contains(tmp, "发送号码")){
	    						csri = new CarrierSmsRecordInfo();
	    						csri.setMappingId(ci.getMappingId());//映射id
	    						
	    						String startDate = sheet.getCell(2,i).getContents();
	    						csri.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//通话月份
	    						csri.setTime(startDate);//收/发短信时间,格式：yyyy-MM-dd HH:mm:ss
	    						csri.setPeerNumber(sheet.getCell(1,i).getContents());//对方号码
	    						csri.setLocation("");//通话地(自己的)
	    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
	    						if(context.getUserName().equals(tmp)){
	    							csri.setSendType("SEND");//SEND-发送; RECEIVE-收取
	    						}else{
	    							csri.setSendType("RECEIVE");//SEND-发送; RECEIVE-收取
	    						}
	    						csri.setServiceName("");//业务名称. e.g. 点对点(网内)
		    					Double fee1 = Double.parseDouble(sheet.getCell(3,i).getContents()) * 100;
			    				csri.setFee(fee1.intValue());//通话费(单位:分)
	    						ci.getSmses().add(csri);
	    					}
    					}
	    				book.close();
	    				input.close();
	    			} catch (IOException e) {
	    				logger.info("==>[{}]解析短信记录出错了:[{}]", context.getTaskId(), e);
	    			} catch (BiffException e) {
	    				logger.info("==>[{}]无法解析或该月无短信记录:[{}]", context.getTaskId(), e);
	    			}
				}
	        }

			logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
	        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
	        for (Page p : pages) {
	        	if(null != p && !StringUtils.contains(p.getWebResponse().getContentAsString(), "数据为空")){
	    			try {
	    				InputStream input = p.getWebResponse().getContentAsStream();
	    				Workbook book = Workbook.getWorkbook(input);
	    				Sheet sheet = book.getSheet(0);
	    				for(int i=0; i<sheet.getRows()-1; i++){
	    					tmp = sheet.getCell(0,i).getContents();
	    					if(!StringUtils.contains(tmp, "开始时间")){
	    						cndi = new CarrierNetDetailInfo();
		    					cndi.setMappingId(ci.getMappingId());//映射id
		    					String startDate = tmp;
		    					cndi.setTime(startDate);//上网时间，格式：yyyy-MM-dd HH:mm:ss
		    					cndi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));
		    					
		    					String duration = sheet.getCell(1,i).getContents();	    					
		    					int dur = 0;
		    					String durs = RegexUtils.matchValue("(\\d+)小?时", duration).trim();
		    					if(StringUtils.isNotEmpty(durs)){
		    						dur += Integer.parseInt(durs) * 3600;
		    					}
		    					durs = RegexUtils.matchValue("(\\d+)分钟", duration).trim();
		    					if(StringUtils.isNotEmpty(durs)){
		    						dur += Integer.parseInt(durs) * 60;
		    					}
		    					durs = RegexUtils.matchValue("(\\d+)秒", duration).trim();
		    					if(StringUtils.isNotEmpty(durs)){
		    						dur += Integer.parseInt(durs);
		    					}
		    					cndi.setDuration(dur);//流量使用时长
		    					
		    					duration = sheet.getCell(2,i).getContents();	
		    					durs = RegexUtils.matchValue("(\\d+)GB", duration).trim();
		    					dur = 0;
		    					if(StringUtils.isNotEmpty(durs)){
		    						dur += Integer.parseInt(durs) * 1048576;
		    					}
		    					durs = RegexUtils.matchValue("(\\d+)MB", duration).trim();
		    					if(StringUtils.isNotEmpty(durs)){
		    						dur += Integer.parseInt(durs) * 1024;
		    					}
		    					durs = RegexUtils.matchValue("(\\d+)KB", duration).trim();
		    					if(StringUtils.isNotEmpty(durs)){
		    						dur += Integer.parseInt(durs);
		    					}
		    					cndi.setSubflow(dur);//流量使用量，单位:KB
		    					cndi.setLocation(sheet.getCell(4,i).getContents());//流量使用地点
		    					cndi.setNetType(sheet.getCell(3,i).getContents());//网络类型
		    					cndi.setServiceName(sheet.getCell(5,i).getContents());//业务名称
		    					Double fee1 = Double.parseDouble(sheet.getCell(6,i).getContents()) * 100;
		    					cndi.setFee(fee1.intValue());//通信费(单位:分)
		    					ci.getNets().add(cndi);
	    					}
    					}
	    				book.close();
	    				input.close();
	    			} catch (IOException e) {
	    				logger.error("==>[{}]解析上网记录出错了:", context.getTaskId(), e);
	    			} catch (BiffException e) {
	    				logger.error("==>[{}]无法解析或该月无上网记录", context.getTaskId());
	    			}
				}
	        }

	        logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
	        CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
	        Double fee;
	        Calendar calendar = Calendar.getInstance();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
	        		JSONObject pi = JSONObject.parseObject(p.getWebResponse().getContentAsString());
	        		String tip = pi.getString("tip");
	        		if(!"没有查询到账单数据".equals(tip)){
	        			cbdi = new CarrierBillDetailInfo();
	        			cbdi.setMappingId(ci.getMappingId());//映射id
		        	    cbdi.setNotes(tip);//备注
		        	    
		        	    tmp = pi.getString("billingCycle1");
		        	    if(StringUtils.isNotEmpty(tmp)){
		        	    	Date date = DateUtils.stringToDate(tmp, "yyyy年MM月");
		        	    	calendar.setTime(date);		        	    	
		        	    	cbdi.setBillStartDate(DateUtils.getFirstDay(calendar, "yyyy-MM-dd"));//账期起始日期，格式：yyyy-MM-dd
		        	    	cbdi.setBillEndDate(DateUtils.getLastDay(calendar,"yyyy-MM-dd"));//账期结束日期，格式：yyyy-MM-dd
		        	    	cbdi.setBillMonth(tmp.replace("年", "-").replace("月", ""));//账单月，格式：yyyy-MM
		        	    	
		        	    	JSONArray tmps = pi.getJSONArray("billItemList");
		        	    	if(CollectionUtil.isNotEmpty(tmps)){
		        	    		JSONArray acctItems = tmps.getJSONObject(0).getJSONArray("acctItems");
		        	    		if(CollectionUtil.isNotEmpty(acctItems)){
		        	    			for(int i=0; i< acctItems.size(); i++){
		        	    				JSONObject acctItem = acctItems.getJSONObject(i);
		        	    				String feeStr = acctItem.getString("acctItemFee");
		        	    				String acctItemName = acctItem.getString("acctItemName");
		        	    				fee = 0d;
		        	    				if(StringUtils.isNotEmpty(feeStr)){
		        	    					fee = Double.parseDouble(feeStr) * 100;
		        	    				}
		        	    				if(acctItemName.contains("语音通信费")){
		        	    					cbdi.setVoiceFee(cbdi.getVoiceFee() + fee.intValue());//语音费 单位分
		        	    				}else if(acctItemName.contains("上网")){
		        	    					cbdi.setWebFee(cbdi.getWebFee() + fee.intValue());//网络流量费 单位分
		        	    				}else if(acctItemName.contains("短信")){
		        	    					cbdi.setSmsFee(cbdi.getSmsFee() + fee.intValue());//短彩信费 单位分
		        	    				}else if(acctItemName.contains("月基本费")){
		        	    					cbdi.setBaseFee(cbdi.getBaseFee() + fee.intValue());//套餐及固定费 单位分
		        	    				}else{
		        	    					logger.error("==>[{}]暂无[{}]类型解析...", context.getTaskId(), acctItemName);
		        	    				}
		        	    			}
		        	    		}
		        	    	}
		        	    	String feeStr = tmps.getJSONObject(0).getString("billFee");
		        	    	if(StringUtils.isNotEmpty(feeStr)){
    	    					fee = Double.parseDouble(feeStr) * 100;
    	    					cbdi.setTotalFee(fee.intValue());//总费用 单位分
    	    				}else{
    	    					cbdi.setTotalFee(cbdi.getBaseFee() + cbdi.getVoiceFee() + cbdi.getWebFee() + cbdi.getSmsFee() + cbdi.getExtraFee());//总费用 单位分
    	    				}
		        	    	
		        	    	cbdi.setRelatedMobiles("");//本手机关联号码, 多个手机号以逗号分隔
		        	    	ci.getBills().add(cbdi);
		        	    }
	        		}
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
	        page = cc.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
	        CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
        	if(null != page){
				JSONObject pi = JSONObject.parseObject(page.getWebResponse().getContentAsString());
				JSONArray tmps = pi.getJSONArray("listCumulationInfoSet");
				if(CollectionUtil.isNotEmpty(tmps)) {
					for (int i = 0; i < tmps.size(); i++) {
						JSONObject t = tmps.getJSONObject(i);
						cpti = new CarrierPackageItemInfo();
						cpti.setMappingId(ci.getMappingId());//映射id
						cpti.setItem(t.getString("accuName"));//套餐项目名称
						cpti.setTotal(t.getString("total"));//项目总量
						cpti.setUsed(t.getString("already"));//项目已使用量
						cpti.setUnit(t.getString("unitName"));//单位：语音-分; 流量-KB; 短/彩信-条
						tmp = t.getString("endTime");
						tmp = tmp.replace("年","-").replace("月","-").replace("日","");
						cpti.setBillStartDate(tmp.substring(0, 7)+"-01");//账单起始日, 格式为yyyy-MM-dd
						cpti.setBillEndDate(tmp.substring(0, 10));//账单结束日, 格式为yyyy-MM-dd
						ci.getPackages().add(cpti);
					}
				}
        	}
	        
	        logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.RECHARGE_INFO.getCode());
	        CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		JSONObject pi = JSONObject.parseObject(p.getWebResponse().getContentAsString());
	        		JSONArray tmps = pi.getJSONArray("listPaymentHistory");
	        		if(CollectionUtil.isNotEmpty(tmps)){
	        			for(int i=0; i< tmps.size(); i++){
        	    			JSONObject t = tmps.getJSONObject(i);
        	    			curi = new CarrierUserRechargeItemInfo();
	        				curi.setMappingId(ci.getMappingId());//映射id
	        				String startDate = t.getString("paymentDate");
	        				startDate = startDate.replace("T", " ");
	        				curi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
	        				curi.setRechargeTime(startDate.toString());//充值时间，格式：yyyy-MM-dd HH:mm:ss 
	        				Double fee1 = Double.parseDouble(t.getString("fee")) * 100;
	        				curi.setAmount(fee1.intValue());//充值金额(单位: 分) 
	        				curi.setType(t.getString("paymentType"));//充值方式. e.g. 现金 
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