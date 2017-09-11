package com.xinyan.spider.isp.mobile.processor.cmcc;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.cmcc.JiangSuCmccParser;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description:江苏移动处理类
 * @author: jiangmengchen
 * @date: 2017-05-02 13:06
 * @version: v1.0
 */
@Component
public class JiangSuCmccPssor extends AbstractProcessor {

    @Autowired
    private JiangSuCmccParser parser;

    protected static Logger logger = LoggerFactory.getLogger(JiangSuCmccPssor.class);

    @Override
    public Result doLogin(WebClient webClient, Context context) {
        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        HashMap<String, String> header = new HashMap<>();
        try {
            reqParam.add(new NameValuePair("userLoginTransferProtocol", "https"));
            reqParam.add(new NameValuePair("redirectUrl", "http://service.js.10086.cn/index.html"));
            reqParam.add(new NameValuePair("reqUrl", "login"));
            reqParam.add(new NameValuePair("busiNum", "LOGIN"));
            reqParam.add(new NameValuePair("operType", "0"));
            reqParam.add(new NameValuePair("passwordType", "1"));
            reqParam.add(new NameValuePair("isSavePasswordVal", "0"));
            reqParam.add(new NameValuePair("isSavePasswordVal_N", "1"));
            reqParam.add(new NameValuePair("currentD", "1"));
            reqParam.add(new NameValuePair("loginFormTab", "http"));
            reqParam.add(new NameValuePair("loginType", "1"));
            reqParam.add(new NameValuePair("smsFlag", "1"));
            reqParam.add(new NameValuePair("phone-login", "on"));
            reqParam.add(new NameValuePair("mobile", context.getUserName()));
            reqParam.add(new NameValuePair("city", "on"));
            reqParam.add(new NameValuePair("password", JavaScriptUtils.invoker("js/JiangSuCmccDes.js", "valDesEncryptSet", context.getPassword())));
            String url = "http://service.js.10086.cn/actionDispatcher.do";
            header.put("Referer", "http://service.js.10086.cn/login.html?url=http://service.js.10086.cn/index.html");
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            if (page.getWebResponse().getContentAsString().contains("http://service.js.10086.cn/index.html")) {
                logger.info("登录成功");
                result.setSuccess();
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
    public Result doCrawler(WebClient webClient, Context context) {
        Result result = new Result();

        //页面缓存
        CacheContainer cacheContainer = new CacheContainer();
        CarrierInfo carrierInfo = new CarrierInfo();
        context.setMappingId(carrierInfo.getMappingId());
        result.setData(carrierInfo);
        BeanUtils.copyProperties(context, carrierInfo);
        try {
            //1.采集基础信息
            getLogger().info("==>[{}]1.采集基本信息开始.....", context.getTaskId());
            result = processBaseInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]1.采集基本信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集基础信息失败
                return result;
            }

            //5.采集账单信息
            getLogger().info("==>[{}]5.采集账单信息开始.....", context.getTaskId());
            result = processBillInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]5.采集账单信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { //采集账单信息失败
                return result;
            }

            //2.采集通话详单
            getLogger().info("==>[{}]1.采集通话详单开始.....", context.getTaskId());
            result = processCallRecordInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]1.采集通话详单结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
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

            //6.采集套餐信息
            getLogger().info("==>[{}]6.采集套餐信息开始.....", context.getTaskId());
            result = processPackageItemInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>[{}]6.采集套餐信息结束{}.", context.getTaskId(), result);
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

            //解析账单信息
            getLogger().info("==>[{}]14.解析账单信息开始.....", context.getTaskId());
            result = parser.billParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]14.解析账单信息结束.....", context.getTaskId());
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

            //解析套餐信息
            getLogger().info("==>[{}]15.解析套餐信息开始.....", context.getTaskId());
            result = parser.packageItemInfoParse(context, cacheContainer, carrierInfo);
            getLogger().info("==>[{}]15.解析套餐信息结束.....", context.getTaskId());
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
    public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {

        Result result = new Result();
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Referer", "http://service.js.10086.cn/my/MY_ZDCX.html?t=1493881703214");
            logger.info("==>[{}]采集基本信息开始", context.getTaskId());
            Page page = getPage(webClient, "http://service.js.10086.cn/my/MY_GRZLGL.html", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集基本信息出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集基本信息出错);
            return result;
        }

        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        try {
            logger.info("==>[{}]采集通话记录开始.", context.getTaskId());
            return processAllInfo(webClient, context, carrierInfo, cacheContainer, 1);
        } catch (Exception e) {
            logger.error("==>[{}]采集通话记录出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集通话详情出错);
            return result;
        }
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        try {
            logger.info("==>[{}]采集短信记录开始.", context.getTaskId());
            return processAllInfo(webClient, context, carrierInfo, cacheContainer, 6);
        } catch (Exception e) {
            logger.error("==>[{}]采集短信记录出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集短信记录出错);
            return result;
        }
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        try {
            logger.info("==>[{}]采集上网记录开始.", context.getTaskId());
            return processAllInfo(webClient, context, carrierInfo, cacheContainer, 7);
        } catch (Exception e) {
            logger.error("==>[{}]采集上网记录出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集上网记录出错);
            return result;
        }
    }

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {

        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        try {
            List<Page> pageList = new ArrayList<>();
            for (int i = 0; i >= -5; i--) {
                String _month = DateUtils.getDiffMonth("yyyyMM", i);
                String logFlag = String.format("==>6.%s采集[%s]账单信息,[%s]", Math.abs(i) + 1, _month, context.getTaskId());
                String url = "http://service.js.10086.cn/my/actionDispatcher.do";
                reqParam.clear();
                reqParam.add(new NameValuePair("reqUrl", "MY_GRZDQuery"));
                reqParam.add(new NameValuePair("busiNum", "ZDCX"));
                if (i == 0) {
                    reqParam.add(new NameValuePair("methodName", "getMobileRealTimeBill"));
                } else {
                    reqParam.add(new NameValuePair("methodName", "getMobileHistoryBill"));
                    reqParam.add(new NameValuePair("beginDate", _month));
                }
                Page resultPage = getPage(webClient, url,
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "\"success\":true")) {
                    pageList.add(resultPage);
                    logger.info("==>[{}]6.{}采集[{}]账单信息成功.", context.getTaskId(), Math.abs(i) + 1, _month);
                } else {
                    logger.info("==>[{}]6.{}采集[{}]账单信息失败.", context.getTaskId(), Math.abs(i) + 1, _month);
                }

            }
            cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pageList);//把返回结果添加到缓存（重要）
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集账单信息出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集账单记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo
            carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            logger.info("==>[{}]采集套餐信息开始", context.getTaskId());
            String url = "http://service.js.10086.cn/my/MY_WDTC.html?t=" + Calendar.getInstance().getTimeInMillis();
            Page resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), resultPage);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集套餐信息出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集套餐信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer) {
        Result result = new Result();
        String url = "http://service.js.10086.cn/my/actionDispatcher.do";
        try {
            logger.info("==>[{}]采集充值记录开始", context.getTaskId());
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("reqUrl", "CZJFJLQuery"));
            reqParam.add(new NameValuePair("busiNum", "CZJF_CZJFJL"));
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            cacheContainer.putPage(ProcessorCode.RECHARGE_INFO.getCode(), page);//把返回结果添加到缓存（重要）
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集充值信息出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集充值记录出错);
            return result;
        }
        return result;
    }

    private String sendSmsAndValidate(WebClient webClient, Context context, String param) {
        List<NameValuePair> reqSmsParam = new ArrayList<>();
        reqSmsParam.add(new NameValuePair("busiNum", param));
        Map<String, String> header = new HashMap<>();
        header.put("Referer", "http://service.js.10086.cn/my/MY_QDCX.html?t=" + Calendar.getInstance().getTimeInMillis());
        String url = "http://service.js.10086.cn/my/sms.do";
        Page page = getPage(webClient, url, HttpMethod.GET, reqSmsParam, header);

        boolean isSend = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
        if (isSend) {
            return rotation(context,webClient,120);
        } else {
            return null;
        }
    }

    private Result processAllInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer
            cacheContainer, int type) {
        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        List<Page> pageList = new ArrayList<>();
        for (int i = 0; i >= -5; i--) {
            String _month = DateUtils.getDiffMonth("yyyyMM", i);
            String startTime = DateUtils.getFirstDay("yyyy-MM-dd", i);
            String endTime = DateUtils.getLastDay("yyyy-MM-dd", i);

            String logFlag = String.format("==>4." + (Math.abs(i) + 1) + "采集[" + startTime + "]至[{" + endTime + "}]短信记录,第[{}]次尝试请求.....");

            String url = "http://service.js.10086.cn/my/actionDispatcher.do";
            String logType = "";
            reqParam.clear();
            reqParam.add(new NameValuePair("reqUrl", "MY_QDCXQueryNew"));
            reqParam.add(new NameValuePair("busiNum", "QDCX"));
            reqParam.add(new NameValuePair("queryMonth", _month));
            switch (type) {
                case 1:
                    reqParam.add(new NameValuePair("queryItem", "1"));
                    reqParam.add(new NameValuePair("qryPages", "1:1002:-1"));
                    logType = "通话";
                    break;

                case 7:
                    reqParam.add(new NameValuePair("queryItem", "7"));
                    reqParam.add(new NameValuePair("qryPages", "7:1000:-1"));
                    logType = "上网";
                    break;

                case 6:
                    reqParam.add(new NameValuePair("queryItem", "6"));
                    reqParam.add(new NameValuePair("qryPages", "6:1006:-1"));
                    logType = "短信";
                    break;
            }
            reqParam.add(new NameValuePair("qryNo", "1"));
            reqParam.add(new NameValuePair("operType", "3"));
            reqParam.add(new NameValuePair("queryBeginTime", startTime));
            reqParam.add(new NameValuePair("queryEndTime", i == 0 ? DateUtils.getCurrentDate() : endTime));

            Page resultPage = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

            if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "\"success\":true")) {
                pageList.add(resultPage);
                logger.info("==>[{}]4.{}采集{}记录[{}]至[{}]短信记录成功.", context.getTaskId(), logType, Math.abs(i) + 1, startTime, endTime);
            } else {
                result.setResult(StatusCode.爬取中);
                context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
                sendUpdateLog(result, context);

                String verifyCode = sendSmsAndValidate(webClient, context, "QDCX");
                if (StringUtils.isNotBlank(verifyCode)) {
                    reqParam.add(new NameValuePair("smsNum", verifyCode));
                    reqParam.add(new NameValuePair("confirmFlg", "1"));
                    resultPage = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                    if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "\"success\":true")) {
                        pageList.add(resultPage);
                    } else {
                        logger.info("[{}]==>短信验证码错误", context.getTaskId());
                        result.setResult(StatusCode.短信验证码错误);
                        return result;
                    }
                } else {
                    logger.info("[{}]==>发送短信验证码失败", context.getTaskId());
                    result.setResult(StatusCode.发送短信验证码失败);
                    return result;
                }
                logger.info("==>[{}]4.{}采集{}记录[{}]至[{}]记录失败.", context.getTaskId(), Math.abs(i) + 1, logType, startTime, endTime);
            }
        }

        switch (type) {
            case 1:
                cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode(), pageList);
                break;

            case 7:
                cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), pageList);
                break;

            case 6:
                cacheContainer.putPages(ProcessorCode.SMS_INFO.getCode(), pageList);
                break;
        }
        result.setSuccess();
        return result;
    }

    @Override
    protected Result loginout(WebClient webClient) {
        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        String url = "http://service.js.10086.cn/actionDispatcher.do";
        reqParam.add(new NameValuePair("mobile", ""));
        reqParam.add(new NameValuePair("reqUrl", "logout"));
        Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
        result.setSuccess();
        return result;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
