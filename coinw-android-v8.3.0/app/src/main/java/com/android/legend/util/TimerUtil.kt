package com.android.legend.util

import android.content.Context
import android.os.CountDownTimer
import android.widget.TextView
import androidx.core.content.ContextCompat
import huolongluo.byw.R

class TimerUtil {

    companion object{
        private const val TIME_COUNT_DOWN=120000L
        /**
         * kotlin风格的倒计时工厂函数，简化代码
         */
        inline fun createCountDownTimer(
            millisInFuture: Long,
            countDownInterval: Long,
            crossinline onTick: (millisUntilFinished: Long) -> Unit = {},
            crossinline onFinish: () -> Unit = {}
        ) = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onFinish() {
                onFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

        }

        fun isTimeExceedInterval(lastTime:Long,interval:Long):Boolean{
            return System.currentTimeMillis()-lastTime>=interval
        }
    }
    private var countDownTimer: CountDownTimer? = null
    //用于发送验证码的倒计时
    fun startCountDown(tvOperate: TextView, context: Context) {
        countDownTimer?.cancel()
        tvOperate.isEnabled=false
        tvOperate.setTextColor(ContextCompat.getColor(context,R.color.text_main3))
        countDownTimer=object :CountDownTimer(TIME_COUNT_DOWN,1000){
            override fun onFinish() {
                tvOperate.isEnabled=true
                tvOperate.setTextColor(ContextCompat.getColor(context,R.color.accent_main))
                tvOperate.text=context.getString(R.string.send_again)
            }

            override fun onTick(millisUntilFinished: Long) {
                tvOperate.text="${context.getString(R.string.send_again)} (${millisUntilFinished/1000}S)"
            }
        }
        countDownTimer?.start()
    }
    fun release(){
        countDownTimer?.cancel()
    }
}