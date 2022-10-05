package huolongluo.byw.byw.ui.fragment.maintab01.listen;

import huolongluo.byw.byw.ui.fragment.maintab01.bean.TitleEntity;

/**
 * Created by hy on 2018/8/29 0029.
 */

public interface MarketDataCallback<T, E> {

    void onSuccess(T object);

    void onTitleSuccess(E object);

    void onFail(String failMessage);

    void onTitleSuccess(TitleEntity titleEntity);
}
