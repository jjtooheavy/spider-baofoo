package com.xinyan.spider.isp.mobile.parser.cmcc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateFormatUtils;
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
import com.xinyan.spider.isp.common.utils.FormatUtils;
import com.xinyan.spider.isp.common.utils.PageUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.mobile.model.CarrierBillDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierCallDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.model.CarrierNetDetailInfo;
import com.xinyan.spider.isp.mobile.model.CarrierPackageItemInfo;
import com.xinyan.spider.isp.mobile.model.CarrierSmsRecordInfo;
import com.xinyan.spider.isp.mobile.model.CarrierUserInfo;
import com.xinyan.spider.isp.mobile.model.CarrierUserRechargeItemInfo;

/**
 * @author heliang
 * @author heliang
 * @version V1.0
 * @description
 * @date 2016年8月25日 下午1:41:05
 * @description 添加继承类AbstractParser相关处理
 */
@Component
public class ShangHaiCmccParser {
    protected static Logger logger = LoggerFactory.getLogger(ShangHaiCmccParser.class);

    /**
     * 解析基本信息
     *
     * @param context
     * @param cacheContainer
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result basicInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();

        // 个人基本信息解析
		CarrierUserInfo carrierUserInfo = new CarrierUserInfo();
        Page page = cacheContainer.getPage(ProcessorCode.BASIC_INFO.getCode());
        String strBasicData = page.getWebResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(strBasicData);
		JSONObject basicInfoData = jsonObject.getJSONObject("value");
        // 手机号、真实姓名、入网时间、联系地址、身份证号码、Email
		carrierUserInfo.setName(basicInfoData.getString("name")); // 姓名
		carrierUserInfo.setIdCard(basicInfoData.getString("zjNum")); //身份证
		carrierUserInfo.setMobile(basicInfoData.getString("strPhoneNo"));// 手机号
		carrierUserInfo.setOpenTime(DateUtils.dateToString(
				DateUtils.stringToDate(basicInfoData.getString("creaateDate"), "yyyy年MM月dd日"), "yyyy-MM-dd"));//入网时间
		String level= basicInfoData.getString("creditLevel");//星级
		carrierUserInfo.setLastModifyTime(DateUtils.dateToString(
				new Date(), "yyyy-MM-dd HH:mm:ss")); //上次更新时间

        carrierUserInfo.setCarrier("CHINA_MOBILE"); // 运营商 CHINA_MOBILE 中国移动
//        // CHINA_TELECOM 中国电信
//        // CHINA_UNICOM 中国联通
        carrierUserInfo.setProvince("上海"); // 所属省份
        carrierUserInfo.setCity("上海"); // 所属城市
		if("1".equals(level)){
			carrierUserInfo.setLevel("一星");
		}else if("2".equals(level)){
			carrierUserInfo.setLevel("二星");
		}else if("3".equals(level)){
			carrierUserInfo.setLevel("三星");
		}else if("4".equals(level)){
			carrierUserInfo.setLevel("四星");
		}else if("5".equals(level)){
			carrierUserInfo.setLevel("五星");
		}else{
			carrierUserInfo.setLevel("无");
		}
		page = cacheContainer.getPage(ProcessorCode.AMOUNT.getCode());//余额

		strBasicData = page.getWebResponse().getContentAsString();

		jsonObject = JSONObject.parseObject(strBasicData);
		basicInfoData = jsonObject.getJSONObject("value");

		carrierUserInfo.setAvailableBalance(
                (int) (Double.parseDouble(basicInfoData.getString("usable_fee")) * 100)); // 当前可用余额（单位:
        // 分）
		carrierUserInfo.setPackageName(basicInfoData.getString("plan_name"));
        carrierInfo.setCarrierUserInfo(carrierUserInfo);
        result.setSuccess();
        return result;
    }

    /**
     * 解析通话记录
     *
     * @param context
     * @param cacheContainer
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result callRecordParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();
        List<CarrierCallDetailInfo> calls = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
        if(pages != null){
	        for (Page page : pages) {
	            // 获取采集年份
	            String year = DateUtils.getCurrentYear();
	            List<HtmlElement> e = PageUtils.getElementByXpath(page, "//*[@type=\"text/javascript\"]");
	            if (CollectionUtil.isNotEmpty(e)) {
	                String strValue = e.get(0).toString();
	                if (strValue.indexOf("value") > 0) {
	                    strValue = strValue.substring(strValue.indexOf("[[") + 2);
	                    strValue = strValue.substring(0, strValue.lastIndexOf("]]"));
	                    strValue = strValue.replaceAll("\"", "");
	                    strValue = strValue.replaceAll("\\],\\[", "|");
	                    strValue = strValue.replaceAll("'", "");
	                    String[] record = strValue.split("\\|");
	                    for (String r : record) {
	                        String[] d = r.split(",");
	                        CarrierCallDetailInfo c = new CarrierCallDetailInfo();
	                        c.setMappingId(carrierInfo.getMappingId());
	                        c.setTime(year + "-" + d[1]);// 通话时间
	                        c.setPeerNumber(d[4]);// 对方号码
	                        c.setLocation(d[2]);// 通话地点
	                        c.setLocationType(d[6]);// 通话地类型. e.g.省内漫游
	                        c.setDuration(String.valueOf(Integer.parseInt(d[5].split(":")[0]) * 60 * 60
	                                + Integer.parseInt(d[5].split(":")[1]) * 60 + Integer.parseInt(d[5].split(":")[2])));// 通话时长
	                       	if("主叫".equals(d[3])){
								c.setDialType("DIAL");// 通话类型
							}else if("被叫".equals(d[3])){
								c.setDialType("DIALED");// 通话类型
							}else{
								c.setDialType("");// 通话类型
							}
	                        c.setFee((int) (Double.parseDouble(d[8]) * 100));
	                        c.setBillMonth(year+"-"+d[1].split("-")[0]);
	                        calls.add(c);
	                    }
	                } else {
	                    logger.info("==>暂无此详单类型的记录");
	                }
	            }
	        }
        }
        carrierInfo.setCalls(calls);
        result.setSuccess();
        return result;
    }

    /**
     * 解析短信记录
     *
     * @param context
     * @param cacheContainer
     * @param context
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result smsInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();
        List<CarrierSmsRecordInfo> smses = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.SMS_INFO.getCode());
        if(pages != null){
	        for (Page page : pages) {
	
	            // 获取采集年份
	            String year = DateUtils.getCurrentYear();
	            List<HtmlElement> elements = PageUtils.getElementByXpath(page, "//script[@type]");
	            String strValue = elements.get(0).toString();
	            if (strValue.indexOf("value") > 0) {
	                strValue = strValue.substring(strValue.indexOf("[[") + 2);
	                strValue = strValue.substring(0, strValue.lastIndexOf("]]"));
	                strValue = strValue.replaceAll("\"", "");
	                strValue = strValue.replaceAll("\\],\\[", "|");
	                strValue = strValue.replaceAll("\'", "");
	                String[] record = strValue.split("\\|");
	
	                for (String r : record) {
	                    CarrierSmsRecordInfo s = new CarrierSmsRecordInfo();
	                    String[] d = r.split(",");
	                    s.setMappingId(carrierInfo.getMappingId());
	                    s.setTime(year + "-" + d[1]);// 发送时间
	                    s.setPeerNumber(d[3]);// 与本机通话手机号码
	                    s.setLocation(d[2]);// 发送地
						if(d[4].equals("接收")){
							s.setSendType("RECRIVE");// 发送类型
						}else{
							s.setSendType("SEND");// 发送类型
						}
						if(d[5].equals("短信")){
							s.setMsgType("SMS");// 发送类型
						}else if(d[5].equals("彩信")){
							s.setMsgType("MMS");// 发送类型
						}
	                    s.setFee((int) (Double.parseDouble(d[7]) * 100));// 通话费(单位:分)
	                    s.setBillMonth(year + "-" + d[1].substring(0,2));
	                    smses.add(s);

					}
	            } else {
	                logger.info("==>暂无此详单类型的记录");
	            }
	        }
        }

        carrierInfo.setSmses(smses);
        result.setSuccess();
        return result;
    }

    /**
     * 解析上网记录
     *
     * @param context
     * @param cacheContainer
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result netInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();

        List<CarrierNetDetailInfo> nets = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.NET_INFO.getCode());
        if(pages != null){
	        for (Page page : pages) {
	
	            // 获取采集年份
	            String year = DateUtils.getCurrentYear();
	            List<HtmlElement> elements = PageUtils.getElementByXpath(page, "//script[@type]");
	            if(null != elements && elements.size() > 0){
					String strValue = elements.get(0).toString();
					if (strValue.indexOf("value") > 0) {
						strValue = strValue.substring(strValue.indexOf("[[") + 2);
						strValue = strValue.substring(0, strValue.lastIndexOf("]]"));
						strValue = strValue.replaceAll("\"", "");
						strValue = strValue.replaceAll("\\],\\[", "|");
						strValue = strValue.replaceAll("\'", "");
						String[] record = strValue.split("\\|");
						for (String r : record) {
							String[] d = r.split(",");
							CarrierNetDetailInfo n = new CarrierNetDetailInfo();
							n.setMappingId(carrierInfo.getMappingId());
							n.setTime((year + "-" + d[1]));// 上网时间
							n.setBillMonth(year + "-" + d[1].substring(0,2));
							n.setDuration(Integer.parseInt(d[4].split(":")[0]) * 60 * 60
									+ Integer.parseInt(d[4].split(":")[1]) * 60 + Integer.parseInt(d[4].split(":")[2]));// 上网时长
							if (d[5].contains("KB")) {
								n.setSubflow(Integer.parseInt(d[5].replace("KB", "")));// 流量使用量，单位:KB
							} else {
								n.setSubflow(Integer.parseInt(d[5].replace("MB", "")) * 1024);// 流量使用量，单位:KB
							}

							n.setLocation(d[2]);// 上网地点
							n.setServiceName(d[6]);// 业务名称
							n.setNetType(d[8].replace("网络", ""));// 上网类型
							n.setFee((int) (Double.parseDouble(d[7]) * 100));// 通话费(单位:分)
							nets.add(n);
						}
					} else {
						logger.info("==>暂无此详单类型的记录");
					}
				}

	        }
        }
        carrierInfo.setNets(nets);
        result.setSuccess();
        return result;
    }

    /**
     * 解析账单信息
     *
     * @param context
     * @param cacheContainer
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result billParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();

        List<CarrierBillDetailInfo> bills = new ArrayList<>();

        List<Page> pages = cacheContainer.getPages(ProcessorCode.BILL_INFO.getCode());
        if(pages != null){
	        for (Page page : pages) {
				if(page.getWebResponse().getContentAsString().contains("您暂无该类型的业务数据")){
					continue;
				}
	            String strTemp = page.getWebResponse().getContentAsString();
	            CarrierBillDetailInfo billinfo = new CarrierBillDetailInfo();
	            billinfo.setMappingId(carrierInfo.getMappingId());
				String reg = "";
				Matcher m;
	            if(page.getWebResponse().getContentAsString().contains("计费周期")){//非当前月时间解析
					reg = "计费周期：</b></td><td>(.*?)月";
					m = Pattern.compile(reg).matcher(strTemp);
					if (m.find()) {
						billinfo.setBillMonth(FormatUtils.formatDate(m.group(0).replace("计费周期：</b></td><td>", ""), false));// 账单月份
					}
					// 开始周期
					reg = "计费周期：</b></td><td>(.*?)</td></tr>";
					m = Pattern.compile(reg).matcher(strTemp);
					if (m.find()) {
						billinfo.setBillStartDate(FormatUtils.formatDate(m.group(0).replace("计费周期：</b></td><td>", ""), true));// 开始月份
					}
					// 结束周期
					reg = "计费周期：</b></td><td>(.*?)</td></tr>";
					m = Pattern.compile(reg).matcher(strTemp);
					if (m.find()) {
						billinfo.setBillEndDate(FormatUtils.formatDate(m.group(0).split("至")[1], true));// 结束月份
					}
				}else{//当前月账单周期
					Calendar calendar = Calendar.getInstance();
					billinfo.setBillMonth(DateFormatUtils.format(calendar, "yyyy-MM"));
					billinfo.setBillStartDate(DateFormatUtils.format(calendar, "yyyy-MM")+"-01");
					billinfo.setBillEndDate(DateFormatUtils.format(calendar, "yyyy-MM-dd"));// 结束月份
				}
	            // 套餐及固定费 单位分
	            String temp = strTemp.substring(strTemp.indexOf("套餐及固定费</p>"), strTemp.indexOf("套餐外上网费</p>"));
	            reg = "<td style=\"text-align:left;padding-left:40px;\">(.*?)</td>";

	            m = Pattern.compile(reg).matcher(temp);
	            if (m.find()) {
	                billinfo.setBaseFee((int) (Double.parseDouble(m.group(0).replace("<td style=\"text-align:left;padding-left:40px;\">", "").replace("</td>", "")) * 100));
	            }else{
					reg = "<td style='text-align:left;padding-left:40px;'>(.*?)</td>";
					m = Pattern.compile(reg).matcher(temp);
					if(m.find()){
						billinfo.setBaseFee((int) (Double.parseDouble(m.group(0).replace("<td style='text-align:left;padding-left:40px;'>", "").replace("</td>", "").replace("￥","")) * 100));
					}
				}
	            // 增值业务费 单位分
	            temp = strTemp.substring(strTemp.indexOf("增值业务费</p>"), strTemp.indexOf("其他费用</p>"));
	            reg = "<td style=\"text-align:left;padding-left:40px;\">(.*?)</td>";
	            m = Pattern.compile(reg).matcher(temp);
	            if (m.find()) {
	                billinfo.setExtraServiceFee((int)( Double.parseDouble(m.group(0).replace("<td style=\"text-align:left;padding-left:40px;\">", "").replace("</td>", "").replace("￥","")) * 100));
	            }else{
					reg = "<td style='text-align:left;padding-left:40px;'>(.*?)</td>";
					m = Pattern.compile(reg).matcher(temp);
					if(m.find()){
						billinfo.setExtraServiceFee((int) (Double.parseDouble(m.group(0).replace("<td style='text-align:left;padding-left:40px;'>", "").replace("</td>", "").replace("￥","")) * 100));
					}
				}
	            // 语音费 单位分
	            temp = strTemp.substring(strTemp.indexOf("套餐外语音通信费</p>"), strTemp.indexOf("套餐外短彩信费</p>"));
	            reg = "<td style=\"text-align:left;padding-left:40px;\">(.*?)</td>";
	            m = Pattern.compile(reg).matcher(temp);
	            if (m.find()) {
	                billinfo.setVoiceFee((int) (Double.parseDouble(m.group(0).replace("<td style=\"text-align:left;padding-left:40px;\">", "").replace("</td>", "").replace("￥","")) * 100));
	            }else{
					reg = "<td style='text-align:left;padding-left:40px;'>(.*?)</td>";
					m = Pattern.compile(reg).matcher(temp);
					if(m.find()){
						billinfo.setVoiceFee((int) (Double.parseDouble(m.group(0).replace("<td style='text-align:left;padding-left:40px;'>", "").replace("</td>", "").replace("￥","")) * 100));
					}
				}
	            //短彩信费 单位分
	            temp = strTemp.substring(strTemp.indexOf("套餐外短彩信费</p>"), strTemp.indexOf("代收费业务费用</p>"));
	            reg = "<td style=\"text-align:left;padding-left:40px;\">(.*?)</td>";
	            m = Pattern.compile(reg).matcher(temp);
	            if (m.find()) {
	                billinfo.setSmsFee((int) (Double.parseDouble(m.group(0).replace("<td style=\"text-align:left;padding-left:40px;\">", "").replace("</td>", "").replace("￥","")) * 100));
	            }else{
					reg = "<td style='text-align:left;padding-left:40px;'>(.*?)</td>";
					m = Pattern.compile(reg).matcher(temp);
					if(m.find()){
						billinfo.setSmsFee((int) (Double.parseDouble(m.group(0).replace("<td style='text-align:left;padding-left:40px;'>", "").replace("</td>", "").replace("￥","")) * 100));
					}
				}
	            //网络流量费 单位分
	            temp = strTemp.substring(strTemp.indexOf("套餐外上网费</p>"), strTemp.indexOf("增值业务费</p>"));
	            reg = "<td style=\"text-align:left;padding-left:40px;\">(.*?)</td>";
	            m = Pattern.compile(reg).matcher(temp);
	            if (m.find()) {
	                billinfo.setWebFee((int) (Double.parseDouble(m.group(0).replace("<td style=\"text-align:left;padding-left:40px;\">", "").replace("</td>", "").replace("￥","")) * 100));
	            }else{
					reg = "<td style='text-align:left;padding-left:40px;'>(.*?)</td>";
					m = Pattern.compile(reg).matcher(temp);
					if(m.find()){
						billinfo.setWebFee((int) (Double.parseDouble(m.group(0).replace("<td style='text-align:left;padding-left:40px;'>", "").replace("</td>", "").replace("￥","")) * 100));
					}
				}
	            //其它费用 单位分
	            temp = strTemp.substring(strTemp.indexOf("其他费用</p>"), strTemp.indexOf("减免费用</p>"));
	            reg = "<td style=\"text-align:left;padding-left:40px;\">(.*?)</td>";
	            m = Pattern.compile(reg).matcher(temp);
	            if (m.find()) {
	                billinfo.setExtraFee((int) (Double.parseDouble(m.group(0).replace("<td style=\"text-align:left;padding-left:40px;\">", "").replace("</td>", "").replace("￥","")) * 100));
	            }else{
					reg = "<td style='text-align:left;padding-left:40px;'>(.*?)</td>";
					m = Pattern.compile(reg).matcher(temp);
					if(m.find()){
						billinfo.setExtraFee((int) (Double.parseDouble(m.group(0).replace("<td style='text-align:left;padding-left:40px;'>", "").replace("</td>", "").replace("￥","")) * 100));
					}
				}
	            // 总金额
	            reg = "本期总费用：</b></td>\\s+<td style=\"width:200px\"><strong>￥(.*?)元";
	            m = Pattern.compile(reg).matcher(strTemp);
	            if (m.find()) {
	                billinfo.setTotalFee((int) (Double.parseDouble(m.group(0).split("￥")[1].replace("元", "")) * 100));
	            } else {
	                reg = "本期总费用：</b></td><td style='width:230px'><strong>￥(.*?)</strong>";
	                m = Pattern.compile(reg).matcher(strTemp);
	                if (m.find()) {
	                    billinfo.setTotalFee((int) (Double.parseDouble(m.group(0).replace("本期总费用：</b></td><td style='width:230px'><strong>￥", "").replace("</strong>", "")) * 100));
	                } else {
	                    billinfo.setTotalFee(0);
	                }
	            }
	            //优惠费 单位分
	            temp = strTemp.substring(strTemp.indexOf("减免费用</p>"), strTemp.indexOf("套餐外语音通信费</p>"));
	            reg = "<td style=\"text-align:left;padding-left:40px;\">(.*?)</td>";
	            m = Pattern.compile(reg).matcher(temp);
	            if (m.find()) {
	                billinfo.setDiscount((int)( Double.parseDouble(m.group(0).replace("<td style=\"text-align:left;padding-left:40px;\">", "").replace("</td>", "").replace("￥","")) * 100));
	            }else{
					reg = "<td style='text-align:left;padding-left:40px;'>(.*?)</td>";
					m = Pattern.compile(reg).matcher(temp);
					if(m.find()){
						billinfo.setDiscount((int) (Double.parseDouble(m.group(0).replace("<td style='text-align:left;padding-left:40px;'>", "").replace("</td>", "").replace("￥","")) * 100));
					}
				}
	            bills.add(billinfo);
	        }
		}

        carrierInfo.setBills(bills);

        result.setSuccess();
        return result;
    }

    /**
     * 解析套餐信息
     *
     * @param context
     * @param cacheContainer
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result packageItemInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<CarrierPackageItemInfo> packageItemInfos = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
        if(pages != null){
	        for (Page page : pages) {
	        	//获取计费日期
				String strTemp = page.getWebResponse().getContentAsString();
				String reg = "计费周期：</b></td><td>(.*?)</td>";
				Matcher m = Pattern.compile(reg).matcher(strTemp);
				String bill_start_date ="";
				String bill_end_date = "";
				if (m.find()){
					String date = m.group(1).trim();
					bill_start_date = DateUtils.dateToString(
							DateUtils.stringToDate(date.substring(0,date.indexOf("至")), "yyyy年MM月dd日"), "yyyy-MM-dd");
					bill_end_date = DateUtils.dateToString(
							DateUtils.stringToDate(date.substring(date.indexOf("至")+1), "yyyy年MM月dd日"), "yyyy-MM-dd");
				}
				List<HtmlElement> elements = PageUtils.getElementByXpath(page, "//tr[@class=\"tbody_bottom_gray\"]");
				for(HtmlElement ele:elements.subList(2,elements.size())){
					if(PageUtils.getValueByXpath(ele, "./td[1]").contains("(")){//判断是否为套餐
						CarrierPackageItemInfo packageItemInfo = new CarrierPackageItemInfo();//不按月份，在这里new
						packageItemInfo.setMappingId(carrierInfo.getMappingId());
						packageItemInfo.setBillStartDate(bill_start_date); //账单起始日, 格式为yyyy-MM-dd
						packageItemInfo.setBillEndDate(bill_end_date);//账单结束日, 格式为yyyy-MM-dd
						String item = PageUtils.getValueByXpath(ele, "./td[1]");//item
						String unit = item.substring(item.lastIndexOf("(")+1,item.lastIndexOf(")"));
						packageItemInfo.setItem(item.substring(0,item.lastIndexOf("(")));//设置名称
						if (item.contains("语音")){//通话套餐
							packageItemInfo.setUnit("分");
							packageItemInfo.setTotal(PageUtils.getValueByXpath(ele, "./td[2]").replace("分钟",""));
							packageItemInfo.setUsed(PageUtils.getValueByXpath(ele, "./td[3]").replace("分钟",""));
						}else{//流量
							packageItemInfo.setUnit("KB");
							if(unit.contains("MB")){
								packageItemInfo.setTotal(String.valueOf((int)(Double.parseDouble(PageUtils.getValueByXpath(ele, "./td[2]").replace("MB","")) * 1024)));
								packageItemInfo.setUsed(String.valueOf((int)(Double.parseDouble(PageUtils.getValueByXpath(ele, "./td[3]").replace("MB","")) * 1024)));
							}else if(unit.contains("KB")){
								packageItemInfo.setTotal(PageUtils.getValueByXpath(ele, "./td[2]").replace("KB",""));
								packageItemInfo.setUsed(PageUtils.getValueByXpath(ele, "./td[3]").replace("KB",""));
							}else if(unit.contains("GB")){
								packageItemInfo.setTotal(String.valueOf((int)(Double.parseDouble(PageUtils.getValueByXpath(ele, "./td[2]").replace("GB","")) *1024*1024)));
								packageItemInfo.setUsed(String.valueOf((int)(Double.parseDouble(PageUtils.getValueByXpath(ele, "./td[3]").replace("GB","")) *1024*1024)));
							}

						}
						packageItemInfos.add(packageItemInfo);
					}
				    }
	            }
	        }
        carrierInfo.setPackages(packageItemInfos);
        result.setSuccess();
        return result;

    }

    /**
     * 解析亲情号码
     *
     * @param context
     * @param cacheContainer
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result userFamilyMemberParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        result.setSuccess();
        return result;

    }

    /**
     * 解析充值记录
     *
     * @param context
     * @param cacheContainer
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result userRechargeItemInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<CarrierUserRechargeItemInfo> rechargeItemInfos = new ArrayList<>();

        List<Page> pages = cacheContainer.getPages(ProcessorCode.RECHARGE_INFO.getCode());
        if(pages != null){
	        for(Page page : pages){
	            JSONObject userRechargeItemObj = JSONObject.parseObject(page.getWebResponse().getContentAsString());
	            JSONArray jsonArray = (JSONArray) userRechargeItemObj.get("adlList");
	            for (Object object : jsonArray) {
	                CarrierUserRechargeItemInfo rechargeItemInfo = new CarrierUserRechargeItemInfo();
	                rechargeItemInfo.setMappingId(carrierInfo.getMappingId());
	                JSONObject jsonObject = (JSONObject) object;
	                rechargeItemInfo.setAmount((jsonObject.getIntValue("fee")));//充值金额(单位: 分)
	                rechargeItemInfo.setBillMonth(jsonObject.getString("busi_date").substring(0,7));//充值月份，格式：yyyy-MM
	                rechargeItemInfo.setType(jsonObject.getString("spec_name"));//充值方式. e.g. 现金
	                rechargeItemInfo.setRechargeTime(jsonObject.getString("busi_date"));//充值时间，格式：yyyy-MM-dd HH:mm:ss
	                rechargeItemInfos.add(rechargeItemInfo);
	            }
	            carrierInfo.setRecharges(rechargeItemInfos);
	            result.setSuccess();
	        }
        }
        return result;

    }

}
