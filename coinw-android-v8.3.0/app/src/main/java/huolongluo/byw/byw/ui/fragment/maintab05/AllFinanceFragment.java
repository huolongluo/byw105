package huolongluo.byw.byw.ui.fragment.maintab05;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.coinw.biz.event.BizEvent;
import com.android.legend.base.BaseFragment;
import com.android.legend.ui.earn.finance.EarnFinanceFragment;
import com.android.legend.ui.finance.ZjFinanceFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.reflect.TypeToken;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.fragment.bdb.BdbFinanceFragment;
import huolongluo.byw.byw.ui.fragment.bdb.bean.BdbFinancialAgreementStatus;
import huolongluo.byw.byw.ui.fragment.contractTab.ConTractFinanceFragment;
import huolongluo.byw.byw.ui.fragment.contractTab.ContractUserInfoEntity;
import huolongluo.byw.byw.ui.fragment.maintab03.BbFinanceFragment;
import huolongluo.byw.byw.ui.fragment.maintab03.FinanceFragment;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.c2c.oct.bean.OrderCancelBean;
import huolongluo.byw.reform.c2c.oct.fragment.OtcFinanceFragment;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.AgreementUtils;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.view.ViewPagerSlide;
import huolongluo.bywx.utils.DataUtils;
import huolongluo.bywx.utils.EncryptUtils;
import okhttp3.Request;
public class AllFinanceFragment extends BaseFragment {
    public static final int TYPE_ZC = 0;
    public static final int TYPE_BB = 2;
    public static final int TYPE_FB = 1;
    public static final int TYPE_HY = 3;
    public static final int POSITION_BDB = 4;
    public static final int TYPE_EARN = 5;
    public static final int TYPE_ZJ = 6;//赠金

    SlidingTabLayout tablayout;
    private ViewPagerSlide viewPager;
    private TextView tv_tab_title_hy, tv_tab_title_bdb;
    private Map<Integer,Integer> indexMap;

    @Override
    public boolean isRegisterEventBus() {
        return true;
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_all_finance;
    }

    @Override
    public void initView(@NotNull View view) {
        tablayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.viewPager);
        configGet();

        String mTitles[];
        Fragment[] fragments;
        indexMap = new HashMap<>();
        //TODO 因合代码后发版内容变更，拆代码的风险很高，故采用加开关来处理
        //TODO 币贷宝待修改
        //TODO 由于不可抗原因，杠杆服务需要下架
        int index = 0;
        if (Constant.IS_BDB_CLOSE) {
            if (DataUtils.isOpenHeader()) {
                mTitles = new String[]{getString(R.string.finance), getString(R.string.balance_fb), getString(R.string.balance_bb), getString(R.string.balance_hy), getString(R.string.balance_earn), getString(R.string.balance_zj)};
                fragments = new Fragment[]{new FinanceFragment(), new OtcFinanceFragment(), new BbFinanceFragment(), new ConTractFinanceFragment(), new EarnFinanceFragment(),ZjFinanceFragment.Companion.newInstance()};
                indexMap.put(TYPE_ZC,index++);
                indexMap.put(TYPE_FB,index++);
                indexMap.put(TYPE_BB,index++);
                indexMap.put(TYPE_HY,index++);
                indexMap.put(TYPE_EARN,index++);
                indexMap.put(TYPE_ZJ,index++);
            } else {
                mTitles = new String[]{getString(R.string.finance), getString(R.string.balance_bb), getString(R.string.balance_hy), getString(R.string.balance_earn), getString(R.string.balance_zj)};
                fragments = new Fragment[]{new FinanceFragment(), new BbFinanceFragment(), new ConTractFinanceFragment(), new EarnFinanceFragment(),ZjFinanceFragment.Companion.newInstance()};
                indexMap.put(TYPE_ZC,index++);
                indexMap.put(TYPE_BB,index++);
                indexMap.put(TYPE_HY,index++);
                indexMap.put(TYPE_EARN,index++);
                indexMap.put(TYPE_ZJ,index++);
            }
        } else {
            if (DataUtils.isOpenHeader()) {
                mTitles = new String[]{getString(R.string.finance), getString(R.string.balance_fb), getString(R.string.balance_bb), getString(R.string.balance_hy), getString(R.string.balance_earn), getString(R.string.balance_bdb), getString(R.string.balance_zj)};
                fragments = new Fragment[]{new FinanceFragment(), new OtcFinanceFragment(), new BbFinanceFragment(), new ConTractFinanceFragment(), new EarnFinanceFragment(), new BdbFinanceFragment(),ZjFinanceFragment.Companion.newInstance()};
                indexMap.put(TYPE_ZC,index++);
                indexMap.put(TYPE_FB,index++);
                indexMap.put(TYPE_BB,index++);
                indexMap.put(TYPE_HY,index++);
                indexMap.put(TYPE_EARN,index++);
                indexMap.put(POSITION_BDB,index++);
                indexMap.put(TYPE_ZJ,index++);
            } else {
                mTitles = new String[]{getString(R.string.finance), getString(R.string.balance_bb), getString(R.string.balance_hy), getString(R.string.balance_earn), getString(R.string.balance_bdb), getString(R.string.balance_zj)};
                fragments = new Fragment[]{new FinanceFragment(), new BbFinanceFragment(), new ConTractFinanceFragment(), new EarnFinanceFragment(), new BdbFinanceFragment(),ZjFinanceFragment.Companion.newInstance()};
                indexMap.put(TYPE_ZC,index++);
                indexMap.put(TYPE_BB,index++);
                indexMap.put(TYPE_HY,index++);
                indexMap.put(TYPE_EARN,index++);
                indexMap.put(POSITION_BDB,index++);
                indexMap.put(TYPE_ZJ,index++);
            }
        }

        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), fragments);
//        viewPager.setOffscreenPageLimit(mTitles.length);
        viewPager.setAdapter(adapter);
        tablayout.setIsNeedCheckServiceStop(CoinwHyUtils.isServiceStop);//整个资产的tab不可点击
        tablayout.setViewPager(viewPager, mTitles);

        //
        //TODO 由于不可抗原因，杠杆服务需要下架
//        tv_tab_title_lever = tablayout.getTitleView(TYPE_LEVER);
//        tv_tab_title_lever.setCompoundDrawablePadding(10);
        tv_tab_title_hy = tablayout.getTitleView(TYPE_HY);
        tv_tab_title_hy.setCompoundDrawablePadding(10);
        if (!Constant.IS_BDB_CLOSE) {
            tv_tab_title_bdb = tablayout.getTitleView(indexMap.get(POSITION_BDB));
            tv_tab_title_bdb.setCompoundDrawablePadding(10);
        }
        //        //
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
        tablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
//        X_SystemBarUI.initSystemBar(getActivity(), R.color.transparent);

        if(CoinwHyUtils.isServiceStop){
            viewPager.setNoScroll(true);
        }
        initHyOpenStatus();
        if (!Constant.IS_BDB_CLOSE) {
            initBdbOpenStatus();
        }
    }

    @Override
    public void initData() {
    }
    public void switchItem(int type){//type就是position
        try {
            if(viewPager==null||indexMap==null||indexMap.size()<=indexMap.get(type)){
                return;
            }
            viewPager.setCurrentItem(indexMap.get(type));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initHyOpenStatus() {
        if(!UserInfoManager.isLogin()){
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.GET_HY_USER, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("CONTRACT_TYPES", result);
                ContractUserInfoEntity contractUserInfoEntity = GsonUtil.json2Obj(result, ContractUserInfoEntity.class);
                if (null != contractUserInfoEntity && null != contractUserInfoEntity.getData()) {
                    ContractUserInfoEntity.DataBean data = contractUserInfoEntity.getData();
                    if (data.getStatus() == 0) {//未开通
                        if (tv_tab_title_hy != null) {
                            tv_tab_title_hy.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tan_2), null);
                        }
                    } else if (data.getStatus() == 1) {//已开通
                        if (tv_tab_title_hy != null) {
                            tv_tab_title_hy.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                    }
                }
            }
        });
    }

    private void initBdbOpenStatus() {
        if (AgreementUtils.isBdbOpen()) {
            if (tv_tab_title_bdb != null) {
                tv_tab_title_bdb.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        } else {
            if(!UserInfoManager.isLogin()){
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("type", 1);

            Type type = new TypeToken<SingleResult<String>>() {
            }.getType();
            OKHttpHelper.getInstance().get(UrlConstants.GET_BDB_AGREEMENT_STATUS + "?" + EncryptUtils.encryptStr(params) + "&loginToken=" + UserInfoManager.getToken(),
                    checkBdbAgreementCallback, type);
        }
    }

    void configGet() {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.configGet, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {
                OrderCancelBean orderCancelBean = GsonUtil.json2Obj(result, OrderCancelBean.class);
                if (null != orderCancelBean && null != orderCancelBean.getData()) {
                    OrderCancelBean.DataBean.EnableExchangeMer enableExchangeMer = orderCancelBean.getData().
                            getEnableExchangeMer();
                    UserInfoManager.setEnableExchangeMer(enableExchangeMer.getValue());
                }
            }
        });
    }

    /**
     * 红包领取通知
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RedEnvelope(BizEvent.RedEnvelope data) {
        DialogUtils.getInstance().showRegistSucDialog(getContext());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hyOpenAgreementSuccess(BizEvent.Contract.OpenAgreementSuccess event) {
        if (tv_tab_title_hy != null) {
            tv_tab_title_hy.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bdbOpenAgreementSuccess(BizEvent.Bdb.OpenAgreementSuccess event) {
        if (tv_tab_title_bdb != null) {
            tv_tab_title_bdb.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void gotoPosition(BizEvent.Bdb.GotoEvent event) {
        viewPager.setCurrentItem(event.position);
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

    private INetCallback<SingleResult<String>> checkBdbAgreementCallback = new INetCallback<SingleResult<String>>() {
        @Override
        public void onSuccess(SingleResult<String> response) throws Throwable {
            if (response == null || response.data == null || !response.code.equals("200")) {
                return;
            }
            Type type = new TypeToken<SingleResult<BdbFinancialAgreementStatus>>() {
            }.getType();
            SingleResult<BdbFinancialAgreementStatus> result = GsonUtil.json2Obj(response.data, type);
            if (result.code.equals("0")) {
                if (result.data.getStatus() == 1) {//已开通
                    if (tv_tab_title_bdb != null) {
                        tv_tab_title_bdb.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                    AgreementUtils.saveBdbOpen(getContext());
                } else {//未开通
                    if (tv_tab_title_bdb != null) {
                        tv_tab_title_bdb.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tan_2), null);
                    }
                }
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            //TODO 处理异常情况
        }
    };
}
