package huolongluo.byw.byw.ui.fragment.maintab03;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFinanceFragment;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.address.AdressManageActivity;
import huolongluo.byw.byw.ui.activity.cointixian.CoinTXNewActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.byw.ui.adapter.NewUserAssetsAdapter;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.UserWalletbn;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.mine.activity.FinanceRecordActivity;
import huolongluo.byw.reform.mine.activity.RechargeActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;
/**
 * <p>
 * Created by 火龙裸 on 2017/9/5 0005.
 * 资产账户的资产界面
 */
public class FinanceFragment extends BaseFinanceFragment {
    @BindView(R.id.rv_assets)
    RecyclerView rv_assets;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;
    @BindView(R.id.tvFinanceEye)
    TextView tvFinanceEye;
    @BindView(R.id.ivEtf)
    ImageView ivEtf;
    @BindView(R.id.search_et)
    EditText search_et;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    @BindView(R.id.no_data_view)
    LinearLayout no_data_view;
    @BindView(R.id.error_vew_ll)
    LinearLayout error_vew_ll;
    //默认隐藏小额币种
    private String isOpen = "1";
    private String isClose = "0";
    public static int select = -1;//搜索后选中的那一项的id。根据id来定位coin的位置
    private DecimalFormat df = new DecimalFormat("0.0000");
    private List<AssetCoinsBean> mAssetcoinsList = new ArrayList<>();//所有币种列表
    private List<AssetCoinsBean> mAssetcoinsList1 = new ArrayList<>();//折合大于0的币种列表，用于隐藏小额币种
    private List<AssetCoinsBean> mSearchList = new ArrayList<>();
    private NewUserAssetsAdapter userAssetsAdapter;
    private UserWalletbn userWalletbn;
    private int positionTx;
    private boolean isVisibleToUser;

    public static FinanceFragment getInstance() {
        Bundle args = new Bundle();
        FinanceFragment fragment = new FinanceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_finance;
    }

    @Override
    protected void initDagger() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).activityComponent().inject(this);
        }
    }

    @Override
    protected void initViewsAndEvents(View rootView) {
        ivHideCoin.setSelected(true);
        EventBus.getDefault().register(this);
        ivEtf.setVisibility(View.VISIBLE);
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
                SPUtils.saveString(mContext,"isOpen",isOpen);
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
                        refreshTotalAsset(userWalletbn.getTotalAsset()+"");
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
        tvOperate1.setText(getString(R.string.address_man));
        setLeftDrawable(tvOperate1,R.mipmap.ic_address_white);
        eventClick(tvOperate1).subscribe(o -> {
            startActivity(AdressManageActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        tvOperate2.setVisibility(View.VISIBLE);
        tvOperate2.setText(getString(R.string.finance_record));
        setLeftDrawable(tvOperate2,R.mipmap.ic_record_white);
        eventClick(tvOperate2).subscribe(o -> {
            Intent intent = new Intent(getActivity(), FinanceRecordActivity.class);
            intent.putExtra("id", 0);
            startActivity(intent);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        rv_assets.setNestedScrollingEnabled(false);
        rv_assets.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        userAssetsAdapter = new NewUserAssetsAdapter(getActivity(), mAssetcoinsList);
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
        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("getUserWallet" + UserInfoManager.getUserInfo().getFid());
        Logger.getInstance().debug(TAG, "result: " + result);
        if (!TextUtils.isEmpty(result)) {
            try {
                userWalletbn = GsonUtil.json2Obj(result, UserWalletbn.class);
                if (userWalletbn != null && userWalletbn.getCode() == 0) {
                    refreshTotalAsset(userWalletbn.getTotalAsset()+"");
                    mAssetcoinsList = userWalletbn.getUserWallet();
                    userAssetsAdapter.replaceAll(mAssetcoinsList);
                    mAssetcoinsList1.clear();
                    for (int i = 0; i < mAssetcoinsList.size(); i++) {
                        if (DoubleUtils.parseDouble(mAssetcoinsList.get(i).getZhehe()) >= 1) {
                            mAssetcoinsList1.add(mAssetcoinsList.get(i));
                        }
                    }
                    //存储是否勾选隐藏小额币种来显示数据，默认是隐藏小额币种
                    String  isOpen1 = SPUtils.getString(mContext, "isOpen", "");
                    if (!isOpen1.isEmpty()) {
                        isOpen = isOpen1;
                    }
                    if (isOpen.equals("1")) {
                        ivHideCoin.setSelected(true);
                        userAssetsAdapter.replaceAll(mAssetcoinsList1);
                    } else {
                        ivHideCoin.setSelected(false);
                        userAssetsAdapter.replaceAll(mAssetcoinsList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            for (AssetCoinsBean bean : mAssetcoinsList) {
                if (bean.getShortName().toLowerCase().contains(text.toLowerCase())) {
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
        if(!UserInfoManager.isLogin()){
            return;
        }
        HashMap<String, String> params = new HashMap<>();
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
        params.put("loginToken", UserInfoManager.getToken());//4427660C8125F62B658FFAE9F6C376F2_1536627572130_67347
        OkhttpManager.postAsync(UrlConstants.GET_USER_WALLET , params, new OkhttpManager.DataCallBack() {
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
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.dd43) + "");
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                if (!isAdded()) {
                    return;
                }
                error_vew_ll.setVisibility(View.GONE);
                if (refresh_layout != null && refresh_layout.isRefreshing()) {
                    SnackBarUtils.ShowBlue(getActivity(), getString(R.string.dd45));
                    refresh_layout.setRefreshing(false);
                }
                DialogManager.INSTANCE.dismiss();
                try {
                    userWalletbn = GsonUtil.json2Obj(result, UserWalletbn.class);
                    if (userWalletbn.getCode() == 0) {
                        if (isClose.equals("1")) {
                            tv_totalasset.setText("********");
                            tvUnit.setText("***");
                            refreshFinanceEyeUi(false);
                        } else {
                            if (userWalletbn != null) {
                                refreshTotalAsset(userWalletbn.getTotalAsset()+"");
                                refreshFinanceEyeUi(true);
                            }
                        }
                        mAssetcoinsList = userWalletbn.getUserWallet();
                        mAssetcoinsList1.clear();
                        //存储是否勾选隐藏小额币种来显示数据，默认是隐藏小额币种
                        String  isOpen1 = SPUtils.getString(mContext, "isOpen", "");
                        if (!isOpen1.isEmpty()) {
                            isOpen = isOpen1;
                        }
                        if (isOpen.equals("0")) {
                            ivHideCoin.setSelected(false);
                            userAssetsAdapter.replaceAll(mAssetcoinsList);
                            for (int i = 0; i < mAssetcoinsList.size(); i++) {
                                if (DoubleUtils.parseDouble(mAssetcoinsList.get(i).getZhehe()) > 1) {
                                    mAssetcoinsList1.add(mAssetcoinsList.get(i));
                                }
                            }
                        } else {
                            for (int i = 0; i < mAssetcoinsList.size(); i++) {
                                if (DoubleUtils.parseDouble(mAssetcoinsList.get(i).getZhehe()) > 1) {
                                    mAssetcoinsList1.add(mAssetcoinsList.get(i));
                                }
                            }
                            ivHideCoin.setSelected(true);
                            userAssetsAdapter.replaceAll(mAssetcoinsList1);
                        }
                        CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("getUserWallet" + UserInfoManager.getUserInfo().getFid(), result);
                    } else {
                        if (!userAssetsAdapter.hasData()) {
                            error_vew_ll.setVisibility(View.VISIBLE);
                        } else {
                            error_vew_ll.setVisibility(View.GONE);
                        }
                        if (!(userWalletbn.getCode() == 401)){
                            SnackBarUtils.ShowRed(getActivity(), getString(R.string.dd46));
                        }
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
            }
        });
    }

    /**
     * 用户点击 充值 或 提现
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickAssets(Event.clickAssets clickAssets) {
        if (clickAssets.clickType == -1) {
            getActivity().finish();
            Intent intent = new Intent("jerry");
            intent.putExtra("change", "yes");
            MainActivity.tag = 5;
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            return;
        } else if (clickAssets.clickType == 1) // 点击充值
        {
            if (userAssetsAdapter.getItem(clickAssets.position) != null && userAssetsAdapter.getItem(clickAssets.position).isIsRecharge()) {
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
                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.dd66), getString(R.string.dd60), getString(R.string.dd48));
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
                    if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                        Bundle bundle = new Bundle();
                        AssetCoinsBean coinBean;
                        if (isOpen.equals("0")) {
                            coinBean = mSearchList.get(clickAssets.position);
                        } else {
                            coinBean = mSearchList.get(clickAssets.position);
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
                        DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.dd66), getString(R.string.dd60), getString(R.string.dd49));
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
                                    startActivity(LoginActivity.class);
                                } else {
                                    startActivity(SafeCentreActivity.class);
                                }
                            }
                        });
                    }
                } else {
                    DialogUtils.getInstance().showImageDialogStopCharge(getActivity(), getString(R.string.dd50), R.mipmap.chongzhi);
                }
            } else {
                DialogUtils.getInstance().showImageDialogStopCharge(getActivity(), getString(R.string.dd50), R.mipmap.chongzhi);
            }
        } else if (clickAssets.clickType == 2) // 点击提现
        {
            if (userAssetsAdapter.getItem(clickAssets.position) != null && userAssetsAdapter.getItem(clickAssets.position).isIsWithDraw()) {
                //提现之前判断是否已经设置交易密码，没有设置交易密码的话，提示请先设置交易密码
                    if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                        //JIRA:COIN-1721
                        //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
                        if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                            DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.no_withdrawal), getString(R.string.as16));
                            return;
                        }
                        positionTx = clickAssets.position;
                        if(BuildConfig.DEBUG||BuildConfig.ENV_DEV) {
                            gotoTxActivity(positionTx);
                        }else{
                            checkTx();
                        }
                    } else {
                        DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.dd66), getString(R.string.dd60), getString(R.string.dd51));
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
                if (userAssetsAdapter.getItem(clickAssets.position).isIsWithDraw()) {
                    //提现之前判断是否已经设置交易密码，没有设置交易密码的话，提示请先设置交易密码
                        if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                            //JIRA:COIN-1721
                            //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
                            if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                                DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.no_withdrawal), getString(R.string.as16));
                                return;
                            }
                            positionTx = clickAssets.position;
                            if(BuildConfig.DEBUG||BuildConfig.ENV_DEV) {
                                gotoTxActivity(positionTx);
                            }else{
                                checkTx();
                            }
                        } else {
                            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.dd70), getString(R.string.dd60), getString(R.string.dd54));
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
                                        startActivity(LoginActivity.class);
                                    } else {
                                        startActivity(SafeCentreActivity.class);
                                    }
                                }
                            });
                        }
                } else {
                    DialogUtils.getInstance().showImageDialog(getActivity(), getString(R.string.dd57), R.mipmap.tixian);
                }
            } else {
                DialogUtils.getInstance().showImageDialog(getActivity(), getString(R.string.dd57), R.mipmap.tixian);
            }
        }
    }
    //检查用户是否存在穿仓损失
    private void checkTx() {
        gotoTxActivity(positionTx);
    }

    private void gotoTxActivity(int position) {
        // 传递可用，冻结金额。币种ID。短名称
        Bundle bundle = new Bundle();
        AssetCoinsBean coinBean;
        if (isOpen.equals("0")) {
            coinBean = userAssetsAdapter.getItem(position);
        } else {
            coinBean = userAssetsAdapter.getItem(position);
        }
        bundle.putParcelable("assetBean", coinBean);
        //提现界面
        startActivity(CoinTXNewActivity.class, bundle);
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
            getUserWallet();
        }
    }

}