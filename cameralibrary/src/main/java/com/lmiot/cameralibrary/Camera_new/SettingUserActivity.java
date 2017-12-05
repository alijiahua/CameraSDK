package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lmiot.cameralibrary.Camera_new.BridgeService.UserInterface;
import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.R;
import com.lmiot.tiblebarlibrary.LmiotTitleBar;

import vstc2.nativecaller.NativeCaller;

public class SettingUserActivity extends BaseActivity implements UserInterface,View.OnClickListener{


    private boolean successFlag = false;
    private int CAMERAPARAM = 0xffffffff;//״̬
    private final int TIMEOUT = 3000;
    private final int FAILED = 0;
    private final int SUCCESS = 1;
    private final int PARAMS = 3;
    private String strDID;//camera id
    private String cameraName;
    private String operatorName = "";
    private String operatorPwd = "";
    private String visitorName = "";
    private String visitorPwd = "";
    private String adminName = "";
    private String adminPwd = "";
    private ProgressDialog progressDialog;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FAILED://set failed
                    showToast(R.string.user_set_failed);
                    break;
                case SUCCESS://set success
                    showToast(R.string.user_set_success);
                    NativeCaller.PPPPRebootDevice(strDID);

                    Log.d("info", "user:" + adminName + " pwd:" + adminPwd);
                    final Intent intent = new Intent(ContentCommon.STR_CAMERA_INFO_RECEIVER);
                    intent.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
                    intent.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
                    intent.putExtra(ContentCommon.STR_CAMERA_USER, adminName);
                    intent.putExtra(ContentCommon.STR_CAMERA_PWD, adminPwd);
                    intent.putExtra(ContentCommon.STR_CAMERA_OLD_ID, strDID);
                    intent.putExtra(ContentCommon.CAMERA_OPTION, ContentCommon.CHANGE_CAMERA_USER);
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            sendBroadcast(intent);
                        }
                    }, 3000);
                    finish();

                    break;
                case PARAMS://get user params
                    successFlag = true;
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    mEditName.setText(adminName);
                    mEditPwd.setText(adminPwd);
                    break;

                default:
                    break;
            }
        }
    };

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (!successFlag) {
                successFlag = false;
                progressDialog.dismiss();
//					showToast(R.string.user_getparams_failed);
            }
        }
    };
    private TextView mTextView0;
    private EditText mEditName;
    private TextView mTextView1;
    private EditText mEditPwd;
    private CheckBox mCboxShowPwd;
    private LmiotTitleBar mLmiotTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromOther();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settinguser);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.user_getparams));
        progressDialog.show();
        mHandler.postDelayed(runnable, TIMEOUT);
        findView();
        setLisetener();
        BridgeService.setUserInterface(this);
        NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_PARAMS);
    }

    private void setLisetener() {
        MyTextWatch myNameTextWatch = new MyTextWatch(R.id.edit_name);
        mEditName.addTextChangedListener(myNameTextWatch);
        MyTextWatch myPwdTextWatch = new MyTextWatch(R.id.edit_pwd);
        mEditPwd.addTextChangedListener(myPwdTextWatch);
    }

    private void getDataFromOther() {
        Intent intent = getIntent();
        strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
        cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
        adminName = "admin";
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
        super.onPause();
    }

    private void findView() {

        mTextView0 = findViewById(R.id.textView0);
        mEditName = findViewById(R.id.edit_name);
        mTextView1 = findViewById(R.id.textView1);
        mEditPwd = findViewById(R.id.edit_pwd);
        mCboxShowPwd = findViewById(R.id.cbox_show_pwd);
        mCboxShowPwd.setOnClickListener(this);

        mLmiotTitleBar = findViewById(R.id.id_lmiot_title_bar);
        mLmiotTitleBar.setOnItemClickListener(new LmiotTitleBar.onItemClickListener() {
            @Override
            public void onBackClick(View view) {
                finish();
            }

            @Override
            public void onMenuClick(View view) {
                setUser();
            }

            @Override
            public void onTitleClick(View view) {

            }
        });


    }


    private void setUser() {
        if (successFlag) {

            Log.d("info", "adminName:" + adminName + " adminPwd:" + adminPwd);
            NativeCaller.PPPPUserSetting(strDID, visitorName, visitorPwd, operatorName, operatorPwd, adminName, adminPwd);
        } else {
            showToast(R.string.user_set_failed);
        }
    }

    private Runnable settingRunnable = new Runnable() {

        @Override
        public void run() {
            if (!successFlag) {
                showToast(R.string.user_set_failed);
            }
        }
    };

    /**
     * BridgeService Feedback execute
     **/
    public void CallBack_UserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {
        Log.d("info", " did:" + did + " user1:" + user1 + " pwd1:" + pwd1 + " user2:" + user2 + " pwd2:" + pwd2 + " user3:" + user3 + " pwd3:" + pwd3);
        adminName = user3;
        adminPwd = pwd3;
        mHandler.sendEmptyMessage(PARAMS);
    }

    /**
     * BridgeService Feedback execute
     **/
    public void CallBack_SetSystemParamsResult(int paramType, int result) {
        Log.d("info", "result:" + result + " paramType:" + paramType);
        mHandler.sendEmptyMessage(result);
    }

    /**
     * BridgeService Feedback execute
     **/
    public void setPPPPMsgNotifyData(String did, int type, int param) {
        if (strDID.equals(did)) {
            if (ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS == type) {
                CAMERAPARAM = param;
            }
        }
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();

            if (i == R.id.cbox_show_pwd) {
            if (mCboxShowPwd.isChecked()) {
                mEditPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                mEditPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }

        }

    }

    private class MyTextWatch implements TextWatcher {
        private int id;

        public MyTextWatch(int id) {
            this.id = id;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String result = s.toString();
            if (id == R.id.edit_name) {
                adminName = result;

            } else if (id == R.id.edit_pwd) {
                Log.i("info", "result:" + result);
                adminPwd = result;

            } else {
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int rid) {
        Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_LONG).show();
    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1,
                                   String user2, String pwd2, String user3, String pwd3) {
        // TODO Auto-generated method stub
        Log.e("用户信息", "管理员名称" + adminName + "管理员密码" + adminPwd);
        adminName = user3;
        adminPwd = pwd3;
        operatorName = user2;
        operatorPwd = pwd2;
        mHandler.sendEmptyMessage(PARAMS);
    }

    @Override
    public void callBackSetSystemParamsResult(String did, int paramType,
                                              int result) {
        // TODO Auto-generated method stub
        mHandler.sendEmptyMessage(result);
    }

    @Override
    public void callBackPPPPMsgNotifyData(String did, int type, int param) {
        // TODO Auto-generated method stub

    }

}
