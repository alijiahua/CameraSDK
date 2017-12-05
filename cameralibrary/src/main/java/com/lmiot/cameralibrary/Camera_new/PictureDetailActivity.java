package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmiot.cameralibrary.R;
import com.lmiot.tiblebarlibrary.LmiotTitleBar;

public class PictureDetailActivity extends BaseActivity  {


    private ImageView mIdImg;
    private ImageView mIdShare;
    private ImageView mIdDel;
    private LmiotTitleBar mLmiotTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_picture_detail);
        intiView();

    }


    /**
     * 设置页头
     */
    private void intiView() {
        String picName = getIntent().getStringExtra("PicName");

        mIdImg = findViewById(R.id.id_img);
        mIdShare = findViewById(R.id.id_share);
        mIdDel = findViewById(R.id.id_del);

        mLmiotTitleBar = findViewById(R.id.id_lmiot_title_bar);
        mLmiotTitleBar.setTitle(picName);
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





}
