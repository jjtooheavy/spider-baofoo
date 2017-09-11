package com.xinyan.spider.isp.mobile.processor.cmcc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.cmcc.HuBeiCmccParser;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;

/**
 * @Description:湖北移动处理类
 * @author: heliang
 * @date: 2016-09-12 19:12
 * @version: v1.0
 */
@Component
public class HuBeiCmccPssor extends AbstractProcessor {

    @Autowired
    private HuBeiCmccParser parser;

    protected static Logger logger = LoggerFactory.getLogger(HuBeiCmccPssor.class);

    @Override
    public Result doLogin(WebClient webClient, Context context) {
        Result result = new Result();
        try {
            logger.info("==>[{}]1.1获取验证码开始.....", context.getTaskId());
            getPage(webClient, "https://hb.ac.10086.cn/SSO/loginbox?service=servicenew&style=mymobile&continue=http://www.hb.10086.cn/servicenew/index.action&ignoreGroupCheck=true", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            String verifyCode = getVerifyCode(webClient, "https://hb.ac.10086.cn/SSO/img?codeType=0", null);
            if (StringUtils.isNotEmpty(verifyCode)) {
                result.setResult(StatusCode.请输入图片验证码);
                context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_IMG);//需要设置回调子状态
                sendValidataMsg(result, context, false, webClient, Constants.DEQUEUE_TIME_OUT, verifyCode);
                logger.info("==>[{}]1.1获取验证码结束.", context.getTaskId());
                return result;
            } else {
                logger.info("==>[{}]1.1获取图片验证码失败.", context.getTaskId());
                result.setResult(StatusCode.获取图片验证码错误);
                return result;
            }
        } catch (Exception e) {
            logger.error("==>登录出现异常", e);
            result.setResult(StatusCode.获取图片验证码错误);
            return result;
        }finally {
            if(!StatusCode.请输入图片验证码.getCode().equals(result.getCode())){//不需要回调
                sendLoginMsg(result, context, false, webClient);
            }
            close(webClient);
        }
    }

    @Override
    protected Result doLoginByIMG(WebClient webClient, Context context) {
        Result result = new Result();
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        logger.info("==>[{}]1.2用户登陆开始.....", context.getTaskId());
        try {
            header.put("Referer","https://hb.ac.10086.cn/SSO/loginbox?service=servicenew&style=mymobile&continue=http://www.hb.10086.cn/servicenew/index.action&ignoreGroupCheck=true");
            reqParam.clear();
            reqParam.add(new NameValuePair("accountType", "0"));
            reqParam.add(new NameValuePair("username", context.getUserName()));
            reqParam.add(new NameValuePair("passwordType", "1"));
            reqParam.add(new NameValuePair("password", context.getPassword()));
            reqParam.add(new NameValuePair("smsRandomCode", ""));
            reqParam.add(new NameValuePair("emailusername", "请输入登录帐号"));
            reqParam.add(new NameValuePair("emailpassword", ""));
            reqParam.add(new NameValuePair("validateCode", context.getUserInput()));
            reqParam.add(new NameValuePair("action", "/SSO/loginbox"));
            reqParam.add(new NameValuePair("style", "mymobile"));
            reqParam.add(new NameValuePair("service", "servicenew"));
            reqParam.add(new NameValuePair("continue", "http://www.hb.10086.cn/servicenew/index.action"));
            reqParam.add(new NameValuePair("submitMode", "login"));

            String url = "https://hb.ac.10086.cn/SSO/loginbox";
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            logger.info("==>[{}]1.2用户登陆结束.", context.getTaskId());

            logger.info("==>[{}]1.2用户登陆结果验证开始.....", context.getTaskId());
            //登陆成功
            if (StringUtils.contains(page.getWebResponse().getContentAsString(), "name=\"SAMLart\"")) {
                result.setSuccess();
                result.setData(result);
            } else {
                logger.info("登陆失败：" + page.getWebResponse().getContentAsString());
                if (StringUtils.contains(page.getWebResponse().getContentAsString(), "输入验证码有误，请重新输入")) {
                    result.setResult(StatusCode.图片验证码错误);
                    return result;
                } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "帐号被锁定")) {
                    result.setResult(StatusCode.帐号被锁定);
                    return result;
                } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "您连续输入密码错误次数已达")) {
                    result.setResult(StatusCode.服务密码错误);
                    return result;
                } else {
                    result.setResult(StatusCode.登陆失败);
                    return result;
                }
            }
            logger.info("==>[{}]1.2用户登陆结果验证结束.", context.getTaskId());


            logger.info("==>[{}]1.2登陆成功跳转页面开始.....", context.getTaskId());

            String SAMLart = RegexUtils.matchValue("name=\"SAMLart\"\r\n\t\t\t\tvalue=\"(.*?)\"", page.getWebResponse().getContentAsString());
            String RelayState = RegexUtils.matchValue("name=\"RelayState\"\r\n\t\t\t\tvalue=\"(.*?)\"", page.getWebResponse().getContentAsString());
            String PasswordType = RegexUtils.matchValue("name=\"PasswordType\"\r\n\t\t\t\tvalue=\"(.*?)\"", page.getWebResponse().getContentAsString());
            context.setParam1(SAMLart);
            if (StringUtils.isBlank(SAMLart) || StringUtils.isBlank(RelayState) || StringUtils.isBlank(PasswordType)) {
                //获取SAMLart，RelayState，PasswordType值失败
                result.setResult(StatusCode.获取SAMLart失败);
                return result;
            }

            reqParam.clear();
            reqParam.add(new NameValuePair("RelayState", RelayState));
            reqParam.add(new NameValuePair("SAMLart", SAMLart));
            reqParam.add(new NameValuePair("artifact", SAMLart));
            reqParam.add(new NameValuePair("accountType", "0"));
            reqParam.add(new NameValuePair("PasswordType", PasswordType));
            reqParam.add(new NameValuePair("errorMsg", ""));
            reqParam.add(new NameValuePair("errFlag", ""));
            reqParam.add(new NameValuePair("telNum", ""));

            url = "http://www.hb.10086.cn/servicenew/postLogin.action?timeStamp=" + Calendar.getInstance().getTimeInMillis();
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);


//            url = "http://www.hb.10086.cn/my/account/myStar.action";
//            page = getPage(webClient, url, HttpMethod.GET, null, null);
//
//            //跳转后值变化
//            SAMLart = RegexUtils.matchValue("name=\"SAMLart\"\r\n\t\t\t\tvalue=\"(.*?)\"", page.getWebResponse().getContentAsString());
//            RelayState = RegexUtils.matchValue("name=\"RelayState\"\r\n\t\t\t\tvalue=\"(.*?)\"", page.getWebResponse().getContentAsString());

            //很重要
//            reqParam.clear();
//            reqParam.add(new NameValuePair("RelayState", RelayState));
//            reqParam.add(new NameValuePair("SAMLart", SAMLart));
//            reqParam.add(new NameValuePair("PasswordType", ""));
//            reqParam.add(new NameValuePair("errorMsg", ""));
//            reqParam.add(new NameValuePair("errFlag", ""));
//            reqParam.add(new NameValuePair("telNum", ""));
//            url = "http://www.hb.10086.cn/my/notify.action";
//            page = getPage(webClient, url, HttpMethod.POST, reqParam, null);
            logger.info("==>[{}]1.2登陆成功跳转页面结束.", context.getTaskId());

            logger.info("==>[{}]1.2正在请求发送短信验证码开始.....", context.getTaskId());
            url = "http://www.hb.10086.cn/my/account/smsRandomPass!sendSmsCheckCode.action?menuid=myDetailBill";
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            if (StringUtils.contains(page.getWebResponse().getContentAsString(), "\"result\":\"1\"")) {
                logger.info("==>[{}]1.2已经发送短信验证码.", context.getTaskId());
                result.setResult(StatusCode.请输入短信验证码);
                context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);//需要设置回调子状态
                sendValidataMsg(result, context, false, webClient, Constants.DEQUEUE_TIME_OUT, null);
                logger.info("==>[{}]1.2正在请求发送短信验证码结束.", context.getTaskId());
                return result;
            } else {
                logger.info("==>[{}]1.2湖北移动动态密码发送失败!", context.getTaskId());
                result.setResult(StatusCode.发送短信验证码失败);
                return result;
            }


        } catch (Exception e) {
            logger.error("==>登录出现异常", e);
            result.setResult(StatusCode.登陆出错);
            return result;
        }finally {
            if(!StatusCode.请输入短信验证码.getCode().equals(result.getCode())){//不需要回调
                sendLoginMsg(result, context, false, webClient);
                if(!result.isSuccess()){
                    loginout(webClient);
                }
            }

            close(webClient);
        }

    }

    @Override
    protected Result doLoginBySMS(WebClient webClient, Context context) throws Exception {
        List<NameValuePair> reqParam = new ArrayList<>();
        Result result = new Result();
        try {
            String rsaEmpoent = "10001";
            String rsaModule = "8a4928b7e4ce5943230539120cb6ee7a64000034b11b923a91faf8c381dd09b4a9a9a6fa02ca0bd3b90576ac1498983f7c78d8f8f5126a24a30f75eac86815c3430fe3e77f81a326d0d2f7ffbfe285bb368175d66c29777ec031c0c75f64da92aa43866fdfa2597cfb4ce614f450e95670be7cc27e4b05b7a48ca876305e5d51";
            String password = JavaScriptUtils.invoker("js/HuBeiCmccDes.js", "encryptedString", rsaEmpoent, rsaModule, context.getPassword());
            String andSmsCode = JavaScriptUtils.invoker("js/HuBeiCmccDes.js", "encryptedString", rsaEmpoent, rsaModule, context.getUserInput());
            reqParam.add(new NameValuePair("detailBean.billcycle", DateUtils.getCustomYearMont("yyyyMM")));
            reqParam.add(new NameValuePair("detailBean.selecttype", "0"));
            reqParam.add(new NameValuePair("detailBean.flag", "GSM"));
            reqParam.add(new NameValuePair("selecttype", "全部查询"));
            reqParam.add(new NameValuePair("flag", "通话详单"));
            reqParam.add(new NameValuePair("groupId", "tabs3"));
            reqParam.add(new NameValuePair("detailBean.password", password));
            reqParam.add(new NameValuePair("detailBean.chkey", andSmsCode));
            String url = "http://www.hb.10086.cn/my/billdetails/billDetailQry.action?postion=outer";
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

            if (StringUtils.contains(page.getWebResponse().getContentAsString(), "您输入的服务密码或短信验证码错误或者过期")) {
                logger.info("==>[{}]1.3短信验证码错误.", context.getTaskId());
                result.setResult(StatusCode.短信验证码错误);
                return result;
            } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "如果您尝试多次仍然无法成功")) {
                result.setResult(StatusCode.人工为您处理反馈);
                return result;
            } else {
                result.setSuccess();
                logger.info("==>[{}]1.3短信验证码正确.", context.getTaskId());
            }
        } catch (Exception e) {
            logger.error("==>登录出现异常", e);
            result.setResult(StatusCode.登陆出错);
            return result;
        } finally {
            sendLoginMsg(result, context, false, webClient);
            if(!result.isSuccess()){
                loginout(webClient);
            }
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
        try {
            //1.采集通话详单
            getLogger().info("==>[{}]1.采集通话详单开始.....", context.getTaskId());
            result = processCallRecordInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]1.采集通话详单结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集通话详单失败
                return result;
            }
            //2.采集基础信息
            getLogger().info("==>[{}]2.采集基本信息开始.....", context.getTaskId());
            result = processBaseInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]2.采集基本信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集基础信息失败
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
//            getLogger().info("==>[{}]7.采集亲情号码开始.....", context.getTaskId());
//            result = processUserFamilyMember(webClient, context, carrierInfo, cacheContainer);
//            getLogger().info("==>[{}]7.采集亲情号码结束{}.", context.getTaskId(), result);
//            if (!result.isSuccess()) {
//                return result;
//            }

            //8.采集充值记录
            getLogger().info("==>[{}]8.采集充值记录开始.....", context.getTaskId());
            result = processUserRechargeItemInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]8.采集充值记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }
            result.setData(carrierInfo);
        } catch (Exception ex) {
            getLogger().error("==>[{}]数据抓取出现异常", context.getTaskId(), ex);
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
            getLogger().info("==>[{}]9.解析数据异常.", context.getTaskId());
        } finally {
            sendAnalysisMsg(result, context);
            close(webClient);
        }

        getLogger().info("==>[{}]0.数据抓取结束{}\n,详情:{}", context.getTaskId(), result, result.getData());
        return result;
    }

    public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {

        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        try {

            logger.info("==>[{}]2.1抓取个人信息开始.....", context.getTaskId());
            url = "http://www.hb.10086.cn/my/account/basicInfoAction.action";
            Page resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]2.1抓取个人信息结束.", context.getTaskId());

            logger.info("==>[{}]2.2抓取星级信息开始.....", context.getTaskId());
            url = "http://www.hb.10086.cn/my/account/myStar.action";
            resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.VIP_LVL.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]2.2抓取星级信息结束.", context.getTaskId());

            logger.info("==>[{}]2.3抓取套餐名称开始.....", context.getTaskId());
            url = "http://www.hb.10086.cn/my/balance/qryPriv.action";
            resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.POINTS_VALUE.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]2.3抓取套餐名称结束.", context.getTaskId());

            logger.info("==>[{}]2.4抓取余额信息开始.....", context.getTaskId());
            url = "http://www.hb.10086.cn/my/balance/queryBalance.action";
            resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]2.4抓取余额信息结束.", context.getTaskId());


            result.setSuccess();

        } catch (Exception e) {
            logger.error("==>采集基本信息出现异常", e);
            result.setResult(StatusCode.解析基础信息出错);
            return result;
        }

        return result;
    }


    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        try {

            List<Page> pageList = new ArrayList<>();

            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String startDT = DateUtils.getFirstDay("yyyyMMdd", i);
                String endDT = DateUtils.getLastDay("yyyyMMdd", i);

                reqParam.clear();
                reqParam.add(new NameValuePair("menuid", "myDetailBill"));
                reqParam.add(new NameValuePair("detailBean.billcycle", month));
                reqParam.add(new NameValuePair("detailBean.startdate", startDT));
                reqParam.add(new NameValuePair("detailBean.enddate", endDT));
                reqParam.add(new NameValuePair("detailBean.flag", "GSM"));
                reqParam.add(new NameValuePair("detailBean.selecttype", "0"));
                url = "http://www.hb.10086.cn/my/billdetails/generateNewDetailExcel.action";
                String logFlag = String.format("==>3." + (Math.abs(i) + 1) + "采集[" + month + "]通话详单,第[{}]次尝试请求.....");
                Page resultPage = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                pageList.add(resultPage);
                logger.info("==>3." + (Math.abs(i) + 1) + "采集[" + month + "]通话详单成功.");
            }

            cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集通话详情出现异常", e);
            result.setResult(StatusCode.解析通话详情出错);
            return result;
        }
        return result;
    }

    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        try {
            List<Page> pageList = new ArrayList<>();
            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String startDT = DateUtils.getFirstDay("yyyyMMdd", i);
                String endDT = DateUtils.getLastDay("yyyyMMdd", i);
                reqParam.clear();
                reqParam.add(new NameValuePair("menuid", "myDetailBill"));
                reqParam.add(new NameValuePair("detailBean.billcycle", month));
                reqParam.add(new NameValuePair("detailBean.startdate", startDT));
                reqParam.add(new NameValuePair("detailBean.enddate", endDT));
                reqParam.add(new NameValuePair("detailBean.flag", "SMS"));
                reqParam.add(new NameValuePair("detailBean.selecttype", "0"));

                url = "http://www.hb.10086.cn/my/billdetails/generateNewDetailExcel.action";
                String logFlag = String.format("==>4." + (Math.abs(i) + 1) + "采集[" + month + "]短信记录,第[{}]次尝试请求.....");
                Page resultPage = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                pageList.add(resultPage);
                logger.info("==>[{}]3." + (Math.abs(i) + 1) + "采集[" + month + "]短信记录成功.", context.getTaskId());
            }
            cacheContainer.putPages(ProcessorCode.SMS_INFO.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集短信记录出现异常", e);
            result.setResult(StatusCode.解析短信记录出错);
            return result;
        }
        return result;
    }

    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {

        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();

        try {

            List<Page> pageList = new ArrayList<>();

            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String startDT = DateUtils.getFirstDay("yyyyMMdd", i);
                String endDT = DateUtils.getLastDay("yyyyMMdd", i);

                reqParam.clear();
                reqParam.add(new NameValuePair("menuid", "myDetailBill"));
                reqParam.add(new NameValuePair("detailBean.billcycle", month));
                reqParam.add(new NameValuePair("detailBean.startdate", startDT));
                reqParam.add(new NameValuePair("detailBean.enddate", endDT));
                reqParam.add(new NameValuePair("detailBean.flag", "GPRSWLAN"));
                reqParam.add(new NameValuePair("detailBean.selecttype", "0"));

                url = "http://www.hb.10086.cn/my/billdetails/generateNewDetailExcel.action";
                String logFlag = String.format("==>5." + (Math.abs(i) + 1) + "采集[" + month + "]上网记录,第[{}]次尝试请求.....");
                Page resultPage = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                pageList.add(resultPage);
                logger.info("==>[{}]4." + (Math.abs(i) + 1) + "采集[" + month + "]上网记录成功.", context.getTaskId());
            }

            cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集上网记录出现异常", e);
            result.setResult(StatusCode.解析上网记录出错);
            return result;
        }
        return result;
    }

    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {

        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();

        try {
            List<Page> pageList = new ArrayList<>();

            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String qryMonthType = "";
                String current = DateUtils.getCurrentMonth();   //当前月
                if (month.equals(current)) {
                    qryMonthType = "current";
                } else {
                    qryMonthType = "history";
                }
                url = "http://www.hb.10086.cn/my/balance/showbillMixQuery.action?postion=outer";

                reqParam.clear();
                reqParam.add(new NameValuePair("qryMonthType", qryMonthType));
                reqParam.add(new NameValuePair("theMonth", month));
                reqParam.add(new NameValuePair("menuid", "myBill"));
                reqParam.add(new NameValuePair("groupId", "tabs3"));
                String logFlag = String.format("==>6." + (Math.abs(i) + 1) + "采集[" + month + "]账单信息,第[{}]次尝试请求.....");
                Page resultPage = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                pageList.add(resultPage);
                logger.info("==>[{}]5." + (Math.abs(i) + 1) + "采集[" + month + "]账单信息成功.", context.getTaskId());
            }

            cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pageList);//把返回结果添加到缓存（重要）
            result.setSuccess();

        } catch (Exception e) {
            logger.error("==>采集账单信息出现异常", e);
            result.setResult(StatusCode.解析账单信息出错);
            return result;
        }
        return result;
    }

//套餐信息与账单信息是一个页面
    public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        String url = "";
        try {
            cacheContainer.putPages(ProcessorCode.PACKAGE_ITEM.getCode(), cacheContainer.getPages(ProcessorCode.BILL_INFO.getCode()));//把返回结果添加到缓存（重要）
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集套餐信息出现异常", e);
            result.setResult(StatusCode.采集套餐信息出错);
            return result;
        }
        return result;
    }

    public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        String url = "";
        List<Page> pageList = new ArrayList<>();
        try {
            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String startDT = DateUtils.getFirstDay("yyyyMMdd", i);
                String endDT = DateUtils.getLastDay("yyyyMMdd", i);
                if (i == 0) {
                    endDT = DateUtils.getLastDay("yyyyMMdd", i).compareTo(DateUtils.getCurrentDate1()) > 0 ? DateUtils.getCurrentDate1() : DateUtils.getLastDay("yyyyMMdd", i);
                }
                url = "http://shop.10086.cn/i/v1/cust/his/" + context.getUserName() + "?startTime=" + startDT + "&endTime=" + endDT + "&_=" + Calendar.getInstance().getTimeInMillis();
                String logFlag = String.format("==>6.采集[" + DateUtils.getCurrentMonth2() + "]充值记录信息,第[{}]次尝试请求.....");
                List<NameValuePair> respParam = new ArrayList<>();
                respParam.add(new NameValuePair("startTime",startDT));
                respParam.add(new NameValuePair("endTime",endDT));
                respParam.add(new NameValuePair("_",Calendar.getInstance().getTimeInMillis()+""));
                Page ssoPage = getPage(webClient, "https://login.10086.cn/SSOCheck.action?channelID=12003&backUrl=http://shop.10086.cn/i/?f=home", HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
                Page logininfoPage = getPage(webClient, "http://shop.10086.cn/i/v1/auth/loginfo?_="+Calendar.getInstance().getTimeInMillis(), HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, null);


                Page resultPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
                pageList.add(resultPage);
                logger.info("==>[{}]8.采集[" + month + "]月充值记录信息成功.", context.getTaskId());
            }
            cacheContainer.putPages(ProcessorCode.RECHARGE_INFO.getCode(), pageList);//把返回结果添加到缓存（重要）
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集充值信息出现异常", e);
            result.setResult(StatusCode.解析充值记录出错);
            return result;
        }
        return result;
    }


    @Override
    protected Result loginout(WebClient webClient) {
        Result result = new Result();
        String url = "https://hb.ac.10086.cn/logout";
        Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
        result.setSuccess();
        return result;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}


