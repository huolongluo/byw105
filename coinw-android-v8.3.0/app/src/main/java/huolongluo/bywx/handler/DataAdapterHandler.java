package huolongluo.bywx.handler;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import huolongluo.byw.byw.ui.fragment.maintab01.bean.RiseFallBean;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.RiseFallResult;
import huolongluo.byw.util.GsonUtil;
import huolongluo.bywx.utils.AppUtils;
/**
 * 数据适配处理器
 * <br>由于历史原因，暂不能大重构
 * <br>目的暂为了解决主线程做大量耗时操作
 */
public class DataAdapterHandler {
    public static Object getObject(String url, String text, Type type) {
        Object obj = GsonUtil.json2Obj(text, type);
        if (obj == null) {
            Logger.getInstance().debug("DataAdapterHandler", "url: " + url + " getObject: " + type.toString(), new Exception());
        }
        return DataAdapterHandler.parse(obj);
    }

    public static Object getObject(String text, Type type) {
        Object obj = GsonUtil.json2Obj(text, type);
        if (obj == null) {
            Logger.getInstance().debug("DataAdapterHandler", "getObject: " + type.toString(), new Exception());
        }
        return DataAdapterHandler.parse(obj);
    }

    public static Object getObject(String url, Reader reader, Type type) {
        Object obj = GsonUtil.json2Obj(reader, type);
        if (obj == null) {
            Logger.getInstance().debug("DataAdapterHandler", "url: " + url + " getObject2: " + type.toString(), new Exception());
            return getObject(AppUtils.getString(reader), type);
        }
        return DataAdapterHandler.parse(obj);
    }

    public static Object getObject(Reader reader, Type type) {
        Object obj = GsonUtil.json2Obj(reader, type);
        if (obj == null) {
            Logger.getInstance().debug("DataAdapterHandler", "getObject2: " + type.toString(), new Exception());
            return getObject(AppUtils.getString(reader), type);
        }
        return DataAdapterHandler.parse(obj);
    }

    public static Object parse(Object obj) {
        if (obj instanceof RiseFallBean) {//Home
            return getObject((RiseFallBean) obj);
        } else if (obj instanceof RiseFallResult) {//
            return getObject((RiseFallResult) obj);
        }
        return obj;
    }

    private static RiseFallBean getObject(RiseFallBean riseFall) {//socket
        if (riseFall == null || riseFall.totalAsset == null || riseFall.totalAsset.isEmpty()) {
            return riseFall;
        }
        if (riseFall.getTotalAsset().size() > 0) {
            List<List<String>> list = riseFall.getTotalAsset();
            Collections.reverse(list);
            riseFall.totalAsset = list;
            riseFall.parseEntry();
            return riseFall;
        }
        riseFall.parseEntry();
        return riseFall;
    }

    private static RiseFallResult getObject(RiseFallResult result) {//http
        if (result == null || result.data == null || result.data.data == null) {
            return result;
        }
        RiseFallBean riseFall = getObject(result.data.data);
        riseFall.parseEntry();
        result.data.data = riseFall;
        return result;
    }
}
