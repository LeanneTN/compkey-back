package com.example.compkeyback.controller;

import com.example.compkeyback.annotation.IpLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 接口测试，实际的项目中并不需要
 */
public class IpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpController.class);
    private static final String MESSAGE = "请求失败,你的IP访问太频繁";
    //这里就不获取请求的ip,而是写死一个IP
    @ResponseBody
    @RequestMapping("iplimiter")
    @IpLimiter(ipAdress = "127.198.66.01", limit = 5, time = 10, message = MESSAGE)
    public String sendPayment(HttpServletRequest request) throws Exception {
        return "请求成功";
    }
    @ResponseBody
    @RequestMapping("iplimiter1")
    @IpLimiter(ipAdress = "127.188.145.54", limit = 4, time = 10, message = MESSAGE)
    public String sendPayment1(HttpServletRequest request) throws Exception {
        return "请求成功";
    }
}
