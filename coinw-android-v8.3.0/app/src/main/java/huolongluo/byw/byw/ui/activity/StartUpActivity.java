package huolongluo.byw.byw.ui.activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.legend.modular_contract_sdk.api.ModularContractSDK;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.DomainHelper;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Share;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.heyue.HeYueUtil;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.UserInfoResult;
import huolongluo.byw.reform.c2c.oct.bean.C2cIsShowBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.AgreementUtils;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.L;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.config.ConfigurationUtils;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.bywx.HttpRequestManager;
import huolongluo.bywx.StatusHolder;
import huolongluo.bywx.utils.EncryptUtils;
import okhttp3.Request;
/**
 * <p>
 * Created by ????????? on 2017/9/3 0003.
 */
public class StartUpActivity extends AppCompatActivity {
    private Handler handler = new Handler(Looper.getMainLooper());
    private final String TAG = "StartUpActivity";
    private boolean isConfig=false;//??????????????????????????????????????????,???????????????????????????
    private Timer DomainTimer;

    private Boolean mGifIsEnd = false;// gif ??????????????????
    private Boolean mMainProcessIsEnd = false;// ???????????????????????????

    protected void setLang() {
        String language = SPUtils.getString(this, Constant.KEY_LANG, "");
        if (TextUtils.isEmpty(language)) {
            language = ConfigurationUtils.getDefaultLanguage(BaseApp.getSelf());
            SPUtils.saveString(this, Constant.KEY_LANG, language);
        }
        ConfigurationUtils.updateWebViewSettings(this);
        ConfigurationUtils.updateActivity(this, language);
        ModularContractSDK.INSTANCE.resetLanguage(language);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusHolder.getInstance().setKill(false);

        // api 28 ???????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        setContentView(R.layout.activity_start);
//        Constant.isServiceStop = false;
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setLang();
        initViewsAndEvents();
    }

    protected void initViewsAndEvents() {
        if(BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")){
            getDomain();//????????????????????????release?????????
        }else{
            getConfig();
        }

        ImageView ivSplash = findViewById(R.id.iv_splash);

        Glide.with(this)
                .asGif()
                .load(R.drawable.bg_splash)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .listener(new RequestListener<GifDrawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {

                        GifDrawable gifDrawable = (GifDrawable) resource;
                        gifDrawable.setLoopCount(1);
                        gifDrawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                super.onAnimationStart(drawable);
                                Logger.getInstance().error("gif start");
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                super.onAnimationEnd(drawable);
                                mGifIsEnd = true;
                                to();
                            }
                        });

                        return false;
                    }

                })
                .into(ivSplash);

    }
    //??????????????????
    private void getDomain(){
        DomainTimer=new Timer();//??????????????????2s????????????????????????????????????????????????
        DomainTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logger.getInstance().debug(TAG,"DomainTimer??????:"+System.currentTimeMillis());
                getConfig();
                if(NetworkUtil.isNetworkConnected(StartUpActivity.this)){
                    Logger.getInstance().report("??????????????????????????????2s",TAG+"-getDomain-run");
                }
                releaseDomainTimer();
            }
        },2000);
        OkhttpManager.get(UrlConstants.DYNAMIC_DOMAIN, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().debug(TAG,"???????????????????????? e:"+e.getMessage());
                getConfig();
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, "???????????????????????? result:" + result);
                DomainUtil.INSTANCE.switchDomain(StartUpActivity.this, result, true);
                getConfig();
            }
        });
    }

    private void getConfig() {//????????????????????????,??????????????????
        if(isConfig) return;
        isConfig=true;
        releaseDomainTimer();//????????????????????????????????????timer

        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.getConfig, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                getUserInfo();
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, "getConfig result:" + result);
                getUserInfo();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.optInt("code");
                    if (AppConstants.CODE.STOP_SERVICE_CODE.equals(""+code)) {
                        Constant.STOP_SERVICE_IS_STOP_STARTUP = true;
                    }

                    Logger.getInstance().debug(TAG, "getConfig Constant.STOP_SERVICE_IS_STOP_STARTUP:" +
                            Constant.STOP_SERVICE_IS_STOP_STARTUP);

                    // ???????????????
                    C2cIsShowBean c2cIsShowBean = GsonUtil.json2Obj(result, C2cIsShowBean.class);
                    if (c2cIsShowBean.getCOIN_LOAN_DISPLAY_CLOSE() != null && !TextUtils.isEmpty(c2cIsShowBean.getCOIN_LOAN_DISPLAY_CLOSE().value)) {
                        if (c2cIsShowBean.getCOIN_LOAN_DISPLAY_CLOSE().value.equals("true")) {//??????
                            Constant.IS_BDB_CLOSE = true;
                        } else {
                            Constant.IS_BDB_CLOSE = false;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void releaseDomainTimer() {
        if (DomainTimer != null) {
            DomainTimer.cancel();
            DomainTimer = null;
        }
    }

    private void to() {
        // ????????? start-> ?????????????????? -> ??????Config -> ?????????????????? -> end
        // ????????????????????????gif??????????????? ???????????????
        if (!mMainProcessIsEnd || !mGifIsEnd){
            return;
        }

        ModularContractSDK.INSTANCE.setHost(DomainHelper.getSwapUrl());
        HeYueUtil.getInstance().init();//???????????????sdk
        Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
        Uri uri = getIntent().getParcelableExtra(Constant.KEY_THIRD_LAUNCH_URI);
        if (uri != null) {
            intent.putExtra(Constant.KEY_THIRD_LAUNCH_URI, uri);
        }
        boolean result = handler.post(() -> {
            if (CoinwHyUtils.isServiceStop) {//??????????????????????????????????????????MainActivity
                finish();//??????????????????????????????????????????????????????????????????????????????????????????
                return;
            }
            if (!Share.get().getisFirstOpen()) {
                BaseApp.FIST_OPEN_C2C = true;
                Share.get().setisFirstOpen(true);
            }
            if (!Share.get().getisFirstOpenOtc()) {
                BaseApp.FIST_OPEN_OTC = true;
            }
            //Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
            //intent.putExtra("isPast", isPast);
            //if (!TextUtils.isEmpty(loginToken)) {
            //    intent.putExtra("loginToken", loginToken);
            //}
            startActivity(intent);
            finish();
        });
        try {
            if (!result) {
                startActivity(intent);
                finish();
                Logger.getInstance().report("StartUpActivity start is " + result + " FINGERPRINT: " + Build.FINGERPRINT);
            }
        } catch (Throwable t) {
        }
    }

    private void getUserInfo() {
        String token = SPUtils.getLoginToken();
        if (TextUtils.isEmpty(token)) {
            mMainProcessIsEnd = true;
            to();
            return;
        }
        Type type = new TypeToken<UserInfoResult>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("type", "1");
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", SPUtils.getLoginToken());
        HttpRequestManager.getInstance().post(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, callback, type);
    }

    private INetCallback<UserInfoResult> callback = new INetCallback<UserInfoResult>() {
        @Override
        public void onSuccess(UserInfoResult result) throws Throwable {
            Logger.getInstance().debug(TAG, "startup result!", new Exception());
            if (result == null) {
                //TODO ??????????????????
                Logger.getInstance().debug(TAG, "result is null.");
                mMainProcessIsEnd = true;
                to();
                return;
            }
            if (result.data == null) {
                Logger.getInstance().debug(TAG, "data is null.");
                mMainProcessIsEnd = true;
                to();
                return;
            }
            boolean isPast = false;
            String loginToken = "";
            //
            if (result.data.code == 0) {
                UserInfoManager.setUserInfoBean(result.data.userInfo);
                AgreementUtils.initAgreementOpenStatus(StartUpActivity.this, result.data.userInfo.getFid() + "");
                UserInfoManager.setToken(SPUtils.getLoginToken());
                isPast = true;
                loginToken = SPUtils.getLoginToken();
            } else if (result.data.code == -100000001) {//token??????
                isPast = false;
                loginToken = SPUtils.getLoginToken();
                SPUtils.saveLoginToken("");
                UserInfoManager.setToken("");
                //???????????????????????????SDK
                ModularContractSDK.INSTANCE.logout();
            }
            mMainProcessIsEnd = true;
            to();
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
            //TODO ??????????????????
            SPUtils.saveLoginToken("");
            mMainProcessIsEnd = true;
            to();
            Logger.getInstance().report(e);
        }
    };

    @Override
    protected void onDestroy() {
        if (handler != null) {
            try {
                handler.removeCallbacksAndMessages(null);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
