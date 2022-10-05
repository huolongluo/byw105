package huolongluo.byw.byw.ui.fragment.maintab05.cnycthis;

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
import huolongluo.byw.byw.net.UrlConstants;
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
public class CnyTXHFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.no_data_tv)
    RelativeLayout no_data_tv;
    @BindView(R.id.sort_view)
    LinearLayout sort_view;
    @BindView(R.id.net_error_view)
    FrameLayout net_error_view;
    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    @BindView(R.id.status_tv)
    TextView status_tv;
    private PullToRefreshListView lv_content = null;
    private PageLoadHelper mHelper = null;
    private LoadingView mLoadView = null;
    private List<ChongzhiListBean> mData;
    private CnyTXAdapter adapter;
    private PopupWindow popupWindow;

    public static CnyTXHFragment getInstance() {
        Bundle args = new Bundle();
        CnyTXHFragment fragment = new CnyTXHFragment();
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
        sort_view.setOnClickListener(this);
        lv_content = rootView.findViewById(R.id.lv_address_content);
        lv_content.setPullToListViewListener(refershListener);
        mLoadView = new LoadingView(getActivity(), lv_content);
        mLoadView.setOnRetryListener(new LoadingView.onRetryListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        // getData();
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
            TextView tv_2_2 = contentView.findViewById(R.id.tv_2_2);
            TextView tv_3_3 = contentView.findViewById(R.id.tv_3_3);
            TextView tv_4_4 = contentView.findViewById(R.id.tv_4_4);
            TextView tv_5_5 = contentView.findViewById(R.id.tv_5_5);
            tv_2_2.setText(R.string.ee6);
            tv_3_3.setText(R.string.ee7);
            tv_4_4.setText(R.string.d8d);
            tv_5_5.setText(R.string.ee9);
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
            popupWindow.showAtLocation(sort_view, Gravity.BOTTOM, 0, 0);
            // popupWindow.showAsDropDown(sort_view,Util.dp2px(getActivity(),-30),Util.dp2px(getActivity(),5));
        } else {
            popupWindow.showAtLocation(sort_view, Gravity.BOTTOM, 0, 0);
            //popupWindow.showAsDropDown(sort_view,Util.dp2px(getActivity(),-30),Util.dp2px(getActivity(),5));
        }
    }

    @Override
    public void onDestroy() {
        AppHelper.dismissPopupWindow(popupWindow);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sort_view:
                showPop();
                break;
            case R.id.tv_1:
            case R.id.tv_2:
            case R.id.tv_3:
            case R.id.tv_4:
            case R.id.tv_5:
                AppHelper.dismissPopupWindow(popupWindow);
                if (v.getId() == R.id.tv_1) {
                    status_tv.setText(R.string.ee10);
                }
                if (v.getId() == R.id.tv_2) {
                    status_tv.setText(R.string.ee11);
                }
                if (v.getId() == R.id.tv_3) {
                    status_tv.setText(R.string.ee7);
                }
                if (v.getId() == R.id.tv_4) {
                    status_tv.setText(R.string.ee13);
                }
                if (v.getId() == R.id.tv_5) {
                    status_tv.setText(R.string.ee14);
                }
                status = (String) v.getTag();
                mHelper.firstLoad();
                getData();
                break;
            case R.id.shendu_cancle_bn:
                AppHelper.dismissPopupWindow(popupWindow);
                break;
        }
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
        return R.layout.fragment_cny_his_tx;
    }

    String status = "0";

    //获取提现记录
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put("status", status);// 1等待提现 2正在处理 3提现成功 4用户撤销
        params.put("currentPage", mHelper.getPage() + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.withdrawListCny);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.withdrawListCny, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(getActivity(), errorMsg);
                net_error_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("提现记录", result);
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
                        mData.addAll(bean.getFcapitaloperations());
                        if (mData.size() == 0) {
                            no_data_tv.setVisibility(View.VISIBLE);
                            lv_content.setVisibility(View.GONE);
                        } else {
                            no_data_tv.setVisibility(View.GONE);
                            lv_content.setVisibility(View.VISIBLE);
                        }
                        setAdapter();
                        mHelper.setLoadMore(mData.size());
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
                        return;
                    }
                    mHelper.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackBarUtils.ShowRed(getActivity(), getString(R.string.ee15));
                }
            }
        });
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new CnyTXAdapter(getActivity(), mData, clickLenter);
            lv_content.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    CnyTXAdapter.OperationClickListener clickLenter = new CnyTXAdapter.OperationClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void delete(int position) {
            canael(position);
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
        netTags.add(UrlConstants.DOMAIN + UrlConstants.cancelWithdrawcny);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.cancelWithdrawcny, params, new OkhttpManager.DataCallBack() {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        adapter = null;
        getData();
    }
}
