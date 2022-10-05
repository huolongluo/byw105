package huolongluo.byw.byw.ui.fragment.contractTab;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.bywx.utils.DoubleUtils;
public class ContractCoinDetailActivity extends BaseActivity {
    @BindView(R.id.money1)
    TextView money1;
    @BindView(R.id.money2)
    TextView money2;
    @BindView(R.id.money3)
    TextView money3;
    @BindView(R.id.money4)
    TextView money4;
    @BindView(R.id.iv_logo)
    ImageView iv_logo;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.title_tv)
    TextView tvName;
    @BindView(R.id.tv_huazhuan)
    Button tv_huazhuan;
    @BindView(R.id.tv_trade)
    Button tv_trade;
    @BindView(R.id.tv_chicang)
    Button tv_chicang;
    private DecimalFormat df = new DecimalFormat(Constant.ASSETS_DEFAULT_AMOUNT);
    private ContractListEntity.DataBean.DetailBean coinBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_coin_detail);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            coinBean = extras.getParcelable("coinBean");
            if (null == coinBean) {
                return;
            }
            //新接口改动
            money1.setText(df.format(DoubleUtils.parseDouble(coinBean.getTotalVol())));
            money2.setText(df.format(DoubleUtils.parseDouble(coinBean.getAvailableBalance())));
            money3.setText(df.format(DoubleUtils.parseDouble(coinBean.getFreezeVol())));
            money4.setText(df.format(DoubleUtils.parseDouble(coinBean.getTotalIm())));
            tv_name.setText(coinBean.getCoinName());
            RequestOptions ro = new RequestOptions();
            ro.placeholder(R.mipmap.rmblogo);
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            tvName.setText(coinBean.getCoinName() + getString(R.string.zx29));
            Glide.with(this).load(coinBean.getCoinUrl()).apply(ro).into(iv_logo);
        }
    }

    @OnClick({R.id.back_iv, R.id.tv_huazhuan, R.id.tv_chicang, R.id.tv_trade})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.tv_chicang:
                gotoPosition();
                break;
            case R.id.tv_huazhuan:
                transfer();
                break;
            case R.id.tv_trade:
                gotoTrade();
                break;
        }
    }

    private void gotoTrade() {
        //TODO 交易
        try {
            String coinName = "";
            if (coinBean != null) {
                coinName = coinBean.getCoinName();
            }
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_CONTRACT_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE);
            intent.putExtra("type", 1);//代表为合约交易
            intent.putExtra("coinName", coinName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void gotoPosition() {
        //TODO 持仓
        //TODO 交易
        try {
            String coinName = "";
            if (coinBean != null) {
                coinName = coinBean.getCoinName();
            }
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_CONTRACT_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE);
            intent.putExtra("type", 1);//代表为合约交易
            intent.putExtra("position", 1);//代表是跳转持仓
            intent.putExtra("coinName", coinName);//
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void transfer() {
        if (CoinwHyUtils.checkIsStopService(ContractCoinDetailActivity.this)) {
            return;
        }
        if (FastClickUtils.isFastClick(1000)) {
            return;
        }
        AccountTransferActivity.Companion.launch(this, TransferAccount.WEALTH.getValue(),TransferAccount.CONTRACT.getValue(),coinBean.getCoinId(),null,
                false,coinBean.getCoinName());
    }
}
