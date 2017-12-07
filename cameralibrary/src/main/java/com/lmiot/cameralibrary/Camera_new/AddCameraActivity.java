package com.lmiot.cameralibrary.Camera_new;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.lmiot.androidtools_library.SDK.ZxingSdk;
import com.lmiot.cameralibrary.Camera_new.BridgeService.AddCameraInterface;
import com.lmiot.cameralibrary.Camera_new.BridgeService.CallBackMessageInterface;
import com.lmiot.cameralibrary.Camera_new.adapter.SearchListAdapter;
import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.Camera_new.utils.SystemValue;
import com.lmiot.cameralibrary.R;
import com.lmiot.cameralibrary.SQL.CamerBean;
import com.lmiot.cameralibrary.SQL.SqlUtil;
import com.lmiot.cameralibrary.Util.ApiUtls;
import com.lmiot.cameralibrary.Util.JumpActivityUtils;
import com.lmiot.cameralibrary.Util.ToastUtil;
import com.lmiot.cameralibrary.Util.WifiConnectionUtil;
import com.lmiot.tiblebarlibrary.LmiotTitleBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mediatek.android.IoTManager.IoTManagerNative;
import voice.encoder.DataEncoder;
import voice.encoder.VoicePlayer;
import vstc2.nativecaller.NativeCaller;


public class AddCameraActivity extends BaseActivity implements AddCameraInterface
        , CallBackMessageInterface ,OnClickListener{


    private static final int SEARCH_TIME = 3000;


    private SearchListAdapter listAdapter = null;
    private ProgressDialog progressdlg = null;
    private IoTManagerNative IoTManager;
    private WifiManager mWifiManager;
    private VoicePlayer player = new VoicePlayer();
    private boolean isSearched;
    private Intent intentbrod;
    private String mConnectedSsid = "";
    private String mConnectedPassword;
    private String mAuthString;
    private byte mAuthMode;
    private byte AuthModeOpen = 0x00;
    private byte AuthModeShared = 0x01;
    private byte AuthModeAutoSwitch = 0x02;
    private byte AuthModeWPA = 0x03;
    private byte AuthModeWPANone = 0x05;
    private byte AuthModeWPA2 = 0x06;
    private byte AuthModeWPA2PSK = 0x07;
    private byte AuthModeWPA1WPA2 = 0x08;
    private byte AuthModeWPA1PSKWPA2PSK = 0x09;
    private String sendMac = null;
    private String wifiName;
    private String currentBssid;




    private Map<String,String> mSearchList = new HashMap<>();
    private SearchAdapter mSearchAdapter;
    private WifiConnectionUtil mWifiConnectionUtil;
    private String mWifiName;
    private AlertDialog mShowDialog;
    private String mAddWay;
    private Intent mIntent;
    private String mCameraResult;
    private String mPasswordText = "";
    private EditText mIdCameraId;
    private EditText mIdCameraName;
    private EditText mIdCameraPs;
    private Button mIdCameraSave;
    private LinearLayout mIdZxingLayout;
    private ListView mIdAddDeviceListview;
    private TextView mIdRestarSearch;
    private RelativeLayout mIdSearchLayout;
    private LmiotTitleBar mLmiotTitleBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_camera);
        InitView();
        GetIntentData();
        ConnectService();

    }

    /**
     * 连接摄像头服务器：01
     */
    private void ConnectService() {
        Intent intent = new Intent();
        intent.setClass(AddCameraActivity.this, BridgeService.class);
        startService(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
                    Thread.sleep(500);
                } catch (Exception e) {

                }
            }
        }).start();
    }

    /**
     * 获取传递过来的数据
     */
    private void GetIntentData() {
        mAddWay = getIntent().getStringExtra("addWay");
        if (mAddWay != null) {

            switch (mAddWay) {
                case "wifi":  //wifi智能快配
                    StartWifiSearch();
                    break;
                case "udp": //局域网扫描
                    mIdSearchLayout.setVisibility(View.VISIBLE);
                    StartSearchUdp();
                    break;
                case "zxing": //二维码扫描
                    mIdRestarSearch.setVisibility(View.GONE);


                    ZxingSdk.startScan(AddCameraActivity.this, new ZxingSdk.onResultLitener() {
                        @Override
                        public void result(String result) {
                            mIntent = new Intent(AddCameraActivity.this, AddCameraActivity.class);
                            mIntent.putExtra("cameraResult", result);
                            startActivity(mIntent);
                            overridePendingTransition(R.anim.move_left_out_activity, R.anim.move_right_in_activity);
                        }
                    });

                    break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mCameraResult = getIntent().getStringExtra("cameraResult");
        if (mCameraResult != null) {
            mIdZxingLayout.setVisibility(View.VISIBLE);
            mIdCameraId.setText(mCameraResult);

        } else {
            mIdZxingLayout.setVisibility(View.GONE);
        }

    }


    /**
     * 开始wifi智能快配
     */
    private void StartWifiSearch() {
        getWifi();

        IoTManager = new IoTManagerNative();
        IoTManager.InitSmartConnection();

        mWifiConnectionUtil = new WifiConnectionUtil(AddCameraActivity.this);
        mWifiName = mWifiConnectionUtil.getWifiConnectedSsid();


        View v = View.inflate(AddCameraActivity.this, R.layout.camaer, null);

        TextView name = (TextView) v.findViewById(R.id.id_tv_wifi_ssid);
        final EditText mPassword = (EditText) v.findViewById(R.id.id_edit_wifi_pass);
        Button sure = (Button) v.findViewById(R.id.id_btn_search_device);
        TextView otherWifi = (TextView) v.findViewById(R.id.id_tv_use_other_wifi);
        name.setText(mWifiName);

        sure.setOnClickListener(new OnClickListener() {  //确定搜索
            @Override
            public void onClick(View v) {
                mPasswordText = mPassword.getText().toString();

                wifi_searchDevice(mPasswordText);
                mShowDialog.dismiss();
            }
        });

        otherWifi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
            }
        });


        mShowDialog = new AlertDialog.Builder(this)
                .setView(v)
                .show();


    }


    /**
     * 获取wifi信息
     */
    private void getWifi() {

        try {
            WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifiInfo = wifiMan.getConnectionInfo();

            wifiName = wifiInfo.getSSID().toString();
            if (wifiName.length() > 2 && wifiName.charAt(0) == '"'
                    && wifiName.charAt(wifiName.length() - 1) == '"') {
                wifiName = wifiName.substring(1, wifiName.length() - 1);
            }

            List<ScanResult> wifiList = wifiMan.getScanResults();
            ArrayList<String> mList = new ArrayList<String>();
            mList.clear();

            for (int i = 0; i < wifiList.size(); i++) {
                mList.add((wifiList.get(i).BSSID).toString());

            }

            currentBssid = wifiInfo.getBSSID();
            if (currentBssid == null) {
                for (int i = 0; i < wifiList.size(); i++) {
                    if ((wifiList.get(i).SSID).toString().equals(wifiName)) {
                        currentBssid = (wifiList.get(i).BSSID).toString();
                        break;
                    }
                }
            } else {
                if (currentBssid.equals("00:00:00:00:00:00")
                        || currentBssid.equals("")) {
                    for (int i = 0; i < wifiList.size(); i++) {
                        if ((wifiList.get(i).SSID).toString().equals(wifiName)) {
                            currentBssid = (wifiList.get(i).BSSID).toString();
                            break;
                        }
                    }
                }
            }
            if (currentBssid == null) {
                finish();
            }

            String tomacaddress[] = currentBssid.split(":");
            int currentLen = currentBssid.split(":").length;

            for (int m = currentLen - 1; m > -1; m--) {
                for (int j = mList.size() - 1; j > -1; j--) {
                    if (!currentBssid.equals(mList.get(j))) {
                        String array[] = mList.get(j).split(":");
                        if (!tomacaddress[m].equals(array[m])) {
                            mList.remove(j);//
                        }
                    }
                }
                if (mList.size() == 1 || mList.size() == 0) {
                    if (m == 5) {
                        sendMac = tomacaddress[m].toString();
                    } else if (m == 4) {
                        sendMac = tomacaddress[m].toString()
                                + tomacaddress[m + 1].toString();
                    } else {
                        sendMac = tomacaddress[5].toString()
                                + tomacaddress[4].toString()
                                + tomacaddress[3].toString();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 把wifi信息发给摄像头
     *
     * @param password
     */
    private void wifi_searchDevice(String password) {
        progressdlg.setMessage(getString(R.string.searching_tip));
        progressdlg.show();

        sendSonic(sendMac, password);
        setSmartLink();
        startSearch();

    }

    private void sendSonic(String mac, final String wifi) {
        byte[] midbytes = null;

        try {
            midbytes = HexString2Bytes(mac);
            printHexString(midbytes);
        } catch (Exception e) {
            Log.d("AddCameraActivity", "mac:" + e.getMessage());
            e.printStackTrace();
        }
        if (midbytes.length > 6) {
            Toast.makeText(AddCameraActivity.this, "no support",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] b = null;
        int num = 0;
        if (midbytes.length == 2) {
            b = new byte[]{midbytes[0], midbytes[1]};
            num = 2;
        } else if (midbytes.length == 3) {
            b = new byte[]{midbytes[0], midbytes[1], midbytes[2]};
            num = 3;
        } else if (midbytes.length == 4) {
            b = new byte[]{midbytes[0], midbytes[1], midbytes[2], midbytes[3]};
            num = 4;
        } else if (midbytes.length == 5) {
            b = new byte[]{midbytes[0], midbytes[1], midbytes[2],
                    midbytes[3], midbytes[4]};
            num = 5;
        } else if (midbytes.length == 6) {
            b = new byte[]{midbytes[0], midbytes[1], midbytes[2],
                    midbytes[3], midbytes[4], midbytes[5]};
            num = 6;
        } else if (midbytes.length == 1) {
            b = new byte[]{midbytes[0]};
            num = 1;
        }

        int a[] = new int[19];
        a[0] = 6500;
        int i, j;
        for (i = 0; i < 18; i++) {
            a[i + 1] = a[i] + 200;
        }

        player.setFreqs(a);

        player.play(DataEncoder.encodeMacWiFi(b, wifi.trim()), 5, 1000);

    }

    private static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    private static void printHexString(byte[] b) {
        // System.out.print(hint);
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print("aaa" + hex.toUpperCase() + " ");
        }
        System.out.println("");
    }


    private void setSmartLink() {
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            WifiInfo WifiInfo = mWifiManager.getConnectionInfo();
            mConnectedSsid = WifiInfo.getSSID();
            int iLen = mConnectedSsid.length();
            if (mConnectedSsid.startsWith("\"") && mConnectedSsid.endsWith("\"")) {
                mConnectedSsid = mConnectedSsid.substring(1, iLen - 1);
            }
            List<ScanResult> ScanResultlist = mWifiManager.getScanResults();

            for (int i = 0, len = ScanResultlist.size(); i < len; i++) {
                ScanResult AccessPoint = ScanResultlist.get(i);

                if (AccessPoint.SSID.equals(mConnectedSsid)) {
                    boolean WpaPsk = AccessPoint.capabilities.contains("WPA-PSK");
                    boolean Wpa2Psk = AccessPoint.capabilities.contains("WPA2-PSK");
                    boolean Wpa = AccessPoint.capabilities.contains("WPA-EAP");
                    boolean Wpa2 = AccessPoint.capabilities.contains("WPA2-EAP");

                    if (AccessPoint.capabilities.contains("WEP")) {
                        mAuthString = "OPEN-WEP";
                        mAuthMode = AuthModeOpen;
                        break;
                    }

                    if (WpaPsk && Wpa2Psk) {
                        mAuthString = "WPA-PSK WPA2-PSK";
                        mAuthMode = AuthModeWPA1PSKWPA2PSK;
                        break;
                    } else if (Wpa2Psk) {
                        mAuthString = "WPA2-PSK";
                        mAuthMode = AuthModeWPA2PSK;
                        break;
                    } else if (WpaPsk) {
                        mAuthString = "WPA-PSK";
                        byte authModeWPAPSK = 0x04;
                        mAuthMode = authModeWPAPSK;
                        break;
                    }

                    if (Wpa && Wpa2) {
                        mAuthString = "WPA-EAP WPA2-EAP";
                        mAuthMode = AuthModeWPA1WPA2;
                        break;
                    } else if (Wpa2) {
                        mAuthString = "WPA2-EAP";
                        mAuthMode = AuthModeWPA2;
                        break;
                    } else if (Wpa) {
                        mAuthString = "WPA-EAP";
                        mAuthMode = AuthModeWPA;
                        break;
                    }

                    mAuthString = "OPEN";
                    mAuthMode = AuthModeOpen;

                }
            }

        }
    }


    /**
     * 开始搜索局域网设备
     */
    private void StartSearchUdp() {
        stopCameraPPPP();
        startSearch();

    }


    @Override
    protected void onStop() {
        super.onStop();
        progressdlg.dismiss();
        NativeCaller.StopSearch();

    }


    /**
     * 初始化数据
     */
    private void InitView() {



        mIdCameraId = findViewById(R.id.id_camera_id);
        mIdCameraName = findViewById(R.id.id_camera_name);
        mIdCameraPs = findViewById(R.id.id_camera_ps);
        mIdCameraSave = findViewById(R.id.id_camera_save);
        mIdZxingLayout = findViewById(R.id.id_zxing_layout);
        mIdAddDeviceListview = findViewById(R.id.id_add_device_listview);
        mIdRestarSearch = findViewById(R.id.id_restar_search);
        mIdSearchLayout = findViewById(R.id.id_search_layout);

        mIdRestarSearch.setOnClickListener(this);
        mIdCameraSave.setOnClickListener(this);

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


        progressdlg = new ProgressDialog(this);
        progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdlg.setMessage(getString(R.string.searching_tip));
        listAdapter = new SearchListAdapter(this);
        BridgeService.setAddCameraInterface(this);
        BridgeService.setCallBackMessage(this);


    }



    /**
     * 添加二维码扫描的摄像头
     */
    private void SaveZxingCamera() {

            String cameraID = mIdCameraId.getText().toString();
            String cameraPs = mIdCameraPs.getText().toString();


            if (TextUtils.isEmpty(cameraID)) {
                ToastUtil.ToastMessage(AddCameraActivity.this, getString(R.string.no_empty));
                return;
            }

        SqlUtil.getInstance().add(new CamerBean(null,cameraID, ApiUtls.getInstance().getUerName(),cameraID,cameraPs,false));
        JumpActivityUtils.JumpToActivity(this,CameraDevices.class,true,true);


    }


    private void stopCameraPPPP() {
        NativeCaller.StopPPPP(SystemValue.deviceId);
    }


    Runnable updateThread = new Runnable() {

        public void run() {
            if (IoTManager != null) {
                IoTManager.StopSmartConnection();
            }

            NativeCaller.StopSearch();
            progressdlg.dismiss();
            mIdRestarSearch.setVisibility(View.VISIBLE);
            Message msg = updateListHandler.obtainMessage();
            msg.what = 1;
            updateListHandler.sendMessage(msg);
        }
    };

    Handler updateListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ShowSearchGridView(); //显示已经查找到的设备
                    break;
            }


        }
    };


    /**
     * 显示搜索到的设备
     */
    private void ShowSearchGridView() {

        List<CamerBean> camerBeanList=new ArrayList<>();
        for(Map.Entry<String, String> map:mSearchList.entrySet()){

            camerBeanList.add(new CamerBean(null,map.getKey(), ApiUtls.getInstance().getUerName(),map.getValue(),"",false));

        }

        mSearchAdapter = new SearchAdapter(camerBeanList);
        mIdAddDeviceListview.setAdapter(mSearchAdapter);

    }

    @Override
    public void onClick(View view) {

        int i = view.getId();

            if (i == R.id.id_restar_search) {
            startSearch();

        } else if (i == R.id.id_camera_save) {
            SaveZxingCamera();

        }

    }


    /**
     * 搜索设备适配器
     */

    private class SearchAdapter extends BaseAdapter {


        List<CamerBean> camerBeanList;

        public SearchAdapter(List<CamerBean> camerBeanList) {
            this.camerBeanList = camerBeanList;
        }

        @Override
        public int getCount() {
            return camerBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = View.inflate(AddCameraActivity.this, R.layout.item_ym_search_device, null);
            ImageView img = (ImageView) v.findViewById(R.id.id_device_img);
            final TextView name = (TextView) v.findViewById(R.id.id_name);
            ImageView add = (ImageView) v.findViewById(R.id.id_add);

            name.setText(camerBeanList.get(position).getCameraID());

            add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CamerBean search = SqlUtil.getInstance().search(camerBeanList.get(position).getCameraID());

                    if(search!=null){
                        ToastUtil.ToastMessage(AddCameraActivity.this,getString(R.string.has_added));
                        return;
                    }

                    SqlUtil.getInstance().add(new CamerBean(null,camerBeanList.get(position).getCameraID(), ApiUtls.getInstance().getUerName(),camerBeanList.get(position).getCameraName(),"",false));
                       finish();

                }
            });

            return v;
        }
    }




    public static String int2ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    private void startSearch() {
        try {
            listAdapter.ClearAll();
            progressdlg.setMessage(getString(R.string.searching_tip));
            progressdlg.setCancelable(false);
            progressdlg.setCanceledOnTouchOutside(true);
            progressdlg.show();

            mIdRestarSearch.setVisibility(View.INVISIBLE);

            if (!mConnectedSsid.equals("")) {
                IoTManager.StartSmartConnection(mConnectedSsid, mPasswordText.trim(), "FF:FF:FF:FF:FF:FF", (byte) mAuthMode);
            }

            new Thread(new SearchThread()).start();

            switch (mAddWay) {
                case "wifi":  //wifi智能快配,10秒后停止搜索
                    progressdlg.setCancelable(false);
                    updateListHandler.postDelayed(updateThread, 15000);
                    break;
                case "udp": //局域网扫描，15秒后停止搜索
                    progressdlg.setCancelable(false);
                    updateListHandler.postDelayed(updateThread, SEARCH_TIME);
                    break;
                case "zxing": //二维码扫描

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class SearchThread implements Runnable {
        @Override
        public void run() {
            Log.d("tag", "startSearch");
            NativeCaller.StartSearch();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
        }
    }

    /**
     * 搜索设备回调接口
     * BridgeService callback
     **/
    @Override
    public void callBackSearchResultData(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {

        mSearchList.put(strDeviceID,strDeviceID);

    }




    @Override
    public void CallBackGetStatus(String did, String resultPbuf, int cmd) {
        // TODO Auto-generated method stub
        if (cmd == ContentCommon.CGI_IEGET_STATUS) {
            String cameraType = spitValue(resultPbuf, "upnp_status=");
            int intType = Integer.parseInt(cameraType);
            int type14 = (int) (intType >> 16) & 1;// 14位 来判断是否报警联动摄像机
            if (intType == 2147483647) {// 特殊值
                type14 = 0;
            }

            if (type14 == 1) {


            }

        }
    }

    private String spitValue(String name, String tag) {
        String[] strs = name.split(";");
        for (int i = 0; i < strs.length; i++) {
            String str1 = strs[i].trim();
            if (str1.startsWith("var")) {
                str1 = str1.substring(4, str1.length());
            }
            if (str1.startsWith(tag)) {
                String result = str1.substring(str1.indexOf("=") + 1);
                return result;
            }
        }
        return -1 + "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (player != null) {
                player.stop();
            }
            if (IoTManager != null) {
                IoTManager.StopSmartConnection();
            }

            Intent intent = new Intent();
            intent.setClass(this, BridgeService.class);
            stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
