package huolongluo.byw.heyue.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;

public class HYCoinDetailActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.back_iv)
    ImageView backIV;
    @BindView(R.id.iv_logo)
    ImageView logoIV;
    @BindView(R.id.title_tv)
    TextView titleTxt;
    @BindView(R.id.tv_name)
    TextView nameTxt;
    @BindView(R.id.tv_account_balance)
    TextView accountBalanceTxt;//账户余额
    @BindView(R.id.tv_free_margin)
    TextView freeMarginTxt;//可用保证金
    @BindView(R.id.tv_commission_margin)
    TextView commissionMarginTxt;//委托保证金
    @BindView(R.id.tv_position_margin)
    TextView positionMarginTxt;//仓位保证金
    @BindView(R.id.tv_transfer)
    TextView transferTxt;//划转
    @BindView(R.id.tv_trade)
    TextView tradeTxt;//交易
    @BindView(R.id.tv_position)
    TextView positionTxt;//持仓
    Unbinder unbinder;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_hy_coin_detail;
    }

    @Override
    protected void injectDagger() {

    }


    protected void initViewsAndEvents() {
        viewClick(backIV, this);
        viewClick(transferTxt, this);
        viewClick(tradeTxt, this);
        viewClick(positionTxt, this);
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == R.id.back_iv) {
            finish();
        } else if (vId == R.id.tv_transfer) {
            //划转
            gotoTransfer();
        } else if (vId == R.id.tv_trade) {
            //交易
            gotoTrade();
        } else if (vId == R.id.tv_position) {
            //持仓
            gotoPosition();
        }
    }

    private void gotoTransfer() {
        //TODO 划转
    }

    private void gotoTrade() {
        //TODO 交易
        try {
            int id = 0;
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_CONTRACT_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE);
            intent.putExtra("tradeId", id);
            intent.putExtra("type", 1);//代表为合约交易
            intent.putExtra("coinName", "");//由H5未传过来，本地传空串到交易页面，待数据加载后才更新界面
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
            int id = 0;
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_CONTRACT_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE);
            intent.putExtra("tradeId", id);
            intent.putExtra("type", 1);//代表为合约交易
            intent.putExtra("position", 1);//代表是跳转持仓
            intent.putExtra("coinName", "");//由H5未传过来，本地传空串到交易页面，待数据加载后才更新界面
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }
}
