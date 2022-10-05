package huolongluo.byw.byw.net.okhttp;


import huolongluo.byw.byw.net.BaseResponse;

/**
 * Created by SLAN on 2016/11/16.
 */

public abstract class HttpOnNextListener2<T>
{
    public abstract void onNext(T response);

    public void onFail(BaseResponse errResponse)
    {
    }

    public void onError(Throwable error)
    {
    }
}
