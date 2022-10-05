package huolongluo.byw.reform.c2c.oct.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcodes.utils.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.databinding.ActivityPaymentaccountNewBinding;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.adapter.BrandCardAdapter;
import huolongluo.byw.reform.c2c.oct.bean.BrandCardsEneity;
import huolongluo.byw.reform.c2c.oct.bean.CardStatusEntity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;

/**
 * Created by dell on 2019/6/4.
 */

public class PaymentAccountActivityNew extends BaseActivity {

    TextView title_tv;
    TextView btn_next;
    ImageView right_iv;
    ActivityPaymentaccountNewBinding mBinding;
    private BrandCardsEneity thirdAccountBean;
    private List<BrandCardsEneity.DataBeanX.DataBean> data;
    private BrandCardAdapter cardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_paymentaccount_new);
        title_tv = fv(R.id.title_tv);
        right_iv = fv(R.id.right_iv);
        title_tv.setText(R.string.qa19);
        right_iv.setImageDrawable(getResources().getDrawable(R.mipmap.add));
        viewClick(fv(R.id.right_iv), v -> showPopupWindow());
        viewClick(fv(R.id.back_iv), v -> finish());

//        BrandCardsEneity dataEntity =
//                GsonUtil.json2Obj(BrandCardsEneity.brandListJson, BrandCardsEneity.class);
        initAdapter();
    }

    //停止滚动
    public static final int SCROLL_STATE_IDLE = 0;
    //正在被外部拖拽,一般为用户正在用手指滚动
    public static final int SCROLL_STATE_DRAGGING = 1;
    //自动滚动开始
    public static final int SCROLL_STATE_SETTLING = 2;
    private int rcStatus = 0;

    private void initAdapter() {
        data = new ArrayList<>();
        cardAdapter = new BrandCardAdapter(data);
        mBinding.brandCardList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.brandCardList.setAdapter(cardAdapter);
        mBinding.brandCardList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                currentPage = ++currentPage;
                get_thirdpay();
            }
        });
        mBinding.brandCardList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                rcStatus = newState;
            }
        });
        cardAdapter.notifyDataSetChanged();
        cardAdapter.onClickListener((v, position) -> {
            switch (data.get(position).getType()) {
                case BrandCardAdapter.ZFB:
                    aliPayAction(data.get(position));
                    break;
                case BrandCardAdapter.BRAND_CARD:
                    backAction(data.get(position));
                    break;
                case BrandCardAdapter.WX:
                    wechartPay(data.get(position));
                    break;
            }
        });
        cardAdapter.onCardStatusListener((v, position, status) -> {
            if (rcStatus != SCROLL_STATE_IDLE) return;
            Map<String, String> params = new HashMap<>();
            params.put("loginToken", UserInfoManager.getToken());
            params.put("id", String.valueOf(data.get(position).getId()));
            DialogManager.INSTANCE.showProgressDialog(this);
            if (openResult) {
                DialogManager.INSTANCE.dismiss();
                openResult = false;
                return;
            }
            //支付方式开启和关闭
            OkhttpManager.postAsync(status ? UrlConstants.PayEnabled : UrlConstants.PayDisabled
                    , params, new OkhttpManager.DataCallBack() {
                        @Override
                        public void requestFailure(Request request, Exception e, String errorMsg) {
                            if (isFinishing()) {
                                return;
                            }
                            DialogManager.INSTANCE.dismiss();
                            e.printStackTrace();
                            SnackBarUtils.ShowRed(PaymentAccountActivityNew.this, errorMsg);
                        }

                        @Override
                        public void requestSuccess(String result) {
                            if (isFinishing()) {
                                return;
                            }
                            DialogManager.INSTANCE.dismiss();
                            initAlertAction(result, v, position, status);
                        }
                    });
//            DialogView aDefault = DialogView.getDefault(this);
//            aDefault.initDialog("", "您要关闭的收款方式正在广告中使用，请先下架广告再进行关闭", "知道了", true).show();
//            aDefault.setPositiveListener(() -> aDefault.dialog.dismiss());
        });
    }

    boolean openResult = false;

    private void initAlertAction(String result, View v, int position, boolean status) {
        try {
            CardStatusEntity cardStatusEntity = new Gson().fromJson(result, CardStatusEntity.class);
            if (null != cardStatusEntity.getData()) {
                if (cardStatusEntity.getData().getCode() == -101) {
                    if (v instanceof Switch) {
                        openResult = true;
                        ((Switch) v).setChecked(true);
                    }

                    DialogUtils.getInstance().showTwoButtonDialog(PaymentAccountActivityNew.this,
                            cardStatusEntity.getData().getValue(), getString(R.string.qa21), getString(R.string.qa20));
                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                        @Override
                        public void onLiftClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                openResult = false;
                                DialogUtils.getInstance().dismiss();
                            }
                        }

                        @Override
                        public void onRightClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                openResult = false;
                                DialogUtils.getInstance().dismiss();
                            }
                            Intent intent = new Intent(PaymentAccountActivityNew.this, SellerInfoActivity.class);
                            intent.putExtra("userId", UserInfoManager.getUserInfo().getFid() + "");
                            startActivity(intent);
                        }
                    });
                } else if (cardStatusEntity.getData().getCode() == -102) {
                    if (v instanceof Switch) {
                        openResult = true;
                        ((Switch) v).setChecked(true);
                    }
                    DialogUtils.getInstance().showOneButtonDialog(PaymentAccountActivityNew.this,
                            cardStatusEntity.getData().getValue(), getString(R.string.qa22));
                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                        @Override
                        public void onLiftClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                openResult = false;
                                DialogUtils.getInstance().dismiss();
                            }
                        }

                        @Override
                        public void onRightClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                openResult = false;
                                DialogUtils.getInstance().dismiss();
                            }
                        }
                    });
                }
            } else if (cardStatusEntity.getCode() == -1) {
                ToastUtils.showShortToast(cardStatusEntity.getValue());
                if (v instanceof Switch) {
                    openResult = false;
                    ((Switch) v).setChecked(false);
                }
            } else {
                data.get(position).setStatus(status ? 2 : 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wechartPay(BrandCardsEneity.DataBeanX.DataBean dataBean) {
        if (dataBean != null) {
            if (dataBean.getAccount() != null) {
                Intent intent = new Intent(this, OtcAddWechartActivityNew.class);
                intent.putExtra("type", 2);
                intent.putExtra("data", dataBean);
                intent.putExtra("accountId", dataBean.getAccount());
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, OtcAddWechartActivityNew.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        }
    }

    private void aliPayAction(BrandCardsEneity.DataBeanX.DataBean dataBean) {
        if (dataBean != null) {
            if (dataBean.getAccount() != null) {
                Intent intent = new Intent(this, OtcAddZhifubActivityNew.class);
                intent.putExtra("type", 2);

                intent.putExtra("data", dataBean);

                intent.putExtra("accountId", dataBean.getAccount());
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, OtcAddZhifubActivityNew.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        }
    }

    private void backAction(BrandCardsEneity.DataBeanX.DataBean dataBean) {
        if (dataBean != null) {
            if (dataBean.getAccount() != null) {
                Intent intent = new Intent(this, OtcAddBankActivityNew.class);
                intent.putExtra("type", 2);
                intent.putExtra("data", dataBean);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, OtcAddBankActivityNew.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPage = 1;
        get_thirdpay();
    }

    int currentPage = 1;
    int pageSize = 10;

    void get_thirdpay() {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params.put("currentPage", String.valueOf(currentPage));
        params.put("pageSize", String.valueOf(pageSize));
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.getPaymentList, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                if (isFinishing()) {
                    return;
                }
                DialogManager.INSTANCE.dismiss();
                e.printStackTrace();
                SnackBarUtils.ShowRed(PaymentAccountActivityNew.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                if (isFinishing()) {
                    return;
                }
                DialogManager.INSTANCE.dismiss();
                try {
                    thirdAccountBean = new Gson().fromJson(result, BrandCardsEneity.class);
                    if (thirdAccountBean != null && thirdAccountBean.getCode() == 0)
                        updateUI();
                    else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    void updateUI() {
        List<BrandCardsEneity.DataBeanX.DataBean> data1 = thirdAccountBean.getData().getData();
        if (null == data1 || data1.size() == 0) {
            if (currentPage != 1) {
                return;
            }
        }
        if (currentPage == 1) {
            data.clear();
        }
        data.addAll(data1);
        mBinding.noData.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
        cardAdapter.notifyDataSetChanged();
//        if (thirdAccountBean.getData() != null) {
//            if (thirdAccountBean.getData().getOtcUserBank() != null
//                    || thirdAccountBean.getData().getOtcUserWechat() != null
//                    || thirdAccountBean.getData().getOtcUserAlipay() != null
//            ) {
//                UserInfoManager.getOtcUserInfoBean().getData().setOtcUserLevel(4);
//                btn_next.setVisibility(View.VISIBLE);
//            } else {
//                btn_next.setVisibility(View.INVISIBLE);
//            }
//        }
    }

    public void showPopupWindow() {
        //显示设置的pop布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.select_card_layout, null);//布局自定义,,,,也可以使用自定义的popupwindow
        LinearLayout zfb = inflate.findViewById(R.id.zfb_view);
        LinearLayout yhk = inflate.findViewById(R.id.yhk_view);
        LinearLayout wx = inflate.findViewById(R.id.wx_view);
        TextView hide = inflate.findViewById(R.id.hide);//取消弹框
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        zfb.setOnClickListener(v -> {
            popupWindow.dismiss();
            aliPayAction(new BrandCardsEneity.DataBeanX.DataBean());
        });
        yhk.setOnClickListener(v -> {
            popupWindow.dismiss();
            backAction(new BrandCardsEneity.DataBeanX.DataBean());
        });
        wx.setOnClickListener(v -> {
            popupWindow.dismiss();
            wechartPay(new BrandCardsEneity.DataBeanX.DataBean());
        });
        hide.setOnClickListener(v -> popupWindow.dismiss());
        popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
    }

    abstract static class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        //用来标记是否正在向上滑动
        private boolean isSlidingUpward = false;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            // 当不滑动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //获取最后一个完全显示的itemPosition
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                int itemCount = manager.getItemCount();

                // 判断是否滑动到了最后一个item，并且是向上滑动
                if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                    //加载更多
                    onLoadMore();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
            isSlidingUpward = dy > 0;
        }

        /**
         * 加载更多回调
         */
        public abstract void onLoadMore();
    }
}
