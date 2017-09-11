package com.xinyan.spider.isp.mobile.parser.cmcc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.mobile.model.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * @Description:江苏移动数据解析
 * @author: jiangmengchen
 * @date: 2017-05-02 14:41
 * @version: v1.0
 */
@Component
public class JiangSuCmccParser {

    protected static Logger logger = LoggerFactory.getLogger(JiangSuCmccParser.class);

    /**
     * 解析基本信息
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
     * @return
     * @description
     * @author jiangmengchen
     * @create 2017-05-02 16:43
     */
    public Result basicInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        logger.info("==>[{}]解析基本信息开始", context.getTaskId());
        HtmlPage page = (HtmlPage) cacheContainer.getPage(ProcessorCode.BASIC_INFO.getCode());
        CarrierUserInfo carrierUserInfo = new CarrierUserInfo();
        if(page!=null){
            List<HtmlScript> scripts = (List<HtmlScript>) page.getByXPath("//script");
            String content = "", cityInfo = "";
            for (HtmlScript htmlScript : scripts) {
                if (htmlScript.toString().contains("userMsg")) {
                    if ("".equals(content)) {
                        content = htmlScript.toString();
                    }
                    continue;
                }
                if (htmlScript.toString().contains("city")) {
                    if ("".equals(cityInfo)) {
                        cityInfo = htmlScript.toString();
                    }
                }
            }
            content = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            JSONObject jsonObject = JSONObject.parseObject(content);

            cityInfo = cityInfo.substring(cityInfo.indexOf("{"), cityInfo.lastIndexOf("}") + 1);
            JSONObject cityObject = JSONObject.parseObject(cityInfo);
            JSONObject resultObj = (JSONObject) jsonObject.get("resultObj");
            JSONObject cityInfoObj = (JSONObject) cityObject.get("resultObj");
            JSONObject userMsg = resultObj.getJSONObject("userMsg");
            carrierUserInfo.setMobile(context.getUserName());
            carrierUserInfo.setOpenTime(DateUtils.dateToString(DateUtils.stringToDate(cityInfoObj.getString("userApplyDate"), "yyyyMMddHHmmss"), "yyyy-MM-dd"));
            carrierUserInfo.setProvince("江苏");
            carrierUserInfo.setAvailableBalance((int) Double.parseDouble(resultObj.getString("accountBalance")) * 100);
            carrierUserInfo.setCarrier("中国移动");
            carrierUserInfo.setLevel(userMsg.getString("agentLevel").equals("") ? "未知" : userMsg.getString("agentLevel"));
            String stateStr = userMsg.getString("userState");
            int state = 0;
            try {
                state = Integer.parseInt(stateStr);
            } catch (Exception e) {
            }
            carrierUserInfo.setState(state);
            carrierUserInfo.setMappingId(carrierInfo.getMappingId());
            carrierUserInfo.setName(userMsg.getString("userName"));
            carrierUserInfo.setCity(cityInfoObj.getString("city"));
        }
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
     * @create 2017-05-02 16:43
     */
    public Result callRecordParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        logger.info("==>[{}]解析通话记录开始", context.getTaskId());
        List<Page> pages = cacheContainer.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
        if (pages != null) {
            for (Page page : pages) {
                JSONObject jsonObject = JSONObject.parseObject(page.getWebResponse().getContentAsString());
                JSONObject resultObj = (JSONObject) jsonObject.get("resultObj");
                JSONObject qryResult = resultObj.getJSONObject("qryResult");
                JSONArray gsmBillDetails = qryResult.getJSONArray("gsmBillDetail");
                for (int i = 1; i <= gsmBillDetails.size() - 1; i++) {
                    if (gsmBillDetails.size() > 1) {
                        JSONObject gsmBillDetail = (JSONObject) gsmBillDetails.get(i);
                        CarrierCallDetailInfo carrierCallDetailInfo = new CarrierCallDetailInfo();
                        carrierCallDetailInfo.setBillMonth(gsmBillDetail.getString("startTime").substring(0, 7));
                        carrierCallDetailInfo.setDialType(gsmBillDetail.getString("statusType").contains("被叫") ? "DIALED" : "DIAL");
                        String callDuration = gsmBillDetail.getString("callDuration");
                        String[] times = RegexUtils.matchMutiValue("(\\d+):(\\d+):(\\d+)", callDuration);
                        int total = Integer.parseInt(times[0]) * 60 + Integer.parseInt((times[1])) * 3600 + Integer.parseInt((times[2]));
                        carrierCallDetailInfo.setDuration(total + "");
                        carrierCallDetailInfo.setFee((int) Double.parseDouble(gsmBillDetail.getString("firstCfee")) * 100);
                        carrierCallDetailInfo.setLocation(gsmBillDetail.getString("visitArear"));
                        carrierCallDetailInfo.setLocationType(gsmBillDetail.getString("roamType"));
                        carrierCallDetailInfo.setPeerNumber(gsmBillDetail.getString("otherParty"));
                        carrierCallDetailInfo.setTime(gsmBillDetail.getString("startTime"));
                        carrierCallDetailInfo.setMappingId(carrierInfo.getMappingId());
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
     * @create 2017-05-02 16:43
     */
    public Result smsInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();
        logger.info("==>[{}]解析短信记录开始", context.getTaskId());
        //短信记录
        List<Page> pages = cacheContainer.getPages(ProcessorCode.SMS_INFO.getCode());
        if (pages != null) {
            for (Page page : pages) {
                JSONObject jsonObject = JSONObject.parseObject(page.getWebResponse().getContentAsString());
                JSONObject resultObj = (JSONObject) jsonObject.get("resultObj");
                JSONObject qryResult = resultObj.getJSONObject("qryResult");
                JSONArray smsBillDetails = qryResult.getJSONArray("smsBillDetail");
                for (int i = 1; i <= smsBillDetails.size() - 1; i++) {
                    if (smsBillDetails.size() > 1) {
                        JSONObject smsBillDetail = (JSONObject) smsBillDetails.get(i);
                        CarrierSmsRecordInfo carrierSmsRecordInfo = new CarrierSmsRecordInfo();
                        carrierSmsRecordInfo.setMappingId(carrierInfo.getMappingId());
                        carrierSmsRecordInfo.setMsgType("SMS");
                        carrierSmsRecordInfo.setSendType(smsBillDetail.getString("statusType").contains("收") ? "RECEIVE" : "SEND");
                        carrierSmsRecordInfo.setTime(smsBillDetail.getString("startTime"));
                        carrierSmsRecordInfo.setBillMonth(smsBillDetail.getString("startTime").substring(0, 7));
                        carrierSmsRecordInfo.setFee((int) Double.parseDouble(smsBillDetail.getString("totalFee")) * 100);
                        carrierSmsRecordInfo.setLocation(smsBillDetail.getString("visitArear"));
                        carrierSmsRecordInfo.setPeerNumber(smsBillDetail.getString("otherParty"));
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
     * @create 2017-05-02 16:43
     */
    public Result netInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();
        logger.info("==>[{}]解析上网记录开始", context.getTaskId());
        List<Page> pages = cacheContainer.getPages(ProcessorCode.NET_INFO.getCode());
        if (pages != null) {
            for (Page page : pages) {
                String content = page.getWebResponse().getContentAsString();
                JSONObject jsonObject = JSONObject.parseObject(content);
                JSONObject resultObj = (JSONObject) jsonObject.get("resultObj");
                JSONObject qryResult = (JSONObject) resultObj.get("qryResult");
                JSONArray gprsBillDetails = qryResult.getJSONArray("gprsBillDetail");
                for (int i = 1; i <= gprsBillDetails.size() - 1; i++) {
                    if (gprsBillDetails.size() > 1) {
                        JSONObject object = (JSONObject) gprsBillDetails.get(i);
                        CarrierNetDetailInfo carrierNetDetailInfo = new CarrierNetDetailInfo();
                        carrierNetDetailInfo.setBillMonth(object.getString("startTime").substring(0, 7));
                        carrierNetDetailInfo.setMappingId(carrierInfo.getMappingId());
                        carrierNetDetailInfo.setDuration(object.getInteger("duration"));
                        carrierNetDetailInfo.setFee((int) Double.parseDouble(object.getString("totalFee")) * 100);
                        carrierNetDetailInfo.setLocation(object.getString("visitArear"));
                        carrierNetDetailInfo.setNetType(object.getString("cdrApnni"));
                        carrierNetDetailInfo.setServiceName(object.getString("msnc"));
                        carrierNetDetailInfo.setSubflow(object.getInteger("busyData"));
                        carrierNetDetailInfo.setTime(object.getString("startTime"));
                        carrierInfo.getNets().add(carrierNetDetailInfo);
                    }
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
     * @create 2017-05-02 16:43
     */
    public Result billParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        logger.info("==>[{}]解析账单信息开始", context.getTaskId());
        List<Page> pages = cacheContainer.getPages(ProcessorCode.BILL_INFO.getCode());
        if (pages != null) {
            for (Page page : pages) {
                JSONObject jsonObject = JSONObject.parseObject(page.getWebResponse().getContentAsString());
                JSONObject resultObj = (JSONObject) jsonObject.get("resultObj");
                JSONArray feeDetailList = resultObj.getJSONObject("billBean").getJSONObject("billRet").getJSONArray("feeDetailList");
                CarrierBillDetailInfo carrierBillDetailInfo = new CarrierBillDetailInfo();
                String dateRange = resultObj.getJSONObject("billBean").getString("feeTime");
                String[] timeArr = dateRange.split(" 至 ");
                String startTime = timeArr[0].replace("年", "-").replace("月", "-").replace("日", "");
                String endTime = timeArr[1].replace("年", "-").replace("月", "-").replace("日", "");
                carrierBillDetailInfo.setBillMonth(startTime.substring(0, 7));
                carrierBillDetailInfo.setBillStartDate(startTime);
                carrierBillDetailInfo.setBillEndDate(endTime);
                for (Object o : feeDetailList) {
                    carrierBillDetailInfo.setMappingId(carrierInfo.getMappingId());
                    carrierBillDetailInfo.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(resultObj.getString("searchMonth"), "yyyyMM"), "yyyy-MM"));
                    JSONObject feeDetail = (JSONObject) o;
                    switch (feeDetail.getString("feeTypeId")) {
                        case "AA":
                            carrierBillDetailInfo.setBaseFee(feeDetail.getInteger("fee"));
                            break;
                        case "AE":
                            carrierBillDetailInfo.setExtraServiceFee(feeDetail.getInteger("fee"));
                            break;
                    }
                }
                carrierBillDetailInfo.setTotalFee(resultObj.getJSONObject("billBean").getJSONObject("billRet").getInteger("totalFee"));
                carrierInfo.getBills().add(carrierBillDetailInfo);
            }
        }
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
     * @author jiangmengchen
     * @create 2017-05-02 16:43
     */
    public Result packageItemInfoParse(Context context, CacheContainer
            cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        logger.info("==>[{}]解析套餐信息开始", context.getTaskId());
        HtmlPage page = (HtmlPage) cacheContainer.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
        if(page!=null){
            HtmlScript htmlScript = (HtmlScript) page.getByXPath("//script[last()]").get(1);
            String content = htmlScript.toString();
            content = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            JSONObject jsonObject = JSONObject.parseObject(content);
            JSONObject resultObj = (JSONObject) jsonObject.get("resultObj");
            JSONArray keyPkgInfo = resultObj.getJSONArray("KEY_PkgInfo");
            for (Object o : keyPkgInfo) {
                JSONObject group = (JSONObject) o;
                JSONArray subUsedInfoList = group.getJSONArray("subUsedInfoList");
                for (Object object : subUsedInfoList) {
                    JSONObject subUsedInfo = (JSONObject) object;
                    CarrierPackageItemInfo carrierPackageItemInfo = new CarrierPackageItemInfo();
                    carrierPackageItemInfo.setItem(group.getString("pkgName") + "-" + subUsedInfo.getString("pkgName"));
                    carrierPackageItemInfo.setTotal(subUsedInfo.getString("total"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                    carrierPackageItemInfo.setBillStartDate(transDateBegin);
                    carrierPackageItemInfo.setBillEndDate(transDateEnd);
                    carrierPackageItemInfo.setMappingId(carrierInfo.getMappingId());
                    carrierPackageItemInfo.setUnit(subUsedInfo.getString("pkgName").contains("通话?") ? "分钟" : "KB");
                    carrierPackageItemInfo.setUsed(subUsedInfo.getInteger("total") - subUsedInfo.getInteger("remain") + "");
                    carrierInfo.getPackages().add(carrierPackageItemInfo);
                }
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
     * @create 2017-05-02 16:43
     */
    public Result userRechargeItemInfoParse(Context context, CacheContainer
            cacheContainer, CarrierInfo carrierInfo) {
        Result result = new Result();
        logger.info("==>[{}]解析充值记录开始", context.getTaskId());
        Page page = cacheContainer.getPage(ProcessorCode.RECHARGE_INFO.getCode());
        if (page != null) {
            JSONObject jsonObject = JSONObject.parseObject(page.getWebResponse().getContentAsString());
            JSONObject resultObj = (JSONObject) jsonObject.get("resultObj");
            JSONArray array = resultObj.getJSONArray("czList");
            for (Object o : array) {
                JSONObject object = (JSONObject) o;
                CarrierUserRechargeItemInfo carrierUserRechargeItemInfo = new CarrierUserRechargeItemInfo();
                carrierUserRechargeItemInfo.setMappingId(carrierInfo.getMappingId());
                carrierUserRechargeItemInfo.setAmount(object.getInteger("payMoney"));
                carrierUserRechargeItemInfo.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(object.getString("payTime"), "yyyyMMddHHmmss"), "yyyy-MM"));
                carrierUserRechargeItemInfo.setRechargeTime(DateUtils.dateToString(DateUtils.stringToDate(object.getString("payTime"), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                carrierUserRechargeItemInfo.setType(object.getString("payMode"));
                carrierInfo.getRecharges().add(carrierUserRechargeItemInfo);
            }
        }
        result.setSuccess();
        return result;
    }
}
