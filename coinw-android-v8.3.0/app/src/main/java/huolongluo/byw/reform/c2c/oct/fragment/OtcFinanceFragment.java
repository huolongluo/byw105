package huolongluo.byw.reform.c2c.oct.fragment;

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

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.legend.ui.login.LoginActivity;
import com.android.legend.ui.transfer.AccountTransferRecordActivity;
import com.android.tu.loadingdialog.LoadingDailog;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFinanceFragment;
import huolongluo.byw.byw.bean.entity.ConvertBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.cointixian.CoinTXNewActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.byw.ui.adapter.NewUserOtcAssetsAdapter;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.UserWalletbn;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.mine.activity.RechargeActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;

/**
 * <p>
 * Created by 火龙裸 on 2017/9/5 0005.
 * 资产界面
 */
public class OtcFinanceFragment extends BaseFinanceFragment {
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
    private List<AssetCoinsBean> mAssetcoinsList = new ArrayList<>();
    private List<AssetCoinsBean> mAssetcoinsList1 = new ArrayList<>();
    private List<AssetCoinsBean> mSearchList = new ArrayList<>();
    private NewUserOtcAssetsAdapter userAssetsAdapter;
    private UserWalletbn userWalletbn;
    private boolean isVisibleToUser;

    public static OtcFinanceFragment getInstance() {
        Bundle args = new Bundle();
        OtcFinanceFragment fragment = new OtcFinanceFragment();
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
        tvHideCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.question_des), getString(R.string.iknow1));
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
                    if (userWalletbn != null) {
                        refreshTotalAsset(userWalletbn.totalAsset+"");
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
        userAssetsAdapter = new NewUserOtcAssetsAdapter(getActivity(), mAssetcoinsList);
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
        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("getotcUserWallet");
        if (!TextUtils.isEmpty(result)) {
            try {
                userWalletbn = GsonUtil.json2Obj(result, UserWalletbn.class);
                if (userWalletbn != null && userWalletbn.getCode() == 0) {
                    refreshTotalAsset(userWalletbn.totalAsset+"");
                    mAssetcoinsList = userWalletbn.getUserWallet();
                    userAssetsAdapter.replaceAll(mAssetcoinsList);
                    mAssetcoinsList1.clear();
                    for (int i = 0; i < mAssetcoinsList.size(); i++) {
                        if (Double.parseDouble(mAssetcoinsList.get(i).getFrozen()) + Double.parseDouble(mAssetcoinsList.get(i).getTotal()) >= 1) {
                            mAssetcoinsList1.add(mAssetcoinsList.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void search(String text) {
        mSearchList.clear();
        for (AssetCoinsBean bean : mAssetcoinsList) {
            if (bean.getShortName().toLowerCase().contains(text.toLowerCase())) {
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

    private LoadingDailog mDialog;

    //获取资产
    private void getUserWallet() {
        HashMap<String, String> params = new HashMap<>();
        if (refresh_layout!=null&&!refresh_layout.isRefreshing()) {
            //  showPro();
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
            // dismissPro();
            SnackBarUtils.ShowRed(getActivity(), getString(R.string.qa44) + "");
        }
        params.put("loginToken", UserInfoManager.getToken());//4427660C8125F62B658FFAE9F6C376F2_1536627572130_67347
        OkhttpManager.postAsync(UrlConstants.getOtcUserWallet + "?loginToken=" + UserInfoManager.getToken(), params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                if (isDetached() || !isAdded()) {
                    return;
                }
                if (refresh_layout != null && refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);
                }
                DialogManager.INSTANCE.dismiss();
                if (userAssetsAdapter.getItemCount() > 0) {
                    error_vew_ll.setVisibility(View.GONE);
                } else {
                    error_vew_ll.setVisibility(View.VISIBLE);
                }
                // dismissPro();
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.qa45) + "");
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                if (!isAdded()) {
                    return;
                }
                Log.i("getUserWallet", "getUserWallet==   " + result);
                error_vew_ll.setVisibility(View.GONE);
                //  showLoginDialog(result);
                if (refresh_layout != null && refresh_layout.isRefreshing()) {
                    // showMessage("已刷新至最新数据", 1);
                    SnackBarUtils.ShowBlue(getActivity(), getString(R.string.qa46));
                    refresh_layout.setRefreshing(false);
                }
                DialogManager.INSTANCE.dismiss();
                int LENGTH = 400;
                // dismissPro();
                try {
                    userWalletbn = GsonUtil.json2Obj(result, UserWalletbn.class);
                    if (userWalletbn.getCode() == 0) {
                        if (isClose.equals("1")) {
                            tv_totalasset.setText("********");
                            tvUnit.setText("***");
                            refreshFinanceEyeUi(false);
                        } else {
                            if (userWalletbn != null) {
                                refreshTotalAsset(userWalletbn.totalAsset+"");
                                refreshFinanceEyeUi(true);
                            }
                        }
                        mAssetcoinsList = userWalletbn.getUserWallet();
                        mAssetcoinsList1.clear();
                        if (isOpen.equals("0")) {
                            userAssetsAdapter.replaceAll(mAssetcoinsList);
                            for (int i = 0; i < mAssetcoinsList.size(); i++) {
                                if (Double.parseDouble(mAssetcoinsList.get(i).getFrozen()) + Double.parseDouble(mAssetcoinsList.get(i).getTotal()) != 0) {
                                    mAssetcoinsList1.add(mAssetcoinsList.get(i));
                                }
                            }
                        } else {
                            for (int i = 0; i < mAssetcoinsList.size(); i++) {
                                if (Double.parseDouble(mAssetcoinsList.get(i).getFrozen()) + Double.parseDouble(mAssetcoinsList.get(i).getTotal()) != 0) {
                                    mAssetcoinsList1.add(mAssetcoinsList.get(i));
                                }
                            }
                            userAssetsAdapter.replaceAll(mAssetcoinsList1);
                        }
                        CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("getotcUserWallet", result);
                    } else {
                        if(CoinwHyUtils.isServiceStop){
                            return;
                        }
                        if (!userAssetsAdapter.hasData()) {
                            error_vew_ll.setVisibility(View.VISIBLE);
                        } else {
                            error_vew_ll.setVisibility(View.GONE);
                        }
                        //  Toast.makeText(getActivity(), "服务器返回失败，请稍后重试", Toast.LENGTH_LONG).show();
                        SnackBarUtils.ShowRed(getActivity(), getString(R.string.qa47));
                    }
                } catch (Exception e) {
                    if (!userAssetsAdapter.hasData()) {
                        error_vew_ll.setVisibility(View.VISIBLE);
                    } else {
                        error_vew_ll.setVisibility(View.GONE);
                    }
                    // Toast.makeText(getActivity(), "服务器异常，请稍后重试", Toast.LENGTH_LONG).show();
                    SnackBarUtils.ShowRed(getActivity(), getString(R.string.qa48));
                    // showLoginDialog(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 用户点击 充值 或 提现
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickAssets(Event.clickAssets clickAssets) {
        if (true) {
            return;
        }
        if (clickAssets.clickType == -1) {
            getActivity().finish();
            Intent intent = new Intent("jerry");
            intent.putExtra("change", "yes");
            MainActivity.tag = 5;
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            return;
        } else if (clickAssets.clickType == 1) // 点击充值
        {
            //if (clickAssets.position != 0 || !clickAssets.coinName.equalsIgnoreCase("CNYT")) {
            if (userAssetsAdapter.getItem(clickAssets.position) != null && userAssetsAdapter.getItem(clickAssets.position).isIsRecharge()) {
//                    if (userInfoBean.isHasC2Validate()){
//                        Intent intent = new Intent("jerry");
//                        intent.putExtra("change", "yes");
//                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                    Bundle bundle = new Bundle();
                    AssetCoinsBean coinBean;
                    if (isOpen.equals("0")) {
                        coinBean = userAssetsAdapter.getItem(clickAssets.position);
                    } else {
                        coinBean = userAssetsAdapter.getItem(clickAssets.position);
                    }
                    bundle.putString("cnName", coinBean.getCnName());
                    bundle.putString("shortName", coinBean.getShortName());
                    bundle.putInt("coinId", coinBean.getId()); // 获取二维码图片用的coinId
                    bundle.putString("address", coinBean.getCnName());
                    bundle.putBoolean("iseos", coinBean.isIseos());
                    bundle.putString("logo", coinBean.getLogo());
                    bundle.putString("mainNetworkSpecification", coinBean.getMainNetworkSpecification());
                    Intent intent = new Intent(getActivity(), RechargeActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    // showMessage("请先设置交易密码", 2);
                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.qa49), getString(R.string.qa50), getString(R.string.qa51));
                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                        @Override
                        public void onLiftClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onRightClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (TextUtils.isEmpty(SPUtils.getLoginToken())) {
//                startActivity(LoOrReActivity.class);
                                startActivity(LoginActivity.class);
                            } else {
                                startActivity(SafeCentreActivity.class);
                            }
                        }
                    });
                }
            } else if (mSearchList.size() > 0) {
                /**
                 * 添加了mSearchList.size() > 0判断，当用户输入搜索内容时，搜索的内容在mSearchList中
                 * sloop 2019.1.15
                 */
                if (mSearchList.get(clickAssets.position).isIsRecharge()) {
//                    if (userInfoBean.isHasC2Validate()){
//                        Intent intent = new Intent("jerry");
//                        intent.putExtra("change", "yes");
//                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                        Bundle bundle = new Bundle();
                        AssetCoinsBean coinBean;
                        if (isOpen.equals("0")) {
                            coinBean = mSearchList.get(clickAssets.position);
                        } else {
                            coinBean = mSearchList.get(clickAssets.position);
                        }
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("cnName", coinBean.getCnName());
                        bundle1.putString("shortName", coinBean.getShortName());
                        bundle1.putInt("coinId", coinBean.getId()); // 获取二维码图片用的coinId
                        bundle1.putString("address", coinBean.getCnName());
                        bundle1.putBoolean("iseos", coinBean.isIseos());
                        bundle1.putString("logo", coinBean.getLogo());
                        bundle1.putString("mainNetworkSpecification", coinBean.getMainNetworkSpecification());
                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                        intent.putExtras(bundle1);
                        startActivity(intent);
                    } else {
                        // showMessage("请先设置交易密码", 2);
                        DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.qa53), getString(R.string.qa54), getString(R.string.qa52));
                        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                            @Override
                            public void onLiftClick(AlertDialog dialog, View view) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onRightClick(AlertDialog dialog, View view) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                if (TextUtils.isEmpty(SPUtils.getLoginToken())) {
//                startActivity(LoOrReActivity.class);
                                    startActivity(LoginActivity.class);
                                } else {
                                    startActivity(SafeCentreActivity.class);
                                }
                            }
                        });
                    }
                }
            } else {
                //showMessage("充值暂未开放", 1);
                DialogUtils.getInstance().showImageDialogStopCharge(getActivity(), getString(R.string.qa55), R.mipmap.chongzhi);
            }
           /* } else {


                getActivity().finish();


//                startActivity(RMBRechargeActivity.class);
                Intent intent = new Intent("jerry");
                intent.putExtra("change", "yes");
                CameraMainActivity.tag = 5;
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }*/
        } else if (clickAssets.clickType == 2) // 点击提现
        {
            // if (clickAssets.position != 0 || !clickAssets.coinName.equalsIgnoreCase("CNYT")) {
            if (userAssetsAdapter.getItem(clickAssets.position) != null && userAssetsAdapter.getItem(clickAssets.position).isIsWithDraw()) {
                //提现之前判断是否已经设置交易密码，没有设置交易密码的话，提示请先设置交易密码
                if (UserInfoManager.getUserInfo().isHasC2Validate()) {
                    //if (safeCentreBean != null) {
                    if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                        //
                        //JIRA:COIN-1721
                        //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
                        if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                            DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.no_withdrawal), getString(R.string.as16));
                            return;
                        }
                        // 传递可用，冻结金额。币种ID。短名称
                        Bundle bundle = new Bundle();
                        AssetCoinsBean coinBean;
                        if (isOpen.equals("0")) {
                            coinBean = userAssetsAdapter.getItem(clickAssets.position);
                        } else {
                            coinBean = userAssetsAdapter.getItem(clickAssets.position);
                        }
                        bundle.putParcelable("assetBean", coinBean);
                        startActivity(CoinTXNewActivity.class, bundle);
                    } else {
                        //..  showMessage("请先设置交易密码", 2);
                        DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.qq6), getString(R.string.qa68), getString(R.string.qa56));
                        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                            @Override
                            public void onLiftClick(AlertDialog dialog, View view) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onRightClick(AlertDialog dialog, View view) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                if (TextUtils.isEmpty(SPUtils.getLoginToken())) {
//                startActivity(LoOrReActivity.class);
                                    startActivity(LoginActivity.class);
                                } else {
                                    startActivity(SafeCentreActivity.class);
                                }
                            }
                        });
                    }
                } else if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                    showMessage(getString(R.string.qa57), 2);
                } else {
                    // showMessage("请先进行实名认证", 2);
                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.qq3), getString(R.string.qa69), getString(R.string.qa58));
                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                        @Override
                        public void onLiftClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onRightClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            startActivity(RenZhengBeforeActivity.class);
                        }
                    });
                }
            } else if (mSearchList.size() > 0) {
                /**
                 * 添加了mSearchList.size() > 0判断，当用户输入搜索内容时，搜索的内容在mSearchList中
                 * sloop 2019.1.15
                 */
                if (userAssetsAdapter.getItem(clickAssets.position).isIsWithDraw()) {
                    //提现之前判断是否已经设置交易密码，没有设置交易密码的话，提示请先设置交易密码
                    if (UserInfoManager.getUserInfo().isHasC2Validate()) {
                        //if (safeCentreBean != null) {
                        if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                            //
                            //JIRA:COIN-1721
                            //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
                            if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                                DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.no_withdrawal), getString(R.string.as16));
                                return;
                            }
                            // 传递可用，冻结金额。币种ID。短名称
                            Bundle bundle = new Bundle();
                            AssetCoinsBean coinBean;
                            if (isOpen.equals("0")) {
                                coinBean = userAssetsAdapter.getItem(clickAssets.position);
                            } else {
                                coinBean = userAssetsAdapter.getItem(clickAssets.position);
                            }
                            bundle.putParcelable("assetBean", coinBean);
                            startActivity(CoinTXNewActivity.class, bundle);
                        } else {
                            //..  showMessage("请先设置交易密码", 2);
                            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.qq4), getString(R.string.qq1), getString(R.string.qa59));
                            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                                @Override
                                public void onLiftClick(AlertDialog dialog, View view) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onRightClick(AlertDialog dialog, View view) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                    if (TextUtils.isEmpty(SPUtils.getLoginToken())) {
//                startActivity(LoOrReActivity.class);
                                        startActivity(LoginActivity.class);
                                    } else {
                                        startActivity(SafeCentreActivity.class);
                                    }
                                }
                            });
                        }
                    } else if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                        showMessage(getString(R.string.qa60), 2);
                    } else {
                        // showMessage("请先进行实名认证", 2);
                        DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.qq5), getString(R.string.qq2), getString(R.string.qa61));
                        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                            @Override
                            public void onLiftClick(AlertDialog dialog, View view) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onRightClick(AlertDialog dialog, View view) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                startActivity(RenZhengBeforeActivity.class);
                            }
                        });
                    }
                }
            } else {
                // showMessage("该币暂时停止提现", 1);
                DialogUtils.getInstance().showImageDialog(getActivity(), getString(R.string.qa62), R.mipmap.tixian);
            }
           /* } else {
                if (userInfoBean.isHasC2Validate()) {
//                    Bundle bundle = new Bundle();
//                    bundle.putDouble("frozen", coinsBeanList.get(clickAssets.position).getFrozen());
//                    bundle.putDouble("total", coinsBeanList.get(clickAssets.position).getTotal());
//                    startActivity(RmbTiXianActivity.class);
                    Intent intent = new Intent("jerry");
                    intent.putExtra("change", "yes");
                    CameraMainActivity.tag = 6;
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                } else if (userInfoBean.isPostC2Validate()) {
                    showMessage("认证正在审核中，请等待审核通过", 2);
                } else {
                    // showMessage("请先进行实名认证", 2);

                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), "请先进行实名认证", "取消", "认证");
                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                        @Override
                        public void onLiftClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onRightClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            startActivity(RenZhengBeforeActivity.class);

                        }
                    });

                }
            }*/
        }
    }

    /**
     * 兑换买币按钮 回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exchangeClick(Event.exchangeClick exchangeClick) {
        HashMap<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params.put("coinId", String.valueOf(exchangeClick.getId()));
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.CONVERT, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                if (isDetached()) {
                    return;
                }
                DialogManager.INSTANCE.dismiss();
                MToast.show(getActivity(), getString(R.string.qa64), 1);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    ConvertBean rechargeBean = com.alibaba.fastjson.JSONObject.parseObject(result, ConvertBean.class);
//                    if (rechargeBean.getCode() == 0) {
                    MToast.show(getActivity(), rechargeBean.getValue(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
            // DialogManager.INSTANCE.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
            getUserWallet();//获取数据
        }
    }
}