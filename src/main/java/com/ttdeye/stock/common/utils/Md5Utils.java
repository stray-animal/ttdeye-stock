package com.ttdeye.stock.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Slf4j
public class Md5Utils {

    /**
     * MD5签名
     *
     * @param str
     * @return
     */
     static String encryptMD5(String str, String charset) {
        StringBuilder sb = new StringBuilder();
        for (byte b : md5(str, charset)) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString().toLowerCase();
    }

    static String md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes(Charset.defaultCharset()));
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            // System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] md5(String str, String charset) {
        if (charset == null) {
            charset = "UTF-8";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            return md5.digest(str.getBytes(charset));
        } catch (Exception e) {
            log.error("MD5加密出错。数据是：" + str, e);
        }
        return null;
    }


    public static void main(String[] args) {
        String pwd = getMD5("wesd1992-08-26 12:12:12","utf-8");
        System.out.println(checkMD5(pwd,"wesd1992-08-26 12:12:12"));
    }

    //生成MD5
    public static String getMD5(String message) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  // 创建一个md5算法对象
            byte[] messageByte = message.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte);              // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte);                            // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String getMD5(String message,String charseName) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  // 创建一个md5算法对象
            byte[] messageByte = message.getBytes(charseName);
            byte[] md5Byte = md.digest(messageByte);              // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte);                            // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    // 二进制转十六进制
    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if(num < 0) {
                num += 256;
            }
            if(num < 16){
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

    public static boolean checkMD5(String md5,String str) {

         if (getMD5(str,"utf-8").equals(md5)){
             return true;
         }else{
             return  false;
         }

    }
}
