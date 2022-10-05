package huolongluo.byw.byw.ui.fragment.maintab05;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.byw.ui.fragment.maintab05.bean.BuyBean;
import huolongluo.byw.byw.ui.fragment.maintab05.bean.ShanghuBean;
import huolongluo.byw.byw.ui.fragment.maintab05.bean.ShanghuBeanList;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/19.
 */
public class CZFragment extends BaseFragment {
    @BindView(R.id.lv_content)
    ListView lv_content;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;
    private FrameLayout net_error_view;
    private CZAdapter mAdapter;
    private List<ShanghuBeanList> mData;
    private BuyDialog dialog;
    private ShanghuBean bean = null;
    private BuyBean buyBean = null;

    public static CZFragment getInstance() {
        Bundle args = new Bundle();
        CZFragment fragment = new CZFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDagger() {
    }

    /**
     * override this method to do operation in the fragment
     * @param rootView
     */
    @Override
    protected void initViewsAndEvents(View rootView) {
        net_error_view = rootView.findViewById(R.id.net_error_view);
        net_error_view.setVisibility(View.GONE);
        net_error_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        btn_reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.INSTANCE.showProgressDialog(getActivity(), getString(R.string.ee26));
                getData();
            }
        });
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getData();
                            Thread.sleep(8000l);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh_layout.setRefreshing(false);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });



        /*ll_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("History","CZ");
                startActivity(CnyCTHistoryActivity.class,bundle);
            }
        });*/
        //  getData();
    }

    /**
     * override this method to return content view id of the fragment
     */
    @Override
    protected int getContentViewId() {
        // return R.layout.fragment_chongzhi;
        return R.layout.fragment_cz;
    }

    private void setAdapter() {
        mAdapter = new CZAdapter(getActivity(), mData, listener);
        lv_content.setAdapter(mAdapter);
    }

    CZAdapter.OperationClickListener listener = new CZAdapter.OperationClickListener() {
        @Override
        public void isBuy(int position) {
            if (!UserInfoManager.isLogin()) {
                startActivity(LoginActivity.class);
                return;
            } else if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
                if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                    showMessage(getString(R.string.ee17), 2);
                } else {
                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.ee29), getString(R.string.ee30), getString(R.string.ee28));
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
                            startActivity(RenZhengBeforeActivity.class);
                        }
                    });
                }
                return;
            }
            if (bean != null) {
                if (bean.getUnfinished() != 0) {//判断当前有没有未付款订单，有的话不允许在进行其他操作
                    showMessage(getString(R.string.ee31), 2);
                } else {
                    if (bean.getCancellations() > 3) {//判断当前操作是否大于三次，大于的话不允许在进行此操作
                        showMessage(getString(R.string.ee32), 2);
                    } else {
                        BuyDialog buyDialog = new BuyDialog(getActivity());
                        buyDialog.setPositiveListener(new BuyDialog.DialogPositiveListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick() {
                                String loginToken = SPUtils.getLoginToken();
                                String pcId = String.valueOf(mData.get(position).getPcId());
                                String money = buyDialog.etInfo.getText().toString();
                                String fremark = fremark();
                                if (money.isEmpty()) {
                                    showMessage(getString(R.string.ee33), 2);
                                    return;
                                }
                                if (Double.valueOf(money) < 100) {
                                    showMessage(getString(R.string.ee34), 2);
                                    return;
                                }
                                String body = "pcId=" + pcId + "&money=" + money + "&fremark=" + fremark;
                                RSACipher rsaCipher = new RSACipher();
                                try {
                                    String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
                                    Buy(loginToken, body1);
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
                            }
                        });
                        dialog = buyDialog;
                        buyDialog.initDialog("").show();
                    }
                }
            }
        }
    };

    private String fremark() {
        int num = (int) ((Math.random() * 9 + 1) * 10000);
        return String.valueOf(num);
    }

    //获取数据
    private void getData() {
        if(!HttpUtils.isNetworkAvailable(getActivity())){
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        if (android.text.TextUtils.isEmpty(UserInfoManager.getToken())) {
            params.put("loginToken", "0");
        } else {
            params.put("loginToken", UserInfoManager.getToken());
        }
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_CNYT_USER_INFO, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager.INSTANCE.dismiss();
                //   SnackBarUtils.ShowRed(getActivity(),errorMsg);
                //
                e.printStackTrace();
                net_error_view.setVisibility(View.VISIBLE);
                if (refresh_layout != null && refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);
                }
            }

            @Override
            public void requestSuccess(String result) {
                if (refresh_layout != null && refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);
                }
                DialogManager.INSTANCE.dismiss();
                if (net_error_view.getVisibility() == View.VISIBLE) {
                    net_error_view.setVisibility(View.GONE);
                }
                try {
                    bean = GsonUtil.json2Obj(result, ShanghuBean.class);

                    if (bean.getCode() == 0) {
                        if (mData == null) {
                            mData = new ArrayList<>();
                        }
                        mData.clear();
                        mData = bean.getProxyrechargeList();
                        setAdapter();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //点击买入
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Buy(String loginToken, String body) {
        HashMap<String, String> params = new HashMap<>();
        params.put("body", body);
        params.put("loginToken", loginToken);
        showProgress("");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.alipayManual, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                MToast.show(getActivity(), errorMsg, 1);
                hideProgress();
            }

            @Override
            public void requestSuccess(String result) {
                hideProgress();
                Log.d("是否购买成功", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code != 0) {
                        showMessage(value, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    buyBean = GsonUtil.json2Obj(result, BuyBean.class);
                    if (buyBean.getCode() == 0) {
                        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("orderDetail", buyBean);
                        bundle.putString("fromClass", "CZFragment");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        dialog.dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), R.string.ee36, Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
