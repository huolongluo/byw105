package huolongluo.byw.reform.mine.activity;

import android.content.Intent;
import android.os.Bundle;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.fragment.maintab03.FinanceFragment;
import huolongluo.byw.reform.base.BaseSwipeBackActivity;


/**
 * Created by Administrator on 2018/11/22 0022.
 */

public class FinanceActivity extends BaseSwipeBackActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_finance);

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new FinanceFragment()).commitAllowingStateLoss();

    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
