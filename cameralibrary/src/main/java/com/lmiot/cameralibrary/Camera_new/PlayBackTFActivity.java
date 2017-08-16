package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lmiot.cameralibrary.Camera_new.BridgeService.PlayBackTFInterface;
import com.lmiot.cameralibrary.Camera_new.adapter.PlayBackAdapter;
import com.lmiot.cameralibrary.Camera_new.bean.PlayBackBean;
import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import vstc2.nativecaller.NativeCaller;

/**
 *
 * */
public class PlayBackTFActivity extends BaseActivity implements
        OnItemClickListener, PlayBackTFInterface, OnClickListener {


    private ProgressDialog progressDialog;
    private PlayBackAdapter mAdapter;
    private int TIMEOUT = 2000;
    private final int PARAMS = 1;
    private boolean successFlag = false;
    private String strName;
    private String strDID;
    public View loadMoreView;
    private Button loadMoreButton;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PARAMS:
                    successFlag = true;
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    mAdapter.notifyDataSetChanged();
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
                progressDialog.dismiss();
                if (mAdapter.getCount() > 0) {
                    Log.i("info", "Video");
                    mListview.setVisibility(View.VISIBLE);
                    mNoVideo.setVisibility(View.GONE);
                } else {
                    Log.i("info", "noVideo");
                    mNoVideo.setVisibility(View.VISIBLE);
                    mListview.setVisibility(View.GONE);

                }
            }
        }
    };

    private Handler handler = new Handler();
    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private TextView mNoVideo;
    private ListView mListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDataFromOther();
        setContentView(R.layout.playbacktf);
        findView();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.remote_video_getparams));
        progressDialog.show();
        mHandler.postDelayed(runnable, TIMEOUT);
        setListener();
        initDate();
        mAdapter = new PlayBackAdapter(PlayBackTFActivity.this, this);

        BridgeService.setPlayBackTFInterface(this);
        NativeCaller.PPPPGetSDCardRecordFileList(strDID, 0, 500);
        mListview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    private void initDate() {
        int byear = 0;
        int bmonth = 0;
        int bday = 0;

        Calendar calendar = Calendar.getInstance();
        int eyear = calendar.get(Calendar.YEAR);
        int emonth = calendar.get(Calendar.MONTH);
        int eday = calendar.get(Calendar.DAY_OF_MONTH);
        if (eday == 1) {
            Calendar ca2 = new GregorianCalendar(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) - 1, 1);
            byear = ca2.get(Calendar.YEAR);
            bmonth = ca2.get(Calendar.MONTH);
            bday = ca2.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            byear = eyear;
            bmonth = emonth;
            bday = eday - 1;
        }
        Calendar bca = new GregorianCalendar(byear, bmonth, bday);
        Calendar eca = new GregorianCalendar(eyear, emonth, eday);
        Date bdate = bca.getTime();
        Date edate = eca.getTime();
        bdate.getTime();
        edate.getTime();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String strDateBegin = f.format(bdate);
        String strDateEnd = f.format(edate);

    }

    private void getDataFromOther() {
        Intent intent = getIntent();
        strName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
        strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
        String strPwd = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
        String strUser = intent.getStringExtra(ContentCommon.STR_CAMERA_USER);
        Log.d("info", "PlayBackTFActivity  strName:" + strName + " strDID:"
                + strDID + " strPwd:" + strPwd + " strUser:" + strUser);
    }

    protected void onPause() {
        overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
        super.onPause();
    }

    private void setListener() {
        mListview.setOnItemClickListener(this);
        progressDialog.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }

        });
    }

    private void findView() {


        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mNoVideo = findViewById(R.id.no_video);
        mListview = findViewById(R.id.listview);

        mIvBack.setOnClickListener(this);
        mTvBack.setOnClickListener(this);


        mIdTitle.setText("远程录像");
        mTvModify.setVisibility(View.GONE);
        mIvAdd.setVisibility(View.GONE);

        loadMoreView = getLayoutInflater()
                .inflate(R.layout.loadmorecount, null);
        loadMoreButton = (Button) loadMoreView.findViewById(R.id.btn_load);
        loadMoreView.setVisibility(View.GONE);
        mListview.addFooterView(loadMoreView);
        loadMoreButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (fileTFCount - mAdapter.getCount() > 0) {
                    LoadMoreData();
                } else {
                    loadMoreView.setVisibility(View.GONE);
                }

            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        Log.d("playBackTFActivity...", "!!!!!!!!!!" + position);
        if (position < mAdapter.arrayList.size()) {
            PlayBackBean playBean = mAdapter.getPlayBean(position);
            String filepath = playBean.getPath();
            String mess = filepath.substring(0, 14);
            Intent intent = new Intent(this, PlayBackActivity.class);
            intent.putExtra("did", playBean.getDid());
            intent.putExtra("filepath", playBean.getPath());
            intent.putExtra("videotime", mess);
            Log.i("info", "filepath:" + filepath + "---mess:" + mess + "---");
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);//
        } else {
            if (fileTFCount - mAdapter.getCount() > 0) {

                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        LoadMoreData();
                    }
                }, 2000);
            } else {
                loadMoreView.setVisibility(View.GONE);
            }

        }

    }


    private int fileTFCount = 0;
    private int totalSize = 0;
    private int getCurrentPageIndex = 0;
    private int TotalPageSize = 0;

    public void LoadMoreData() {
        int count = mAdapter.getCount();
        if (count + 500 <= fileTFCount
                && getCurrentPageIndex + 1 <= TotalPageSize) {
            getCurrentPageIndex += 1;
            NativeCaller.PPPPGetSDCardRecordFileList(strDID,
                    getCurrentPageIndex, 500);
            mAdapter.notifyDataSetChanged();
            loadMoreButton.setText("获取更多视频文件...");
        } else {
            int filecount = fileTFCount - count;
            NativeCaller.PPPPGetSDCardRecordFileList(strDID,
                    getCurrentPageIndex++, filecount);
            mAdapter.notifyDataSetChanged();
            loadMoreButton.setText("已经加载完毕");
            loadMoreButton.setVisibility(View.GONE);
        }
    }

    /**
     * BridgeService callback
     **/


    public void callBackRecordFileSearchResult(String did, String filename,
                                               int size, int recordcount, int pagecount, int pageindex,
                                               int pagesize, int bEnd) {
        Log.d("info", "CallBack_RecordFileSearchResult did: " + did
                + " filename: " + filename + " size: " + size
                + " recordcount :" + recordcount + "pagecount: " + pagecount
                + "pageindex:" + pageindex + "pagesize: " + pagesize + "bEnd:"
                + bEnd);
        if (strDID.equals(did)) {
            fileTFCount = recordcount;
            getCurrentPageIndex = pageindex;
            totalSize = size;
            TotalPageSize = pagesize;
            PlayBackBean bean = new PlayBackBean();
            bean.setDid(did);
            bean.setPath(filename);
            mAdapter.addPlayBean(bean);
            if (TotalPageSize % 500 == 0) {

            }
            if (bEnd == 1) {
                mHandler.sendEmptyMessage(PARAMS);
            }
        }
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back || i == R.id.tv_back) {
            finish();

        }

    }
}



