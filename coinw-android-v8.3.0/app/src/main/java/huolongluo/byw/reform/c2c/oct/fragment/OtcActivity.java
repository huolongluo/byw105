package huolongluo.byw.reform.c2c.oct.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.legend.base.BaseActivity;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.fragment.maintab05.C2CFragmnet;
import huolongluo.byw.byw.ui.oneClickBuy.FastTradeFragment;
import huolongluo.byw.view.NoScrollViewPager;

public class OtcActivity extends BaseActivity {

    public static void launch(Context context){
        context.startActivity(new Intent(context, OtcActivity.class));
    }

    public static void launchFastTrade(Context context, String amount, boolean isCnyt){
        Intent intent = new Intent(context, OtcActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("amount", amount);
        intent.putExtra("is_cnyt", isCnyt);
        context.startActivity(intent);
    }

    NoScrollViewPager mNoScrollViewPager;
    OtcFragment mOtcFragment = new OtcFragment();
    FastTradeFragment mFastTradeFragment = new FastTradeFragment();
    C2CFragmnet mC2cFragment = new C2CFragmnet();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_otc;
    }

    @Override
    protected void initView() {
        mNoScrollViewPager = findViewById(R.id.noScrollViewPager);
        mNoScrollViewPager.setScroll(false);
        Fragment [] fragments = {mOtcFragment, mFastTradeFragment, mC2cFragment};
        mNoScrollViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//                super.destroyItem(container, position, object);
            }
        });

        int type = getIntent().getIntExtra("type", 0);
        mNoScrollViewPager.setCurrentItem(type, false);

        String amount = getIntent().getStringExtra("amount");
        boolean isCnyt = getIntent().getBooleanExtra("is_cnyt", false);
        mFastTradeFragment.setAmount(amount, isCnyt);
    }

    public void selectFirstTradeFragment(){
        mNoScrollViewPager.setCurrentItem(1, false);
    }

    public void selectOtcFragment(){
        mNoScrollViewPager.setCurrentItem(0, false);
    }

    public void selectC2cFragment(){
        mNoScrollViewPager.setCurrentItem(2, false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initObserve() {

    }
}
