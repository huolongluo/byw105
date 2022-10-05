package huolongluo.byw.byw.ui.fragment.bdb;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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

import com.android.coinw.biz.event.BizEvent;
import com.android.legend.ui.transfer.AccountTransferRecordActivity;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFinanceFragment;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.fragment.bdb.adapter.BdbBalanceAdapter;
import huolongluo.byw.byw.ui.fragment.bdb.bean.BdbFinancialAgreementStatus;
import huolongluo.byw.byw.ui.fragment.bdb.bean.BdbFinancialDetail;
import huolongluo.byw.byw.ui.fragment.bdb.bean.BdbFinancialInfo;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.AgreementUtils;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.EncryptUtils;
/**
 * 币贷宝资产界面
 */
public class BdbFinanceFragment extends BaseFinanceFragment {
    public static final String CACHE_KEY = "getBdbUserWallet";
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
    private String isOpen = "0";
    private String isClose = "0";
    public static int select = -1;//搜索后选中的那一项的id。根据id来定位coin的位置
    private List<BdbFinancialDetail> mAssetcoinsList = new ArrayList<>();//所有币种数据
    private List<BdbFinancialDetail> mAssetcoinsList1 = new ArrayList<>();//隐藏零余额币种后的数据
    private List<BdbFinancialDetail> mSearchList = new ArrayList<>();
    private BdbBalanceAdapter userAssetsAdapter;
    private BdbFinancialInfo bdbFinancialInfo;
    private boolean isVisibleToUser;

    public static BdbFinanceFragment getInstance() {
        Bundle args = new Bundle();
        BdbFinanceFragment fragment = new BdbFinanceFragment();
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
        EventBus.getDefault().register(this);
        llHideCoin.setVisibility(View.GONE);
        error_vew_ll.setVisibility(View.GONE);
        error_vew_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  refresh_layout.setRefreshing(true);
                getUserWallet();
            }
        });
        btn_reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  refresh_layout.setRefreshing(true);
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
                if (!s.toString().isEmpty()) {
                    search(s.toString());
                } else {
                    userAssetsAdapter.replaceAll(mAssetcoinsList);
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
                    if (bdbFinancialInfo != null) {
                        refreshTotalAsset(bdbFinancialInfo.getCnytPrice());
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
        eventClick(tvOperate1).subscribe(o -> {
            AccountTransferRecordActivity.Companion.launch(getActivity());
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        rv_assets.setNestedScrollingEnabled(false);
        rv_assets.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        userAssetsAdapter = new BdbBalanceAdapter(getActivity(), mAssetcoinsList);
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
        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString(CACHE_KEY);
        if (!TextUtils.isEmpty(result)) {
            try {
                bdbFinancialInfo = GsonUtil.json2Obj(result, BdbFinancialInfo.class);
                if (bdbFinancialInfo != null) {
                    refreshTotalAsset(bdbFinancialInfo.getCnytPrice());
                    mAssetcoinsList = bdbFinancialInfo.getDetail();
                    userAssetsAdapter.replaceAll(mAssetcoinsList);
                    mAssetcoinsList1.clear();
                    for (int i = 0; i < mAssetcoinsList.size(); i++) {
                        if (Double.parseDouble(mAssetcoinsList.get(i).getAvailableVol()) != 0) {
                            mAssetcoinsList1.add(mAssetcoinsList.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //控制切换至当前界面时的业务操作
        this.isVisibleToUser=isVisibleToUser;
        getData();
        if (isVisibleToUser) {
            checkAgreementIsOpen();
        } else {

        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void checkAgreementIsOpen() {
        if(AgreementUtils.isBdbOpen()){
        }else{
            Map<String, Object> params = new HashMap<>();
            params.put("type", 1);

            Type type = new TypeToken<SingleResult<String>>() {
            }.getType();
            OKHttpHelper.getInstance().get(UrlConstants.GET_BDB_AGREEMENT_STATUS+"?"+EncryptUtils.encryptStr(params)+"&loginToken=" + UserInfoManager.getToken(),
                    checkAgreementCallback, type);
        }
    }

    private void openAgreement() {
        DialogManager.INSTANCE.showProgressDialog(getActivity());
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);
        params=EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());

        Type type = new TypeToken<SingleResult<String>>() {
        }.getType();
        OKHttpHelper.getInstance().post(UrlConstants.OPEN_BDB_AGREEMENT, params,openAgreementCallback, type);
    }

    //获取资产
    private void getUserWallet() {
        if (refresh_layout!=null&&!refresh_layout.isRefreshing()) {
            if (getActivity() != null) {
                if (!getActivity().isFinishing()) {
                    DialogManager.INSTANCE.showProgressDialog(getActivity(), getString(R.string.qa67));
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
            SnackBarUtils.ShowRed(getActivity(), getString(R.string.qa44) + "");
        }

        DialogManager.INSTANCE.showProgressDialog(getActivity());
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);

        Type type = new TypeToken<SingleResult<String>>() {
        }.getType();
        OKHttpHelper.getInstance().get(UrlConstants.GET_BDB_BALANCE+"?"+EncryptUtils.encryptStr(params)+"&loginToken=" + UserInfoManager.getToken(),
                getBalanceCallback, type);
    }

    void search(String text) {
        mSearchList.clear();
        for (BdbFinancialDetail bean : mAssetcoinsList) {
            if (bean.getCoinName().toLowerCase().contains(text.toLowerCase())) {
                mSearchList.add(bean);
            }
        }
        userAssetsAdapter.replaceAll(mSearchList);
        if (mSearchList.size() == 0) {
            no_data_view.setVisibility(View.VISIBLE);
        } else {
            no_data_view.setVisibility(View.GONE);
        }
    }

    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        SPUtils.saveLoginToken("");
    }
    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openAgreement(Event.BdbOpenAgreement event) {
        showAgreementDialog();
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
            getUserWallet();//获取数据
        }
    }
    private void showAgreementDialog(){
        DialogUtils.getInstance().showTwoButtonDialog(getActivity(),
                getResources().getString(R.string.bdb_agreement_title), getResources().getString(R.string.bdb_agreement_content),
                getResources().getString(R.string.bdb_agreement_cancel), getResources().getString(R.string.bdb_agreement_open),
                new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                        openAgreement();
                    }
                });
    }

    private INetCallback<SingleResult<String>> getBalanceCallback = new INetCallback<SingleResult<String>>() {
        @Override
        public void onSuccess(SingleResult<String> response) throws Throwable {
            DialogManager.INSTANCE.dismiss();
            if (!isAdded()) {
                return;
            }
            error_vew_ll.setVisibility(View.GONE);
            if (refresh_layout != null && refresh_layout.isRefreshing()) {
                SnackBarUtils.ShowBlue(getActivity(), getString(R.string.qa46));
                refresh_layout.setRefreshing(false);
            }
            Logger.getInstance().debug(TAG, "kline result!", new Exception());
            if (response == null || response.data == null||!response.code.equals("200")) {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            try {
                Type type = new TypeToken<SingleResult<BdbFinancialInfo>>() {
                }.getType();
                SingleResult<BdbFinancialInfo> result=GsonUtil.json2Obj(response.data,type);
                if (result.code.equals("0")) {
                    bdbFinancialInfo=result.data;
                    if (isClose.equals("1")) {
                        tv_totalasset.setText("********");
                        tvUnit.setText("***");
                        refreshFinanceEyeUi(false);
                    } else {
                        if (bdbFinancialInfo != null) {
                            refreshTotalAsset(bdbFinancialInfo.getCnytPrice());
                            refreshFinanceEyeUi(true);
                        }
                    }
                    mAssetcoinsList = bdbFinancialInfo.getDetail();
                    mAssetcoinsList1.clear();
                    if (isOpen.equals("0")) {
                        userAssetsAdapter.replaceAll(mAssetcoinsList);
                        for (int i = 0; i < mAssetcoinsList.size(); i++) {
                            if (Double.parseDouble(mAssetcoinsList.get(i).getAvailableVol()) != 0) {
                                mAssetcoinsList1.add(mAssetcoinsList.get(i));
                            }
                        }
                    } else {
                        for (int i = 0; i < mAssetcoinsList.size(); i++) {
                            if (Double.parseDouble(mAssetcoinsList.get(i).getAvailableVol()) != 0) {
                                mAssetcoinsList1.add(mAssetcoinsList.get(i));
                            }
                        }
                        userAssetsAdapter.replaceAll(mAssetcoinsList1);
                    }
                    CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put(CACHE_KEY,
                            GsonUtil.obj2Json(result.data,BdbFinancialInfo.class));
                } else {
                    if (!userAssetsAdapter.hasData()) {
                        error_vew_ll.setVisibility(View.VISIBLE);
                    } else {
                        error_vew_ll.setVisibility(View.GONE);
                    }
                    SnackBarUtils.ShowRed(getActivity(), getString(R.string.qa47));
                }
            } catch (Exception e) {
                if (!userAssetsAdapter.hasData()) {
                    error_vew_ll.setVisibility(View.VISIBLE);
                } else {
                    error_vew_ll.setVisibility(View.GONE);
                }
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.qa48));
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            DialogManager.INSTANCE.dismiss();
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
            // dismissPro();
            SnackBarUtils.ShowRed(getActivity(), getString(R.string.qa45) + "");
            e.printStackTrace();
        }
    };

    private INetCallback<SingleResult<String>> checkAgreementCallback = new INetCallback<SingleResult<String>>() {
        @Override
        public void onSuccess(SingleResult<String> response) throws Throwable {
            Logger.getInstance().debug(TAG, "kline result!", new Exception());
            if (response == null || response.data == null||!response.code.equals("200")) {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            Type type = new TypeToken<SingleResult<BdbFinancialAgreementStatus>>() {
            }.getType();
            SingleResult<BdbFinancialAgreementStatus> result=GsonUtil.json2Obj(response.data,type);
            if (result.code.equals("0")) {
                if (result.data.getStatus() == 1) {//已开通
                    AgreementUtils.saveBdbOpen(getContext());
                } else {//未开通
                    showAgreementDialog();
                }
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
            //TODO 处理异常情况
        }
    };
    private INetCallback<SingleResult<String>> openAgreementCallback = new INetCallback<SingleResult<String>>() {
        @Override
        public void onSuccess(SingleResult<String> response) throws Throwable {
            DialogManager.INSTANCE.dismiss();
            Logger.getInstance().debug(TAG, "kline result!", new Exception());
            if (response == null || response.data == null||!response.code.equals("200")) {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            Type type = new TypeToken<SingleResult<Object>>() {
            }.getType();
            SingleResult<Object> result=GsonUtil.json2Obj(response.data,type);
            if (result.code.equals("0")) {
                AgreementUtils.saveBdbOpen(getContext());
                EventBus.getDefault().post(new BizEvent.Bdb.OpenAgreementSuccess());
                getUserWallet();
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            DialogManager.INSTANCE.dismiss();
            Logger.getInstance().debug(TAG, "error", e);
            //TODO 处理异常情况
        }
    };
}