package com.lmiot.cameralibrary.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmiot.cameralibrary.Camera_new.CameraDevices;
import com.lmiot.cameralibrary.Camera_new.PlayActivity;
import com.lmiot.cameralibrary.Camera_new.utils.SystemValue;
import com.lmiot.cameralibrary.R;
import com.lmiot.cameralibrary.SQL.CamerBean;

/**
 * 创建日期：2017-12-07 15:11
 * 作者:Mr Li
 * 描述:
 */
public class ApiUtls {
    private static final ApiUtls ourInstance = new ApiUtls();

    public static ApiUtls getInstance() {
        return ourInstance;
    }

    private ApiUtls() {
    }



    private  static String userName01="123456";
    private  static String showItem01="";

    public  void setUerName(String userName) {
        userName01=userName;
    }

    public  String getUerName( ) {
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




    public void skipCamera(Activity context,CamerBean camerBean) {
        if(camerBean.getPsRight()){  //已经存过密码
            ConnectCameraData(context,camerBean.getCameraID(), camerBean.getCameraPassword());
        }
        else{
            ShowPsDialog(context,camerBean);
        }
    }

    /**
     * 输入密码对话框
     *
     * @param camerBean
     */
    private void ShowPsDialog(final Activity context, final CamerBean camerBean) {
        final Dialog dailog = LayoutDialogUtil.createDailog(context, R.layout.dialog_change_edit_layout);
        dailog.setCancelable(false);
        TextView title = (TextView) dailog.findViewById(R.id.id_title);
        title.setText(R.string.judge_ps);
        final EditText edit = (EditText) dailog.findViewById(R.id.id_edit);
        edit.setHint(R.string.input_camera_ps);

        Button sure = (Button) dailog.findViewById(R.id.id_sure);
        ImageView cancel = (ImageView) dailog.findViewById(R.id.id_cancel);


        dailog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edit.getText().toString();
                ConnectCameraData(context,camerBean.getCameraID(), pass);
                dailog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailog.dismiss();
            }
        });

    }

    /**
     * 连接摄像头
     */
    private void ConnectCameraData(Activity context,String mCameraID, String mCameraPs) {
        SystemValue.deviceName = "admin";
        SystemValue.deviceId = mCameraID;
        SystemValue.devicePass = mCameraPs;
        JumpActivityUtils.JumpToActivity(context, PlayActivity.class, false, true);

    }


}
