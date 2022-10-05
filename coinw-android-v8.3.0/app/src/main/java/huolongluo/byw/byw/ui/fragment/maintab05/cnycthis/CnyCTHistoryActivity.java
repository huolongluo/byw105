package huolongluo.byw.byw.ui.fragment.maintab05.cnycthis;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.LineTabVPAdapter;
import huolongluo.byw.log.Logger;
/**
 * Created by LS on 2018/7/20.
 */
public class CnyCTHistoryActivity extends BaseActivity {
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.tv_custom)
    TextView tvCustom;
    @BindView(R.id.tv_cnyt)
    TextView tvCnyt;
    @BindView(R.id.ll_back)
    LinearLayout back;
    private List<Fragment> fragments = null;
    public static int currentPager = 0;
    private String history = "";

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_c2c_his;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initViewsAndEvents() {
        fragments = new ArrayList<Fragment>();
        fragments.add(CnyCHFragment.getInstance());
        fragments.add(CnyTXHFragment.getInstance());
        if (getBundle() != null) {
            history = getBundle().getString("History");
        }
//        setAdapter();
        if (history.equals("TX")) {
            mViewpager.setCurrentItem(1);
            currentPager = 1;
        } else {
            mViewpager.setCurrentItem(0);
            currentPager = 0;
        }
        eventClick(back).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageSelected(int position) {
                currentPager = position;
                if (position == 0) {
                    tvCustom.setBackground(getResources().getDrawable(R.drawable.market_custom_bg));
                    tvCnyt.setBackground(getResources().getDrawable(R.drawable.market_norml_bg));
                }
                if (position == 1) {
                    tvCustom.setBackground(getResources().getDrawable(R.drawable.market_norml_bg));
                    tvCnyt.setBackground(getResources().getDrawable(R.drawable.market_custom_bg));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        eventClick(tvCustom).subscribe(o -> {
            tvCustom.setBackground(getResources().getDrawable(R.drawable.market_custom_bg));
            tvCnyt.setBackground(getResources().getDrawable(R.drawable.market_norml_bg));
            mViewpager.setCurrentItem(0);
            currentPager = 0;
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvCnyt).subscribe(o -> {
            tvCustom.setBackground(getResources().getDrawable(R.drawable.market_norml_bg));
            tvCnyt.setBackground(getResources().getDrawable(R.drawable.market_custom_bg));
            mViewpager.setCurrentItem(1);
            currentPager = 1;
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        setAdapter();
    }

    private void setAdapter() {
        mViewpager.setAdapter(new LineTabVPAdapter(this, getSupportFragmentManager(), fragments, getResources().getStringArray(R.array.market_subitems)));
        mViewpager.setCurrentItem(currentPager);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
