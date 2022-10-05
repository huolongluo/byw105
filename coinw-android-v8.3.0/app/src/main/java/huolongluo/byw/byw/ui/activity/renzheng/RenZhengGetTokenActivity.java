package huolongluo.byw.byw.ui.activity.renzheng;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcodes.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.renzheng.model.TokenUrl;
import huolongluo.byw.helper.FaceVerifyHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.C2cIsShowBean;
import huolongluo.byw.reform.safecenter.RenzhengBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.MToast;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/1/3 0003.
 */

public class RenZhengGetTokenActivity extends BaseActivity {
    private static final String TAG = "RenZhengGetTokenActivit";
    Unbinder unbinder;


    @BindView(R.id.xing_tv)
    TextView xing_tv;

    @BindView(R.id.cardId_tv)
    TextView cardId_tv;
    @BindView(R.id.next_bn)
    Button next_bn;

    @BindView(R.id.back_iv)
    ImageButton back_iv;

    @BindView(R.id.title_tv)
    TextView title_tv;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_renzheng_gettoken);

        unbinder = ButterKnife.bind(this);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_tv.setText(getString(R.string.identity1));
        next_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*   Intent intent=new Intent(RenZhengGetTokenActivity.this, RenzhengActivity.class);
                intent.putExtra("cardId",cardId_tv.getText().toString());
                startActivity(intent);*/
                if (FastClickUtils.isFastClick(1000)) {
                    return;
                }
                getIsAliKyc();
            }
        });

    }
    //通过接口判断使用阿里kyc还是
    private void getIsAliKyc(){
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.getConfig, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.DOMAIN + UrlConstants.getConfig + " errorMsg: " + errorMsg);
                Logger.getInstance().error(e);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                //{"RED_ENVELOPE_DISPLAY_CLOSE":{"describe":"关闭红包功能展示给用户 true 关闭","value":"true"},"C2C_CLOSE":{"describe":"关闭C2C true 关闭","value":"true"},"RED_ENVELOPE_CLOSE":{"describe":"关闭红包 true 关闭","value":"false"}}
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.DOMAIN + UrlConstants.getConfig + " result: " + result);
                C2cIsShowBean c2cIsShowBean = GsonUtil.json2Obj(result, C2cIsShowBean.class);
                if (c2cIsShowBean != null) {
                    if(c2cIsShowBean.getOTC_KYC_ALI_YUN_FACE_ID().value.equals("true")){//走阿里的实人认证
                        getTokenUrl();
                    }else{//走原来的旷视验证
                        faceIdGetH5Token();
                    }
                }
            }
        });
    }
    private void getTokenUrl(){
        Map<String, String> params = new HashMap<>();
        params.put("identityNo",cardId_tv.getText().toString());
        params.put("realName", xing_tv.getText().toString());
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.getKycTokenUrl, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().debug(TAG, "url: " +UrlConstants.getKycTokenUrl + " errorMsg: " + errorMsg);
                Logger.getInstance().error(e);
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.getKycTokenUrl + " result:" + result);
                SingleResult<TokenUrl> response= GsonUtil.json2Obj(result, new TypeToken<SingleResult<TokenUrl>>(){}.getType());
                if (response.code.equals("0")) {
                    FaceVerifyHelper.getInstance().verify(RenZhengGetTokenActivity.this,response.data.getUrl(),true);
                } else if(response.code.equals("-2")){
                    showManualDialog(response.value);
                }
                else {
                    MToast.show(RenZhengGetTokenActivity.this, response.value, 1);
                }
            }
        });
    }

    void faceIdGetH5Token() {

        Map<String, String> params = new HashMap<>();

        //params.put(" loginToken", UserInfoManager.getToken());

        if (TextUtils.isEmpty(xing_tv.getText().toString())) {
            MToast.show(RenZhengGetTokenActivity.this, getString(R.string.please_inname), 1);
            return;
        }
        if (TextUtils.isEmpty(cardId_tv.getText().toString())) {
            MToast.show(RenZhengGetTokenActivity.this, getString(R.string.please_id), 1);
            return;
        }
        params.put("type", "1");
        params.put("identityNo", cardId_tv.getText().toString());
        params.put("realName", xing_tv.getText().toString());
        params.put("new_kyc", "true");
        DialogManager.INSTANCE.showProgressDialog(this, getString(R.string.loading));
        OkhttpManager.getAsync(UrlConstants.DOMAIN + UrlConstants.faceIdGetH5Token + "?loginToken=" + UserInfoManager.getToken() + "&" + OkhttpManager.encryptGet(params), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Log.e("faceIdGetH5Token", "errorMsg= " + errorMsg);
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("faceIdGetH5Token", "result= " + result);
                DialogManager.INSTANCE.dismiss();
                try {

                    RenzhengBean bean = JSONObject.parseObject(result, RenzhengBean.class);

                    if (bean.getCode() == 0) {
                        String token = bean.getData().getToken();
                        Intent intent = new Intent(RenZhengGetTokenActivity.this, WebviewActivity.class);
                        intent.putExtra("data", token);

                        startActivity(intent);
                    } else if (bean.getCode() == -2) {//{"value":"您今天的智能验证已达到上限3次，请转人工审核","code":-2}
                        showManualDialog(bean.getValue());
                    } else {
                        MToast.show(RenZhengGetTokenActivity.this, bean.getValue(), 1);
                    }
                } catch (Exception e) {
                    MToast.show(RenZhengGetTokenActivity.this, getString(R.string.ser_exp), 1);
                    e.printStackTrace();
                }
            }
        });
    }

    private void showManualDialog(String value){
        ToastUtils.showLongToast(R.string.verify_times_over);
    }

}
