package huolongluo.byw.byw.ui.activity.cthistory;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.widget.pulltorefresh.LoadingView;
import huolongluo.byw.widget.pulltorefresh.PageLoadHelper;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/11.
 */

public class CHistoryFragment extends BaseFragment{
    private List<CTHistoryListBean> mData;
    private List<CTHistoryListBean> mData1;
    private CTHistoryAdapter mAdapter = null;

    private PullToRefreshListView lv_content = null;
    private PageLoadHelper mHelper = null;
    private LoadingView mLoadView = null;

    @BindView(R.id.tv_no_data)
    TextView tv_no_data; // 没有数据


    public static CHistoryFragment getInstance()
    {
        Bundle args = new Bundle();
        CHistoryFragment fragment = new CHistoryFragment();
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
        lv_content.setPullLoadEnable(false);
//        lv_content.setOnItemClickListener(itemClickListener);
        if (mHelper == null){
            mHelper = new PageLoadHelper(getActivity(), lv_content,
                    mLoadView);
        }else
            mHelper.setView(lv_content, mLoadView);
        lv_content.setPullLoadEnable(false);

        getData();

        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),CHistoryDetailActivity.class);
                intent.putExtra("sour",1);
                intent.putExtra("logo",mData1.get((int) id).getLogo());
                intent.putExtra("fshortName",mData1.get((int) id).getFshortName());
                intent.putExtra("famount",mData1.get((int) id).getFamount());
                intent.putExtra("fstatus",mData1.get((int) id).getFstatus());
                intent.putExtra("recharge_virtual_address",mData1.get((int) id).getRecharge_virtual_address());
                intent.putExtra("withdraw_virtual_address",mData1.get((int) id).getWithdraw_virtual_address());//提现地址
                intent.putExtra("txid",mData1.get((int) id).getTxid());
                intent.putExtra("fcreateTime",mData1.get((int) id).getFcreateTime());
                intent.putExtra("blockUrl",mData1.get((int) id).getBlockUrl());
                startActivity(intent);
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
        return R.layout.fragment_ct_layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getData(){
        HashMap<String,String> parmas = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        if (loginToken == null){
            return;
        }
        RSACipher rsaCipher = new RSACipher();
        String body = "id="+ URLEncoder.encode("-1")+"&ftype=" +URLEncoder.encode("1")+"&currentPage=" +URLEncoder.encode(mHelper.getPage()+"");
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            parmas.put("body", body1);
            parmas.put("loginToken",loginToken);
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

//        parmas.put("id","-1");//-1代表获取全部数据
//        parmas.put("currentPage", String.valueOf(currentPage));
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_RECHARGELIST, parmas, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {

                CTHistoryBean bean = null;
                try {
                    bean = GsonUtil.json2Obj(result, CTHistoryBean.class);
//                    if (mData == null) {
//                        mData = new ArrayList<>();
//                    }
                    if (mData1 == null){
                        mData1 = new ArrayList<>();
                    }
//                    mData.clear();
//                    mData1.clear();
//                    mData = bean.getList();

                    if (bean.getList().size()>0){
                        if (mHelper.getLoadType() != BaseActivity.LoadType.LOADMORE) {
                            mData1.clear();
                        }
                        for (int i = 0; i < bean.getList().size(); i++) {
//                            if (bean.getList().get(i).getFtype().equals("1")) {
                                mData1.add(bean.getList().get(i));
//                            }
                        }

                        if ( mData1.size() == 0)
                        {
                            lv_content.setVisibility(View.GONE);
                            tv_no_data.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            // TODO: 2017/12/29 显示数据
                            lv_content.setVisibility(View.VISIBLE);
                            tv_no_data.setVisibility(View.GONE);

                        }

                        setAdapter();
                        mHelper.setLoadMore(mData1.size());
                    }else {
                        mHelper.setLoadMore(0);
                        if(mData == null)
//                            mHelper.onEmpty();
                            return;
                    }
                    setAdapter();
                    mHelper.onComplete();
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });
    }

        private void setAdapter(){
        if (mAdapter == null){
            mAdapter = new CTHistoryAdapter(mData1,getActivity());
            lv_content.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        mAdapter = null;
        getData();
    }
}
