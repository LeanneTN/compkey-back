package com.example.compkeyback.Filter;

import com.alibaba.fastjson.JSON;
import com.example.compkeyback.common.ApiResult;
import com.example.compkeyback.common.CommonResponse;
import com.example.compkeyback.common.ResultEnum;
import com.example.compkeyback.util.IPAddressUtil;
import com.example.compkeyback.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义拦截类，针对url+ip在一定时间内访问的次数来将ip禁用
 */
@Slf4j
public class IpUrlLimitInterceptor implements HandlerInterceptor {
    private static final String LOCK_IP_URL_KEY="lock_ip_";

    private static final String IP_URL_REQ_TIME="ip_url_times_";

    private static final long LIMIT_TIMES=5;

    private static final int IP_LOCK_TIME=60;

    private static IPAddressUtil ipAddressUtil;

    private static RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull Object o) throws Exception{
        log.info("request请求地址uri={},ip={}", httpServletRequest.getRequestURI(), ipAddressUtil.getRemoteAddress(httpServletRequest));
        if (ipIsLock(ipAddressUtil.getRemoteAddress(httpServletRequest))){
            log.info("ip访问被禁止={}",ipAddressUtil.getRemoteAddress(httpServletRequest));
            //可以得知哪个ip被禁止，其实不返回也行
            ApiResult result = new ApiResult(ResultEnum.LOCK_IP.getCode(),ResultEnum.LOCK_IP.getDescription(),"ip访问被禁止");
            returnJson(httpServletResponse, JSON.toJSONString(result));
            return false;
        }
        if(!addRequestTime(ipAddressUtil.getRemoteAddress(httpServletRequest),httpServletRequest.getRequestURI())){
            ApiResult result = new ApiResult(ResultEnum.LOCK_IP.getCode(),ResultEnum.LOCK_IP.getDescription(),"访问超时");
            returnJson(httpServletResponse, JSON.toJSONString(result));
            return false;
        }
        return true;

    }

    /**
     * @Descrption: 记录请求次数
     * @param ip
     * @param uri
     * @return java.lang.Boolean
     * @author yoyo
     * @date 2022-11-17
     */
    private boolean addRequestTime(String ip, String uri) {

        String key = IP_URL_REQ_TIME+ip+uri;

        if(redisUtil.hasKey(key)){

            long time=redisUtil.incr(key,(long)1);

            if(time >=LIMIT_TIMES){

                redisUtil.set(LOCK_IP_URL_KEY+ip,IP_LOCK_TIME);

                return false;

            }

        }else {

            boolean set = redisUtil.set(key, (long) 1, 1);

        }

        return true;

    }


    /**
     * @Description: 判断ip是否被禁用
     * @author: yoyo
     * @date: 2022-11-16
     * @param ip
     * @return java.lang.Boole-an
     */
    private Boolean ipIsLock(String ip){
        if(redisUtil.hasKey(LOCK_IP_URL_KEY+ip)){
            return true;
        }
        return false;
    }



    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {
            log.error("LoginInterceptor response error ---> {}", e.getMessage(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}

