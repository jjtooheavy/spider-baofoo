package com.xinyan.spider.isp.mobile.processor.unicom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.Constants.HistoryBill;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.*;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * 联通处理类
 *
 * @author heliang
 * @version V1.0
 * @description
 * @date 2016年8月8日 下午4:33:47
 */
@Component
public class ChinaUnicomPssor extends AbstractProcessor {

    protected static Logger logger = LoggerFactory.getLogger(ChinaUnicomPssor.class);
    private static final String BASE_URL = "https://uac.10010.com";
    private static final String ISERVICE_URL = "http://iservice.10010.com";
    private static final String LOGOUT_URL = "http://www.10010.com/mall-web/Index/logout";


    /**
     * 多种登录模式经处理后，最后统一调用登录
     *
     * @param webClient
     * @param context
     * @return
     */
    @Override
    public Result doLogin(WebClient webClient, Context context) {
        Result result = new Result();
        try {
            //2.登陆
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("callback", "jQuery1720002017596467626004_1470655440601"));
            reqParam.add(new NameValuePair("req_time", Calendar.getInstance().getTimeInMillis() + ""));
            reqParam.add(new NameValuePair("redirectURL", "http://www.10010.com"));
            reqParam.add(new NameValuePair("userName", context.getUserName()));
            reqParam.add(new NameValuePair("password", context.getPassword()));
            reqParam.add(new NameValuePair("pwdType", "01"));
            reqParam.add(new NameValuePair("productType", "01"));
            reqParam.add(new NameValuePair("redirectType", "01"));
            reqParam.add(new NameValuePair("rememberMe", "1"));
            reqParam.add(new NameValuePair("_", Calendar.getInstance().getTimeInMillis() + ""));

            Page resultPage = getPage(webClient, BASE_URL + "/portal/Service/MallLogin", HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

            if (null != resultPage) {
                //登录失败
                result.setFail();

                String msg = resultPage.getWebResponse().getContentAsString();
                if (StringUtils.contains(msg, "0000")) {//登陆成功
                    result.setSuccess();
                    return new Result(StatusCode.联通登入超时);
                } else if (StringUtils.contains(msg, "不支持初始密码登录")) {//不支持初始密码登录
                    return new Result(StatusCode.不支持初始密码登录);
                } else if (StringUtils.contains(msg, "不支持简单密码登录")) {//不支持简单密码登录
                    return new Result(StatusCode.不支持简单密码登录);
                } else if (StringUtils.contains(msg, "用户名或密码不正确")) {//用户名或密码不正确
                    return new Result(StatusCode.用户名或密码错误);
                } else if (StringUtils.contains(msg, "登录密码出错已达上限")) {//登录密码出错已达上限
                    return new Result(StatusCode.登录密码出错已达上限);
                } else if (StringUtils.contains(msg, "7009") || StringUtils.contains(msg, "7211")) {//系统忙，请稍后再试
                    return new Result(StatusCode.系统忙);
                }
            } else {
                //登录失败
                return new Result(StatusCode.登陆失败);
            }
        } catch (FailingHttpStatusCodeException e) {
            logger.error("==>[{}]联通官网繁忙", context.getTaskId(), e);
            result.setResult(StatusCode.官网系统繁忙);
            return result;
        }catch (Exception e) {
            logger.error("==>[{}]登录出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.登陆出错);
            return result;
        } finally {
            //设置回调
            if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())) {//不需要回调
                sendLoginMsg(result, context, false, webClient);
            }
            close(webClient);
        }
        return result;
    }

    /**
     * 短信验证登录
     *
     * @param webClient
     * @param context
     * @return
     */
    @Override
    public Result doLoginBySMS(WebClient webClient, Context context) {
        Result result = new Result();
        try {
            //2.登陆
            List<NameValuePair> reqParam = new ArrayList<>();

            reqParam.add(new NameValuePair("inputcode", context.getUserInput()));
            reqParam.add(new NameValuePair("menuId", "000100030001"));
            Page resultPage = getPage(webClient, ISERVICE_URL + "/e3/static/query/verificationSubmit?_=" + Calendar.getInstance().getTimeInMillis() + "&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            JSONObject info = JSONObject.parseObject(resultPage.getWebResponse().getContentAsString());
            if ("00".equals(info.getString("flag"))) {
                result.setSuccess();
            } else {
                //登录失败
                return new Result(StatusCode.验证短信验证码错误);
            }
        } catch (Exception e) {
            logger.error("==>[{}]登录出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.验证短信验证码异常);
            return result;
        } finally {
            sendLoginMsg(result, context, false, webClient);
            close(webClient);
        }
        return result;
    }


    @Override
    public Result doCrawler(WebClient webClient, Context context) {
        //页面缓存
        CacheContainer cacheContainer = new CacheContainer();
        CarrierInfo carrierInfo = new CarrierInfo();
        context.setMappingId(carrierInfo.getMappingId());
        Result result = new Result();
        //2.解析授权信息
        result.setData(carrierInfo);
        BeanUtils.copyProperties(context, carrierInfo);
        //2.采集基础信息
        getLogger().info("==>[{}]1.采集基本信息开始.....", context.getTaskId());
        try {
            result = processBaseInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]1.采集基本信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集基础信息失败
                return result;
            }

            //3.采集通话详单
            getLogger().info("==>[{}]2.采集通话详单开始.....", context.getTaskId());
            result = processCallRecordInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]2.采集通话详单结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集通话详单失败
                return result;
            }

            //4.采集短信记录
            getLogger().info("==>[{}]3.采集短信记录开始.....", context.getTaskId());
            result = processSmsInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]3.采集短信记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集短信记录失败
                return result;
            }

            //5.采集上网记录
            getLogger().info("==>[{}]4.采集上网记录开始.....", context.getTaskId());
            result = processNetInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]4.采集上网记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集上网记录失败
                return result;
            }

            //6.采集账单信息
            getLogger().info("==>[{}]5.采集账单信息开始.....", context.getTaskId());
            result = processBillInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]5.采集账单信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集账单信息失败
                return result;
            }
            //7.采集套餐信息
            getLogger().info("==>[{}]6.采集套餐信息开始.....", context.getTaskId());
            result = processPackageItemInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]6.采集套餐信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }
            //8.采集亲情号码
            getLogger().info("==>[{}]7.采集亲情号码开始.....", context.getTaskId());
            result = processUserFamilyMember(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]7.采集亲情号码结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }
            //9.采集充值记录
            getLogger().info("==>[{}]8.采集充值记录开始.....", context.getTaskId());
            result = processUserRechargeItemInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]8.采集充值记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }

            try {
                getLogger().info("==>[{}]8.退出登陆开始.....", context.getTaskId());
                loginout(webClient);
                getLogger().info("==>[{}]8.退出登陆结束.", context.getTaskId());
                if (!result.isSuccess()) { //解析办理业务信息失败
                    return result;
                }
                result.setData(carrierInfo);
            } catch (Exception e) {
                //退出出错不影响主流程
                logger.error("==>[{}]9.解析数据异常:[{}]", context.getTaskId(), e);
            }
        } catch (Exception ex) {
            logger.error("==>[{}]数据抓取出现异常:[{}]", context.getTaskId(), ex);
            result.setFail();
            return result;
        }finally {
            sendUpdateLog(result, context);
            sendAnalysisMsg(result, context);
            close(webClient);
        }
        getLogger().info("==>[{}]0.数据抓取结束{}\n,详情:{}", context.getTaskId(), result, result.getData());
        return result;
    }


    @Override
    public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("_", Calendar.getInstance().getTimeInMillis() + ""));

            final Page resultPage = getPage(webClient, ISERVICE_URL + "/e3/static/check/checklogin", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            if (null != resultPage) {//操作成功
                JSONObject info = JSONObject.parseObject(resultPage.getWebResponse().getContentAsString());
                if (info.getBooleanValue("isLogin")) {
                    logger.info("==>[{}]1.1解析个人信息开始.....", context.getTaskId());
                    carrierInfo.getCarrierUserInfo().setCarrier("CHINA_UNICOM");
                    JSONObject userInfo = info.getJSONObject("userInfo");
                    carrierInfo.getCarrierUserInfo().setName(userInfo.getString("custName"));//真实姓名
                    carrierInfo.getCarrierUserInfo().setMobile(context.getUserName());//本机号码
                    String opendate = userInfo.getString("opendate");
                    String openDate = null;
                    try {
                        openDate = DateUtils.dateToString(DateUtils.stringToDate(opendate.substring(0, 8), DateUtils.PATTERN2), DateUtils.PATTERN1);//入网时间
                    } catch (Exception e) {
                        logger.error("==>[{}]日期转换出错了", context.getTaskId());
                    }
                    carrierInfo.getCarrierUserInfo().setOpenTime(openDate);
                    carrierInfo.getCarrierUserInfo().setIdCard(userInfo.getString("certnum"));//证件号码（身份证号）
                    carrierInfo.getCarrierUserInfo().setPackageName(userInfo.getString("packageName"));//套餐名称
                    carrierInfo.getCarrierUserInfo().setProvince(context.getTaskSubType().split("_")[2]);

                    carrierInfo.getCarrierUserInfo().setCity(context.getTaskSubType().split("_")[1]);

                    carrierInfo.getCarrierUserInfo().setLevel(userInfo.getString("custlvl"));//用户等级（星级）
                    if (!StringUtils.isEmpty(userInfo.getString("laststatdate"))) {
                        carrierInfo.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(DateUtils.stringToDate(userInfo.getString("laststatdate"), DateUtils.PATTERN3), DateUtils.PATTERN7));
                    }
                    if (Constants.NOT_EXIST.equals(userInfo.getString("status"))) {
                        carrierInfo.getCarrierUserInfo().setState(99);
                    } else if (Constants.NORMAL.contains(userInfo.getString("status"))) {
                        carrierInfo.getCarrierUserInfo().setState(0);
                    } else if (Constants.UNIDIRECTIONAL_STOP.equals(userInfo.getString("status"))) {
                        carrierInfo.getCarrierUserInfo().setState(1);
                    } else if (Constants.STOP.equals(userInfo.getString("status"))) {
                        carrierInfo.getCarrierUserInfo().setState(2);
                    } else if (Constants.PRE_CANCELLATION.equals(userInfo.getString("status"))) {
                        carrierInfo.getCarrierUserInfo().setState(3);
                    } else if (Constants.CANCELLATION.equals(userInfo.getString("status"))) {
                        carrierInfo.getCarrierUserInfo().setState(4);
                    } else if (Constants.TRANSFER.equals(userInfo.getString("status"))) {
                        carrierInfo.getCarrierUserInfo().setState(5);
                    } else if (Constants.CHANGE.equals(userInfo.getString("status"))) {
                        carrierInfo.getCarrierUserInfo().setState(6);
                    } else {
                        carrierInfo.getCarrierUserInfo().setState(-1);
                    }
                    logger.info("==>[{}]1.1解析个人信息结束.", context.getTaskId());
                } else {
                    return new Result(StatusCode.解析基础信息出错);
                }
            } else {
                return new Result(StatusCode.解析基础信息出错);
            }

            final Page headerView = getPage(webClient, ISERVICE_URL + "/e3/static/query/headerView", HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            if (null != headerView) {//操作成功
                JSONObject headerViewInfo = JSONObject.parseObject(headerView.getWebResponse().getContentAsString());
                if (headerViewInfo.getBooleanValue("issuccess")) {
                    logger.info("==>[{}]1.2解析余额积分信息开始.....", context.getTaskId());
                    JSONObject resultInfo = headerViewInfo.getJSONObject("result");
                    carrierInfo.getCarrierUserInfo().setAvailableBalance((int) Double.parseDouble(resultInfo.getString("account")) * 100);//可用余额
                    logger.info("==>[{}]1.2解析余额积分信息结束.", context.getTaskId());
                } else {
                    return new Result(StatusCode.解析积分余额信息出错);
                }
            } else {
                return new Result(StatusCode.解析积分余额信息出错);
            }
            result.setSuccess();
        } catch (FailingHttpStatusCodeException e){
            logger.error("==>[{}]联通官网繁忙", context.getTaskId(), e);
            result.setResult(StatusCode.官网系统繁忙);
            return result;

        }catch (Exception e) {
            logger.error("==>[{}]解析基本信息出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.解析基础信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<CarrierCallDetailInfo> carrierCallDetailInfoList = new ArrayList<>();
        try {
            //查询最近6个月通话详单
            for (int i = 1; i < 7; i++) {
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                calendar.set(Calendar.MONTH, month - i + 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");

                List<NameValuePair> reqParam = new ArrayList<>();
                reqParam.add(new NameValuePair("pageNo", "1"));
                reqParam.add(new NameValuePair("pageSize", "1000"));
                reqParam.add(new NameValuePair("beginDate", transDateBegin));
                reqParam.add(new NameValuePair("endDate", transDateEnd));

                String logFlag = String.format("==>[%s]2.%s解析[%s]至[%s]通话详单,第[{}]次尝试请求.....", context.getTaskId(), i, transDateBegin, transDateEnd);
                Page callDetailPage = getPage(webClient, ISERVICE_URL + "/e3/static/query/callDetail?_=" + Calendar.getInstance().getTimeInMillis() + "&menuid=000100030001",
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                if (null != callDetailPage) {//有通话详单结果
                    JSONObject callDetail = JSONObject.parseObject(callDetailPage.getWebResponse().getContentAsString());
                    if (callDetail.getBooleanValue("isSuccess")) {////操作成功

                        JSONObject pageMap = callDetail.getJSONObject("pageMap");
                        if (null != pageMap) {
                            JSONArray results = pageMap.getJSONArray("result");
                            if (null != results && CollectionUtils.isNotEmpty(results)) {//有通话记录
                                JSONObject d;
                                for (int j = 0; j < results.size(); j++) {
                                    CarrierCallDetailInfo carrierCallDetailInfo = new CarrierCallDetailInfo();
                                    d = (JSONObject) results.get(j);
                                    carrierCallDetailInfo.setMappingId(carrierInfo.getMappingId());
                                    carrierCallDetailInfo.setLocation(d.getString("homeareaName"));//通话地点
                                    carrierCallDetailInfo.setTime(d.getString("calldate") + " " + d.getString("calltime"));//通话时间
                                    carrierCallDetailInfo.setBillMonth(d.getString("calldate").substring(0, 7));
                                    String hour = RegexUtils.matchValue("(\\d+)时", d.getString("calllonghour"));
                                    String min = RegexUtils.matchValue("(\\d+)分", d.getString("calllonghour"));
                                    String sec = RegexUtils.matchValue("(\\d+)秒", d.getString("calllonghour"));
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
                                    carrierCallDetailInfo.setDuration(finalDuration + "");//通话时长
                                    if(null!=d.getString("calltypeName")){
                                        carrierCallDetailInfo.setDialType(d.getString("calltypeName").contains("主叫") ? "DIAL" : "DIALED");//呼叫类型
                                    }
                                    carrierCallDetailInfo.setPeerNumber(d.getString("othernum"));//对方号码
                                    carrierCallDetailInfo.setLocationType(d.getString("landtype"));//通话类型
                                    carrierCallDetailInfoList.add(carrierCallDetailInfo);
                                }
                            }
                        }
                        getLogger().info("==>[{}]2.{}解析[{}]通话详单结束,共[{}]记录,目前取前[1000]条记录.", context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"), callDetail.getString("totalRecord"));
                    } else {
                        getLogger().info("==>[{}]2.{}解析[{}]通话详单结束,没有查询结果.", context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                    }
                } else {
                    getLogger().info("==>[{}]2.{}解析[{}]通话详单结束,没有查询结果.", context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                }
                calendar.add(Calendar.MONTH, -1);
            }
            carrierInfo.setCalls(carrierCallDetailInfoList);
            result.setSuccess();
        } catch (FailingHttpStatusCodeException e) {
            logger.error("==>[{}]联通官网繁忙", context.getTaskId(), e);
            result.setResult(StatusCode.官网系统繁忙);
            return result;
        }catch (Exception e) {
            logger.error("==>[{}]解析通话详情出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.解析通话详情出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        result.setResult(StatusCode.爬取中);
        context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
        sendUpdateLog(result, context);

        List<NameValuePair> reqParam = new ArrayList<>();

        reqParam.clear();
        reqParam.add(new NameValuePair("_", Calendar.getInstance().getTimeInMillis() + ""));
        getPage(webClient, ISERVICE_URL + "/e3/static/check/checklogin", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

        reqParam.clear();
        reqParam.add(new NameValuePair("menuId", "000100030002"));
        Page resultPage = getPage(webClient, "http://iservice.10010.com/e3/static/query/checkmapExtraParam?_=1493810015816", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
        List<CarrierSmsRecordInfo> carrierSmsRecordInfoList = new ArrayList<>();
        try {
            //查询最近6个月短信记录
            for (int i = 1; i < 7; i++) {
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                calendar.set(Calendar.MONTH, month - i + 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                final String transDateBegin = DateFormatUtils.format(calendar, "yyyyMMdd");
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                final String transDateEnd = DateFormatUtils.format(calendar, "yyyyMMdd");
                List<NameValuePair> reqSMsParam = new ArrayList<>();
                reqSMsParam.add(new NameValuePair("pageNo", "1"));
                reqSMsParam.add(new NameValuePair("pageSize", "20"));
                reqSMsParam.add(new NameValuePair("begindate", transDateBegin));
                if (i == 1) {
                    reqSMsParam.add(new NameValuePair("enddate", transDateEnd.compareTo(DateUtils.getCurrentDate()) > 0 ? DateUtils.getCurrentDate1() : transDateEnd));
                } else {
                    reqSMsParam.add(new NameValuePair("enddate", transDateEnd));
                }

                String logFlag = String.format("==>[%s]3.%s解析[%s]至[%s]短信记录,第[{}]次尝试请求.....", context.getTaskId(), i, transDateBegin, transDateEnd);
                Page smsPage = getPage(webClient, ISERVICE_URL + "/e3/static/query/sms?_=" + Calendar.getInstance().getTimeInMillis() + "&menuid=000100030002",
                        HttpMethod.POST, reqSMsParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != smsPage) {//有短信记录结果
                    JSONObject sms = JSONObject.parseObject(smsPage.getWebResponse().getContentAsString());
                    if (sms.getBooleanValue("isSuccess")) {//操作成功

                        JSONObject pageMap = sms.getJSONObject("pageMap");
                        if (null != pageMap) {
                            JSONArray results = pageMap.getJSONArray("result");

                            if (null != results && CollectionUtils.isNotEmpty(results)) {//有短信记录
                                JSONObject d;
                                for (int j = 0; j < results.size(); j++) {
                                    CarrierSmsRecordInfo carrierSmsRecordInfo = new CarrierSmsRecordInfo();
                                    carrierSmsRecordInfo.setMappingId(carrierInfo.getMappingId());
                                    d = (JSONObject) results.get(j);
                                    carrierSmsRecordInfo.setBillMonth(d.getString("smsdate").substring(0, 7));
                                    carrierSmsRecordInfo.setPeerNumber(d.getString("othernum"));
                                    carrierSmsRecordInfo.setLocation(d.getString("homearea"));
                                    carrierSmsRecordInfo.setTime(d.getString("smsdate") + " " + d.getString("smstime"));
                                    String smstype = d.getString("smstype");
                                    if ("1".equals(smstype)) {
                                        carrierSmsRecordInfo.setSendType("RECEIVE");
                                    } else if ("2".equals(smstype)) {
                                        carrierSmsRecordInfo.setSendType("SEND");
                                    } else {
                                        carrierSmsRecordInfo.setSendType(smstype);
                                    }
                                    carrierSmsRecordInfo.setFee(((Double) (d.getDouble("fee") * 100)).intValue());
                                    String msgType = d.getString("businesstype");
                                    if ("01".equals(msgType)) {
                                        carrierSmsRecordInfo.setMsgType("SMS");
                                    } else {
                                        carrierSmsRecordInfo.setMsgType("MMS");
                                    }
                                    carrierSmsRecordInfoList.add(carrierSmsRecordInfo);
                                }
                            }
                        }
                        getLogger().info("==>[{}]3.{}解析[{}]短信记录结束,共[{}]记录,目前取前[1000]条记录.", context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"), sms.getString("mmsCount"));
                    } else {
                        getLogger().info("==>[{}]3.{}解析[{}]短信记录结束,没有查询结果.", context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                    }
                } else {
                    getLogger().info("==>[{}]3.{}解析[{}]短信记录结束,没有查询结果.", context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                }
                calendar.add(Calendar.MONTH, -1);
            }

            carrierInfo.setSmses(carrierSmsRecordInfoList);
            result.setSuccess();
        } catch (FailingHttpStatusCodeException e) {
            logger.error("==>[{}]联通官网繁忙", context.getTaskId(), e);
            result.setResult(StatusCode.官网系统繁忙);
            return result;
        }catch (Exception e) {
            logger.error("==>[{}]解析短信记录出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.解析短信记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<CarrierNetDetailInfo> carrierNetDetailInfoList = new ArrayList<>();
        int totalpage = 0;
        try {
            //查询最近6个月上网记录
            for (int i = 1; i < 7; i++) {
                List<NameValuePair> reqParam = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                calendar.set(Calendar.MONTH, month - i + 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");

                reqParam.clear();
                reqParam.add(new NameValuePair("pageNo", "1"));
                reqParam.add(new NameValuePair("pageSize", "100"));
                reqParam.add(new NameValuePair("beginDate", transDateBegin));
                if (i == 1) {
                    reqParam.add(new NameValuePair("endDate", transDateEnd.compareTo(DateUtils.getCurrentDate()) > 0 ? DateUtils.getCurrentDate() : transDateEnd));
                } else {
                    reqParam.add(new NameValuePair("endDate", transDateEnd));
                }
                String logFlag = String.format("==>[%s]4.%s解析[%s]上网记录,第[{}]次尝试请求.....", context.getTaskId(), i, transDateEnd);
                Page netInfoPage = getPage(webClient, ISERVICE_URL + "/e3/static/query/callFlow?_=" + Calendar.getInstance().getTimeInMillis() + "&accessURL=http://iservice.10010.com/e4/query/basic/call_flow_iframe1.html?menuCode=000100030004&menuid=000100030004",
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != netInfoPage) {//有上网记录结果
                    JSONObject netInfo = JSONObject.parseObject(netInfoPage.getWebResponse().getContentAsString());
                    if (netInfo.getBooleanValue("issuccess")) {//操作成功
                        JSONObject jsonObject = netInfo.getJSONObject("result");
                        JSONArray record = jsonObject.getJSONArray("record");
                        if (null != record && CollectionUtils.isNotEmpty(record)) {//有上网记录
                            JSONObject d;
                            for (int j = 0; j < record.size(); j++) {
                                if (j > 999) {
                                    logger.info("==>[{}]超过1000条记录,退出", context.getTaskId());
                                    break;
                                }
                                CarrierNetDetailInfo carrierNetDetailInfo = new CarrierNetDetailInfo();
                                d = (JSONObject) record.get(j);
                                carrierNetDetailInfo.setBillMonth(transDateBegin.substring(0, 7));
                                carrierNetDetailInfo.setMappingId(carrierInfo.getMappingId());
                                carrierNetDetailInfo.setLocation(d.getString("homearea"));//上网地点
                                carrierNetDetailInfo.setTime(d.getString("begindate") + " " + d.getString("begintime"));//上网时间
                                carrierNetDetailInfo.setDuration(d.getInteger("longhour"));//上网时长
                                carrierNetDetailInfo.setNetType(d.getString("nettypename"));//上网类型
                                carrierNetDetailInfo.setServiceName(d.getString("svcname"));//业务名称
                                carrierNetDetailInfo.setFee(((Double) (d.getDouble("totalfee") * 100)).intValue());//费用
                                Double fee = d.getDouble("deratefee");
                                carrierNetDetailInfo.setSubflow(fee.intValue());//上网流量
                                carrierNetDetailInfoList.add(carrierNetDetailInfo);
                            }
                        }
                        getLogger().info("==>[{}]4.{}解析[{}]上网记录结束,共[{}]记录,目前取前[1000]条记录.", context.getTaskId(), i, transDateEnd, netInfo.getJSONObject("result").getString("totalrecord"));
                    } else {
                        getLogger().info("==>[{}]4.{}解析[{}]上网记录结束,没有查询结果.", context.getTaskId(), i, transDateEnd);
                    }
                } else {
                    getLogger().info("==>[{}]4.{}解析[{}]上网记录结束,没有查询结果.", context.getTaskId(), i, transDateEnd);
                }

            }
            carrierInfo.setNets(carrierNetDetailInfoList);
            result.setSuccess();
        } catch (FailingHttpStatusCodeException e) {
            logger.error("==>[{}]联通官网繁忙", context.getTaskId(), e);
            result.setResult(StatusCode.官网系统繁忙);
            return result;
        }catch (Exception e) {
            logger.error("==>[{}]解析上网记录出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.解析上网记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<CarrierBillDetailInfo> carrierBillDetailInfoList = new ArrayList<>();
        try {
            //查询最近6个月账单信息
            for (int i = 1; i < 7; i++) {
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                calendar.set(Calendar.MONTH, month - i);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                final String billdate = DateFormatUtils.format(calendar, "yyyyMM");
                String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");

                List<NameValuePair> reqParam = new ArrayList<>();
                reqParam.add(new NameValuePair("querytype", "0001"));
                reqParam.add(new NameValuePair("querycode", "0001"));
                reqParam.add(new NameValuePair("billdate", billdate));
                reqParam.add(new NameValuePair("flag", "1"));
                String logFlag = String.format("==>[%s]5.%s解析[%s]账单信息,第[{}]次尝试请求.....", context.getTaskId(), i, billdate);
                Page historyBillPage = getPage(webClient, ISERVICE_URL + "/e3/static/query/queryHistoryBill?_=" + Calendar.getInstance().getTimeInMillis() + "&accessURL=http://iservice.10010.com/e4/skip.html?menuCode=000100020001&menuCode=000100020001&menuid=000100020001",
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                if (null != historyBillPage) {//有账单信息结果
                    JSONObject historyBill = JSONObject.parseObject(historyBillPage.getWebResponse().getContentAsString());
                    if (historyBill.getBooleanValue("issuccess")) {//操作成功
                        JSONObject resultInfo = historyBill.getJSONObject("result");

                        if (null != resultInfo) {
                            CarrierBillDetailInfo carrierBillDetailInfo = new CarrierBillDetailInfo();
                            carrierBillDetailInfo.setMappingId(carrierInfo.getMappingId());
                            carrierBillDetailInfo.setBillStartDate(transDateBegin);
                            carrierBillDetailInfo.setBillEndDate(transDateEnd);
                            carrierBillDetailInfo.setBillMonth(DateUtils.dateToString(DateUtils.stringToDate(resultInfo.getString("cycleid"), "yyyyMM"), "yyyy-MM"));//账单月份
                            try {
                                JSONArray billinfo = resultInfo.getJSONArray("billinfo");
                                for (Object o : billinfo) {
                                    JSONObject jsonObject = (JSONObject) o;
                                    String integrateitem = jsonObject.getString("integrateitem");
                                    if (integrateitem.contains(HistoryBill.个人实际费用.getName())) {
                                        carrierBillDetailInfo.setActualFee(((Double) (jsonObject.getDouble("value") * 100)).intValue());//个人实际费用
                                    } else if (integrateitem.contains(HistoryBill.其它优惠.getName())) {
                                        carrierBillDetailInfo.setExtraDiscount(((Double) (jsonObject.getDouble("value") * 100)).intValue());//其它优惠
                                    } else if (integrateitem.contains(HistoryBill.优惠费.getName())) {
                                        carrierBillDetailInfo.setDiscount(((Double) (jsonObject.getDouble("value") * 100)).intValue());//优惠费
                                    } else if (integrateitem.contains(HistoryBill.增值业务费.getName())) {
                                        carrierBillDetailInfo.setExtraServiceFee(((Double) (jsonObject.getDouble("fee") * 100)).intValue());//增值业务费
                                    } else if (integrateitem.contains(HistoryBill.抵扣合计.getName())) {
                                        carrierBillDetailInfo.setDiscount((((Double) (jsonObject.getDouble("value") * 100)).intValue()));
                                        carrierBillDetailInfo.setTotalFee(((Double) (jsonObject.getDouble("value") * 100)).intValue());//总费用
                                    } else if (integrateitem.contains(HistoryBill.月固定费.getName())) {
                                        carrierBillDetailInfo.setBaseFee(((Double) (jsonObject.getDouble("fee") * 100)).intValue());//月固定费
                                    } else if (integrateitem.contains(HistoryBill.短彩信费.getName())) {
                                        carrierBillDetailInfo.setSmsFee(((Double) (jsonObject.getJSONArray("detailList").getJSONObject(0).getDouble("value") * 100)).intValue());//短彩信费
                                    } else if (integrateitem.contains(HistoryBill.网络流量费.getName())) {
                                        carrierBillDetailInfo.setWebFee(((Double) (jsonObject.getDouble("value") * 100)).intValue());//网络流量费
                                    } else if (integrateitem.contains(HistoryBill.语音通话费.getName())) {
                                        carrierBillDetailInfo.setVoiceFee(((Double) (jsonObject.getDouble("fee") * 100)).intValue());//语音费
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("==>[{}]解析账单信息[套餐消费]出现异常", context.getTaskId(), e);
                            }
                            carrierBillDetailInfo.setPaidFee(((Double) (resultInfo.getDouble("allfee") * 100)).intValue());//总金额

                            carrierBillDetailInfoList.add(carrierBillDetailInfo);
                        }

                        getLogger().info("==>[{}]5.{}解析[{}]账单信息结束.", i, DateFormatUtils.format(calendar, "yyyy-MM"), context.getTaskId());
                    } else if ("success".equals(historyBill.getString("historyResultState"))) {
                        CarrierBillDetailInfo carrierBillDetailInfo = new CarrierBillDetailInfo();
                        String billMonth = transDateBegin.substring(0, 7);
                        JSONArray list = historyBill.getJSONArray("historyResultList");
                        carrierBillDetailInfo.setBillStartDate(transDateBegin);
                        carrierBillDetailInfo.setBillMonth(billMonth);
                        carrierBillDetailInfo.setMappingId(carrierInfo.getMappingId());
                        carrierBillDetailInfo.setBillEndDate(transDateEnd);
                        for (Object o : list) {
                            JSONObject jsonObject = (JSONObject) o;
                            String name = jsonObject.getString("name");
                            if (name.contains(HistoryBill.基本月租.getName()) || name.contains(HistoryBill.月固定费.getName())) {
                                carrierBillDetailInfo.setBaseFee((((Double) (jsonObject.getDouble("value") * 100)).intValue()));
                            } else if (name.contains(HistoryBill.国内通话费.getName()) || name.contains(HistoryBill.语音通话费.getName())) {
                                carrierBillDetailInfo.setVoiceFee((((Double) (jsonObject.getDouble("value") * 100)).intValue()));
                            } else if (name.contains(HistoryBill.短信通信费.getName())) {
                                carrierBillDetailInfo.setSmsFee((((Double) (jsonObject.getDouble("value") * 100)).intValue()));
                            } else if (name.contains(HistoryBill.增值业务费.getName())) {
                                carrierBillDetailInfo.setExtraServiceFee((((Double) (jsonObject.getDouble("value") * 100)).intValue()));
                            } else if (name.contains(HistoryBill.上网费.getName())) {
                                carrierBillDetailInfo.setWebFee((((Double) (jsonObject.getDouble("value") * 100)).intValue()));
                            } else if (name.contains(HistoryBill.其他费用.getName())) {
                                carrierBillDetailInfo.setExtraFee((((Double) (jsonObject.getDouble("value") * 100)).intValue()));
                            } else if (name.contains(HistoryBill.实际应缴合计.getName())) {
                                carrierBillDetailInfo.setActualFee((((Double) (jsonObject.getDouble("value") * 100)).intValue()));
                            }
                        }
                        carrierBillDetailInfoList.add(carrierBillDetailInfo);

                    } else {
                        getLogger().info("==>[{}]5.{}解析[{}]账单信息结束,没有查询结果.", context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                    }
                } else {
                    getLogger().info("==>[{}]5.{}解析[{}]账单信息结束,没有查询结果.", context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                }
                calendar.add(Calendar.MONTH, -1);
            }

            carrierInfo.setBills(carrierBillDetailInfoList);
            result.setSuccess();
        } catch (FailingHttpStatusCodeException e){
            logger.error("==>[{}]联通官网繁忙", context.getTaskId(), e);
            result.setResult(StatusCode.官网系统繁忙);
            return result;}
        catch (Exception e) {
            logger.error("==>[{}]解析账单信息出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.解析账单信息出错);
            return result;
        }
        return result;
    }


    @Override
    protected Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<CarrierPackageItemInfo> carrierPackageItemInfoList = new ArrayList<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        try {
            reqParam.add(new NameValuePair("_", String.valueOf(Calendar.getInstance().getTimeInMillis())));
            reqParam.add(new NameValuePair("accessURL", "0001"));
            reqParam.add(new NameValuePair("billdate", "http://iservice.10010.com/e4/skip.html?menuCode=000100040001"));
            reqParam.add(new NameValuePair("menuCode", "000100040001"));
            String logFlag = String.format("==>[%s]6.爬取套餐信息,第[{}]次尝试请求.....", context.getTaskId());
            Page page = getPage(webClient, ISERVICE_URL + "/e3/static/query/newQueryLeavePackageData",
                    HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
            if (null != page) {//有账单信息结果
                JSONObject bill = JSONObject.parseObject(page.getWebResponse().getContentAsString());
                JSONArray flowListComboArr = bill.getJSONArray("fourpackage");
                if (null != flowListComboArr) {
                    for (int i = 0; i < flowListComboArr.size(); i++) {
                        CarrierPackageItemInfo carrierPackageItemInfo = new CarrierPackageItemInfo();
                        carrierPackageItemInfo.setMappingId(carrierInfo.getMappingId());
                        if("3".equals(((JSONObject) flowListComboArr.get(i)).getString("elemType"))){
                            carrierPackageItemInfo.setItem(((JSONObject) flowListComboArr.get(i)).getString("addUpItemName"));//套餐项目名称
                            if("MB".equals(((JSONObject) flowListComboArr.get(i)).getString("canUseUnitVal"))) {
                                carrierPackageItemInfo.setTotal(String.valueOf(((JSONObject) flowListComboArr.get(i)).getDouble("addUpUpper")*1024));//项目总量
                            }else if("GB".equals(((JSONObject) flowListComboArr.get(i)).getString("canUseUnitVal"))) {
                                carrierPackageItemInfo.setTotal(String.valueOf(((JSONObject) flowListComboArr.get(i)).getDouble("addUpUpper")*1024*1024));//项目总量
                            }else{
                                carrierPackageItemInfo.setTotal(((JSONObject) flowListComboArr.get(i)).getString("addUpUpper"));//项目总量
                            }
                            if("MB".equals(((JSONObject) flowListComboArr.get(i)).getString("usedUnitVal"))) {
                                carrierPackageItemInfo.setUsed(String.valueOf(((JSONObject) flowListComboArr.get(i)).getDouble("xusedValue")*1024));//使用量
                            }else if("GB".equals(((JSONObject) flowListComboArr.get(i)).getString("usedUnitVal"))) {
                                carrierPackageItemInfo.setUsed(String.valueOf(((JSONObject) flowListComboArr.get(i)).getDouble("xusedValue")*1024*1024));//使用量
                            }else{
                                carrierPackageItemInfo.setUsed(((JSONObject) flowListComboArr.get(i)).getString("xusedValue"));//使用量
                            }
                            carrierPackageItemInfo.setUnit("KB");
                            carrierPackageItemInfo.setBillStartDate(transDateBegin);
                            carrierPackageItemInfo.setBillEndDate(transDateEnd);
                            carrierPackageItemInfoList.add(carrierPackageItemInfo);
                        }else if("1".equals(((JSONObject) flowListComboArr.get(i)).getString("elemType"))) {
                            carrierPackageItemInfo.setItem(((JSONObject) flowListComboArr.get(i)).getString("addUpItemName"));//套餐项目名称
                            carrierPackageItemInfo.setTotal(((JSONObject) flowListComboArr.get(i)).getString("addUpUpper"));//项目总量
                            carrierPackageItemInfo.setUsed(((JSONObject) flowListComboArr.get(i)).getString("xusedValue"));//使用量
                            carrierPackageItemInfo.setUnit("分");
                            carrierPackageItemInfo.setBillStartDate(transDateBegin);
                            carrierPackageItemInfo.setBillEndDate(transDateEnd);
                            carrierPackageItemInfoList.add(carrierPackageItemInfo);
                        }
                    }
                }
            }
            carrierInfo.setPackages(carrierPackageItemInfoList);
            result.setSuccess();
        }catch (FailingHttpStatusCodeException e) {
            logger.error("==>[{}]联通官网繁忙", context.getTaskId(), e);
            result.setResult(StatusCode.官网系统繁忙);
            return result;
        }catch (Exception e) {
            logger.error("==>[{}]解析账单信息出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.解析账单信息出错);
            return result;
        }
        return result;
    }

    @Override
    protected Result processUserFamilyMember(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        return new Result(StatusCode.SUCCESS);
    }

    @Override
    protected Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<CarrierUserRechargeItemInfo> carrierUserRechargeItemInfoList = new ArrayList<>();

        try {
            for (int i = 1; i < 7; i++) {
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                calendar.set(Calendar.MONTH, month - i + 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                final String transDateBegin = DateFormatUtils.format(calendar, "yyyyMMdd");
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                final String transDateEnd = DateFormatUtils.format(calendar, "yyyyMMdd");
                List<NameValuePair> reqParam = new ArrayList<>();
                reqParam.add(new NameValuePair("beginDate", transDateBegin));
                if (i == 1) {
                    reqParam.add(new NameValuePair("endDate", transDateEnd.compareTo(DateUtils.getCurrentDate()) > 0 ? DateUtils.getCurrentDate1() : transDateEnd));
                } else {
                    reqParam.add(new NameValuePair("endDate", transDateEnd));
                }
                reqParam.add(new NameValuePair("pageNo", "1"));
                reqParam.add(new NameValuePair("pageSize", "20"));
                String logFlag = String.format("==>[%s]8.%s采集[%s]缴费记录信息,第[{}]次尝试请求.....", context.getTaskId(), i, transDateBegin + "-" + transDateEnd);
                Page page = getPage(webClient, ISERVICE_URL + "/e3/static/query/paymentRecord?_=" + Calendar.getInstance().getTimeInMillis() + "&accessURL=http://iservice.10010.com/e4/query/calls/paid_record-iframe.html?menuCode=000100010003&menuid=000100010003",
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != page) {//有账单信息结果
                    JSONObject bill = JSONObject.parseObject(page.getWebResponse().getContentAsString());
                    if (null != bill) {//操作成功
                        JSONObject resultInfo = bill.getJSONObject("pageMap");
                        if (null != resultInfo) {
                            JSONArray resultInfoArr = resultInfo.getJSONArray("result");
                            for (int j = 0; j < resultInfoArr.size(); j++) {
                                CarrierUserRechargeItemInfo carrierUserRechargeItemInfo = new CarrierUserRechargeItemInfo();
                                carrierUserRechargeItemInfo.setBillMonth(DateFormatUtils.format(calendar, "yyyy-MM"));
                                carrierUserRechargeItemInfo.setMappingId(carrierInfo.getMappingId());
                                carrierUserRechargeItemInfo.setRechargeTime(((JSONObject) resultInfoArr.get(j)).getString("paydate")+" 00:00:00");
                                carrierUserRechargeItemInfo.setAmount(((Double) (((JSONObject) resultInfoArr.get(j)).getDouble("payfee") * 100)).intValue());
                                carrierUserRechargeItemInfo.setType(((JSONObject) resultInfoArr.get(j)).getString("paychannel"));
                                carrierUserRechargeItemInfoList.add(carrierUserRechargeItemInfo);
                            }
                        }


                    }
                }
            }
            carrierInfo.setRecharges(carrierUserRechargeItemInfoList);
            result.setSuccess();
        }catch (FailingHttpStatusCodeException e) {
            logger.error("==>[{}]联通官网繁忙", context.getTaskId(), e);
            result.setResult(StatusCode.官网系统繁忙);
            return result;
        } catch (Exception e) {
            logger.error("==>[{}]解析账单信息出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.解析账单信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected Result loginout(WebClient webClient) {
        Result result = new Result();
        try {
            getPage(webClient, LOGOUT_URL, HttpMethod.POST, null, null);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("退出登录失败", e);
        }
        return result;
    }
}
