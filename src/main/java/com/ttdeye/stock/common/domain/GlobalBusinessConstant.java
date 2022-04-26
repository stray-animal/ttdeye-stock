package com.ttdeye.stock.common.domain;

/**
 * @author zhangboqing
 * @date 2018/10/13
 */
public class GlobalBusinessConstant {

    public static class SECRET_KEY {
        public final static String BASE64SECRET = "szaisinoMDk4ZjZbudinglingyangiY2Q0NjIxZDM3M2NhZGU-";
    }

        public static class EXPIRE_TIMES {

            public static final long MINUTES_ONE = 60L;//一分钟

            public static final long MINUTES_FIVE = 5 * 60L;//五分钟

            public static final long MINUTES_TEN = 10 * 60L;//十分钟

            public static final long MINUTES_FIFTEEN = 15 * 60L;//十分钟

            public static final long MINUTES_THIRTY = 30 * 60L;//三十分钟

            public static final long HOURS_ONE = 60 * 60L;//一小时

            public static final long HOURS_TWO = 2 * 60 * 60L;//两小时

            public static final long HOURS_EIGHT = 8 * 60 * 60L;//八小时

            public static final long DAYS_ONE = 24 * 60 * 60L;//一天

            public static final long WEEKS_ONE = 7 * 24 * 60 * 60L;//一周

            public static final long MONTHS_ONE = 30 * 24 * 60 * 60L;//一月

            public static final long YEARS_ONE = 365 * 24 * 60 * 60L;//一年

        }

}
