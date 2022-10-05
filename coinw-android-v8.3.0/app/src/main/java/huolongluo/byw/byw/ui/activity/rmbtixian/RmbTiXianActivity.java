package huolongluo.byw.byw.ui.activity.rmbtixian;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.android.legend.ui.viewmodel.BasicViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.CommonBean;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.bancard.BankCardListActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.ImmersedStatusbarUtils;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by 火龙裸 on 2018/1/3.
 */
public class RmbTiXianActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.toolbar_center_title)
    TextView toolbar_center_title;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.lin1)
    RelativeLayout lin1;
    @BindView(R.id.tv_usercny)
    TextView tv_usercny;
    @BindView(R.id.tv_no)
    TextView tv_no;
    @BindView(R.id.et_bankcard)
    EditText et_bankcard;
    @BindView(R.id.tv_choosebank)
    TextView tv_choosebank;
    @BindView(R.id.et_withdrawBalance)
    TextView et_withdrawBalance;
    @BindView(R.id.et_tradepass)
    EditText et_tradepass;
    @BindView(R.id.ll_google_view)
    LinearLayout ll_google_view;
    @BindView(R.id.et_google_code)
    EditText et_google_code;
    @BindView(R.id.ll_phoneview)
    LinearLayout ll_phoneview;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.tv_getcode)
    TextView tv_getcode;
    @BindView(R.id.tv_sure)
    TextView tv_sure;
    private String shortName;
    public static int coinId;
    private Double frozen;
    private Double total;
    public static String bankType = "";
    public static String bankId = "";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_rmb_tixian;
    }

    private BasicViewModel viewModel;

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
        EventBus.getDefault().register(this);
    }

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        //全屏。
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1); // 沉浸式
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(getString(R.string.rmbtx));
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        initToolBar();
        if (getBundle() != null) {
            frozen = getBundle().getDouble("frozen");
            total = getBundle().getDouble("total");
            tv_usercny.setText(getString(R.string.dj) + frozen);
            tv_no.setText(getString(R.string.useful) + total);
        }
        if (SPUtils.getBoolean(RmbTiXianActivity.this, SPUtils.IS_BIND_PHONE, false)) {
            ll_phoneview.setVisibility(View.VISIBLE);
        } else {
            ll_phoneview.setVisibility(View.VISIBLE);
        }
        if (SPUtils.getBoolean(RmbTiXianActivity.this, SPUtils.IS_BIND_GOOGLE, false)) {
            ll_google_view.setVisibility(View.VISIBLE);
        } else {
            ll_google_view.setVisibility(View.GONE);
        }
        eventClick(iv_left).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_choosebank).subscribe(o -> {
            startActivity(BankCardListActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        //由于服务器接口废弃，因此同步改动
        viewModel = new ViewModelProvider(this).get(BasicViewModel.class);
        viewModel.getSms().observe(this, result -> sendMessageSucce(result.getData()));
        viewModel.getWithDrawCny().observe(this, result -> rmbTiXianSucce(result.getData()));
        eventClick(tv_getcode).subscribe(o -> {
            viewModel.sendSMS("4");
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_sure).subscribe(o -> {
            if ((!TextUtils.isEmpty(et_bankcard.getText().toString().trim())) && (!TextUtils.isEmpty(et_tradepass.getText().toString().trim()))//
                    && (!TextUtils.isEmpty(et_withdrawBalance.getText().toString().trim()))) {
                RSACipher rsaCipher = new RSACipher();
                String body = null;
                if (SPUtils.getBoolean(RmbTiXianActivity.this, SPUtils.IS_BIND_GOOGLE, false)) {
                    body = "tradePwd=" + URLEncoder.encode(et_tradepass.getText().toString().trim()) + "&withdrawBalance=" + URLEncoder.encode(et_withdrawBalance.getText().toString().trim()) + "&phoneCode=" + URLEncoder.encode(et_code.getText().toString().trim()) + "&totpCode=" + URLEncoder.encode(et_google_code.getText().toString().trim()) + "&withdrawBank=" + URLEncoder.encode(bankId);
                } else {
                    body = "tradePwd=" + URLEncoder.encode(et_tradepass.getText().toString().trim()) + "&withdrawBalance=" + URLEncoder.encode(et_withdrawBalance.getText().toString().trim()) + "&phoneCode=" + URLEncoder.encode(et_code.getText().toString().trim()) + "&withdrawBank=" + URLEncoder.encode(bankId);
                }

                try {
                    String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
                    viewModel.withDrawCny(body1);
//                    subscription = rmbTiXianPresent.rmbTiXian(body1, SPUtils.getLoginToken());
//                subscription = addAddressPresent.addCoinAddress(Share.get().getIsBindPhone() ? et_code.getText().toString().trim() : "", //
//                        Share.get().getIsBindGoogle() ? et_google.getText().toString().trim() : "", //
//                        et_beizhu.getText().toString().trim(), et_coinaddress.getText().toString().trim(), CoinTiXianActivity.coinId, Share.get().getLogintoken());
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
//                subscription = rmbTiXianPresent.rmbTiXian(et_tradepass.getText().toString().trim(), et_withdrawBalance.getText().toString().trim(), null,//
//                        Share.get().getIsBindPhone() ? et_code.getText().toString().trim() : "", //
//                        Share.get().getIsBindGoogle() ? et_google_code.getText().toString().trim() : "", //
//                        null, Share.get().getLogintoken());
            } else {
                showMessage(getString(R.string.wz), 1);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectCoinAddress(Event.clickBankCard clickBankCard) {
        et_bankcard.setText(clickBankCard.bankNumber);
    }

    /**
     * 回调
     * 获取 “验证码” 成功
     */
    public void sendMessageSucce(CommonBean response) {
        if (response.getCode() == 0) {
            //   showMessage("获取验证码成功", 2);
            SnackBarUtils.ShowBlue(RmbTiXianActivity.this, getString(R.string.get_code_suc));
            subscription = Observable.interval(0, 1, TimeUnit.SECONDS).limit(61).map(aLong -> 120 - aLong).doOnSubscribe(() -> {
                if (tv_getcode != null) {
                    tv_getcode.setEnabled(false);
                }
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                if (tv_getcode != null) {
                    tv_getcode.setText(aLong + " s");
                }
            }).doOnCompleted(() -> {
                if (tv_getcode != null) {
                    tv_getcode.setText(getString(R.string.click_get));
                    tv_getcode.setEnabled(true);
                }
            }).onErrorReturn(throwable -> {
                Logger.getInstance().errorLog(throwable);
                return 1L;
            }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
        } else {
            showMessage(response.getValue(), 1);
        }
    }

    public void rmbTiXianSucce(CommonBean response) {
        if (response.getCode() == 0) {
            //showMessage("提现成功", 1);
            SnackBarUtils.ShowBlue(RmbTiXianActivity.this, getString(R.string.txsuc));
            close();
        } else {
            showMessage(response.getValue(), 1);
        }
    }

    @Override
    protected void onDestroy() {
        AppHelper.unsubscribe(subscription);
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
