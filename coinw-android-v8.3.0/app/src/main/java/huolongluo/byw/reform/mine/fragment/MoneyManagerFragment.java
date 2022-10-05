package huolongluo.byw.reform.mine.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.base.ByperpayBean;
import huolongluo.byw.reform.mine.activity.MoneyManagerActivity;
import huolongluo.byw.reform.mine.adapter.BaseMoneyMagAdapter;
import huolongluo.byw.reform.mine.adapter.MoneyMagAdapter;
import huolongluo.byw.reform.mine.adapter.MoneyMagAdapter1;
import huolongluo.byw.reform.mine.adapter.MoneyMagAdapter2;
import huolongluo.byw.reform.mine.click.CheckHppClick;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
/**
 * Created by Administrator on 2019/1/6 0006.
 */
public class MoneyManagerFragment extends BaseFragment implements CheckHppClick {
    RecyclerView recyclerView;
    TextView textview;
    SwipeRefreshLayout refresh_layout;
    ScrollView scrollView;
    int type = 1;
    String url = "https://www.hyperpay.tech/?con=api&ctl=coinw_product&type=";//传多语言标识
    private BaseMoneyMagAdapter adapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_moneymanager;
    }

    public List<ByperpayBean.LicaiBean> list = new ArrayList<>();

    @Override
    protected void onCreatedView(View rootView) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type");
        }
        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString(type + "hpy");
        if (!TextUtils.isEmpty(result)) {
            ByperpayBean byperpayBean = new Gson().fromJson(result, ByperpayBean.class);
            list.clear();
            list.addAll(byperpayBean.getData());
        }
        refresh_layout = rootView.findViewById(R.id.refresh_layout);
        scrollView = rootView.findViewById(R.id.scrollView);
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hyperpay();
            }
        });
        textview = rootView.findViewById(R.id.textview);
        if (type == 1) {
            textview.setText(R.string.qs35);
        } else if (type == 2) {
            textview.setText(R.string.qs36);
        } else if (type == 3) {
            textview.setText(R.string.qs37);
        } else if (type == 4) {
            textview.setText(R.string.qs38);
        }
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        if (type == 1) {
            adapter = new MoneyMagAdapter(this, list, getAContext());
        } else if (type == 2) {
            adapter = new MoneyMagAdapter1(this, list, getAContext());
        } else {
            adapter = new MoneyMagAdapter2(this, list, getAContext());
        }
        recyclerView.setAdapter(adapter);
        hyperpay();
    }

    void hyperpay() {
        String reqUrl = url + type + "&lang=" + AppUtils.getLanguageTag();
        OkhttpManager.get(reqUrl, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                if (refresh_layout != null && refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);
                }
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("requestSuccess= ", "result=  " + result);
                if (type == 3) {
                    Log.e("requestSuccess3= ", "result=  " + result);
                }
                if (refresh_layout != null && refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);
                    MToast.showButton(getActivity(), getString(R.string.qs39), 1);
                }
                CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put(type + "hpy", result);
                ByperpayBean byperpayBean = new Gson().fromJson(result, ByperpayBean.class);
                if (byperpayBean.getCode() == 200) {
                    list.clear();
                    list.addAll(byperpayBean.getData());
                    adapter.updata(list);
                } else {
                    MToast.show(getActivity(), byperpayBean.getMsg(), 1);
                }
            }
        });
    }

    @Override
    public void onClick() {
        if (getActivity() != null && getActivity() instanceof MoneyManagerActivity) {
            ((MoneyManagerActivity) getActivity()).checkApp();
        }
    }
}
