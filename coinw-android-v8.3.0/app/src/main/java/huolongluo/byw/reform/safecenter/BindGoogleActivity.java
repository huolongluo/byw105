package huolongluo.byw.reform.safecenter;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.bindgoogle.BindGoogleOneActivity;
import huolongluo.byw.byw.ui.activity.bindgoogle.BindGoogleTwoActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CreateQRImage;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.MToast;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/11/21 0021.
 */

public class BindGoogleActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BindGoogleActivity";
    @BindView(R.id.key_tv)
    TextView key_tv;
    @BindView(R.id.tv_down)
    TextView tv_down;
    @BindView(R.id.et_google_code)
    EditText et_google_code;
    @BindView(R.id.cope_google_code)
    TextView cope_google_code;
    @BindView(R.id.tv_next)
    TextView tv_next;
    @BindView(R.id.qr_code_iv)
    ImageView qr_code_iv;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;


    String key;
    String device_name;
    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bind_google);


        unbinder = ButterKnife.bind(this);

        tv_down.setOnClickListener(this);
        cope_google_code.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        back_iv.setOnClickListener(this);
        title_tv.setText(getString(R.string.bind_google));
        getKey();
    }

    private CreateQRImage mCreateQRImage = new CreateQRImage();

    //获取Google秘钥
    private void getKey() {

        Map<String, String> params = new HashMap<>();

        params.put("type", "1");
        params = OkhttpManager.encrypt(params);

        String loginToken = UserInfoManager.getToken();

        if (loginToken == null) {


            return;
        }
        params.put("loginToken", loginToken);
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_GOOGLEAUTH);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_GOOGLEAUTH, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {//{"device_name":"coinw:624382750@qq.com","code":0,"totpKey":"HE7CQC7DBX4UXDLX"}
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.d("获取Google认证", result);
                    int code = jsonObject.getInt("code");
                    String name = jsonObject.getString("device_name");
                    // device_name = jsonObject.getString("device_name");
                    if (code == 0) {
                        key = jsonObject.getString("totpKey");
                        key_tv.setText(key);
                        qr_code_iv.setImageBitmap(mCreateQRImage.createQRImage("otpauth://totp/" + name + "?secret=" + key, qr_code_iv.getWidth(), qr_code_iv.getHeight(),false));

                    } else {
                        String value = jsonObject.getString("value");
                        key_tv.setText("");
                        MToast.showButton(BindGoogleActivity.this, value, 1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MToast.showButton(BindGoogleActivity.this, getString(R.string.qs52), 1);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_down:
                toCopy();

                break;
            case R.id.cope_google_code:
                paste();

                break;
            case R.id.tv_next:
                BindGoogle();
                break;
            case R.id.back_iv:
                finish();
                break;
        }
    }

    private void BindGoogle() {


        if (TextUtils.isEmpty(key)) {
            MToast.show(this, getString(R.string.qs53), 2);
            return;
        }
        if (TextUtils.isEmpty(et_google_code.getText().toString().trim())) {
            //   showMessage("请输入Google验证码!",2);
            MToast.show(this, getString(R.string.qs54), 2);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("totpKey", key);
        params.put("totpCode", et_google_code.getText().toString());
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_BIND_GOOGLE);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_BIND_GOOGLE, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().report("绑定google验证码接口返回异常errorMsg:"+errorMsg,TAG+"-BindGoogle-requestFailure");
                MToast.show(BindGoogleActivity.this,errorMsg,1);
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.d("获取Google认证", result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {

                        // SnackBarUtils.ShowBlue(BindGoogleThreeActivity.this,"绑定成功");

                        BindGoogleTwoActivity.isFinish = true;
                        BindGoogleOneActivity.isFinish = true;
                        SPUtils.saveBoolean(BindGoogleActivity.this,SPUtils.IS_BIND_GOOGLE,true);
                        //Share.get().saveIsBindGoogle(true);
                        finish();
                    } else {
                        Logger.getInstance().report("绑定google验证码接口返回异常value:"+value,TAG+"-BindGoogle-requestSuccess");
                        MToast.show(BindGoogleActivity.this,value,1);
                    }
                } catch (JSONException e) {
                    Logger.getInstance().report(e);
                    MToast.show(BindGoogleActivity.this, getString(R.string.qs55), 1);
                    e.printStackTrace();
                }
            }
        });
    }

    void paste() {


        String text = NorUtils.getClipboardText(this);
        et_google_code.setText(text);
        et_google_code.setSelection(text.length());
    }

    private void toCopy() {
        key = key_tv.getText().toString();
        ClipboardManager copy = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        if (key != null) {
            copy.setText(key);
            //  showMessage("复制成功", 1);

            MToast.showButton(this, getString(R.string.qs56), 1);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
