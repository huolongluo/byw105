//package com.liuzhongjun.videorecorddemo.activity;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//
//import com.liuzhongjun.videorecorddemo.R;
//import com.liuzhongjun.videorecorddemo.util.CameraUtils;
//
//
//public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.auth_activity);
//        initView();
//    }
//
//    private void initView() {
//        findViewById(R.id.back_iv).setOnClickListener(this);
//        findViewById(R.id.base_auth).setOnClickListener(this);
//        findViewById(R.id.high_auth).setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.back_iv) {
//            finish();
//        } else if (view.getId() == R.id.base_auth) {
//            CameraUtils.toBaseReZheng(this);
//        } else if (view.getId() == R.id.high_auth) {
//            CameraUtils.toHighReZheng(this);
//        }
//    }
//}
