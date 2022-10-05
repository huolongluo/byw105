package huolongluo.byw.byw.net.okhttp;

/**
 * Created by SLAN on 2016/8/10.
 */

public interface HttpOnNextListener<T>
{
    void onNext(T response);
}

