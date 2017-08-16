package com.lmiot.cameralibrary.Camera_new;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmiot.cameralibrary.R;

public class PictureDetailActivity extends BaseActivity  implements View.OnClickListener{


    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private ImageView mIdImg;
    private ImageView mIdShare;
    private ImageView mIdDel;

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

        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mIdImg = findViewById(R.id.id_img);
        mIdShare = findViewById(R.id.id_share);
        mIdDel = findViewById(R.id.id_del);

        mIvBack.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
        mIdShare.setOnClickListener(this);
        mIdDel.setOnClickListener(this);

        mTvBack.setText("返回");
        mIdTitle.setText(picName);
        mTvModify.setVisibility(View.GONE);
        mIvAdd.setVisibility(View.GONE);

    }




    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back) {
        } else if (i == R.id.tv_back) {
        } else if (i == R.id.id_share) {
        } else if (i == R.id.id_del) {
        }

    }
}
