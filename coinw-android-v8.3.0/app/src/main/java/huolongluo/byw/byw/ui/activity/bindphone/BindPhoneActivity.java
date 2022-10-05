package huolongluo.byw.byw.ui.activity.bindphone;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.CommonBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.byw.ui.activity.setchangepsw.SetChangePswActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetNickActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetUserInfoActivity;
import huolongluo.byw.reform.c2c.oct.activity.PaymentAccountActivityNew;
import huolongluo.byw.reform.login_regist.AreaCodeChoiceActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.LogicLanguage;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.config.ConfigurationUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by 火龙裸 on 2018/2/1.
 */
public class BindPhoneActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    /* @BindView(R.id.my_toolbar)
     Toolbar my_toolbar;
     @BindView(R.id.lin1)
     RelativeLayout lin1;*/
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_phone_code)
    EditText et_phone_code;
    @BindView(R.id.tv_get_code)
    TextView tv_get_code;
    @BindView(R.id.getconde_new)
    TextView getconde_new;
    @BindView(R.id.et_google_code)
    EditText et_google_code;
    @BindView(R.id.code_new)
    EditText code_new;
    @BindView(R.id.btn_to_sure)
    TextView btn_to_sure;
    @BindView(R.id.phone_new)
    TextView phone_new;
    @BindView(R.id.googleCode)
    TextView googleCode;
    @BindView(R.id.rl_now_phone)
    LinearLayout rl_now_phone;
    @BindView(R.id.rl_google)
    RelativeLayout rl_google;
    @BindView(R.id.areaCode_ll1)
    LinearLayout areaCode_ll1;
    @BindView(R.id.areaCode_tv1)
    TextView areaCode_tv1;
    @BindView(R.id.areaCode_ll)
    LinearLayout areaCode_ll;
    @BindView(R.id.areaCode_tv)
    TextView areaCode_tv;
    private String phone;
    private String code;
    private String totpCode;
    private boolean isBind;
    private boolean isBindGoogle;
    private Subscription subscription, subscription2, subscription3;

    @Override
    protected int getContentViewId() {
        //  return R.layout.activity_bind_phone;
        return R.layout.activity_bindphone;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
    }

    boolean fromToc = false;//是否冲otc跳过来的

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        if (getIntent() != null) {
            isBind = getIntent().getBooleanExtra("isBindTelephone", false);
            isBindGoogle = getIntent().getBooleanExtra("isBindGoogle", false);
            fromToc = getIntent().getBooleanExtra("fromToc", false);
        }
        if (fromToc) {
            btn_to_sure.setText("下一步");
        }
        title_tv.setText(isBind ? getString(R.string.change_phone) : getString(R.string.bind_phone));
        rl_now_phone.setVisibility(isBind ? View.VISIBLE : View.GONE);
        rl_google.setVisibility(isBindGoogle ? View.VISIBLE : View.GONE);
        if (isBind) {
            et_phone.setText(UserInfoManager.getUserInfo().getTel());
            et_phone.setEnabled(false);
            areaCode_ll1.setVisibility(View.GONE);
            areaCode_ll.setVisibility(View.VISIBLE);
        } else {
            et_phone.setEnabled(true);
            areaCode_ll1.setVisibility(View.VISIBLE);
            areaCode_ll.setVisibility(View.GONE);
        }
        areaCode_ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BindPhoneActivity.this, AreaCodeChoiceActivity.class), 101);
            }
        });
        areaCode_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BindPhoneActivity.this, AreaCodeChoiceActivity.class), 102);
            }
        });
        eventClick(back_iv).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(googleCode).subscribe(o -> {
            String text = NorUtils.getClipboardText(this);
            et_google_code.setText(text);
            et_google_code.setSelection(text.length());
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_get_code).subscribe(o -> {
            int type = 3;
            if (isBind) {
                type = 3;
            } else {
                type = 2;
            }
            if (TextUtils.isEmpty(et_phone.getText().toString().trim())) {
                showMessage(getString(R.string.please_inpho), 2);
                return;
            } else if (et_phone.getText().toString().trim().length() > 15 || et_phone.getText().toString().trim().length() < 5) {
                showMessage(getString(R.string.phone_error), 2);
                return;
            } else {
                RSACipher rsaCipher = new RSACipher();
                //  String body = "phone=" + URLEncoder.encode(et_phone.getText().toString().trim()) + "&type=" + URLEncoder.encode("3")+"&areaCode="+areaCode_tv1.getText().toString().replace("+","");
                String body = "phone=" + URLEncoder.encode(et_phone.getText().toString().trim()) + "&type=" + URLEncoder.encode(type + "") + "&areaCode=" + areaCode_tv1.getText().toString().replace("+", "");
                try {
                    String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
//                params.put("body", body1);
                    Map<String, String> params = new HashMap<>();
                    params.put("body", body1);
                    params.put("loginToken", UserInfoManager.getToken());
                    getCode(params);
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
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        //新手机发送验证码
        eventClick(getconde_new).subscribe(o -> {
            sendNewPhoneCode();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(btn_to_sure).subscribe(o -> {
            if (!btn_registerable()) {
                return;
            }
            ;
            Map<String, String> params = new HashMap<>();
            if (rl_google.getVisibility() == View.VISIBLE) {
                params.put("totpCode", totpCode);
            }
            if (!isBind) {
                params.put("areaCode", areaCode_tv1.getText().toString().replace("+", ""));
                params.put("code", code);
                params.put("phone", phone);
                params = OkhttpManager.encrypt(params);
                bindPhone(params);
            } else {
                params.put("areaCode", areaCode_tv.getText().toString().replace("+", ""));
                params.put("phone", phone_new.getText().toString());
                params.put("oldcode", code);
                params.put("newcode", code_new.getText().toString());
                params = OkhttpManager.encrypt(params);
                validatePhone(params);
//              与以前程序员联系确认，以下内容为错误代码行，则删除掉（注释掉）
//                RSACipher rsaCipher = new RSACipher();
//                try {
//                    // String body = "phone=" + URLEncoder.encode(phone_new.getText().toString()) + "&newcode=" + URLEncoder.encode("888888")+ "&oldcode=" + URLEncoder.encode("888888") + "&totpCode=" + URLEncoder.encode(totpCode);
//                    String body = "phone=" + phone_new.getText().toString() + "&newcode=" + code_new.getText().toString() + "&oldcode=" + code + "&areaCode=86";
//                    String body1 = rsaCipher.encrypt(body, UrlConstants.publicKeys);
//                    //   subscription = bindPhonePresent.bindPhone(body1, Share.get().getLogintoken()); // 开始请求绑定
//                    //    bindPhone(body1);
//                    params.clear();
//                    params.put("body", URLEncoder.encode(body1));
//                    validatePhone(params);
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                } catch (NoSuchPaddingException e) {
//                    e.printStackTrace();
//                } catch (InvalidKeyException e) {
//                    e.printStackTrace();
//                } catch (IllegalBlockSizeException e) {
//                    e.printStackTrace();
//                } catch (BadPaddingException e) {
//                    e.printStackTrace();
//                }
            }
//            subscription = bindPhonePresent.bindPhone(phone, code, totpCode, Share.get().getLogintoken()); // 开始请求绑定
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        areaCode_tv.setText(LogicLanguage.getPhonePreByLanguage(this));
        areaCode_tv1.setText(LogicLanguage.getPhonePreByLanguage(this));
    }

    void getCode(Map<String, String> parmas) {
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_SMS, parmas, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(BindPhoneActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("value");
                    if (code == 0) {
                        SnackBarUtils.ShowBlue(BindPhoneActivity.this, getString(R.string.get_code_suc));
                        subscription = Observable.interval(0, 1, TimeUnit.SECONDS).limit(120).map(aLong -> 120 - aLong).doOnSubscribe(() -> {
                            if (tv_get_code != null) {
                                tv_get_code.setEnabled(false);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                            if (tv_get_code != null) {
                                tv_get_code.setText(aLong + " s");
                            }
                        }).doOnCompleted(() -> {
                            if (tv_get_code != null) {
                                tv_get_code.setText(getString(R.string.send_code));
                                tv_get_code.setEnabled(true);
                            }
                        }).doOnError(throwable -> {
                            Logger.getInstance().error(throwable);
                        }).subscribe();
                    } else {
                        MToast.show(BindPhoneActivity.this, msg, 1);
                    }
                } catch (JSONException e) {
                    //tvGetCodePhone.setEnabled(true);
                    SnackBarUtils.ShowRed(BindPhoneActivity.this, getString(R.string.send_fail));
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (data != null) {
                String areaCode = data.getStringExtra("areaCode");
                areaCode_tv1.setText(areaCode);
            }
        } else if (requestCode == 102) {
            if (data != null) {
                String areaCode = data.getStringExtra("areaCode");
                areaCode_tv.setText(areaCode);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void sendNewPhoneCode() {
        String phone = phone_new.getText().toString();
        if (TextUtils.isEmpty(phone) || phone.length() < 5 || phone.length() > 15) {
            MToast.showButton(this, getString(R.string.inputphone), 1);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "2");
        params.put("phone", phone);
        params.put("areaCode", areaCode_tv1.getText().toString().replace("+", ""));
        Log.e("params=", "=  " + UrlConstants.DOMAIN + "app/sendMessageCode?" + OkhttpManager.encryptGet(params) + "&loginToken=" + UserInfoManager.getToken());
        OkhttpManager.getAsync(UrlConstants.DOMAIN + "app/sendMessageCode.html?" + OkhttpManager.encryptGet(params) + "&loginToken=" + UserInfoManager.getToken(), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                MToast.showButton(BindPhoneActivity.this, errorMsg, 1);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code1 = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code1 == 0) {
                        subscription2 = Observable.interval(0, 1, TimeUnit.SECONDS).limit(61).map(aLong -> 120 - aLong).doOnSubscribe(() -> {
                            if (getconde_new != null) {
                                getconde_new.setEnabled(false);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                            if (getconde_new != null) {
                                getconde_new.setText(aLong + " s");
                            }
                        }).doOnCompleted(() -> {
                            if (getconde_new != null) {
                                getconde_new.setText(getString(R.string.send_code));
                                getconde_new.setEnabled(true);
                            }
                        }).doOnError(throwable -> {
                            Logger.getInstance().error(throwable);
                        }).subscribe();
                    } else {
                    }
                    MToast.showButton(BindPhoneActivity.this, value, 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void validatePhone(Map<String, String> parmas) {
        parmas.put("loginToken", UserInfoManager.getToken());
        Log.e("parmas==  ", "=  " + parmas);
        showProgress("");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.validatePhone, parmas, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                hideProgress();
                MToast.show(BindPhoneActivity.this, errorMsg, 1);
                Logger.getInstance().debug("validatePhone", errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                hideProgress();
                //{"code":"200","data":{"code":-1,"msg":"手机号码已存在"},"message":"执行成功"}
                try {
                    Logger.getInstance().debug("validatePhone", result);
                    JSONObject jsonObject1 = new JSONObject(result);
                    int code1 = jsonObject1.getInt("code");
                    String value = jsonObject1.getString("value");
                    if (code1 == 0) {
                        if (TextUtils.isEmpty(value)) {
                            value = getString(R.string.change_suc);
                        }
                        showMessage(value, 1);
                        // SnackBarUtils.ShowBlue(BindPhoneActivity.this,"绑定成功");
                        Intent intent = new Intent();
                        intent.putExtra("message", getString(R.string.change_suc));
                        intent.putExtra("phone", phone_new.getText().toString() + "");
                        setResult(212, intent);
                        close();
                    } else {
                        MToast.showButton(BindPhoneActivity.this, value, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MToast.show(BindPhoneActivity.this, getString(R.string.service_expec), 1);
                }
            }
        });
    }

    //继续下一步设置
    void nextSet() {
        if (UserInfoManager.getUserInfo().getIsHasTradePWD() != 1) {//没有交易密码
            Intent intent = new Intent(this, SetChangePswActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("setChangePswTitle", "设置交易密码");
            bundle.putBoolean("isBindGoogle", isBindGoogle);
            bundle.putBoolean("isBindTelephone", true);
            bundle.putBoolean("fromToc", true);
            intent.putExtra("bundle", bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } else if (!UserInfoManager.getUserInfo().isHasC2Validate()) {//没有实名认证
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
            DialogUtils.getInstance().showTwoButtonDialog(this, getString(R.string.otc_warn), "取消", "确定");
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
            DialogUtils.getInstance().showTwoButtonDialog(this, "OTC新用户交易前需要先完成一次C2C小额充值交易(3000元以下)", "取消", "确定");
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

    private void bindPhone(Map<String, String> parmas) {
        parmas.put("loginToken", UserInfoManager.getToken());
        showProgress("");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.BIND_PHONE, parmas, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                hideProgress();
                MToast.show(BindPhoneActivity.this, getString(R.string.net_timeout), 1);
            }

            @Override
            public void requestSuccess(String result) {
                hideProgress();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        if (!fromToc) {
                            if (TextUtils.isEmpty(value)) {
                                value = getString(R.string.bind_suc);
                            }
                            showMessage(value, 1);
                            // SnackBarUtils.ShowBlue(BindPhoneActivity.this,"绑定成功");
                            Intent intent = new Intent();
                            intent.putExtra("message", getString(R.string.bind_suc));
                            setResult(212, intent);
                            close();
                        } else {
                            showMessage(value, 1);
                            nextSet();
                        }
                    } else {
                        showMessage(value, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MToast.show(BindPhoneActivity.this, getString(R.string.service_expec), 1);
                }
            }
        });
    }


    private boolean btn_registerable() {
        phone = et_phone.getText().toString().trim();
        code = et_phone_code.getText().toString().trim();
        totpCode = et_google_code.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            MToast.show(this, getString(R.string.empty_phon), 1);
            return false;
        } else if (phone.length() > 20 && phone.length() < 5) {
            MToast.show(this, getString(R.string.inputphone), 1);
            return false;
        }
        if (TextUtils.isEmpty(code)) {
            MToast.show(this, getString(R.string.empt_code), 1);
            return false;
        }
        if (rl_google.getVisibility() == View.VISIBLE && TextUtils.isEmpty(totpCode)) {
            MToast.show(this, getString(R.string.google_error), 1);
            return false;
        }
        if (isBind) {
            if (TextUtils.isEmpty(phone_new.getText().toString().trim())) {
                MToast.show(this, getString(R.string.empty_phone), 1);
                return false;
            }
            if (TextUtils.isEmpty(code_new.getText().toString().trim())) {
                MToast.show(this, getString(R.string.phone_error1), 1);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        AppHelper.unsubscribe(subscription);
        AppHelper.unsubscribe(subscription2);
        AppHelper.unsubscribe(subscription3);
        super.onDestroy();
    }
}
