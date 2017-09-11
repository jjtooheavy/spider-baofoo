package com.xinyan.spider.isp.mobile.processor.telecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.Base64;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.FujianTelecomParser;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 福建电信处理类
 *
 * @author jiangmengchen
 * @version V1.0
 * @description
 * @date 2017年6月3日 下午3:38:43
 */
@Component
public class FujianTelecomPssor extends AbstractTelecomPssor {
    @Autowired
    private FujianTelecomParser parser;
    protected static Logger logger = LoggerFactory.getLogger(FujianTelecomPssor.class);

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
            getLogger().error("==>[{}]福建数据采集出现异常", context.getTaskId(), ex);
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
            reqParam.add(new NameValuePair("fastcode", "01470663"));
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            header.clear();
            header.put("Referer", "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=01470663");
            url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10014&toStUrl=http://fj.189.cn/newcmsweb/commonIframe.jsp?URLPATH=/service/manage/index.jsp&id=6&fastcode=01470663&cityCode=fj";
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            url = "http://fj.189.cn/service/manage/productdetail.jsp";
            header.clear();
            header.put("Referer", "http://fj.189.cn/service/manage/index.jsp");
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);

            url = "http://fj.189.cn/BillAjaxServlet.do";
            header.clear();
            reqParam.clear();
            reqParam.add(new NameValuePair("method", "realtime"));
            reqParam.add(new NameValuePair("PRODNO", context.getUserName()));
            reqParam.add(new NameValuePair("PRODTYPE", "50"));
            header.put("Referer", "http://fj.189.cn/service/bill/realtime.jsp");
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), page);
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
            List<Page> pages = getCallAndSmsAndNetInfo(webClient, context, 0);
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
            List<Page> pages = getCallAndSmsAndNetInfo(webClient, context, 3);
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
            List<Page> pages = getCallAndSmsAndNetInfo(webClient, context, 2);
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
            header.put("referer", "http://fj.189.cn/service/bill/bill.jsp");
            String url = "http://fj.189.cn/service/bill/custbill.jsp";

            List<Page> pages = new ArrayList<>();
            //查询最近6个月账单
            for (int i = 1; i < 7; i++) {//前6个月(不算当月)
                calendar.add(Calendar.MONTH, -1);
                final String month = DateFormatUtils.format(calendar, "yyyy-MM");
                final String yearAndMonth = DateFormatUtils.format(calendar, "yyyyMM");

                reqParam.clear();
                reqParam.add(new NameValuePair("ck_month", yearAndMonth));
                reqParam.add(new NameValuePair("citycode", "593"));
                reqParam.add(new NameValuePair("PRODNO", context.getUserName()));
                reqParam.add(new NameValuePair("PRODTYPE", "50"));
                String logFlag = String.format("==>[%s]6.%s采集[%s]账单信息,第[{}]次尝试请求.....", context.getTaskId(), i, month);

                Page historyBillPage = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
                if (null != historyBillPage) {//添加缓存信息
                    pages.add(historyBillPage);
                    logger.info("==>[{}]6.{}采集[{}]账单信息成功", context.getTaskId(), i, month);
                } else {
                    logger.info("==>[{}]6.{}采集[{}]账单信息结束,没有查询结果", context.getTaskId(), i, month);
                }
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
        String url = "http://fj.189.cn/BillAjaxServlet.do";
        Map<String, String> header = new HashMap<>();
        try {
            List<Page> pages = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                List<NameValuePair> reqParam = new ArrayList<>();
                final String month = DateFormatUtils.format(calendar, "yyyy-MM");
                final String yearAndMonth = DateFormatUtils.format(calendar, "yyyyMM");
                reqParam.add(new NameValuePair("method", "formula"));
                reqParam.add(new NameValuePair("NUMBER", context.getUserName()));
                reqParam.add(new NameValuePair("NUMTYPE", "50"));
                reqParam.add(new NameValuePair("MONTH", yearAndMonth));
                String logFlag = String.format("==>[%s]6.%s采集[%s]套餐信息,第[{}]次尝试请求.....", context.getTaskId(), i, month);
                header.clear();
                header.put("Referer", "http://fj.189.cn/service/bill/formula.jsp");
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
            List<NameValuePair> reqParam = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            final String endDate = DateFormatUtils.format(calendar, "yyyyMM");
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 5);
            final String startDate = DateFormatUtils.format(calendar, "yyyyMM");
            String url = "http://fj.189.cn/service/pay/query/payAllQuery_Result.jsp";
            reqParam.clear();
            reqParam.add(new NameValuePair("isRequest", "yes"));
            reqParam.add(new NameValuePair("PRODTYPE", "50"));
            reqParam.add(new NameValuePair("PRODNO", context.getUserName()));
            reqParam.add(new NameValuePair("STARTTIME", startDate));
            reqParam.add(new NameValuePair("STARTH", startDate));
            reqParam.add(new NameValuePair("OVERTIME", endDate));
            reqParam.add(new NameValuePair("OVERH", endDate));
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
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
        getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);

        result.setSuccess();
        return result;
    }

    private List<Page> getCallAndSmsAndNetInfo(WebClient webClient, Context context, int type) {
        List<Page> pages = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
            final String transDate = DateFormatUtils.format(calendar, "yyyyMM");
            String url = "http://fj.189.cn/service/bill/result/mobile/mobile_call_result.jsp?MONTH=" + transDate + "&PRODNO=" + Base64.encode(context.getUserName()) + "&PRODTYPE=50&PAGENO=1&v_choosetype=" + type;
            String typeDetail = "";
            String num = "3";
            switch (type) {
                case 0:
                    typeDetail = "通话";
                    break;
                case 3:
                    typeDetail = "短信";
                    num = "4";
                    break;
                case 2:
                    url = "http://fj.189.cn/service/bill/mobile_net_result.jsp?MONTH=" + transDate + "&PRODNO=" + Base64.encode(context.getUserName()) + "=&PRODTYPE=50&v_choosetype=2";
                    typeDetail = "上网";
                    num = "5";
                    break;
            }
            String logFlag = String.format("==>[%s]%s.%s采集[%s]%s记录.....", context.getTaskId(), num, i, transDate, typeDetail);
            Page page = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
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
