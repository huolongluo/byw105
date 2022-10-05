package huolongluo.byw.reform.mine.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.mine.adapter.GradeAdapter;
import huolongluo.byw.reform.mine.bean.VipBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;

/**
 * Created by hy on 2019/3/5 0005.
 */
public class GradeActivity extends BaseActivity implements View.OnClickListener {
    Unbinder unbinder;
    @BindView(R.id.tate_tv)
    TextView tate_tv;
    @BindView(R.id.tate_tv3)
    TextView tate_tv3;
    @BindView(R.id.tate_tv1)
    TextView tate_tv1;
    @BindView(R.id.myVip_tv)
    TextView myVip_tv;
    @BindView(R.id.daoqi_tv)
    TextView daoqi_tv;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.history_iv)
    ImageView history_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
//    @BindView(R.id.linearLayout)
//    LinearLayout linearLayout;
    GradeAdapter adapter;
    int currentGrade = 1;
    String fee = "";
    String endTime;
    List<VipBean.ViP> viPList = new ArrayList<>();
    private VipBean vipBean;
    int buyFee = 0;
    String[] gradeFee;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        unbinder = ButterKnife.bind(this);
        title_tv.setText(getString(R.string.f1));
        daoqi_tv.setVisibility(View.GONE);
//        linearLayout.setBackgroundColor(getResources().getColor(R.color.base_bg_white));
        back_iv.setOnClickListener(this);
        history_iv.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new GradeAdapter(currentGrade, viPList, this);
        recyclerView.setAdapter(adapter);
        gradeFee = getResources().getStringArray(R.array.grade_fee);
        adapter.setOnClickListener(new GradeAdapter.OnClickListener() {
            @Override
            public void onClick(int grade, String price) {
                if (vipBean != null) {

                   /* if (vipBean.getVipsize() != 0) {
                        DialogUtils.getInstance().showGradeDialog(GradeActivity.this, "您当前的VIP" + vipBean.getFfees() + "尚在生效中\n请到期后再升级");
                    } else {
                    }*/
                    buyFee = grade;
                    String msg;
                    String tip = "";
                    msg = getString(R.string.f2) + grade;
                    if (vipBean.getVipsize() != 0) {
                        tip = String.format(getString(R.string.f3), vipBean.getFfees(), grade);
                    } else {
                        msg = String.format(getString(R.string.f4), grade);
                    }
                    //https://jira.legenddigital.com.au/browse/COIN-1492
                    DialogUtils.getInstance().showPayGradeDialog(GradeActivity.this, msg, tip, price + " CWT", vipBean.getFtotal() + " CWT", new DialogUtils.onBnClickListener() {
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
                            saveVipPurchase();
                        }
                    });
                } else {
                    SnackBarUtils.ShowRed(GradeActivity.this, getString(R.string.net_timeout1));
                    nodeVipPurchase();
                }
                // DialogUtils.getInstance().showGradeDialog(GradeActivity.this,"您当前的VIP5尚在生效中\n请到期后再升级");
            }
        });
        //
        nodeVipPurchase();
    }

    void saveVipPurchase() {
        DialogManager.INSTANCE.showProgressDialog(this, getString(R.string.load));
        Map<String, String> params = new HashMap<>();
        params.put("ffees", buyFee + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.saveVipPurchase);
        OkhttpManager.postAsync(UrlConstants.saveVipPurchase, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                MToast.show(GradeActivity.this, getString(R.string.f5), 2);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("msg");
                    if (code == 0) {
                        nodeVipPurchase();
                    }
                    MToast.show(GradeActivity.this, value, 2);
                } catch (JSONException e) {   //{"result":true,"code":0,"value":"vip购买成功","total":99976.65021887}
                    e.printStackTrace();
                }
            }
        });
    }

    void nodeVipPurchase() {
        //更新状态
        EventBus.getDefault().post(new Event.refreshVip());
        DialogManager.INSTANCE.showProgressDialog(this, getString(R.string.load));
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.nodeVipPurchase);
        OkhttpManager.postAsync(UrlConstants.nodeVipPurchase, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                MToast.show(GradeActivity.this, getString(R.string.f6), 2);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    vipBean = new Gson().fromJson(result, VipBean.class);
                    if (vipBean != null && vipBean.getCode() == 0) {
                        myVip_tv.setText("VIP" + vipBean.getFfees());
                        viPList.clear();
                        currentGrade = vipBean.getFfees();
                        if (!TextUtils.isEmpty(vipBean.getSendTime())) {
                            daoqi_tv.setVisibility(View.VISIBLE);
                            daoqi_tv.setText(String.format(getString(R.string.f7), vipBean.getSendTime()));
                            endTime = vipBean.getSendTime();
                        }
                        adapter.setCurrentGrade(currentGrade);
                        viPList.addAll(vipBean.getVipFees());
                        adapter.notifyDataSetChanged();
                        for (VipBean.ViP viP : viPList) {
                            if (viP.getId() == currentGrade) {
                                tate_tv.setText(viP.getMakerFfees() + "%");
                                tate_tv3.setText(viP.getTakerFfees() + "%");
                                fee = viP.getTradeFfees();
                                break;
                            }
                        }
                    } else if (vipBean != null) {
                        MToast.show(GradeActivity.this, vipBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.history_iv:
                Intent intent = new Intent(this, UpgradeActivity.class);
                intent.putExtra("grade", currentGrade);
                intent.putExtra("fee", fee);
                intent.putExtra("maker", tate_tv.getText());
                intent.putExtra("taker", tate_tv3.getText());
                if (!TextUtils.isEmpty(endTime)) {
                    intent.putExtra("endTime", endTime + "");
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
