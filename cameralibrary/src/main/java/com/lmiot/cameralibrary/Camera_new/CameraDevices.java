package com.lmiot.cameralibrary.Camera_new;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lmiot.cameralibrary.Camera_new.utils.SystemValue;
import com.lmiot.cameralibrary.R;
import com.lmiot.cameralibrary.SQL.CamerBean;
import com.lmiot.cameralibrary.SQL.SqlUtil;
import com.lmiot.cameralibrary.Util.DataUtil;
import com.lmiot.cameralibrary.Util.JumpActivityUtils;
import com.lmiot.cameralibrary.Util.LayoutDialogUtil;
import com.lmiot.tiblebarlibrary.LmiotTitleBar;
import java.util.ArrayList;
import java.util.List;


public class CameraDevices extends BaseActivity implements View.OnClickListener {


    private Intent mIntent;
    private myAdapter mMyAdapter;
    private List<CamerBean> mCameracontent;
    private Button mIdAddBt;
    private LinearLayout mIdAddLayout;
    private GridView mIdDeviceGridview;
    private LmiotTitleBar mLmiotTitleBar;
    private static onMoreItemListener onMoreItemListener;

    public static void setOnLongItemListener(onMoreItemListener onLongItemListener) {
        onMoreItemListener = onLongItemListener;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_camera_new);
        SqlUtil.getInstance().initDbHelp(this); //数据库初始化
        initView();

    }



    private void initView() {
        mLmiotTitleBar = findViewById(R.id.id_lmiot_title_bar);
        mIdAddBt = findViewById(R.id.id_add_bt);
        mIdAddLayout = findViewById(R.id.id_add_layout);
        mIdDeviceGridview = findViewById(R.id.id_device_gridview);

        mIdAddBt.setOnClickListener(this);


        mLmiotTitleBar.setOnItemClickListener(new LmiotTitleBar.onItemClickListener() {
            @Override
            public void onBackClick(View view) {
                finish();
            }

            @Override
            public void onMenuClick(View view) {
                AddDialog();
            }

            @Override
            public void onTitleClick(View view) {

            }
        });


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

        mCameracontent = SqlUtil.getInstance().searchAll();
        Log.d("CameraDevices", "mCameracontent.size():" + mCameracontent.size());


        Log.d("CameraDevices", new Gson().toJson(mCameracontent));
        ShowListView();


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

            if ( i==R.id.id_add_bt) {
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


             final CamerBean camerBean = mCameracontent.get(position);
            textView.setText(camerBean.getCameraName());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(camerBean.getPsRight()){  //已经存过密码
                        ConnectCameraData(camerBean.getCameraID(), camerBean.getCameraPassword());
                    }
                    else{
                        ShowPsDialog(camerBean);
                    }



                }
            });


            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    SetCameraDialog(camerBean);
                    return true;
                }
            });


            return v;
        }
    }

    /**
     * 输入密码对话框
     *
     * @param camerBean
     */
    private void ShowPsDialog(final CamerBean camerBean) {
        final Dialog dailog = LayoutDialogUtil.createDailog(CameraDevices.this, R.layout.dialog_change_edit_layout);
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
                ConnectCameraData(camerBean.getCameraID(), pass);
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
     * 长按设置摄像头
     */
    private void SetCameraDialog(final CamerBean camerBean) {

        final Dialog dialog = LayoutDialogUtil.createBottomDailog(CameraDevices.this, R.layout.dialog_change_fast_scene_layout);
        TextView bt01 = (TextView) dialog.findViewById(R.id.id_bt01);
        View line1 = dialog.findViewById(R.id.id_line1);
        View line2 = dialog.findViewById(R.id.id_line2);
        TextView bt02 = (TextView) dialog.findViewById(R.id.id_bt02);
        TextView bt03 = (TextView) dialog.findViewById(R.id.id_bt03);
        TextView cancel = (TextView) dialog.findViewById(R.id.id_cancel);
        bt01.setText(R.string.rename);
        bt02.setText(R.string.del_camera);

        String moreItem = DataUtil.getMoreItem();

        if(TextUtils.isEmpty(moreItem)){ //是否显示该菜单,空字符串则不显示，该菜单具体逻辑通过接口抛出处理
            bt03.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }
        else{
            bt03.setText(moreItem);
            bt03.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        }



        bt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RenameCamera(camerBean);


                dialog.dismiss();
            }
        });


        bt02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SqlUtil.getInstance().del(camerBean.getCameraID());
                GetCameraDevices();
                dialog.dismiss();
            }
        });

        bt03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMoreItemListener != null) {
                    onMoreItemListener.itemClick(view);
                }

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
     * @param camerBean
     */
    private void RenameCamera(final CamerBean camerBean) {

        final Dialog dailog0 = LayoutDialogUtil.createDailog(CameraDevices.this, R.layout.dialog_zone_layout);
        final TextView mtitle0 = (TextView) dailog0.findViewById(R.id.id_title);
        final EditText editText0 = (EditText) dailog0.findViewById(R.id.id_edit);
        Button cancel = (Button) dailog0.findViewById(R.id.id_cancel);
        final Button sure = (Button) dailog0.findViewById(R.id.id_sure);

        editText0.setText(camerBean.getCameraName());
        editText0.setSelection(editText0.getText().length());
        mtitle0.setText(R.string.rename);
        sure.setText(R.string.complete01);

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
                    camerBean.setCameraName(trim);
                    SqlUtil.getInstance().update(camerBean);
                    GetCameraDevices();

                    dailog0.dismiss();
                } else {
                    Toast.makeText(CameraDevices.this, getString(R.string.no_empty), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }





    /**
     * 连接摄像头
     */
    private void ConnectCameraData(String mCameraID, String mCameraPs) {
        SystemValue.deviceName = "admin";
        SystemValue.deviceId = mCameraID;
        SystemValue.devicePass = mCameraPs;
        JumpActivityUtils.JumpToActivity(CameraDevices.this, PlayActivity.class, false, true);


    }






    /**
     * 添加对话框
     */
    private void AddDialog() {
        mIdAddLayout.setVisibility(View.GONE);
        final Dialog dialog = LayoutDialogUtil.createBottomDailog(CameraDevices.this, R.layout.dialog_change_fast_scene_layout);
        TextView bt01 = (TextView) dialog.findViewById(R.id.id_bt01);
        View line1 = dialog.findViewById(R.id.id_line1);
        View line2 = dialog.findViewById(R.id.id_line1);
        TextView bt02 = (TextView) dialog.findViewById(R.id.id_bt02);
        TextView bt03 = (TextView) dialog.findViewById(R.id.id_bt03);
        TextView cancel = (TextView) dialog.findViewById(R.id.id_cancel);
        bt01.setText(R.string.add_by_wifi);
        bt02.setText(R.string.add_by_udp);
        bt03.setText(R.string.add_by_zxing);

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

    public  interface onMoreItemListener {
        void itemClick(View view);

    }


}