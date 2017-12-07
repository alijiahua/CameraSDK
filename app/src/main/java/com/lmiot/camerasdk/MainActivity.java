package com.lmiot.camerasdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lmiot.cameralibrary.Camera_new.CameraDevices;
import com.lmiot.cameralibrary.SQL.CamerBean;
import com.lmiot.cameralibrary.Util.ApiUtls;

public class MainActivity extends AppCompatActivity implements CameraDevices.onMoreItemListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt= (Button) findViewById(R.id.id_bt);

        CameraDevices.setOnLongItemListener(this);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CameraDevices.class));

                ApiUtls.getInstance().setUerName("用户ID"); //设置保存摄像头数据的用户ID
                ApiUtls.getInstance().setMoreItem("隐藏菜单");//使用隐藏菜单，需继承CameraDevices.onMoreItemListener

            }
        });
    }


    @Override
    public void itemClick(CamerBean camerBean) {

    }
}
