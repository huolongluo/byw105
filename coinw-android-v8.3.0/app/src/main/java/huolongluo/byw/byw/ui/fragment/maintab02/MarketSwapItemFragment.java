package huolongluo.byw.byw.ui.fragment.maintab02;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.coinw.biz.event.BizEvent;
import com.legend.modular_contract_sdk.api.ProductTickerProvider;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;
import com.legend.modular_contract_sdk.component.market_listener.Ticker;
import com.legend.modular_contract_sdk.repository.model.Product;
import com.legend.modular_contract_sdk.ui.contract.vm.SwapContractViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * zh行情合约的itemfragment
 */
public class MarketSwapItemFragment extends Fragment implements View.OnClickListener{
    private final String TAG = "MarketSwapItemFragment";
    private View rootView;
    private List<Product> products=new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    protected RecyclerView rv;
    protected MarketSwapItemAdapter adapter = null;
    private ImageView sortIV1, sortIV2, sortIV3;
    private int sortType = 0;//排序方式 1,11,2,12,3,13
    private int tag = 0;//区分类型的标志 反向：0，1：正向 , 2 : 混合 , 3 : 模拟
    private boolean mIsVisibleToUser=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_market_swap_item, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    public static MarketSwapItemFragment newInstance(int tag){
        MarketSwapItemFragment fragment=new MarketSwapItemFragment();
        Bundle arguments=new Bundle();
        arguments.putInt("tag",tag);
        fragment.setArguments(arguments);
        return fragment;
    }

    private void initView() {
        tag=getArguments().getInt("tag");
        EventBus.getDefault().register(this);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        rv = rootView.findViewById(R.id.rv);
        initAdapter();
        sortIV1 = rootView.findViewById(R.id.sort_iv1);
        sortIV2 = rootView.findViewById(R.id.sort_iv2);
        sortIV3 = rootView.findViewById(R.id.sort_iv3);

        rootView.findViewById(R.id.price_market_ll).setOnClickListener(this);
        rootView.findViewById(R.id.zhangf_market_ll).setOnClickListener(this);
        refreshLayout.setOnRefreshListener(() -> {
            getData();
        });
    }
    protected void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        ((DefaultItemAnimator)rv.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new MarketSwapItemAdapter(requireContext());
        rv.setAdapter(adapter);
    }
    private void refreshAdapter(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.price_market_ll:
                if (sortType == 2) {
                    sortIV2.setImageResource(R.mipmap.market_sort_2);
                    sortType = 12;
                    Collections.sort(products, (o1, o2) -> {
                        if(DoubleUtils.parseDouble(o1.getMLast()) -DoubleUtils.parseDouble(o2.getMLast())>0) return 1; else return -1;
                    });
                    refreshAdapter();
                } else if (sortType == 12) {
                    sortIV2.setImageResource(R.mipmap.market_sort_0);
                    sortType = 0;
                    sortRestore();
                } else {
                    sortIV2.setImageResource(R.mipmap.market_sort_1);
                    sortIV1.setImageResource(R.mipmap.market_sort_0);
                    sortIV3.setImageResource(R.mipmap.market_sort_0);
                    sortType = 2;
                    Collections.sort(products, (o1, o2) -> {
                        if(DoubleUtils.parseDouble(o2.getMLast())-DoubleUtils.parseDouble(o1.getMLast())>0) return 1; else return -1;
                    });
                    refreshAdapter();
                }
                break;
            case R.id.zhangf_market_ll:
                if (sortType == 3) {
                    sortIV3.setImageResource(R.mipmap.market_sort_2);
                    sortType = 13;
                    Collections.sort(products, (o1, o2) -> {
                        if(DoubleUtils.parseDouble(o1.getMChangeRate())-DoubleUtils.parseDouble(o2.getMChangeRate())>0) return 1; else return -1;
                    });
                    refreshAdapter();
                } else if (sortType == 13) {
                    sortIV3.setImageResource(R.mipmap.market_sort_0);
                    sortType = 0;
                    sortRestore();
                } else {
                    sortIV3.setImageResource(R.mipmap.market_sort_1);
                    sortIV1.setImageResource(R.mipmap.market_sort_0);
                    sortIV2.setImageResource(R.mipmap.market_sort_0);
                    sortType = 3;
                    Collections.sort(products, (o1, o2) -> {
                        if(DoubleUtils.parseDouble(o2.getMChangeRate())-DoubleUtils.parseDouble(o1.getMChangeRate())>0) return 1; else return -1;
                    });
                    refreshAdapter();
                }
                break;
        }
    }
    private void sortRestore(){
        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getMSort()-o2.getMSort();
            }
        });
        refreshAdapter();
    }

    private void initData() {
        ProductTickerProvider.INSTANCE.setMTickerCallback( ticker -> {
            Logger.getInstance().debug(TAG,"setMTickerCallback ticker:"+ GsonUtil.obj2Json(ticker,Ticker.class));
            for (int i = 0; i < products.size() ; i++) {
                if(products.get(i).getMBase().toLowerCase().equals(ticker.getCurrencyCode().toLowerCase())){
                    products.get(i).setMLast(ticker.getLast());
                    products.get(i).setMChangeRate(ticker.getChangeRate());
                    adapter.notifyItemChanged(i);
                    break;
                }

            }
            return null;
        });
        ProductTickerProvider.INSTANCE.setMProductCallback(list->{
            Logger.getInstance().debug(TAG,"setMProductCallback 获取合约所有交易对成功");
            if(list==null||list.size()==0) return null;
            Logger.getInstance().debug(TAG,"getMProductsLiveData products:"+ GsonUtil.obj2Json(list,List.class));
            setAdapterData(list);
            return null;
        });
    }
    private void setAdapterData(List<Product> list){
        if (products == null||adapter==null)  return ;
        products.clear();
        products.addAll(list);
        adapter.setNewInstance(products);
        adapter.notifyDataSetChanged();
    }
    public void resetView() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    public boolean hasData = false;

    public void refresh() {
        resetView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(rv==null) return ;
        mIsVisibleToUser=isVisibleToUser;
        Logger.getInstance().debug(TAG,"setUserVisibleHint mIsVisibleToUser:"+mIsVisibleToUser);
        if(isVisibleToUser){
            refresh();
            getData();
        }else {
            removeSocketListener();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Logger.getInstance().debug(TAG,"onResume mIsVisibleToUser:"+mIsVisibleToUser);
        if(mIsVisibleToUser){
            getData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        removeSocketListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void stopRefresh() {
        try {
            if (refreshLayout == null) {
                return;
            }
            refreshLayout.setRefreshing(false);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public void getData() {
        Logger.getInstance().debug(TAG,"getData");
        stopRefresh();
        removeSocketListener();
        if(ProductTickerProvider.INSTANCE.getMProductList()==null||ProductTickerProvider.INSTANCE.getMProductList().size()==0){//读取交易对接口后不用再读取
            ProductTickerProvider.INSTANCE.start();
        }else{
            if(adapter.getData()==null||adapter.getData().size()==0){//getMProductList有时会有数据但adapter还未设置数据
                setAdapterData(ProductTickerProvider.INSTANCE.getMProductList());
            }
            ProductTickerProvider.INSTANCE.addTickerListener();
        }
    }
    private void removeSocketListener(){
        CoinwHyUtils.removeAllMarketListener(ProductTickerProvider.INSTANCE.getMMarketListenerList());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPricingMethod(BizEvent.ChangeExchangeRate event) {
        refreshAdapter();
    }

}
