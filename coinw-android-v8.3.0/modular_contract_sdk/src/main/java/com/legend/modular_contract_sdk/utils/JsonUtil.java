package com.legend.modular_contract_sdk.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * create by Administrator on 2019/09/25 0025
 */
public class JsonUtil {
    private static Gson sGson;

    @Nullable
    public static <T> T fromJsonToObject(String json, Class<T> classOfT) {
        return fromJsonToObject(json, (Type) classOfT);
    }

    @Nullable
    public static <T> T fromJsonToObject(String json, Type typeOfT) {
        try {
            return getGson().fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Gson getGson() {
        if (sGson == null) {
            synchronized (JsonUtil.class) {
                sGson = new Gson();
            }
        }

        return sGson;
    }

    @Nullable
    public static <T> List<T> fromJsonToList(String json, Class<T> classT) {
        try {
            return getGson().fromJson(json, new ListOfSomething<>(classT));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static JSONObject getJsonObject(String coinFollowStateJson) {
        if (TextUtils.isEmpty(coinFollowStateJson)) {
            return null;
        }

        try {
            return new JSONObject(coinFollowStateJson);
        } catch (JSONException e) {
            return null;
        }
    }

    private JsonUtil() {

    }

    public static String toJson(Object obj) {
        return getGson().toJson(obj);
    }

}
