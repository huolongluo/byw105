package huolongluo.byw.byw.ui.fragment.maintab05;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.LoginBean;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.MToast;
import okhttp3.Request;
/**
 * Created by hy on 2018/9/17 0017.
 * 扫码登录页面
 */
public class ScanQRLoginActivity extends AppCompatActivity {
    private static final String TAG = "ScanQRLoginActivity";
    private Button confirm_login_bn;
    private TextView cancel_tv;
    private TextView title_tv;
    private TextView toolbar_center_title;
    private ImageView back_iv;
    private String url;
    private String loginName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr_login);
        initView();
        initData();
    }

    private void initData() {
        url = getIntent().getStringExtra("loginUrl");
        loginName =UserInfoManager.getUserInfo().getLoginName();
        Logger.getInstance().debug(TAG,"url:"+url);
    }

    private void initView() {
        confirm_login_bn = findViewById(R.id.confirm_login_bn);
        cancel_tv = findViewById(R.id.cancel_tv);
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.ee41);
        confirm_login_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmFarLogin();
            }
        });
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                farLogin(url, 0);
                finish();
            }
        });
        back_iv = findViewById(R.id.back_iv);
        back_iv.setVisibility(View.VISIBLE);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (!isLogin) {
                    farLogin(url, 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isLogin) {
            farLogin(url, 0);
        }
    }

    private boolean isLogin;

    //二维码登录
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void farLogin(String qrurl, int type) {
        // params.put("loginToken",Share.get().getLogintoken());
        String logintoken = SPUtils.getLoginToken();
        if (logintoken.isEmpty()) {
//            showMessage("请先登录",2);
            return;
        }
        String body = null;
        RSACipher rsaCipher = new RSACipher();
        String logintoken1 = URLEncoder.encode(logintoken);
        body = "type=" + type;
        try {
            body = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            body = URLEncoder.encode(body);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        String url = qrurl + "&body=" + body;
        if (type == 0) {
            isLogin = true;
        }
        OkhttpManager.getAsync(url, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                // showMessage("扫码", 1);
                Log.e("扫码requestFailure", "扫码失败");
                e.printStackTrace();
                // MToast.show(ScanQRLoginActivity.this,"网络请求超时，请稍后重试",1);
                //  SnackBarUtils.ShowRed(ScanQRLoginActivity.this,"网络请求超时，请稍后重试");
                Intent intent = new Intent();
                intent.putExtra("message", R.string.ee42);
                intent.putExtra("type", 1);
                setResult(212, intent);
                finish();
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("扫码", "requestSuccess " + result);
                try {
                    JSONObject jsonObject = JSON.parseObject(result);//0 取消登录成功  1 设置登录状态成功  -1 扫码成功   -2 当前操作已经失效
                    if (jsonObject != null) {
                        int state = jsonObject.getIntValue("status");
                        Log.i("扫码", "requestSuccessss " + result);
                        if (state == -1) {
                        } else if (state == 1) {
                            //  MToast.show(ScanQRLoginActivity.this,"登录成功",2);
                        } else if (state == -2) {
                            MToast.show(ScanQRLoginActivity.this, getString(R.string.ee52), 1);
                            finish();
                        }
                    } else {
                        MToast.show(ScanQRLoginActivity.this, getString(R.string.ee53), 1);
                        finish();
                    }
                } catch (Exception e) {
                    MToast.show(ScanQRLoginActivity.this, getString(R.string.ee51), 1);
                    finish();
                    e.printStackTrace();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void confirmFarLogin() {
        String logintoken = SPUtils.getLoginToken();
        if (logintoken.isEmpty()) {
//            showMessage("请先登录",2);
            return;
        }
        String body = null;
        RSACipher rsaCipher = new RSACipher();
        String logintoken1 = URLEncoder.encode(logintoken);
        // String type = URLEncoder.encode("1");
        // String body = "email="+phone1+"&password="+pwd1+"&phoneCode="+code1;
        body = "loginName=" + loginName + "&type=1";
        /// body = "type="+type;
        try {
            Logger.getInstance().debug(TAG,"body:"+body);
            body = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            body = URLEncoder.encode(body);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        String url = this.url + "&body=" + body;
        OkhttpManager.getAsync(url, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                //  MToast.show(ScanQRLoginActivity.this,"登录失败",2);
                //SnackBarUtils.ShowRed(ScanQRLoginActivity.this,"登录失败,网络请求超时");
                Intent intent = new Intent();
                intent.putExtra("message", getString(R.string.ee55));
                intent.putExtra("type", 1);
                setResult(212, intent);
                Log.w("扫码requestFailuress", "扫码失败");
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = JSON.parseObject(result);//0 取消登录成功  1 设置登录状态成功  -1 扫码成功   -2 当前操作已经失效
                    int state = jsonObject.getIntValue("status");
                    Log.i("扫码", "requestSuccessss " + result);
                    if (state == 1) {
                        isLogin = true;
                        MToast.show(ScanQRLoginActivity.this, getString(R.string.ee50), 2);
                    } else if (state == -2) {
                        isLogin = true;
                        MToast.show(ScanQRLoginActivity.this, getString(R.string.ee49), 1);
                    }
                    finish();
                    LoginBean loginBean = null;
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.putExtra("message", getString(R.string.ee51));
                    intent.putExtra("type", 1);
                    setResult(212, intent);
                    e.printStackTrace();
                }
            }
        });
    }
}
