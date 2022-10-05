package com.legend.modular_contract_sdk.widget.depthview;


import com.legend.modular_contract_sdk.component.market_listener.Depth.PendingOrder;

import java.util.List;

public interface DepthFutureViewInterface {

    int TYPE_SPOT = 1;
    int TYPE_CONTRACT = 2;

    void setData(List<PendingOrder> depthSells, List<PendingOrder> depthBuys, double maxAmount, String name);

    void setLastIndex(String last, String lastIndex, String change, String markIndex, boolean upOrDown);

    void setUnit(String priceUnit, String amountUnit);

    // 设置类型 1:币币 2:合约 币币时展示折合CNY价格,合约时展示指数价格
    void setType(int type);

    String getName();

    void setAmountTitle(String title);

    void setOnItemClickListener(OnItemClickListener onItemClickListener);

    void setOnIndexPriceClickListener(OnIndexPriceClickListener onIndexPriceClickListener);

    void setLotSize(double lotSize, int amountScale, int priceScale);

    void reset();

    void setMaxSize(int maxSize);

    interface OnItemClickListener {
        void onItemClicked(boolean isBuy,PendingOrder depth);
    }

    interface OnIndexPriceClickListener {
        void onIndexPriceClicked(String indexPrice);
    }

    default void setRowCount(int asksRowCount, int bidsRowCount) {
    }
}
