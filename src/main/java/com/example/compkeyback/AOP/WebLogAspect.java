package com.example.compkeyback.AOP;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Aspect
@Component
public class WebLogAspect {
    private final static Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
    //** 以 controller 包下定义的所有请求为切入点 */
    @Pointcut("execution(public * com.example.compkeyback.controller.*.*(..))")
    public void webLog() {}
    /**
     * 在切点之前织入
     * @param joinPoint
     * @throws Throwable
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 打印请求相关参数
        logger.info("========================================== Start ==========================================");
        // 打印请求 url
        logger.info("URL            : {}", request.getRequestURL().toString());
        // 打印 Http method
        logger.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        logger.info("IP             : {}", getIpAddr(request));
        // 打印请求入参
        printRequestParamsLog(joinPoint);
        // 打印header信息
        printHeaderInfoLog(request);
    }
    /**
     * 在切点之后织入
     * @throws Throwable
     */
    @After("webLog()")
    public void doAfter() throws Throwable {
        logger.info("=========================================== End ===========================================");
        // 每个请求之间空一行
        logger.info("");
    }

    /**
     * 环绕
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
//        logger.info("Response Result: {}", new Gson().toJson(result));
        logger.info("Response Result: {}", JSON.toJSONString(result));
        // 执行耗时
        logger.info("Use Time       : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }


    /**
     * 打印request请求参数
     */
    public void printRequestParamsLog(JoinPoint joinPoint){
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] parameterValues = joinPoint.getArgs(); // 获取方法参数
        StringBuffer paramsBuf = new StringBuffer();
        for (int i = 0; i < parameterValues.length; i++) {
            logger.info("Request params : {}", parameterNames[i]+" = "+parameterValues[i]);
        }
    }

    /**
     * 打印header信息
     */
    public void printHeaderInfoLog(HttpServletRequest request){
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            //logger的意思相当于是在控制台打印
            logger.info("Header         : {}", key+" = "+value);
        }
    }

    /**
     * @Description: 获取ip，通过这个来确认用户的身份
     */
    public String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
            // = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        // 或者对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        return ipAddress;
    }
}
