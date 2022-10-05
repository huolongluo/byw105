package huolongluo.byw.byw.ui.fragment.maintab02;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
public class MarketSwapAdapter extends FragmentStatePagerAdapter {
    private MarketSwapItemFragment[] fragments = new MarketSwapItemFragment[]{};

    public MarketSwapAdapter(FragmentManager fm, MarketSwapItemFragment[] fragments) {
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
        return fragments.length;
    }
}
