package com.lmiot.cameralibrary.Util;

/**
 * 创建日期：2017-12-05 08:30
 * 作者:Mr Li
 * 描述:
 */
public class DataUtil {

    private  static String userName01="123456";
    private  static String showItem01="";

    public static void setUerName(String userName) {
        userName01=userName;
    }

    public static String getUerName( ) {
      return  userName01;
    }

    /**
     * 长按隐藏菜单，使用时设为true
     * @param showItem
     */
    public static void setMoreItem(String showItem) {
        showItem01=showItem;
    }

    public static String getMoreItem( ) {
       return showItem01;
    }

}
