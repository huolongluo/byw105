package huolongluo.byw.reform.mine.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.tip.MToast;

/**
 * Created by hy on 2018/8/2 0002.
 * 历史充值记录详情
 */

public class HppRecordDetailActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int getContentViewId() {
        //return R.layout.activity_chistory_detail;
        return R.layout.activity_hpprecord_detail;
    }

    @Override
    protected void injectDagger() {

    }


    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.number_tv)
    TextView number_tv;
    @BindView(R.id.state_tv)
    TextView state_tv;

    @BindView(R.id.dealID_tv)
    TextView dealID_tv;
    @BindView(R.id.date_tv)
    TextView date_tv;
    @BindView(R.id.copeId_tv)
    Button copeId_tv;
    @BindView(R.id.title_tv)
    TextView deTitle_tv;
    @BindView(R.id.back_iv)
    ImageButton back_iv;

    @BindView(R.id.browerOpen_tv)
    Button browerOpen_tv;

    @BindView(R.id.head_iv)
    ImageView head_iv;
    @BindView(R.id.dealID_ll)
    LinearLayout dealID_ll;
    String Status_s;
    String id;
    String time;
    String amount;
    String coinName;
    String mLogoUrl = "";

    @Override
    protected void initViewsAndEvents() {
       /* intent.putExtra("Status_s",record.getStatus_s());
        intent.putExtra("id",record.getOrderNo());
        intent.putExtra("time",record.getFcreateTime()+"");
        intent.putExtra("amount",record.getAmount()+"");
        intent.putExtra("coinName",record.getCoinType().getfShortName()+"");*/
        back_iv.setOnClickListener(this);
        Intent intent = getIntent();

        if (intent != null) {
            Status_s = intent.getStringExtra("Status_s");
            id = intent.getStringExtra("id");
            time = intent.getStringExtra("time");
            amount = intent.getStringExtra("amount");
            coinName = intent.getStringExtra("coinName");
        }


        dealID_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager copy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                if (!android.text.TextUtils.isEmpty(dealID_tv.getText().toString())) {
                    copy.setText(dealID_tv.getText().toString());


                    MToast.show(HppRecordDetailActivity.this, getString(R.string.qs22), 1);
                }

                return true;
            }
        });


        name_tv.setText(coinName);
        state_tv.setText(Status_s);
        number_tv.setText(amount);
        dealID_tv.setText(id);
        if (!android.text.TextUtils.isEmpty(time)) {
            date_tv.setText(DateUtils.format(Long.parseLong(time), "yyyy-MM-dd HH:mm:ss"));
        }


        // DisplayImageUtils.displayImage(head_iv, mLogoUrl, this, R.mipmap.default_head, true, false);
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.default_head);
        ro.centerCrop();
        Glide.with(this).load(mLogoUrl).apply(ro).into(head_iv);
        String sour = intent.getStringExtra("status");
        if (android.text.TextUtils.equals(sour, "1")) {
            deTitle_tv.setText(R.string.qs23);

        } else {
            deTitle_tv.setText(R.string.qs24);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_iv://返回
                finish();
                break;
            case R.id.copeId_tv://复制
               /* ClipData myClip = ClipData.newPlainText("text", mTxid + "");
                ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                myClipboard.setPrimaryClip(myClip);
                //showMessage("复制成功", 2);
                SnackBarUtils.ShowBlue(HppRecordDetailActivity.this,"复制成功");*/
                break;
            case R.id.browerOpen_tv:
                // TODO: 2018/8/2 0002 浏览器打开


                break;
        }
    }
}
