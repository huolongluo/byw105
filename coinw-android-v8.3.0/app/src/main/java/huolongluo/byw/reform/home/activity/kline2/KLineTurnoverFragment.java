package huolongluo.byw.reform.home.activity.kline2;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.coinw.api.kx.model.XLatestDeal;
import com.android.legend.model.CommonResult;
import com.android.legend.socketio.SocketIOClient;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.kline2.common.KLineEntity;
import huolongluo.byw.util.GsonUtil;
//k线最新成交页面
public class KLineTurnoverFragment extends BaseFragment {
    private KLineViewModel viewModel;

    private KLineEntity entity;
    private KLineTurnoverView turnoverView;
    private boolean isVisibleToUser=false;

    public static KLineTurnoverFragment newInstance(KLineEntity entity){
        Bundle bundle=new Bundle();
        bundle.putParcelable("entity",entity);
        KLineTurnoverFragment fragment=new KLineTurnoverFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initDagger() {
    }
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_kline_turnover;
    }
    @Override
    protected void initViewsAndEvents(View rootView) {
        entity=getArguments().getParcelable("entity");
        FrameLayout nested=rootView.findViewById(R.id.nested);
        turnoverView = new KLineTurnoverView(mContext);
        turnoverView.updateCoinName(entity.getCoinName(),entity.getCnyName());
        nested.addView(turnoverView.getView(),new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    private void subscribeSocket(){
        if(entity==null){
            return;
        }
        getData();
        String pair = entity.getCoinName() + "-" + entity.getCnyName();
        TypeToken type=new TypeToken<List<XLatestDeal>>(){};
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_LATEST_DEAL+pair,type,(it)->{
            Logger.getInstance().debug(TAG,"socket返回 最新成交:"+GsonUtil.obj2Json(it,List.class));
            try {
                List<XLatestDeal> list=(List<XLatestDeal>)it;
                if(list!=null&&list.size()>0){
                    turnoverView.refreshSocketData(list);
                }
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
        String pair = entity.getCoinName()+ "-" + entity.getCnyName();
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_LATEST_DEAL+pair);
    }
    private void getData(){
        viewModel= new ViewModelProvider(getActivity()).get(KLineViewModel.class);
        viewModel.getLatestData(entity.getId()+"");

        viewModel.getLatestData().observe(getActivity(), new Observer<CommonResult<List<XLatestDeal>>>() {
            @Override
            public void onChanged(CommonResult<List<XLatestDeal>> result) {
                Logger.getInstance().debug(TAG,"viewModel.getLatestData():"+GsonUtil.obj2Json(result,CommonResult.class));
                if(result.isSuccess()){
                    if(result.getData()!=null){
                        turnoverView.refreshDatas(result.getData());
                    }
                }
            }
        });
    }
    public void refreshData(KLineEntity entity){
        if(turnoverView!=null){//切币先清空列表
            turnoverView.refreshDatas(null);
        }
        if(isVisibleToUser){
            unSubscribeSocket();
        }
        this.entity=entity;
        Logger.getInstance().debug(TAG,"isVisibleToUser:"+isVisibleToUser+" entity:"+ GsonUtil.obj2Json(entity,KLineEntity.class));
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
        if(isVisibleToUser){
            subscribeSocket();
        }else{
            unSubscribeSocket();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isVisibleToUser){
            subscribeSocket();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unSubscribeSocket();
    }
}
