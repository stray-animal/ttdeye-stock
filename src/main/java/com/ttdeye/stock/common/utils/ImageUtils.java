package com.ttdeye.stock.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * @author luoyunlong
 * @date 2018/11/20 10:49
 */
public class ImageUtils {

    /**
     * 图片通过Base64编码之后转换成字符串
     * @param in 文件输入流
     * @return
     */
    public static String imageToString(InputStream in) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(Base64.encodeBase64(data));
    }

    /**
     * 图片通过Base64编码之后转换成字符串
     * @param filePath 文件全路径
     * @return
     */
    public static String imageToString(String filePath) {
        InputStream in = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.isFile()) {
                    in = new FileInputStream(file);
                    return imageToString(in);
                }
            }
        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\lenovo\\Desktop\\保养手册\\默认.png";
        System.out.println(imageToString(filePath));
    }
}
