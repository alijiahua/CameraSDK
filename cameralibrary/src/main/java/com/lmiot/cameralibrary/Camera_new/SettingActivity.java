package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.Camera_new.utils.SystemValue;
import com.lmiot.cameralibrary.R;


/**
 * @author 设备系统设置
 */
public class SettingActivity extends BaseActivity  implements View.OnClickListener{

    private String strDID;
    private String cameraName;
    private String cameraPwd;
    private Intent mIntent;
    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private LinearLayout mWifiSetting;
    private LinearLayout mPwdSetting;
    private LinearLayout mSdSetting;
    private LinearLayout mTfSetting;
    private LinearLayout mUpdateFirmware;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        getDataFromOther();
        initView();
    }

    //获取activity传过来的数据
    private void getDataFromOther() {
        Intent intent = getIntent();
        strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
        cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
        cameraPwd = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
    }

    //初始化控件
    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mWifiSetting = findViewById(R.id.wifi_setting);
        mPwdSetting = findViewById(R.id.pwd_setting);
        mSdSetting = findViewById(R.id.sd_setting);
        mTfSetting = findViewById(R.id.tf_setting);
        mUpdateFirmware = findViewById(R.id.update_firmware);

        mIvBack.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
        mWifiSetting.setOnClickListener(this);
        mPwdSetting.setOnClickListener(this);
        mSdSetting.setOnClickListener(this);
        mTfSetting.setOnClickListener(this);
        mUpdateFirmware.setOnClickListener(this);




        mIdTitle.setText("设置");
        mTvModify.setVisibility(View.GONE);
        mIvAdd.setVisibility(View.GONE);

    }



    private <T> void JumpActivity(Class<T> tClass) {
        mIntent = new Intent(SettingActivity.this, tClass);
        mIntent.putExtra(ContentCommon.STR_CAMERA_ID, SystemValue.deviceId);
        mIntent.putExtra(ContentCommon.STR_CAMERA_NAME, SystemValue.deviceName);
        mIntent.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
        startActivity(mIntent);
        overridePendingTransition(R.anim.move_left_out_activity, R.anim.move_right_in_activity);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back || i == R.id.tv_back) {
            finish();

        } else if (i == R.id.wifi_setting) {
            JumpActivity(SettingWifiActivity.class);

        } else if (i == R.id.pwd_setting) {
            JumpActivity(SettingUserActivity.class);

        } else if (i == R.id.sd_setting) {
            JumpActivity(SettingSDCardActivity.class);

        } else if (i == R.id.tf_setting) {
            JumpActivity(PlayBackTFActivity.class);

        } else if (i == R.id.update_firmware) {
            JumpActivity(FirmwareUpdateActiviy.class);

        }

    }
}