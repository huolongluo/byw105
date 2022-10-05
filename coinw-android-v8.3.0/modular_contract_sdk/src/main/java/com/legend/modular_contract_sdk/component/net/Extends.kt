package com.legend.modular_contract_sdk.component.net

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.legend.modular_contract_sdk.R
import com.orhanobut.logger.Logger
import java.lang.Exception

lateinit var mToast: Toast

sealed class LoadState(val msg: String) {
    class StartLoading(msg: String = "") : LoadState(msg)
    class LogicalError(val code: String, msg: String = "") : LoadState(msg)
    class Failure(msg: String) : LoadState(msg)
    class Complete(msg: String = "") : LoadState(msg)
}

fun requestStateHandle(context: Context, state: LoadState) {
    when (state::class) {
        LoadState.StartLoading::class -> {
//            Logger.e("开始加载")
        }
        LoadState.LogicalError::class -> {
            if (!::mToast.isInitialized) {
                mToast = Toast.makeText(context, state.msg, Toast.LENGTH_SHORT)
            } else {
                mToast.setText(state.msg)
            }
            mToast.show()
            Logger.e("逻辑错误 code=${(state as LoadState.LogicalError).code} msg=${state.msg}")
        }
        LoadState.Failure::class -> {
            var errorMsg=state.msg
            if(TextUtils.isEmpty(errorMsg)){
                errorMsg=context.resources.getString(R.string.mc_sdk_net_error)
            }
            if(errorMsg.contains(" host ")||errorMsg.contains("failed to connect")||errorMsg.contains("SSL handshake")){//带host字样的不提示
                return
            }
            if (!::mToast.isInitialized) {
                mToast = Toast.makeText(
                    context,
                        errorMsg,
                    Toast.LENGTH_SHORT
                )
            } else {
                mToast.setText(errorMsg)
            }
            mToast.show()
            Logger.e("加载失败 ${state.msg}")
        }
        LoadState.Complete::class -> {
//            Logger.e("加载完成")
        }
        else -> {
            Logger.e("未知状态")
        }
    }
}