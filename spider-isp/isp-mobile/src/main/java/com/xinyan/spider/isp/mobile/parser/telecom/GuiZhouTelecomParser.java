package com.xinyan.spider.isp.mobile.parser.telecom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.PageUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 黑龙江电信解析类
 *
 * @author jiangmengchen
 * @version V1.0
 * @description
 * @date 2017年6月3日 下午3:38:43
 */
@Component
public class GuiZhouTelecomParser {

    protected static Logger logger = LoggerFactory.getLogger(GuiZhouTelecomParser.class);

    public Result parse(Context context, CarrierInfo ci, CacheContainer cc) {
        Result result = new Result();
        try {
            logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
            String pageInfo = "";
            ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
            ci.getCarrierUserInfo().setProvince("贵州");//所属省份
            ci.getCarrierUserInfo().setName(context.getIdName());
            ci.getCarrierUserInfo().setIdCard(context.getIdCard());
            ci.getCarrierUserInfo().setCity(context.getMobileHCodeDto().getCityName());
            ci.getCarrierUserInfo().setState(-1);
            ci.getCarrierUserInfo().setMobile(context.getUserName());
            ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            //余额
            Page page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                JSONObject balanceInfo = JSONObject.parseObject(pageInfo);
                ci.getCarrierUserInfo().setAvailableBalance((int) (Double.parseDouble(balanceInfo.getString("ResultBalance")) * 100));
            }
            //套餐
            page = cc.getPage(ProcessorCode.AMOUNT.getCode());
            if(page!=null){
                List<HtmlElement> elements = PageUtils.getElementByXpath(page, "//*/ul[@class='jb']/li[1]");
                if (elements.size() > 0) {
                    String packageName = PageUtils.getElementByXpath(page, "//*/ul[@class='jb']/li[1]").get(0).asText();
                    ci.getCarrierUserInfo().setPackageName(packageName);
                }
            }
            logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
            List<Page> calls = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
            if(calls!=null){
                for (Page callPage : calls) {
                    if (callPage.getWebResponse().getContentAsString().contains("false")) {
                        break;
                    }
                    JSONObject jsonObject = JSONObject.parseObject(callPage.getWebResponse().getContentAsString());
                    if (jsonObject.getString("CDMA_CALL_CDR") != null) {
                        JSONArray array = jsonObject.getJSONArray("CDMA_CALL_CDR");
                        if (array.size() > 0) {
                            for (Object o : array) {
                                JSONObject object = (JSONObject) o;
                                CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
                                ccdi.setBillMonth(object.getString("CYCLE"));
                                ccdi.setDialType(object.getString("CALLING_TYPE_NAME").contains("主叫") ? "DIAL" : "DIALED");
                                ccdi.setDuration(object.getString("DURATION"));
                                ccdi.setFee((int) (Double.parseDouble(object.getString("FEE5")) * 100));
                                ccdi.setTime(object.getString("START_DATE"));
                                ccdi.setLocation("");
                                ccdi.setLocationType("");
                                ccdi.setPeerNumber(object.getString("ORG_CALLED_NBR"));
                                ccdi.setMappingId(ci.getMappingId());
                                ci.getCalls().add(ccdi);
                            }
                        }
                    }
                }
            }

            logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
            List<Page> smsPages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
            if(smsPages!=null){
                for (Page smsPage : smsPages) {
                    JSONObject jsonObject = JSONObject.parseObject(smsPage.getWebResponse().getContentAsString());
                    if (jsonObject.getString("CDMA_SMS_CDR") != null) {
                        JSONArray array = jsonObject.getJSONArray("CDMA_SMS_CDR");
                        if (array.size() > 0) {
                            for (Object o : array) {
                                JSONObject object = (JSONObject) o;
                                CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
                                csri.setBillMonth(object.getString("CYCLE"));
                                csri.setFee((int) (Double.parseDouble(object.getString("FEE2")) * 100));
                                csri.setLocation(object.getString("CALLED_AREA"));
                                csri.setMappingId(ci.getMappingId());
                                csri.setMsgType("SMS");
                                csri.setPeerNumber(object.getString("ORG_CALLED_NBR"));
                                csri.setSendType(object.getString("CALLING_TYPE_NAME").contains("主叫") ? "SEND" : "RECEIVE");
                                csri.setServiceName(object.getString("NAME"));
                                csri.setTime(object.getString("START_DATE"));
                                ci.getSmses().add(csri);
                            }
                        }
                    }
                }
            }

            logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
            List<Page> pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
            if(pages!=null){
                for (Page netPage : pages) {
                    JSONObject jsonObject = JSONObject.parseObject(netPage.getWebResponse().getContentAsString());
                    if (jsonObject.getString("CDMA_DATA_CDR") != null) {
                        JSONArray array = jsonObject.getJSONArray("CDMA_DATA_CDR");
                        if (array.size() > 0) {
                            for (Object o : array) {
                                JSONObject object = (JSONObject) o;
                                CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
                                cndi.setMappingId(ci.getMappingId());
                                cndi.setBillMonth(object.getString("CYCLE"));
                                cndi.setServiceName(object.getString("NAME"));
                                cndi.setLocation("");
                                cndi.setTime(object.getString("START_DATE"));
                                cndi.setNetType(object.getString("NETWORK_TYPE"));
                                cndi.setDuration(Integer.parseInt(object.getString("DURATION")));
                                cndi.setSubflow(Integer.parseInt(object.getString("AMOUNT")));
                                cndi.setFee((int) (Double.parseDouble(object.getString("FEE2")) * 100));
                                ci.getNets().add(cndi);
                            }
                        }
                    }
                }
            }

            logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
            if(pages!=null){
                for (Page p : pages) {
                    if (null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())) {
                        String pageContent = p.getWebResponse().getContentAsString();
                        try {
                            CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
                            JSONArray array = JSONArray.parseArray(pageContent);
                            for (Object o : array) {
                                JSONObject object = (JSONObject) o;
                                String itemName = object.getString("AcctItemName");
                                if (itemName.contains("基本月租")) {
                                    cbdi.setBaseFee(((int) (Double.parseDouble(object.getString("RealAmout")))));
                                } else if (itemName.contains("天翼月功能")) {
                                    cbdi.setExtraFee(((int) (Double.parseDouble(object.getString("RealAmout")))));
                                } else if (itemName.contains("小计")) {
                                    cbdi.setTotalFee(((int) (Double.parseDouble(object.getString("RealAmout")))));
                                }
                            }
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            String billStartDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            String billEndDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                            cbdi.setBillMonth(billStartDate.substring(0, 7));
                            cbdi.setBillStartDate(billStartDate);
                            cbdi.setBillEndDate(billEndDate);
                            cbdi.setMappingId(ci.getMappingId());
                            ci.getBills().add(cbdi);
                        } catch (Exception e) {
                            //继续循环
                        }
                    }
                }
            }
            logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
            if (null != pages && pages.size() > 0) {
                for (Page packagePage : pages) {
                    List<HtmlElement> rows = PageUtils.getElementByXpath(packagePage, "//table[@cellpadding='5']/tbody/tr");
                    for (int i = 1; i < rows.size(); i++) {
                        CarrierPackageItemInfo cpii = new CarrierPackageItemInfo();
                        cpii.setTotal(((HtmlTableRow) rows.get(i)).getCell(1).asText());
                        cpii.setItem(((HtmlTableRow) rows.get(i)).getCell(0).asText());
                        String total = ((HtmlTableRow) rows.get(i)).getCell(1).asText();
                        String gb = RegexUtils.matchValue("(\\d+).(\\d+)(G)", total);
                        String mb = RegexUtils.matchValue("(\\d+).(\\d+)(M)", total);
                        String kb = RegexUtils.matchValue("(\\d+).(\\d+)(K)", total);
                        int totalInt = 0;
                        if (StringUtils.isNotBlank(gb)) {
                            totalInt = Integer.parseInt(gb) * 1048576;
                        }
                        if (StringUtils.isNotBlank(mb)) {
                            totalInt += Integer.parseInt(mb) * 1024;
                        }
                        if (StringUtils.isNotBlank(kb)) {
                            totalInt += Integer.parseInt(kb);
                        }
                        String userd = ((HtmlTableRow) rows.get(i)).getCell(1).asText();
                        String usedGb = RegexUtils.matchValue("(\\d+).(\\d+)(G)", userd);
                        String usedMb = RegexUtils.matchValue("(\\d+).(\\d+)(M)", userd);
                        String usedKb = RegexUtils.matchValue("(\\d+).(\\d+)(K)", userd);
                        int usedInt = 0;
                        if (StringUtils.isNotBlank(usedGb)) {
                            usedInt = Integer.parseInt(gb) * 1048576;
                        }
                        if (StringUtils.isNotBlank(usedMb)) {
                            usedInt += Integer.parseInt(mb) * 1024;
                        }
                        if (StringUtils.isNotBlank(usedKb)) {
                            usedInt += Integer.parseInt(kb);
                        }
                        cpii.setTotal(totalInt + "");
                        cpii.setUsed(usedInt + "");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(DateUtils.stringToDate(((HtmlTableRow) rows.get(i)).getCell(4).asText(), "yyyy-MM-dd"));
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                        String startDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        String endDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                        cpii.setBillStartDate(startDate);
                        cpii.setBillEndDate(endDate);
                        cpii.setUnit(userd.contains("M") ? "KB" : "分钟");
                        cpii.setMappingId(ci.getMappingId());
                        ci.getPackages().add(cpii);
                    }
                }
            }

            logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
            page = cc.getPage(ProcessorCode.RECHARGE_INFO.getCode());
            if(page!=null){
                JSONArray array = JSONArray.parseArray(page.getWebResponse().getContentAsString());
                for (Object o : array) {
                    JSONObject object = (JSONObject) o;
                    CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
                    curi.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(object.getString("PaymentDate"), "yyyyMMddHHmmss"), "yyyy-MM"));
                    curi.setMappingId(ci.getMappingId());
                    curi.setRechargeTime(DateUtils.dateToString(DateUtils.stringToDate(object.getString("PaymentDate"), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                    curi.setAmount((int) (Double.parseDouble(object.getString("Amount"))));
                    curi.setType(object.getString("PaymentMethod"));
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

