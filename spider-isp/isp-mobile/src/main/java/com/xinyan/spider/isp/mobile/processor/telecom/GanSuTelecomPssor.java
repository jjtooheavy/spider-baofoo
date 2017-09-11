package com.xinyan.spider.isp.mobile.processor.telecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.GanSuTelecomParser;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 甘肃电信处理类
 *
 * @author jiangmengchen
 * @version V1.0
 * @description
 * @date 2017年6月3日 下午3:38:43
 */
@Component
public class GanSuTelecomPssor extends AbstractTelecomPssor {
    @Autowired
    private GanSuTelecomParser parser;
    protected static Logger logger = LoggerFactory.getLogger(GanSuTelecomPssor.class);

    @Override
    public Result login(WebClient webClient, Context context) {
        Result result = new Result();
        try {
            // 1.开始登陆
            logger.info("==>[{}]1.用户正常认证开始.....", context.getTaskId());
            String returnCode = telecomLogin(webClient, context, true);
            if ("0000".equals(returnCode)) {
                result.setResult(StatusCode.登陆成功);
            }
            return result;
        } catch (Exception e) {
            logger.error("==>[{}]1.用户正常认证出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.登陆失败);
            return result;
        } finally {
            sendLoginMsg(result, context, true, webClient);
        }
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
        result.setSuccess();
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
            getLogger().error("==>[{}]甘肃数据采集出现异常", context.getTaskId(), ex);
            result.setFail();
            return result;
        } finally {
            sendAnalysisMsg(result, context);
        }

        getLogger().info("==>[{}]数据抓取结束{}\n,详情:{}", context.getTaskId(), result, result.getData());
        return result;
    }

    @Override
    public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            logger.info("==>[{}]1.1采集基本信息开始", context.getTaskId());
            String url = "";
            Page page;
            Map<String, String> header = new HashMap<>();
            url = "http://www.189.cn/dqmh/my189/checkMy189Session.do";
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("fastcode", "20000072"));
            page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
            header.clear();
            header.put("Referer", "http://www.189.cn/dqmh/my189/initMy189home.do");
            url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10028&toStUrl=http://gs.189.cn/web/self/myPackageSearchV7.action?fastcode=20000072&cityCode=gs";
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            url = "http://gs.189.cn/web/self/myPackageSearchV7.action?fastcode=20000072&cityCode=gs";
            header.clear();
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            logger.info("==>[{}]2.1正在请求发送短信验证码开始.....", context.getTaskId());
            result.setResult(StatusCode.爬取中);
            context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
            sendUpdateLog(result, context);

            url = "http://gs.189.cn/web/json/sendSMSRandomNumSMZ.action";
            header.clear();
            header.put("Referer", "http://gs.189.cn/web/self/myPackageSearchV7.action?fastcode=20000072&cityCode=gs");
            reqParam.clear();
            reqParam.add(new NameValuePair("productGroup", "4:13321312850"));
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);

            if (page.getWebResponse().getContentAsString().contains("\"result\":1")) {
                logger.info("==>[{}]2.1已经发送短信验证码.", context.getTaskId());

                //从redis中取得短信验证码
                logger.info("==>[{}]2.1等待接收短信验证码.....", context.getTaskId());
                String msgCode = "";
                boolean isCodeSend = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
                if (isCodeSend) {
                    msgCode = rotation(context,webClient,120);
                }
                if (!StringUtils.isEmpty(msgCode)) {//获取到了短信验证码
                    reqParam.clear();
                    reqParam.add(new NameValuePair("mobilenum", context.getUserName()));
                    reqParam.add(new NameValuePair("busitype", "3"));
                    reqParam.add(new NameValuePair("validatecode", msgCode));
                    reqParam.add(new NameValuePair("smsLevel", "4"));
                    url = "http://gs.189.cn/web/commonJson/checkMobileValidateCodeForSmz.action";
                    header.clear();
                    header.put("Referer", "http://gs.189.cn/web/self/myPackageSearchV7.action?fastcode=20000072&cityCode=gs");
                    page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);

                    if (!page.getWebResponse().getContentAsString().contains("1")) {//您输入的查询验证码错误或过期
                        //短信验证码错误
                        result.setResult(StatusCode.短信验证码错误);
                        return result;
                    } else {
                        logger.info("==>2.1[{}]短信验证码正确.", context.getTaskId());
                    }
                } else {//接收短信验证码超时
                    result.setResult(StatusCode.接收短信验证码超时);
                    return result;
                }
            } else {//获发送短信验证码失败
                result.setResult(StatusCode.发送短信验证码失败);
                return result;
            }

            url = "http://gs.189.cn/web/self/showMyPackageV7.action";
            header.clear();
            reqParam.clear();
            reqParam.add(new NameValuePair("productGroup", "4:13321312850"));
            header.put("Referer", "http://gs.189.cn/web/self/myPackageSearchV7.action?fastcode=20000072&cityCode=gs");
            page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,header);
            cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);
            url = "http://gs.189.cn/service/v7/fycx/ssye/index.shtml?fastcode=10000595&cityCode=gs";
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            url = "http://gs.189.cn/web/v7/fee/getBalanceFeeInfo.action";
            header.clear();
            reqParam.clear();
            reqParam.add(new NameValuePair("productInfo", "4:13321312850"));
            reqParam.add(new NameValuePair("functionCode", "010001001003"));
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), page);
            //星级等信息
            url = "http://gs.189.cn/web/jsonV6/getUserInfo.action";
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.VIP_LVL.getCode(), page);
            //身份证
            url = "http://gs.189.cn/web/self/showMyProfileV7.action";
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            String idCard = RegexUtils.matchValue("身份证</p></td>(.*?)</td>", page.getWebResponse().getContentAsString().replaceAll("\\s",""));
            context.setIdCard(idCard.replaceAll("<.*?>",""));
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集基本信息出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集基本信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            List<Page> pages = getCallAndSmsAndNetInfo(webClient, context, 6);
            cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集通话详情出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集通话详情出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            List<Page> pages = getCallAndSmsAndNetInfo(webClient, context, 8);
            cacheContainer.putPages(ProcessorCode.SMS_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集短信记录出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集短信记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            List<Page> pages = getCallAndSmsAndNetInfo(webClient, context, 7);
            cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集上网记录出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集上网记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            Calendar calendar = Calendar.getInstance();
            List<NameValuePair> reqParam = new ArrayList<>();
            Map<String, String> header = new HashMap<>();
            header.put("referer", "http://gs.189.cn/service/v7/fycx/yzd/index.shtml?fastcode=10000599&cityCode=gs");
            String url = "http://gs.189.cn/web/v7/fee/getBillPay.action";

            List<Page> pages = new ArrayList<>();
            reqParam.clear();
            reqParam.add(new NameValuePair("productInfo", "4:13321312850"));
            String logFlag = String.format("==>[%s]6.采集账单信息,第[{}]次尝试请求.....", context.getTaskId());
            Page historyBillPage = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
            if (null != historyBillPage) {//添加缓存信息
                pages.add(historyBillPage);
                logger.info("==>[{}]6.采集账单信息成功", context.getTaskId());
            } else {
                logger.info("==>[{}]6.采集账单信息结束,没有查询结果", context.getTaskId());
            }
            cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集账单信息出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集账单记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        String url = "http://gs.189.cn/web/v7/fee/getPackageUseInfoV2.action";
        Map<String, String> header = new HashMap<>();
        try {
            List<Page> pages = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            for (int i = 1; i < 3; i++) {//包含当前月份
                List<NameValuePair> reqParam = new ArrayList<>();
                final String month = DateFormatUtils.format(calendar, "yyyy-MM");
                final String yearAndMonth = DateFormatUtils.format(calendar, "yyyyMM");
                reqParam.add(new NameValuePair("queryType", "type_package"));
                reqParam.add(new NameValuePair("searchTime", yearAndMonth));
                reqParam.add(new NameValuePair("productInfo", "4:13321312850"));
                String logFlag = String.format("==>[%s]6.%s采集[%s]套餐信息,第[{}]次尝试请求.....", context.getTaskId(), i, month);
                header.clear();
                header.put("Referer", "http://gs.189.cn/service/v7/fycx/tcsyqk/index.shtml?fastcode=10000598&cityCode=gs");
                Page packagePage = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
                pages.add(packagePage);
                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ProcessorCode.PACKAGE_ITEM.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>[{}]采集已办理业务信息出现异常:", context.getTaskId(), e);
            result.setResult(StatusCode.采集办理业务信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processUserFamilyMember(WebClient webClient, Context context, CarrierInfo carrierInfo,
                                          CacheContainer cacheContainer) {
        logger.info("==>7.[{}]暂无亲情号码取样", context.getTaskId());
        return new Result(StatusCode.SUCCESS);
    }

    @Override
    public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            logger.info("==>7.[{}]采集充值信息开始.....", context.getTaskId());

//            String url = "http://gs.189.cn/web/pay2/dealTurnV7.action?fastcode=10000608&cityCode=gs";
//            Page page = getPage(webClient,url,HttpMethod.POST,null,null);
//
//            url = "http://gs.189.cn/web/jsonV6/getUserStopInfo.action";
//            page = getPage(webClient,url,HttpMethod.POST,null,null);

            List<NameValuePair> reqParam = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            final String endDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 5);
            final String startDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            String url = "http://gs.189.cn/web/pay2/dealCheckV7.action";
            reqParam.clear();
            reqParam.add(new NameValuePair("numberType", "4:13321312850"));
            reqParam.add(new NameValuePair("beginTime", startDate));
            reqParam.add(new NameValuePair("endTime", endDate));
            Map<String,String> header = new HashMap<>();
            header.put("Referer","http://gs.189.cn/web/pay2/dealTurnV7.action?fastcode=10000608&cityCode=gs");
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam, header);
            if (page != null) {
                cacheContainer.putPage(ProcessorCode.RECHARGE_INFO.getCode(), page);
            }
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>8.[{}]解析充值记录出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.采集充值记录出错);
            return result;
        }
        return result;
    }

    @Override
    protected Result loginout(WebClient webClient) {
        Result result = new Result();
        logger.info("==>9.电信统一退出开始.....");
        String url = "http://www.189.cn/login/logout.do";
        getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

        result.setSuccess();
        return result;
    }

    private List<Page> getCallAndSmsAndNetInfo(WebClient webClient, Context context, int type) {
        List<Page> pages = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 1; i < 3; i++) {//包含当前月份
            final String transDate = DateFormatUtils.format(calendar, "yyyyMM");
            String url = "http://gs.189.cn/web/json/searchDetailedFee.action?timestamp=" + Calendar.getInstance().getTimeInMillis() + "&productGroup=4:13321312850&orderDetailType=" + type + "&queryMonth=" + transDate;
            String typeDetail = "";
            String num = "3";
            switch (type) {
                case 6:
                    typeDetail = "通话";
                    break;
                case 8:
                    typeDetail = "短信";
                    num = "4";
                    break;
                case 7:
                    typeDetail = "上网";
                    num = "5";
                    break;
            }
            String logFlag = String.format("==>[%s]%s.%s采集[%s]%s记录.....", context.getTaskId(), num, i, transDate, typeDetail);
            Page page = getPage(webClient, url, HttpMethod.POST, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
            pages.add(page);
            calendar.add(Calendar.MONTH, -1);
        }
        return pages;
    }


    @Override
    protected Logger getLogger() {
        return logger;
    }

}
