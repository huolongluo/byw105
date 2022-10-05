package com.liuzhongjun.videorecorddemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.liuzhongjun.videorecorddemo.R;
import com.liuzhongjun.videorecorddemo.util.CameraApp;
import com.liuzhongjun.videorecorddemo.util.ConvertSecondUtil;
import com.liuzhongjun.videorecorddemo.util.UploadUtil;
import com.liuzhongjun.videorecorddemo.util.VideoInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;

import okhttp3.ResponseBody;


public class UploadActivity extends AppCompatActivity {
    private TextView status1;
    private TextView status2;
    private Button upLoadBt;
    private TextView totalTime;
    private TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_view);
        initView();
    }

    private void initView() {
        final int time = ConvertSecondUtil.getSecond(VideoInfo.time1) + ConvertSecondUtil.getSecond(VideoInfo.time2);
        upLoadBt = findViewById(R.id.bt_next);
        totalTime = findViewById(R.id.totalTime);
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.camera_b2);
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        upLoadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (time > 60 * 2) {
                    Toast.makeText(UploadActivity.this, R.string.sc1, Toast.LENGTH_SHORT).show();
                    return;
                }
                upload();
                status1.setText(R.string.camera_b3);
                status2.setText(R.string.camera_b4);
                upLoadBt.setClickable(false);
                upLoadBt.setBackgroundColor(getResources().getColor(R.color.upload_bt_color));
                showProgress();
            }
        });
        TextView time1 = findViewById(R.id.time1);
        TextView time2 = findViewById(R.id.time2);
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        time1.setText(String.format(getString(R.string.camera_b5), VideoInfo.time1));
        time2.setText(String.format(getString(R.string.camera_b6), VideoInfo.time2));
        totalTime.setText(getString(R.string.camera_b7) + ConvertSecondUtil.getTimeStrBySecond(time));
    }

    /**
     * 创建线程实现文件的上传
     *
     * @param
     */
    synchronized public void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(VideoInfo.file1);
                    File file2 = new File(VideoInfo.file2);
                    ResponseBody upload = UploadUtil.getInstance().upload(getApplicationContext(),CameraApp.host + "app/kyc/video/upload", file, file2);
                    String string = upload.string();
                    try {
                        final JSONObject json = new JSONObject(string);
                        final JSONObject data = json.getJSONObject("data");
                        int code = data.getInt("code");
                        if (code == 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hindProgress();
                                    Toast.makeText(getApplication(), R.string.camera_b8, Toast.LENGTH_SHORT).show();
                                    status1.setText(R.string.camera_b8);
                                    status2.setText(R.string.camera_b8);
                                    startActivity(new Intent(UploadActivity.this, UploadSuccessfulActivity.class));
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        upLoadBt.setClickable(true);
                                        upLoadBt.setBackgroundColor(getResources().getColor(R.color.upload_bt_color));
                                        hindProgress();
                                        status1.setText(R.string.camera_b9);
                                        status2.setText(R.string.camera_b9);
                                        Toast.makeText(getApplication(), data.getString("value"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hindProgress();
                                status1.setText(R.string.camera_b9);
                                status2.setText(R.string.camera_b9);
                                upLoadBt.setClickable(true);
                                upLoadBt.setBackgroundColor(getResources().getColor(R.color.upload_bt_color1));
                                Toast.makeText(getApplication(), R.string.camera_b9, Toast.LENGTH_SHORT).show();

                            }
                        });
                        e.printStackTrace();
                    }
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hindProgress();
                            status1.setText(R.string.camera_b9);
                            status2.setText(R.string.camera_b9);
                            upLoadBt.setClickable(true);
                            upLoadBt.setBackgroundColor(getResources().getColor(R.color.upload_bt_color1));
                            Toast.makeText(getApplication(), R.string.camera_b9, Toast.LENGTH_SHORT).show();

                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void showProgress() {
        try {
            Class<?> aClass = Class.forName("huolongluo.byw.byw.manager.DialogManager");
            Method getMeaning = aClass.getDeclaredMethod("showProgressDialog", Context.class);
            // 错误的方式，枚举对应的class没有newInstance方法，会报NoSuchMethodException，应该使用getEnumConstants方法
            //Object o = aClass.newInstance();NoSuchMethodException
            Object[] oo = aClass.getEnumConstants();
            Object invoke = getMeaning.invoke(oo[0], UploadActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hindProgress() {
        try {
            Class<?> aClass = Class.forName("huolongluo.byw.byw.manager.DialogManager");
            Method getMeaning = aClass.getDeclaredMethod("dismiss");
            // 错误的方式，枚举对应的class没有newInstance方法，会报NoSuchMethodException，应该使用getEnumConstants方法
            //Object o = aClass.newInstance();NoSuchMethodException
            Object[] oo = aClass.getEnumConstants();
            Object invoke = getMeaning.invoke(oo[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
