package huolongluo.bywx.utils;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;

public class DataUtils {
    private static final String TAG = "DataUtils";
    private static List<HashMap<String, String>> bchList = null;

    /**
     * 合并深度2.0配置
     */
    public static void loadDepthConfig() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);
        params = EncryptUtils.encrypt(params);
        //请求地区数据
        Type type = new TypeToken<SingleResult<HashMap<String, String[]>>>() {
        }.getType();
        INetCallback<SingleResult<HashMap<String, String[]>>> callback = new INetCallback<SingleResult<HashMap<String, String[]>>>() {
            @Override
            public void onSuccess(SingleResult<HashMap<String, String[]>> result) throws Throwable {
                if (result == null) {
                    return;
                }
                Logger.getInstance().debug(TAG, "depthV2!", new Exception());
                HashMap<String, String[]> dataMap = result.data;
                String json = GsonUtil.obj2Json(dataMap, Map.class);
                SPUtils.saveString(BaseApp.getSelf(), AppConstants.COMMON.KEY_DEPTH_CONFIG, json);
                Logger.getInstance().debug(TAG, "depthV2: " + json);
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                Logger.getInstance().debug(TAG, "error", e);
            }
        };
        //TODO 测试专用
//        String url = "http://172.24.249.2:3000/mock/22/api/v1/public?command=returnDepthConfig";
        String url = UrlConstants.ACTION_CONFIG_DEPTH;
        OKHttpHelper.getInstance().get(url, params, callback, type);
    }

    public static void loadHeaderConfig() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);
        params = EncryptUtils.encrypt(params);
        //请求地区数据
        Type type = new TypeToken<SingleResult<HashMap<String, String>>>() {
        }.getType();
        INetCallback<SingleResult<HashMap<String, String>>> callback = new INetCallback<SingleResult<HashMap<String, String>>>() {
            @Override
            public void onSuccess(SingleResult<HashMap<String, String>> result) throws Throwable {
                if (result == null) {
                    return;
                }
                HashMap<String, String> dataMap = result.data;
                String json = GsonUtil.obj2Json(dataMap, Map.class);
                SPUtils.saveString(BaseApp.getSelf(), AppConstants.COMMON.KEY_HEADER_CONFIG, json);
                Logger.getInstance().debug(TAG, "OTC header: " + json);
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                Logger.getInstance().debug(TAG, "error", e);
            }
        };
        String url = UrlConstants.ACTION_CONFIG_HEADER;
        OKHttpHelper.getInstance().post(url, params, callback, type);
    }

    /**
     * 是否打开OTC开关
     *
     * @return
     */
    public static boolean isOpenHeader() {
        String json = SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_HEADER_CONFIG, "");
        if (TextUtils.isEmpty(json)) {
            return true;
        }
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> dataMap = GsonUtil.json2Obj(json, type);
        if (dataMap == null || dataMap.isEmpty()) {
            return true;
        }
        return TextUtils.equals(dataMap.get("enableOtc"), "true");
    }

    /**
     * BCHABC分叉业务
     */
    public static void loadData() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);
        Type type = new TypeToken<SingleResult<List<HashMap<String, String>>>>() {
        }.getType();
        INetCallback<SingleResult<List<HashMap<String, String>>>> callback = new INetCallback<SingleResult<List<HashMap<String, String>>>>() {
            @Override
            public void onSuccess(SingleResult<List<HashMap<String, String>>> result) throws Throwable {
                if (result == null) {
                    return;
                }
                Logger.getInstance().debug(TAG, "BCH!", new Exception());
                List<HashMap<String, String>> dataMap = result.data;
                String json = GsonUtil.obj2Json(dataMap, List.class);
                SPUtils.saveString(BaseApp.getSelf(), AppConstants.COMMON.KEY_BCH_HARD_CONFIG, json);
                Logger.getInstance().debug(TAG, "BCH: " + json);
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                Logger.getInstance().debug(TAG, "error", e);
            }
        };
        //TODO 测试专用
//        String url = "http://172.24.249.2:3000/mock/22/api/v1/public?command=returnDepthConfig";
        String url = UrlConstants.ACTION_BCH_HARD;
        OKHttpHelper.getInstance().get(url, params, callback, type);
    }

    /**
     * BCH分叉业务
     *
     * @param id
     * @return
     */
    public static boolean isBCH(String id) {
        getBCHList();
        if (bchList == null || bchList.isEmpty()) {
            return false;
        }
        for (HashMap<String, String> dataMap : bchList) {
            if (TextUtils.equals(id, dataMap.get("tradeId"))) {
                SPUtils.saveString(BaseApp.getSelf(), id, DateUtils.getCurrentDate(System.currentTimeMillis(), "yyyy/MM/dd"));
                return true;
            }
        }
        return false;
    }

    public static String getBCHUrl(String id) {
        getBCHList();
        if (bchList == null || bchList.isEmpty()) {
            return "";
        }
        for (HashMap<String, String> dataMap : bchList) {
            if (TextUtils.equals(id, dataMap.get("tradeId"))) {
                return dataMap.get("url");
            }
        }
        return "";
    }

    public static String getBCHDesc(String id) {
        getBCHList();
        if (bchList == null || bchList.isEmpty()) {
            return "";
        }
        for (HashMap<String, String> dataMap : bchList) {
            if (TextUtils.equals(id, dataMap.get("tradeId"))) {
                return dataMap.get("desc");
            }
        }
        return "";
    }

    public static boolean isBCHABC(String name) {
        getBCHList();
        if (bchList == null || bchList.isEmpty()) {
            return false;
        }
        for (HashMap<String, String> dataMap : bchList) {
            if (TextUtils.equals(name, dataMap.get("defaultName"))) {
                return true;
            }
        }
        return false;
    }

    private static void getBCHList() {
        //{"code":"200","data":[{"coinName":"BCHA","coinId":"181","desc":"风险提示： Bitcoin Cash分叉具有极大的不确定性，交易或者投资分叉期货代币具有极大的风险，如果某一条链缺乏矿工的支持，很可能无法存续，持有的相关代币将有归零风险。币赢不为任何分叉期货代币的价值作出任何承诺！","url":"https://coinw.zendesk.com/hc/zh-cn/articles/360056569414","defaultName":"BCHABC","tradeId":"206"},{"coinName":"BCHN","coinId":"182","desc":"风险提示： Bitcoin Cash分叉具有极大的不确定性，交易或者投资分叉期货代币具有极大的风险，如果某一条链缺乏矿工的支持，很可能无法存续，持有的相关代币将有归零风险。币赢不为任何分叉期货代币的价值作出任何承诺！","url":"https://coinw.zendesk.com/hc/zh-cn/articles/360056569414","defaultName":"BCHABC","tradeId":"207"}],"forceUpdate":0,"message":"执行成功"}
        //BCH分叉
        String json = SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_BCH_HARD_CONFIG, "");
        Type type = new TypeToken<List<HashMap<String, String>>>() {
        }.getType();
        bchList = GsonUtil.json2Obj(json, type);
    }
}
