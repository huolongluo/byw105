package huolongluo.byw.byw.ui.fragment.maintab01;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
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

import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab02.MarketETFFragment;
import huolongluo.byw.byw.ui.fragment.maintab02.MarketSwapFragment;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.c2c.oct.bean.Text;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Util;
import huolongluo.byw.view.ViewPagerSlide;
/**
 * zh行情主Fragment，在MainActivity下
 */
public class MarketHomeFragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    public static final int TAB_SELF=0;
    public static final int TAB_BB=2;
    public static final int TAB_CONTRACT=3;
    public static final int TAB_ETF=4;
    public static final int TAB_PLATE=1;
    private ViewPagerSlide transationVp;
    private List<String> title = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private AllMarketFragment marketSelfFragment;
    private MarketPlateFragment marketPlateFragment;
    private MarketNewFragment marketFragment;
    private MarketSwapFragment marketSwapFragment;
    private MarketETFFragment marketETFFragment;
    private TextView transferBBTv, transferSwapTv;
    private TextView transferETFTv,tvSelf,tvPlate;
    private LinearLayout bbTabLl;
    private ConstraintLayout swapTabLl, etfTabLl;
    private TransactionVpAdapter transactionVpAdapter;
    private LinearLayout searchLayout;
    private boolean init = false;
    private int lastClickPosition=-1;
    private int currentClickPosition=1;
    private boolean isClick=true;
    private boolean isVisibleToUser=false;

    @Override
    protected int getRootViewResId() {
        return R.layout.fgt_market_home;
    }

    @Override
    protected void onCreatedView(View rootView) {
        bbTabLl = rootView.findViewById(R.id.transfer_bb_trans_ll);
        transferBBTv = rootView.findViewById(R.id.transfer_bb_trans_tv);
        swapTabLl = rootView.findViewById(R.id.transfer_bb_swap_ll);
        transferSwapTv = rootView.findViewById(R.id.tv_swap);
        etfTabLl = rootView.findViewById(R.id.transfer_bb_etf_ll);
        transferETFTv = rootView.findViewById(R.id.tv_etf);
        tvSelf = rootView.findViewById(R.id.tvSelf);
        tvPlate = rootView.findViewById(R.id.tvPlate);
        bbTabLl.setOnClickListener(this);
        swapTabLl.setOnClickListener(this);
        etfTabLl.setOnClickListener(this);
        tvSelf.setOnClickListener(this);
        tvPlate.setOnClickListener(this);
        transationVp = rootView.findViewById(R.id.transation_vp);
        title.add(getResources().getString(R.string.dd14));
        title.add(getResources().getString(R.string.plate));
        title.add(getResources().getString(R.string.bb_tab));
        title.add(getResources().getString(R.string.swap));
        title.add(getResources().getString(R.string.etf_tab));
        marketSelfFragment=new AllMarketFragment();
        marketSelfFragment.mType="300";
        MarketDataPresent.getSelf().setDataCallback(marketSelfFragment);
        marketFragment = new MarketNewFragment();
        marketSwapFragment = new MarketSwapFragment();
        marketETFFragment = new MarketETFFragment();
        marketPlateFragment=new MarketPlateFragment();
        fragmentList.add(marketSelfFragment);
        fragmentList.add(marketPlateFragment);
        fragmentList.add(marketFragment);
        fragmentList.add(marketSwapFragment);
        fragmentList.add(marketETFFragment);
        transactionVpAdapter = new TransactionVpAdapter(getChildFragmentManager(), fragmentList, title);
        //设置viewPager
        transationVp.setOffscreenPageLimit(5);
        transationVp.setAdapter(transactionVpAdapter);
        transationVp.addOnPageChangeListener(this);
        searchLayout = rootView.findViewById(R.id.ll_search);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marketFragment == null || marketFragment.ll_search == null) {
                    return;
                }
                marketFragment.ll_search.performClick();
            }
        });

        if(CoinwHyUtils.isServiceStop){
            refreshPosition(TAB_CONTRACT);
        }
        selectTab();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.init = true;
    }

    public void setDisableStopService() {
        if (transationVp != null) {
            transationVp.setNoScroll(true);
            transationVp.setCurrentItem(TAB_CONTRACT);
        }
    }
    private void setUserVisible(boolean isVisible){
        if (transationVp == null) {
            return;
        }
        try {
            switch (transationVp.getCurrentItem()){
                case TAB_SELF:
                    marketSelfFragment.setUserVisibleHint(isVisible);
                    break;
                case TAB_PLATE:
                    marketPlateFragment.setUserVisibleHint(isVisible);
                    break;
                case TAB_BB:
                    marketFragment.setUserVisibleHint(isVisible);
                    break;
                case TAB_CONTRACT:
                    marketSwapFragment.setUserVisibleHint(isVisible);
                    break;
                case TAB_ETF:
                    marketETFFragment.setUserVisibleHint(isVisible);
                    break;
            }
        }catch (Throwable t){
            t.printStackTrace();
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        if(isVisibleToUser){
            setUserVisible(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setUserVisible(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser=isVisibleToUser;
        if (isVisibleToUser) {
            if (!init) {
                init = true;
            }
        }
        setUserVisible(isVisibleToUser);
    }
    private void refreshLevel1TabUi(){
        switch (lastClickPosition){
            case TAB_SELF:
                setNormal(tvSelf);
                break;
            case TAB_BB:
                setNormal(transferBBTv);
                break;
            case TAB_CONTRACT:
                setNormal(transferSwapTv);
                break;
            case TAB_ETF:
                setNormal(transferETFTv);
                break;
            case TAB_PLATE:
                setNormal(tvPlate);
                break;
        }
        switch (currentClickPosition){
            case TAB_SELF:
                setSelected(tvSelf);
                break;
            case TAB_BB:
                setSelected(transferBBTv);
                break;
            case TAB_CONTRACT:
                setSelected(transferSwapTv);
                break;
            case TAB_ETF:
                setSelected(transferETFTv);
                break;
            case TAB_PLATE:
                setSelected(tvPlate);
                break;
        }
    }
    private void setSelected(TextView tv){
        tv.setSelected(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
    }
    private void setNormal(TextView tv){
        tv.setSelected(false);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSelf:
                if(currentClickPosition==TAB_SELF) return;
                isClick=true;
                refreshPosition(TAB_SELF);
                selectTab();
                break;
            case R.id.transfer_bb_trans_ll:
                if(currentClickPosition==TAB_BB) return;
                isClick=true;
                refreshPosition(TAB_BB);
                selectTab();
                break;
            case R.id.transfer_bb_swap_ll:
                if(currentClickPosition==TAB_CONTRACT) return;
                isClick=true;
                refreshPosition(TAB_CONTRACT);
                selectTab();
                break;
            case R.id.transfer_bb_etf_ll:
                if(currentClickPosition==TAB_ETF) return;
                isClick=true;
                refreshPosition(TAB_ETF);
                selectTab();
                break;
            case R.id.tvPlate:
                if(currentClickPosition==TAB_PLATE) return;
                isClick=true;
                refreshPosition(TAB_PLATE);
                selectTab();
                break;
        }
    }
    private void refreshPosition(int position){
        lastClickPosition=currentClickPosition;
        currentClickPosition=position;
    }
    private void selectTab() {
        if (MainActivity.self == null) {
            return;
        }
        if (null == MainActivity.self.drawer_layout) {
            return;
        }
        switch (currentClickPosition){
            case TAB_SELF:
                if (CoinwHyUtils.checkIsStopService(getActivity())) {
                    return;
                }
                searchLayout.setVisibility(View.GONE);
                transationVp.setCurrentItem(TAB_SELF);
                break;
            case TAB_BB:
                if (CoinwHyUtils.checkIsStopService(getActivity())) {
                    return;
                }
                searchLayout.setVisibility(View.VISIBLE);
                transationVp.setCurrentItem(TAB_BB);
                break;
            case TAB_CONTRACT:
                searchLayout.setVisibility(View.GONE);
                // 禁止手势滑动
                MainActivity.self.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                transationVp.setCurrentItem(TAB_CONTRACT);
                break;
            case TAB_ETF:
                if (CoinwHyUtils.checkIsStopService(getActivity())) {
                    return;
                }
                searchLayout.setVisibility(View.GONE);
                // 禁止手势滑动
                MainActivity.self.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                transationVp.setCurrentItem(TAB_ETF);
                break;
            case TAB_PLATE:
                if (CoinwHyUtils.checkIsStopService(getActivity())) {
                    return;
                }
                searchLayout.setVisibility(View.GONE);
                transationVp.setCurrentItem(TAB_PLATE);
                break;
        }
        refreshLevel1TabUi();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(isClick){
            isClick=false;
            return;
        }
        refreshPosition(position);
        selectTab();
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
            if (listTitle == null) {
                return "";
            }
            return listTitle.get(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
