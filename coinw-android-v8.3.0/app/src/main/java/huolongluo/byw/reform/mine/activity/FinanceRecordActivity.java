package huolongluo.byw.reform.mine.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.coinw.biz.event.BizEvent;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.cthistory.CHistoryDetailActivity;
import huolongluo.byw.byw.ui.activity.cthistory.FinanceRecordUtil;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.FinanceRecordBean;
import huolongluo.byw.model.FinanceRecordInfo;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.mine.adapter.FinanceRecordAdapter;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.EncryptUtils;
//财务记录页面
public class FinanceRecordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "FinanceRecordActivity";

    @BindView(R.id.listView)
    PullToRefreshListView listView;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.select_bn)
    LinearLayout select_bn;
    @BindView(R.id.root_view)
    RelativeLayout root_view;
    @BindView(R.id.tv_type)
    TextView tv_type;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.nodata_view)
    LinearLayout nodata_view;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    @BindView(R.id.net_error_view)
    FrameLayout net_error_view;
    Unbinder unbinder;
    private PopupWindow popupWindow;
    private FinanceRecordAdapter adapter;
    int id = 0;
    private int currentPage = 1;
    private int type=0;//区分接口调用类型
    private int fstatus;//以前的老旧字段，含义不清

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_record);
        unbinder = ButterKnife.bind(this);

        initView();
        initData();
    }
    @Override
    protected void onDestroy() {
        AppHelper.dismissPopupWindow(popupWindow);
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
    private void initView(){
        select_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        btn_reLoad.setOnClickListener(this);
        listView.setPullLoadEnable(true);
        listView.setPullRefreshEnable(false);
        adapter = new FinanceRecordAdapter(this);
        listView.setAdapter(adapter);
        listView.setPullToListViewListener(new PullToRefreshListView.PullToListViewListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getData();
            }

            @Override
            public void onLoadMore() {
                currentPage++;
                getData();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FinanceRecordBean bean = adapter.getItem(position-1);//因为有下拉刷新
                FinanceRecordUtil.gotoDetail(FinanceRecordActivity.this,bean);
            }
        });

    }
    private void initData(){
        id = getIntent().getIntExtra("id", 0);
        if (id == 0) {
            title_tv.setText(getString(R.string.h8));
        } else {
            title_tv.setText(String.format(getString(R.string.h9), getIntent().getStringExtra("coinName")));
        }
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
    }

    private void showDialog() {
        //设置要显示的view
        View view = View.inflate(this, R.layout.record_doalog, null);
        LinearLayout bn1 = view.findViewById(R.id.bn1);
        LinearLayout bn4 = view.findViewById(R.id.bn4);
        LinearLayout bn5 = view.findViewById(R.id.bn5);
        LinearLayout bn6 = view.findViewById(R.id.bn6);
        LinearLayout bn7 = view.findViewById(R.id.bn7);
        TextView bn3 = view.findViewById(R.id.bn3);
        bn1.setOnClickListener(this);
        bn3.setOnClickListener(this);
        bn4.setOnClickListener(this);
        bn5.setOnClickListener(this);
        bn6.setOnClickListener(this);
        bn7.setOnClickListener(this);
        //此处可按需求为各控件设置属性
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.FILL_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(view);
        popupWindow.showAtLocation(root_view, Gravity.BOTTOM, 0, 0);
    }

    private void getData(){
        nodata_view.setVisibility(View.GONE);
        net_error_view.setVisibility(View.GONE);
        DialogManager2.INSTANCE.showProgressDialog(this);
        Type types = new TypeToken<SingleResult<SingleResult<FinanceRecordInfo>>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("currentPage", currentPage);
        params.put("symbol", id);
        params.put("fstatus", fstatus);
        Logger.getInstance().debug(TAG,"getData params:"+params);
        params= EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());

        OKHttpHelper.getInstance().post(UrlConstants.GET_FINANCE_RECORD, params, getDataCallback, types);
    }
    private void change(int type){
        currentPage = 1;
        AppHelper.dismissPopupWindow(popupWindow);
        this.type=type;
        adapter.clear();
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bn1://充值提现
                if(type== Constant.TYPE_FINANCE_RECORD_RECHARGE){
                    AppHelper.dismissPopupWindow(popupWindow);
                    return;
                }
                tv_type.setText(getString(R.string.recharge_withdraw));
                fstatus=1;
                change(Constant.TYPE_FINANCE_RECORD_RECHARGE);
                break;
            case R.id.bn3://取消
                AppHelper.dismissPopupWindow(popupWindow);
                break;
            case R.id.bn4://空投
                if(type==Constant.TYPE_FINANCE_RECORD_AIR){
                    AppHelper.dismissPopupWindow(popupWindow);
                    return;
                }
                tv_type.setText(getString(R.string.i5));//
                fstatus=0;

                change(Constant.TYPE_FINANCE_RECORD_AIR);
                break;
            case R.id.bn5://全部
                if(type==Constant.TYPE_FINANCE_RECORD_ALL){
                    AppHelper.dismissPopupWindow(popupWindow);
                    return;
                }
                tv_type.setText(getString(R.string.h7));//
                fstatus=0;

                change(Constant.TYPE_FINANCE_RECORD_ALL);
                break;
            case R.id.bn6://返还
                if(type==Constant.TYPE_FINANCE_RECORD_FEE){
                    AppHelper.dismissPopupWindow(popupWindow);
                    return;
                }
                tv_type.setText(getString(R.string.return_des));//
                fstatus=3;

                change(Constant.TYPE_FINANCE_RECORD_FEE);
                break;
            case R.id.bn7://理财
                if(type==Constant.TYPE_FINANCE_RECORD_MANAGE_MONEY){
                    AppHelper.dismissPopupWindow(popupWindow);
                    return;
                }
                tv_type.setText(getString(R.string.manage_money));//
                fstatus=0;

                change(Constant.TYPE_FINANCE_RECORD_MANAGE_MONEY);
                break;
            case R.id.btn_reLoad://重新加载
                getData();
                break;
        }
    }
    private void nodata(){
        if (adapter != null && adapter.getCount() == 0) {
            listView.setPullLoadEnable(false);
            net_error_view.setVisibility(View.GONE);
            nodata_view.setVisibility(View.VISIBLE);
        } else {
            nodata_view.setVisibility(View.GONE);
            net_error_view.setVisibility(View.VISIBLE);
        }
    }
    private void error(){
        if (adapter != null && adapter.getCount() == 0) {
            listView.setPullLoadEnable(false);
            net_error_view.setVisibility(View.VISIBLE);
            nodata_view.setVisibility(View.GONE);
        } else {
            nodata_view.setVisibility(View.GONE);
            net_error_view.setVisibility(View.VISIBLE);
        }
    }

    private INetCallback<SingleResult<SingleResult<FinanceRecordInfo>>> getDataCallback = new INetCallback<SingleResult<SingleResult<FinanceRecordInfo>>>() {
        @Override
        public void onSuccess(SingleResult<SingleResult<FinanceRecordInfo>> response) throws Throwable {
            listView.stopLoadMore();
            DialogManager2.INSTANCE.dismiss();
            Logger.getInstance().debug(TAG,"result:"+GsonUtil.obj2Json(response,SingleResult.class));
            if (response == null || response.data == null||!TextUtils.equals(response.code,"200")) {
                Logger.getInstance().debug(TAG, "result is null.");
                nodata();
                return;
            }
            if (response.data.code.equals("0")) {
                ArrayList<FinanceRecordBean> list=response.data.data.getList();
                if(list==null){
                    return;
                }
                if(currentPage==1&&list.size()==0){
                    nodata();
                }else{
                    if(currentPage==1){
                        adapter.clear();
                        adapter.refresh(list);
                    }else{
                        adapter.refresh(list);
                    }
                    if(currentPage>=response.data.data.getTotalPages()){
                        listView.setPullLoadEnable(false);
                    }else{
                        listView.setPullLoadEnable(true);
                    }
                }
            }else{
                error();
                SnackBarUtils.ShowRed(FinanceRecordActivity.this,response.data.message);
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            listView.stopLoadMore();
            DialogManager2.INSTANCE.dismiss();
            Logger.getInstance().debug(TAG, "error", e);
            error();
        }
    };
}
