package com.legend.modular_contract_sdk.utils;
import com.legend.modular_contract_sdk.repository.model.Product;
import com.legend.modular_contract_sdk.ui.chart.CandlesTimeUnit;
public class McConstants {
    public static final class COMMON{
        public static final int PER_PAGE_SIZE=20;
        public static final int CONTRACT_TYPE_PERMANENT=1;//合约类型为永续
        public static final String PAIR_RIGHT_NAME="USDT";//币对右币名称
        public static final int DEFAULT_PERCENT_PRECISION = 2;//百分数的默认精度
        public static int CURRENT_PAIR_PRECISION = 4;//当前选中币对的精度
        public static int CURRENT_WAREHOUSE =1;//当前仓位
        public static Product CURRENT_PRODUCT = null;
    }

    public static final class KLINE{
        /**
         * K线图时间参数，数组的位置与UI相对应
         */
        public static final CandlesTimeUnit[] SELECT_TIME_ARRAY = {
                CandlesTimeUnit.MIN1,
                CandlesTimeUnit.MIN15,
                CandlesTimeUnit.HUOR1,
                CandlesTimeUnit.HUOR4,
                CandlesTimeUnit.DAY,
                CandlesTimeUnit.MIN_1,
                CandlesTimeUnit.MIN5,
                CandlesTimeUnit.WEEK
        };
        //全屏的数组按顺序
        public static final CandlesTimeUnit[] SELECT_FULL_TIME_ARRAY = {
                CandlesTimeUnit.MIN1,
                CandlesTimeUnit.MIN_1,
                CandlesTimeUnit.MIN5,
                CandlesTimeUnit.MIN15,
                CandlesTimeUnit.HUOR1,
                CandlesTimeUnit.HUOR4,
                CandlesTimeUnit.DAY,
                CandlesTimeUnit.WEEK
        };
    }
}
