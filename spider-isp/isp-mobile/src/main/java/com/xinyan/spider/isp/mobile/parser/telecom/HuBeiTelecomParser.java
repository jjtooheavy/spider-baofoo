package com.xinyan.spider.isp.mobile.parser.telecom;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.utils.*;
import com.xinyan.spider.isp.mobile.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 湖北电信数据解析类
 * @description
 * @author yyj
 * @date 2017年5月11日 下午4:33:47
 * @version V1.0
 */
@Component
public class HuBeiTelecomParser {
	
    protected static Logger logger = LoggerFactory.getLogger(HuBeiTelecomParser.class);
	
	public Result parse(Context context, CarrierInfo ci, CacheContainer cc){
		Result result = new Result();
		try{
			logger.info("==>[{}]正在解析运营商用户信息...");
			String pageInfo = "";
			ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
			ci.getCarrierUserInfo().setProvince("湖北省");//所属省份
			
			// 积分、余额、星级、、Email
			List<Page> pages = cc.getPages(ProcessorCode.AMOUNT.getCode());
			if(pages!=null){
				pageInfo = pages.get(0).getWebResponse().getContentAsString();
				String[] tmps = pageInfo.split(",");
				if(null != tmps && tmps.length>2){
					ci.getCarrierUserInfo().setName(tmps[0]);//姓名
					Double amount = Double.parseDouble(tmps[2]) * 100;
					ci.getCarrierUserInfo().setAvailableBalance(amount.intValue());//当前可用余额（单位: 分）
				}
				//套餐名称
				pageInfo = pages.get(1).getWebResponse().getContentAsString();
				ci.getCarrierUserInfo().setPackageName(RegexUtils.matchValue("<td height=\"12\" align=\"center\">(.*?)</td>", pageInfo).trim());//
				//星级
				pageInfo = pages.get(2).getWebResponse().getContentAsString();
				ci.getCarrierUserInfo().setLevel(RegexUtils.matchValue("</span>--><span>星级:(.*?星)</span></a>", pageInfo.replaceAll("\\s+","")).trim());//
			}
			Page page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
			if(page!=null){
				pageInfo = page.getWebResponse().getContentAsString();
				pageInfo = pageInfo.replaceAll("\\s", " ").replaceAll("\"", "'");
				pageInfo = pageInfo.substring(pageInfo.indexOf("机主姓名")+1);
				if(pageInfo.indexOf("温馨提示") > 0){
					pageInfo = pageInfo.substring(0,pageInfo.indexOf("温馨提示"));
				}
				ci.getCarrierUserInfo().setIdCard(RegexUtils.matchValue("<li id='zjhmli1'>证件号码：(.*?)</li>", pageInfo).trim());//证件号
				String address = RegexUtils.matchValue("<li id='txdzli1'>通信地址：(.*?市).*?</li>", pageInfo);
				ci.getCarrierUserInfo().setCity(RegexUtils.matchValue("省(.*?市)", address));//所属城市
				String openTime = RegexUtils.matchValue("<li id='cjrqli1'>创建日期：(.*?)</li>", pageInfo);
				ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(
						new Date(), "yyyy-MM-dd HH:mm:ss")); //上次更新时间
				if(StringUtils.isNotEmpty(openTime) && openTime.length()>10){
					ci.getCarrierUserInfo().setOpenTime(openTime.substring(0,10));//入网时间，格式：yyyy-MM-dd
				}else{
					ci.getCarrierUserInfo().setOpenTime(openTime);//入网时间，格式：yyyy-MM-dd
				}
				ci.getCarrierUserInfo().setAddress(address);//地址
			}

			
			logger.info("==>[{}]正在解析通话详单...");
	        pages = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
			CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
	        if(pages!=null){
				for (Page p : pages) {
					if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
						List<HtmlElement> elements = PageUtils.getElementByXpath(p, "//*[@id=\"xd01\"]/div/ul/table/tbody/tr");
						for (HtmlElement element : elements) {
							HtmlTableRow tableRow = (HtmlTableRow) element;
							String tmp = tableRow.getCell(0).asText();
							if(!StringUtils.contains(tmp, "起始时间") && tableRow.getCells().size() > 8){
								ccdi = new CarrierCallDetailInfo();
								ccdi.setMappingId(ci.getMappingId());//映射id

								String startDate = tmp.replaceAll("/", "-");
								ccdi.setTime(startDate);//通话时间，格式：yyyy-MM-dd HH:mm:ss
								ccdi.setPeerNumber(tableRow.getCell(2).asText());//对方号码
								ccdi.setLocation(tableRow.getCell(7).asText());//通话地(自己的)
								ccdi.setDuration(tableRow.getCell(3).asText());//通话时长(单位:秒)
								String callType = tableRow.getCell(5).asText();
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
								ccdi.setLocationType(tableRow.getCell(8).asText());//通话地类型. e.g.省内漫游
								ci.getCalls().add(ccdi);
							}
						}
					}
				}
			}

	        
			logger.info("==>[{}]正在解析短信记录...");
	        pages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
	        CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
    				List<HtmlElement> elements = PageUtils.getElementByXpath(p, "//*[@id=\"xd01\"]/div/ul/table/tbody/tr");
    				for (HtmlElement element : elements) {
    					HtmlTableRow tableRow = (HtmlTableRow) element;
    					String tmp = tableRow.getCell(0).asText();
    					if(!StringUtils.contains(tmp, "发送时间") && tableRow.getCells().size() > 5){
							csri = new CarrierSmsRecordInfo();
	    					csri.setMappingId(ci.getMappingId());//映射id
	    					String startDate = tmp.replaceAll("/", "-");
	    					csri.setTime(tableRow.getCell(0).asText());//时间
	    					csri.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//通话月份
		    				csri.setPeerNumber(tableRow.getCell(1).asText());//对方号码
	    					csri.setLocation(tableRow.getCell(2).asText());//通话地(自己的)
	    					String callType = tableRow.getCell(3).asText();
	    					if(callType.contains("短信")){
	    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
	    					}else if(callType.contains("彩信")){
	    						csri.setMsgType("MMS");//SMS-短信; MMS-彩信
	    					}else{
	    						csri.setMsgType(callType);//SMS-短信; MMS-彩信
	    					}
	    					csri.setServiceName(callType);//业务名称. e.g. 点对点(网内)
	    					Double fee1 = Double.parseDouble(tableRow.getCell(4).asText()) * 100;
		    				csri.setFee(fee1.intValue());//通话费(单位:分)
	        				ci.getSmses().add(csri);
						}
    				}
	        	}
	        }
			
			logger.info("==>[{}]正在解析上网记录...");
	        pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
	        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
	        		List<HtmlElement> elements = PageUtils.getElementByXpath(p, "//*[@id=\"xd01\"]/div/ul/table/tbody/tr");
					for (HtmlElement element : elements) {
						HtmlTableRow tableRow = (HtmlTableRow) element;
						String tmp = tableRow.getCell(0).asText();
    					if(!StringUtils.contains(tmp, "开始时间") && tableRow.getCells().size() > 6){
    						cndi = new CarrierNetDetailInfo();
	    					cndi.setMappingId(ci.getMappingId());//映射id
	    					
	    					String startDate = tmp.replaceAll("/", "-");
	    					cndi.setTime(startDate);//上网时间，格式：yyyy-MM-dd HH:mm:ss
	    					cndi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));
	    					
	    					String duration = tableRow.getCell(1).asText();//	    					
	    					int dur = 0;
	    					String durs = RegexUtils.matchValue("(\\d+)时", duration);
	    					if(StringUtils.isNotEmpty(durs)){
	    						dur += Integer.parseInt(durs) * 3600;
	    					}
	    					durs = RegexUtils.matchValue("(\\d+)分钟", duration);
	    					if(StringUtils.isNotEmpty(durs)){
	    						dur += Integer.parseInt(durs) * 60;
	    					}
	    					durs = RegexUtils.matchValue("(\\d+)秒", duration);
	    					if(StringUtils.isNotEmpty(durs)){
	    						dur += Integer.parseInt(durs);
	    					}
	    					cndi.setDuration(dur);//流量使用时长
	    					
	    					duration = tableRow.getCell(2).asText();;
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
	    					cndi.setLocation(tableRow.getCell(4).asText());//流量使用地点
	    					cndi.setNetType(tableRow.getCell(3).asText());//网络类型
	    					cndi.setServiceName(tableRow.getCell(5).asText());//业务名称
	    					Double fee1 = Double.parseDouble(tableRow.getCell(6).asText()) * 100;
	    					cndi.setFee(fee1.intValue());//通信费(单位:分)
	    					ci.getNets().add(cndi);
						}
					}
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析账单记录...");
	        pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
	        Double fee;
	        for (Page p : pages) {
				CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
	        		pageInfo = p.getWebResponse().getContentAsString().replaceAll("\\s", "").replaceAll("\"", "'");	
	    			pageInfo = pageInfo.substring(pageInfo.indexOf("客户姓名")+1);
	    			if(pageInfo.indexOf("温馨提示") > 0){
	    				pageInfo = pageInfo.substring(0,pageInfo.indexOf("温馨提示"));
	    			}
	        		
	        		cbdi.setMappingId(ci.getMappingId());//映射id
	        	    cbdi.setNotes(RegexUtils.matchValue("<tdstyle='padding-left:25px;'>(.*?)<inputtype='button'onclick='toPay", pageInfo));//备注
	        	    String date = RegexUtils.matchValue("账单周期：</td><td>(.*?)</td>", pageInfo);
	        	    if(null != date){
	        	    	cbdi.setBillStartDate(date.substring(0,date.indexOf("-")).replace("/", "-"));//账期起始日期，格式：yyyy-MM-dd
	        	    	cbdi.setBillEndDate(date.substring(date.indexOf("-")+1,date.length()).replace("/", "-"));//账期结束日期，格式：yyyy-MM-dd
	        	    	cbdi.setBillMonth(date.replace("/", "-").substring(0,7));//账单月，格式：yyyy-MM
	        	    }
	        	    
	        	    String feeStr = RegexUtils.matchValue("套餐使用费</div></td><tdwidth='50'align='right'bgColor=''><fontclass='shuzi'>(.*?)元", pageInfo);
	        	    if(StringUtils.isNotEmpty(feeStr)){
	        	    	fee = Double.parseDouble(feeStr) * 100;
	            	    cbdi.setBaseFee(fee.intValue());//套餐及固定费 单位分
	        	    }
					String voice = RegexUtils.matchValue("语音通信费</font>.*?<fontclass='shuzi'>(.*?)元</font>", pageInfo);
					if(StringUtils.isNotEmpty(voice)){
						fee = Double.parseDouble(voice) * 100;
						cbdi.setVoiceFee(fee.intValue());//套餐及固定费 单位分
					}
					String web = RegexUtils.matchValue("上网及数据通信费</font>.*?<fontclass='shuzi'>(.*?)元</font>", pageInfo);
					if(StringUtils.isNotEmpty(web)){
						fee = Double.parseDouble(web) * 100;
						cbdi.setWebFee(fee.intValue());//套餐及固定费 单位分
					}
	        	    
	        	    feeStr = RegexUtils.matchValue("本期费用合计：(.*?)元", pageInfo);
	        	    if(StringUtils.isNotEmpty(feeStr)){
	        	    	fee = Double.parseDouble(feeStr) * 100;
	        	    	cbdi.setActualFee(fee.intValue());//个人实际费用 单位分
						cbdi.setTotalFee(fee.intValue());
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
	        	    
	        	    String[] tmp = RegexUtils.matchMutiValue("&nbsp;</td><tdalign='center'valign='middle'>(.*?)</td><tdalign='center'valign='middle'>=</td><tdalign='center'valign='middle'>(.*?)</td>", pageInfo);
	        	    if(null != tmp && tmp.length > 1){
	        	    	cbdi.setPoint(Integer.parseInt(tmp[0]));//本期可用积分
		        	    cbdi.setLastPoint(Integer.parseInt(tmp[1]));//上期可用积分
	        	    }
	        	    
	        	    cbdi.setRelatedMobiles("");//本手机关联号码, 多个手机号以逗号分隔
					ci.getBills().add(cbdi);
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析套餐信息...");
	        page = cc.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
	        CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
        	if(null != page){
        		List<HtmlElement> trs = PageUtils.getElementByXpath(page, "//table//tr[@class='hovergray']");
        		for(HtmlElement tr : trs){
        			List<HtmlElement> tds = PageUtils.getElementByXpath(tr, "td");
        			if(CollectionUtil.isNotEmpty(tds) && tds.size()>6){
        				cpti = new CarrierPackageItemInfo();

        				cpti.setMappingId(ci.getMappingId());//映射id
        			    cpti.setItem(tds.get(0).asText().trim());//套餐项目名称 
        			    cpti.setTotal(tds.get(2).asText().trim().replaceAll("&nbsp;", ""));//项目总量 
        			    cpti.setUsed(tds.get(3).asText().trim());//项目已使用量 
        			    cpti.setUnit(tds.get(5).asText().trim());//单位：语音-分; 流量-KB; 短/彩信-条 
        			    String tmp = tds.get(6).asText().trim().replace("年", "-").replace("月", "-");
        			    cpti.setBillStartDate(tmp.substring(0, 7)+"-01");//账单起始日, 格式为yyyy-MM-dd
        			    cpti.setBillEndDate(tmp.substring(0, 10));//账单结束日, 格式为yyyy-MM-dd
    					
    					ci.getPackages().add(cpti);
        			}
        		}
        	}
	        
	        logger.info("==>[{}]正在解析充值记录...");
	        pages = cc.getPages(ProcessorCode.RECHARGE_INFO.getCode());
	        CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		pageInfo = p.getWebResponse().getContentAsString();
	        		if(StringUtils.isNotEmpty(pageInfo)){
	        			String[] pis = pageInfo.split(",");
	        			if(null != pis && pis.length>3){
	        				curi = new CarrierUserRechargeItemInfo();
	        				curi.setMappingId(ci.getMappingId());//映射id
	        				StringBuilder startDate = new StringBuilder(pis[2]);
	        				startDate.insert(4, "-");
	        				startDate.insert(7, "-");
	        				curi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
	        				curi.setRechargeTime(startDate.toString());//充值时间，格式：yyyy-MM-dd HH:mm:ss 
	        				Double fee1 = Double.parseDouble(pis[1]) * 100;
	        				curi.setAmount(fee1.intValue());//充值金额(单位: 分)
							if(pis[3].indexOf("&")>0){
								curi.setType(pis[3].substring(0,pis[3].indexOf("&")));//充值方式. e.g. 现金
							}else{
								curi.setType(pis[3]);//充值方式. e.g. 现金
							}
	        				ci.getRecharges().add(curi);
	        			}
	        		}
	        	}
	        }
	        result.setData(ci);
	        result.setResult(StatusCode.解析成功);
		}catch(Exception e){
			logger.info("==>[{}]解析出错了", context.getTaskId(), e);
			result.setResult(StatusCode.数据解析中发生错误);
		}
	    return result;
	}
}