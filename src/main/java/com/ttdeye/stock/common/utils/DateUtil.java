package com.ttdeye.stock.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Comment: $comment$
 * @Author: Zhangyongming
 * @Date: $date$ $time$
 */
public class DateUtil {


    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<String, DateTimeFormatter>();

    private static final int PATTERN_CACHE_SIZE = 500;

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String HH_MM_SS = "HH:mm:ss";

    /**
     * Date -> LocalDateTime
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime -> Date
     *
     * @param localDateTime
     * @return Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date转换为格式化时间
     * @param date date
     * @param pattern 格式
     * @return
     */
    public static String format(Date date, String pattern){
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), pattern);
    }

    /**
     * localDateTime转换为格式化时间
     * @param localDateTime localDateTime
     * @param pattern 格式
     * @return
     */
    public static String format(LocalDateTime localDateTime, String pattern){
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * 格式化字符串转为Date
     * @param time 格式化时间
     * @param pattern 格式
     * @return
     */
    public static Date parseDate(String time, String pattern){
        if (StringUtils.isBlank(time)) {
            return null;
        }
        return Date.from(parseLocalDateTime(time, pattern).atZone(ZoneId.systemDefault()).toInstant());

    }

    /**
     * 格式化字符串转为LocalDateTime
     * @param time 格式化时间
     * @param pattern 格式
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String time, String pattern){
        if (StringUtils.isBlank(time)) {
            return null;
        }
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return YYYY_MM_DD.equals(pattern) ? LocalDate.parse(time, formatter).atStartOfDay() : LocalDateTime.parse(time, formatter);
    }

    /**
     * 在缓存中创建DateTimeFormatter
     * @param pattern 格式
     * @return
     */
    private static DateTimeFormatter createCacheFormatter(String pattern){
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern specification");
        }
        DateTimeFormatter formatter = FORMATTER_CACHE.get(pattern);
        if(formatter == null){
            if(FORMATTER_CACHE.size() < PATTERN_CACHE_SIZE){
                formatter = DateTimeFormatter.ofPattern(pattern);
                DateTimeFormatter oldFormatter = FORMATTER_CACHE.putIfAbsent(pattern, formatter);
                if(oldFormatter != null){
                    formatter = oldFormatter;
                }
            }
        }

        return formatter;
    }


    /**
     * @Description 获取明天的当前时间，年月日时分秒--盛大专用
     * @Author 张永明
     * @Date  2018/7/27 
     * @Param 
     * @return 
     **/
    public static String getTomorrow(){
        //取明天这个时候的时间
        LocalDateTime starttime = LocalDateTime.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
        String startTime = starttime.format(formatter);
        return startTime;
    }

    public static Date getTomorrowStartOfDayDate(){
        return localDateTimeToDate(getTomorrowStartOfDayDateTime());
    }

    public static LocalDateTime getTomorrowStartOfDayDateTime(){
        return LocalDate.now().plusDays(1).atStartOfDay();
    }

    public static LocalDateTime getRandomFiveYearFuture(){
        ThreadLocalRandom localRandom = ThreadLocalRandom.current();

        LocalDateTime now = LocalDateTime.now();

        return now.plusDays(localRandom.nextInt(32))
                .plusMonths(localRandom.nextInt(12))
                .plusYears(localRandom.nextInt(5));
    }

}
