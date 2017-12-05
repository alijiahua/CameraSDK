package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lmiot.cameralibrary.Camera_new.BridgeService.Firmware;
import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.R;
import com.lmiot.cameralibrary.Util.DialogUtils;
import com.lmiot.cameralibrary.Util.ToastUtil;
import com.lmiot.tiblebarlibrary.LmiotTitleBar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import vstc2.nativecaller.NativeCaller;

public class FirmwareUpdateActiviy extends BaseActivity implements Firmware ,View.OnClickListener{



    private String did = null;
    private ProgressDialog progressDialog = null;
    private String LocalSysver = "noinfo";
    private String language;
    private boolean isGetSysData = false;
    private String download_server;
    private String filePath_sys;
    private String oemID;

    private boolean sys_isnew = false;

    private RelativeLayout service_sysver;

    //
    private Handler hander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    isGetSysData = true;
                    mSysver.setText(getString(R.string.now_version) +"："+ LocalSysver);
                    DialogUtils.ShowDialog(FirmwareUpdateActiviy.this, getString(R.string.getting_version));
                    getFirmware();
                    break;
            }
        }
    };

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (!isGetSysData) {
                isGetSysData = false;
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    };

    private Handler sysVerhander = new Handler() {
        public void handleMessage(Message msg) {

            DialogUtils.HiddenDialog();
            mVer = (String) msg.obj;
            Log.e("info", "sys:" + mVer);
            mServiceSysverText.setText(getString(R.string.new_version) + "："+mVer);

            if (mVer.equals(LocalSysver)) {
                mUpdate.setVisibility(View.GONE);
            } else {
                mUpdate.setVisibility(View.VISIBLE);
            }

        }

        ;
    };

    private Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case 1:
                    Log.i("info", "did:" + did + "download_server:"
                            + download_server + "filePath_sys:" + filePath_sys);
                    new AlertDialog.Builder(FirmwareUpdateActiviy.this)
                            .setTitle(R.string.check_has_update)
                            .setCancelable(false)
                            .setNegativeButton(R.string.no_update_now, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            sys_isnew = false;
                            NativeCaller.UpgradeFirmware(did, download_server, filePath_sys, 0);
                            Toast.makeText(getApplicationContext(), R.string.restarting, Toast.LENGTH_LONG).show();
                        }
                    }).show();

                    break;
                default:
                    break;

            }
        }

    };
    private String mVer = "";
    private TextView mSysver;
    private TextView mServiceSysverText;
    private TextView mUpdate;
    private LmiotTitleBar mLmiotTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_firmware_view);
        language = Locale.getDefault().getCountry();
        getDate();
        findView();
        showDiglog();
        hander.postDelayed(runnable, 5000);

        NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_STATUS);
        BridgeService.setFirmware(this);

    }

    private void getDate() {
        Intent intent = getIntent();
        did = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
    }

    private void findView() {

        mSysver = findViewById(R.id.sysver);
        mServiceSysverText = findViewById(R.id.service_sysver_text);
        mUpdate = findViewById(R.id.update);


        mUpdate.setOnClickListener(this);

        mLmiotTitleBar = findViewById(R.id.id_lmiot_title_bar);
        mLmiotTitleBar.setOnItemClickListener(new LmiotTitleBar.onItemClickListener() {
            @Override
            public void onBackClick(View view) {
                finish();
            }

            @Override
            public void onMenuClick(View view) {

            }

            @Override
            public void onTitleClick(View view) {

            }
        });

    }

    private void showDiglog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.getting_versioning));
        progressDialog.show();
    }


    //获取版本
    private void getFirmware() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        getFirmwareData getdata = new getFirmwareData();
        new Thread(getdata).start();
    }

    private String serverVer = null;


    @Override
    public void onClick(View view) {

        int i = view.getId();

            if (i == R.id.update) {
            if (download_server != null && filePath_sys != null) {
                if (download_server.length() == 0 || filePath_sys.length() == 0) {
                    ToastUtil.ToastMessage(this, getString(R.string.cannot_update));
                    return;
                }
                if (LocalSysver.equals(serverVer)) {
                    ToastUtil.ToastMessage(this, getString(R.string.no_need_update));
                    return;
                }
                updateHandler.sendEmptyMessage(1);
            } else {
                Toast.makeText(this, "", Toast.LENGTH_LONG).show();
            }


        }

    }

    /*
     * 获取固件版本线程
     */
    class getFirmwareData implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            String[] params = {"firmware", LocalSysver, language};
            String result = sendHttpFirmwareMessge("firmware", params);

            if (result == null || result.equals("")) {
                return;
            }

            try {
                JSONObject obj = new JSONObject(result);
                String ssver = obj.optString("name");
                if (ssver == null) {
                    ssver = "";
                }
                String filepath = obj.optString("download_file");
                if (filepath == null) {
                    filepath = "";
                }
                String downloadServer = obj.optString("download_server");
                if (downloadServer == null) {
                    downloadServer = "";
                }
                if (ssver.trim().length() == 0 || filepath.trim().length() == 0 || downloadServer.trim().length() == 0) {
                    return;
                } else {
                    Message msg = new Message();
                    msg.obj = ssver;
                    serverVer = ssver;
                    sysVerhander.sendMessage(msg);
                    download_server = downloadServer;
                    filePath_sys = filepath;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /*
     * http获取版当前版本本方法
     */
    public static String sendHttpFirmwareMessge(String MethodName, String... Parameters) {
        int len = Parameters.length;
        if (len == 0)
            return null;
        if (MethodName.length() == 0)
            return null;
        String uriString = "http://api4.eye4.cn:808";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append("/" + Parameters[i]);
        }
        uriString = uriString + sb.toString();
        Log.e("url", uriString);
        try {
            URL url = new URL(uriString);
            URI uri = new URI(url.getProtocol(), url.getHost() + ":808",
                    url.getPath(), url.getQuery(), null);

            HttpGet httpRequest = new HttpGet(uri);
            // 取得HttpClient 对象
            HttpClient httpclient = new DefaultHttpClient();
            // 请求httpClient ，取得HttpRestponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /* 取出响应字符串 */
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                if (strResult == null) {
                    return null;
                } else {
                    JSONObject obj = new JSONObject(strResult);
                    int ret = obj.optInt("ret");
                    int errcode = obj.optInt("errcode");
                    if (errcode == 333) {
                        return null;
                    } else {
                        return strResult;
                    }
                }
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 回调
     */
    @Override
    public void CallBack_UpdateFirmware(String uid, String sysver, String appver, String oemid) {
        // TODO Auto-generated method stub
        Log.i("info", "callback" + sysver + "==appver" + appver + "oemid" + oemid);
        LocalSysver = sysver;
        oemID = oemid;
        if (oemID == null || oemID.equals("")) {
            oemID = "OEM";
        }
        if (did.equalsIgnoreCase(uid)) {
            hander.sendEmptyMessage(1);
        }

    }

}
