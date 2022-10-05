package huolongluo.bywx;
public interface OnResultCallback<T> {
    void onResult(T result, String[] params);

    void onFail();
}
