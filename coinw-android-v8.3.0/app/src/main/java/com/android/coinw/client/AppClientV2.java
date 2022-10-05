package com.android.coinw.client;
import android.text.TextUtils;

import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
public class AppClientV2 {
    private static AppClientV2 instance = new AppClientV2();
    private volatile ConcurrentHashMap<String, Socket> socketMap = new ConcurrentHashMap<>();

    public static AppClientV2 getInstance() {
        if (instance == null) {
            synchronized (AppClientV2.class) {
                if (instance == null) {
                    instance = new AppClientV2();
                }
            }
        }
        return instance;
    }

    public Socket getSocket(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        try {
            Socket mSocket = IO.socket(url);
            socketMap.put(url, mSocket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
        return null;
    }
}
