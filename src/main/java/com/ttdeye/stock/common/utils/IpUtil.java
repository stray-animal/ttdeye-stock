package com.ttdeye.stock.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @Comment:  ip地址工具类
 * @Author: Zhangyongming
 * @Date: 获取本机ip地址
 */
public class IpUtil {


    /**
     * 获取本机的IP地址
     * Zhangyongming
     * 201807810
     * @return
     */
    private static StringBuilder getHostIP() {

        StringBuilder ifConfig = new StringBuilder();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        ifConfig.append(inetAddress.getHostAddress().toString() + "\n");
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ifConfig;
    }

    /**
     * 获取客户端IP
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && "0:0:0:0:0:0:0:1".equalsIgnoreCase(ip)) {
            ip = "127.0.0.1";
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ip!=null && ip.length()>15){ //"***.***.***.***".length() = 15
            if(ip.indexOf(",")>0){
                ip = ip.substring(0,ip.indexOf(","));
            }
        }
        return ip;
    }

    public static void main(String[] args) {
        System.out.println(IpUtil.getHostIP());
    }
}
