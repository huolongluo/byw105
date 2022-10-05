package huolongluo.byw.model;

public class IMResult<T> {
    //{"success":true,"code":0,"data":{"accid":"5d78b8e3a0932547d1d020cc","token":"c3f57cd12562a10f17f3703de91ac2fc"}}
    public String code;
    public boolean success;
    public T data;
}
