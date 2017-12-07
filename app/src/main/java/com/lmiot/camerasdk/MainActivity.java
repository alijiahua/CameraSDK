package com.lmiot.camerasdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.lmiot.cameralibrary.Camera_new.CameraDevices;
import com.lmiot.cameralibrary.Util.DataUtil;

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
                DataUtil.setMoreItem("隐藏菜单");

            }
        });
    }

    @Override
    public void itemClick(View view) {
        Toast.makeText(this, "隐藏菜单", Toast.LENGTH_SHORT).show();
    }
}
