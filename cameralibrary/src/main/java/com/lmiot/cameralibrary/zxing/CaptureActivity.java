package com.lmiot.cameralibrary.zxing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lmiot.cameralibrary.Camera_new.AddCameraActivity;
import com.lmiot.cameralibrary.Camera_new.BaseActivity;
import com.lmiot.cameralibrary.R;
import com.lmiot.cameralibrary.zxing.camera.CameraManager;
import com.lmiot.cameralibrary.zxing.decoding.CaptureActivityHandler;
import com.lmiot.cameralibrary.zxing.decoding.InactivityTimer;
import com.lmiot.cameralibrary.zxing.view.ViewfinderView;
import java.io.IOException;
import java.util.Vector;

public class CaptureActivity extends BaseActivity implements Callback ,View.OnClickListener{
    public static final String QR_RESULT = "RESULT";



    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    // private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    CameraManager cameraManager;
    private String mResultText;
    private String mCameraFlag;
    private Intent mIntent;
    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private SurfaceView mSurfaceview;
    private ViewfinderView mViewfinderview;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderview);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        intiView();
        mCameraFlag = getIntent().getStringExtra("camera");

    }


    /**
     * 设置页头
     */
    private void intiView() {


        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mSurfaceview = findViewById(R.id.surfaceview);
        mViewfinderview = findViewById(R.id.viewfinderview);

        mTvBack.setOnClickListener(this);
        mIvBack.setOnClickListener(this);


        mIdTitle.setText("二维码扫描");
        mTvModify.setVisibility(View.GONE);
        mIvAdd.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // CameraManager.init(getApplication());
        cameraManager = new CameraManager(getApplication());

        viewfinderView.setCameraManager(cameraManager);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            // CameraManager.get().openDriver(surfaceHolder);
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        showResult(obj);
    }

    private void showResult(final Result rawResult) {

        mResultText = rawResult.getText();


        Log.d("CaptureActivity", "扫描结果：" + mResultText);


            //发现摄像头
            if ((mResultText.length() == 15) || (mResultText.length() == 17)) {
                mIntent = new Intent(CaptureActivity.this, AddCameraActivity.class);
                mIntent.putExtra("cameraResult", mResultText);
                startActivity(mIntent);
                CaptureActivity.this.finish();
                overridePendingTransition(R.anim.move_left_out_activity, R.anim.move_right_in_activity);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(mResultText);
                builder.setPositiveButton(R.string.goto01, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        try {

                            Uri uri = Uri.parse(mResultText);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CaptureActivity.this, R.string.cannot_open, Toast.LENGTH_SHORT).show();
                            restartPreviewAfterDelay(0L);
                        }

                    }
                });
                builder.setNegativeButton(R.string.back, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        restartPreviewAfterDelay(0L);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
    }










    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(MessageIDs.restart_preview, delayMS);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            try {
                AssetFileDescriptor fileDescriptor = getAssets().openFd("qrbeep.ogg");
                this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            } catch (IOException e) {
                this.mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_back || i == R.id.iv_back) {
            finish();

        }
    }
}