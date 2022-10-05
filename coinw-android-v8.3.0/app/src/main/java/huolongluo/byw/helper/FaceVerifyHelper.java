package huolongluo.byw.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.security.cloud.CloudRealIdentityTrigger;
import com.alibaba.security.realidentity.ALRealIdentityCallback;
import com.alibaba.security.realidentity.ALRealIdentityResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;

public class FaceVerifyHelper {
    private static final String TAG = "FaceVerifyHelper";
    private static FaceVerifyHelper instance;
    private Context context;
    private String mTriggerType;//接口用于区分扫码还是其他方式进行的验证
    private boolean mIsKycVerify=false;
    private DialogUtils.onBnClickListener mBtnClickListener;
    private FaceDialogListener mFaceDialogListener;

    public static FaceVerifyHelper getInstance() {
        FaceVerifyHelper localInstance = instance;
        if (localInstance == null) {
            synchronized (FaceVerifyHelper.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new FaceVerifyHelper();
                }
            }
        }
        return localInstance;
    }
    public void verify(Context context,String triggerType,boolean isKycVerify) {
        verify(context, null,null,triggerType,isKycVerify);
    }
    public void verify(Context context,String triggerType) {
        verify(context, null,null,triggerType);
    }
    public void verify(Context context, DialogUtils.onBnClickListener listener,String triggerType) {
        verify(context, listener,null,triggerType);
    }
    public void verify(Context context, DialogUtils.onBnClickListener listener,FaceDialogListener faceDialogListener,
                       String triggerType) {
        verify(context,listener,faceDialogListener,triggerType,false);
    }

    public void verify(Context context, DialogUtils.onBnClickListener listener,FaceDialogListener faceDialogListener,
                       String triggerType,boolean isKycVerify) {

        if (context == null) {
            return;
        }
        this.context = context;
        this.mBtnClickListener = listener;
        this.mFaceDialogListener=faceDialogListener;
        this.mTriggerType=triggerType;
        this.mIsKycVerify=isKycVerify;

        getSdkToken();
    }

    //阿里验证前需要获取token
    private void getSdkToken() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        if(!TextUtils.isEmpty(mTriggerType)){
            params.put("url", mTriggerType);
        }
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.getAliVerifySdkToken, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    Logger.getInstance().debug(TAG, "url: " + UrlConstants.getAliVerifySdkToken + " result:" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String sdkToken = data.getString("sdkToken");
                        //注意引用的Context
                        CloudRealIdentityTrigger.start(context, sdkToken, getALRealIdentityCallback());
                    } else if (code == 124) {
                        String value = jsonObject.getString("value");
                        aliVerifyBlack(context, value);
                        return;
                    }else{
                        MToast.show(context, jsonObject.getString("value"), 1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //阿里sdk验证成功需要调该接口
    private void check() {
        if(mIsKycVerify){
            DialogManager.INSTANCE.showProgressDialog(context);
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        if(!TextUtils.isEmpty(mTriggerType)){
            params.put("url", mTriggerType);
        }
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.checkAliVerifySdk, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                if(mIsKycVerify){
                    DialogManager.INSTANCE.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) {
                if(mIsKycVerify){
                    DialogManager.INSTANCE.dismiss();
                }
                try {
                    Logger.getInstance().debug(TAG, "url: " + UrlConstants.checkAliVerifySdk + " result:" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        if(mIsKycVerify){//kyc验证成功直接返回首页
                            String data = jsonObject.getString("data");//此处新增需要验证data为1,
                            if (data.equals("1")) {//通过验证
                                context.startActivity(new Intent(context, MainActivity.class));
                            }
                            return;
                        }
                        if(mBtnClickListener==null){//不需要弹窗
                            return;
                        }

                        String data = jsonObject.getString("data");
                        if (data.equals("1")) {//通过验证
                            String successMsg = context.getString(R.string.str_risk_verify_succeed);
                            String clickMsg = context.getString(R.string.str_continue_trading);
                            DialogUtils.getInstance().showRiskVerifyButtonDialog(context, successMsg, R.mipmap.ic_status_success, clickMsg, mBtnClickListener);
                            if(mFaceDialogListener!=null){
                                mFaceDialogListener.showDialog();
                            }
                        }

                    } else if (code == 124) {
                        String value = jsonObject.getString("value");
                        aliVerifyBlack(context, value,mFaceDialogListener);
                    }else{
                        MToast.show(context, jsonObject.getString("value"), 1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //返回124 在黑名单内的统一弹框处理
    public void aliVerifyBlack(Context context, String value) {
        aliVerifyBlack(context,value,null);
    }
    //返回124 在黑名单内的统一弹框处理
    public void aliVerifyBlack(Context context, String value,FaceDialogListener faceDialogListener) {
        mFaceDialogListener=faceDialogListener;
        if(mFaceDialogListener!=null){
            mFaceDialogListener.showDialog();
        }
        DialogUtils.getInstance().showRiskTipButtonDialog(context, value,
                new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                        if(mFaceDialogListener!=null){
                            mFaceDialogListener.dismissDialog();
                        }
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                        if(mFaceDialogListener!=null){
                            mFaceDialogListener.dismissDialog();
                        }
                    }
                });
    }

    /**
     * 基础回调的方式 TODO
     *
     * @return
     */
    private ALRealIdentityCallback getALRealIdentityCallback() {
        return new ALRealIdentityCallback() {
            @Override
            public void onAuditResult(ALRealIdentityResult result, String s) {
                //DO your things
                if (result == null) {
                    //TODO 处理异常情况
                    Logger.getInstance().debug(TAG, "ALRealIdentityResult is null");
                    return;
                }
                Logger.getInstance().debug(TAG, "aliface-audit: " + result.audit + " s: " + s);
                handleResult(result, s);
            }
        };
    }

    private void handleResult(ALRealIdentityResult result, String msg) {
        if (context == null) {
            context = BaseApp.getSelf();
        }
        //成功失败都需要调用该接口验证
        check();

        if (ALRealIdentityResult.AUDIT_PASS == result) {//认证通过
        } else if (ALRealIdentityResult.AUDIT_FAIL == result) {
            //code:2~12

        } else if (ALRealIdentityResult.AUDIT_NOT == result) {
            //ALRealIdentityResult.AUDIT_NOT
//            -1	未完成认证，原因是：用户在认证过程中，主动退出
//            3001	未完成认证，原因是：认证token无效或已过期
//            3101	未完成认证，原因是：用户姓名身份证实名校验不匹配
//            3102	未完成认证，原因是：实名校验身份证号不存在
//            3103	未完成认证，原因是：实名校验身份证号不合法
//            3104	未完成认证，原因是：认证已通过，重复提交
//            3204	未完成认证，原因是：非本人操作。
//            3206	未完成认证，原因是：非本人操作。
        }
    }

    public interface FaceDialogListener{
        void showDialog();
        void dismissDialog();
    }
}
