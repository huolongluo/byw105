package huolongluo.byw.byw.ui.activity.safe_centre;

import android.app.AlertDialog;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.android.legend.ui.mine.safe.ModifyLoginPwdActivity;
import com.google.gson.Gson;
import com.legend.common.util.StatusBarUtils;

import org.json.JSONObject;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.SafeCentreBean;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.bindemail.BindEmailActivity;
import huolongluo.byw.byw.ui.activity.bindgoogle.BindGoogleOneActivity;
import huolongluo.byw.byw.ui.activity.bindphone.BindPhoneActivity;
import huolongluo.byw.byw.ui.activity.setchangepsw.SetChangePswActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Request;

/**
 * Created by 火龙裸 on 2018/1/30.
 */

public class SafeCentreActivity extends SwipeBackActivity {
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.rl_item_bind_email)
    RelativeLayout rl_item_bind_email;
    @BindView(R.id.rl_item_bind_phone)
    RelativeLayout rl_item_bind_phone;
    @BindView(R.id.rl_item_bind_google)
    RelativeLayout rl_item_bind_google;
    @BindView(R.id.rl_item_bind_trade_psw)
    RelativeLayout rl_item_bind_trade_psw;
    @BindView(R.id.tv_bind_google)
    TextView tv_bind_google;
    @BindView(R.id.tv_bind_trade_psw)
    TextView tv_bind_trade_psw;

    @BindView(R.id.tv_bing_email)
    TextView tv_bing_email;
    @BindView(R.id.tv_bing_phone)
    TextView tv_bing_phone;
    @BindView(R.id.rltLoginPwd)
    RelativeLayout rltLoginPwd;
    @BindView(R.id.title_tv)
    TextView title_tv;


    private String setChangePswTradeTitle = "";

    private boolean isBindGoogle; // 是否绑定谷歌
    private boolean isBindEmail; // 是否绑定谷歌
    private boolean isBindTelephone; // 是否绑定手机号

    private SafeCentreBean safeCentreBean = null;

    Unbinder unbinder;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.activity_safe_center);

        unbinder = ButterKnife.bind(this);

        mSwipeBackLayout = getSwipeBackLayout();

        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(200);//

        title_tv.setText(R.string.aa88);

        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        rl_item_bind_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SafeCentreActivity.this, BindEmailActivity.class);
                if (isBindEmail) {
                    if (safeCentreBean != null) {

                        intent.putExtra("isBindEmail", isBindEmail);
                        intent.putExtra("email", safeCentreBean.getEmailTXT());
                    }
                } else {
                    intent.putExtra("isBindEmail", isBindEmail);
                }


                startActivity(intent);
            }
        });

        rl_item_bind_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(BindPhoneActivity.class);
                Intent intent = new Intent(SafeCentreActivity.this, BindPhoneActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                intent.putExtra("isBindGoogle", isBindGoogle);
                intent.putExtra("isBindTelephone", isBindTelephone);
                startActivityForResult(intent, 100);
            }
        });


        rl_item_bind_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBindGoogle) {
                    MToast.show(SafeCentreActivity.this, getString(R.string.aa89), 1);
                } else {
                    startActivityForResult(new Intent(SafeCentreActivity.this, BindGoogleOneActivity.class), 100);
                }


            }
        });

        rl_item_bind_trade_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.verifyIsBindGoogleOrMobile(SafeCentreActivity.this)){
                    return;
                }
                Intent intent = new Intent(SafeCentreActivity.this, SetChangePswActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("setChangePswTitle", setChangePswTradeTitle);
                bundle.putBoolean("isBindGoogle", isBindGoogle);
                bundle.putBoolean("isBindTelephone", isBindTelephone);

                intent.putExtra("bundle", bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, 100);
            }
        });
        rltLoginPwd.setOnClickListener(v->{
            if(!Util.verifyIsBindGoogleOrMobile(SafeCentreActivity.this)){
                return;
            }
            ModifyLoginPwdActivity.Companion.launch(this);
        });

        if (UserInfoManager.getUserInfo().isBindEmail()) {
            tv_bing_email.setText(R.string.aa91);
            isBindEmail = true;
        } else {
            isBindEmail = false;

            tv_bing_email.setText(R.string.aa92);
        }

        if (UserInfoManager.getUserInfo().isBindMobil()) {
            isBindTelephone = true;
            tv_bing_phone.setText(R.string.aa93);
        } else {
            isBindTelephone = false;
            rl_item_bind_phone.setClickable(true);
            tv_bing_phone.setText(R.string.aa94);
        }

        if (UserInfoManager.getUserInfo().isGoogleBind()) {
            isBindGoogle = true;

            tv_bind_google.setText(R.string.aa952);
        } else {
            isBindGoogle = false;
            rl_item_bind_google.setClickable(true);
            tv_bind_google.setText(R.string.aa96);
        }

        rl_item_bind_trade_psw.setClickable(true);

        if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
            tv_bind_trade_psw.setText(R.string.aa97);
            setChangePswTradeTitle = getString(R.string.aa98);
        } else {
            tv_bind_trade_psw.setText(R.string.dd51);
            setChangePswTradeTitle = getString(R.string.bb1);
        }
    }


    private void Login(String pwd) {


        Map<String, String> params = new HashMap<>();
        params.put("password", pwd);

        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());


        DialogManager.INSTANCE.showProgressDialog(this, getString(R.string.bb2));

        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.fingerprintVerification, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                //  showMessage("登录失败,网络请求超时", 1);
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(SafeCentreActivity.this, errorMsg);

                e.printStackTrace();
            }


            @Override
            public void requestSuccess(String result) {
                //{"value":"密码验证成功","result":true,"code":0}
                DialogManager.INSTANCE.dismiss();

                Log.d("指纹识别", "  service return = :  " + result);


                try {

                    JSONObject jsonObject = new JSONObject(result);

                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");

                    if (code == 0) {
                        openFingerprint();
                    } else {
                        SnackBarUtils.ShowRed(SafeCentreActivity.this, value + "");
                    }
                } catch (Exception e) {

                    SnackBarUtils.ShowRed(SafeCentreActivity.this, getString(R.string.bb3) + "");

                    e.printStackTrace();
                }
            }
        });
    }


    //开启指纹识别
    void openFingerprint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager manager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            if (!manager.isHardwareDetected()) {
                MToast.showButton(SafeCentreActivity.this, getString(R.string.bb4), 1);
                return;
            } else if (!manager.hasEnrolledFingerprints()) {
                MToast.showButton(SafeCentreActivity.this, getString(R.string.bb5), 1);
                return;
            }
        } else {
            FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(SafeCentreActivity.this);
            if (!managerCompat.isHardwareDetected()) {
                MToast.showButton(SafeCentreActivity.this, getString(R.string.bb6), 1);
                return;
            } else if (!managerCompat.hasEnrolledFingerprints()) {
                MToast.showButton(SafeCentreActivity.this, getString(R.string.bb7), 1);
                return;
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();


    }


    //获取用户交易密码设置情况
    private void getTradePWD() {
        HashMap<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(SPUtils.getLoginToken())) {
            return;
        }
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.getSecurity, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {
                try {


                    Log.e("requestSuccess", "requestSuccess== " + result);


                    safeCentreBean = GsonUtil.json2Obj(result, SafeCentreBean.class);

                    if (safeCentreBean != null) {


                        UserInfoManager.getDeaflt().setSafeCentreBean(safeCentreBean);
                        if (safeCentreBean.isIsBindEmail()) {
                            //tv_bind_email.setVisibility(View.VISIBLE);
                            //tv_bind_email.setText(safeCentreBean.getEmailTXT());

                            tv_bing_email.setText(R.string.bb10);
                            isBindEmail = true;
                        } else {
                            isBindEmail = false;

                            tv_bing_email.setText(R.string.bb11);
                            //tv_bind_email.setVisibility(View.GONE);
                        }

                        if (safeCentreBean.isIsBindTelephone()) {
                            isBindTelephone = true;
                            //tv_bind_phone.setText(safeCentreBean.getTelNumberTXT());
                            tv_bing_phone.setText(R.string.bb10);
                            // rl_item_bind_phone.setClickable(false);
                            UserInfoManager.getUserInfo().setBindMobil(true);
                        } else {
                            isBindTelephone = false;
                            rl_item_bind_phone.setClickable(true);
                            tv_bing_phone.setText(R.string.bb11);
                            UserInfoManager.getUserInfo().setBindMobil(false);
                        }

                        if (safeCentreBean.getIsBindGoogle()) {
                            isBindGoogle = true;
                            tv_bind_google.setText(safeCentreBean.getGoogleTXT());
                            //  rl_item_bind_google.setClickable(false);
                            tv_bind_google.setText(R.string.bb10);
                        } else {
                            isBindGoogle = false;
                            rl_item_bind_google.setClickable(true);
                            tv_bind_google.setText(R.string.bb11);
                        }

                        //rl_item_bind_login.setClickable(true);
                        rl_item_bind_trade_psw.setClickable(true);


                        if (safeCentreBean.isIsTradePassword()) {
                            tv_bind_trade_psw.setText(R.string.bb12);
                            setChangePswTradeTitle = getString(R.string.bb13);
                            UserInfoManager.getUserInfo().setIsHasTradePWD(1);
                        } else {
                            tv_bind_trade_psw.setText(R.string.dd51);
                            setChangePswTradeTitle = getString(R.string.bb15);
                            UserInfoManager.getUserInfo().setIsHasTradePWD(0);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("getLogintoken= safe", "===  " + SPUtils.getLoginToken());
//        subscription = safeCentrePresent.getSafeCentre(Share.get().getLogintoken()); // 开始获取安全中心列表信息
        getTradePWD(); // 开始获取安全中心列表信息
        getUserInfo();
    }


    private void getUserInfo() {

        Map<String, String> params = new HashMap<>();

        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");

                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("userInfo");
                        UserInfoBean infoBean = new Gson().fromJson(object.toString(), UserInfoBean.class);

                        UserInfoManager.getDeaflt().setUserInfoBean(infoBean);
                        UserInfoManager.getDeaflt().setToken(SPUtils.getLoginToken());
                        //登录IM
                        BaseApp.getSelf().loginIM();
                        //
                    }


                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == 212) {
            if (data != null) {
                String message = data.getStringExtra("message");
                SnackBarUtils.ShowBlue(this, message);
            }
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
