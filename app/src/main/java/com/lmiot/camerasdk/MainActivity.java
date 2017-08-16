package com.lmiot.camerasdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lmiot.cameralibrary.Camera_new.CameraDevices;
import com.lmiot.cameralibrary.Util.SPUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt= (Button) findViewById(R.id.id_bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtil.setSessionID("963d9039e6874be68f4bae6128ad5cc");
                startActivity(new Intent(MainActivity.this, CameraDevices.class));
            }
        });
    }
}
