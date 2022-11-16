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
//            return CommonResponse.creatForSuccess("ip被ban");
            return false;
        }
        if(!addRequestTime(ipAddressUtil.getRemoteAddress(httpServletRequest),httpServletRequest.getRequestURI())){
            ApiResult result = new ApiResult(ResultEnum.LOCK_IP.getCode(),ResultEnum.LOCK_IP.getDescription(),"访问超时");
//            returnJson(httpServletResponse, JSON.toJSONString(result));
            return false;
        }
        return true;

    }

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

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
     * @Description: 判断ip是否被禁用
     * @author: shuyu.wang
     * @date: 2019-10-12 13:08
     * @param ip
     * @return java.lang.Boolean
     */
    private Boolean ipIsLock(String ip){
        RedisUtil redisUtil=getRedisUtil();
        if(redisUtil.hasKey(LOCK_IP_URL_KEY+ip)){
            return true;
        }
        return false;
    }

    private RedisUtil getRedisUtil() {
    }


}

