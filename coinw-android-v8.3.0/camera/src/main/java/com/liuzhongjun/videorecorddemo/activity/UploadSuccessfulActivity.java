package com.liuzhongjun.videorecorddemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.liuzhongjun.videorecorddemo.R;


public class UploadSuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_successful);
        TextView title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.camera_b2);
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(UploadSuccessfulActivity.this, "huolongluo.byw.byw.ui.activity.main.MainActivity"));
                startActivity(intent);
                finish();
            }
        });
    }
}
