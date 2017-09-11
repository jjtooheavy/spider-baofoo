package com.xinyan.spider.isp;

import com.xinyan.spider.isp.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * @author heliang
 */
@SpringBootApplication
@EnableConfigurationProperties()
@ComponentScan(basePackages = "com.xinyan.spider")
@PropertySource(value = {"file:/data/xinyan/spider/config/spider-isp.properties"})
public class IspMainApplication {

    private static final Logger logger = LoggerFactory.getLogger(IspMainApplication.class);

    public static void main(String[] args) {

        logger.info(">正在启动[{}]程序...", Constants.SPIDER_NAME);

        SpringApplication.run(IspMainApplication.class, args);
    }
}
