package com.xinyan.spider.isp.mobile.parser.cmcc;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.*;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.PageUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.mobile.model.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:河南移动数据解析
 * @author: jiangmengchen
 * @date: 2017-05-08 15:09
 * @version: v1.0
 */
@Component
public class HeNanCmccParser {

    protected static Logger logger = LoggerFactory.getLogger(HeNanCmccParser.class);

    /**
     * 解析基本信息(机主信息)
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-08 16:43
     */
    public Result basicInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
            //个人基本信息解析
        Page page = cacheContainer.getPage(ProcessorCode.BASIC_INFO.getCode());
        if(page!=null){
            String strBasicData = page.getWebResponse().getContentAsString();
            String name = RegexUtils.matchValue("机主姓名：</span><span class=\"f14px\">(.*)<a style=\\\"color:#BBBB00;text-decoration:none;\\\">", strBasicData).replace("&nbsp;", "").trim();
            String city = RegexUtils.matchValue("地市归属：</span><span class=\"f14px o_num\">(.*)</span></li>", strBasicData).trim();
            String idCard = RegexUtils.matchValue("入网身份证号：</span><span class=\"f14px o_num\">(.*)</span></li>", strBasicData).trim();
            String openTime = RegexUtils.matchValue("入网时间：</span><span class=\"f14px o_num\">(.*)</span></li>", strBasicData).replace("年", "-").replace("月", "-").replace("日", "").trim();
            String taocan = RegexUtils.matchValue("套餐名称：</span><span class=\"f14px\">(.*)</span></li>", strBasicData).trim();
            String userStatus = RegexUtils.matchValue("用户状态： </span><span class=\"fl4px o_num\">(.*)</span></li>", strBasicData).trim();
            String level = RegexUtils.matchValue("星级：.*?</span><spanclass=\"user_info_font\">(.*?)</span>", strBasicData.replaceAll("\\s+","")).trim();
            CarrierUserInfo carrierUserInfo = new CarrierUserInfo();
            if(level.equals("")){
                carrierUserInfo.setLevel("无星");
            }else{
                carrierUserInfo.setLevel(level);
            }
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
            carrierUserInfo.setCity(city);
            carrierUserInfo.setName(name);
            carrierUserInfo.setMappingId(carrierInfo.getMappingId());
            carrierUserInfo.setIdCard(idCard);
            carrierUserInfo.setMobile(context.getUserName());
            carrierUserInfo.setLastModifyTime(DateUtils.dateToString(
                    new Date(), "yyyy-MM-dd HH:mm:ss")); //上次更新时间
            carrierUserInfo.setOpenTime(openTime);
            carrierUserInfo.setProvince("河南");
            carrierUserInfo.setCarrier("中国移动");
            carrierUserInfo.setPackageName(taocan);
            page = cacheContainer.getPage(ProcessorCode.AMOUNT.getCode());
            carrierUserInfo.setAvailableBalance((int)(Double.parseDouble(RegexUtils.matchValue("账户余额：</span>\\s+<span class=\"user_info_num\">(.*)</span>",page.getWebResponse().getContentAsString()))*100));
            carrierInfo.setCarrierUserInfo(carrierUserInfo);
        }

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
     * @create 2017-05-08 16:43
     */
    public Result callRecordParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                HtmlPage htmlPage = (HtmlPage) page;
                List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//tbody[@id='tbody_call']/tr");
                String date = "";
                for (HtmlTableRow tr : trs) {
                    String title = tr.getCell(0).asText();
                    if(title.contains("合计")){
                        break;
                    }
                    if(title.contains("-")){//显示时间栏目
                        date = title;
                    }else{
                        CarrierCallDetailInfo carrierCallDetailInfo = new CarrierCallDetailInfo();
                        carrierCallDetailInfo.setTime(date + " " + tr.getCell(0).asText());
                        carrierCallDetailInfo.setLocation(tr.getCell(1).asText());
                        carrierCallDetailInfo.setMappingId(carrierInfo.getMappingId());
                        carrierCallDetailInfo.setFee((int) (Double.parseDouble(tr.getCell(8).asText()) * 100));
                        carrierCallDetailInfo.setLocationType(tr.getCell(5).asText());
                        carrierCallDetailInfo.setBillMonth(date.substring(0, 7));
                        if(tr.getCell(2).asText().contains("主叫")){
                            carrierCallDetailInfo.setDialType("DIAL");
                        }else{
                            carrierCallDetailInfo.setDialType("DIALED");
                        }
                        int sum=0;
                        String str = tr.getCell(4).asText().replace("秒","");
                        if(str.contains("时")){
                            sum = Integer.parseInt(str.substring(0,str.indexOf('时')))*60*60;
                            str = str.substring(str.indexOf("时")+1);
                        }
                        if (str.contains("分")){
                            sum+= Integer.parseInt(str.substring(0,str.indexOf('分')))*60;
                            str = str.substring(str.indexOf("分")+1);
                        }
                        sum+=Integer.parseInt(str);
                        carrierCallDetailInfo.setDuration(String.valueOf(sum));
                        carrierCallDetailInfo.setPeerNumber(tr.getCell(3).asText());
                        carrierInfo.getCalls().add(carrierCallDetailInfo);
                    }
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
     * @create 2017-05-08 16:43
     */
    public Result smsInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.SMS_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                HtmlPage htmlPage = (HtmlPage) page;
                List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//tbody[@id='tbody_smsAndmms']/tr");
                String date = "";
                for (HtmlTableRow tr : trs) {
                    String title = tr.getCell(0).asText();
                    if(title.contains("合计")){
                        break;
                    }
                    if(title.contains("-")) {//显示时间栏目
                        date = title;
                    }else{
                        CarrierSmsRecordInfo carrierSmsRecordInfo = new CarrierSmsRecordInfo();
                        carrierSmsRecordInfo.setTime(date + " " + tr.getCell(0).asText());
                        carrierSmsRecordInfo.setLocation(tr.getCell(1).asText());
                        carrierSmsRecordInfo.setMappingId(carrierInfo.getMappingId());
                        carrierSmsRecordInfo.setFee((int) (Double.parseDouble(tr.getCell(8).asText()) * 100));
                        carrierSmsRecordInfo.setBillMonth(date.substring(0, 7));
                        if(tr.getCell(4).asText().equals("短信")){
                            carrierSmsRecordInfo.setMsgType("SMS");// 发送类型
                        }else if(tr.getCell(4).asText().equals("彩信")){
                            carrierSmsRecordInfo.setMsgType("MMS");// 发送类型
                        }else{
                            carrierSmsRecordInfo.setMsgType(tr.getCell(4).asText());
                        }
                        if(tr.getCell(3).asText().contains("接")){
                            carrierSmsRecordInfo.setSendType("RECRIVE");// 发送类型
                        }else if(tr.getCell(3).asText().contains("发")){
                            carrierSmsRecordInfo.setSendType("SEND");// 发送类型
                        }else{
                            carrierSmsRecordInfo.setSendType(tr.getCell(3).asText());
                        }
                        carrierSmsRecordInfo.setServiceName(tr.getCell(5).asText());//摩羯数据不对，自渠道正确
                        carrierSmsRecordInfo.setPeerNumber(tr.getCell(2).asText());//修改
                        carrierInfo.getSmses().add(carrierSmsRecordInfo);
                    }
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
     * @create 2017-05-08 16:43
     */
    public Result netInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.NET_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                HtmlPage htmlPage = (HtmlPage) page;
                List<HtmlTableRow> trs = (List<HtmlTableRow>) htmlPage.getByXPath("//tbody[@id=\"tbody_flow\"]/tr");
                String date = "";
                for (HtmlTableRow tr : trs) {
                    String title = tr.getCell(0).asText();
                    if(title.contains("合计")){
                        break;
                    }
                    if(title.contains("-")) {//显示时间栏目
                        date = title;
                    }else{
                        CarrierNetDetailInfo carrierNetDetailInfo = new CarrierNetDetailInfo();
                        carrierNetDetailInfo.setTime(date + " " + tr.getCell(0).asText());
                        carrierNetDetailInfo.setLocation(tr.getCell(1).asText());
                        carrierNetDetailInfo.setNetType(tr.getCell(2).asText());
                        carrierNetDetailInfo.setMappingId(carrierInfo.getMappingId());
                        carrierNetDetailInfo.setFee((int) (Double.parseDouble(tr.getCell(6).asText()) * 100));
                        carrierNetDetailInfo.setBillMonth(date.substring(0, 7));
                        carrierNetDetailInfo.setServiceName(tr.getCell(5).asText());
//                        carrierNetDetailInfo.setSubflow((int) (Double.parseDouble(tr.getCell(3).asText().replace("MB", ".").replace("KB", "")) * 100));//修改
                        int sum=0;
                        String str = tr.getCell(3).asText().replace("KB","");
                        if(str.contains("GB")){
                            sum = Integer.parseInt(str.substring(0,str.indexOf('G')))*1024*1204;
                            str = str.substring(str.indexOf("GB")+2);
                        }
                        if (str.contains("MB")){
                            sum+= Integer.parseInt(str.substring(0,str.indexOf('M')))*1024;
                            str = str.substring(str.indexOf("MB")+2);
                        }
                        sum+=Integer.parseInt(str);
                        carrierNetDetailInfo.setSubflow(sum);
                        carrierNetDetailInfo.setServiceName(tr.getCell(4).asText());
                        carrierInfo.getNets().add(carrierNetDetailInfo);
                    }

                }
            }
        }

        result.setSuccess();
        return result;
    }

    /**
     * 解析账单信息（月账单查询）
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-08 16:43
     */
    public Result billParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.BILL_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                CarrierBillDetailInfo carrierBillDetailInfo = new CarrierBillDetailInfo();
                HtmlPage htmlPage = (HtmlPage) page;
                HtmlTable table = htmlPage.getFirstByXPath("//div[@class='detail_table']/table");
                String voiceFee = RegexUtils.matchValue("套餐外费用\t话音通信费\t(.*" +
                        ")", table.getRow(2).asText());
                carrierBillDetailInfo.setVoiceFee((int) (Double.parseDouble(voiceFee) * 100));
                String netFee = RegexUtils.matchValue("上网费\t(.*)", table.getRow(3).asText());
                carrierBillDetailInfo.setWebFee((int) (Double.parseDouble(netFee) * 100));
                String smsFee = RegexUtils.matchValue("短彩信费\t(.*)", table.getRow(4).asText());
                carrierBillDetailInfo.setSmsFee((int) (Double.parseDouble(smsFee) * 100));
                String totalFee = RegexUtils.matchValue("合计费用\t(.*)", table.getRow(8).asText());
                carrierBillDetailInfo.setActualFee((int) (Double.parseDouble(totalFee) * 100));
                carrierBillDetailInfo.setTotalFee((int) (Double.parseDouble(totalFee) * 100));
                String packageFee = RegexUtils.matchValue("套餐及固定费\t(.*)\t查看详情", table.getRow(1).asText());
                carrierBillDetailInfo.setBaseFee((int) (Double.parseDouble(packageFee) * 100));
                String zengzhiFee = RegexUtils.matchValue("自有增值业务费\t(.*)", table.getRow(5).asText());
                carrierBillDetailInfo.setExtraServiceFee((int) (Double.parseDouble(zengzhiFee) * 100));
                String daishouFee = RegexUtils.matchValue("代收费业务费\t(.*)", table.getRow(6).asText());
                String otherFee = RegexUtils.matchValue("其他费用\t(.*)", table.getRow(7).asText());
                carrierBillDetailInfo.setExtraFee((int) (Double.parseDouble(otherFee) * 100));

                DomText jifen = ((HtmlPage) page).getFirstByXPath("//div[@class='detail_base']/ul/li[3]/span[2]/b/text()");
                DomText dateRande = ((HtmlPage) page).getFirstByXPath("//div[@class='detail_base']/ul/li[3]/span[4]/text()");
                carrierBillDetailInfo.setPoint((int) (Double.parseDouble(jifen.asText()) * 100));
                String startDate = dateRande.asText().split("至")[0];
                String endDate = dateRande.asText().split("至")[1];
                carrierBillDetailInfo.setBillStartDate(startDate);
                carrierBillDetailInfo.setBillEndDate(endDate);
                carrierBillDetailInfo.setBillMonth(startDate.substring(0, 7));
                carrierBillDetailInfo.setMappingId(carrierInfo.getMappingId());
                carrierInfo.getBills().add(carrierBillDetailInfo);
            }
        }

        result.setSuccess();
        return result;
    }

    /**
     * 解析办理业务信息（业务查询，套餐余量）
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-08 16:43
     */
    public Result packageItemInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {//硬代码需要修改
        Result result = new Result();
        Page page = cacheContainer.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
        if(page!=null){
            String strTemp = page.getWebResponse().getContentAsString().replaceAll("\\s+", "");
            String reg = "<divclass=\"detail\"><ul(.*?)<ul><div";
            String inHtml = RegexUtils.matchValue(reg,strTemp);
            reg = "<li><spanclass=\"detail_list_title\"title=.*?>(.*?)</span><spanclass=\"used\">.*?</span><spanclass=\"used_title\">(.*?)<spanclass=\"last\">(.*?)</span>";
            List<List<String>>packageList = RegexUtils.matchesMutiValue(reg,inHtml);//每一个list都是一行套餐数据
            for(List<String> list:packageList){
                if(list.size()!=3){
                    continue;
                }else{
                    if(list.get(1).contains("分")){
                        CarrierPackageItemInfo voice = voice(list);
                        voice.setMappingId(carrierInfo.getMappingId());
                        carrierInfo.getPackages().add(voice);

                    }else{
                        CarrierPackageItemInfo web = web(list);
                        web.setMappingId(carrierInfo.getMappingId());
                        carrierInfo.getPackages().add(web);
                    }
                }
            }
        }
        result.setSuccess();
        return result;
    }

    private CarrierPackageItemInfo web(List<String> list) {

        double total = Double.parseDouble(RegexUtils.matchValue("已用(.*)M", list.get(1))) + Double.parseDouble(RegexUtils.matchValue("剩余约(.*)MB", list.get(2)));
        CarrierPackageItemInfo carrierPackageItemInfo = new CarrierPackageItemInfo();
        carrierPackageItemInfo.setUsed((int)(Double.parseDouble(RegexUtils.matchValue("已用(.*)M", list.get(1)))*1024)+"");
        carrierPackageItemInfo.setTotal((int)(total*1024) + "");//转化为KB
        carrierPackageItemInfo.setItem(list.get(0).replace("：",""));
        carrierPackageItemInfo.setUnit("KB");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        carrierPackageItemInfo.setBillStartDate(DateFormatUtils.format(calendar, "yyyy-MM-dd"));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        carrierPackageItemInfo.setBillEndDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd"));//当月查询设置现在时间
        return carrierPackageItemInfo;
    }

    private CarrierPackageItemInfo voice(List<String> list) {

        double total = Double.parseDouble(RegexUtils.matchValue("已用(.*)分", list.get(1))) + Double.parseDouble(RegexUtils.matchValue("剩余约(.*)分", list.get(2)));
        CarrierPackageItemInfo carrierPackageItemInfo = new CarrierPackageItemInfo();
        carrierPackageItemInfo.setUsed(RegexUtils.matchValue("已用(.*)分", list.get(1)));
        carrierPackageItemInfo.setTotal(total + "");
        carrierPackageItemInfo.setItem(list.get(0).replace("：",""));
        carrierPackageItemInfo.setUnit("分");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        carrierPackageItemInfo.setBillStartDate(DateFormatUtils.format(calendar, "yyyy-MM-dd"));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        carrierPackageItemInfo.setBillEndDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        return carrierPackageItemInfo;
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
        List<Page> pages = cacheContainer.getPages(ProcessorCode.RECHARGE_INFO.getCode());
        if(pages!=null){
            for (Page page : pages) {
                HtmlPage htmlPage = (HtmlPage) page;
                List<HtmlTableRow> tables = (List<HtmlTableRow>) htmlPage.getByXPath("//div[@class='detail_table']/table/tbody/tr");
                for (int i = 1; i <= tables.size() - 1; i++) {
                    String date = tables.get(i).getCell(0).asText().substring(tables.get(i).getCell(0).asText().indexOf("(")+1,tables.get(i).getCell(0).asText().lastIndexOf(")"));
                    String type = tables.get(i).getCell(1).asText();
                    String detail = tables.get(i).getCell(2).asText();
                    String fee = tables.get(i).getCell(3).asText();
                    CarrierUserRechargeItemInfo carrierUserRechargeItemInfo = new CarrierUserRechargeItemInfo();
                    carrierUserRechargeItemInfo.setBillMonth(date.substring(0, 7));
                    carrierUserRechargeItemInfo.setMappingId(carrierInfo.getMappingId());
                    carrierUserRechargeItemInfo.setType(type);
                    carrierUserRechargeItemInfo.setAmount((int) (Double.parseDouble(fee) * 100));
                    carrierUserRechargeItemInfo.setRechargeTime(date);
                    carrierInfo.getRecharges().add(carrierUserRechargeItemInfo);
                }
            }
        }
        result.setSuccess();
        return result;
    }
}

