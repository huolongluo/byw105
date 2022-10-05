package huolongluo.byw.byw.ui.activity.renzheng;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.adapter.AreaListAdapter;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.Area;
import huolongluo.byw.model.AreaResult;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.WrapContentLinearLayoutManager;
import huolongluo.bywx.utils.EncryptUtils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/**
 * Created by LS on 2018/7/23.
 */
public class CountryActiivty extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv)
    RecyclerView rv;
    AreaListAdapter adapter;
    private List<Area> dataList = new ArrayList<Area>();
    private final String TAG = "CountryActivity";

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_country;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @Override
    protected void initViewsAndEvents() {
        initToolBar();
        eventClick(back_iv).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        adapter = new AreaListAdapter(this, dataList, R.layout.item_area_list);
        rv.setNestedScrollingEnabled(false);
        rv.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new SuperAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int viewType, int position) {
                try {
                    Intent intent = new Intent();
                    intent.putExtra("countryName", dataList.get(position).area);
                    intent.putExtra("id", dataList.get(position).id);
                    intent.putExtra("code", dataList.get(position).code);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Throwable t) {
                }
            }
        });
        refreshLayout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        refreshLayout.setOnRefreshListener(() -> {
            rx.Observable.just("").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(String s) {
                    SnackBarUtils.ShowBlue(CountryActiivty.this, getString(R.string.reslas));
                }
            });
            refresh();
        });
        refresh();
    }

    private void refresh() {
        DialogManager.INSTANCE.showProgressDialog(this);
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);
        params = EncryptUtils.encrypt(params);
        //请求地区数据
        netTags.add(UrlConstants.LIST_AREA);
        Type type = new TypeToken<SingleResult<AreaResult>>() {
        }.getType();
        OKHttpHelper.getInstance().post(UrlConstants.LIST_AREA, params, callback, type);
    }

    private INetCallback<SingleResult<AreaResult>> callback = new INetCallback<SingleResult<AreaResult>>() {
        @Override
        public void onSuccess(SingleResult<AreaResult> result) throws Throwable {
            if (result == null) {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                closeDialog(false);
                return;
            }
            if (result.data == null || result.data == null) {
                Logger.getInstance().debug(TAG, "data is null.");
                closeDialog(false);
                return;
            }
            if (result.data.areaList != null && !result.data.areaList.isEmpty()) {
                dataList.clear();
                dataList.addAll(result.data.areaList);
                adapter.replaceAll(dataList);
                adapter.notifyDataSetChanged();
            }
            closeDialog(true);
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
            //TODO 处理异常情况
            closeDialog(false);
        }
    };

    protected void closeDialog(boolean success) {
        DialogManager.INSTANCE.dismiss();
        if (!success) {
            Toast.makeText(this, R.string.aa78, Toast.LENGTH_LONG).show();
        }
    }

    private void initToolBar() {
        title_tv.setText(getString(R.string.select_area));
    }

    @Override
    public void finish() {
        super.finish();
    }
}
