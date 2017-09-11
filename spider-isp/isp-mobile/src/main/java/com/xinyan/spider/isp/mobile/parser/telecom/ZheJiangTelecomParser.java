package com.xinyan.spider.isp.mobile.parser.telecom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.utils.CollectionUtil;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.PageUtils;
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
 * 浙江电信数据解析类
 * @description
 * @author yyj
 * @date 2017年5月6日 下午4:33:47
 * @version V1.0
 */
@Component
public class ZheJiangTelecomParser {

    protected static Logger logger = LoggerFactory.getLogger(ZheJiangTelecomParser.class);

	public Result parse(Context context, CarrierInfo ci, CacheContainer cc){
		Result result = new Result();
		try{
			logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
			List<Page> pages = new ArrayList<>();
			String pageInfo = cc.getString(ProcessorCode.BASIC_INFO.getCode());
			if(pageInfo!=null){
				ci.getCarrierUserInfo().setName(RegexUtils.matchValue("cust_name</string><string>(.*?)</string>", pageInfo).trim());//姓名
				ci.getCarrierUserInfo().setIdCard(RegexUtils.matchValue("cust_reg_nbr</string><string>(.*?)</string>", pageInfo).trim());//证件号
				ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
				ci.getCarrierUserInfo().setProvince("浙江");//所属省份
				ci.getCarrierUserInfo().setCity(context.getMobileHCodeDto().getCityName());//所属城市
				ci.getCarrierUserInfo().setAddress(RegexUtils.matchValue("area_id</string><long>(.*?)</long>", pageInfo).trim());//地址
				ci.getCarrierUserInfo().setLevel(RegexUtils.matchValue("<string>level</string><.+>(.*?)<.+><string>isCampusNet", pageInfo).trim());//帐号星级
				ci.getCarrierUserInfo().setOpenTime("");//入网时间，格式：yyyy-MM-dd

				pages = cc.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
				if(CollectionUtil.isNotEmpty(pages) && pages.size() > 0){
					Page p = pages.get(0);
					JSONObject tmp = JSONObject.parseObject(p.getWebResponse().getContentAsString());
					JSONObject j = tmp.getJSONObject("ll_country_ever_detail");
					if(null != j){
						ci.getCarrierUserInfo().setPackageName(j.getString("ProductName"));//套餐名称
					}
				}
			}
    	    
			Page page = cc.getPage(ProcessorCode.AMOUNT.getCode());
			if(page!=null){
				Double amount = Double.parseDouble(RegexUtils.matchValue("</string><string>(.*?)</string>", page.getWebResponse().getContentAsString())) * 100;
				ci.getCarrierUserInfo().setAvailableBalance(amount.intValue());//当前可用余额（单位: 分）
				ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss"));//最近一次更新时间，格式: yyyy-MM-dd HH:mm:ss
			}

			logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
	        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
	        
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
	        		pageInfo = p.getWebResponse().getContentAsString();
					if (StringUtils.contains(pageInfo, "清单详情</td>")) {
						String strTemps = RegexUtils.matchValue("备注</td>([\\s\\S]*?)温馨提示：</span>",pageInfo);
						List<List<String>> matches = RegexUtils.matchesMutiValue("</span></td>([\\s\\S]*?)</tr>", strTemps);
						for (int i = 0; i < matches.size(); i++) {
							List<String> callItem = matches.get(i);
							for(String pi : callItem){
								pi = pi.replaceAll("\"", "");
								String[] tds = RegexUtils.matchMutiValue(">(.*?)</td>", pi);
								if(tds.length>11){
									ccdi = new CarrierCallDetailInfo();
			        				ccdi.setMappingId(ci.getMappingId());//映射id
			        				String time = tds[2].trim();
//			        				time = time.substring(time.indexOf("(")+1, time.lastIndexOf(")"));
//			        				time = time.replaceAll("T", " ");
//			        				time = time.substring(0, 19);
			        				ccdi.setTime(time);//通话时间，格式：yyyy-MM-dd HH:mm:ss
			        				ccdi.setPeerNumber(tds[0].trim());//对方号码
			        				ccdi.setLocation(tds[4].trim());//通话地(自己的)
			        				String callDuriation = tds[5].trim();
			        				ccdi.setLocationType(callDuriation.substring(callDuriation.indexOf("(")+1, callDuriation.lastIndexOf(")")));//通话地类型. e.g.省内漫游
			        				
			        				callDuriation = tds[3].trim();
			        				callDuriation = callDuriation.substring(callDuriation.indexOf("(")+1, callDuriation.lastIndexOf(")"));
			        				ccdi.setDuration(callDuriation);//通话时长(单位:秒)
			        				String callType = tds[1].trim();
			        				if(callType.contains("主叫")){
			        					ccdi.setDialType("DIAL");//DIAL-主叫; DIALED-被叫
			        				}else if(callType.contains("被叫")){
			        					ccdi.setDialType("DIALED");//DIAL-主叫; DIALED-被叫
			        				}else{
			        					ccdi.setDialType(callType);//DIAL-主叫; DIALED-被叫
			        				}
			        				
			        				callDuriation = tds[9].trim();
			        				callDuriation = callDuriation.substring(callDuriation.indexOf("(")+1, callDuriation.lastIndexOf(")"));
			        				Double totalFee = Double.parseDouble(callDuriation);
			        				ccdi.setFee(totalFee.intValue());//通话费(单位:分)
			        				ccdi.setBillMonth(time.substring(0, time.lastIndexOf("-")));//通话月份
			        				ci.getCalls().add(ccdi);
								}
							}
						}
					}
				}
	        }

			logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
	        CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
	        		pageInfo = p.getWebResponse().getContentAsString();
					if (StringUtils.contains(pageInfo, "清单详情</td>")) {
						pageInfo = RegexUtils.matchValue("备注</td>([\\s\\S]*?)温馨提示：</span>",pageInfo);
						String[] matches = pageInfo.split("</tr>");
						for(String pi : matches){
							pi = pi.replaceAll("\"", "");
							String[] tds = RegexUtils.matchMutiValue(">(.*?)</td>", pi);
							if(tds.length>7){
								csri = new CarrierSmsRecordInfo();
		    					csri.setMappingId(ci.getMappingId());//映射id
								String time = tds[2].trim();
//		        				time = time.substring(time.indexOf("(")+1, time.lastIndexOf(")"));
//		        				time = time.replaceAll("T", " ");
//		        				time = time.substring(0, 19);
								csri.setTime(time);//收/发短信时间，格式：yyyy-MM-dd HH:mm:ss
								csri.setBillMonth(time.substring(0, time.lastIndexOf("-")));//通话月份
		    					csri.setPeerNumber(tds[0]);//对方号码
		    					csri.setLocation("");//通话地(自己的)
		    					String callType = tds[1];
		    					if(callType.contains("短信")){
		    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
		    					}else if(callType.contains("彩信")){
		    						csri.setMsgType("MMS");//SMS-短信; MMS-彩信
		    					}else{
		    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
		    					}
		    					csri.setSendType("");//SEND-发送; RECEIVE-收取
		    					csri.setServiceName(callType);//业务名称. e.g. 点对点(网内)
		    					String callDuriation = tds[5].trim();
		        				callDuriation = callDuriation.substring(callDuriation.indexOf("(")+1, callDuriation.lastIndexOf(")"));
		        				Double totalFee = Double.parseDouble(callDuriation);
		        				csri.setFee(totalFee.intValue());//通话费(单位:分)
		        				ci.getSmses().add(csri);
							}
						}
					}
	        	}
	        }

			logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
	        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
	        		pageInfo = p.getWebResponse().getContentAsString();
					if (StringUtils.contains(pageInfo, "清单详情</td>")) {
						pageInfo = RegexUtils.matchValue("业务内容</td>([\\s\\S]*?)合计</td>",pageInfo);
						String[] matches = pageInfo.split("</tr>");
						for(String pi : matches){
							pi = pi.replaceAll("\"", "");
							String[] tds = RegexUtils.matchMutiValue(">(.*?)</td>", pi);
							if(tds.length>7){
								cndi = new CarrierNetDetailInfo();
		    					cndi.setMappingId(ci.getMappingId());//映射id

		    					String time = tds[0].trim();
//		        				time = time.substring(time.indexOf("(")+1, time.lastIndexOf(")"));
//		        				time = time.replaceAll("T", " ");
//		        				time = time.substring(0, 19);
		    					cndi.setTime(time);//上网时间，格式：yyyy-MM-dd HH:mm:ss
		    					cndi.setBillMonth(time.substring(0, time.lastIndexOf("-")));
		    					
		    					String callDuriation = tds[1];
		        				callDuriation = callDuriation.substring(callDuriation.indexOf("(")+1, callDuriation.lastIndexOf(")"));
		        				cndi.setDuration(Integer.parseInt(callDuriation));//流量使用时长

		        				callDuriation = tds[2].trim();
		    					cndi.setSubflow(Integer.parseInt(callDuriation));//流量使用量，单位:KB
		    					
		    					cndi.setLocation(tds[5]);//流量使用地点
		    					cndi.setNetType(tds[3]);//网络类型
		    					cndi.setServiceName(tds[4]);//业务名称
		    					
		    					callDuriation = tds[6];
		        				callDuriation = callDuriation.substring(callDuriation.indexOf("(")+1, callDuriation.lastIndexOf(")"));
		    					Double fee1 = Double.parseDouble(callDuriation);
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
	        Double fee;
	        for (Page p : pages) {
	        	if(null != p){
	        		pageInfo = p.getWebResponse().getContentAsString();
	        		if(pageInfo.indexOf("帐户名称")>0){
        				pageInfo = pageInfo.replaceAll("&nbsp;", "");
		        		pageInfo = pageInfo.substring(pageInfo.indexOf("帐户名称"));
		        		cbdi = new CarrierBillDetailInfo();
		        		
		        	    cbdi.setMappingId(ci.getMappingId());//映射id
		        	    String tmp = RegexUtils.matchValue("帐单周期：(.*?)</td>", pageInfo);
		        	    tmp = tmp.replaceAll("年", "-");
		        	    tmp = tmp.replaceAll("月", "-");
		        	    tmp = tmp.replaceAll("日", "");
		        	    String[] tmps = tmp.split("至");
		        	    if(null != tmps && tmps.length > 1){
			        	    cbdi.setBillMonth(tmps[0].substring(0,7));//账单月，格式：yyyy-MM
			        	    cbdi.setBillStartDate(tmps[0]);//账期起始日期，格式：yyyy-MM-dd
			        	    cbdi.setBillEndDate(tmps[1]);//账期结束日期，格式：yyyy-MM-dd
			        	    
			        	    String feeStr = RegexUtils.matchValue("套餐月基本费</td><td class=iv_1_0_0_0>(.*?)</td>", pageInfo);
			        	    if(StringUtils.isNotEmpty(feeStr)){
			        	    	fee = Double.parseDouble(feeStr) * 100;
			        	    	cbdi.setBaseFee(fee.intValue());//套餐及固定费 单位分
			        	    }
			        	    feeStr = RegexUtils.matchValue("来电显示功能费</td><td class=iv_1_0_0_0>(.*?)</td>", pageInfo);
			        	    if(StringUtils.isNotEmpty(feeStr)){
			        	    	fee = Double.parseDouble(feeStr) * 100;
			        	    	cbdi.setExtraFee(fee.intValue());//其它费用 单位分
			        	    }
			        	    
			        	    feeStr = RegexUtils.matchValue("短信SP通信费</td><td class=iv_1_0_0_0>(.*?)</td>", pageInfo);
			        	    if(StringUtils.isNotEmpty(feeStr)){
			        	    	fee = Double.parseDouble(feeStr) * 100;
				        	    cbdi.setSmsFee(cbdi.getSmsFee() + fee.intValue());//短彩信费 单位分
			        	    }
			        	    
			        	    feeStr = RegexUtils.matchValue("短信彩信费</td><td class=iv_1_1_0_0>(.*?)</td>", pageInfo);
			        	    if(StringUtils.isNotEmpty(feeStr)){
			        	    	fee = Double.parseDouble(feeStr) * 100;
			        	    	cbdi.setSmsFee(cbdi.getSmsFee() + fee.intValue());//短彩信费 单位分
			        	    }
			        	    
			        	    feeStr = RegexUtils.matchValue("套餐优惠</td><td class=iv_1_0_0_0>(.*?)</td>", pageInfo);
			        	    if(StringUtils.isNotEmpty(feeStr)){
			        	    	feeStr = feeStr.replace("-", "");
			        	    	fee = Double.parseDouble(feeStr) * 100;
			        	    	cbdi.setDiscount(fee.intValue());//优惠费 单位分
			        	    }
			        	    
			        	    feeStr = RegexUtils.matchValue("本期费用合计：(.*?)元", pageInfo);
			        	    if(StringUtils.isNotEmpty(feeStr)){
			        	    	fee = Double.parseDouble(feeStr) * 100;
			        	    	cbdi.setActualFee(fee.intValue());//个人实际费用 单位分
			        	    }
			        	    feeStr = RegexUtils.matchValue("本期已付费用：(.*?)元", pageInfo);
			        	    if(StringUtils.isNotEmpty(feeStr)){
			        	    	fee = Double.parseDouble(feeStr) * 100;
			        	    	cbdi.setPaidFee(fee.intValue());//本期已付费用 单位分
			        	    }
			        	    feeStr = RegexUtils.matchValue("本期未交费用：(.*?)元", pageInfo);
			        	    if(StringUtils.isNotEmpty(feeStr)){
			        	    	fee = Double.parseDouble(feeStr) * 100;
			        	    	cbdi.setUnpaidFee(fee.intValue());//本期未付费用 单位分
			        	    }
			        	    cbdi.setTotalFee(cbdi.getBaseFee() + cbdi.getVoiceFee() + cbdi.getWebFee() + cbdi.getSmsFee() + cbdi.getExtraFee());//总费用 单位分
			        	    cbdi.setPoint(0);//本期可用积分
			        	    cbdi.setLastPoint(0);//上期可用积分
			        	    cbdi.setRelatedMobiles(RegexUtils.matchValue("<td colspan=6 class=td_user_num>(.*?)</td>", pageInfo));//本手机关联号码, 多个手机号以逗号分隔
			        	    cbdi.setNotes(RegexUtils.matchValue("帐单周期：.+</td><td>(.*?)。</td></tr><tr class=font_bl>", pageInfo));//备注
			        	    ci.getBills().add(cbdi);
		        	    }
	        		}
	        	}
	        }

	        logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
	        CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		pageInfo = p.getWebResponse().getContentAsString().replaceAll("=", ":");
	        		JSONObject tmp = JSONObject.parseObject(p.getWebResponse().getContentAsString());
    				//国内手机上网流量
    				JSONArray tmps = tmp.getJSONArray("ll_country_details");
    				if(CollectionUtil.isNotEmpty(tmps)){
    					for(int i=0; i<tmps.size(); i++){
    						cpti = new CarrierPackageItemInfo();
    						JSONObject j = (JSONObject) tmps.get(i);
    						cpti.setMappingId(ci.getMappingId());//映射id
    						cpti.setItem("国内手机上网流量");//套餐项目名称
    						String durs = j.getString("FreeResLimit");
	    					if(StringUtils.isNotEmpty(durs)){
	    						fee = Double.parseDouble(durs) * 1024;
	    						cpti.setTotal(fee.intValue()+"");//项目总量
	    					}
	    					durs = j.getString("FreeResUsed");
	    					if(StringUtils.isNotEmpty(durs)){
	    						fee = Double.parseDouble(durs) * 1024;
	    						cpti.setUsed(fee.intValue()+"");//项目已使用量
	    					}
    						cpti.setUnit("KB");//单位：语音-分; 流量-KB; 短/彩信-条
	    					StringBuilder startDateNew = new StringBuilder (j.getString("ValidDate"));
	    					startDateNew.insert(4, "-");
	    					startDateNew.insert(7, "-");
	   						cpti.setBillStartDate(startDateNew.substring(0, 10));//账单起始日, 格式为yyyy-MM-dd
	   						
	    					startDateNew = new StringBuilder (j.getString("ExpDate"));
	    					startDateNew.insert(4, "-");
	    					startDateNew.insert(7, "-");
    						cpti.setBillEndDate(startDateNew.substring(0, 10));//账单结束日, 格式为yyyy-MM-dd
    						ci.getPackages().add(cpti);
    					}
    				}
    				
    				//国内拨打国内时长
    				tmps = tmp.getJSONArray("yuyinList");
    				if(CollectionUtil.isNotEmpty(tmps)){
    					for(int i=0; i<tmps.size(); i++){
    						cpti = new CarrierPackageItemInfo();
    						JSONObject j = (JSONObject) tmps.get(i);
    						cpti.setMappingId(ci.getMappingId());//映射id
    						cpti.setItem(j.getString("FreeResName"));//套餐项目名称
    						cpti.setTotal(j.getString("FreeResLimit"));//项目总量
    						cpti.setUsed(j.getString("FreeResUsed"));//项目已使用量
    						cpti.setUnit(j.getString("ResUnit"));//单位：语音-分; 流量-KB; 短/彩信-条
	    					StringBuilder startDateNew = new StringBuilder (j.getString("ValidDate"));
	    					startDateNew.insert(4, "-");
	    					startDateNew.insert(7, "-");
	   						cpti.setBillStartDate(startDateNew.substring(0, 10));//账单起始日, 格式为yyyy-MM-dd
	   						
	    					startDateNew = new StringBuilder (j.getString("ExpDate"));
	    					startDateNew.insert(4, "-");
	    					startDateNew.insert(7, "-");
    						cpti.setBillEndDate(startDateNew.substring(0, 10));//账单结束日, 格式为yyyy-MM-dd
    						ci.getPackages().add(cpti);
    					}
    				}
    				
    				//天翼视讯省内定向流量
    				tmps = tmp.getJSONArray("ll_directional_details");
    				if(CollectionUtil.isNotEmpty(tmps)){
    					for(int i=0; i<tmps.size(); i++){
    						cpti = new CarrierPackageItemInfo();
    						JSONObject j = (JSONObject) tmps.get(i);
    						cpti.setMappingId(ci.getMappingId());//映射id
    						cpti.setItem("天翼视讯省内定向流量");//套餐项目名称
    						String durs = j.getString("FreeResLimit");
	    					if(StringUtils.isNotEmpty(durs)){
	    						fee = Double.parseDouble(durs) * 1024;
	    						cpti.setTotal(fee.intValue() + "");//项目总量
	    					}
	    					durs = j.getString("FreeResUsed");
	    					if(StringUtils.isNotEmpty(durs)){
	    						fee = Double.parseDouble(durs) * 1024;
	    						cpti.setUsed(fee.intValue() + "");//项目已使用量
	    					}
    						cpti.setUnit("KB");//单位：语音-分; 流量-KB; 短/彩信-条
	    					StringBuilder startDateNew = new StringBuilder (j.getString("ValidDate"));
	    					startDateNew.insert(4, "-");
	    					startDateNew.insert(7, "-");
	    					startDateNew.substring(0, 10);
	   						cpti.setBillStartDate(startDateNew.substring(0, 10));//账单起始日, 格式为yyyy-MM-dd
	   						
	    					startDateNew = new StringBuilder (j.getString("ExpDate"));
	    					startDateNew.insert(4, "-");
	    					startDateNew.insert(7, "-");
	    					startDateNew.substring(0, 10);
    						cpti.setBillEndDate(startDateNew.substring(0, 10));//账单结束日, 格式为yyyy-MM-dd
    						ci.getPackages().add(cpti);
    					}
    				}
	        	}
	        }

	        logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
	        page = cc.getPage(ProcessorCode.RECHARGE_INFO.getCode());
	        if(null != page){
	        	CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
	        	List<HtmlElement> trs = PageUtils.getElementByXpath(page, "//div[@class='tab-data']/table/tbody/tr");
	        	for(HtmlElement tr : trs){
	        		List<HtmlElement> tds = PageUtils.getElementByXpath(tr, "td");
	        		curi = new CarrierUserRechargeItemInfo();
	        		curi.setMappingId(ci.getMappingId());//映射id
				    String startDate = tds.get(4).asText().trim();
				    curi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
				    curi.setRechargeTime(startDate);//充值时间，格式：yyyy-MM-dd HH:mm:ss
				    String tmp = tds.get(5).asText().trim().replace("元", "");
				    fee = Double.parseDouble(tmp) * 100;
				    curi.setAmount(fee.intValue());//充值金额(单位: 分)
				    curi.setType(tds.get(1).asText().trim());//充值方式. e.g. 现金
	        		ci.getRecharges().add(curi);
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
