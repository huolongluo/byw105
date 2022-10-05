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

import org.greenrobot.eventbus.EventBus;
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
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.byw.ui.activity.setchangepsw.SetChangePswActivity;
import huolongluo.byw.helper.IdentityVerifyHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.c2c.oct.activity.OtcBuyActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetNickActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetUserInfoActivity;
import huolongluo.byw.reform.c2c.oct.activity.PaymentAccountActivityNew;
import huolongluo.byw.reform.c2c.oct.activity.SellerInfoActivity;
import huolongluo.byw.reform.c2c.oct.bean.AdvertisementsBean;
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
 * OTC购买广告页
 */
public class OtcItemBuyFragment extends BaseFragment {
    PullToRefreshListView recyclerView;
    private OtcAdapter adapter;
    RelativeLayout no_data_tv;
    String coinId;
    int currentPage = 1;
    int orderType = 1;
    private AdvertisementsBean.DataBeanX.DataBean mDataBean;
    List<AdvertisementsBean.DataBeanX.DataBean> dataBeanList = new ArrayList<>();

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_otcitem;
    }

    @Override
    protected void onCreatedView(View rootView) {
        EventBus.getDefault().register(this);
        coinId = getArguments().getString("coinId");
        recyclerView = fv(R.id.recyclerView);
        no_data_tv = fv(R.id.no_data_tv);
        //  recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
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
                //  pressButon(dataBean);
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
        recyclerView.setPullRefreshEnable(true);
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
                recyclerView.setPullRefreshEnable(true);
                recyclerView.setPullLoadEnable(true);
                currentPage++;
                advertisements();
            }
        });
        advertisements();
    }
    void pressButon(AdvertisementsBean.DataBeanX.DataBean dataBean) {
        if (!check1()) {
            return;
        }
        if (dataBean.getUid() == UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getUid()) {
            MToast.show(getAContext(), getString(R.string.as39), 2);
            return;
        }
//        if ((UserInfoManager.getOtcUserInfoBean().getData().isBankId() && dataBean.getBankId() != 0)
//                || (UserInfoManager.getOtcUserInfoBean().getData().isAlipayId() && dataBean.getAlipayId() != 0)
//                || (UserInfoManager.getOtcUserInfoBean().getData().isWechatId() && dataBean.getWechatId() != 0)) {
//        if (ArrUtils.listContains(
//                UserInfoManager.getOtcUserInfoBean().getData().getPayAccountTypes(), dataBean.getPayAccountTypes())) {
        Intent intent = new Intent(getAContext(), OtcBuyActivity.class);
        intent.putExtra("dataBean", dataBean);
        intent.putExtra("TradeType", 1);//购买
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.otc_act_in, android.R.anim.fade_out);
//        }
//        else {
//
//            DialogUtils.getInstance().showTwoButtonDialog(getAContext(), "商户不支持您的支付方式 , 您需要添加并激活相应的支付方式", "取消", "去设置");
//            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
//                @Override
//                public void onLiftClick(AlertDialog dialog, View view) {
//
//                    if (dialog != null) {
//                        DialogUtils.getInstance().dismiss();
//                    }
//
//                }
//
//                @Override
//                public void onRightClick(AlertDialog dialog, View view) {
//                    if (dialog != null) {
//                        DialogUtils.getInstance().dismiss();
//                    }
//                    startActivity(PaymentAccountActivityNew.class);
//
//                }
//            });
//        }
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
                        DialogUtils.getInstance().showOneButtonDialog(getActivity(), value, getString(R.string.as15));
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
    boolean secondCheck() {
        if (UserInfoManager.getOtcUserInfoBean() == null) {
            return false;
        }
        if (!UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isAllowOtc()) {
            DialogUtils.getInstance().showOneButtonDialog(getAContext(), getString(R.string.as40), getString(R.string.as16));
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
        } else if (UserInfoManager.getOtcUserInfoBean().getData().isMerch()) {
            return true;//如果是商家，不需要判断风控
        } else if (!UserInfoManager.getOtcUserInfoBean().getData().isOpenLimit()) {
            return true;
        }
//        else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isNewUser()) {
//            DialogUtils.getInstance().showTwoButtonDialog(getAContext(), getString(R.string.as19), getString(R.string.as18), getString(R.string.as17));
//            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
//                @Override
//                public void onLiftClick(AlertDialog dialog, View view) {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
//                }
//
//                @Override
//                public void onRightClick(AlertDialog dialog, View view) {
//                    if (dialog != null) {
//                        dialog.dismiss();
//
//                    }
//                    if (getActivity() instanceof CameraMainActivity) {
//
//                        ((CameraMainActivity) getActivity()).otcToC2c();
//
//                    }
//
//                }
//            });
//            return false;
//        }
        else {
            return true;
        }
    }

    boolean check1() {
        if (!UserInfoManager.isLogin()) {//未登录
            startActivity(LoginActivity.class);
            return false;
        } else if (!UserInfoManager.getUserInfo().isBindMobil()) {//未绑定手机号
            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as20), getString(R.string.as21), getString(R.string.as22));
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
            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as23), getString(R.string.as25), getString(R.string.as24));
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
                    bundle.putString("setChangePswTitle", getString(R.string.as26));
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
        }else if (!secondCheck()) {//风控校验
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean() == null) {
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 2) {
            MToast.show(getAContext(), getString(R.string.as30), 1);
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
        }
//        else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 3) {//没有设置支付信息
//            MToast.show(getAContext(), "请先设置支付信息", 1);
//            startActivity(PaymentAccountActivityNew.class);
//            return false;
//        }
        else {
            return true;
        }
    }

    // 权限校验
    boolean check() {
        if (!UserInfoManager.isLogin()) {
            startActivity(LoginActivity.class);
            return false;
        } else {//已登录,判断实名认证
            if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
                DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as33), getString(R.string.as32), getString(R.string.as31));
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
                        startActivity(RenZhengBeforeActivity.class);
                    }
                });
                return false;
            } else {//已实名认证
                if (UserInfoManager.getUserInfo().getIsHasTradePWD() != 1) {//没有密码
                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as36), getString(R.string.as35), getString(R.string.as34));
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
                            startActivity(SafeCentreActivity.class);
                        }
                    });
                    return false;
                } else if (UserInfoManager.getOtcUserInfoBean() == null) {
                    return false;
                } else {//判断是否设置基本信息
                    if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 2) {
                        MToast.show(getAContext(), getString(R.string.as37), 1);
                        if (TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname())) {
                            startActivity(OtcSetNickActivity.class);
                        } else {
                            Intent intent = new Intent(getAContext(), OtcSetUserInfoActivity.class);
                            intent.putExtra("nickName", UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname());
                            startActivity(intent);
                        }
                        return false;
                    } else {  //没有设置支付信息
                        if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 3) {//没有设置支付信息
                            MToast.show(getAContext(), getString(R.string.as38), 1);
                            startActivity(PaymentAccountActivityNew.class);
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
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

    void advertisements() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "2");
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
        Log.e("advertisements", "==  " + params.toString());
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.advertisements, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                recyclerView.stopRefresh();
                recyclerView.stopLoadMore();
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
                            recyclerView.stopRefresh();
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
                }catch (Exception e){
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
                holder = (OtcAdapter.viewHold) convertView.getTag();
            }
            AdvertisementsBean.DataBeanX.DataBean dataBean = dataBeanList.get(position);
            holder.name_tv.setText(dataBean.getUserName());
            holder.total_tv.setText(dataBean.getLeftAmount_s() + "");
            //  holder.coinName_tv.setText(dataBean.getCoinName());
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
            if (dataBean.getPayAccountTypes() != null) {
                for (int i = 0; i < dataBean.getPayAccountTypes().size(); i++) {
                    switch (dataBean.getPayAccountTypes().get(i)) {
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
            public TextView onlineDot_iv,tvTop;
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
                bank_iv = itemView.findViewById(R.id.bank_iv);
                alipay_iv = itemView.findViewById(R.id.alipay_iv);
                wechat_iv = itemView.findViewById(R.id.wechat_iv);
                namelog_tv = itemView.findViewById(R.id.namelog_tv);
                isVip_iv = itemView.findViewById(R.id.isVip_iv);
                onlineDot_iv = itemView.findViewById(R.id.onlineDot_iv);
                tvTop = itemView.findViewById(R.id.tv_top);
                head_ll = itemView.findViewById(R.id.head_ll);
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
