package com.ttdeye.stock.common.utils;


import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


/**
 * 二维码图片
 */
public class QrCodeUtil {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 300;
    public static final String FORMAT = "png";
    public static final String CHARTSET = "utf-8";

    public static void main(String[] args) {
//        String filePath = "/Users/honest/Documents/soft/zhao.png";
        //        createQRcode(filePath);

//        String filePath1 = "https://sp0.baidu.com/5aU_bSa9KgQFm2e88IuM_a/micxp1.duapp.com/qr.php?value=www.baidu.com";
        String filePath1 = "file:///Users/honest/Desktop/%E4%BF%A1%E4%BA%A7-%E8%BD%A6%E6%9C%8D/OCR%E8%BD%A6%E7%89%8C/large/20180828163725332858-%E9%84%82AP24V5-large.jpg";
      String aa  =  createQRcodeByUrl(filePath1,"png");
        System.out.println("字节码："+aa);
//                testReadQRcode(filePath1);
    }

    private static void testReadQRcode(String filePath) {
        String result = getQRresult(filePath);

            System.out.println("二维内容：" + result);

    }

    /**
     * @param
     * @Title:createQRcode
     * @Description:创建二维码
     * @author 张永明
     * @修改时间：2018年2月26日 上午9:44:45
     * @修改内容：创建
     */
    public static String createQRcodeByUrl(String url, String type) {

        if (url == null || type == null || "".equals(url) || "".equals(type)) {
            return null;
        }

        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, CHARTSET);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, type, bos);
            byte[] imageBytes = bos.toByteArray();
            Base64 encoder = new Base64();
            String imageString = new String(encoder.encode(imageBytes));
            bos.close();
            return imageString;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * @param filePath
     * @return
     * @Title:getQRresult
     * @Description:读取二维码
     * @author 张永明
     * @修改时间：2018年7月20日 上午9:45:19
     * @修改内容：创建
     */
    public static String getQRresult(String filePath) {
        /**
         * 如果用的jdk是1.9，需要配置下面这一行。
         */
        //System.setProperty("java.specification.version", "1.9");
        Result result = null;
        try {
            File file = new File(filePath);

            BufferedImage bufferedImage = ImageIO.read(file);
            BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));

            HashMap hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, CHARTSET);
            result = new MultiFormatReader().decode(bitmap, hints);

            return result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}