package huolongluo.byw.heyue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.coinw.biz.trade.TradeETFFragment;
import com.android.coinw.biz.trade.TradeFragment;
import com.legend.modular_contract_sdk.api.ModularContractSDK;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;
import com.legend.modular_contract_sdk.ui.experience_gold.ExperienceGoldActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.contractTab.ContractUserInfoEntity;
import huolongluo.byw.databinding.FragmentTransactionHomeBinding;
import huolongluo.byw.heyue.HeYueUtil;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.c2c.oct.fragment.OtcActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.view.NoScrollViewPager;
import okhttp3.Request;

/**
 * 项目名称：my_byw
 * 包名：huolongluo.byw.reform.trade.fragment
 * 开发者：Long
 * 电话：15375447216
 * 邮箱：127124zhao@gmail.com
 * 日期：2019-11-14
 */
public class TransActionHomeFragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    public static final int TYPE_BB = 0;
    public static final int TYPE_ETF = 1;
    NoScrollViewPager transationVp;
    private List<String> title = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private TradeFragment tradeFragment;
//    private TradeETFFragment tradeETFFragment;
    private TextView transferBBTv;
    private ImageView ivExperienceGold;
    private TextView transferETFTv;
    private LinearLayout bbTabLl;
    private ConstraintLayout etfTabLl;
    private LinearLayout otcTabLl;
    TextView freeze;
    private int type = 0;
    private TransactionVpAdapter transactionVpAdapter;

    private FragmentTransactionHomeBinding mBinding;

    //此方法目的是币币后台不能返回合约ID的处理方法
    public void setTypeAndPosition(String contractName, int type, int position) {
        this.type = type;
    }

    //此方法是可以拿到合约ID
    public void setTypeAndPosition(int contractId, int type, int position) {
        this.type = type;
    }

    public boolean isOpenETF() {
        if (transationVp == null) {
            return false;
        }
        return transationVp.getCurrentItem() == TYPE_ETF;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTransactionHomeBinding.inflate(inflater, container, false);
        mBinding.setPageIndex(0);
        rootView = mBinding.getRoot();
        onCreatedView(rootView);
        return rootView;
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_transaction_home;
    }

    @Override
    protected void onCreatedView(View rootView) {
        bbTabLl = rootView.findViewById(R.id.transfer_bb_trans_ll);
        transferBBTv = rootView.findViewById(R.id.transfer_bb_trans_tv);
        ivExperienceGold = rootView.findViewById(R.id.iv_experience_gold);
        //ETF
        etfTabLl = rootView.findViewById(R.id.transfer_bb_etf_ll);
        transferETFTv = rootView.findViewById(R.id.transfer_bb_etf_tv);
        otcTabLl = rootView.findViewById(R.id.transfer_otc);
        //
        freeze = rootView.findViewById(R.id.freeze);
        bbTabLl.setOnClickListener(this);
        etfTabLl.setOnClickListener(this);
        otcTabLl.setOnClickListener(this);
        ivExperienceGold.setOnClickListener(this);
        transationVp = rootView.findViewById(R.id.transation_vp);
        //通过反射，动态设置tablayout底部下划线宽度
//        TabUiUtil.setTabWidth(transationTablayout,3);
        //设置tablayout
        title.add(getResources().getString(R.string.bb_tab));
//        title.add(getResources().getString(R.string.etf_tab));
        tradeFragment = new TradeFragment();
//        tradeETFFragment = new TradeETFFragment();
        fragmentList.add(tradeFragment);
//        fragmentList.add(tradeETFFragment);
        transactionVpAdapter = new TransactionVpAdapter(getChildFragmentManager(), fragmentList, title);
        //设置viewPager
        transationVp.setScroll(false);
        transationVp.setOffscreenPageLimit(2);
        transationVp.setAdapter(transactionVpAdapter);
        transationVp.addOnPageChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.getInstance().debug(TAG, "onResume");
        if (transationVp != null) {
            if (transationVp.getCurrentItem() == TYPE_BB) {
                tradeFragment.setUserVisibleHint(true);
            }
//            else if (transationVp.getCurrentItem() == TYPE_ETF) {
//                tradeETFFragment.setUserVisibleHint(true);
//            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Logger.getInstance().debug(TAG, "isVisibleToUser: " + isVisibleToUser);
        if (isVisibleToUser) {
            if (type == TYPE_BB) {
                selectTab(TYPE_BB);
            } else if (type == TYPE_ETF) {
                selectTab(TYPE_ETF);
            }
        }
        if (tradeFragment == null) {
            return;
        }
        if (transationVp != null) {
            if (transationVp.getCurrentItem() == TYPE_BB) {
                tradeFragment.setUserVisibleHint(isVisibleToUser);
            }
//            else if (transationVp.getCurrentItem() == TYPE_ETF) {
//                tradeETFFragment.setUserVisibleHint(isVisibleToUser);
//            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transfer_bb_trans_ll:
                if (CoinwHyUtils.checkIsStopService(getActivity())) {
                    return;
                }
                selectTab(TYPE_BB);
                //切换成中文
//                LogicLanguage.changeZhOrEn(getActivity(),true);
//                SwapSDK.INSTANCE.changeLanguage(Locale.SIMPLIFIED_CHINESE); //语言切换
                break;
            case R.id.transfer_bb_etf_ll:
                if (CoinwHyUtils.checkIsStopService(getActivity())) {
                    return;
                }
                selectTab(TYPE_ETF);
                break;
            case R.id.transfer_otc:
                if (UserInfoManager.isLogin()){
                    OtcActivity.launch(getActivity());
                } else {
                    startActivity(LoginActivity.class);
                }
                break;
            case R.id.iv_experience_gold:
                if (CoinwHyUtils.isToOtherPage()) {
                    return;
                }
                ExperienceGoldActivity.Companion.launch(getActivity());
                break;
        }
    }

    public void updateExchangeRate(String pricingUnit, String exchangeRate) {
//        if (contractFragment != null) {
//            contractFragment.updateExchangeRate(pricingUnit, exchangeRate);
//        }
    }

    public void selectTab(int index) {
        type = index;
//        03-27 11:46:09.565 10700 28329 28329 E AndroidRuntime: FATAL EXCEPTION: main
//        03-27 11:46:09.565 10700 28329 28329 E AndroidRuntime: Process: huolongluo.byw, PID: 28329
//        03-27 11:46:09.565 10700 28329 28329 E AndroidRuntime: java.lang.NullPointerException: Attempt to read from field 'androidx.drawerlayout.widget.DrawerLayout huolongluo.byw.byw.ui.activity.main.MainActivity.h' on a null object reference
//        03-27 11:46:09.565 10700 28329 28329 E AndroidRuntime: 	at j.a.g.d.b.selectTab(TransActionHomeFragment.java:1)
        if (MainActivity.self == null || null == MainActivity.self.drawer_layout) {
            return;
        }
        if (index == TYPE_BB) {
            ivExperienceGold.setVisibility(View.GONE);
//            TradeDataHelper.getInstance().setETFCoin(false);
            if (CoinwHyUtils.isServiceStop) {
                return;
            }
            //允许手势滑动
//            MainActivity.self.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            transationVp.setCurrentItem(TYPE_BB);
        } else if (index == TYPE_ETF) {
            ivExperienceGold.setVisibility(View.GONE);
            if (CoinwHyUtils.isServiceStop) {
                return;
            }
            MainActivity.self.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            transationVp.setCurrentItem(TYPE_ETF);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        selectTab(position);
        mBinding.setPageIndex(position);
        freeze.setVisibility(View.GONE);//只有合约才会有冻结功能
        if (!CoinwHyUtils.isServiceStop) {//停服使用停服的逻辑，这是正常的逻辑在停服时需要屏蔽
            //判断是否是合约
//            if (position == TYPE_HY) {
//                if (UserInfoManager.isLogin()) {
//                    getContractUserInfo();
//                }
//            }
        }

    }

    private void getContractUserInfo() {
        if (TextUtils.isEmpty(UserInfoManager.getToken())) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.GET_HY_USER, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().error(errorMsg, e);
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("CONTRACT_TYPES", result);
                ContractUserInfoEntity contractUserInfoEntity = GsonUtil.json2Obj(result, ContractUserInfoEntity.class);
                if (null != contractUserInfoEntity && null != contractUserInfoEntity.getData()) {
                    ContractUserInfoEntity.DataBean data = contractUserInfoEntity.getData();
                    freeze.setVisibility(View.GONE);
                    if (data.getStatus() == 0) {//未开通
                        HeYueUtil.getInstance().openHY();
                    } else if (data.getStatus() == 1) {//已开通
                        ModularContractSDK.INSTANCE.login(data.getToken());
                    } else if (data.getStatus() == 2) {//冻结
                        freeze.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public static class TransactionVpAdapter extends FragmentPagerAdapter {
        private List<Fragment> listFragment;
        private List<String> listTitle;

        public TransactionVpAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
            super(fm);
            this.listFragment = fragmentList;
            this.listTitle = titleList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return listTitle.get(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == this.requestCode && data != null) {
//            int sid = data.getIntExtra("id", -1);
//            String coinName = data.getStringExtra("coinName");
//            String cnyName = data.getStringExtra("cnyName");
//            int selfselection = data.getIntExtra("selfselection", 0);
//            if (sid > -1) {
//                BizEvent.Trade.CoinEvent coinEvent = new BizEvent.Trade.CoinEvent(sid + "", coinName, cnyName, selfselection);
//                onChangeCoin(coinEvent);
//            }
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void applyTheme() {
        tradeFragment.applyTheme();
//        tradeETFFragment.applyTheme();
    }
}
