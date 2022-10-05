package huolongluo.byw.util.pricing;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.lang.String;
//key模糊匹配 （contains）
public class LikeHashMap<K,V> extends HashMap<K,V> {
    public V get(java.lang.String key){
        Iterator<Entry<K, V>> iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            K k=entry.getKey();
            if(key.contains(k.toString())){

                return entry.getValue();
            }
        }
        return null;
    }
}
