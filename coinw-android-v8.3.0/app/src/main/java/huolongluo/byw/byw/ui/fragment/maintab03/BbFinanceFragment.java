package huolongluo.byw.byw.ui.fragment.maintab03;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.legend.model.BaseBizResponse;
import com.android.legend.model.CommonResult;
import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.model.finance.BbFinanceAccountListBean;
import com.android.legend.model.finance.BbFinanceBean;
import com.android.legend.ui.transfer.AccountTransferRecordActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFinanceFragment;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.adapter.BbFinanceAdapter;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.activity.FinanceViewModel;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * 币币账户的资产界面
 */
public class BbFinanceFragment extends BaseFinanceFragment {
    @BindView(R.id.rv_assets)
    RecyclerView rv_assets;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;
    @BindView(R.id.search_et)
    EditText search_et;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    @BindView(R.id.no_data_view)
    LinearLayout no_data_view;
    @BindView(R.id.error_vew_ll)
    LinearLayout error_vew_ll;
    ////默认隐藏小额币种
    private String isOpen = "1";
    private String isClose = "0";
    public static int select = -1;//搜索后选中的那一项的id。根据id来定位coin的位置
    private List<BbFinanceAccountListBean> mAssetcoinsList = new ArrayList<>();//所有币种列表
    private List<BbFinanceAccountListBean> mAssetcoinsList1 = new ArrayList<>();//折合大于0的币种列表，用于隐藏小额币种
    private List<BbFinanceAccountListBean> mSearchList = new ArrayList<>();
    private BbFinanceAdapter userAssetsAdapter;
    private BbFinanceBean userWalletbn;
    private FinanceViewModel viewModel;
    private boolean isVisibleToUser;

    public static BbFinanceFragment getInstance() {
        Bundle args = new Bundle();
        BbFinanceFragment fragment = new BbFinanceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_finance;
    }

    @Override
    protected void initDagger() {

    }

    @Override
    protected void initViewsAndEvents(View rootView) {
        ivHideCoin.setSelected(true);
        EventBus.getDefault().register(this);
        error_vew_ll.setVisibility(View.GONE);
        error_vew_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserWallet();
            }
        });
        btn_reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserWallet();
            }
        });
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
                if (!s.toString().isEmpty()) {
                } else {
                    no_data_view.setVisibility(View.GONE);
                }
            }
        });
        ivHideCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHideCoin.setSelected(!ivHideCoin.isSelected());
                if (isOpen.equals("1")) {
                    isOpen = "0";
                    userAssetsAdapter.replaceAll(mAssetcoinsList);
                } else {
                    isOpen = "1";
                    userAssetsAdapter.replaceAll(mAssetcoinsList1);
                }
                SPUtils.saveString(mContext,"isOpenBb",isOpen);
            }
        });
        tvHideCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.question_des), getString(R.string.iknow1));
            }
        });
        tvFinanceEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClose.equals("0")) {
                    isClose = "1";
                    tv_totalasset.setText("********");
                    tvUnit.setText("***");
                    refreshFinanceEyeUi(false);
                    if (userAssetsAdapter != null) {
                        userAssetsAdapter.setHide(true);
                        userAssetsAdapter.notifyDataSetChanged();
                    }
                } else {
                    isClose = "0";
                    if (userWalletbn != null) {
                        refreshTotalAsset(userWalletbn.getTotal());
                        refreshFinanceEyeUi(true);
                    }
                    if (userAssetsAdapter != null) {
                        userAssetsAdapter.setHide(false);
                        userAssetsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        tvOperate1.setVisibility(View.VISIBLE);
        tvOperate1.setText(getString(R.string.a35));
        setLeftDrawable(tvOperate1,R.mipmap.ic_record_white);
        eventClick(tvOperate1).subscribe(o -> {
            AccountTransferRecordActivity.Companion.launch(getActivity());
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        rv_assets.setNestedScrollingEnabled(false);
        rv_assets.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        userAssetsAdapter = new BbFinanceAdapter(getActivity(), mAssetcoinsList);
        rv_assets.setAdapter(userAssetsAdapter);
        // 设置下拉进度的主题颜色
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserWallet();
            }
        });
        initObserver();
        getData();
    }
    private FinanceViewModel getViewModel(){
        if(viewModel==null){
            viewModel = new ViewModelProvider(getActivity()).get(FinanceViewModel.class);
        }
        return viewModel;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //控制切换至当前界面时的业务操作
        this.isVisibleToUser=isVisibleToUser;
        getData();
        super.setUserVisibleHint(isVisibleToUser);
    }
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
    private void getData(){
        if(isVisibleToUser){
            getUserWallet();
        }
    }

    private void initObserver() {
        getViewModel().getBbFinanceData().observe(this, new Observer<CommonResult<BaseBizResponse<BbFinanceBean>>>() {
            @Override
            public void onChanged(CommonResult<BaseBizResponse<BbFinanceBean>> baseBizResponseCommonResult) {
                DialogManager.INSTANCE.dismiss();
                if (baseBizResponseCommonResult.isSuccess()) {
                    if (!isAdded()) {
                        return;
                    }
                    error_vew_ll.setVisibility(View.GONE);
                    if (refresh_layout != null && refresh_layout.isRefreshing()) {
                        SnackBarUtils.ShowBlue(getActivity(), getString(R.string.dd45));
                        refresh_layout.setRefreshing(false);
                    }
                    try {
                        userWalletbn = baseBizResponseCommonResult.getData().getBizVo();
                        if (isClose.equals("1")) {
                            tv_totalasset.setText("********");
                            tvUnit.setText("***");
                            refreshFinanceEyeUi(false);
                        } else {
                            if (userWalletbn != null) {
                                refreshTotalAsset(userWalletbn.getTotal());
                                refreshFinanceEyeUi(true);
                            }
                        }
                        mAssetcoinsList = userWalletbn.getAccounts();
                        mAssetcoinsList1.clear();
                        //存储是否勾选隐藏小额币种来显示数据，默认是隐藏小额币种
                        String  isOpen1 = SPUtils.getString(mContext, "isOpenBb", "");
                        if (!isOpen1.isEmpty()) {
                            isOpen = isOpen1;
                        }
                        if (isOpen.equals("0")) {
                            ivHideCoin.setSelected(false);
                            userAssetsAdapter.replaceAll(mAssetcoinsList);
                            for (int i = 0; i < mAssetcoinsList.size(); i++) {
                                if (DoubleUtils.parseDouble(mAssetcoinsList.get(i).getBalanceAmount()) > 1) {
                                    mAssetcoinsList1.add(mAssetcoinsList.get(i));
                                }
                            }
                        } else {
                            for (int i = 0; i < mAssetcoinsList.size(); i++) {
                                if (DoubleUtils.parseDouble(mAssetcoinsList.get(i).getBalanceAmount()) > 1) {
                                    mAssetcoinsList1.add(mAssetcoinsList.get(i));
                                }
                            }
                            ivHideCoin.setSelected(true);
                            userAssetsAdapter.replaceAll(mAssetcoinsList1);
                        }
                    } catch (Exception e) {
                        if (!userAssetsAdapter.hasData()) {
                            error_vew_ll.setVisibility(View.VISIBLE);
                        } else {
                            error_vew_ll.setVisibility(View.GONE);
                        }
                        SnackBarUtils.ShowRed(getActivity(), getString(R.string.dd47));
                        e.printStackTrace();
                    }
                } else {
                    if (isDetached() || !isAdded()) {
                        return;
                    }
                    if (refresh_layout != null && refresh_layout.isRefreshing()) {
                        refresh_layout.setRefreshing(false);
                    }
                    if (userAssetsAdapter.getItemCount() > 0) {
                        error_vew_ll.setVisibility(View.GONE);
                    } else {
                        error_vew_ll.setVisibility(View.VISIBLE);
                    }
                    if(TextUtils.equals(baseBizResponseCommonResult.getMessage(),getString(R.string.net_exp))){//因为该接口data内嵌套了一层data，需要区分错误提示文案
                        SnackBarUtils.ShowRed(getActivity(), baseBizResponseCommonResult.getMessage());
                    }else{
                        if(baseBizResponseCommonResult.getData()!=null&&baseBizResponseCommonResult.getData().getBizMsg()!=null){
                            SnackBarUtils.ShowRed(getActivity(), baseBizResponseCommonResult.getData().getBizMsg());//接口给的异常文案
                        }
                    }

                }
            }
        });
    }

    void search(String text) {
        mSearchList.clear();
        if (TextUtils.isEmpty(text)) {
            if (isOpen.equals("0")) {
                mSearchList.addAll(mAssetcoinsList);
            } else {
                mSearchList.addAll(mAssetcoinsList1);
            }
        } else {
            for (BbFinanceAccountListBean bean : mAssetcoinsList) {
                if (bean.getCoin().getCoinName().toLowerCase().contains(text.toLowerCase())) {
                    mSearchList.add(bean);
                }
            }
        }
        userAssetsAdapter.replaceAll(mSearchList);
        if (mSearchList.size() == 0) {
            no_data_view.setVisibility(View.VISIBLE);
        } else {
            no_data_view.setVisibility(View.GONE);
        }
    }

    //获取资产
    private void getUserWallet() {
        if (refresh_layout!=null&&!refresh_layout.isRefreshing()) {
            if (getActivity() != null) {
                if (!getActivity().isFinishing()) {
                    DialogManager.INSTANCE.showProgressDialog(getActivity(), getString(R.string.dd42));
                }
            }
        }
        if (refresh_layout!=null&&!BaseApp.isNetAvailable) {
            if (refresh_layout.isRefreshing()) {
                refresh_layout.setRefreshing(false);
            }
            DialogManager.INSTANCE.dismiss();
            if (userAssetsAdapter.getItemCount() > 0) {
                error_vew_ll.setVisibility(View.GONE);
            } else {
                error_vew_ll.setVisibility(View.VISIBLE);
            }
            SnackBarUtils.ShowRed(getActivity(), getString(R.string.dd43) + "");
        }
        if (refresh_layout!=null){
            getViewModel().getBbFinanceData(TransferAccount.SPOT.getValue());
        }
    }
    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        SPUtils.saveLoginToken("");
    }

    @Override
    public void hideProgress() {
        if (getActivity() != null) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}