package huolongluo.byw.byw.ui.activity.cthistory;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.ui.activity.AdvertActivity;
import huolongluo.byw.util.tip.SnackBarUtils;

/**
 * Created by hy on 2018/8/2 0002.
 * 历史充值记录详情
 */

public class CHistoryDetailActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int getContentViewId() {
        //return R.layout.activity_chistory_detail;
        return R.layout.activity_chistory_detail;
    }

    @Override
    protected void injectDagger() {

    }

    private String mFamount;
    private String mFstatus;
    private String mRecharge_virtual_address;//充值地址
    private String mWithdraw_virtual_address;//提现地址
    private String mTxid;
    private String mFcreateTime;
    private String mLogoUrl;
    private String mBlockUrl;
    private String fshortName;//币名称
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.number_tv)
    TextView number_tv;
    @BindView(R.id.state_tv)
    TextView state_tv;
    @BindView(R.id.address_tv)
    TextView address_tv;
    @BindView(R.id.dealID_tv)
    TextView dealID_tv;
    @BindView(R.id.date_tv)
    TextView date_tv;
    @BindView(R.id.copeId_tv)
    Button copeId_tv;
    @BindView(R.id.title_tv)
    TextView deTitle_tv;
    @BindView(R.id.llAddress)
    FrameLayout llAddress;

    @BindView(R.id.browerOpen_tv)
    Button browerOpen_tv;
    @BindView(R.id.back_iv)
    ImageView back_ib;
    @BindView(R.id.head_iv)
    ImageView head_iv;
    @BindView(R.id.dealID_ll)
    FrameLayout dealID_ll;
    @BindView(R.id.item1)
    TextView item1;
    @BindView(R.id.item2)
    TextView item2;
    @BindView(R.id.item3)
    TextView item3;
    @BindView(R.id.item4)
    TextView item4;

    @Override
    protected void initViewsAndEvents() {
       /* intent.putExtra("famount",mData1.get((int) id).getFamount());
        intent.putExtra("fstatus",mData1.get((int) id).getFstatus());
        intent.putExtra("recharge_virtual_address",mData1.get((int) id).getRecharge_virtual_address());
        intent.putExtra("txid",mData1.get((int) id).getTxid());
        intent.putExtra("fcreateTime",mData1.get((int) id).getFcreateTime());
         intent.putExtra("sour","2");*/

        Intent intent = getIntent();
        fshortName = getIntent().getStringExtra("fshortName");
        mFamount = intent.getStringExtra("famount");
        mFstatus = intent.getStringExtra("fstatus");
        mRecharge_virtual_address = intent.getStringExtra("recharge_virtual_address");
        mWithdraw_virtual_address = intent.getStringExtra("withdraw_virtual_address");
        mTxid = intent.getStringExtra("txid");
        mLogoUrl = intent.getStringExtra("logo");
        mFcreateTime = intent.getStringExtra("fcreateTime");
        mBlockUrl = intent.getStringExtra("blockUrl");
        int airdrop = intent.getIntExtra("airdrop", 0);
        name_tv.setText(fshortName);
        number_tv.setText(mFamount);
        state_tv.setText(mFstatus);

        dealID_tv.setText(mTxid);
        date_tv.setText(mFcreateTime);
        // DisplayImageUtils.displayImage(head_iv, mLogoUrl, this, R.mipmap.default_head, true, false);
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.coinw_icon);
        ro.centerCrop();
        try {
            int iconId = Integer.parseInt(mLogoUrl);
            Glide.with(this).load(iconId).apply(ro).into(head_iv);
        } catch (Exception e) {
            Glide.with(this).load(mLogoUrl).apply(ro).into(head_iv);
        }

        String sour = intent.getStringExtra("sour");
        if (airdrop == 1) {//空投
            deTitle_tv.setText(getString(R.string.airdrop_detail));
            copeId_tv.setVisibility(View.GONE);
            dealID_ll.setVisibility(View.GONE);
            browerOpen_tv.setVisibility(View.GONE);
            address_tv.setText(mBlockUrl);
        } else if (android.text.TextUtils.equals(sour, "1")) {
            copeId_tv.setVisibility(View.GONE);
            dealID_ll.setVisibility(View.GONE);
            browerOpen_tv.setVisibility(View.GONE);
            deTitle_tv.setText(getString(R.string.czxq));
            address_tv.setText(mRecharge_virtual_address);
        } else if (airdrop == 2) {//返还详情
            deTitle_tv.setText(getString(R.string.return_detail));
            copeId_tv.setVisibility(View.GONE);
            dealID_ll.setVisibility(View.GONE);
            browerOpen_tv.setVisibility(View.GONE);
            address_tv.setText(mBlockUrl);
            item3.setText(getString(R.string.d14) + ":");
            item1.setText(getString(R.string.str_type));
            item2.setText(getString(R.string.num) + ":");
        } else if (airdrop == 3) {//理财
            deTitle_tv.setText(getString(R.string.manage_money_detail));
            llAddress.setVisibility(View.GONE);
        } else {
            deTitle_tv.setText(getString(R.string.txxq));
            copeId_tv.setVisibility(View.VISIBLE);
            browerOpen_tv.setVisibility(View.VISIBLE);
            dealID_ll.setVisibility(View.VISIBLE);

            address_tv.setText(mWithdraw_virtual_address);
        }

        if (android.text.TextUtils.isEmpty(mTxid)) {
            dealID_tv.setText("-- --");
            copeId_tv.setVisibility(View.GONE);
            browerOpen_tv.setVisibility(View.GONE);
        } else if (mTxid.contains("coinw") || mTxid.contains("coinW") || mTxid.contains("coin")) {
            dealID_tv.setText(R.string.internal_transfer);
            copeId_tv.setVisibility(View.GONE);
            browerOpen_tv.setVisibility(View.GONE);
        } else {
            dealID_tv.setText(mTxid);
            copeId_tv.setVisibility(View.VISIBLE);
            browerOpen_tv.setVisibility(View.VISIBLE);
        }

        back_ib.setOnClickListener(this);
        copeId_tv.setOnClickListener(this);
        browerOpen_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_iv://返回
                finish();
                break;
            case R.id.copeId_tv://复制
                ClipData myClip = ClipData.newPlainText("text", mTxid + "");
                ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                myClipboard.setPrimaryClip(myClip);
                //showMessage("复制成功", 2);
                SnackBarUtils.ShowBlue(CHistoryDetailActivity.this, getString(R.string.copy_suc));
                break;
            case R.id.browerOpen_tv:
                // TODO: 2018/8/2 0002 浏览器打开
                if (!android.text.TextUtils.isEmpty(mBlockUrl)) {

//                    Intent intent=new Intent(CHistoryDetailActivity.this, NormanWebviewActivity.class);
//                    intent.putExtra("url",mBlockUrl + mTxid);
//                    intent.putExtra("title","提现查询");
//
//                 //   Uri uri = Uri.parse(mBlockUrl + mTxid);
//                  //  Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.txcx)); // 公告标题
                    bundle.putString("id", mBlockUrl + mTxid); // 对应点击的公告id
                    startActivity(AdvertActivity.class, bundle);
                }

                break;
        }
    }
}
