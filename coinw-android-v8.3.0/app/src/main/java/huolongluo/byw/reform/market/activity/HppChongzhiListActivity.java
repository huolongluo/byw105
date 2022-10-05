package huolongluo.byw.reform.market.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.cointixian.HppTXActivity;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.UserWalletbn;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.mine.bean.BindHpyBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/12/10 0010.
 */
public class HppChongzhiListActivity extends BaseActivity {

    Unbinder unbinder;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.right_listview)
    ListView right_listview;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.tv_assest)
    TextView tv_assest;
    private UserWalletbn userWalletbn;
    private List<AssetCoinsBean> mAssetcoinsList0 = new ArrayList<>();
    private List<AssetCoinsBean> mAssetcoinsList = new ArrayList<>();
    private TixianAdapter adapter;
    private LattetAdapter lattetAdapter;
    Map<String, Integer> map = new HashMap<>();
    private List<String> stringList = new ArrayList<>();
    String type = null;
    String coinName = null;
    String coinId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hpp_chongzhi_list);
        unbinder = ButterKnife.bind(this);
        //
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
        }
        if (TextUtils.equals(type, "tixian")) {
            title_tv.setText(R.string.ws50);
            tv_assest.setText(R.string.ws51);
        } else {
            title_tv.setText(R.string.ws57);
            tv_assest.setText(R.string.ws58);
        }
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lattetAdapter = new LattetAdapter();
        right_listview.setAdapter(lattetAdapter);
        right_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lattetAdapter != null && lattetAdapter.getCount() > position) {
                    String letter = lattetAdapter.getItem(position);
                    int positions = map.get(letter);
                    if (adapter != null && adapter.getCount() > positions) {
                        listView.setSelection(positions);
                        lattetAdapter.setPosition(position);
                        lattetAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        adapter = new TixianAdapter(this, mAssetcoinsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AssetCoinsBean bean = adapter.getItem(position);
                //提现之前判断是否已经设置交易密码，没有设置交易密码的话，提示请先设置交易密码
                if (UserInfoManager.getUserInfo().isHasC2Validate()) {
                    //if (safeCentreBean != null) {
                    if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
                        // 传递可用，冻结金额。币种ID。短名称
                        if (TextUtils.equals(type, "tixian")) {
                            if (bindHpyBean != null) {
                                if (!bindHpyBean.isHasHyperpayBind()) {
                                    MToast.show(HppChongzhiListActivity.this, getString(R.string.ws59), 1);
                                    return;
                                }
                            } else {
                                MToast.show(HppChongzhiListActivity.this, getString(R.string.ws60), 1);
                                bindHyperpay();
                                return;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("shortName", bean.getShortName());
                            bundle.putInt("coinId", bean.getId()); // 品种ID
                            bundle.putString("logo", bean.getLogo());
                            bundle.putBoolean("iseos", bean.isIseos());
                            bundle.putString("memoName", bean.getMemoName() + "");
                            bundle.putDouble("frozen", DoubleUtils.parseDouble(bean.getFrozen())); // 冻结
                            bundle.putDouble("total", DoubleUtils.parseDouble(bean.getTotal())); // 可用
                            bundle.putInt("withdrawDigit", bean.getWithdrawDigit()); // 提现小数位数
                            bundle.putString("minWithdraw", bean.getHyperpayWithDrawMin()); // 提现小数位数
                            bundle.putString("maxWithdraw", bean.getHyperpayWithDrawMax()); // 提现小数位数
                            Intent intent = new Intent(HppChongzhiListActivity.this, HppTXActivity.class);
                            intent.putExtra("bundle", bundle);
                            //提现界面
                            startActivity(intent);
                        } else {
                            //去hpp充值
                            coinName = bean.getCnName();
                            coinId = bean.getId() + "";
                            createRecharge(coinId);
                            //   checkApp();
                        }
                    } else {
                        //..  showMessage("请先设置交易密码", 2);
                        DialogUtils.getInstance().showTwoButtonDialog(HppChongzhiListActivity.this, getString(R.string.ws81), getString(R.string.ws82), getString(R.string.ws83));
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
                                if (TextUtils.isEmpty(SPUtils.getLoginToken())) {
//                startActivity(LoOrReActivity.class);
                                    startActivity(new Intent(HppChongzhiListActivity.this, LoginActivity.class));
                                } else {
                                    startActivity(new Intent(HppChongzhiListActivity.this, SafeCentreActivity.class));
                                }
                            }
                        });
                    }
                } else if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                    MToast.show(HppChongzhiListActivity.this, getString(R.string.ws61), 2);
                } else {
                    // showMessage("请先进行实名认证", 2);
                    DialogUtils.getInstance().showTwoButtonDialog(HppChongzhiListActivity.this, getString(R.string.ws79), getString(R.string.ws80), getString(R.string.ws62));
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
                            startActivity(new Intent(HppChongzhiListActivity.this, RenZhengBeforeActivity.class));
                        }
                    });
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == firstVisibleItem) {
                    return;
                }
                if (adapter != null && adapter.getCount() > firstVisibleItem) {
                    AssetCoinsBean bean = adapter.getItem(firstVisibleItem);
                    String name = bean.getCnName();
                    if (!TextUtils.isEmpty(name)) {
                        String firsetLitter = name.substring(0, 1).toLowerCase();
                        int posotion = stringList.indexOf(firsetLitter);
                        Log.i("weizhi", "posotion==  " + posotion + "    firs = " + firsetLitter);
                        if (lattetAdapter != null) {
                            lattetAdapter.setPosition(posotion);
                            lattetAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        getUserWallet();
        bindHyperpay();
    }

    private BindHpyBean bindHpyBean;

    void bindHyperpay() {
        String imei = DeviceUtils.getImei(getApplicationContext());
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
                Log.i("绑定", "= " + result);
                try {
                    // JSONObject jsonObject=new JSONObject(result);
                    bindHpyBean = new Gson().fromJson(result, BindHpyBean.class);
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

    private String appId;

    void createRecharge(String coinId) {
        Map<String, String> params = new HashMap<>();
        params.put("coinId", coinId);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.createRecharge);
        OkhttpManager.postAsync(UrlConstants.createRecharge, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        String qrcode = jsonObject.getString("qrcode");
                        appId = jsonObject.getString("appId");
                        checkApp(qrcode);
                    } else {
                        String value = jsonObject.getString("value");
                        MToast.show(HppChongzhiListActivity.this, value, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("充值", "== " + result);
            }
        });
    }

    void checkApp(String qrcode) {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        String packageName = null;
        for (PackageInfo infos : packageInfos) {
            Log.i("infos", "=" + infos.packageName);
            if (TextUtils.equals(infos.packageName, UrlConstants.hppPackapeNameo)) {
                packageName = infos.packageName;
                if (infos.versionCode < UrlConstants.hppVerCode) {
                    showInstallDoalog(getString(R.string.ws63), getString(R.string.ws64));
                    return;
                }
            } else if (TextUtils.equals(infos.packageName, UrlConstants.hppPackapeNamet)) {
                //   startActivity(new Intent(BindHPayActivity.this,BindHPaySuccessActivity.class));
                packageName = infos.packageName;
                if (infos.versionCode < UrlConstants.hppVerCode) {
                    showInstallDoalog(getString(R.string.ws65), getString(R.string.ws66));
                    return;
                }
            }
            if (!TextUtils.isEmpty(packageName)) {
                showConfirmDoalog(qrcode, packageName);
                return;
            }
        }
        showInstallDoalog(getString(R.string.nHyperPay), getString(R.string.ws67));
    }

    void showConfirmDoalog(String qrcode, String packageName) {
        DialogUtils.getInstance().showTwoButtonDialog(this, getString(R.string.ws68), getString(R.string.ws69), getString(R.string.ws70));
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
                Intent intent = new Intent(Intent.ACTION_MAIN);
                //前提：知道要跳转应用的包名、类名
                // ComponentName componentName = new ComponentName("com.legendwd.hcash", "com.legendwd.hcash.main.login.LoginActivity");
                ComponentName componentName = new ComponentName(packageName, "com.legendwd.hyperpay" + ".main.coinw.HPYSendCoinwActivity");
                intent.setComponent(componentName);
                // String uniqueId=null;
                // String appId=null;
                if (!TextUtils.isEmpty(appId)) {
                    intent.putExtra("appId", appId);
                    intent.putExtra("coinName", coinName);
                    intent.putExtra("orderId", qrcode);
                    startActivityForResult(intent, 101);
                } else {
                    createRecharge(coinId);
                    MToast.show(HppChongzhiListActivity.this, getString(R.string.ws71), 1);
                }
            }
        });
    }

    void showInstallDoalog(String text, String rightText) {
        DialogUtils.getInstance().showTwoButtonDialog(this, text, getString(R.string.ws72), rightText);
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
                intent.setData(Uri.parse(UrlConstants.getDownloadHpp(HppChongzhiListActivity.this)));//Url 就是你要打开的网址
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

    //获取资产
    private void getUserWallet() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        DialogManager.INSTANCE.showProgressDialog(this, "");
        OkhttpManager.getAsync(UrlConstants.GET_USER_WALLET + "?loginToken=" + UserInfoManager.getToken() + "&" + OkhttpManager.encryptGet(params), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(HppChongzhiListActivity.this, getString(R.string.ws75));
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                Log.i("充值列表", "result=  " + result);
                try {
                    userWalletbn = GsonUtil.json2Obj(result, UserWalletbn.class);
                    if (userWalletbn.getCode() == 0) {
                        mAssetcoinsList0.clear();
                        mAssetcoinsList.clear();
                        List<AssetCoinsBean> list = userWalletbn.getUserWallet();
                        for (AssetCoinsBean bean : list) {
                            if (TextUtils.equals(type, "tixian")) {
                                if (bean.isIsHyperpayWithDraw()) {
                                    mAssetcoinsList.add(bean);
                                }
                            } else {
                                if (bean.isIsHyperpayRecharge()) {
                                    mAssetcoinsList.add(bean);
                                }
                            }
                        }
                        //  mAssetcoinsList = userWalletbn.getUserWallet();
                        Collections.sort(mAssetcoinsList, new Comparator<AssetCoinsBean>() {
                            @Override
                            public int compare(AssetCoinsBean o1, AssetCoinsBean o2) {
                                String a = o1.getCnName().substring(0, 1).toLowerCase();
                                String b = o2.getCnName().substring(0, 1).toLowerCase();
                                return a.compareTo(b);
                            }
                        });
                        stringList = new ArrayList<>();
                        for (int i = 0; i < mAssetcoinsList.size(); i++) {
                            String firstLitter = mAssetcoinsList.get(i).getCnName().substring(0, 1).toLowerCase();
                            if (!map.containsKey(firstLitter)) {
                                stringList.add(firstLitter);
                                map.put(firstLitter, i);
                            }
                        }
                        lattetAdapter.setLettterList(stringList);
                        lattetAdapter.notifyDataSetChanged();
                        adapter.notifityData(mAssetcoinsList);
                        for (AssetCoinsBean bean : mAssetcoinsList) {
                            if (bean.isIsRecharge()) {
                                mAssetcoinsList0.add(bean);
                            }
                        }
                    } else {
                        MToast.show(HppChongzhiListActivity.this, getString(R.string.ws76), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class LattetAdapter extends BaseAdapter {

        int position;
        List<String> lettterList = new ArrayList<>();

        @Override
        public int getCount() {
            return lettterList.size();
        }

        public void setLettterList(List<String> lettterList) {
            this.lettterList.clear();
            this.lettterList.addAll(lettterList);
        }

        @Override
        public String getItem(int position) {
            if (position < 0 || lettterList == null || lettterList.isEmpty()) {
                return "";
            }
            return lettterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.tixian_list_item1, null);
            }
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText(lettterList.get(position));
            if (this.position == position) {
                textView.setBackgroundResource(R.drawable.tixian_dot);
                textView.setTextColor(Color.WHITE);
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.transparent));
                textView.setTextColor(getResources().getColor(R.color.ff262046));
            }
            return convertView;
        }

        public void setPosition(int posotion) {
            position = posotion;
        }
    }

    private static class TixianAdapter extends BaseAdapter {

        Context context;
        private List<AssetCoinsBean> mAssetcoinsList = new ArrayList<>();

        public TixianAdapter(Context context, List<AssetCoinsBean> mAssetcoinsList) {
            this.context = context;
            this.mAssetcoinsList.addAll(mAssetcoinsList);
        }

        public void notifityData(List<AssetCoinsBean> mAssetcoinsList) {
            this.mAssetcoinsList.clear();
            this.mAssetcoinsList.addAll(mAssetcoinsList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAssetcoinsList.size();
        }

        @Override
        public AssetCoinsBean getItem(int position) {
            return mAssetcoinsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AssetCoinsBean bean = mAssetcoinsList.get(position);
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.tixian_list_item, null);
            }
            ImageView coinLogo_iv = convertView.findViewById(R.id.coinLogo_iv);
            ImageView hpp_canbind_iv = convertView.findViewById(R.id.hpp_canbind_iv);
            TextView tv_name = convertView.findViewById(R.id.tv_name);
            TextView sum_tv = convertView.findViewById(R.id.sum_tv);
            RequestOptions ro = new RequestOptions();
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            Glide.with(context).load(bean.getLogo()).apply(ro).into(coinLogo_iv);
            hpp_canbind_iv.setVisibility(View.GONE);
            tv_name.setText(bean.getCnName());
           /* if (bean.isIsRecharge()) {
                sum_tv.setText("");
            } else {
                sum_tv.setText("暂停");
            }*/
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (data != null) {
                boolean status = data.getBooleanExtra("status", false);
                if (status) {
                    MToast.show(this, getString(R.string.ws77), 1);
                    finish();
                } else {
                    MToast.show(this, getString(R.string.ws78), 1);
                }
            }
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
