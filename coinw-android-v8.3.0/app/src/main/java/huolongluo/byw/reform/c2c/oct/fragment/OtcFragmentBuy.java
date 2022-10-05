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
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.oneClickBuy.C2cStatus;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.c2c.oct.activity.OtcFiltrateActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcOrderManagerActivity;
import huolongluo.byw.reform.c2c.oct.bean.OtcCoinBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
/**
 * Created by Administrator on 2019/3/12 0012.
 */
public class OtcFragmentBuy extends BaseFragment implements View.OnClickListener {
    ViewPager viewPager;
    FrameLayout net_error_view;
    Button btn_reLoad;
    SlidingTabLayout tablayout;
    private String[] mTitles;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private OtcCoinBean otcCoinBean;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_otc_buy;
    }

    Fragment fragment[];
    boolean isMe = false;
    private PopupWindow popupWindow;

    @Override
    protected void onCreatedView(View rootView) {
        if(CoinwHyUtils.isServiceStop){
            return;
        }

        viewPager = fv(R.id.viewPager);
        tablayout = fv(R.id.tablayout);
        net_error_view = fv(R.id.net_error_view);
        btn_reLoad = fv(R.id.btn_reLoad);
        btn_reLoad.setOnClickListener(this);
        viewClick(net_error_view, v -> {//网络错误
            get_base_userinfo();
            open_coins();
        });
        rootView.findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOneKey();
            }
        });


     /*   for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }
        tablayout.setTabData(mTabEntities);*/
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
        //TODO 放到异步处理
        //根据缓存数据进行初始化
        String oneKeyData = SPUtils.getString(getActivity(), AppConstants.LOCAL.KEY_LOCAL_ONE_KEY, "");
        if (!TextUtils.isEmpty(oneKeyData)) {
            OtcCoinBean bean = GsonUtil.json2Obj(oneKeyData, OtcCoinBean.class);
            if (bean != null) {
                C2cStatus.oneClickCoinsId = bean.getData();
            }
        }
        open_coins();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        loadOneKey();
    }

    private void loadOneKey() {

        Logger.getInstance().debug(TAG, "loadOneKey", new Exception());
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        OkhttpManager.postAsync(UrlConstants.ACTION_ONE_KEY, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                Logger.getInstance().debug(TAG, "loadOneKey", e);
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, "loadOneKey-result: " + result);
                try {
                    OtcCoinBean bean = GsonUtil.json2Obj(result, OtcCoinBean.class);
                    if (bean != null) {
                        SPUtils.saveString(getActivity(), AppConstants.LOCAL.KEY_LOCAL_ONE_KEY, result);
                        C2cStatus.oneClickCoinsId = bean.getData();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    void initTitle() {
        List<OtcCoinBean.DataBean> bean = otcCoinBean.getData();
//        C2cStatus.oneClickCoinsId = bean;
        EventBus.getDefault().post(new Event.UPTitle());
        if (bean != null) {
        }
        if (bean != null && bean.size() > 0) {
            mTitles = new String[bean.size()];
            for (int i = 0; i < bean.size(); i++) {
                mTitles[i] = bean.get(i).getCoinName();
            }
        }
        fragment = new Fragment[mTitles.length];
        for (int i = 0; i < fragment.length; i++) {
            OtcItemBuyFragment otcFragment = new OtcItemBuyFragment();
            Bundle bundle = new Bundle();
            bundle.putString("coinId", bean.get(i).getCoinId() + "");
            otcFragment.setArguments(bundle);
            fragment[i] = otcFragment;
        }
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), fragment);
        viewPager.setAdapter(adapter);
        tablayout.setViewPager(viewPager, mTitles);
    }

    void advertisements() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
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

    void open_coins() {
        //加载一键买卖币支持的类型
        loadOneKey();
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
