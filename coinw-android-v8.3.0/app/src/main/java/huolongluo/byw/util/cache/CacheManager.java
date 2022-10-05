package huolongluo.byw.util.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by haiyang on 2018/9/9.
 */

public class CacheManager {

    private static CacheManager self;
    private static ACache mAcache;

    private static boolean isCache=false;


    public static CacheManager getDefault(Context mContext){



        if(self==null){
            synchronized (CacheManager.class)
            {
                if(self==null){
                    self=new CacheManager();
                }
            }
        }
        if(mAcache==null){
            mAcache = ACache.get(mContext);
        }

        return self;
    }


    private CacheManager() {

    }

    public ACache getAcache(){
       return mAcache;
    }


    public static boolean isCache(){
        return isCache;
    }

    public static void setCacheSuccess(){
        isCache=true;
    }
}
