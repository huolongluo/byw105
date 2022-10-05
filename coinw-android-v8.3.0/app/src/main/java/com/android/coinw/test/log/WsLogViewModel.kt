package com.android.coinw.test.log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.legend.common.BusMutableLiveData
import com.android.legend.model.SocketIOResponse
import java.util.*
import kotlin.collections.ArrayList

class WsLogViewModel : ViewModel() {
    private val list = LinkedList<SocketIOResponse>()
    val logData = MutableLiveData<List<SocketIOResponse>>()
    val updateLogData = BusMutableLiveData<SocketIOResponse>()

    fun addLog(log: SocketIOResponse) {
        if (list.size > 1000) {
            list.removeAt(0)
        }
        list.add(log)
        updateLogData.postValue(log)
    }

    fun loadData() {
        logData.value = list.reversed()
    }
}