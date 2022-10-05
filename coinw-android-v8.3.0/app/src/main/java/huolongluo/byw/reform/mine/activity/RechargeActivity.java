package huolongluo.byw.reform.mine.activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.bean.ChainBean;
import huolongluo.byw.byw.bean.RechargeChainBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CreateQRImage;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
/**
 * zh充值
 */
public class RechargeActivity extends BaseActivity {
    private static final String TAG = "RechargeActivity";
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    Unbinder unbinder;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.rvChain)
    RecyclerView rvChain;
    @BindView(R.id.tv_address1)
    TextView tv_address1;
    @BindView(R.id.qr_code_iv)
    ImageView qr_code_iv;
    private RechargeChainBean rechargeChainBean;
    private Bitmap qrImage;
    @BindView(R.id.des)
    TextView des;
    private String cnName;
    @BindView(R.id.title_name)
    TextView title_name;
    private boolean iseos;
    @BindView(R.id.memo_ll)
    LinearLayout memo_ll;
    @BindView(R.id.main_tv)
    TextView main_tv;
    private BaseQuickAdapter<ChainBean, BaseViewHolder> adapter;
    private int currentChainPosition=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_activity);
        unbinder = ButterKnife.bind(this);
        back_iv.setOnClickListener(v -> finish());
        initBundle();
        initChainView();
    }

    private void initBundle() {
        Intent intent = getIntent();
        if (null != intent.getExtras()) {
            Bundle extras = intent.getExtras();
            cnName = extras.getString("cnName");
            title_tv.setText(String.format(getString(R.string.recharge_), cnName));
            AppUtils.showDialogForGRINBiz(this, cnName, getString(R.string.str_msg_coin_grin_recharge));
            int coinId = extras.getInt("coinId");
            String shortName = extras.getString("shortName");
            String logo = extras.getString("logo");
            iseos = extras.getBoolean("iseos");
            Logger.getInstance().debug(TAG, "initBundle iseos:" + iseos);
            String mainNetworkSpecification = extras.getString("mainNetworkSpecification");
            showRechargeDialog(coinId + "", shortName, logo, mainNetworkSpecification, !TextUtils.equals("0", mainNetworkSpecification));
        }
    }
    private void initChainView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        adapter=new BaseQuickAdapter<ChainBean, BaseViewHolder>(R.layout.item_chain) {
            @Override
            protected void convert(@NotNull BaseViewHolder helper, ChainBean chainBean) {
                TextView tvChain=helper.itemView.findViewById(R.id.tvChain);
                tvChain.setText(chainBean.getTokenName());
                if(currentChainPosition==helper.getLayoutPosition()){
                    tvChain.setSelected(true);
                }else{
                    tvChain.setSelected(false);
                }
            }
        };
        adapter.setOnItemClickListener((adapter, view, position) -> {
            changeChain(position);
        });
        rvChain.setLayoutManager(layoutManager);
        rvChain.setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void showRechargeDialog(String id, String coinName, String logoUrl, String specification, boolean isShow) {
        if (isShow) {
            main_tv.setVisibility(View.VISIBLE);
            main_tv.setText("(" + specification + ")");
        } else {
            main_tv.setVisibility(View.GONE);
        }
        Map<String, String> params = new HashMap<>();
        params.put("symbol", id);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this, getString(R.string.loading));
        OkhttpManager.postAsync(UrlConstants.GET_RECHARGE_ADDRESS, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager.INSTANCE.dismiss();
                MToast.show(RechargeActivity.this, getString(R.string.dd59), 1);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    rechargeChainBean = GsonUtil.json2Obj(result, RechargeChainBean.class);
                    initChainData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void changeChain(int position){
        if(rechargeChainBean==null||rechargeChainBean.getAddresses()==null||rechargeChainBean.getAddresses().size()==0) return;
        currentChainPosition=position;
        ChainBean chainBean=rechargeChainBean.getAddresses().get(position);
        if(iseos){
            memo_ll.setVisibility(View.VISIBLE);
            tv_address.setText(rechargeChainBean.getDecryptAccessKey());
            tv_address1.setText(chainBean.getDecryptAddress());
        }else{
            memo_ll.setVisibility(View.GONE);
            tv_address.setText(chainBean.getDecryptAddress());
        }
        des.setText(String.format(getString(R.string.recharge_des2), cnName + (cnName.contains("USDT") ? "-" + chainBean.getTokenName() : "")));
        qrImage(chainBean.getDecryptAddress());
//        adapter.addChildClickViewIds();
        adapter.notifyDataSetChanged();
    }
    private void initChainData() {
        if(rechargeChainBean==null||rechargeChainBean.getAddresses()==null||rechargeChainBean.getAddresses().size()==0) return;
        adapter.setList(rechargeChainBean.getAddresses());
        changeChain(0);
    }

    private void qrImage(String address) {
        if (!TextUtils.isEmpty(address)) {
            CreateQRImage mCreateQRImage = new CreateQRImage();
            qrImage = mCreateQRImage.createQRImage(address, Util.dp2px(RechargeActivity.this, 140), Util.dp2px(RechargeActivity.this, 140), false);
            qr_code_iv.setImageBitmap(qrImage);
        }
    }

    @OnClick({R.id.save_qr_code, R.id.copy, R.id.copy1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_qr_code:
                saveImg();
                break;
            case R.id.copy:
                copyAddress(tv_address);
                break;
            case R.id.copy1:
                copyAddress(tv_address1);
                break;
        }
    }

    private void copyAddress(TextView address) {
        ClipboardManager copy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (!android.text.TextUtils.isEmpty(address.getText().toString())) {
            copy.setText(address.getText().toString());
            MToast.show(RechargeActivity.this, getString(R.string.copy_suc), 1);
        }
    }

    private void saveImg() {
        DialogManager.INSTANCE.showProgressDialog(this);
        new Thread(() -> {
            if (qrImage != null) {
                saveImageToGallery(RechargeActivity.this, qrImage);
                runOnUiThread(() -> {
                    DialogManager.INSTANCE.dismiss();
                    MToast.show(RechargeActivity.this, getString(R.string.save_suc), 2);
                });
            } else {
                runOnUiThread(() -> {
                    DialogManager.INSTANCE.dismiss();
                    MToast.show(RechargeActivity.this, getString(R.string.save_fail), 2);
                });
            }
        }).start();
    }

    //保存文件到指定路径
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        //  String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        String storePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            // MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
