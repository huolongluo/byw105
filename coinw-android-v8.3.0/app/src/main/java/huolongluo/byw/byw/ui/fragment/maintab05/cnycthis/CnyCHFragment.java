package huolongluo.byw.byw.ui.fragment.maintab05.cnycthis;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
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
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.fragment.maintab05.OrderDetailActivity;
import huolongluo.byw.byw.ui.fragment.maintab05.bean.BuyBean;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.pulltorefresh.LoadingView;
import huolongluo.byw.widget.pulltorefresh.PageLoadHelper;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/20.
 */
public class CnyCHFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.no_data_tv)
    RelativeLayout no_data_tv;
    @BindView(R.id.net_error_view)
    FrameLayout net_error_view;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    LinearLayout sort_view;
    @BindView(R.id.status_tv)
    TextView status_tv;
    private PullToRefreshListView lv_content = null;
    private PageLoadHelper mHelper = null;
    private LoadingView mLoadView = null;
    private List<ChongzhiListBean> mData;
    private List<ChongzhiListBean> mData1;
    private CnyCZAdapter adapter;
    private PopupWindow popupWindow;

    public static CnyCHFragment getInstance() {
        Bundle args = new Bundle();
        CnyCHFragment fragment = new CnyCHFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDagger() {
    }

    /**
     * override this method to do operation in the fragment
     *
     * @param rootView
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents(View rootView) {
        sort_view = findView(R.id.sort_view);
        lv_content = rootView.findViewById(R.id.lv_address_content);
        lv_content.setPullToListViewListener(refershListener);
        mLoadView = new LoadingView(getActivity(), lv_content);
        mLoadView.setOnRetryListener(new LoadingView.onRetryListener() {
            @Override
            public void onRetry() {
                // TODO Auto-generated method stub
                mHelper.firstLoad();
                getData();
            }
        });
        btn_reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        lv_content.setPullLoadEnable(false);
//        lv_content.setOnItemClickListener(itemClickListener);
        if (mHelper == null) {
            mHelper = new PageLoadHelper(getActivity(), lv_content,
                    mLoadView);
        } else
            mHelper.setView(lv_content, mLoadView);
        lv_content.setPullLoadEnable(false);
        getData();
        sort_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });
    }

    PullToRefreshListView.PullToListViewListener refershListener = new PullToRefreshListView.PullToListViewListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onRefresh() {
            // TODO Auto-generated method stub
            mHelper.refersh();
            getData();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onLoadMore() {
            // TODO Auto-generated method stub
            mHelper.loadMore();
            getData();
        }
    };

    /**
     * override this method to return content view id of the fragment
     */
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_cny_his;
    }

    String status = "0";
    //获取充值记录

    private void getData() {
        DialogManager.INSTANCE.showProgressDialog(getActivity(), getString(R.string.load));
        Map<String, String> params = new HashMap<>();
        params.put("currentPage", mHelper.getPage() + "");
        params.put("status", status);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.rechargeListCny);
        showProgress("");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.rechargeListCny, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(getActivity(), errorMsg);
                DialogManager.INSTANCE.dismiss();
                net_error_view.setVisibility(View.VISIBLE);
                hideProgress();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                if (null == net_error_view)
                    return;
                hideProgress();
                net_error_view.setVisibility(View.GONE);
                Log.d("充值记录", result);
                ChongzhiBean bean = null;
                try {
                    bean = GsonUtil.json2Obj(result, ChongzhiBean.class);
                    if (mData == null) {
                        mData = new ArrayList<>();
                    }
                    if (bean.getFcapitaloperations().size() > 0) {
                        if (mHelper.getLoadType() != BaseActivity.LoadType.LOADMORE) {
                            mData.clear();
                        }
//                        mData = bean.getFcapitaloperations();
                        for (ChongzhiListBean info : bean.getFcapitaloperations()) {
                            mData.add(info);
                        }
                        if (mData.size() == 0) {
                            no_data_tv.setVisibility(View.VISIBLE);
                            lv_content.setVisibility(View.GONE);
                        } else {
                            no_data_tv.setVisibility(View.GONE);
                            lv_content.setVisibility(View.VISIBLE);
                        }
                        setAdapter();
                        mHelper.setLoadMore(bean.getFcapitaloperations().size());
                    } else {
                        if (mHelper.getLoadType() != BaseActivity.LoadType.LOADMORE) {
                            mData.clear();
                        }
                        setAdapter();
                        mHelper.setLoadMore(0);
                        if (mData.size() == 0) {
                            no_data_tv.setVisibility(View.VISIBLE);
                            lv_content.setVisibility(View.GONE);
                        } else {
                            no_data_tv.setVisibility(View.GONE);
                            lv_content.setVisibility(View.VISIBLE);
                        }
                        if (mData == null)
                            // mHelper.onEmpty();
                            return;
                    }
                    mHelper.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    // setAdapter();
                }
            }
        });
    }

    void showPop() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.c2c_his_pup_item, null, false);
            popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            //    window.setContentView(contentView);
            LinearLayout tv_1 = contentView.findViewById(R.id.tv_1);
            LinearLayout tv_2 = contentView.findViewById(R.id.tv_2);
            LinearLayout tv_3 = contentView.findViewById(R.id.tv_3);
            LinearLayout tv_4 = contentView.findViewById(R.id.tv_4);
            LinearLayout tv_5 = contentView.findViewById(R.id.tv_5);
            LinearLayout shendu_cancle_bn = contentView.findViewById(R.id.shendu_cancle_bn);
            tv_1.setOnClickListener(this);
            tv_2.setOnClickListener(this);
            tv_3.setOnClickListener(this);
            tv_4.setOnClickListener(this);
            tv_5.setOnClickListener(this);
            shendu_cancle_bn.setOnClickListener(this);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            //  popupWindow.showAsDropDown(sort_view,Util.dp2px(getActivity(),-30),Util.dp2px(getActivity(),5));
            popupWindow.showAtLocation(sort_view, Gravity.BOTTOM, 0, 0);
        } else {
            popupWindow.showAtLocation(sort_view, Gravity.BOTTOM, 0, 0);
            //   popupWindow.showAsDropDown(sort_view,Util.dp2px(getActivity(),-30),Util.dp2px(getActivity(),5));
        }
    }

    @Override
    public void onDestroy() {
        AppHelper.dismissPopupWindow(popupWindow);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        AppHelper.dismissPopupWindow(popupWindow);
        if (v.getId() == R.id.tv_1) {
            status_tv.setText(R.string.ee1);
        }
        if (v.getId() == R.id.tv_2) {
            status_tv.setText(R.string.ee2);
        }
        if (v.getId() == R.id.tv_3) {
            status_tv.setText(R.string.ee3);
        }
        if (v.getId() == R.id.tv_4) {
            status_tv.setText(R.string.ee4);
        }
        if (v.getId() == R.id.tv_5) {
            status_tv.setText(R.string.ee5);
        }
        if (v.getId() != R.id.shendu_cancle_bn) {
            status = (String) v.getTag();
            mHelper.firstLoad();
            getData();
        }
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new CnyCZAdapter(getActivity(), mData, clickLenter);
            lv_content.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    CnyCZAdapter.OperationClickListener clickLenter = new CnyCZAdapter.OperationClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void delete(int position) {
            canael(position);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void detail(int position) {
            getDetail(position);
        }
    };

    //取消充值(撤销)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void canael(int position) {
        HashMap<String, String> params = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = "tradeId=" + URLEncoder.encode(String.valueOf(mData.get(position).getFid()));
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", SPUtils.getLoginToken());
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
        netTags.add(UrlConstants.DOMAIN + UrlConstants.cancelRechargeCnySubmit);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.cancelRechargeCnySubmit, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        showMessage(value, 2);
                        getData();
                    } else {
                        showMessage(value, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取单条充值记录
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getDetail(int position) {
        HashMap<String, String> params = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = "tradeId=" + URLEncoder.encode(String.valueOf(mData.get(position).getFid()));
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", SPUtils.getLoginToken());
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
        netTags.add(UrlConstants.DOMAIN + UrlConstants.alipayManualById);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.alipayManualById, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("是否购买成功", result);
                BuyBean buyBean = null;
                try {
                    buyBean = GsonUtil.json2Obj(result, BuyBean.class);
                    if (buyBean.isResult()) {
                        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("orderDetail", buyBean);
                        bundle.putString("fromClass", "CnyCHFragment");
                        bundle.putString("status", mData.get(position).getFstatus());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                    } else {
//                        Toast.makeText(context,"购买失败",Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
    }
}
