package com.ttdeye.stock.common.utils;

import java.util.Calendar;
import java.util.Date;

/***
 **@author 张永明
 **@date 2022/6/8 19:08
 ***/
public class DateLaterUtils {


    public static Date getLastDate(Date currenttime,Integer shelfLife){
        Calendar c = Calendar.getInstance();

        c.setTime(currenttime);
        c.add(Calendar.DATE, shelfLife);

        return c.getTime();
    }



    public static int dateSorted(Date date1,Date date2){
        if(date1.before(date2)){
            return -1;
        }
        if(date1.after(date2)){
            return 1;
        }
        return 0;
    }

}
