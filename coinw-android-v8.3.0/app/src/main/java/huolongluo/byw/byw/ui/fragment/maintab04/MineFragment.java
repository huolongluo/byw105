package huolongluo.byw.byw.ui.fragment.maintab04;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.ETFHepler;
import com.android.coinw.test.TestActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.legend.common.component.theme.ThemeManager;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.bean.VersionInfo;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.UpVersionActivity;
import huolongluo.byw.byw.ui.activity.address.AdressManageActivity;
import huolongluo.byw.byw.ui.activity.feedback.FeedBackActivity;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainViewModel;
import huolongluo.byw.byw.ui.activity.renzheng.AuthActivity;
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.byw.ui.dialog.AddDialog;
import huolongluo.byw.byw.ui.fragment.maintab05.ScanQRLoginActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.Config;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.c2c.oct.bean.TotalUserWalletBean;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.reform.mine.activity.BindHPayActivity;
import huolongluo.byw.reform.mine.activity.BindHPaySuccessActivity;
import huolongluo.byw.reform.mine.activity.ChangeLanguageActivity;
import huolongluo.byw.reform.mine.activity.GradeActivity;
import huolongluo.byw.reform.mine.activity.MoneyManagerActivity;
import huolongluo.byw.reform.mine.activity.PricingMethodActivity;
import huolongluo.byw.reform.mine.activity.PyramidSaleWebViewActivity;
import huolongluo.byw.reform.mine.activity.ReturnFeeMoneyActivity;
import huolongluo.byw.reform.mine.activity.TradeOrderListActivity;
import huolongluo.byw.reform.mine.bean.BindHpyBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.ApkUtils;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.L;
import huolongluo.byw.util.LogicLanguage;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.UpgradeUtils;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DataUtils;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;
/**
 * <p>
 * Created by 火龙裸 on 2017/9/5 0005.
 */
public class MineFragment extends BaseFragment{
    @BindView(R.id.rl_item01)
    RelativeLayout rl_item01;
    @BindView(R.id.rl_item02)
    RelativeLayout rl_item02;
    @BindView(R.id.rl_item08)
    RelativeLayout rl_item08;
    @BindView(R.id.rl_network)
    RelativeLayout rl_network;
    @BindView(R.id.ll_1)
    LinearLayout ll_1;
    @BindView(R.id.ll_2)
    LinearLayout ll_2;
    @BindView(R.id.v_language)
    View v_language;
    @BindView(R.id.v_pricing_method)
    View v_pricing_method;
    @BindView(R.id.v_network)
    View v_network;
    @BindView(R.id.tv_version_name)
    TextView tv_version_name; // 当前版本号
    @BindView(R.id.tv_logout)
    TextView tv_logout;
    @BindView(R.id.tvChangeSkin)
    TextView tvChangeSkin;
    @BindView(R.id.tvChangeUpAndDrop)
    TextView tvChangeUpAndDrop;
    @BindView(R.id.changeIp)
    TextView changeIp;
    @BindView(R.id.changeContractIp)
    TextView changeContractIp;
    @BindView(R.id.rl_yijian)
    RelativeLayout rl_yijian;
    @BindView(R.id.ll_nologin)
    RelativeLayout llNologin;
    @BindView(R.id.ll_login)
    LinearLayout llLogin;
    @BindView(R.id.tv_username)
    TextView tvUserName;
    @BindView(R.id.tv_udid)
    TextView tvUdid;
    @BindView(R.id.iv_OnorOff)
    ImageView iv_OnorOff;
    @BindView(R.id.rl_hpy)
    RelativeLayout rl_hpy;
    @BindView(R.id.address_manager_rl)
    RelativeLayout address_manager_rl;
    @BindView(R.id.tv_post)
    TextView tv_post;
    @BindView(R.id.iv_shenfen_right)
    ImageView iv_shenfen_right;
    @BindView(R.id.kefu)
    ImageView kefu;
    @BindView(R.id.nologin_tv)
    TextView nologin_tv;
    @BindView(R.id.my_finance_rl)
    RelativeLayout my_finance_rl;
    @BindView(R.id.rlt_my_balance_stop)
    RelativeLayout rlt_my_balance_stop;//停机可用合约显示我的资产
    @BindView(R.id.redEnvelope_rl)
    RelativeLayout redEnvelope_rl;
    @BindView(R.id.weidan_rl)
    RelativeLayout weidan_rl;
    @BindView(R.id.my_finance_iv)
    ImageView my_finance_iv;
    @BindView(R.id.head_view)
    LinearLayout head_view;
    @BindView(R.id.money_manager_rl)
    RelativeLayout money_manager_rl;
    // private ImageButton iv_qr_code;
    @BindView(R.id.lll)
    LinearLayout lll;
    @BindView(R.id.vip_text)
    TextView vip_text;
    @BindView(R.id.fee_fanxian_rl)
    RelativeLayout fee_fanxian_rl;
    @BindView(R.id.kefu_rl)
    RelativeLayout kefu_rl;
    //TODO 因合代码后发版内容变更，拆代码的风险很高，故采用加开关来处理
    //TODO 币贷宝待修改
    //@BindView(R.id.my_finance_ll)
    //LinearLayout my_finance_ll;//币币账号资产
    //@BindView(R.id.my_otcfinance_ll)
    //LinearLayout my_otcfinance_ll;//买币账号资产
    //@BindView(R.id.ll_contract)
    //LinearLayout ll_contract;//合约账号资产
    @BindView(R.id.totalMoney_tv)
    TextView totalMoney_tv;//总资产
    //    @BindView(R.id.bibiMoney_tv)
//    TextView bibiMoney_tv;//币币资产
//    @BindView(R.id.fabiMoney_tv)
//    TextView fabiMoney_tv;//买币资产
    @BindView(R.id.fanyong_manager_rl_1)
    RelativeLayout fanyong_manager_rl_1;
    @BindView(R.id.bindHpp_tv)
    TextView bindHpp_tv;
    @BindView(R.id.vip_ll)
    LinearLayout vip_ll;
    @BindView(R.id.otc_manager_rl)
    RelativeLayout otc_manager_rl;
    @BindView(R.id.language_rl)
    RelativeLayout language_rl;
    @BindView(R.id.rlt_pricing_method)
    RelativeLayout rltPricingMethod;
    @BindView(R.id.tv_pricing)
    TextView tvPricing;
    @BindView(R.id.rl_myvip)
    RelativeLayout rl_myvip;
    private VersionInfo versionInfo;
    private UserInfoBean userInfoBean;
    private String OnorOff = "2";//1是打开，2关闭
    @BindView(R.id.zican_ll)
    LinearLayout zican_ll;
    @BindView(R.id.eye)
    ImageView imgEye;
    @BindView(R.id.tv_test)
    TextView testTxt;
    //提醒弹窗
    private AlertDialog financeDialog;
    @BindView(R.id.tv_language)
    TextView tv_language;
    @BindView(R.id.scroll_view)
    ScrollView scroll_view;
    @BindView(R.id.nm_history_rl)
    RelativeLayout nm_history_rl;
    @BindView(R.id.ll_hbt)
    View hbtView;
    private TotalUserWalletBean lastUserWalletBean;
    private MainViewModel mainViewModel;

    //***************************************************************************************************************
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initDagger() {
//        ((BaseActivity) getActivity()).activityComponent().inject(this);
        EventBus.getDefault().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPricingMethod(BizEvent.ChangeExchangeRate event) {
        refreshTotalMoney();
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents(View rootView) {
        scroll_view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    EventBus.getDefault().post(new BizEvent.ShowRedEnvelope(true));
                    break;
                case MotionEvent.ACTION_UP:
                    EventBus.getDefault().post(new BizEvent.ShowRedEnvelope(false));
                    break;
            }
            return false;
        });
        vip_ll.setOnClickListener(new View.OnClickListener() {//我的等级
            @Override
            public void onClick(View v) {
                if(CoinwHyUtils.checkIsStopService(getActivity())){
                    return;
                }
                if (UserInfoManager.isLogin()) {
                    startActivity(GradeActivity.class);
                } else {
                    startActivity(LoginActivity.class);
                }
            }
        });
        nm_history_rl.setOnClickListener(new View.OnClickListener() {//尼玛记录
            @Override
            public void onClick(View v) {

            }
        });
        rl_myvip.setOnClickListener(new View.OnClickListener() {//我的等级
            @Override
            public void onClick(View v) {
                gotoVipInfo();
            }
        });
        language_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//语言切换
                startActivityForResult(new Intent(getActivity(), ChangeLanguageActivity.class), 101);
            }
        });
        hbtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHBTPartner();
            }
        });
        rltPricingMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//计价方式切换
                startActivityForResult(new Intent(getActivity(), PricingMethodActivity.class), 102);
            }
        });
        //推荐返佣
        fanyong_manager_rl_1.setOnClickListener(v -> {
            //TODO 先关闭，待后期上线
            Intent intent = new Intent(getActivity(), PyramidSaleWebViewActivity.class);
            intent.putExtra("url", UrlConstants.INVITE);
            intent.putExtra("token", UserInfoManager.getToken());
            intent.putExtra("title", getString(R.string.str_invitation_reward));
            getActivity().startActivity(intent);
        });
        money_manager_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MoneyManagerActivity.class);
            }
        });
        eventClick(kefu).subscribe(o -> {
            startActivity(WebViewActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        //个人资产
        eventClick(my_finance_rl).subscribe(o -> {
            if (UserInfoManager.isLogin()) {
//                gotoFinance(AllFinanceActivity.TYPE_ZC);
            } else {
                startActivity(LoginActivity.class);
            }
            //    startActivity(new Intent(getActivity(), RenZhengGetTokenActivity.class));
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        //停机可用合约我的资产未登录的点击
        eventClick(rlt_my_balance_stop).subscribe(o -> {
            if (!UserInfoManager.isLogin()) {
                startActivity(LoginActivity.class);
            }
            //    startActivity(new Intent(getActivity(), RenZhengGetTokenActivity.class));
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(zican_ll).subscribe(o -> {
            if(CoinwHyUtils.isServiceStop){
//                gotoFinance(AllFinanceActivity.TYPE_HY);
                return;
            }
//            gotoFinance(AllFinanceActivity.TYPE_ZC);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
//        eventClick(my_finance_ll).subscribe(o -> {
//
//          /*  if (UserInfoManager.isLogin()) {
//                startActivity(FinanceActivity.class);
//            } else {
//                startActivity(LoginActivity.class);
//            }*/
////            Intent intent = new Intent(getActivity(), AllFinanceActivity.class);
////            //   intent.putExtra("posotion",0);
////            startActivity(intent);
//            gotoFinance(AllFinanceActivity.TYPE_BB);
//        }, throwable -> {
//            Logger.getInstance().error(throwable);
//        });
//        //TODO 因合代码后发版内容变更，拆代码的风险很高，故采用加开关来处理
//        //TODO 币贷宝待修改
//        eventClick(my_otcfinance_ll).subscribe(o -> {//买币账号
//            gotoFinance(AllFinanceActivity.TYPE_FB);
//        }, throwable -> {
//            Logger.getInstance().error(throwable);
//        });
//        eventClick(ll_contract).subscribe(o -> {//合约账号
//            gotoFinance(AllFinanceActivity.TYPE_HY);
//        }, throwable -> {
//            Logger.getInstance().error(throwable);
//        });
        eventClick(lll).subscribe(o -> {
            if (UserInfoManager.isLogin()) {
                startActivity(ReturnFeeMoneyActivity.class);
            } else {
                startActivity(LoginActivity.class);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(otc_manager_rl).subscribe(o -> {
            //startActivity(DemoActivity.class);
            if (UserInfoManager.isLogin()) {
                // startActivity(DemoActivity.class);
            } else {
                //startActivity(LoginActivity.class);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(my_finance_iv).subscribe(o -> {
            if (UserInfoManager.isLogin()) {
//                startActivity(AllFinanceActivity.class);
//                toFinance(AllFinanceActivity.TYPE_FB);
            } else {
                startActivity(LoginActivity.class);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        //
        //地址管理
        eventClick(address_manager_rl).subscribe(o -> {
            if (UserInfoManager.isLogin()) {
                startActivity(AdressManageActivity.class);
            } else {
                startActivity(LoginActivity.class);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(rl_hpy).subscribe(o -> {
            if (bindHpyBean != null) {
                if (bindHpyBean.isHasHyperpayBind()) {
                    Intent intent = new Intent(getActivity(), BindHPaySuccessActivity.class);
                    intent.putExtra("appId", bindHpyBean.getAppId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), BindHPayActivity.class);
                    intent.putExtra("uniqueId", bindHpyBean.getUniqueId());
                    intent.putExtra("appId", bindHpyBean.getAppId());
                    startActivity(intent);
                }
            } else {
                if (UserInfoManager.isLogin()) {
                    bindHyperpay();
                    MToast.show(getActivity(), getString(R.string.err), 1);
                } else {
                    startActivity(LoginActivity.class);
                }
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(llNologin).subscribe(o -> {
            Bundle bundle = new Bundle();
            bundle.putString("fromClass", MineFragment.class.toString());
            startActivity(LoginActivity.class, bundle);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(weidan_rl).subscribe(o -> {
            if (UserInfoManager.isLogin()) {
                startActivity(TradeOrderListActivity.class);
            } else {
                startActivity(LoginActivity.class);
            }
            // uploading();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(redEnvelope_rl).subscribe(o -> {
            if(CoinwHyUtils.checkIsStopService(getActivity())){
                return;
            }
            if (UserInfoManager.isLogin()) {
                Intent intent = new Intent(getActivity(), NewsWebviewActivity.class);
                intent.putExtra("url", UrlConstants.MY_REDENVELOPE_URL);
                intent.putExtra("token", UserInfoManager.getToken());
                intent.putExtra("title", getResources().getString(R.string.red_envelope));
                getActivity().startActivity(intent);
            } else {
                startActivity(LoginActivity.class);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        kefu_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WebViewActivity.class);
            }
        });
        eventClick(iv_OnorOff).subscribe(o -> {
            whetherDeductible();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(rl_yijian).subscribe(o -> {
            if (UserInfoManager.isLogin()) {
                startActivity(FeedBackActivity.class);
            } else {
                startActivity(LoginActivity.class);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(rl_item01).subscribe(o -> // 安全中心
        {
            if (!UserInfoManager.isLogin()) {
//                startActivity(LoOrReActivity.class);
                startActivity(LoginActivity.class);
            } else {
                startActivity(SafeCentreActivity.class);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(rl_item02).subscribe(o -> // 身份认证
        {
            gotoIdentity();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(rl_item08).subscribe(o -> {
            getVersion();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(rl_network).subscribe(o -> {
            NetworkDetectActivity.launch(mContext);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_logout).subscribe(o -> {
            AddDialog dialog = new AddDialog();
            dialog.setDialog(AddDialog.EXIT_APP);
            dialog.setOnClick(new AddDialog.OnClick() {
                @Override
                public void onItemClick() {
                    if(mainViewModel!=null){
                        mainViewModel.logout();
                    }
                }
            });
            dialog.show(getActivity().getSupportFragmentManager(), getClass().getSimpleName());
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvChangeSkin).subscribe(o -> {

        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvChangeUpAndDrop).subscribe(o -> {
            ThemeManager.INSTANCE.changeTickerColorMode();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        if (ApkUtils.isApkInDebug(getActivity().getApplicationContext()) || BuildConfig.ENV_DEV) {
            changeIp.setVisibility(View.VISIBLE);
            changeIp.setOnClickListener(view -> {
                DialogUtils.getInstance().showChangeIp(getActivity());
            });
            changeContractIp.setVisibility(View.VISIBLE);
            changeContractIp.setOnClickListener(view -> {
                DialogUtils.getInstance().showChangeContractIp(getActivity());
            });
            testTxt.setVisibility(View.VISIBLE);
            testTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), TestActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            changeIp.setVisibility(View.GONE);
            changeContractIp.setVisibility(View.GONE);
            testTxt.setVisibility(View.GONE);
        }
        tv_version_name.setText("V" + BuildConfig.VERSION_NAME);
        checkUserInfo(); // 检查 用户 是否登录
        //  getUserInfo();
        if (UserInfoManager.isLogin()) {
            bindHyperpay();
        }
        imgEye.setOnClickListener(view -> {
            if (Constant.showMoney) {
                Constant.showMoney = false;
                imgEye.setImageDrawable(getResources().getDrawable(R.mipmap.eye2));
                totalMoney_tv.setText("** ");
//                bibiMoney_tv.setText("** CNYT");
//                fabiMoney_tv.setText("** CNYT");
            } else {
                Constant.showMoney = true;
                getTotalUserWallet();
                imgEye.setImageDrawable(getResources().getDrawable(R.mipmap.eye1));
            }
        });
        if (LogicLanguage.getLanguage(getContext()).contains("zh")) {
            tv_language.setText(R.string.dd91);
        } else if (LogicLanguage.getLanguage(getContext()).contains("en")) {
            tv_language.setText("English");
            rl_yijian.setVisibility(View.GONE);
        } else if (LogicLanguage.getLanguage(getContext()).contains("ko")) {
            tv_language.setText("한국어");
            rl_yijian.setVisibility(View.GONE);
        }
        tvPricing.setText(PricingMethodUtil.getPricingName());
        initData();
    }
    private void initData(){
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);
    }

    private BindHpyBean bindHpyBean;

    public void setDisableStopService(){
        if(ll_1==null){
            return;
        }
        ll_1.setVisibility(View.GONE);
        ll_2.setVisibility(View.GONE);
        language_rl.setVisibility(View.GONE);
        v_language.setVisibility(View.GONE);
        rltPricingMethod.setVisibility(View.GONE);
        v_pricing_method.setVisibility(View.GONE);
        rl_network.setVisibility(View.GONE);
        v_network.setVisibility(View.GONE);
        if (!UserInfoManager.isLogin()) {
            rlt_my_balance_stop.setVisibility(View.VISIBLE);
        }else{
            rlt_my_balance_stop.setVisibility(View.GONE);
        }

    }

    //获取总资产
    void getTotalUserWallet() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.getTotalUserWallet, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    TotalUserWalletBean userWalletBean = new Gson().fromJson(result, TotalUserWalletBean.class);
                    if (userWalletBean.getCode() == 0) {
                        updataTotalUserWallet(userWalletBean);
                    } else {
                        userWalletBean = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void bindHyperpay() {
        String imei = "012345678";
        try {
            imei = DeviceUtils.getImei(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params.put("imei", imei);
        OkhttpManager.postAsync(UrlConstants.bindHyperpay, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                Log.i("绑定", "= " + result);
                try {
                    // JSONObject jsonObject=new JSONObject(result);
                    bindHpyBean = new Gson().fromJson(result, BindHpyBean.class);
                    if (bindHpyBean.getCode() != 0) {
                        bindHpyBean = null;
                        return;
                    }
                    if (bindHpyBean != null && bindHpyBean.isHasHyperpayBind()) {
                        bindHpp_tv.setText(R.string.Bound);
                    } else {
                        bindHpp_tv.setText(R.string.unbind);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getUserEntrustCashBackCharges() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        if (TextUtils.isEmpty(UserInfoManager.getToken())) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        OkhttpManager.getAsync(UrlConstants.DOMAIN + UrlConstants.getUserEntrustCashBackCharges + "?loginToken=" + UserInfoManager.getToken() + "&" + OkhttpManager.encryptGet(params), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result);
                    //{"data":{"cnytAmount":"0","count":"0","open":"true"},"value":""}
                    com.alibaba.fastjson.JSONObject object = jsonObject.getJSONObject("data");
                    String menuOpen = object.getString("menuOpen");
                    if (TextUtils.equals(menuOpen, "true")) {
                        fee_fanxian_rl.setVisibility(View.VISIBLE);
                    } else {
                        fee_fanxian_rl.setVisibility(View.GONE);
                    }
                }catch (Exception e){

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    Log.e("扫码", "LoginName =  " + userInfoBean.getLoginName());
                    if (userInfoBean != null) {
                        String ret = bundle.getString(CodeUtils.RESULT_STRING);
                        Log.e("扫码", "retshaoma =  " + ret);
                        // showLoginDialog(ret);
                        if (!URLUtil.isValidUrl(ret) || TextUtils.isEmpty(ret)) {
                            MToast.show(getActivity(), getString(R.string.aa1), 1);
                            return;
                        } else if (!ret.contains("/user/appQrcode/login.html")) {
                            MToast.show(getActivity(), getString(R.string.aa1), 1);
                            return;
                        }
                        MToast.show(getActivity(), getString(R.string.aa3), 1);
                        Intent intent = new Intent(getActivity(), ScanQRLoginActivity.class);
                        intent.putExtra("loginUrl", ret);
                        intent.putExtra("loginName", userInfoBean.getLoginName());
                        startActivityForResult(intent, 100);
                    } else {
                        //   MToast.show(getActivity(),"网络异常，请稍后重试",1);
                        SnackBarUtils.ShowRed(getActivity(), getString(R.string.aa4));
                        getUserInfo();
                    }
                }
            }
        } else if (requestCode == 101 && resultCode == 3) {
            Log.e("重启", "重启2");
            if (TextUtils.equals(Constant.currentLanguage, "zh")) {
                //   mDataBinding.imageView.setImageResource(R.drawable.eng);
            } else {
                //  mDataBinding.imageView.setImageResource(R.drawable.ch);
            }
            //    CameraMainActivity.page=3;
            if (getActivity() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getActivity().recreate();
                }
            }
        } else if (requestCode == 102 && resultCode == getActivity().RESULT_OK) {
            tvPricing.setText(PricingMethodUtil.getPricingName());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showLoginDialog(String s) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        View view = View.inflate(getActivity(), R.layout.dialog_show, null);
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // farLogin(s,0);
                AppHelper.dismissDialog(dialog);
                MToast.show(getActivity(), getString(R.string.aa5), 1);
            }
        });
        tv_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  farLoginsecond(s);
                AppHelper.dismissDialog(dialog);
            }
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
        // farLogin(s,-1);
    }

    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        checkUserInfo();
        getUserInfo();
        refreshConfigList();
        checkHBTPartner();
    }

    private void refreshConfigList() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.getConfigList, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.DOMAIN + UrlConstants.getConfigList + " errorMsg: " + errorMsg);
                Logger.getInstance().error(e);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.DOMAIN + UrlConstants.getConfigList + " result: " + result);
                try {
                    Type type = new TypeToken<SingleResult<Config>>() {
                    }.getType();
                    SingleResult<Config> ftiResult = GsonUtil.json2Obj(result, type);
                    if (ftiResult != null && ftiResult.data != null) {
                        handleFinancalTimeInterval(ftiResult.data);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public void gotoVipInfo(){
        if (UserInfoManager.isLogin()) {
            startActivity(GradeActivity.class);
        } else {
            startActivity(LoginActivity.class);
        }
    }
    public void gotoIdentity() {
        if (!UserInfoManager.isLogin()) {
            startActivity(LoginActivity.class);
        } else {
            //解决已知BUG
            //java.lang.NullPointerException: Attempt to invoke virtual method 'boolean huolongluo.byw.byw.bean.UserInfoBean.isHasC2Validate()' on a null object reference
            if (userInfoBean == null) {
                MToast.show(getActivity(), getString(R.string.relogin), 1);
                return;
            }
            Intent intentx = new Intent();
            intentx.setComponent(new ComponentName(getActivity(), AuthActivity.class));
            getActivity().startActivity(intentx);
//                if (userInfoBean.isHasC2Validate() && userInfoBean.isHasC3Validate()) {
//                    Intent intent = new Intent(getActivity(), RenzhengActivity.class);
//                    intent.putExtra("cardId", "unknow");
//                    showMessage(getString(R.string.authenticated), 2);
//                } else if (userInfoBean.isHasC2Validate() && !userInfoBean.isHasC3Validate()) {
//                    if (userInfoBean.getNationality() == 0) {//外国人不能高级认证
//                        RenZhenDialog dialog = new RenZhenDialog(getActivity());
//                        dialog.initDialog("").show();
//                    } else {
//                        // startActivity(RenZhengBeforeActivity.class);
//                        //进入高级认证
//                        faceIdGetH5Token();//中国初级认证过的，直接进入高级认证
//                    }
//                } else {
//                    if (userInfoBean.isPostC2Validate()) {
//                        showMessage(getString(R.string.wait), 2);
//                    } else {
//                        // startActivity(RenZhengInfoActivity.class);
//                        startActivity(RenZhengBeforeActivity.class);
//                    }
//                }
        }
    }

    public void gotoFinance(int type) {
        if (!UserInfoManager.isLogin()) {
            startActivity(LoginActivity.class);
            return;
        }
        if (checkFinancalTime()) {
            try {
                showFinancalDialog(type);
            } catch (Throwable t) {
            }
        } else {
            toFinance(type);
        }
    }

    private void toFinance(int type) {
//        if (type == AllFinanceActivity.TYPE_FB) {
//            if (!DataUtils.isOpenHeader()) {
//                return;
//            }
//        }
//        Intent intent = new Intent(getActivity(), AllFinanceActivity.class);
//        intent.putExtra("posotion", type);
//        startActivity(intent);
    }

    //提示用户弹窗
    private void showFinancalDialog(int type) {
        financeDialog = new AlertDialog.Builder(getActivity()).create();
        View view = View.inflate(getActivity(), R.layout.finance_dialog, null);
        TextView know_tv = view.findViewById(R.id.know_tv);
        know_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                financeDialog.dismiss();
                toFinance(type);
            }
        });
        financeDialog.setCancelable(true);
        financeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        Window windows = financeDialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        financeDialog.setView(view);
        financeDialog.show();
    }

    /**
     * 检查是否需要显示弹窗
     * @return
     */
    private boolean checkFinancalTime() {
        //处理间隔时间，业务单位（天）
        int interval = SPUtils.getInt(getActivity(), "financal_time_interval", -1);
        long latestTime = SPUtils.getLong(getActivity(), "latest_financal_time", -1L);
        //说明系统内未第一次弹窗，则需要马上弹窗
        if (latestTime == -1L) {
            //更新最后一次弹窗时间
            SPUtils.putLong(getActivity(), "latest_financal_time", System.currentTimeMillis());
            return true;
        }
        if (interval > 0) {
            //判断是否大于了业务规则时间
            long timeMillis = System.currentTimeMillis();
            //说明业务规则时间大于1天
            long intervalMills = interval * 24 * 60 * 60 * 1000L;//获得间隔毫秒值
            if (timeMillis >= latestTime + intervalMills) {//超过业务规则时间，则需要弹窗
                //更新最后一次弹窗时间
                SPUtils.putLong(getActivity(), "latest_financal_time", System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    private void handleFinancalTimeInterval(Config fti) {
        if (fti == null) {
            return;
        }
        //TODO 实时更新
        SPUtils.saveInt(getActivity(), "financal_time_interval", fti.appFinancalTimeInterval);
        //TODO 控制OTC开关
        //TODO 控制多语言开关
    }

    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        // Share.get().setLogintoken("");
        SPUtils.saveLoginToken("");
        UserInfoManager.clearUser();
        checkUserInfo();
        CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("getUserWallet" + UserInfoManager.getUserInfo().getFid(), "");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {   // 不在最前端显示 相当于调用了onPause();
            L.e("=============== 不在最前端显示 相当于调用了onPause() =============");
            return;
        } else {  // 在最前端显示 相当于调用了onResume()  但是当该Fragment没有实例化的情况下，不会走这里;
            //网络数据刷新
            L.e("================= 在最前端显示 相当于调用了onResume() =====================");
            checkUserInfo();
            getUserInfo();
        }
    }

    /**
     * 检查用户 是否登陆
     * <p>
     * 刷新界面
     */
    private void checkUserInfo() {
        // if (!TextUtils.isEmpty(Share.get().getLogintoken())) {
        if (UserInfoManager.isLogin()) {
//            tv_user_name.setText("账号：" + Share.get().getUserName());
//            tv_user_jf.setVisibility(View.VISIBLE);
//            tv_user_jf.setText("UID：" + Share.get().getUid());
            tv_logout.setVisibility(View.VISIBLE);
            //head_view.setBackgroundColor(getResources().getColor(R.color.f99262046));
            tvUserName.setText(SPUtils.getString(getActivity(), SPUtils.USER_NAME, ""));
            if (UserInfoManager.getUserInfo() != null) {
                tvUdid.setText("UID:" + UserInfoManager.getUserInfo().getFid());
                vip_text.setText(UserInfoManager.getUserInfo().getVip() + "");
            }
            // tv_vip.setText(Share.get().getVip());
            llLogin.setVisibility(View.VISIBLE);
            llNologin.setVisibility(View.GONE);
            getUserEntrustCashBackCharges();
            my_finance_rl.setVisibility(View.GONE);
            zican_ll.setVisibility(View.VISIBLE);
            if(CoinwHyUtils.isServiceStop){
                rlt_my_balance_stop.setVisibility(View.GONE);
            }
            // iv_qr_code.setVisibility(View.VISIBLE);
        } else { // iv_qr_code.setVisibility(View.GONE);
//            tv_user_name.setText("登录/注册");
//            tv_user_jf.setVisibility(View.GONE);
            my_finance_rl.setVisibility(View.VISIBLE);
            zican_ll.setVisibility(View.GONE);
            if(CoinwHyUtils.isServiceStop){
                rlt_my_balance_stop.setVisibility(View.VISIBLE);
            }
            totalMoney_tv.setText("** ");
//            bibiMoney_tv.setText("** CNYT");
//            fabiMoney_tv.setText("** CNYT");
            tv_logout.setVisibility(View.GONE);
            //head_view.setBackgroundColor(getResources().getColor(R.color.base_col));
            llLogin.setVisibility(View.GONE);
            llNologin.setVisibility(View.VISIBLE);
            tv_post.setText("");
        }
    }

    //获取版本信息
    private void getVersion() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        //渠道包-不提供更新功能
        if (BuildConfig.APP_CHANNEL_VALUE == 1) {
           return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GetVersion, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.dd75));
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                Log.e("检查版本", "result=" + result);
                VersionInfo versionInfo = null;
                try {
                    versionInfo = GsonUtil.json2Obj(result, VersionInfo.class);
                    if (DoubleUtils.parseDouble(BuildConfig.VERSION_CODE+"") < DoubleUtils.parseDouble(versionInfo.getAndroid_version_code())) {
                        UpgradeUtils.getInstance().upgrade(getActivity(), versionInfo);
                    } else {
                        Log.e("SnackBarUtils", "SnackBarUtils");
                        SnackBarUtils.ShowBlue(getActivity(), getString(R.string.dd76));
                        //  showMessage("已是最新版本",1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 收到 点击了 确定更新按钮对话框
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.clickSureUpVersioin clickSureUpVersioin) {
        Bundle bundle = new Bundle();
        bundle.putString("downUrl", versionInfo.getAndroid_downurl());
        startActivity(UpVersionActivity.class, bundle);
        CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("getUserWallet" + UserInfoManager.getUserInfo().getFid(), "");
    }

    @Override
    public void hideProgress() {
        if (getActivity() != null) {
            DialogManager.INSTANCE.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UpgradeUtils.getInstance().release();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        if (financeDialog != null) {
            try {
                if (financeDialog.isShowing()) {
                    financeDialog.dismiss();
                }
            } catch (Throwable t) {
            }
        }
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (BaseApp.getSelf() != null && !BaseApp.getSelf().isConnected()) {
            return;
        }
        if (isVisibleToUser) {
            if (UserInfoManager.isLogin()) {
                getUserInfo();
                getTotalUserWallet();
            }
            //根据服务器红包大开关进行控制
            if(!CoinwHyUtils.isServiceStop && redEnvelope_rl != null){
                redEnvelope_rl.setVisibility(AppUtils.isRedEnvelopeClose() ? View.GONE : View.VISIBLE);
            }
            checkHBTPartner();
        }
    }

    //刷新资产信息
    void updataTotalUserWallet(TotalUserWalletBean userWalletBean) {
        lastUserWalletBean=userWalletBean;
        if (Constant.showMoney) {
            refreshTotalMoney();
        }
    }
    private void refreshTotalMoney(){
        if(lastUserWalletBean==null){
            return;
        }
        totalMoney_tv.setText("≈" + PricingMethodUtil.getPricingUnit() +
                PricingMethodUtil.getResultByExchangeRate(lastUserWalletBean.getTotalAsset(),lastUserWalletBean.getUnit()));
    }

    //获取用户信息
    private void getUserInfo() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        if (!UserInfoManager.isLogin()) {
            if (hbtView != null) {
                hbtView.setVisibility(View.GONE);
            }
            return;
        }
        params.put("loginToken", UserInfoManager.getToken());
        String postToken = UserInfoManager.getToken();
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                //   showLoginDialog(result);
                Log.d("个人信息", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    Log.e("校验token", "code= : " + result);
                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("userInfo");
                        UserInfoManager.setToken(SPUtils.getLoginToken());
                        userInfoBean = new Gson().fromJson(object.toString(), UserInfoBean.class);
                        //检查是否开通HBT合伙人
                        checkHBTPartner();
                        if (userInfoBean != null) {
                            UserInfoManager.setUserInfoBean(userInfoBean);
                            //登录IM
                            BaseApp.getSelf().loginIM();
                            //
                        }
                        checkUserInfo();
                        if (userInfoBean.isIdDeductible()) {
                            iv_OnorOff.setImageDrawable(getResources().getDrawable(R.mipmap.switch_on));
                        } else {
                            iv_OnorOff.setImageDrawable(getResources().getDrawable(R.mipmap.switch_off));
                        }
                        if (userInfoBean.isHasC2Validate() && userInfoBean.isHasC3Validate()) {
                            tv_post.setText(R.string.dd77);
                            tv_post.setTextColor(getResources().getColor(R.color.ff8881a6));
                        } else if (userInfoBean.isHasC2Validate() && !userInfoBean.isHasC3Validate()) {
                            tv_post.setText(R.string.dd78);
                            tv_post.setTextColor(getResources().getColor(R.color.ff8881a6));
                            //  iv_shenfen_right.setVisibility(View.GONE);
//                            rl_item02.setClickable(false);
                        } else {
                            if (userInfoBean.isPostC2Validate()) {
                                tv_post.setText(R.string.dd79);
                                tv_post.setTextColor(getResources().getColor(R.color.red));
                            } else if (!TextUtils.isEmpty(userInfoBean.getKsTokenUrl())) {
                                //TODO 直接跳转旷视
                                tv_post.setText(R.string.authenticate);
                                tv_post.setTextColor(getResources().getColor(R.color.color_f8f8f8));
                            } else {
                                tv_post.setText(R.string.dd80);
                                tv_post.setTextColor(getResources().getColor(R.color.color_f8f8f8));
//                                rl_item02.setClickable(true);
                                iv_shenfen_right.setVisibility(View.VISIBLE);
                            }
                        }
//                        RealNameDialog dialog = new RealNameDialog(getActivity(),userInfoBean);
//                        dialog.setClickListener(new RealNameDialog.DialogClickListener() {
//                            @Override
//                            public void onClick() {
//
//                            }
//                        });
//                        dialog.initDialog("").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //ETF标识
        ETFHepler.getETFDisclaimer();
    }

    private void gotoHBTPartner() {
        //TODO 跳转至HBT合伙人页面
        //TODO 先关闭，待后期上线
        Intent intent = new Intent(getActivity(), PyramidSaleWebViewActivity.class);
        intent.putExtra("url", UrlConstants.HBT_H5_URL);
        intent.putExtra(PyramidSaleWebViewActivity.EXTRA_TYPE, PyramidSaleWebViewActivity.TYPE_HBT);
        intent.putExtra("token", UserInfoManager.getToken());
        intent.putExtra("title", getString(R.string.str_hbt));
        getActivity().startActivity(intent);
    }

    private void checkHBTPartner() {
        //异常情况
        if (hbtView == null) {
            return;
        }
        //未登录或者不是HBT合伙人，隐藏入口
        if (userInfoBean == null || !userInfoBean.getIsHBTPartner() || !UserInfoManager.isLogin()) {
            hbtView.setVisibility(View.GONE);
        } else {
            hbtView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        if (UserInfoManager.isLogin()) {
            getUserInfo();
            bindHyperpay();
            getTotalUserWallet();
        }
        checkHBTPartner();
        super.onResume();
    }

    //是否开启coins抵扣
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void whetherDeductible() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        if (!UserInfoManager.isLogin()) {
            MToast.show(getActivity(), getString(R.string.ee58), 1);
            return;
        }
        String idDeductible = "";
        if (UserInfoManager.getUserInfo().isIdDeductible()) {
            idDeductible = "0";
        } else {
            idDeductible = "1";
        }
        params.put("idDeductible", idDeductible);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.whetherDeductible, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.dd83));
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean results = jsonObject.getBoolean("result");
                    String value = jsonObject.getString("value");
                    if (results) {
                        showMessage(value, 2);
                        getUserInfo();
                    } else {
                        showMessage(value, 2);
                    }
                } catch (JSONException e) {
                    SnackBarUtils.ShowRed(getActivity(), getString(R.string.dd90));
                    e.printStackTrace();
                }
            }
        });
    }
}
