package huolongluo.byw.reform.home.activity.kline2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.coinw.api.kx.model.XDepthData;
import com.android.coinw.biz.trade.TradeUtils;
import com.android.coinw.biz.trade.model.OrderSide;
import com.android.legend.model.CommonResult;
import com.android.legend.socketio.SocketIOClient;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.ReqData;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.home.activity.kline2.adapter.KLineOrderAdapter;
import huolongluo.byw.reform.home.activity.kline2.common.KLine2Util;
import huolongluo.byw.reform.home.activity.kline2.common.KLineEntity;
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants;
import huolongluo.byw.reform.trade.bean.TradeOrder;
import huolongluo.byw.util.GsonUtil;
//币币的k线委托订单（深度）页面
public class KLineOrderFragment extends BaseFragment {
    private static final String TAG = "KLineOrderFragment";
    private KLineViewModel viewModel;

    private RecyclerView mRecyclerView;
    private KLineOrderAdapter mOrderBookAdapter;
    private LinearLayoutManager linearLayoutManager;
    private KLineEntity entity;
    private List<TradeOrder.OrderInfo> list=new ArrayList<>();
    private boolean isVisibleToUser=true;

    public static KLineOrderFragment newInstance(KLineEntity entity){
        Bundle bundle=new Bundle();
        bundle.putParcelable("entity",entity);
        KLineOrderFragment fragment=new KLineOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initDagger() {
    }
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_kline_order;
    }
    @Override
    protected void initViewsAndEvents(View rootView) {
        entity=getArguments().getParcelable("entity");

        mRecyclerView = rootView.findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mOrderBookAdapter = new KLineOrderAdapter(getActivity(),entity);
        mRecyclerView.setAdapter(mOrderBookAdapter);
        initData();
        initObserver();
    }
    private void initData(){
        viewModel=new ViewModelProvider(getActivity()).get(KLineViewModel.class);
    }
    private void initObserver(){
        viewModel.getDepthData().observe(getActivity(), new Observer<CommonResult<XDepthData>>() {
            @Override
            public void onChanged(CommonResult<XDepthData> xDepthDataCommonResult) {
                Logger.getInstance().debug(TAG, "viewModel.getDepthData() xDepthDataCommonResult:"+
                        GsonUtil.obj2Json(xDepthDataCommonResult,CommonResult.class));
                if(xDepthDataCommonResult.isSuccess()){
                    if (xDepthDataCommonResult.getData() == null) {
                        Logger.getInstance().debug(TAG, "result is null.");
                        return;
                    }
                    if(TextUtils.equals(xDepthDataCommonResult.getCode(),"200")){
                        TradeOrder order=new TradeOrder();
                        order.handicap= TradeUtils.getDepthList(xDepthDataCommonResult.getData());
                        if(order.handicap==null){
                            return;
                        }
                        updateList(order);
                    }
                }
            }
        });
    }

    private void subscribeSocket(){
        if(entity==null){
            return;
        }
        getData();
        String pair = entity.getCoinName() + "-" + entity.getCnyName();
        TypeToken type=new TypeToken<XDepthData>(){};
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_DEPTH+pair,type,(it)->{
            Logger.getInstance().debug(TAG,"socket返回 盘口:"+it);
            try {
                TradeOrder order=new TradeOrder();
                order.handicap= TradeUtils.getDepthList((XDepthData)it);
                if(order.handicap==null){
                    return null;
                }
                updateList(order);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }
    private void unSubscribeSocket(){
        if(entity==null){
            return;
        }
        String pair = entity.getCoinName() + "-" + entity.getCnyName();
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_DEPTH+pair);
    }
    //http更新列表
    private void updateList(TradeOrder result){
        Logger.getInstance().debug(TAG,"updateList result:"+GsonUtil.obj2Json(result,TradeOrder.class));
        if(result==null||!result.result)
        {
            if(mOrderBookAdapter!=null){
                mOrderBookAdapter.update(null);
            }
        }
        //刷新委托订单
        if(mOrderBookAdapter!=null){
            if(result.handicap==null){
                mOrderBookAdapter.update(null);
                return;
            }
            mOrderBookAdapter.update(result.handicap);
        }
    }
    private void getData(){
        viewModel.getDepthData(entity.getId()+"");
    }
    public void refreshData(KLineEntity entity){
        if(mOrderBookAdapter!=null){//切币先清空列表
            list.clear();
            mOrderBookAdapter.update(list);
        }
        if(isVisibleToUser){
            unSubscribeSocket();
        }
        this.entity=entity;
        Logger.getInstance().debug(TAG,"isVisibleToUser:"+isVisibleToUser+" entity:"+GsonUtil.obj2Json(entity,KLineEntity.class));
        if(isVisibleToUser){
            subscribeSocket();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(entity==null){
            return;
        }
        this.isVisibleToUser=isVisibleToUser;
        if(entity.getTradeType()!=Kline2Constants.TRADE_TYPE_ETF){//etf因为净值是在该socket内，需要切换tab也不反订阅socket
            if(isVisibleToUser){
                subscribeSocket();
            }else{
                unSubscribeSocket();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(entity.getTradeType()==Kline2Constants.TRADE_TYPE_ETF)//etf hint 不会关闭socket，所以每次回来都需要重新订阅
        {
            subscribeSocket();
        }else{
            if(isVisibleToUser){
                subscribeSocket();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unSubscribeSocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
