package huolongluo.byw.byw.ui.fragment.contractTab;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.ui.transfer.AccountTransferActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFinanceFragment;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.UserWalletbn;
import huolongluo.byw.heyue.HeYueUtil;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;
/**
 * <p>
 * Created by 火龙裸 on 2017/9/5 0005.
 * 资产界面
 */
public class ConTractFinanceFragment extends BaseFinanceFragment {
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
    @BindView(R.id.freeze)
    TextView freeze;
    private String isOpen = "0";
    private String isClose = "0";
    public static int select = -1;//搜索后选中的那一项的id。根据id来定位coin的位置
    private DecimalFormat df = new DecimalFormat("0.0000");
    private List<AssetCoinsBean> mAssetcoinsList = new ArrayList<>();
    private List<AssetCoinsBean> mAssetcoinsList1 = new ArrayList<>();
    private List<AssetCoinsBean> mSearchList = new ArrayList<>();
    private ContractAdapter userAssetsAdapter;
    private UserWalletbn userWalletbn;
    private List<ContractListEntity.DataBean.DetailBean> contractListInfo = new ArrayList<>();
    private List<ContractListEntity.DataBean.DetailBean> contractListInfo1 = new ArrayList<>();
    private List<ContractListEntity.DataBean.DetailBean> mSearchList1 = new ArrayList<>();
    private ContractListEntity.DataBean contractInfodata;
    private boolean isShowOpenInfo = true;
    public static final int NOT_START = 0, END = 2, IN_PROGRESS = 1;
    boolean NMStatue;//泥码跳转页面类型 true：历史记录 false ： 网页
    private NMInfoEntity nmInfoEntity;
    private boolean isVisibleToUser;

    public static ConTractFinanceFragment getInstance() {
        Bundle args = new Bundle();
        ConTractFinanceFragment fragment = new ConTractFinanceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_finance_contact;
    }

    @Override
    protected void initDagger() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).activityComponent().inject(this);
        }
    }

    @Override
    protected void initViewsAndEvents(View rootView) {
        EventBus.getDefault().register(this);
        llHideCoin.setVisibility(View.GONE);
        error_vew_ll.setVisibility(View.GONE);
        error_vew_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  refresh_layout.setRefreshing(true);
                getUserWallet();
                getContractList();
            }
        });
        btn_reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserWallet();
                getContractList();
            }
        });
        tvHideCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.question_des), getString(R.string.iknow1));
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
                if (!s.toString().isEmpty()) {
                    search(s.toString());
                } else {
                    userAssetsAdapter.replaceAll(contractListInfo);
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
                    userAssetsAdapter.replaceAll(contractListInfo);
                } else {
                    isOpen = "1";
                    userAssetsAdapter.replaceAll(contractListInfo1);
                }
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
                    if (contractInfodata != null) {
                        refreshTotalAsset(contractInfodata.getCnytPrice());
                        refreshFinanceEyeUi(true);
                    }
                    if (userAssetsAdapter != null) {
                        userAssetsAdapter.setHide(false);
                        userAssetsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        rv_assets.setNestedScrollingEnabled(false);
        rv_assets.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        userAssetsAdapter = new ContractAdapter(getActivity(), contractListInfo);
        rv_assets.setAdapter(userAssetsAdapter);
        // 设置下拉进度的主题颜色
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContractUserInfo();
                getContractList();
            }
        });
    }

    void search(String text) {
        mSearchList1.clear();
        for (ContractListEntity.DataBean.DetailBean bean : contractListInfo) {
            if (bean.getCoinName().toLowerCase().contains(text.toLowerCase())) {
                mSearchList1.add(bean);
            }
        }
        userAssetsAdapter.replaceAll(mSearchList1);
        if (mSearchList1.size() == 0) {
            no_data_view.setVisibility(View.VISIBLE);
        } else {
            no_data_view.setVisibility(View.GONE);
        }
    }

    //获取资产
    private void getUserWallet() {
        HashMap<String, String> params = new HashMap<>();
        if (refresh_layout!=null&&!refresh_layout.isRefreshing()) {
            //  showPro();
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
            // dismissPro();
            SnackBarUtils.ShowRed(getActivity(), getString(R.string.dd43) + "");
        }
        params.put("loginToken", UserInfoManager.getToken());//4427660C8125F62B658FFAE9F6C376F2_1536627572130_67347
        getContractList();
    }

    private void getContractList() {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.CONTRACT_FINANCIAL, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                if (refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);
                }
                DialogManager.INSTANCE.dismiss();
                Logger.getInstance().debug(TAG,"getContractList result:"+result);
                ContractListEntity contractListEntity = GsonUtil.json2Obj(result, ContractListEntity.class);
                if (null != contractListEntity && null != contractListEntity.getData()) {
                    contractInfodata = contractListEntity.getData();
                    refreshTotalAsset(contractInfodata.getCnytPrice());
                    contractListInfo = contractInfodata.getDetail();
                    userAssetsAdapter.replaceAll(contractListInfo);
                    contractListInfo1.clear();
                    for (int i = 0; i < contractListInfo.size(); i++) {
                        if (DoubleUtils.parseDouble(contractListInfo.get(i).getCashVol()) >= 1) {
                            contractListInfo1.add(contractListInfo.get(i));
                        }
                    }
                }
            }
        });
    }

    private void getMyContractAsset() {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.CONTRACT_ASSET, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("CONTRACT_ASSET", result);
//                ContractEntity orderCancelBean = GsonUtil.json2Obj(result, ContractEntity.class);
//                if (null != orderCancelBean && null != orderCancelBean.getData()) {
//                    List<ContractEntity.DataBean> data = orderCancelBean.getData();
//                }
            }
        });
    }

    public void showDialog() {
        if (null == nmInfoEntity) {
            return;
        }
        huolongluo.byw.byw.ui.fragment.contractTab.nima.DialogUtils.Companion.getInstance().whatNm(getActivity(), nmInfoEntity, () -> {
            String url=null;
            if (nmInfoEntity != null && nmInfoEntity.getData() != null) {
                url=nmInfoEntity.getData().getUrl();
            }
            AccountTransferActivity.Companion.launch(getActivity(), TransferAccount.WEALTH.getValue(),TransferAccount.CONTRACT.getValue(),null,url,
                    false,"USDT");
            return null;
        });
    }

    private void openContract() {
        HeYueUtil.getInstance().toLoginOrAgreementActivity();
    }

    private void getContractUserInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.GET_HY_USER, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("CONTRACT_TYPES", result);
                ContractUserInfoEntity contractUserInfoEntity = GsonUtil.json2Obj(result, ContractUserInfoEntity.class);
                if (null != contractUserInfoEntity && null != contractUserInfoEntity.getData()) {
                    ContractUserInfoEntity.DataBean data = contractUserInfoEntity.getData();
                    freeze.setVisibility(View.GONE);
                    if (data.getStatus() == 0) {//未开通
                        if (isShowOpenInfo) {
                            openContract();
                            isShowOpenInfo = false;
                        }
                    } else if (data.getStatus() == 1) {//已开通
                    } else if (data.getStatus() == 2) {//冻结
                        freeze.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        //  checkUserInfo();
    }

    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        //  Share.get().setLogintoken("");
        SPUtils.saveLoginToken("");
        // checkUserInfo();
    }

    @Override
    public void hideProgress() {
        if (getActivity() != null) {
            // DialogManager.INSTANCE.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //控制切换至当前界面时的业务操作
        this.isVisibleToUser=isVisibleToUser;
        getData();
        if (isVisibleToUser) {
            getContractUserInfo();
        } else {

        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}