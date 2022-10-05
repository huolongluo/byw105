package com.android.coinw.biz.trade.helper;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
public class ETFHepler {
    private static final String TAG = "ETFHelper";

    //需要登录后才可使用
    public static void showAgreeTip(Context context, final INetCallback<SingleResult<SingleResult<String>>> callback) {
        String loginToken = UserInfoManager.getToken();
        if (TextUtils.isEmpty(loginToken)) {
            return;
        }
        DialogUtils.getInstance().showETFTipDialog(context, String.format(context.getString(R.string.str_disclaimer_content), DomainUtil.INSTANCE.getWebUrlSuffix())
                , new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
                iagree(callback);
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
            }
        });
    }

    //同意负责声明
    private static void iagree(INetCallback<SingleResult<SingleResult<String>>> callback) {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        String loginToken = UserInfoManager.getToken();
        if (TextUtils.isEmpty(loginToken)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("type", "1");
        params.put("loginToken", loginToken);
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", loginToken);
        Type type = new TypeToken<SingleResult<SingleResult<String>>>() {
        }.getType();
        OKHttpHelper.getInstance().postForStringResult(UrlConstants.ACTION_DISCLAIMER_AGREE, params, callback, type);
    }

    /**
     * 是否已经同意免责声明
     * @return
     */
    public static boolean checkETFDisclaimer() {
        int disclaimer = SPUtils.getInt(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF_DISCLAIMER, -1);
        return disclaimer == 1;
    }

    public static void getETFDisclaimer() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        String loginToken = UserInfoManager.getToken();
        if (TextUtils.isEmpty(loginToken)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("type", "1");
        params.put("loginToken", loginToken);
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", loginToken);
        Type type = new TypeToken<SingleResult<SingleResult<Boolean>>>() {
        }.getType();
        OKHttpHelper.getInstance().postForStringResult(UrlConstants.ACTION_DISCLAIMER, params, new INetCallback<SingleResult<SingleResult<Boolean>>>() {
            @Override
            public void onSuccess(SingleResult<SingleResult<Boolean>> result) throws Throwable {
                try {
                    if (result.data.data) {
                        SPUtils.saveInt(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF_DISCLAIMER, result.data.data ? 1 : 0);
                    }
                }catch (Throwable t){

                }
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
            }
        }, type);
    }
}
