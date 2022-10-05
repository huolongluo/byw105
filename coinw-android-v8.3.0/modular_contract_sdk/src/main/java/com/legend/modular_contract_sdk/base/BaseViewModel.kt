package com.legend.modular_contract_sdk.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.legend.modular_contract_sdk.component.net.LoadState
import com.legend.modular_contract_sdk.repository.model.BaseResp
import com.legend.modular_contract_sdk.repository.model.RespInterface
import com.legend.modular_contract_sdk.widget.McBusMutableLiveData
import kotlinx.coroutines.*
import java.lang.Exception

open class BaseViewModel : ViewModel() {


    var requestStateLiveData: MutableLiveData<LoadState?> = MutableLiveData()
    var showLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var showMessageLiveData: McBusMutableLiveData<String> = McBusMutableLiveData()

    fun <T : RespInterface<M>, M> request(
        errorBlock: ((Throwable) -> Unit)? = null,
        isShowLoading: Boolean = false,
        requestBlock: suspend CoroutineScope.() -> T
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
            onError(throwable.message ?: "No Message")
            errorBlock?.invoke(throwable)
            if (isShowLoading) {
                showLoadingLiveData.postValue(false)
            }
        }) {
            onStartLoading()
            if (isShowLoading) {
                showLoadingLiveData.postValue(true)
            }

            withContext(Dispatchers.IO) {

                requestBlock().apply {
                    if (!this.isReqSuccess()) {
                        onLogicalError(this.getReqCode(), this.getReqMsg()!!)
                    }
                }
            }
            onComplete()
            if (isShowLoading) {
                showLoadingLiveData.postValue(false)
            }
        }
    }

    suspend fun <T : BaseResp<M>, M> suspendRequest(
            errorBlock: ((Throwable) -> Unit)? = null,
            isShowLoading: Boolean = false,
            requestBlock: suspend CoroutineScope.() -> T
    ) {

        coroutineScope {
            launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                throwable.printStackTrace()
                onError(throwable.message ?: "No Message")
                errorBlock?.invoke(throwable)
                if (isShowLoading) {
                    showLoadingLiveData.postValue(false)
                }
            }) {
                onStartLoading()
                if (isShowLoading) {
                    showLoadingLiveData.postValue(true)
                }

                withContext(Dispatchers.IO) {

                    requestBlock().apply {
                        if (!this.isSuccess) {
                            onLogicalError(this.code.toString(), this.msg!!)
                        }
                    }
                }
                onComplete()
                if (isShowLoading) {
                    showLoadingLiveData.postValue(false)
                }
            }
        }


    }

    fun <T : BaseResp<M>, M> request(vararg requestBlocks: suspend () -> T) {
        viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
            onError(throwable.message ?: "No Message")
        }) {
            onStartLoading()
            withContext(Dispatchers.IO) {

                var requestQueue = requestBlocks.map {
                    return@map async { it() }
                }

                requestQueue.forEach {
                    it.await().apply {
//                        Logger.d("await() -> apply ${this.code}  ${this.isSuccess}")
                        if (!this.isSuccess) {
                            onLogicalError(this.code.toString(), this.msg!!)
                        }
                    }
                }
            }
            onComplete()
        }
    }

    fun onStartLoading() {
        requestStateLiveData.postValue(LoadState.StartLoading())
    }

    fun onLogicalError(code: String, msg: String) {
        requestStateLiveData.postValue(LoadState.LogicalError(code, msg))
    }

    fun onError(msg: String) {
        requestStateLiveData.postValue(LoadState.Failure(msg))
    }

    fun onComplete() {
        requestStateLiveData.postValue(LoadState.Complete())
    }

}