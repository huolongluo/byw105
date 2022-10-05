package com.android.coinw.biz.trade.helper;
import android.text.TextUtils;

import com.android.coinw.biz.trade.model.Coin;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.LimitedTimeTipsResult;
import huolongluo.byw.model.TradeResult;
import huolongluo.byw.reform.trade.bean.TradeInfoBean;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.MemCache;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.sp.SpUtils2;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
import huolongluo.bywx.utils.ValueUtils;
/**
 * 维护当前选中的币种信息
 */
public class TradeDataHelper {
    //默认为CWT
    private Coin coin;
    private Coin etfCoin;
    //默认深度
    private String depth = "-1";
    private int defaultGear = 8;
    //默认档位
    private int gear = defaultGear;
    //ETF
    private String etfDepth = "-1";
    private int etfGear = defaultGear;
    private boolean isETF = false;
    private LimitedTimeTipsResult.LimitedTimeTips etfLimitedTimeTips;
    //
    private LimitedTimeTipsResult.LimitedTimeTips limitedTimeTips;
    private MemCache<String, String> cache = new MemCache<String, String>(10, 1000 * 1500);
    private static TradeDataHelper instance;

    private TradeDataHelper() {
    }

    public static TradeDataHelper getInstance() {
        if (instance == null) {
            synchronized (TradeDataHelper.class) {
                if (instance == null) {
                    instance = new TradeDataHelper();
                }
            }
        }
        return instance;
    }

    public void setETFCoin(boolean isETF) {
        this.isETF = isETF;
    }

    public boolean isETF() {
        return isETF;
    }

    public String getClickNum(boolean clickSell, boolean isSell, double count, int scale) {
        return "";
    }

    public double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 更新输入密码过期时长控制
     */
    public void updatePasswordTime(long time) {
        SpUtils2.saveLong(BaseApp.getSelf(), "password_time", time);
    }

    /**
     * 是否过期
     * @return
     */
    public boolean isExpired() {
        long time = SpUtils2.getLong(BaseApp.getSelf(), "password_time", 0);
        long currentTime = System.currentTimeMillis();
        if (time == 0) {
            return true;
        }
        if (currentTime >= time) {
            return true;
        }
        return false;
    }

    public void updateCoin(Coin coin) {
        Logger.getInstance().debug("TradeDataHelper", "coin: " + GsonUtil.obj2Json(coin, Coin.class), new Exception());
        this.isETF = coin.isETF;
        if (coin.isETF) {
            Logger.getInstance().debug("guocj", "updateCoin: " + GsonUtil.obj2Json(coin, Coin.class));
            this.etfCoin = coin;
        } else {
            this.coin = coin;
        }
    }

    public void updateCnyName(String cnyName, boolean isETF) {
        if (isETF) {
            if (!TextUtils.isEmpty(cnyName) && this.etfCoin != null) {
                Logger.getInstance().debug("guocj", "updateCnyName: " + GsonUtil.obj2Json(coin, Coin.class));
                this.etfCoin.cnyName = cnyName;
            }
        } else {
            if (!TextUtils.isEmpty(cnyName) && this.coin != null) {
                this.coin.cnyName = cnyName;
            }
        }
    }

    public void updateCoin(MarketListBean market) {
        if (market == null) {
            //TODO 处理异常情况
            return;
        }
        Coin marketCoin = new Coin(market.getId(), market.getCoinName(), market.getCnyName(), market.getMycurrency(), isETF);
        //TODO 待大量测试
        if (isETF) {
            Logger.getInstance().debug("guocj", "updateCoin2: " + GsonUtil.obj2Json(market, MarketListBean.class));
            this.etfCoin = marketCoin;
        } else {
            this.coin = marketCoin;
        }
    }

    public Coin getCoin() {
        if (isETF) {
            //TODO 获得缓存获得
            if (this.etfCoin == null || this.etfCoin.id <= 0 || !this.etfCoin.isETF) {
                Coin c = new Coin(0, "--", "--", -1, true);
                c.isETF = true;
                //通过反序列化数据获得ETF币种信息
                String strEtfCoin = SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF, "");
                Logger.getInstance().debug("TradeDataHelper", "data1:" + strEtfCoin);
                if (!TextUtils.isEmpty(strEtfCoin)) {
                    //java.lang.NullPointerException: Attempt to read from field 'int com.android.coinw.biz.trade.model.Coin.a' on a null object reference
                    //    at e.b.a.b.b.a.t.e(TradeDataHelper.java:1)
                    //    at e.b.a.b.b.y.setUserVisibleHint(TradeAbsFragment.java:10)
                    Coin tmp = GsonUtil.json2Obj(strEtfCoin, Coin.class);
                    if (tmp != null) {
                        c = tmp;
                    }
                }
                String strEtfCoinId = AppUtils.getETFCoinId();
                Logger.getInstance().debug("TradeDataHelper", "strEtfCoinId:" + strEtfCoinId);
                if (!TextUtils.isEmpty(strEtfCoinId)) {
                    try {
//                        etfCoin = new Coin();
//                        etfCoin.isETF = true;
                        c.id = Integer.parseInt(strEtfCoinId);
                    } catch (Throwable t) {
                        Logger.getInstance().error(t);
                    }
                }
                this.etfCoin = c;
            }
            return etfCoin;
        } else {
            if (this.coin == null) {
                //默认为CWT
                Coin coin = new Coin();
                coin.id = AppUtils.getDefaultCoinId();
                coin.coinName = AppUtils.getDefaultCoinName();//币名称
                coin.cnyName = AppUtils.getDefaultCnyName();//买币名称
                this.coin = coin;
            }
            return this.coin;
        }
    }

    public Coin getCoin(boolean etf) {
        if (etf) {
            //TODO 获得缓存获得
            if (this.etfCoin == null || !this.etfCoin.isETF || this.etfCoin.id == 0) {
                Coin c = new Coin(172, "BTC3L", "USDT", -1, true);
                //通过反序列化数据获得ETF币种信息
                String strEtfCoin = SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF, "");
                if (!TextUtils.isEmpty(strEtfCoin)) {
                    Coin tmp = GsonUtil.json2Obj(strEtfCoin, Coin.class);
                    if (tmp != null) {
                        c = tmp;
                    }
                }
                String strEtfCoinId = AppUtils.getETFCoinId();
                if (!TextUtils.isEmpty(strEtfCoinId)) {
                    try {
                        c.id = Integer.parseInt(strEtfCoinId);
                        c.isETF = true;
                    } catch (Throwable t) {
                        Logger.getInstance().error(t);
                    }
                }
                this.etfCoin = c;
            }
            return etfCoin;
        } else {
            if (this.coin == null) {
                //默认为CWT
                Coin coin = new Coin();
                coin.id = AppUtils.getDefaultCoinId();
                coin.coinName = AppUtils.getDefaultCoinName();//币名称
                coin.cnyName = AppUtils.getDefaultCnyName();//买币名称
                this.coin = coin;
            }
            return this.coin;
        }
    }

    /**
     * 当前选择的币种ID
     * @return
     */
    public int getId() {
        return getCoin().id;
    }

    public int getId(boolean etf) {
        return getCoin(etf).id;
    }

    /**
     * 当前选择的币种名称
     * @return
     */
    public String getCoinName() {
        return ValueUtils.getString(getCoin().coinName);
    }

    public String getCoinName(boolean isETF) {
        return ValueUtils.getString(getCoin(isETF).coinName);
    }

    /**
     * 当前选择的币种CNYT名称
     * @return
     */
    public String getCnyName() {
        return ValueUtils.getString(getCoin().cnyName);
    }

    public String getCnyName(boolean isETF) {
        return ValueUtils.getString(getCoin(isETF).cnyName);
    }

    public void updateOptionalCoin(int isSelf) {
        if (coin == null) {
            //TODO 处理异常情况
            return;
        }
        if (isETF) {
            etfCoin.isSelf = isSelf;
        } else {
            coin.isSelf = isSelf;
        }
    }

    /**
     * 当前币种是否为自选币种
     * @return
     */
    public boolean isOptionalCoin() {
        return getCoin().isSelf == 1;
    }

    /**
     * 当前币种自选的值
     * @return
     */
    public int getOptionalCoinValue() {
        return getCoin().isSelf;
    }

    /**
     * 更新用户当前选择的深度
     * @param depth
     */
    public void updateDepth(String depth,boolean isETF) {
        if (isETF) {
            if (TextUtils.isEmpty(depth)) {
                this.etfDepth = "-1";
            }
            this.etfDepth = depth;
        } else {
            if (TextUtils.isEmpty(depth)) {
                this.depth = "-1";
            }
            this.depth = depth;
        }
    }

    public String getDepth(boolean isETF) {
        if (isETF) {
            return ValueUtils.getString(this.etfDepth);
        } else {
            return ValueUtils.getString(this.depth);
        }
    }

    /**
     * 更新用户当前选择的档位
     * @param gear
     */
    public void updateGear(String gear, boolean isETF) {
        if (isETF) {
            if (TextUtils.isEmpty(gear)) {
                this.etfGear = defaultGear;
            }
            try {
                this.etfGear = Integer.valueOf(gear);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else {
            if (TextUtils.isEmpty(gear)) {
                this.gear = defaultGear;
            }
            try {
                this.gear = Integer.valueOf(gear);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }
    }

    /**
     * 当前币种的档位
     * @return
     */
    public int getGear() {
        if (isETF) {
            return this.etfGear;
        } else {
            return this.gear;
        }
    }

    public void setLimitedTimeTips(LimitedTimeTipsResult.LimitedTimeTips limitedTimeTips) {
        if (isETF) {
            this.etfLimitedTimeTips = limitedTimeTips;
        } else {
            this.limitedTimeTips = limitedTimeTips;
        }
    }

    public LimitedTimeTipsResult.LimitedTimeTips getLimitedTimeTips() {
        if (isETF) {
            return this.etfLimitedTimeTips;
        } else {
            return limitedTimeTips;
        }
    }

    public void refreshETF(String id) {
        //当etf有数据不再调用接口，解决因为该类isetf的值会因为调用该接口被更新为true导致的bug,该类isetf的逻辑优化为外部传入后可移除该判断
        if(!TextUtils.equals(getCoin(true).coinName,"--"))
        {
            return;
        }
        Logger.getInstance().debug("TradeDataHelper", "refreshETF: " + id);
        //缓存ETF默认币种的基本信息
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", id);
        params.put("mergerType", "-1");
        params = EncryptUtils.encrypt(params);
        Type type = new TypeToken<TradeResult>() {
        }.getType();
        OKHttpHelper.getInstance().postForStringResult(UrlConstants.DOMAIN + UrlConstants.exchangeDepth, params, callback, type);
    }

    protected INetCallback<TradeResult> callback = new INetCallback<TradeResult>() {
        @Override
        public void onSuccess(TradeResult tr) throws Throwable {
            if (tr == null) {
                //TODO 处理异常情况
                return;
            }
            String resultJson = GsonUtil.obj2Json(tr, TradeResult.class);
            Logger.getInstance().debug("TradeDataHelper", "json: " + resultJson);
            if (tr.data == null) {
                return;
            }
            TradeInfoBean result = tr.data.data;
            if (result == null) {
                return;
            }
            if (!TextUtils.isEmpty(AppUtils.getETFCoinId()) && TextUtils.equals(AppUtils.getETFCoinId(), result.getFid() + "")) {
                //序列化
                Coin coin = new Coin();
                coin.id = result.getFid();
                coin.coinName = result.getCoinName();//币名称
                coin.cnyName = result.getCnyName();//买币名称
                coin.isETF = true;
                TradeDataHelper.getInstance().updateCoin(coin);
                String json = GsonUtil.obj2Json(coin, Coin.class);
                SPUtils.saveString(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF, json);
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
//            Logger.getInstance().debug(TAG, "error", e);
            //TODO 处理异常情况
        }
    };
}
