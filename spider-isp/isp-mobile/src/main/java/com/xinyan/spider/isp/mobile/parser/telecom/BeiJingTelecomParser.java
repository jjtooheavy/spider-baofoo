package com.xinyan.spider.isp.mobile.parser.telecom;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
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
 * 北京电信数据解析类
 * @description
 * @author yyj
 * @date 2017年６月1日 下午4:33:47
 * @version V1.0
 */
@Component
public class BeiJingTelecomParser {
	
    protected static Logger logger = LoggerFactory.getLogger(BeiJingTelecomParser.class);
	
	public Result parse(Context context, CarrierInfo ci, CacheContainer cc){
		Result result = new Result();
		try{
			logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
			String pageInfo = "";
			ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
			ci.getCarrierUserInfo().setProvince("北京");//所属省份
			
			// 积分、余额、星级、、Email
			Page page = cc.getPage(ProcessorCode.AMOUNT.getCode());
			if(null != page){
				pageInfo = page.getWebResponse().getContentAsString();
				if(StringUtils.isNotEmpty(pageInfo) && !StringUtils.contains(pageInfo, "请稍后重试")){
					String tmp = RegexUtils.matchValue("\"accountBalance\":\"(.*?)\",", pageInfo);
					Double amount = Double.parseDouble(tmp) * 100;
					ci.getCarrierUserInfo().setAvailableBalance(amount.intValue());//当前可用余额（单位: 分）
				}
			}
			page = cc.getPage(ProcessorCode.OTHER_INFO.getCode());
			if(null!=page){
				pageInfo = page.getWebResponse().getContentAsString();
				if(StringUtils.isNotEmpty(pageInfo) && StringUtils.contains(pageInfo,"套餐名称")){
					String tmp = RegexUtils.matchValue("套餐名称.*?<tbody><tr><td>(.*?)</td>",pageInfo.replaceAll("\\s+",""));
					ci.getCarrierUserInfo().setPackageName(tmp);
				}
			}
			
			page = cc.getPage(ProcessorCode.POINTS_VALUE.getCode());
			if(null != page){
				pageInfo = page.getWebResponse().getContentAsString();
				pageInfo = pageInfo.replaceAll("\\s", " ").replaceAll("\"", "'");
				pageInfo = pageInfo.substring(pageInfo.indexOf("个人资料")+1);
				if(pageInfo.indexOf("立即提交") > 0){
					pageInfo = pageInfo.substring(0,pageInfo.indexOf("立即提交"));
				}
				ci.getCarrierUserInfo().setIdCard(RegexUtils.matchValue("input type='text' name='certificateNumber' .* value='(.*?)'/> ", pageInfo));//证件号
			}
			page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
			if(null != page){
				pageInfo = page.getWebResponse().getContentAsString();
				pageInfo = pageInfo.replaceAll("\\s", " ").replaceAll("\"", "'");
				pageInfo = pageInfo.substring(pageInfo.indexOf("客户资料修改")+1);
				if(pageInfo.indexOf("提交保存") > 0){
					pageInfo = pageInfo.substring(0,pageInfo.indexOf("提交保存"));
				}
				ci.getCarrierUserInfo().setName(RegexUtils.matchValue("<th>客户名称：</th>                   <td class='tl'>(.*?)</td> ", pageInfo).trim());//姓名
				String address = RegexUtils.matchValue("id='custAddress_new' type='text' style='width:210px;' class='ued-text none js_input' value='(.*?)'", pageInfo);
				ci.getCarrierUserInfo().setCity("北京");//所属城市
				String openTime = RegexUtils.matchValue("入网时间：</th>                     <td class='tl'>(.*?)</td>", pageInfo);
				if(StringUtils.isNotEmpty(openTime) && openTime.length()>10){
					ci.getCarrierUserInfo().setOpenTime(openTime.substring(0,10));//入网时间，格式：yyyy-MM-dd
				}else{
					ci.getCarrierUserInfo().setOpenTime(openTime);//入网时间，格式：yyyy-MM-dd
				}
				ci.getCarrierUserInfo().setAddress(address);//地址
				ci.getCarrierUserInfo().setLevel("");//帐号星级
				ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss"));//最近一次更新时间，格式: yyyy-MM-dd HH:mm:ss
			}
			
			logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
	        List<Page> pages = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
	        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
    				List<HtmlElement> elements = PageUtils.getElementByXpath(p, "//table[@class='ued-table']/tbody/tr");
    				for (HtmlElement element : elements) {
    					HtmlTableRow tableRow = (HtmlTableRow) element;
    					String tmp = tableRow.getCell(0).asText();
    					if(!"序号".equals(tmp) && tableRow.getCells().size() > 9){
    						ccdi = new CarrierCallDetailInfo();
    						ccdi.setMappingId(ci.getMappingId());//映射id
    						String startDate = tableRow.getCell(5).asText();
    						ccdi.setTime(startDate);//通话时间，格式：yyyy-MM-dd HH:mm:ss
    						ccdi.setPeerNumber(tableRow.getCell(4).asText());//对方号码
    						ccdi.setLocation(tableRow.getCell(3).asText());//通话地(自己的)
    						ccdi.setDuration(tableRow.getCell(8).asText());//通话时长(单位:秒)
    						String callType = tableRow.getCell(1).asText();//呼叫类型
    						if(callType.contains("主叫")){
								ccdi.setDialType("DIAL");//DIAL-主叫; DIALED-被叫
							}else if(callType.contains("被叫")){
								ccdi.setDialType("DIALED");//DIAL-主叫; DIALED-被叫
							}else{
								ccdi.setDialType(callType);//DIAL-主叫; DIALED-被叫
							}
    						Double totalFee = Double.parseDouble(tableRow.getCell(9).asText()) * 100;
    						ccdi.setFee(totalFee.intValue());//通话费(单位:分)
    						ccdi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//通话月份
    						ccdi.setLocationType(tableRow.getCell(2).asText());//通话地类型. e.g.省内漫游
    						ci.getCalls().add(ccdi);
    					}
    				}
	        	}
	        }
	        
			logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
	        CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
    				List<HtmlElement> elements = PageUtils.getElementByXpath(p, "//table[@class='ued-table']/tbody/tr");
    				for (HtmlElement element : elements) {
    					HtmlTableRow tableRow = (HtmlTableRow) element;
    					String tmp = tableRow.getCell(0).asText();
    					if(!"序号".equals(tmp) && tableRow.getCells().size() > 5){
							csri = new CarrierSmsRecordInfo();
	    					csri.setMappingId(ci.getMappingId());//映射id
	    					String startDate = tableRow.getCell(4).asText();
	    					csri.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//通话月份
		    				csri.setPeerNumber(tableRow.getCell(3).asText());//对方号码
	    					csri.setLocation("");//通话地(自己的)
	    					String callType = tableRow.getCell(1).asText();
	    					if(callType.contains("短信")){
	    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
	    					}else if(callType.contains("彩信")){
	    						csri.setMsgType("MMS");//SMS-短信; MMS-彩信
	    					}else{
	    						csri.setMsgType(callType);//SMS-短信; MMS-彩信
	    					}
	    					
	    					callType = tableRow.getCell(2).asText();
	    					if(callType.contains("发")){
	    						csri.setSendType("SEND");//SEND-发送; RECEIVE-收取
	    					}else if(callType.contains("收")){
	    						csri.setSendType("RECEIVE");//DIAL-主叫; DIALED-被叫
	    					}
	    					
	    					csri.setServiceName(tableRow.getCell(1).asText());//业务名称. e.g. 点对点(网内)
	    					Double fee1 = Double.parseDouble(tableRow.getCell(5).asText()) * 100;
		    				csri.setFee(fee1.intValue());//通话费(单位:分)
	        				ci.getSmses().add(csri);
						}
    				}
	        	}
	        }
			
			logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
	        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
	        		List<HtmlElement> elements = PageUtils.getElementByXpath(p, "//table[@class='ued-table']/tbody/tr");
					for (HtmlElement element : elements) {
						HtmlTableRow tableRow = (HtmlTableRow) element;
						String tmp = tableRow.getCell(0).asText();
    					if(!"序号".equals(tmp) && tableRow.getCells().size() > 7){
    						cndi = new CarrierNetDetailInfo();
	    					cndi.setMappingId(ci.getMappingId());//映射id
	    					
	    					String startDate = tableRow.getCell(1).asText();
	    					cndi.setTime(startDate);//上网时间，格式：yyyy-MM-dd HH:mm:ss
	    					cndi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));
	    					
	    					String duration = tableRow.getCell(2).asText();//	    					
	    					int dur = 0;
	    					String durs = RegexUtils.matchValue("(\\d+)小?时", duration);
	    					if(StringUtils.isNotEmpty(durs)){
	    						dur += Integer.parseInt(durs) * 3600;
	    					}
	    					durs = RegexUtils.matchValue("(\\d+)分", duration);
	    					if(StringUtils.isNotEmpty(durs)){
	    						dur += Integer.parseInt(durs) * 60;
	    					}
	    					durs = RegexUtils.matchValue("(\\d+)秒", duration);
	    					if(StringUtils.isNotEmpty(durs)){
	    						dur += Integer.parseInt(durs);
	    					}
	    					cndi.setDuration(dur);//流量使用时长
	    					
	    					duration = tableRow.getCell(3).asText();;
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
	    					
	    					cndi.setLocation(tableRow.getCell(5).asText());//流量使用地点
	    					cndi.setNetType(tableRow.getCell(4).asText());//网络类型
	    					cndi.setServiceName(tableRow.getCell(7).asText());//业务名称
	    					Double fee1 = Double.parseDouble(tableRow.getCell(6).asText()) * 100;
	    					cndi.setFee(fee1.intValue());//通信费(单位:分)
	    					ci.getNets().add(cndi);
						}
					}
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
	        Double fee;
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
					CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
	        		pageInfo = p.getWebResponse().getContentAsString().replaceAll("\\s", "").replaceAll("\"", "'");
	        		pageInfo = pageInfo.replaceAll("&nbsp;", "");
	    			pageInfo = pageInfo.substring(pageInfo.indexOf("客户名称")+1);
	    			if(pageInfo.indexOf("温馨提示") > 0){
	    				pageInfo = pageInfo.substring(0,pageInfo.indexOf("温馨提示"));
	    			}
	        		
	        	    String[] tmp = RegexUtils.matchMutiValue("计费周期:(.*?)--(.*?)</td>", pageInfo);
	        	    if(null != tmp && tmp.length > 1){
	        	    	cbdi.setBillStartDate(tmp[0].replace("/", "-"));//账期起始日期，格式：yyyy-MM-dd
	        	    	cbdi.setBillEndDate(tmp[1].replace("/", "-"));//账期结束日期，格式：yyyy-MM-dd
	        	    	cbdi.setBillMonth(tmp[0].replace("/", "-").substring(0,7));//账单月，格式：yyyy-MM
	        	    	cbdi.setMappingId(ci.getMappingId());//映射id
	        	    	cbdi.setNotes(RegexUtils.matchValue("计费周期:.+</td><tdstyle='font-weight:bolder'>(.*?)</td>", pageInfo));//备注
	        	    	String feeStr = RegexUtils.matchValue("主套餐基本费</td><tdclass='BillTdStyle06'align='right'>(.*?)</td>", pageInfo);
	        	    	if(StringUtils.isNotEmpty(feeStr)){
	        	    		fee = Double.parseDouble(feeStr) * 100;
	        	    		cbdi.setBaseFee(fee.intValue());//套餐及固定费 单位分
	        	    	}
	        	    	
	        	    	feeStr = RegexUtils.matchValue("语音通信费</td><tdclass='BillTdStyle06'align='right'>(.*?)</td>", pageInfo);
	        	    	if(StringUtils.isNotEmpty(feeStr)){
	        	    		fee = Double.parseDouble(feeStr) * 100;
	        	    		cbdi.setVoiceFee(fee.intValue());//语音费 单位分
	        	    	}
	        	    	
	        	    	feeStr = RegexUtils.matchValue("本期费用合计：(.*?)元", pageInfo);
	        	    	if(StringUtils.isNotEmpty(feeStr)){
	        	    		fee = Double.parseDouble(feeStr) * 100;
	        	    		cbdi.setTotalFee(fee.intValue());//总费用 单位分
	        	    		cbdi.setDiscount(fee.intValue());//优惠费 单位分
	        	    	}
	        	    	
	        	    	feeStr = RegexUtils.matchValue("本期已付费用：(.*?)元", pageInfo);
	        	    	if(StringUtils.isNotEmpty(feeStr)){
	        	    		fee = Double.parseDouble(feeStr) * 100;
	        	    		cbdi.setPaidFee(fee.intValue());//本期已付费用 单位分
	        	    	}
	        	    	
	        	    	feeStr = RegexUtils.matchValue("本期应付费用：(.*?)元", pageInfo);
	        	    	if(StringUtils.isNotEmpty(feeStr)){
	        	    		fee = Double.parseDouble(feeStr) * 100;
	        	    		cbdi.setUnpaidFee(fee.intValue());//本期未付费用 单位分
	        	    	}
	        	    	
	        	    	tmp = RegexUtils.matchMutiValue("<tdclass='BillTdStyle12'align='center'colspan='3'>(\\d*?)</td><tdclass='BillTdStyle12'align='center'>=</td><tdclass='BillTdStyle12'align='center'colspan='3'>(\\d*?)</td>", pageInfo);
	        	    	if(null != tmp && tmp.length > 1){
	        	    		cbdi.setPoint(Integer.parseInt(tmp[0]));//本期可用积分
	        	    		cbdi.setLastPoint(Integer.parseInt(tmp[1]));//上期可用积分
	        	    	}
	        	    	cbdi.setRelatedMobiles(context.getUserName());//本手机关联号码, 多个手机号以逗号分隔
	        	    	ci.getBills().add(cbdi);
	        	    }
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
	        page = cc.getPage(ProcessorCode.PACKAGE_ITEM.getCode()+ "1");
	        CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
        	if(null != page){
        		pageInfo = page.getWebResponse().getContentAsString().replaceAll("\\s", "").replaceAll("\"", "'");
        		List<List<String>> tmps = RegexUtils.matchesMutiValue("<tr><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>", pageInfo);
				for(List<String> tmp : tmps){
					cpti = new CarrierPackageItemInfo();
					cpti.setMappingId(ci.getMappingId());//映射id
				    cpti.setItem(tmp.get(0));//套餐项目名称 
				    ci.getCarrierUserInfo().setPackageName(tmp.get(0));//套餐名称
				    cpti.setBillStartDate(tmp.get(1));//账单起始日, 格式为yyyy-MM-dd
				    if("长期有效".equals(tmp.get(2))){
				    	cpti.setBillEndDate("2099-12-31");//账单结束日, 格式为yyyy-MM-dd
				    }else{
				    	cpti.setBillEndDate(tmp.get(2));//账单结束日, 格式为yyyy-MM-dd
				    }
					ci.getPackages().add(cpti);
				}
        	}
        	page = cc.getPage(ProcessorCode.PACKAGE_ITEM.getCode()+ "2");
        	if(null != page){
        		pageInfo = page.getWebResponse().getContentAsString().replaceAll("\\s", "").replaceAll("\"", "'");
				List<List<String>> tmps = RegexUtils.matchesMutiValue("<tr><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>", pageInfo);
				for(List<String> tmp : tmps){
					cpti = new CarrierPackageItemInfo();
					cpti.setMappingId(ci.getMappingId());//映射id
				    cpti.setItem(tmp.get(0));//套餐项目名称 
				    cpti.setBillStartDate(tmp.get(1));//账单起始日, 格式为yyyy-MM-dd
				    if("长期有效".equals(tmp.get(2))){
				    	cpti.setBillEndDate("2099-12-31");//账单结束日, 格式为yyyy-MM-dd
				    }else{
				    	cpti.setBillEndDate(tmp.get(2));//账单结束日, 格式为yyyy-MM-dd
				    }
					ci.getPackages().add(cpti);
				}
        	}
        	page = cc.getPage(ProcessorCode.PACKAGE_ITEM.getCode()+ "3");
        	if(null != page){
        		pageInfo = page.getWebResponse().getContentAsString().replaceAll("\\s", "").replaceAll("\"", "'");
        		List<List<String>> tmps = RegexUtils.matchesMutiValue("<tr><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td>", pageInfo);
				for(List<String> tmp : tmps){
					cpti = new CarrierPackageItemInfo();
					cpti.setMappingId(ci.getMappingId());//映射id
				    cpti.setItem(tmp.get(0));//套餐项目名称 
				    cpti.setBillStartDate(tmp.get(2));//账单起始日, 格式为yyyy-MM-dd
				    if("长期有效".equals(tmp.get(3))){
				    	cpti.setBillEndDate("2099-12-31");//账单结束日, 格式为yyyy-MM-dd
				    }else{
				    	cpti.setBillEndDate(tmp.get(3));//账单结束日, 格式为yyyy-MM-dd
				    }
					ci.getPackages().add(cpti);
				}
        	}
        	
	        logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
	        pages = cc.getPages(ProcessorCode.RECHARGE_INFO.getCode());
	        CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		pageInfo = p.getWebResponse().getContentAsString().replaceAll("\\s", "").replaceAll("\"", "'");
	        		if(StringUtils.isNotEmpty(pageInfo)){
	        			List<List<String>> tmps = RegexUtils.matchesMutiValue("<tr><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td></tr>", pageInfo);
	    				for(List<String> tmp : tmps){
	    					curi = new CarrierUserRechargeItemInfo();
	    					curi.setMappingId(ci.getMappingId());//映射id
	    					String startDate = tmp.get(1);
	    					curi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
	    					curi.setRechargeTime(startDate.toString());//充值时间，格式：yyyy-MM-dd HH:mm:ss 
	    					Double fee1 = Double.parseDouble(tmp.get(3)) * 100;
	    					curi.setAmount(fee1.intValue());//充值金额(单位: 分)
    						curi.setType(tmp.get(5));//充值方式. e.g. 现金
	    					ci.getRecharges().add(curi);
	    				}
	        		}
	        	}
	        }
	        result.setData(ci);
	        result.setResult(StatusCode.解析成功);
		}catch(Exception e){
			logger.info("==>[{}]解析出错了[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.数据解析中发生错误);
		}
	    return result;
	}
}