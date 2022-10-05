package huolongluo.byw.byw.ui.activity.banklist;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.legend.ui.viewmodel.BasicViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.BankListBean;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.addbankcard.AddBankCardActivity;
import huolongluo.byw.byw.ui.adapter.BankListAdapter;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.ImmersedStatusbarUtils;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.WrapContentLinearLayoutManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/**
 * <p>
 * Created by 火龙裸 on 2018/1/5 0005.
 */
public class BankListActivity extends BaseActivity{
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.toolbar_center_title)
    TextView toolbar_center_title;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.lin1)
    RelativeLayout lin1;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;
    @BindView(R.id.rv_bank)
    RecyclerView rv_bank;
    private List<BankListBean.ListBean> listBeen = new ArrayList<>();
    private BankListAdapter bankListAdapter;
    private BasicViewModel viewModel;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_bank_list;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
    }

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        //全屏。
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1); // 沉浸式
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(getString(R.string.sel_bank));
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }

    @Override
    protected void initViewsAndEvents() {
        initToolBar();
        eventClick(iv_left).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        rv_bank.setNestedScrollingEnabled(false);
        rv_bank.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bankListAdapter = new BankListAdapter(this, listBeen, R.layout.item_bank_list);
        rv_bank.setAdapter(bankListAdapter);
        bankListAdapter.setOnItemClickListener((itemView, viewType, position) -> {
            EventBus.getDefault().post(new Event.clickBankName(listBeen.get(position).getName()));
            AddBankCardActivity.openBankType = String.valueOf(listBeen.get(position).getId());
            close();
        });
        //由于服务器接口废弃，因此同步改动
        viewModel = new ViewModelProvider(this).get(BasicViewModel.class);
        viewModel.getBankList().observe(this, result -> getBankListSucce(result.getData()));
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        refresh_layout.setOnRefreshListener(() -> {
            rx.Observable.just("").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    refresh_layout.setRefreshing(false);
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(String s) {
                    //   showMessage("已刷新至最新数据" + s, 1);
                    SnackBarUtils.ShowBlue(BankListActivity.this, getString(R.string.reslas));
                }
            });
//            subscription = bankListPresent.getBankList(SPUtils.getLoginToken());
            viewModel.getWithdrawBankList();
            // System.out.println(Thread.currentThread().getName());
        });
//        subscription = bankListPresent.getBankList(SPUtils.getLoginToken());
        viewModel.getWithdrawBankList();
    }

    public void getBankListSucce(BankListBean response) {
        if (response.isResult()) {
            if (!listBeen.isEmpty()) {
                listBeen.clear();
            }
            this.listBeen = response.getList();
            bankListAdapter.replaceAll(listBeen);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
