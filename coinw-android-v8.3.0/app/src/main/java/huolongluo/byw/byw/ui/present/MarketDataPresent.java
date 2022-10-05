package huolongluo.byw.byw.ui.present;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.legend.model.config.CurrencyPairBean;
import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.model.market.SelfBean;
import com.android.legend.model.market.SelfInfo;
import com.android.legend.ui.market.MarketHelper;
import com.legend.modular_contract_sdk.utils.MathUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TitleEntity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.TradingArea;
import huolongluo.byw.byw.ui.fragment.maintab01.listen.MarketDataCallback;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.kline2.common.KLine2Util;
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants;
import huolongluo.byw.reform.market.MarketCoinSortManager;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.NumberUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.cache.CacheManager;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * Created by hy on 2018/8/30 0030.
 */
public class MarketDataPresent {
    private CopyOnWriteArrayList<MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea>> mDataCallbackList = new CopyOnWriteArrayList<>();
    private List<TradingArea.TradItem> listTitle = new ArrayList<>();
    private List<TitleEntity.FbsBean> listTitle1 = new ArrayList<>();
    private Map<Integer, List<MarketListBean>> listMap = new HashMap<>();
    public static List<Integer> listSelf = new ArrayList<>();//存储币币自选的列表 暂时以最小的改动改


    private MarketDataPresent() {
    }

    private static MarketDataPresent mp;

    public static MarketDataPresent getSelf() {
        if (mp == null) {
            synchronized (MarketDataPresent.class) {
                if (mp == null) {
                    mp = new MarketDataPresent();
                }
            }
        }
        return mp;
    }

    public List<MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea>> getCallBackList() {
        return mDataCallbackList;
    }

    public List<TradingArea.TradItem> getTitleList() {
        if (listTitle.size() > 0) {
            return listTitle;
        }
        return null;
    }

    public List<TitleEntity.FbsBean> getTitleList1() {
        if (listTitle1.size() > 0) {
            return listTitle1;
        }
        return null;
    }

    private Map<Integer, List<MarketListBean>> listMap1 = new HashMap<>();

    public Map<Integer, List<MarketListBean>> getMarketData() {
        return listMap1;
    }

    public void setMp() {
    }

    //自选改变的时候更新列表状态
    public void setSelect(int id, int state) {//0未选，1自选
        if (listMap1.containsKey(0)) {
            List<MarketListBean> listBeans = listMap1.get(0);
            if (listBeans != null) {
                for (MarketListBean bean : listBeans) {
                    //currentCoinId
                    if (bean.getId() == id) {
                        bean.setSelfselection(state);
                    }
                }
            }
        }
    }

    public void setListMap(Map<Integer, List<MarketListBean>> map) {
        if (map != null && map.size() > 0) {
            listMap1 = map;
        }
    }

    public void setDataCallback(MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback) {
        if (!mDataCallbackList.contains(callback)) {
            mDataCallbackList.add(callback);
        }
    }

    public void removeDataCallback(MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback) {
        if (mDataCallbackList.contains(callback)) {
            mDataCallbackList.remove(callback);
        }
    }

    public void clearCallback() {
        if (mDataCallbackList != null) {
            mDataCallbackList.clear();
        }
    }
    //请求title
    public void requestTitle1() {
        listTitle1.clear();
        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("tradingArea");
        if (!TextUtils.isEmpty(result)) {
            try {
                TitleEntity titleEntity = GsonUtil.json2Obj(result, TitleEntity.class);
                if (titleEntity.getCode() == 0) {
                    for (TitleEntity.FbsBean ti : titleEntity.getFbs()) {
                        listTitle1.add(ti);
                    }
                    listMap.clear();
                    listMap.put(0, new ArrayList<>());
                    listMap.put(1, new ArrayList<>());
                    for (int i = 0; i < listTitle1.size(); i++) {
                        listMap.put(listTitle1.get(i).getType(), new ArrayList<>());
                        if (listTitle1.get(i).getAreaCoins().size() != 0) {
                            for (TitleEntity.FbsBean.AreaCoinsBean ti : listTitle1.get(i).getAreaCoins()) {
                                listMap.put(ti.getFid(), new ArrayList<>());
                            }
                        }
                    }
                    for (MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback : mDataCallbackList) {
                        callback.onTitleSuccess(titleEntity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("type", "1");
        //05-28 17:41:11.147 D/AndroidRuntime(25048): Shutting down VM
        //05-28 17:41:12.501 E/AndroidRuntime(25048): FATAL EXCEPTION: main
        //05-28 17:41:12.501 E/AndroidRuntime(25048): Process: huolongluo.byw, PID: 25048
        //05-28 17:41:12.501 E/AndroidRuntime(25048): java.lang.NoClassDefFoundError: huolongluo.byw.helper.OKHttpHelper
        //05-28 17:41:12.501 E/AndroidRuntime(25048):     at huolongluo.byw.util.OkhttpManager.p_getAsync(OkhttpManager.java:428)
        //05-28 17:41:12.501 E/AndroidRuntime(25048):     at huolongluo.byw.util.OkhttpManager.getAsync(OkhttpManager.java:419)
        try {
            OkhttpManager.getAsync(UrlConstants.DOMAIN + UrlConstants.TRADING_AREA_MAIN + "?" + OkhttpManager.encryptGet(map), new OkhttpManager.DataCallBack() {
                @Override
                public void requestFailure(Request request, Exception e, String errorMsg) {
                    for (MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback : MarketDataPresent.getSelf().getCallBackList()) {
                        callback.onFail("fail");
                    }
                    Log.e("交易区", "url= :" + UrlConstants.TRADING_AREA + "    result= :  request");
                    e.printStackTrace();
                }

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void requestSuccess(String result) {
                    listTitle1.clear();
                    Logger.getInstance().debug("交易区11", "url= :" + UrlConstants.TRADING_AREA + "     :  " + result, new Exception());
//                Logger.getInstance().debug("requestTitle1", result);
                    try {
                        TitleEntity titleEntity = GsonUtil.json2Obj(result, TitleEntity.class);
                        if (titleEntity.getCode() == 0) {
                            CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("tradingArea", result);
                            for (TitleEntity.FbsBean ti : titleEntity.getFbs()) {
                                listTitle1.add(ti);
                            }
                            listMap.clear();
                            listMap.put(0, new ArrayList<>());//自选
                            listMap.put(1, new ArrayList<>());//我的币种
                            for (int i = 0; i < listTitle1.size(); i++) {
                                listMap.put(listTitle1.get(i).getType(), new ArrayList<>());
                                if (listTitle1.get(i).getAreaCoins().size() != 0) {
                                    for (TitleEntity.FbsBean.AreaCoinsBean ti : listTitle1.get(i).getAreaCoins()) {
//                                    listMap.put(ti.getFid(), new ArrayList<>());
                                    }
                                }
                            }
                            Iterator<MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea>> iterator = mDataCallbackList.iterator();
                            while (iterator.hasNext()) {
                                MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback = iterator.next();

                                callback.onTitleSuccess(titleEntity);
                            }
                            EventBus.getDefault().post(titleEntity);
                        } else {
                            for (MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback : MarketDataPresent.getSelf().getCallBackList()) {
                                callback.onFail("fail");
                            }
                        }
                    } catch (Exception e) {
                        for (MarketDataCallback<Map<Integer, List<MarketListBean>>, TradingArea> callback : MarketDataPresent.getSelf().getCallBackList()) {
                            callback.onFail("fail");
                        }
                        e.printStackTrace();
                    }
                }
            });
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }
    //pairList所有币对列表，用于查找本地自选
    public static void getSelfList(){
        if(UserInfoManager.isLogin()){
            getSelfHttpList();
        }else{
            getSelfLocalList();
        }
    }
    private static void getSelfLocalList(){
        List<MarketListBean> pairList= MarketHelper.INSTANCE.getPairList();
        if(pairList==null||pairList.size()==0) return;
        listSelf.clear();
        for (int i = 0; i <pairList.size() ; i++) {
            if(SPUtils.getBoolean(BaseApp.getSelf(), KLine2Util.getSelfSpKey(pairList.get(i).getCoinName(),
                    pairList.get(i).getCnyName(), Kline2Constants.TRADE_TYPE_COIN), false)){
                listSelf.add(pairList.get(i).getId());
            }
        }
        EventBus.getDefault().post(new BizEvent.Market.RefreshSelfList());
    }
    public static void getSelfHttpList(){
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.getAsync( UrlConstants.GET_SELF_LIST + "?" + OkhttpManager.encryptGet(params), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    int code1 = jsonObject.getIntValue("code");
                    if(code1==200){
                        SelfInfo selfInfo=GsonUtil.json2Obj(jsonObject.getJSONObject("data").toJSONString(),SelfInfo.class);
                        listSelf.clear();
                        for (SelfBean bean : selfInfo.getSPOT()) {
                            listSelf.add(NumberUtil.toInt(bean.getTrademId()));
                            CurrencyPairBean currencyPairBean=CurrencyPairUtil.getCurrencyPairById(NumberUtil.toLong(bean.getTrademId()));
                            SPUtils.saveBoolean(BaseApp.getSelf(), KLine2Util.getSelfSpKey(currencyPairBean.getLeftCoinName(),
                                    currencyPairBean.getRightCoinName(),Kline2Constants.TRADE_TYPE_COIN), true);
                        }
                        EventBus.getDefault().post(new BizEvent.Market.RefreshSelfList());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public static void exchangeMarket(int type) {

    }
}
