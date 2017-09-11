package com.xinyan.spider.isp.mobile.parser.telecom;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.utils.*;
import com.xinyan.spider.isp.mobile.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 湖南电信数据解析类
 * @description
 * @author yyj
 * @date 2017年4月8日 下午4:33:47
 * @version V1.0
 */
@Component
public class HuNanTelecomParser {
	
    protected static Logger logger = LoggerFactory.getLogger(HuNanTelecomParser.class);
	
	public Result parse(Context context, CarrierInfo ci, CacheContainer cc){
		Result result = new Result();
		try{
			logger.info("==>[{}]正在解析运营商用户信息...");
			String pageInfo = "";
			ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
			ci.getCarrierUserInfo().setProvince("湖南省");//所属省份
			
			pageInfo = cc.getString(ProcessorCode.BASIC_INFO.getCode()+"1");
			if(pageInfo!=null){
				pageInfo = pageInfo.replaceAll("\\s", " ").replaceAll("\"", "'");
				ci.getCarrierUserInfo().setPackageName(RegexUtils.matchValue("套餐名称：</th>\\s+<td  colspan='3'>(.*?)</td>", pageInfo));//套餐名称

				ci.getCarrierUserInfo().setCity(RegexUtils.matchValue("var areaName = '(.*?)'", pageInfo));//所属城市
				String openTime = RegexUtils.matchValue("生效时间：</th>                   <td colspan='3'>(.*?)</td>", pageInfo);
				if(StringUtils.isNotEmpty(openTime) && openTime.length()>10){
					ci.getCarrierUserInfo().setOpenTime(openTime.substring(0,10));//入网时间，格式：yyyy-MM-dd
				}else{
					ci.getCarrierUserInfo().setOpenTime(openTime);//入网时间，格式：yyyy-MM-dd
				}
			}

			pageInfo = cc.getString(ProcessorCode.BASIC_INFO.getCode()+"2");
			if(pageInfo!=null){
				pageInfo = pageInfo.replaceAll("\\s", " ").replaceAll("\"", "'");
				ci.getCarrierUserInfo().setName(RegexUtils.matchValue("客户姓名：(.*?)</td>", pageInfo).trim());//姓名
				ci.getCarrierUserInfo().setIdCard(RegexUtils.matchValue("证件号码：(.*?)</td>", pageInfo).trim());//证件号
				ci.getCarrierUserInfo().setAddress(RegexUtils.matchValue("客户地址：(.*?)</td>", pageInfo).trim());//地址
				ci.getCarrierUserInfo().setLevel(RegexUtils.matchValue("客户等级：(.*?)</td>", pageInfo).trim());//帐号星级

				pageInfo = cc.getString(ProcessorCode.BASIC_INFO.getCode()+"3");
				pageInfo = pageInfo.replaceAll("\\s", " ").replaceAll("\"", "'");
				Double amount = Double.parseDouble(RegexUtils.matchValue("<td>自由话费</td>\\s.*?<td>¥(.*?)</td>", pageInfo)) * 100;
				ci.getCarrierUserInfo().setAvailableBalance(amount.intValue());//当前可用余额（单位: 分）
				ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")); //上次更新时间

			}

			
			logger.info("==>[{}]正在解析通话详单...");
	        List<Page> pages = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
	        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
	        for (Page p : pages) {
	        	if(null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())){
	        		pageInfo = p.getWebResponse().getContentAsString();
	            	pageInfo = pageInfo.replaceAll("\\s", "").replaceAll("&nbsp;", "");
	            	
	            	String[] trs = RegexUtils.matchMutiValue("<tr>(.*?)</tr>", pageInfo);
	                for(String tr : trs){
	                	if(!StringUtils.contains(tr , "<th>")){
	                		tr = tr.replaceAll("<td.*?>", "");
	                		String[] tds = tr.split("</td>");
	                		if(tds.length>7){
	                			ccdi = new CarrierCallDetailInfo();
		        				ccdi.setMappingId(ci.getMappingId());//映射id
		        				StringBuilder startDateNew = new StringBuilder (tds[1].trim());  
		        				ccdi.setTime(startDateNew.insert(10, " ").toString());//通话时间，格式：yyyy-MM-dd HH:mm:ss
		        				ccdi.setPeerNumber(tds[3].trim());//对方号码
		        				ccdi.setLocation(tds[5].trim());//通话地(自己的)
		        				int dur = 0;
		        				String callDuriation = tds[4].trim();
		        				String durs = RegexUtils.matchValue("(\\d+)小时", callDuriation);
		        				if(!StringUtils.isEmpty(durs)){
		        					dur += Integer.parseInt(durs) * 3600;
		        				}
		        				durs = RegexUtils.matchValue("(\\d+)分", callDuriation);
		        				if(!StringUtils.isEmpty(durs)){
		        					dur += Integer.parseInt(durs) * 60;
		        				}
		        				durs = RegexUtils.matchValue("(\\d+)秒", callDuriation);
		        				if(!StringUtils.isEmpty(durs)){
		        					dur += Integer.parseInt(durs);
		        				}
		        				ccdi.setDuration(dur+"");//通话时长(单位:秒)
		        				
		        				String callType = tds[2].trim();
		        				if(callType.contains("主叫")){
		        					ccdi.setDialType("DIAL");//DIAL-主叫; DIALED-被叫
		        				}else if(callType.contains("被叫")){
		        					ccdi.setDialType("DIALED");//DIAL-主叫; DIALED-被叫
		        				}else{
		        					ccdi.setDialType(callType);//DIAL-主叫; DIALED-被叫
		        				}
		        				Double totalFee = Double.parseDouble(tds[6].trim()) * 100;
		        				ccdi.setFee(totalFee.intValue());//通话费(单位:分)
		        				ccdi.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));//通话月份
		        				ccdi.setLocationType(tds[7].trim());//通话地类型. e.g.省内漫游
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
	        		
	        		pageInfo = p.getWebResponse().getContentAsString();
	        		pageInfo = pageInfo.replaceAll("\\s", "").replaceAll("&nbsp;", "");
	            	
	            	String[] trs = RegexUtils.matchMutiValue("<tr>(.*?)</tr>", pageInfo);
	                for(String tr : trs){
	                	if(!StringUtils.contains(tr , "<th>")){
	                		tr = tr.replaceAll("<td.*?>", "");
	                		String[] tds = tr.split("</td>");
	                		if(tds.length>5){
		        				csri = new CarrierSmsRecordInfo();
		    					csri.setMappingId(ci.getMappingId());//映射id
								StringBuilder startDateNew = new StringBuilder (tds[1]);  
								csri.setTime(startDateNew.insert(10, " ").toString());//收/发短信时间，格式：yyyy-MM-dd HH:mm:ss
								csri.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));//通话月份
		    					csri.setPeerNumber(tds[3]);//对方号码
		    					csri.setLocation("");//通话地(自己的)
		    					String callType = tds[5];
		    					if(callType.contains("短信")){
		    						csri.setMsgType("SMS");//SMS-短信; MMS-彩信
		    					}else if(callType.contains("彩信")){
		    						csri.setMsgType("MMS");//SMS-短信; MMS-彩信
		    					}else{
		    						csri.setMsgType(callType);//SMS-短信; MMS-彩信
		    					}
		    					csri.setServiceName(callType);//业务名称. e.g. 点对点(网内)
		    					Double fee1 = Double.parseDouble(tds[4]) * 100;
		    					csri.setFee(fee1.intValue());//通话费(单位:分)
		        				ci.getSmses().add(csri);
	                		}
	                	}
	        		}
	        	}
	        }
			
			logger.info("==>[{}]正在解析上网记录...");
	        List<String> pageInfos = cc.getStrings(ProcessorCode.NET_INFO.getCode());
	        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
	        if(pageInfos!=null){
				for (String p : pageInfos) {
					if(StringUtils.isNotEmpty(p)){
						pageInfo = p;
						String[] trs = RegexUtils.matchMutiValue("<tr>(.*?)</tr>", pageInfo);
						for(String tr : trs){
							if(!StringUtils.contains(tr , "<th>")){
								tr = tr.replaceAll("<td(.*?)>", "");
								String[] tds = tr.split("</td>");
								if(tds.length>7){
									cndi = new CarrierNetDetailInfo();
									cndi.setMappingId(ci.getMappingId());//映射id
									StringBuilder startDateNew = new StringBuilder (tds[1].trim());
									cndi.setTime(startDateNew.insert(10, " ").toString());//上网时间，格式：yyyy-MM-dd HH:mm:ss
									cndi.setBillMonth(startDateNew.substring(0, startDateNew.lastIndexOf("-")));
									String duration = tds[2];

									int dur = 0;
									String durs = RegexUtils.matchValue("(\\d+)时", duration);
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

									duration = tds[3];
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
									cndi.setLocation(tds[5]);//流量使用地点
									cndi.setNetType(tds[4]);//网络类型
									cndi.setServiceName(tds[7]);//业务名称
									Double fee1 = Double.parseDouble(tds[6]) * 100;
									cndi.setFee(fee1.intValue());//通信费(单位:分)

									ci.getNets().add(cndi);
								}
							}
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
	        		pageInfo = p.getWebResponse().getContentAsString().replaceAll("\\s", "").replaceAll("&nbsp;", "");
	        		cbdi.setMappingId(ci.getMappingId());//映射id
	        	    cbdi.setNotes(RegexUtils.matchValue("账单周期：.*</td><tdcolspan='2'>(.*?)</td></tr><tr><tdcolspan='2'>", pageInfo));//备注
	        	    cbdi.setBillMonth(PageUtils.getValueById(p, "queryMonth"));//账单月，格式：yyyy-MM
	        	    String[] tmp = RegexUtils.matchMutiValue("账单周期：(.*?)-(.*?)</td>", pageInfo);
	        	    if(null != tmp && tmp.length > 1){
	        	    	cbdi.setBillStartDate(tmp[0].replace("/", "-"));//账期起始日期，格式：yyyy-MM-dd
	        	    	cbdi.setBillEndDate(tmp[1].replace("/", "-"));//账期结束日期，格式：yyyy-MM-dd
	        	    }
	        	    
	        	    tmp = RegexUtils.matchMutiValue("本期新增积分<br/>(.*?)=(.*?)-", pageInfo);
	        	    if(null != tmp && tmp.length > 1){
	        	    	fee = Double.parseDouble(tmp[0]);
		        	    cbdi.setPoint(fee.intValue());//本期可用积分
		        	    fee = Double.parseDouble(tmp[1]);
		        	    cbdi.setLastPoint(fee.intValue());//上期可用积分
	        	    }
	        	    
	        	    tmp = RegexUtils.matchMutiValue("手机:(\\d{11})", pageInfo);
	        	    cbdi.setRelatedMobiles("");
	        	    for(String g : tmp){
	        	    	cbdi.setRelatedMobiles(cbdi.getRelatedMobiles()+"," + g);//本手机关联号码, 多个手机号以逗号分隔
	        	    }
	        	    cbdi.setRelatedMobiles(cbdi.getRelatedMobiles().replaceFirst(",", ""));
	        	    
	        	    String feeStr = RegexUtils.matchValue("本期费用合计：(.*?)<br/>", pageInfo);
	        	    if(StringUtils.isNotEmpty(feeStr)){
	        	    	fee = Double.parseDouble(feeStr) * 100;
	            	    cbdi.setTotalFee(fee.intValue());//总费用 单位分
	        	    }
	        	    
	        	    feeStr = RegexUtils.matchValue("本期已付费用：(.*?).其中", pageInfo);
	        	    if(StringUtils.isNotEmpty(feeStr)){
	        	    	fee = Double.parseDouble(feeStr) * 100;
	        	    	cbdi.setPaidFee(fee.intValue());//本期已付费用 单位分
	        	    }
	        	    
	        	    feeStr = RegexUtils.matchValue("本期应付费用：(.*?)=", pageInfo);
	        	    if(StringUtils.isNotEmpty(feeStr)){
	        	    	fee = Double.parseDouble(feeStr) * 100;
	             	    cbdi.setUnpaidFee(fee.intValue());//本期未付费用 单位分
	        	    }
	        	    
	        	    if(StringUtils.isNotEmpty(pageInfo) && pageInfo.indexOf("(主)")>0){
	        	    	pageInfo = pageInfo.substring(pageInfo.indexOf("(主)"));
	        	    }
	        	    if(StringUtils.isNotEmpty(pageInfo) && pageInfo.indexOf("(本项小计)")>0){
	        		    pageInfo = pageInfo.substring(0,pageInfo.indexOf("本项小计"));
	        	    }	        
	        	    
	        	    List<List<String>> tr = RegexUtils.matchesMutiValue("<tr><tdwidth='50%'style='font-weight:bold'>(.*?)</td><tdwidth='50%'>(.*?)</td></tr>", pageInfo);
	        	   
	        	    for(List<String> tds : tr){
	        	    	if(CollectionUtil.isNotEmpty(tds) && tds.size() > 1){
	        	    		String title = tds.get(0);
	        	    		if(StringUtils.isNotEmpty(tds.get(1)) && StringUtils.isNotEmpty(title)){
	        	    			 fee = Double.parseDouble(tds.get(1)) * 100;
	        	    			 if("月基本费".equals(title)){
	        	    				 cbdi.setBaseFee(fee.intValue());//套餐及固定费 单位分
	        	    			 }else if(title.contains("国内漫游费")){
	        	    				 cbdi.setVoiceFee(fee.intValue());//语音费 单位分
	        	    			 }else if(title.contains("短信彩信费")){
	        	    				 cbdi.setSmsFee(0);//短彩信费 单位分
	        	    			 }else if(title.contains("手机上网费")){
	        	    				 cbdi.setWebFee(fee.intValue());//网络流量费 单位分
	        	    			 }else if(title.contains("可选包")){
	        	    				 cbdi.setExtraFee(fee.intValue());//其它费用 单位分
	        	    			 }
	        	    		}
	        	    	}
	        	    }	        	    
	        	    cbdi.setDiscount(0);//优惠费 单位分
	        	    cbdi.setExtraDiscount(0);//其它优惠 单位分
	        	    cbdi.setActualFee(0);//个人实际费用 单位分
					ci.getBills().add(cbdi);
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析套餐信息...");
	        pages = cc.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
	        CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
	        //页面无时间
	        int month=0;
	        for (Page p : pages) {
	        	if(null != p){
					String startDT = DateUtils.getFirstDay("yyyy-MM-dd", month);
					String endDT = DateUtils.getLastDay("yyyy-MM-dd", month);
					month-=1;
					//获取日期
	        		List<HtmlElement> trs = PageUtils.getElementByXpath(p, "//table[@class='taoc_table']//tr");
	        		for(HtmlElement tr : trs){
	        			List<HtmlElement> tds = PageUtils.getElementByXpath(tr, "td");
	        			if(CollectionUtil.isNotEmpty(tds) && tds.size()>7){
	        				cpti = new CarrierPackageItemInfo();
							String unit = tds.get(5).asText().trim();//单位：语音-分; 流量-KB; 短/彩信-条
							if(unit.contains("MB")){//转化为kb
								cpti.setTotal(String.valueOf((int)(Double.parseDouble(tds.get(2).asText().replace(",","").trim()) * 1024)));
								cpti.setUsed(String.valueOf((int)(Double.parseDouble(tds.get(3).asText().replace(",","").trim()) * 1024)));//项目已使用量
								cpti.setUnit("KB");
							}else{//分钟
								cpti.setTotal(String.valueOf((int)(Double.parseDouble(tds.get(2).asText().replace(",","").trim()) )));
								cpti.setUsed(String.valueOf((int)(Double.parseDouble(tds.get(3).asText().replace(",","").trim()))));//项目已使用量
								cpti.setUnit("分");
							}
	        				cpti.setMappingId(ci.getMappingId());//映射id
	        			    cpti.setItem(tds.get(0).asText().trim());//套餐项目名称
	        			    cpti.setBillStartDate(startDT);//账单起始日, 格式为yyyy-MM-dd
	        			    cpti.setBillEndDate(endDT);//账单结束日, 格式为yyyy-MM-dd
	    					ci.getPackages().add(cpti);
	        			}
	        		}
	        	}
	        }
	        
	        logger.info("==>[{}]正在解析充值记录...");
	        pages = cc.getPages(ProcessorCode.RECHARGE_INFO.getCode());
	        CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
	        for (Page p : pages) {
	        	if(null != p){
	        		List<HtmlElement> trs = PageUtils.getElementByXpath(p, "//table[@class='taoc_table']//tr");
	        		for(HtmlElement tr : trs){
	        			List<HtmlElement> tds = PageUtils.getElementByXpath(tr, "td");
	        			if(CollectionUtil.isNotEmpty(tds) && tds.size()>3){
	        				curi = new CarrierUserRechargeItemInfo();
	        				curi.setMappingId(ci.getMappingId());//映射id
	        			    String startDate = tds.get(0).asText().trim();
	        			    curi.setBillMonth(startDate.substring(0, startDate.lastIndexOf("-")));//账单月，格式：yyyy-MM
	        			    curi.setRechargeTime(startDate);//充值时间，格式：yyyy-MM-dd HH:mm:ss 
	        			    Double fee1 = Double.parseDouble(tds.get(2).asText().trim()) * 100;
	        			    curi.setAmount(fee1.intValue());//充值金额(单位: 分) 
	        			    curi.setType(tds.get(3).asText().trim());//充值方式. e.g. 现金 
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
