package huolongluo.byw.reform.c2c.oct.fragment.sellfragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.c2c.oct.activity.OtcAppealDetailActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcOrderCancleDetailActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcTradeCompleteActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellOtherPayedActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellPaymentActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellWaitOtherPayActivity;
import huolongluo.byw.reform.c2c.oct.adapter.OtcOrderSellManItemAdapter;
import huolongluo.byw.reform.c2c.oct.bean.OtcGetOrderBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
/**
 * Created by Administrator on 2019/5/15 0015.
 * <p>
 * 用户订单管理出售界面
 */
public class OtcorderSellManItemFragment extends BaseFragment {
    PullToRefreshListView listView;
    int currentPage = 1;
    private OtcGetOrderBean otcGetOrderBean;
    LinearLayout nodata_view;
    List<OtcGetOrderBean.DataBean.ListDataBean> listDataBeans = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * @param status 订单状态0 未付款，1 已付款，2 已完成，3 用户取消，4 系统撤单，5 申诉中 6，撤销申诉中
     * @param type 1-买 2-卖
     * @param orderType 1用户订单 , 2 商户订单
     * @return
     */
    public static OtcorderSellManItemFragment newFragment(int status, int type, int orderType) {
        OtcorderSellManItemFragment fragment = new OtcorderSellManItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        bundle.putInt("dealType", type);
        bundle.putInt("orderType", orderType);
        Log.e("paraaaaams", "2orderType= " + orderType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public OtcorderSellManItemFragment() {
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_otcordermanitem;
    }

    OtcOrderSellManItemAdapter adapter;
    int status;
    int dealType;
    int orderType;
    RelativeLayout no_data_tv;

    @Override
    protected void onCreatedView(View rootView) {
        fv(R.id.loading_views).setVisibility(View.VISIBLE);
        listView = fv(R.id.listView);
        nodata_view = fv(R.id.nodata_view);
        no_data_tv = fv(R.id.no_data_tv);
        no_data_tv.setBackgroundColor(getResources().getColor(R.color.transparent));
        status = getArguments().getInt("status", 0);
        dealType = getArguments().getInt("dealType", 0);
        orderType = getArguments().getInt("orderType", 0);
        if (status == 2) {
            status = 3;
        } else if (status == 3) {
            status = 2;
        } else if (status == 4) {
            status = 5;
        }
        if (orderType == 2) {
            if (status == 0) {
                status = 1;
            } else if (status == 1) {
                status = 0;
            }
        }
        adapter = new OtcOrderSellManItemAdapter(getAContext(), status, listDataBeans);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OtcGetOrderBean.DataBean.ListDataBean bean = adapter.getItem((int) id);
                //java.lang.NullPointerException: Attempt to invoke virtual method 'int huolongluo.byw.reform.c2c.oct.bean.OtcGetOrderBean$DataBean$ListDataBean.getId()' on a null object reference
                //    at g.a.j.b.b.c.a.a.onItemClick(OtcorderSellManItemFragment.java:4)
                //    at android.widget.AdapterView.performItemClick(AdapterView.java:330)
                //    at android.widget.AbsListView.performItemClick(AbsListView.java:1262)
                //    at android.widget.AbsListView$PerformClick.run(AbsListView.java:3309)
                //    at android.widget.AbsListView$3.run(AbsListView.java:4294)
                //    at android.os.Handler.handleCallback(Handler.java:900)
                //    at android.os.Handler.dispatchMessage(Handler.java:103)
                //    at android.os.Looper.loop(Looper.java:219)
                //    at android.app.ActivityThread.main(ActivityThread.java:8347)
                //    at java.lang.reflect.Method.invoke(Native Method)
                //    at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:513)
                //    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1055)
                if (bean == null) {
                    return;
                }
                if (status == 1) {   //已支付
                    Intent intent = new Intent(getAContext(), OtcUserSellOtherPayedActivity.class);
                    // Intent intent = new Intent(getAContext(), OtcTradeCompleteActivity.class);
                    intent.putExtra("orderId", bean.getId());
                    startActivity(intent);
                }
                // else if (bean.getStatus() == 5 || bean.getStatus() == 6)
                else if (status == 5) {  //申诉
                    Intent intent = new Intent(getAContext(), OtcAppealDetailActivity.class);
                    intent.putExtra("orderId", bean.getId());
                    startActivity(intent);
                } else if (bean.getStatus() == 3 || bean.getStatus() == 4) {  //取消
                    Intent intent = new Intent(getAContext(), OtcOrderCancleDetailActivity.class);
                    intent.putExtra("orderId", bean.getId());
                    intent.putExtra("tradeType", 2);//出售
                    startActivity(intent);
                } else if (status == 2) {//已完成
                    Intent intent = new Intent(getAContext(), OtcTradeCompleteActivity.class);
                    intent.putExtra("orderId", bean.getId());
                    intent.putExtra("tradeType", 2);
                    startActivity(intent);
                    // startActivity(OtcTradeCompleteActivity.class);
                } else if (bean.getStatus() == 0) {//未支付

                  /*  if(orderType==2){
                        Intent intent = new Intent(getAContext(), OtcUserSellPaymentActivity.class);
                        intent.putExtra("data", bean.getId());
                        startActivity(intent);
                    }else */
                    if (orderType == 1) {
                        if (bean.getPayType() == 0) {//未设置支付方式
                            Intent intent = new Intent(getAContext(), OtcUserSellPaymentActivity.class);
                            intent.putExtra("data", bean.getId());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getAContext(), OtcUserSellWaitOtherPayActivity.class);
                            intent.putExtra("orderId", bean.getId() + "");
                            intent.putExtra("type", 0);
                            startActivity(intent);
                            //   payOrder(bean.getId());//未支付的情况下
                        }
                    } else if (orderType == 2) {
//                        ToastUtils.showLongToast("对方尚未付款，请收到付款短信后到待确认查看");
//                        Intent intent = new Intent(getAContext(), OtcUnpaidActivity.class);
//                        intent.putExtra("orderId", c);
//                        startActivity(intent);
                        Intent intent = new Intent(getAContext(), OtcUserSellWaitOtherPayActivity.class);
                        intent.putExtra("orderId", bean.getId() + "");
                        intent.putExtra("type", 1);
                        startActivity(intent);
                    }
                }
            }
        });
        listView.setPullLoadEnable(true);
        listView.setPullToListViewListener(new PullToRefreshListView.PullToListViewListener() {
            @Override
            public void onRefresh() {
                listView.stopLoadMore();
                currentPage = 1;
                get_order();
            }

            @Override
            public void onLoadMore() {
                currentPage++;
                listView.setPullLoadEnable(true);
                listView.stopRefresh();
                get_order();
            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        //释放资源
        AppHelper.release(handler);
        super.onDestroy();
        if (adapter != null) {
            adapter.clearClock();
        }
        EventBus.getDefault().unregister(this);
    }

    boolean isVisibleToUser;

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            currentPage = 1;
            get_order();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            currentPage = 1;
            get_order();
        }
    }

    private PayOrderInfoBean payOrderInfoBean;

    //查询订单信息
    void payOrder(int orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.pay_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                fv(R.id.loading_views).setVisibility(View.GONE);
            }

            @Override
            public void requestSuccess(String result) {
                fv(R.id.loading_views).setVisibility(View.GONE);
                try {
                    payOrderInfoBean = new Gson().fromJson(result, PayOrderInfoBean.class);
                    if (payOrderInfoBean.getCode() == 0) {
                        Intent intent = new Intent(getAContext(), OtcUserSellWaitOtherPayActivity.class);
                        intent.putExtra("orderId", payOrderInfoBean.getData().getOrder().getId() + "");
                        //intent.putExtra("payType", payType);
                        intent.putExtra("complaintPayedTips", payOrderInfoBean.getData().getComplaintPayedTips());
                        intent.putExtra("payType", payOrderInfoBean.getData().getOrder().getPayType());
                        intent.putExtra("totalAmount", payOrderInfoBean.getData().getOrder().getTotalAmount());
                        intent.putExtra("payLimit", payOrderInfoBean.getData().getPayLimit());
                        intent.putExtra("transReferNum", payOrderInfoBean.getData().getOrder().getTransReferNum());
                        intent.putExtra("payBean", payOrderInfoBean.getData().getPayMent());
                        intent.putExtra("type", 1);
                        startActivity(intent);
                    } else {
                        MToast.show(getAContext(), payOrderInfoBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 尝试重新加载
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reload(Event.IMMessage msg) {
        //判断是否刷新
        if (AppUtils.isRefreshOrder(msg.messages, status)) {
            Logger.getInstance().debug(TAG, "reload-status: " + status);
            refresh();
        }
    }

    public void refresh() {
        //TODO 大量测试
        handler.post(new Runnable() {
            @Override
            public void run() {
                get_order();
            }
        });
    }

    void get_order() {
        Map<String, String> params = new HashMap<>();
        params.put("dealType", dealType + "");//1-买 2-卖
        params.put("orderType", orderType + "");//1用户订单 , 2 商户订单
        params.put("status", status + "");//0 未付款，1 已付款，2 已完成，3 用户取消，4 系统撤单，5 申诉中
        params.put("pageNo", currentPage + "");
        params.put("pageSize", "10");
        Log.e("params", "paraaaaams= " + params.toString());
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.get_orders, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                fv(R.id.loading_views).setVisibility(View.GONE);
                SnackBarUtils.ShowRed(getActivity(), errorMsg);
                listView.stopRefresh();
                listView.stopLoadMore();
            }

            @Override
            public void requestSuccess(String result) {
                fv(R.id.loading_views).setVisibility(View.GONE);
                try {
                    if (currentPage == 1) {
                        listView.stopRefresh();
                    } else {
                        listView.stopLoadMore();
                    }
                    otcGetOrderBean = new Gson().fromJson(result, OtcGetOrderBean.class);
                    if (otcGetOrderBean.getCode() == 0) {
                        if (currentPage == 1) {
                            listDataBeans.clear();
                        }
                        listDataBeans.addAll(otcGetOrderBean.getData().getListData());
                        if (currentPage >= otcGetOrderBean.getData().getTotalPage()) {
                            listView.setPullLoadEnable(false);
                        } else {
                            listView.setPullLoadEnable(true);
                        }
                        adapter.notifyDataSetChanged();
                        if (listDataBeans.size() == 0) {
                            nodata_view.setVisibility(View.VISIBLE);
                        } else {
                            nodata_view.setVisibility(View.GONE);
                        }
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
