package huolongluo.byw.byw.ui.fragment.maintab01;

import huolongluo.byw.base.BaseView;
import huolongluo.byw.base.BasePresenter;
import huolongluo.byw.byw.bean.HomeAdvertBean;
import huolongluo.byw.byw.bean.HomeMarketBean;
import huolongluo.byw.byw.bean.VersionInfo;
import rx.Subscription;

/**
 * <p>
 * Created by 火龙裸 on 2017/9/5 0005.
 */

public class MarketContract
{
    interface View extends BaseView
    {
        void getHomeMarketSucce(HomeMarketBean response);
        
        void getHomeAdvertSucce(HomeAdvertBean response);

        void getVersionSucce(VersionInfo result);
    }

    interface Presenter extends BasePresenter<View>
    {
        Subscription getHomeMarket();
        
        Subscription getHomeAdvert(String body);

        Subscription getVersion();
    }
}
