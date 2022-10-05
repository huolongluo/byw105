package com.legend.modular_contract_sdk.utils

import com.legend.modular_contract_sdk.ui.chart.McHisData
import com.tictactec.ta.lib.Core
import com.tictactec.ta.lib.MInteger

object McDataUtils {

    private val core = Core()

    /**
     * calculate average price and ma data
     */
    @JvmOverloads
    @JvmStatic
    fun calculateHisData(list: List<McHisData>, lastData: McHisData? = null): List<McHisData> {


        val ma5List = calculateMA(5, list)
        val ma10List = calculateMA(10, list)
        val ma20List = calculateMA(20, list)
        val ma30List = calculateMA(30, list)

        val ma5VolList = calculateVolMA(5, list)
        val ma10VolList = calculateVolMA(10, list)

        for (i in list.indices) {
            val hisData = list[i]

            hisData.ma5 = ma5List[i]
            hisData.ma10 = ma10List[i]
            hisData.ma20 = ma20List[i]
            hisData.ma30 = ma30List[i]

            hisData.volMa5 = ma5VolList[i]
            hisData.volMa10 = ma10VolList[i]
        }
        computeMACD(list)
        computeBOLL(list)
        computeRSI(list)
        computeKDJ(list)
//        computeSAR(list)
//        computeWR(list)
//        computeOBV(list)
        return list
    }


    /**
     * calculate MA value, return a double array
     *
     * @param dayCount for example: 5, 10, 20, 30
     */
    private fun calculateMA(dayCount: Int, data: List<McHisData>): List<Double> {
        val result = ArrayList<Double>(data.size)
        var i = 0
        val len = data.size
        while (i < len) {
            if (i < dayCount - 1) {
                result.add(Double.NaN)
                i++
                continue
            }
            var sum = 0.0
            for (j in 0 until dayCount) {
                sum += data[i - j].close
            }
            result.add(sum / dayCount)
            i++
        }
        return result
    }

    private fun calculateVolMA(dayCount: Int, data: List<McHisData>): List<Double> {
        val result = ArrayList<Double>(data.size)
        var i = 0
        val len = data.size
        while (i < len) {
            if (i < dayCount - 1) {
                result.add(Double.NaN)
                i++
                continue
            }
            var sum = 0.0
            for (j in 0 until dayCount) {
                sum += data[i - j].vol
            }
            result.add(sum / dayCount)
            i++
        }
        return result
    }


    /**
     * 计算 MACD
     */
    private fun computeMACD(entries: List<McHisData>) {
        var ema12 = 0.0
        var ema26 = 0.0
        var diff = 0.0
        var dea = 0.0
        var macd = 0.0

        var ema5 = 0.0
        var ema10 = 0.0
        var ema30 = 0.0

        for (i in entries.indices) {
            val entry = entries[i]

            if (i == 0) {
                ema12 = entry.close
                ema26 = entry.close

                ema5 = entry.close
                ema10 = entry.close
                ema30 = entry.close
            } else {
                // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                ema12 = ema12 * 11f / 13f + entry.close * 2f / 13f
                ema26 = ema26 * 25f / 27f + entry.close * 2f / 27f

                ema5 = ema5 * 4f / 6f + entry.close * 2f / 6f
                ema10 = ema10 * 9f / 11f + entry.close * 2f / 11f
                ema30 = ema30 * 29f / 31f + entry.close * 2f / 31f
            }

            // DIF = EMA（12） - EMA（26） 。
            // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
            // 用（DIF-DEA）*2 即为 MACD 柱状图。
            diff = ema12 - ema26
            dea = dea * 8f / 10f + diff * 2f / 10f
            macd = (diff - dea) * 2f

            entry.diff = diff
            entry.dea = dea
            entry.macd = macd

            entry.ema5 = ema5
            entry.ema10 = ema10
            entry.ema30 = ema30
        }
    }

    private fun computeBOLL(data: List<McHisData>) {
        for (i in data.indices) {
            val entry = data[i]

            if (i == 0) {
                entry.mb = entry.close
                entry.up = java.lang.Double.NaN
                entry.dn = java.lang.Double.NaN
            } else {
                var n = 20
                if (i < 20) {
                    n = i + 1
                }

                var md = 0f
                for (j in i - n + 1..i) {
                    val c = data[j].close
                    val m = entry.ma20
                    val value = c - m
                    md += (value * value).toFloat()
                }

                md = md / (n - 1)
                md = Math.sqrt(md.toDouble()).toFloat()

                entry.mb = entry.ma20
                entry.up = entry.mb + 2f * md
                entry.dn = entry.mb - 2f * md
            }
        }
    }

    /**
     * 计算 RSI
     */
    private fun computeRSI(entries: List<McHisData>) {
        var rsi1 = 0.0
        var rsi2 = 0.0
        var rsi3 = 0.0
        var rsi1ABSEma = 0.0
        var rsi2ABSEma = 0.0
        var rsi3ABSEma = 0.0
        var rsi1MaxEma = 0.0
        var rsi2MaxEma = 0.0
        var rsi3MaxEma = 0.0

        for (i in entries.indices) {
            val entry = entries[i]

            if (i == 0) {
                rsi1 = 0.0
                rsi2 = 0.0
                rsi3 = 0.0
                rsi1ABSEma = 0.0
                rsi2ABSEma = 0.0
                rsi3ABSEma = 0.0
                rsi1MaxEma = 0.0
                rsi2MaxEma = 0.0
                rsi3MaxEma = 0.0
            } else {
                val Rmax = Math.max(0.0, entry.close - entries[i - 1].close)
                val RAbs = Math.abs(entry.close - entries[i - 1].close)

                rsi1MaxEma = (Rmax + (6f - 1) * rsi1MaxEma) / 6f
                rsi1ABSEma = (RAbs + (6f - 1) * rsi1ABSEma) / 6f

                rsi2MaxEma = (Rmax + (12f - 1) * rsi2MaxEma) / 12f
                rsi2ABSEma = (RAbs + (12f - 1) * rsi2ABSEma) / 12f

                rsi3MaxEma = (Rmax + (24f - 1) * rsi3MaxEma) / 24f
                rsi3ABSEma = (RAbs + (24f - 1) * rsi3ABSEma) / 24f

                rsi1 = rsi1MaxEma / rsi1ABSEma * 100
                rsi2 = rsi2MaxEma / rsi2ABSEma * 100
                rsi3 = rsi3MaxEma / rsi3ABSEma * 100
            }

            entry.rsi1 = rsi1
            entry.rsi2 = rsi2
            entry.rsi3 = rsi3
        }
    }

    /**
     * 计算 KDJ
     */
    private fun computeKDJ(entries: List<McHisData>) {
        var k = 0.0
        var d = 0.0

        for (i in entries.indices) {
            val entry = entries[i]

            var startIndex = i - 8
            if (startIndex < 0) {
                startIndex = 0
            }

            var max9 = java.lang.Double.MIN_VALUE
            var min9 = java.lang.Double.MAX_VALUE
            for (index in startIndex..i) {
                max9 = Math.max(max9, entries[index].high)
                min9 = Math.min(min9, entries[index].low)
            }

            val distance = max9 - min9
            val rsv = if (distance == 0.0) 0.0 else 100.0 * (entry.close - min9) / distance
            if (i == 0) {
                k = rsv
                d = rsv
            } else {
                k = (rsv + 2f * k) / 3f
                d = (k + 2f * d) / 3f
            }

            entry.k = k
            entry.d = d
            entry.j = 3f * k - 2 * d
        }
    }

    private fun computeSAR(entries: List<McHisData>) {
        val output = DoubleArray(entries.size)
        val begin = MInteger()
        val length = MInteger()
        val highs = entries.map { it.high }.toDoubleArray()
        val lows = entries.map { it.low }.toDoubleArray()
        core.sar(0, entries.size - 1, highs, lows, 0.02, 0.2, begin, length, output)
        for ((i, v) in entries.withIndex()) {
            if (i < begin.value) {
                v.sar = Double.NaN
            } else {
                v.sar = output[i - begin.value]
            }
        }
    }

    private fun computeWR(entries: List<McHisData>) {
        val output10 = DoubleArray(entries.size)
        val output6 = DoubleArray(entries.size)
        val begin = MInteger()
        val length = MInteger()
        val highs = entries.map { it.high }.toDoubleArray()
        val lows = entries.map { it.low }.toDoubleArray()
        val closes = entries.map { it.close }.toDoubleArray()
        core.willR(0, entries.size - 1, highs, lows, closes, 10, begin, length, output10)
        val begin10 = begin.value
        core.willR(0, entries.size - 1, highs, lows, closes, 6, begin, length, output6)
        val begin6 = begin.value
        for (i in output10.indices) {
            if (i < begin10) {
                entries[i].wr10 = 0.0
            } else {
                entries[i].wr10 = output10[i - begin10]
            }
            if (i < begin6) {
                entries[i].wr6 = 0.0
            } else {
                entries[i].wr6 = output6[i - begin6]
            }
        }
    }

    private fun computeOBV(entries: List<McHisData>) {
        val dayCount = 30
        val output = DoubleArray(entries.size)
        val begin = MInteger()
        val length = MInteger()
        val prices = entries.map { it.close }.toDoubleArray()
        val volume = entries.map { it.vol }.toDoubleArray()

        core.obv(0, entries.size - 1, prices, volume, begin, length, output)
        for (i in entries.indices) {
            entries[i].obv = output[i]
        }

        var i = 0
        val len = entries.size
        while (i < len) {
            if (i < dayCount - 1) {
                entries[i].maobv = Double.NaN
                i++
                continue
            }
            var sum = 0.0
            for (j in 0 until dayCount) {
                sum += entries[i - j].obv
            }

            entries[i].maobv = (sum / dayCount)
            i++
        }

    }

    /**
     * 复制旧的data的指标给新的data
     */
    @JvmStatic
    fun copyIndex(oldData: McHisData, newData: McHisData) {

        newData.ma5 = oldData.ma5
        newData.ma10 = oldData.ma10
        newData.ma20 = oldData.ma20
        newData.ma30 = oldData.ma30

        newData.volMa5 = oldData.volMa5
        newData.volMa10 = oldData.volMa10

        newData.dea = oldData.dea
        newData.diff = oldData.diff
        newData.macd = oldData.macd

        newData.k = oldData.k
        newData.d = oldData.d
        newData.j = oldData.j

        newData.rsi1 = oldData.rsi1
        newData.rsi2 = oldData.rsi2
        newData.rsi3 = oldData.rsi3

        newData.up = oldData.up
        newData.mb = oldData.mb
        newData.dn = oldData.dn

        newData.ema5 = oldData.ema5
        newData.ema10 = oldData.ema10
        newData.ema30 = oldData.ema30

        newData.sar = oldData.sar

        newData.wr6 = oldData.wr6
        newData.wr10 = oldData.wr10

        newData.obv = oldData.obv
        newData.maobv = oldData.maobv

    }
}