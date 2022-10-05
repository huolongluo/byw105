package com.android.legend.extension

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import huolongluo.byw.byw.base.BaseApp

@MainThread
inline fun <reified VM : ViewModel> Fragment.appViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = createViewModelLazy(VM::class, { BaseApp.getSelf().viewModelStore }, factoryProducer)