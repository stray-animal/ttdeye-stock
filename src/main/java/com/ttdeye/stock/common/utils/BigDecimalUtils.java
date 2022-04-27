package com.ttdeye.stock.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * bigdecimal 操作处理工具类
 */
public class BigDecimalUtils {

    /**
     * bigdecimal to str
     *
     * @param in
     * @return
     */
    public static String toStr(BigDecimal in) {
        if (in != null) {
            return in.toString();
        }
        return "";
    }

    /**
     * 过滤空
     *
     * @param in
     * @return
     */
    public static BigDecimal ifNullSet0(BigDecimal in) {
        if (in != null) {
            return in;
        }
        return BigDecimal.ZERO;
    }

    /**
     * 校验是否为空
     *
     * @param in
     * @return
     */
    public static Boolean isNotNull(BigDecimal in) {
        if (in != null && !"".equals(in)) {
            return true;
        }

        return false;
    }

    /**
     * 保留小数点2位
     * 保留小数点2位四舍五入
     * @param decimal
     * @return
     */
    public static BigDecimal format2(BigDecimal decimal, int scale) {
        // 保留两位小数
        BigDecimal format = ifNullSet0(decimal).setScale(scale, BigDecimal.ROUND_HALF_UP);

        return format;
    }

    /**
     * 保留小数点2位
     * 保留小数点2位四舍五入
     * @param decimal
     * @return
     */
    public static String format2Str(BigDecimal decimal, int scale) {
        // 保留两位小数
        BigDecimal format = ifNullSet0(decimal).setScale(scale, BigDecimal.ROUND_HALF_UP);

        return format.toString();
    }

    /**
     * BigDecimal to int
     *
     * @param decimal
     * @return
     */
    public static Integer getBigToInt(BigDecimal decimal) {

        // 精度设置
        return ifNullSet0(decimal).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
    }

    /**
     * 元2分 int
     *
     * @param amount
     * @return
     */
    public static Integer getY2F(BigDecimal amount) {

        return ifNullSet0(amount).multiply(new BigDecimal(100)).intValue();
    }

    /**
     * 元2分 int
     *
     * @param amount
     * @return
     */
    public static BigDecimal getY2BigF(BigDecimal amount) {

        return ifNullSet0(amount).multiply(new BigDecimal(100));
    }

    /**
     * 元2分 long
     *
     * @param amount
     * @return
     */
    public static Long getY2FLong(BigDecimal amount) {

        return ifNullSet0(amount).multiply(new BigDecimal(100)).longValue();
    }

    /**
     * BigDecimal
     *
     * @param decimal
     * @return
     */
    public static BigDecimal getBigDecimal(BigDecimal decimal) {
        // 精度设置
        return ifNullSet0(decimal).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * int to BigDecimal
     *
     * @param quant
     * @return
     */
    public static BigDecimal getIntToBig(Integer quant) {
        // 精度设置
        return new BigDecimal(quant);
    }

    /**
     * 求和
     *
     * @param in
     * @return
     */
    public static BigDecimal sum(BigDecimal... in) {
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < in.length; i++) {
            result = result.add(ifNullSet0(in[i]));
        }
        return result;
    }

    /**
     * 两者之和
     *
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal add(BigDecimal b1, BigDecimal b2) {
        return format2(ifNullSet0(b1).add(ifNullSet0(b2)), 2);
    }

    /**
     * 减法操作
     *
     * @param decimal1
     * @param decimal2
     * @return
     */
    public static BigDecimal subtract(BigDecimal decimal1, BigDecimal decimal2) {
        return format2((ifNullSet0(decimal1)).subtract(ifNullSet0(decimal2)), 2);
    }

    /**
     * 除法运算(保留2位四舍五入)
     *
     * @param decimal1
     * @param decimal2
     * @return
     */
    public static BigDecimal divide(BigDecimal decimal1, BigDecimal decimal2, int scale) {
        return ifNullSet0(decimal1).divide(decimal2, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * b1小于等于b2
     *
     * @param b1
     * @param b2
     * @return
     */
    public static Boolean compareTo(BigDecimal b1, BigDecimal b2) {
        if (ifNullSet0(b1).compareTo(ifNullSet0(b2)) < 1) {
            return true;
        }

        return false;
    }

    /**
     * b1等于b2
     * @param b1
     * @param b2
     * @return
     */
    public static Boolean eqTo(BigDecimal b1, BigDecimal b2) {
        if (ifNullSet0(b1).compareTo(ifNullSet0(b2)) == 0) {
            return true;
        }

        return false;
    }

    /**
     * 返回 b1或b2
     *
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal getPay(BigDecimal b1, BigDecimal b2) {
        if (ifNullSet0(b1).compareTo(BigDecimal.ZERO) == 1) {
            return b1;
        }

        if (ifNullSet0(b2).compareTo(BigDecimal.ZERO) == 1) {
            return b2;
        }

        return BigDecimal.ZERO;
    }

    /**
     * 乘法
     *
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal multiply(BigDecimal b1, BigDecimal b2) {
        BigDecimal result = ifNullSet0(b1).multiply(ifNullSet0(b2));
        // 保留2位小数四舍五入

        return result;
    }

    /**
     * 金额为分的格式
     */
    public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";

    /**
     * 将分为单位的转换为元并返回金额格式的字符串 （除100）
     *
     * @param amount
     * @return
     * @throws Exception
     */
    public static String changeF2Y(Long amount) throws Exception {
        if (!amount.toString().matches(CURRENCY_FEN_REGEX)) {
            throw new Exception("金额格式有误");
        }

        int flag = 0;
        String amString = amount.toString();
        if (amString.charAt(0) == '-') {
            flag = 1;
            amString = amString.substring(1);
        }
        StringBuffer result = new StringBuffer();
        if (amString.length() == 1) {
            result.append("0.0").append(amString);
        } else if (amString.length() == 2) {
            result.append("0.").append(amString);
        } else {
            String intString = amString.substring(0, amString.length() - 2);
            for (int i = 1; i <= intString.length(); i++) {
                if ((i - 1) % 3 == 0 && i != 1) {
                    result.append(",");
                }
                result.append(intString.substring(intString.length() - i, intString.length() - i + 1));
            }
            result.reverse().append(".").append(amString.substring(amString.length() - 2));
        }
        if (flag == 1) {
            return "-" + result.toString();
        } else {
            return result.toString();
        }
    }

    /**
     * 将分为单位的转换为元 （除100）
     *
     * @param amount
     * @return
     * @throws Exception
     */
    public static BigDecimal changeF2Y(String amount) {
        if (StringUtils.isNotBlank(amount)) {
            return BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100));
        }

        return BigDecimal.ZERO;
    }

    /**
     * 将分为单位的转换为元 （除100）
     *
     * @param amount
     * @return
     */
    public static BigDecimal getF2Y(Long amount) {
        if (amount != null) {
            return new BigDecimal(amount).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 将分为单位的转换为元 （除100）
     * @param amount
     * @return
     */
    public static BigDecimal getBigF2Y(BigDecimal amount) {
        if (amount != null) {
            return amount.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 将元为单位的转换为分 （乘100）
     *
     * @param amount
     * @return
     */
    public static String changeY2F(Long amount) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).toString();
    }

    /**
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
     *
     * @param amount
     * @return
     */
    public static String changeY2F(String amount) {
        String currency = amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        return amLong.toString();
    }

    /**
     * 金额正负处理
     * @param symbol
     * @param decimal
     * @return
     */
    public static String appendStr(String symbol, BigDecimal decimal) {
        return String.format("%s%s", symbol, decimal);
    }

    public static void main(String[] args) {
        System.out.println( format2(new BigDecimal(1000), 2));
    }


    /**
     * Long 加法
     * @param a
     * @param b
     * @return
     */
    public static long add(long a , long b){
        BigDecimal bigintegerA = new BigDecimal(a);
        BigDecimal bigintegerB = new BigDecimal(b);
        return bigintegerA.add(bigintegerB).longValue();
    }

    /**
     * Long 加法
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal add(Long a , Long b){
        BigDecimal bigintegerA = new BigDecimal(a);
        BigDecimal bigintegerB = new BigDecimal(b);
        return bigintegerA.add(bigintegerB);
    }

    /**
     * Long 减法
     * @param a
     * @param b
     * @return
     */
    public static long subtract(long a , long b){
        BigDecimal bigintegerA = new BigDecimal(a);
        BigDecimal bigintegerB = new BigDecimal(b);
        return bigintegerA.subtract(bigintegerB).longValue();
    }

    /**
     * Long 乘法
     * @param a
     * @param b
     * @return
     */
    public static long multiply(long a , long b){
        BigDecimal bigintegerA = new BigDecimal(a);
        BigDecimal bigintegerB = new BigDecimal(b);
        return bigintegerA.multiply(bigintegerB).longValue();

    }

    /**
     * Long 除法
     * @param a
     * @param b
     * @return
     */
    public long divide(long a , long b){
        BigDecimal bigintegerA = new BigDecimal(a);
        BigDecimal bigintegerB = new BigDecimal(b);
        return bigintegerA.divide(bigintegerB).longValue();

    }
}
