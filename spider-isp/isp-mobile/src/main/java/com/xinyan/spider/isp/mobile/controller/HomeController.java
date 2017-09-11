package com.xinyan.spider.isp.mobile.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by johnny on 2017/6/7.
 */
@RequestMapping("/check")
@RestController
public class HomeController {

    @RequestMapping()
    public String home(){
        return "success";
    }
}
