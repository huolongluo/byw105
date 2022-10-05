package huolongluo.byw.reform.c2c.oct.fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.legend.ui.transfer.AccountTransferActivity;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.c2c.oct.activity.OtcFiltrateActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcOrderManagerActivity;
import huolongluo.byw.reform.c2c.oct.bean.OtcCoinBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
/**
 * Created by Administrator on 2019/3/12 0012.
 */
public class OtcFragmentSell extends BaseFragment implements View.OnClickListener {
    ViewPager viewPager;
    FrameLayout net_error_view;
    Button btn_reLoad;
    SlidingTabLayout tablayout;
    private String[] mTitles;
    private OtcCoinBean otcCoinBean;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_otc_sell;
    }

    Fragment fragment[];
    boolean isMe = false;

    @Override
    protected void onCreatedView(View rootView) {
        viewPager = fv(R.id.viewPager);
        tablayout = fv(R.id.tablayout);
        net_error_view = fv(R.id.net_error_view);
        btn_reLoad = fv(R.id.btn_reLoad);
        btn_reLoad.setOnClickListener(this);
        tablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (!isMe) {
                    isMe = true;
                    viewPager.setCurrentItem(position);
                } else {
                    isMe = false;
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (!isMe) {
                    tablayout.setCurrentTab(position);
                } else {
                    isMe = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewClick(net_error_view, v -> {//网络错误
            get_base_userinfo();
            open_coins();
        });
        open_coins();
    }

    void get_base_userinfo() {
        if (TextUtils.isEmpty(UserInfoManager.getToken())) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.get_base_userinfo, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    OtcUserInfoBean otcUserInfoBean = new Gson().fromJson(result, OtcUserInfoBean.class);
                    otcUserInfoBean.refreshAsset();
                    if (otcUserInfoBean.getCode() == 0) {
                        UserInfoManager.setOtcUserInfoBean(otcUserInfoBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void initTitle() {
        List<OtcCoinBean.DataBean> bean = otcCoinBean.getData();
        if (bean != null && bean.size() > 0) {
            mTitles = new String[bean.size()];
            for (int i = 0; i < bean.size(); i++) {
                mTitles[i] = bean.get(i).getCoinName();
            }
        }
        fragment = new Fragment[mTitles.length];
        for (int i = 0; i < fragment.length; i++) {
            OtcItemSellFragment otcFragment = new OtcItemSellFragment();
            Bundle bundle = new Bundle();
            bundle.putString("coinId", bean.get(i).getCoinId() + "");
            otcFragment.setArguments(bundle);
            fragment[i] = otcFragment;
        }
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), fragment);
        viewPager.setAdapter(adapter);
        tablayout.setViewPager(viewPager, mTitles);
    }

    void open_coins() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        OkhttpManager.postAsync(UrlConstants.open_coins, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                net_error_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void requestSuccess(String result) {
                net_error_view.setVisibility(View.GONE);
                Gson gson = new Gson();
                //JSONObject jsonObject=new JSONObject(result);
                otcCoinBean = gson.fromJson(result, OtcCoinBean.class);
                AppUtils.updateOTCCoinBean(otcCoinBean);
                if (otcCoinBean.getCode() == 0) {
                    initTitle();
                } else {
                    SnackBarUtils.ShowRed(getActivity(), otcCoinBean.getValue());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayout1://划转
                //JIRA:COIN-1721
                //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
                if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                    DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.no_transfer), getString(R.string.as16));
                    return;
                }
                AccountTransferActivity.Companion.launch(getActivity(), null,null,null,null,
                        false,null);
                break;
            case R.id.linearLayout2://订单管理
                startActivity(OtcOrderManagerActivity.class);
                break;
            case R.id.filtrate_iv://筛选
                startActivity(OtcFiltrateActivity.class);
                break;
            case R.id.btn_reLoad://网络错误，从新加载
                open_coins();
                break;
        }
    }

    public static class PageAdapter extends FragmentPagerAdapter {
        Fragment fragment[];

        public PageAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            fragment = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragment[position];
        }

        @Override
        public int getCount() {
            return fragment.length;
        }
    }
}
