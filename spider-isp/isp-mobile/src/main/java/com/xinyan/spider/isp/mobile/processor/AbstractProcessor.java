package com.xinyan.spider.isp.mobile.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.base.http.*;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.mobile.service.CaptchaService;
import com.xinyan.spider.isp.common.utils.Base64;
import com.xinyan.spider.isp.common.utils.*;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;

/**
 * 处理类
 *
 * @author heliang
 * @version v1.0
 * @description
 * @date 2016年8月8日 下午3:48:57
 */
public class AbstractProcessor implements PageProcessor {


    //是否允许代理
    protected static boolean enableProxy = false;
    //代理配置
    private static ProxyConfig proxyConfig = new ProxyConfig();

    @Value("${spiderManager}")
    private String spiderManager;
    @Value("${spiderRoute}")
    private String spiderRoute;
    @Autowired
    private CaptchaService captchaService;

    /**
     * 登陆
     *
     * @param context
     * @return
     */
    public Result login(WebClient webClient, Context context) {
        webClient = initWebClient(webClient);
        //创建Client客户端
        Result result = new Result();
        getLogger().info("==>[{}]登陆开始......", context.getTaskId());
        try {
            //具体的登陆逻辑
            if (Constants.VALIDATE_TYPE_CODE_SMS.equals(context.getTaskSubStatus())) {
                if (StringUtils.isEmpty(context.getUserInput())) {
                    return new Result(StatusCode.请输入短信验证码);
                }
                result = doLoginBySMS(webClient, context);
            } else if (Constants.VALIDATE_TYPE_CODE_IMG.equals(context.getTaskSubStatus())) {
                if (StringUtils.isEmpty(context.getUserInput())) {
                    return new Result(StatusCode.请输入图片验证码);
                }
                result = doLoginByIMG(webClient, context);
            } else {
                webClient.getCookieManager().clearCookies();
                result = doLogin(webClient, context);
            }
        } catch (Exception ex) {
            if (ex instanceof SocketTimeoutException || ex instanceof HttpHostConnectException) {
                getLogger().error("==>[{}]程序访问超时", context.getTaskId(), ex);
                result.setErrorCode(StatusCode.网络超时.getCode());
            } else {
                getLogger().error("==>[{}]登陆出现异常", context.getTaskId(), ex);
                result.setErrorCode(StatusCode.操作失败.getCode());
            }
        }finally {
            // 设置回调
            if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
                    && !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
                //不成功退出
                if(!result.isSuccess()){
                    loginout(webClient);
                }
            }
        }
        getLogger().info("==>[{}]登陆结束.", context.getTaskId());
        return result;
    }

    /**
     * 抓取和解析
     *
     * @context
     */
    public Result crawler(WebClient webClient, Context context) {
        webClient = initWebClient(webClient);
        //创建Client客户端
        Result result = new Result();
        getLogger().info("==>[{}]数据抓取和解析开始......", context.getTaskId());
        try {
            //具体的登陆逻辑
            result = doCrawler(webClient, context);
        } catch (Exception ex) {
            getLogger().error("==>[{}]数据抓取出现异常.", context.getTaskId(), ex);
            result.setErrorCode(StatusCode.操作失败.getCode());
        } finally {
            close(webClient);
        }
        getLogger().info("==>[{}]数据抓取和解析结束.", context.getTaskId());
        return result;
    }


    /**
     * 具体的登陆逻辑
     *
     * @param webClient
     * @param context
     * @return
     */
    protected Result doLoginByIMG(WebClient webClient, Context context) throws Exception {
        throw new RuntimeException("==>请重写该方法[具体的登陆逻辑]");
    }

    /**
     * 具体的登陆逻辑
     *
     * @param webClient
     * @param context
     * @return
     */
    protected Result doLoginBySMS(WebClient webClient, Context context) throws Exception {
        throw new RuntimeException("==>请重写该方法[具体的登陆逻辑]");
    }

    /**
     * 具体的登陆逻辑
     *
     * @param webClient
     * @param context
     * @return
     */
    protected Result doLogin(WebClient webClient, Context context) throws Exception {
        throw new RuntimeException("==>请重写该方法[具体的登陆逻辑]");
    }

    /**
     * 具体的数据抓取逻辑
     *
     * @param webClient
     * @param context
     * @return
     */
    protected Result doCrawler(WebClient webClient, Context context) throws Exception {
        throw new RuntimeException("==>请重写该方法[具体的数据抓取逻辑]");
    }

    /**
     * 回调登陆状态
     *
     * @param result     结果
     * @param context    上下文
     * @param cookieFlag true为传递Cookie false为传递WebClient
     * @param webClient  web客户端
     * @return
     */
    protected boolean sendLoginMsg(Result result, Context context, boolean cookieFlag, WebClient webClient) {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> subBody = new HashMap<>();
        boolean loginStatus = false;
        JSONObject jsonObject = null;
        int retryTimes = 0;
        boolean responseFlag = false;
        if (StatusCode.SUCCESS.getCode().equals(result.getCode())) {
            loginStatus = true;
        }
        try {
            subBody.put("userName", context.getUserName());
            subBody.put("userPwd", context.getPassword());
            subBody.put("idCard", context.getIdCard());//身份证号码
            subBody.put("idName", context.getIdName());//身份证姓名

            if (cookieFlag) {
                subBody.put("cookies", webClient.getCookieManager().getCookies());
            } else {
                subBody.put("webClient", ObjSerializableUtils.writeToStr(webClient));
            }
            subBody.put("mobileHCodeDto", context.getMobileHCodeDto());
            subBody.put("param1", context.getParam1());
            subBody.put("param2", context.getParam2());
            subBody.put("param3", context.getParam3());
            subBody.put("param4", context.getParam4());
            subBody.put("param5", context.getParam5());
            subBody.put("param6", context.getParam6());

            body.put("code", result.getCode());
            body.put("msg", result.getMsg())  ;
            body.put("success", loginStatus);
            body.put("taskId", context.getTaskId());
            body.put("taskType", context.getTaskType());
            body.put("context", subBody);
            body.put("wait", false);

            do {

                jsonObject = HttpHelpUtil.execute(HttpMethod.POST, spiderManager + "/work/login", HttpContentType.JSON, null, body);
                if (null != jsonObject && jsonObject.getBoolean("success")) {
                    getLogger().info("==>[{}]第[{}]次回调登陆状态成功", context.getTaskId(), (retryTimes + 1));
                    responseFlag = true;
                } else {
                    getLogger().info("==>[{}]第[{}]次回调登陆状态失败", context.getTaskId(), (retryTimes + 1));
                }
            } while (!responseFlag && retryTimes++ < Constants.MAX_SENDCRAWLERMSG_TIIMES);//需要再次尝试
        } catch (Exception ex) {
            getLogger().info("==>[{}]第[{}]次回调登陆状态异常", context.getTaskId(), (retryTimes + 1), ex);
        }
        return responseFlag;
    }

    /**
     * 发送验证码请求
     * @param result     结果
     * @param context    上下文
     * @param cookieFlag true为传递Cookie false为传递WebClient
     * @param webClient  web客户端
     * @param waitTime   等待时间 单位秒
     * @return
     */
    protected boolean sendValidataMsg(Result result, Context context, boolean cookieFlag, WebClient webClient, int waitTime, String imgCode) {
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> subBody = new HashMap<>();
        int retryTimes = 0;
        boolean responseFlag = false;
        try {
            subBody.put("userName", context.getUserName());
            subBody.put("userPwd", context.getPassword());
            subBody.put("idCard", context.getIdCard());//身份证号码
            subBody.put("idName", context.getIdName());//身份证姓名

            if (cookieFlag) {
                subBody.put("cookies", webClient.getCookieManager().getCookies());
            } else {
                subBody.put("webClient", ObjSerializableUtils.writeToStr(webClient));
            }
            subBody.put("mobileHCodeDto", context.getMobileHCodeDto());
            subBody.put("param1", context.getParam1());
            subBody.put("param2", context.getParam2());
            subBody.put("param3", context.getParam3());
            subBody.put("param4", context.getParam4());
            subBody.put("param5", context.getParam5());
            subBody.put("param6", context.getParam6());

            body.put("code", result.getCode());
            body.put("msg", result.getMsg());
            body.put("success", true);
            body.put("taskId", context.getTaskId());
            body.put("taskType", context.getTaskType());
            body.put("taskSubStatus", context.getTaskSubStatus());
            body.put("taskSubType", context.getTaskSubType());
            body.put("context", subBody);
            body.put("wait", true);//需要等待
            body.put("waitTime", waitTime);//等待时间
            body.put("codeType", context.getTaskSubStatus());//验证码类型
            body.put("imgCode", imgCode);
            JSONObject jsonObject = null;
            do {
                jsonObject = HttpHelpUtil.execute(HttpMethod.POST, spiderManager + "/work/login", HttpContentType.JSON, null, body);
                if (null != jsonObject && jsonObject.getBoolean("success")) {
                    getLogger().info("==>[{}]第[{}]次发送" + context.getTaskSubStatus() + "验证码成功", context.getTaskId(), (retryTimes + 1));
                    responseFlag = true;
                } else {
                    getLogger().info("==>[{}]第[{}]次发送" + context.getTaskSubStatus() + "验证码失败", context.getTaskId(), (retryTimes + 1));
                }
            } while (!responseFlag && retryTimes++ < Constants.MAX_SENDCRAWLERMSG_TIIMES);//需要再次尝试
        } catch (Exception ex) {
            getLogger().info("==>[{}]第[{}]次回调登陆状态异常", context.getTaskId(), (retryTimes + 1), ex);
        }
        return responseFlag;
    }

    /**
     * 同步请求验证码接口
     * @param context
     * @param waitTime
     * @param codeType
     * @param imgCode
     * @return
     */
    protected boolean sendValidataMsgByCrawling(Context context, int waitTime, String codeType, String imgCode) {
        Map<String, Object> body = new HashMap<>();
        int retryTimes = 0;
        boolean responseFlag = false;
        try {
            body.put("taskId", context.getTaskId());
            body.put("waitTime", waitTime);
            body.put("imgCode", imgCode);
            body.put("msg", "爬取中等待" + codeType + "验证码");
            body.put("codeType", codeType);
            JSONObject jsonObject = null;
            do {
                jsonObject = HttpHelpUtil.execute(HttpMethod.POST, spiderManager + "/work/verificationCode", HttpContentType.JSON, null, body);
                if (null != jsonObject && jsonObject.getBoolean("success")) {
                    getLogger().info("==>[{}]第[{}]次发送" + codeType + "验证码成功", context.getTaskId(), (retryTimes + 1));
                    responseFlag = true;
                } else {
                    getLogger().info("==>[{}]第[{}]次发送" + codeType + "验证码失败", context.getTaskId(), (retryTimes + 1));
                }
            } while (!responseFlag && retryTimes++ < Constants.MAX_SENDCRAWLERMSG_TIIMES);//需要再次尝试
        } catch (Exception ex) {
            getLogger().info("==>[{}]第[{}]次回调登陆状态异常", context.getTaskId(), (retryTimes + 1), ex);
        }
        return responseFlag;
    }

    /**
     * 回调解析结果
     * @param result
     * @param context
     * @return
     */
    protected boolean sendAnalysisMsg(Result result, Context context) {
        Map<String, Object> body = new HashMap<>();
        boolean crawlerStatus = false;
        if (StatusCode.SUCCESS.getCode().equals(result.getCode())) {
            crawlerStatus = true;
        }
        body.put("code", result.getCode());
        body.put("msg", result.getMsg());
        body.put("success", crawlerStatus);
        body.put("taskId", context.getTaskId());
        body.put("taskType", context.getTaskType());
        body.put("mappingId", context.getMappingId());
        body.put("data", result.getData());

        JSONObject jsonObject = null;
        int retryTimes = 0;
        boolean responseFlag = false;
        try {
            do {
                jsonObject = HttpHelpUtil.execute(HttpMethod.POST, spiderManager + "/work/analysis", HttpContentType.JSON, null, body);
                if (null != jsonObject && jsonObject.getBoolean("success")) {
                    getLogger().info("==>[{}]第[{}]次回调解析结果成功", context.getTaskId(), (retryTimes + 1));
                    responseFlag = true;
                } else {
                    getLogger().info("==>[{}]第[{}]次回调解析结果失败", context.getTaskId(), (retryTimes + 1));
                }
            } while (!responseFlag && retryTimes++ < Constants.MAX_SENDCRAWLERMSG_TIIMES);//需要再次尝试
        } catch (Exception ex) {
            getLogger().info("==>[{}]第[{}]次回调解析结果异常", context.getTaskId(), (retryTimes + 1), ex);
            try {
                Thread.sleep(120000);
            } catch (Exception e) {
            }
        }
        return responseFlag;
    }

    /**
     * 下载验证码图片并识别
     * 自动打码
     * @param webClient
     * @param url
     * @param codeType
     * @return
     */
    protected String getVerifyCode(WebClient webClient,String url, int codeType){
        String verifyCode="";
        try{
            Page page = webClient.getPage(url);
            verifyCode = captchaService.recognition(ImageIO.read(page.getWebResponse().getContentAsStream()),codeType);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return verifyCode;
    }

    /**
     * 下载验证码图片并识别
     * 自动打码
     * @param webClient
     * @param surl
     * @param codeType
     * @param header
     * @return
     */
    protected String getVerifyCode(WebClient webClient,String surl,int codeType,  HashMap<String,String> header){
        String verifyCode="";
        try{
            URL url = new URL(surl);
            WebRequest webRequest = new WebRequest(url, HttpMethod.GET);
            if(header != null){
                Iterator iter = header.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    webRequest.setAdditionalHeader(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            Page page =  webClient.getPage(webRequest);
            verifyCode = captchaService.recognition(ImageIO.read(page.getWebResponse().getContentAsStream()),codeType);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return verifyCode;
    }

    /**
     * 下载验证码图片（流）并识别
     * 自动打码
     * @param webClient
     * @param surl
     * @param codeType
     * @param header
     * @return
     */
    protected String getVerifyCodeAsStream(WebClient webClient,String surl,int codeType, HashMap<String,String> header){
        String verifyCode="";
        try{
            URL url = new URL(surl);
            WebRequest webRequest = new WebRequest(url, HttpMethod.GET);
            if(header != null){
                Iterator iter = header.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    webRequest.setAdditionalHeader(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            Page page =  webClient.getPage(webRequest);
            verifyCode = captchaService.recognition(page.getWebResponse().getContentAsStream(),codeType);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return verifyCode;
    }

    /**
     * 下载验证码图片并识别
     * 返回Based64图片
     * @param webClient
     * @param url
     * @return
     */
    protected String getVerifyCode(WebClient webClient, String url, Map<String, String> header) {
        try {
            URL surl = new URL(url);
            WebRequest webRequest = new WebRequest(surl, HttpMethod.GET);
            if ((header != null)) {
                Iterator<Map.Entry<String, String>> iter = header.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    webRequest.setAdditionalHeader(entry.getKey(), entry.getValue());
                }
            }
            Page page = webClient.getPage(webRequest);
            if (null != page) {
                return Base64.getBase64ByImgInputStream(page.getWebResponse().getContentAsStream());
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 更新状态日志
     *
     * @param result
     * @param context
     * @return
     */
    protected boolean sendUpdateLog(Result result, Context context) {
        Map<String, Object> body = new HashMap<>();
        boolean crawlerStatus = false;
        if (StatusCode.SUCCESS.getCode().equals(result.getCode())) {
            body.put("taskStatus", TaskStatus.CRAWLING_SUCCESS);
            crawlerStatus = true;
        } else if (StatusCode.爬取中.getCode().equals(result.getCode())) {
            body.put("taskStatus", TaskStatus.CRAWLING);
        } else {
            body.put("taskStatus", TaskStatus.CRAWLING_FAILED);
        }
        body.put("code", result.getCode());
        body.put("msg", result.getMsg());
        body.put("success", crawlerStatus);
        body.put("taskId", context.getTaskId());
        body.put("taskType", context.getTaskType());

        JSONObject jsonObject = null;
        int retryTimes = 0;
        boolean responseFlag = false;
        try {
            do {
                jsonObject = HttpHelpUtil.execute(HttpMethod.POST, spiderManager + "/work/updateLog", HttpContentType.JSON, null, body);
                if (null != jsonObject && jsonObject.getBoolean("success")) {
                    getLogger().info("==>[{}]第[{}]次更新状态日志成功", context.getTaskId(), (retryTimes + 1));
                    responseFlag = true;
                } else {
                    getLogger().info("==>[{}]第[{}]次更新状态日志失败", context.getTaskId(), (retryTimes + 1));
                }
            } while (!responseFlag && retryTimes++ < Constants.MAX_SENDCRAWLERMSG_TIIMES);//需要再次尝试
        } catch (Exception ex) {
            getLogger().info("==>[{}]第[{}]次更新状态日志异常", context.getTaskId(), (retryTimes + 1), ex);
        }
        return responseFlag;
    }

    /**
     * 爬取基础信息
     *
     * @param webClient
     * @param context
     * @return
     * @description
     * @author heliang
     * @create 2016年8月12日 下午3:39:26
     */
    protected Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        throw new RuntimeException("该方法[爬取基础信息]必须重写");
    }

    /**
     * 爬取通话详情
     *
     * @param webClient
     * @param context
     * @return
     * @description
     * @author heliang
     * @create 2016年8月12日 下午3:39:26
     */
    protected Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        throw new RuntimeException("该方法[爬取通话详情]必须重写");
    }

    /**
     * 爬取短信记录
     *
     * @param webClient
     * @param context
     * @param carrierInfo
     * @return
     * @description
     * @author heliang
     * @create 2016年8月15日 下午2:32:59
     */
    protected Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        throw new RuntimeException("该方法[爬取短信记录]必须重写");
    }

    /**
     * 爬取上网记录
     *
     * @param webClient
     * @param context
     * @param carrierInfo
     * @return
     * @description
     * @author heliang
     * @create 2016年8月15日 下午2:32:59
     */
    protected Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        throw new RuntimeException("该方法[爬取上网记录]必须重写");
    }

    /**
     * 爬取账单信息
     *
     * @param webClient
     * @param context
     * @param carrierInfo
     * @return
     * @description
     * @author heliang
     * @create 2016年8月15日 下午2:32:59
     */
    protected Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        throw new RuntimeException("该方法[爬取账单信息]必须重写");
    }


    /**
     * 爬取套餐信息
     *
     * @param webClient
     * @param context
     * @param carrierInfo
     * @return
     * @description
     * @author heliang
     * @create 2016年8月15日 下午2:32:59
     */
    protected Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        throw new RuntimeException("该方法[爬取套餐信息]必须重写");
    }

    /**
     * 爬取亲情号码
     *
     * @param webClient
     * @param context
     * @param carrierInfo
     * @return
     * @description
     * @author heliang
     * @create 2016年8月15日 下午2:32:59
     */
    protected Result processUserFamilyMember(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        throw new RuntimeException("该方法[爬取亲情号码]必须重写");
    }

    /**
     * 充值记录详情明细
     *
     * @param webClient
     * @param context
     * @param carrierInfo
     * @return
     * @description
     * @author heliang
     * @create 2016年8月15日 下午2:32:59
     */
    protected Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
        throw new RuntimeException("该方法[充值记录详情明细]必须重写");
    }

    /**
     * 退出登陆
     *
     * @param webClient
     * @return
     * @description
     * @author heliang
     * @create 2016-09-02 10:49
     */
    protected Result loginout(WebClient webClient) {
        throw new RuntimeException("该方法[退出登陆]必须重写");
    }

    /**
     * 获取日志
     *
     * @return
     * @description
     * @author heliang
     * @create 2016年8月8日 下午5:35:32
     */
    protected Logger getLogger() {
        throw new RuntimeException("该方法[获取日志]必须重写");
    }

    /**
     * 获取页面
     *
     * @param webClient  访问客户端
     * @param url        访问地址
     * @param httpMethod 提交方法
     * @param params     访问参数
     * @param header     访问Header
     * @return
     * @description
     * @author heliang
     * @create 2016年8月13日 上午9:48:59
     */
    public Page getPage(WebClient webClient, String url, HttpMethod httpMethod, List<NameValuePair> params, Map<String, String> header) {
        return getPage(webClient, url, httpMethod, params, CharsetCode.UTF8, header);
    }

    /**
     * 获取页面
     *
     * @param webClient  访问客户端
     * @param url        访问地址
     * @param httpMethod 提交方法
     * @param params     访问参数
     * @param header     访问Header
     * @param bodyStr    Body参数
     * @return
     * @description
     * @author heliang
     * @create 2016年8月13日 上午9:48:59
     */
    public Page getPage(WebClient webClient, String url, HttpMethod httpMethod, List<NameValuePair> params, Map<String, String> header, String bodyStr) {
        return getPage(webClient, url, httpMethod, params, CharsetCode.UTF8, header, bodyStr);
    }

    /**
     * 获取页面
     * <b>有重复尝试机制</b>
     *
     * @param webClient  访问客户端
     * @param url        访问地址
     * @param httpMethod 提交方法
     * @param params     访问参数
     * @param retryTimes 重复尝试次数
     * @param logFlag    日志标志
     * @param header     访问Header
     * @return
     * @description
     * @author heliang
     * @create 2016年8月13日 上午9:48:59
     */
    public Page getPage(WebClient webClient, String url, HttpMethod httpMethod, List<NameValuePair> params,
                        int retryTimes, String logFlag, Map<String, String> header) {
        Page page;
        int rt = 1;
        while (rt <= retryTimes) {
            try {
                getLogger().info(logFlag, rt);
                page = getPage(webClient, url, httpMethod, params, CharsetCode.UTF8, header);
                if (null != page) {
                    return page;
                } else {
                    getLogger().error("==>响应内容为空,正在再次尝试请求");
                    rt++;
                }
            } catch (Exception e) {
                getLogger().error("==>获取响应出错了,正在再次尝试请求:", e);
                rt++;
                try {
                    Thread.sleep(5000);
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    /**
     * 获取页面
     * <b>有重复尝试机制</b>
     *
     * @param webClient   访问客户端
     * @param url         访问地址
     * @param httpMethod  提交方法
     * @param params      访问参数
     * @param retryTimes  重复尝试次数
     * @param logFlag     日志标志
     * @param header      访问Header
     * @param charsetCode 请求编码
     * @return
     * @description
     * @author york
     * @create 2016年9月23日 上午11:48:59
     */
    public Page getPage(WebClient webClient, String url, HttpMethod httpMethod, List<NameValuePair> params,
                        int retryTimes, String logFlag, Map<String, String> header, CharsetCode charsetCode) {
        Page page;
        int rt = 1;
        while (rt <= retryTimes) {
            try {
                getLogger().info(logFlag, rt);
                page = getPage(webClient, url, httpMethod, params, charsetCode, header);
                if (null != page) {
                    return page;
                } else {
                    getLogger().error("==>响应内容为空,正在再次尝试请求");
                    rt++;
                }
            } catch (Exception e) {
                getLogger().error("==>获取响应出错了,正在再次尝试请求:", e);
                rt++;
                try {
                    Thread.sleep(5000);
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    /**
     * 获取页面
     * <b>有重复尝试机制</b>
     *
     * @param webClient  访问客户端
     * @param url        访问地址
     * @param httpMethod 提交方法
     * @param params     访问参数
     * @param retryTimes 重复尝试次数
     * @param logFlag    日志标志
     * @param header     访问Header
     * @param bodyStr    Body参数
     * @return
     * @description
     * @author heliang
     * @create 2016年8月13日 上午9:48:59
     */
    public Page getPage(WebClient webClient, String url, HttpMethod httpMethod, List<NameValuePair> params,
                        int retryTimes, String logFlag, Map<String, String> header, String bodyStr) {
        Page page;
        int rt = 1;
        while (rt <= retryTimes) {
            try {
                getLogger().info(logFlag, rt);
                page = getPage(webClient, url, httpMethod, params, CharsetCode.UTF8, header, bodyStr);
                if (null != page) {
                    return page;
                } else {
                    getLogger().error("==>响应内容为空,正在再次尝试请求");
                    rt++;
                }
            } catch (Exception e) {
                getLogger().error("==>获取响应出错了,正在再次尝试请求:", e);
                rt++;
            }
        }
        return null;
    }

    /**
     * 获取页面
     *
     * @param webClient  访问客户端
     * @param url        访问地址
     * @param httpMethod 提交方法
     * @param params     访问参数
     * @param header     访问Header
     * @return
     * @description
     * @author heliang
     * @create 2016年8月13日 上午9:48:59
     */
    public Page getPage(WebClient webClient, String url, HttpMethod httpMethod, List<NameValuePair> params, Map<String, String> header, CharsetCode charsetCode) {
        return getPage(webClient, url, httpMethod, params, charsetCode, header);
    }

    //--------------------------------------------------------
    //以下方法无须关注
    //--------------------------------------------------------

    private Page getPage(WebClient webClient, String url, HttpMethod httpMethod, List<NameValuePair> params,
                         CharsetCode charset, Map<String, String> header) {
        if (httpMethod == HttpMethod.GET) {
            return doGet(webClient, url, params, charset, header, "");
        } else {
            return doPost(webClient, url, params, charset, header, "");
        }
    }

    private Page getPage(WebClient webClient, String url, HttpMethod httpMethod, List<NameValuePair> params,
                         CharsetCode charset, Map<String, String> header, String bodyStr) {
        if (httpMethod == HttpMethod.GET) {
            return doGet(webClient, url, params, charset, header, bodyStr);
        } else {
            return doPost(webClient, url, params, charset, header, bodyStr);
        }
    }

    private Page doPost(WebClient webClient, String pageUrl, List<NameValuePair> reqParam,
                        CharsetCode charset, Map<String, String> header, String bodyStr) {
        try {
            URL url = new URL(pageUrl);
            WebRequest webRequest = new WebRequest(url, HttpMethod.POST);
            webRequest.setAdditionalHeader("Accept-Language", "zh-CN");
            if (charset == null) {
                charset = CharsetCode.UTF8;
            }
            webRequest.setCharset(charset.getCode());
            if (reqParam != null) {
                webRequest.setRequestParameters(reqParam);
            }
            if (null != header) {
                for (String key : header.keySet()) {
                    webRequest.setAdditionalHeader(key, header.get(key));
                }
            }
            if (StringUtils.isNotBlank(bodyStr)) {
                webRequest.setRequestBody(bodyStr);
            }
            return webClient.getPage(webRequest);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Page doGet(WebClient webClient, String pageUrl, List<NameValuePair> reqParam,
                       CharsetCode charset, Map<String, String> header, String bodyStr) {
        try {
            URL url;
            if (CollectionUtil.isEmpty(reqParam)) {
                url = new URL(pageUrl);
            } else {
                url = new URL(pageUrl + "?" + EntityUtils.toString(reqParam));
            }

            WebRequest webRequest = new WebRequest(url, HttpMethod.GET);
            if (null != header) {
                for (String key : header.keySet()) {
                    webRequest.setAdditionalHeader(key, header.get(key));
                }
            }
            if (charset == null) {
                charset = CharsetCode.UTF8;
            }
            webRequest.setCharset(charset.getCode());
            if (StringUtils.isNotBlank(bodyStr)) {
                webRequest.setRequestBody(bodyStr);
            }
            return webClient.getPage(webRequest);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 创建默认的webclient
     * <b>如果需要特殊处理,需要重写些方法</b>
     *
     * @return
     */
    private WebClient initWebClient(WebClient webClient) {
        if (null == webClient) {
            webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
        }
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setCssEnabled(false);// 禁用css支持
        webClient.getOptions().setThrowExceptionOnScriptError(false);// js运行错误时，是否抛出异常
        webClient.getOptions().setJavaScriptEnabled(false); //禁用JS
        webClient.getOptions().setTimeout(120000);

        if (enableProxy) {
            if (StringUtils.isNotBlank(proxyConfig.getProxyHost())) {
                webClient.getOptions().setProxyConfig(proxyConfig);
            }
        }
        return webClient;
    }

    protected void close(WebClient webClient) {
        if (webClient != null) {
            webClient.close();
            webClient = null;
        }
    }

    protected String rotation(Context context, WebClient webClient, int sec) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        map.put("task_id", context.getTaskId());
        params.put("data_content", JSON.toJSONString(map));
        JSONObject object;
        long time = new Date().getTime();
        try {
            do {
                object = HttpHelpUtil.execute(HttpMethod.POST, spiderRoute + "/task/v1/verify/code", HttpContentType.JSON, null, params);
                if (object == null || object.getString("verify_code") == null) {
                    if (new Date().getTime() - time > sec * 1000) {
                        getLogger().info("==>接收短信验证码超时.[{}]", context.getTaskId());
                        Result result = new Result();
                        result.setResult(StatusCode.接收短信验证码超时);
                        sendUpdateLog(result, context);
                        loginout(webClient);
                        return null;
                    }
                    Thread.sleep(2000L);
                }
            } while (object == null || object.getString("verify_code") == null);
            return object.getString("verify_code").replace("\"", "");
        } catch (Exception e) {
            getLogger().error("==>[{}]获取验证码失败", context.getTaskId(), e);
            Result result = new Result();
            result.setResult(StatusCode.接收短信验证码超时);
            sendUpdateLog(result, context);
            loginout(webClient);
            return null;
        }
    }
}
