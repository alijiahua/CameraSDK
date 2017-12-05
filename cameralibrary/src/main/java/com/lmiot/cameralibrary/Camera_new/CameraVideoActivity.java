package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.Camera_new.utils.SystemValue;
import com.lmiot.cameralibrary.R;
import com.lmiot.tiblebarlibrary.LmiotTitleBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CameraVideoActivity extends BaseActivity  {



    private Intent mIntent;
    private String mCameraID;
    private List<File> mList;
    private ListView mIdPictureListview;
    private LmiotTitleBar mLmiotTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_video);
        intiView();


    }


    /**
     * 设置页头
     */
    private void intiView() {

        mIdPictureListview = findViewById(R.id.id_picture_listview);


        mCameraID = getIntent().getStringExtra("CameraID");


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

    @Override
    protected void onResume() {
        super.onResume();
        GetPicture();
    }

    private void GetPicture() {
        List<Bitmap> bitmapList = new ArrayList<>();

        File div = new File(Environment.getExternalStorageDirectory(),
                "ipcamerademo/video");
        if (!div.exists()) {
            div.mkdirs();
        }
        File[] files = div.listFiles();

        mList = new ArrayList<>();

        for (File file : files) {
            if (file.getName().contains(mCameraID)) {
                mList.add(file);
            }

        }


        PicAdapter picAdapter = new PicAdapter();
        mIdPictureListview.setAdapter(picAdapter);

    }


    private class PicAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mList.size();
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
            View v = View.inflate(CameraVideoActivity.this, R.layout.item_camera_video, null);

            ImageView img = (ImageView) v.findViewById(R.id.id_img);
            ImageView del = (ImageView) v.findViewById(R.id.id_del);
            TextView name = (TextView) v.findViewById(R.id.id_name);
            TextView timer = (TextView) v.findViewById(R.id.id_time);


            final String picName = mList.get(position).getName();


            name.setText(picName);

            Calendar cal = Calendar.getInstance();
            long time = mList.get(position).lastModified();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cal.setTimeInMillis(time);
            final String picTimer = formatter.format(cal.getTime()); //最后修改时间

            timer.setText(getString(R.string.save_time)+ picTimer);

            final String absolutePath = mList.get(position).getAbsolutePath();
            final Bitmap bitmap = createVideoThumbnail(absolutePath);
            if (bitmap != null) {
                img.setImageBitmap(bitmap);
            } else {
                Log.d("PicAdapter", "Bitmap为空");
            }

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(CameraVideoActivity.this)
                            .setMessage(R.string.sure_del_video)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mList.get(position).delete();
                                    GetPicture();
                                }
                            })
                            .show();


                }
            });


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      /*  mIntent = new Intent();
                        mIntent.setAction(Intent.ACTION_VIEW);
                        mIntent.setDataAndType(Uri.fromFile(mList.get(position)), "video*//*");
                        startActivity(mIntent);*/


                    String path = mList.get(position).getAbsolutePath();
                    mIntent = new Intent(CameraVideoActivity.this, ShowLocalVideoActivity.class);
                    mIntent.putExtra("did", SystemValue.deviceId);
                    mIntent.putExtra("filepath", path);
                    //  mIntent.putExtra("arrayList", arrayList);
                    mIntent.putExtra("position", position);
                    mIntent.putExtra(ContentCommon.STR_CAMERA_NAME, SystemValue.deviceName);
                    //mIntent.putExtra("videotime", videoTime.get(position));
                    //mIntent.putExtra("timeList", videoTime);
                    startActivity(mIntent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                }
            });


            return v;
        }
    }


    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;

        bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
        return bitmap;
    }





}
