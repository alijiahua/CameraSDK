package com.lmiot.cameralibrary.Camera_new;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lmiot.cameralibrary.Camera_new.bean.CameraRespone;
import com.lmiot.cameralibrary.Camera_new.bean.ResponedBean;
import com.lmiot.cameralibrary.Camera_new.utils.SystemValue;
import com.lmiot.cameralibrary.R;
import com.lmiot.cameralibrary.Util.DialogUtils;
import com.lmiot.cameralibrary.Util.JumpActivityUtils;
import com.lmiot.cameralibrary.Util.LayoutDialogUtil;
import com.lmiot.cameralibrary.Util.SPUtil;
import com.lmiot.cameralibrary.Util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;


public class CameraDevices extends BaseActivity implements View.OnClickListener {


    private Intent mIntent;
    private myAdapter mMyAdapter;
    private List<CameraRespone.ContentBean> mCameracontent;
    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private Button mIdAddBt;
    private LinearLayout mIdAddLayout;
    private GridView mIdDeviceGridview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_camera_new);
        String sessionID = SPUtil.getSessionID();
        if(TextUtils.isEmpty(SPUtil.getSessionID())){
            ToastUtil.ToastMessage(CameraDevices.this,"您还没设置sessionID!");
            finish();
        }



        initView();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initView() {


        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mIdAddBt = findViewById(R.id.id_add_bt);
        mIdAddLayout = findViewById(R.id.id_add_layout);
        mIdDeviceGridview = findViewById(R.id.id_device_gridview);

        mIvBack.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        mIdAddBt.setOnClickListener(this);



        mIdTitle.setText("云摄像头");
        mTvModify.setVisibility(View.GONE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        GetCameraDevices();

    }


    /**
     * 获取云端摄像头列表
     */
    private void GetCameraDevices() {

        try {
            String getCameraURL = SPUtil.AppendServerUrl("/devicethird/qrycameralist");
            OkHttpUtils.post()
                    .url(getCameraURL)
                    .addParams("sessionID", SPUtil.getSessionID())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(String response) {
                            Log.d("MainActivity", "摄像头数据:" + response);

                            resloveData(response);

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void resloveData(String response) {
        try {
            CameraRespone cameraRespone = new Gson().fromJson(response, CameraRespone.class);

            if (cameraRespone.getErrcode().equals("0")) {
                mCameracontent = cameraRespone.getContent();
                ShowListView();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示listview
     */
    private void ShowListView() {

        if (mCameracontent == null) {
            mCameracontent = new ArrayList<>();
        }

        if (mCameracontent.size() == 0) {
            mIdAddLayout.setVisibility(View.VISIBLE);
        } else {
            mIdAddLayout.setVisibility(View.GONE);
        }


        mMyAdapter = new myAdapter();
        mIdDeviceGridview.setAdapter(mMyAdapter);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back || i == R.id.tv_back) {
            finish();

        } else if (i == R.id.iv_add || i == R.id.id_add_bt) {
            AddDialog();

        }
    }


    /**
     * 摄像头适配器
     */
    public class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCameracontent.size();
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

            View v = View.inflate(CameraDevices.this, R.layout.layout_video_devicelist_res, null);
            TextView textView = (TextView) v.findViewById(R.id.video_devicename_id);
            TextView deviceState = (TextView) v.findViewById(R.id.video_connectstate_id);

            final String strName = mCameracontent.get(position).getStrName();
            String nickName = mCameracontent.get(position).getNickName();
            if (TextUtils.isEmpty(nickName)) {
                textView.setText(strName);
            } else {
                textView.setText(nickName);
            }


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String cameraPs = SPUtil.GetCameraPs(CameraDevices.this, strName);
                    Log.d("myAdapter", "保存的密码：" + cameraPs);

                    if (cameraPs == null) {
                        ShowPsDialog(strName);
                    } else {
                        ConnectCameraData(strName, cameraPs);


                    }

                }
            });


            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    SetCameraDialog(mCameracontent.get(position));
                    return true;
                }
            });


            return v;
        }
    }

    /**
     * 输入密码对话框
     *
     * @param strName
     */
    private void ShowPsDialog(final String strName) {
        final Dialog dailog = LayoutDialogUtil.createDailog(CameraDevices.this, R.layout.dialog_change_edit_layout);
        dailog.setCancelable(false);
        TextView title = (TextView) dailog.findViewById(R.id.id_title);
        title.setText("密码校验");
        final EditText edit = (EditText) dailog.findViewById(R.id.id_edit);
        edit.setHint("请输入摄像头密码");

        Button sure = (Button) dailog.findViewById(R.id.id_sure);
        Button cancel = (Button) dailog.findViewById(R.id.id_cancel);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edit.getText().toString();
                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(CameraDevices.this, R.string.no_empty, Toast.LENGTH_SHORT).show();
                } else {
                    ConnectCameraData(strName, pass);
                    dailog.dismiss();
                }

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
     * 长按设置摄像头
     */
    private void SetCameraDialog(final CameraRespone.ContentBean deviceInfo) {

        final Dialog dialog = LayoutDialogUtil.createBottomDailog(CameraDevices.this, R.layout.dialog_change_fast_scene_layout);
        TextView bt01 = (TextView) dialog.findViewById(R.id.id_bt01);
        View line1 = dialog.findViewById(R.id.id_line1);
        View line2 = dialog.findViewById(R.id.id_line2);
        TextView bt02 = (TextView) dialog.findViewById(R.id.id_bt02);
        TextView bt03 = (TextView) dialog.findViewById(R.id.id_bt03);
        TextView cancel = (TextView) dialog.findViewById(R.id.id_cancel);
        bt01.setText("重命名");
        bt02.setText("删除摄像头");

        bt03.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);


        bt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RenameCamera(deviceInfo.getStrName(), deviceInfo.getNickName());
                dialog.dismiss();
            }
        });


        bt02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleCameraData(deviceInfo.getNDevID() + "");
                SPUtil.SetCameraPs(CameraDevices.this, deviceInfo.getStrName(), null);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    /**
     * 重命名摄像头
     *
     * @param nickName
     */
    private void RenameCamera(final String strName, final String nickName) {

        final Dialog dailog0 = LayoutDialogUtil.createDailog(CameraDevices.this, R.layout.dialog_zone_layout);
        final TextView mtitle0 = (TextView) dailog0.findViewById(R.id.id_title);
        final EditText editText0 = (EditText) dailog0.findViewById(R.id.id_edit);
        Button cancel = (Button) dailog0.findViewById(R.id.id_cancel);
        final Button sure = (Button) dailog0.findViewById(R.id.id_sure);

        editText0.setText(nickName);
        editText0.setSelection(editText0.getText().length());
        mtitle0.setText("修改名称");
        sure.setText("完成");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailog0.dismiss();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = editText0.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    RenameDevice(strName, trim);

                    dailog0.dismiss();
                } else {
                    Toast.makeText(CameraDevices.this, "不能为空哦", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    /**
     * 重命名
     *
     * @param strName
     */
    private void RenameDevice(String strName, String newName) {

        DialogUtils.ShowDialog(CameraDevices.this);
        String url = SPUtil.AppendServerUrl("/devicethird/renamecamera");
        OkHttpUtils.post()
                .url(url)
                .addParams("sessionID", SPUtil.getSessionID())
                .addParams("strName", strName)
                .addParams("newName", newName)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.d("CameraDevices", "重命名错误：" + e.getMessage());
                        DialogUtils.HiddenDialog();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response) {
                        DialogUtils.HiddenDialog();
                        Log.d("CameraDevices", "重命名结果：" + response);
                        ResponedBean responedBean = new Gson().fromJson(response, ResponedBean.class);
                        if (responedBean.getErrcode().equals("0")) {
                            ToastUtil.ToastMessage(getApplicationContext(), "重命名成功！");
                            GetCameraDevices();
                        } else {
                            ToastUtil.ToastMessage(getApplicationContext(), "重命名失败：" + responedBean.getErrmsg());
                        }

                    }
                });


    }


    /**
     * 连接摄像头
     */
    private void ConnectCameraData(String mCameraID, String mCameraPs) {
        Log.d("CameraDevices", "连接服务器:" + mCameraID);
        SystemValue.deviceName = "admin";
        SystemValue.deviceId = mCameraID;
        SystemValue.devicePass = mCameraPs;

        JumpActivityUtils.JumpToActivity(CameraDevices.this, PlayActivity.class, false, true);


    }


    /**
     * 删除摄像头数据
     */
    private void DeleCameraData(String CameraID) {
        String del_URL = SPUtil.AppendServerUrl("/devicethird/delcamera");

        OkHttpUtils.post()
                .url(del_URL)
                .addParams("sessionID", SPUtil.getSessionID())
                .addParams("devID", CameraID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.d("SearchDevice", "删除结果：" + response);
                        ResponedBean responedBean = new Gson().fromJson(response, ResponedBean.class);
                        if (responedBean.getErrcode().equals("0")) {
                            Toast.makeText(CameraDevices.this, R.string.del_success, Toast.LENGTH_SHORT).show();
                            GetCameraDevices();
                        }

                    }
                });

    }




    /**
     * 添加对话框
     */
    private void AddDialog() {
        mIdAddLayout.setVisibility(View.GONE);
        SPUtil.SetCameraList(mCameracontent); //保存目前的已经添加的摄像头
        final Dialog dialog = LayoutDialogUtil.createBottomDailog(CameraDevices.this, R.layout.dialog_change_fast_scene_layout);
        TextView bt01 = (TextView) dialog.findViewById(R.id.id_bt01);
        View line1 = dialog.findViewById(R.id.id_line1);
        View line2 = dialog.findViewById(R.id.id_line1);
        TextView bt02 = (TextView) dialog.findViewById(R.id.id_bt02);
        TextView bt03 = (TextView) dialog.findViewById(R.id.id_bt03);
        TextView cancel = (TextView) dialog.findViewById(R.id.id_cancel);
        bt01.setText("WIFI智能快配(未联网)");
        bt02.setText("局域网扫描（已联网）");
        bt03.setText("二维码扫描（已联网）");

        bt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(CameraDevices.this, AddCameraActivity.class);
                mIntent.putExtra("addWay", "wifi");
                startActivity(mIntent);
                dialog.dismiss();
            }
        });

        bt02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(CameraDevices.this, AddCameraActivity.class);
                mIntent.putExtra("addWay", "udp");
                startActivity(mIntent);

                dialog.dismiss();
            }
        });
        bt03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(CameraDevices.this, AddCameraActivity.class);
                mIntent.putExtra("addWay", "zxing");
                startActivity(mIntent);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


}