package huolongluo.byw.reform.c2c.oct.activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcodes.utils.ToastUtils;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.databinding.ActivitySellinfoBinding;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.adapter.SellInfoAdapter;
import huolongluo.byw.reform.c2c.oct.bean.AdvertisementBean;
import huolongluo.byw.reform.c2c.oct.bean.DeleteBean;
import huolongluo.byw.reform.c2c.oct.bean.GetOrgInfoBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;
/**
 * Created by dell on 2019/7/17.
 */
public class SellerInfoActivity extends BaseActivity {
    private static final int SELL = 0, BUY = 1;
    private ActivitySellinfoBinding mBinding;
    String userId;
    GetOrgInfoBean bean;
    private List<AdvertisementBean.DataBean.SellBean> list = new ArrayList<>();
    private List<AdvertisementBean.DataBean.SellBean> buyList = new ArrayList<>();
    private List<AdvertisementBean.DataBean.SellBean> sellList = new ArrayList<>();
    private SellInfoAdapter adapter;
    private View view1;
    private View view2;
    private ArrayList<View> viewList;
    private SellInfoAdapter buyAdapter;
    private SellInfoAdapter sellAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sellinfo);
        mBinding.include.titleTv.setText(R.string.qa27);
        if (getIntent() != null) {
            userId = getIntent().getStringExtra("userId");
        }
        mBinding.include.backIv.setOnClickListener(v -> {
            finish();
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list.add(new AdvertisementBean.DataBean.SellBean(1));
        // list.add(new AdvertisementBean.DataBean.SellBean(2));
        list.add(new AdvertisementBean.DataBean.SellBean(1));
        //  list.add(new AdvertisementBean.DataBean.SellBean(2));
        adapter = new SellInfoAdapter(list);
        mBinding.recyclerView.setAdapter(adapter);
        mBinding.tablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mBinding.vp.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
//        mBinding.scrollView.scrollTo(0, 0);
        getOrgInfo();
        advertisementList();
        setVp();
    }

    private void setVp() {
        ViewPager vp = findViewById(R.id.vp);
        view1 = View.inflate(this, R.layout.sell_buy_list, null);
        view2 = View.inflate(this, R.layout.sell_buy_list, null);
        viewList = new ArrayList<>();
        viewList.add(view2);
        viewList.add(view1);
        String[] title = {getString(R.string.qa23), getString(R.string.qa24)};
        vp.setAdapter(pagerAdapter);
        mBinding.tablayout.setViewPager(vp, title);
        RecyclerView bugRecyclerView = view1.findViewById(R.id.rcList);
        bugRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        buyAdapter = new SellInfoAdapter(buyList);
        bugRecyclerView.setAdapter(buyAdapter);
        buyAdapter.setOnItemClickListener(new SellInfoAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, AdvertisementBean.DataBean.SellBean bean) {
                sendOrderStatue(position, BUY, bean);
            }

            @Override
            public void onDeleteClick(String id) {
                deleteAdw(id);
            }

            @Override
            public void editAdw(int id) {
                editValidate(id);
            }
        });
        RecyclerView sellRecyclerView = view2.findViewById(R.id.rcList);
        sellRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        sellAdapter = new SellInfoAdapter(sellList);
        sellRecyclerView.setAdapter(sellAdapter);
        sellAdapter.setOnItemClickListener(new SellInfoAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, AdvertisementBean.DataBean.SellBean bean) {
                sendOrderStatue(position, SELL, bean);
            }

            @Override
            public void onDeleteClick(String id) {
                deleteAdw(id);
            }

            @Override
            public void editAdw(int id) {
                editValidate(id);
            }
        });
        swipeRefreshLayout = findViewById(R.id.swipe_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        swipeRefreshLayout.setOnRefreshListener(this::advertisementList);
        Button bt_sell_putaway = view1.findViewById(R.id.putaway);
        Button bt_buy_putaway = view2.findViewById(R.id.putaway);
        //判断是否登录
        OtcUserInfoBean ouib = UserInfoManager.getOtcUserInfoBean();
        UserInfoBean uib = UserInfoManager.getUserInfo();
        //判断是否为自己的广告
        if (!UserInfoManager.isLogin() || ouib == null || ouib.getData() == null || !ouib.getData().isMerch() || uib == null || !TextUtils.equals(userId, uib.getFid() + "")) {
            //未登录或者不是商户
            bt_sell_putaway.setVisibility(View.GONE);
            bt_buy_putaway.setVisibility(View.GONE);
        } else if (ouib.getData().isMerch()) {
            bt_sell_putaway.setVisibility(View.VISIBLE);
            bt_buy_putaway.setVisibility(View.VISIBLE);
        }
        bt_sell_putaway.setOnClickListener(v -> {
            toWebView(UrlConstants.getOtcAdPush());
        });
        bt_buy_putaway.setOnClickListener(v -> {
            toWebView(UrlConstants.getOtcAdPush());
        });
    }

    private void toWebView(String push) {
        Intent intent = new Intent(SellerInfoActivity.this, NewsWebviewActivity.class);
        intent.putExtra("url", push);
        intent.putExtra("token", UserInfoManager.getToken());
        intent.putExtra("useH5Title", true);
        startActivity(intent);
    }

    private void sendOrderStatue(int position, int type, AdvertisementBean.DataBean.SellBean bean) {
        Map<String, String> params = new HashMap<>();
        params.put("adId", String.valueOf(bean.getId()));
        params.put("loginToken", UserInfoManager.getToken());
//        params = OkhttpManager.encrypt(params);
        DialogManager.INSTANCE.showProgressDialog(this);
        String url = (bean.getStatus() == 0 || bean.getStatus() == 2) ? UrlConstants.unshelve : UrlConstants.shelve;
        OkhttpManager.postAsync(url, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
//                SnackBarUtils.ShowRed(SellerInfoActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
                ToastUtils.showShortToast(errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                GetOrgInfoBean statue = new Gson().fromJson(result, (Type) GetOrgInfoBean.class);
                if (statue != null && statue.getCode() == 0) {
//                    SnackBarUtils.ShowRed(SellerInfoActivity.this, statue.getValue());
                    ToastUtils.showShortToast(statue.getValue());
                    //局部刷新数据，不需要改变整体顺序
//                    if (type == BUY) {
//                        buyList.get(position).setStatus((bean.getStatus() == 0 || bean.getStatus() == 2) ? 4 : 0);
//                        buyAdapter.notifyDataSetChanged();
//                    } else {
//                        sellList.get(position).setStatus((bean.getStatus() == 0 || bean.getStatus() == 2) ? 4 : 0);
//                        sellAdapter.notifyDataSetChanged();
//                    }
                    advertisementList();//点击单个页面，单条item 刷新所有数据不知道怎么想的
                } else if (statue != null && statue.getCode() == -1) {
//                    SnackBarUtils.ShowRed(SellerInfoActivity.this, statue.getValue());
                    if (statue.getData() != null && statue.getData().getCode() == -102) {
                        DialogUtils.getInstance().showTwoButtonDialog(SellerInfoActivity.this, statue.getValue(), getString(R.string.qa25), getString(R.string.qa26));
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
                                startActivity(new Intent(SellerInfoActivity.this, PaymentAccountActivityNew.class));
                            }
                        });
                    } else {
                        ToastUtils.showShortToast(statue.getValue());
                    }
                }
            }
        });
    }

    void getOrgInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("orgId", userId);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.getOrgInfo, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(SellerInfoActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                bean = new Gson().fromJson(result, GetOrgInfoBean.class);
                if (bean != null && bean.getCode() == 0) {
                    setData();
                }
            }
        });
    }

    /**
     * 是否可以编辑
     * @param adwId
     */
    void editValidate(int adwId) {
        Map<String, String> params = new HashMap<>();
        params.put("adId", String.valueOf(adwId));
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.EDIT_VALIDATE, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(SellerInfoActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                bean = new Gson().fromJson(result, GetOrgInfoBean.class);
                if (bean != null && bean.getCode() == 0) {
                    toWebView(String.format(UrlConstants.getOtcAdEdit(), adwId));
                } else {
                    ToastUtils.showShortToast( "该广告不能被编辑");
                }
            }
        });
    }

    /**
     * 删除广告
     */
    void deleteAdw(String adwId) {
        DialogUtils.getInstance().showConfirmDialog(this, getString(R.string.str_confirm_delete_ad), getString(R.string.cancle), getString(R.string.confirm), new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
                //用户已取消
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
                doDeleteAdw(adwId);
            }
        });
    }

    private void doDeleteAdw(String adwId) {
        Map<String, String> params = new HashMap<>();
        params.put("adId", adwId);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.ADVERTISEMENT_DELETE, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(SellerInfoActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                DeleteBean deleteBean = new Gson().fromJson(result, DeleteBean.class);
                if (bean != null) {
                    if (bean.getCode() == 0) {
                        advertisementList();
                    }
                    ToastUtils.showShortToast(  deleteBean.getValue());
                } else {
                    ToastUtils.showShortToast(  "解析失败");
                }
            }
        });
    }

    void setData() {
        if (bean.getData() != null) {
            mBinding.nameTv.setText(bean.getData().getNickname() + "");
            mBinding.registTimeTv.setText(bean.getData().getPlatformRegisterTime() + "");
            mBinding.totalTv.setText(bean.getData().getTotalOrder() + "");
            mBinding.total30Tv.setText(bean.getData().getThirtyDaysOrder() + "");
            mBinding.speedTv.setText(bean.getData().getAvgDealTime() + "");
            mBinding.finishRateTv.setText(bean.getData().getTotalOrderCompPercentage() + "");
            mBinding.head.setText(bean.getData().getNickname().substring(0, 1));
            if (bean.getData().isEmailAuth()) {
                mBinding.bingEmailIv.setImageResource(R.mipmap.cheched);
            } else {
                mBinding.bingEmailIv.setImageResource(R.mipmap.weirz);
            }
            if (bean.getData().isPhoneAuth()) {
                mBinding.bingphoneIv.setImageResource(R.mipmap.cheched);
            } else {
                mBinding.bingphoneIv.setImageResource(R.mipmap.weirz);
            }
            if (bean.getData().isRealNameAuth()) {
                mBinding.shiming1Iv.setImageResource(R.mipmap.cheched);
            } else {
                mBinding.shiming1Iv.setImageResource(R.mipmap.weirz);
            }
            if (bean.getData().isHighGradeAuth()) {
                mBinding.shiming2Iv.setImageResource(R.mipmap.cheched);
            } else {
                mBinding.shiming2Iv.setImageResource(R.mipmap.weirz);
            }
            if (bean.getData().isIsAuthMerchant()) {
                mBinding.isVipIv.setVisibility(View.VISIBLE);
            } else {
                mBinding.isVipIv.setVisibility(View.INVISIBLE);
            }
        }
    }

    void advertisementList() {
        Map<String, String> params = new HashMap<>();
        params.put("orgId", userId);
        if (!TextUtils.isEmpty(UserInfoManager.getToken())) {
            params.put("loginToken", UserInfoManager.getToken());
        }
//        params = OkhttpManager.encrypt(params);
        //   params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.advertisement_list, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(SellerInfoActivity.this, errorMsg);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    AdvertisementBean bean = new Gson().fromJson(result, AdvertisementBean.class);
                    if (bean != null && bean.getCode() == 0) {
                        list.clear();
                        buyList.clear();
                        sellList.clear();
                        list.add(new AdvertisementBean.DataBean.SellBean(1));
                        if (bean.getData() == null || bean.getData().getSell() == null || bean.getData().getSell().size() == 0) {
                            list.add(new AdvertisementBean.DataBean.SellBean(2));
                            sellList.add(new AdvertisementBean.DataBean.SellBean(2));
                        } else {
                            list.addAll(bean.getData().getSell());
                            sellList.addAll(bean.getData().getSell());
                        }
                        list.add(new AdvertisementBean.DataBean.SellBean(1));
                        if (bean.getData() == null || bean.getData().getBuy() == null || bean.getData().getBuy().size() == 0) {
                            list.add(new AdvertisementBean.DataBean.SellBean(2));
                            buyList.add(new AdvertisementBean.DataBean.SellBean(2));
                        } else {
                            list.addAll(bean.getData().getBuy());
                            buyList.addAll(bean.getData().getBuy());
                        }
                        adapter.notifyDataSetChanged();
                        sellAdapter.notifyDataSetChanged();
                        buyAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        advertisementList();
    }
}
