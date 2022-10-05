package com.legend.modular_contract_sdk.common

import androidx.lifecycle.MutableLiveData

class NotNullMutableLiveData<T> : MutableLiveData<T>() {
    override fun postValue(value: T?) {
        if (value != null) {
            super.postValue(value)
        }
    }

    override fun setValue(value: T?) {
        if (value != null) {
            super.setValue(value)
        }
    }
}

fun <T> MutableLiveData<T>.postValueNotNull(value: T?) {
    value?.let {
        postValue(value)
    }
}

fun <T> MutableLiveData<T>.setValueNotNull(value: T?) {
    value?.let {
        this.value = value
    }
}