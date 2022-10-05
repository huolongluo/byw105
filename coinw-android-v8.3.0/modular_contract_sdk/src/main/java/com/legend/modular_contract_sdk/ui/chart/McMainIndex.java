package com.legend.modular_contract_sdk.ui.chart;
/**
 * 主指标
 */
public enum McMainIndex {
    MA("MA"), BOLL("BOLL"), EMA("EMA"), SAR("SAR"), NONE("NONE");

    private final String name;

    McMainIndex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
