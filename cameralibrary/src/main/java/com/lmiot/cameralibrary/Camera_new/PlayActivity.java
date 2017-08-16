package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.lmiot.cameralibrary.Camera_new.BridgeService.PlayInterface;
import com.lmiot.cameralibrary.Camera_new.utils.AudioPlayer;
import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.Camera_new.utils.CustomAudioRecorder;
import com.lmiot.cameralibrary.Camera_new.utils.CustomAudioRecorder.AudioRecordResult;
import com.lmiot.cameralibrary.Camera_new.utils.CustomBuffer;
import com.lmiot.cameralibrary.Camera_new.utils.CustomBufferData;
import com.lmiot.cameralibrary.Camera_new.utils.CustomBufferHead;
import com.lmiot.cameralibrary.Camera_new.utils.CustomVideoRecord;
import com.lmiot.cameralibrary.Camera_new.utils.MyRender;
import com.lmiot.cameralibrary.Camera_new.utils.SystemValue;
import com.lmiot.cameralibrary.R;
import com.lmiot.cameralibrary.Util.DialogUtils;
import com.lmiot.cameralibrary.Util.LayoutDialogUtil;
import com.lmiot.cameralibrary.Util.SPUtil;
import com.lmiot.cameralibrary.Util.ToastUtil;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vstc2.nativecaller.NativeCaller;

public class PlayActivity extends BaseActivity implements BridgeService.IpcamClientInterface, OnTouchListener, OnGestureListener, OnClickListener, PlayInterface, AudioRecordResult {

    private static final String LOG_TAG = "PlayActivity";
    private static final int AUDIO_BUFFER_START_CODE = 0xff00ff;


    //视频数据
    private byte[] videodata = null;
    private int videoDataLen = 0;
    public int nVideoWidths = 0;
    public int nVideoHeights = 0;
    private int mTag = 0;

    private boolean bProgress = true;
    private GestureDetector gt = new GestureDetector(this);
    private final int BRIGHT = 1;//亮度标志
    private final int CONTRAST = 2;//对比度标志
    private final int IR_STATE = 14;//IR(夜视)开关
    private int nResolution = 0;//分辨率值
    private int nBrightness = 0;//亮度值
    private int nContrast = 0;//对比度

    private boolean bInitCameraParam = false;
    private boolean bManualExit = false;
    private boolean bDisplayFinished = true;
    private CustomBuffer AudioBuffer = null;
    private AudioPlayer audioPlayer = null;
    private boolean bAudioStart = false;

    private boolean isLeftRight = false;
    private boolean isUpDown = false;
    private Runnable mRunnable;
    private boolean isHorizontalMirror = false;
    private boolean isVerticalMirror = false;
    private boolean isUpDownPressed = false;
    private boolean isShowtoping = false;
    private int nStreamCodecType;//分辨率格式

    private Intent mIntent;


    private PopupWindow controlWindow;//设备方向控制提示控件
    private PopupWindow presetBitWindow;//预置位面板
    //上下左右提示文本
    private TextView control_item;
    //正在控制设备
    private boolean isControlDevice = false;

    private String stqvga = "qvga";
    private String stvga = "vga";
    private String stqvga1 = "qvga1";
    private String stvga1 = "vga1";
    private String stp720 = "p720";
    private String sthigh = "high";
    private String stmiddle = "middle";
    private String stmax = "max";

    //预位置设置
    private Button[] btnLeft = new Button[16];
    private Button[] btnRigth = new Button[16];
    private ViewPager prePager;
    private List<View> listViews;
    //分辨率标识符
    private boolean ismax = false;
    private boolean ishigh = false;
    private boolean isp720 = false;
    private boolean ismiddle = false;
    private boolean isqvga1 = false;
    private boolean isvga1 = false;
    private boolean isqvga = false;
    private boolean isvga = false;

    private boolean isTakepic = false;
    private boolean isPictSave = false;
    private boolean isTalking = false;//是否在说话
    private boolean isMcriophone = false;//是否在
    //视频录像方法
    private CustomVideoRecord myvideoRecorder;
    public boolean isH264 = false;//是否是H264格式标志
    public boolean isJpeg = false;
    private boolean isTakeVideo = false;
    private long videotime = 0;// 录每张图片的时间

    private int timeTag = 0;
    private int timeOne = 0;
    private int timeTwo = 0;
    private boolean bAudioRecordStart = false;
    //送话器
    private CustomAudioRecorder customAudioRecorder;

    private MyRender myRender;

    //镜像标志
    private boolean m_bUpDownMirror;
    private boolean m_bLeftRightMirror;


    private static final String STR_MSG_PARAM = "msgparam";
    private static final String STR_DID = "did";
    private Intent intentbrod = null;

    private int i = 0;//拍照张数标志
    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private CheckBox mIrSwitch;
    private ImageView mPtzHoriMirror;
    private ImageView mPtzVertMirror;
    private ImageView mPtzBrightness;
    private ImageView mPtzContrast;
    private ImageView mPtzDefaultSet;
    private PercentRelativeLayout mTopBg;
    private GLSurfaceView mMysurfaceview;
    private CheckBox mPtzAudio;
    private RelativeLayout mPtzAudioLayout;
    private ImageView mPtzScreen;
    private CheckBox mPtzTalk;
    private RelativeLayout mPtzTalkLayout;
    private ImageView mIdCentrol;
    private ImageView mIdDown;
    private ImageView mIdRight;
    private ImageView mIdUp;
    private ImageView mIdLeft;
    private TextView mIdVideoing;
    private ImageView mPtzTakePhotos;
    private ImageView mPtzTakeVideos;
    private ImageView mPtzResolution;
    private ImageView mPreset;
    private PercentLinearLayout mIdMain;

    /****
     * 退出确定dialog
     */
    public void showSureDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.app);
        builder.setTitle("退出摄像头界面");
        builder.setMessage(R.string.exit_alert);
        builder.setPositiveButton(R.string.str_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Process.killProcess(Process.myPid());
                        Intent intent = new Intent("finish");
                        sendBroadcast(intent);
                        PlayActivity.this.finish();
                    }
                });
        builder.setNegativeButton(R.string.str_cancel, null);
        builder.show();
    }


    private Handler deviceParamsHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    m_bUpDownMirror = false;
                    m_bLeftRightMirror = false;

                    mPtzVertMirror.setImageResource(R.drawable.ptz_vert_mirror);
                    mPtzHoriMirror.setImageResource(R.drawable.ptz_hori_mirror);

                    break;
                case 1:
                    m_bUpDownMirror = true;
                    m_bLeftRightMirror = false;

                    mPtzHoriMirror.setImageResource(R.drawable.ptz_hori_mirror);
                    mPtzVertMirror.setImageResource(R.drawable.ptz_vert_mirror_press);

                    break;
                case 2:
                    m_bUpDownMirror = false;
                    m_bLeftRightMirror = true;
                    mPtzHoriMirror.setImageResource(R.drawable.ptz_hori_mirror_press);
                    mPtzVertMirror.setImageResource(R.drawable.ptz_vert_mirror);
                    break;
                case 3:
                    m_bUpDownMirror = true;
                    m_bLeftRightMirror = true;
                    mPtzVertMirror.setImageResource(R.drawable.ptz_vert_mirror_press);
                    mPtzHoriMirror.setImageResource(R.drawable.ptz_hori_mirror_press);
                    break;

                case 0x1589:
                    String state = (String) msg.obj;
                    break;
                default:
                    break;
            }
        }
    };


    //默认视频参数
    private void defaultVideoParams() {
        nBrightness = 1;
        nContrast = 128;
        NativeCaller.PPPPCameraControl(SystemValue.deviceId, 1, 0);
        NativeCaller.PPPPCameraControl(SystemValue.deviceId, 2, 128);
        showToast(R.string.ptz_default_vedio_params);
    }



    //设置视频可见
    private void setViewVisible() {
        if (bProgress) {
            bProgress = false;
            DialogUtils.HiddenDialog();
            getCameraParams();
        }
    }

    int disPlaywidth;
    private Bitmap mBmp;
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1 || msg.what == 2) {
                setViewVisible();
            }
            if (!isPTZPrompt) {
                isPTZPrompt = true;
            }
            int width = getWindowManager().getDefaultDisplay().getWidth();
            int height = getWindowManager().getDefaultDisplay().getHeight();

            switch (msg.what) {


                case 1: // h264
                {
                    if (reslutionlist.size() == 0) {
                        if (nResolution == 0) {
                            ismax = true;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stmax, ismax);
                        } else if (nResolution == 1) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = true;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(sthigh, ishigh);
                        } else if (nResolution == 2) {
                            ismax = false;
                            ismiddle = true;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stmiddle, ismiddle);
                        } else if (nResolution == 3) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = true;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stp720, isp720);
                            nResolution = 3;
                        } else if (nResolution == 4) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = true;
                            addReslution(stvga1, isvga1);
                        } else if (nResolution == 5) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = true;
                            isvga1 = false;
                            addReslution(stqvga1, isqvga1);
                        }
                    } else {
                        if (reslutionlist.containsKey(SystemValue.deviceId)) {
                            getReslution();
                        } else {
                            if (nResolution == 0) {
                                ismax = true;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stmax, ismax);
                            } else if (nResolution == 1) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = true;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(sthigh, ishigh);
                            } else if (nResolution == 2) {
                                ismax = false;
                                ismiddle = true;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stmiddle, ismiddle);
                            } else if (nResolution == 3) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = true;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stp720, isp720);
                                nResolution = 3;
                            } else if (nResolution == 4) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = true;
                                addReslution(stvga1, isvga1);
                            } else if (nResolution == 5) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = true;
                                isvga1 = false;
                                addReslution(stqvga1, isqvga1);
                            }
                        }

                    }

                    myRender.writeSample(videodata, nVideoWidths, nVideoHeights);
                }
                break;
                case 2: // JPEG
                {
                    if (reslutionlist.size() == 0) {
                        if (nResolution == 1) {
                            isvga = true;
                            isqvga = false;
                            addReslution(stvga, isvga);
                        } else if (nResolution == 0) {
                            isqvga = true;
                            isvga = false;
                            addReslution(stqvga, isqvga);
                        }
                    } else {
                        if (reslutionlist.containsKey(SystemValue.deviceId)) {
                            getReslution();
                        } else {
                            if (nResolution == 1) {
                                isvga = true;
                                isqvga = false;
                                addReslution(stvga, isvga);
                            } else if (nResolution == 0) {
                                isqvga = true;
                                isvga = false;
                                addReslution(stqvga, isqvga);
                            }
                        }
                    }
                    mBmp = BitmapFactory.decodeByteArray(videodata, 0,
                            videoDataLen);
                    if (mBmp == null) {
                        bDisplayFinished = true;
                        return;
                    }
                    if (isTakepic) {
                        takePicture(mBmp);
                        isTakepic = false;
                    }
                    nVideoWidths = mBmp.getWidth();
                    nVideoHeights = mBmp.getHeight();


                }
                break;
                default:
                    break;
            }
            if (msg.what == 1 || msg.what == 2) {
                bDisplayFinished = true;
            }
        }

    };

    private void getCameraParams() {

        NativeCaller.PPPPGetSystemParams(SystemValue.deviceId,
                ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
    }

    private Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d("tag", "断线了");
                Toast.makeText(getApplicationContext(),
                        R.string.pppp_status_disconnect, Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     /*   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);*/

        setContentView(R.layout.play);
        ConnectService();
        initData();
        ResumeData();

    }

    private void initData() {
        disPlaywidth = getWindowManager().getDefaultDisplay().getWidth();
        findView();
        AudioBuffer = new CustomBuffer();
        audioPlayer = new AudioPlayer(AudioBuffer);
        customAudioRecorder = new CustomAudioRecorder(this);
        myvideoRecorder = new CustomVideoRecord(this, SystemValue.deviceId);
        BridgeService.setPlayInterface(this);
        myRender = new MyRender(mMysurfaceview);
        mMysurfaceview.setRenderer(myRender);
    }

    private void VideoMethod() {

        NativeCaller.StartPPPPLivestream(SystemValue.deviceId, 10, 1);//确保不能重复start
        getCameraParams();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void ResumeData() {

        ConnectCameraData();


    }


    /**
     * 连接摄像头
     */
    private void ConnectCameraData() {
        mTag = 0;
        BridgeService.setIpcamClientInterface(this);
        NativeCaller.Init();
        new Thread(new StartPPPPThread()).start();

    }


    /**
     * 连接摄像头服务器：01
     */
    private void ConnectService() {
        Intent intent = new Intent();
        intent.setClass(PlayActivity.this, BridgeService.class);
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








    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!bProgress) {
                Date date = new Date();
                if (timeTag == 0) {
                    timeOne = date.getSeconds();
                    timeTag = 1;
                    showToast(R.string.main_show_back);
                } else if (timeTag == 1) {
                    timeTwo = date.getSeconds();
                    if (timeTwo - timeOne <= 3) {
                        Intent intent = new Intent("finish");
                        sendBroadcast(intent);
                        PlayActivity.this.finish();
                        timeTag = 0;
                    } else {
                        timeTag = 1;
                        showToast(R.string.main_show_back);
                    }
                }
            } else {
                showSureDialog();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!bProgress) {
                showBottom();
            } else {
                showSureDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void setResolution(int Resolution) {
        Log.d("tag", "setResolution resolution:" + Resolution);
        NativeCaller.PPPPCameraControl(SystemValue.deviceId, 16, Resolution);
    }

    private void findView() {


        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mIrSwitch = findViewById(R.id.ir_switch);
        mPtzHoriMirror = findViewById(R.id.ptz_hori_mirror);
        mPtzVertMirror = findViewById(R.id.ptz_vert_mirror);
        mPtzBrightness = findViewById(R.id.ptz_brightness);
        mPtzContrast = findViewById(R.id.ptz_contrast);
        mPtzDefaultSet = findViewById(R.id.ptz_default_set);
        mTopBg = findViewById(R.id.top_bg);
        mMysurfaceview = findViewById(R.id.mysurfaceview);
        mPtzAudio = findViewById(R.id.ptz_audio);
        mPtzAudioLayout = findViewById(R.id.ptz_audio_layout);
        mPtzScreen = findViewById(R.id.ptz_screen);
        mPtzTalk = findViewById(R.id.ptz_talk);
        mPtzTalkLayout = findViewById(R.id.ptz_talk_layout);
        mIdCentrol = findViewById(R.id.id_centrol);
        mIdDown = findViewById(R.id.id_down);
        mIdRight = findViewById(R.id.id_right);
        mIdUp = findViewById(R.id.id_up);
        mIdLeft = findViewById(R.id.id_left);
        mIdVideoing = findViewById(R.id.id_videoing);
        mPtzTakePhotos = findViewById(R.id.ptz_take_photos);
        mPtzTakeVideos = findViewById(R.id.ptz_take_videos);
        mPtzResolution = findViewById(R.id.ptz_resolution);
        mPreset = findViewById(R.id.preset);
        mIdMain = findViewById(R.id.id_main);



        mIvBack.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
        mPtzAudio.setOnClickListener(this);
        mPtzTalk.setOnClickListener(this);
        mPtzHoriMirror.setOnClickListener(this);
        mPtzVertMirror.setOnClickListener(this);
        mPtzTakeVideos.setOnClickListener(this);
        mPtzTakePhotos.setOnClickListener(this);
        mPtzScreen.setOnClickListener(this);
        mPtzBrightness.setOnClickListener(this);
        mPtzContrast.setOnClickListener(this);
        mPtzResolution.setOnClickListener(this);
        mPreset.setOnClickListener(this);
        mPtzDefaultSet.setOnClickListener(this);
        mIrSwitch.setOnClickListener(this);
        mIdUp.setOnClickListener(this);
        mIdDown.setOnClickListener(this);
        mIdRight.setOnClickListener(this);
        mIdLeft.setOnClickListener(this);


                DialogUtils.ShowDialog(PlayActivity.this);
        //方向控制提示框
        initControlDailog();
        //视频渲染画面控件
        mMysurfaceview.setOnTouchListener(this);
        mMysurfaceview.setLongClickable(true);//确保手势识别正确工作


        //显示设备名称
        mIdTitle.setText(SystemValue.deviceId + "");
        mTvModify.setVisibility(View.GONE);
        mIvAdd.setImageResource(R.drawable.home_setting_white);

    }

    private boolean isDown = false;
    private boolean isSecondDown = false;
    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (!isDown) {
            x1 = event.getX();
            y1 = event.getY();
            isDown = true;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                originalScale = getScale();
                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs((x1 - x2)) < 25 && Math.abs((y1 - y2)) < 25) {


                    if (!isSecondDown) {
                        if (!bProgress) {
                            showBottom();
                        }
                    }
                    isSecondDown = false;
                }
                x1 = 0;
                x2 = 0;
                y1 = 0;
                y2 = 0;
                isDown = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                isSecondDown = true;
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();

                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 0f) {

                    }
                }
        }

        return gt.onTouchEvent(event);
    }

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private int mode = NONE;
    private float oldDist;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float mMaxZoom = 2.0f;
    float mMinZoom = 0.3125f;
    float originalScale;
    float baseValue;
    protected Matrix mBaseMatrix = new Matrix();
    protected Matrix mSuppMatrix = new Matrix();
    private Matrix mDisplayMatrix = new Matrix();
    private final float[] mMatrixValues = new float[9];

    protected void zoomTo(float scale, float centerX, float centerY) {
        Log.d("zoomTo", "zoomTo scale:" + scale);
        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        } else if (scale < mMinZoom) {
            scale = mMinZoom;
        }

        float oldScale = getScale();
        float deltaScale = scale / oldScale;
        Log.d("deltaScale", "deltaScale:" + deltaScale);
        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
    }

    protected Matrix getImageViewMatrix() {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }

    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    protected float getScale() {
        return getScale(mSuppMatrix);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private float spacing(MotionEvent event) {
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            //return FloatMath.sqrt(x * x + y * y);
            return (float) Math.sqrt(x * x + y * y);
        } catch (Exception e) {
        }
        return 0;
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("tag", "onDown");

        return false;
    }

    private final int MINLEN = 80;//最小间距
    private boolean isPTZPrompt;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        float x1 = e1.getX();
        float x2 = e2.getX();
        float y1 = e1.getY();
        float y2 = e2.getY();

        float xx = x1 > x2 ? x1 - x2 : x2 - x1;
        float yy = y1 > y2 ? y1 - y2 : y2 - y1;

        if (xx > yy) {
            if ((x1 > x2) && (xx > MINLEN)) {// right
                if (!isControlDevice)
                    new ControlDeviceTask(ContentCommon.CMD_PTZ_RIGHT).execute();

            } else if ((x1 < x2) && (xx > MINLEN)) {// left
                if (!isControlDevice)
                    new ControlDeviceTask(ContentCommon.CMD_PTZ_LEFT).execute();
            }

        } else {
            if ((y1 > y2) && (yy > MINLEN)) {// down
                if (!isControlDevice)
                    new ControlDeviceTask(ContentCommon.CMD_PTZ_DOWN).execute();
            } else if ((y1 < y2) && (yy > MINLEN)) {// up
                if (!isControlDevice)
                    new ControlDeviceTask(ContentCommon.CMD_PTZ_UP).execute();
            }

        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }


    private void showBottom() {
        if (isUpDownPressed) {
            isUpDownPressed = false;
        } else {
            isUpDownPressed = true;
        }
    }


    /**
     *
     */
    private void ShowVideoDialog() {
        final Dialog dialog = LayoutDialogUtil.createBottomDailog(PlayActivity.this, R.layout.dialog_change_fast_scene_layout);
        TextView bt01 = (TextView) dialog.findViewById(R.id.id_bt01);
        View line1 = dialog.findViewById(R.id.id_line1);
        View line2 = dialog.findViewById(R.id.id_line1);
        TextView bt02 = (TextView) dialog.findViewById(R.id.id_bt02);
        TextView bt03 = (TextView) dialog.findViewById(R.id.id_bt03);
        TextView cancel = (TextView) dialog.findViewById(R.id.id_cancel);
        bt01.setVisibility(View.GONE);
        line1.setVisibility(View.GONE);

        bt02.setText("查看本地录像");
        bt03.setText("查看远程录像");


        bt02.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

        /*      mIntent = new Intent(PlayActivity.this, CameraVideoActivity.class);
                mIntent.putExtra("CameraID", SystemValue.deviceId);
                startActivity(mIntent);
                overridePendingTransition(R.anim.move_left_out_activity, R.anim.move_right_in_activity);*/

                mIntent = new Intent(PlayActivity.this, LocalPictureAndVideoActivity.class);
                mIntent.putExtra(ContentCommon.STR_CAMERA_ID,
                        SystemValue.deviceId);
                mIntent.putExtra(ContentCommon.STR_CAMERA_NAME,
                        SystemValue.deviceName);
                mIntent.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
                startActivity(mIntent);


                dialog.dismiss();
            }
        });

        bt03.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTag == 1) {
                    mIntent = new Intent(PlayActivity.this, PlayBackTFActivity.class);
                    mIntent.putExtra(ContentCommon.STR_CAMERA_ID, SystemValue.deviceId);
                    mIntent.putExtra(ContentCommon.STR_CAMERA_NAME, SystemValue.deviceName);
                    mIntent.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
                    startActivity(mIntent);
                    overridePendingTransition(R.anim.move_left_out_activity, R.anim.move_right_in_activity);
                } else {
                    ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
                }


                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.iv_back || i1 == R.id.tv_back) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {  //横屏时点击返回
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mTopBg.setVisibility(View.VISIBLE);
                mIdMain.setVisibility(View.VISIBLE);
                mTvBack.setVisibility(View.VISIBLE);
                mIdTitle.setVisibility(View.VISIBLE);
                mIvAdd.setVisibility(View.VISIBLE);

            } else { //竖屏时点击返回

                finish();

            }


        } else if (i1 == R.id.iv_add) {
            if (mTag == 1) {
                mIntent = new Intent(PlayActivity.this, SettingActivity.class);
                mIntent.putExtra(ContentCommon.STR_CAMERA_ID, SystemValue.deviceId);
                mIntent.putExtra(ContentCommon.STR_CAMERA_NAME, SystemValue.deviceName);
                mIntent.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
                startActivity(mIntent);
                overridePendingTransition(R.anim.move_left_out_activity, R.anim.move_right_in_activity);
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_audio) {
            if (mTag == 1) {
                goAudio();
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_talk) {
            if (mTag == 1) {
                goMicroPhone();
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_hori_mirror) {
            if (mTag == 1) {
                int value1;

                if (m_bLeftRightMirror) {

                    mPtzHoriMirror.setImageResource(R.drawable.ptz_hori_mirror);
                    if (m_bUpDownMirror) {
                        value1 = 1;
                    } else {
                        value1 = 0;
                    }
                } else {
                    mPtzHoriMirror.setImageResource(R.drawable.ptz_hori_mirror_press);
                    if (m_bUpDownMirror) {
                        value1 = 3;
                    } else {
                        value1 = 2;
                    }
                }

                NativeCaller.PPPPCameraControl(SystemValue.deviceId, 5, value1);
                m_bLeftRightMirror = !m_bLeftRightMirror;
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_vert_mirror) {
            if (mTag == 1) {
                int value;
                if (m_bUpDownMirror) {
                    mPtzVertMirror.setImageResource(R.drawable.ptz_vert_mirror);
                    if (m_bLeftRightMirror) {
                        value = 2;
                    } else {
                        value = 0;
                    }
                } else {
                    mPtzVertMirror.setImageResource(R.drawable.ptz_vert_mirror_press);
                    if (m_bLeftRightMirror) {
                        value = 3;
                    } else {
                        value = 1;
                    }
                }
                NativeCaller.PPPPCameraControl(SystemValue.deviceId, 5, value);
                m_bUpDownMirror = !m_bUpDownMirror;
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_take_videos) {
            if (mTag == 1) {
                goTakeVideo();
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_take_photos) {
            if (mTag == 1) {
                if (existSdcard()) {// 判断sd卡是否存在
                    //takePicture(mBmp);
                    isTakepic = true;
                } else {
                    showToast(R.string.ptz_takepic_save_fail);
                }
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_screen) {
            if (mTag == 1) {
                SetScreen();


            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_brightness) {
            if (mTag == 1) {
                setBrightOrContrast(BRIGHT);
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_contrast) {
            if (mTag == 1) {
                setBrightOrContrast(CONTRAST);
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ptz_resolution) {
            mIntent = new Intent(PlayActivity.this, CameraPictureActivity.class);
            mIntent.putExtra("CameraID", SystemValue.deviceId);
            startActivity(mIntent);
            overridePendingTransition(R.anim.move_left_out_activity, R.anim.move_right_in_activity);


        } else if (i1 == R.id.preset) {
            ShowVideoDialog();

        } else if (i1 == R.id.ptz_default_set) {
            if (mTag == 1) {
                defaultVideoParams();
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.ir_switch) {
            if (mTag == 1) {
                if (mIrSwitch.isChecked()) {
                    NativeCaller.PPPPCameraControl(SystemValue.deviceId, IR_STATE, 1);
                    Toast.makeText(this, "IR开", Toast.LENGTH_SHORT).show();

                } else {
                    NativeCaller.PPPPCameraControl(SystemValue.deviceId, IR_STATE, 0);
                    Toast.makeText(this, "IR关", Toast.LENGTH_SHORT).show();
                }
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }


        } else if (i1 == R.id.id_up) {
            if (mTag == 1) {
                new ControlDeviceTask(ContentCommon.CMD_PTZ_UP).execute();
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }

        } else if (i1 == R.id.id_down) {
            if (mTag == 1) {
                new ControlDeviceTask(ContentCommon.CMD_PTZ_DOWN).execute();
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }

        } else if (i1 == R.id.id_left) {
            if (mTag == 1) {
                new ControlDeviceTask(ContentCommon.CMD_PTZ_LEFT).execute();
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }

        } else if (i1 == R.id.id_right) {
            if (mTag == 1) {
                new ControlDeviceTask(ContentCommon.CMD_PTZ_RIGHT).execute();
            } else {
                ToastUtil.ToastMessage(PlayActivity.this, "请连接成功后再进行操作！");
            }

        }
    }


    /*
     *异步控制方向
     */
    private class ControlDeviceTask extends AsyncTask<Void, Void, Integer> {
        private int type;

        public ControlDeviceTask(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (type == ContentCommon.CMD_PTZ_RIGHT) {
                control_item.setText(R.string.right);
            } else if (type == ContentCommon.CMD_PTZ_LEFT) {
                control_item.setText(R.string.left);
            } else if (type == ContentCommon.CMD_PTZ_UP) {
                control_item.setText(R.string.up);
            } else if (type == ContentCommon.CMD_PTZ_DOWN) {
                control_item.setText(R.string.down);
            }
            if (controlWindow != null && controlWindow.isShowing())
                controlWindow.dismiss();

            if (controlWindow != null && !controlWindow.isShowing())
                controlWindow.showAtLocation(mMysurfaceview, Gravity.CENTER, 0, 0);
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            isControlDevice = true;
            if (type == ContentCommon.CMD_PTZ_RIGHT) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_RIGHT);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_RIGHT_STOP);
            } else if (type == ContentCommon.CMD_PTZ_LEFT) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_LEFT);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_LEFT_STOP);
            } else if (type == ContentCommon.CMD_PTZ_UP) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_UP);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_UP_STOP);
            } else if (type == ContentCommon.CMD_PTZ_DOWN) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_DOWN);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_DOWN_STOP);
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            isControlDevice = false;
            if (controlWindow != null && controlWindow.isShowing())
                controlWindow.dismiss();
        }

    }

    /*
     * 上下左右提示框
     */
    private void initControlDailog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.control_device_view, null);
        control_item = (TextView) view.findViewById(R.id.textView1_play);
        controlWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        controlWindow.setBackgroundDrawable(new ColorDrawable(0));
        controlWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                controlWindow.dismiss();
            }
        });
        controlWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    controlWindow.dismiss();
                }
                return false;
            }
        });
    }


    //判断sd卡是否存在
    private boolean existSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    // 拍照
    private void takePicture(final Bitmap bmp) {
        if (!isPictSave) {
            isPictSave = true;
            new Thread() {
                public void run() {
                    savePicToSDcard(bmp);
                }
            }.start();
        } else {
            return;
        }
    }

    /*
     * 保存到本地
     * 注意：此处可以做本地数据库sqlit 保存照片，以便于到本地照片观看界面从SQLite取出照片
     */
    private synchronized void savePicToSDcard(final Bitmap bmp) {
        String strDate = getStrDate();
        //String date = strDate.substring(0, 10);
        FileOutputStream fos = null;
        try {
            File div = new File(Environment.getExternalStorageDirectory(),
                    "ipcamerademo/takepic");
            if (!div.exists()) {
                div.mkdirs();
            }
            ++i;
            Log.e("", i + "");
            File file = new File(div, strDate + "_" + SystemValue.deviceId + "_" + i + ".jpg");
            fos = new FileOutputStream(file);
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                fos.flush();
                Log.d("tag", "takepicture success");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showToast(R.string.ptz_takepic_ok);
                    }
                });
            }
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(R.string.ptz_takepic_fail);
                }
            });
            Log.d("tag", "exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            isPictSave = false;
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos = null;
            }
        }
    }

    //时间格式
    private String getStrDate() {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String strDate = f.format(d);
        return strDate;
    }

    /*
     * 录像
     */
    private void goTakeVideo() {
        if (isTakeVideo) {
            mIdVideoing.setVisibility(View.GONE);
            showToast(R.string.ptz_takevideo_end);
            Log.d("tag", "停止录像");
            if (!isJpeg) {
                NativeCaller.RecordLocal(SystemValue.deviceId, 0);
            }

            isTakeVideo = false;
            mPtzTakeVideos.setImageResource(R.drawable.ptz_takevideo);
            myvideoRecorder.stopRecordVideo();
        } else {
            isTakeVideo = true;
            mIdVideoing.setVisibility(View.VISIBLE);
            showToast(R.string.ptz_takevideo_begin);
            Log.d("tag", "开始录像");
            videotime = (new Date()).getTime();
            mPtzTakeVideos.setImageResource(R.drawable.ptz_takevideo_pressed);
            if (!isJpeg) {
                NativeCaller.RecordLocal(SystemValue.deviceId, 1);
            }

            if (isJpeg) {
                myvideoRecorder.startRecordVideo(2);
            } else {
                myvideoRecorder.startRecordVideo(1);
            }
        }
    }

    private void stopTakevideo() {
        if (isTakeVideo) {
            showToast(R.string.ptz_takevideo_end);
            Log.d("tag", "停止录像");
            isTakeVideo = false;
            // cameratakevideo.stopRecordVideo(SystemValue.deviceId);
            myvideoRecorder.stopRecordVideo();
        }
    }

    //讲话
    private void StartTalk() {
        if (customAudioRecorder != null) {
            Log.i("info", "startTalk");
            customAudioRecorder.StartRecord();
            NativeCaller.PPPPStartTalk(SystemValue.deviceId);
        }
    }

    //停止讲话
    private void StopTalk() {
        if (customAudioRecorder != null) {
            Log.i("info", "stopTalk");
            customAudioRecorder.StopRecord();
            NativeCaller.PPPPStopTalk(SystemValue.deviceId);
        }
    }

    //监听
    private void StartAudio() {
        synchronized (this) {

            try {
                AudioBuffer.ClearAll();
                audioPlayer.AudioPlayStart();
                NativeCaller.PPPPStartAudio(SystemValue.deviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //停止监听
    private void StopAudio() {
        synchronized (this) {

            try {
                audioPlayer.AudioPlayStop();
                AudioBuffer.ClearAll();
                NativeCaller.PPPPStopAudio(SystemValue.deviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 监听
     */
    private void goAudio() {
        if (!isMcriophone) {
            if (bAudioStart) {
                isTalking = false;
                bAudioStart = false;
                mPtzAudio.setChecked(false);
                StopAudio();
            } else {
                isTalking = true;
                bAudioStart = true;
                mPtzAudio.setChecked(true);
                StartAudio();
            }

        } else {
            isMcriophone = false;
            bAudioRecordStart = false;
            mPtzAudio.setChecked(false);
            StopTalk();
            isTalking = true;
            bAudioStart = true;
            mPtzAudio.setChecked(true);
            StartAudio();
        }

    }

    /*
     * 对讲
     */
    private void goMicroPhone() {
        if (!isTalking) {
            if (bAudioRecordStart) {
                Log.d("tag", "停止说话");
                isMcriophone = false;
                bAudioRecordStart = false;
                mPtzTalk.setChecked(false);
                StopTalk();
            } else {
                Log.d("info", "开始说话");
                isMcriophone = true;
                bAudioRecordStart = true;
                mPtzTalk.setChecked(true);
                StartTalk();
            }
        } else {
            isTalking = false;
            bAudioStart = false;
            mPtzTalk.setChecked(false);
            StopAudio();
            isMcriophone = true;
            bAudioRecordStart = true;
            mPtzTalk.setChecked(true);
            StartTalk();
        }

    }


    /**
     * 获取reslution
     */
    public static Map<String, Map<Object, Object>> reslutionlist = new HashMap<String, Map<Object, Object>>();

    /**
     * 增加reslution
     */
    private void addReslution(String mess, boolean isfast) {
        if (reslutionlist.size() != 0) {
            if (reslutionlist.containsKey(SystemValue.deviceId)) {
                reslutionlist.remove(SystemValue.deviceId);
            }
        }
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(mess, isfast);
        reslutionlist.put(SystemValue.deviceId, map);
    }

    private void getReslution() {
        if (reslutionlist.containsKey(SystemValue.deviceId)) {
            Map<Object, Object> map = reslutionlist.get(SystemValue.deviceId);
            if (map.containsKey("qvga")) {
                isqvga = true;
            } else if (map.containsKey("vga")) {
                isvga = true;
            } else if (map.containsKey("qvga1")) {
                isqvga1 = true;
            } else if (map.containsKey("vga1")) {
                isvga1 = true;
            } else if (map.containsKey("p720")) {
                isp720 = true;
            } else if (map.containsKey("high")) {
                ishigh = true;
            } else if (map.containsKey("middle")) {
                ismiddle = true;
            } else if (map.containsKey("max")) {
                ismax = true;
            }
        }
    }

    /*
     * @param type
     * 亮度饱和对比度
     */
    private void setBrightOrContrast(final int type) {

        if (!bInitCameraParam) {
            return;
        }
        int width = getWindowManager().getDefaultDisplay().getWidth();
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.brightprogress, null);
        SeekBar seekBar = (SeekBar) layout.findViewById(R.id.brightseekBar1);
        seekBar.setMax(255);
        switch (type) {
            case BRIGHT:
                seekBar.setProgress(nBrightness);
                break;
            case CONTRAST:
                seekBar.setProgress(nContrast);
                break;
            default:
                break;
        }
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                switch (type) {
                    case BRIGHT:// 亮度
                        nBrightness = progress;
                        NativeCaller.PPPPCameraControl(SystemValue.deviceId, BRIGHT, nBrightness);
                        break;
                    case CONTRAST:// 对比度
                        nContrast = progress;
                        NativeCaller.PPPPCameraControl(SystemValue.deviceId, CONTRAST, nContrast);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress,
                                          boolean arg2) {

            }
        });

    }

    @Override
    protected void onDestroy() {

        try {
            NativeCaller.StopPPPPLivestream(SystemValue.deviceId);
            StopAudio();
            StopTalk();
            stopTakevideo();
            NativeCaller.Free();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    /***
     * BridgeService callback 视频参数回调
     **/
    @Override
    public void callBackCameraParamNotify(String did, int resolution, int brightness, int contrast, int hue, int saturation, int flip, int mode) {
        Log.e("设备返回的参数", resolution + "," + brightness + "," + contrast + "," + hue + "," + saturation + "," + flip + "," + mode);
        nBrightness = brightness;
        nContrast = contrast;
        nResolution = resolution;
        bInitCameraParam = true;
        deviceParamsHandler.sendEmptyMessage(flip);
    }


    /***
     * BridgeService callback 视频数据流回调
     **/
    @Override
    public void callBackVideoData(byte[] videobuf, int h264Data, int len, int width, int height) {
        Log.d("底层返回数据", "videobuf:" + videobuf + "--" + "h264Data" + h264Data + "len" + len + "width" + width + "height" + height);
        if (!bDisplayFinished)
            return;
        bDisplayFinished = false;
        videodata = videobuf;
        videoDataLen = len;
        Message msg = new Message();
        if (h264Data == 1) { // H264
            nVideoWidths = width;
            nVideoHeights = height;
            if (isTakepic) {
                isTakepic = false;
                byte[] rgb = new byte[width * height * 2];
                NativeCaller.YUV4202RGB565(videobuf, rgb, width, height);
                ByteBuffer buffer = ByteBuffer.wrap(rgb);
                mBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                mBmp.copyPixelsFromBuffer(buffer);
                takePicture(mBmp);
            }
            isH264 = true;
            msg.what = 1;
        } else { // MJPEG
            isJpeg = true;
            msg.what = 2;
        }
        mHandler.sendMessage(msg);
        //录像数据
        if (isTakeVideo) {

            Date date = new Date();
            long times = date.getTime();
            int tspan = (int) (times - videotime);
            Log.d("tag", "play  tspan:" + tspan);
            videotime = times;
            if (videoRecorder != null) {
                if (isJpeg) {

                    videoRecorder.VideoRecordData(2, videobuf, width, height, tspan);
                }
            }
        }
    }

    /***
     * BridgeService callback
     **/
    @Override
    public void callBackMessageNotify(String did, int msgType, int param) {
        Log.d("tag", "MessageNotify did: " + did + " msgType: " + msgType
                + " param: " + param);
        if (bManualExit)
            return;

        if (msgType == ContentCommon.PPPP_MSG_TYPE_STREAM) {
            nStreamCodecType = param;
            return;
        }

        if (msgType != ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
            return;
        }

        if (!did.equals(SystemValue.deviceId)) {
            return;
        }

        Message msg = new Message();
        msg.what = 1;
        msgHandler.sendMessage(msg);
    }

    /***
     * BridgeService callback
     **/
    @Override
    public void callBackAudioData(byte[] pcm, int len) {
        Log.d(LOG_TAG, "AudioData: len :+ " + len);
        if (!audioPlayer.isAudioPlaying()) {
            return;
        }
        CustomBufferHead head = new CustomBufferHead();
        CustomBufferData data = new CustomBufferData();
        head.length = len;
        head.startcode = AUDIO_BUFFER_START_CODE;
        data.head = head;
        data.data = pcm;
        AudioBuffer.addData(data);
    }

    /***
     * BridgeService callback
     **/
    @Override
    public void callBackH264Data(byte[] h264, int type, int size) {
        Log.d("tag", "CallBack_H264Data" + " type:" + type + " size:" + size);
        if (isTakeVideo) {
            Date date = new Date();
            long time = date.getTime();
            int tspan = (int) (time - videotime);
            Log.d("tag", "play  tspan:" + tspan);
            videotime = time;
            if (videoRecorder != null) {
                videoRecorder.VideoRecordData(type, h264, size, 0, tspan);
            }
        }
    }

    //对讲数据
    @Override
    public void AudioRecordData(byte[] data, int len) {
        // TODO Auto-generated method stub
        if (bAudioRecordStart && len > 0) {
            NativeCaller.PPPPTalkAudioData(SystemValue.deviceId, data, len);
        }
    }

    //定义录像接口
    public void setVideoRecord(VideoRecorder videoRecorder) {
        this.videoRecorder = videoRecorder;
    }

    public VideoRecorder videoRecorder;

    public interface VideoRecorder {
        abstract public void VideoRecordData(int type, byte[] videodata, int width, int height, int time);
    }


    /**
     * 切换横竖屏
     */
    private void SetScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mTopBg.setVisibility(View.GONE);
        mIdMain.setVisibility(View.GONE);
        mTvBack.setVisibility(View.GONE);
        mIdTitle.setVisibility(View.GONE);
        mIvAdd.setVisibility(View.GONE);

    }








    private Handler PPPPMsgHandler = new Handler() {
        public void handleMessage(Message msg) {

            Bundle bd = msg.getData();
            int msgParam = bd.getInt(STR_MSG_PARAM);
            int msgType = msg.what;
            String did = bd.getString(STR_DID);

            Log.i("aaa", did + "====" + msgType + "--msgParam:" + msgParam);

            switch (msgType) {
                case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
                    int resid;
                    switch (msgParam) {
                        case ContentCommon.PPPP_STATUS_CONNECTING://0
                            resid = R.string.pppp_status_connecting; //正在连接
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_FAILED://连接失败
                            resid = R.string.pppp_status_connect_failed;
                            break;
                        case ContentCommon.PPPP_STATUS_DISCONNECT://断线
                            resid = R.string.pppp_status_disconnect;
                            break;
                        case ContentCommon.PPPP_STATUS_INITIALING://已连接, 正在初始化
                            resid = R.string.pppp_status_initialing;
                            break;
                        case ContentCommon.PPPP_STATUS_INVALID_ID://ID号无效
                            resid = R.string.pppp_status_invalid_id;
                            break;
                        case ContentCommon.PPPP_STATUS_ON_LINE://2 在线状态:连接成功
                            resid = R.string.pppp_status_online;
                            //摄像机在线之后读取摄像机类型
                            String cmd = "get_status.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
                                    + "&user=admin&pwd=" + SystemValue.devicePass;

                            SPUtil.SetCameraPs(PlayActivity.this, SystemValue.deviceId, SystemValue.devicePass);

                            NativeCaller.TransferMessage(did, cmd, 1);

                            //连接成功后自动打开视频
                            mTag = 1;
                            VideoMethod();

                            break;
                        case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://摄像机不在线
                            resid = R.string.device_not_on_line;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://连接超时
                            resid = R.string.pppp_status_connect_timeout;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_ERRER://密码错误
                            resid = R.string.pppp_status_pwd_error;
                            SPUtil.SetCameraPs(PlayActivity.this, SystemValue.deviceId, null);
                            break;
                        default:
                            resid = R.string.pppp_status_unknown; //未知状态
                    }

                    ToastUtil.ToastMessage(PlayActivity.this, getString(resid));


                    if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
                        NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
                            || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
                        NativeCaller.StopPPPP(did);
                    }
                    break;
                case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
                    break;

            }

        }
    };


    @Override
    public void BSMsgNotifyData(String did, int type, int param) {
        Log.d("ip", "id:" + did + ",type:" + type + ", param:" + param);
        Bundle bd = new Bundle();
        Message msg = PPPPMsgHandler.obtainMessage();
        msg.what = type;
        bd.putInt(STR_MSG_PARAM, param);
        bd.putString(STR_DID, did);
        msg.setData(bd);
        PPPPMsgHandler.sendMessage(msg);


    }

    @Override
    public void BSSnapshotNotify(String did, byte[] bImage, int len) {

    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {

    }

    @Override
    public void CameraStatus(String did, int status) {

    }


    class StartPPPPThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
                startCameraPPPP();
            } catch (Exception e) {

            }
        }
    }

    private void startCameraPPPP() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }

        if (SystemValue.deviceId.toLowerCase().startsWith("vsta")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK");
        } else {
            NativeCaller.StartPPPP(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "");
        }
    }


}

















