package huolongluo.byw.byw.ui.fragment.maintab01;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

public class LineTabVPAdapter extends FragmentPagerAdapter {

    private String[] mTitles = null;
    private WeakReference<Context> mRefer = null;
    private List<Fragment> mData = null;

    public LineTabVPAdapter(Context context, FragmentManager fm, List<Fragment> list, String[] titles) {
        super(fm);
        mRefer = new WeakReference<Context>(context);
        mData = list;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < 0 || mData == null || mData.isEmpty()) {
            return null;
        }
        return mData.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }
}
