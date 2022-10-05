package huolongluo.byw.reform.market.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.UserWalletbn;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.mine.activity.RechargeActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.adapter.LatterAdapter;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/12/10 0010.
 */
public class ChongzhiListActivity extends BaseActivity {
    private static final String TAG = "ChongzhiListActivity";
    Unbinder unbinder;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.right_listview)
    ListView right_listview;
    @BindView(R.id.search_et)
    EditText search_et;
    @BindView(R.id.ll_eye)
    LinearLayout ll_eye;
    @BindView(R.id.iv_open)
    ImageView iv_open;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.tv_assest)
    TextView tv_assest;
    @BindView(R.id.hypercharge_ll)
    LinearLayout hypercharge_ll;
    private UserWalletbn userWalletbn;
    private List<AssetCoinsBean> mAssetcoinsList0 = new ArrayList<>();
    private List<AssetCoinsBean> mAssetcoinsList = new ArrayList<>();
    private List<AssetCoinsBean> searchList = new ArrayList<>();
    private TixianAdapter adapter;
    private LatterAdapter lattetAdapter;
    boolean isShowAll = false;
    Map<String, Integer> mapHide = new HashMap<>();
    Map<String, Integer> map = new HashMap<>();
    private List<String> stringListHide = new ArrayList<>();
    private List<String> stringList = new ArrayList<>();
    private int firstPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tixian_list);
        unbinder = ButterKnife.bind(this);
        tv_assest.setText(R.string.ws39);
        title_tv.setText(R.string.ws40);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lattetAdapter = new LatterAdapter();
        right_listview.setAdapter(lattetAdapter);
        right_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lattetAdapter != null && lattetAdapter.getCount() > position) {
                    String letter = lattetAdapter.getItem(position);
                    int positions;
                    if(isShowAll){
                        positions= mapHide.get(letter);
                    }else{
                        positions= map.get(letter);
                    }

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
                if (bean.isIsRecharge()) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("cnName", bean.getCnName());
                    bundle1.putString("shortName", bean.getShortName());
                    bundle1.putInt("coinId", bean.getId()); // 获取二维码图片用的coinId
                    bundle1.putString("address", bean.getCnName());
                    bundle1.putBoolean("iseos", bean.isIseos());
                    bundle1.putString("logo", bean.getLogo());
                    bundle1.putString("mainNetworkSpecification", bean.getMainNetworkSpecification());
                    Intent intent = new Intent(ChongzhiListActivity.this, RechargeActivity.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);

                 /*   } else {
                        // showMessage("请先设置交易密码", 2);


                        DialogUtils.getInstance().showTwoButtonDialog(ChongzhiListActivity.this, "请先设置交易密码", "取消", "设置");
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
                                if (TextUtils.isEmpty(Share.get().getLogintoken())) {
//                startActivity(LoOrReActivity.class);
                                    startActivity(new Intent(ChongzhiListActivity.this, LoginActivity.class));
                                } else {
                                    startActivity(new Intent(ChongzhiListActivity.this, SafeCentreActivity.class));
                                }
                            }
                        });

                    }*/
                } else {
                    //showMessage("充值暂未开放", 1);
                    DialogUtils.getInstance().showImageDialog(ChongzhiListActivity.this, getString(R.string.ws41), R.mipmap.chongzhi);
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
                firstPosition = firstVisibleItem;
                if (adapter != null && adapter.getCount() > firstVisibleItem) {
                    AssetCoinsBean bean = adapter.getItem(firstVisibleItem);
                    String name = bean.getCnName();
                    if (!TextUtils.isEmpty(name)) {
                        String firsetLitter = name.substring(0, 1).toLowerCase();
                        int posotion ;
                        if(isShowAll){
                            posotion= stringListHide.indexOf(firsetLitter);
                        }else{
                            posotion= stringList.indexOf(firsetLitter);
                        }
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
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text)) {
                    searchList.clear();
                    for (AssetCoinsBean bean : mAssetcoinsList) {
                        if (bean.getCnName().contains(text)) {
                            searchList.add(bean);
                        }
                    }
                    if (adapter != null) {
                        adapter.notifityData(searchList);
                    }
                } else {
                    iv_open.setImageResource(R.drawable.address_show);
                    isShowAll = true;
                    adapter.notifityData(mAssetcoinsList);
                }
            }
        });
        ll_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowAll) {
                    if (adapter != null) {
                        isShowAll = false;
                        iv_open.setImageResource(R.drawable.show);
                        lattetAdapter.setLettterList(stringList);
                        adapter.notifityData(mAssetcoinsList);
                    }
                } else {
                    if (adapter != null) {
                        iv_open.setImageResource(R.drawable.address_show);
                        isShowAll = true;
                        lattetAdapter.setLettterList(stringListHide);
                        adapter.notifityData(mAssetcoinsList0);
                    }
                }
            }
        });
        hypercharge_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChongzhiListActivity.this, HppChongzhiListActivity.class);
                startActivity(intent);
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
                SnackBarUtils.ShowRed(ChongzhiListActivity.this, getString(R.string.ws44));
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
                        mAssetcoinsList = userWalletbn.getUserWallet();
                        Collections.sort(mAssetcoinsList, new Comparator<AssetCoinsBean>() {
                            @Override
                            public int compare(AssetCoinsBean o1, AssetCoinsBean o2) {
                                String a = o1.getCnName().substring(0, 1).toLowerCase();
                                String b = o2.getCnName().substring(0, 1).toLowerCase();
                                return a.compareTo(b);
                            }
                        });
                        stringList = new ArrayList<>();
                        stringListHide = new ArrayList<>();
                        for (int i = 0; i < mAssetcoinsList.size(); i++) {
                            String firstLitter = mAssetcoinsList.get(i).getCnName().substring(0, 1).toLowerCase();
                            if (!map.containsKey(firstLitter)) {
                                stringList.add(firstLitter);
                                map.put(firstLitter, i);
                            }
                            if (mAssetcoinsList.get(i).isIsRecharge()) {
                                mAssetcoinsList0.add(mAssetcoinsList.get(i));
                            }
                        }
                        for (int i = 0; i <mAssetcoinsList0.size() ; i++) {
                            String firstLitter = mAssetcoinsList0.get(i).getCnName().substring(0, 1).toLowerCase();
                            if (!mapHide.containsKey(firstLitter)) {
                                stringListHide.add(firstLitter);
                                mapHide.put(firstLitter, i);
                            }
                        }
                        if(isShowAll){
                            lattetAdapter.setLettterList(stringListHide);
                        }else{
                            lattetAdapter.setLettterList(stringList);
                        }
                        adapter.notifityData(mAssetcoinsList);
                    } else {
                        MToast.show(ChongzhiListActivity.this, getString(R.string.ws45), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
            ImageView hpp_canbind_iv = convertView.findViewById(R.id.hpp_canbind_iv);
            ImageView coinLogo_iv = convertView.findViewById(R.id.coinLogo_iv);
            TextView tv_name = convertView.findViewById(R.id.tv_name);
            TextView sum_tv = convertView.findViewById(R.id.sum_tv);
            RequestOptions ro = new RequestOptions();
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            Glide.with(context).load(bean.getLogo()).apply(ro).into(coinLogo_iv);
            tv_name.setText(bean.getCnName());
            if (bean.isIsHyperpayRecharge()) {
                hpp_canbind_iv.setVisibility(View.VISIBLE);
            } else {
                hpp_canbind_iv.setVisibility(View.GONE);
            }
            hpp_canbind_iv.setVisibility(View.GONE);
            if (bean.isIsRecharge()) {
                sum_tv.setText("");
            } else {
                sum_tv.setText(R.string.ws46);
            }
            return convertView;
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
