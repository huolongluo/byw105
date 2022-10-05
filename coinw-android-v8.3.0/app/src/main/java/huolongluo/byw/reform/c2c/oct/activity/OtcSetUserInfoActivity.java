package huolongluo.byw.reform.c2c.oct.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.bindphone.BindPhoneActivity;
import huolongluo.byw.databinding.ActivityOtcsetUserinfoBinding;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;

/**
 * Created by dell on 2019/6/4.
 */

public class OtcSetUserInfoActivity extends BaseActivity {

    String nickName;
    ActivityOtcsetUserinfoBinding mBinding;
      int resource;
      boolean fromToc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  setContentView(R.layout.activity_otcset_userinfo);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcset_userinfo);
        if (getIntent() != null) {
            nickName = getIntent().getStringExtra("nickName");
            resource = getIntent().getIntExtra("resource",0);
            fromToc = getIntent().getBooleanExtra("fromToc",false);
        }
        mBinding.etNickname.setText(nickName+"");
             if((nickName+"").length()>0){
                 mBinding.headTv.setText((nickName+"").charAt(0)+"");
             }

                if(!UserInfoManager.getOtcUserInfoBean().getData().isMerch()){
                    mBinding.sellshopLl.setVisibility(View.INVISIBLE);
                }else {
                    mBinding.sellshopLl.setVisibility(View.VISIBLE);
                }

         if(resource==1){
             mBinding.btnSave.setText(R.string.xx31);
             mBinding.include.titleTv.setText(R.string.xx32);
         }else {
             mBinding.include.titleTv.setText(R.string.xx33);
         }

           if(fromToc){
               mBinding.btnSave.setText(R.string.xx34);
           }


           viewClick(mBinding.sellshopLl,v -> {

               Intent intent = new Intent(this, SellerInfoActivity.class);
               intent.putExtra("userId", UserInfoManager.getUserInfo().getFid() + "");
               startActivity(intent);


           });


        //   title_tv.setText("设置昵称");


        viewClick(mBinding.emailRl,v -> {

            DialogUtils.getInstance().showOneButtonDialog(this, getString(R.string.xx36), getString(R.string.xx35));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    dialog.dismiss();
                }
            });

        });

        viewClick(mBinding.phoneRl,v -> {

            DialogUtils.getInstance().showTwoButtonDialog(this, getString(R.string.xx38), getString(R.string.xx39), getString(R.string.xx37));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    dialog.dismiss();
                    Intent intent = new Intent(OtcSetUserInfoActivity.this, BindPhoneActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    intent.putExtra("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
                    intent.putExtra("isBindTelephone", UserInfoManager.getUserInfo().isBindMobil());
                    startActivityForResult(intent, 100);
                }
            });

        });


        viewClick(mBinding.include.backIv, v -> finish());
        viewClick(mBinding.btnSave, v -> {


            if (TextUtils.isEmpty(mBinding.etPhone.getText().toString())) {
                MToast.show(this, getString(R.string.xx40), 1);
                return;
            }


            updateUserinfo();

        });


        if (!TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getEmail())) {
            mBinding.etEmail.setText(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getEmail());
            mBinding.etEmail.setEnabled(false);
            mBinding.emailStatusTv.setText(R.string.xx41);
            mBinding.emailRl.setVisibility(View.VISIBLE);
        } else {
            mBinding.emailRl.setVisibility(View.GONE);
            mBinding.emailStatusTv.setText(R.string.xx42);
            mBinding.emailStatusTv.setTextColor(getResources().getColor(R.color.black));
        }
        if (!TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getTelephone())) {
            mBinding.etPhone.setText(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getTelephone());

            if(UserInfoManager.getUserInfo().isBindMobil()){
                mBinding.phoneRl.setVisibility(View.VISIBLE);
                mBinding.etPhone.setEnabled(false);
            }else {
                mBinding.phoneRl.setVisibility(View.GONE);
                mBinding.etPhone.setEnabled(true);
            }


            mBinding.phoneStatusTv.setText(R.string.xx43);
        } else {
            mBinding.emailRl.setVisibility(View.GONE);
            mBinding.phoneStatusTv.setText(R.string.xx44);
            mBinding.phoneStatusTv.setTextColor(getResources().getColor(R.color.black));
        }

        if (!TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getWechat())) {

        }


        mBinding.etWechat.setText(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getWechat() + "");
        mBinding.etZhifub.setText(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getAlipay() + "");
        mBinding.etWhatsapp.setText(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getWhasapp() + "");
        mBinding.etTelegram.setText(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getTelegram() + "");
        mBinding.etSkype.setText(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getSkype() + "");


    }


    void updateUserinfo() {


        if(!TextUtils.isEmpty(mBinding.etEmail.getText().toString().trim())&&!mBinding.etEmail.getText().toString().contains("@")){
            MToast.showButton(OtcSetUserInfoActivity.this, getString(R.string.xx45), 2);
            return;
        }




        Map<String, String> params = new HashMap<>();

        params.put("type", "2");//1,设置昵称，2设置基本信息
        params.put("email", mBinding.etEmail.getText().toString());//设置昵称
        params.put("alipay", mBinding.etZhifub.getText().toString());//支付宝
        params.put("wechat", mBinding.etWechat.getText().toString());//微信
        params.put("telephone", mBinding.etPhone.getText().toString());//电话

        params = OkhttpManager.encrypt(params);

        params.put("whasapp", mBinding.etWhatsapp.getText().toString());
        params.put("telegram", mBinding.etTelegram.getText().toString());
        params.put("skype", mBinding.etSkype.getText().toString());

        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.update_userinfo, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcSetUserInfoActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {

                        if(fromToc){
                            UserInfoManager.getOtcUserInfoBean().getData().setOtcUserLevel(3);
                            startActivity(new Intent(OtcSetUserInfoActivity.this, PaymentAccountActivityNew.class));

                        }


                        finish();
                    } else {

                    }
                    MToast.show(OtcSetUserInfoActivity.this, baseBean.getValue(), 1);

                } catch (Exception e) {
                }
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode==212){
            if(data!=null){
                String phone=  data.getStringExtra("phone");
                mBinding.etPhone.setText(phone+"");
            }
        }


    }
}
