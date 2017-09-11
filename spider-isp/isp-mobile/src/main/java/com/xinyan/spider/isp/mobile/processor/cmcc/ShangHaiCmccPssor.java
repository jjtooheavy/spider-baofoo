package com.xinyan.spider.isp.mobile.processor.cmcc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.cmcc.ShangHaiCmccParser;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;

/**
 * 上海移动处理类
 *
 * @author heliang
 * @version V1.0
 * @description
 * @date 2016年8月22日 下午3:22:12
 */
@Component
public class ShangHaiCmccPssor extends AbstractProcessor {


    protected static Logger logger = LoggerFactory.getLogger(ShangHaiCmccPssor.class);

    private static final String BASE_URL = "http://www.sh.10086.cn";
    @Autowired
    private ShangHaiCmccParser parser;
    @Override
    public Result doLogin(WebClient webClient, Context context) {
        logger.info("==>[{}]1.1获取短信验证码开始.....",context.getTaskId());
        Result result = new Result();
        try {
            Page resultPage = getPage(webClient, "https://sh.ac.10086.cn/login",
                    HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            //1.获取短信验证码
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("act", "1"));
            reqParam.add(new NameValuePair("telno", context.getUserName()));
            resultPage = getPage(webClient, "https://sh.ac.10086.cn/loginjt",
                    HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            if (StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "result\":\"0")) {
                result.setResult(StatusCode.请输入短信验证码);
                context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);//需要设置回调子状态
                sendValidataMsg(result, context, false, webClient, Constants.DEQUEUE_TIME_OUT, null);
                return result;
            } else {
                logger.info("==>[{}]1.1获取短信验证码失败.", context.getTaskId());
                result.setResult(StatusCode.短信验证码错误);
                return result;
            }
        } catch (Exception e) {
            logger.error("==>[{}]1.1获取短信验证码异常",context.getTaskId(), e);
            result.setResult(StatusCode.登陆出错);
            return result;
        }finally {
            if(!StatusCode.请输入短信验证码.getCode().equals(result.getCode())){//不需要回调
                sendLoginMsg(result, context, false, webClient);
            }
            close(webClient);
        }
    }

    @Override
    protected Result doLoginBySMS(WebClient webClient, Context context) throws Exception {
        Result result = new Result();
        try {
            //1.加密用户名和密码
            logger.info("==>[{}]1.2加密用户名和密码开始.....",context.getTaskId());
            String rsaUserName = JavaScriptUtils.invoker("js/ShangHaiCmccDes.js", "enString", context.getUserName());
            String rsaPassword = JavaScriptUtils.invoker("js/ShangHaiCmccDes.js", "enString", context.getPassword());
            String dtm = JavaScriptUtils.invoker("js/ShangHaiCmccDes.js", "enString", context.getUserInput());
            logger.info("==>[{}]1.1加密用户名和密码结束.",context.getTaskId());
            //2.开始登陆
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("telno", rsaUserName));
            reqParam.add(new NameValuePair("password", rsaPassword));
            reqParam.add(new NameValuePair("authLevel", "5"));
            reqParam.add(new NameValuePair("dtm", dtm));
            reqParam.add(new NameValuePair("ctype", "1"));
            reqParam.add(new NameValuePair("decode", "1"));
            reqParam.add(new NameValuePair("source", "wsyyt"));
            logger.info("==>[{}]1.2用户验证开始.....",context.getTaskId());
            Page resultPage = getPage(webClient, "https://sh.ac.10086.cn/loginjt?act=2",
                    HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            logger.info("==>[{}]1.2用户验证结束.",context.getTaskId());

            if (null != resultPage) {
                String msg = resultPage.getWebResponse().getContentAsString();
                if (StringUtils.contains(msg, "result\":\"0")) {//登陆成功
                    String uid = JSONObject.parseObject(msg).getString("uid");
                    context.setParam1(uid);
                    result.setSuccess();
                } else if (StringUtils.contains(msg, "不支持简单密码登录")) {//不支持简单密码登录
                    result.setResult(StatusCode.系统不再下发);
                    return result;
                } else if (StringUtils.contains(msg, "密码错误")) {//密码错误
                    result.setResult(StatusCode.用户名或密码错误);
                    return result;
                } else {
                    result.setResult(StatusCode.验证短信验证码错误);
                    return result;
                }
            } else {
                //登录失败
                result.setResult(StatusCode.登陆失败);
                return result;
            }
        } catch (Exception e) {
            logger.error("==>[{}]1.2登录出现异常",context.getTaskId(), e);
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
            result = parser.basicInfoParse(context,cacheContainer,carrierInfo);
            getLogger().info("==>[{}]10.解析基本信息结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析通话详单
            getLogger().info("==>[{}]11.解析通话详单开始.....", context.getTaskId());
            result = parser.callRecordParse(context,cacheContainer,carrierInfo);
            getLogger().info("==>[{}]11.解析通话详单结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析短信记录
            getLogger().info("==>[{}]12.解析短信记录开始.....", context.getTaskId());
            result = parser.smsInfoParse(context,cacheContainer,carrierInfo);
            getLogger().info("==>[{}]12.解析短信记录结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析上网记录
            getLogger().info("==>[{}]13.解析上网记录开始.....", context.getTaskId());
            result = parser.netInfoParse(context,cacheContainer,carrierInfo);
            getLogger().info("==>[{}]13.解析上网记录结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析账单信息
            getLogger().info("==>[{}]14.解析账单信息开始.....", context.getTaskId());
            result = parser.billParse(context,cacheContainer,carrierInfo);
            getLogger().info("==>[{}]14.解析账单信息结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析套餐信息
            getLogger().info("==>[{}]15.解析套餐信息开始.....", context.getTaskId());
            result = parser.packageItemInfoParse(context,cacheContainer,carrierInfo);
            getLogger().info("==>[{}]15.解析套餐信息结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析亲情号码
            getLogger().info("==>[{}]16.解析亲情号码开始.....", context.getTaskId());
            result = parser.userFamilyMemberParse(context,cacheContainer,carrierInfo);
            getLogger().info("==>[{}]16.解析亲情号码结束.....", context.getTaskId());
            if (!result.isSuccess()) {
                return result;
            }
            //解析充值记录
            getLogger().info("==>[{}]17.解析充值记录开始.....", context.getTaskId());
            result = parser.userRechargeItemInfoParse(context,cacheContainer,carrierInfo);
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
        try {
            logger.info("==>[{}]1.2抓取个人信息开始.....",context.getTaskId());
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("uid", context.getParam1()));
            //重要！！登陆完成需要加载一下，否则不是登录状态
            Page busiPage = getPage(webClient, BASE_URL + "/sh/wsyyt/busi.json?sid=WF000022",
                    HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            Page resultPage = getPage(webClient, "http://www.sh.10086.cn/sh/wsyyt/action?act=myarea.getinfoManageMore", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);//基本信息
            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(),resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]1.2抓取个人信息结束.",context.getTaskId());
            logger.info("==>[{}]1.1抓取余额信息开始.....",context.getTaskId());
            resultPage = getPage(webClient,  "http://www.sh.10086.cn/sh/wsyyt/action?act=my.getaccountinfo", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);//套餐和余额
            //存入缓存
            if (null != resultPage) {
                cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(),resultPage);
            } else {
                result.setResult(StatusCode.解析基础信息出错);
                return result;
            }
            logger.info("==>[{}]1.1抓取余额信息结束.",context.getTaskId());


            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集基本信息出现异常", e);
            result.setResult(StatusCode.解析基础信息出错);
            return result;
        }

        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        logger.info("==>[{}]2.1抓取通话详单信息开始.....",context.getTaskId());
        try {

            //获取详单前需要先加载页面，否则会失败
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("uid", context.getParam1()));
            Page busiPage = getPage(webClient, BASE_URL + "/sh/wsyyt/busi.json?sid=WF000022",
                    HttpMethod.POST, reqParam, null);
            Page historySearchPage = getPage(webClient, BASE_URL + "/sh/wsyyt/busi/historySearch.do?method=getOneAndFileBillPageNew&firstPage=y&month=&type=&uniqueKey=14&uniqueName=详单查询",
                    HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            List<Page> pageList = new ArrayList<>();

            //查询最近6个月通话详单
            for (int i = 0; i < 6; i++) {

                String month = DateUtils.getDiffMonth("yyyy-MM", -i);
                String transDateBegin = DateUtils.getFirstDay("yyyy-MM-dd", -i);
                String transDateEnd = DateUtils.getLastDay("yyyy-MM-dd", -i);
                if (transDateEnd.compareTo(DateUtils.getCurrentDate()) > 0) {
                    transDateEnd = DateUtils.getCurrentDate();
                }
                reqParam = new ArrayList<>();
                reqParam.add(new NameValuePair("billType", "NEW_GSM"));
                reqParam.add(new NameValuePair("startDate", transDateBegin));
                reqParam.add(new NameValuePair("endDate", transDateEnd));
                reqParam.add(new NameValuePair("searchStr", "-1"));
                reqParam.add(new NameValuePair("index", "0"));
                reqParam.add(new NameValuePair("r", Calendar.getInstance().getTimeInMillis() + ""));
                reqParam.add(new NameValuePair("isCardNo", "0"));
                reqParam.add(new NameValuePair("gprsType", ""));
                reqParam.add(new NameValuePair("filterfield", "输入对方号码："));
                reqParam.add(new NameValuePair("filterValue", ""));
                String url = "http://www.sh.10086.cn/sh/wsyyt/busi/historySearch.do?method=getFiveBillDetailAjax";//获取详单路径
                if (i == 0) {
                    reqParam = new ArrayList<>();
                    reqParam.add(new NameValuePair("billType", "NEW_GSM"));
                    reqParam.add(new NameValuePair("startDate", transDateBegin));
                    reqParam.add(new NameValuePair("endDate", transDateEnd));
                    reqParam.add(new NameValuePair("jingque", ""));
                    reqParam.add(new NameValuePair("searchStr", "-1"));
                    reqParam.add(new NameValuePair("index", "0"));
                    reqParam.add(new NameValuePair("r", Calendar.getInstance().getTimeInMillis() + ""));
                    reqParam.add(new NameValuePair("isCardNo", "0"));
                    reqParam.add(new NameValuePair("gprsType", ""));

                    url = url.replace("getFiveBillDetailAjax", "getOneBillDetailAjax");
                }

                String logFlag = String.format("==>["+context.getTaskId()+"]2." + (i + 1) + "采集[" + transDateBegin + "]至[{" + transDateEnd + "}]通话详单,第[{}]次尝试请求.....");

                Page callDetailPage = getPage(webClient, url,
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                if (StringUtils.contains(callDetailPage.getWebResponse().getContentAsString(), "暂无此详单类型的记录")) {
                    getLogger().info("==>[{}]2.{}采集[{}]通话详单结束,没有查询结果.",context.getTaskId(), i + 1, month);
                } else {
                    pageList.add(callDetailPage);
                    getLogger().info("==>[{}]2.{}采集[{}]通话详单成功.",context.getTaskId(), i + 1, month);
                }
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

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        logger.info("==>[{}]3.1抓取短信详单信息开始.....",context.getTaskId());
        Result result = new Result();
        try {

            List<Page> pageList = new ArrayList<>();

            //查询最近6个月短信记录
            for (int i = 0; i < 6; i++) {

                String month = DateUtils.getDiffMonth("yyyy-MM", -i);
                String transDateBegin = DateUtils.getFirstDay("yyyy-MM-dd", -i);
                String transDateEnd = DateUtils.getLastDay("yyyy-MM-dd", -i);
                if (transDateEnd.compareTo(DateUtils.getCurrentDate()) > 0) {
                    transDateEnd = DateUtils.getCurrentDate();
                }

                List<NameValuePair> reqParam = new ArrayList<>();
                reqParam.add(new NameValuePair("billType", "NEW_SMS"));
                reqParam.add(new NameValuePair("startDate", transDateBegin));
                reqParam.add(new NameValuePair("searchStr", "-1"));
                reqParam.add(new NameValuePair("index", "0"));
                reqParam.add(new NameValuePair("r", Calendar.getInstance().getTimeInMillis() + ""));
                reqParam.add(new NameValuePair("isCardNo", "0"));
                reqParam.add(new NameValuePair("gprsType", ""));

                String url = "http://www.sh.10086.cn/sh/wsyyt/busi/historySearch.do?method=getFiveBillDetailAjax";
                if (0 == i) {
                    url = url.replace("getFiveBillDetailAjax", "getOneBillDetailAjax");
                    reqParam.add(new NameValuePair("endDate", transDateEnd));
                    reqParam.add(new NameValuePair("jingque", ""));
                } else {
                    reqParam.add(new NameValuePair("endDate", transDateEnd));
                    reqParam.add(new NameValuePair("filterfield", "输入对方号码："));
                    reqParam.add(new NameValuePair("filterValue", ""));
                }

                String logFlag = String.format("==>["+context.getTaskId()+"]3." + (i + 1) + "采集[" + transDateBegin + "]至[{" + transDateEnd + "}]短信记录,第[{}]次尝试请求.....");
                HtmlPage smsPage = (HtmlPage) getPage(webClient, url,
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                if (StringUtils.contains(smsPage.getWebResponse().getContentAsString(), "暂无此详单类型的记录")) {
                    getLogger().info("==>[{}]3.{}采集[{}]短信记录结束,没有查询结果.",context.getTaskId(), i + 1, month);
                } else {
                    getLogger().info("==>[{}]3.{}采集[{}]短信记录成功.",context.getTaskId(), i + 1, month);
                    pageList.add(smsPage);
                }

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

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        logger.info("==>[{}]4.1抓取上网信息开始.....",context.getTaskId());
        Result result = new Result();
        try {

            List<Page> pageList = new ArrayList<>();

            //查询最近6个月上网记录
            for (int i = 0; i < 6; i++) {

                String month = DateUtils.getDiffMonth("yyyy-MM", -i);
                String transDateBegin = DateUtils.getFirstDay("yyyy-MM-dd", -i);
                String transDateEnd = DateUtils.getLastDay("yyyy-MM-dd", -i);
                if (transDateEnd.compareTo(DateUtils.getCurrentDate()) > 0) {
                    transDateEnd = DateUtils.getCurrentDate();
                }

                List<NameValuePair> reqParam = new ArrayList<>();
                reqParam.add(new NameValuePair("billType", "NEW_GPRS"));
                reqParam.add(new NameValuePair("startDate", transDateBegin));
                reqParam.add(new NameValuePair("endDate", transDateEnd));
                reqParam.add(new NameValuePair("searchStr", "-1"));
                reqParam.add(new NameValuePair("index", "0"));
                reqParam.add(new NameValuePair("r", Calendar.getInstance().getTimeInMillis() + ""));
                reqParam.add(new NameValuePair("isCardNo", "0"));
                reqParam.add(new NameValuePair("gprsType", ""));

                String url = "http://www.sh.10086.cn/sh/wsyyt/busi/historySearch.do?method=getFiveBillDetailAjax";

                if (0 == i) {
                    url = url.replace("getFiveBillDetailAjax", "getOneBillDetailAjax");
                    reqParam.add(new NameValuePair("endDate", transDateEnd));
                    reqParam.add(new NameValuePair("jingque", ""));
                } else {
                    reqParam.add(new NameValuePair("filterfield", "输入对方号码："));
                    reqParam.add(new NameValuePair("filterValue", ""));
                }

                String logFlag = String.format("==>["+context.getTaskId()+"]4." + (i + 1) + "采集[" + transDateBegin + "]至[{" + transDateEnd + "}]上网记录,第[{}]次尝试请求.....");
                HtmlPage netPage = (HtmlPage) getPage(webClient, url,
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                if (StringUtils.contains(netPage.getWebResponse().getContentAsString(), "暂无此详单类型的记录")) {
                    getLogger().info("==>[{}]4.{}采集[{}]上网记录结束,没有查询结果.",context.getTaskId(), i + 1, month);
                } else {
                    pageList.add(netPage);
                    getLogger().info("==>[{}]4.{}采集[{}]上网记录成功.",context.getTaskId(), i + 1, month);
                }
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

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        logger.info("==>[{}]5.1抓取账单信息开始.....",context.getTaskId());
        Result result = new Result();
        try {
            Calendar calendar = Calendar.getInstance();
            List<Page> pageList = new ArrayList<>();

            //查询最近6个月账单
            for (int i = 0; i < 6; i++) {

                final String transDateBegin = DateFormatUtils.format(calendar, "yyyy年MM月");

                List<NameValuePair> reqParam = new ArrayList<>();
                reqParam.add(new NameValuePair("showType", "0"));
                reqParam.add(new NameValuePair("r", Calendar.getInstance().getTimeInMillis() + ""));

                String url = "http://www.sh.10086.cn/sh/wsyyt/busi/historySearch.do?method=FiveBillAllNewAjax";

                if (0 == i) {
                    url = "http://www.sh.10086.cn/sh/wsyyt/busi/historySearch.do?method=getFiveBillAllNewBillDetail";
                    String logFlag = String.format("==>["+context.getTaskId()+"]5." + i + "采集[" + DateFormatUtils.format(calendar, "yyyy-MM") + "]账单信息,第[{}]次尝试请求.....");
                    HtmlPage historyBillPage = (HtmlPage) getPage(webClient, url,
                            HttpMethod.POST, null, Constants.MAX_RETRY_TIIMES, logFlag, null);//当前月份账单请求特殊
                    if (StringUtils.contains(historyBillPage.getWebResponse().getContentAsString(), "费用信息")) {
                        pageList.add(historyBillPage);
                        getLogger().info("==>[{}]5.{}采集[{}]账单信息成功.",context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                    } else {
                        getLogger().info("==>[{}]5.{}采集[{}]账单信息结束,没有查询结果.",context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                    }
                    calendar.add(Calendar.MONTH, -1);
                    continue;
                } else {
                    reqParam.add(new NameValuePair("dateTime", transDateBegin));
                    reqParam.add(new NameValuePair("tab", "tab1_15"));
                    reqParam.add(new NameValuePair("isPriceTaxSeparate", null));
                }

                String logFlag = String.format("==>["+context.getTaskId()+"]5." + i + "采集[" + DateFormatUtils.format(calendar, "yyyy-MM") + "]账单信息,第[{}]次尝试请求.....");
                HtmlPage historyBillPage = (HtmlPage) getPage(webClient, url,
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

                if (StringUtils.contains(historyBillPage.getWebResponse().getContentAsString(), "计费周期")) {
                    pageList.add(historyBillPage);
                    getLogger().info("==>[{}]5.{}采集[{}]账单信息成功.",context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                } else {
                    getLogger().info("==>[{}]5.{}采集[{}]账单信息结束,没有查询结果.",context.getTaskId(), i, DateFormatUtils.format(calendar, "yyyy-MM"));
                }

                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集上网记录出现异常", e);
            result.setResult(StatusCode.解析账单信息出错);
            return result;
        }
        return result;
    }

    @Override
    protected Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        logger.info("==>[{}]6.1抓取套餐信息开始.....",context.getTaskId());
        Result result = new Result();
        List<Page> pageList = new ArrayList<>();
        try {
            Calendar calendar = Calendar.getInstance();
            //当前月的套餐情况
            String logFlag = String.format("==>[{}]6.1采集套餐信息,第[{}]次尝试请求.....",context.getTaskId());
            List<NameValuePair> reqParam = new ArrayList<>();
            for(int i=0;i<6;i++){
                calendar.add(Calendar.MONTH, -1);
                final String transDateBegin = DateFormatUtils.format(calendar, "yyyy年MM月");
                logFlag = String.format("==>["+context.getTaskId()+"]6.1采集套餐信息,第[{}]次尝试请求.....");
                String url = "http://www.sh.10086.cn/sh/wsyyt/busi/historySearch.do?method=FiveBillAllNewAjax";
                reqParam.clear();
                reqParam.add(new NameValuePair("dateTime", transDateBegin));
                reqParam.add(new NameValuePair("tab", "tab1_15"));
                reqParam.add(new NameValuePair("isPriceTaxSeparate", null));
                reqParam.add(new NameValuePair("showType", "0"));
                reqParam.add(new NameValuePair("r", Calendar.getInstance().getTimeInMillis() + ""));
                HtmlPage packageItemPage = (HtmlPage) getPage(webClient, url,
                        HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (StringUtils.contains(packageItemPage.getWebResponse().getContentAsString(), "通信量使用信息明细")) {
                    pageList.add(packageItemPage);
                    getLogger().info("==>[{}]6.2采集套餐信息成功.",context.getTaskId());
                } else {
                    getLogger().info("==>[{}]6.3采集套餐信息结束,没有查询结果.",context.getTaskId());
                }

            }
            cacheContainer.putPages(ProcessorCode.PACKAGE_ITEM.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集套餐信息出现异常", e);
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
        logger.info("==>[{}]8.1抓取充值记录开始.....",context.getTaskId());
        Result result = new Result();
        List<Page> pageList = new ArrayList<>();
        try {
            String logFlag = String.format("==>[{}]8.1采集充值记录信息,第[{}]次尝试请求.....",context.getTaskId());
            String url = "http://www.sh.10086.cn/sh/wsyyt/busi/historySearch.do?method=changeHistory";
            HtmlPage userRechargeItem = (HtmlPage) getPage(webClient, url,
                    HttpMethod.POST, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
            if (StringUtils.contains(userRechargeItem.getWebResponse().getContentAsString(), "adlList")) {
                pageList.add(userRechargeItem);
                getLogger().info("==>[{}]8.2采集充值记录信息成功.",context.getTaskId());
            } else {
                getLogger().info("==>[{}]8.3采集充值记录信息结束,没有查询结果.",context.getTaskId());
            }
            cacheContainer.putPages(ProcessorCode.RECHARGE_INFO.getCode(), pageList);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集充值记录信息出现异常", e);
            result.setResult(StatusCode.采集充值记录出错);
            return result;
        }
        return result;
    }

    @Override
    protected Result loginout(WebClient webClient) {
        Result result = new Result();
        String url = "http://www1.10086.cn/service/sso/logout.jsp?channelID=12027&backUrl=http%3A%2F%2Fwww.sh.10086.cn%2Fsh%2Fservice%2F";
        Page page = getPage(webClient, url, HttpMethod.GET, null, null);
        result.setSuccess();
        return result;
    }


    @Override
    protected Logger getLogger() {
        return logger;
    }
}
