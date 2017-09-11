package com.xinyan.spider.isp.mobile.processor.telecom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
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
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.HeiLongJiangTelecomParser;

/**
 * 黑龙江电信处理类
 *
 * @author jiangmengchen
 * @version V1.0
 * @description
 * @date 2017年6月3日 下午3:38:43
 */
@Component
public class HeiLongJiangTelecomPssor extends AbstractTelecomPssor {
    @Autowired
    private HeiLongJiangTelecomParser parser;
    protected static Logger logger = LoggerFactory.getLogger(HeiLongJiangTelecomPssor.class);

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
            getLogger().error("==>[{}]黑龙江数据采集出现异常", context.getTaskId(), ex);
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
            String url = "";
            Page resultPage;
            String pageInfo;
            Map<String, String> header = new HashMap<>();
            List<NameValuePair> reqParam = new ArrayList<>();
            header.put("referer", "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00520485");
            url = "http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10010&toStUrl=http://hl.189.cn/service/zzfw.do?method=fycx&id=6&fastcode=00520481&cityCode=hl";
            getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            reqParam.clear();
            url = "http://hl.189.cn/service/zzfw.do?method=khzlgl&id=19&fastcode=00560519&cityCode=hl";
            resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            pageInfo = resultPage.getWebResponse().getContentAsString();

            logger.info("==>[{}]2.1正在请求发送短信验证码开始.....", context.getTaskId());
            result.setResult(StatusCode.爬取中);
            context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
            sendUpdateLog(result, context);

            header.put("referer", "http://hl.189.cn/service/zzfw.do?method=fycx&id=6&fastcode=00520481&cityCode=hl");
            reqParam.clear();
            reqParam.add(new NameValuePair("method", "sendMsg"));
            url = "http://hl.189.cn/service/userCheck.do";
            resultPage = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            pageInfo = resultPage.getWebResponse().getContentAsString().trim();

            if ("1".equals(pageInfo)) {
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
                    reqParam.add(new NameValuePair("method", "checkDX"));
                    reqParam.add(new NameValuePair("yzm", msgCode));
                    url = "http://hl.189.cn/service/zzfw.do";
                    resultPage = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
                    pageInfo = resultPage.getWebResponse().getContentAsString();

                    if (StringUtils.contains(pageInfo, "验证码错误")) {//您输入的查询验证码错误或过期
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


            logger.info("==>2.2[{}]采集基本信息开始.....", context.getTaskId());
            url = "http://hl.189.cn/service/crm_cust_info_show.do?funcName=custSupport&canAdd2Tool=canAdd2Tool";//资料
            resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            if (null != resultPage) {//添加缓存信息
                cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), resultPage);
            } else {
                result.setResult(StatusCode.采集基本信息出错);
                return result;
            }
            logger.info("==>2.2[{}]采集基本信息结束.", context.getTaskId());

            //获取余额
            url = "http://www.189.cn/dqmh/my189/checkMy189Session.do";
            reqParam.clear();
            reqParam.add(new NameValuePair("fastcode", "00520481"));
            getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

            header.clear();
            header.put("Referer", "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00520484");
            url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10010&toStUrl=http://hl.189.cn/service/zzfw.do?method=fycx&id=6&fastcode=00520481&cityCode=hl";
            getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);

            url = "http://hl.189.cn/service/zzfw.do?method=fycx&id=6&fastcode=00520481&cityCode=hl";
            getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);

            url = "http://hl.189.cn/service/selectBallance.do?method=ballance";//余额
            header.clear();
            header.put("Referer", "http://hl.189.cn/service/zzfw.do?method=fycx&id=6&fastcode=00520481&cityCode=hl");
            resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            if (null != resultPage) {//添加缓存信息
                cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), resultPage);
            }

            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集基本信息出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集基本信息出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        Map<String, String> header = new HashMap<>();
        header.put("accept-Language", "zh-CN");
        header.put("referer", "http://hl.189.cn/service/cqd/detailQueryCondition.do");
        try {

            String url = "";
            url = "http://www.189.cn/dqmh/my189/checkMy189Session.do";
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.add(new NameValuePair("fastcode", "00520485"));
            getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);

            url = "http://hl.189.cn/service/zzfw.do?method=fycx&id=9&fastcode=00520484&cityCode=hl";
            getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);


            header.clear();
            header.put("Referer", "http://hl.189.cn/service/cqd/detailQueryCondition.do");
            url = "http://hl.189.cn/service/cqd/queryDetailList.do?isMobile=0&seledType=0&queryType=&pageSize=10&pageNo=1&flag=&pflag=&accountNum=18946207587%3A2000004&callType=3&selectType=2&detailType=0&selectedDate=&method=queryCQDMain";
            Page callDetailPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            url = "http://hl.189.cn/service/detailListExcel.do?method=exportExcel";
            header.clear();
            header.put("Referer", "http://hl.189.cn/service/cqd/queryDetailList.do?isMobile=0&seledType=0&queryType=&pageSize=10&pageNo=1&flag=&pflag=&accountNum=18946207587%3A2000004&callType=3&selectType=2&detailType=0&selectedDate=&method=queryCQDMain");

            reqParam.clear();
            reqParam.add(new NameValuePair("flagstat", "flagstat"));
            reqParam.add(new NameValuePair("selectedDate", ""));
            reqParam.add(new NameValuePair("startDate", ""));
            reqParam.add(new NameValuePair("endDate", ""));
            reqParam.add(new NameValuePair("seledType", "0"));
            reqParam.add(new NameValuePair("selectType", "2"));
            reqParam.add(new NameValuePair("callType", "3"));
            reqParam.add(new NameValuePair("pageSize", ""));
            reqParam.add(new NameValuePair("pageNo", ""));
            reqParam.add(new NameValuePair("detailType", "0"));
            reqParam.add(new NameValuePair("valueType", "4"));
            reqParam.add(new NameValuePair("accountNum", context.getUserName() + ":2000004"));
            String logFlag = String.format("==>3.[%s]采集漫游通话详单", context.getTaskId());
            Page manyou = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);

            reqParam.clear();
            reqParam.add(new NameValuePair("flagstat", "flagstat"));
            reqParam.add(new NameValuePair("selectedDate", ""));
            reqParam.add(new NameValuePair("startDate", ""));
            reqParam.add(new NameValuePair("endDate", ""));
            reqParam.add(new NameValuePair("seledType", "1"));
            reqParam.add(new NameValuePair("selectType", "2"));
            reqParam.add(new NameValuePair("callType", "3"));
            reqParam.add(new NameValuePair("pageSize", ""));
            reqParam.add(new NameValuePair("pageNo", ""));
            reqParam.add(new NameValuePair("detailType", "1"));
            reqParam.add(new NameValuePair("valueType", "4"));
            reqParam.add(new NameValuePair("accountNum", context.getUserName() + ":2000004"));
            logFlag = String.format("==>3.[%s]采集长途通话详单", context.getTaskId());
            Page changtu = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);

            reqParam.clear();
            reqParam.add(new NameValuePair("flagstat", "flagstat"));
            reqParam.add(new NameValuePair("selectedDate", ""));
            reqParam.add(new NameValuePair("startDate", ""));
            reqParam.add(new NameValuePair("endDate", ""));
            reqParam.add(new NameValuePair("seledType", "2"));
            reqParam.add(new NameValuePair("selectType", "2"));
            reqParam.add(new NameValuePair("callType", "3"));
            reqParam.add(new NameValuePair("pageSize", ""));
            reqParam.add(new NameValuePair("pageNo", ""));
            reqParam.add(new NameValuePair("detailType", "2"));
            reqParam.add(new NameValuePair("valueType", "4"));
            reqParam.add(new NameValuePair("accountNum", context.getUserName() + ":2000004"));
            logFlag = String.format("==>3.[%s]采集市话通话详单", context.getTaskId());
            Page shihua = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);

            if (null != callDetailPage) {//添加缓存信息
                logger.info("==>3.采集[{}]通话详单成功.", context.getTaskId());
                cacheContainer.putPage(ProcessorCode.CALLRECORD_INFO.getCode(), manyou);
                cacheContainer.putPage(ProcessorCode.CALLRECORD_INFO.getCode() + 1, changtu);
                cacheContainer.putPage(ProcessorCode.CALLRECORD_INFO.getCode() + 2, shihua);
            } else {
                logger.info("==>3.采集[{}]通话详单结束,没有查询结果.", context.getTaskId());
            }
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集通话详情出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集通话详情出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        String url = "";
        try {
            url = "http://hl.189.cn/service/detailListExcel.do?method=exportExcel";
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.clear();
            reqParam.add(new NameValuePair("flagstat", "flagstat"));
            reqParam.add(new NameValuePair("selectedDate", ""));
            reqParam.add(new NameValuePair("startDate", ""));
            reqParam.add(new NameValuePair("endDate", ""));
            reqParam.add(new NameValuePair("seledType", "5"));
            reqParam.add(new NameValuePair("selectType", "2"));
            reqParam.add(new NameValuePair("callType", "3"));
            reqParam.add(new NameValuePair("pageSize", ""));
            reqParam.add(new NameValuePair("pageNo", ""));
            reqParam.add(new NameValuePair("detailType", "5"));
            reqParam.add(new NameValuePair("valueType", "4"));
            reqParam.add(new NameValuePair("accountNum", context.getUserName() + ":2000004"));
            String logFlag = String.format("==>5.1[%s]采集短信记录", context.getTaskId());
            Page callDetailPage = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
            if (null != callDetailPage) {//添加缓存信息
                logger.info("==>4.[{}]采集短信记录成功.", context.getTaskId());
                cacheContainer.putPage(ProcessorCode.SMS_INFO.getCode(), callDetailPage);
            } else {
                logger.info("==>4.[{}]采集短信记录结束,没有查询结果.", context.getTaskId());
            }
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集短信记录出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集短信记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        try {
            Calendar calendar = Calendar.getInstance();
            List<Page> pages = new ArrayList<>();
            //查询最近6个月上网记录
            for (int i = 1; i < 7; i++) {
                final String yearAndMonth = DateFormatUtils.format(calendar, "yyyyMM");
                final String month = DateFormatUtils.format(calendar, "yyyy-MM");

                String logFlag = String.format("==>5.%s采集[%s]上网记录", i, month);
                String url = "http://hl.189.cn/service/cqd/queryFlowDetailList.do?isMobile=0&seledType=4&queryType=&pageSize=10&pageNo=1&flag=&pflag=&accountNum=" + context.getUserName() + "2000004&callType=3&selectType=1&detailType=4&selectedDate=" + yearAndMonth + "&method=queryCQDMain";

                Page callDetailPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if (null != callDetailPage) {//添加缓存信息
                    pages.add(callDetailPage);
                    logger.info("==>5.{}采集[{}]上网记录成功.", i, month);
                } else {
                    logger.info("==>5.{}采集[{}]上网记录结束,没有查询结果.", i, month);
                }
                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集上网记录出现异常:[{}]", context.getTaskId(), e);
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
            header.put("referer", "http://hl.189.cn/service/billDateChoiceNew.do?method=doInit");
            String url = "http://hl.189.cn/service/billDateChoiceNew.do";

            List<Page> pages = new ArrayList<>();
            //查询最近6个月账单
            for (int i = 1; i < 7; i++) {//前6个月(不算当月)
                calendar.add(Calendar.MONTH, -1);
                final String month = DateFormatUtils.format(calendar, "yyyy-MM");
                final String yearAndMonth = DateFormatUtils.format(calendar, "yyyyMM");

                reqParam.clear();
                reqParam.add(new NameValuePair("method", "doSearch"));
                reqParam.add(new NameValuePair("selectDate", yearAndMonth));
                String logFlag = String.format("==>6.%s采集[%s]账单信息,第[{}]次尝试请求.....", i, month);

                Page historyBillPage = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
                if (null != historyBillPage) {//添加缓存信息
                    pages.add(historyBillPage);
                    logger.info("==>6.{}采集[{}]账单信息成功.{}", i, month, context.getTaskId());
                } else {
                    logger.info("==>6.{}采集[{}]账单信息结束,没有查询结果.{}", i, month, context.getTaskId());
                }
            }
            cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集账单信息出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集账单记录出错);
            return result;
        }
        return result;
    }

    @Override
    public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        Result result = new Result();
        String url = "http://hl.189.cn/service/setmeal_sit_query.do?method=query";
        try {
            logger.info("==>7.[{}]采集已办理业务信息开始.....", context.getTaskId());
            Page businessPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            if (null != businessPage) {//添加缓存信息
                cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), businessPage);
                logger.info("==>7.[{}]采集已办理业务信息成功.", context.getTaskId());
            } else {
                logger.info("==>7.[{}]采集已办理业务信息结束,没有查询结果.", context.getTaskId());
            }
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>采集已办理业务信息出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.采集办理业务信息出错);
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
            logger.info("==>7.[{}]采集充值信息开始.....", context.getTaskId());
            Calendar calendar = Calendar.getInstance();
            final String endDate = DateFormatUtils.format(calendar, "yyyyMM");
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 5);
            final String startDate = DateFormatUtils.format(calendar, "yyyyMM");
            List<NameValuePair> reqParam = new ArrayList<>();
            String url = "http://hl.189.cn/service/payQuery.do?opFlag=query";
            reqParam.add(new NameValuePair("startDate", startDate));
            reqParam.add(new NameValuePair("endDate", endDate));
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
        getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

        result.setSuccess();
        return result;
    }


    @Override
    protected Logger getLogger() {
        return logger;
    }

}
