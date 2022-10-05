package com.android.coinw.model.result;
public class Result<T> {
    /**
     * 服务器返回的json数据
     */
    public String message;
    public String code;
    public int forceUpdate;
    public SubResult<T> data;

    public static class SubResult<T> {

        public String value;
        public int code;
        public T data;
    }

}
