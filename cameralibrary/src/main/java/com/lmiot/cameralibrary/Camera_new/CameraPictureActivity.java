package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lmiot.cameralibrary.R;
import com.lmiot.tiblebarlibrary.LmiotTitleBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CameraPictureActivity extends BaseActivity  {


    private Intent mIntent;
    private String mCameraID;
    private List<File> mList;
    private ListView mIdPictureListview;
    private LmiotTitleBar mLmiotTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_picture);
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
                "ipcamerademo/takepic");
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
            View v = View.inflate(CameraPictureActivity.this, R.layout.item_camera_picture, null);

            ImageView img = (ImageView) v.findViewById(R.id.id_img);
            TextView name = (TextView) v.findViewById(R.id.id_name);
            TextView timer = (TextView) v.findViewById(R.id.id_time);


            final String picName = mList.get(position).getName();


            name.setText(picName);

            Calendar cal = Calendar.getInstance();
            long time = mList.get(position).lastModified();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cal.setTimeInMillis(time);
            final String picTimer = formatter.format(cal.getTime()); //最后修改时间

            timer.setText(getString(R.string.save_time) + picTimer);

            final String absolutePath = mList.get(position).getAbsolutePath();
            final Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
            if (bitmap != null) {
                img.setImageBitmap(bitmap);
            }


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIntent = new Intent();
                    mIntent.setAction(Intent.ACTION_VIEW);
                    mIntent.setDataAndType(Uri.fromFile(mList.get(position)), "image/*");
                    startActivity(mIntent);

                }
            });


            return v;
        }
    }




}
