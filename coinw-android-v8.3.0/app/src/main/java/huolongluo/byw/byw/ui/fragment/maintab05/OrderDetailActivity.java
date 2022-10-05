package huolongluo.byw.byw.ui.fragment.maintab05;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.fragment.maintab05.bean.BuyBean;
import huolongluo.byw.byw.ui.fragment.maintab05.cnycthis.CnyCTHistoryActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/19.
 */
public class OrderDetailActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_bank)
    TextView tv_bank;
    @BindView(R.id.tv_fremark)
    TextView tv_fremark;
    @BindView(R.id.tv_order)
    TextView tv_order;
    @BindView(R.id.ll_buy_one)
    TextView ll_buy_one;
    @BindView(R.id.ll_buy_two)
    TextView ll_buy_two;
    @BindView(R.id.tv_sure)
    TextView tv_sure;
    @BindView(R.id.tv_bankNum)
    TextView tv_bankNum;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.copy_tv1)
    TextView copy_tv1;
    @BindView(R.id.copy_tv2)
    TextView copy_tv2;
    @BindView(R.id.copy_tv3)
    TextView copy_tv3;
    private BuyBean buyBean;
    private String fromClass;
    private String status;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        //return R.layout.activity_order_detail;
        return R.layout.activity_orderdetail;
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        copy_tv1.setOnClickListener(listener);
        copy_tv2.setOnClickListener(listener);
        copy_tv3.setOnClickListener(listener);
        title_tv.setText(R.string.ee37);
        eventClick(back_iv).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(ll_buy_two).subscribe(o -> {
            startActivity(CnyCTHistoryActivity.class);
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(ll_buy_one).subscribe(o -> {
            Now_fukuan();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fromClass = bundle.getString("fromClass");
            if (fromClass.equals("CZFragment")) {
                buyBean = bundle.getParcelable("orderDetail");
            } else {
                status = bundle.getString("status");
                buyBean = bundle.getParcelable("orderDetail");
            }
        }
        if (buyBean != null) {
            DecimalFormat format = new DecimalFormat("#0.#");
            tv_money.setText(NorUtils.NumberFormat(4).format(DoubleUtils.parseDouble(buyBean.getMoney())));
            tv_name.setText(buyBean.getFownerName());
            tv_bank.setText(buyBean.getFbankName() + "/" + buyBean.getFbankAddress());
            tv_fremark.setText(buyBean.getFremark());
            tv_order.setText(buyBean.getTradeId() + "");
            tv_bankNum.setText(buyBean.getFbankNumber());
            if (status != null) {
                if (status.equals("4")) {
                    ll_buy_one.setVisibility(View.GONE);
                    ll_buy_two.setVisibility(View.GONE);
                    tv_sure.setVisibility(View.VISIBLE);
                } else if (status.equals("3") || status.equals("2")) {
                    ll_buy_one.setVisibility(View.GONE);
                    ll_buy_two.setVisibility(View.GONE);
                    tv_sure.setVisibility(View.VISIBLE);
                } else {
                    ll_buy_one.setVisibility(View.VISIBLE);
                    ll_buy_two.setVisibility(View.VISIBLE);
                    tv_sure.setVisibility(View.GONE);
                }
            }
        }
        eventClick(tv_sure).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClipboardManager copy = (ClipboardManager) OrderDetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            switch (v.getId()) {
                case R.id.copy_tv1:
                    if (buyBean != null) {
                        copy.setText(buyBean.getFownerName());
                        // Toast.makeText(OrderDetailActivity.this,"复制成功",1).show();
                        SnackBarUtils.ShowBlue(OrderDetailActivity.this, getString(R.string.ee38));
                    }
                    break;
                case R.id.copy_tv2:
                    if (buyBean != null) {
                        copy.setText(buyBean.getFbankNumber());
                        // Toast.makeText(OrderDetailActivity.this,"复制成功",1).show();
                        SnackBarUtils.ShowBlue(OrderDetailActivity.this, getString(R.string.ee38));
                    }
                    break;
                case R.id.copy_tv3:
                    if (buyBean != null) {
                        copy.setText(buyBean.getFremark());
                        // Toast.makeText(OrderDetailActivity.this,"复制成功",1).show();
                        SnackBarUtils.ShowBlue(OrderDetailActivity.this, getString(R.string.ee38));
                    }
                    break;
            }
        }
    };

    //我已付款
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Now_fukuan() {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        if (loginToken.isEmpty()) {
            return;
        }
        RSACipher rsaCipher = new RSACipher();
        String body = "tradeId=" + URLEncoder.encode(String.valueOf(buyBean.getTradeId()));
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
        showProgress("");
        netTags.add(UrlConstants.DOMAIN + UrlConstants.rechargeCnySubmit);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.rechargeCnySubmit, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                hideProgress();
            }

            @Override
            public void requestSuccess(String result) {
                hideProgress();
                Log.d("点击确定买入操作", result);
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
                    e.printStackTrace();
                }
            }
        });
    }
}
