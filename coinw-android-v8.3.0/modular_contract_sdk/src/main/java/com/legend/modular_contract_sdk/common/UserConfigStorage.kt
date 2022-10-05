package com.legend.modular_contract_sdk.common

import android.content.Context
import android.text.TextUtils
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.ui.chart.CandlesTimeUnit
import com.legend.modular_contract_sdk.ui.chart.McMainIndex
import com.legend.modular_contract_sdk.ui.chart.McSubIndex

object UserConfigStorage {

    const val KLINE_MAIN_INDEX = "kLineMainIndex"
    const val KLINE_SUB_INDEX = "kLineSubIndex"
    private var selectTime:CandlesTimeUnit?=null

    private val sharedPreferences by lazy {
        ModularContractSDK.context?.getSharedPreferences("mc_sdk_user_config", Context.MODE_PRIVATE)
    }
    fun saveString(key: String?, value: String?) {
        sharedPreferences?.edit()?.putString(key, value)?.commit()
    }
    fun saveInt(key: String?, value: Int) {
        sharedPreferences?.edit()?.putInt(key, value)?.commit()
    }
    fun getLeverage(defLeverage: Int = 100): Int {
        return sharedPreferences?.getInt("mc_sdk_user_config_leverage", defLeverage)!!
    }

    fun setLeverage(leverage: Int) {
        sharedPreferences?.edit()?.apply {
            putInt("mc_sdk_user_config_leverage", leverage)
            apply()
        }
    }

    /**
     * 获取保存的主图得信息
     * @param context
     * @return
     */
    fun getKLineMainIndex(): McMainIndex {
        val mainIndexStr = sharedPreferences?.getString(KLINE_MAIN_INDEX, McMainIndex.MA.getName())
        var mainIndex: McMainIndex
        mainIndex = when {
            TextUtils.equals(mainIndexStr, McMainIndex.MA.getName()) -> {
                McMainIndex.MA
            }
            TextUtils.equals(mainIndexStr, McMainIndex.BOLL.getName()) -> {
                McMainIndex.BOLL
            }
            TextUtils.equals(mainIndexStr, McMainIndex.EMA.getName()) -> {
                McMainIndex.EMA
            }
            TextUtils.equals(mainIndexStr, McMainIndex.SAR.getName()) -> {
                McMainIndex.SAR
            }
            TextUtils.equals(mainIndexStr, McMainIndex.NONE.getName()) -> {
                McMainIndex.NONE
            }
            else -> { // 默认为MA
                McMainIndex.MA
            }
        }
        return mainIndex
    }

    /**
     * 保存副图的指标
     */
    fun saveKLineSubIndex(subIndex: McSubIndex) {
        saveString(KLINE_SUB_INDEX, subIndex.getName())
    }

    /**
     * 保存主图的指标
     */
    fun saveKLineMainIndex(mainIndex: McMainIndex) {
        saveString(KLINE_MAIN_INDEX, mainIndex.getName())
    }

    /**
     * 获取保存的副图指标
     */
    fun getKLineSubIndex(): McSubIndex {
        val subIndexStr = sharedPreferences?.getString(KLINE_SUB_INDEX, McSubIndex.NONE.getName())
        val subIndex: McSubIndex
        subIndex = if (TextUtils.equals(subIndexStr, McSubIndex.MACD.getName())) {
            McSubIndex.MACD
        } else if (TextUtils.equals(subIndexStr, McSubIndex.KDJ.getName())) {
            McSubIndex.KDJ
        } else if (TextUtils.equals(subIndexStr, McSubIndex.RSI.getName())) {
            McSubIndex.RSI
        } else if (TextUtils.equals(subIndexStr, McSubIndex.OBV.getName())) {
            McSubIndex.OBV
        } else if (TextUtils.equals(subIndexStr, McSubIndex.WR.getName())) {
            McSubIndex.WR
        } else if (TextUtils.equals(subIndexStr, McSubIndex.NONE.getName())) {
            McSubIndex.NONE
        } else { // 默认为None
            McSubIndex.NONE
        }
        return subIndex
    }
    fun getSelectTime(): CandlesTimeUnit {
        if(selectTime==null) return CandlesTimeUnit.DAY
        return selectTime as CandlesTimeUnit
    }
    fun setSelectTime(selectTime: CandlesTimeUnit) {
        this.selectTime=selectTime
    }
}