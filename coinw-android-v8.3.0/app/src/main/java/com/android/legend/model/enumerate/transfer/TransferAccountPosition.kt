package com.android.legend.model.enumerate.transfer

enum class TransferAccountPosition(val position: Int) {
    ALL(0),WEALTH_TO_OTC(1),OTC_TO_WEALTH(2),WEALTH_TO_SPOT(3),SPOT_TO_WEALTH(4),
    WEALTH_TO_CONTRACT(5),CONTRACT_TO_WEALTH(6),WEALTH_TO_BDB(7),
    BDB_TO_WEALTH(8),SPOT_TO_OTC(9),OTC_TO_SPOT(10),WEALTH_TO_EARN(11),EARN_TO_WEALTH(12)
}