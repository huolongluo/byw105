package huolongluo.byw.byw.ui.fragment.maintab01.listen;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.AllMarketFragment;
import huolongluo.byw.byw.ui.fragment.maintab01.AllMarketFragmentTest;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TitleEntity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.log.Logger;
import huolongluo.bywx.utils.ValueUtils;
/**
 * zh行情，主区，DAO区的Fragment，上级为MarketNewFragment
 */
public class MainAreaFragment extends AllMarketFragment implements MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> {
    SlidingTabLayout tablayout;
    private View mViews;
    private AllMarketFragmentTest[] fragments;
    private String[] mTitles;
    private ViewPager viewPager;
    private TitleEntity.FbsBean item;
    private static final String PARAM = "param";
    private static String TAG = "AreaFragment";

    public MainAreaFragment() {
        super();
    }
    public void setDataValue(TitleEntity.FbsBean item){
        this.item = item;
        mTitles = new String[item.getAreaCoins().size()];
    }

    //解决问题android.support.v4.app.Fragment$InstantiationException:
    // Unable to instantiate fragment huolongluo.byw.byw.ui.fragment.maintab01.listen.AreaFragment: could not find Fragment constructor
    public static MainAreaFragment newInstance(TitleEntity.FbsBean item) {
        Bundle args = new Bundle();
        args.putSerializable(PARAM, item);
        MainAreaFragment fragment = new MainAreaFragment();
        //TODO 待修改-数据加载流程待优化
        fragment.item = item;
        fragment.mTitles = new String[item.getAreaCoins().size()];
        fragment.setArguments(args);
        Logger.getInstance().debug(TAG, "newInstance1", new Exception());
        return fragment;
    }
    @Override
    public void refreshAdapter(){
        if(fragments==null){
            return;
        }
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i] != null) {
                fragments[i].refreshAdapter();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        Object obj = bundle.getSerializable(PARAM);
        if (obj instanceof TitleEntity.FbsBean) {
            item = (TitleEntity.FbsBean) obj;
            mTitles = new String[item.getAreaCoins().size()];
            areaName = item.getAreaName();
            areaCoinsStr = item.getAreaCoinsStr();
        }
        Logger.getInstance().debug(TAG, "newInstance2", new Exception());
        if (item == null) {
            return;
        }
        mTitles = new String[item.getAreaCoins().size()];
        Logger.getInstance().debug(TAG, "newInstance3", new Exception());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViews = inflater.inflate(R.layout.main_area_layout, null);
        tablayout = mViews.findViewById(R.id.tablayout);
        viewPager = mViews.findViewById(R.id.viewPager);
        tablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        initTitle();
        return mViews;
    }

    void initTitle() {
        if (mTitles == null) {
            return;
        }
        if (fragments != null) {
            try {
                for (AllMarketFragmentTest mf : fragments) {
                    mf.release();
                }
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }
        fragments = new AllMarketFragmentTest[mTitles.length];
        for (int i = 0; i < fragments.length; i++) {
            TitleEntity.FbsBean.AreaCoinsBean area = item.getAreaCoins().get(i);
            mTitles[i] = area.getCoinName();
            fragments[i] = new AllMarketFragmentTest();
            fragments[i].pageCount = i;
            fragments[i].mType = String.valueOf(item.getType());
            fragments[i].fid = area.getFid();
            fragments[i].coinName = area.getCoinName();
            fragments[i].areaName = areaName;
            fragments[i].areaCoinsStr = areaCoinsStr;
            setMarketDataCallback(fragments[i]);
        }
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tablayout.setViewPager(viewPager, mTitles);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tablayout.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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

    private void setMarketDataCallback(MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback) {
        MarketDataPresent.getSelf().setDataCallback(callback);
    }
}
