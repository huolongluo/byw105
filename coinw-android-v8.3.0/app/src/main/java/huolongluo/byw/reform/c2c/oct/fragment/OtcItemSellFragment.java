package huolongluo.byw.reform.c2c.oct.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.legend.ui.login.LoginActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.bindphone.BindPhoneActivity;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.byw.ui.activity.setchangepsw.SetChangePswActivity;
import huolongluo.byw.helper.IdentityVerifyHelper;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.c2c.oct.activity.OtcBuyActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetNickActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetUserInfoActivity;
import huolongluo.byw.reform.c2c.oct.activity.PaymentAccountActivityNew;
import huolongluo.byw.reform.c2c.oct.activity.SellerInfoActivity;
import huolongluo.byw.reform.c2c.oct.bean.AdvertisementsBean;
import huolongluo.byw.reform.c2c.oct.utils.ArrUtils;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/3/12 0012.
 * OTC出售广告页
 */
public class OtcItemSellFragment extends BaseFragment {
    PullToRefreshListView recyclerView;
    private OtcAdapter adapter;
    List<AdvertisementsBean.DataBeanX.DataBean> dataBeanList = new ArrayList<>();
    String coinId;
    int currentPage = 1;
    RelativeLayout no_data_tv;
    //TODO 后面评估把这块代码整合了
    int orderType = 2;
    private AdvertisementsBean.DataBeanX.DataBean mDataBean;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_otcitem;
    }

    @Override
    protected void onCreatedView(View rootView) {
        coinId = getArguments().getString("coinId");
        recyclerView = fv(R.id.recyclerView);
        no_data_tv = fv(R.id.no_data_tv);
        adapter = new OtcAdapter(dataBeanList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new OtcAdapter.OnClickListener() {
            @Override
            public void onClick(AdvertisementsBean.DataBeanX.DataBean dataBean) {
                if (!UserInfoManager.isLogin()) {//未登录
                    startActivity(LoginActivity.class);
                    return;
                }
                mDataBean = dataBean;
                validate(dataBean);
            }

            @Override
            public void onHeadClick(AdvertisementsBean.DataBeanX.DataBean dataBean) {
                if (dataBean.getIsExternal()) {
                    return;
                }
                Intent intent = new Intent(getAContext(), SellerInfoActivity.class);
                intent.putExtra("userId", dataBean.getUid() + "");
                startActivity(intent);
            }
        });
        recyclerView.setPullLoadEnable(false);
        recyclerView.setPullToListViewListener(new PullToRefreshListView.PullToListViewListener() {
            @Override
            public void onRefresh() {
                recyclerView.stopLoadMore();
                currentPage = 1;
                advertisements();
            }

            @Override
            public void onLoadMore() {
                recyclerView.stopRefresh();
                currentPage++;
                advertisements();
            }
        });
        advertisements();
    }

    String moneyNum = "";
    boolean bankStatus;
    boolean alipayStatus;
    boolean wechatStatus;

    //筛选刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.OtcFlitrade event) {
        Log.e("onMessageEvent", "= " + event.moneyNum);
        moneyNum = event.moneyNum;
        bankStatus = event.bankStatus;
        alipayStatus = event.alipayStatus;
        wechatStatus = event.wechatStatus;
        currentPage = 1;
        advertisements();
    }

    void validate(AdvertisementsBean.DataBeanX.DataBean dataBean) {
        Map<String, String> params = new HashMap<>();
        params.put("adId", dataBean.getId() + "");
        params.put("isExternal", dataBean.getIsExternal() + "");
        params.put("orderType", orderType + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(getActivity());
        OkhttpManager.postAsync(UrlConstants.validate, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager.INSTANCE.dismiss();
                MToast.show(getActivity(), errorMsg, 1);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                   /* {
                        "code": -1,
                            "error": "业务异常",
                            "result": true,
                            "value": "您先完成一次C2C或OTC购买交易1小时后方能与其交易"
                    }*/
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        pressButon(dataBean);
                    } else {
                        String value = jsonObject.getString("value");
                        DialogUtils.getInstance().showOneButtonDialog(getActivity(), value, getString(R.string.as42));
                        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                            @Override
                            public void onLiftClick(AlertDialog dialog, View view) {
                            }

                            @Override
                            public void onRightClick(AlertDialog dialog, View view) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void pressButon(AdvertisementsBean.DataBeanX.DataBean dataBean) {
        if (!check1()) {
            return;
        }
        if (dataBean.getUid() == UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getUid()) {
            MToast.show(getAContext(), getString(R.string.as43), 2);
            return;
        }
        if (ArrUtils.listContains(dataBean.getPayAccountTypes(), UserInfoManager.getOtcUserInfoBean().getData().getPayAccountTypes())) {
            Intent intent = new Intent(getAContext(), OtcBuyActivity.class);
            intent.putExtra("TradeType", 2);
            intent.putExtra("dataBean", dataBean);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.otc_act_in, android.R.anim.fade_out);
        } else {
            DialogUtils.getInstance().showTwoButtonDialog(getAContext(), getString(R.string.as46), getString(R.string.as45), getString(R.string.as44));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        DialogUtils.getInstance().dismiss();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        DialogUtils.getInstance().dismiss();
                    }
                    startActivity(PaymentAccountActivityNew.class);
                }
            });
        }
    }

    void advertisements() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params.put("coinId", coinId);
        params.put("pageNo", currentPage + "");
        params.put("pageSize", "10");
        if (bankStatus) {
            params.put("bankPay", "1");
        }
        if (alipayStatus) {
            params.put("aliPay", "3");
        }
        if (wechatStatus) {
            params.put("wechatPay", "2");
        }
        if (!TextUtils.isEmpty(moneyNum)) {
            params.put("amount", moneyNum);
        }
        //UUEX兼容性参数
        params.put("fillExternal", "true");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.advertisements, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                recyclerView.stopRefresh();
                recyclerView.stopLoadMore();
                try {
                    AdvertisementsBean advertisementsBean = new Gson().fromJson(result, AdvertisementsBean.class);
                    if (advertisementsBean.getCode() == 0) {
                        if (currentPage == 1) {
                            dataBeanList.clear();
                        }
                        dataBeanList.addAll(advertisementsBean.getData().getData());
                        adapter.setData(dataBeanList);
                        if (currentPage >= advertisementsBean.getData().getTotalPage()) {
                            recyclerView.setPullLoadEnable(false);
                        } else {
                            recyclerView.setPullLoadEnable(true);
                        }
                        if (dataBeanList.size() == 0) {
                            no_data_tv.setVisibility(View.VISIBLE);
                        } else {
                            no_data_tv.setVisibility(View.GONE);
                        }
                    } else {
                    }
                    Log.e("advertisements", "result=  " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class OtcAdapter extends BaseAdapter {
        List<AdvertisementsBean.DataBeanX.DataBean> dataBeanList;

        public OtcAdapter(List<AdvertisementsBean.DataBeanX.DataBean> dataBeanList) {
            this.dataBeanList = dataBeanList;
        }

        public void setData(List<AdvertisementsBean.DataBeanX.DataBean> dataBeanList) {
            this.dataBeanList = dataBeanList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHold holder;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.otcitem_fragment_item, null);
                holder = new viewHold(convertView);
                convertView.setTag(holder);
            } else {
                holder = (viewHold) convertView.getTag();
            }
            holder.buy_tv.setText(R.string.as47);
            AdvertisementsBean.DataBeanX.DataBean dataBean = dataBeanList.get(position);
            holder.name_tv.setText(dataBean.getUserName());
            holder.total_tv.setText(dataBean.getLeftAmount_s() + "");
            // holder.coinName_tv.setText(dataBean.getCoinName());
            holder.price_tv.setText(dataBean.getPrice_s() + "");
            holder.limit_num_tv.setText(dataBean.getOrderMin_s() + "-" + dataBean.getOrderMax_s() + dataBean.getCoinName());
            holder.realAmount_tv.setText(dataBean.getCompleteOrderNum() + "");
            holder.completeOrderRate_tv.setText(dataBean.getCompleteOrderRate() + "%");
            if (dataBean.isIsOnline()) {
                holder.onlineDot_iv.setVisibility(View.VISIBLE);
                holder.onlineDot_iv.setText(R.string.online);
            } else {
                holder.onlineDot_iv.setVisibility(View.GONE);
            }
            if (dataBean.isTop()) {
                holder.tvTop.setVisibility(View.VISIBLE);
            } else {
                holder.tvTop.setVisibility(View.GONE);
            }

            if (dataBean.getUserName().length() > 0) {
                holder.namelog_tv.setText(dataBean.getUserName().charAt(0) + "");
                holder.namelog_tv.setBackgroundResource(Util.getRadom());
            }
            if (dataBean.getLevelCode() == 3 || dataBean.getLevelCode() == 4) {
                holder.isVip_iv.setVisibility(View.VISIBLE);
            } else {
                holder.isVip_iv.setVisibility(View.GONE);
            }
            holder.alipay_iv.setVisibility(View.GONE);
            holder.bank_iv.setVisibility(View.GONE);
            holder.wechat_iv.setVisibility(View.GONE);
            //10-25 13:55:05.182 E/AndroidRuntime( 7139): FATAL EXCEPTION: main
            //10-25 13:55:05.182 E/AndroidRuntime( 7139): Process: huolongluo.byw, PID: 7139
            //10-25 13:55:05.182 E/AndroidRuntime( 7139): java.lang.NullPointerException: Attempt to invoke interface method 'int java.util.List.size()' on a null object reference
            //10-25 13:55:05.182 E/AndroidRuntime( 7139): at huolongluo.byw.reform.c2c.oct.fragment.OtcItemSellFragment$OtcAdapter.getView(OtcItemSellFragment.java:339)
//            for (int i = 0; i < dataBean.getPayAccountTypes().size(); i++) {
            if (dataBean.getPayAccountTypes() != null) {
                for (Integer index : dataBean.getPayAccountTypes()) {
                    switch (index) {
                        case Constant.ALIPAY:
                            holder.alipay_iv.setVisibility(View.VISIBLE);
                            break;
                        case Constant.BANK:
                            holder.bank_iv.setVisibility(View.VISIBLE);
                            break;
                        case Constant.WECHAT:
                            holder.wechat_iv.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
            holder.buy_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick(dataBean);
                    }
                }
            });
            holder.head_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onHeadClick(dataBean);
                    }
                }
            });
            return convertView;
        }

        public static interface OnClickListener {
            void onClick(AdvertisementsBean.DataBeanX.DataBean dataBean);

            void onHeadClick(AdvertisementsBean.DataBeanX.DataBean dataBean);
        }

        OnClickListener onClickListener;

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public static class viewHold {
            public TextView buy_tv;
            public TextView name_tv;
            public TextView total_tv;
            public TextView coinName_tv;
            public TextView price_tv;
            public TextView limit_num_tv;
            public TextView realAmount_tv;
            public TextView completeOrderRate_tv;
            public ImageView bank_iv;
            public ImageView alipay_iv;
            public ImageView wechat_iv;
            public TextView namelog_tv;
            public ImageView isVip_iv;
            public TextView onlineDot_iv, tvTop;
            public LinearLayout head_ll;


            public viewHold(View itemView) {
                buy_tv = itemView.findViewById(R.id.buy_tv);
                name_tv = itemView.findViewById(R.id.name_tv);
                total_tv = itemView.findViewById(R.id.total_tv);
                coinName_tv = itemView.findViewById(R.id.coinName_tv);
                price_tv = itemView.findViewById(R.id.price_tv);
                limit_num_tv = itemView.findViewById(R.id.limit_num_tv);
                realAmount_tv = itemView.findViewById(R.id.realAmount_tv);
                completeOrderRate_tv = itemView.findViewById(R.id.completeOrderRate_tv);
                namelog_tv = itemView.findViewById(R.id.namelog_tv);
                isVip_iv = itemView.findViewById(R.id.isVip_iv);
                bank_iv = itemView.findViewById(R.id.bank_iv);
                alipay_iv = itemView.findViewById(R.id.alipay_iv);
                wechat_iv = itemView.findViewById(R.id.wechat_iv);
                onlineDot_iv = itemView.findViewById(R.id.onlineDot_iv);
                head_ll = itemView.findViewById(R.id.head_ll);
                tvTop = itemView.findViewById(R.id.tv_top);
            }
        }
    }

    boolean secondCheck() {
        if (UserInfoManager.getOtcUserInfoBean() == null) {
            return false;
        }
        if (!UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isAllowOtc()) {
            DialogUtils.getInstance().showOneButtonDialog(getAContext(), getString(R.string.as49), getString(R.string.as48));
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
                }
            });
            return false;
        } else {
            return true;
        }
    }

    boolean check1() {
        if (!UserInfoManager.isLogin()) {//未登录
            startActivity(LoginActivity.class);
            return false;
        } else if (!UserInfoManager.getUserInfo().isBindMobil()) {//未绑定手机号
            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as52), getString(R.string.as51), getString(R.string.as50));
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
                    Intent intent = new Intent(getAContext(), BindPhoneActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
                    intent.putExtra("isBindTelephone", false);
                    intent.putExtra("fromToc", true);
                    startActivity(intent);
                }
            });
            return false;
        } else if (UserInfoManager.getUserInfo().getIsHasTradePWD() != 1) {//没有交易密码
            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as53), getString(R.string.as54), getString(R.string.as55));
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
                    Intent intent = new Intent(getAContext(), SetChangePswActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("setChangePswTitle", getString(R.string.as56));
                    bundle.putBoolean("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
                    bundle.putBoolean("isBindTelephone", true);
                    bundle.putBoolean("fromToc", true);
                    intent.putExtra("bundle", bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            });
            return false;
        } else if (!IdentityVerifyHelper.getInstance().isOtcIdentityOk(getActivity())) {//没有实名认证
            return false;
        } else if (!secondCheck()) {//风控校验
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean() == null) {
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 2) {
            MToast.show(getAContext(), getString(R.string.as60), 1);
            if (TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname())) {
                Intent intent = new Intent(getAContext(), OtcSetNickActivity.class);
                intent.putExtra("fromToc", true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getAContext(), OtcSetUserInfoActivity.class);
                intent.putExtra("nickName", UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname());
                intent.putExtra("fromToc", true);
                startActivity(intent);
            }
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 3) {//没有设置支付信息
            DialogUtils.getInstance().showTwoButtonDialog(getAContext(), getString(R.string.as63), getString(R.string.as62), getString(R.string.as61));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        DialogUtils.getInstance().dismiss();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        DialogUtils.getInstance().dismiss();
                    }
                    startActivity(PaymentAccountActivityNew.class);
                }
            });
//            MToast.show(getAContext(), "您需要添加并激活至少一种收款方式，才能出售数字货币", 1);
//            startActivity(PaymentAccountActivityNew.class);
            return false;
        } else {
            return true;
        }
    }
    // 权限校验
//    boolean check() {
//
//        if (!UserInfoManager.isLogin()) {
//
//            startActivity(LoginActivity.class);
//            return false;
//        } else {//已登录,判断实名认证
//
//            if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
//
//
//                DialogUtils.getInstance().showTwoButtonDialog(getActivity(), "请先进行实名认证", "取消", "认证");
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
//                        startActivity(RenZhengBeforeActivity.class);
//
//                    }
//                });
//
//                return false;
//            } else {//已实名认证
//
//                if (UserInfoManager.getUserInfo().getIsHasTradePWD() != 1) {//没有密码
//                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), "请先设置交易密码", "取消", "设置");
//                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
//                        @Override
//                        public void onLiftClick(AlertDialog dialog, View view) {
//                            if (dialog != null) {
//                                dialog.dismiss();
//                            }
//                        }
//
//                        @Override
//                        public void onRightClick(AlertDialog dialog, View view) {
//                            if (dialog != null) {
//                                dialog.dismiss();
//                            }
//                            startActivity(SafeCentreActivity.class);
//                        }
//                    });
//
//                    return false;
//                } else {//判断是否设置基本信息
//
//                    if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 2) {
//
//                        MToast.show(getAContext(), "请先设置基本信息", 1);
//                        if (TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname())) {
//                            startActivity(OtcSetNickActivity.class);
//                        } else {
//                            Intent intent = new Intent(getAContext(), OtcSetUserInfoActivity.class);
//                            intent.putExtra("nickName", UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname());
//                            startActivity(intent);
//                        }
//
//                        return false;
//                    } else {  //没有设置支付信息
//                        if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 3) {//没有设置支付信息
//                            MToast.show(getAContext(), "买家仅支持通过银行卡向您付款，您需要添加并激活相应的收款方式", 1);
//                            startActivity(PaymentAccountActivityNew.class);
//                            return false;
//                        } else {
//                            return true;
//                        }
//                    }
//
//
//                }
//
//            }
//
//        }
//
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
