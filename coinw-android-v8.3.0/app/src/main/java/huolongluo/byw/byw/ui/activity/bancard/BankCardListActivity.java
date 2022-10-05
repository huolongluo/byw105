package huolongluo.byw.byw.ui.activity.bancard;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.CommonBean;
import huolongluo.byw.byw.bean.UserAssetsBean;
import huolongluo.byw.byw.bean.WithdrawInfoBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.addbankcard.AddBankCardActivity;
import huolongluo.byw.byw.ui.activity.rmbtixian.RmbTiXianActivity;
import huolongluo.byw.byw.ui.adapter.BankCardListAdapter;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import okhttp3.Request;
/**
 * Created by 火龙裸 on 2018/1/4.
 */
public class BankCardListActivity extends BaseActivity{
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.right_iv)
    ImageView right_iv;
    @BindView(R.id.lv_card_list)
    ListView lv_card_list;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.net_error_view)
    FrameLayout net_error_view;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    @BindView(R.id.tv_no_data)
    RelativeLayout tv_no_data;
    private List<WithdrawInfoBean> withdrawInfoBeen = new ArrayList<>();
    private BankCardListAdapter bankCardListAdapter;
    int deletePosition = -1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bankcard_list;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
    }

    private void initToolBar() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        right_iv.setImageResource(R.mipmap.add);
        title_tv.setText(getString(R.string.add_bank));
        initToolBar();
        eventClick(back_iv).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        btn_reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawInfo();
            }
        });
        eventClick(right_iv).subscribe(o -> {
            startActivity(AddBankCardActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        lv_card_list.setEmptyView(tv_no_data);
        bankCardListAdapter = new BankCardListAdapter(this, withdrawInfoBeen, R.layout.item_bankcard_list);
        lv_card_list.setAdapter(bankCardListAdapter);

     /*   lv_card_list.setOnMenuItemClickListener((position, menu, index) ->
        {
            RSACipher rsaCipher = new RSACipher();
            String body = "id="+ URLEncoder.encode(String.valueOf(withdrawInfoBeen.get(position).getId()));
            try {
                String body1 = rsaCipher.encrypt(body, UrlConstants.publicKeys);
                subscription = bankCardListPresent.deleteBankCard(body1, Share.get().getLogintoken(),position);

                Log.d("获取到了验证码",body1);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }

            return false;
        });*/
        bankCardListAdapter.setAdapterListener(new BankCardListAdapter.BankCardListAdapterListener() {
            @Override
            public void onDelete(String id, int position) {
                DialogUtils.getInstance().showTwoButtonDialog(BankCardListActivity.this, getString(R.string.condelete_bank), getString(R.string.cancle), getString(R.string.confirm));
                DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        dialog.dismiss();
                        deletePosition = position;
                        deleteBankCard(id);
                    }
                });
            }
        });
        lv_card_list.setOnItemClickListener((parent, view, position, id) -> {
            EventBus.getDefault().post(new Event.clickBankCard(withdrawInfoBeen.get(position).getBankType(), //
                    withdrawInfoBeen.get(position).getAddress(), withdrawInfoBeen.get(position).getBankNumber(), withdrawInfoBeen.get(position).getId() + ""));
            RmbTiXianActivity.bankId = String.valueOf(withdrawInfoBeen.get(position).getId());
            RmbTiXianActivity.bankType = withdrawInfoBeen.get(position).getBankType();
            Intent intent = new Intent();
            intent.putExtra("bankName", withdrawInfoBeen.get(position).getBankNumber());
            intent.putExtra("bankId", withdrawInfoBeen.get(position).getId() + "");
            setResult(102, intent);
            close();
        });
    }

    void deleteBankCard(String id) {
        DialogManager.INSTANCE.dismiss();
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        showProgress("");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.Delete_BankAddress, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                hideProgress();
                DialogManager.INSTANCE.dismiss();
                MToast.show(BankCardListActivity.this, getString(R.string.net_timeout1), 1);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                hideProgress();
                if (isMyDestroy()) {
                    return;
                }
                Log.i("删除银行卡", "url : " + UrlConstants.Delete_BankAddress + " ;  result= :  " + result);
                DialogManager.INSTANCE.dismiss();//{"result":true,"code":0,"value":"操作成功"}
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("value");
                    if (code == 0) {
                        MToast.show(BankCardListActivity.this, msg, 1);
                        if (withdrawInfoBeen != null && withdrawInfoBeen.size() > deletePosition) {
                            withdrawInfoBeen.remove(deletePosition);
                        }
                        bankCardListAdapter.replaceAll(withdrawInfoBeen);
                        bankCardListAdapter.notifyDataSetChanged();
                    } else {
                        MToast.show(BankCardListActivity.this, msg, 1);
                        MToast.show(BankCardListActivity.this, msg, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void withdrawInfo() {
        DialogManager.INSTANCE.showProgressDialog(this, getString(R.string.load));
        HashMap<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.Withdraw_Info, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager.INSTANCE.dismiss();
                if (isMyDestroy()) {
                    return;
                }
                e.printStackTrace();
                net_error_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                if (isMyDestroy()) {
                    return;
                }
                net_error_view.setVisibility(View.GONE);
                DialogManager.INSTANCE.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("value");
                    if (code == 0) {
                        JSONArray jsonObject1 = jsonObject.getJSONArray("withdrawInfo");
                        if (jsonObject1 != null) {
                            withdrawInfoBeen = new Gson().fromJson(jsonObject1.toString(), new TypeToken<List<WithdrawInfoBean>>() {
                            }.getType());
                            bankCardListAdapter.replaceAll(withdrawInfoBeen);
                        }
                    } else {
                        MToast.show(BankCardListActivity.this, msg, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        withdrawInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
