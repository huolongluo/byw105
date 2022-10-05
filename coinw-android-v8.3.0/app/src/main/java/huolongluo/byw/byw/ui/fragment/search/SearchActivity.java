package huolongluo.byw.byw.ui.fragment.search;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
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
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.SelectCoinBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/16.
 */
public class SearchActivity extends BaseActivity {
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.lv_content)
    ListView lv_content;
    @BindView(R.id.tv_result)
    TextView tv_result;
    private List<SelectCoinBean> mData = new ArrayList<>();
    private List<SelectCoinBean> mData1 = new ArrayList<>();
    private SearchAdapter mAdapter = null;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_search_new;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        if (getBundle() != null) {
            //接收从上个界面带过来的数据，在这个界面就不用再做耗时操作
            //   mData = (List<SelectCoinBean>) getBundle().getSerializable("coisList");
        }
        eventClick(tv_cancel).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        getData();
        initData();
        et_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchStr = et_content.getText().toString();
                    if (!TextUtils.isEmpty(searchStr)) {
//                        hideKeyboard();
                        search();
                    }
                    return true;
                }
                return false;
            }
        });
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchStr = et_content.getText().toString();
                if (!TextUtils.isEmpty(searchStr)) {
                    search();
                } else {
                    setAdapter(mData);
                    if (tv_result != null && tv_cancel != null) {
                        tv_result.setVisibility(View.GONE);
                        lv_content.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // CameraMainActivity.tag = 1;
                if (et_content.getText().toString().isEmpty()) {
                    if (mData.size() > i) {
//                        CameraMainActivity.selectCoinTitle = mData.get(i).getCoinName() + "/" +mData.get(i).getCnyName();
//                        CameraMainActivity.selectOptionID = Integer.valueOf(mData.get(i).getId());
//                        CameraMainActivity.selfselection = mData.get(i).getSelfselection();
//                        CameraMainActivity.selectOptionName = mData.get(i).getCoinName();
                        SelectCoinBean coinBean = mData.get(i);
                        Coin coin = new Coin(Integer.valueOf(coinBean.getId()), coinBean.getCoinName(), coinBean.getCnyName(), Integer.valueOf(coinBean.getSelfselection()));
                        TradeDataHelper.getInstance().updateCoin(coin);
                    }
                } else {
                    if (mData.size() > i) {
//                        CameraMainActivity.selectCoinTitle = mData1.get(i).getCoinName() + "/" +mData1.get(i).getCnyName();
//                        CameraMainActivity.selectOptionID = Integer.valueOf(mData1.get(i).getId());
//                        CameraMainActivity.selfselection = mData1.get(i).getSelfselection();
//                        CameraMainActivity.selectOptionName = mData1.get(i).getCoinName();
                        SelectCoinBean coinBean = mData.get(i);
                        Coin coin = new Coin(Integer.valueOf(coinBean.getId()), coinBean.getCoinName(), coinBean.getCnyName(), Integer.valueOf(coinBean.getSelfselection()));
                        TradeDataHelper.getInstance().updateCoin(coin);
                    }
                }
                close();
            }
        });
    }

    //获取币种列表  未登录传0，登录传token
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getData() {
        String loginToken = SPUtils.getLoginToken();
        if (loginToken.equals("")) {
            loginToken = "0";
        }
        HashMap<String, String> params = new HashMap<>();
//        params.put("message",loginToken);
        RSACipher rsaCipher = new RSACipher();
        String body = "message=" + URLEncoder.encode(loginToken);
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
        Log.d("币种列表", loginToken);
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_MARKET_DATA);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_MARKET_DATA, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("币种列表", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (mData == null) {
                        mData = new ArrayList<>();
                    }
                    mData.clear();
                    for (int i = 0; i < data.length(); i++) {
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
                        mData.add(selectCoinBean);
                    }
                    if (mData.size() > 0) {
                        if (tv_result != null && lv_content != null) {
                            tv_result.setVisibility(View.GONE);
                            lv_content.setVisibility(View.VISIBLE);
                        }
                    }
//                            else {
//                                tv_result.setVisibility(View.VISIBLE);
//                                lv_content.setVisibility(View.GONE);
//                            }
                    setAdapter(mData);
                    Log.d("自选数目", mData1.size() + ">>>>>>" + "全部币种" + mData.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData() {
        if (mData.size() > 0) {
            if (tv_result != null && lv_content != null) {
                tv_result.setVisibility(View.GONE);
                lv_content.setVisibility(View.VISIBLE);
            }
            setAdapter(mData);
        }
    }

    private void setAdapter(List<SelectCoinBean> list) {
        if (mAdapter == null) {
            mAdapter = new SearchAdapter(this, list, listener);
            lv_content.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    SearchAdapter.OperationClickListener listener = new SearchAdapter.OperationClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void delete(int position) {
            if (mData.size() <= position) {
                return;
            }
            if (et_content.getText().toString().isEmpty()) {
                if (mData.get(position).getSelfselection().equals("0")) {
                    saveSelf(mData, position);
                } else {
                    Delete(mData, position);
                }
            } else {
                if (mData1.get(position).getSelfselection().equals("0")) {
                    saveSelf(mData1, position);
                } else {
                    Delete(mData1, position);
                }
            }
        }
    };

    //搜索结果
    private void search() {
        String searchStr = et_content.getText().toString();
        mData1.clear();
        for (SelectCoinBean coinOperation : mData) {
            //    OnchainCoinOperation coinOperation = mData.get(i);
            if (coinOperation.getCoinName().contains(searchStr) || coinOperation.getCoinName().toLowerCase().contains(searchStr)) {
                mData1.add(coinOperation);
            }
        }
        if (mData1.size() > 0) {
            tv_result.setVisibility(View.GONE);
            setAdapter(mData1);
        } else {
            tv_result.setVisibility(View.VISIBLE);
            setAdapter(mData1);
        }
    }

    //新增自选
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveSelf(List<SelectCoinBean> list, int position) {

    }

    //删除自选
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Delete(List<SelectCoinBean> list, int position) {

    }
}
