package huolongluo.byw.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.android.legend.ui.transfer.AccountTransferActivity;
import com.google.gson.reflect.TypeToken;
import com.legend.common.TestThemeActivity;
import com.uuzuche.lib_zxing.DisplayUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.CommonBean;
import huolongluo.byw.byw.bean.entity.HomeUserStepBean;
import huolongluo.byw.byw.inform.activity.NoticeActivity;
import huolongluo.byw.byw.net.UrlConstants;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab04.WebViewActivity;
import huolongluo.byw.byw.ui.oneClickBuy.C2cStatus;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.heyue.ui.TransActionHomeFragment;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.c2c.oct.bean.OtcCoinBean;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.reform.home.activity.WelfareWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.zxing.MipcaActivityCapture;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DataUtils;
import huolongluo.bywx.utils.EncryptUtils;
import okhttp3.Request;
public class NewUserHomeHeaderView extends LinearLayout implements View.OnClickListener {
    public static final String TAG = "NewUserHomeHeaderView";
    public static final int STEP_REQUEST_INTERVAL = 3000;
    public static final int STEP1 = 0;
    public static final int STEP2 = 1;
    public static final int STEP3 = 2;
    public static final int STEP4 = 3;
    public static final int STEP_FINISH = 4;
    private Context context;
    private Callback callback;
    private Pair<View, TextView>[] stepViews = new Pair[4];
    private TextView tvCnyt;
    private TextView tvUsdt;
    private TextView tvLastStep;//剩下步数
    private ImageView ivStep1;
    private ImageView ivStep2;
    private ImageView ivStep3;
    private EditText etAmount;
    private View stepViewGroup;
    private View finishViewGroup;
    private int currentStep = 0;
    /**
     * 用户上次请求step的时间，要做一个控制请求间隔的判断
     */
    private long lastStepRequestTime = 0L;

    public NewUserHomeHeaderView(Context context) {
        this(context, null);
    }

    public NewUserHomeHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewUserHomeHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_new_user_home_header, this);
        tvCnyt = findViewById(R.id.tv_cnyt);
        tvUsdt = findViewById(R.id.tv_usdt);
        tvLastStep = findViewById(R.id.tv_last_step);
        etAmount = findViewById(R.id.et_amount);
        View vStep1 = findViewById(R.id.v_step1);
        TextView tvStep1 = findViewById(R.id.tv_step1);
        ivStep1 = findViewById(R.id.ivStep1);
        ivStep2 = findViewById(R.id.ivStep2);
        ivStep3 = findViewById(R.id.ivStep3);
        View vStep2 = findViewById(R.id.v_step2);
        TextView tvStep2 = findViewById(R.id.tv_step2);
        View vStep3 = findViewById(R.id.v_step3);
        TextView tvStep3 = findViewById(R.id.tv_step3);
        View vStep4 = findViewById(R.id.v_step4);
        TextView tvStep4 = findViewById(R.id.tv_step4);
        stepViewGroup = findViewById(R.id.step_view);
        finishViewGroup = findViewById(R.id.finish_view);
        findViewById(R.id.btn_buy).setOnClickListener(this);
        findViewById(R.id.rlt_welfare).setOnClickListener(this);
        findViewById(R.id.iv_novice).setOnClickListener(this);
        findViewById(R.id.rlt_customer_service).setOnClickListener(this);
        tvCnyt.setOnClickListener(this);
        tvUsdt.setOnClickListener(this);
        stepViews[0] = Pair.create(vStep1, tvStep1);
        stepViews[1] = Pair.create(vStep2, tvStep2);
        stepViews[2] = Pair.create(vStep3, tvStep3);
        stepViews[3] = Pair.create(vStep4, tvStep4);
        tvCnyt.setSelected(true);
        for (Pair<View, TextView> p : stepViews) {
            if (p.first != null) {
                p.first.setOnClickListener(this);
            }
            if (p.second != null) {
                p.second.setOnClickListener(this);
            }
        }
        if (DataUtils.isOpenHeader()) {
            findViewById(R.id.otcLL).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.otcLL).setVisibility(View.GONE);
        }
        loadOneKey();
    }

    public void setStep() {
        Logger.getInstance().debug(TAG, "setStep currentStep:" + currentStep);
        if (currentStep < STEP_FINISH) {
            if (currentStep == STEP1) {
                tvLastStep.setText(String.format(AppUtils.getString(R.string.home_welcome_coinw_info), (STEP_FINISH - currentStep)));
            } else {
                tvLastStep.setText(String.format(AppUtils.getString(R.string.home_welcome_coinw_info_last), (STEP_FINISH - currentStep)));
            }
            for (int i = 0; i < stepViews.length; i++) {
                Pair<View, TextView> p = stepViews[i];
                if (p.first != null) {
                    p.first.setSelected(i == currentStep);
                }
                TextView tv = p.second;
                if (tv != null) {
                    tv.setSelected(i == currentStep);
                }
                setArrow();
            }
            stepViewGroup.setVisibility(VISIBLE);
            finishViewGroup.setVisibility(GONE);
        } else { // 完成了首页引导步骤
            stepViewGroup.setVisibility(GONE);
            finishViewGroup.setVisibility(VISIBLE);
            // 如果用户在新手版首页，而且用户是新用户，显示弹窗
            Logger.getInstance().debug(TAG, "setStep SPUtils.isProHome(getContext()):" + SPUtils.isProHome(getContext()) + "UserInfoManager.getUserInfo().isNewComer():" + UserInfoManager.getUserInfo().isNewComer());
            if (!BaseApp.getSelf().isOpenStepCompleteDialog && !SPUtils.isProHome(getContext()) && UserInfoManager.getUserInfo().isNewComer()) {
                BaseApp.getSelf().isOpenStepCompleteDialog = true;
                DialogUtils.getInstance().showTwoButtonDialog(context,
                        context.getResources().getString(R.string.hint_info),
                        context.getResources().getString(R.string.home_finish_dialog_content),
                        context.getResources().getString(R.string.cancle),
                        context.getResources().getString(R.string.home_goto_profession_dialog_btn),
                        new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                        markEnterProHome();
                    }
                });
            }
        }
    }
    private void loadOneKey() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        OkhttpManager.postAsync(UrlConstants.ACTION_ONE_KEY, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                Logger.getInstance().debug(TAG, "loadOneKey", e);
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, "loadOneKey-result: " + result);
                try {
                    OtcCoinBean bean = GsonUtil.json2Obj(result, OtcCoinBean.class);
                    if (bean != null) {
                        SPUtils.saveString(context, AppConstants.LOCAL.KEY_LOCAL_ONE_KEY, result);
                        initFastTradeTitle(bean.getData());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void initFastTradeTitle(List<OtcCoinBean.DataBean> list){
        for (int i = 0; i <list.size() ; i++) {
            if(list.get(i).getCoinName().equalsIgnoreCase("CNYT")){
                tvCnyt.setVisibility(VISIBLE);
            } else{
                tvCnyt.setVisibility(GONE);
            }
            if(list.get(i).getCoinName().equalsIgnoreCase("USDT")){
                tvUsdt.setVisibility(VISIBLE);
            } else {
                tvUsdt.setVisibility(GONE);
            }
        }
    }

    /**
     * 标记进入专业版首页
     */
    public void markEnterProHome() {
        if (callback != null) {
            callback.onProHomeClick(); // 标记成功，进入新首页
        }
        if (!UserInfoManager.isLogin()) {
            return;
        }
        //Dialog dialogLoading = CustomLoadingDialog.createLoadingDialog(context);
        //dialogLoading.show();
        Type type = new TypeToken<SingleResult<CommonBean>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OKHttpHelper.getInstance().removeRequest(UrlConstants.NEW_USER_HOME_PRO_IN);
        OKHttpHelper.getInstance().post(UrlConstants.NEW_USER_HOME_PRO_IN, params, new INetCallback<SingleResult<CommonBean>>() {
            @Override
            public void onSuccess(SingleResult<CommonBean> o) throws Throwable {
                //AppHelper.dismissDialog(dialogLoading);
                if (TextUtils.equals("200", o.code)) {
                    if (o.data.getCode() == 0) {
                    } else {
                        Logger.getInstance().debug(TAG + " -> " + o.data.getValue());
                        //ToastUtil.shortToast(context, o.data.getValue());
                    }
                } else {
                    Logger.getInstance().debug(TAG + " -> " + o.message);
                    //ToastUtil.shortToast(context, o.message);
                }
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                //AppHelper.dismissDialog(dialogLoading);
                Logger.getInstance().error(e);
            }
        }, type);
    }

    /**
     * 请求网络，刷新目前新手指引进行到第几步
     */
    public void refreshStepInfo() {
        if (!UserInfoManager.isLogin()) {
            currentStep = STEP1;
            setStep();
            return;
        }
        if (!UserInfoManager.getUserInfo().isNewComer()) {
            currentStep = STEP_FINISH;
            setStep();
            return;
        }
        // 如果上次请求时间小于请求的间隔，不进行网络请求。防止tab频繁点击引起的频繁请求
        // 可能测试会出问题，先取消相关逻辑
        /* if (System.currentTimeMillis() - lastStepRequestTime < STEP_REQUEST_INTERVAL) {
            return;
        } */
        Type type = new TypeToken<SingleResult<HomeUserStepBean>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OKHttpHelper.getInstance().removeRequest(UrlConstants.NEW_USER_HOME_STEP);
        // 记录上次网络请求时间
        lastStepRequestTime = System.currentTimeMillis();
        OKHttpHelper.getInstance().post(UrlConstants.NEW_USER_HOME_STEP, params, new INetCallback<SingleResult<HomeUserStepBean>>() {
            @Override
            public void onSuccess(SingleResult<HomeUserStepBean> o) throws Throwable {
                if (TextUtils.equals("200", o.code)) {
                    if (o.data.getCode() == 0) {
                        currentStep = o.data.getStatus();
                        setStep();
                    } else {
                        Logger.getInstance().debug(TAG + " -> " + o.message);
                    }
                } else {
                    Logger.getInstance().debug(TAG + " -> " + o.message);
                }
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                Logger.getInstance().error(e);
            }
        }, type);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cnyt:
                tvCnyt.setSelected(true);
                tvUsdt.setSelected(false);
                break;
            case R.id.tv_usdt:
                tvCnyt.setSelected(false);
                tvUsdt.setSelected(true);
                break;
            case R.id.btn_buy:
                if (callback != null) {
                    String amount = etAmount.getText().toString().trim();
                    MainActivity.self.gotoFastTrade(amount, tvCnyt.isSelected());
                }
                break;
            case R.id.iv_novice:
                gotoNovice();
                break;
            case R.id.rlt_customer_service:
                gotoCustomerService();
                break;
            case R.id.rlt_welfare:
                gotoWelfare();
                break;
            case R.id.v_step1:
            case R.id.tv_step1:
                if (!isViewClickable(v)) {
                    return;
                }
                //  去登录
                if (!UserInfoManager.isLogin() && context instanceof BaseActivity) {
                    ((BaseActivity) context).startActivity(LoginActivity.class);
                }
                break;
            case R.id.v_step2:
            case R.id.tv_step2:
                if (!isViewClickable(v)) {
                    return;
                }
                //  去快捷交易
                MainActivity.self.gotoFastTrade();
                break;
            case R.id.v_step3:
            case R.id.tv_step3:
                if (!isViewClickable(v)) {
                    return;
                }
                //  去划转
                if (!UserInfoManager.isLogin()) {
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).startActivity(LoginActivity.class);
                    }
                } else {
                    AccountTransferActivity.Companion.launch(context, null, null, null, null,
                            true, null);
                }
                break;
            case R.id.v_step4:
            case R.id.tv_step4:
                if (!isViewClickable(v)) {
                    return;
                }
                // 去交易
                MainActivity.self.gotoTrade(TransActionHomeFragment.TYPE_BB);
                break;
        }
    }

    private boolean isViewClickable(View view) {
        return view.isSelected();
    }

    private void gotoWelfare() {
        Intent intent = new Intent(context, WelfareWebviewActivity.class);
        intent.putExtra("url", UrlConstants.ACTION_VIEWWELFARE);
        intent.putExtra("token", UserInfoManager.getToken());
        intent.putExtra("title", context.getString(R.string.str_welfare_center));
        context.startActivity(intent);
    }

    private void gotoNovice() {
        Intent intentx = new Intent(context, NewsWebviewActivity.class);
        intentx.putExtra("url", UrlConstants.NOVICE);
        intentx.putExtra("token", UserInfoManager.getToken());
        intentx.putExtra("title", AppUtils.getString(R.string.home_novice));
        context.startActivity(intentx);
    }

    private void gotoCustomerService() {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        OKHttpHelper.getInstance().removeRequest(UrlConstants.NEW_USER_HOME_STEP);
        OKHttpHelper.getInstance().removeRequest(UrlConstants.NEW_USER_HOME_PRO_IN);
    }

    private void setArrow() {
        ivStep1.setVisibility(GONE);
        ivStep2.setVisibility(GONE);
        ivStep3.setVisibility(GONE);
        if(currentStep==STEP1){
            ivStep1.setVisibility(VISIBLE);
        }else if(currentStep==STEP2){
            ivStep2.setVisibility(VISIBLE);
        }else if(currentStep==STEP3){
            ivStep3.setVisibility(VISIBLE);
        }
    }

    public interface Callback {
        /**
         * 点击去专业版首页的回调
         */
        void onProHomeClick();
    }
}
