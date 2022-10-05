package huolongluo.byw.reform.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.mine.adapter.HppAdapter;
import huolongluo.byw.reform.mine.bean.BindHpyBean;
import huolongluo.byw.reform.mine.bean.HppRecord;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/2/20 0020.
 */

public class hPPRecordActivity extends BaseActivity {

    ImageButton back_iv;
    TextView title_tv;
    TextView noData_tv;
    PullToRefreshListView lv_content;
    List<HppRecord> recordList = new ArrayList<>();
    private HppAdapter adapter;
    private BindHpyBean bindHpyBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hpprecord);

        lv_content = findViewById(R.id.lv_content);
        back_iv = findViewById(R.id.back_iv);
        title_tv = findViewById(R.id.title_tv);
        noData_tv = findViewById(R.id.noData_tv);
        title_tv.setText(getString(R.string.c9));
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //  lv_content.setPullLoadEnable(false);
        lv_content.setPullRefreshEnable(false);
        lv_content.setPullLoadEnable(false);
        adapter = new HppAdapter(recordList, this);
        lv_content.setAdapter(adapter);
        //lv_content.setEmptyView(noData_tv);
        lv_content.setPullToListViewListener(new PullToRefreshListView.PullToListViewListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                hyperpayRecords();
                lv_content.setPullLoadEnable(true);
            }
        });

        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HppRecord record = adapter.getItem((int) id);

                Intent intent = new Intent(hPPRecordActivity.this, HppRecordDetailActivity.class);
                intent.putExtra("Status_s", record.getStatus_s());
                intent.putExtra("id", record.getOrderNo());
                intent.putExtra("time", record.getFcreateTime().getTime() + "");
                intent.putExtra("amount", record.getAmount() + "");
                intent.putExtra("coinName", record.getCoinType().getfShortName() + "");
                intent.putExtra("status", record.getType() + "");

                startActivity(intent);


            }
        });
        hyperpayRecords();

    }

    int currentPage = 1;

    void hyperpayRecords() {

        Map<String, String> params = new HashMap<>();

        params.put("type", "0");
        params.put("currentPage", currentPage + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.hyperpayRecords);
        OkhttpManager.postAsync(UrlConstants.hyperpayRecords, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                lv_content.stopLoadMore();
            }

            @Override
            public void requestSuccess(String result) {
                Log.i("财务记录", "= " + result);
                lv_content.stopLoadMore();
                try {

                    // JSONObject jsonObject=new JSONObject(result);
                    // lv_content.stopLoadMore();
                    bindHpyBean = new Gson().fromJson(result, BindHpyBean.class);

                    if (bindHpyBean.getRechargeRecord() != null) {
                        if (bindHpyBean.getRechargeRecord().size() > 0) {
                            recordList.addAll(bindHpyBean.getRechargeRecord());
                            adapter.notifyDataSetChanged();
                            if (bindHpyBean.getRechargeRecord().size() < 20) {
                                lv_content.setPullLoadEnable(false);
                            } else {
                                lv_content.setPullLoadEnable(true);
                                currentPage++;
                            }

                        } else {
                            lv_content.setPullLoadEnable(false);
                        }

                    }
                    if (adapter.getCount() == 0) {
                        noData_tv.setVisibility(View.VISIBLE);
                    } else {
                        noData_tv.setVisibility(View.GONE);
                    }
                    if (bindHpyBean.getCode() != 0) {
                        bindHpyBean = null;
                        return;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
