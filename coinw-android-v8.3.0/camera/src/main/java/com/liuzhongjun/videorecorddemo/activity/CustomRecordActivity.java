package com.liuzhongjun.videorecorddemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.liuzhongjun.videorecorddemo.R;
import com.liuzhongjun.videorecorddemo.util.CameraApp;
import com.liuzhongjun.videorecorddemo.util.CameraUtils;
import com.liuzhongjun.videorecorddemo.util.DialogUtils;
import com.liuzhongjun.videorecorddemo.util.VideoUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by pc on 2017/3/20.
 *
 * @author liuzhongjun
 */

public class CustomRecordActivity extends AppCompatActivity implements View.OnClickListener {
    private int currentTextSize = 5;//第二段录像描述信息，初始大小
    private static final String TAG = "CustomRecordActivity";

    private Executor executor = Executors.newFixedThreadPool(1);
    private int cameraId = 1;
    //UI
    private LinearLayout des2_ll;
    private TextView mRecordControl;
    private ImageView mPauseRecord;
    private SurfaceView surfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Chronometer mRecordTime;
    //DATA

    //录像机状态标识
    private int mRecorderState;

    public static final int STATE_INIT = 0;
    public static final int STATE_RECORDING = 1;
    public static final int STATE_PAUSE = 2;


    //    private boolean isRecording;// 标记，判断当前是否正在录制
//    private boolean isPause; //暂停标识
    private long mPauseTime = 0;           //录制暂停时间间隔

    // 存储文件
    private File mVecordFile;
    private Camera mCamera;
    private MediaRecorder mediaRecorder;
    private String currentVideoFilePath;
    private String saveVideoPath = "";
    private TextView title_tv;
    private View des1;//第一段視頻，描述文字
    private TextView des2;//第2段視頻，描述文字
    private boolean isFirst = true;
    private boolean showDes = true;
    private TextView page;
    private TextView right_iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        initView();
        isFirst = getIntent().getBooleanExtra("isFirst", true);
        showDes = getIntent().getBooleanExtra("showDes", true);
        initDialogHint();
    }

    private boolean initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1);
                return false;
            }
        }
        return true;
    }

    /**
     * 初始提示信息
     */
    private void initDialogHint() {
        page.setText(isFirst ? "1" : "2");
        if (isFirst) {
            des2_ll.setVisibility(View.GONE);
            des1.setVisibility(View.VISIBLE);
        } else {
            des2_ll.setVisibility(View.VISIBLE);
            des1.setVisibility(View.GONE);
        }
        if (!showDes) {
            return;
        }
        if (isFirst) {
            des2_ll.setVisibility(View.GONE);
            des1.setVisibility(View.VISIBLE);
            DialogUtils.showKycDes(this, true, new DialogUtils.ShowListener() {
                @Override
                public void OnListener() {
                    des1.setVisibility(View.VISIBLE);
                }
            });
        } else {

            DialogUtils.showKycDes(this, false, new DialogUtils.ShowListener() {
                @Override
                public void OnListener() {
                    des2_ll.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void initView() {
        if (!initPermission()) {
            Toast.makeText(this, R.string.no_permission_camera, Toast.LENGTH_SHORT).show();
            finish();
        }
        des1 = findViewById(R.id.des1);
        des2 = findViewById(R.id.des2);
        des2_ll = findViewById(R.id.des2_ll);
        des2.setText(String.format(getResources().getString(R.string.userinfo),
                CameraApp.realName, CameraApp.identityNo));
        surfaceView = (SurfaceView) findViewById(R.id.record_surfaceView);
        mRecordControl = (TextView) findViewById(R.id.record_control);
        mRecordTime = (Chronometer) findViewById(R.id.record_time);
        mPauseRecord = (ImageView) findViewById(R.id.record_pause);
        findViewById(R.id.Zoom).setOnClickListener(this);
        findViewById(R.id.ZoomOut).setOnClickListener(this);
        page = findViewById(R.id.page);
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        right_iv = findViewById(R.id.right_iv);
//        right_iv.setVisibility(View.VISIBLE);
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.camera_b2);

        mRecordControl.setOnClickListener(this);
//        mPauseRecord.setOnClickListener(this);
        mPauseRecord.setEnabled(false);
        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });
        //配置SurfaceHolder
        mSurfaceHolder = surfaceView.getHolder();
        // 设置Surface不需要维护自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
        mSurfaceHolder.setFixedSize(320, 280);
        // 设置该组件不会让屏幕自动关闭
        mSurfaceHolder.setKeepScreenOn(true);
        //回调接口
        mSurfaceHolder.addCallback(mSurfaceCallBack);
    }


    private SurfaceHolder.Callback mSurfaceCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            if (mSurfaceHolder.getSurface() == null) {
                return;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            releaseCamera();
        }
    };


    /**
     * 初始化摄像头
     *
     * @throws IOException
     * @author liuzhongjun
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void initCamera() {

        if (mCamera != null) {
            releaseCamera();
        }
        try {
            mCamera = Camera.open(cameraId);//会报错Fail to connect to camera service
            if (mCamera == null) {
                Toast.makeText(this, "未能获取到相机！", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error Camera.open msg: " + e.getMessage());
        }
        try {
            //将相机与SurfaceHolder绑定
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //配置CameraParams
            configCameraParams();
            //启动相机预览
            mCamera.startPreview();
        } catch (Exception e) {
            //有的手机会因为兼容问题报错，这就需要开发者针对特定机型去做适配了
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            Toast.makeText(this, "未能获取到相机！", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 设置摄像头为竖屏`
     *
     * @author lip
     * @date 2015-3-16
     */
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    private void configCameraParams() {
        Camera.Parameters params = mCamera.getParameters();
        //设置相机的横竖屏(竖屏需要旋转90°)
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            params.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);
        } else {
            params.set("orientation", "landscape");
            mCamera.setDisplayOrientation(0);
        }
        //设置聚焦模式
//        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        //缩短Recording启动时间
        params.setRecordingHint(true);
        //影像稳定能力
        if (params.isVideoStabilizationSupported())
            params.setVideoStabilization(true);
        mCamera.setParameters(params);
    }


    /**
     * 释放摄像头资源
     *
     * @author liuzhongjun
     * @date 2016-2-5
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 开始录制视频
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public boolean startRecord() {

        initCamera();
        //录制视频前必须先解锁Camera
        mCamera.unlock();
        configMediaRecorder();
        try {
            //开始录制
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 停止录制视频
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void stopRecord() {
        // 设置后不会崩
        mediaRecorder.setOnErrorListener(null);
        mediaRecorder.setPreviewDisplay(null);
        try {
            //Process Name: 'huolongluo.byw'
            //Thread Name: 'main'
            //Back traces starts.
            //java.lang.RuntimeException: stop failed.
            //	at android.media.MediaRecorder.native_stop(Native Method)
            //	at android.media.MediaRecorder.stop(MediaRecorder.java:1901)
            //停止录制
            mediaRecorder.stop();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        mediaRecorder.reset();
        //释放资源
        mediaRecorder.release();
        mediaRecorder = null;
    }

    public void switchCamera() {
        if (cameraId == 1) {
            cameraId = 0;
        } else {
            cameraId = 1;
        }
        initCamera();
    }

    public void pauseRecord() {


    }

    /**
     * 合并录像视频方法
     */
    private void mergeRecordVideoFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] str = new String[]{saveVideoPath, currentVideoFilePath};
                    //将2个视频文件合并到 append.mp4文件下
                    VideoUtils.appendVideo(CustomRecordActivity.this, getSDPath(CustomRecordActivity.this) + "append.mp4", str);
                    File reName = new File(saveVideoPath);
                    File f = new File(getSDPath(CustomRecordActivity.this) + "append.mp4");
                    //再将合成的append.mp4视频文件 移动到 saveVideoPath 路径下
                    f.renameTo(reName);
                    if (reName.exists()) {
                        f.delete();
                        new File(currentVideoFilePath).delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 点击中间按钮，执行的UI更新操作
     */
    private void refreshControlUI() {
        if (mRecorderState == STATE_INIT) {
            //录像时间计时
            mRecordTime.setBase(SystemClock.elapsedRealtime());
            mRecordTime.start();

//            mRecordControl.setImageResource(R.drawable.recordvideo_stop);
            mRecordControl.setText(R.string.Completed);
            //1s后才能按停止录制按钮
            mRecordControl.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecordControl.setEnabled(true);
                }
            }, 1000);
//            mPauseRecord.setVisibility(View.VISIBLE);
            mPauseRecord.setEnabled(true);


        } else if (mRecorderState == STATE_RECORDING) {
            mPauseTime = 0;
            try {
                //Process Name: 'huolongluo.byw'
                //Thread Name: 'main'
                //Back traces starts.
                //java.lang.RuntimeException: stop failed.
                //	at android.media.MediaRecorder.native_stop(Native Method)
                //	at android.media.MediaRecorder.stop(MediaRecorder.java:1901)
                //停止录制
                mRecordTime.stop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            mRecordControl.setText(R.string.Completed);
            mPauseRecord.setVisibility(View.GONE);
            mPauseRecord.setEnabled(false);
        }

    }

    /**
     * 点击暂停继续按钮，执行的UI更新操作
     */
    private void refreshPauseUI() {
        if (mRecorderState == STATE_RECORDING) {
            mPauseRecord.setImageResource(R.drawable.control_play);

            mPauseTime = SystemClock.elapsedRealtime();
            try {
                //Process Name: 'huolongluo.byw'
                //Thread Name: 'main'
                //Back traces starts.
                //java.lang.RuntimeException: stop failed.
                //	at android.media.MediaRecorder.native_stop(Native Method)
                //	at android.media.MediaRecorder.stop(MediaRecorder.java:1901)
                //停止录制
                mRecordTime.stop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (mRecorderState == STATE_PAUSE) {
            mPauseRecord.setImageResource(R.drawable.control_pause);

            if (mPauseTime == 0) {
                mRecordTime.setBase(SystemClock.elapsedRealtime());
            } else {
                mRecordTime.setBase(SystemClock.elapsedRealtime() - (mPauseTime - mRecordTime.getBase()));
            }
            mRecordTime.start();
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.record_control) {
            if (mRecorderState == STATE_INIT) {
                if (getSDPath(getApplicationContext()) == null)
                    return;
                //视频文件保存路径，configMediaRecorder方法中会设置
                currentVideoFilePath = getSDPath(getApplicationContext()) + getVideoName();
                //开始录制视频
                if (!startRecord())
                    return;
                refreshControlUI();
                mRecorderState = STATE_RECORDING;
            } else if (mRecorderState == STATE_RECORDING) {
                mRecordControl.setClickable(false);
                //停止视频录制
                stopRecord();
                //先给Camera加锁后再释放相机
                mCamera.lock();
                releaseCamera();

                refreshControlUI();

                //判断是否进行视频合并
                if ("".equals(saveVideoPath)) {
                    saveVideoPath = currentVideoFilePath;
                } else {
                    mergeRecordVideoFile();
                }
                mRecorderState = STATE_INIT;

                //延迟一秒跳转到播放器，（确保视频合并完成后跳转） TODO 具体的逻辑可根据自己的使用场景跳转
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecordControl.setClickable(true);
                        Intent intent = new Intent(CustomRecordActivity.this, PlayVideoActivity.class);
                        intent.putExtra("isFirst", isFirst);
                        Bundle bundle = new Bundle();
                        bundle.putString("videoPath", saveVideoPath);
                        bundle.putString("time", mRecordTime.getText().toString());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }, 500);
            } else if (mRecorderState == STATE_PAUSE) {
                //代表视频暂停录制时，点击中心按钮
                Intent intent = new Intent(CustomRecordActivity.this, PlayVideoActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("isFirst", isFirst);
                bundle.putString("videoPath", saveVideoPath);
                bundle.putString("time", mRecordTime.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        } else if (i == R.id.record_pause) {
            if (mRecorderState == STATE_RECORDING) {
                //正在录制的视频，点击后暂停

                //取消自动对焦
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success)
                            CustomRecordActivity.this.mCamera.cancelAutoFocus();
                    }
                });

                stopRecord();

                refreshPauseUI();


                //判断是否进行视频合并
                if ("".equals(saveVideoPath)) {
                    saveVideoPath = currentVideoFilePath;
                } else {
                    mergeRecordVideoFile();
                }

                mRecorderState = STATE_PAUSE;

            } else if (mRecorderState == STATE_PAUSE) {

                if (getSDPath(getApplicationContext()) == null)
                    return;

                //视频文件保存路径，configMediaRecorder方法中会设置
                currentVideoFilePath = getSDPath(getApplicationContext()) + getVideoName();

                //继续视频录制
                if (!startRecord()) {
                    return;
                }
                refreshPauseUI();

                mRecorderState = STATE_RECORDING;
            }
        } else if (i == R.id.Zoom) {
            if (currentTextSize > 7) currentTextSize = 7;
            des2.setTextSize(CameraUtils.sp2px(this, ++currentTextSize));
        } else if (i == R.id.ZoomOut) {
            if (currentTextSize < 5) currentTextSize = 5;
            des2.setTextSize(CameraUtils.sp2px(this, --currentTextSize));
        }

    }


    /**
     * 配置MediaRecorder()
     */

    private void configMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setOnErrorListener(OnErrorListener);

        //使用SurfaceView预览
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        //1.设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //2.设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //3.设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置立体声
//        mediaRecorder.setAudioChannels(2);
        //设置最大录像时间 单位：毫秒
//        mediaRecorder.setMaxDuration(60 * 1000);
        //设置最大录制的大小 单位，字节
//        mediaRecorder.setMaxFileSize(1024 * 1024);
        //音频一秒钟包含多少数据位
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        mediaRecorder.setAudioEncodingBitRate(44100);
        if (mProfile != null && mProfile.videoBitRate > 2 * 1024 * 1024)
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        else
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
//        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);

        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        mediaRecorder.setOrientationHint(cameraId == 1 ? 270 : 90);
        //设置录像的分辨率
        mediaRecorder.setVideoSize(1280, 720);

        //设置录像视频输出地址
        mediaRecorder.setOutputFile(currentVideoFilePath);
    }

    private MediaRecorder.OnErrorListener OnErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            CameraUtils.report("高级认证录制异常,what=" + what + " extra:" + extra, TAG + "-OnErrorListener-onError");

            showRecordErrorDialog();
        }
    };

    private void showRecordErrorDialog() {
        stopRecord();

        AlertDialog dialog = new AlertDialog.Builder(CustomRecordActivity.this).create();
        View view = View.inflate(CustomRecordActivity.this, R.layout.dialog_show_one1_camera, null);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(getString(R.string.camera_record_failed));
        tv_dialog_ok.setText(getString(R.string.camera_retry));
        tv_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCamera.lock();
                    releaseCamera();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 创建视频文件保存路径
     */
    public static String getSDPath(Context context) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, "请查看您的SD卡是否存在！", Toast.LENGTH_SHORT).show();
            return null;
        }

        File sdDir = Environment.getExternalStorageDirectory();
        File eis = new File(sdDir.toString() + "/RecordVideo/");
        if (!eis.exists()) {
            eis.mkdir();
        }
        return sdDir.toString() + "/RecordVideo/";
    }

    private String getVideoName() {
        return "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
    }
}
