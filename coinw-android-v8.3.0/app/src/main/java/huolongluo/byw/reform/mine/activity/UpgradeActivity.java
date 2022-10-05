package huolongluo.byw.reform.mine.activity;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.mine.adapter.UpgradeAdapter;
import huolongluo.byw.reform.mine.bean.VipPurchaseBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.MToast;
import okhttp3.Request;

/**
 * Created by hy on 2019/3/5 0005.
 */

public class UpgradeActivity extends BaseActivity implements View.OnClickListener {

    Unbinder unbinder;
    @BindView(R.id.tate_tv)
    TextView tate_tv;
    @BindView(R.id.tate_tv3)
    TextView tate_tv3;
    @BindView(R.id.tate_tv1)
    TextView tate_tv1;
    @BindView(R.id.myVip_tv)
    TextView myVip_tv;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.daoqi_tv)
    TextView daoqi_tv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.history_iv)
    ImageView history_iv;
    @BindView(R.id.tv_no_data)
    LinearLayout tv_no_data;
    UpgradeAdapter adapter;
    List<VipPurchaseBean.VipPurchase> list = new ArrayList<>();
    String[] gradeFee;
    private int grade;
    private String fee;
    private String makerFee;
    private String takerFee;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_grade);
        unbinder = ButterKnife.bind(this);
        daoqi_tv.setVisibility(View.GONE);
        title_tv.setText(R.string.qs31);
        if (getIntent() != null) {
            grade = getIntent().getIntExtra("grade", 1);
            fee = getIntent().getStringExtra("fee");
            makerFee = getIntent().getStringExtra("maker");
            takerFee = getIntent().getStringExtra("taker");
            myVip_tv.setText("VIP" + grade);
            String endTime = getIntent().getStringExtra("endTime");
            if (!TextUtils.isEmpty(endTime)) {
                daoqi_tv.setVisibility(View.VISIBLE);
                daoqi_tv.setText(endTime + getString(R.string.qs30));

            }
        }
        history_iv.setVisibility(View.GONE);

        back_iv.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new UpgradeAdapter(this, list);
        recyclerView.setAdapter(adapter);
        // gradeFee=  getResources().getStringArray(R.array.grade_fee);;

        //tate_tv.setText(gradeFee[grade-1]);
        if (!TextUtils.isEmpty(makerFee) && !TextUtils.isEmpty(takerFee)) {
            tate_tv.setText(makerFee);
            tate_tv3.setText(takerFee);
        }

       /* if(grade==6){
            tate_tv1.setText("免手续费");
        }*/
        vipPurchaseList();

    }


    void vipPurchaseList() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());

        netTags.add(UrlConstants.vipPurchaseList);
        OkhttpManager.postAsync(UrlConstants.vipPurchaseList, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                tv_no_data.setVisibility(View.VISIBLE);
            }

            @Override
            public void requestSuccess(String result) {
                if (tv_no_data == null) return;

                VipPurchaseBean bean = new Gson().fromJson(result, VipPurchaseBean.class);
                if (bean != null) {
                    if (bean.getCode() == 0) {
                        list.clear();
                        list.addAll(bean.getVipList());
                        if (list.size() == 0) {
                            tv_no_data.setVisibility(View.VISIBLE);
                        } else {
                            tv_no_data.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        MToast.show(UpgradeActivity.this, bean.getValue(), 2);
                    }
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
