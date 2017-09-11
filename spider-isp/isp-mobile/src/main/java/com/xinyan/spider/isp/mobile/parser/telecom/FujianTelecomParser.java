package com.xinyan.spider.isp.mobile.parser.telecom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.PageUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 黑龙江电信解析类
 *
 * @author jiangmengchen
 * @version V1.0
 * @description
 * @date 2017年6月3日 下午3:38:43
 */
@Component
public class FujianTelecomParser {

    protected static Logger logger = LoggerFactory.getLogger(FujianTelecomParser.class);

    public Result parse(Context context, CarrierInfo ci, CacheContainer cc) {
        Result result = new Result();
        try {
            logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
            JSONObject jsonObject = null;
            String pageInfo = "";
            ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
            ci.getCarrierUserInfo().setProvince("福建");//所属省份
            ci.getCarrierUserInfo().setName(context.getIdName());
            ci.getCarrierUserInfo().setIdCard(context.getIdCard());
            ci.getCarrierUserInfo().setCity(context.getMobileHCodeDto().getCityName());
            ci.getCarrierUserInfo().setState(-1);
            //个人信息
            Page page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                HtmlTableRow htmlTableRow = (HtmlTableRow) PageUtils.getElementByXpath(page, "//table[@cellpadding='5']/tbody/tr").get(1);
                String packageName = htmlTableRow.getCell(0).asText();
                ci.getCarrierUserInfo().setPackageName(packageName);
                ci.getCarrierUserInfo().setMobile(context.getUserName());
            }
            //余额
            page = cc.getPage(ProcessorCode.AMOUNT.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                jsonObject = JSONObject.parseObject(pageInfo);
                String money = jsonObject.getString("OHERMONEY");
                ci.getCarrierUserInfo().setAvailableBalance((int) (Double.parseDouble(money) * 100));
                ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            }
            logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
            List<Page> calls = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
            if(calls!=null){
                for (Page callPage : calls) {
                    HtmlTable table = (HtmlTable) PageUtils.getElementByXpath(callPage, "//table[@border='0']").get(1);
                    List<HtmlTableRow> rows = (List) PageUtils.getElementByXpath(table, "//tbody/tr");
                    for (int i = 3; i <= rows.size() - 1; i++) {
                        if (rows.get(i).asXml().contains("暂无您所查询的数据清单")) {
                            continue;
                        }
                        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
                        ccdi.setBillMonth(rows.get(i).getCell(1).asText().substring(0, 7));
                        ccdi.setDialType(rows.get(i).getCell(3).asText().contains("主叫") ? "DIAL" : "DIALED");
                        String hour = RegexUtils.matchValue("(\\d+)时", rows.get(i).getCell(2).asText());
                        String min = RegexUtils.matchValue("(\\d+)分", rows.get(i).getCell(2).asText());
                        String sec = RegexUtils.matchValue("(\\d+)秒", rows.get(i).getCell(2).asText());
                        int finalDuration = 0;
                        if (StringUtils.isNotBlank(hour)) {
                            finalDuration = Integer.parseInt(hour) * 3600;
                        }
                        if (StringUtils.isNotBlank(min)) {
                            finalDuration += Integer.parseInt(min) * 60;
                        }
                        if (StringUtils.isNotBlank(sec)) {
                            finalDuration += +Integer.parseInt(sec);
                        }
                        ccdi.setDuration(finalDuration + "");
                        ccdi.setFee((int) (Double.parseDouble(rows.get(i).getCell(6).asText()) * 100));
                        ccdi.setTime(rows.get(i).getCell(1).asText());
                        ccdi.setLocation(rows.get(i).getCell(4).asText());
                        ccdi.setLocationType(rows.get(i).getCell(4).asText());
                        ccdi.setPeerNumber(rows.get(i).getCell(5).asText());
                        ccdi.setMappingId(ci.getMappingId());
                        ci.getCalls().add(ccdi);
                    }
                }
            }


            logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
            List<Page> smsPages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
            if(smsPages!=null){
                for (Page callPage : smsPages) {
                    HtmlTable table = (HtmlTable) PageUtils.getElementByXpath(callPage, "//table[@border='0']").get(1);
                    List<HtmlTableRow> rows = (List) PageUtils.getElementByXpath(table, "//tbody/tr");
                    for (int i = 3; i <= rows.size() - 1; i++) {
                        if (rows.get(i).asXml().contains("暂无您所查询的数据清单")) {
                            continue;
                        }
                        CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
                        csri.setBillMonth(rows.get(i).getCell(2).asText().substring(0, 7));
                        csri.setFee((int) (Double.parseDouble(rows.get(i).getCell(6).asText()) * 100));
                        csri.setLocation("");
                        csri.setMappingId(ci.getMappingId());
                        csri.setMsgType("SMS");
                        csri.setPeerNumber(rows.get(i).getCell(4).asText());
                        csri.setSendType(rows.get(i).getCell(5).asText().contains("发送") ? "SEND" : "RECEIVE");
                        csri.setServiceName("");
                        csri.setTime(rows.get(i).getCell(2).asText());
                        ci.getSmses().add(csri);
                    }
                }
            }

            logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
            List<Page> pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
            if(pages!=null){
                for (Page p : pages) {
                    HtmlPage htmlPage = (HtmlPage) p;
                    HtmlTable table = (HtmlTable) PageUtils.getElementByXpath(htmlPage, "//table[@border='0']").get(1);
                    List<HtmlTableRow> rows = (List) PageUtils.getElementByXpath(table, "//tbody/tr");
                    for (int i = 5; i <= rows.size() - 1; i++) {
                        if (rows.get(i).asXml().contains("暂无您所查询的数据清单")) {
                            continue;
                        }
                        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
                        cndi.setMappingId(ci.getMappingId());
                        cndi.setBillMonth(rows.get(i).getCell(1).asText().substring(0, 7));
                        cndi.setServiceName(rows.get(i).getCell(6).asText());
                        cndi.setLocation("");
                        cndi.setTime(rows.get(i).getCell(1).asText());
                        cndi.setNetType(rows.get(i).getCell(4).asText());
                        String duration = rows.get(i).getCell(2).asText();
                        String hour = RegexUtils.matchValue("(\\d+)时", duration);
                        String min = RegexUtils.matchValue("(\\d+)分", duration);
                        String sec = RegexUtils.matchValue("(\\d+)秒", duration);
                        int finalDuration = 0;
                        if (StringUtils.isNotBlank(hour)) {
                            finalDuration = Integer.parseInt(hour) * 3600;
                        }
                        if (StringUtils.isNotBlank(min)) {
                            finalDuration = finalDuration + Integer.parseInt(min) * 60;
                        }
                        if (StringUtils.isNotBlank(sec)) {
                            finalDuration = finalDuration + Integer.parseInt(sec);
                        }
                        cndi.setDuration(finalDuration);

                        String subflow = rows.get(i).getCell(3).asText();
                        String gb = RegexUtils.matchValue("(\\d+)GB", subflow);
                        String mb = RegexUtils.matchValue("(\\d+)MB", subflow);
                        String kb = RegexUtils.matchValue("(\\d+)KB", subflow);
                        int finalSubflow = 0;
                        if (StringUtils.isNotBlank(gb)) {
                            finalSubflow = Integer.parseInt(gb) * 1048576;
                        }
                        if (StringUtils.isNotBlank(mb)) {
                            finalSubflow = finalSubflow + Integer.parseInt(mb) * 1024;
                        }
                        if (StringUtils.isNotBlank(kb)) {
                            finalSubflow = finalSubflow + Integer.parseInt(kb);
                        }
                        cndi.setSubflow(finalSubflow);
                        cndi.setFee((int) (Double.parseDouble(rows.get(i).getCell(7).asText())));
                        ci.getNets().add(cndi);
                    }
                }
            }


            logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
            for (Page p : pages) {
                if (null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())) {
                    String pageContent = p.getWebResponse().getContentAsString();
                    if (pageContent.contains("内存查询不到记录")) {
                        continue;
                    }
                    CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
                    String baseFee = RegexUtils.matchValue("<span>\\s+(.*?)\\s+</span>\\s+</span>\\s+<span class=\"leftk\">&nbsp;&nbsp;&nbsp;&nbsp;套餐月基本费</span>", pageContent);
                    String extraFee = RegexUtils.matchValue("<span>\\s+(.*?)\\s+</span>\\s+</span>\\s+<span class=\"leftk\">&nbsp;&nbsp;&nbsp;&nbsp;其它费</span>", pageContent);
                    String totalFee = RegexUtils.matchValue("<li><span class=\"fw_b\">本期费用合计：(.*?)元</span></li>", pageContent);
                    String paidFee = RegexUtils.matchValue("<li><span class=\"fw_b\">本期已付费用：(.*?)元</span>", pageContent);
                    String actualFee = RegexUtils.matchValue("<span class=\"fw_b\">本期应付费用：<span class=\"c14\">(.*?)元</span>", pageContent);
                    String point = RegexUtils.matchValue("<th>本期可用积分</th>\\s+<th class=\"align_c\" width=\"5%\">=(.*?)</th>", pageContent);
                    String lastPoint = RegexUtils.matchValue("<th>上期末可用积分</th>\\s+<th class=\"align_c\" width=\"5%\">-(.*?)</th>", pageContent);
                    String dateRange = RegexUtils.matchValue("账单周期：</span>(.*?)</li>", pageContent);
                    String[] dates = dateRange.split("‐");
                    String billStartDate = DateUtils.dateToString(DateUtils.stringToDate(dates[0], "yyyy/MM/dd"), "yyyy-MM-dd");
                    String billEndDate = DateUtils.dateToString(DateUtils.stringToDate(dates[1], "yyyy/MM/dd"), "yyyy-MM-dd");
                    if (NumberUtils.isNumber(actualFee)) {
                        cbdi.setActualFee((int) (Double.parseDouble(actualFee) * 100));
                    }
                    if (NumberUtils.isNumber(baseFee)) {
                        cbdi.setBaseFee((int) (Double.parseDouble(baseFee) * 100));
                    }
                    if (NumberUtils.isNumber(extraFee)) {
                        cbdi.setExtraFee((int) (Double.parseDouble(extraFee) * 100));
                    }
                    if (NumberUtils.isNumber(totalFee)) {
                        cbdi.setTotalFee((int) (Double.parseDouble(totalFee) * 100));
                    }
                    if (NumberUtils.isNumber(point)) {
                        cbdi.setPoint((Integer.parseInt(point)));
                    }
                    if (NumberUtils.isNumber(lastPoint)) {
                        cbdi.setLastPoint((Integer.parseInt(lastPoint)));
                    }
                    if (NumberUtils.isNumber(paidFee)) {
                        cbdi.setPaidFee((int) (Double.parseDouble(paidFee) * 100));
                    }
                    cbdi.setBillMonth(billStartDate.substring(0, 7));
                    cbdi.setBillStartDate(billStartDate);
                    cbdi.setBillEndDate(billEndDate);
                    cbdi.setMappingId(ci.getMappingId());
                    ci.getBills().add(cbdi);
                }
            }

            logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
            if (null != pages && pages.size() > 0) {
                Calendar calendar = Calendar.getInstance();
                for (Page packagePage : pages) {
                    String content = packagePage.getWebResponse().getContentAsString();
                    if (content.contains("请稍候再试")) {
                        continue;
                    }
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                    String begin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                    jsonObject = JSONObject.parseObject(content);
                    Map<String, Object> dataMap = jsonObject.getJSONObject("DATAMAP");
                    Collection<Object> objects = dataMap.values();
                    for (Object o : objects) {
                        JSONArray array = (JSONArray) o;
                        for (Object item : array) {
                            CarrierPackageItemInfo cpii = new CarrierPackageItemInfo();
                            JSONObject group = (JSONObject) item;
                            cpii.setTotal(group.getString("LIMIT_VALUE"));
                            cpii.setItem(group.getString("GROUP_NAME"));
                            cpii.setUnit(group.getString("UNIT_NAME"));
                            cpii.setUsed(group.getString("CURR_VALUE"));
                            cpii.setBillStartDate(begin);
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            String end = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                            cpii.setBillEndDate(end);
                            cpii.setMappingId(ci.getMappingId());
                            ci.getPackages().add(cpii);
                        }
                    }
                    calendar.add(Calendar.MONTH, -1);
                }
            }

            logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
            page = cc.getPage(ProcessorCode.RECHARGE_INFO.getCode());
            HtmlPage htmlPage = (HtmlPage) page;
            List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@class='listtable']/tbody/tr");
            for (int i = 1; i < trs.size(); i++) {
                CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
                curi.setBillMonth(trs.get(i).getCell(2).asText().trim().substring(0, 7));
                curi.setMappingId(ci.getMappingId());
                curi.setRechargeTime(trs.get(i).getCell(2).asText().trim());
                curi.setAmount((int) (Double.parseDouble(trs.get(i).getCell(4).asText()) * 100));
                curi.setType(trs.get(i).getCell(1).asText());
                ci.getRecharges().add(curi);
            }

            result.setData(ci);
            result.setResult(StatusCode.解析成功);
        } catch (Exception e) {
            logger.info("==>[{}]解析出错了", context.getTaskId(), e);
            result.setResult(StatusCode.数据解析中发生错误);
        }
        return result;
    }
}

