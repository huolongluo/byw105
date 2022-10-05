package huolongluo.byw.reform.mine.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.coinw.biz.trade.TradeViewModel;
import com.android.coinw.biz.trade.model.OrderSide;
import com.android.legend.model.CommonResult;
import com.android.legend.model.Page2;
import com.android.legend.model.enumerate.order.OrderStatus;
import com.android.legend.model.order.OrderItemBean;
import com.legend.common.base.ThemeActivity;
import com.legend.common.util.StatusBarUtils;
import com.legend.common.util.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.mine.adapter.OrderAdapter;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.NumberUtil;
import huolongluo.byw.util.system.KeybordS;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
/**
 * 全部委托和历史委托
 */
public class TradeOrderListActivity extends ThemeActivity implements View.OnClickListener {
    private static final String TAG = "TradeOrderListActivity";
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.lv_content)
    PullToRefreshListView lv_content;
    @BindView(R.id.shaixuan)
    LinearLayout shaixuan;
    @BindView(R.id.saixuan_view)
    LinearLayout saixuan_view;
    @BindView(R.id.coinName_tv)
    EditText coinName_tv;
    @BindView(R.id.confirm_bn)
    Button confirm_bn;
    @BindView(R.id.buy_bn)
    Button buy_bn;
    @BindView(R.id.sell_bn)
    Button sell_bn;
    @BindView(R.id.reset_bn)
    Button reset_bn;
    @BindView(R.id.all_order)
    TextView all_order;
    @BindView(R.id.history_order)
    TextView history_order;
    @BindView(R.id.current_tv)
    TextView current_tv;
    @BindView(R.id.net_error_view)
    FrameLayout net_error_view;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    @BindView(R.id.selectCny_rl)
    RelativeLayout selectCny_rl;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.buy_bn_1)
    Button buy_bn_1;
    @BindView(R.id.sell_bn_1)
    Button sell_bn_1;
    @BindView(R.id.ll_menu_root)
    View menuView;
    @BindView(R.id.history_status_ll)
    LinearLayout history_status_ll;
    private ArrayAdapter<String> arrAdapter;
    Unbinder unbinder;
    private List<OrderItemBean> list = new ArrayList<>();
    private OrderAdapter adapter;
    String cnytName = "0";
    String coinName = "0";
    String symbol =null;//筛选币的id,查所有币只能传null或不传，不能传0
    String[] cnyList = new String[]{"CNYT", "USDT"};

    public String cnyNameSer = "CNYT";
    //新加需求
    @BindView(R.id.iv_doubt)
    View doubtView;
    @BindView(R.id.tv_doubt_desc)
    TextView doubtDescTxt;
    @BindView(R.id.btn_day7)
    Button day7Btn;
    @BindView(R.id.btn_day30)
    Button day30Btn;
    @BindView(R.id.btn_day90)
    Button day90Btn;
    @BindView(R.id.btn_day180)
    Button day180Btn;
    private TradeViewModel viewModel;
    private long afterId;
    private long before = 0;
    private boolean active=true;//是否为活跃订单
    private Long startAt,endAt;
    private String side;
    private String orderStatus;
    private long cancelOrderId=0;//撤单的id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.activity_tradeorder_list);
        unbinder = ButterKnife.bind(this);

        back_iv.setOnClickListener(this);
        DialogManager2.INSTANCE.showProgressDialog(this, getString(R.string.b6));
        adapter = new OrderAdapter(this, list);
        adapter.setListener(new onDeleteListener() {
            @Override
            public void delete(long orderId) {
                cancelOlder(orderId);
            }
        });
        lv_content.setPullLoadEnable(false);
        lv_content.setAdapter(adapter);
        shaixuan.setOnClickListener(this);
        confirm_bn.setOnClickListener(this);
        saixuan_view.setVisibility(View.GONE);
        buy_bn.setOnClickListener(this);
        sell_bn.setOnClickListener(this);
        reset_bn.setOnClickListener(this);
        btn_reLoad.setOnClickListener(this);
        selectCny_rl.setOnClickListener(this);
        buy_bn_1.setOnClickListener(this);
        sell_bn_1.setOnClickListener(this);
        all_order.setOnClickListener(this);
        history_order.setOnClickListener(this);
        menuView.setOnClickListener(this);
        doubtView.setOnClickListener(this);
        day7Btn.setOnClickListener(this);
        day30Btn.setOnClickListener(this);
        day90Btn.setOnClickListener(this);
        day180Btn.setOnClickListener(this);
        lv_content.setPullToListViewListener(new PullToRefreshListView.PullToListViewListener() {
            @Override
            public void onRefresh() {
                afterId=0;
                before = 0;
                getOrderList();
            }

            @Override
            public void onLoadMore() {
                getOrderList();
            }
        });
        List<TradingArea.TradItem> list = MarketDataPresent.getSelf().getTitleList();
        if (list != null) {
            if (list.size() > 0) {
                cnyNameSer = list.get(0).getfShortName();
            }
            cnyList = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                cnyList[i] = list.get(i).getfShortName();
            }
        }
        arrAdapter = new ArrayAdapter<String>(this, R.layout.spinner_view2, cnyList);
        spinner.setAdapter(arrAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cnyNameSer = cnyList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        buy_bn_1.setVisibility(View.GONE);
        sell_bn_1.setVisibility(View.GONE);
        initSelected(all_order, history_order);

        initData();
    }
    private void initData(){
        viewModel=new ViewModelProvider(this).get(TradeViewModel.class);
        initObserver();
        getOrderList();
    }
    private void initObserver(){
        viewModel.getOrdersData().observe(this, new Observer<CommonResult<Page2<List<OrderItemBean>>>>() {
            @Override
            public void onChanged(CommonResult<Page2<List<OrderItemBean>>> page2CommonResult) {
                DialogManager2.INSTANCE.dismiss();
                if(page2CommonResult.isSuccess()){
                    net_error_view.setVisibility(View.GONE);
                    lv_content.setVisibility(View.VISIBLE);
                    lv_content.stopRefresh();
                    if (coinName_tv != null && android.text.TextUtils.isEmpty(coinName_tv.getText().toString().trim())) {
                        current_tv.setText(getString(R.string.filtrate));
                    } else if (coinName_tv != null) {
                        current_tv.setText((coinName_tv.getText().toString().trim() + "/" + cnyNameSer).toUpperCase());
                    }
                    if(page2CommonResult.getData()==null||page2CommonResult.getData().getResult()==null) {
                        return;
                    }

                    before = page2CommonResult.getData().getBefore();

                    if(afterId==0) {
                        list.clear();
                    }
                    list.addAll(page2CommonResult.getData().getResult());
                    if(page2CommonResult.getData().getHasMore()) {
                        lv_content.setPullLoadEnable(true);
                    }
                    else {
                        lv_content.setPullLoadEnable(false);
                    }
                    if (list.size() > 1) {
                        afterId = list.get(list.size() - 1).getOrderId();
                    }
                    adapter.notifyDataSetChanged();

                    View emptyView = lv_content.getEmptyView();
                    if (list.size() <= 0) {
                        if (emptyView == null) {
                            View view = View.inflate(getApplicationContext(), R.layout.empty_view1, null);
                            updateLanguageText(view);
                            lv_content.setEmptyView(view);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            updateLanguageText(emptyView);
                        }
                    } else {
                        if (emptyView != null) {
                            emptyView.setVisibility(View.GONE);
                        }
                    }
                }else{
                    SnackBarUtils.ShowRed(TradeOrderListActivity.this, page2CommonResult.getMessage());
                    if(TextUtils.equals(page2CommonResult.getCode(),"-1")){
                        net_error_view.setVisibility(View.VISIBLE);
                        View emptyView = lv_content.getEmptyView();
                        if (emptyView != null) {
                            emptyView.setVisibility(View.GONE);
                        }
                        lv_content.setVisibility(View.GONE);
                        lv_content.stopRefresh();
                    }
                }
            }
        });

        viewModel.getCancelOrderData().observe(this, new Observer<CommonResult<String>>() {
            @Override
            public void onChanged(CommonResult<String> stringCommonResult) {
                DialogManager2.INSTANCE.dismiss();
                if(stringCommonResult.isSuccess()){
                    net_error_view.setVisibility(View.GONE);
                    //为和IOS统一，故取本地文案实现
                    SnackBarUtils.ShowBlue(TradeOrderListActivity.this, getString(R.string.cancel_order));
                    for (OrderItemBean bean : list) {
                        if (bean.getOrderId() == cancelOrderId) {
                            list.remove(bean);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (list.size() <= 0) {
                        View emptyView = lv_content.getEmptyView();
                        if (emptyView == null) {
                            View view = View.inflate(getApplicationContext(), R.layout.empty_view1, null);
                            lv_content.setEmptyView(view);
                            updateLanguageText(view);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            updateLanguageText(emptyView);
                        }
                    } else {
                        View emptyView = lv_content.getEmptyView();
                        if (emptyView != null) {
                            emptyView.setVisibility(View.GONE);
                        }
                    }
                }else{
                    SnackBarUtils.ShowRed(TradeOrderListActivity.this, stringCommonResult.getMessage());
                    if(TextUtils.equals(stringCommonResult.getCode(),"-1")){
                        net_error_view.setVisibility(View.VISIBLE);
                        View emptyView = lv_content.getEmptyView();
                        if (emptyView != null) {
                            emptyView.setVisibility(View.GONE);
                        }
                        lv_content.setVisibility(View.GONE);
                        lv_content.stopRefresh();
                    }
                }
            }
        });
    }
    private void reset(){
        clearFilter();
        afterId=0;
        buy_bn.setBackgroundResource(R.drawable.trade_bug4);
        buy_bn_1.setBackgroundResource(R.drawable.trade_bug4);
        sell_bn.setBackgroundResource(R.drawable.trade_bug4);
        sell_bn_1.setBackgroundResource(R.drawable.trade_bug4);
        saixuan_view.setVisibility(View.GONE);
        current_tv.setText(R.string.filtrate);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_bn_1://用户核销
                clickButton(buy_bn_1);
                orderStatus= OrderStatus.CANCELLED.getStatus();
                break;
            case R.id.sell_bn_1://完全成交
                clickButton(sell_bn_1);
                orderStatus= OrderStatus.FILLED.getStatus();
                break;
            case R.id.reset_bn:
                //清除时间过滤条件
                reset();
                getOrderList();
                break;
            case R.id.buy_bn://买入
                resetButton();
                buy_bn.setBackgroundResource(R.drawable.quote_blue);
                side= OrderSide.BUY.getSide();
                break;
            case R.id.sell_bn://卖出
                resetButton();
                sell_bn.setBackgroundResource(R.drawable.quote_bg_sell);
                side= OrderSide.SELL.getSide();
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.shaixuan:
                if (saixuan_view.getVisibility() == View.GONE) {
                    saixuan_view.setVisibility(View.VISIBLE);
                    current_tv.setText(R.string.putaway);
                } else {
                    if (KeybordS.isSoftInputShow(this)) {
                        KeybordS.closeKeybord(coinName_tv, this);
                    }
                    saixuan_view.setVisibility(View.GONE);
                    current_tv.setText(R.string.filtrate);
                }
                break;
            case R.id.confirm_bn://筛选确定
                afterId = 0;
                String cnyt = cnyNameSer;
                String coin = coinName_tv.getText().toString().trim();
                if (!coin.isEmpty()) {
                    cnytName = cnyt;
                    coinName = coin;
                    symbol= CurrencyPairUtil.getPairId(coinName+CurrencyPairUtil.SEPARATOR+cnytName)+"";
                    Logger.getInstance().debug(TAG,"symbol:"+symbol);
                    if(TextUtils.equals(symbol,"0")){
                        MToast.show(this, getString(R.string.coin_error), 1);
                        symbol=null;
                        return;
                    }
                }
                getOrderList();
                if (saixuan_view.getVisibility() == View.VISIBLE) {
                    saixuan_view.setVisibility(View.GONE);
                    current_tv.setText(R.string.filtrate);
                    if (KeybordS.isSoftInputShow(this)) {
                        KeybordS.closeKeybord(coinName_tv, this);
                    }
                }
                break;
            case R.id.btn_reLoad:
                afterId = 0;
                getOrderList();
                break;
            case R.id.selectCny_rl://筛选cny
                break;
            case R.id.history_order:
                reset();
                active=false;
                buy_bn.setBackgroundResource(R.drawable.trade_bug4);
                buy_bn_1.setBackgroundResource(R.drawable.trade_bug4);
                sell_bn.setBackgroundResource(R.drawable.trade_bug4);
                sell_bn_1.setBackgroundResource(R.drawable.trade_bug4);
                doubtView.setVisibility(View.VISIBLE);
                buy_bn.setVisibility(View.GONE);
                sell_bn.setVisibility(View.GONE);
                buy_bn_1.setVisibility(View.VISIBLE);
                sell_bn_1.setVisibility(View.VISIBLE);

                getOrderList();
                initSelected(history_order, all_order);
                break;
            case R.id.all_order:
                reset();
                active=true;
                buy_bn.setBackgroundResource(R.drawable.trade_bug4);
                buy_bn_1.setBackgroundResource(R.drawable.trade_bug4);
                sell_bn.setBackgroundResource(R.drawable.trade_bug4);
                sell_bn_1.setBackgroundResource(R.drawable.trade_bug4);
                doubtView.setVisibility(View.GONE);
                buy_bn_1.setVisibility(View.GONE);
                sell_bn_1.setVisibility(View.GONE);
                buy_bn.setVisibility(View.VISIBLE);
                sell_bn.setVisibility(View.VISIBLE);

                getOrderList();
                initSelected(all_order, history_order);
                break;
            case R.id.ll_menu_root:
                if (saixuan_view.getVisibility() == View.VISIBLE) {
                    saixuan_view.setVisibility(View.GONE);
                    current_tv.setText(R.string.filtrate);
                    if (KeybordS.isSoftInputShow(this)) {
                        KeybordS.closeKeybord(coinName_tv, this);
                    }
                }
                break;
            case R.id.iv_doubt://
                showOrHideView();
                break;
            case R.id.btn_day7:
                changeDay(day7Btn);
                break;
            case R.id.btn_day30:
                changeDay(day30Btn);
                break;
            case R.id.btn_day90:
                changeDay(day90Btn);
                break;
            case R.id.btn_day180:
                changeDay(day180Btn);
                break;
        }
    }
    private void clearFilter(){
        clearDay();
        symbol=null;
        side=null;
        orderStatus=null;
        coinName="";
        cnytName="";
        coinName_tv.setText("");
    }
    private void clearDay() {
        startAt = null;//清除时间过滤条件
        endAt=null;
        day7Btn.setBackgroundResource(R.drawable.trade_bug4);
        day30Btn.setBackgroundResource(R.drawable.trade_bug4);
        day90Btn.setBackgroundResource(R.drawable.trade_bug4);
        day180Btn.setBackgroundResource(R.drawable.trade_bug4);
        day7Btn.setTextColor(getResources().getColor(R.color.white));
        day30Btn.setTextColor(getResources().getColor(R.color.white));
        day90Btn.setTextColor(getResources().getColor(R.color.white));
        day180Btn.setTextColor(getResources().getColor(R.color.white));
    }

    private void changeDay(Button targetBtn) {
        clearDay();
        targetBtn.setBackgroundResource(R.drawable.trade_bug3);
        endAt=System.currentTimeMillis();
        switch (NumberUtil.toInt((String) targetBtn.getTag())){
            case 1://7day
                startAt=endAt.longValue()-7*24*60*60*1000l;
                break;
            case 3://1month
                startAt=endAt.longValue()-30*24*60*60*1000l;
                break;
            case 4://3month
                startAt=endAt.longValue()-90*24*60*60*1000l;
                break;
            case 5://6monty
                startAt=endAt.longValue()-180*24*60*60*1000l;
                break;
        }
    }

    private void showOrHideView() {
        if (doubtDescTxt.getVisibility() == View.VISIBLE) {
            doubtDescTxt.setVisibility(View.GONE);
        } else {
            doubtDescTxt.setVisibility(View.VISIBLE);
        }
    }

    private void clickButton(Button targetBtn) {
        buy_bn_1.setBackgroundResource(R.drawable.trade_bug4);
        sell_bn_1.setBackgroundResource(R.drawable.trade_bug4);
        targetBtn.setBackgroundResource(R.drawable.trade_bug3);
    }


    private void resetButton() {
        buy_bn.setBackgroundResource(R.drawable.trade_bug4);
        sell_bn.setBackgroundResource(R.drawable.trade_bug4);
    }
    //撤单
    private void cancelOlder(long orderId) {
        cancelOrderId=orderId;
        DialogManager2.INSTANCE.showProgressDialog(this);
        viewModel.cancelOrder(orderId);
    }

    void getOrderList() {
        viewModel.getOrders(active, before, startAt, endAt, symbol, orderStatus, side);
    }

    private void updateLanguageText(View view) {
        //TODO 切换多语言时，放在second目录下的资源，有可能未被翻译
        //解决思路：
        //1、重新配置对应的资源文件
        //2、重新设置多语言的完整方案（现有方案有待优化）
        //3、暂时采用规避方法
        String text = getString(R.string.no_data);
        try {
            ((TextView) view.findViewById(R.id.tv_nodata)).setText(text);
        } catch (Throwable t) {
            Logger.getInstance().debug("TradeOrderListActivity", t);
        }
    }

    public interface onDeleteListener {
        void delete(long orderId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void initSelected(TextView view1, TextView view2) {
        view1.setTextColor(ThemeUtil.INSTANCE.getThemeColor(this, R.attr.col_text_title));
        view2.setTextColor(ThemeUtil.INSTANCE.getThemeColor(this, R.attr.col_text_content));
    }
}
