package huolongluo.byw.view.kline;
/**
 * 次指标
 */
public enum SubIndex {
    MACD("MACD"),
    KDJ("KDJ"),
    RSI("RSI"),
    OBV("OBV"),
    WR("WR"),
    NONE("NONE");

    private final String name;

    SubIndex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
