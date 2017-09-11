package com.xinyan.spider.isp.mobile.parser.cmcc;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.*;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * @Description:福建数据解析类
 * @author: jiangmengchen
 * @date: 2017-05-11 15:54
 * @version: v1.0
 */
@Component
public class FuJianCmccParser {

    protected static Logger logger = LoggerFactory.getLogger(FuJianCmccParser.class);

    /**
     * 解析基本信息
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-11 16:43
     */
    public Result basicInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        HtmlPage page = (HtmlPage) cacheContainer.getPage(ProcessorCode.BASIC_INFO.getCode());
        CarrierUserInfo carrierUserInfo = new CarrierUserInfo();
        if(page!=null){
            carrierUserInfo.setCarrier("中国移动");
            carrierUserInfo.setIdCard(RegexUtils.matchValue("证件号码：</span>(.*?)</li>", page.getWebResponse().getContentAsString()));
            carrierUserInfo.setProvince("福建");
            carrierUserInfo.setOpenTime("");
            carrierUserInfo.setName(RegexUtils.matchValue("姓名：</span>(.*?)</li>", page.getWebResponse().getContentAsString()));
            carrierUserInfo.setMobile(context.getUserName());
            carrierUserInfo.setAddress(RegexUtils.matchValue("</span>通信地址：</span>(.*?)</li>", page.getWebResponse().getContentAsString()));
            String modifyTime = RegexUtils.matchValue("<li><span class=\"name1 fwb\">修改时间：</span>(.*?)</li>", page.getWebResponse().getContentAsString());
            modifyTime = modifyTime.replace("年", "-").replace("月", "-").replace("日", " ").replace("时", ":").replace("分", ":").replace("秒", "");
            String openTime = RegexUtils.matchValue("<li><span class=\"name1 fwb\">入网时间：</span>(.*?)</li>", page.getWebResponse().getContentAsString());
            openTime = openTime.replace("年", "-").replace("月", "-").replace("日", " ").replace("时", ":").replace("分", ":").replace("秒", "");
            carrierUserInfo.setLastModifyTime(modifyTime);
            carrierUserInfo.setOpenTime(openTime);
            carrierUserInfo.setCity(((HtmlOption) page.getFirstByXPath("//select[@id='home_city']/option[1]")).asText());
            carrierUserInfo.setLevel(((HtmlFont) page.getFirstByXPath("//ul[@class='password_serve']/li[16]/font")).asText());
            String userStatus = RegexUtils.matchValue("<li><span class=\"name1 fwb\">用户状态：</span>(.*?)</li>", page.getWebResponse().getContentAsString());
            if (Constants.NOT_EXIST.equals(userStatus)) {
                carrierUserInfo.setState(99);
            } else if (Constants.NORMAL.contains(userStatus)) {
                carrierUserInfo.setState(0);
            } else if (Constants.UNIDIRECTIONAL_STOP.equals(userStatus)) {
                carrierUserInfo.setState(1);
            } else if (Constants.STOP.equals(userStatus)) {
                carrierUserInfo.setState(2);
            } else if (Constants.PRE_CANCELLATION.equals(userStatus)) {
                carrierUserInfo.setState(3);
            } else if (Constants.CANCELLATION.equals(userStatus)) {
                carrierUserInfo.setState(4);
            } else if (Constants.TRANSFER.equals(userStatus)) {
                carrierUserInfo.setState(5);
            } else if (Constants.CHANGE.equals(userStatus)) {
                carrierUserInfo.setState(6);
            } else {
                carrierUserInfo.setState(-1);
            }
        }

        carrierUserInfo.setMappingId(carrierInfo.getMappingId());
        carrierInfo.setCarrierUserInfo(carrierUserInfo);
        result.setSuccess();
        return result;
    }

    /**
     * 解析通话记录
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-11 16:43
     */
    public Result callRecordParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
        String year = DateUtils.getCurrentYear();
        if(pages!=null){
            for (Page page : pages) {
                String content = page.getWebResponse().getContentAsString();
                List<List<String>> calls = RegexUtils.matchesMutiValue("\\s+<tr>\\s+<td\\s+width=\"100\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"70\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"70\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"80\" style=\"word-wrap:break-word;word-break:break-all;\"\\s+>&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"90\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"60\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"120\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+style=\"border-right:0;\">&nbsp;(.*?)<br></td>\\s+", content);
                for (List<String> call : calls) {
                    CarrierCallDetailInfo carrierCallDetailInfo = new CarrierCallDetailInfo();
                    carrierCallDetailInfo.setMappingId(carrierInfo.getMappingId());
                    carrierCallDetailInfo.setBillMonth(year + "-" + call.get(0).substring(0, 2));
                    carrierCallDetailInfo.setDialType(call.get(5).contains("主叫") ? "DIAL" : "DIALED");
                    carrierCallDetailInfo.setDuration(call.get(4));
                    carrierCallDetailInfo.setPeerNumber(call.get(3));
                    carrierCallDetailInfo.setFee((int) (Double.parseDouble(call.get(7)) * 100));
                    carrierCallDetailInfo.setLocation(call.get(1));
                    carrierCallDetailInfo.setLocationType(call.get(2));
                    carrierCallDetailInfo.setTime(year + "-" + call.get(0));
                    carrierInfo.getCalls().add(carrierCallDetailInfo);
                }
            }
        }

        result.setSuccess();
        return result;
    }

    /**
     * 解析短信记录
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @param context
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-11 16:43
     */
    public Result smsInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.SMS_INFO.getCode());
        String year = DateUtils.getCurrentYear();
        if(pages!=null){
            for (Page page : pages) {
                String content = page.getWebResponse().getContentAsString();
                List<List<String>> smss = RegexUtils.matchesMutiValue("\\s+<tr>\\s+<td\\s+width=\"20%\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"10%\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"12%\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"12%\" style=\"word-wrap:break-word;word-break:break-all;\"\\s+>&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"10%\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"12%\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"12%\">&nbsp;(.*?)<br></td>" +
                        "\\s+<td\\s+width=\"12%\" style=\"border-right:0;\">&nbsp;(.*?)<br></td>\\s+", content);
                for (List<String> call : smss) {
                    CarrierSmsRecordInfo carrierSmsRecordInfo = new CarrierSmsRecordInfo();
                    carrierSmsRecordInfo.setMappingId(carrierInfo.getMappingId());
                    carrierSmsRecordInfo.setBillMonth(year + "-" + call.get(0).substring(0, 2));
                    carrierSmsRecordInfo.setTime(year + "-" + call.get(0));
                    carrierSmsRecordInfo.setLocation(call.get(1));
                    carrierSmsRecordInfo.setPeerNumber(call.get(3));
                    carrierSmsRecordInfo.setMsgType("SMS");
                    carrierSmsRecordInfo.setSendType(call.get(2).contains("发送") ? "SEND" : "RECEIVE");
                    carrierSmsRecordInfo.setFee((int) (Double.parseDouble(call.get(7)) * 100));
                    carrierSmsRecordInfo.setServiceName(call.get(5));
                    carrierInfo.getSmses().add(carrierSmsRecordInfo);
                }
            }
        }
        result.setSuccess();
        return result;
    }

    /**
     * 解析上网记录
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-11 16:43
     */
    public Result netInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.NET_INFO.getCode());
        String year = DateUtils.getCurrentYear();
        if(pages!=null){
            for (Page page : pages) {
                HtmlPage htmlPage = (HtmlPage) page;
                List<HtmlTableRow> tables = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@id='page231']/tbody/tr");
                for (HtmlTableRow table : tables) {
                    if (StringUtils.isBlank(table.asText())) {
                        continue;
                    }
                    CarrierNetDetailInfo carrierNetDetailInfo = new CarrierNetDetailInfo();
                    carrierNetDetailInfo.setMappingId(carrierInfo.getMappingId());
                    carrierNetDetailInfo.setBillMonth(year + "-" + table.getCell(0).asText().substring(0, 2));
                    carrierNetDetailInfo.setTime(year + "-" + table.getCell(0).asText());
                    carrierNetDetailInfo.setLocation(table.getCell(1).asText());
                    carrierNetDetailInfo.setFee((int) (Double.parseDouble(table.getCell(6).asText()) * 100));
                    carrierNetDetailInfo.setDuration(Integer.parseInt(table.getCell(3).asText().trim()));
                    carrierNetDetailInfo.setNetType(table.getCell(2).asText());
                    carrierNetDetailInfo.setServiceName(table.getCell(5).asText());
                    String gb = RegexUtils.matchValue("(\\d+).(\\d+)\\(G\\)", table.getCell(4).asText());
                    String mb = RegexUtils.matchValue("(\\d+).(\\d+)\\(M\\)", table.getCell(4).asText());
                    String kb = RegexUtils.matchValue("(\\d+).(\\d+)\\(K\\)", table.getCell(4).asText());
                    int finalSubflow = 0;
                    if (StringUtils.isNotBlank(gb)) {
                        finalSubflow = Integer.parseInt(gb) * 1048576;
                    }
                    if (StringUtils.isNotBlank(mb)) {
                        finalSubflow = finalSubflow + Integer.parseInt(mb) * 1024;
                    }
                    if (StringUtils.isNotBlank(kb)) {
                        finalSubflow = finalSubflow + (int) (Math.floor(Double.parseDouble(kb)));
                    }
                    carrierNetDetailInfo.setSubflow(finalSubflow);
                    carrierInfo.getNets().add(carrierNetDetailInfo);
                }
            }
        }
        result.setSuccess();
        return result;
    }

    /**
     * 解析账单信息
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-11 16:43
     */
    public Result billParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        HtmlPage page = (HtmlPage) cacheContainer.getPage(ProcessorCode.BILL_INFO.getCode());
        if(page!=null){
            List<HtmlDivision> dates = (List<HtmlDivision>) page.getByXPath("//*/div[@class='expense fl']");
            List<HtmlDivision> divisions = (List<HtmlDivision>) page.getByXPath("//div[contains(@id,'billcontent')]/div");
            for (int i = 0; i <= divisions.size() - 1; i++) {
                CarrierBillDetailInfo carrierBillDetailInfo = new CarrierBillDetailInfo();
                carrierBillDetailInfo.setMappingId(carrierInfo.getMappingId());
                carrierBillDetailInfo.setBaseFee((int) (Double.parseDouble(RegexUtils.matchValue("<span class=\"color-1 fr\">\\s+(.*?)元\\s+</span>\\s+固定费用\\s+</p>", divisions.get(i).asXml())) * 100));
                carrierBillDetailInfo.setVoiceFee((int) (Double.parseDouble(RegexUtils.matchValue("<span class=\"color-1 fr\">\\s+(.*?)元\\s+</span>\\s+语音通信费\\s+</p>", divisions.get(i).asXml())) * 100));
                carrierBillDetailInfo.setWebFee((int) (Double.parseDouble(RegexUtils.matchValue("<span class=\"color-1 fr\">\\s+(.*?)元\\s+</span>\\s+上网费\\s+</p>", divisions.get(i).asXml())) * 100));
                carrierBillDetailInfo.setSmsFee((int) (Double.parseDouble(RegexUtils.matchValue("<span class=\"color-1 fr\">\\s+(.*?)元\\s+</span>\\s+短彩信\\s+</p>", divisions.get(i).asXml())) * 100));
                carrierBillDetailInfo.setExtraServiceFee((int) (Double.parseDouble(RegexUtils.matchValue("<span class=\"color-1 fr\">\\s+(.*?)元\\s+</span>\\s+增值业务费\\s+</p>", divisions.get(i).asXml())) * 100));
                carrierBillDetailInfo.setExtraFee((int) (Double.parseDouble(RegexUtils.matchValue("<span class=\"color-1 fr\">\\s+(.*?)元\\s+</span>\\s+其他费用\\s+</p>", divisions.get(i).asXml())) * 100));
                String totalFee = RegexUtils.matchValue("<p class=\"mgt-20\">\\s+本月消费：\\s+<span class=\"color-7\" id=\"consume\">\\s+(.*?)\\s+</span>", page.asXml());
                carrierBillDetailInfo.setTotalFee((int) (Double.parseDouble(totalFee) * 100));
                carrierBillDetailInfo.setActualFee((int) (Double.parseDouble(totalFee) * 100));
                String dateRange = RegexUtils.matchValue("<p class=\"mgt-10\" id=\"accountscycle\">\\s+记账周期：(.*)\\s+</p>", dates.get(i).asXml());
                String startDate = DateUtils.dateToString(DateUtils.stringToDate(dateRange.split("~")[0], "yyyy/MM/dd"), "yyyy-MM-dd");
                String endDate = DateUtils.dateToString(DateUtils.stringToDate(dateRange.split("~")[1], "yyyy/MM/dd"), "yyyy-MM-dd");
                carrierBillDetailInfo.setBillStartDate(startDate);
                carrierBillDetailInfo.setBillEndDate(endDate);
                carrierBillDetailInfo.setBillMonth(startDate.substring(0, 7));
                carrierInfo.getBills().add(carrierBillDetailInfo);
            }
            int balance = (int) (Double.parseDouble(RegexUtils.matchValue("账户余额：\\s+<span class=\"color-7\" id=\"Balance\">\\s+(.*?)\\s+</span>", dates.get(0).asXml())) * 100);
            carrierInfo.getCarrierUserInfo().setAvailableBalance(balance);
        }

        result.setSuccess();
        return result;
    }

    /**
     * 解析办理业务信息
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-11 16:43
     */
    public Result packageItemInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        HtmlPage page = (HtmlPage) cacheContainer.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
        if(page!=null){
            List<HtmlTableRow> table = (List<HtmlTableRow>) page.getByXPath("//table[@class='table_box']/tbody/tr");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            for (int i = 1; i <= table.size() - 1; i++) {
                if (table.get(i).getChildElementCount() <= 1) {
                    continue;
                }
                String rowspan = table.get(i).getCell(0).getAttribute("rowspan");
                CarrierPackageItemInfo carrierPackageItemInfo = new CarrierPackageItemInfo();
                carrierPackageItemInfo.setMappingId(carrierInfo.getMappingId());
                carrierPackageItemInfo.setBillStartDate(transDateBegin);
                carrierPackageItemInfo.setBillEndDate(transDateEnd);
                if (StringUtils.isNotBlank(rowspan)) {
                    carrierPackageItemInfo.setUnit(table.get(i).getCell(0).asText().contains("流量") ? "MB" : "分钟");
                    carrierPackageItemInfo.setItem(table.get(i).getCell(2).asText());
                    if (i == 1) {
                        carrierInfo.getCarrierUserInfo().setPackageName(table.get(i).getCell(2).asText());
                    }
                    carrierPackageItemInfo.setTotal(table.get(i).getCell(3).asText());
                    carrierPackageItemInfo.setUsed(table.get(i).getCell(4).asText());
                } else {
                    carrierPackageItemInfo.setUnit(table.get(i).getCell(1).asText().contains("流量") ? "MB" : "分钟");
                    carrierPackageItemInfo.setItem(table.get(i).getCell(1).asText());
                    if (i == 1) {
                        carrierInfo.getCarrierUserInfo().setPackageName(table.get(i).getCell(2).asText());
                    }
                    carrierPackageItemInfo.setTotal(table.get(i).getCell(2).asText());
                    carrierPackageItemInfo.setUsed(table.get(i).getCell(4).asText());
                }
                carrierInfo.getPackages().add(carrierPackageItemInfo);
            }
        }
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
     * @author jiangmengchen
     * @create 2017-05-08 16:43
     */
    public Result userRechargeItemInfoParse(Context context, CacheContainer
            cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        Page page = cacheContainer.getPage(ProcessorCode.RECHARGE_INFO.getCode());
        if(page!=null){
            HtmlPage htmlPage = (HtmlPage) page;
            List<HtmlTableRow> rows = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@class='table_box']/tbody/tr");
            for (int i = 1; i <= rows.size() - 1; i++) {
                CarrierUserRechargeItemInfo carrierUserRechargeItemInfo = new CarrierUserRechargeItemInfo();
                carrierUserRechargeItemInfo.setMappingId(carrierInfo.getMappingId());
                carrierUserRechargeItemInfo.setRechargeTime(rows.get(i).getCell(0).asText());
                carrierUserRechargeItemInfo.setAmount((int) (Double.parseDouble(rows.get(i).getCell(2).asText()) * 100));
                carrierUserRechargeItemInfo.setType(rows.get(i).getCell(1).asText());
                carrierUserRechargeItemInfo.setBillMonth(rows.get(i).getCell(0).asText().substring(0, 7));
                carrierInfo.getRecharges().add(carrierUserRechargeItemInfo);
            }
        }
        result.setSuccess();
        return result;
    }
}
