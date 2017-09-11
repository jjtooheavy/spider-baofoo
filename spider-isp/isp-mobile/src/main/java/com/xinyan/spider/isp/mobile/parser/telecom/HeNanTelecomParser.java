package com.xinyan.spider.isp.mobile.parser.telecom;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
 * @Description:河南电信数据解析
 * @author: jiangmengchen
 * @date: 2017-05-27 16:43
 * @version: v1.0
 */
@Component
public class HeNanTelecomParser {

    protected static Logger logger = LoggerFactory.getLogger(HeNanTelecomParser.class);

    @SuppressWarnings("unchecked")
    public Result parse(Context context, CarrierInfo ci, CacheContainer cc) {
        Result result = new Result();
        try {
            logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
            String pageInfo = "";
            ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
            ci.getCarrierUserInfo().setProvince("河南省");//所属省份

            //个人信息
            Page page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                String name = RegexUtils.matchValue("<td width=\"120px\" class=\"od\" align=\"right\">客户名称：</td>\\s+<td>(.*?)</td>", pageInfo);
                ci.getCarrierUserInfo().setName(name);
                ci.getCarrierUserInfo().setState(-1);
                ci.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss"));
                ci.getCarrierUserInfo().setIdCard(RegexUtils.matchValue("<td width=\"120px\" class=\"od\" align=\"right\">证件号码：</th>\\s+<td>(.*?)</td>", pageInfo));
                ci.getCarrierUserInfo().setAddress(RegexUtils.matchValue("<td width=\"120px\" class=\"od\" align=\"right\">客户住址：</td>\\s+<td><span class=\"text_content\"></span>", pageInfo));//地址
                ci.getCarrierUserInfo().setMobile(context.getUserName());
                ci.getCarrierUserInfo().setOpenTime(RegexUtils.matchValue("<td width=\"120px\" class=\"od\" align=\"right\">创建日期：</td>(.*?)<td></td>", pageInfo));
                ci.getCarrierUserInfo().setLevel("");//帐号星级
                ci.getCarrierUserInfo().setCity("");
            }
            //余额
            page = cc.getPage(ProcessorCode.BASIC_INFO.getCode() + 1);
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                ci.getCarrierUserInfo().setAvailableBalance((int) (Double.parseDouble(RegexUtils.matchValue("可用余额：<span class=\"sum\">(.*?)</span>元", pageInfo)) * 100));
                ci.getCarrierUserInfo().setPackageName("");
            }
            logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
            List<Page> pages = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
            if(pages!=null){
                for (Page p : pages) {
                    HtmlPage htmlPage = (HtmlPage) p;
                    List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@id='listQry']/tbody/tr");
                    for (HtmlTableRow tr : trs) {
                        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
                        ccdi.setPeerNumber(tr.getCell(1).asText());
                        ccdi.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(tr.getCell(2).asText(), "yyyyMMddHHmmss"), "yyyy-MM"));
                        ccdi.setLocationType("市话");
                        ccdi.setLocation("市话");
                        ccdi.setDialType(tr.getCell(5).asText().equals("主叫") ? "DIAL" : "DIALED");
                        ccdi.setTime(DateUtils.dateToString(DateUtils.stringToDate(tr.getCell(2).asText(), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                        ccdi.setDuration(tr.getCell(4).asText());
                        ccdi.setFee((int) (Double.parseDouble(tr.getCell(6).asText()) * 100));
                        ccdi.setMappingId(ci.getMappingId());
                        ci.getCalls().add(ccdi);
                    }
                }
            }
            pages = cc.getPages(ProcessorCode.CALLRECORD_INFO.getCode() + 1);
            if(pages!=null){
                for (Page p : pages) {
                    HtmlPage htmlPage = (HtmlPage) p;
                    List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@id='listQry']/tbody/tr");
                    for (HtmlTableRow tr : trs) {
                        CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
                        ccdi.setPeerNumber(tr.getCell(1).asText());
                        ccdi.setLocationType("长途");
                        ccdi.setLocation("长途");
                        ccdi.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(tr.getCell(2).asText(), "yyyyMMddHHmmss"), "yyyy-MM"));
                        ccdi.setDialType(tr.getCell(5).asText().equals("主叫") ? "DIAL" : "DIALED");
                        ccdi.setTime(DateUtils.dateToString(DateUtils.stringToDate(tr.getCell(2).asText(), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                        ccdi.setDuration(tr.getCell(4).asText());
                        ccdi.setFee((int) (Double.parseDouble(tr.getCell(6).asText()) * 100));
                        ccdi.setMappingId(ci.getMappingId());
                        ci.getCalls().add(ccdi);
                    }
                }
            }
            logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.SMS_INFO.getCode());
            if(pages!=null){
                for (Page p : pages) {
                    HtmlPage htmlPage = (HtmlPage) p;
                    List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@id='listQry']/tbody/tr");
                    for (HtmlTableRow tr : trs) {
                        CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
                        csri.setMappingId(ci.getMappingId());
                        csri.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(tr.getCell(2).asText(), "yyyyMMddHHmmss"), "yyyy-MM"));
                        csri.setFee((int) (Double.parseDouble(tr.getCell(4).asText())));
                        csri.setTime(DateUtils.dateToString(DateUtils.stringToDate(tr.getCell(2).asText(), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                        csri.setLocation("");
                        csri.setPeerNumber(tr.getCell(1).asText());
                        csri.setMsgType("SMS");
                        csri.setSendType(tr.getCell(3).asText().contains("发送") ? "SEND" : "RECEIVE");
                        csri.setServiceName("");
                        ci.getSmses().add(csri);
                    }
                }
            }

            logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
            if(pages!=null){
                for (Page p : pages) {
                    HtmlPage htmlPage = (HtmlPage) p;
                    List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@id='listQry']/tbody/tr");
                    for (HtmlTableRow tr : trs) {
                        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
                        cndi.setMappingId(ci.getMappingId());
                        cndi.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(tr.getCell(1).asText(), "yyyyMMddHHmmss"), "yyyy-MM"));
                        cndi.setServiceName("");
                        cndi.setLocation(tr.getCell(5).asText());
                        cndi.setTime(DateUtils.dateToString(DateUtils.stringToDate(tr.getCell(1).asText(), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                        cndi.setNetType(tr.getCell(4).asText());
                        String hour = RegexUtils.matchValue("(\\d+)时", tr.getCell(2).asText());
                        String min = RegexUtils.matchValue("(\\d+)分", tr.getCell(2).asText());
                        String sec = RegexUtils.matchValue("(\\d+)秒", tr.getCell(2).asText());
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
                        cndi.setDuration(finalDuration);
                        String durs = RegexUtils.matchValue("(\\d+)G", tr.getCell(3).asText()).trim();
                        int dur = 0;
                        if (StringUtils.isNotEmpty(durs)) {
                            dur += Integer.parseInt(durs) * 1048576;
                        }
                        durs = RegexUtils.matchValue("(\\d+)M", tr.getCell(3).asText()).trim();
                        if (StringUtils.isNotEmpty(durs)) {
                            dur += Integer.parseInt(durs) * 1024;
                        }
                        durs = RegexUtils.matchValue("(\\d+)K", tr.getCell(3).asText()).trim();
                        if (StringUtils.isNotEmpty(durs)) {
                            dur += Integer.parseInt(durs);
                        }
                        cndi.setSubflow(dur);
                        cndi.setFee((int) (Double.parseDouble(tr.getCell(7).asText()) * 100));
                        ci.getNets().add(cndi);
                    }
                }
            }


            logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
            for (Page p : pages) {
                if (null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())) {
                    CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
                    String pageContent = p.getWebResponse().getContentAsString();
                    String date = RegexUtils.matchValue("账期：</span>(\\d+)", pageContent);
                    if (StringUtils.isBlank(date)) {
                        continue;
                    }
                    cbdi.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(date, "yyyyMM"), "yyyy-MM"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtils.stringToDate(date, "yyyyMM"));
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

                    cbdi.setMappingId(ci.getMappingId());
                    String baseFee = RegexUtils.matchValue("套餐月基本费</td>\\s+<td style=\"border-right:0px;\"><span id=\"parent_1_10_2\">(.*?)</span></td>", pageContent);
                    String extraFee = RegexUtils.matchValue("<td class=\"h6 lh5 p29\">来电显示费</td>\\s+<td class=\"h6 lh5 p29\">(.*?)</td>", pageContent);
                    String voiceFee = RegexUtils.matchValue("<td class=\"h6 lh5 p29\">语音通话费</td>\\s+<td class=\"h6 lh5 p29\">(.*?)</td>", pageContent);
                    String extraDiscount = RegexUtils.matchValue("<td class=\"h6 lh5 p29\">优惠费用</td>\\s+<td class=\"h6 lh5 p29\">-(.*?)</td>", pageContent);
                    cbdi.setExtraFee((int) (Double.parseDouble(extraFee.equals("") ? "0" : extraFee) * 100));
                    cbdi.setExtraDiscount((int) (Double.parseDouble(baseFee.equals("") ? "0" : baseFee) * 100));
                    cbdi.setVoiceFee((int) (Double.parseDouble(voiceFee.equals("") ? "0" : voiceFee) * 100));
                    cbdi.setDiscount((int) (Double.parseDouble(extraDiscount.equals("") ? "0" : extraDiscount) * 100));
                    cbdi.setTotalFee((int) (Double.parseDouble(RegexUtils.matchValue("费用总计：(.*?) 元</td>", pageContent)) * 100));
                    int point = RegexUtils.matchValue("<td>本期末可用积分</td>\\s+<td>=(.*?)</td>", pageContent).equals("") ? 0 : Integer.parseInt(RegexUtils.matchValue("<td>本期末可用积分</td>\\s+<td>=(.*?)</td>", pageContent));
                    int lastPoint = RegexUtils.matchValue("<td>上期末可用积分</td>\\s+<td>-(.*?)</td>", pageContent).equals("") ? 0 : Integer.parseInt(RegexUtils.matchValue("<td>上期末可用积分</td>\\s+<td>-(.*?)</td>", pageContent));
                    cbdi.setPoint(point);
                    cbdi.setLastPoint(lastPoint);
                    cbdi.setBillStartDate(DateUtils.dateToString(DateUtils.stringToDate(date + "01", "yyyyMMdd"), "yyyy-MM-dd"));
                    cbdi.setBillEndDate(DateFormatUtils.format(calendar, "yyyy-MM-dd"));
                    cbdi.setRelatedMobiles("");//本手机关联号码, 多个手机号以逗号分隔
                    ci.getBills().add(cbdi);
                }
            }

            logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
            page = cc.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
            if (null != page) {
                List<HtmlElement> elements = PageUtils.getElementByXpath(page, "//table/tbody/tr");
                for (int i = 1; i < elements.size(); i++) {
                    CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
                    HtmlTableRow tableRow = (HtmlTableRow) elements.get(i);
                    cpti.setMappingId(ci.getMappingId());//映射id
                    cpti.setItem(tableRow.getCell(0).asText().trim());//套餐项目名称
                    ci.getCarrierUserInfo().setPackageName(((HtmlTableRow) elements.get(1)).getCell(0).asText().trim());
                    String startTmp = tableRow.getCell(1).asText().trim();
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTime(DateUtils.stringToDate(startTmp, "yyyy-MM-dd"));
                    } catch (Exception e) {
                        continue;
                    }
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    String endTmp = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                    cpti.setBillStartDate(startTmp);//账单起始日, 格式为yyyy-MM-dd
                    cpti.setBillEndDate(endTmp);//账单结束日, 格式为yyyy-MM-dd
                    ci.getPackages().add(cpti);
                }
            }

            logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
            page = cc.getPage(ProcessorCode.RECHARGE_INFO.getCode());
            if (null != page) {
                List<HtmlElement> elements = PageUtils.getElementByXpath(page, "//table[@id='Infotable']/tbody/tr");
                for (HtmlElement element : elements) {
                    HtmlTableRow tableRow = (HtmlTableRow) element;
                    CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
                    curi.setBillMonth(tableRow.getCell(3).asText().substring(0, 7));
                    curi.setMappingId(ci.getMappingId());
                    curi.setRechargeTime(tableRow.getCell(3).asText());
                    curi.setAmount((int) (Double.parseDouble(tableRow.getCell(2).asText()) * 100));
                    curi.setType(tableRow.getCell(1).asText());
                    ci.getRecharges().add(curi);
                }
            }

            result.setData(ci);
            result.setResult(StatusCode.解析成功);
        } catch (Exception e) {
            logger.info("==>[{}]解析出错了:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.数据解析中发生错误);
        }
        return result;
    }
}
