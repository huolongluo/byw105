package com.android.coinw.utils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
public class MapUtils {
    public static Map cloneMap(HashMap dataMap) {
        Map cloneMap = new HashMap();
        Set<Map.Entry<String, Object>> set = dataMap.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            cloneMap.put(entry.getKey(), entry.getValue());
        }
        return cloneMap;
    }

    /**
     * 通过大量实测验证后，采用读取与更新数据分开进行处理（场景：同一集合对象，更新和遍历元素）
     * 由于多线程问题，同一对象读取锁问题的解决。
     * @param dataMap
     * @return
     */
    public static Map cloneMap(ConcurrentHashMap dataMap) {
        Map cloneMap = new ConcurrentHashMap();
        Set<Map.Entry<String, Object>> set = dataMap.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            cloneMap.put(entry.getKey(), entry.getValue());
        }
        return cloneMap;
    }
}
