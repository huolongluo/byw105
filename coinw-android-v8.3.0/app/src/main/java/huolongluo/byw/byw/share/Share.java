package huolongluo.byw.byw.share;
import com.cocosw.favor.FavorAdapter;

import huolongluo.byw.byw.base.BaseApp;

/**
 * Created by 火龙裸先生 on 2017/8/11 0011.
 */

public class Share
{
    public static ShareData get()
    {
        return new FavorAdapter.Builder(BaseApp.getSelf()).build().create(ShareData.class);
    }
}
