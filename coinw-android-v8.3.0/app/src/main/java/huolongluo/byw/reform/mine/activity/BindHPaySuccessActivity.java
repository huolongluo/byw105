package huolongluo.byw.reform.mine.activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcodes.utils.PhoneUtils;
import com.google.gson.Gson;

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
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.market.activity.HppChongzhiListActivity;
import huolongluo.byw.reform.mine.adapter.HppAdapter;
import huolongluo.byw.reform.mine.bean.BindHpyBean;
import huolongluo.byw.reform.mine.bean.HppRecord;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import okhttp3.Request;
/**
 * Created by hy on 2018/12/18 0018.
 */
public class BindHPaySuccessActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BindHPaySuccessActivity";
    Unbinder unbinder;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.unbind_bn)
    Button unbind_bn;
    @BindView(R.id.lv_content)
    ListView lv_content;
    @BindView(R.id.all_bn)
    Button all_bn;
    @BindView(R.id.chongzhi_tv)
    TextView chongzhi_tv;
    @BindView(R.id.tixian_tv)
    TextView tixian_tv;
    @BindView(R.id.noData_tv)
    TextView noData_tv;
    private EditText et_code_phone;
    private EditText et_code_google;
    String appId = null;
    List<HppRecord> recordList = new ArrayList<>();
    private HppAdapter adapter;
    private AlertDialog dialog;
    private TextView tv_getCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindhpay_success);
        unbinder = ButterKnife.bind(this);
        back_iv.setOnClickListener(v -> finish());
        title_tv.setText(getString(R.string.f8));
        unbind_bn.setOnClickListener(this);
        all_bn.setOnClickListener(this);
        chongzhi_tv.setOnClickListener(this);
        tixian_tv.setOnClickListener(this);
        lv_content.setEmptyView(noData_tv);
        adapter = new HppAdapter(recordList, this);
        lv_content.setAdapter(adapter);
        appId = getIntent().getStringExtra("appId");
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HppRecord record = adapter.getItem(position);
                Intent intent = new Intent(BindHPaySuccessActivity.this, HppRecordDetailActivity.class);
                intent.putExtra("Status_s", record.getStatus_s());
                intent.putExtra("id", record.getOrderNo());
                intent.putExtra("time", record.getFcreateTime().getTime() + "");
                intent.putExtra("amount", record.getAmount() + "");
                intent.putExtra("coinName", record.getCoinType().getfShortName() + "");
                intent.putExtra("status", record.getType() + "");
                startActivity(intent);
            }
        });
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public Object getLastCustomNonConfigurationInstance() {
        return super.getLastCustomNonConfigurationInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unbind_bn://解绑
                if (!UserInfoManager.getUserInfo().isBindMobil()) {
                    MToast.show(this, getString(R.string.f9), 1);
                    return;
                }
                showUnBindDialog();
                break;
            case R.id.all_bn:
                startActivity(new Intent(this, hPPRecordActivity.class));
                break;
            case R.id.tixian_tv:
                Intent intent = new Intent(this, HppChongzhiListActivity.class);
                intent.putExtra("type", "tixian");
                startActivity(intent);
                break;
            case R.id.chongzhi_tv:
                //Intent intent1 = new Intent(this, HppChongzhiListActivity.class);
                // intent1.putExtra("type", "chongzhi");
                //startActivity(intent1);
                if (BaseApp.isNetAvailable) {
                    checkApp();
                } else {
                    MToast.show(this, getString(R.string.f10), 1);
                }
                break;
        }
    }

    private BindHpyBean bindHpyBean;

    @Override
    protected void onResume() {
        super.onResume();
        bindHyperpay();
    }

    void bindHyperpay() {
        //867394034561976
        String imei = PhoneUtils.getSimOperatorName();
        if (TextUtils.isEmpty(imei)) {
            imei = "012345678";
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params.put("imei", imei);
        netTags.add(UrlConstants.bindHyperpay);
        OkhttpManager.postAsync(UrlConstants.bindHyperpay, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("bindHyperpay", "result= " + result);
                try {
                    // JSONObject jsonObject=new JSONObject(result);
                    bindHpyBean = new Gson().fromJson(result, BindHpyBean.class);
                    if (bindHpyBean.getCode() != 0) {
                        bindHpyBean = null;
                        return;
                    }
                    appId = bindHpyBean.getAppId();
                    if (bindHpyBean.getHyperpayRecord() != null && bindHpyBean.getHyperpayRecord().size() > 0) {
                        recordList.clear();
                        recordList.addAll(bindHpyBean.getHyperpayRecord());
                        adapter.notifyDataSetChanged();
                    }
                    if (!bindHpyBean.isHasHyperpayBind()) {
                        MToast.show(BindHPaySuccessActivity.this, getString(R.string.g1), 1);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void checkApp() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        String packageName = null;
        for (PackageInfo infos : packageInfos) {
            Log.i("infos", "=" + infos.packageName);
            if (TextUtils.equals(infos.packageName, UrlConstants.hppPackapeNameo)) {
                packageName = infos.packageName;
                Log.i("版本", "==  " + infos.versionCode);
                if (infos.versionCode < UrlConstants.hppVerCode) {
                    showInstallDoalog(getString(R.string.g2), getString(R.string.g3));
                    return;
                }
            } else if (TextUtils.equals(infos.packageName, UrlConstants.hppPackapeNamet)) {
                packageName = infos.packageName;
                if (infos.versionCode < UrlConstants.hppVerCode) {
                    showInstallDoalog(getString(R.string.g4), getString(R.string.g5));
                    return;
                }
                //   startActivity(new Intent(BindHPayActivity.this,BindHPaySuccessActivity.class));
            }
            if (!TextUtils.isEmpty(packageName)) {
                showOpenDialog(packageName);
                return;
            }
        }
        showInstallDoalog(getString(R.string.nHyperPay), getString(R.string.g6));
    }

    void showOpenDialog(String pagName) {
        DialogUtils.getInstance().showTwoButtonDialog(this, getString(R.string.g7), getString(R.string.g8), getString(R.string.g9));
        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
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
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    //前提：知道要跳转应用的包名、类名
                    // ComponentName componentName = new ComponentName("com.legendwd.hcash", "com.legendwd.hcash.main.login.LoginActivity");
                    ComponentName componentName = new ComponentName(pagName, "com.legendwd.hyperpay" + ".main.coinw.HPYSendCoinwHistoryActivity");
                    intent.setComponent(componentName);
                    intent.putExtra("appId", appId);
                    intent.putExtra("userName", UserInfoManager.getUserInfo().getLoginName());
                    startActivity(intent);
                } catch (Exception e) {
                    Logger.getInstance().report("跳转HPYSendCoinwHistoryActivity页面异常errorMsg:" + e.getMessage(), TAG + "-StartActivity-HPYSendCoinwHistoryActivity");
                    e.printStackTrace();
                }
            }
        });
    }

    void showInstallDoalog(String text, String rightText) {
        DialogUtils.getInstance().showTwoButtonDialog(this, text, getString(R.string.j8), rightText);
        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
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
                Intent intent = new Intent();
                intent.setData(Uri.parse(UrlConstants.getDownloadHpp(BindHPaySuccessActivity.this)));//Url 就是你要打开的网址
                intent.setAction(Intent.ACTION_VIEW);
                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                Log.i("版本升级", "ResolveInfo= size:" + list.size());
                for (ResolveInfo info : list) {
                    Log.i("版本升级", "ResolveInfo= :" + info.loadLabel(getPackageManager()).toString());
                }
                if (list.size() > 0) {
                    startActivity(intent); //启动浏览器
                    ///浏览器存在
                }
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

    void showUnBindDialog() {
        dialog = new AlertDialog.Builder(this).create();
        View view = View.inflate(this, R.layout.unbindhpp_dialog_view, null);
        dialog.setCancelable(true);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
        tv_getCode = view.findViewById(R.id.tv_getCode);
        TextView googleCode = view.findViewById(R.id.googleCode);
        et_code_phone = view.findViewById(R.id.et_code_phone);
        et_code_google = view.findViewById(R.id.et_code_google);
        googleCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (mClipboardManager.hasPrimaryClip() && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                    CharSequence text = item.getText();
                    if (!TextUtils.isEmpty(text)) {
                        et_code_google.setText(text);
                    }
                }
            }
        });
        tv_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_getCode.setEnabled(false);
                getMsg();
            }
        });
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        LinearLayout google_ll = view.findViewById(R.id.google_ll);
        confirm_tv.setEnabled(true);
        if (UserInfoManager.getUserInfo().isGoogleBind()) {
            google_ll.setVisibility(View.VISIBLE);
        } else {
            google_ll.setVisibility(View.GONE);
        }
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer.onFinish();
                }
            }
        });
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_tv.setEnabled(false);
                unBindHyperpay(confirm_tv);
            }
        });
    }

    void unBindHyperpay(TextView tv) {
        if (et_code_phone == null || TextUtils.isEmpty(et_code_phone.getText().toString())) {
            MToast.show(this, getString(R.string.hx), 1);
            tv.setEnabled(true);
            return;
        }
        if (UserInfoManager.getUserInfo().isGoogleBind() && (et_code_google == null || TextUtils.isEmpty(et_code_google.getText().toString()))) {
            MToast.show(this, getString(R.string.h2), 1);
            tv.setEnabled(true);
            return;
        }
        Map<String, String> map = new HashMap<>();
        if (UserInfoManager.getUserInfo().isGoogleBind())
            map.put("totpCode", et_code_google.getText().toString());
        map.put("phoneCode", et_code_phone.getText().toString());
        map = OkhttpManager.encrypt(map);
        map.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.unBindHyperpay);
        OkhttpManager.postAsync(UrlConstants.unBindHyperpay, map, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                tv.setEnabled(true);
                e.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer.onFinish();
                }
                MToast.show(BindHPaySuccessActivity.this, getString(R.string.h3), 1);
            }

            @Override
            public void requestSuccess(String result) {
                tv.setEnabled(true);
                Log.i("解绑", "result= " + result);
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer.onFinish();
                }
                try {
                    JSONObject object = new JSONObject(result);
                    int code = object.getInt("code");
                    String value = object.optString("value");
                    MToast.show(BindHPaySuccessActivity.this, value + "", 1);
                    if (code == 0) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MToast.show(BindHPaySuccessActivity.this, getString(R.string.h4), 1);
                }
            }
        });
    }

    void getMsg() {
        Map<String, String> map = new HashMap<>();
        //String body = "type=" + URLEncoder.encode("16") + "&phone=" + account;
        map.put("type", "19");
        Map<String, String> params = OkhttpManager.encrypt(map);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.SendMessageCode);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.SendMessageCode, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                MToast.show(BindHPaySuccessActivity.this, getString(R.string.h5), 1);
                tv_getCode.setEnabled(true);
            }

            @Override
            public void requestSuccess(String result) {//{"result":true,"code":-1,"value":"发送频繁,请稍后再试"}
                Log.i("解绑发送验证码", "result= " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.optString("value", "");
                    if (code == 0) {
                        countDownTimer.start();
                    } else {
                        tv_getCode.setEnabled(true);
                    }
                    MToast.show(BindHPaySuccessActivity.this, value, 1);
                } catch (JSONException e) {
                    tv_getCode.setEnabled(true);
                    e.printStackTrace();
                }
            }
        });
    }

    public CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (tv_getCode != null) {
                tv_getCode.setEnabled(false);
                tv_getCode.setText(millisUntilFinished / 1000 + getString(R.string.qs20));
            }
        }

        @Override
        public void onFinish() {
            if (tv_getCode != null) {
                tv_getCode.setEnabled(true);
                tv_getCode.setText(R.string.h6);
            }
        }
    };
}
