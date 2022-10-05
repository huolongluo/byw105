package huolongluo.byw.byw.ui.activity.coindetail;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.model.finance.BbFinanceAccountListBean;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.UserAssetsBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.cthistory.FinanceRecordUtil;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.FinanceRecordBean;
import huolongluo.byw.model.FinanceRecordInfo;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.mine.activity.FinanceRecordActivity;
import huolongluo.byw.reform.mine.adapter.FinanceRecordAdapter;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.utils.DoubleUtils;
import huolongluo.bywx.utils.EncryptUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/18.
 */
public class BbCoinDetailActivity extends huolongluo.byw.reform.base.BaseActivity {
    private static final String TAG = "CoinDetailActivity";
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.iv_logo)
    ImageView iv_logo;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_assest_all)
    TextView tv_assest_all;
    @BindView(R.id.tv_asset_unit)
    TextView tv_asset_unit;
    @BindView(R.id.tv_assest_keyong)
    TextView tv_assest_keyong;
    @BindView(R.id.tv_assest_dongjie)
    TextView tv_assest_dongjie;
    @BindView(R.id.lv_content)
    ListView lv_content;
    @BindView(R.id.all_bn)
    TextView all_bn;
    private BbFinanceAccountListBean coinsBean;
    private DecimalFormat df = new DecimalFormat(Constant.ASSETS_DEFAULT_AMOUNT);
    ;
    private List<FinanceRecordBean> mData1 = new ArrayList<>();
    private FinanceRecordAdapter adapter;
    Unbinder unbinder;
    private RadioButton radioB_2;
    private RadioButton radioB_3;
    private RadioButton radioB_1;
    private int areaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);
        unbinder = ButterKnife.bind(this);
        initViewsAndEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    protected void initViewsAndEvents() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            coinsBean = bundle.getParcelable("coinBean");
            areaType = bundle.getInt("areaType");
        }
        radioB_1 = findViewById(R.id.radioB_1);
        radioB_1.setVisibility(View.GONE);

        radioB_2 = findViewById(R.id.radioB_2); radioB_2.setText(getString(R.string.d33));
        radioB_3 = findViewById(R.id.radioB_3);
        if ("CNYT".equalsIgnoreCase(coinsBean.getCoin().getCoinName())) {
            radioB_3.setText(getString(R.string.fbjy));
        }
        if(coinsBean.getPairs()==null||coinsBean.getPairs().size()==0){
            radioB_3.setVisibility(View.GONE);
        }
        adapter = new FinanceRecordAdapter(this);
        lv_content.setAdapter(adapter);
        viewClick(radioB_2, v -> {
            AccountTransferActivity.Companion.launch(BbCoinDetailActivity.this, TransferAccount.WEALTH.getValue(),TransferAccount.SPOT.getValue()
                    ,coinsBean.getCoin().getCoinId(),null,true,coinsBean.getCoin().getCoinName());
        });
        viewClick(radioB_3, v -> {
            Intent intent = new Intent(BbCoinDetailActivity.this, MainActivity.class);
            intent.putExtra("tradeId", coinsBean.getPairs().get(0).getPairId());
            intent.putExtra("coinName",coinsBean.getPairs().get(0).getBaseName());
            intent.putExtra("from", "CoinDetailActivity");
            intent.putExtra("areaType", areaType);
            startActivity(intent);
        });
        getUserInfo();
        getData();
        all_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BbCoinDetailActivity.this, FinanceRecordActivity.class);
                intent.putExtra("id", coinsBean.getCoin().getCoinId());
                intent.putExtra("coinName", coinsBean.getCoin().getCoinName());
                startActivity(intent);
            }
        });
        if (coinsBean != null) {
            RequestOptions ro = new RequestOptions();
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            Glide.with(this).load(coinsBean.getCoin().getCoinLog()).apply(ro).into((ImageView) iv_logo);
            tv_name.setText(coinsBean.getCoin().getCoinName());
            if (coinsBean.getBalanceAmount() == null) {
                tv_assest_all.setText(Constant.ASSETS_DEFAULT_AMOUNT);
            } else {
                tv_assest_all.setText(PricingMethodUtil.getResultByExchangeRate(coinsBean.getBalanceAmount(),"CNY",Constant.ASSETS_AMOUNT_PRECISION));
            }
            tv_asset_unit.setText(getResources().getString(R.string.zczh) + "(" + PricingMethodUtil.getPricingUnit() + ")");
            tv_assest_dongjie.setText(df.format(DoubleUtils.parseDouble(coinsBean.getHoldBalance())) + "");
            tv_assest_keyong.setText(df.format(DoubleUtils.parseDouble(coinsBean.getAvailableBalance())) + "");
            title_tv.setText(coinsBean.getCoin().getCoinName() + getString(R.string.finance));
        }
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FinanceRecordBean bean = adapter.getItem(i);
                FinanceRecordUtil.gotoDetail(BbCoinDetailActivity.this,bean);
            }
        });
    }

    private void getData() {
        Type types = new TypeToken<SingleResult<SingleResult<FinanceRecordInfo>>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("type", 0);
        params.put("currentPage", 1);
        params.put("symbol", coinsBean.getCoin().getCoinId());
        params.put("fstatus", 0);
        params= EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());

        OKHttpHelper.getInstance().post(UrlConstants.GET_FINANCE_RECORD, params, getDataCallback, types);

    }

    private void getUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        if (!UserInfoManager.isLogin()) {
            return;
        }
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                UserAssetsBean userAssetsBean = null;
                try {
                    userAssetsBean = GsonUtil.json2Obj(result, UserAssetsBean.class);
                    if (userAssetsBean.isResult()) {
                        //登录IM
                        BaseApp.getSelf().loginIM();
                    } else {
                        SnackBarUtils.ShowRed(BbCoinDetailActivity.this, getString(R.string.getuseinfo_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        radioB_2.setEnabled(true);
        radioB_3.setEnabled(true);
    }
    private INetCallback<SingleResult<SingleResult<FinanceRecordInfo>>> getDataCallback = new INetCallback<SingleResult<SingleResult<FinanceRecordInfo>>>() {
        @Override
        public void onSuccess(SingleResult<SingleResult<FinanceRecordInfo>> response) throws Throwable {
            Logger.getInstance().debug(TAG,"result:"+GsonUtil.obj2Json(response,SingleResult.class));
            if (response == null || response.data == null||!TextUtils.equals(response.code,"200")) {
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            if (response.data.code.equals("0")) {
                List<FinanceRecordBean> list=response.data.data.getList();
                if(list==null||mData1==null){
                    return;
                }
                if(list.size()>5){
                    list=list.subList(0,5);
                }
                mData1.clear();
                mData1.addAll(list);
                adapter.refresh(mData1);
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
        }
    };
}
