package com.whl.wako.kernel.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class IPUtils  {
    private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);

    public static String getLocalIp(){
        String localIp="";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            if(ip==null){
                logger.warn("getLocalIp ip is null");
                return localIp;
            }
            localIp = ip.getHostAddress();
            if(BeanUtils.isNotEmpty(localIp)){
                return localIp;
            }
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (null != network) {
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        localIp = ip.getHostAddress();
                        logger.info("InetAddress.getInetAddresses enum localIp:{}",localIp);
                        if(BeanUtils.isNotEmpty(localIp)){
                            return localIp;
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("InetAddress.getLocalHost exception",e);
        }
        return localIp;
    }

    public static boolean isMask(String mask,String ip){
        logger.info("IPUtils mask:{},ip:{}",mask,ip);
        if(BeanUtils.isEmpty(mask) || BeanUtils.isEmpty(ip)){
            return false;
        }
        boolean match = ip.matches(mask);
        logger.info("IPUtils mask:{},ip:{},match:{}",mask,ip,match);
        return match;
    }

    public static boolean isMask(String mask, List<String> ips){
        boolean match = false;
        logger.info("IPUtils mask:{},ips:{}",mask, JSON.toJSONString(ips));
        if(BeanUtils.isEmpty(mask) || BeanUtils.isEmpty(ips)){
            return false;
        }
        for(String ip:ips){
            match = match || isMask(mask,ip);
        }
        return match;
    }

    public static boolean isMask(List<String> masks, String ip){
        boolean match = false;
        logger.info("IPUtils masks:{},ip:{}",JSON.toJSONString(masks), ip);
        if(BeanUtils.isEmpty(masks) || BeanUtils.isEmpty(ip)){
            return false;
        }
        for(String mask:masks){
            match = match || isMask(mask,ip);
        }
        return match;
    }
}
