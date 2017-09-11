package com.xinyan.spider.isp.mobile.parser.cmcc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.common.utils.UnitTrans;
import com.xinyan.spider.isp.mobile.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heliang
 * @author heliang
 * @version V1.0
 * @description
 * @date 2016年8月25日 下午1:41:05
 */
@Component
public class HuNanCmccParser {
    protected static Logger logger = LoggerFactory.getLogger(HuNanCmccParser.class);

    /**
     * 解析基本信息
     *
     * @param context
     * @param cacheContainer
     * @param carrierInfo
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
            carrierInfo.getCarrierUserInfo().setMappingId(carrierInfo.getMappingId()); // 映射id
            carrierInfo.getCarrierUserInfo().setMobile(context.getUserName()); // 手机号码
            carrierInfo.getCarrierUserInfo().setName(RegexUtils.matchValue("\"name\":\"(.*?)\"", strBasicData)); // 姓名
            carrierInfo.getCarrierUserInfo().setCarrier("CHINA_MOBILE"); // 运营商
            // CHINA_MOBILE
            // 中国移动
            // CHINA_TELECOM
            // 中国电信
            // CHINA_UNICOM
            // 中国联通
            carrierInfo.getCarrierUserInfo().setIdCard(context.getIdCard()); // 证件号
            carrierInfo.getCarrierUserInfo().setProvince("湖南"); // 所属省份
            carrierInfo.getCarrierUserInfo().setAddress(RegexUtils.matchValue("\"address\":\"(.*?)\"", strBasicData)); // 地址
            carrierInfo.getCarrierUserInfo().setLevel(RegexUtils.matchValue("\"starLevel\":\"(.*?)\"", strBasicData)); // 星级
            carrierInfo.getCarrierUserInfo()
                    .setOpenTime(DateUtils.dateToString(DateUtils
                                    .stringToDate(RegexUtils.matchValue("\"inNetDate\":\"(.*?)\"", strBasicData), "yyyyMMddHHmmss"),
                            "yyyy-MM-dd")); // 入网时间，格式：yyyy-MM-dd
            carrierInfo.getCarrierUserInfo()
                    .setState("00".equals(RegexUtils.matchValue("\"status\":\"(.*?)\"", strBasicData)) ? 0 : -1); // 帐号状态,
            // -1未知
            // 0正常
            // 1单向停机
            // 2停机
            // 3预销户
            // 4销户
            // 5过户
            // 6改号
            // 99号码不存在
            carrierInfo.getCarrierUserInfo()
                    .setLastModifyTime(DateUtils.dateToString(DateUtils
                                    .stringToDate(RegexUtils.matchValue("\"sOperTime\":\"(.*?)\"", strBasicData), "yyyyMMddHHmmss"),
                            "yyyy-MM-dd HH:mm:ss")); // 最近一次更新时间，格式: yyyy-MM-dd
            // HH:mm:ss


            page = cacheContainer.getPage(ProcessorCode.OTHER_INFO.getCode());
            strBasicData = page.getWebResponse().getContentAsString();
            carrierInfo.getCarrierUserInfo().setCity(RegexUtils.matchValue("\"id_name_cd\":\"(.*?)\"", strBasicData));
            // 余额、星级
            page = cacheContainer.getPage(ProcessorCode.AMOUNT.getCode());
            strBasicData = page.getWebResponse().getContentAsString();
            carrierInfo.getCarrierUserInfo().setAvailableBalance(
                    (int) (Double.parseDouble(RegexUtils.matchValue("\"curFee\":\"(.*?)\"", strBasicData)) * 100));
            // 套餐
            page = cacheContainer.getPage(ProcessorCode.POINTS_VALUE.getCode());
            strBasicData = page.getWebResponse().getContentAsString();
            carrierInfo.getCarrierUserInfo()
                    .setPackageName(RegexUtils.matchValue("\"curPlanName\":\"(.*?)\"", strBasicData)); // 套餐
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
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result callRecordParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();

        // 通话记录
        List<CarrierCallDetailInfo> calls = new ArrayList<>();
        List<Page> pages = cacheContainer.getPages(ProcessorCode.CALLRECORD_INFO.getCode());
        if (pages != null) {
            for (Page page : pages) {
                String strTemp = OpeStrToJson(page.getWebResponse().getContentAsString());
                JSONObject jsonObject = JSONObject.parseObject(strTemp);
                JSONArray callDataArr = jsonObject.getJSONArray("data");
                if (callDataArr != null) {
                    for (int i = 0; i < callDataArr.size(); i++) {
                        JSONObject callData = (JSONObject) callDataArr.get(i);
                        CarrierCallDetailInfo carrierCallDetailInfo = new CarrierCallDetailInfo();
                        carrierCallDetailInfo.setMappingId(carrierInfo.getMappingId());
                        carrierCallDetailInfo.setBillMonth(DateUtils.dateToString(
                                DateUtils.stringToDate(jsonObject.getString("startDate"), "yyyyMMdd"), "yyyy-MM")); // 通话月份，格式：yyyy-MM
                        carrierCallDetailInfo.setPeerNumber(callData.getString("anotherNm"));// 对方号码
                        carrierCallDetailInfo.setLocation(callData.getString("commPlac")); // 通话地(自己的)
                        carrierCallDetailInfo.setTime(callData.getString("startTime")); // 通话时间
                        carrierCallDetailInfo.setLocationType(callData.getString("commType")); // 通话地类型.
                        // e.g.省内漫游
                        String hour = RegexUtils.matchValue("(\\d+)时", callData.getString("commTime"));
                        String min = RegexUtils.matchValue("(\\d+)分", callData.getString("commTime"));
                        String sec = RegexUtils.matchValue("(\\d+)秒", callData.getString("commTime"));
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
                        carrierCallDetailInfo.setDuration(finalDuration + ""); // 通话时长(单位:秒)
                        carrierCallDetailInfo.setFee((int) Double.parseDouble(callData.getString("commFee")) * 100); // 费用
                        calls.add(carrierCallDetailInfo);
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
     * @param carrierInfo
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
        if (pages != null) {
            for (Page page : pages) {
                String strTemp = OpeStrToJson(page.getWebResponse().getContentAsString());
                JSONObject jsonObject = JSONObject.parseObject(strTemp);
                JSONArray smsDataArr = jsonObject.getJSONArray("data");
                if (smsDataArr != null) {
                    for (int i = 0; i < smsDataArr.size(); i++) {
                        JSONObject smsData = (JSONObject) smsDataArr.get(i);
                        CarrierSmsRecordInfo carrierSmsRecordInfo = new CarrierSmsRecordInfo();
                        carrierSmsRecordInfo.setMappingId(carrierInfo.getMappingId());
                        carrierSmsRecordInfo.setBillMonth(DateUtils.dateToString(
                                DateUtils.stringToDate(jsonObject.getString("startDate"), "yyyyMMdd"), "yyyy-MM")); // 通话月份，格式：yyyy-MM
                        carrierSmsRecordInfo.setTime(smsData.getString("startTime")); // 发送时间
                        carrierSmsRecordInfo.setPeerNumber(smsData.getString("anotherNm"));// 对方号码
                        carrierSmsRecordInfo.setLocation(smsData.getString("commPlac")); // 通话地(自己的)
                        carrierSmsRecordInfo.setSendType(smsData.getString("commMode").contains("接受?") ? "RECEIVE" : "SEND");//SEND-发送; RECEIVE-收取
                        carrierSmsRecordInfo.setMsgType(smsData.getString("infoType").contains("短信") ? "SMS" : "MMS");//SMS-短信; MMS-彩信
                        carrierSmsRecordInfo.setServiceName(smsData.getString("busiName"));//业务名称. e.g. 点对点(网内)
                        carrierSmsRecordInfo.setFee((int) (Double.parseDouble(smsData.getString("commFee")) * 100));//通话费(单位:分)
                        smses.add(carrierSmsRecordInfo);
                    }
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

        if (pages != null) {
            for (Page page : pages) {
                String strTemp = OpeStrToJson(page.getWebResponse().getContentAsString());
                JSONObject jsonObject = JSONObject.parseObject(strTemp);
                JSONArray netDataArr = jsonObject.getJSONArray("data");
                if (netDataArr != null) {
                    for (int i = 0; i < netDataArr.size(); i++) {
                        JSONObject netData = (JSONObject) netDataArr.get(i);
                        CarrierNetDetailInfo carrierNetDetailInfo = new CarrierNetDetailInfo();
                        carrierNetDetailInfo.setMappingId(carrierInfo.getMappingId());
                        carrierNetDetailInfo.setBillMonth(DateUtils.dateToString(
                                DateUtils.stringToDate(jsonObject.getString("startDate"), "yyyyMMdd"), "yyyy-MM")); // 通话月份，格式：yyyy-MM
                        carrierNetDetailInfo.setTime(
                                carrierNetDetailInfo.getBillMonth().substring(0, 4) + "-" + netData.getString("startTime")); // 上网时间
                        String[] durationArr = netData.getString("commTime").split(":");
                        int duration = 0;
                        if (durationArr.length == 3) {
                            duration = Integer.parseInt(durationArr[0]) * 60 * 60 + Integer.parseInt(durationArr[1]) * 60
                                    + Integer.parseInt(durationArr[2]);
                        } else if (durationArr.length == 2) {
                            duration = Integer.parseInt(durationArr[0]) * 60 + Integer.parseInt(durationArr[2]);
                        } else {
                            String hour = RegexUtils.matchValue("(\\d+)时", durationArr[0]);
                            String min = RegexUtils.matchValue("(\\d+)分", durationArr[0]);
                            String sec = RegexUtils.matchValue("(\\d+)秒", durationArr[0]);
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
                            duration = finalDuration;
                        }
                        carrierNetDetailInfo.setDuration(duration); // 上网时长
                        carrierNetDetailInfo.setSubflow((int) Double.parseDouble(netData.getString("sumFlow")));// 流量使用量，单位:KB
                        carrierNetDetailInfo.setLocation(netData.getString("commPlac"));// 流量使用地点
                        carrierNetDetailInfo.setNetType(netData.getString("netType"));// 网络类型
                        carrierNetDetailInfo.setServiceName(netData.getString("meal"));// 业务名称.
                        // e.g.
                        // 点对点(网内)
                        carrierNetDetailInfo.setFee((int) Double.parseDouble(netData.getString("commFee")) * 100);// 通话费(单位:分)
                        nets.add(carrierNetDetailInfo);
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
     * @param carrierInfo
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    public Result billParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

        Result result = new Result();

        List<CarrierBillDetailInfo> bills = new ArrayList<>();
        Page page = cacheContainer.getPage(ProcessorCode.BILL_INFO.getCode());
        if(page!=null){
            String strTemp = OpeStrToJson(page.getWebResponse().getContentAsString());
            JSONObject jsonObject = JSONObject.parseObject(strTemp);
            JSONArray billDataArr = jsonObject.getJSONArray("data");
            if(billDataArr != null) {
                for (int i = 0; i < billDataArr.size(); i++) {
                    JSONObject billData = (JSONObject) billDataArr.get(i);
                    CarrierBillDetailInfo carrierBillDetailInfo = new CarrierBillDetailInfo();
                    carrierBillDetailInfo.setMappingId(carrierInfo.getMappingId());
                    JSONArray billMaterialsArr = billData.getJSONArray("billMaterials");
                    if (billMaterialsArr.size() > 0) {
                        carrierBillDetailInfo.setBillMonth(DateUtils
                                .dateToString(DateUtils.stringToDate(billData.getString("billMonth"), "yyyyMM"), "yyyy-MM"));// 账单月，格式：yyyy-MM
                        carrierBillDetailInfo.setBillStartDate(DateUtils.dateToString(
                                DateUtils.stringToDate(billData.getString("billStartDate"), "yyyyMMdd"), "yyyy-MM-dd"));// 账期起始日期，格式：yyyy-MM-dd
                        carrierBillDetailInfo.setBillEndDate(DateUtils
                                .dateToString(DateUtils.stringToDate(billData.getString("billEndDate"), "yyyyMMdd"), "yyyy-MM-dd"));// 账期结束日期，格式：yyyy-MM-dd
                        carrierBillDetailInfo.setBaseFee(
                                (int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(0)).getString("billEntriyValue"))
                                        * 100));// 套餐及固定费 单位分
                        carrierBillDetailInfo.setExtraServiceFee(
                                (int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(4)).getString("billEntriyValue"))
                                        * 100));// 增值业务费 单位分
                        carrierBillDetailInfo.setVoiceFee(
                                (int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(1)).getString("billEntriyValue"))
                                        * 100));// 语音费 单位分
                        carrierBillDetailInfo.setSmsFee(
                                (int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(3)).getString("billEntriyValue"))
                                        * 100));// 短彩信费 单位分
                        carrierBillDetailInfo.setWebFee(
                                (int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(2)).getString("billEntriyValue"))
                                        * 100));// 网络流量费 单位分
                        carrierBillDetailInfo.setExtraFee(
                                (int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(6)).getString("billEntriyValue"))
                                        * 100));// 其它费用 单位分
                        carrierBillDetailInfo.setTotalFee((int) (Double.parseDouble(billData.getString("billFee")) * 100));// 总费用
                        // 单位分
                        carrierBillDetailInfo.setDiscount(
                                (int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(7)).getString("billEntriyValue"))
                                        * 100));// 优惠费 单位分
                        carrierBillDetailInfo.setExtraDiscount(
                                (int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(8)).getString("billEntriyValue"))
                                        * 100));// 其它优惠 单位分
                        carrierBillDetailInfo.setActualFee((int) (Double.parseDouble(billData.getString("billFee")) * 100));// 个人实际费用
                        // 单位分
                        bills.add(carrierBillDetailInfo);
                    }
                }
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
        Page page = cacheContainer.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
        if(page!=null){
            JSONObject packageItemInfoObj = JSONObject.parseObject(page.getWebResponse().getContentAsString());
            JSONArray dataArr = packageItemInfoObj.getJSONArray("data");
            if(dataArr != null) {
                JSONArray arrArr = ((JSONObject) dataArr.get(0)).getJSONArray("arr");
                for (Object object : arrArr) {
                    CarrierPackageItemInfo packageItemInfo = new CarrierPackageItemInfo();
                    packageItemInfo.setMappingId(carrierInfo.getMappingId());
                    JSONObject jsonObject = (JSONObject) object;
                    packageItemInfo.setItem(jsonObject.getString("mealName"));   //套餐项目名称
                    JSONObject resInfos = (JSONObject) jsonObject.getJSONArray("resInfos").get(0);
                    JSONObject secResInfos = (JSONObject) resInfos.getJSONArray("secResInfos").get(0);
                    JSONObject resConInfo = secResInfos.getJSONObject("resConInfo");
                    packageItemInfo.setUsed(resConInfo.getString("useMeal")); //项目已使用量
                    packageItemInfo.setUnit(UnitTrans.tran(resConInfo.getString("unit")));//单位：语音-分; 流量-KB; 短/彩信-条
                    packageItemInfo.setTotal(resConInfo.getString("totalMeal"));
                    packageItemInfo.setBillStartDate(DateUtils.getFirstDay("yyyy-MM-dd", 0));
                    packageItemInfo.setBillEndDate(DateUtils.getLastDay("yyyy-MM-dd", 0));
                    packageItemInfos.add(packageItemInfo);
                }
            }
        }
        carrierInfo.setPackages(packageItemInfos);
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
        Page page = cacheContainer.getPage(ProcessorCode.RECHARGE_INFO.getCode());
        if (page != null) {
            JSONObject packageItemInfoObj = JSONObject.parseObject(page.getWebResponse().getContentAsString());
            JSONArray dataArr = packageItemInfoObj.getJSONArray("data");
            if(dataArr != null) {
                for (Object object : dataArr) {
                    CarrierUserRechargeItemInfo rechargeItemInfo = new CarrierUserRechargeItemInfo();
                    rechargeItemInfo.setMappingId(carrierInfo.getMappingId());
                    JSONObject jsonObject = (JSONObject) object;
                    rechargeItemInfo.setAmount((int) (Double.parseDouble(jsonObject.getString("payFee")) * 100));//充值金额(单位: 分)
                    rechargeItemInfo.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(jsonObject.getString("payDate"), "yyyyMMddHHmmss"), "yyyy-MM"));//充值月份，格式：yyyy-MM
                    rechargeItemInfo.setType(jsonObject.getString("payTypeName"));//充值方式. e.g. 现金
                    rechargeItemInfo.setRechargeTime(DateUtils.dateToString(DateUtils.stringToDate(jsonObject.getString("payDate"), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));//充值时间，格式：yyyy-MM-dd HH:mm:ss
                    rechargeItemInfos.add(rechargeItemInfo);
                }
            }
            carrierInfo.setRecharges(rechargeItemInfos);
        }
        result.setSuccess();
        return result;

    }


    /**
     * 处理字符串成json格式
     *
     * @param str
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 16:43
     */
    private static String OpeStrToJson(String str) {

        String opeStr = "";

        String strOpe = "jQuery18308911999785481797_1494312492711(";
        if (StringUtils.isNotBlank(str) && StringUtils.contains(str, "(")) {
            opeStr = str.substring(strOpe.length(), str.length() - 1);
        } else {
            opeStr = str;
        }

        return opeStr;

    }
}
