package huolongluo.byw.byw.ui.fragment.maintab01;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONException;
import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.coinw.biz.trade.model.Coin;
import com.android.coinw.utils.Utilities;
import com.android.legend.socketio.SocketIOClient;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.share.Share;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TitleEntity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MainAreaFragment;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MarketDataCallback;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.Hot;
import huolongluo.byw.model.MarketResult;
import huolongluo.byw.reform.home.activity.ShareActivity;
import huolongluo.byw.reform.home.activity.kline2.common.KLine2Util;
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants;
import huolongluo.byw.reform.market.MarketAdapter;
import huolongluo.byw.reform.mine.activity.MoneyManagerActivity;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.ScreenShotListenManager;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.system.KeybordS;
import huolongluo.byw.view.CustomLoadingDialog;
import huolongluo.bywx.HttpRequestManager;
import huolongluo.bywx.OnResultCallback;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
import rx.Subscription;
/**
 * Created by LS on 2018/7/4.
 * zh行情页面版块的主Fragment,和MarketSwapFragment，MarketETFFragment同级
 * 上级为MarketHomeFragment
 */
public class MarketPlateFragment extends BaseFragment{


    //***************************************************************************************************************
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_market_new;
    }

    public static MarketPlateFragment getInstance() {
        Bundle args = new Bundle();
        MarketPlateFragment fragment = new MarketPlateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDagger() {
    }


    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initViewsAndEvents(View rootView) {

    }


}
