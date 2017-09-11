package com.xinyan.spider.isp.mobile.processor.cmcc;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.cmcc.HeNanCmccParser;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:河南移动处理类
 * @author: heliang
 * @date: 2016-09-07 13:28
 * @version: v1.0
 */
@Component
public class HeNanCmccPssor extends AbstractProcessor {

    @Autowired
    private HeNanCmccParser parser;

    protected static Logger logger = LoggerFactory.getLogger(HeNanCmccPssor.class);

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
        List<NameValuePair> reqParam = new ArrayList<>();
        HashMap<String, String> header = new HashMap<>();
        logger.info("==>[{}]1.1验证河南移动官网是否可以访问开始.....", context.getTaskId());
        try {
            String url = "https://login.10086.cn/login.html?channelID=10371&backUrl=http://service.ha.10086.cn/service/index.action";
            Page page = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);
            url = "https://login.10086.cn/needVerifyCode.htm?accountType=01&account=" + context.getUserName() + "&timestamp=1493692139357";
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            if (page.getWebResponse().getContentAsString().contains("{\"needVerifyCode\":\"0\"}")) {
                header.put("Referer", "https://login.10086.cn/login.html?channelID=10371&backUrl=http://service.ha.10086.cn/service/index.action");
                reqParam.add(new NameValuePair("accountType", "01"));
                reqParam.add(new NameValuePair("account", context.getUserName()));
                reqParam.add(new NameValuePair("password", context.getPassword()));
                reqParam.add(new NameValuePair("pwdType", "01"));
                reqParam.add(new NameValuePair("smsPwd", ""));
                reqParam.add(new NameValuePair("inputCode", ""));
                reqParam.add(new NameValuePair("backUrl", "http://service.ha.10086.cn/service/index.action"));
                reqParam.add(new NameValuePair("rememberMe", "0"));
                reqParam.add(new NameValuePair("channelID", "10371"));
                reqParam.add(new NameValuePair("protocol", "https:"));
                reqParam.add(new NameValuePair("timestamp", "1493691129597"));
                url = "https://login.10086.cn/login.htm";
                page = getPage(webClient, url, HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
                if (page.getWebResponse().getContentAsString().contains("您的账户名与密码不匹配")) {
                    result.setResult(StatusCode.服务密码错误);
                    return result;
                } else if (page.getWebResponse().getContentAsString().contains("已有账号登录,是否使用新账号")) {
                    result.setResult(StatusCode.已有账号登录是否使用新账号);
                    return result;
                } else if (page.getWebResponse().getContentAsString().contains("对不起，您的账户被锁定，24小时候后可重新登录，如有问题请咨询客服。")) {
                    result.setResult(StatusCode.您的账户被锁定24小时候后可重新登录);
                    return result;
                } else if (page.getWebResponse().getContentAsString().contains("认证成功")) {
                    String content = page.getWebResponse().getContentAsString();
                    String artifact = RegexUtils.matchValue("\"artifact\":\"(.*?)\"", content);
                    context.setParam1(artifact);
                    result.setSuccess();
                    result.setData(result);
                } else {
                    result.setResult(StatusCode.登陆失败);
                    return result;
                }
            } else if (page.getWebResponse().getContentAsString().contains("{\"needVerifyCode\":\"1\"}")) {
                url = "https://login.10086.cn/sendRandomCodeAction.action";
                reqParam.clear();
                reqParam.add(new NameValuePair("userName", context.getUserName()));
                reqParam.add(new NameValuePair("type", "01"));
                reqParam.add(new NameValuePair("channelID", "10371"));
                page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
                if ("0".equals(page.getWebResponse().getContentAsString())) {
                    result.setResult(StatusCode.请输入短信验证码);
                    context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
                    sendValidataMsg(result, context, false, webClient, Constants.ENQUEUE_TIME_OUT, null);
                } else {
                    result.setResult(StatusCode.发送短信验证码失败);
                    return result;

                }
            } else {
                result.setResult(StatusCode.登陆失败);
                return result;
            }
            logger.info("==>[{}]1.1用户登录结束.", context.getTaskId());
        } catch (Exception ex) {
            //官网不能正常访问、返回官网正在维护
            result.setResult(StatusCode.官网正在维护);
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
        logger.info("==>[{}]1.2短信验证开始.....", context.getTaskId());
        List<NameValuePair> reqParam = new ArrayList<>();
        Result result = new Result();
        HashMap<String, String> header = new HashMap<>();
        try {
            header.put("Referer", "https://login.10086.cn/login.html?channelID=10371&backUrl=http://service.ha.10086.cn/service/index.action");
            reqParam.add(new NameValuePair("accountType", "01"));
            reqParam.add(new NameValuePair("account", context.getUserName()));
            reqParam.add(new NameValuePair("password", context.getPassword()));
            reqParam.add(new NameValuePair("pwdType", "01"));
            reqParam.add(new NameValuePair("smsPwd", context.getUserInput()));
            reqParam.add(new NameValuePair("inputCode", ""));
            reqParam.add(new NameValuePair("backUrl", "http://service.ha.10086.cn/service/index.action"));
            reqParam.add(new NameValuePair("rememberMe", "0"));
            reqParam.add(new NameValuePair("channelID", "10371"));
            reqParam.add(new NameValuePair("protocol", "https:"));
            reqParam.add(new NameValuePair("timestamp", "1493691129597"));
            String url = "https://login.10086.cn/login.htm";
            Page page = getPage(webClient, url, HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            if (page.getWebResponse().getContentAsString().contains("您的账户名与密码不匹配")) {
                result.setResult(StatusCode.服务密码错误);
                return result;
            } else if (page.getWebResponse().getContentAsString().contains("已有账号登录,是否使用新账号")) {
                result.setResult(StatusCode.已有账号登录是否使用新账号);
                return result;
            } else if (page.getWebResponse().getContentAsString().contains("对不起，您的账户被锁定，24小时候后可重新登录，如有问题请咨询客服。")) {
                result.setResult(StatusCode.您的账户被锁定24小时候后可重新登录);
                return result;
            } else if (page.getWebResponse().getContentAsString().contains("短信随机码不正确或已过期")) {
                result.setResult(StatusCode.验证短信验证码错误);
                return result;
            } else if (page.getWebResponse().getContentAsString().contains("认证成功")) {
                String content = page.getWebResponse().getContentAsString();
                String artifact = RegexUtils.matchValue("\"artifact\":\"(.*?)\"", content);
                context.setParam1(artifact);
                result.setSuccess();
                result.setData(result);
            } else {
                result.setResult(StatusCode.登陆失败);
                return result;
            }
        } catch (Exception e) {
            logger.error("==>登录出现异常", e);
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
            getLogger().info("==>[{}]9.解析数据异常:{}", context.getTaskId(),e);
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
        try {

            logger.info("==>[{}]1.1抓取个人信息开始.....", context.getTaskId());
            url = "http://service.ha.10086.cn/service/self/customer-info-uphold.action?menuCode=1140";
            getPage(webClient, "http://service.ha.10086.cn/sso/getartifact.jsp?backUrl=http://service.ha.10086.cn/service/self/customer-info-uphold.action?menuCode=1140&artifact=" + context.getParam1(), HttpMethod.GET, reqParam, header);
            Page resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), resultPage);
            logger.info("==>[{}]1.1抓取个人信息结束.", context.getTaskId());
            logger.info("==>[{}]1.2抓取账户余额开始.", context.getTaskId());
            resultPage = getPage(webClient, "http://service.ha.10086.cn/service/mobile/my-consume.action", HttpMethod.GET, null, null);
            logger.info("==>[{}]1.2抓取账户余额结束.", context.getTaskId());
            cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), resultPage);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集基本信息出现异常", e);
            result.setResult(StatusCode.采集基本信息出错);
            return result;
        }

        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        String url = "";
        List<NameValuePair> reqParam = new ArrayList<>();
        List<Page> pageList = new ArrayList<>();
        try {
            logger.info("==>[{}]2.1正在请求发送短信验证码开始.....", context.getTaskId());
            result.setResult(StatusCode.爬取中);
            context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
            sendUpdateLog(result, context);
            HashMap<String, String> header = new HashMap<>();
            getPage(webClient, "http://service.ha.10086.cn/sso/getartifact.jsp?backUrl=http://service.ha.10086.cn/service/self/tel-bill!detail.action?menuCode=1026&artifact=" + context.getParam1(), HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);

            url = "http://service.ha.10086.cn/verify!XdcxCodeStatus.action";
            header.put("Referer", "http://service.ha.10086.cn/service/self/tel-bill!detail.action?menuCode=1026");
            Page page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            url = "http://service.ha.10086.cn/verify!XdcxSecondAuthCode.action";
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            if (page.getWebResponse().getContentAsString().contains("\"returnMessage\":\"成功获取二次鉴权码！\"")) {
                result.setSuccess();
            } else {
                result.setResult(StatusCode.发送短信验证码失败);
                return result;
            }
            boolean isSend = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
            if (isSend) {
                String verify_code = rotation(context,webClient,120);
                if (StringUtils.isNotBlank(verify_code)) {
                    boolean flag = verificationSubmit(verify_code, webClient, context);
                    if (flag) {
                        url = "http://service.ha.10086.cn/verify!XdcxCodeStatus.action";
                        page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);
                        if (page.getWebResponse().getContentAsString().contains("\"returnMessage\":\"您已通过短信验证！\"")) {
                            for (int i = 0; i >= -5; i--) {
                                String month = DateUtils.getDiffMonth("yyyyMM", i);
                                String startDate = DateUtils.getFirstDay("yyyyMMdd", i);
                                String endDate = DateUtils.getLastDay("yyyyMMdd", i);
                                String nowDate = DateUtils.getCurrentDate2();
                                if (nowDate.compareTo(endDate) <= 0) {
                                    endDate = nowDate;
                                }

                                String logFlag = String.format("==>3." + (Math.abs(i) + 1) + "采集[" + startDate + "]至[{" + endDate + "}]通话详单,第[{}]次尝试请求.....");
                                url = "http://service.ha.10086.cn/service/self/tel-bill-detail!call.action?type=call&StartDate=" + startDate + "&EndDate=" + endDate + "&FilteredMobileNo=";
                                Page resultPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, null);

                                if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "查询验证码已过期")) {
                                    logger.info("==>[{}]3." + (Math.abs(i) + 1) + "采集【" + month + "】您的详单查询验证码已过期", context.getTaskId());
                                    continue;
                                } else if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "对不起,查询信息不存在")) {
                                    logger.info("==>[{}]3." + (Math.abs(i) + 1) + "采集【" + month + "】通话详单信息完成，没有通话记录", context.getTaskId());
                                    continue;
                                } else {
                                    pageList.add(resultPage);
                                    logger.info("==>[{}]3." + (Math.abs(i) + 1) + "采集【" + month + "】通话详单信息完成", context.getTaskId());
                                }
                            }
                        }
                    } else {
                        logger.info("==>[{}]2.1您输入的二次鉴权码不正确");
                        result.setResult(StatusCode.二次鉴权失败);
                        return result;
                    }
                }
            }
            cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集通话详情出现异常", e);
            result.setResult(StatusCode.采集通话详情出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        String url = "";
        List<NameValuePair> reqParam = new ArrayList<>();
        try {
            List<Page> pageList = new ArrayList<>();
            HashMap<String, String> header = new HashMap<>();
            getPage(webClient, "http://service.ha.10086.cn/sso/getartifact.jsp?backUrl=http://service.ha.10086.cn/service/self/tel-bill!detail.action?menuCode=1026&artifact=" + context.getParam1(), HttpMethod.GET, reqParam, Constants.MAX_SENDMSG_TIIMES,null,header);

            url = "http://service.ha.10086.cn/verify!XdcxCodeStatus.action";
            header.put("Referer", "http://service.ha.10086.cn/service/self/tel-bill!detail.action?menuCode=1026");
            Page page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String startDate = DateUtils.getFirstDay("yyyyMMdd", i);
                String endDate = DateUtils.getLastDay("yyyyMMdd", i);
                String nowDate = DateUtils.getCurrentDate2();
                if (nowDate.compareTo(endDate) <= 0) {
                    endDate = nowDate;
                }
                String logFlag = String.format("==>4." + (Math.abs(i) + 1) + "采集[" + startDate + "]至[{" + endDate + "}]短信记录,第[{}]次尝试请求.....");
                url = "http://service.ha.10086.cn/service/self/tel-bill-detail!smsAndmms.action";
                reqParam.clear();
                reqParam.add(new NameValuePair("type", "smsAndmms"));
                reqParam.add(new NameValuePair("FilteredMobileNo", ""));
                reqParam.add(new NameValuePair("StartDate", startDate));
                reqParam.add(new NameValuePair("EndDate", endDate));
                Page resultPage = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "对不起,查询信息不存在")) {
                    logger.info("==>[{}]4." + (Math.abs(i) + 1) + "采集【" + month + "】短信记录信息完成，没有短信记录");
                    continue;
                } else {
                    pageList.add(resultPage);
                    logger.info("==>采集【" + month + "】短信记录信息完成");
                }
            }
            cacheContainer.putPages(ProcessorCode.SMS_INFO.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集短信记录出现异常", e);
            result.setResult(StatusCode.采集短信记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {

        Result result = new Result();
        String url = "";
        List<NameValuePair> reqParam = new ArrayList<>();

        try {
            List<Page> pageList = new ArrayList<>();
            HashMap<String, String> header = new HashMap<>();
            getPage(webClient, "http://service.ha.10086.cn/sso/getartifact.jsp?backUrl=http://service.ha.10086.cn/service/self/tel-bill!detail.action?menuCode=1026&artifact=" + context.getParam1(), HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);

            url = "http://service.ha.10086.cn/verify!XdcxCodeStatus.action";
            header.put("Referer", "http://service.ha.10086.cn/service/self/tel-bill!detail.action?menuCode=1026");
            Page page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String startDate = DateUtils.getFirstDay("yyyyMMdd", i);
                String endDate = DateUtils.getLastDay("yyyyMMdd", i);
                String nowDate = DateUtils.getCurrentDate2();
                if (nowDate.compareTo(endDate) <= 0) {
                    endDate = nowDate;
                }

                String logFlag = String.format("==>4." + (Math.abs(i) + 1) + "采集[" + startDate + "]至[{" + endDate + "}]上网记录,第[{}]次尝试请求.....");
                url = "http://service.ha.10086.cn/service/self/tel-bill-detail!flow.action";
                reqParam.clear();
                reqParam.add(new NameValuePair("type", "flow"));
                reqParam.add(new NameValuePair("StartDate", startDate));
                reqParam.add(new NameValuePair("EndDate", endDate));

                Page resultPage = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "对不起,查询信息不存在")) {
                    logger.info("==>[{}]4." + (Math.abs(i) + 1) + "采集【" + month + "】上网记录信息完成，没有上网记录");
                    continue;
                } else {
                    pageList.add(resultPage);
                    logger.info("==>采集【" + month + "】上网记录信息完成");
                }
            }
            cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集上网记录出现异常", e);
            result.setResult(StatusCode.采集上网记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        String url = "";
        List<NameValuePair> reqParam = new ArrayList<>();
        try {
            List<Page> pageList = new ArrayList<>();
            String wt_csrf_token = "";
            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String logFlag = String.format("==>[{}]6." + (Math.abs(i) + 1) + "采集[" + month + "]账单信息,第[{}]次尝试请求.....", context.getTaskId());
                reqParam.clear();
                reqParam.add(new NameValuePair("QMonth", month));
                Page page;
                if (i == 0) {
                    HashMap<String, String> header = new HashMap<>();
                    Page check = getPage(webClient, "http://service.ha.10086.cn/sso/getartifact.jsp?backUrl=http://service.ha.10086.cn/service/self/tel-bill!detail.action?menuCode=1026&artifact=" + context.getParam1(), HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
                    header.put("Referer", "http://service.ha.10086.cn/service/mobile/my-consume.action");
                    page = getPage(webClient, "http://service.ha.10086.cn/service/self/tel-bill.action", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
                    wt_csrf_token = RegexUtils.matchValue("name=\"wt_csrf_token\" value=\"(.*?)\">", page.getWebResponse().getContentAsString());
                    pageList.add(page);
                } else {
                    url = "http://service.ha.10086.cn/service/self/tel-bill.action?menuCode=1026";
                    reqParam.add(new NameValuePair("wt_csrf_token", wt_csrf_token));
                    page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                    pageList.add(page);
                }
                logger.info("==>[{}]6." + (Math.abs(i) + 1) + "采集[" + month + "]账单信息成功.", context.getTaskId());
            }

            cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pageList);//把返回结果添加到缓存（重要）
            result.setSuccess();

        } catch (Exception e) {
            logger.error("==>采集账单信息出现异常", e);
            result.setResult(StatusCode.采集账单记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        String url = "";
        try {
//            url = "http://service.ha.10086.cn/version/zfzq/cptc-index!wdtc.action?menuCode=1112";
            url = "http://service.ha.10086.cn/version/zfzq/cptc-index!wdtc.action?menuCode1112 ";
            Page check = getPage(webClient, "http://service.ha.10086.cn/sso/getartifact.jsp?backUrl=http://service.ha.10086.cn/version/zfzq/cptc-index!wdtc.action?menuCode=1112&channelId=10371&artifact=" + context.getParam1(), HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            String logFlag = String.format("==>6.采集[" + DateUtils.getCurrentMonth2() + "]套餐信息,第[{}]次尝试请求.....");
            HashMap<String, String> header = new HashMap<>();
            header.put("Referer", "http://service.ha.10086.cn/version/zfzq/cptc-index!wdtc.action?menuCode=1112");
            Page resultPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, header);
            logger.info("==>[{}]6.采集[" + DateUtils.getCurrentMonth2() + "]套餐信息成功.", context.getTaskId());
            cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), resultPage);//把返回结果添加到缓存（重要）
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
        List<NameValuePair> reqParam = new ArrayList<>();
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Referer", "http://service.ha.10086.cn/service/mobile/my-consume.action");
            String wt_csrf_token = "";
            for (int i = 0; i >= -5; i--) {
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                reqParam.clear();
                reqParam.add(new NameValuePair("QMonth", month));
                if (i == 0) {
                    getPage(webClient, "http://service.ha.10086.cn/sso/getartifact.jsp?backUrl=http://service.ha.10086.cn/service/self/query-month-pay-record.action&channelId=10371&artifact=" + context.getParam1(), HttpMethod.GET, null, header);
                    Page page = getPage(webClient, "http://service.ha.10086.cn/service/self/query-month-pay-record.action", HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,header);
                    wt_csrf_token = RegexUtils.matchValue("name=\"wt_csrf_token\" value=\"(.*?)\">", page.getWebResponse().getContentAsString());
                    pageList.add(page);
                } else {
                    url = "http://service.ha.10086.cn/service/self/query-month-pay-record.action";
                    reqParam.add(new NameValuePair("wt_csrf_token", wt_csrf_token));
                    Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, null, null);
                    pageList.add(page);
                }
                logger.info("==>[{}]8.采集[" + month + "]月充值记录信息成功.", context.getTaskId());
            }
            cacheContainer.putPages(ProcessorCode.RECHARGE_INFO.getCode(), pageList);//把返回结果添加到缓存（重要）
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集充值信息出现异常", e);
            result.setResult(StatusCode.采集充值记录出错);
            return result;
        }
        return result;
    }

    private boolean verificationSubmit(String verifyCode, WebClient webClient, Context context) {
        logger.info("==>短信验证码正确.");
        HashMap<String, String> header = new HashMap<>();
        getPage(webClient, "http://service.ha.10086.cn/sso/getartifact.jsp?backUrl=http://service.ha.10086.cn/service/self/tel-bill!detail.action?menuCode=1026&artifact=" + context.getParam1(), HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
        List<NameValuePair> reqParam = new ArrayList<>();
        String url = "http://service.ha.10086.cn/verify!XdcxSecondAuth.action";
        reqParam.clear();
        reqParam.add(new NameValuePair("verifyCode", verifyCode));

        Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
        if (StringUtils.contains(page.getWebResponse().getContentAsString(), "用户二次鉴权通过")
                || StringUtils.contains(page.getWebResponse().getContentAsString(), ":35 GMT\r\nContent-Language: zh-CN\r\nContent-Length: 104\r\nX-Via: 1.1 dm141:80 (Cdn Cache Server V2.0)\r\nConn")
                || StringUtils.contains(page.getWebResponse().getContentAsString(), ":57 GMT\r\nContent-Language: zh-CN\r\nContent-Length: 104\r\nX-Via: 1.1 dm141:80 (Cdn Cache Server V2.0)\r\nConn")
                || StringUtils.contains(page.getWebResponse().getContentAsString(), "GMT Content-Language: zh-CN Content-Length: 67 X-Via: 1.1 d")) {
            logger.info("==>短信验证码正确.");
            return true;

        } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "随机码错误，请输入六位数字的随机码")) {
            logger.info("==>随机码错误，请输入六位数字的随机码");
            return false;
        } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "您上次申请的二次鉴权码已经失效")) {
            logger.info("==>您上次申请的二次鉴权码已经失效,请重新获取");
            return false;
        } else if (StringUtils.contains(page.getWebResponse().getContentAsString(), "您输入的二次鉴权码不正确")) {
            logger.info("==>您输入的二次鉴权码不正确");
            return false;
        } else {
            logger.info("==>短信验证码验证未通过，短信验证码参数：" + verifyCode + "；营业厅返回结果：" + page.getWebResponse().getContentAsString());
            return false;
        }
    }


    @Override
    protected Result loginout(WebClient webClient) {
        Result result = new Result();
        String url = "http://service.ha.10086.cn/service/gologout.action";
        Page page =  getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);
        result.setSuccess();
        return result;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}

