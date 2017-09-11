package com.xinyan.spider.isp.mobile.parser.cmcc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.*;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.common.utils.*;
import com.xinyan.spider.isp.mobile.model.*;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description:湖北移动数据解析
 * @author: heliang
 * @date: 2016-09-12 19:13
 * @version: v1.0
 */
@Component
public class HuBeiCmccParser {

    protected static Logger logger = LoggerFactory.getLogger(HuBeiCmccParser.class);

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
        Page page = cacheContainer.getPage(ProcessorCode.BASIC_INFO.getCode());
        if(page!=null){
            String strBasicData = page.getWebResponse().getContentAsString();

            // 手机号、真实姓名、入网时间、联系地址、身份证号码、Email
            carrierInfo.getCarrierUserInfo().setMobile(context.getUserName());
            carrierInfo.getCarrierUserInfo()
                    .setName(RegexUtils.matchValue("客户姓名</th>[\\s\\S]*?<td>([\\s\\S]*?)</td>", strBasicData).trim());
            carrierInfo.getCarrierUserInfo()
                    .setLevel(RegexUtils.matchValue("用户等级</th>[\\s\\S]*?<td>([\\s\\S]*?)</td>", strBasicData).trim());
            carrierInfo.getCarrierUserInfo()
                    .setIdCard(RegexUtils.matchValue("证件号码</th>[\\s\\S]*?<td>(.*?)</td>", strBasicData));
            carrierInfo.getCarrierUserInfo().setCarrier("CHINA_MOBILE");
            carrierInfo.getCarrierUserInfo()
                    .setOpenTime(RegexUtils.matchValue("入网时间</th>[\\s\\S]*?<td>(.*?)</td>", strBasicData));
            carrierInfo.getCarrierUserInfo()
                    .setAddress(RegexUtils.matchValue("归属地</th>[\\s\\S]*?<td>(.*?)</td>", strBasicData));
            carrierInfo.getCarrierUserInfo().setProvince(carrierInfo.getCarrierUserInfo().getAddress().substring(0, 2));
            carrierInfo.getCarrierUserInfo().setCity(carrierInfo.getCarrierUserInfo().getAddress().substring(2,
                    RegexUtils.matchValue("归属地</th>[\\s\\S]*?<td>(.*?)</td>", strBasicData).length()));
            //套餐
            page = cacheContainer.getPage(ProcessorCode.POINTS_VALUE.getCode());
            strBasicData = page.getWebResponse().getContentAsString();
            carrierInfo.getCarrierUserInfo().setPackageName(RegexUtils.matchValue("<tr class=\"detail talk\" id=\"talk\">\\s+<th>\\s+<p class.*?>(.*?)\r\n",
                    strBasicData));
            // 星级
            page = cacheContainer.getPage(ProcessorCode.VIP_LVL.getCode());
            strBasicData = page.getWebResponse().getContentAsString();
            String vipLevelstr = RegexUtils.matchValue("<p>我的星级：(.*?)</p>", strBasicData);
            if ("未评级".equals(vipLevelstr)) {
                vipLevelstr = "普通用户";
            }
            carrierInfo.getCarrierUserInfo().setLevel(vipLevelstr);
            carrierInfo.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(
                    new Date(), "yyyy-MM-dd HH:mm:ss")); //上次更新时间
            // 余额
            page = cacheContainer.getPage(ProcessorCode.AMOUNT.getCode());
            strBasicData = page.getWebResponse().getContentAsString();
            carrierInfo.getCarrierUserInfo().setAvailableBalance(
                    (int) Double.parseDouble(RegexUtils.matchValue("\"feeleft\":\"(.*?)\"", strBasicData)) * 100);
        }
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

        // 通话记录
        List<CarrierCallDetailInfo> carrierCallDetailInfos = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                try {
                    InputStream input = page.getWebResponse().getContentAsStream();
                    Workbook book = Workbook.getWorkbook(input);
                    Sheet sheet = book.getSheet(0);
                    for (int i = 8; i < sheet.getRows() - 1; i++) {
                        CarrierCallDetailInfo carrierCallDetailInfo = new CarrierCallDetailInfo();
                        carrierCallDetailInfo.setMappingId(carrierInfo.getMappingId());
                        carrierCallDetailInfo.setBillMonth(sheet.getCell(0, i).getContents().substring(0, 7));// 通话月份
                        carrierCallDetailInfo.setLocation(sheet.getCell(1, i).getContents());// 通话地点
                        carrierCallDetailInfo.setTime(sheet.getCell(0, i).getContents());// 通话时间
                        carrierCallDetailInfo
                                .setDuration(FormatUtils.formatSecondLength(sheet.getCell(4, i).getContents()));// 通话时长
                        if(sheet.getCell(2, i).getContents().contains("主叫")){//通话类型
                            carrierCallDetailInfo.setDialType("DIAL");
                        }else{
                            carrierCallDetailInfo.setDialType("DIALED");
                        }
                        carrierCallDetailInfo.setPeerNumber(sheet.getCell(3, i).getContents());// 对方号码
                        carrierCallDetailInfos.add(carrierCallDetailInfo);
                    }
                    book.close();
                    input.close();
                } catch (IOException e) {
                    logger.error("==>9.2解析通话详单出错了:", e);
                    e.printStackTrace();
                } catch (BiffException e) {
                    logger.error("==>9.2解析通话详单出错了:", e);
                    e.printStackTrace();
                }
            }
        }
        carrierInfo.setCalls(carrierCallDetailInfos);
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

        // 短信记录
        List<CarrierSmsRecordInfo> smses = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.SMS_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                try {
                    InputStream input = page.getWebResponse().getContentAsStream();
                    Workbook book = Workbook.getWorkbook(input);
                    Sheet sheet = book.getSheet(0);
                    for (int i = 6; i < sheet.getRows() - 1; i++) {
                        CarrierSmsRecordInfo carrierSmsRecordInfo = new CarrierSmsRecordInfo();
                        carrierSmsRecordInfo.setMappingId(carrierInfo.getMappingId());
                        carrierSmsRecordInfo.setBillMonth(sheet.getCell(0, i).getContents().substring(0, 7));// 短信月份
                        carrierSmsRecordInfo.setTime(sheet.getCell(0, i).getContents());// 发送时间
                        carrierSmsRecordInfo.setPeerNumber(sheet.getCell(2, i).getContents());// 与本机通话手机号码
                        carrierSmsRecordInfo.setLocation(sheet.getCell(1, i).getContents());// 发送地
                        if(sheet.getCell(3, i).getContents().equals("接收")){
                            carrierSmsRecordInfo.setSendType("RECRIVE");// 发送类型
                        }else{
                            carrierSmsRecordInfo.setSendType("SEND");// 发送类型
                        }
                        carrierSmsRecordInfo.setFee((int) (Double.parseDouble(sheet.getCell(6, i).getContents()) * 100));// 通讯费
                        carrierSmsRecordInfo.setServiceName(sheet.getCell(5, i).getContents());// 业务名称
                        carrierSmsRecordInfo.setMsgType("SMS");
                        smses.add(carrierSmsRecordInfo);
                    }
                    book.close();
                    input.close();
                } catch (IOException e) {
                    logger.error("==>9.3解析短信记录出错了:", e);
                    e.printStackTrace();
                } catch (BiffException e) {
                    logger.error("==>9.3解析短信记录出错了:", e);
                    e.printStackTrace();
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
     * @param carrierInfo
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result netInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();

        // 上网记录
        List<CarrierNetDetailInfo> nets = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.NET_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                try {
                    InputStream input = page.getWebResponse().getContentAsStream();
                    Workbook book = Workbook.getWorkbook(input);
                    Sheet sheet = book.getSheet(0);
                    for (int i = 6; i < sheet.getRows() - 1; i++) {
                        if (sheet.getCell(0, i).getContents().contains("合计")
                                || sheet.getCell(0, i).getContents().contains("WLAN")
                                || sheet.getCell(0, i).getContents().contains("起始时间")) {
                            continue;
                        }
                        CarrierNetDetailInfo netinfo = new CarrierNetDetailInfo();
                        netinfo.setMappingId(carrierInfo.getMappingId());
                        netinfo.setBillMonth(sheet.getCell(0, i).getContents().substring(0, 7));// 上网月份
                        netinfo.setTime(sheet.getCell(0, i).getContents());// 上网时间
                        netinfo.setDuration(
                                Integer.parseInt(FormatUtils.formatSecondLength(sheet.getCell(3, i).getContents())));// 上网时长
                        netinfo.setLocation(sheet.getCell(1, i).getContents());// 上网地点
                        netinfo.setNetType(sheet.getCell(2, i).getContents());// 上网类型
                        if (sheet.getCell(4, i).getContents().contains("M")) {
                            netinfo.setSubflow(Integer.parseInt(sheet.getCell(4, i).getContents().split("\\(M\\)")[0])
                                    * 1024
                                    + Integer.parseInt(
                                    sheet.getCell(4, i).getContents().split("\\(M\\)")[1].replace("(K)", "")));// 上网流量
                        } else {
                            netinfo.setSubflow(Integer.parseInt(sheet.getCell(4, i).getContents().replace("(K)", "")));// 上网流量
                        }
                        if (sheet.getCell(6, i).getContents().contains("null")) {
                            netinfo.setFee(0);
                        } else {
                            netinfo.setFee((int) Double.parseDouble(sheet.getCell(6, i).getContents()) * 100);
                        }

                        nets.add(netinfo);
                    }
                    book.close();
                    input.close();
                } catch (IOException e) {
                    logger.error("==>9.4解析上网记录出错了:", e);
                    e.printStackTrace();
                } catch (BiffException e) {
                    logger.error("==>9.4解析上网记录出错了:", e);
                    e.printStackTrace();
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
        if(pages!=null){
            for (Page page : pages) {
                CarrierBillDetailInfo billinfo = new CarrierBillDetailInfo();
                billinfo.setMappingId(carrierInfo.getMappingId());
                String pageInfo = page.getWebResponse().getContentAsString().replaceAll("\\s+", "");

                String strTime = RegexUtils.matchValue("计费周期：</label><label>(.*?)</label>", pageInfo);
                String startTime = "";
                String endTime = "";
                if (strTime.contains("起截止")) {
                    String[] timeArr = strTime.split("起截止");
                    startTime = timeArr[0].replace("年", "-").replace("月", "-").replace("日", "");
                } else {
                    String[] timeArr = strTime.split("至");
                    startTime = timeArr[0].replace("年", "-").replace("月", "-").replace("日", "");
                    endTime = timeArr[1].replace("年", "-").replace("月", "-").replace("日", "");
                    billinfo.setBillEndDate(endTime);
                }

                if (StringUtils.isEmpty(startTime)) {
                    startTime = RegexUtils.matchValue("^(.*?)01日", startTime).trim();
                }
                billinfo.setBillStartDate(FormatUtils.formatDate(startTime, true));//起始日期
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtils.stringToDate(startTime, "yyyy-MM-dd"));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                billinfo.setBillEndDate(transDateEnd.compareTo(DateUtils.getCurrentDate())>0?DateUtils.getCurrentDate():transDateEnd);//结束日期
                billinfo.setBillMonth(FormatUtils.formatDate(startTime, false));//账单月份
                String baseFee = RegexUtils.matchValue("套餐及固定费</td><tdalign=\"center\"></td><tdalign=\"center\">(.*?)</td>", pageInfo);
                if(baseFee.equals("")){//该月没有账单信息，都设为0
                    billinfo.setBaseFee(0);
                    billinfo.setExtraServiceFee(0);
                    billinfo.setVoiceFee(0);
                    billinfo.setSmsFee(0);
                    billinfo.setWebFee(0);
                    billinfo.setExtraFee(0);
                    billinfo.setTotalFee(0);
                    billinfo.setDiscount(0);
                    billinfo.setActualFee(0);
                    billinfo.setUnpaidFee(0);
                }else{
                    billinfo.setBaseFee((int) (Double.parseDouble(baseFee) * 100));   //套餐固定费
                    String extraServiceFee = RegexUtils.matchValue("自有增值业务费用</td><tdalign=\"center\"></td><tdalign=\"center\">(.*?)</td>", pageInfo);
                    billinfo.setExtraServiceFee((int) (Double.parseDouble(extraServiceFee) * 100));//增值业务费
                    String voiceFee = RegexUtils.matchValue("语音通信费</li></td><tdalign=\"center\"></td><tdalign=\"center\">(.*?)</td>", pageInfo);
                    billinfo.setVoiceFee((int) (Double.parseDouble(voiceFee) * 100));//语音通信费
                    String smsFee = RegexUtils.matchValue("短彩信费</li></td><tdalign=\"center\"></td><tdalign=\"center\">(.*?)</td>", pageInfo);
                    billinfo.setSmsFee((int) (Double.parseDouble(smsFee) * 100));//短彩信费
                    String webFee = RegexUtils.matchValue("上网费</li></td><tdalign=\"center\"></td><tdalign=\"center\">(.*?)</td>", pageInfo);
                    billinfo.setWebFee((int) (Double.parseDouble(webFee) * 100));//上网费
                    String extraFee = RegexUtils.matchValue("其他费用</td><tdalign=\"center\"></td><tdalign=\"center\">(.*?)</td>", pageInfo);
                    billinfo.setExtraFee((int) (Double.parseDouble(extraFee) * 100));//其他费用
                    String totalFee = RegexUtils.matchValue("合计</td><tdalign=\"center\"></td><tdalign=\"center\">(.*?)</td>", pageInfo);
                    billinfo.setTotalFee((int) (Double.parseDouble(totalFee) * 100));//总费用
                    String discount = RegexUtils.matchValue("减免费</td><tdalign=\"center\"></td><tdalign=\"center\">(.*?)</td>", pageInfo);
                    billinfo.setDiscount((int) (Double.parseDouble(discount) * 100));//减免费
                    String actualFee = RegexUtils.matchValue("本期消费：</label><labelclass=\"header_50\">(.*?)</label>", pageInfo);
                    if (StringUtils.isEmpty(actualFee)) {
                        billinfo.setActualFee(0);
                    } else {
                        billinfo.setActualFee((int) (Double.parseDouble(actualFee) * 100));//本期消费
                    }
                    String unpaidFee = RegexUtils.matchValue("本期末欠费：</label><labelclass=\"header_37\">(.*?)</label>", pageInfo);
                    billinfo.setUnpaidFee((int) (Double.parseDouble(unpaidFee) * 100));//本期末欠费
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
    public Result packageItemInfoParse(Context context, CacheContainer
            cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<CarrierPackageItemInfo> bills = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.PACKAGE_ITEM.getCode());
        if(pages!=null){
            for (Page page : pages) {
                String pageInfo = page.getWebResponse().getContentAsString().replaceAll("\\s+", "");
                String strTime = RegexUtils.matchValue("计费周期：</label><label>(.*?)</label>", pageInfo);
                String startTime = "";
                String endTime = "";
                if (strTime.contains("起截止")) {
                    String[] timeArr = strTime.split("起截止");
                    startTime = timeArr[0].replace("年", "-").replace("月", "-").replace("日", "");
                    endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
                } else {
                    String[] timeArr = strTime.split("至");
                    startTime = timeArr[0].replace("年", "-").replace("月", "-").replace("日", "");
                    endTime = timeArr[1].replace("年", "-").replace("月", "-").replace("日", "");
                }
                HtmlPage htmlPage = (HtmlPage) page;
                List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//table[@class=\"new_ZdTab\"][1]/tbody/tr");
                for (HtmlTableRow tr : trs) {
                    String title = tr.getCell(0).asText();
                    if (title.contains("通信量类别名称")) {
                        continue;
                    }
                    CarrierPackageItemInfo carrierPackageItemInfo = new CarrierPackageItemInfo();
                    carrierPackageItemInfo.setItem(tr.getCell(0).asText());//设置名称
                    if (tr.getCell(1).asText().contains("分钟")) {//通话数据
                        carrierPackageItemInfo.setMappingId(carrierInfo.getMappingId());
                        carrierPackageItemInfo.setTotal(tr.getCell(1).asText().replace("分钟", ""));
                        carrierPackageItemInfo.setUsed(tr.getCell(2).asText().replace("分钟", ""));
                        carrierPackageItemInfo.setUnit("分");
                        carrierPackageItemInfo.setBillStartDate(startTime);
                        carrierPackageItemInfo.setBillEndDate(endTime);
                    } else {//流量等记录
                        carrierPackageItemInfo.setMappingId(carrierInfo.getMappingId());
                        carrierPackageItemInfo.setTotal(String.valueOf((int) (Double.parseDouble(tr.getCell(1).asText().replace("M", "")) * 1024)));
                        carrierPackageItemInfo.setUsed(String.valueOf((int) (Double.parseDouble(tr.getCell(2).asText().replace("M", "")) * 1024)));
                        carrierPackageItemInfo.setUnit("KB");
                        carrierPackageItemInfo.setBillStartDate(startTime);
                        carrierPackageItemInfo.setBillEndDate(endTime);
                    }
                    bills.add(carrierPackageItemInfo);
                }
            }
        }
            carrierInfo.setPackages(bills);
            result.setSuccess();
            return result;
        }


    //
    // /**
    // * 解析亲情号码
    // *
    // * @param context
    // * @param cacheContainer
    // * @return
    // * @description
    // * @author heliang
    // * @create 2016-09-02 16:43
    // */
    // public Result userFamilyMemberParse(Context context, CacheContainer
    // cacheContainer, CarrierInfo carrierInfo) {
    // Result result = new Result();
    // return result;
    //
    // }
    //

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
    public Result userRechargeItemInfoParse(Context context, CacheContainer
            cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.RECHARGE_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                String json = page.getWebResponse().getContentAsString();
                JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                List<CarrierUserRechargeItemInfo> rechargeItemInfos = new ArrayList<>();
                for (Object o : jsonArray) {
                    CarrierUserRechargeItemInfo rechargeItemInfo = new CarrierUserRechargeItemInfo();
                    JSONObject object = (JSONObject) o;
                    int payFee = (((Double) (object.getDouble("payFee") * 100)).intValue());
                    String date = DateUtils.dateToString(DateUtils.stringToDate(object.getString("payDate"), "yyyyMMddHHmmss"), "yyyy-MM-dd");
                    rechargeItemInfo.setBillMonth(date.substring(0, 7));
                    rechargeItemInfo.setRechargeTime(DateUtils.dateToString(DateUtils.stringToDate(object.getString("payDate"), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                    rechargeItemInfo.setType(object.getString("payTypeName"));
                    rechargeItemInfo.setAmount(payFee);
                    rechargeItemInfo.setMappingId(carrierInfo.getMappingId());
                    rechargeItemInfos.add(rechargeItemInfo);
                    carrierInfo.getRecharges().add(rechargeItemInfo);
                }
            }
        }
        result.setSuccess();
        return result;

    }
}
