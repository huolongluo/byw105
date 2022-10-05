package huolongluo.byw.byw.ui.activity.bindgoogle;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/13.
 */
public class BindGoogleThreeActivity extends BaseActivity {
    private String totpKey;
    private String device_name;
    @BindView(R.id.ll_back)
    LinearLayout back;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_verificationCode)
    EditText et_verificationCode;
    // @BindView(R.id.google_code)
    //  ItemPasswordLayoutNew googleCode;
    @BindView(R.id.tv_next)
    TextView tvNext;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_bind_google_three;
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initViewsAndEvents() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            totpKey = getIntent().getExtras().getString("totpKey");
            device_name = getIntent().getExtras().getString("device_name");
        }
        eventClick(back).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvNext).subscribe(o -> {
            BindGoogle();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void BindGoogle() {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        if (loginToken == null) {
            showMessage(getString(R.string.please_login), 2);
            return;
        }
        if (totpKey == null) {
            showMessage(getString(R.string.my_empty), 2);
            return;
        }
        if (et_verificationCode.getText().toString() == null) {
            showMessage(getString(R.string.please_ingoo), 2);
            return;
        }
        if (etPwd.getText().toString().isEmpty()) {
            showMessage(getString(R.string.please_inpsw), 2);
            return;
        }
        RSACipher rsaCipher = new RSACipher();
        String body = "totpKey=" + URLEncoder.encode(totpKey) + "&totpCode=" + URLEncoder.encode(et_verificationCode.getText().toString()) + "&password=" + URLEncoder.encode(etPwd.getText().toString());
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", loginToken);
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
//        params.put("totpKey",totpKey);
        //  params.put("totpCode",googleCode.getStrPassword());
//        params.put("password",etPwd.getText().toString());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_BIND_GOOGLE);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_BIND_GOOGLE, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        showMessage(getString(R.string.bind_suc), 2);
                        // SnackBarUtils.ShowBlue(BindGoogleThreeActivity.this,"绑定成功");
                        BindGoogleTwoActivity.isFinish = true;
                        BindGoogleOneActivity.isFinish = true;
                        SPUtils.saveBoolean(BindGoogleThreeActivity.this, SPUtils.IS_BIND_GOOGLE, true);
                        //Share.get().saveIsBindGoogle(true);
                        finish();
                    } else {
                        showMessage(value, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
