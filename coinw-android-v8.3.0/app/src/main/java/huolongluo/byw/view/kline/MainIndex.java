package huolongluo.byw.view.kline;
/**
 * 主指标
 */
public enum MainIndex {
    MA("MA"), BOLL("BOLL"), EMA("EMA"), SAR("SAR"), NONE("NONE");

    private final String name;

    MainIndex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
