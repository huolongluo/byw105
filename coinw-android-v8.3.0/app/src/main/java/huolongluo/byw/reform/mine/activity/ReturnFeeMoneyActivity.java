package huolongluo.byw.reform.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.base.BaseSwipeBackActivity;
import huolongluo.byw.reform.bean.ReturnFeeBean;
import huolongluo.byw.reform.mine.adapter.ReturnFeeAdapter;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.widget.WrapContentLinearLayoutManager;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/1/9 0009.
 */

public class ReturnFeeMoneyActivity extends BaseSwipeBackActivity {

    Unbinder unbinder;

    public List<ReturnFeeBean> list = new ArrayList<>();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.title_tv)
    TextView title_tv;


    @BindView(R.id.cnytAmount_tv)
    TextView cnytAmount_tv;
    @BindView(R.id.count_tv)
    TextView count_tv;

    @BindView(R.id.back_iv1)
    ImageButton back_iv1;

    @BindView(R.id.activity_tv)
    TextView activity_tv;

    @BindView(R.id.right_iv)
    ImageView right_iv;
    private ReturnFeeAdapter adapter;

    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_feemoney);
        unbinder = ButterKnife.bind(this);


        back_iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_tv.setText(getString(R.string.i9));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ReturnFeeAdapter(list, this);
        recyclerView.setAdapter(adapter);
        getEntrstCashBackCharges();
        getUserEntrustCashBackCharges();

        right_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnFeeMoneyActivity.this, NormanWebviewActivity.class);

                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }


    void getEntrstCashBackCharges() {

        Map<String, String> params = new HashMap<>();

        params.put("status", "0");
        params.put("pageNo", "1");
        params.put("pageSize", "10");
        //  params=OkhttpManager.encrypt(params);
        // params.put("loginToken", UserInfoManager.getToken());

        OkhttpManager.getAsync(UrlConstants.DOMAIN + UrlConstants.getEntrstCashBackCharges + "?loginToken=" + UserInfoManager.getToken() + "&" + OkhttpManager.encryptGet(params), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {

                JSONObject jsonObject = JSONObject.parseObject(result);

                List<ReturnFeeBean> lists = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ReturnFeeBean.class);
                list.addAll(lists);
                adapter.updata(list);
            }
        });


    }

    void getUserEntrustCashBackCharges() {

        Map<String, String> params = new HashMap<>();

        // params.put("status","0");
        /// params.put("pageNo","1");
        //params.put("pageSize","10");
        //  params=OkhttpManager.encrypt(params);
        params.put("type", "1");

        OkhttpManager.getAsync(UrlConstants.DOMAIN + UrlConstants.getUserEntrustCashBackCharges + "?loginToken=" + UserInfoManager.getToken() + "&" + OkhttpManager.encryptGet(params), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {

                JSONObject jsonObject = JSONObject.parseObject(result);
                //{"data":{"cnytAmount":"0","count":"0","open":"true"},"value":""}
                JSONObject object = jsonObject.getJSONObject("data");
                String cnytAmount = object.getString("cnytAmount");
                String count = object.getString("count");
                String open = object.getString("open");
                url = object.getString("url");
                if (TextUtils.isEmpty(url)) {
                    right_iv.setVisibility(View.GONE);
                } else {
                    right_iv.setVisibility(View.VISIBLE);
                }
                if (TextUtils.equals("true", open)) {
                    activity_tv.setText(getString(R.string.k1));
                } else {
                    activity_tv.setText(getString(R.string.j9));
                }
                cnytAmount_tv.setText(cnytAmount);
                count_tv.setText(count);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
