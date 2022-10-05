package huolongluo.byw.byw.ui.fragment.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.coinw.biz.event.BizEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.bean.MarketListBean;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.adapter.SelectCoinDialogAdapter;
import huolongluo.byw.byw.ui.present.HotMoneyPresenter;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.kline2.KLineActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.tip.MToast;
/**
 * Created by LS on 2018/7/10.
 */
public class SearchCnytFragment extends BaseFragment {
    private RelativeLayout empty_tv;
    private LinearLayout ll_nologin;
    private LinearLayout ll_zixuan;
    private TextView btn_bus_login;
    private TextView empty_tip_tv;
    private List<MarketListBean> mData = new ArrayList<>();
    private SelectCoinDialogAdapter mAdapter;
    private ListView listView;
    private FrameLayout empty_view;
    private int fid;
    private String cnyName;
    private Handler handler = new Handler();

    public static SearchCnytFragment getInstance(List<MarketListBean> list) {
        SearchCnytFragment fragment = new SearchCnytFragment();
        return fragment;
    }

    @Override
    protected void initDagger() {
    }

    /**
     * 刷新数据
     * @param object
     */
    public void refreshData(Map<Integer, List<MarketListBean>> object) {
        if (refresh_layout == null) {
            return;
        }
        if (refresh_layout.isRefreshing()) {
            refresh_layout.setRefreshing(false);
            MToast.show(getActivity(), getString(R.string.c7), 1);
        }
//        if(mType==300){
//            Logger.getInstance().debug(TAG,"refreshData object:"+GsonUtil.obj2Json(object,Map.class)+"  object.get(300):"+GsonUtil.obj2Json(object.get(300),List.class));
//        }
        Set<Map.Entry<Integer, List<MarketListBean>>> entry = object.entrySet();
        for (Map.Entry<Integer, List<MarketListBean>> entry1 : entry) {
            List<MarketListBean> value = new ArrayList<>();
            if (entry1.getKey() == mType) {
                for (int i = 0; i < entry1.getValue().size(); i++) {
                    MarketListBean bean = entry1.getValue().get(i);
                    //由于服务器接口数据返回极其不规范（短时间协调解决不了），故由客户端解决。。。。。坑
//                    if (fid == bean.getFid()) {
                    if (TextUtils.equals(cnyName, bean.getCnyName())) {
                        value.add(bean);
                    }
                }
                //TODO 待大量测试
//                if(mType==300){
//                    Logger.getInstance().debug(TAG,"refreshData entry1.getValue():"+GsonUtil.obj2Json(entry1.getValue(),List.class));
//                }
                setAdapter(fid == 0 ? new ArrayList<>(entry1.getValue()) : new ArrayList<>(value));
                break;
            }
        }
    }

    /**
     * override this method to do operation in the fragment
     * @param rootView
     */
    @Override
    protected void initViewsAndEvents(View rootView) {
        refresh_layout = rootView.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this::refreshListener);
        ll_nologin = rootView.findViewById(R.id.ll_nologin);
        ll_zixuan = rootView.findViewById(R.id.ll_zixuan);
        btn_bus_login = rootView.findViewById(R.id.btn_bus_login);
        empty_tip_tv = rootView.findViewById(R.id.empty_tip_tv);
        btn_bus_login.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        empty_view = rootView.findViewById(R.id.empty_view);
        empty_tv = rootView.findViewById(R.id.empty_tv);
        listView = (ListView) rootView.findViewById(R.id.lv_content);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            MarketListBean coinBean = (MarketListBean) mAdapter.getItem(i);
            Activity activity = getActivity();
            if (activity instanceof KLineActivity) {
                EventBus.getDefault().post(new BizEvent.Trade.CoinEvent(coinBean.getId() + "", coinBean.getCoinName(), coinBean.getCnyName(), coinBean.getMycurrency(),false));
            } else {
//                CameraMainActivity.selectOptionName = coinBean.getCoinName();
//                CameraMainActivity.selectOptionID = Integer.valueOf(coinBean.getId());
//                CameraMainActivity.selectCoinTitle = coinBean.getCoinName() + "/" + coinBean.getCnyName();
//                CameraMainActivity.selfselection = coinBean.getSelfselection() + "";
                MainActivity.self.gotoTrade(coinBean);
                MainActivity.self.drawer_layout.closeDrawer(GravityCompat.START);
                if (MainActivity.self.sideSearchFragment != null) {
                    MainActivity.self.sideSearchFragment.close();
                }
            }
            HotMoneyPresenter.collectHotData(null, coinBean.getId() + "");
            //
            mAdapter.notifyDataSetInvalidated();
        });
        initData();
    }

    private void refreshListener() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).reFreshSide();
        }
        if (refresh_layout != null) {
            return;
        }
    }

    private int mType = -1;

    private void initData() {
        Logger.getInstance().debug(TAG,"initData",new Exception());
        mAdapter = new SelectCoinDialogAdapter(getActivity(), new ArrayList<>());
        listView.setAdapter(mAdapter);
        setAdapter(mData);
    }
    public void setDataValue(int type,List<MarketListBean> data){
        this.mType = type;
        this.mData = data;
    }
    public void setDataValue(int type,int fid,String cnyName,List<MarketListBean> data){
        this.mType = type;
        this.fid = fid;
        this.cnyName = cnyName;
        this.mData = data;
    }

    public void resetData() {
//        Log.e("SearchCnytFsetData", "type== " + mType + "     mData=  " + mData);
        if (empty_tv == null) {
            return;
        }
        empty_tv.setVisibility(View.GONE);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).reFreshSide();
        }
        if (mData != null) {
            setAdapter(mData);
        }
    }

    public void checkUserInfo() {
        if (UserInfoManager.isLogin()) {
            if (ll_nologin != null) {
                ll_nologin.setVisibility(View.GONE);
            }
        } else if (mType == 1) {
            if (ll_nologin != null) {
                ll_nologin.setVisibility(View.VISIBLE);
            }
        }
        if (mType == Constant.ZI_XUAN) {
            if (empty_tip_tv != null) {
                empty_tip_tv.setText(R.string.cc67);
            }
        } else if (mType == 1) {
            if (empty_tip_tv != null) {
                Logger.getInstance().debug("guocj", "Market", new Exception());
                empty_tip_tv.setText(R.string.cc68);
            }
        }
    }

    public void refreshData(List<MarketListBean> searchData, boolean result, int currentItem) {
        if(refresh_layout==null) return;
        if (refresh_layout.isRefreshing()) {
            refresh_layout.setRefreshing(false);
            MToast.show(getActivity(), getString(R.string.c7), 1);
        }
        //Log.e("SearchCnytFragment", "type== " + mType + "     mData=  " + mData);
        if (searchData.size() == 0 && mType != Constant.ZI_XUAN) {//0是自选，防止币种为空时，显示两个空图标
            empty_tv.setVisibility(View.VISIBLE);
        } else {
            empty_tv.setVisibility(View.GONE);
        }
        setAdapter(searchData);
    }

    /**
     * lv_content
     * override this method to return content view id of the fragment******************************************************
     */
    @Override
    protected int getContentViewId() {
        return R.layout.layout_select_coin_item;
    }

    private SwipeRefreshLayout refresh_layout;

    private void checkDataStatus(int size) {
        //自选
        if (mType == Constant.ZI_XUAN) {
            if (size == 0) {
                empty_view.setVisibility(View.VISIBLE);
            } else {
                empty_view.setVisibility(View.GONE);
            }
        } else {
            if (size == 0) {
                empty_tv.setVisibility(View.VISIBLE);
            } else {
                empty_tv.setVisibility(View.GONE);
            }
        }
    }

    private void setAdapter(List<MarketListBean> list) {
        if (list != null) {
            checkDataStatus(list.size());
            if (fid != 0) {
                List<MarketListBean> beans = new ArrayList<>();
                for (MarketListBean bean : list) {
                    //由于服务器接口数据返回极其不规范（短时间协调解决不了），故由客户端解决。。。。。坑
//                    if (fid == bean.getFid()) {
                    if (TextUtils.equals(cnyName, bean.getCnyName())) {
                        beans.add(bean);
                    }
                }
                updateAdapter(beans);
            } else {
                updateAdapter(list);
            }
        } else {
            Log.e("aaaaaaaa", "aaaaddddaaaaaa");
        }
    }

    private void updateAdapter(List<MarketListBean> beans) {
//        if(mType==300){
//            Logger.getInstance().debug(TAG,"refreshData updateAdapter beans:"+ GsonUtil.obj2Json(beans,List.class));
//        }
        if (mAdapter == null) {
            mAdapter = new SelectCoinDialogAdapter(getActivity(), beans);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.setmData(beans);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void resetView() {
//        08-14 14:51:07.476	29952	Error	AndroidRuntime	FATAL EXCEPTION: main
//        08-14 14:51:07.476	29952	Error	AndroidRuntime	Process: huolongluo.byw, PID: 29952
//        08-14 14:51:07.476	29952	Error	AndroidRuntime	java.lang.NullPointerException
//        08-14 14:51:07.476	29952	Error	AndroidRuntime	at huolongluo.byw.byw.ui.fragment.search.SearchCnytFragment.resetView(SearchCnytFragment.java:409)
        if (empty_tv == null || refresh_layout == null) {
            return;
        }
        empty_tv.setVisibility(View.GONE);
        if (refresh_layout.isRefreshing()) {
            refresh_layout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            try {
                handler.removeCallbacksAndMessages(null);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }
        super.onDestroy();
    }
}
