package com.example.springboot.demo.utils;

import com.example.springboot.demo.enums.LimitType;
import com.example.springboot.demo.exception.LimitException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RateLimiterUtil {

    private static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    public static List<String> getRateKeys(JoinPoint joinPoint, LimitType type){
        StringBuilder id = new StringBuilder();
        //以方法名加参数列表作为唯一标识方法的key
        if (LimitType.ALL.equals(type)){
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            id.append(signature.getMethod().getName());
            Class[] parameterTypes = signature.getParameterTypes();
            for (Class clazz:parameterTypes){
                id.append(clazz.getName());
            }
            id.append(joinPoint.getTarget().getClass());
            if (log.isDebugEnabled()){
                log.debug("token bucket key id:{}",id.toString());
            }
            return getKeys(id.toString());
        }

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) return null;

        HttpServletRequest request = requestAttributes.getRequest();
        //以用户信息作为key
        if (LimitType.USER.equals(type)){
            if (request.getUserPrincipal()!=null){
                id.append(request.getUserPrincipal().getName());
            }else{
                throw new LimitException("The UserPrincipal property in the request is null");
            }
        }

        //以IP地址作为key
        if (LimitType.IP.equals(type)){
            id.append(getIpAddr(request));
        }

        //以URI作为key
        if (LimitType.URI.equals(type)){
            id.append(request.getRequestURI());
        }

        if (LimitType.CUSTOMER.equals(type)){
            if(request.getAttribute(Const.CUSTOM) != null){
                id.append(request.getAttribute(Const.CUSTOM).toString());
            }else {
                throw new LimitException(Const.CUSTOM + " attribute in the request is null");
            }
        }
        if (log.isDebugEnabled()){
            log.debug("rate limit redis key id:{}",id.toString());
        }
        return getKeys(id.toString());
    }

    /**
     * 获取当前网络ip
     *
     * @param request HttpServletRequest
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
