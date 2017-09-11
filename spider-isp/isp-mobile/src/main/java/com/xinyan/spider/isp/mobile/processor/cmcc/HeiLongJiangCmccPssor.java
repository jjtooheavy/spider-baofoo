package com.xinyan.spider.isp.mobile.processor.cmcc;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.cmcc.HeiLongJiangCmccParser;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 黑龙江移动处理类
 *
 * @author heliang
 * @version V1.0
 * @description
 * @date 2016年8月22日 下午3:22:12
 */
@Component
public class HeiLongJiangCmccPssor extends AbstractProcessor {

    @Autowired
    private HeiLongJiangCmccParser parser;

    protected static Logger logger = LoggerFactory.getLogger(HeiLongJiangCmccPssor.class);

    @Override
    public Result doLogin(WebClient webClient, Context context) {

        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        try {

            logger.info("==>[{}]1.1正在验证手机号码开始.....", context.getTaskId());
            url = "https://login.10086.cn/html/login/login.html?channelID=12002";
            Page page = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);

            header.clear();
            header.put("accept", "application/json, text/javascript, */*; q=0.01");
            url = "https://login.10086.cn/needVerifyCode.htm?accountType=01&account=" + context.getUserName() +
                    "&timestamp=1471498637687";
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            if ("{\"needVerifyCode\":\"1\"}".equals(page.getWebResponse().getContentAsString())) {

                url = "https://login.10086.cn/chkNumberAction.action";
                reqParam.clear();
                reqParam.add(new NameValuePair("userName", context.getUserName()));
                page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);

                if (!"true".equals(page.getWebResponse().getContentAsString())) {
                    logger.info("==>[{}]1.1发送手机号码失败.", context.getTaskId());
                    result.setResult(StatusCode.验证手机号码失败);
                    return result;
                }

                url = "https://login.10086.cn/sendRandomCodeAction.action";
                reqParam.clear();
                reqParam.add(new NameValuePair("userName", context.getUserName()));
                reqParam.add(new NameValuePair("type", "01"));
                reqParam.add(new NameValuePair("channelID", "12002"));

                page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
                if ("0".equals(page.getWebResponse().getContentAsString())) {
                    logger.info("==>[{}]1.1已经发送短信验证码.", context.getTaskId());
                    result.setResult(StatusCode.请输入短信验证码);
                    context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
                    sendValidataMsg(result, context, false, webClient, Constants.ENQUEUE_TIME_OUT, null);

                } else {
                    logger.info("==>[{}]发送动态密码发送失败!", context.getTaskId());
                    result.setResult(StatusCode.发送短信验证码失败);
                    return result;
                }
            } else if ("{\"needVerifyCode\":\"0\"}".equals(page.getWebResponse().getContentAsString())) {
                header.clear();
                header.put("Referer", "https://login.10086.cn/login.html?channelID=12003&backUrl=http://shop.10086" +
                        ".cn/i/");
                url = "https://login.10086.cn/login.htm?accountType=01&account=" + context.getUserName() + "&password="
                        + context.getPassword() + "&pwdType=01&smsPwd=&inputCode=&backUrl=http://shop.10086.cn/i/" +
                        "&rememberMe=0&channelID=12003&protocol=https:&timestamp=1471507724633";
                page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
                if (StringUtils.contains(page.getWebResponse().getContentAsString(), "认证成功")) {
                    String artifact = RegexUtils.matchValue("artifact\":\"(.*?)\"", page.getWebResponse().getContentAsString());
                    url = "http://shop.10086.cn/i/v1/auth/getArtifact?backUrl=http%3A%2F%2Fshop.10086.cn%2Fi%2F&artifact="+artifact;
                    page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
                    result.setSuccess();
                    result.setData(result);
                } else {
                    logger.info("==>[{}]1.1登陆失败：" + page.getWebResponse().getContentAsString(), context.getTaskId());
                    if (StringUtils.contains(page.getWebResponse().getContentAsString(), "密码锁定")) {
                        result.setResult(StatusCode.密码锁定);
                        return result;//密码锁定
                    } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "账号锁定")) {
                        result.setResult(StatusCode.账号锁定);
                        return result;//账号锁定
                    } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "系统繁忙")) {
                        result.setResult(StatusCode.验证短信验证码异常);
                        return result;//系统繁忙
                    } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "您的账户名与密码不匹配，请重新输入")) {
                        result.setResult(StatusCode.用户名或密码错误);
                        return result;//您的账户名与密码不匹配，请重新输入
                    } else {
                        result.setResult(StatusCode.登陆失败);
                        return result;
                    }
                }
                logger.info("==>[{}]1.2用户登陆结果验证结束.", context.getTaskId());

                logger.info("==>[{}]1.3登陆成功跳转页面开始.....", context.getTaskId());
                String artifact = RegexUtils.matchValue("artifact\":\"(.*?)\"", page.getWebResponse().getContentAsString());

                StrConvertToCookieContainer(webClient, ".10086.cn");
                url = "https://login.10086.cn/SSOCheck.action?channelID=12003&backUrl=http://shop.10086.cn/i/";
                page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

                url = String.format("http://shop.10086.cn/sso/getartifact.php?backUrl=http://shop.10086.cn/mall_210_210.html?forcelogin=1&artifact=%s"
                        , artifact);
                page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

                StrConvertToCookieContainer(webClient, "login.10086.cn");

                url = "http://shop.10086.cn/i/";
                page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
                header.clear();
                header.put("Referer", "http://shop.10086.cn/i/");
                url = "http://shop.10086.cn/i/v1/auth/loginfo?time=2015828131631478";
                page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

                logger.info("==>[{}]1.3登陆成功跳转页面结束.", context.getTaskId());
            } else {
                logger.info("==>[{}]1.3验证是否发送短信验证码失败!", context.getTaskId());
                result.setResult(StatusCode.验证手机号码失败);
                return result;
            }


        } catch (Exception e) {
            logger.error("==>登录出现异常:[{}]", e);
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

    @Override
    protected Result doLoginBySMS(WebClient webClient, Context context) throws Exception {
        logger.info("==>[{}]1.4短信验证开始.....", context.getTaskId());
        List<NameValuePair> reqParam = new ArrayList<>();
        Result result = new Result();
        HashMap<String, String> header = new HashMap<>();
        try {
            header.clear();
            header.put("Referer", "https://login.10086.cn/login.html?channelID=12003&backUrl=http://shop" +
                    ".10086.cn/i/");
            String url = "https://login.10086.cn/login.htm?accountType=01&account=" + context.getUserName() +
                    "&password=" + context.getPassword() + "&pwdType=01&smsPwd=" + context.getUserInput() +
                    "&inputCode=&backUrl=http://shop.10086.cn/mall_250_250.html?forcelogin=1&rememberMe=0&channelID=12003&protocol=https:&timestamp=1471498940062";
            Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            if (StringUtils.contains(page.getWebResponse().getContentAsString(), "认证成功")) {
                result.setSuccess();
                result.setData(result);
            } else {
                logger.info("==>[{}]1.4短信验证失败：" + page.getWebResponse().getContentAsString(), context.getTaskId());
                if (StringUtils.contains(page.getWebResponse().getContentAsString(), "密码锁定")) {
                    result.setResult(StatusCode.密码锁定);
                    return result;//密码锁定
                } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "账号锁定")) {
                    result.setResult(StatusCode.账号锁定);
                    return result;//账号锁定
                } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "系统繁忙")) {
                    result.setResult(StatusCode.验证短信验证码异常);
                    return result;//系统繁忙
                } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "您的账户名与密码不匹配，请重新输入")) {
                    result.setResult(StatusCode.用户名或密码错误);
                    return result;//您的账户名与密码不匹配，请重新输入
                } else {
                    result.setResult(StatusCode.登陆失败);
                    return result;
                }
            }
            logger.info("==>[{}]1.4验证手机号码结束.", context.getTaskId());
            logger.info("==>[{}]1.4登陆成功跳转页面开始.....", context.getTaskId());
            String artifact = RegexUtils.matchValue("artifact\":\"(.*?)\"", page.getWebResponse().getContentAsString());

            StrConvertToCookieContainer(webClient, ".10086.cn");
            url = "https://login.10086.cn/SSOCheck.action?channelID=12003&backUrl=http://shop.10086.cn/i/";
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            url = String.format("http://shop.10086.cn/sso/getartifact.php?backUrl=http://shop.10086.cn/mall_210_210.html?forcelogin=1&artifact=%s"
                    , artifact);
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            StrConvertToCookieContainer(webClient, "login.10086.cn");

            url = "http://shop.10086.cn/i/";
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            header.clear();
            header.put("Referer", "http://shop.10086.cn/i/");
            url = "http://shop.10086.cn/i/v1/auth/loginfo?time=2015828131631478";
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            logger.info("==>[{}]1.4用户登陆结果验证结束.", context.getTaskId());
        } catch (Exception e) {
            logger.error("==>登录出现异常:[{}]", e);
            result.setResult(StatusCode.登陆出错);
            return result;
        } finally {
            sendLoginMsg(result, context, false, webClient);
            close(webClient);
        }
        return result;

    }

    @Override
    public Result doCrawler(WebClient webClient, Context context) {
        Result result = new Result();

        //页面缓存
        CacheContainer cacheContainer = new CacheContainer();
        CarrierInfo carrierInfo = new CarrierInfo();
        context.setMappingId(carrierInfo.getMappingId());
        result.setData(carrierInfo);
        BeanUtils.copyProperties(context, carrierInfo);
        context.setParam1(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        try {
            //1.采集基础信息
            getLogger().info("==>[{}]1.采集基本信息开始.....", context.getTaskId());
            result = processBaseInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]1.采集基本信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集基础信息失败
                return result;
            }
            //2.采集通话详单
            getLogger().info("==>[{}]2.采集通话详单开始.....", context.getTaskId());
            result = processCallRecordInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]2.采集通话详单结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集通话详单失败
                return result;
            }
            //3.采集短信记录
            getLogger().info("==>[{}]3.采集短信记录开始.....", context.getTaskId());
            result = processSmsInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]3.采集短信记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集短信记录失败
                return result;
            }

            //4.采集上网记录
            getLogger().info("==>[{}]4.采集上网记录开始.....", context.getTaskId());
            result = processNetInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]4.采集上网记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集上网记录失败
                return result;
            }

            //5.采集账单信息
            getLogger().info("==>[{}]5.采集账单信息开始.....", context.getTaskId());
            result = processBillInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]5.采集账单信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集账单信息失败
                return result;
            }

            //6.采集套餐信息
            getLogger().info("==>[{}]6.采集套餐信息开始.....", context.getTaskId());
            result = processPackageItemInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]6.采集套餐信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }

            //7.采集亲情号码
            getLogger().info("==>[{}]7.采集亲情号码开始.....", context.getTaskId());
            result = processUserFamilyMember(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]7.采集亲情号码结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }

            //8.采集充值记录
            getLogger().info("==>[{}]8.采集充值记录开始.....", context.getTaskId());
            result = processUserRechargeItemInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]8.采集充值记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }
            result.setData(carrierInfo);
        } catch (Exception ex) {
            getLogger().error("==>[{}]数据抓取出现异常:[{}]", context.getTaskId(), ex);
            result.setFail();
            return result;
        } finally {
            sendUpdateLog(result, context);
            getLogger().info("==>[{}]9.退出登陆开始.....", context.getTaskId());
            loginout(webClient);
            getLogger().info("==>[{}]9.退出登陆结束.", context.getTaskId());
            close(webClient);
        }

        try {
            //解析基本信息
            getLogger().info("==>[{}]10.解析基本信息开始.....", context.getTaskId());
            result = parser.basicInfoParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]10.解析基本信息结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析通话详单
            getLogger().info("==>[{}]11.解析通话详单开始.....", context.getTaskId());
            result = parser.callRecordParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]11.解析通话详单结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析短信记录
            getLogger().info("==>[{}]12.解析短信记录开始.....", context.getTaskId());
            result = parser.smsInfoParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]12.解析短信记录结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析上网记录
            getLogger().info("==>[{}]13.解析上网记录开始.....", context.getTaskId());
            result = parser.netInfoParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]13.解析上网记录结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析账单信息
            getLogger().info("==>[{}]14.解析账单信息开始.....", context.getTaskId());
            result = parser.billParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]14.解析账单信息结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析套餐信息
            getLogger().info("==>[{}]15.解析套餐信息开始.....", context.getTaskId());
            result = parser.packageItemInfoParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]15.解析套餐信息结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析亲情号码
//            getLogger().info("==>[{}]16.解析亲情号码开始.....", context.getTaskId());
//            result = parser.userFamilyMemberParse(context, cacheContainer, carrierInfo);
//            getLogger().info("==>[{}]16.解析亲情号码结束.....", context.getTaskId());
//            if (!result.isSuccess()) {
//                return result;
//            }
            //解析充值记录
            getLogger().info("==>[{}]17.解析充值记录开始.....", context.getTaskId());
            result = parser.userRechargeItemInfoParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]17.解析充值记录结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            result.setData(carrierInfo);
        } catch (Exception e) {
            result.setFail();
            getLogger().info("==>[{}]9.解析数据异常:[{}]", context.getTaskId(), e);
        } finally {
            sendAnalysisMsg(result, context);
            close(webClient);
        }

        getLogger().info("==>[{}]0.数据抓取结束{}\n,详情:{}", context.getTaskId(), result, result.getData());
        return result;
    }

    @Override
    public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {

        Result result = new Result();

        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        List<Page> pageList = new ArrayList<>();
        try {

            logger.info("==>[{}]1.1抓取余额信息开始.....", context.getTaskId());
            url = String.format("http://shop.10086.cn/i/v1/fee/real/%s?time=201579155022734", context.getUserName());
            Page resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]1.1抓取余额信息结束.", context.getTaskId());

            logger.info("==>[{}]1.2抓取积分信息开始.....", context.getTaskId());
            url = String.format("http://shop.10086.cn/i/v1/busi/plan/%s?_=1494467753386", context.getUserName());
            resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.POINTS_VALUE.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]1.2抓取积分信息结束.", context.getTaskId());

            logger.info("==>[{}]1.3抓取其他基本信息开始.....", context.getTaskId());
            url = String.format("http://shop.10086.cn/i/v1/cust/info/%s?time=201579155022734", context.getUserName());
            resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.OTHER_INFO.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]1.3抓取其他基本信息结束.", context.getTaskId());
            result.setSuccess();

        } catch (Exception e) {
            logger.error("==>采集基本信息出现异常:[{}]", e);
            result.setResult(StatusCode.解析基础信息出错);
            return result;
        }

        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {

        Result result = new Result();

        try {
            result = GetContentInfo(webClient, cacheContainer, context, "3", "通话详单", "02", ProcessorCode.CALLRECORD_INFO.getCode());

        } catch (Exception e) {
            logger.error("==>采集通话详情出现异常:[{}]", e);
            result.setResult(StatusCode.解析通话详情出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {

        Result result = new Result();

        try {
            result = GetContentInfo(webClient, cacheContainer, context, "4", "短信记录", "03", ProcessorCode.SMS_INFO.getCode());
        } catch (Exception e) {
            logger.error("==>采集短信记录出现异常:[{}]", e);
            result.setResult(StatusCode.解析短信记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {

        Result result = new Result();

        try {
            result = GetContentInfo(webClient, cacheContainer, context, "5", "上网记录", "04", ProcessorCode.NET_INFO.getCode());
        } catch (Exception e) {
            logger.error("==>采集上网记录出现异常:[{}]", e);
            result.setResult(StatusCode.解析上网记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        try {
            url = String.format("http://shop.10086.cn/i/v1/fee/billinfo/%s", context.getUserName());
            String logFlag = String.format("==>6.采集账单信息," +
                    "第[{}]次尝试请求.....");
            Page resultPage = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES,
                    logFlag, null);
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.BILL_INFO.getCode(), resultPage);//把返回结果添加到缓存（重要）
            } else {
                result.setResult(StatusCode.解析账单信息出错);
                return result;
            }
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集账单信息出现异常:[{}]", e);
            result.setResult(StatusCode.解析账单信息出错);
            return result;
        }
        return result;
    }

    @Override
    protected Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        logger.info("==>[{}]6.1抓取套餐信息开始.....", context.getTaskId());
        Result result = new Result();
        try {
            String logFlag = String.format("==>[{}]6.1采集套餐信息,第[{}]次尝试请求.....", context.getTaskId());
            String url = "http://shop.10086.cn/i/v1/fee/planbal/" + context.getUserName() + "?_=" + Calendar.getInstance().getTimeInMillis();
            Page packageItemPage = getPage(webClient, url,
                    HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
            if (StringUtils.contains(packageItemPage.getWebResponse().getContentAsString(), "\"retCode\":\"000000\"")) {
                cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), packageItemPage);
                getLogger().info("==>[{}]6.2采集套餐信息成功.", context.getTaskId());
            } else {
                getLogger().info("==>[{}]6.3采集套餐信息结束,没有查询结果.", context.getTaskId());
            }
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集套餐信息出现异常:[{}]", e);
            result.setResult(StatusCode.采集套餐信息出错);
            return result;
        }
        return result;

    }

    @Override
    protected Result processUserFamilyMember(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        result.setSuccess();
        return result;
    }

    @Override
    protected Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        logger.info("==>[{}]8.1抓取充值记录开始.....", context.getTaskId());
        Result result = new Result();
        try {
            String logFlag = String.format("==>[{}]8.1采集充值记录信息,第[{}]次尝试请求.....", context.getTaskId());
            Calendar calendar = Calendar.getInstance();
            Date date = new Date(System.currentTimeMillis());
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -6);
            String url = "http://shop.10086.cn/i/v1/cust/his/" + context.getUserName() + "?startTime=" + DateFormatUtils.format(calendar, "yyyyMMdd") + "&endTime=" + DateFormatUtils.format(Calendar.getInstance(), "yyyyMMdd") + "&_=1494554440027";
            Page userRechargeItem = getPage(webClient, url,
                    HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
            if (StringUtils.contains(userRechargeItem.getWebResponse().getContentAsString(), "\"retCode\":\"000000\"")) {
                cacheContainer.putPage(ProcessorCode.RECHARGE_INFO.getCode(), userRechargeItem);
                getLogger().info("==>[{}]8.2采集充值记录信息成功.", context.getTaskId());
            } else {
                getLogger().info("==>[{}]8.3采集充值记录信息结束,没有查询结果.", context.getTaskId());
            }

            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集充值记录信息出现异常:[{}]", e);
            result.setResult(StatusCode.采集充值记录出错);
            return result;
        }
        return result;
    }


    /**
     * 根据不同的类别获取不同的数据
     *
     * @return
     * @description
     * @author heliang
     * @create 2016-09-20 16:32
     */
    private Result GetContentInfo(WebClient webClient, CacheContainer cacheContainer, Context context, String
            strIndex, String strType, String strCode, String ccType) {
        String url = "";
        Result result = new Result();
        List<Page> pageList = new ArrayList<>();
        HashMap<String, String> header = new HashMap<>();
        int iPageTotal = 0;
        int iRetryTime = 0;
        try {
            for (int i = 0; i >= -5; i--) {

                String _month = DateUtils.getDiffMonth("yyyyMM", i);
                String logFlag = String.format("==>" + strIndex + "." + (Math.abs(i) + 1) + "采集[" + _month + "]第1页" + strType + "信息,第[{}]次尝试请求.....");

                header.clear();
                header.put("Referer", "http://shop.10086.cn/i/?f=billdetailqry&welcome=" + context.getParam1());


                url = String.format("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/%s?callback=jQuery18308911999785481797_1494312492711&curCuror=1&step=100&qryMonth=%s&billType=%s&_=1433989116212",
                        context.getUserName(), _month, strCode);
                Page resultPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, header);
                if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "retCode\":\"000000")) {
                    pageList.add(resultPage);
                    logger.info("==>" + strIndex + "." + (Math.abs(i) + 1) + "采集[" + _month + "]" + strType + "成功.");
                } else if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "临时身份凭证不存在") && iRetryTime == 0 && strIndex.equals("3")) {
                    GetMsgCode(context, webClient);
                    i++;
                    iRetryTime++;
                    continue;
                } else {
                    logger.info("==>" + strIndex + "." + (Math.abs(i) + 1) + "采集[" + _month + "]" + strType + "失败.");
                    continue;
                }

                //获取总页数
                String strPageTotal = RegexUtils.matchValue("curCuror\":(.*?),", resultPage.getWebResponse().getContentAsString());
                if (StringUtils.isNotBlank(strPageTotal)) {
                    iPageTotal = Integer.parseInt(strPageTotal);
                }
                for (int j = 1; j < iPageTotal / 100 + 1; j++) {
                    url = String.format("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/%s?callback=jQuery18308911999785481797_1494312492711&curCuror=%s&step=100&qryMonth=%s&billType=%s&_=1433989116212"
                            , context.getUserName(), j + 100 * j, _month, strCode);
                    logFlag = String.format("==>" + strIndex + "." + (Math.abs(i) + 1) + "采集[" + _month + "]第" + (j + 1) + "页" + strType + "信息,第[{}]次尝试请求.....");
                    resultPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, header);
                    if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "retCode\":\"000000")) {
                        pageList.add(resultPage);
                        logger.info("==>" + strIndex + "." + (Math.abs(i) + 1) + "采集[" + _month + "]第" + (j + 1) + "页" + strType + "成功.");
                    } else {
                        logger.info("==>" + strIndex + "." + (Math.abs(i) + 1) + "采集[" + _month + "]第" + (j + 1) + "页" + strType + "失败.");
                    }
                }
            }
            cacheContainer.putPages(ccType, pageList);

            result.setSuccess();

        } catch (Exception ex) {
            logger.error("==>采集信息出现异常:[{}]", ex);
            result.setResult(StatusCode.采集信息出错);
            return result;
        }
        return result;

    }

    public Result GetMsgCode(Context context, WebClient webClient) {
        HashMap<String, String> header = new HashMap<>();
        Result result = new Result();
        String url = "";
        Page page = null;
        try {
            header.put("Referer", "http://shop.10086.cn/i/?welcome=" + context.getParam1());
            String verifyCode = getVerifyCode(webClient, "http://shop.10086.cn/i/authImg?t=0.15420815539234867", header);
            header.clear();
            header.put("Referer", "http://shop.10086.cn/i/?f=home&welcome=" + context.getParam1());
            url = "https://shop.10086.cn/i/v1/fee/detbillrandomcodejsonp/" + context.getUserName() + "?callback=jQuery18308911999785481797_1494312492711&_=" + Calendar.getInstance().getTimeInMillis();
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            if (page.getWebResponse().getContentAsString().contains("\"retMsg\":\"success\"")) {
                result.setResult(StatusCode.爬取中);
                context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
                sendUpdateLog(result, context);
                boolean isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
                if (isFlag) {
                    String verify_code = rotation(context,webClient,120);
                    if ("SMS_CODE".equals(verify_code)) {
                        header.put("Referer", "http://shop.10086.cn/i/?f=home&welcome=" + context.getParam1());
                        url = "https://shop.10086.cn/i/v1/fee/detbillrandomcodejsonp/" + context.getUserName() + "?callback=jQuery18308911999785481797_1494312492711&_=" + Calendar.getInstance().getTimeInMillis();
                        page = getPage(webClient, url, HttpMethod.GET, null, header);
                        isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
                        if (isFlag) {
                            verify_code = rotation(context,webClient,120);
                        }
                    }
                    if (StringUtils.isNotBlank(verify_code) && StringUtils.isNotEmpty(verifyCode)) {
                        context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_IMG);
                        isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_IMG, verifyCode);
                        if (isFlag) {
                            String verifyImgCode = rotation(context,webClient,120);
                            if (StringUtils.isNotBlank(verifyImgCode)) {
                                isFlag = verificationSubmit(verify_code, verifyImgCode, webClient, context);
                                if (isFlag) {
                                    logger.info("==>信息验证成功!");
                                    result.setSuccess();
                                } else {
                                    logger.info("==>[{}]1.1证码失败.", context.getTaskId());
                                    result.setResult(StatusCode.验证短信验证码错误);
                                    return result;
                                }
                            } else {
                                logger.info("==>[{}]1.1获取图片验证码失败.", context.getTaskId());
                                result.setResult(StatusCode.获取图片验证码错误);
                                return result;
                            }
                        } else {
                            logger.info("==>[{}]1.1获取图片验证码失败.", context.getTaskId());
                            result.setResult(StatusCode.获取图片验证码错误);
                            return result;
                        }
                        logger.info("==>[{}]1.1信息验证结束.", context.getTaskId());
                        return result;


                    } else {
                        logger.info("==>[{}]1.1获取短信验证码失败.", context.getTaskId());
                        result.setResult(StatusCode.发送短信验证码失败);
                        return result;
                    }
                } else {
                    logger.info("==>[{}]1.1发送短信验证码失败.", context.getTaskId());
                    result.setResult(StatusCode.发送短信验证码失败);
                    return result;
                }

            } else {
                logger.info("==>黑龙江移动动态密码发送失败!");
                return new Result(StatusCode.发送短信验证码失败);
            }

        } catch (Exception ex) {
            logger.error("==短信验证码异常:[{}]", ex);
            result.setResult(StatusCode.解析办理业务信息出错);
            return result;
        }

    }

    private boolean verificationSubmit(String verifyCode, String verifyImgCode, WebClient webClient, Context context) {
        HashMap<String, String> header = new HashMap<>();
        String pwdTempRandCode = JavaScriptUtils.invoker("js/ShanDongCmccDes.js", "toBase64encode", verifyCode);
        String pwdTempSerCode = JavaScriptUtils.invoker("js/ShanDongCmccDes.js", "toBase64encode", context.getPassword());
        header.put("Referer", "http://shop.10086.cn/i/?welcome=" + context.getParam1());
        String url = "http://shop.10086.cn/i/v1/res/precheck/" + context.getUserName() + "?captchaVal=" + verifyImgCode + "&_=" + Calendar.getInstance().getTimeInMillis();
        Page page = getPage(webClient, url, HttpMethod.GET, null, header);
        if (page.getWebResponse().getContentAsString().contains("输入正确，校验成功")) {
            header.clear();
            header.put("Referer", "http://shop.10086.cn/i/?welcome=" + context.getParam1());
            url = "https://shop.10086.cn/i/v1/fee/detailbilltempidentjsonp/" + context.getUserName() + "?callback=jQuery18308911999785481797_1494312492711&pwdTempSerCode=" + pwdTempSerCode + "&pwdTempRandCode=" + pwdTempRandCode + "&captchaVal=" + verifyImgCode + "&_=" + Calendar.getInstance().getTimeInMillis();
            page = getPage(webClient, url, HttpMethod.GET, null, header);

            if (StringUtils.contains(page.getWebResponse().getContentAsString(), "认证成功")) {
                logger.info("==>短信验证码正确.");

            } else {
                logger.info("短信验证码校验失败，请稍后重试！");
                //短信验证码输入错误
                return false;
            }
        } else {
            logger.info("图片验证码校验失败，请稍后重试！");
            //短信验证码输入错误
            return false;
        }


        return true;
    }


    public void StrConvertToCookieContainer(WebClient webClient, String domain) {

        for (Cookie cookie : webClient.getCookieManager().getCookies()) {
            Cookie cookieNew = new Cookie(domain, cookie.getName(), cookie.getValue());
            webClient.getCookieManager().addCookie(cookieNew);
        }
    }


    @Override
    protected Result loginout(WebClient webClient) {
        Result result = new Result();
        String url = "http://shop.10086.cn/i/v1/auth/userlogout?_=1497353867389";
        Page page = getPage(webClient, url, HttpMethod.GET, null, null);
        if(page.getWebResponse().getContentAsString().contains("000000")){
            result.setSuccess();
        }
        return result;
    }


    @Override
    protected Logger getLogger() {
        return logger;
    }
}
