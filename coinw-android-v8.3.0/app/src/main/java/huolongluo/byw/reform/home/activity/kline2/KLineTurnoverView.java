package huolongluo.byw.reform.home.activity.kline2;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coinw.api.kx.model.XLatestDeal;
import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.BaseView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.kline2.adapter.KLineTurnoverViewAdapter;
import huolongluo.byw.reform.trade.fragment.RecycleViewDivider;
import huolongluo.byw.widget.WrapContentLinearLayoutManager;
import huolongluo.bywx.utils.ValueUtils;
/**
 * Created by Administrator on 2018/11/15 0015.
 */
public class KLineTurnoverView extends BaseView {
    private KLineTurnoverViewAdapter lastOrderAdapter;

    public KLineTurnoverView(Context context) {
        super(context);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_last_order;
    }

    @Override
    protected void initView() {
        onCreatedView(mRootView);
    }

    @Override
    protected void initData() {
    }

    private RecyclerView recyclerView;
    private TextView cnyTxt, coinNameTxt;

    @Override
    public View getView() {
        return mRootView;
    }

    protected void onCreatedView(View rootView) {
        recyclerView = rootView.findViewById(R.id.rv);
        cnyTxt = rootView.findViewById(R.id.cny_tv);
        coinNameTxt = rootView.findViewById(R.id.coinName_tv);
        List<XLatestDeal> latestList = new ArrayList<>();
        lastOrderAdapter = new KLineTurnoverViewAdapter(latestList);
        recyclerView.setAdapter(lastOrderAdapter);
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL, 1, R.color.color_2B2846));
        recyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        cnyTxt.setText("(" + ValueUtils.getString("", "--") + ")");
        coinNameTxt.setText("(" + ValueUtils.getString("", "--") + ")");
    }

    public void updateCoinName(String coinName,String cnyName){
        cnyTxt.setText("(" + ValueUtils.getString(cnyName, "--") + ")");
        coinNameTxt.setText("(" + ValueUtils.getString(coinName, "--") + ")");
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeCoin(BizEvent.Trade.CoinEvent coinEvent) {
        if (coinEvent == null) {
            return;
        }
        cnyTxt.setText("(" + ValueUtils.getString(coinEvent.cnyName, "--") + ")");
        coinNameTxt.setText("(" + ValueUtils.getString(coinEvent.coinName, "--") + ")");
        lastOrderAdapter.clear();
        lastOrderAdapter.notifyDataSetChanged();
    }
    //http使用，因为接口返回的是列表使用该方法
    public void refreshDatas(List<XLatestDeal> result){
        if(lastOrderAdapter==null){
            return;
        }
        if (result == null||result.size()==0) {
            lastOrderAdapter.updates(null);
            //TODO 处理异常情况
            Logger.getInstance().debug(TAG, "result is null.");
            return;
        }
        lastOrderAdapter.updates(result);
    }
    //socket返回的是单个对象使用该方法
    public void refreshSocketData(List<XLatestDeal> result){
        if(lastOrderAdapter==null){
            return;
        }
        if (result == null) {
            Logger.getInstance().debug(TAG, "result is null.");
            return;
        }
        lastOrderAdapter.updateSocket(result);
    }
}
