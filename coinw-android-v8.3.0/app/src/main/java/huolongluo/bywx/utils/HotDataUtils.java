package huolongluo.bywx.utils;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.Hot;
import huolongluo.byw.model.HotDataResult;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
public class HotDataUtils {
    public static void load() {
        //{"code":"200","data":{"hotsearchList":[{"number":"569","tmid":"121","shortName":"GDP","name":"CNYT","selfselection":0},{"number":"246","tmid":"132","shortName":"MFCC","name":"CNYT","selfselection":0},{"number":"68","tmid":"125","shortName":"HAIC","name":"CNYT","selfselection":0}],"code":0,"value":"操作成功"},"forceUpdate":0,"message":"执行成功"}
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        String loginToken = UserInfoManager.getToken();
        if (TextUtils.isEmpty(loginToken)) {
            loginToken = "0";
        }
        params.put("type", "1");
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", loginToken);
        OKHttpHelper.getInstance().post(UrlConstants.DOMAIN + UrlConstants.HOT_SEARCH, params, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.getInstance().debug("HotDataUtils", "onFailure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null || response.body() == null) {
                    return;
                }
                String result = response.body().string();
                Logger.getInstance().debug("HotDataUtils", "result: " + result);
                HotDataResult hotData = GsonUtil.json2Obj(result, HotDataResult.class);
                if (hotData == null || hotData.data == null || hotData.data.hotsearchList == null) {
                    return;
                }
                Type type = new TypeToken<List<Hot>>() {
                }.getType();
                String json = GsonUtil.obj2Json(hotData.data.hotsearchList, type);
                Logger.getInstance().debug("HotDataUtils", "json: " + json);
                if (!TextUtils.isEmpty(json)) {
                    SPUtils.saveString(BaseApp.getSelf(), AppConstants.COMMON.KEY_HOT_SEARCH, json);
                }
            }
        });
    }
}
