package huolongluo.bywx.kline;
public interface IVolume {
    /**
     * 开盘价
     */
    float getOpenPrice();

    /**
     * 收盘价
     */
    float getClosePrice();

    /**
     * 成交量
     */
    float getVolume();


    /**
     * 持仓量
     */
    float getInterest();

    /**
     * 五(月，日，时，分，5分等)均量
     */
    float getMA5Volume();

    /**
     * 十(月，日，时，分，5分等)均量
     */
    float getMA10Volume();
}
