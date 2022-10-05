package huolongluo.byw.byw.ui.activity.walletlist;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.legend.ui.bottomSheetDialogFragment.ModifyRemarksBottomDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.bean.FeeListBean;
import huolongluo.byw.byw.bean.WithdrawBean;
import huolongluo.byw.byw.bean.WithdrawChainBean;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.addaddress.AddAddressNewActivity;
import huolongluo.byw.byw.ui.dialog.EditAddressDialog;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.reform.base.BaseSwipeBackActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/12.
 * zh地址管理列表
 */
public class NewWalletListActivity extends BaseSwipeBackActivity {
    private List<WithdrawChainBean> addressList = new ArrayList<>();
    private List<FeeListBean> feeList = new ArrayList<>();
    private WalletListNewAdapter adapter;
    private EditAddressDialog dialog = null;
    private String shortName;
    private int id;
    private String fromClass;
    @BindView(R.id.lv_content)
    ListView listView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;
    @BindView(R.id.tv_no_data)
    RelativeLayout tv_no_data; // 没有数据
    @BindView(R.id.back_iv)
    ImageView back;
    @BindView(R.id.ll_choose)
    Button ll_add;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.net_errer_view)
    FrameLayout net_errer_view;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    /**
     * bind layout resource file
     */
    Unbinder unbinder;
    private WithdrawBean withdrawBean;
    private String chainName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newwallet_list);
        unbinder = ButterKnife.bind(this);
        initViewsAndEvents();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        getFeeAndAddress();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void initViewsAndEvents() {
        shortName = getIntent().getStringExtra("shortName");
        fromClass = getIntent().getStringExtra("fromClass");
        chainName = getIntent().getStringExtra("chainName");
        id = getIntent().getIntExtra("id", 0);
        title_tv.setText(shortName + " " + getString(R.string.bb93));
        btn_reLoad.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                getFeeAndAddress();
            }
        });
        listView.setEmptyView(tv_no_data);
        listView.addFooterView(LayoutInflater.from(this).inflate(R.layout.footer_space,  listView,false));
        // 设置下拉进度的主题颜色
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        refresh_layout.setOnRefreshListener(() -> {
            // 开始刷新，设置当前为刷新状态
            //swipeRefreshLayout.setRefreshing(true);
            getFeeAndAddress();
            // 这个不能写在外边，不然会直接收起来
            //swipeRefreshLayout.setRefreshing(false);
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewWalletListActivity.this, AddAddressNewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("shortName", shortName);
                bundle.putString("id", String.valueOf(id));
                bundle.putString("fromClass", "NewWalletListActivity");
                String cn = chainName;
                if(withdrawBean!=null){
                    bundle.putSerializable("names", withdrawBean.getChains());
                }
                bundle.putString("chainName", cn);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void setAdapter() {
        adapter = new WalletListNewAdapter(this, addressList, deleteListener, editListener, editListener1);
        listView.setAdapter(adapter);
    }

    WalletListNewAdapter.OperationClickListener2 editListener1 = new WalletListNewAdapter.OperationClickListener2() {
        @Override
        public void edit1(int position) {
            if (TextUtils.equals(fromClass, "CoinTiXianActivity")) {
                EventBus.getDefault().post(new Event.clickCoinAddress(addressList.get(position).getId(), addressList.get(position).getRemark() + "-" + addressList.get(position).getAddress(),addressList.get(position).getIsInternalAddress()));
                finish();
            }
        }
    };
    WalletListNewAdapter.OperationClickListener1 editListener = new WalletListNewAdapter.OperationClickListener1() {
        @Override
        public void edit(int position) {
            ModifyRemarksBottomDialogFragment.Companion.newInstance(new ModifyRemarksBottomDialogFragment.RemarksListener() {
                @Override
                public void getRemarks(@NotNull String remarks) {
                    String id = String.valueOf(addressList.get(position).getId());
                    Edit(id, remarks);
                }
            }).show(getSupportFragmentManager(),"dialog");
        }
    };
    WalletListNewAdapter.OperationClickListener deleteListener = new WalletListNewAdapter.OperationClickListener() {
        @Override
        public void delete(final int position) {
            String loginToken = SPUtils.getLoginToken();
            String id = String.valueOf(addressList.get(position).getId());
            Delete(loginToken, id);
        }
    };

    //获取手续费
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getFeeAndAddress() {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        RSACipher rsaCipher = new RSACipher();
        String body = "coinId=" + URLEncoder.encode(String.valueOf(id));
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", loginToken);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        DialogManager2.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.GET_WITHDRAW_ADDRESS, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager2.INSTANCE.dismiss();
                MToast.show(NewWalletListActivity.this, getString(R.string.bb95), 1);
                SnackBarUtils.ShowRed(NewWalletListActivity.this, errorMsg);
                e.printStackTrace();
                net_errer_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager2.INSTANCE.dismiss();
                net_errer_view.setVisibility(View.GONE);
                if (dialog != null) {
                    dialog.initDialog("").dismiss();
                }
                if (refresh_layout != null && refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);
                    SnackBarUtils.ShowBlue(NewWalletListActivity.this, getString(R.string.bb96));
                }
                try {
                    withdrawBean = GsonUtil.json2Obj(result, WithdrawBean.class);
                    feeList = withdrawBean.getFee_list();
                    addressList=new ArrayList<>();
                    if (!TextUtils.isEmpty(chainName)) {
                        for (WithdrawChainBean beans : withdrawBean.getList()) {
                            if (chainName.equals(beans.getChainName())) {
                                addressList.add(beans);
                            }
                        }
                    } else {
                        addressList=withdrawBean.getList();
                    }
                    setAdapter();
                } catch (Exception e) {
                    SnackBarUtils.ShowBlue(NewWalletListActivity.this, getString(R.string.cc2));
                    e.printStackTrace();
                }
            }
        });
    }

    //删除提币地址
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Delete(String loginToken, String id) {
        HashMap<String, String> pamars = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = "id=" + URLEncoder.encode(id);
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            pamars.put("body", body1);
            pamars.put("loginToken", loginToken);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
//        pamars.put("loginToken",loginToken);
//        pamars.put("id",id);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.DETELE_ADD, pamars, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new org.json.JSONObject(result);
                    boolean result1 = jsonObject.getBoolean("result");
                    if (result1) {
                        SnackBarUtils.ShowBlue(NewWalletListActivity.this, getString(R.string.cc3));
                        EventBus.getDefault().post(new Event.deleteAddress(id));
                        getFeeAndAddress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //虚拟币提现地址备注修改

    private void Edit(String adressId, String remark) {
        Map<String, String> pamars = new HashMap<>();
        pamars.put("adressId", adressId);
        pamars.put("remark", remark);
        pamars = OkhttpManager.encrypt(pamars);
        pamars.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.EDIT_ADD, pamars, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                if (dialog != null) {
                    dialog.dialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new org.json.JSONObject(result);
                    boolean result1 = jsonObject.getBoolean("result");
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (result1) {
                        // showMessage("修改成功",1);
                        SnackBarUtils.ShowBlue(NewWalletListActivity.this, getString(R.string.cc4));
                        getFeeAndAddress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }
}
