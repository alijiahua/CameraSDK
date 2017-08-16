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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CameraPictureActivity extends BaseActivity implements View.OnClickListener {


    private Intent mIntent;
    private String mCameraID;
    private List<File> mList;
    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private ListView mIdPictureListview;

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


        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mIdPictureListview = findViewById(R.id.id_picture_listview);



        mIvBack.setOnClickListener(this);
        mTvBack.setOnClickListener(this);

        mCameraID = getIntent().getStringExtra("CameraID");


        mTvBack.setText("返回");
        mIdTitle.setText("图片列表");
        mTvModify.setVisibility(View.GONE);
        mIvAdd.setVisibility(View.GONE);

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

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back || i == R.id.tv_back) {
            finish();

        }

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

            timer.setText("保存时间：" + picTimer);

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
