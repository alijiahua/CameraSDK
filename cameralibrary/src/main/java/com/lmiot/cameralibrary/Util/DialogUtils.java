package com.lmiot.cameralibrary.Util;

import android.content.Context;

import com.example.yideng.loaddialoglibrary.LoadDialog;


/**
 * Created by ming on 2016/8/18.
 *加载对话框和
 *
 */
public class DialogUtils {

    private static LoadDialog loadingDialog;

    public static void ShowDialog(Context context){
        try{
            if(loadingDialog!=null){
                loadingDialog=null;
            }
            loadingDialog = new LoadDialog(context);
            loadingDialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void ShowDialog(Context context,String msg){
        try {
            if(loadingDialog!=null){
                loadingDialog=null;
            }
            loadingDialog = new LoadDialog(context);
            loadingDialog.SetText(msg);
            loadingDialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void HiddenDialog(){
        try {

            if(loadingDialog!=null){
                loadingDialog.dismiss();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



}
