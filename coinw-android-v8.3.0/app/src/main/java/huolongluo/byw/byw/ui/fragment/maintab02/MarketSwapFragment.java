package huolongluo.byw.byw.ui.fragment.maintab02;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.legend.modular_contract_sdk.coinw.CoinwHyMarketTab;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.DeviceUtils;
/**
 * zh行情合约的主fragment
 */
public class MarketSwapFragment extends BaseFragment implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener,View.OnClickListener{
    @BindView(R.id.tl_market_swap)
    TabLayout tabLayout;
    @BindView(R.id.vp_market_swap)
    ViewPager viewPager;
    private MarketSwapItemFragment[] items;

    @Override
    protected void initDagger() {
    }

    @Override
    protected void initViewsAndEvents(View rootView) {
        initSwapData();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fgt_market_swap;
    }

    private void initSwapData() {
        initTabAndItem();

        MarketSwapAdapter marketAdapter = new MarketSwapAdapter(getChildFragmentManager(), items);
        viewPager.setAdapter(marketAdapter);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(100);
    }

    private void initTabAndItem() {
        ArrayList<CoinwHyMarketTab> listTab= CoinwHyUtils.getMarketTabs(requireContext());
        if(listTab==null||listTab.size()==0) return;
        items=new MarketSwapItemFragment[listTab.size()];
        for (int i = 0; i < listTab.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setTag(listTab.get(i).getTag());
            View view = View.inflate(getActivity(), R.layout.market_tab_item, null);
            TextView titleTxt = view.findViewById(R.id.tv_cnyt1);
            ImageView iv = view.findViewById(R.id.iv);
            RelativeLayout linearl = view.findViewById(R.id.linearl);
            iv.setVisibility(View.GONE);
            linearl.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, DeviceUtils.dip2px(getActivity(), 29)));
            titleTxt.setText(listTab.get(i).getTabName());
            tab.setCustomView(view);
            tabLayout.addTab(tab);
            items[i]=MarketSwapItemFragment.newInstance(listTab.get(i).getTag());
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //TODO 待完成
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        tabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(viewPager==null){
            return;
        }
        if(isVisibleToUser){
            for (int i = 0; i <items.length ; i++) {
                if(viewPager.getCurrentItem()==i){
                    items[i].setUserVisibleHint(true);
                }else{
                    items[i].setUserVisibleHint(false);
                }
            }
        }else{
            for (int i = 0; i <items.length ; i++) {
                items[i].setUserVisibleHint(false);
            }
        }

    }
}
