package huolongluo.byw.byw.ui.activity.setchangepsw;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.legend.model.enumerate.common.CommonCodeTypeEnum;
import com.android.legend.ui.bottomSheetDialogFragment.CommonSmsGoogleVerifyBottomDialogFragment;
import com.android.legend.util.InputUtil;
import com.android.legend.view.edittext.CommonEditText;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetNickActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetUserInfoActivity;
import huolongluo.byw.reform.c2c.oct.activity.PaymentAccountActivityNew;
import huolongluo.byw.reform.safecenter.ReserTradePswActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.PasswordChecker;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pwd.PwdUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by 火龙裸先生 on 2018/1/30.
 * 修改交易密码
 */
public class SetChangePswActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.rl_now_psw)
    LinearLayout rl_now_psw;
    @BindView(R.id.et_now_psw)
    CommonEditText et_now_psw;
    @BindView(R.id.et_pwd01)
    CommonEditText et_pwd01;
    @BindView(R.id.et_pwd02)
    CommonEditText et_pwd02;
    @BindView(R.id.btn_sure)
    TextView btn_sure;
    @BindView(R.id.reset_tv)
    TextView reset_tv;
    @BindView(R.id.clIntensity)
    ConstraintLayout clIntensity;
    @BindView(R.id.tvIntensity)
    TextView tvIntensity;
    @BindView(R.id.tvNewPwdError2)
    TextView tvNewPwdError2;
    @BindView(R.id.ivPwd1)
    ImageView ivPwd1;
    @BindView(R.id.ivPwd2)
    ImageView ivPwd2;
    @BindView(R.id.ivPwd3)
    ImageView ivPwd3;
    @BindView(R.id.vNowPwd)
    View vNowPwd;
    @BindView(R.id.vNewPwd)
    View vNewPwd;
    @BindView(R.id.vNewPwd2)
    View vNewPwd2;
    private String setChangePswTitle = "";
    private boolean isBindGoogle;
    private int type;//1为修改登陆密码 2设置交易密码3为修改交易密码
    private long stopMills = System.currentTimeMillis();
    private PasswordChecker passwordChecker;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_set_traders_password;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        passwordChecker = new PasswordChecker();
        reset_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserInfoManager.getUserInfo().isHasC3Validate() && !UserInfoManager.getUserInfo().isHasC2Validate()) {
                    if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                        MToast.showButton(SetChangePswActivity.this, getString(R.string.bb19), 1);
                        return;
                    }
                    MToast.showButton(SetChangePswActivity.this, getString(R.string.bb20), 1);
                    return;
                }
                if (!isBindGoogle) {
                    MToast.showButton(SetChangePswActivity.this, getString(R.string.bb21), 1);
                    return;
                }
                startActivity(ReserTradePswActivity.class);
            }
        });
        if (getBundle() != null) {
            setChangePswTitle = getBundle().getString("setChangePswTitle");
            isBindGoogle = getBundle().getBoolean("isBindGoogle");
        }
        title_tv.setText(setChangePswTitle);
        initView();
        eventClick(back_iv).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        et_now_psw.getEt().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                vNowPwd.setSelected(hasFocus);
            }
        });
        et_pwd01.getEt().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                vNewPwd.setSelected(hasFocus);
            }
        });
        et_pwd02.getEt().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                vNewPwd2.setSelected(hasFocus);
            }
        });
        initTextWatcher();
        eventClick(btn_sure).subscribe(o -> {
            if (FastClickUtils.isFastClick(500)) return;
            if (!InputUtil.checkInputPwd(this, et_pwd01.getText().toString().trim(), passwordChecker, null))
                return;
            if (!InputUtil.checkInputPwd(this, et_pwd01.getText().toString().trim(), et_pwd02.getText().toString().trim(), passwordChecker, null))
                return;
            CommonSmsGoogleVerifyBottomDialogFragment.Companion.newInstance(CommonCodeTypeEnum.TYPE_MODIFY_TRADE_PWD.getType(), new CommonSmsGoogleVerifyBottomDialogFragment.CodeListener() {
                @Override
                public void getCode(@NotNull String code, boolean isSms) {
                    changePwd(code,isSms);
                }
            } ).show(getSupportFragmentManager(), "Dialog");
        });
    }

    private boolean isModifyTradePwd() {
        return TextUtils.equals(setChangePswTitle, getString(R.string.bb23));
    }

    private void initTextWatcher() {
        et_pwd01.getEt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                InputUtil.refreshIntensity(SetChangePswActivity.this, et_pwd01.getEt(), clIntensity, tvIntensity, ivPwd1, ivPwd2, ivPwd3, passwordChecker);
                InputUtil.checkInputPwd(SetChangePswActivity.this, s.toString().trim(), et_pwd02.getText().toString().trim(), passwordChecker, tvNewPwdError2);
                if(TextUtils.isEmpty(tvNewPwdError2.getText().toString())){
                    tvNewPwdError2.setVisibility(View.GONE);
                }else{
                    tvNewPwdError2.setVisibility(View.VISIBLE);
                }
                checkEnable();
            }
        });
        et_pwd02.getEt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                InputUtil.checkInputPwd(SetChangePswActivity.this, et_pwd01.getText().toString().trim(), s.toString().trim(), passwordChecker, tvNewPwdError2);
                if(TextUtils.isEmpty(tvNewPwdError2.getText().toString())){
                    tvNewPwdError2.setVisibility(View.GONE);
                }else{
                    tvNewPwdError2.setVisibility(View.VISIBLE);
                }
                checkEnable();
            }
        });
    }

    private void checkEnable() {
        btn_sure.setEnabled((isModifyTradePwd()?!TextUtils.isEmpty(et_now_psw.getText().toString()):true) && !TextUtils.isEmpty(et_pwd01.getText().toString()) && !TextUtils.isEmpty(et_pwd02.getText().toString()));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void changePwd(String code,boolean isSms) {
        HashMap<String, String> params = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body;
        String password1=et_now_psw.getText().toString();
        String password2_2=et_pwd01.getText().toString();
        String vcode=isSms?code:"";
        String totpCode=isSms?"":code;
        if (isBindGoogle) {
            body = "password1=" + URLEncoder.encode(password1) + "&password2=" + URLEncoder.encode(password2_2) + "&vcode=" + URLEncoder.encode(vcode) + "&totpCode=" + URLEncoder.encode(totpCode) + "&type=" + URLEncoder.encode(String.valueOf(type));
        } else {
            body = "type=" + URLEncoder.encode(String.valueOf(type)) + "&password1=" + URLEncoder.encode(password1) + "&password2=" + URLEncoder.encode(password2_2) + "&vcode=" + URLEncoder.encode(vcode);
        }
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", SPUtils.getLoginToken());
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
        DialogManager2.INSTANCE.showProgressDialog(SetChangePswActivity.this);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.changePassword, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager2.INSTANCE.dismiss();
                e.printStackTrace();
                SnackBarUtils.ShowRed(SetChangePswActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager2.INSTANCE.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        Intent intent = new Intent();
                        intent.putExtra("message", value);
                        setResult(212, intent);
                        UserInfoManager.getUserInfo().setIsHasTradePWD(1);
                        close();
                        EventBus.getDefault().post(new Event.refreFansUpInfo());
                    } else {
                        showMessage(value, 1);
                    }
                } catch (JSONException e) {
                }
            }
        });
    }

    //继续下一步设置
    void nextSet() {
        if (!UserInfoManager.getUserInfo().isHasC2Validate()) {//没有实名认证
            Intent intent = new Intent(this, RenZhengBeforeActivity.class);
            startActivity(intent);
            finish();
        } else if (secondCheck()) {//风控通过
            if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 2) {//设置otc的个人基本信息
                if (TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname())) {
                    Intent intent = new Intent(this, OtcSetNickActivity.class);
                    intent.putExtra("fromToc", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, OtcSetUserInfoActivity.class);
                    intent.putExtra("nickName", UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname());
                    intent.putExtra("fromToc", true);
                    startActivity(intent);
                }
            } else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 3) {//没有设置支付信息
                startActivity(PaymentAccountActivityNew.class);
            } else {
                finish();
            }
        }
    }

    boolean secondCheck() {
        if (!UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isAllowOtc()) {
            DialogUtils.getInstance().showTwoButtonDialog(this, getString(R.string.bb45), getString(R.string.bb46), getString(R.string.bb47));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            return false;
        } else if (!UserInfoManager.getOtcUserInfoBean().getData().isOpenLimit()) {
            return true;
        } else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isNewUser()) {
            DialogUtils.getInstance().showTwoButtonDialog(this, getString(R.string.bb48), getString(R.string.bb49), getString(R.string.bb50));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                        finish();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                        finish();
                    }
                }
            });
            return false;
        } else {
            return true;
        }
    }

    private void initView() {
        if (TextUtils.equals(setChangePswTitle, getString(R.string.bb51))) {
            rl_now_psw.setVisibility(View.VISIBLE);
            et_now_psw.getEt().setHint(R.string.bb52);
            type = 3;
        } else if (TextUtils.equals(setChangePswTitle, getString(R.string.bb53))) {
            rl_now_psw.setVisibility(View.GONE);
            type = 2;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - stopMills > 60 * 1000L) {
            clearPwd();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMills = System.currentTimeMillis();
    }

    /**
     * 清除本页面的pwd数据
     */
    private void clearPwd() {
        try {
            Logger.getInstance().debug("clear pwd!");
            if (et_now_psw != null) {
                et_now_psw.getEt().setText("");
            }
            if (et_pwd01 != null) {
                et_pwd01.getEt().setText("");
            }
            if (et_pwd02 != null) {
                et_pwd02.getEt().setText("");
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    @Override
    protected void onDestroy() {
        AppHelper.unsubscribe(subscription);
        clearPwd();
        super.onDestroy();
    }
}
