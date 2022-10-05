package huolongluo.byw.byw.ui.fragment.search;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.coinw.biz.trade.model.Coin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.bean.SelectCoinBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.adapter.SelectCoinDialogAdapter;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/10.
 */

public class SearchCusFragment extends BaseFragment{

    @BindView(R.id.ll_nologin)
    LinearLayout ll_nologin;
    @BindView(R.id.btn_bus_login)
    TextView btn_bus_login;
    @BindView(R.id.ll_zixuan)
    LinearLayout ll_zixuan;

    private List<SelectCoinBean> mData = new ArrayList<>();
    private List<MarketListBean> mData1 = new ArrayList<>();

    private SelectCoinDialogAdapter mAdapter;

    private ListView listView;

    public static SearchCusFragment getInstance(List<SelectCoinBean> list)
    {
        Bundle args = new Bundle();
        SearchCusFragment fragment = new SearchCusFragment();
        fragment.setArguments(args);
        fragment.mData = list;
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
    @Override
    protected void initViewsAndEvents(View rootView) {
        listView = rootView.findViewById(R.id.lv_content);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                KChartActivity.isRefresh = true;
//                CameraMainActivity.selectOptionName = mData1.get(i).getCoinName();
//                CameraMainActivity.selectOptionID = Integer.valueOf(mData1.get(i).getId());
//                CameraMainActivity.selectCoinTitle = mData1.get(i).getCoinName() + "/" + mData1.get(i).getCnyName();
//                CameraMainActivity.selfselection = mData1.get(i).getSelfselection()+"";
                SelectCoinBean coinBean = mData.get(i);
                Coin coin = new Coin(Integer.valueOf(coinBean.getId()),coinBean.getCoinName(),coinBean.getCnyName(),Integer.valueOf(coinBean.getSelfselection()));
                TradeDataHelper.getInstance().updateCoin(coin);
                getActivity().finish();
            }
        });

//        getData();
      //  initData();
    }

    /**
     * override this method to return content view id of the fragment
     */
    @Override
    protected int getContentViewId() {
        return R.layout.layout_select_coin_item;
    }

    //获取币种列表  未登录传0，登录传token
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getData(){
        String loginToken = SPUtils.getLoginToken();
        if (loginToken.equals("")){
            loginToken = "0";
        }
        HashMap<String,String> params = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = "message="+ URLEncoder.encode(loginToken);
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
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
        Log.d("币种列表",loginToken);
        netTags.add(UrlConstants.DOMAIN + UrlConstants.HOME_MARKET + UrlConstants.ACTION + "getMarketData");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.HOME_MARKET + UrlConstants.ACTION + "getMarketData",
                params, new OkhttpManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, Exception e, String errorMsg) {

                    }

                    @Override
                    public void requestSuccess(String result) {
                        Log.d("币种列表",result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray data = jsonObject.getJSONArray("data");

                            mData.clear();
                            mData1.clear();
                            for (int i = 0;i<data.length();i++){
                                JSONObject jsonObject1 = data.getJSONObject(i);
                                SelectCoinBean selectCoinBean = new SelectCoinBean();
                                selectCoinBean.setSelfselection(jsonObject1.getString("selfselection"));
                                selectCoinBean.setId(jsonObject1.getString("id"));
                                selectCoinBean.setCnyName(jsonObject1.getString("cnyName"));
                                selectCoinBean.setCoinName(jsonObject1.getString("coinName"));
                                selectCoinBean.setCnName(jsonObject1.getString("cnName"));
                                selectCoinBean.setExchangeCode(jsonObject1.getString("exchangeCode"));
                                selectCoinBean.setCurrencySymbol(jsonObject1.getString("currencySymbol"));
                                selectCoinBean.setLatestDealPrice(jsonObject1.getString("LatestDealPrice"));
                                selectCoinBean.setOneDayLowest(jsonObject1.getString("OneDayLowest"));
                                selectCoinBean.setOneDayHighest(jsonObject1.getString("OneDayHighest"));
                                selectCoinBean.setOneDayTotal(jsonObject1.getString("OneDayTotal"));
                                selectCoinBean.setPriceRaiseRate(jsonObject1.getString("priceRaiseRate"));
                                selectCoinBean.setLogo(jsonObject1.getString("logo"));

                                Log.d("coinBeans",jsonObject1.getString("exchangeCode"));
                                Log.d("coinBeans",jsonObject1.getString("selfselection"));
                                Log.d("coinBeans",jsonObject1.getString("id"));
                                Log.d("coinBeans",jsonObject1.getString("LatestDealPrice"));
                                Log.d("coinBeans",jsonObject1.getString("logo"));

                                if (mData == null){
                                    mData = new ArrayList<>();
                                }
                                mData.add(selectCoinBean);
                            }
                            for (int i = 0;i<mData.size();i++){
                                if (mData.get(i).getSelfselection().equals("1")){
                                  //  mData1.add(mData.get(i));
                                }
                            }

                            if (mData1.size() == 0){
                                ll_zixuan.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            }else {
                                ll_zixuan.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                                setAdapter(mData1);
                            Log.d("自选数目",mData1.size()+">>>>>>" + "全部币种" + mData.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void initData(){
        for (int i = 0;i<mData.size();i++){
            if (mData.get(i).getSelfselection().equals("1")){
              //  mData1.add(mData.get(i));
            }
        }

        if (mData1.size() == 0){
            ll_zixuan.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else {
            ll_zixuan.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        setAdapter(mData1);
    }

    private void setAdapter(List<MarketListBean> list){
        mAdapter = new SelectCoinDialogAdapter(getActivity(),list);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserInfo();
    }

    /**
     * 检查用户 是否登陆
     * <p>
     * 刷新界面
     */
    public void checkUserInfo()
    {
        if (!TextUtils.isEmpty(SPUtils.getLoginToken()))
        {
            ll_nologin.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }else {
            ll_nologin.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }
}
