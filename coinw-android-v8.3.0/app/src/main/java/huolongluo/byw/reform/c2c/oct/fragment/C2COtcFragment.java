package huolongluo.byw.reform.c2c.oct.fragment;

import android.view.View;
import android.widget.TextView;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.reform.base.BaseFragment;

/**
 * Created by dell on 2019/6/20.
 */

public class C2COtcFragment extends BaseFragment {
    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_c2cotc;
    }


    TextView c2c_select_tv;
    @Override
    protected void onCreatedView(View rootView) {

        c2c_select_tv=fv(R.id.c2c_select_tv);

        viewClick(c2c_select_tv,v -> {

           ((MainActivity)getActivity()).viewPager.setCurrentItem(4,false);

        });

    }
}
