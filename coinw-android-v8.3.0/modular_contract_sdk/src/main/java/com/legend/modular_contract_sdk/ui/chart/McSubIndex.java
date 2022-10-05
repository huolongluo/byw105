package com.legend.modular_contract_sdk.ui.chart;
/**
 * 次指标
 */
public enum McSubIndex {
    MACD("MACD"),
    KDJ("KDJ"),
    RSI("RSI"),
    OBV("OBV"),
    WR("WR"),
    NONE("NONE");

    private final String name;

    McSubIndex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
