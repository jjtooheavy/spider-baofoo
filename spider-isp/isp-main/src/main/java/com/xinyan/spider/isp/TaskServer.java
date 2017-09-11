package com.xinyan.spider.isp;

import com.xinyan.spider.isp.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by heliang on 2016/6/30.
 * 任务服务
 */
@Component
public class TaskServer {

    private Logger logger = LoggerFactory.getLogger(TaskServer.class);

    private ExecutorService executorService=Executors.newFixedThreadPool(1);

    @Autowired
    private SpilderServerTask spilderServer;

    @PostConstruct
    public void start(){
        logger.info(">开始启动[{}]爬虫任务...", Constants.SPIDER_NAME);
        try{
            executorService.execute(spilderServer);
        }catch (Throwable ex){
            logger.info(">启动[{}]爬虫任务失败.", Constants.SPIDER_NAME, ex);
        }
        logger.info(">[{}]爬虫任务已启动.", Constants.SPIDER_NAME);
    }

    @PreDestroy
    public void stop(){
        logger.info(">开始停止[{}]爬虫任务...", Constants.SPIDER_NAME);
        try{
            spilderServer.stop();
            executorService.shutdown();
        }catch (Throwable ex){
            logger.info(">停止[{}]爬虫任务失败.", Constants.SPIDER_NAME, ex);
        }
        logger.info(">[{}]爬虫任务已停止.", Constants.SPIDER_NAME);
    }

}
