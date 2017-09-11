package com.xinyan.spider.isp.mobile.parser.telecom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.PageUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 甘肃电信解析类
 *
 * @author jiangmengchen
 * @version V1.0
 * @description
 * @date 2017年6月3日 下午3:38:43
 */
@Component
public class GanSuTelecomParser {

    protected static Logger logger = LoggerFactory.getLogger(GanSuTelecomParser.class);

    public Result parse(Context context, CarrierInfo ci, CacheContainer cc) {
        Result result = new Result();
        try {
            logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
            String pageInfo = "";
            JSONObject jsonObject = null;
            ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
            ci.getCarrierUserInfo().setProvince("甘肃");//所属省份
            ci.getCarrierUserInfo().setName(context.getIdName());
            ci.getCarrierUserInfo().setIdCard(context.getIdCard());
            ci.getCarrierUserInfo().setCity(context.getMobileHCodeDto().getCityName());
            ci.getCarrierUserInfo().setState(-1);
            //个人信息
            Page page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                HtmlTableRow htmlTableRow = (HtmlTableRow) PageUtils.getElementByXpath(page, "//table[@class='cx_result_table ']/tbody/tr").get(1);
                String packageName = htmlTableRow.getCell(0).asText();
                ci.getCarrierUserInfo().setPackageName(packageName);
                ci.getCarrierUserInfo().setMobile(context.getUserName());
            }
            //余额
            page = cc.getPage(ProcessorCode.AMOUNT.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                jsonObject = JSONObject.parseObject(pageInfo);
                String money = jsonObject.getString("accountMoney");
                ci.getCarrierUserInfo().setAvailableBalance((int) (Double.parseDouble(money) * 100));
                ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            }
            //星级
            page = cc.getPage(ProcessorCode.VIP_LVL.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                String level = RegexUtils.matchValue("\"custviplevel\":(.*?),\"", pageInfo);
                if(level.equals("1")){
                    ci.getCarrierUserInfo().setLevel("一星");
                }else if(level.equals("2")){
                    ci.getCarrierUserInfo().setLevel("二星");
                }else if(level.equals("3")){
                    ci.getCarrierUserInfo().setLevel("三星");
                }else if(level.equals("4")){
                    ci.getCarrierUserInfo().setLevel("四星");
                }else if(level.equals("5")){
                    ci.getCarrierUserInfo().setLevel("五星");
                }else{
                    ci.getCarrierUserInfo().setLevel("普通用户");
                }
                //opentime
                String opentime = RegexUtils.matchValue("\"effdate\":\"(.*?)\",", pageInfo);
                ci.getCarrierUserInfo().setOpenTime(opentime);
            }

            logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
            List<Page> calls = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
            if(calls!=null){
                for (Page callPage : calls) {
                    jsonObject = JSONObject.parseObject(callPage.getWebResponse().getContentAsString());
                    String content = jsonObject.getString("jsonResult");
                    if (StringUtils.isBlank(content)) {
                        continue;
                    }
                    jsonObject = JSONObject.parseObject(content);
                    JSONArray jsonArray = jsonObject.getJSONArray("trList");
                    for (Object o : jsonArray) {
                        JSONObject object = (JSONObject) o;
                        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
                        ccdi.setBillMonth(object.getString("val0").substring(0, 7));
                        ccdi.setDialType(object.getString("val1").contains("主叫") ? "DIAL" : "DIALED");
                        String[] times = RegexUtils.matchMutiValue("(\\d+):(\\d+):(\\d+)", object.getString("val3"));
                        int total = Integer.parseInt(times[0]) * 3600 + Integer.parseInt((times[1])) * 60 + Integer.parseInt((times[2]));
                        ccdi.setDuration(total + "");
                        ccdi.setFee((int) (Double.parseDouble(object.getString("val4")) * 100));
                        ccdi.setTime(object.getString("val0"));
                        ccdi.setLocation(object.getString("val5"));
                        ccdi.setLocationType(object.getString("val6"));
                        ccdi.setPeerNumber(object.getString("val2"));
                        ccdi.setMappingId(ci.getMappingId());
                        ci.getCalls().add(ccdi);
                    }
                }
            }

            logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
            List<Page> smsPages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
            if(smsPages!=null){
                for (Page callPage : smsPages) {
                    jsonObject = JSONObject.parseObject(callPage.getWebResponse().getContentAsString());
                    String content = jsonObject.getString("jsonResult");
                    if (StringUtils.isBlank(content)) {
                        continue;
                    }
                    jsonObject = JSONObject.parseObject(content);
                    JSONArray jsonArray = jsonObject.getJSONArray("trList");
                    for (Object o : jsonArray) {
                        JSONObject object = (JSONObject) o;
                        CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
                        csri.setBillMonth(object.getString("val0").substring(0, 7));
                        csri.setFee((int) (Double.parseDouble(object.getString("val3")) * 100));
                        csri.setLocation("");
                        csri.setMappingId(ci.getMappingId());
                        csri.setMsgType("SMS");
                        csri.setPeerNumber(object.getString("val2"));
                        csri.setSendType("");
                        csri.setServiceName(object.getString("val1"));
                        csri.setTime(object.getString("val0"));
                        ci.getSmses().add(csri);
                    }
                }
            }

            logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
            List<Page> pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
            if(pages!=null){
                for (Page p : pages) {
                    jsonObject = JSONObject.parseObject(p.getWebResponse().getContentAsString());
                    String content = jsonObject.getString("jsonResult");
                    if (StringUtils.isBlank(content)) {
                        continue;
                    }
                    jsonObject = JSONObject.parseObject(content);
                    JSONArray jsonArray = jsonObject.getJSONArray("trList");
                    for (Object o : jsonArray) {
                        JSONObject object = (JSONObject) o;
                        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
                        cndi.setMappingId(ci.getMappingId());
                        cndi.setBillMonth(object.getString("val0").substring(0, 7));
                        cndi.setServiceName(object.getString("val7"));
                        cndi.setLocation(object.getString("val6"));
                        cndi.setTime(object.getString("val0"));
                        cndi.setNetType(object.getString("val5"));
                        String duration = object.getString("val2");
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

                        String subflow = object.getString("val3");
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
                        cndi.setFee((int) (Double.parseDouble(object.getString("val4")) * 100));
                        ci.getNets().add(cndi);
                    }
                }
            }

            logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
            for (Page p : pages) {
                if (null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())) {
                    String pageContent = p.getWebResponse().getContentAsString();
                    if (pageContent.contains("fail")) {
                        continue;
                    }
//                    CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
//                    String baseFee = RegexUtils.matchValue("<span>\\s+(.*?)\\s+</span>\\s+</span>\\s+<span class=\"leftk\">&nbsp;&nbsp;&nbsp;&nbsp;套餐月基本费</span>", pageContent);
//                    String extraFee = RegexUtils.matchValue("<span>\\s+(.*?)\\s+</span>\\s+</span>\\s+<span class=\"leftk\">&nbsp;&nbsp;&nbsp;&nbsp;其它费</span>", pageContent);
//                    String totalFee = RegexUtils.matchValue("<li><span class=\"fw_b\">本期费用合计：(.*?)元</span></li>", pageContent);
//                    String paidFee = RegexUtils.matchValue("<li><span class=\"fw_b\">本期已付费用：(.*?)元</span>", pageContent);
//                    String actualFee = RegexUtils.matchValue("<span class=\"fw_b\">本期应付费用：<span class=\"c14\">(.*?)元</span>", pageContent);
//                    String point = RegexUtils.matchValue("<th>本期可用积分</th>\\s+<th class=\"align_c\" width=\"5%\">=(.*?)</th>", pageContent);
//                    String lastPoint = RegexUtils.matchValue("<th>上期末可用积分</th>\\s+<th class=\"align_c\" width=\"5%\">-(.*?)</th>", pageContent);
//                    String dateRange = RegexUtils.matchValue("账单周期：</span>(.*?)</li>", pageContent);
//                    String[] dates = dateRange.split("‐");
//                    String billStartDate = DateUtils.dateToString(DateUtils.stringToDate(dates[0], "yyyy/MM/dd"), "yyyy-MM-dd");
//                    String billEndDate = DateUtils.dateToString(DateUtils.stringToDate(dates[1], "yyyy/MM/dd"), "yyyy-MM-dd");
//                    if (NumberUtils.isNumber(actualFee)) {
//                        cbdi.setActualFee((int) (Double.parseDouble(actualFee) * 100));
//                    }
//                    if (NumberUtils.isNumber(baseFee)) {
//                        cbdi.setBaseFee((int) (Double.parseDouble(baseFee) * 100));
//                    }
//                    if (NumberUtils.isNumber(extraFee)) {
//                        cbdi.setExtraFee((int) (Double.parseDouble(extraFee) * 100));
//                    }
//                    if (NumberUtils.isNumber(totalFee)) {
//                        cbdi.setTotalFee((int) (Double.parseDouble(totalFee) * 100));
//                    }
//                    if (NumberUtils.isNumber(point)) {
//                        cbdi.setPoint((Integer.parseInt(point)));
//                    }
//                    if (NumberUtils.isNumber(lastPoint)) {
//                        cbdi.setLastPoint((Integer.parseInt(lastPoint)));
//                    }
//                    if (NumberUtils.isNumber(paidFee)) {
//                        cbdi.setPaidFee((int) (Double.parseDouble(paidFee) * 100));
//                    }
//                    cbdi.setBillMonth(billStartDate.substring(0, 7));
//                    cbdi.setBillStartDate(billStartDate);
//                    cbdi.setBillEndDate(billEndDate);
//                    cbdi.setMappingId(ci.getMappingId());
//                    ci.getBills().add(cbdi);
                }
            }

            logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
            if (null != pages && pages.size() > 0) {
                for (Page packagePage : pages) {
                    if (packagePage.getWebResponse().getContentAsString().contains("没有找到相应的剩余套餐信息")) {
                        continue;
                    }
                    List<HtmlTableRow> rows = (List) PageUtils.getElementByXpath(packagePage, "//div[@class='tc_used_detail']/div[@class='detail_list']/div[@class='detail_cont']/table/tbody/tr");
                    for (HtmlTableRow row : rows) {
                        CarrierPackageItemInfo cpii = new CarrierPackageItemInfo();
                        double total = Double.parseDouble(row.getCell(2).asText().replace("M", "").replace("分钟", ""))
                                + Double.parseDouble(row.getCell(3).asText().replace("M", "").replace("分钟", ""));
                        cpii.setTotal(total * 1000 + "");
                        cpii.setItem(row.getCell(0).asText());
                        cpii.setUnit(row.getCell(0).asText().contains("流量") ? "KB" : "分钟");
                        cpii.setUsed(row.getCell(2).asText().replace("M", "").replace("分钟", ""));
                        cpii.setBillStartDate(row.getCell(5).asText());
                        cpii.setBillEndDate(row.getCell(6).asText());
                        cpii.setMappingId(ci.getMappingId());
                        ci.getPackages().add(cpii);
                    }
                }
            }

            logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
            page = cc.getPage(ProcessorCode.RECHARGE_INFO.getCode());
            HtmlPage htmlPage = (HtmlPage) page;
            if(page!=null){
                List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//div[@id='hiddenresult']/table/tbody/tr");
                for (int i = 0; i < trs.size(); i++) {
                    CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
                    curi.setBillMonth(trs.get(i).getCell(1).asText().trim().substring(0, 7));
                    curi.setMappingId(ci.getMappingId());
                    curi.setRechargeTime(trs.get(i).getCell(1).asText().trim());
                    curi.setAmount((int) (Double.parseDouble(trs.get(i).getCell(2).asText()) * 100));
                    curi.setType(trs.get(i).getCell(3).asText());
                    ci.getRecharges().add(curi);
                }
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

