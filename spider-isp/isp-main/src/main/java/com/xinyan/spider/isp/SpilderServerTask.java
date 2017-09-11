package com.xinyan.spider.isp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.xinyan.spider.isp.base.*;
import com.xinyan.spider.isp.base.http.TaskStatus;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.thread.NamedThreadFactory;
import com.xinyan.spider.isp.common.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 接受请求线程
 */
@Service
public class SpilderServerTask implements Runnable, ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(SpilderServerTask.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(100, new NamedThreadFactory());

    private ApplicationContext applicationContext;

    @Value("${spiderRoute}")
    private String spiderRoute;

    @Value("${taskType}")
    private String taskType;

    @Autowired
    private Map<String, String> processorMap;

    //运行标志
    protected volatile boolean flag = true;


    @Override
    public void run() {
        logger.info(">开始接收[{}]任务...", Constants.SPIDER_NAME);
        String taskId = "";
        String taskStatus = "";
        String userInput = "";
        String taskSubStatus = "";
        String taskSubType = "";
        WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
        while (flag) {
            try {
                //从爬虫路由获取任务
                Map<String, Object> map = new HashMap<>();
                map.put("task_type", taskType);
                logger.info(">从爬虫路由获取[{}]任务...", taskType);
                Map<String, Object> params = new HashMap<>();
                params.put("data_content", JSON.toJSONString(map));
                JSONObject object = HttpHelpUtil.execute(HttpMethod.POST, spiderRoute + "/task/v1/fetch", HttpContentType.JSON, null, params);
                if (null != object) {
                    String taskt = object.getString("taskType");
                    if (StringUtils.isNotEmpty(taskt)) {
                        taskId = object.getString("taskId");
                        taskStatus = object.getString("taskStatus");
                        taskSubType = object.getString("taskSubType");
                        taskSubStatus = object.getString("taskSubStatus");
                        userInput = object.getString("userInput");
                        JSONObject subBody = object.getJSONObject("context");
                        Context context = getContext(taskId, taskStatus, taskSubStatus, taskt, userInput, taskSubType, subBody);
                        if (null != subBody.getJSONArray("cookies")) {
                            JSONArray jSONArrayContext = subBody.getJSONArray("cookies");
                            if (jSONArrayContext != null && jSONArrayContext.size() > 0) {
                                for (int i = 0; i < jSONArrayContext.size(); i++) {
                                    CookieInfo cookieInfo = JsonUtils.json2Bean(((JSONObject) jSONArrayContext.get(i)).toJSONString(), CookieInfo.class);
                                    Cookie cookie = new Cookie(cookieInfo.getDomain(), cookieInfo.getName(), cookieInfo.getValue(), cookieInfo.getPath(), TypeUtils.castToDate(cookieInfo.getExpires()), Boolean.parseBoolean(cookieInfo.getSecure()), Boolean.parseBoolean(cookieInfo.getHttpOnly()));
                                    webClient.getCookieManager().addCookie(cookie);
                                }
                            }
                        } else if (null != subBody.getString("webClient")) {
                            webClient = ObjSerializableUtils.deserializeFromStr(subBody.getString("webClient"), WebClient.class);
                        }
                        //根据任务状态执行相应方法
                        if (TaskStatus.START.getCode().equals(taskStatus)) {//开始状态
                            executeLoginWork(webClient, context);//执行登陆任务
                        } else if (TaskStatus.LOGIN_SUCCESS.getCode().equals(taskStatus)) {//登陆成功
                            executeCrawlerWork(webClient, context);
                        } else if (TaskStatus.LOGIN_WAIT.getCode().equals(taskStatus)) {//登陆成功
                            executeLoginWork(webClient, context);
                        } else {
                            logger.info(">任务状态不能为空");
                        }
                    } else {
                        logger.info(">暂未获取到[{}]任务.", taskType);
                    }
                } else {
                    logger.error(">获取[{}]任务出错.", taskType);
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                logger.error(">请求[{}]任务出错.", Constants.SPIDER_NAME, ex);
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                logger.info("======================================");
            }
        }
        logger.info("获取[{}]任务已停止.", taskType);
    }

    public void stop() {
        this.flag = false;
        executorService.shutdown();
    }

    /**
     * 执行登陆任务
     *
     * @param context
     */
    private void executeLoginWork(final WebClient webClient, final Context context) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //日志跟踪
                MDC.put(Constants.SESSION_ID, SessionUtil.getSessionId());
                logger.info(">[{}]开始执行登陆任务...", context.getTaskId());
                try {
                    //1.参数校验
                    StatusCode statusCode = validate(context);
                    if (!StatusCode.SUCCESS.getCode().equals(statusCode.getCode())) {
                        logger.info(">[{}]参数输入有误:", context.getTaskId());
                        return;
                    }
                    //2.获取执行processor
                    final String ispCode = context.getMobileHCodeDto().getCarrierName();
                    final String provTelCode = context.getMobileHCodeDto().getProvTelCode();
                    if (StringUtils.isEmpty(ispCode)) {
                        logger.error(">[{}]获取[{}]运营商和地区信息失败:", context.getTaskId(), context.getUserName());
                        return;
                    }
                    PageProcessor processor = (PageProcessor) applicationContext.getBean(processorMap.get(ispCode.equals("lt") ? ispCode : ispCode + "_" + provTelCode));
                    if (null == processor) {
                        logger.error(">[{}]暂无[{}]处理器:", context.getTaskId(), ispCode);
                        return;
                    }
                    //3.执行爬取任务
                    Result result = processor.login(webClient, context);

                    logger.info(">[{}]登陆结束[{}]", context.getTaskId(), result);
                } catch (Throwable ex) {
                    logger.info(">[{}]登陆出现异常", context.getTaskId(), ex);
                } finally {
                    MDC.remove(Constants.SESSION_ID);
                }
            }
        });
    }

    /**
     * 执行抓取和解析任务
     *
     * @param context
     */
    private void executeCrawlerWork(final WebClient webClient, final Context context) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //日志跟踪
                MDC.put(Constants.SESSION_ID, SessionUtil.getSessionId());
                logger.info(">[{}]执行抓取和解析任务...", context.getTaskId());
                try {
                    //2.获取执行processor
                    final String ispCode = context.getMobileHCodeDto().getCarrierName();
                    final String provTelCode = context.getMobileHCodeDto().getProvTelCode();
                    if (StringUtils.isEmpty(ispCode)) {
                        logger.error(">[{}]获取[{}]运营商和地区信息失败:", context.getTaskId(), context.getUserName());
                        return;
                    }
                    PageProcessor processor = (PageProcessor) applicationContext.getBean(processorMap.get(ispCode.equals("lt") ? ispCode : ispCode + "_" + provTelCode));

                    if (null == processor) {
                        logger.error(">[{}]暂无[{}]处理器:", context.getTaskId(), ispCode);
                        return;
                    }
                    //3.执行爬取任务
                    Result result = processor.crawler(webClient, context);
                    logger.info(">[{}]抓取和解析结果", context.getTaskId(), result);
                } catch (Throwable ex) {
                    logger.info(">[{}]抓取和解析出现异常", context.getTaskId(), ex);
                } finally {
                    MDC.remove(Constants.SESSION_ID);
                }
            }
        });
    }

    /**
     * 参数检验
     *
     * @param context
     * @return
     */
    private StatusCode validate(Context context) throws Exception {
        StatusCode statusCode = StatusCode.SUCCESS;
        //用户名校验
        if (StringUtils.isBlank(context.getUserName())) {
            statusCode = StatusCode.用户名密码不能为空;
        }
        //用户名校验
        if (StringUtils.isBlank(context.getPassword())) {
            statusCode = StatusCode.用户名密码不能为空;
        }
        return statusCode;
    }

    /**
     * 获取Context
     *
     * @param taskId
     * @param taskStatus
     * @param taskSubStatus
     * @param taskType
     * @return
     */
    private Context getContext(String taskId, String taskStatus, String taskSubStatus, String taskType, String userInput, String taskSubType, JSONObject subBody) {
        JSONObject mobileHCodeDto = subBody.getJSONObject("mobileHCodeDto");
        String ispCode = mobileHCodeDto.getString("carrierName");
        String city = mobileHCodeDto.getString("cityName");
        String telCode = mobileHCodeDto.getString("telCode");
        String provTelCode = mobileHCodeDto.getString("provTelCode");
        String mobileHCode = mobileHCodeDto.getString("mobileHCode");
        Context context = new Context();
        String userName = subBody.getString("userName");

        context.setTaskId(taskId);//唯一taskId
        context.setUserName(userName);//用户名
        context.setPassword(subBody.getString("userPwd"));//密码
        context.setIdCard(subBody.getString("idCard"));//身份证号码
        context.setIdName(subBody.getString("idName"));//身份证姓名
        context.setTaskStatus(taskStatus);//任务状态
        context.setTaskSubStatus(taskSubStatus);//任务子状态 用于判断从什么地方开始执行代码,由爬虫传给网关,再回传即可
        context.setTaskType(taskType);//业务类型
        context.setTaskSubType(taskSubType);//业务子类类型
        context.setUserInput(userInput);//验证码值
        context.getMobileHCodeDto().setCarrierName(ispCode);
        context.getMobileHCodeDto().setCityName(city);
        context.getMobileHCodeDto().setMobileHCode(mobileHCode);
        context.getMobileHCodeDto().setTelCode(telCode);
        context.getMobileHCodeDto().setProvTelCode(provTelCode);

        context.setParam1(subBody.getString("param1"));
        context.setParam2(subBody.getString("param2"));
        context.setParam3(subBody.getString("param3"));
        context.setParam4(subBody.getString("param4"));
        context.setParam5(subBody.getString("param5"));
        context.setParam6(subBody.getString("param6"));
        return context;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Map<String, String> getProcessorMap() {
        return processorMap;
    }

    public void setProcessorMap(Map<String, String> processorMap) {
        this.processorMap = processorMap;
    }
}