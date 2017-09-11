package com.xinyan.spider.isp.mobile.processor.cmcc;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.cmcc.FuJianCmccParser;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description:福建移动处理类
 * @author: jiangmengchen
 * @date: 2017-05-11 15:53
 * @version: v1.0
 */
@Component
public class FuJianCmccPssor extends AbstractProcessor {

    @Autowired
    private FuJianCmccParser parser;

    protected static Logger logger = LoggerFactory.getLogger(FuJianCmccPssor.class);


    @Override
    public Result doLogin(WebClient webClient, Context context) {
        Result result = new Result();
        try {
            logger.info("==>[{}]1.1获取图片验证码开始.....", context.getTaskId());
            String verifyCode = getVerifyCode(webClient, "https://fj.ac.10086.cn/common/image.jsp?l=" + Math.random(), null);
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
            logger.error("==>登录出现异常:[{}]", e);
            result.setResult(StatusCode.获取图片验证码错误);
            return result;
        } finally {
            if (!StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {//不需要回调
                result.setResult(StatusCode.请输入图片验证码);
                sendLoginMsg(result, context, false, webClient);
            }
            close(webClient);
        }
    }

    @Override
    public Result doLoginByIMG(WebClient webClient, Context context) {
        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        try {

            logger.info("==>1.1登陆前预处理开始.....");

            String check = "https://fj.ac.10086.cn/login";
            Page checkPage = getPage(webClient, check, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            String content = checkPage.getWebResponse().getContentAsString();
            content = content.substring(content.indexOf("(") + 2, content.lastIndexOf(")") - 1);
            Page page = getPage(webClient, content, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);
            String spid = RegexUtils.matchValue("name=\"spid\" value=\"(.*?)\"", StringUtils.convertStreamToString(page.getWebResponse().getContentAsStream()));
            if (StringUtils.isBlank(spid)) {
                result.setResult(StatusCode.官网正在维护);
                return result;
            }
            logger.info("==>1.1登陆前预处理结束.");

            logger.info("==>1.2用户名和密码加密开始.....");
            String rsaPassword = JavaScriptUtils.invoker("js/FuJianCmccDes.js", "enString", context.getPassword());
            logger.info("==>1.2用户名和密码加密结束.");

            logger.info("==>1.4用户登陆开始.....");
            reqParam.clear();
            reqParam.add(new NameValuePair("type", "B"));
            reqParam.add(new NameValuePair("backurl", "https://fj.ac.10086.cn/4login/backPage.jsp"));
            reqParam.add(new NameValuePair("errorurl", "https://fj.ac.10086.cn/4login/errorPage.jsp"));
            reqParam.add(new NameValuePair("spid", spid));
            reqParam.add(new NameValuePair("RelayState", ""));
            reqParam.add(new NameValuePair("mobileNum", context.getUserName()));
            reqParam.add(new NameValuePair("servicePassword", rsaPassword));
            reqParam.add(new NameValuePair("smsValidCode", ""));
            reqParam.add(new NameValuePair("validCode", context.getUserInput()));
            reqParam.add(new NameValuePair("Password-type", ""));
            reqParam.add(new NameValuePair("button", "登  录"));

            url = "https://fj.ac.10086.cn/Login";
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            logger.info("==>1.4用户登陆结束.");
            logger.info("==>1.5用户登陆结果验证开始.....");
            if (page.getWebResponse().getContentAsString().contains("您输入的密码错误")) {
                logger.error("==>用户登录失败");
                result.setResult(StatusCode.用户名或密码错误);
                return result;
            } else {
                logger.info("==>用户登录成功.");
                String uid = page.getWebResponse().getContentAsString();
                uid = uid.substring(uid.indexOf("(") + 2, uid.lastIndexOf(")") - 1);
                header.put("Referer", "https://fj.ac.10086.cn/Login");
                String samLart = uid.substring(uid.indexOf("SAMLart") + 10, uid.indexOf("SAMLart") + 54);
                context.setParam1(spid);
                context.setParam2(samLart);
                result.setSuccess();
            }

        } catch (Exception e) {
            logger.error("==>登录出现异常:[{}]", e);
            result.setResult(StatusCode.登陆出错);
            return result;
        } finally {
            if (!StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {//不需要回调
                sendLoginMsg(result, context, false, webClient);
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
            //2.采集基础信息
            getLogger().info("==>[{}]2.采集基本信息开始.....", context.getTaskId());
            result = processBaseInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]2.采集基本信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集基础信息失败
                return result;
            }
            //1.采集通话详单
            getLogger().info("==>[{}]1.采集通话详单开始.....", context.getTaskId());
            result = processCallRecordInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]1.采集通话详单结束{}.", context.getTaskId(), result);
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
        try {
            logger.info("==>2.1抓取个人信息开始.....");
            logger.info("==>[{}]2.1正在请求发送短信验证码开始.....", context.getTaskId());
            result.setResult(StatusCode.爬取中);
            context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
            sendUpdateLog(result, context);

            String url = "https://fj.ac.10086.cn/SMSCodeSend";
            HashMap<String, String> header = new HashMap<>();
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("spid", context.getParam1()));
            reqParam.add(new NameValuePair("mobileNum", context.getUserName()));
            reqParam.add(new NameValuePair("validCode", "0000"));
            reqParam.add(new NameValuePair("errorurl", "http://www.fj.10086.cn:80/my/login/send.jsp"));
            header.put("Referer", "http://www.fj.10086.cn/my/index.jsp?id_type=YANZHENGMA");
            String imgResult = "", smsResult = "";
            Page page = getPage(webClient, url, HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            boolean codeIsSend = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
            if (codeIsSend) {
                smsResult = rotation(context,webClient,120);
                if (StringUtils.isBlank(smsResult)) {
                    result.setResult(StatusCode.获取短信验证码失败);
                    return result;
                } else {
                    logger.info("==>获取个人信息短信验证码成功.[{}]", context.getTaskId());
                }
            } else {
                result.setResult(StatusCode.发送短信验证码失败);
                return result;
            }

            logger.info("==>获取个人信息图片验证码开始.[{}]", context.getTaskId());
            String code = getVerifyCode(webClient, "https://fj.ac.10086.cn/common/image.jsp?id=" + Math.random(), header);
            boolean imgIsSend = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_IMG, code);
            if (imgIsSend) {
                imgResult = rotation(context,webClient,120);
                if (StringUtils.isBlank(imgResult)) {
                    result.setResult(StatusCode.获取图片验证码错误);
                    return result;
                } else {
                    logger.info("==>获取个人信息图片验证码成功.[{}]", context.getTaskId());
                }
            } else {
                result.setResult(StatusCode.发送图片验证码失败);
                return result;
            }
            reqParam.clear();
            reqParam.add(new NameValuePair("s02", "false"));
            reqParam.add(new NameValuePair("Password", ""));
            reqParam.add(new NameValuePair("Password-type  ", ""));
            reqParam.add(new NameValuePair("spid", context.getParam1()));
            reqParam.add(new NameValuePair("validCode", ""));
            reqParam.add(new NameValuePair("servicePassword", ""));
            reqParam.add(new NameValuePair("n1", "1"));
            reqParam.add(new NameValuePair("sso", "0"));
            reqParam.add(new NameValuePair("RelayState", "1"));
            reqParam.add(new NameValuePair("ocs_url", ""));
            reqParam.add(new NameValuePair("sp_id", ""));
            reqParam.add(new NameValuePair("do_login_type", ""));
            reqParam.add(new NameValuePair("isValidateCode", "1"));
            reqParam.add(new NameValuePair("type", "A"));
            reqParam.add(new NameValuePair("smscode", smsResult));
            reqParam.add(new NameValuePair("mobileNum", context.getUserName()));
            reqParam.add(new NameValuePair("agentcode", ""));
            reqParam.add(new NameValuePair("backurl", "http://www.fj.10086.cn:80/my/ssoAssert.jsp?typesso=C&CALLBACK_URL=http://www.fj.10086.cn:80/my/user/getUserInfo.do"));
            reqParam.add(new NameValuePair("errorurl", "http://www.fj.10086.cn:80/my/login/send.jsp"));
            reqParam.add(new NameValuePair("smsValidCode", smsResult));
            reqParam.add(new NameValuePair("smscode1", smsResult));
            reqParam.add(new NameValuePair("validCodes", imgResult));

            page = getPage(webClient, "https://fj.ac.10086.cn/Login", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);

            String content = page.getWebResponse().getContentAsString();
            content = content.substring(content.indexOf("(") + 2, content.lastIndexOf(")") - 1);

            page = getPage(webClient, content, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);

            String userInfoUrl = page.getWebResponse().getContentAsString();
            userInfoUrl = userInfoUrl.substring(userInfoUrl.indexOf("location.href") + 17, userInfoUrl.lastIndexOf("\""));
            page = getPage(webClient, userInfoUrl, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);

            logger.info("==>2.1抓取个人信息结束.");
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集基本信息出现异常:[{}]", e);
            result.setResult(StatusCode.采集基本信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            List<Page> callPages = processBillAndNetAndSms(webClient, context, 2);
            cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode(), callPages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集通话详情出现异常:[{}]", e);
            result.setResult(StatusCode.采集通话详情出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            List<Page> smsPags = processBillAndNetAndSms(webClient, context, 4);
            cacheContainer.putPages(ProcessorCode.SMS_INFO.getCode(), smsPags);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集短信记录出现异常:[{}]", e);
            result.setResult(StatusCode.采集短信记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            List<Page> netPags = processBillAndNetAndSms(webClient, context, 8);
            cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), netPags);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集上网记录出现异常:[{}]", e);
            result.setResult(StatusCode.采集上网记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {

        Result result = new Result();

        try {
            Page page = getPage(webClient, "http://www.fj.10086.cn/my/?SAMLart=" + context.getParam2() + "&RelayState=", HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);

            page = getPage(webClient, "http://www.fj.10086.cn/my/query/queryJTBill.do", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            cacheContainer.putPage(ProcessorCode.BILL_INFO.getCode(), page);
            result.setSuccess();

        } catch (Exception e) {
            logger.error("==>采集账单信息出现异常:[{}]", e);
            result.setResult(StatusCode.采集账单记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {

        Result result = new Result();
        String url = "";
        HashMap<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        try {
            Page page = getPage(webClient, "http://www.fj.10086.cn/my/?SAMLart=" + context.getParam2() + "&RelayState=", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            header.put("Referer", "http://www.fj.10086.cn/my/");
            page = getPage(webClient, "http://www.fj.10086.cn/my/info/queryTaocanMessage1.do", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);


            String month = DateUtils.getDiffMonth("yyyyMM", 0);

            reqParam.clear();
            reqParam.add(new NameValuePair("friendTel", new Date() + ""));
            reqParam.add(new NameValuePair("location", "Boston"));
            url = "http://www.fj.10086.cn/my/info/queryTaoCanUse2.do?deal_id=0&start_month=" + month + "&pageinfo=option&dealName=1";
            header.put("Referer", "http://www.fj.10086.cn/my/info/queryTaocanMessage1.do");
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), page);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集已办理业务信息出现异常:[{}]", e);
            result.setResult(StatusCode.采集套餐信息出错);
            return result;
        }
        return result;
    }

    public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        try {
            Map<String, String> header = new HashMap<>();
            Page page = getPage(webClient, "http://www.fj.10086.cn/my/?SAMLart=" + context.getParam2() + "&RelayState=", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            page = getPage(webClient, "http://www.fj.10086.cn/my/fee/query/queryMoneyRecordYY.do", HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);
            String endDate = DateUtils.getCurrentDate();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            String url = "http://www.fj.10086.cn/my/fee/query/queryMoneyRecord.do?starttimes=" + transDateBegin + "&endtimes=" + endDate;
            header.put("Referer", "http://www.fj.10086.cn/my/fee/query/queryMoneyRecordYY.do");
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.RECHARGE_INFO.getCode(), page);//把返回结果添加到缓存（重要）
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集充值信息出现异常:[{}]", e);
            result.setResult(StatusCode.采集充值记录出错);
            return result;
        }
        return result;
    }

    private List<Page> processBillAndNetAndSms(WebClient webClient, Context context, int type) {
        List<NameValuePair> reqParam = new ArrayList<>();
        Page page = getPage(webClient, "http://www.fj.10086.cn/my/?SAMLart=" + context.getParam2() + "&RelayState=", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
        List<Page> pageList = new ArrayList<>();

        for (int i = 0; i >= -5; i--) {
            String yearAndMonth = DateUtils.getDiffMonth("yyyyMM", i);
            String logType = "";
            reqParam.clear();
            reqParam.add(new NameValuePair("search", "search_ajax"));
            reqParam.add(new NameValuePair("friendTel", new Date() + ""));
            switch (type) {
                case 2:
                    reqParam.add(new NameValuePair("class_id", "2"));
                    logType = "通话";
                    break;
                case 4:
                    reqParam.add(new NameValuePair("class_id", "4"));
                    logType = "短信";
                    break;
                case 8:
                    reqParam.add(new NameValuePair("class_id", "8"));
                    logType = "上网";
                    break;
            }

            reqParam.add(new NameValuePair("tel_user_id", "undefined"));
            reqParam.add(new NameValuePair("is_ims_flag", "1"));
            String url = "http://www.fj.10086.cn/my/fee/query/queryNewServiceDetail.do?rom=" + Math.random() + "&start_month_xdcs=" + yearAndMonth;
            try {
                Page resultPage = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
                if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "当天的查询次数大于6次")) {
                    logger.info("==>3." + (Math.abs(i) + 1) + "采集[" + yearAndMonth + "]{}详单失败，当天的查询次数大于6次.[{}]", logType, context.getTaskId());
                } else {
                    pageList.add(resultPage);
                    logger.info("==>3." + (Math.abs(i) + 1) + "采集[" + yearAndMonth + "]{}详单成功.[{}]", logType, context.getTaskId());
                }
            } catch (Exception e) {
                logger.info("==>3." + (Math.abs(i) + 1) + "采集[" + yearAndMonth + "]{}详单失败,即将执行下一步操作.[{}]", logType, context.getTaskId(),e);
            }
        }
        return pageList;
    }

    @Override
    protected Result loginout(WebClient webClient) {
        Result result = new Result();
        String url = "https://fj.ac.10086.cn/logout";
        Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
        result.setSuccess();
        return result;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}

