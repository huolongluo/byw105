package huolongluo.byw.byw.ui.activity.renzheng;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcodes.utils.ToastUtils;
import com.legend.common.util.StatusBarUtils;
import com.liuzhongjun.videorecorddemo.R;
import com.liuzhongjun.videorecorddemo.util.CameraUtils;

import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.utils.PermissionUtils;
public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AuthActivity";
    private TextView status1;
    private TextView status2;
    private UserInfoBean userInfoBean;
    private RelativeLayout base_auth1;
    private RelativeLayout high_auth;
    private ImageView img1;
    private ImageView img2;
    private ImageView arrow1;
    private ImageView arrow2;
    private static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.auth_activity);
        initView();
        initDate();
    }

    private void initDate() {
        userInfoBean = UserInfoManager.getUserInfo();
        C2Validate();
        C3Validate();
        if (userInfoBean.isHasC2Validate() && !userInfoBean.isHasC3Validate() && userInfoBean.getC3status() == 0) {
            Intent intent=new Intent(this, BaseAuthSuccessActivity.class);
            intent.putExtra("nationality",userInfoBean.getNationality());
            startActivity(intent);
        }
    }

    private void C2Validate() {
        //TODO 可不处理 userInfoBean.getKsTokenUrl()不为空的情况
        img1.setImageDrawable(getResources().getDrawable(R.mipmap.t));
        if (userInfoBean.isHasC2Validate()) {
            status1.setText(R.string.camera_e3);
            img1.setImageDrawable(getResources().getDrawable(R.mipmap.d));
            img1.setVisibility(View.VISIBLE);
            base_auth1.setClickable(false);
            arrow1.setVisibility(View.GONE);
        } else if (userInfoBean.isPostC2Validate()) {
            status1.setText(R.string.camera_e4);
            img1.setVisibility(View.VISIBLE);
            base_auth1.setClickable(false);
            arrow1.setVisibility(View.GONE);
        } else {
            status1.setText(R.string.camera_c2);
            img1.setVisibility(View.GONE);
            base_auth1.setClickable(true);
            arrow1.setVisibility(View.VISIBLE);
        }
    }

    private void C3Validate() {
        //TODO 可不处理 userInfoBean.getKsTokenUrl()不为空的情况
        img2.setImageDrawable(getResources().getDrawable(R.mipmap.t));
        if (userInfoBean.isHasC3Validate()) {
            status2.setText(R.string.camera_e3);
            img2.setImageDrawable(getResources().getDrawable(R.mipmap.d));
            img2.setVisibility(View.VISIBLE);
            high_auth.setClickable(false);
            arrow2.setVisibility(View.GONE);
        } else if (userInfoBean.getC3status() == 0) {
            status2.setText(R.string.camera_c2);
            img2.setImageDrawable(getResources().getDrawable(R.mipmap.t));
            img2.setVisibility(View.GONE);
            high_auth.setClickable(true);
            arrow2.setVisibility(View.VISIBLE);
        } else if (userInfoBean.getC3status() == 1) {
            status2.setText(R.string.camera_e4);
            img2.setVisibility(View.VISIBLE);
            high_auth.setClickable(false);
            arrow2.setVisibility(View.GONE);
        } else if (userInfoBean.getC3status() == 2) {
            status2.setText(R.string.camera_e5);
            high_auth.setClickable(true);
            img2.setVisibility(View.VISIBLE);
            Intent intent=new Intent(this, AuthFailureActivity.class);
            intent.putExtra("nationality",userInfoBean.getNationality());
            startActivity(intent);
            arrow2.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        TextView title = findViewById(R.id.title_tv);
        title.setText(R.string.camera_e6);
        findViewById(R.id.back_iv).setOnClickListener(this);
//        base_auth = findViewById(R.id.base_auth);
        base_auth1 = findViewById(R.id.base_auth1);
        base_auth1.setOnClickListener(this);
        high_auth = findViewById(R.id.high_auth);
        high_auth.setOnClickListener(this);
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        arrow1 = findViewById(R.id.iv_arrow1);
        arrow2 = findViewById(R.id.iv_arrow2);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //requestpermissions(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_iv) {
            finish();
        } else if (view.getId() == R.id.base_auth1) {
            if (!userInfoBean.isHasC2Validate()) {
                if (!TextUtils.isEmpty(userInfoBean.getKsTokenUrl())) {
                    Intent intent = new Intent(this, WebviewActivity.class);
                    intent.putExtra("url", userInfoBean.getKsTokenUrl());
                    startActivity(intent);
                    return;
                }
            }
            CameraUtils.toBaseReZheng(this);
        } else if (view.getId() == R.id.high_auth) {
            if (userInfoBean.isHasC2Validate() && !userInfoBean.isHasC3Validate()) {
                if (requestpermissions(this)) {
                    Util.gotoSeniorCertified(this,userInfoBean.getNationality());
                }
            } else if (!userInfoBean.isHasC2Validate()) {
                MToast.show(this,getString(R.string.camera_base_01));
            }
        }
    }

    private boolean requestpermissions(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PermissionUtils.ALL_NEED_PERMISSIONS, REQUEST_PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShortToast(getString(R.string.permossion_denied));
            } else {
                Util.gotoSeniorCertified(this,userInfoBean.getNationality());
            }
        }
    }
}
