package huolongluo.byw.byw.ui.activity.coindetail;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.legend.ui.login.LoginActivity;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.UserAssetsBean;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.cointixian.CoinTXNewActivity;
import huolongluo.byw.byw.ui.activity.cthistory.FinanceRecordUtil;
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.FinanceRecordBean;
import huolongluo.byw.model.FinanceRecordInfo;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.mine.activity.FinanceRecordActivity;
import huolongluo.byw.reform.mine.activity.RechargeActivity;
import huolongluo.byw.reform.mine.adapter.FinanceRecordAdapter;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.DoubleUtils;
import huolongluo.bywx.utils.EncryptUtils;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/18.
 */
public class CoinDetailActivity extends huolongluo.byw.reform.base.BaseActivity {
    private static final String TAG = "CoinDetailActivity";
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.iv_logo)
    ImageView iv_logo;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.title_tv2)
    TextView title_tv2;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_assest_all)
    TextView tv_assest_all;
    @BindView(R.id.tv_asset_unit)
    TextView tv_asset_unit;
    @BindView(R.id.tv_assest_keyong)
    TextView tv_assest_keyong;
    @BindView(R.id.tv_assest_dongjie)
    TextView tv_assest_dongjie;
    @BindView(R.id.lv_content)
    ListView lv_content;
    @BindView(R.id.main_radio)
    RadioGroup main_radio;
    @BindView(R.id.all_bn)
    TextView all_bn;
    @BindView(R.id.no_data_tv)
    RelativeLayout no_data_tv;
    private AssetCoinsBean coinsBean;
    private int areaType;
    private UserInfoBean userInfoBean;
    private DecimalFormat df = new DecimalFormat(Constant.ASSETS_DEFAULT_AMOUNT);
    ;
    private List<FinanceRecordBean> mData1 = new ArrayList<>();
    private FinanceRecordAdapter adapter;
    Unbinder unbinder;
    private RadioButton radioB_2;
    private RadioButton radioB_3;
    private RadioButton radioB_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);
        unbinder = ButterKnife.bind(this);
        initViewsAndEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    int withdrawDigit = 4;

    protected void initViewsAndEvents() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            coinsBean = bundle.getParcelable("coinBean");
            areaType = bundle.getInt("areaType");
            if (coinsBean != null) {
                withdrawDigit = coinsBean.getWithdrawDigit();
            }
        }
        radioB_1 = findViewById(R.id.radioB_1);
        //Drawable drawable1 = getResources().getDrawable(R.drawable.congzhi_tabbutton); //获取图片
        // drawable1.setBounds(0, 0, DeviceUtils.dip2px(this, 22), DeviceUtils.dip2px(this, 22));  //设置图片参数
        // radioB_1.setCompoundDrawables(null, drawable1, null, null);
        radioB_2 = findViewById(R.id.radioB_2);
        //Drawable drawable2 = getResources().getDrawable(R.drawable.tix_tabbutton); //获取图片
        //drawable2.setBounds(0, 0, DeviceUtils.dip2px(this, 22), DeviceUtils.dip2px(this, 22));  //设置图片参数
        // radioB_2.setCompoundDrawables(null, drawable2, null, null);
        radioB_3 = findViewById(R.id.radioB_3);
        // Drawable drawable3 = getResources().getDrawable(R.drawable.jiaoyi_tabbutton); //获取图片
        //drawable3.setBounds(0, 0, DeviceUtils.dip2px(this, 22), DeviceUtils.dip2px(this, 22));  //设置图片参数
        //radioB_3.setCompoundDrawables(null, drawable3, null, null);
        radioB_3.setText(getString(R.string.str_transfer));
        adapter = new FinanceRecordAdapter(this);
        lv_content.setAdapter(adapter);
        viewClick(radioB_1, v -> {
            chongZhi();
            radioB_1.setBackground(getResources().getDrawable(R.drawable.bg_accent_rbtn));
            radioB_1.setTextColor(getResources().getColor(R.color.white));
            radioB_2.setBackground(getResources().getDrawable(R.drawable.bg_normal_rbtn));
            radioB_2.setTextColor(getResources().getColor(R.color.light_text_content));
            radioB_3.setBackground(getResources().getDrawable(R.drawable.bg_normal_rbtn));
            radioB_3.setTextColor(getResources().getColor(R.color.light_text_content));
        });
        viewClick(radioB_2, v -> {
            tiXian();
            radioB_2.setBackground(getResources().getDrawable(R.drawable.bg_accent_rbtn));
            radioB_2.setTextColor(getResources().getColor(R.color.white));
            radioB_1.setBackground(getResources().getDrawable(R.drawable.bg_normal_rbtn));
            radioB_1.setTextColor(getResources().getColor(R.color.light_text_content));
            radioB_3.setBackground(getResources().getDrawable(R.drawable.bg_normal_rbtn));
            radioB_3.setTextColor(getResources().getColor(R.color.light_text_content));
        });
        viewClick(radioB_3, v -> {
            radioB_3.setBackground(getResources().getDrawable(R.drawable.bg_accent_rbtn));
            radioB_3.setTextColor(getResources().getColor(R.color.white));
            radioB_1.setBackground(getResources().getDrawable(R.drawable.bg_normal_rbtn));
            radioB_1.setTextColor(getResources().getColor(R.color.light_text_content));
            radioB_2.setBackground(getResources().getDrawable(R.drawable.bg_normal_rbtn));
            radioB_2.setTextColor(getResources().getColor(R.color.light_text_content));
            if (coinsBean == null) {
                return;
            }
            AccountTransferActivity.Companion.launch(CoinDetailActivity.this, null, null, coinsBean.getId(), null,
                    true, coinsBean.getShortName());
        });
        getUserInfo();
        getData();
        all_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coinsBean == null) {
                    return;
                }
                Intent intent = new Intent(CoinDetailActivity.this, FinanceRecordActivity.class);
                intent.putExtra("id", coinsBean.getId());
                intent.putExtra("coinName", coinsBean.getShortName());
                startActivity(intent);
            }
        });
        if (coinsBean != null) {
//            Glide.with(this).load(coinsBean.getLogo()).error(R.mipmap.rmblogo).centerCrop().into(iv_logo);
            RequestOptions ro = new RequestOptions();
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            Glide.with(this).load(coinsBean.getLogo()).apply(ro).into((ImageView) iv_logo);
            tv_name.setText(coinsBean.getShortName());
            if (coinsBean.getZhehe() == null) {
                tv_assest_all.setText(Constant.ASSETS_DEFAULT_AMOUNT);
            } else {
                tv_assest_all.setText(PricingMethodUtil.getResultByExchangeRate(coinsBean.getZhehe(), "CNY", Constant.ASSETS_AMOUNT_PRECISION));
            }
            tv_asset_unit.setText(getResources().getString(R.string.zczh) + "(" + PricingMethodUtil.getPricingUnit() + ")");
            tv_assest_dongjie.setText(df.format(DoubleUtils.parseDouble(coinsBean.getFrozen())) + "");
            tv_assest_keyong.setText(df.format(DoubleUtils.parseDouble(coinsBean.getTotal())) + "");
            //title_tv.setText(coinsBean.getShortName() + getString(R.string.finance));
            title_tv.setVisibility(View.GONE);
            title_tv2.setText(coinsBean.getShortName() + getString(R.string.finance));
        }
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FinanceRecordBean bean = adapter.getItem(i);
                FinanceRecordUtil.gotoDetail(CoinDetailActivity.this, bean);
            }
        });
    }

    void tiXian() {
        if (coinsBean.isIsWithDraw()) {
            if (userInfoBean == null) {
                getUserInfo();
                MToast.showButton(CoinDetailActivity.this, getString(R.string.ner_exp), 1);
                return;
            }
//            if (userInfoBean.isHasC2Validate()) {
            if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                //
                //JIRA:COIN-1721
                //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
                if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                    DialogUtils.getInstance().setOnclickListener(null);
                    DialogUtils.getInstance().showOneButtonDialog(CoinDetailActivity.this, getString(R.string.no_withdrawal), getString(R.string.as16));
                    return;
                }
                if (BuildConfig.DEBUG || BuildConfig.ENV_DEV) {
                    gotoTxActivity();
                } else {
                    checkTx();
                }
            } else {
                DialogUtils.getInstance().showTwoButtonDialog(CoinDetailActivity.this, getString(R.string.dd66), getString(R.string.dd60), getString(R.string.dd51));
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
                            startActivity(new Intent(CoinDetailActivity.this, LoginActivity.class));
                        } else {
                            startActivity(new Intent(CoinDetailActivity.this, SafeCentreActivity.class));
                        }
                    }
                });
            }
        } else {
            DialogUtils.getInstance().showImageDialog(CoinDetailActivity.this, getString(R.string.aa42), R.mipmap.tixian);
        }
    }

    //检查用户是否存在穿仓损失
    private void checkTx() {
        gotoTxActivity();
//        DialogManager2.INSTANCE.showProgressDialog(this);
//        Type type = new TypeToken<SingleResult<Boolean>>() {
//        }.getType();
//        Map<String, Object> params = new HashMap<>();
//        params.put("loginToken", UserInfoManager.getToken());
//
//        OKHttpHelper.getInstance().get(UrlConstants.CHECK_CLEARANCE_LOSS, params, checkTxCallback, type);
    }

    private void gotoTxActivity() {
        if (coinsBean == null) {
            return;
        }
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("assetBean", coinsBean);
        Intent intent = new Intent(CoinDetailActivity.this, CoinTXNewActivity.class);
        intent.putExtra("bundle", bundle2);
        startActivity(intent);
    }

    void chongZhi() {
        if (coinsBean == null) {
            return;
        }
        if (coinsBean.isIsRecharge()) {
            if (userInfoBean == null) {
                getUserInfo();
                MToast.showButton(CoinDetailActivity.this, getString(R.string.ner_exp), 1);
                return;
            }
            //根据产品要求，充值不需要通过初级认证
            Bundle bundle1 = new Bundle();
            bundle1.putString("cnName", coinsBean.getCnName());
            bundle1.putString("shortName", coinsBean.getShortName());
            bundle1.putInt("coinId", coinsBean.getId()); // 获取二维码图片用的coinId
            bundle1.putString("address", coinsBean.getCnName());
            bundle1.putBoolean("iseos", coinsBean.isIseos());
            bundle1.putString("logo", coinsBean.getLogo());
            bundle1.putString("mainNetworkSpecification", coinsBean.getMainNetworkSpecification());
            Intent intent = new Intent(CoinDetailActivity.this, RechargeActivity.class);
            intent.putExtras(bundle1);
            startActivity(intent);

//            if (userInfoBean.isHasC2Validate()) {
//                if (userInfoBean.isPostC2Validate()) {
//                    Bundle bundle1 = new Bundle();
//                    bundle1.putString("cnName", coinsBean.getCnName());
//                    bundle1.putString("shortName", coinsBean.getShortName());
//                    bundle1.putInt("coinId", coinsBean.getId()); // 获取二维码图片用的coinId
//                    bundle1.putString("address", coinsBean.getCnName());
//                    bundle1.putBoolean("iseos", coinsBean.isIseos());
//                    bundle1.putString("logo", coinsBean.getLogo());
//                    bundle1.putString("mainNetworkSpecification", coinsBean.getMainNetworkSpecification());
//                    Intent intent = new Intent(CoinDetailActivity.this, RechargeActivity.class);
//                    intent.putExtras(bundle1);
//                    startActivity(intent);
//                } else {
//                    MToast.showButton(CoinDetailActivity.this, getString(R.string.shz), 1);
//                }
//            } else {
//                //  showMessage("请先进行实名认证", 2);
//                DialogUtils.getInstance().showTwoButtonDialog(CoinDetailActivity.this, getString(R.string.brrenz), getString(R.string.cancle), getString(R.string.identification));
//                DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
//                    @Override
//                    public void onLiftClick(AlertDialog dialog, View view) {
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onRightClick(AlertDialog dialog, View view) {
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
//                        startActivity(new Intent(CoinDetailActivity.this, RenZhengBeforeActivity.class));
//                        finish();
//                    }
//                });
//            }
        } else {
            //  showMessage("充值暂未开放", 1);
            DialogUtils.getInstance().showImageDialog(CoinDetailActivity.this, getString(R.string.aa43), R.mipmap.chongzhi);
        }
    }

    private void getData() {
        Type types = new TypeToken<SingleResult<SingleResult<FinanceRecordInfo>>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("type", 0);
        params.put("currentPage", 1);
        params.put("symbol", coinsBean.getId());
        params.put("fstatus", 0);
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());

        OKHttpHelper.getInstance().post(UrlConstants.GET_FINANCE_RECORD, params, getDataCallback, types);

    }

    private void getUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        if (!UserInfoManager.isLogin()) {
            return;
        }
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                UserAssetsBean userAssetsBean = null;
                try {
                    userAssetsBean = GsonUtil.json2Obj(result, UserAssetsBean.class);
                    if (userAssetsBean.isResult()) {
                        userInfoBean = userAssetsBean.getUserInfo();
                        //登录IM
                        BaseApp.getSelf().loginIM();
                        //
                    } else {
                        //showMessage("获取用户信息失败",2);
                        SnackBarUtils.ShowRed(CoinDetailActivity.this, getString(R.string.getuseinfo_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        radioB_1.setEnabled(true);
        radioB_2.setEnabled(true);
        radioB_3.setEnabled(true);
    }

    protected INetCallback<SingleResult<Boolean>> checkTxCallback = new INetCallback<SingleResult<Boolean>>() {
        @Override
        public void onSuccess(SingleResult<Boolean> result) throws Throwable {
            DialogManager2.INSTANCE.dismiss();

            if (result == null) {
                //TODO 处理异常情况
                return;
            }
            if (result.code.equals("200")) {
                if (result.data == null) {//没有数据则为0
                    return;
                }
                if (result.data) {//有提币限制
                    DialogUtils.getInstance().showTxLimitDialog(CoinDetailActivity.this);
                } else {
                    gotoTxActivity();
                }
            } else {
                DialogUtils.getInstance().showConfirmDialog(CoinDetailActivity.this, result.message, getResources().getString(R.string.confirm), null);
            }

        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            DialogManager2.INSTANCE.dismiss();
            if (!(e instanceof SocketException)) { // 取消http请求会报java.net.SocketException: Socket closed，这里忽略的这个异常
                SnackBarUtils.ShowRed(CoinDetailActivity.this, getString(R.string.net_timeout2));
            }
            //TODO 处理异常情况
        }
    };
    private INetCallback<SingleResult<SingleResult<FinanceRecordInfo>>> getDataCallback = new INetCallback<SingleResult<SingleResult<FinanceRecordInfo>>>() {
        @Override
        public void onSuccess(SingleResult<SingleResult<FinanceRecordInfo>> response) throws Throwable {
            Logger.getInstance().debug(TAG, "result:" + GsonUtil.obj2Json(response, SingleResult.class));
            if (response == null || response.data == null || !TextUtils.equals(response.code, "200")) {
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            if (response.data.code.equals("0")) {
                List<FinanceRecordBean> list = response.data.data.getList();
                if (list == null || mData1 == null) {
                    return;
                }
                if (list.size() > 5) {
                    list = list.subList(0, 5);
                }
                mData1.clear();
                mData1.addAll(list);
                adapter.refresh(mData1);
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
        }
    };
}
