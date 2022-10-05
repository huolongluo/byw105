package com.netease.nim.uikit.business.session.fragment;

import android.content.Context;

import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

public class IMMessageEntity {
    private static Context context;

    public static void init(Context context) {
        IMMessageEntity.context = context.getApplicationContext();
    }

    private static List<IMMessage> messages = null;

    public static List<IMMessage> getMessages() {
        if (null == messages) {
            messages = new ArrayList<>();
        }
//        else {
//            String unReadMessageInfo = SPUtils.getString(context, "UnReadMessageInfo", "");
//            Type type = new TypeToken<List<com.netease.nimlib.q.a>>() {}.getType();
//            List<IMMessage> msgList = GsonUtil.json2Obj(unReadMessageInfo, type);
//            if (null != msgList && msgList.size() != 0) {
//                messages.addAll(msgList);
//            }
//        }
        return messages;
    }

    private static void setMessages(List<IMMessage> messages) {
        IMMessageEntity.messages = messages;
    }

    public static void saveUnReadMessageInfo() {
//        Type type = new TypeToken<List<com.netease.nimlib.q.a>>() {}.getType();
//        SPUtils.saveString(context, "UnReadMessageInfo", GsonUtil.obj2Json(messages, type));
    }
}
