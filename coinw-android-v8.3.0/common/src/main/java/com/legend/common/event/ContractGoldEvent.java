package com.legend.common.event;
/**
 * 赠金账户跳转合约使用合约体验金
 */
public class ContractGoldEvent {
    private int id;
    public ContractGoldEvent(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}