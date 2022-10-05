package huolongluo.byw.byw.ui.fragment.latestdeal;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.coinw.biz.trade.helper.TradeDataHelper;

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
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/10.
 */

public class LatestDealFragment extends BaseFragment{
    @BindView(R.id.rv_cancel)
    RecyclerView rv_cancel;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;
    @BindView(R.id.lv_content)
    ListView listView;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    @BindView(R.id.net_error_view)
    FrameLayout net_error_view;


    private List<LatestListBean> mData = new ArrayList<>();
//    private LatestAdapter mAdapter = null;

    private LatestListAdapter mAdapter;
    public static LatestDealFragment getInstance()
    {
        Bundle args = new Bundle();
        LatestDealFragment fragment = new LatestDealFragment();
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

        net_error_view.setVisibility(View.GONE);
        net_error_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getData();
            }
        });
       // getData();
        tvAmount.setText("("+TradeDataHelper.getInstance().getCnyName()+")");

        // 设置下拉进度的主题颜色
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        refresh_layout.setOnRefreshListener(() ->
        {
            // 开始刷新，设置当前为刷新状态
            //swipeRefreshLayout.setRefreshing(true);



            getData();
//            subscription = cancelOrderPresent.getCancelData(Share.get().getLogintoken(), 0, CameraMainActivity.selectOptionID + "", 1);
            // System.out.println(Thread.currentThread().getName());

            // 这个不能写在外边，不然会直接收起来
            //swipeRefreshLayout.setRefreshing(false);
        });
//        subscription = cancelOrderPresent.getCancelData(Share.get().getLogintoken(), 0, CameraMainActivity.selectOptionID + "", 1);
    }


    //获取数据
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getData(){
        HashMap<String,String> params = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = "message="+ URLEncoder.encode(String.valueOf(TradeDataHelper.getInstance().getId()));
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
//             params.put("message", String.valueOf(CameraMainActivity.selectOptionID));
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
        netTags.add(UrlConstants.DOMAIN+UrlConstants.GETSUCCESS_DEATIL + UrlConstants.ACTION + "getSuccessDetails");
        OkhttpManager.postAsync(UrlConstants.DOMAIN+UrlConstants.GETSUCCESS_DEATIL + UrlConstants.ACTION + "getSuccessDetails", params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {


                 if(refresh_layout.isRefreshing()){
                     refresh_layout.setRefreshing(false);
                     SnackBarUtils.ShowBlue(getActivity(), getString(R.string.c4));
                 }else {
                     SnackBarUtils.ShowRed(getActivity(), getString(R.string.c5));
                 }

                if(mAdapter!=null){
                    if(mAdapter.getCount()>0){
                        net_error_view.setVisibility(View.GONE);
                    }else {
                        net_error_view.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    net_error_view.setVisibility(View.VISIBLE);
                }
                hideProgress();
            }

            @Override
            public void requestSuccess(String result) {
                hideProgress();
//                Log.d("最新成交",result);
                LatestBean latestBean = null;
                   if(net_error_view.getVisibility()!=View.GONE){
                       net_error_view.setVisibility(View.GONE);
                   }


                try {
                    latestBean = GsonUtil.json2Obj(result, LatestBean.class);
                    if(refresh_layout.isRefreshing()){
                        refresh_layout.setRefreshing(false);
                        SnackBarUtils.ShowBlue(getActivity(), getString(R.string.c7));
                    }
                    if (mData == null){
                        mData = new ArrayList<>();
                    }
                        mData.clear();

                    mData.addAll(latestBean.getRows());

                    if (mData.size() == 0){
                        listView.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }else {
                        listView.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);
                    }

                    setAdapter();

                } catch (Exception e) {
                    if(refresh_layout.isRefreshing()){
                        refresh_layout.setRefreshing(false);
                        SnackBarUtils.ShowBlue(getActivity(), getString(R.string.c8));
                    }
                    if(mAdapter!=null){
                        if(mAdapter.getCount()>0){
                            net_error_view.setVisibility(View.GONE);
                        }else {
                            net_error_view.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        net_error_view.setVisibility(View.VISIBLE);
                    }
                    e.printStackTrace();
                }
            }
        });
    }


    private void setAdapter(){
        mAdapter = new LatestListAdapter(mData,mContext);
        listView.setAdapter(mAdapter);
    }

    /**
     * override this method to return content view id of the fragment
     */
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_latest_deal;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        getData();
        if (tvAmount != null){
            tvAmount.setText("("+TradeDataHelper.getInstance().getCnyName()+")");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            getData();
            if (tvAmount != null){
                tvAmount.setText("("+TradeDataHelper.getInstance().getCnyName()+")");
            }
        }

    }
}
