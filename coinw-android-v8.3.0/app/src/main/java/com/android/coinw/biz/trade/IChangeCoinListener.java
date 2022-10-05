package com.android.coinw.biz.trade;
public interface IChangeCoinListener {
    /**
     * 可考虑传币种对象？？
     * @param id
     * @param coinName
     * @param cnyName
     * @param selfStation
     */
    void onChange(String id, String coinName, String cnyName, int selfStation);
}
