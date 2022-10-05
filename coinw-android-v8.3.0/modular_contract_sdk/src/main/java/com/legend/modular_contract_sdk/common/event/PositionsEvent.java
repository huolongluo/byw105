package com.legend.modular_contract_sdk.common.event;

import com.legend.modular_contract_sdk.repository.model.PositionAndOrder;

import java.util.List;

public class PositionsEvent {
    private List<PositionAndOrder> positionAndOrders;

    public List<PositionAndOrder> getPositionAndOrders() {
        return positionAndOrders;
    }

    public void setPositionAndOrders(List<PositionAndOrder> positionAndOrders) {
        this.positionAndOrders = positionAndOrders;
    }

    public PositionsEvent(List<PositionAndOrder> positionAndOrders) {
        this.positionAndOrders = positionAndOrders;
    }
}
