package huolongluo.byw.reform.market;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.ui.fragment.maintab01.AllMarketFragment;

/**
 * Created by Administrator on 2018/12/12 0012.
 */
public class MarketAdapter extends FragmentStatePagerAdapter {

    private Map<Integer, List<MarketListBean>> listMap = new HashMap<>();
    private AllMarketFragment[] fragments = new AllMarketFragment[]{};

    public MarketAdapter(FragmentManager fm, Map<Integer, List<MarketListBean>> listMap, AllMarketFragment[] fragments) {
        super(fm);
        this.fragments = fragments;
        this.listMap = listMap;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < 0 || fragments == null || fragments.length <= 0) {
            return null;
        }
        //  fragments[position] = new AllMarketFragment();
//        Bundle bundle = new Bundle();
        // bundle.putSerializable("data", (Serializable) listMap.get(Integer.parseInt(fragments[position].getPagerType())));
//        fragments[position].setArguments(bundle);
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
