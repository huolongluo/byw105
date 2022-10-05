package huolongluo.byw.reform.home.activity.kline2.adapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.List;
import huolongluo.byw.view.TabItemView;
//k线底部委托订单最新成交百科的适配器
public class KLineBottomAdapter extends FragmentPagerAdapter {
    List<Fragment> listFragment;
    List<TabItemView> list;
    public KLineBottomAdapter(@NonNull FragmentManager fm, List<Fragment> listFragment,List<TabItemView> list) {
        super(fm);
        this.listFragment = listFragment;
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}