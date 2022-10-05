package huolongluo.byw.byw.ui.fragment.maintab05;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.bancard.BankCardListActivity;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.LineTabVPAdapter;
import huolongluo.byw.byw.ui.fragment.maintab05.cnycthis.CnyCTHistoryActivity;
import huolongluo.byw.byw.ui.oneClickBuy.C2cStatus;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.fragment.OtcActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.view.NoScrollViewPager;
/**
 * Created by LS on 2018/7/18.
 */
public class C2CFragmnet extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.tv_cnyt_c)
    TextView tv_cnyt_c;
    @BindView(R.id.tv_cnyt_t)
    TextView tv_cnyt_t;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R.id.ll_main)
    LinearLayout ll_main;
    @BindView(R.id.ll_nologin)
    LinearLayout ll_nologin;
    @BindView(R.id.btn_bus_login)
    Button btn_bus_login;
    @BindView(R.id.ll_noC2C)
    LinearLayout ll_noC2C;
    @BindView(R.id.btn_C2C)
    Button btn_c2c;
    @BindView(R.id.history_iv)
    ImageView history_iv;
    @BindView(R.id.bank_list_iv)
    ImageView bank_list_iv;
    @BindView(R.id.help_iv)
    ImageView help_iv;
    @BindView(R.id.otc_select_tv)
    TextView otc_select_tv;
    @BindView(R.id.c2c_select_tv)
    TextView c2c_select_tv;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    private List<Fragment> mData = new ArrayList<Fragment>();
    public static int currentPager = 0;
    private LocalBroadcastManager broadcastManager;
    private TXFragment txFragment;
    @BindView(R.id.c2cTrade)
    TextView c2cTrade;
    @BindView(R.id.titlex)
    RelativeLayout titlex;
    @BindView(R.id.otc_title)
    TextView otc_title;

    /**
     * 注册广播接收器
     */
    private void registerReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("jerry");
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
    }

    private BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String change = intent.getStringExtra("change");
            if ("CnytC".equals(change)) {
                // 这地方只能在主线程中刷新UI,子线程中无效，因此用Handler来实现
                new Handler().post(new Runnable() {
                    public void run() {
                        //在这里来写你需要刷新的地方
                        //例如：testView.setText("恭喜你成功了");
                        viewpager.setCurrentItem(0, false);
                    }
                });
            } else {
                // 这地方只能在主线程中刷新UI,子线程中无效，因此用Handler来实现
                new Handler().post(new Runnable() {
                    public void run() {
                        if(viewpager==null) return;
                        viewpager.setCurrentItem(1, false);
                    }
                });
            }
        }
    };

    public static C2CFragmnet getInstance() {
        Bundle args = new Bundle();
        C2CFragmnet fragment = new C2CFragmnet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDagger() {
    }

    /**
     * override this method to do operation in the fragment
     * @param rootView
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initViewsAndEvents(View rootView) {
        mData.clear();
        CZFragment czFragment = new CZFragment();
        txFragment = new TXFragment();
        mData.add(czFragment);
        mData.add(txFragment);
        registerReceiver();
        history_iv.setOnClickListener(this);
        bank_list_iv.setOnClickListener(this);
        help_iv.setOnClickListener(this);
        c2c_select_tv.setOnClickListener(this);
        viewpager.setScroll(false);

//        if (BaseApp.FIST_OPEN_C2C == true) {
//            BaseApp.FIST_OPEN_C2C = false;
//            DialogUtils.getInstance().showC2cInfoDialog(requireContext());
//        }

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageSelected(int position) {
                currentPager = position;
                if (position == 0) {
                    tv_cnyt_c.setBackground(getActivity().getResources().getDrawable(R.drawable.market_custom_bg));
                    tv_cnyt_t.setBackground(getActivity().getResources().getDrawable(R.drawable.market_norml_bg));
                }
                if (position == 1) {
                    tv_cnyt_c.setBackground(getActivity().getResources().getDrawable(R.drawable.market_norml_bg));
                    tv_cnyt_t.setBackground(getActivity().getResources().getDrawable(R.drawable.market_custom_bg));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        eventClick(ivBack).subscribe(o -> {
           requireActivity().finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });

        eventClick(tv_cnyt_c).subscribe(o -> {
            tv_cnyt_c.setBackground(getActivity().getResources().getDrawable(R.drawable.market_custom_bg));
            tv_cnyt_t.setBackground(getActivity().getResources().getDrawable(R.drawable.market_norml_bg));
            viewpager.setCurrentItem(0, false);
            currentPager = 0;
//            }else {
//                Toast.makeText(getActivity(),"请先登录",1).show();
//            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_cnyt_t).subscribe(o -> {
            tv_cnyt_c.setBackground(getActivity().getResources().getDrawable(R.drawable.market_norml_bg));
            tv_cnyt_t.setBackground(getActivity().getResources().getDrawable(R.drawable.market_custom_bg));
            viewpager.setCurrentItem(1, false);
            currentPager = 1;
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(btn_bus_login).subscribe(o -> {
            startActivity(LoginActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(btn_c2c).subscribe(o -> {
            if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                showMessage(getString(R.string.ee17), 2);
                return;
            } else {
                startActivity(RenZhengBeforeActivity.class);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        viewClick(otc_select_tv, v -> {
            //切换到otc
            ((OtcActivity) getActivity()).selectOtcFragment();
        });
        if (C2cStatus.isShow) {
            c2cTrade.setVisibility(View.GONE);
        } else {
            c2cTrade.setVisibility(View.VISIBLE);
        }
        setAdapter();
    }

    private void setAdapter() {
        viewpager.setAdapter(new LineTabVPAdapter(getActivity(), getChildFragmentManager(), mData, getActivity().getResources().getStringArray(R.array.market_subitems)));
        viewpager.setCurrentItem(currentPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        //  setAdapter();
        if (txFragment != null) {
            txFragment.setView();
        }
        checkUserInfo();
        registerReceiver();

        c2cTrade.setText("otc");
        c2cTrade.setOnClickListener(view -> ((OtcActivity) getActivity()).selectOtcFragment());
        if (C2cStatus.isShowFast) {//开启
            titlex.setVisibility(View.VISIBLE);
            otc_title.setVisibility(View.GONE);
        } else {
            titlex.setVisibility(View.GONE);
            otc_title.setVisibility(View.VISIBLE);
        }

//        getUserInfo();
    }


    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        // Share.get().setLogintoken("");
        SPUtils.saveLoginToken("");
        checkUserInfo();
    }

    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        checkUserInfo();
    }

    /**
     * override this method to return content view id of the fragment*******************************************
     */
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_c2c;
    }

    /**
     * 检查用户 是否登陆
     * <p>
     * 刷新界面
     */
    private void checkUserInfo() {

       /* if (!TextUtils.isEmpty(UserInfoManager.getToken()))
        {
            ll_nologin.setVisibility(View.GONE);
            getUserInfo();
            ll_main.setVisibility(View.VISIBLE);

        }
        else
        {
            ll_main.setVisibility(View.GONE);
            ll_nologin.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(mAdDownLoadReceiver);//注销广播
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_iv:
                if (!UserInfoManager.isLogin()) {
                    //  Toast.makeText(getActivity(), "请先登录", 1).show();
                    startActivity(LoginActivity.class);
                    return;
                }
                if (UserInfoManager.getUserInfo().isHasC2Validate()) {
                    startActivity(CnyCTHistoryActivity.class);
                } else if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                    showMessage(getString(R.string.ee18), 2);
                } else {
                    //   showMessage("请进行身份验证后查看历史记录", 2);
                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.ee20), getString(R.string.ee21), getString(R.string.ee19));
                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                        @Override
                        public void onLiftClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onRightClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            startActivity(RenZhengBeforeActivity.class);
                        }
                    });
                }
                break;
            case R.id.bank_list_iv:
                if (!UserInfoManager.isLogin()) {
                    // Toast.makeText(getActivity(), "请先登录", 1).show();
                    startActivity(LoginActivity.class);
                    return;
                }
                if (UserInfoManager.getUserInfo().isHasC2Validate()) {
                    startActivity(BankCardListActivity.class);
                } else if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                    showMessage(getString(R.string.ee17), 2);
                } else {
                    //  showMessage("请先进行身份验证", 2);
                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.ee24), getString(R.string.ee25), getString(R.string.ee23));
                    DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                        @Override
                        public void onLiftClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onRightClick(AlertDialog dialog, View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            startActivity(RenZhengBeforeActivity.class);
                        }
                    });
                }
                break;
            case R.id.help_iv:
                DialogUtils.getInstance().showC2cInfoDialog(getActivity());
                break;
            case R.id.c2c_select_tv:
                ((OtcActivity) getActivity()).selectFirstTradeFragment();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
