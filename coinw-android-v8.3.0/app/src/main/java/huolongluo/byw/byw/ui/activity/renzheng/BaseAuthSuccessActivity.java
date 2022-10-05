package huolongluo.byw.byw.ui.activity.renzheng;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcodes.utils.ToastUtils;
import com.google.gson.Gson;
import com.legend.common.util.StatusBarUtils;
import com.liuzhongjun.videorecorddemo.util.CameraApp;

import java.util.HashMap;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.C2AuthSuccessfulEntity;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.Util;
import huolongluo.bywx.utils.PermissionUtils;
import okhttp3.Request;

public class BaseAuthSuccessActivity extends AppCompatActivity implements View.OnClickListener {
    private Button toFB;//跳转买币
    private Button toBB;//跳转币币
    private LinearLayout toSeniorCertification;//跳转币币
    private TextView title_tv;
    private TextView des;
    private static int REQUEST_PERMISSION_CODE = 1;
    private int nationality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(com.liuzhongjun.videorecorddemo.R.layout.basic_vip_success_activity);
        nationality=getIntent().getIntExtra("nationality",1);
        initView();
        initData();
    }

    private void initData() {
        HashMap<String, String> parmas = new HashMap<>();
        parmas.put("body", UserInfoManager.getEnCodeToken(UserInfoManager.getToken()));
        parmas.put("loginToken", CameraApp.token);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.C2_DES, parmas, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {
                C2AuthSuccessfulEntity c2AuthSuccessfulEntity = new Gson().fromJson(result, C2AuthSuccessfulEntity.class);
                if (c2AuthSuccessfulEntity.getCode() == 200) {
                    des.setText(String.format(getString(R.string.camera_a3), c2AuthSuccessfulEntity.getValue().getKYC_DAY_SELL(),
                            c2AuthSuccessfulEntity.getValue().getKYC_SINGLE_SELL()));
                }
            }
        });
    }

    private void initView() {
        des = findViewById(R.id.des);
        toFB = findViewById(R.id.toFb);
        toBB = findViewById(R.id.toBb);
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.base_02);
        toSeniorCertification = findViewById(R.id.toSeniorCertification);
        findViewById(com.liuzhongjun.videorecorddemo.R.id.back_iv).setOnClickListener(view -> finish());
        toFB.setOnClickListener(this);
        toBB.setOnClickListener(this);
        toSeniorCertification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toFb:
                toTransfer(1);
                break;
            case R.id.toBb:
                toTransfer(2);
                break;
            case R.id.toSeniorCertification:
                if (requestpermissions(this)) {
                    Util.gotoSeniorCertified(this,nationality);
                }
                break;
        }
    }

    private boolean requestpermissions(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(activity, PermissionUtils.ALL_NEED_PERMISSIONS, REQUEST_PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * type  =1 为买币交易  2为币币
     *
     * @param type
     */
    private void toTransfer(int type) {
        Intent intent = new Intent(BaseAuthSuccessActivity.this, MainActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("from", "OtcTransferActivity");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ToastUtils.showShortToast(R.string.permossion_denied);
            } else {
                Util.gotoSeniorCertified(this,nationality);
                finish();
            }
        }
    }
}
