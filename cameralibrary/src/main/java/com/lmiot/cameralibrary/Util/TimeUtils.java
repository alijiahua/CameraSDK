package com.lmiot.cameralibrary.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ming on 2016/3/15.
 */
public class TimeUtils {

    public static String getCurrentDateInt(){  /*获取当前日期*/

        SimpleDateFormat format = new SimpleDateFormat("MMss");
        Date date=new Date(System.currentTimeMillis());
        return format.format(date);
    }
    public static String getCurrentDate(){  /*获取当前日期*/

        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
        Date date=new Date(System.currentTimeMillis());
        return format.format(date);
    }

    public static String getPreDate(){  /*获取当前日期*/

        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
        Date date=new Date(System.currentTimeMillis()-24*3600*1000);
        return format.format(date);
    }
    public static String getCurrentTime(){  /*获取当前时间*/

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date=new Date(System.currentTimeMillis());
        return format.format(date);
    }


    public static String convertSecondTime(int seconds) {  /*把秒数转为xx小时xx分xx秒*/

        int hours=(int)(seconds/3600);
        int minute=(int)((seconds%3600)/60);
        int second=seconds-hours*3600-minute*60;

        if(hours==0){
            return  minute+"分"+second+"秒";
        }
        else{
            return  hours+"小时"+ minute+"分"+second+"秒";
        }
    }
    public static String convertMinuteTime(int minutes) {  /*把分钟转为xx小时xx分*/

        int hours=(int)(minutes/60);
        int minute=minutes-hours*60;

            return  hours+"小时"+ minute+"分";
    }







    /*
 * 将时间转换为时间戳
 */
    public static String dateToStamp(String s) {

        try {
            String res;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
            return res;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }



    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(int s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s*1000L);
        res = simpleDateFormat.format(date);
        return res;
    }








}
