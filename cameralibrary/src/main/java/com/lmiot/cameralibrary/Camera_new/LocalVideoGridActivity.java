package com.lmiot.cameralibrary.Camera_new;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lmiot.cameralibrary.Camera_new.adapter.ShowLocPicGridViewAdapter;
import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vstc2.nativecaller.NativeCaller;

/**
 *
 * */
public class LocalVideoGridActivity extends BaseActivity implements
        OnItemClickListener, OnItemLongClickListener,View.OnClickListener {


    private String strDID;
    private String strDate;
    private ArrayList<String> aList;
    private ArrayList<String> videoTime;
    private ArrayList<Map<String, Object>> arrayList;
    private String strCameraName;
    private boolean isEditing = false;
    private int position = -1;
    private int seletNum;
    private ShowLocPicGridViewAdapter mAdapter;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mAdapter.notifyDataSetChanged();
        }
    };
    private LinearLayout DelBottomLayout;
    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private GridView mGridView1;
    private Button mSelectall;
    private Button mSelectreverse;
    private Button mDelete;
    private LinearLayout mDelBottomLayout;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDataFromOther();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showlocalpicgrid);
        findView();

        mAdapter = new ShowLocPicGridViewAdapter(this, strDID);
        mAdapter.setMode(2);// 
        mGridView1.setAdapter(mAdapter);
        mGridView1.setOnItemClickListener(this);
        mGridView1.setOnItemLongClickListener(this);
        initBmp();
    }

    private void initBmp() {
        Log.d("info", "LocalVideoGridActivity  initBmp:");
        new Thread() {
            public void run() {
                for (int i = 0; i < arrayList.size(); i++) {
                    Map<String, Object> map = arrayList.get(i);
                    String path = (String) map.get("path");
                    File file = new File(path);
                    FileInputStream in = null;
                    try {
                        in = new FileInputStream(file);
                        byte[] header = new byte[4];
                        in.read(header);
                        int fType = byteToInt(header);
                        Log.d("tag", "fType:" + fType);
                        switch (fType) {
                            case 1: {// h264
                                Log.d("tag", "h264");
                                byte[] sizebyte = new byte[4];
                                byte[] typebyte = new byte[4];
                                byte[] timebyte = new byte[4];
                                in.read(sizebyte);
                                in.read(typebyte);
                                in.read(timebyte);
                                int length = byteToInt(sizebyte);
                                int bIFrame = byteToInt(typebyte);
                                Log.d("tag", "bIFrame:" + bIFrame);
                                byte[] h264byte = new byte[length];
                                in.read(h264byte);
                                byte[] yuvbuff = new byte[720 * 1280 * 3 / 2];
                                int[] wAndh = new int[2];
                                int result = NativeCaller.DecodeH264Frame(h264byte,
                                        1, yuvbuff, length, wAndh);
                                if (result > 0) {
                                    Log.d("tag", "h264");
                                    int width = wAndh[0];
                                    int height = wAndh[1];
                                    Log.d("tag", "width:" + width + " height:"
                                            + height);
                                    byte[] rgb = new byte[width * height * 2];
                                    NativeCaller.YUV4202RGB565(yuvbuff, rgb, width,
                                            height);
                                    ByteBuffer buffer = ByteBuffer.wrap(rgb);
                                    Bitmap bitmap = Bitmap.createBitmap(width,
                                            height, Bitmap.Config.RGB_565);
                                    bitmap.copyPixelsFromBuffer(buffer);
                                    Matrix matrix = new Matrix();
                                    float scaleX = ((float) 140)
                                            / bitmap.getWidth();
                                    float scaleY = ((float) 120)
                                            / bitmap.getHeight();
                                    matrix.postScale(scaleX, scaleY);
                                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                            bitmap.getWidth(), bitmap.getHeight(),
                                            matrix, true);
                                    mAdapter.addBitmap(bitmap, path, 0);
                                    handler.sendEmptyMessage(1);
                                } else {
                                    Log.d("tag", "h264");
                                }
                            }
                            break;
                            case 2: {// jpg
                                byte[] lengthBytes = new byte[4];
                                byte[] timeBytes = new byte[4];
                                in.read(lengthBytes);
                                in.read(timeBytes);
                                int time = byteToInt(timeBytes);
                                int length = byteToInt(lengthBytes);
                                byte[] contentBytes = new byte[length];
                                in.read(contentBytes);
                                Bitmap btp = BitmapFactory.decodeByteArray(
                                        contentBytes, 0, contentBytes.length);
                                if (btp != null) {
                                    Matrix matrix = new Matrix();
                                    float scaleX = ((float) 140) / btp.getWidth();
                                    float scaleY = ((float) 120) / btp.getHeight();
                                    matrix.postScale(scaleX, scaleY);
                                    Bitmap bitmap = Bitmap.createBitmap(btp, 0, 0,
                                            btp.getWidth(), btp.getHeight(),
                                            matrix, true);
                                    mAdapter.addBitmap(bitmap, path, 0);
                                    handler.sendEmptyMessage(1);
                                } else {
                                    Bitmap bmp = BitmapFactory.decodeResource(
                                            getResources(), R.drawable.bad_video);
                                    Matrix matrix = new Matrix();
                                    float scaleX = ((float) 140) / bmp.getWidth();
                                    float scaleY = ((float) 120) / bmp.getHeight();
                                    matrix.postScale(scaleX, scaleY);
                                    Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0,
                                            bmp.getWidth(), bmp.getHeight(),
                                            matrix, true);
                                    mAdapter.addBitmap(bitmap, path, 1);
                                    handler.sendEmptyMessage(1);
                                }
                            }
                            default:
                                break;
                        }

                    } catch (Exception e) {
                        Log.d("tag",
                                "LocalViewGrid  initBmp:" + e.getMessage());
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                                in = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (arrayList.size() == 0) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isEditing) {
                seletNum = 0;
                isEditing = false;
                DelBottomLayout.setVisibility(View.GONE);
                ArrayList<Map<String, Object>> arrayPics = mAdapter
                        .getArrayPics();
                for (int i = 0; i < arrayPics.size(); i++) {
                    Map<String, Object> map = arrayPics.get(i);
                    map.put("status", 0);
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void findView() {


        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mGridView1 = findViewById(R.id.gridView1);
        mSelectall = findViewById(R.id.selectall);
        mSelectreverse = findViewById(R.id.selectreverse);
        mDelete = findViewById(R.id.delete);
        mDelBottomLayout = findViewById(R.id.del_bottom_layout);

        mIvBack.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
        mSelectall.setOnClickListener(this);
        mSelectreverse.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        mIvAdd.setVisibility(View.GONE);
        mTvModify.setVisibility(View.GONE);
        mIdTitle.setText(strDate + "/" + arrayList.size());
    }

    private void getDataFromOther() {
        Intent intent = getIntent();
        strDID = intent.getStringExtra("did");
        strDate = intent.getStringExtra("date");
        strCameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
        videoTime = (ArrayList<String>) intent.getSerializableExtra("videotime");
        aList = (ArrayList<String>) intent.getSerializableExtra("list");
        Log.i("info", "videoTime:" + videoTime);
        arrayList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < aList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            String path = aList.get(i);
            map.put("path", path);
            map.put("status", 0);
            arrayList.add(map);
        }
        aList.clear();
        aList = null;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {

        try {
            if (!isEditing) {
                if (this.position != position) {
                    this.position = -1;
                    Map<String, Object> map = arrayList.get(position);
                    String path = (String) map.get("path");
                    Intent intent = new Intent(this, ShowLocalVideoActivity.class);
                    intent.putExtra("did", strDID);
                    intent.putExtra("filepath", path);
                    intent.putExtra("arrayList", arrayList);
                    intent.putExtra("position", position);
                    intent.putExtra(ContentCommon.STR_CAMERA_NAME, strCameraName);
                    intent.putExtra("videotime", videoTime.get(position));
                    intent.putExtra("timeList", videoTime);
                    startActivityForResult(intent, 2);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                } else {
                    this.position = -1;
                }
            } else {
                if (this.position != position) {
                    this.position = -1;
                    ArrayList<Map<String, Object>> arrayPics = mAdapter
                            .getArrayPics();
                    Map<String, Object> map = arrayPics.get(position);
                    Map<String, Object> map2 = arrayList.get(position);
                    int status = (Integer) map.get("status");
                    if (status == 0) {
                        seletNum++;
                        map2.put("status", 1);
                        map.put("status", 1);
                    } else {
                        seletNum--;
                        map2.put("status", 0);
                        map.put("status", 0);
                    }
                    mAdapter.notifyDataSetChanged();
                    checkSelect();
                } else {
                    this.position = -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkSelect() {
        for (int i = 0; i < arrayList.size(); i++) {
            Map<String, Object> map = arrayList.get(i);
            int status = (Integer) map.get("status");
            if (status == 1) {
                return;
            }
        }
        DelBottomLayout.setVisibility(View.GONE);
        isEditing = false;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {
        this.position = position;
        DelBottomLayout.setVisibility(View.VISIBLE);
        isEditing = true;
        Log.d("tag", "onItemLongClick");
        ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
        Map<String, Object> map = arrayPics.get(position);
        Map<String, Object> map2 = arrayList.get(position);
        int status = (Integer) map.get("status");
        if (status == 0) {
            seletNum++;
            map2.put("status", 1);
            map.put("status", 1);
        } else {
            seletNum--;
            map.put("status", 0);
            map2.put("status", 0);
        }
        mAdapter.notifyDataSetChanged();
        checkSelect();
        return false;
    }

    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
        return b;
    }

    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arrayList.clear();
        arrayList = null;
    }


    @Override
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.iv_back || i1 == R.id.tv_back) {
            if (isEditing) {
                seletNum = 0;
                isEditing = false;
                DelBottomLayout.setVisibility(View.GONE);
                ArrayList<Map<String, Object>> arrayPics = mAdapter
                        .getArrayPics();
                for (int i = 0; i < arrayPics.size(); i++) {
                    Map<String, Object> map = arrayPics.get(i);
                    map.put("status", 0);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                finish();
            }

        } else if (i1 == R.id.selectall) {
            ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
            for (int i = 0; i < arrayPics.size(); i++) {
                Map<String, Object> map = arrayPics.get(i);
                Map<String, Object> map2 = arrayList.get(i);
                int status = (Integer) map.get("status");
                if (status != 1) {
                    map2.put("status", 1);
                    map.put("status", 1);
                }
            }
            seletNum = arrayPics.size();
            mAdapter.notifyDataSetChanged();

        } else if (i1 == R.id.selectreverse) {
            ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
            for (int i = 0; i < arrayPics.size(); i++) {
                Map<String, Object> map = arrayPics.get(i);
                Map<String, Object> map2 = arrayList.get(i);
                int status = (Integer) map.get("status");
                switch (status) {
                    case 0:
                        seletNum++;
                        map2.put("status", 1);
                        map.put("status", 1);
                        break;
                    case 1:
                        seletNum--;
                        map2.put("status", 0);
                        map.put("status", 0);
                        break;

                    default:
                        break;
                }
            }
            mAdapter.notifyDataSetChanged();

        } else if (i1 == R.id.delete) {
            Log.d("tag", "delete");
            seletNum = 0;
            ArrayList<Map<String, Object>> delPics = mAdapter.DelPics();
            Log.d("tag", "delPics.size:" + delPics.size());
            if (delPics.size() == 0) {
                isEditing = false;
                DelBottomLayout.setVisibility(View.GONE);
            } else {
                boolean flag = true;
                for (int i = 0; i < delPics.size() && flag; i++) {
                    Map<String, Object> map = delPics.get(i);
                    int status = (Integer) map.get("status");
                    if (status == 1) {
                        flag = false;
                    }
                }
                if (!flag) {
                    isEditing = false;
                    DelBottomLayout.setVisibility(View.GONE);
                }
            }
            mAdapter.notifyDataSetChanged();

        } else {
        }

    }
}