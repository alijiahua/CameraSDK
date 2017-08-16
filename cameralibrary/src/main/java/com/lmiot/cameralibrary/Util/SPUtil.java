package com.lmiot.cameralibrary.Util;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.lmiot.cameralibrary.Camera_new.bean.CameraRespone;
import java.util.List;

/**
 * Created by Administrator on 2015-12-13.
 */
public class SPUtil {

    private static List<CameraRespone.ContentBean> cameracontent01=null;
    private static String serverUrl="http://iot.insi.cn/AppServer";
    private static String sessionID="";


    /**
     * 保存所有摄像头
     * @param cameracontent
     */
    public static void SetCameraList(List<CameraRespone.ContentBean> cameracontent) {
        cameracontent01=cameracontent;
    }

    public static List<CameraRespone.ContentBean>  GetCameraList() {
        return cameracontent01;
    }

    /**
     * 获取摄像头密码
     */
    public static boolean SetCameraPs(Context context, String cameraID,String password) {
        SharedPreferences sp = context.getSharedPreferences("CameraLirary", 0);
        Editor editor = sp.edit();
        editor.putString(cameraID, password);
        return editor.commit();
    }

    public static String GetCameraPs(Context context,String cameraID) {
        SharedPreferences sp = context.getSharedPreferences("CameraLirary", 0);
        return sp.getString(cameraID,null);
    }



    /**
     * 保存服务器地址
     * @param serverUrl
     */
    public static void setServerUrl(String serverUrl) {
        SPUtil.serverUrl = serverUrl;
    }
    public static String getServerUrl() {
        return serverUrl;
    }

    /**
     * 拼接网络请求地址
     **/
    public static String AppendServerUrl(String part) {
        StringBuilder serverUrl = new StringBuilder();
        serverUrl.append(SPUtil.getServerUrl());
        serverUrl.append(part);
        return serverUrl.toString();
    }

    public static void setSessionID(String sessionID) {
        SPUtil.sessionID = sessionID;
    }
    public static String getSessionID( ) {
        return sessionID;
    }


}
