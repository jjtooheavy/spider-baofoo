package com.xinyan.spider.isp.mobile.processor.telecom;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.HeNanTelecomParser;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 河南电信处理类
 * Created by jiangmengchen on 17-5-27.
 *
 * @version V1.0
 */
@Component
public class HeNanTelecomPssor extends AbstractTelecomPssor {

    protected static Logger logger = LoggerFactory.getLogger(HeNanTelecomPssor.class);

    @Autowired
    private HeNanTelecomParser parser;

    /**
     * 正常认证登录
     *
     * @param webClient
     * @param context
     * @return
     */
    @Override
    public Result doLogin(WebClient webClient, Context context) {
        Result result = new Result();
        try {
            // 1.开始登陆
            logger.info("==>1.[{}]用户正常认证开始.....", context.getTaskId());
            String returnCode = telecomLogin(webClient, context, true);
            if ("0000".equals(returnCode)) {
                result.setResult(StatusCode.登陆成功);
            }
            return result;
        } catch (Exception e) {
            logger.error("==>1.[{}]用户正常认证出现异常:[{}]", context.getTaskId(), e);
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
            logger.error("==>[{}]河南数据采集出现异常:[{}]", context.getTaskId(), ex);
            result.setFail();
            return result;
        } finally {
            sendAnalysisMsg(result, context);
        }

        getLogger().info("==>[{}]数据抓取结束{}\n,详情:{}", context.getTaskId(), result, result.getData());
        return result;
    }

    @Override
    public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
                                  CacheContainer cacheContainer) {
        Result result = new Result();
        String url = "";
        try {
            logger.info("==>1.1[{}]采集基本信息开始.....", context.getTaskId());
            List<NameValuePair> reqParam = new ArrayList<>();

            url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10017&toStUrl=http://ha.189.cn/service/iframe/manage/my_selfinfo_iframe.jsp?fastcode=20000374&cityCode=ha";
            Map<String, String> header = new HashedMap<>();
            header.put("Referer", "http://www.189.cn/dqmh/my189/initMy189home.do");
            Page resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), resultPage);//添加缓存信息

            url = "http://ha.189.cn/service/iframe/bill/iframe_ye.jsp";
            reqParam.add(new NameValuePair("ACC_NBR", context.getUserName()));
            reqParam.add(new NameValuePair("PROD_TYPE", "933069769065"));
            reqParam.add(new NameValuePair("ACCTNBR97", ""));
            resultPage = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode() + 1, resultPage);//添加缓存信息

            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>1.[{}]采集基本信息出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集基本信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        List<Page> shihuaPages = new ArrayList<>();
        List<Page> changtuPages = new ArrayList<>();
        String url = "";
        try {
            Calendar calendar = Calendar.getInstance();
            logger.info("==>[{}]2.1正在请求发送短信验证码开始.....", context.getTaskId());
            result.setResult(StatusCode.爬取中);
            context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
            sendUpdateLog(result, context);

            url = "http://ha.189.cn/service/bill/getRand.jsp";
            String smsResult = "";
            final String date = DateFormatUtils.format(calendar, "yyyyMM");
            reqParam.clear();
            reqParam.add(new NameValuePair("PRODTYPE", "703069722362"));
            reqParam.add(new NameValuePair("RAND_TYPE", "002"));
            reqParam.add(new NameValuePair("BureauCode", "0370"));
            reqParam.add(new NameValuePair("ACC_NBR", context.getUserName()));
            reqParam.add(new NameValuePair("PROD_PWD", ""));
            reqParam.add(new NameValuePair("REFRESH_FLAG", "1"));
            reqParam.add(new NameValuePair("BEGIN_DATE", ""));
            reqParam.add(new NameValuePair("BEGIN_DATE", ""));
            reqParam.add(new NameValuePair("END_DATE", ""));
            reqParam.add(new NameValuePair("ACCT_DATE", date));
            reqParam.add(new NameValuePair("FIND_TYPE", "2"));
            reqParam.add(new NameValuePair("SERV_NO", ""));
            reqParam.add(new NameValuePair("QRY_FLAG", "1"));
            reqParam.add(new NameValuePair("ValueType", "4"));
            reqParam.add(new NameValuePair("MOBILE_NAME", context.getUserName()));
            reqParam.add(new NameValuePair("OPER_TYPE", "CR1"));
            reqParam.add(new NameValuePair("PASSWORD", ""));
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

            if (page.getWebResponse().getContentAsString().contains("您短时间内不能重复获取随机密码")) {
                result.setResult(StatusCode.发送短信验证码失败);
                return result;
            }

            boolean isCodeSend = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
            if (isCodeSend) {
                smsResult = rotation(context,webClient,120);
                if (StringUtils.isBlank(smsResult)) {
                    result.setResult(StatusCode.发送短信验证码失败);
                    return result;
                }
            } else {
                result.setResult(StatusCode.发送短信验证码失败);
                return result;
            }

            url = "http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp";
            reqParam.clear();
            reqParam.add(new NameValuePair("PRODTYPE", "703069722362"));
            reqParam.add(new NameValuePair("RAND_TYPE", "002"));
            reqParam.add(new NameValuePair("BureauCode", "0370"));
            reqParam.add(new NameValuePair("ACC_NBR", context.getUserName()));
            reqParam.add(new NameValuePair("PROD_TYPE", "703069722362"));
            reqParam.add(new NameValuePair("PROD_PWD", ""));
            reqParam.add(new NameValuePair("REFRESH_FLAG", "1"));
            reqParam.add(new NameValuePair("BEGIN_DATE", ""));
            reqParam.add(new NameValuePair("END_DATE", ""));
            reqParam.add(new NameValuePair("ACCT_DATE", date));
            reqParam.add(new NameValuePair("FIND_TYPE", "2"));
            reqParam.add(new NameValuePair("SERV_NO", ""));
            reqParam.add(new NameValuePair("QRY_FLAG", "1"));
            reqParam.add(new NameValuePair("ValueType", "4"));
            reqParam.add(new NameValuePair("MOBILE_NAME", context.getUserName()));
            reqParam.add(new NameValuePair("OPER_TYPE", "CR1"));
            reqParam.add(new NameValuePair("PASSWORD", smsResult));

            getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
            // 查询最近6个月通话详单
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                final String month = DateFormatUtils.format(calendar, "yyyyMM");
                String logFlag = String.format("==>3.%s[%s]采集[%s]市话通话记录,第[{}]次尝试请求.....", i, context.getTaskId(), month);
                reqParam.clear();
                reqParam.add(new NameValuePair("ACC_NBR", context.getUserName()));
                reqParam.add(new NameValuePair("PRODTYPE", "703069722362"));
                reqParam.add(new NameValuePair("BEGIN_DATE", ""));
                reqParam.add(new NameValuePair("END_DATE", ""));
                reqParam.add(new NameValuePair("ValueType", "4"));
                reqParam.add(new NameValuePair("REFRESH_FLAG", "1"));
                reqParam.add(new NameValuePair("radioQryType", "on"));
                reqParam.add(new NameValuePair("QRY_FLAG", "1"));
                reqParam.add(new NameValuePair("ACCT_DATE", month));
                reqParam.add(new NameValuePair("ACCT_DATE_1", month));
                List<NameValuePair> shihuaReqParam = new ArrayList<>(reqParam);
                shihuaReqParam.add(new NameValuePair("FIND_TYPE", "2"));
                Page shihua = getPage(webClient, url, HttpMethod.POST, shihuaReqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != shihua) {
                    if (!shihua.getWebResponse().getContentAsString().contains("没有合适的产品")) {
                        logger.info("==>3.[{}]采集[{}]市话通话记录成功.{}", i, month, context.getTaskId());
                        shihuaPages.add(shihua);
                    } else {
                        logger.info("==>3.[{}]采集[{}]市话通话记录结束,没有查询结果.{}", i, month, context.getTaskId());
                    }
                } else {
                    logger.info("==>3.[{}]采集[{}]市话通话记录结束,没有查询结果.{}", i, month, context.getTaskId());
                }
                List<NameValuePair> changtuReqParam = new ArrayList<>(reqParam);

                logFlag = String.format("==>3.%s[%s]采集[%s]长途通话记录,第[{}]次尝试请求.....", i, context.getTaskId(), month);
                changtuReqParam.add(new NameValuePair("FIND_TYPE", "1"));
                Page changtu = getPage(webClient, url, HttpMethod.POST, changtuReqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != changtu) {
                    if (!changtu.getWebResponse().getContentAsString().contains("没有合适的产品")) {
                        logger.info("==>3.[{}]采集[{}]长途通话记录成功.", context.getTaskId(), month);
                        changtuPages.add(changtu);
                    } else {
                        logger.info("==>3.[{}]采集[{}]长途通话记录结束,没有查询结果.", context.getTaskId(), month);
                    }
                } else {
                    logger.info("==>3.[{}]采集[{}]长途通话记录结束,没有查询结果.", context.getTaskId(), month);
                }
                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode(), shihuaPages);
            cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode() + 1, changtuPages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>2.[{}]采集通话详情出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集通话详情出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        List<Page> pages = new ArrayList<>();
        String url = "http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp";
        try {
            Calendar calendar = Calendar.getInstance();

            // 查询最近6个月短信记录
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                final String month = DateFormatUtils.format(calendar, "yyyyMM");
                String logFlag = String.format("==>3.%s[%s]采集[%s]短信记录,第[{}]次尝试请求.....", i, context.getTaskId(), month);
                reqParam.clear();
                reqParam.add(new NameValuePair("ACC_NBR", context.getUserName()));
                reqParam.add(new NameValuePair("PRODTYPE", "703069722362"));
                reqParam.add(new NameValuePair("BEGIN_DATE", ""));
                reqParam.add(new NameValuePair("END_DATE", ""));
                reqParam.add(new NameValuePair("ValueType", "4"));
                reqParam.add(new NameValuePair("REFRESH_FLAG", "1"));
                reqParam.add(new NameValuePair("FIND_TYPE", "5"));
                reqParam.add(new NameValuePair("radioQryType", "on"));
                reqParam.add(new NameValuePair("QRY_FLAG", "1"));
                reqParam.add(new NameValuePair("ACCT_DATE", month));
                reqParam.add(new NameValuePair("ACCT_DATE_1", month));
                Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != page && !page.getWebResponse().getContentAsString().contains("没有合适的产品")) {
                    logger.info("==>3.[{}]采集[{}]短信记录成功.[{}]", i, month, context.getTaskId());
                    pages.add(page);
                } else {
                    logger.info("==>3.[{}]采集[{}]短信记录结束,没有查询结果.[{}]", i, month, context.getTaskId());
                }
                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ProcessorCode.SMS_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>3.[{}]采集短信记录出现异常", context.getTaskId(), e);
            result.setResult(StatusCode.采集短信记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        List<Page> pages = new ArrayList<>();
        String url = "http://ha.189.cn/service/listQuery/fycx/inxxall.jsp";
        try {
            Calendar calendar = Calendar.getInstance();

            // 查询最近6个月短信记录
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                final String month = DateFormatUtils.format(calendar, "yyyyMM");
                String logFlag = String.format("==>3.%s[%s]采集[%s]上网记录",i,context.getTaskId(),month);
                reqParam.clear();
                reqParam.add(new NameValuePair("ACC_NBR", context.getUserName()));
                reqParam.add(new NameValuePair("PRODTYPE", "703069722362"));
                reqParam.add(new NameValuePair("BEGIN_DATE", ""));
                reqParam.add(new NameValuePair("END_DATE", ""));
                reqParam.add(new NameValuePair("ValueType", "4"));
                reqParam.add(new NameValuePair("REFRESH_FLAG", "1"));
                reqParam.add(new NameValuePair("FIND_TYPE", "4"));
                reqParam.add(new NameValuePair("radioQryType", "on"));
                reqParam.add(new NameValuePair("QRY_FLAG", "1"));
                reqParam.add(new NameValuePair("ACCT_DATE", month));
                reqParam.add(new NameValuePair("ACCT_DATE_1", month));
                Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != page) {
                    if (!page.getWebResponse().getContentAsString().contains("没有合适的产品")) {
                        logger.info("==>3.[{}]采集[{}]上网记录成功.", context.getTaskId(), month);
                        pages.add(page);
                    } else {
                        logger.info("==>3.[{}]采集[{}]上网记录结束,没有查询结果.", context.getTaskId(), month);
                    }
                } else {
                    logger.info("==>3.[{}]采集[{}]上网记录结束,没有查询结果.", context.getTaskId(), month);
                }
                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>3.[{}]采集上网记录出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集上网记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        List<NameValuePair> reqParam = new ArrayList<>();
        List<Page> pages = new ArrayList<>();
        String url = "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            // 查询最近6个月通话详单
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                final String month = DateFormatUtils.format(calendar, "yyyyMM");
                reqParam.clear();
                reqParam.add(new NameValuePair("ACC_NBR", context.getUserName()));
                reqParam.add(new NameValuePair("AreaCode", "0370"));
                reqParam.add(new NameValuePair("usertype", "1"));
                reqParam.add(new NameValuePair("DATE", month));
                url = "http://ha.189.cn/service/iframe/bill/iframe_zd.jsp";
                String logFlag = String.format("==>2." + i + "[" + context.getTaskId() + "]采集[" + month + "]账单,第[{}]次尝试请求.....");
                Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != page) {//添加缓存信息
                    if (!page.getWebResponse().getContentAsString().contains("数据库查询失败")) {
                        logger.info("==>2.[{}]采集[{}]账单成功.", context.getTaskId(), month);
                        pages.add(page);
                    } else {
                        logger.info("==>2.[{}]采集[{}]账单结束,没有查询结果.", context.getTaskId(), month);
                    }
                } else {
                    logger.info("==>2.[{}]采集[{}]账单结束,没有查询结果.", context.getTaskId(), month);
                }
                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>2.[{}]采集通话详情出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集账单记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            List<NameValuePair> reqParam = new ArrayList<>();
            String url = "http://ha.189.cn/service/iframe/bill/iframe_zyyw.jsp";
            String logFlag = String.format("==>6.[%s]采集[已办理]套餐信息,第[{}]次尝试请求.....",context.getTaskId());
            reqParam.add(new NameValuePair("ACC_NBR", context.getUserName()));
            reqParam.add(new NameValuePair("PROD_TYPE", "703069722362"));
            reqParam.add(new NameValuePair("ACCTNBR97", ""));
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
            if (null != page) {// 添加缓存信息
                logger.info("==>6.[{}]采集[已办理]套餐信息成功.", context.getTaskId());
                cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), page);
            } else {
                logger.info("==>6.[{}]采集[已办理]套餐信息结束,没有查询结果.", context.getTaskId());
            }
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>6.[{}]解析套餐信息出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集办理业务信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processUserFamilyMember(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        logger.info("==>7.[{}]暂无亲情号码取样");
        return new Result(StatusCode.SUCCESS);
    }

    @Override
    public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            Calendar calendar = Calendar.getInstance();
            List<NameValuePair> reqParam = new ArrayList<>();
            String url = "http://ha.189.cn/service/pay/khxxgl/myserv_snList.jsp";

            reqParam.add(new NameValuePair("REFRESH_FLAG", "2"));
            reqParam.add(new NameValuePair("IPAGE_INDEX", "1"));
            reqParam.add(new NameValuePair("ASK_TYPE", "100"));
            reqParam.add(new NameValuePair("SERV_TYPE1", ""));
            reqParam.add(new NameValuePair("AREACODE", "0371"));
            reqParam.add(new NameValuePair("SERV_NO1", ""));
            reqParam.add(new NameValuePair("OPEN_TYPE", ""));
            final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            reqParam.add(new NameValuePair("END_ASK_DATE", transDateBegin));
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 6);
            final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            reqParam.add(new NameValuePair("START_ASK_DATE", transDateEnd));
            reqParam.add(new NameValuePair("STATE", "ALL"));

            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

            cacheContainer.putPage(ProcessorCode.RECHARGE_INFO.getCode(), page);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>8.解析充值记录出现异常:[{}]", context.getTaskId(), e);
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

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
