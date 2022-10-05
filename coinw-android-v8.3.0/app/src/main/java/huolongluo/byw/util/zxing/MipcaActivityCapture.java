package huolongluo.byw.util.zxing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.util.RGBLuminanceSource;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.zxing.camera.CameraManager;
import huolongluo.byw.util.zxing.decoding.CaptureActivityHandler;
import huolongluo.byw.util.zxing.decoding.InactivityTimer;
import huolongluo.byw.util.zxing.view.ViewfinderView;
import huolongluo.bywx.helper.AppHelper;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends BaseActivity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private ImageView lightBtn;
    private ImageView btn_light_top;
    private TextView tvLight;
    private boolean isLight;
    private LinearLayout llTableInfo, llRescan;
    private TextView tvRestaurantName, tvNumber, tvErrorTip, tvPhoto;
    private Button btnConfirm;
    private final int REQUEST_CODE_SCAN_GALLERY = 10003;
    private String photo_path;
    private ProgressDialog mProgress;
    private Bitmap scanBitmap;
    private TextView tvTitle;
    //Confirm Link 弹框
    private RelativeLayout rl_Link;
    private Button btn_confirm_link, btn_cancel_link;
    private LinearLayout ll_link;
    private TextView tv_link;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        //版本大于6.0的情况
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                // 方法 checkSelfPermission  在 android 6.0以下没有该方法
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
        tvLight = findViewById(R.id.tv_light);
        lightBtn = findViewById(R.id.btn_light);
        btn_light_top = findViewById(R.id.btn_light_top);
        lightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //版本大于6.0的情况
                if (Build.VERSION.SDK_INT >= 23) {
                    // 方法 checkSelfPermission  在 android 6.0以下没有该方法
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        isLight = !isLight;
                        //lightBtn.setSelected(isLight);
                        btn_light_top.setVisibility(isLight ? View.VISIBLE : View.INVISIBLE);
                        if (isLight) {
                            CameraManager.get().openLight();
                            tvLight.setText("Tap to turn light off");
                        } else {
                            CameraManager.get().offLight();
                            tvLight.setText("Tap to turn light on");
                        }
                    }
                } else {
                    isLight = !isLight;
                    //lightBtn.setSelected(isLight);
                    btn_light_top.setVisibility(isLight ? View.VISIBLE : View.INVISIBLE);
                    if (isLight) {
                        CameraManager.get().openLight();
                        tvLight.setText("Tap to turn light off");
                    } else {
                        CameraManager.get().offLight();
                        tvLight.setText("Tap to turn light on");
                    }
                }
            }
        });
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = findViewById(R.id.viewfinder_view);
        ImageButton mButtonBack = findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MipcaActivityCapture.this.finish();
            }
        });
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        tvPhoto = findViewById(R.id.tv_photo);
        tvPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开手机中的相册
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                innerIntent.setType("image/*");
                startActivityForResult(innerIntent, REQUEST_CODE_SCAN_GALLERY);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_GALLERY:
                    handleAlbumPic(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理选择的图片
     *
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleAlbumPic(Intent data) {
        //获取选中图片的路径
//		photo_path = UriUtil.getRealPathFromUri(MipcaActivityCapture.this, data.getData());
        photo_path = Util.getPath(getApplicationContext(), data.getData());
        mProgress = new ProgressDialog(MipcaActivityCapture.this);
        mProgress.setMessage(getString(R.string.scan));
        mProgress.setCancelable(false);
        mProgress.show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppHelper.dismissDialog(mProgress);
                Result result = scanningImage(photo_path);
                if (result != null) {
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
                    bundle.putString(CodeUtils.RESULT_STRING, result.getText());
                    resultIntent.putExtras(bundle);
                    MipcaActivityCapture.this.setResult(RESULT_OK, resultIntent);
                    MipcaActivityCapture.this.finish();
                    finish();
//					Toast.makeText(MipcaActivityCapture.this, result.getText(), Toast.LENGTH_SHORT).show();
                } else {
//					finish();
                    Toast.makeText(MipcaActivityCapture.this, getString(R.string.sbcw), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 授权回调处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (checkPermissionResult(grantResults)) {
        } else {
            showMessage("Permission denied", 2);
        }
    }

    /**
     * 检测请求结果码判定是否授权
     *
     * @param grantResults
     * @return
     */
    private boolean checkPermissionResult(int[] grantResults) {
        if (grantResults != null) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initView() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    void restartCamera() {
        viewfinderView.setVisibility(View.VISIBLE);
        SurfaceView surfaceView = findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);
        // 恢复活动监控器
    }

    /**
     * 使Zxing能够继续扫描
     */
    public void continuePreview() {
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return 0;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @Override
    protected void initViewsAndEvents() {
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(MipcaActivityCapture.this, getString(R.string.srcerror), Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, resultString);
            resultIntent.putExtras(bundle);
            MipcaActivityCapture.this.setResult(RESULT_OK, resultIntent);
            MipcaActivityCapture.this.finish();
        }
        //	MipcaActivityCapture.this.finish();
    }

    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 重新扫描
            continuePreview();
        }
    };

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}
