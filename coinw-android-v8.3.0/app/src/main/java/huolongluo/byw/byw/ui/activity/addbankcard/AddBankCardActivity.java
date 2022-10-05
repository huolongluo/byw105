package huolongluo.byw.byw.ui.activity.addbankcard;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
import javax.inject.Inject;

import butterknife.BindView;
import cn.addapp.pickers.entity.City;
import cn.addapp.pickers.entity.County;
import cn.addapp.pickers.entity.Province;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.CommonBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.banklist.BankListActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.AddressPickTask;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
/**
 * Created by 火龙裸 on 2018/1/4.
 */
public class AddBankCardActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.et_bankcard)
    EditText et_bankcard;
    @BindView(R.id.et_banklist)
    TextView et_banklist;
    @BindView(R.id.et_bankaddress)
    TextView et_bankaddress;
    @BindView(R.id.et_zhihang)
    EditText et_zhihang;
    @BindView(R.id.ll_google_view)
    LinearLayout ll_google_view;
    @BindView(R.id.et_google)
    EditText et_google;
    @BindView(R.id.ll_phoneview)
    LinearLayout ll_phoneview;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.tv_getcode)
    TextView tv_getcode;
    @BindView(R.id.tv_sure)
    TextView tv_sure;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    public static String openBankType = "";
    private Subscription subscription, subscription2;

    @Override
    protected int getContentViewId() {
        // return R.layout.activity_add_bank_card;
        return R.layout.activity_add_bankcard;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
        EventBus.getDefault().register(this);
    }

    private void initToolBar() {
        //全屏。
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        my_toolbar.setTitle("");
        title_tv.setText(R.string.aa31);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        initToolBar();
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_name.setText(UserInfoManager.getUserInfo().getRealName());
        if (UserInfoManager.getUserInfo().isBindMobil()) {
            ll_phoneview.setVisibility(View.VISIBLE);
        } else {
            ll_phoneview.setVisibility(View.GONE);
        }
        if (UserInfoManager.getUserInfo().isGoogleBind()) {
            ll_google_view.setVisibility(View.VISIBLE);
        } else {
//            showMessage("请先绑定Google",2);
            ll_google_view.setVisibility(View.GONE);
        }
        eventClick(et_banklist).subscribe(o -> {
            startActivity(BankListActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(et_bankaddress).subscribe(o -> {
            startAddressPicker();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_getcode).subscribe(o -> {
            getSms();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_sure).subscribe(o -> {
            if ((!TextUtils.isEmpty(et_bankaddress.getText().toString().trim())) && (!TextUtils.isEmpty(et_bankcard.getText().toString().trim())) //
                    && (!TextUtils.isEmpty(et_banklist.toString().trim())) && (!TextUtils.isEmpty(et_zhihang.toString().trim()))) {
                add();
            } else {
                showMessage(getString(R.string.aa32), 1);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    private void startAddressPicker() {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideProvince(false);
        task.setHideCounty(false);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                //showMessage("数据初始化失败", 1);
                SnackBarUtils.ShowRed(AddBankCardActivity.this, getString(R.string.aa33));
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                if (county == null) {
                    et_bankaddress.setText(province.getAreaName() + city.getAreaName());
                } else {
                    et_bankaddress.setText(province.getAreaName() + city.getAreaName() + county.getAreaName());
                }
            }
        });
        task.execute(getString(R.string.aa34), getString(R.string.aa35), getString(R.string.aa36));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickDrawerLayout(Event.clickBankName clickBankName) {
        et_banklist.setText(clickBankName.bankName);
    }

    /**
     * 回调
     * 获取验证码成功
     */
    public void sendMessageSucce(CommonBean response) {
        if (response.getCode() == 0) {
            // showMessage("获取验证码成功", 2);
            SnackBarUtils.ShowBlue(AddBankCardActivity.this, getString(R.string.aa37));
            subscription = Observable.interval(0, 1, TimeUnit.SECONDS).limit(61).map(aLong -> 120 - aLong).doOnSubscribe(() -> tv_getcode.setEnabled(false)).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                if (tv_getcode != null) {
                    tv_getcode.setText(aLong + " s");
                }
            }).doOnCompleted(() -> {
                if (tv_getcode != null) {
                    tv_getcode.setText(R.string.aa68);
                    tv_getcode.setEnabled(true);
                }
            }).onErrorReturn(throwable -> {
                Logger.getInstance().errorLog(throwable);
                return 61L;
            }).doOnError(throwable -> {
                Logger.getInstance().error(throwable);
            }).subscribe();
        } else {
            showMessage(response.getValue(), 1);
        }
    }

    public void addBankCardSucce(CommonBean response) {
        if (response.getCode() == 0) {
            close();
        } else {
            showMessage(response.getValue(), 1);
        }
    }

    @Override
    protected void onDestroy() {
        AppHelper.unsubscribe(subscription);
        AppHelper.unsubscribe(subscription2);
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t) {
            Logger.getInstance().errorLog(t);
        }
        super.onDestroy();
    }
    //获取短信验证码

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getSms() {
        HashMap<String, String> params = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = "type=" + URLEncoder.encode("10");
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
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
//        params.put("body",body);
        params.put("loginToken", SPUtils.getLoginToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.SendMessageCode);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.SendMessageCode, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("获取验证码成功", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean result1 = jsonObject.getBoolean("result");
                    String value = jsonObject.getString("value");
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        showMessage(value, 2);
                        subscription2 = Observable.interval(0, 1, TimeUnit.SECONDS).limit(61).map(aLong -> 120 - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                            if (tv_getcode != null) {
                                tv_getcode.setText(aLong + " s");
                            }
                        }).doOnCompleted(() -> {
                            if (tv_getcode != null) {
                                tv_getcode.setText(R.string.aa68);
                                tv_getcode.setEnabled(true);
                            }
                        }).doOnSubscribe(() -> {
                            if (tv_getcode != null) {
                                tv_getcode.setEnabled(false);
                            }
                        }).onErrorReturn(throwable -> {
                            Logger.getInstance().errorLog(throwable);
                            return 61L;
                        }).doOnError(throwable -> {
                            Logger.getInstance().error(throwable);
                        }).subscribe();
                    } else {
                        showMessage(value, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //添加银行卡
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void add() {
        HashMap<String, String> params = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = null;
        if (UserInfoManager.getUserInfo().isGoogleBind()) {
            body = "phoneCode=" + URLEncoder.encode(et_code.getText().toString()) + "&totpCode=" + URLEncoder.encode(et_google.getText().toString()) + "&openBankType=" + URLEncoder.encode(openBankType);
        } else {
            body = "phoneCode=" + URLEncoder.encode(et_code.getText().toString()) + "&openBankType=" + URLEncoder.encode(openBankType);
        }
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("account", et_bankcard.getText().toString());
            params.put("address", et_bankaddress.getText().toString());
            params.put("others", et_zhihang.getText().toString());
            params.put("loginToken", UserInfoManager.getToken());
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
//        params.put("loginToken",Share.get().getLogintoken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.SetWithdrawCnyBankInfo);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.SetWithdrawCnyBankInfo, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                MToast.show(AddBankCardActivity.this, errorMsg, 1);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        showMessage(value, 2);
                        finish();
                    } else {
                        showMessage(value, 2);
                    }
                } catch (JSONException e) {
                    MToast.show(AddBankCardActivity.this, getString(R.string.aa40), 1);
                    e.printStackTrace();
                }
            }
        });
    }
}
