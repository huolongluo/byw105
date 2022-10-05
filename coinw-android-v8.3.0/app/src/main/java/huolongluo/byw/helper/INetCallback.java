package huolongluo.byw.helper;

public interface INetCallback<T> {

    void onSuccess(T t) throws Throwable;

    void onFailure(Exception e) throws Throwable;
}
