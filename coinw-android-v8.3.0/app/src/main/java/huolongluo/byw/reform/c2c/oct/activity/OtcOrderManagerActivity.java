package huolongluo.byw.reform.c2c.oct.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.flyco.tablayout.CommonTabLayout;
import com.google.android.material.tabs.TabLayout;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.fragment.OtcOrderMainagerBuyFragment;
import huolongluo.byw.reform.c2c.oct.fragment.OtcOrderMainagerSellFragment;
/**
 * Created by Administrator on 2019/5/15 0015.
 */
public class OtcOrderManagerActivity extends BaseActivity implements View.OnClickListener {
    CommonTabLayout tablayout;
    TextView title_tv;
    ImageButton back_iv;
    TabLayout tabLayout;
    private OtcOrderMainagerBuyFragment otcFragmentBuy;
    private OtcOrderMainagerSellFragment otcFragmentSell;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otcordermanager);
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setEnableGesture(false);
        }
        title_tv = fv(R.id.title_tv);
        back_iv = fv(R.id.back_iv);
        tabLayout = fv(R.id.tabLayout);
        title_tv.setText(R.string.xx7);
        back_iv.setOnClickListener(v -> {
            finish();
        });
        otcFragmentBuy = OtcOrderMainagerBuyFragment.newInStance(1);
        getSupportFragmentManager().beginTransaction().add(R.id.framelayout, otcFragmentBuy).commit();
        initTitle();
    }

    void switchs(int page) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (page == 0) {
            // buy_tv.setTextColor(getResources().getColor(R.color.white));
            // sell_tv.setTextColor(getResources().getColor(R.color.ffa9a4c0));
            if (otcFragmentSell != null) {
                transaction.hide(otcFragmentSell);
            }
            transaction.show(otcFragmentBuy);
        } else {
            //sell_tv.setTextColor(getAContext().getResources().getColor(R.color.white));
            //   buy_tv.setTextColor(getAContext().getResources().getColor(R.color.ffa9a4c0));
            transaction.hide(otcFragmentBuy);
            if (otcFragmentSell == null) {
                otcFragmentSell = OtcOrderMainagerSellFragment.newInStance(1);
                transaction.add(R.id.framelayout, otcFragmentSell);
                //   transaction.add(R.id.framelayout, otcFragmentSell);
            } else {
                transaction.show(otcFragmentSell);
            }
        }
        transaction.commit();
    }

    void initTitle() {
        tabLayout.removeAllTabs();
        TabLayout.Tab tab1 = tabLayout.newTab();//自选
        tab1.setTag(0);
        View view1 = View.inflate(this, R.layout.search_tab_item, null);
        TextView zixuanview = view1.findViewById(R.id.tv_cnyt1);
        TextView number_tv = view1.findViewById(R.id.number_tv);
        LinearLayout tab_llview = view1.findViewById(R.id.tab_llview);
        tab_llview.setEnabled(false);
        ImageView iv = view1.findViewById(R.id.iv);
        iv.setVisibility(View.GONE);
        RelativeLayout relativeL = view1.findViewById(R.id.relativeL);
        relativeL.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        zixuanview.setText(R.string.xx8);
        tab1.setCustomView(view1);
        tabLayout.addTab(tab1);
        TabLayout.Tab tab2 = tabLayout.newTab();//自选
        tab2.setTag(1);
        View view2 = View.inflate(this, R.layout.search_tab_item, null);
        TextView zixuanview2 = view2.findViewById(R.id.tv_cnyt1);
        TextView number_tv2 = view2.findViewById(R.id.number_tv);
        ImageView iv2 = view2.findViewById(R.id.iv);
        iv2.setVisibility(View.GONE);
        RelativeLayout relativeL2 = view2.findViewById(R.id.relativeL);
        relativeL2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        zixuanview2.setText(R.string.xx9);
        tab2.setCustomView(view2);
        tabLayout.addTab(tab2);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // mViewpager.setCurrentItem(tab.getPosition(), false);
                tab.getCustomView().findViewById(R.id.tab_llview).setEnabled(false);
                switchs(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tab_llview).setEnabled(true);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.getTabAt(0).select();
    }

    void upLoad() {
        //  OkhttpManager.postAsync();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
