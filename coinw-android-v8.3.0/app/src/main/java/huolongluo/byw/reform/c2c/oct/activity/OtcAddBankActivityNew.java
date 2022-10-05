package huolongluo.byw.reform.c2c.oct.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import cn.addapp.pickers.entity.City;
import cn.addapp.pickers.entity.County;
import cn.addapp.pickers.entity.Province;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.banklist.BankListActivity;
import huolongluo.byw.byw.ui.dialog.AddDialog;
import huolongluo.byw.databinding.ActivityOtcaddbankBinding;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.AddPayAcountEntity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.BrandCardsEneity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.AddressPickTask;
import okhttp3.Request;

/**
 * Created by dell on 2019/6/4.
 */

public class OtcAddBankActivityNew extends BaseActivity {
    ActivityOtcaddbankBinding mBinding;
    int type = 1;
    BrandCardsEneity.DataBeanX.DataBean bankBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcaddbank);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initData() {
        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", 1);
            bankBean = (BrandCardsEneity.DataBeanX.DataBean) getIntent().getSerializableExtra("data");
        }
        if (bankBean != null) {
            mBinding.etBankcard.setText(bankBean.getAccount());
            mBinding.etBanklist.setText(bankBean.getBankName());
            mBinding.etBankaddress.setText(bankBean.getBankProvince() + bankBean.getBankCity() + "");
            mBinding.etZhihang.setText(bankBean.getBankAdress());
        }
        mBinding.tvName.setText(UserInfoManager.getUserInfo().getRealName());
        mBinding.include.titleTv.setText(type == 1 ? getString(R.string.cx20) : getString(R.string.cx21));
        mBinding.deleteBtn.setVisibility(type == 1 ? View.GONE : View.VISIBLE);
    }

    private void initView() {
        viewClick(mBinding.include.backIv, v -> finish());
        viewClick(mBinding.deleteBtn, v -> deleteCard());
        viewClick(mBinding.etBanklist, v -> startActivity(new Intent(this, BankListActivity.class)));
        viewClick(mBinding.etBankaddress, v -> startAddressPicker());
        viewClick(mBinding.tvSure, v -> {
            if ((!TextUtils.isEmpty(mBinding.etBankaddress.getText().toString().trim())) && (!TextUtils.isEmpty(mBinding.etBankcard.getText().toString().trim())) //
                    && (!TextUtils.isEmpty(mBinding.etBanklist.toString().trim())) && (!TextUtils.isEmpty(mBinding.etZhihang.toString().trim()))) {
                if (19 < mBinding.etBankcard.getText().toString().length() || 16 > mBinding.etBankcard.getText().toString().length()) {
                    MToast.show(this, getString(R.string.cx22), 1);
                    return;
                }
                if (TextUtils.isEmpty(mBinding.etTradepsw.getText().toString())) {
                    MToast.show(this, getString(R.string.cx23), 1);
                    return;
                }
                updateBank();
            } else {
                MToast.show(OtcAddBankActivityNew.this, getString(R.string.cx24), 1);
            }
        });
    }

    private void deleteCard() {
        AddDialog dialog = new AddDialog();
        dialog.setDialog(AddDialog.DELETE_CARD);
        dialog.show(getSupportFragmentManager(), getClass().getSimpleName());
        dialog.setOnClick(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(bankBean.getId()));//1 新增银行卡 , 2 修改银行卡
            params.put("loginToken", UserInfoManager.getToken());
            DialogManager.INSTANCE.showProgressDialog(this);
            OkhttpManager.postAsync(UrlConstants.deleteBankCard, params, new OkhttpManager.DataCallBack() {
                @Override
                public void requestFailure(Request request, Exception e, String errorMsg) {
                    e.printStackTrace();
                    SnackBarUtils.ShowRed(OtcAddBankActivityNew.this, errorMsg);
                    DialogManager.INSTANCE.dismiss();
                }

                @Override
                public void requestSuccess(String result) {
                    DialogManager.INSTANCE.dismiss();
                    try {
                        BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                        if (baseBean.getCode() == 0) {
                            finish();
                        }
                        MToast.show(OtcAddBankActivityNew.this, baseBean.getValue(), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    String mProvince;
    String mCity;

    void updateBank() {
        if (TextUtils.isEmpty(mCity) || TextUtils.isEmpty(mProvince)) {
            mProvince = bankBean.getBankProvince();
            mCity = bankBean.getBankCity();
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", type + "");//1 新增银行卡 , 2 修改银行卡

        params.put("id", bankBean == null ? "" : bankBean.getId() + "");//1 新增银行卡 , 2 修改银行卡
        params.put("bankNum", mBinding.etBankcard.getText().toString() + "");
        params.put("ftradePassword", mBinding.etTradepsw.getText().toString() + "");
        params = OkhttpManager.encrypt(params);
        //params.put("bankCity", mBinding.etBankaddress.getText().toString() + "");
        params.put("bankCity", mCity + "");
        params.put("bankProvince", mProvince + "");
        params.put("bankAdress", mBinding.etZhihang.getText().toString() + "");
        params.put("bankName", mBinding.etBanklist.getText().toString() + "");
        params.put("loginToken", UserInfoManager.getToken());
        params.put("account", mBinding.etBankcard.getText().toString());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(type == 1 ? UrlConstants.AddAcount : UrlConstants.update_bankCard,
                params, new OkhttpManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, Exception e, String errorMsg) {
                        e.printStackTrace();
                        SnackBarUtils.ShowRed(OtcAddBankActivityNew.this, errorMsg);
                        DialogManager.INSTANCE.dismiss();
                    }

                    @Override
                    public void requestSuccess(String result) {
                        DialogManager.INSTANCE.dismiss();
                        try {
                            AddPayAcountEntity baseBean = new Gson().fromJson(result, AddPayAcountEntity.class);
                            if (baseBean.getCode() == 0) {
                                finish();
                            }
                            MToast.show(OtcAddBankActivityNew.this, baseBean.getValue(), 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void startAddressPicker() {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideProvince(false);
        task.setHideCounty(true);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                //showMessage("数据初始化失败", 1);
                SnackBarUtils.ShowRed(OtcAddBankActivityNew.this, getString(R.string.cx25));
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                if (county == null) {
                    mBinding.etBankaddress.setText(province.getAreaName() + city.getAreaName());
                    mProvince = province.getAreaName();
                    mCity = city.getAreaName();
                } else {
                    mProvince = province.getAreaName();
                    mCity = city.getAreaName();
                    mBinding.etBankaddress.setText(province.getAreaName() + city.getAreaName() + county.getAreaName());
                }
            }
        });
        task.execute("贵州", "毕节", "纳雍");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickDrawerLayout(Event.clickBankName clickBankName) {
        mBinding.etBanklist.setText(clickBankName.bankName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
