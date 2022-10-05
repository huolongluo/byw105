package huolongluo.byw.byw.base;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.pricing.PricingMethodUtil;
public class BaseFinanceFragment extends BaseFragment{
    @BindView(R.id.tvFinanceEye)
    public TextView tvFinanceEye;
    @BindView(R.id.tvOperate1)
    public TextView tvOperate1;
    @BindView(R.id.tvOperate2)
    public TextView tvOperate2;
    @BindView(R.id.ivHideCoin)
    public ImageView ivHideCoin;
    @BindView(R.id.tvHideCoin)
    public TextView tvHideCoin;
    @BindView(R.id.llHideCoin)
    public LinearLayout llHideCoin;
    @BindView(R.id.tv_totalasset)
    public TextView tv_totalasset;
    @BindView(R.id.tvUnit)
    public TextView tvUnit;
    @Override
    protected void initDagger() {
    }

    @Override
    protected void initViewsAndEvents(View rootView) {
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    public void refreshFinanceEyeUi(boolean isOpen){
        Drawable drawable;
        if(isOpen){
            drawable=getResources().getDrawable(R.mipmap.ic_eye_white_open);
        }else{
            drawable=getResources().getDrawable(R.mipmap.ic_eye_white_close);
        }
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tvFinanceEye.setCompoundDrawables(null,null,drawable,null);
    }
    public void setLeftDrawable(TextView tv,int resId){
        Drawable drawable=getResources().getDrawable(resId);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tv.setCompoundDrawables(drawable,null,null,null);
    }

    public void refreshTotalAsset(String price) {
        tv_totalasset.setText(PricingMethodUtil.getResultByExchangeRate(price,
                "CNY", 4));
        tvUnit.setText(PricingMethodUtil.getPricingSelectType());
    }
}
