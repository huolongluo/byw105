package huolongluo.byw.reform.c2c.oct.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;

/**
 * Created by dell on 2019/6/4.
 */

public class OtcSetNickActivity extends BaseActivity {


    TextView title_tv;
    TextView btn_next;
    EditText et_nickname;
    boolean fromToc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otcset_nick);


        if (getIntent() != null) {
            fromToc = getIntent().getBooleanExtra("fromToc", false);
        }

        title_tv = fv(R.id.title_tv);
        btn_next = fv(R.id.btn_next);
        et_nickname = fv(R.id.et_nickname);
        title_tv.setText(R.string.xx29);
        //

        viewClick(fv(R.id.back_iv), v -> {
            finish();
        });
        viewClick(btn_next, v -> {

            String nickname = et_nickname.getText().toString();
            if (!android.text.TextUtils.isEmpty(nickname) && nickname.length() < 11) {
                updateUserinfo();
            } else {
                MToast.show(OtcSetNickActivity.this, getString(R.string.xx30), 2);
            }


            // Intent intent = new Intent(this, OtcSetUserInfoActivity.class);
            //intent.putExtra("nickName", et_nickname.getText().toString());
            // startActivity(intent);
        });


    }


    void updateUserinfo() {

        Map<String, String> params = new HashMap<>();

        params.put("type", "1");//  1 设置昵称, 2 设置基本信息
        params.put("nickname", et_nickname.getText().toString());//

        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this, "");
        OkhttpManager.postAsync(UrlConstants.update_userinfo, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcSetNickActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();

                try {
                    BaseBean data = new Gson().fromJson(result, BaseBean.class);

                    if (data.getCode() == 0) {
                        UserInfoManager.getOtcUserInfoBean().getData().setOtcUserLevel(3);
                        finish();
//                        Intent intent = new Intent(OtcSetNickActivity.this, OtcSetUserInfoActivity.class);
//                        intent.putExtra("nickName", et_nickname.getText().toString());
//                        intent.putExtra("fromToc", fromToc);
//                        startActivity(intent);
                    } else {

                        MToast.show(OtcSetNickActivity.this, data.getValue(), 1);
                    }

                } catch (Exception e) {


                }


            }
        });

    }
}
