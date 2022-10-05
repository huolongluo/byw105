package com.android.coinw.test.log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.legend.common.BusMutableLiveData
import java.util.*

class HttpLogViewModel : ViewModel() {
    private val list = LinkedList<String>()
    val logData = MutableLiveData<List<String>>()
    val updateLogData = BusMutableLiveData<String>()

    fun addLog(log: String) {
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