package huolongluo.byw.reform.c2c.oct.fragment;
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
import huolongluo.byw.reform.c2c.oct.activity.OtcPaymentActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcPrepaidActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcTradeCompleteActivity;
import huolongluo.byw.reform.c2c.oct.adapter.OtcOrderManItemAdapter;
import huolongluo.byw.reform.c2c.oct.bean.OtcGetOrderBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
/**
 * Created by Administrator on 2019/5/15 0015.
 * 用户订单管理购买界面
 */
public class OtcorderManItemFragment extends BaseFragment {
    private PullToRefreshListView listView;
    private LinearLayout nodata_view;
    private RelativeLayout no_data_tv;
    private int currentPage = 1;
    private OtcGetOrderBean otcGetOrderBean;
    private List<OtcGetOrderBean.DataBean.ListDataBean> listDataBeans = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * @param status 订单状态0 未付款，1 已付款，2 已完成，3 用户取消，4 系统撤单，5 申诉中 6，撤销申诉中
     * @param type 1-买 2-卖
     * @param orderType 1用户订单 , 2 商户订单
     * @return
     */
    public static OtcorderManItemFragment newFragment(int status, int type, int orderType) {
        OtcorderManItemFragment fragment = new OtcorderManItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        bundle.putInt("dealType", type);
        bundle.putInt("orderType", orderType);
        //  Log.e("newFragment", "bound1  = " + bundle.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    public OtcorderManItemFragment() {
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_otcordermanitem;
    }

    OtcOrderManItemAdapter adapter;
    int status;
    int dealType;
    int orderType;

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
        adapter = new OtcOrderManItemAdapter(getAContext(), status, listDataBeans);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OtcGetOrderBean.DataBean.ListDataBean bean = adapter.getItem((int) id);
                //
                if (bean == null) {
                    return;
                }
                if (status == 1) {   //已支付
                    Intent intent = new Intent(getAContext(), OtcPrepaidActivity.class);
                    // Intent intent = new Intent(getAContext(), OtcTradeCompleteActivity.class);
                    intent.putExtra("orderId", bean.getId());
                    startActivity(intent);
                }
                //  else if (bean.getStatus() == 5 || bean.getStatus() == 6)
                else if (status == 5) {  //申诉
                    Intent intent = new Intent(getAContext(), OtcAppealDetailActivity.class);
                    intent.putExtra("orderId", bean.getId());
                    startActivity(intent);
                } else if (bean.getStatus() == 3 || bean.getStatus() == 4) {  //取消
                    Intent intent = new Intent(getAContext(), OtcOrderCancleDetailActivity.class);
                    intent.putExtra("orderId", bean.getId());
                    intent.putExtra("tradeType", 1);
                    startActivity(intent);
                } else if (bean.getStatus() == 2) {//已完成
                    Intent intent = new Intent(getAContext(), OtcTradeCompleteActivity.class);
                    intent.putExtra("orderId", bean.getId());
                    intent.putExtra("tradeType", 1);
                    startActivity(intent);
                    // startActivity(OtcTradeCompleteActivity.class);
                } else if (bean.getStatus() == 0) {//未支付
                    Intent intent = new Intent(getAContext(), OtcPaymentActivity.class);
                    intent.putExtra("data", bean.getId());
                    intent.putExtra("tradeType", 1);
                    intent.putExtra("isOneKey", bean.isOneKey());
                    startActivity(intent);
                }
            }
        });
        listView.setPullLoadEnable(true);
        listView.setPullToListViewListener(new PullToRefreshListView.PullToListViewListener() {
            @Override
            public void onRefresh() {
                listView.stopLoadMore();
                listView.setPullLoadEnable(true);
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
        Log.e("setUserVisibleHintd", "=  " + dealType);
        if (isVisibleToUser) {
            currentPage = 1;
            get_order();
        }
    }

    private PayOrderInfoBean payOrderInfoBean;

    public void refresh() {
        //TODO 大量测试
        handler.post(new Runnable() {
            @Override
            public void run() {
                get_order();
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

    void get_order() {
        Map<String, String> params = new HashMap<>();
        params.put("dealType", dealType + "");//1-买 2-卖
        params.put("orderType", orderType + "");//1用户订单 , 2 商户订单
        params.put("status", status + "");//0 未付款，1 已付款，2 已完成，3 用户取消，4 系统撤单，5 申诉中
        params.put("pageNo", currentPage + "");
        params.put("pageSize", "10");
        //   Log.e("newFragment","bound3  = status: "+status+" dealType  :"+dealType+"   orderType : "+orderType);
        Log.e("params", "paraaaaams= " + params.toString());
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.get_orders, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(getActivity(), errorMsg);
                listView.stopRefresh();
                listView.stopLoadMore();
                fv(R.id.loading_views).setVisibility(View.GONE);
            }

            @Override
            public void requestSuccess(String result) {
                fv(R.id.loading_views).setVisibility(View.GONE);
                listView.stopRefresh();
                listView.stopLoadMore();
                try {
                    otcGetOrderBean = new Gson().fromJson(result, OtcGetOrderBean.class);
                    if (otcGetOrderBean.getCode() == 0) {
                        if (currentPage == 1) {
                            listView.stopRefresh();
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

    @Override
    public void onDestroy() {
        //释放资源
        AppHelper.release(handler);
        super.onDestroy();
        if (adapter != null) {
            adapter.release();
        }
        EventBus.getDefault().unregister(this);
    }
}
