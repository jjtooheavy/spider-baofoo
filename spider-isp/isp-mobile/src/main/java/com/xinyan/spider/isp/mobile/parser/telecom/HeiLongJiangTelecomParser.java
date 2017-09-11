package com.xinyan.spider.isp.mobile.parser.telecom;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.*;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Calendar;
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
public class HeiLongJiangTelecomParser {

    protected static Logger logger = LoggerFactory.getLogger(HeiLongJiangTelecomParser.class);

    @SuppressWarnings("unchecked")
    public Result parse(Context context, CarrierInfo ci, CacheContainer cc) {
        Result result = new Result();
        try {
            logger.info("==>[{}]正在解析运营商用户信息...", context.getTaskId());
            String pageInfo = "";
            ci.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
            ci.getCarrierUserInfo().setProvince("黑龙江");//所属省份

            //个人信息
            Page page = cc.getPage(ProcessorCode.BASIC_INFO.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                String name = RegexUtils.matchValue("<td width=\"12%\" height=\"47\" align=\"right\">客户名称：</td>\\s+<td align=\"left\" colspan=\"3\">&nbsp;&nbsp;(.*?)</td>", pageInfo);
                ci.getCarrierUserInfo().setName(name);
                ci.getCarrierUserInfo().setAddress(RegexUtils.matchValue("通讯地址：</td>\\s+<td><span style=\"float:left\"><input type=\"text\" name=\"crmCustInfoVO.address\" size=\"44\" value=\"\"></span>", pageInfo));//地址
                ci.getCarrierUserInfo().setMobile(context.getUserName());
            }
            //余额
            page = cc.getPage(ProcessorCode.AMOUNT.getCode());
            if(page!=null){
                pageInfo = page.getWebResponse().getContentAsString();
                ci.getCarrierUserInfo().setAvailableBalance((int) (Double.parseDouble(RegexUtils.matchValue("<td width=\"25%\" height=\"47\" align=\"right\">\\s+普通预存款余额：\\s+</td>\\s+<td width=\"25%\" align=\"center\">\\s+(.*?)（元）", pageInfo).trim()) * 100));
            }

            logger.info("==>[{}]正在解析通话详单...", context.getTaskId());
            Page manyou = cc.getPage(ProcessorCode.CALLRECORD_INFO.getCode());
            Page changtu = cc.getPage(ProcessorCode.CALLRECORD_INFO.getCode() + 1);
            Page shihua = cc.getPage(ProcessorCode.CALLRECORD_INFO.getCode() + 2);

            InputStream input = manyou.getWebResponse().getContentAsStream();
            Workbook book = Workbook.getWorkbook(input);
            Sheet sheet = book.getSheet(0);
            for (int i = 1; i < sheet.getRows(); i++) {
                CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
                ccdi.setPeerNumber(sheet.getCell(3, i).getContents());
                ccdi.setLocation(sheet.getCell(9, i).getContents());
                ccdi.setLocationType("");
                ccdi.setBillMonth(sheet.getCell(4, i).getContents().substring(0, 7));
                ccdi.setTime(sheet.getCell(4, i).getContents());
                ccdi.setDialType(sheet.getCell(7, i).getContents().equals("主叫") ? "DIAL" : "DIALED");

                String hour = RegexUtils.matchValue("(\\d+)时", sheet.getCell(5, i).getContents());
                String min = RegexUtils.matchValue("(\\d+)分", sheet.getCell(5, i).getContents());
                String sec = RegexUtils.matchValue("(\\d+)秒", sheet.getCell(5, i).getContents());
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
                ccdi.setDuration(finalDuration+"");
                ccdi.setFee((int) (Double.parseDouble(sheet.getCell(6, i).getContents()) * 100));
                ccdi.setMappingId(ci.getMappingId());
                ccdi.setLocationType("漫游");
                ci.getCalls().add(ccdi);
            }
            input = changtu.getWebResponse().getContentAsStream();
            book = Workbook.getWorkbook(input);
            sheet = book.getSheet(0);
            for (int i = 1; i < sheet.getRows(); i++) {
                CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
                ccdi.setPeerNumber(sheet.getCell(3, i).getContents());
                ccdi.setLocation(sheet.getCell(9, i).getContents());
                ccdi.setLocationType("");
                ccdi.setBillMonth(sheet.getCell(4, i).getContents().substring(0, 7));
                ccdi.setTime(sheet.getCell(4, i).getContents());
                ccdi.setDialType(sheet.getCell(7, i).getContents().equals("主叫") ? "DIAL" : "DIALED");
                ccdi.setDuration(sheet.getCell(5, i).getContents());
                ccdi.setFee((int) (Double.parseDouble(sheet.getCell(6, i).getContents()) * 100));
                ccdi.setMappingId(ci.getMappingId());
                ccdi.setLocationType("长途");
                ci.getCalls().add(ccdi);
            }
            input = shihua.getWebResponse().getContentAsStream();
            book = Workbook.getWorkbook(input);
            sheet = book.getSheet(0);
            for (int i = 1; i < sheet.getRows(); i++) {
                CarrierCallDetailInfo ccdi = new CarrierCallDetailInfo();
                ccdi.setPeerNumber(sheet.getCell(3, i).getContents());
                ccdi.setLocation(sheet.getCell(9, i).getContents());
                ccdi.setLocationType("");
                ccdi.setBillMonth(sheet.getCell(4, i).getContents().substring(0, 7));
                ccdi.setTime(sheet.getCell(4, i).getContents());
                ccdi.setDialType(sheet.getCell(7, i).getContents().equals("主叫") ? "DIAL" : "DIALED");
                ccdi.setDuration(sheet.getCell(5, i).getContents());
                ccdi.setFee((int) (Double.parseDouble(sheet.getCell(6, i).getContents()) * 100));
                ccdi.setMappingId(ci.getMappingId());
                ccdi.setLocationType("市话");
                ci.getCalls().add(ccdi);
            }


            book.close();
            input.close();

            logger.info("==>[{}]正在解析短信记录...", context.getTaskId());
            page = cc.getPage(ProcessorCode.SMS_INFO.getCode());
            if(page!=null){
                input = page.getWebResponse().getContentAsStream();
                book = Workbook.getWorkbook(input);
                sheet = book.getSheet(0);
                for (int i = 1; i < sheet.getRows(); i++) {
                    CarrierSmsRecordInfo csri = new CarrierSmsRecordInfo();
                    csri.setMappingId(ci.getMappingId());
                    csri.setBillMonth(sheet.getCell(3, i).getContents().substring(0, 7));
                    csri.setTime(sheet.getCell(3, i).getContents());
                    csri.setPeerNumber(sheet.getCell(2, i).getContents());
                    csri.setSendType(sheet.getCell(6, i).getContents().equals("主叫") ? "SEND" : "RECEIVE");
                    csri.setMsgType("SMS");
                    csri.setFee((int) (Double.parseDouble(sheet.getCell(5, i).getContents())));
                    ci.getSmses().add(csri);
                }
            }
            logger.info("==>[{}]正在解析上网记录...", context.getTaskId());
            List<Page> pages = cc.getPages(ProcessorCode.NET_INFO.getCode());
            if(pages!=null){
                for (Page p : pages) {
                    HtmlPage htmlPage = (HtmlPage) p;
                    List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@id='tb1']/tbody/tr");
                    for (HtmlTableRow tr : trs) {
                        CarrierNetDetailInfo cndi = new CarrierNetDetailInfo();
                        cndi.setMappingId(ci.getMappingId());
                        cndi.setBillMonth(tr.getCell(1).asText().substring(0, 7));
                        cndi.setServiceName("");
                        cndi.setLocation(tr.getCell(5).asText());
                        cndi.setTime(tr.getCell(1).asText());
                        cndi.setNetType(tr.getCell(4).asText());
                        String duration = tr.getCell(2).asText();
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

                        String subflow = tr.getCell(3).asText();
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
                        cndi.setFee((int) (Double.parseDouble(tr.getCell(7).asText())));
                        ci.getNets().add(cndi);
                    }
                }
            }

            logger.info("==>[{}]正在解析账单记录...", context.getTaskId());
            pages = cc.getPages(ProcessorCode.BILL_INFO.getCode());
            for (Page p : pages) {
                if (null != p && StringUtils.isNotEmpty(p.getWebResponse().getContentAsString())) {
                    String pageContent = p.getWebResponse().getContentAsString();
                    if (pageContent.contains("对不起，系统忙，请稍后再试")) {
                        continue;
                    }
                    CarrierBillDetailInfo cbdi = new CarrierBillDetailInfo();
                    String dateRange = RegexUtils.matchValue("计费账期:(.*?)\\s+</td>", pageContent);
                    cbdi.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(dateRange.split("-")[0], "yyyy/MM/dd"), "yyyy-MM"));
                    cbdi.setMappingId(ci.getMappingId());
                    String baseFee = RegexUtils.matchValue("基本月租费\\s+</td>\\s+<td class=\"td5\">\\s+&nbsp;\\s+(.*?)\\s+</td>", pageContent);
                    cbdi.setBaseFee((int) (Double.parseDouble(baseFee.equals("") ? "0" : baseFee) * 100));
                    String webFee = RegexUtils.matchValue("手机国内上网费\\s+</td>\\s+<td class=\"td5\">\\s+(.*?)\\s+&nbsp;\\s+</td>", pageContent);
                    cbdi.setWebFee((int) (Double.parseDouble(webFee.equals("") ? "0" : webFee) * 100));
                    String voiceFee = RegexUtils.matchValue("国内通话费\\s+</td>\\s+<td class=\"td5\">\\s+(.*?)\\s+&nbsp;\\s+</td>", pageContent);
                    cbdi.setVoiceFee((int) (Double.parseDouble(voiceFee.equals("") ? "0" : voiceFee) * 100));
                    cbdi.setTotalFee((int) (Double.parseDouble(RegexUtils.matchValue("<td  > 本期费用合计：(.*?) </br>", pageContent)) * 100));
                    int point = RegexUtils.matchValue("本期末可用积分 </td>\\s+<td width=\"7%\"  class=\"td5\">=(.*?) </td>", pageContent).equals("") ? 0 : Integer.parseInt(RegexUtils.matchValue("本期末可用积分 </td>\\s+<td width=\"7%\"  class=\"td5\">=(.*?) </td>", pageContent));
                    int lastPoint = RegexUtils.matchValue("上期末可用积分 </td>\\s+<td width=\"7%\" class=\"td5\">-(.*?)</td>", pageContent).equals("") ? 0 : Integer.parseInt(RegexUtils.matchValue("上期末可用积分 </td>\\s+<td width=\"7%\" class=\"td5\">-(.*?)</td>", pageContent));
                    cbdi.setPoint(point);
                    cbdi.setLastPoint(lastPoint);
                    cbdi.setBillStartDate(DateUtils.dateToString(DateUtils.stringToDate(dateRange.split("-")[0], "yyyy/MM/dd"), "yyyy-MM-dd"));
                    cbdi.setBillEndDate(DateUtils.dateToString(DateUtils.stringToDate(dateRange.split("-")[1], "yyyy/MM/dd"), "yyyy-MM-dd"));
                    cbdi.setRelatedMobiles("");//本手机关联号码, 多个手机号以逗号分隔
                    ci.getBills().add(cbdi);
                }
            }

            logger.info("==>[{}]正在解析套餐信息...", context.getTaskId());
            page = cc.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
            if (null != page) {
                HtmlPage htmlPage = (HtmlPage) page;
                String dateRange = RegexUtils.matchValue("截止时间：<font class=\"color\">(.*?)</font>", page.getWebResponse().getContentAsString());
                List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table/tbody/tr");
                for (int i = 1; i <= trs.size() - 1; i++) {
                    CarrierPackageItemInfo cpti = new CarrierPackageItemInfo();
                    cpti.setMappingId(ci.getMappingId());//映射id
                    cpti.setItem(trs.get(i).getCell(1).asText().trim());//套餐项目名称
                    ci.getCarrierUserInfo().setPackageName(trs.get(i).getCell(1).asText().trim());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtils.stringToDate(dateRange, "yyyy-MM-dd HH:mm:ss"));
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                    cpti.setBillStartDate(DateFormatUtils.format(calendar, "yyyy-MM-dd"));//账单起始日, 格式为yyyy-MM-dd
                    cpti.setBillEndDate(dateRange.substring(0, 10));//账单结束日, 格式为yyyy-MM-dd
                    if (trs.get(i).getCell(3).asText().contains("分")) {
                        cpti.setUnit("分钟");
                        cpti.setTotal(RegexUtils.matchValue("(\\d+)\\s+.分钟", trs.get(i).getCell(3).asText()));
                        cpti.setUsed(RegexUtils.matchValue("(\\d+).分钟", trs.get(i).getCell(4).asText()));
                    } else {
                        cpti.setUnit("KB");
                        cpti.setTotal(RegexUtils.matchValue("(\\d+)\\s+.KB", trs.get(i).getCell(3).asText()));
                        cpti.setUsed(RegexUtils.matchValue("(\\d+)\\s+.KB", trs.get(i).getCell(4).asText()));
                    }
                    ci.getPackages().add(cpti);
                }
            }

            logger.info("==>[{}]正在解析充值记录...", context.getTaskId());
            page = cc.getPage(ProcessorCode.RECHARGE_INFO.getCode());
            if(page!=null){
                HtmlPage htmlPage = (HtmlPage) page;
                List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table/tbody/tr");
                for (int i = 1; i < trs.size(); i++) {
                    if ("".equals(trs.get(i).asText())) {
                        continue;
                    }
                    CarrierUserRechargeItemInfo curi = new CarrierUserRechargeItemInfo();
                    curi.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(trs.get(i).getCell(2).asText().trim()
                            , "yyyyMMddHHmmss"), "yyyy-MM"));
                    curi.setMappingId(ci.getMappingId());
                    curi.setRechargeTime(DateUtils.dateToString(DateUtils.stringToDate(trs.get(i).getCell(2).asText().trim()
                            , "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                    curi.setAmount((int) (Double.parseDouble(trs.get(i).getCell(3).asText()) * 100));
                    curi.setType(trs.get(i).getCell(1).asText());
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

