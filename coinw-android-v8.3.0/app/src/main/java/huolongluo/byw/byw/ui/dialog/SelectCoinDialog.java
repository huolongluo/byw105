package huolongluo.byw.byw.ui.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

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

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.SelectCoinBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.adapter.SelectCoinDialogAdapter;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/6.
 */
public class SelectCoinDialog {
    public Dialog dialog;
    private Context context;
    DialogPositiveListener positiveListener;
    DialogNegativeListener negativeListener;
    public OnItemClickListener itemClickListener;
    private SelectCoinDialogAdapter mAdapter;
    private ListView listView;
    private List<SelectCoinBean> mData = new ArrayList<>();
    private List<SelectCoinBean> mData1 = new ArrayList<>();

    public SelectCoinDialog(Context context, List<SelectCoinBean> listBeans) {
        this.context = context;
        this.mData = listBeans;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getSelfselection().equals("1")) {
                mData1.add(mData.get(i));
            }
        }
    }

    public SelectCoinDialog(Context context) {
        this.context = context;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Dialog initDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_select_coin, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog = ResultDialog.creatAlertDialog(context, view);
        listView = view.findViewById(R.id.lv_content);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemClickListener.onItem(i);
                dialog.dismiss();
            }
        });
        TextView tvCus = view.findViewById(R.id.tv_custom);
        TextView tvCnyt = view.findViewById(R.id.tv_cnyt);
        tvCus.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                tvCus.setBackground(context.getResources().getDrawable(R.drawable.market_custom_bg));
                tvCnyt.setBackgroundColor(context.getResources().getColor(R.color.transparent));
//                    getData("0");
                mData1.clear();
                for (int i = 0; i < mData.size(); i++) {
                    if (mData.get(i).getSelfselection().equals("1")) {
                        mData1.add(mData.get(i));
                    }
                }
                setAdapter(mData1);
            }
        });
        tvCnyt.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                tvCnyt.setBackground(context.getResources().getDrawable(R.drawable.market_custom_bg));
                tvCus.setBackgroundColor(context.getResources().getColor(R.color.transparent));
//                    getData("1");
                setAdapter(mData);
            }
        });
        LinearLayout llMain = view.findViewById(R.id.ll_main);
        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
//        getData("1");
        setAdapter(mData);
        tvCus.setTag(-1);
        tvCnyt.setTag(-2);
        return dialog;
    }

    //获取币种列表  未登录传0，登录传token
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getData(String type) {
        String loginToken = SPUtils.getLoginToken();
        if (loginToken.equals("")) {
            loginToken = "0";
        }
        HashMap<String, String> params = new HashMap<>();
        DialogManager.INSTANCE.showProgressDialog(context, "");
//        params.put("type",type);
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
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.HOME_MARKET + UrlConstants.ACTION + "getMarketData", params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("币种列表", result);
                DialogManager.INSTANCE.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    mData.clear();
                    mData1.clear();
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
                        Log.d("coinBeans", jsonObject1.getString("exchangeCode"));
                        Log.d("coinBeans", jsonObject1.getString("selfselection"));
                        Log.d("coinBeans", jsonObject1.getString("id"));
                        Log.d("coinBeans", jsonObject1.getString("LatestDealPrice"));
                        Log.d("coinBeans", jsonObject1.getString("logo"));
                        if (mData == null) {
                            mData = new ArrayList<>();
                        }
                        mData.add(selectCoinBean);
                    }
                    for (int i = 0; i < mData.size(); i++) {
                        if (mData.get(i).getSelfselection().equals("1")) {
                            mData1.add(mData.get(i));
                        }
                    }
                    if (type.equals("0")) {
                        setAdapter(mData1);
                    } else {
                        setAdapter(mData);
                    }
                    Log.d("自选数目", mData1.size() + ">>>>>>" + "全部币种" + mData.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAdapter(List<SelectCoinBean> list) {
        // mAdapter = new SelectCoinDialogAdapter(context,list);
        listView.setAdapter(mAdapter);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            if (positiveListener != null) {
                positiveListener.onClick(tag);
            }
            dialog.dismiss();
        }
    };

    public interface DialogPositiveListener {
        void onClick(int tag);
    }

    public interface DialogNegativeListener {
        void onClick(int tag);
    }

    public interface DialogClickListener {
        void onClick();
    }

    public interface OnItemClickListener {
        void onItem(int i);
    }
}
