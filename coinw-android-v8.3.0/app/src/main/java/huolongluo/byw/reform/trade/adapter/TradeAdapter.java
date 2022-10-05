package huolongluo.byw.reform.trade.adapter;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TradeAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments;

    public TradeAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < 0 || fragments == null || fragments.length <= 0) {
            return null;
        }
        return fragments[position];
    }

    @Override
    public int getCount() {
        if (fragments == null || fragments.length <= 0) {
            return 0;
        }
        return fragments.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }
}
