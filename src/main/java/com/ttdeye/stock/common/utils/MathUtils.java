package com.ttdeye.stock.common.utils;

import java.math.BigDecimal;

/**
 * @author zhangboqing
 * @date 2018/8/4
 */
public class MathUtils {
    // 进行加法运算
    public static BigDecimal add(BigDecimal d1, BigDecimal d2) {
        return d1.add(d2);
    }

//    public static BigDecimal add(BigDecimal d1, BigDecimal d2, BigDecimal d3) {
//        return d1.add(d2).add(d3);
//    }

    public static BigDecimal add(BigDecimal d1, BigDecimal d2, BigDecimal ... d3) {
        if( null == d1 ){
            d1 = BigDecimal.valueOf(0);
        }
        if( null == d2 ){
            d2 = BigDecimal.valueOf(0);
        }
        d1 = d1.add(d2);
        for ( BigDecimal data:d3 ) {
            if( null != data ){
                d1 = d1.add(data);
            }
        }
        return d1;
    }

    //获取负数
    public static BigDecimal getMinusDecimal(BigDecimal decimal) {
        if (isBiggerOrEquals(decimal, new BigDecimal(0))) {
            return decimal.multiply(new BigDecimal(-1));
        }
        return decimal;
    }

    // 进行减法运算
    public static BigDecimal sub(BigDecimal d1, BigDecimal d2) {
        return d1.subtract(d2);
    }

    /**
     * 如果结果是负数,则自动赋值为0
     * @param d1 被减数
     * @param d2 减数
     * @param isChangeMinus 是否要把负数变为0
     * @return
     */
    public static BigDecimal sub(BigDecimal d1, BigDecimal d2, boolean isChangeMinus) {
        if( isChangeMinus ){
            if( d1.compareTo(d2) <= 0 ){
                return BigDecimal.valueOf(0.00);
            }
        }
        return d1.subtract(d2);
    }

    // 进行乘法运算
    public static BigDecimal mul(BigDecimal d1, BigDecimal d2) {
        Double one = d1.doubleValue() * 1000;
        Double two = d2.doubleValue() * 1000;
        BigDecimal b1 = new BigDecimal(one);
        BigDecimal b2 = new BigDecimal(two);
        return div(b1.multiply(b2), new BigDecimal(1000 * 1000), 2);
    }

    // 进行除法运算
    public static BigDecimal div(BigDecimal d1, BigDecimal d2, int len) {
        return d1.divide(d2, len, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 乘法保留小数计算(非四舍五入,直接抹掉多余位小数)
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal mul(BigDecimal d1, BigDecimal d2, Integer len) {
        //这里使用BigDecimal.doubleValue()的话会丢失精度
        return d1.multiply(d2).setScale(len, BigDecimal.ROUND_DOWN);
    }

    /**
     * 除法保留小数计算(非四舍五入,直接抹掉多余位小数)
     * @param d1
     * @param d2
     * @param len
     * @return
     */
    public static BigDecimal divNotHalfUp(BigDecimal d1, BigDecimal d2, int len) {
        return d1.divide(d2, len, BigDecimal.ROUND_DOWN);
    }

    // d1 是否比 d2 值更大 或者 相等
    public static Boolean isBiggerOrEquals(BigDecimal d1, BigDecimal d2) {
        if (d1 == null) {
            return false;
        } else {
            if (d2 == null) {
                return true;
            }
        }
        return d1.compareTo(d2) >= 0;
    }

    // d1 是否比 d2 值更大 或者 相等
    public static Boolean isBetweenZeroAndOneundred(BigDecimal d1) {
        Boolean result = false;
        if (d1 == null) {
            return false;
        }
        if (isBiggerOrEquals(d1, BigDecimal.ZERO) && d1.compareTo(new BigDecimal("100")) < 0) {
            result = true;
        }
        return result;
    }

    // 进行四舍五入操作
    public static BigDecimal round(BigDecimal d1, int len) {
        BigDecimal b2 = new BigDecimal(1);
        // 任何一个数字除以1都是原数字
        // ROUND_HALF_UP是BigDecimal的一个常量，表示进行四舍五入的操作
        return d1.divide(b2, len, BigDecimal.ROUND_HALF_UP);
    }

    /** 计算浮动比率 */
    public static BigDecimal floatRate(BigDecimal a, BigDecimal b) throws Exception {

        if(a == null) {
            throw new Exception("计算值不能为空");
        }

        if(b == null) {
            throw new Exception("计算值不能为空");
        }


        // flag 为判断 a 是否 与b交换位置, 交换位置表示返回值需要为负数
        boolean flag = false;
        if (a.compareTo(b) < 0) {
            BigDecimal c = a;
            a = b;
            b = c;
            flag = true;
        } else if (a.compareTo(b) == 0) {
            return BigDecimal.ZERO;
        }
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal mul = mul(a, new BigDecimal(100));
            if (flag) {
                mul = getMinusDecimal(mul);
            }
            return mul;
        } else {
            BigDecimal sub = sub(a, b);
            BigDecimal div = div(sub, b, 4);
            BigDecimal mul = mul(div, new BigDecimal(100));
            if (flag) {
                mul = getMinusDecimal(mul);
            }
            return mul;
        }
    }

    public static BigDecimal percentage(BigDecimal a, BigDecimal b) throws Exception {
        if(a == null) {
            throw new Exception("计算值不能为空");
        }

        if(b == null) {
            throw new Exception("计算值不能为空");
        }
        // 如果两个中任意一个都为0, 就直接返回0
        if (a.compareTo(BigDecimal.ZERO) == 0 || b.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // 如果两个都不为0
        BigDecimal div = div(a, b, 4);
        BigDecimal mul = mul(div, new BigDecimal(100));
        return mul;
    }

    //测试
    public static void main(String[] args) {
        BigDecimal b1 = new BigDecimal(0);
        b1 = new BigDecimal(102.70);
        BigDecimal b2 = new BigDecimal(15);

//        System.out.println(b1);
//        System.out.println(b2);
//
//        System.out.println(floatRate(b1, b2));
//
//        System.out.println(b1);
//        System.out.println(b2);
//


//        System.out.println(mul(b1,b2,2));
//
//        System.out.println(divNotHalfUp(b1,b2, 2));

        System.out.println(add(BigDecimal.valueOf(1),BigDecimal.valueOf(0),BigDecimal.valueOf(3),BigDecimal.valueOf(10.5)));
        System.out.println(add(null,BigDecimal.valueOf(0),BigDecimal.valueOf(3),BigDecimal.valueOf(7)));
    }
}