package huolongluo.byw.heyue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.coinw.biz.event.BizEvent;
import com.blankj.utilcodes.utils.ToastUtils;
import com.legend.modular_contract_sdk.api.ModularContractSDK;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.fragment.contractTab.ContractUserInfoEntity;
import huolongluo.byw.heyue.ui.OpenHYActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.AgreementUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import okhttp3.Request;
/**
 */
public class HeYueUtil {
    private static HeYueUtil heYueUtil = new HeYueUtil();

    public static HeYueUtil getInstance() {
        if (null == heYueUtil) {
            synchronized (HeYueUtil.class) {
                heYueUtil = new HeYueUtil();
            }
        }
        return heYueUtil;
    }

    /**
     * 初始化合约sdk
     */
    public void init() {
        getHeYueUser(UserInfoManager.getToken());
    }

    /**
     * 开通合约账户
     * @param loginToken
     */
    public void openHeYueAccount(Context context, String loginToken) {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", loginToken);
        OkhttpManager.postAsync(UrlConstants.OPEN_HY_ACCOUNT, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                LogUtils.getInstance().i("开通合约账户失败，数据返回message=" + e.getMessage() + "errorMsg=" + errorMsg + "request=" + request.toString());
            }

            @Override
            public void requestSuccess(String result) {
                LogUtils.getInstance().i("开通合约账户成功，数据返回=" + result);
                if (!TextUtils.isEmpty(result)) {
                    AgreementUtils.saveHyOpen(context);
                    EventBus.getDefault().post(new BizEvent.Contract.OpenAgreementSuccess());

                    ContractUserInfoEntity contractUserInfoEntity = GsonUtil.json2Obj(result, ContractUserInfoEntity.class);
                    if (null != contractUserInfoEntity && null != contractUserInfoEntity.getData()) {
                        ContractUserInfoEntity.DataBean data = contractUserInfoEntity.getData();
                        ModularContractSDK.INSTANCE.login(UserInfoManager.getToken());
                    }
                    //开始设置用户信息
                    ToastUtils.showShortToast(R.string.open_hy_success);
                    ((Activity) context).finish();//关闭当前界面
                }
            }
        });
    }

    /**
     * 获取合约用户信息
     * @param loginToken
     */
    public void getHeYueUser(String loginToken) {
        if (TextUtils.isEmpty(loginToken)) {
            //如果Token值为空时，说明用户未登录
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", loginToken);
        OkhttpManager.postAsync(UrlConstants.GET_HY_USER, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                Logger.getInstance().error(e);
            }

            @Override
            public void requestSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    ContractUserInfoEntity contractUserInfoEntity = GsonUtil.json2Obj(result, ContractUserInfoEntity.class);
                    if (null != contractUserInfoEntity && null != contractUserInfoEntity.getData()) {
                        ContractUserInfoEntity.DataBean data = contractUserInfoEntity.getData();
                        if(data.getStatus()==1){
                            ModularContractSDK.INSTANCE.login(UserInfoManager.getToken());
                        }
                    }
                }
            }
        });
    }

    /**
     * 跳转到开通合约界面
     */
    public void openHY() {
        //跳转到开通合约界面
        Intent intent = new Intent(BaseApp.applicationContext, OpenHYActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.applicationContext.startActivity(intent);
    }

    /**
     * 判断跳转到coinw的登录界面或协议页面
     */
    public void toLoginOrAgreementActivity() {
        //判断，如果coinw用户没登录，跳转到coinw登录界面
        if (UserInfoManager.isLogin()) {
            //跳转到开通合约界面
            Intent intent = new Intent(BaseApp.applicationContext, OpenHYActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseApp.applicationContext.startActivity(intent);
        } else {
            Intent intent = new Intent(BaseApp.applicationContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseApp.applicationContext.startActivity(intent);
        }
    }

}
