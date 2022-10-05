package huolongluo.byw.byw.ui.activity.scan;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.ImmersedStatusbarUtils;
/**
 * Created by LS on 2018/7/14.
 */
public class ScanActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.toolbar_center_title)
    TextView toolbar_center_title;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.lin1)
    RelativeLayout lin1;
    @BindView(R.id.btn_light)
    Button btn_light;
    private boolean isLight;
    private Camera camera;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_scan;
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
        getWindow().setFlags(WindowManager.LayoutParams.ALPHA_CHANGED, WindowManager.LayoutParams.ALPHA_CHANGED);
        initToolBar();
        eventClick(iv_left).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        CaptureFragment captureFragment = new CaptureFragment();
        Log.d("bug", "onCreate: " + 2);
        CodeUtils.setFragmentArgs(captureFragment, R.layout.camera_new);  //设置自定义扫码界面
        captureFragment.setAnalyzeCallback(analyzeCallback);
        Log.d("bug", "onCreate: " + 3);
        //R.id.fl_zxing_container  对应   setContentView 布局中的  Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.myCamera, captureFragment).commitAllowingStateLoss();  // 替换setContenView设置的布局中的  ID为myCamera
        eventClick(btn_light).subscribe(o -> {
            //版本大于6.0的情况
            if (Build.VERSION.SDK_INT >= 23) {
                // 方法 checkSelfPermission  在 android 6.0以下没有该方法
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    isLight = !isLight;
                    btn_light.setSelected(isLight);
//                    flashlightUtils();
                    if (isLight) {
                        openFlashlight();
                    } else {
                        closeFlashlight();
                    }
                }
            } else {
                isLight = !isLight;
                btn_light.setSelected(isLight);
//                flashlightUtils();
                if (isLight) {
                    openFlashlight();
                } else {
                    closeFlashlight();
                }
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }
    };

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1);
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(R.string.bb16);
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //grab seurface view and callback
//        cameraView = (CameraSurfaceView) findViewById(R.id.cameraView);
        try {
            camera = Camera.open();
//            cameraView.setCamera(camera);
            //release previous autofocus and assign new one
            camera.cancelAutoFocus();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    // TODO Auto-generated method stub
                }
            });
        } catch (Exception e) {
            //had an issue accessing the camera prompt user
            //TODO create user prompt
            e.printStackTrace();
        }
    }

    Camera mCamera;
    Camera.Parameters mParameters;

    // 检测当前设备是否配置闪光灯
    boolean checkFlashlight(View view) {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, R.string.bb17, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // 打开闪光灯
    void openFlashlight() {
        try {
            mCamera = Camera.open();
            int textureId = 0;
            mCamera.setPreviewTexture(new SurfaceTexture(textureId));
            mCamera.startPreview();
            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // 关闭闪光灯
    void closeFlashlight() {
        if (mCamera != null) {
            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
