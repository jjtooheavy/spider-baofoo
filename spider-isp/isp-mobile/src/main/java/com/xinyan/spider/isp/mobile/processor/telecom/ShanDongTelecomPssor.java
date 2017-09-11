package com.xinyan.spider.isp.mobile.processor.telecom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.xinyan.spider.isp.mobile.parser.telecom.ShanDongTelecomParser;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * 山东电信处理类
 * @description
 * @author heliang
 * @date 2017年4月21日 下午3:38:43
 * @version V1.0
 */
@Component
public class ShanDongTelecomPssor extends AbstractTelecomPssor{
    protected static Logger logger = LoggerFactory.getLogger(ShanDongTelecomPssor.class);

    @Autowired
    private ShanDongTelecomParser parser;

    /**
     * 正常认证登录
     * @param webClient
     * @param context
     * @return
     */
    @Override
    public Result doLogin(WebClient webClient, Context context) {
        Result result = new Result();
        HashMap<String, String > header = new HashMap<>();
        try {
            // 1.开始登陆
            logger.info("==>0.1[{}]用户正常认证开始.....", context.getTaskId());

            String url = "http://www.189.cn/sd/service/";
            getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);

            String returnCode = telecomLogin(webClient, context, true);
            result.setCode(returnCode);
            result.setMsg(StatusCode.getMsg(returnCode));
            if(!StatusCode.SUCCESS.getCode().equals(returnCode)){
                return result;
            }
            url = "http://sd.189.cn/selfservice/service/account";
            Page resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            logger.info("==>0.1[{}]用户正常认证结束{}", context.getTaskId(), result);
        } catch (Exception e) {
            logger.error("==>0.1[{}]用户正常认证出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.登陆出错);
        } finally {
            // 设置回调
            if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
                    && !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
                sendLoginMsg(result, context, false, webClient);
            }
        }
        return result;
    }



    /**
     * 图片认证登录
     * @param webClient
     * @param context
     * @return
     */
    @Override
    public Result doLoginByIMG(WebClient webClient, Context context) {
        Result result = new Result();

        if(StringUtils.isEmpty(context.getUserInput())){
            return new Result(StatusCode.请输入图片验证码);
        }
        try {
            // 1.开始登陆
            logger.info("==>1.[{}]用户图片认证开始.....", context.getTaskId());
            String returnCode = telecomLoginByIMG(webClient, context);
            result.setCode(returnCode);
            result.setMsg(StatusCode.getMsg(returnCode));

            if(StatusCode.SUCCESS.getCode().equals(returnCode)){//发送短信验证码
                result.setResult(StatusCode.SUCCESS);
            }
            logger.info("==>1.[{}]用户图片认证结束[{}]", context.getTaskId(), result);
        } catch (Exception e) {
            logger.error("==>1.[{}]用户图片认证出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.登陆出错);
        } finally {
            // 设置回调
            if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
                    && !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
                sendLoginMsg(result, context, true, webClient);
            }
        }
        return result;
    }

    @Override
    public Result doCrawler(WebClient webClient, Context context) {
        // 页面缓存
        CacheContainer cacheContainer = new CacheContainer();
        CarrierInfo carrierInfo = new CarrierInfo();
        context.setMappingId(carrierInfo.getMappingId());
        BeanUtils.copyProperties(context, carrierInfo);
		carrierInfo.getCarrierUserInfo().setMobile(context.getUserName());// 手机号码

        Result result = telecomCrawler(webClient, context, carrierInfo, cacheContainer);
        if (!result.isSuccess()) { // 采集基础信息失败
            return result;
        }
        
        try {
            result = parser.parse(context, carrierInfo, cacheContainer);
            if (!result.isSuccess()) {
                return result;
            }
        } catch (Exception ex) {
            getLogger().error("==>[{}]山东数据解析出现异常:[{}]", context.getTaskId(), ex);
            result.setFail();
            return result;
        } finally {
            sendAnalysisMsg(result, context);
        }
        return result;
    }

    @Override
    public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
                                  CacheContainer cacheContainer) {
        Result result = new Result();
        HashMap<String, String > header = new HashMap<>();
        String url = "";
        try {
            url = "http://sd.189.cn/selfservice/cust/querymanage?100";
            header.clear();
            header.put("Content-Type", "application/json");
            header.put("Accept", "application/json, text/javascript, */*");
            String bodyStr ="{}";
            Page resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
            String verifyCode = getVerifyCode(webClient, "http://sd.189.cn/selfservice/validatecode/codeimg.jpg", null);
            result.setResult(StatusCode.爬取中);
            context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_IMG);
            sendUpdateLog(result, context);
            boolean isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_IMG, verifyCode);
            if (isFlag) {
                String verifyImgCode = rotation(context,webClient,120);
                if (StringUtils.isNotBlank(verifyImgCode)) {
                    header.clear();
                    header.put("Content-Type", "application/json");
                    header.put("Accept", "application/json, text/javascript, */*");
                    url = "http://sd.189.cn/selfservice/service/sendSms";
                    bodyStr = "{\"orgInfo\":\"" + context.getUserName() + "\",\"valicode\":\"" + verifyImgCode + "\",\"smsFlag\":\"real_2busi_validate\"}";
                    resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                    if("3".equals(resultPage.getWebResponse().getContentAsString())){
                        logger.info("==>2.1[{}]你输入的验证码错误！.....",context.getTaskId());
                        result.setResult(StatusCode.图片验证码错误);
                        return result;
                    }else if("0".equals(resultPage.getWebResponse().getContentAsString())){
                        logger.info("==>2.1[{}]发送短信成功！.....",context.getTaskId());
                        context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
                        isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
                        if (isFlag) {
                            String verifySmsCode = rotation(context,webClient,120);
                            if (!StringUtils.isEmpty(verifySmsCode)) {
                                url = "http://sd.189.cn/selfservice/service/busiVa";
                                header.clear();
                                header.put("Content-Type", "application/json");
                                header.put("Accept", "application/json, text/javascript, */*");
                                bodyStr = "{\"username_2busi\":\"undefined\"" + ",\"credentials_no_2busi\":\"undefined" + "\",\"validatecode_2busi\":\"" + verifyImgCode + "\",\"randomcode_2busi\":\"" + verifySmsCode + "\",\"randomcode_flag\":\"0\"}";
                                //bodyStr = "{\"username_2busi\":\"" + URLEncoder.encode(cacheContainer.getString("realName"), "utf-8") + "\",\"credentials_type_2busi\":\"1\",\"credentials_no_2busi\":\"" + cacheContainer.getString("identityCard") + "\",\"validatecode_2busi\":\"" + verifyCode + "\",\"randomcode_2busi\":\"" + verifySmsCode + "\",\"randomcode_flag\":\"0\",\"rid\":1,\"fid\":\"bill_monthlyDetail\"}";

                                resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                                if(resultPage.getWebResponse().getContentAsString().contains("\"retnCode\":3")){
                                    logger.info("==>2.1[{}]短信随机密码超时，请重新获取短信!.....",context.getTaskId());
                                    result.setResult(StatusCode.接收短信验证码超时);
                                    return result;
                                }else if(resultPage.getWebResponse().getContentAsString().contains("\"retnCode\":5")){
                                    logger.info("==>2.1[{}]验证未通过!.....",context.getTaskId());
                                    result.setResult(StatusCode.短信验证码错误);
                                    return result;
                                }else if(resultPage.getWebResponse().getContentAsString().contains("\"retnCode\":0")){
                                    logger.info("==>2.1[{}]二次短信验证通过！.....",context.getTaskId());
                                }else{
                                    logger.info("==>2.1[{}]验证未通过!.....",context.getTaskId());
                                    result.setResult(StatusCode.短信验证码错误);
                                    return result;
                                }
                                logger.info("==>1.1[{}]采集个人信息开始.....", context.getTaskId());
                                url = "http://sd.189.cn/selfservice/cust/querymanage?100";
                                header.clear();
                                header.put("Content-Type", "application/json");
                                header.put("Accept", "application/json, text/javascript, */*");
                                bodyStr ="{}";
                                resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                                String  strResultPage = resultPage.getWebResponse().getContentAsString();
                                if (!StringUtils.isEmpty(strResultPage)) {
                                    cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(),
                                            resultPage);
                                }
                                String realName = RegexUtils.matchValue("\"name\":\"(.*?)\",", resultPage.getWebResponse().getContentAsString());
                                String identityCard = RegexUtils.matchValue("\"indentNbr\":\"(.*?)\"", resultPage.getWebResponse().getContentAsString());
                                cacheContainer.putString("realName", realName);
                                cacheContainer.putString("identityCard", identityCard);
                                //入网时间获取
                                url = "http://sd.189.cn/selfservice/cust/loadMyProductInfo";
                                bodyStr = "{\"accNbr\":\"" + context.getUserName() + "\",\"areaCode\":\"" + context.getParam1() + "\",\"accNbrType\":\"4\",\"queryType\":\"2\",\"queryMode\":\"1\"}";
                                resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                                strResultPage =  resultPage.getWebResponse().getContentAsString();
                                if (!StringUtils.isEmpty(strResultPage)) {
                                    cacheContainer.putPage(ProcessorCode.REGISTER_DATE.getCode(),
                                            resultPage);
                                }
                                //星级获取
                                url = "http://sd.189.cn/selfservice/vip/userStarInfo";
                                bodyStr = "{}";
                                resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                                strResultPage = resultPage.getWebResponse().getContentAsString();
                                if (!StringUtils.isEmpty(strResultPage)) {
                                    cacheContainer.putPage(ProcessorCode.VIP_LVL.getCode(),
                                            resultPage);
                                }
                                logger.info("==>1.1[{}]抓取基本信息成功.",context.getTaskId());
                                logger.info("==>1.2[{}]抓取套餐信息开始.",context.getTaskId());
                                url = "http://sd.189.cn/selfservice/cust/queryAllProductInfo";
                                header.clear();
                                header.put("Content-Type", "application/json");
                                header.put("Accept", "application/json, text/javascript, */*");
//                                bodyStr ="{\"accNbr\":\"" + context.getUserName() + "\",\"accNbrType\":\"4\",\"areaCode\":\"" + context.getParam1() + "\"}";
                                bodyStr = "{}";
                                resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                                cacheContainer.putPage(ProcessorCode.POINTS_VALUE.getCode(),
                                        resultPage);
                                logger.info("==>1.2[{}]抓取套餐信息成功.",context.getTaskId());
                                logger.info("==>1.3[{}]抓取余额信息开始.",context.getTaskId());
                                url = "http://sd.189.cn/selfservice/bill/";
                                resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,null);
                                url = "http://sd.189.cn/selfservice/bill/queryBalance";
                                header.clear();
                                header.put("Content-Type", "application/json");
                                header.put("Accept", "application/json, text/javascript, */*");
                                bodyStr = "{\"accNbr\":\"" + context.getUserName() + "\",\"areaCode\":\"" + context.getParam1() + "\",\"accNbrType\":\"4\"}";
                                resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                                cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(),
                                        resultPage);
                                result.setSuccess();
                                logger.info("==>1.3[{}]抓取余额信息成功.",context.getTaskId());
                                result.setSuccess();

                            }
                        }
                    }else{
                        logger.info("==>2.1[{}]发送短信验证码失败！.....",context.getTaskId());
                        result.setResult(StatusCode.发送短信验证码失败);
                        return result;
                    }
                } else {
                    logger.info("==>2.1[{}]接收用户输入超时.", context.getTaskId());
                    result.setResult(StatusCode.接收用户输入超时);
                    return result;
                }
            } else {
                logger.info("==>2.1[{}]发送图片验证码失败.", context.getTaskId());
                result.setResult(StatusCode.发送图片验证码错误);
                return result;
            }
        } catch (Exception ex) {
            logger.error("==短信验证码异常:[{}]", ex);
            result.setResult(StatusCode.解析办理业务信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        HashMap<String, String > header = new HashMap<>();
        List<Page> pages = new ArrayList<>();
        String url = "";
        try {
            String verifyCode = getVerifyCode(webClient, "http://sd.189.cn/selfservice/validatecode/codeimg.jpg", null);
                result.setResult(StatusCode.爬取中);
                context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_IMG);
                sendUpdateLog(result, context);
                boolean isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_IMG, verifyCode);
                if (isFlag) {
                    String verifyImgCode = rotation(context,webClient,120);
                    if (StringUtils.isNotBlank(verifyImgCode)) {
                        header.clear();
                        header.put("Content-Type", "application/json");
                        header.put("Accept", "application/json, text/javascript, */*");
                        url = "http://sd.189.cn/selfservice/service/sendSms";
                        String bodyStr = "{\"orgInfo\":\"" + context.getUserName() + "\",\"valicode\":\"" + verifyImgCode + "\",\"smsFlag\":\"real_2busi_validate\"}";
                        Page resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                        if("3".equals(resultPage.getWebResponse().getContentAsString())){
                            logger.info("==>2.1[{}]你输入的验证码错误！.....",context.getTaskId());
                            result.setResult(StatusCode.图片验证码错误);
                            return result;
                        }else if("0".equals(resultPage.getWebResponse().getContentAsString())){
                            logger.info("==>2.1[{}]发送短信成功！.....",context.getTaskId());
                            context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
                            isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
                            if (isFlag) {
                                String verifySmsCode = rotation(context,webClient,120);
                                if (!StringUtils.isEmpty(verifySmsCode)) {
                                    url = "http://sd.189.cn/selfservice/service/realnVali";
                                    header.clear();
                                    header.put("Content-Type", "application/json");
                                    header.put("Accept", "application/json, text/javascript, */*");
                                    bodyStr = "{\"username_2busi\":\"" + URLEncoder.encode(cacheContainer.getString("realName"), "utf-8") + "\",\"credentials_type_2busi\":\"1\",\"credentials_no_2busi\":\"" + cacheContainer.getString("identityCard") + "\",\"validatecode_2busi\":\"" + verifyImgCode + "\",\"randomcode_2busi\":\"" + verifySmsCode + "\",\"randomcode_flag\":\"0\",\"rid\":1,\"fid\":\"bill_monthlyDetail\"}";
                                    resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
                                    if(resultPage.getWebResponse().getContentAsString().contains("\"retnCode\":3")){
                                        logger.info("==>2.1[{}]短信随机密码超时，请重新获取短信!.....",context.getTaskId());
                                        result.setResult(StatusCode.接收短信验证码超时);
                                        return result;
                                    }else if(resultPage.getWebResponse().getContentAsString().contains("\"retnCode\":5")){
                                        logger.info("==>2.1[{}]验证未通过!.....",context.getTaskId());
                                        result.setResult(StatusCode.短信验证码错误);
                                        return result;
                                    }else if(resultPage.getWebResponse().getContentAsString().contains("\"retnCode\":0")){
                                        logger.info("==>2.1[{}]二次短信验证通过！.....",context.getTaskId());
                                    }else{
                                        logger.info("==>2.1[{}]验证未通过!.....",context.getTaskId());
                                        result.setResult(StatusCode.短信验证码错误);
                                        return result;
                                    }
                                    List<NameValuePair> reqParam = new ArrayList<>();
            //二次验证
                                    for (int i = 0; i >= -6; i--){
                                        String startDate = DateUtils.getFirstDay("yyyyMMdd", i);// 指定月月初
                                        String endDate = DateUtils.getLastDay("yyyyMMdd", i);// 指定月末
                                        String logFlag = String.format("==>2." + i + "["+context.getTaskId()+"]采集[" + startDate
                                                + "]至[{" + endDate + "}]通话详单.....");
                                        header.clear();
                                        header.put("Content-Type", "application/json");
                                        header.put("Accept", "application/json, text/javascript, */*");
                                        url = "http://sd.189.cn/selfservice/bill/queryBillDetailNum";
                                        bodyStr = "{\"accNbr\":\"" + context.getUserName() + "\",\"billingCycle\":\"" + startDate.substring(0,6) + "\",\"ticketType\":\"0\"}";
                                        resultPage = getPage(webClient, url, HttpMethod.POST, null,
                                                Constants.MAX_RETRY_TIIMES, logFlag, header,bodyStr);
                                        if(resultPage.getWebResponse().getContentAsString().contains("\"records\":\"0\"")){
                                            logger.info("==>2.1[{}]没有数据!.....",context.getTaskId());
                                            continue;
                                        }
                                        url = "http://sd.189.cn/selfservice/bill/queryBillDetail";
                                        header.clear();
                                        header.put("Content-Type", "application/json");
                                        header.put("Accept", "application/json, text/javascript, */*");
                                        bodyStr = "{\"accNbr\":\"" + context.getUserName() + "\",\"billingCycle\":\"" + startDate.substring(0,6) + "\",\"pageRecords\":\"" + 500000 + "\",\"pageNo\":\"1\",\"qtype\":\"0\",\"totalPage\":\"1\",\"queryType\":\"4\"}";
                                        resultPage = getPage(webClient, url, HttpMethod.POST, null,
                                                Constants.MAX_RETRY_TIIMES, logFlag, header,bodyStr);
                                        if(resultPage.getWebResponse().getContentAsString().contains("抱歉，请求参数的号码非法")){
                                            continue;
                                        }
                                        //重写下载xls便于解析
                                        url = "http://sd.189.cn/selfservice/bill/billDownload";
                                        header.clear();
                                        header.put("Content-Type", "application/json");
                                        header.put("Accept", "application/json, text/javascript, */*");
                                        reqParam.clear();
                                        reqParam.add(new NameValuePair("accNbr", context.getUserName()));
                                        reqParam.add(new NameValuePair("billingCycle", startDate.substring(0,6)));
                                        reqParam.add(new NameValuePair("qtype", "0"));
                                        resultPage = getPage(webClient, url, HttpMethod.GET, reqParam,
                                                Constants.MAX_RETRY_TIIMES, logFlag, header);
                                        if(resultPage.getWebResponse().getContentAsString().contains("抱歉，请求参数的号码非法")){
                                            continue;
                                        }
                                        pages.add(resultPage);

                                    }
                                }
                                cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode(),
                                        pages);
                                logger.info("==>2.1[{}]采集通话详单成功",context.getTaskId());
                                result.setSuccess();
                            }
                        }else{
                            logger.info("==>2.1[{}]发送短信失败！.....",context.getTaskId());
                            result.setResult(StatusCode.发送短信验证码失败);
                            return result;
                        }
                    } else {
                        logger.info("==>2.1[{}]获取图片验证码错误.", context.getTaskId());
                        result.setResult(StatusCode.获取图片验证码错误);
                        return result;
                    }
                } else {
                    logger.info("==>2.1[{}]发送图片验证码失败.", context.getTaskId());
                    result.setResult(StatusCode.发送图片验证码失败);
                    return result;
                }
        } catch (Exception ex) {
            logger.error("==短信验证码异常:[{}]", ex);
            result.setResult(StatusCode.解析办理业务信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        HashMap<String, String > header = new HashMap<>();
        List<Page> pageList = new ArrayList<>();
        logger.info("==>3.1[{}]采集短信信息开始",context.getTaskId());
        List<NameValuePair> reqParam = new ArrayList<>();
        try{
            for (int i = 0; i >= -6; i--){
                String startDate = DateUtils.getFirstDay("yyyyMMdd", i);// 指定月月初
                String endDate = DateUtils.getLastDay("yyyyMMdd", i);// 指定月末
                String logFlag = String.format("==>3." + i + "["+context.getTaskId()+"]采集[" + startDate
                        + "]至[{" + endDate + "}]短信详单.....");
                String url = "http://sd.189.cn/selfservice/bill/queryBillDetailNum";
                header.clear();
                header.put("Content-Type", "application/json");
                header.put("Accept", "application/json, text/javascript, */*");
                String bodyStr = "{\"accNbr\":\"" + context.getUserName() + "\",\"billingCycle\":\"" + startDate.substring(0,6) + "\",\"ticketType\":\"1\"}";
                Page resultPage = getPage(webClient, url, HttpMethod.POST, null,
                        Constants.MAX_RETRY_TIIMES, logFlag, header,bodyStr);
                if(resultPage.getWebResponse().getContentAsString().contains("\"records\":\"0\"")){
                    logger.info("=>4.1没有数据!.....");
                    continue;
                }
                //重写下载xls便于解析
                url = "http://sd.189.cn/selfservice/bill/billDownload";
                header.clear();
                header.put("Content-Type", "application/json");
                header.put("Accept", "application/json, text/javascript, */*");
                reqParam.clear();
                reqParam.add(new NameValuePair("accNbr", context.getUserName()));
                reqParam.add(new NameValuePair("billingCycle", startDate.substring(0,6)));
                reqParam.add(new NameValuePair("qtype", "1"));
                resultPage = getPage(webClient, url, HttpMethod.GET, reqParam,
                        Constants.MAX_RETRY_TIIMES, logFlag, header);
                pageList.add(resultPage);
            }
            cacheContainer.putPages(ProcessorCode.SMS_INFO.getCode(), pageList);
            logger.info("==>3.1[{}]采集短信记录成功",context.getTaskId());
            result.setSuccess();
        }catch (Exception e){
            logger.error("==>采集短信记录出现异常", e);
            result.setResult(StatusCode.解析短信记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<Page> pageList = new ArrayList<>();
        HashMap<String, String > header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
        logger.info("==>4.1[{}]采集上网信息开始",context.getTaskId());
        try{
            for (int i = 0; i >= -6; i--){
                String startDate = DateUtils.getFirstDay("yyyyMMdd", i);// 指定月月初
                String endDate = DateUtils.getLastDay("yyyyMMdd", i);// 指定月末
                String logFlag = String.format("==>4." + i + "["+context.getTaskId()+"]采集[" + startDate
                        + "]至[{" + endDate + "}]上网详单.....");
                String url = "http://sd.189.cn/selfservice/bill/queryBillDetailNum";
                header.clear();
                header.put("Content-Type", "application/json");
                header.put("Accept", "application/json, text/javascript, */*");
                String bodyStr = "{\"accNbr\":\"" + context.getUserName() + "\",\"billingCycle\":\"" + startDate.substring(0,6) + "\",\"ticketType\":\"3\"}";
                Page resultPage = getPage(webClient, url, HttpMethod.POST, null,
                        Constants.MAX_RETRY_TIIMES, logFlag, header,bodyStr);
                if(resultPage.getWebResponse().getContentAsString().contains("\"records\":\"0\"")){
                    logger.info("=>5.1没有数据!.....");
                    continue;
                }
                //重写下载xls便于解析
                url = "http://sd.189.cn/selfservice/bill/billDownload";
                header.clear();
                header.put("Content-Type", "application/json");
                header.put("Accept", "application/json, text/javascript, */*");
                reqParam.clear();
                reqParam.add(new NameValuePair("accNbr", context.getUserName()));
                reqParam.add(new NameValuePair("billingCycle", startDate.substring(0,6)));
                reqParam.add(new NameValuePair("qtype", "3"));
                resultPage = getPage(webClient, url, HttpMethod.GET, reqParam,
                        Constants.MAX_RETRY_TIIMES, logFlag, header);
                pageList.add(resultPage);
            }
            cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), pageList);
            logger.info("==>4.1[{}]采集上网记录成功",context.getTaskId());
            result.setSuccess();
        }catch (Exception e){
            logger.error("==>采集上网记录出现异常", e);
            result.setResult(StatusCode.解析上网记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<Page> pageList = new ArrayList<>();
        HashMap<String, String > header = new HashMap<>();
        logger.info("==>5.1[{}]采集账单信息开始",context.getTaskId());
        try{
            header.clear();
            header.put("Content-Type", "application/json");
            header.put("Accept", "application/json, text/javascript, */*");
            String url = "http://sd.189.cn/selfservice/cust/checkIsLogin";//检测登录
            String bodyStr = "{\"basePath\":\"http://sd.189.cn/selfservice/bill?tag=queryBalance\"}";
            Page resultPage = getPage(webClient, url, HttpMethod.POST, null,
                    Constants.MAX_RETRY_TIIMES, null, header,bodyStr);
            String msg = resultPage.getWebResponse().getContentAsString();
            JSONObject jsonObject = JSONObject.parseObject(msg);
            String areaCode = jsonObject.getString("areaCode");
            context.setParam1(areaCode);//将城市代码传入
            for (int i = -1; i >= -6; i--){
                String startDate = DateUtils.getFirstDay("yyyyMMdd", i);// 指定月月初
                String endDate = DateUtils.getLastDay("yyyyMMdd", i);// 指定月末
                String month = DateUtils.getDiffMonth("yyyyMM", i);
                String logFlag = String.format("==>5." + i + "["+context.getTaskId()+"]采集[" + startDate
                        + "]至[{" + endDate + "}]账单.....");
                url = "http://sd.189.cn/selfservice/bill/getCustBill";
                header.clear();
                header.put("Content-Type", "application/json");
                header.put("Accept", "application/json, text/javascript, */*");
                bodyStr = "{\"accNbr\":\"" + context.getUserName() + "\",\"areaCode\":\""+areaCode+"\",\"billCycle\":\""+month+"\",\"ptype\":\"4\"}";
                resultPage = getPage(webClient, url, HttpMethod.POST, null,
                        Constants.MAX_RETRY_TIIMES, logFlag, header,bodyStr);
                if(resultPage.getWebResponse().getContentAsString().contains("{}")){
                    logger.info("==>5.1[{}]没有数据!.....",context.getTaskId());
                    continue;
                }
                pageList.add(resultPage);
            }
            cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pageList);
            result.setSuccess();
            logger.info("==>5.1[{}]采集账单信息成功",context.getTaskId());
        }catch (Exception e){
            logger.error("==>采集账单信息出现异常", e);
            result.setResult(StatusCode.解析账单信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        HashMap<String, String > header = new HashMap<>();
        try {
            String url = "http://sd.189.cn/selfservice/bill/queryOfferInfo";
            String areaCode = context.getParam1();
            String bodyStr = "{\"accNbr\":\""+context.getUserName()+"\",\"areaCode\":\""+areaCode+"\",\"accNbrType\":\"4\"}";
            header.clear();
            header.put("Content-Type", "application/json");
            header.put("Accept", "application/json, text/javascript, */*");
            // 查询套餐使用情况
            String logFlag = String.format("==>6.["+context.getTaskId()+"]套餐使用情况,第[{}]次尝试请求.....");
            Page page = getPage(webClient, url, HttpMethod.POST, null,
                    Constants.MAX_RETRY_TIIMES, null, header,bodyStr);
            if(null != page){
                logger.info("==>6.[{}]采集套餐使用情况成功.", context.getTaskId());
                cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), page);
            }else{
                logger.info("==>6.[{}]采集套餐使用情况结束,没有查询结果.", context.getTaskId());
            }
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>6.[{}]采集账单信息出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.解析账单信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processUserFamilyMember(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        logger.info("==>7.[{}]暂无亲情号码取样", context.getTaskId());
        return new Result(StatusCode.SUCCESS);
    }

    @Override
    public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            Calendar calendar = Calendar.getInstance();
            HashMap<String, String > header = new HashMap<>();
            String url = "http://sd.189.cn/selfservice/bill/queryPaymentDetail";
            final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            calendar.add(Calendar.MONTH, -6);
            final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            String areaCode = context.getParam1();
            String bodyStr = "{\"accNbr\":\""+context.getUserName()+"\",\"beginDate\":\""+transDateBegin+"\",\"endDate\":\""+transDateEnd+"\",\"areaCode\":\""+areaCode+"\",\"accNbrType\":\"4\"}";
            header.clear();
            header.put("Content-Type", "application/json");
            header.put("Accept", "application/json, text/javascript, */*");
            String logFlag = String.format("==>8.["+context.getTaskId()+"]采集充值记录["+transDateBegin+"]-["+transDateEnd+"],第[{}]次尝试请求.....");
            Page page = getPage(webClient, url, HttpMethod.POST, null,
                    Constants.MAX_RETRY_TIIMES, null, header,bodyStr);
            if (null != page) {// 添加缓存信息
            	cacheContainer.putPage(ProcessorCode.RECHARGE_INFO.getCode(), page);
                logger.info("==>8.[{}]采集充值记录[半年以内]成功.", context.getTaskId());
            } else {
                logger.info("==>8.[{}]采集充值记录[半年以内]结束,没有查询结果.", context.getTaskId());
            }
           
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>8.[{}]采集充值记录出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.解析充值记录出错);
            return result;
        }
        return result;
    }

    @Override
    protected Result loginout(WebClient webClient){
        Result result = new Result();
        logger.info("==>9.电信统一退出开始.....");
        String url = "http://www.189.cn/login/logout.do";
        getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);

        result.setSuccess();
        return result;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
