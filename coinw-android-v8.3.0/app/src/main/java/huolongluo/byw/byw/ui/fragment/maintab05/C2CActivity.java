package huolongluo.byw.byw.ui.fragment.maintab05;

import android.content.Context;
import android.content.Intent;

import com.android.legend.base.BaseActivity;

import huolongluo.byw.R;

public class C2CActivity extends BaseActivity {

    public static void launch(Context context){
        context.startActivity(new Intent(context, C2CActivity.class));
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void initView() {
        C2CFragmnet c2cFragment = new C2CFragmnet();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container, c2cFragment)
                .commit();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initObserve() {

    }
}
